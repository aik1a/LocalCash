"""Download licensed vector data at build time only; Android never uses the network."""
import hashlib, json, os, pathlib, shutil, struct, sys, urllib.request
ROOT = pathlib.Path(__file__).resolve().parents[2]
OUT = ROOT / 'app/build/generated/offlineMaps/maps/vector'
SOURCES = {
    'world.map': 'https://download.mapsforge.org/maps/v5/world/world.map',
    'chile.map': 'https://ftp-stud.hs-esslingen.de/Mirrors/download.mapsforge.org/maps/v5/south-america/chile.map',
}
def digest(path):
    h = hashlib.sha256()
    with path.open('rb') as f:
        for block in iter(lambda: f.read(1024 * 1024), b''): h.update(block)
    return h.hexdigest()
def valid(path):
    if not path.exists() or path.stat().st_size < 1000: return False
    with path.open('rb') as f: header = f.read(36)
    return len(header) == 36 and header[:20] == b'mapsforge binary OSM' and struct.unpack('>q', header[28:36])[0] == path.stat().st_size
def main():
    OUT.mkdir(parents=True, exist_ok=True)
    records = []
    for name, url in SOURCES.items():
        path = OUT / name
        if not valid(path):
            temp = path.with_suffix('.part')
            print('Preparing offline map:', name, flush=True)
            request = urllib.request.Request(url, headers={'User-Agent':'LocalCash-academic-build/1.2'})
            try:
                with urllib.request.urlopen(request, timeout=180) as source, temp.open('wb') as target:
                    shutil.copyfileobj(source, target, 1024 * 1024)
                if not valid(temp): raise ValueError('Not a valid Mapsforge file: ' + name)
                os.replace(temp, path)
            finally:
                temp.unlink(missing_ok=True)
        sha = digest(path)
        records.append({'name':name, 'url':url, 'bytes':path.stat().st_size, 'sha256':sha})
        (OUT / (name + '.sha256')).write_text(sha, encoding='ascii')
        print(name, path.stat().st_size, sha, flush=True)
    (OUT / 'provenance.json').write_text(json.dumps({'license':'ODbL-1.0', 'attribution':'© OpenStreetMap contributors', 'maps':records}, indent=2))
    (OUT / 'LICENSE.txt').write_text('Map data © OpenStreetMap contributors, ODbL 1.0.\nhttps://www.openstreetmap.org/copyright\nhttps://opendatacommons.org/licenses/odbl/1-0/\nWorld is a coarse basemap; Chile contains street data. Additional countries may be imported locally as .map files.\n')
if __name__ == '__main__': main()
