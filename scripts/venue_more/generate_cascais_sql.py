"""Liquibase SQL for Cascais municipal sports facilities (Câmara Municipal de Cascais, CC BY).
Usage: python3 scripts/venue_more/generate_cascais_sql.py cascais.jsonl out.sql"""
import json,sys,re,html,unicodedata
def fold(s): return ''.join(c for c in unicodedata.normalize('NFKD',(s or '').casefold()) if not unicodedata.combining(c))
RULES=[(r'piscina|natacao',['Natação']),(r'pavilh',['Futsal','Basquetebol','Voleibol','Andebol']),(r'\bpadel\b',['Padel']),
 (r'beach tennis',['Ténis de praia']),(r'\btenis\b|racquet',['Ténis']),(r'polidesportiv|ringue|campo de jogos',['Futsal','Basquetebol']),
 (r'street basket',['Basquetebol']),(r'campo de futebol|campo +futebol|parque desportivo|foot-ball',['Futebol']),
 (r'voleibol|campos? de areia',['Voleibol de Praia']),(r'atletismo',['Atletismo']),(r'skate',['Skate']),(r'escalada',['Escalada']),
 (r'golf',['Golfe']),(r'hipic|hipodromo',['Hipismo']),(r'naval|nautic',['Vela']),(r'surf',['Surf']),(r'teqball',['Teqball']),
 (r'patinagem',['Patinagem']),(r'petanca',['Petanca']),(r'mountain bike',['Ciclismo']),(r'autodromo',['Automobilismo']),(r'bowling',['Bowling'])]
SKIP=re.compile(r'\b(escola|esc\. e\.b|colegio|school|externato|sede|prisional|bombeiros|medicina|aldeia de criancas|fitness|yoga|workout|wellness|health|ginasio|gymnasium|crossfit|vivafit|estalagem|campismo|cercica|espondilite|derose|perfolie|sulydance|geriatrico|lynxrace|ocr|hotel|marina|mesa teqball|academia de artes)\b|^mr\. big|^teqball$|^campo de areia$|^pista de atletismo$|^ringue de patinagem$')
def q(v): return 'NULL' if v in (None,'') else "'"+str(v).replace("'","''")+"'"
out=['-- Cascais sports facilities (Câmara Municipal de Cascais, dados abertos "Equipamentos Desportivos", CC BY).',
     '-- Schools, gyms, club headquarters and fitness circuits are left out. Skipped when a venue already sits within ~70 m or has the same name nearby.','']
n=0
for l in open(sys.argv[1]):
    r=json.loads(l);p=r['p'];name=re.sub(r'\s+',' ',html.unescape(p['nome'])).strip();f=fold(name)
    if SKIP.search(f) or r['lat'] is None: continue
    sports=[]
    for pat,vals in RULES:
        if re.search(pat,f):
            for v in vals:
                if v not in sports: sports.append(v)
    if not sports: continue
    lat,lon=r['lat'],r['lon']
    if 'Natação' in sports and len(sports)==1: vt='Piscina'
    elif 'pavilh' in f: vt='Pavilhão desportivo'
    elif sports==['Padel']: vt='Clube de padel'
    elif sports==['Ténis']: vt='Clube de ténis'
    elif sports==['Futebol']: vt='Campo de futebol'
    elif sports==['Golfe']: vt='Campo de golfe'
    else: vt='Complexo desportivo'
    street=' '.join(x for x in [(p.get('rua') or '').strip(),(p.get('numero_policia') or '').strip()] if x)
    addr=', '.join(x for x in [street,(p.get('codigo_postal') or '').strip()] if x) or None
    phone=(p.get('telefone') or '').strip() or None
    ref='CMC:DESPORTO:'+re.sub(r'[^a-z0-9]+','-',f).strip('-')[:60]
    out.append(f"""INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT {q(name)}, 'Cascais', 'Lisboa', {q(addr)},
       {q(';'.join(sports))}, {q(vt)}, NULL,
       {lat:.7f}, {lon:.7f}, NULL, {q(phone)}, 'OFFICIAL', {q(ref)},
       {'TRUE' if vt=='Pavilhão desportivo' else 'FALSE'}, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference={q(ref)}
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - ({lat:.7f})) <= 0.00063 AND ABS(v.longitude - ({lon:.7f})) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM({q(name)}))
         AND (v.latitude IS NULL OR (ABS(v.latitude - ({lat:.7f})) <= 0.0030 AND ABS(v.longitude - ({lon:.7f})) <= 0.0040)))
);
""");n+=1
open(sys.argv[2],'w').write('\n'.join(out));print(n)
