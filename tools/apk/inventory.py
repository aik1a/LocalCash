"""Inventory accepted T03–T07A delivery artifacts. Never signs or modifies APKs."""
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import urllib.request
import zipfile

ROOT = Path('evidence')
ROOT.mkdir(exist_ok=True)
SOURCES = json.loads(Path('tools/apk/source-artifacts.json').read_text())
TOKEN = os.environ['GH_TOKEN']
SDK = Path(os.environ['ANDROID_HOME']) / 'build-tools' / '36.0.0'

def command(args, target):
    result = subprocess.run([str(a) for a in args], text=True, capture_output=True)
    target.write_text(result.stdout + result.stderr)
    target.with_suffix(target.suffix + '.exit-code.txt').write_text(str(result.returncode) + '\n')
    if result.returncode:
        raise RuntimeError(f'{target.name}: exit {result.returncode}')
    return result.stdout + result.stderr

def download(artifact, destination):
    # urllib strips Authorization on the cross-host redirect via this handler.
    class Redirect(urllib.request.HTTPRedirectHandler):
        def redirect_request(self, req, fp, code, msg, headers, newurl):
            redirected = super().redirect_request(req, fp, code, msg, headers, newurl)
            if redirected:
                redirected.remove_header('Authorization')
            return redirected
    request = urllib.request.Request(artifact['archive_download_url'], headers={
        'Authorization': 'Bearer ' + TOKEN, 'Accept': 'application/vnd.github+json',
        'X-GitHub-Api-Version': '2022-11-28'})
    with urllib.request.build_opener(Redirect()).open(request, timeout=90) as response:
        destination.write_bytes(response.read())
    digest = 'sha256:' + hashlib.sha256(destination.read_bytes()).hexdigest()
    if digest != artifact['digest']:
        raise AssertionError(f'Artifact {artifact["id"]}: digest mismatch')

inventory = []
for artifact in SOURCES:
    artifact_id = artifact['id']
    archive = ROOT / f'artifact-{artifact_id}.zip'
    download(artifact, archive)
    with zipfile.ZipFile(archive) as source:
        if source.testzip() is not None:
            raise AssertionError('Bad ZIP CRC')
        apk_entries = [n for n in source.namelist() if n.endswith('.apk')]
        if not apk_entries:
            raise AssertionError(f'No APK in {artifact_id}')
        for index, entry in enumerate(apk_entries):
            tag = f'{artifact_id}-{index}'
            apk = ROOT / f'{tag}.apk'
            apk.write_bytes(source.read(entry))
            signature = command([SDK / 'apksigner', 'verify', '--verbose', '--print-certs', apk], ROOT / f'{tag}-apksigner.log')
            badging = command([SDK / 'aapt', 'dump', 'badging', apk], ROOT / f'{tag}-badging.log')
            package = re.search(r"^package: name='([^']+)' versionCode='([0-9]+)' versionName='([^']*)'", badging, re.M)
            fingerprints = re.findall(r'^Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]+)$', signature, re.M)
            if not package or not fingerprints:
                raise AssertionError(f'Unrecognized metadata {tag}')
            inventory.append({
                'id': tag, 'artifact_id': artifact_id, 'entry': entry,
                'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
                'package': package[1], 'version_code': int(package[2]), 'version_name': package[3],
                'signer_sha256': sorted(f.lower() for f in fingerprints),
                'certificate_dn': re.findall(r'^Signer #\d+ certificate DN: (.*)$', signature, re.M),
                'schemes': re.findall(r'^(Verified using .*?: (?:true|false))$', signature, re.M),
                'commit': artifact['workflow_run']['head_sha'], 'run': artifact['workflow_run']['id'],
                'artifact_zip_sha256': artifact['digest'], 'apksigner_exit': 0,
                'role': 'application' if package[1] == 'com.aik1a.localcash' else 'instrumentation',
            })
application = [item for item in inventory if item['role'] == 'application']
compatibility = []
for previous in application:
    for current in application:
        if previous['id'] == current['id']:
            continue
        same_signer = previous['signer_sha256'] == current['signer_sha256']
        compatible = same_signer and current['version_code'] >= previous['version_code']
        compatibility.append({'from': previous['id'], 'to': current['id'],
                              'same_package': previous['package'] == current['package'],
                              'same_signer': same_signer, 'compatible': compatible,
                              'reason': 'SAME_SIGNER_AND_NONDECREASING_VERSION' if compatible else 'SIGNER_MISMATCH' if not same_signer else 'VERSION_DOWNGRADE'})
result = {'tool': 'Android SDK Build Tools 36.0.0 apksigner verify --verbose --print-certs / aapt dump badging',
          'inventory': inventory, 'compatibility': compatibility,
          'compatible_distinct_pairs': sum(x['compatible'] for x in compatibility),
          'limitations': ['Inventory limited to retained accepted T03–T07A delivery runs; no user device APK supplied.',
                          'Self-reinstallation of an identical APK is not a historical version upgrade.',
                          'Signature lineage rotation is not introduced or inferred.']}
(ROOT / 'inventory.json').write_text(json.dumps(result, indent=2) + '\n')
(ROOT / 'source-artifacts.json').write_text(json.dumps(SOURCES, indent=2) + '\n')
print(json.dumps({'result': 'INVENTORY_PASS', 'apks': len(inventory), 'applications': len(application),
                  'compatible_distinct_pairs': result['compatible_distinct_pairs']}, indent=2))
if os.environ.get('GITHUB_OUTPUT'):
    with open(os.environ['GITHUB_OUTPUT'], 'a') as output:
        output.write('compatible=' + str(result['compatible_distinct_pairs']) + '\n')
