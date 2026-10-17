"""Verifica cobertura XYZ exacta, licencia/hash, PNG y geometría cartográfica versionada."""
import gzip,hashlib,io,json,pathlib,zipfile,xml.etree.ElementTree as E
from PIL import Image
from generate_offline_tiles import ROOT,BBOX,SOURCE,xy,tile_ranges
p=ROOT/'app/src/main/assets/maps';meta=json.loads((p/'metadata.json').read_text());data=(p/'santiago_centro.zip').read_bytes()
assert hashlib.sha256(data).hexdigest()==meta['zip_sha256']
assert len(data)<=6*1024*1024
assert meta['source_name']==SOURCE and meta['bbox']==list(BBOX) and meta['scheme']=='XYZ' and meta['license']=='ODbL-1.0'
extract=ROOT/'tools/maps/santiago-centro.osm.xml.gz';provenance=json.loads((ROOT/'tools/maps/download-provenance.json').read_text())
assert hashlib.sha256(extract.read_bytes()).hexdigest()==meta['extract_gzip_sha256']==provenance['gzip_sha256']
raw=gzip.decompress(extract.read_bytes());assert hashlib.sha256(raw).hexdigest()==provenance['raw_sha256']
tree=E.fromstring(raw);names={t.get('v') for w in tree.findall('way') for t in w.findall('tag') if t.get('k')=='name'}
assert any('Libertador Bernardo' in n for n in names)
expected={f'{SOURCE}/{z}/{x}/{y}.png' for z in [14,15,16] for x in tile_ranges(z)[0] for y in tile_ranges(z)[1]}
with zipfile.ZipFile(io.BytesIO(data)) as archive:
 assert set(archive.namelist())==expected and len(expected)==meta['tiles']==64
 assert archive.testzip() is None
 for name in expected:
  im=Image.open(io.BytesIO(archive.read(name)));im.load();assert im.format=='PNG' and im.size==(256,256)
 for z in [14,15,16]:
  x,y=map(int,xy(-33.450,-70.660,z));im=Image.open(io.BytesIO(archive.read(f'{SOURCE}/{z}/{x}/{y}.png'))).convert('RGB')
  colors=im.getcolors(65537);assert len(colors)>15
  changed=sum(count for count,color in colors if color!=(238,234,224));assert changed>1000
  print(f'PASS centro z{z}: {changed} pixels cartográficos, {len(colors)} colores, OSM real.')
print('LOCALCASH_ATLAS_PASS: 64 PNG XYZ, bbox z14–16, <=6MiB, SHA256/origen ODbL y vías reales.')
