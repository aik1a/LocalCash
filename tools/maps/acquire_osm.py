"""Mantenimiento manual: fallback de datos OSM autorizado por ADR-002. Nunca CI/runtime."""
import datetime,gzip,hashlib,json,pathlib,urllib.request,xml.etree.ElementTree as ET
root=pathlib.Path(__file__).resolve().parent
records=[];nodes={};ways={}
for west,south,east,north in [(-70.675,-33.465,-70.660,-33.450),(-70.660,-33.465,-70.645,-33.450),(-70.675,-33.450,-70.660,-33.435),(-70.660,-33.450,-70.645,-33.435)]:
 url=f'https://api.openstreetmap.org/api/0.6/map?bbox={west},{south},{east},{north}'
 req=urllib.request.Request(url,headers={'User-Agent':'LocalCashAcademicOfflineAtlas/1.0 (https://github.com/aik1a/LocalCash)'})
 with urllib.request.urlopen(req,timeout=60) as response:raw=response.read()
 tree=ET.fromstring(raw)
 records.append({'url':url,'utc':datetime.datetime.now(datetime.timezone.utc).isoformat(),'response_sha256':hashlib.sha256(raw).hexdigest(),'bytes':len(raw)})
 for n in tree.findall('node'):nodes[n.attrib['id']]=n
 for w in tree.findall('way'):
  tags={t.attrib['k']:t.attrib['v'] for t in w.findall('tag')}
  if 'highway' in tags or 'waterway' in tags or tags.get('leisure') in ['park','garden'] or tags.get('natural')=='water':ways[w.attrib['id']]=w
 print('Fetched bounded data',url,len(raw),flush=True)
needed={n.attrib['ref'] for w in ways.values() for n in w.findall('nd')}
assert not needed-set(nodes),'Geometría incompleta'
result=ET.Element('osm',{'version':'0.6','generator':'LocalCash OSM selection','copyright':'OpenStreetMap and contributors','license':'https://opendatacommons.org/licenses/odbl/1-0/'})
for i in sorted(needed,key=int):result.append(nodes[i])
for i in sorted(ways,key=int):result.append(ways[i])
raw=ET.tostring(result,encoding='utf-8',xml_declaration=True)
compressed=gzip.compress(raw,mtime=0)
(root/'santiago-centro.osm.xml.gz').write_bytes(compressed)
info={'provider':'OpenStreetMap API 0.6','license':'ODbL-1.0','attribution':'© OpenStreetMap contributors','copyright_url':'https://www.openstreetmap.org/copyright','records':records,'selection':'ways: highway, waterway, leisure park/garden, natural water; all referenced nodes; sorted by numeric OSM id','raw_sha256':hashlib.sha256(raw).hexdigest(),'gzip_sha256':hashlib.sha256(compressed).hexdigest(),'nodes':len(needed),'ways':len(ways),'raw_bytes':len(raw),'gzip_bytes':len(compressed),'fallback':'Overpass-api.de POST HTTP502; kumi GET HTTP500; official API full bbox >50000 nodes; four bounded quadrants of ADR bbox, no tile download.'}
(root/'download-provenance.json').write_text(json.dumps(info,ensure_ascii=False,indent=2)+'\n')
print(json.dumps(info,ensure_ascii=False),flush=True)
