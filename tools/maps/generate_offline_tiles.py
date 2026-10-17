"""Render propio de un extracto OSM versionado. No usa red ni servidores de tiles."""
import argparse,gzip,hashlib,io,json,math,pathlib,zipfile,xml.etree.ElementTree as ET
from PIL import Image,ImageDraw,ImageFont,__version__ as PILLOW_VERSION
ROOT=pathlib.Path(__file__).resolve().parents[2]
BBOX=(-33.465,-70.675,-33.435,-70.645)
SOURCE='LocalCashOSM';BG='#eeeae0'
def xy(lat,lon,z):
 n=2**z;return ((lon+180)/360*n,(1-math.asinh(math.tan(math.radians(lat)))/math.pi)/2*n)
def tile_ranges(z):
 s,w,n,e=BBOX;left,bottom=xy(s,w,z);right,top=xy(n,e,z)
 return range(math.floor(left),math.floor(right)+1),range(math.floor(top),math.floor(bottom)+1)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--output',type=pathlib.Path,default=ROOT/'app/src/main/assets/maps/santiago_centro.zip');parser.add_argument('--preview',type=pathlib.Path);args=parser.parse_args()
 src=ROOT/'tools/maps/santiago-centro.osm.xml.gz';data=src.read_bytes();tree=ET.fromstring(gzip.decompress(data))
 nodes={n.attrib['id']:(float(n.attrib['lat']),float(n.attrib['lon'])) for n in tree.findall('node')}
 ways=[]
 for w in tree.findall('way'):
  tags={t.attrib['k']:t.attrib['v'] for t in w.findall('tag')};coords=[nodes[n.attrib['ref']] for n in w.findall('nd')]
  if len(coords)>1:ways.append((int(w.attrib['id']),tags,coords))
 ways.sort();entries=[];font=ImageFont.load_default(size=11)
 for z in [14,15,16]:
  xs,ys=tile_ranges(z);x0,y0=xs.start,ys.start
  canvas=Image.new('RGB',(len(xs)*256,len(ys)*256),BG);draw=ImageDraw.Draw(canvas)
  def points(coords):return [(round((xy(lat,lon,z)[0]-x0)*256),round((xy(lat,lon,z)[1]-y0)*256)) for lat,lon in coords]
  for ident,tags,coords in ways:
   pts=points(coords)
   if len(pts)>=4 and coords[0]==coords[-1]:
    if tags.get('leisure') in ['park','garden']:draw.polygon(pts,fill='#b6cfa2',outline='#a4bc90')
    elif tags.get('natural')=='water':draw.polygon(pts,fill='#a5cbd9')
   if tags.get('waterway'):draw.line(pts,fill='#a5cbd9',width=max(2,round(5*2**(z-15))),joint='curve')
  roads=[w for w in ways if 'highway' in w[1]]
  rank={'motorway':8,'trunk':7,'primary':6,'secondary':5,'tertiary':4,'residential':3,'unclassified':3,'service':2}
  roads.sort(key=lambda w:(rank.get(w[1]['highway'],1),w[0]))
  for border in [True,False]:
   for ident,tags,coords in roads:
    kind=tags['highway'];major=rank.get(kind,1)
    width=max(1,round((major+1)*2**(z-16)))
    color='#d5cbbb' if border else ('#edc778' if major>=5 else '#faf8f3')
    if major==1 and not border:color='#d7d3c7'
    draw.line(points(coords),fill=color,width=width+(2 if border and major>1 else 0),joint='curve')
  occupied=[]
  for ident,tags,coords in roads:
   if not tags.get('name') or rank.get(tags['highway'],1)<(4 if z==14 else 3):continue
   px,py=points(coords)[len(coords)//2];name=tags['name']
   if len(name)>36:name=name[:35]+'…'
   bounds=draw.textbbox((px,py),name,font=font,anchor='mm',stroke_width=1)
   if bounds[0]<0 or bounds[1]<0 or bounds[2]>=canvas.width or bounds[3]>=canvas.height:continue
   if any(not(bounds[2]+14<r[0] or bounds[0]-14>r[2] or bounds[3]+12<r[1] or bounds[1]-12>r[3]) for r in occupied):continue
   draw.text((px,py),name,font=font,anchor='mm',fill='#5b554b',stroke_width=1,stroke_fill='#f8f5ed');occupied.append(bounds)
  for x in xs:
   for y in ys:
    tile=canvas.crop(((x-x0)*256,(y-y0)*256,(x-x0+1)*256,(y-y0+1)*256))
    buffer=io.BytesIO();tile.save(buffer,format='PNG',optimize=False,compress_level=9)
    entries.append((f'{SOURCE}/{z}/{x}/{y}.png',buffer.getvalue()))
  if z==16 and args.preview:
   args.preview.parent.mkdir(parents=True,exist_ok=True);canvas.save(args.preview)
 args.output.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(args.output,'w',compression=zipfile.ZIP_STORED) as archive:
  for name,raw in sorted(entries):
   info=zipfile.ZipInfo(name,date_time=(2020,1,1,0,0,0));info.compress_type=zipfile.ZIP_STORED;info.create_system=3;info.external_attr=0o100644<<16;archive.writestr(info,raw)
 meta={'source_name':SOURCE,'tile_size':256,'scheme':'XYZ','zooms':[14,15,16],'bbox':list(BBOX),'center':[-33.450,-70.660],'tiles':len(entries),'zip_bytes':args.output.stat().st_size,'zip_sha256':hashlib.sha256(args.output.read_bytes()).hexdigest(),'extract_gzip_sha256':hashlib.sha256(data).hexdigest(),'pillow_version':PILLOW_VERSION,'license':'ODbL-1.0','attribution':'Datos del mapa © OpenStreetMap contributors · ODbL','copyright_url':'https://www.openstreetmap.org/copyright','style_license':'CC0-1.0','extract_asset':'maps/santiago-centro.osm.xml.gz','style':'LocalCash simple raster, generated locally from OSM ways'}
 args.output.with_name('metadata.json').write_text(json.dumps(meta,ensure_ascii=False,indent=2)+'\n');print(json.dumps(meta,ensure_ascii=False))
if __name__=='__main__':main()
