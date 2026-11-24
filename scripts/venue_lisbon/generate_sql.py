import json,collections,unicodedata,re
rows=[json.loads(l[7:]) for l in open('out.txt') if l.startswith('QA-ROW ')]
SKIP_CAT={'Estabelecimento de ensino da rede pública (JI/1º a 12º ano)'}
KEEP_TIPO={'Pequeno Campo de Jogos','Grande Campo de Jogos','Pavilhão Desportivo','Piscina','Instalação de Atletismo','Estrutura Artificial de Escalada','Instalação de Golfe','Instalação de Ciclismo','Instalação de Tiro com Arco','Sala de Desporto','Infraestrutura de Desportos Náuticos'}
def sports_for(t,s):
    s=(s or '');low=s.lower();out=[]
    if t=='Sala de Desporto':
        if 'squash' in low: return ['Squash']
        if 'judo' in low: return ['Judo']
        if 'karat' in low: return ['Karaté']
        if 'boxe' in low: return ['Boxe']
        if 'ginástica' in low: return ['Ginástica']
        return None  # fitness rooms, dance studios, etc.: not added
    if 'ginásio ao ar livre' in low: return None
    if 'ténis' in low and 'mesa' not in low: out.append('Ténis')
    if 'padel' in low: out.append('Padel')
    if 'futebol' in low: out.append('Futebol')
    if 'polidesportivo' in low or 'minicampo' in low: out+=['Futsal','Basquetebol']
    if 'basquet' in low: out.append('Basquetebol')
    if 'voleib' in low: out.append('Voleibol')
    if 'andebol' in low: out.append('Andebol')
    if 'rugby' in low or 'râguebi' in low: out.append('Rugby')
    if 'hóquei' in low: out.append('Hóquei')
    if 'skate' in low: out.append('Skate')
    if t=='Pavilhão Desportivo': out+=['Futsal','Basquetebol','Voleibol','Andebol']
    if t=='Piscina': out.append('Natação')
    if t=='Instalação de Atletismo': out.append('Atletismo')
    if t=='Estrutura Artificial de Escalada': out.append('Escalada')
    if t=='Instalação de Golfe': out.append('Golfe')
    if t=='Instalação de Ciclismo': out.append('Ciclismo')
    if t=='Instalação de Tiro com Arco': out.append('Tiro com Arco')
    if t=='Infraestrutura de Desportos Náuticos': out+=['Vela','Canoagem']
    if t=='Grande Campo de Jogos' and not out: out.append('Futebol')
    if t=='Pequeno Campo de Jogos' and not out: out+=['Futsal']
    return out or None
groups=collections.OrderedDict()
for r in rows:
    if r['CATEGORIA'] in SKIP_CAT or r['TIPOLOGIA'] not in KEEP_TIPO: continue
    sp=sports_for(r['TIPOLOGIA'],r['SUBTIPOLOGIA'])
    if not sp: continue
    name=(r['DESIGNACAO'] or '').strip()
    if len(name)<4: continue
    key=(unicodedata.normalize('NFKD',name.casefold()),(r['MORADA'] or '').strip().casefold())
    g=groups.setdefault(key,{'name':name,'addr':(r['MORADA'] or '').strip(),'cp':(r['CODIGOPOSTAL'] or '').strip(),'fr':r['FREGUESIA'],'ids':[],'sports':[],'tipos':set(),'subs':set(),'lat':[],'lon':[]})
    g['ids'].append(r['OBJECTID']);g['tipos'].add(r['TIPOLOGIA']);g['subs'].add(r['SUBTIPOLOGIA'] or '')
    for s in sp:
        if s not in g['sports']: g['sports'].append(s)
    g['lat'].append(r['lat']);g['lon'].append(r['lon'])
def q(v): return 'NULL' if v is None else "'"+str(v).replace("'","''")+"'"
out=['-- Lisbon municipal sports facilities (Câmara Municipal de Lisboa, Geodados "Instalações Desportivas", CC0).',
     '-- Public schools and fitness rooms are left out. One venue per facility; skipped when any venue already sits within ~70 m.','']
n=0
for g in groups.values():
    lat=sum(g['lat'])/len(g['lat']);lon=sum(g['lon'])/len(g['lon'])
    if not (36.8<=lat<=42.3 and -9.6<=lon<=-6.0): continue
    tipos=g['tipos'];sports=g['sports']
    if tipos=={'Piscina'}: vt='Piscina'
    elif tipos=={'Pavilhão Desportivo'}: vt='Pavilhão desportivo'
    elif set(sports)=={'Padel'}: vt='Clube de padel'
    elif set(sports)=={'Ténis'}: vt='Clube de ténis'
    else: vt='Complexo desportivo'
    indoor='TRUE' if any('coberto' in s.lower() and 'descoberto' not in s.lower() for s in g['subs']) or 'Pavilhão Desportivo' in tipos else 'FALSE'
    addr=(g['addr']+(' '+g['cp'] if g['cp'] else '')).strip() or 'Localização no mapa: %.5f, %.5f (morada por confirmar)'%(lat,lon)
    ref='CML:DESPORTO:%d'%min(g['ids'])
    out.append(f"""INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT {q(g['name'])}, 'Lisboa', 'Lisboa', {q(addr)},
       {q(';'.join(sports))}, {q(vt)}, NULL,
       {lat:.7f}, {lon:.7f}, NULL, NULL, 'OFFICIAL', {q(ref)},
       {indoor}, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference={q(ref)}
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - ({lat:.7f})) <= 0.00063 AND ABS(v.longitude - ({lon:.7f})) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM({q(g['name'])}))
         AND (v.latitude IS NULL OR (ABS(v.latitude - ({lat:.7f})) <= 0.0030 AND ABS(v.longitude - ({lon:.7f})) <= 0.0040)))
);
""")
    n+=1
open('lisbon.sql','w').write('\n'.join(out))
print(n,'venues')
c=collections.Counter(s for g in groups.values() for s in g['sports']); print(c.most_common(20))
print([g['name'] for g in list(groups.values())[:15]])
