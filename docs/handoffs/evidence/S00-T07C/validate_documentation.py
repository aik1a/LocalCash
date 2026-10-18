"""T07C documentary checks only; no build, emulator or audit gate."""
import argparse
import hashlib
import json
from pathlib import Path
import posixpath
import re
from urllib.parse import unquote, urlsplit
import xml.etree.ElementTree as ET
import zipfile

root=Path(__file__).parent
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--docs-root',type=Path,default=root/'repo')
parser.add_argument('--sources',type=Path,default=root/'source-checks.json')
parser.add_argument('--tree-file',type=Path,default=root/'tree-paths.json')
parser.add_argument('--apk',type=Path,default=root.parent/'audit-t07a/API36/app/build/outputs/apk/debug/app-debug.apk')
parser.add_argument('--artifact',type=Path,default=root.parent/'audit-t07a/API36.zip')
parser.add_argument('--pdf',type=Path,default=root.parent/'project_sources/01-LocalCash-Proyecto-Android.pdf')
parser.add_argument('--output',type=Path,default=root/'validation-result.json')
args=parser.parse_args()
docs=args.docs_root
sources=json.loads(args.sources.read_text())
known=set(json.loads(args.tree_file.read_text()))
manifest=json.loads((docs/'docs/delivery/S00-package-manifest.json').read_text())
scope=[docs/p for p in manifest['documentation_files'] if p.endswith('.md')]
known.update(str(p.relative_to(docs)) for p in docs.rglob('*') if p.is_file())
issues=[]; links=[]; externals=set()
for p in sorted(scope):
    rel=str(p.relative_to(docs)); content=p.read_text()
    assert content.count('```')%2==0,rel+' fence mismatch'
    for label,target in re.findall(r'\[([^\]]*)\]\(([^)]+)\)',content):
        if re.match(r'^[a-zA-Z][a-zA-Z0-9+.-]*:',target):
            externals.add(target);continue
        target=unquote(urlsplit(target).path)
        if not target:continue
        resolved=posixpath.normpath(posixpath.join(posixpath.dirname(rel),target))
        links.append({'document':rel,'target':resolved})
        if resolved not in known:issues.append({'document':rel,'missing':resolved})
assert not issues,issues
assert all(x['identical'] for x in sources['identity'])
assert all(x['status']=='completed' and x['conclusion']=='success' for x in sources['ci'])
activity_root=ET.fromstring(sources['manifest']); ns='{http://schemas.android.com/apk/res/android}'
declared=activity_root.find('application').findall('activity')
names={a.get(ns+'name').rsplit('.',1)[-1] for a in declared}
protected=names-{'AccesoActivity'}
assert len(protected)==8 and len(names)==9
arch=(docs/'ARCHITECTURE.md').read_text()
assert all('| '+name+' |' in arch for name in protected)
assert sources['original_architecture'] in arch
assert sources['original_build'] in (docs/'docs/delivery/BUILD.md').read_text()
assert sources['original_presentation'] in (docs/'docs/delivery/PRESENTATION.md').read_text()
for token in ['minSdk 24','targetSdk 36','versionCode 1',"versionName '1.0'",'androidx.biometric:biometric:1.1.0','org.osmdroid:osmdroid-android:6.1.18']:
    assert token in sources['app_gradle'],token
assert manifest['historical_main_certificates']==10 and manifest['historical_compatible_distinct_pairs']==0
assert manifest['in_place_upgrade']=='BLOCKED_SIGNER_MISMATCH'
assert manifest['sqlite_preservation_after_upgrade']=='NOT TESTED'
apk=args.apk
assert hashlib.sha256(apk.read_bytes()).hexdigest()==manifest['apk']['sha256']
with zipfile.ZipFile(apk) as z:
    assert hashlib.sha256(z.read('assets/maps/santiago_centro.zip')).hexdigest()==manifest['map']['sha256']
pdf=args.pdf
assert hashlib.sha256(pdf.read_bytes()).hexdigest()==manifest['teacher_pdf']['sha256']
artifact=next(x for x in sources['artifacts'] if x['id']==manifest['apk']['artifact_id'])
assert not artifact['expired'] and artifact['digest']=='sha256:'+manifest['apk']['artifact_zip_sha256']
zip_path=args.artifact
assert hashlib.sha256(zip_path.read_bytes()).hexdigest()==manifest['apk']['artifact_zip_sha256']
requirements=['LC-P01','LC-P02','LC-P03','LC-P04','LC-AUTH-01','LC-CON-01','LC-ACC-01','LC-MOV-01','LC-MOV-02','LC-MAP-01','LC-BUY-01','LC-BUY-02','LC-BUY-03','LC-SHARED-01','LC-SHARED-02','LC-DEBT-01','LC-DEBT-02']+['LC-TECH-'+str(i).zfill(2) for i in range(1,7)]
trace=(docs/'docs/requirements/TRACEABILITY.md').read_text().split('## Registro histórico')[0]
assert all(re.search(r'\b'+r+r'\b',trace) for r in requirements)
result={'result':'PASS','exit_code':0,'scope':'Document validation; not Android execution or independent audit','documents_checked':len(scope),'internal_links_checked':len(links),'broken_internal_links':issues,'external_urls_found':sorted(externals),'external_link_limit':'CI run/artifact references checked using original GitHub API metadata; other HTTP URLs were not all fetched','protected_routes':sorted(protected),'canonical_route':'ARCHITECTURE.md; docs/design/ARCHITECTURE.md links to it','original_historical_documents_preserved':3,'requirement_ids_checked':len(requirements),'apk_sha256':manifest['apk']['sha256'],'pdf_sha256':manifest['teacher_pdf']['sha256'],'source_identity':sources['identity'],'ci_checked':sources['ci'],'new_build':'N/A documentation-only','new_android_tests':'NOT EXECUTED'}
args.output.write_text(json.dumps(result,indent=2)+'\n')
print(json.dumps({k:v for k,v in result.items() if k not in ['external_urls_found','source_identity','ci_checked']},indent=2))
