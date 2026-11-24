"""Build the Liquibase SQL for SNID venues from the QA-ROW lines of venue-snid-candidates (geocode=1).
Usage: python3 scripts/venue_snid/generate_sql.py rows.jsonl out.sql
Source: IPDJ - Sistema Nacional de Informação Desportiva, CC BY 4.0 (credited via source_reference 'SNID:<id>')."""
import json,sys,re,collections
rows=[json.loads(l) for l in open(sys.argv[1]) if l.strip()]
SMALL={'de','da','do','das','dos','e','a','o','em','na','no','nas','nos'}
def title(s):
    s=re.sub(r'\s+',' ',s).strip()
    if s!=s.upper(): return s[0].upper()+s[1:]
    w=s.lower().split(' ')
    return ' '.join(x if (i and x in SMALL) else x[:1].upper()+x[1:] for i,x in enumerate(w))
mun2dist={}
for r in rows:
    if r.get('district') and r.get('municipality'): mun2dist.setdefault(r['municipality'],collections.Counter())[r['district']]+=1
def district(r):
    if r.get('district'): return r['district']
    if r.get('state') and r['state'] not in ('',None) and not r['state'].startswith('Região'): return r['state']
    c=mun2dist.get(r.get('municipality'));return c.most_common(1)[0][0] if c else None
def q(v): return 'NULL' if v in (None,'') else "'"+str(v).replace("'","''")+"'"
out=['-- Sports facilities from IPDJ - Sistema Nacional de Informação Desportiva (SNID), CC BY 4.0.',
     '-- Schools, generic and unnamed records were filtered out. Municipality from OpenStreetMap Nominatim.',
     '-- Skipped when any venue already sits within ~70 m, or a venue with the same name is nearby.','']
n=0;seen=set()
for r in rows:
    lat,lon=r['lat'],r['lon']
    if not (36.8<=lat<=42.3 and -9.6<=lon<=-6.0): continue
    if r['id'] in seen: continue
    seen.add(r['id'])
    name=title(r['name']);sports=r['sports'];low=name.lower()
    if 'piscina' in low or sports==['Natação']: vt='Piscina'
    elif 'pavilh' in low: vt='Pavilhão desportivo'
    elif set(sports)=={'Padel'}: vt='Clube de padel'
    elif set(sports)=={'Ténis'}: vt='Clube de ténis'
    elif 'Futebol' in sports and len(sports)==1: vt='Campo de futebol'
    else: vt='Complexo desportivo'
    indoor='TRUE' if vt=='Pavilhão desportivo' or 'coberto' in low and 'descoberto' not in low else 'FALSE'
    city=r.get('municipality') or None
    cp=(r.get('cp') or '').strip()
    if not re.fullmatch(r'\d{4}-\d{3}',cp): cp=''
    parts=[p for p in [(r.get('street') or '').strip(), ' '.join(x for x in [cp,city or ''] if x)] if p]
    addr=', '.join(parts) or 'Localização no mapa: %.5f, %.5f (morada por confirmar)'%(lat,lon)
    ref='SNID:%d'%r['id']
    out.append(f"""INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT {q(name)}, {q(city)}, {q(district(r))}, {q(addr)},
       {q(';'.join(sports))}, {q(vt)}, NULL,
       {lat:.7f}, {lon:.7f}, NULL, NULL, 'OFFICIAL', {q(ref)},
       {indoor}, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference={q(ref)}
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - ({lat:.7f})) <= 0.00063 AND ABS(v.longitude - ({lon:.7f})) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM({q(name)}))
         AND (v.latitude IS NULL OR (ABS(v.latitude - ({lat:.7f})) <= 0.0030 AND ABS(v.longitude - ({lon:.7f})) <= 0.0040)))
);
""")
    n+=1
open(sys.argv[2],'w').write('\n'.join(out))
print(n,'candidates')
