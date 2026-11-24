"""Read-only: SNID facilities worth adding (identifiable name + sport we can infer), optionally with the
municipality from OSM Nominatim (1 request/second). Prints QA-ROW json lines.
Data: IPDJ - Sistema Nacional de Informação Desportiva (SNID), CC BY."""
import json, os, re, sys, time, unicodedata, urllib.parse, urllib.request, collections
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
URL = 'https://laqsa01.blob.core.windows.net/downloads/snid-localizacao-de-instalacoes-desportivas-20260901.json'
GEOCODE = os.environ.get('GEOCODE') == '1'
def fold(s):
    return ''.join(c for c in unicodedata.normalize('NFKD', (s or '').casefold()) if not unicodedata.combining(c))
RULES = [
    (r'\bpiscina', ['Natação']),
    (r'\bpavilh', ['Futsal', 'Basquetebol', 'Voleibol', 'Andebol']),
    (r'\bpadel\b', ['Padel']),
    (r'\btenis\b(?! de mesa)', ['Ténis']),
    (r'\bpolidesportiv|\bringue\b|\bpequeno campo|\bmini ?campo|\bcampo de jogos', ['Futsal']),
    (r'\bcampo de futebol|\bestadio\b|\bgrande campo|\bcampo de futebol 11|\bcampo sintetico|\bcampo relvado', ['Futebol']),
    (r'\batletismo\b|\bpista de atletismo', ['Atletismo']),
    (r'\bskate|\bskatepark', ['Skate']),
    (r'\bescalada|\brocodromo', ['Escalada']),
    (r'\bgolfe?\b', ['Golfe']),
    (r'\bsquash\b', ['Squash']),
    (r'\bbasquet', ['Basquetebol']),
    (r'\bvoleibol|\bvolei\b', ['Voleibol']),
    (r'\brugby|\braguebi', ['Rugby']),
    (r'\bhoquei', ['Hóquei']),
    (r'\bbtt\b|\bciclismo|\bvelodromo|\bbmx\b', ['Ciclismo']),
    (r'\bremo\b|\bcanoagem|\bnautic', ['Canoagem']),
    (r'\bkarting|\bkartodromo', ['Karting']),
    (r'\btiro com arco', ['Tiro com Arco']),
]
SKIP = re.compile(r'\b(escola|eb ?\d|eb\d|e\.b\.|agrupamento|colegio|jardim de infancia|ginasio ao ar livre|circuito de manutencao|parque infantil|sala de desporto|centro escolar|universidade|faculdade|instituto superior|seminario|prisao|estabelecimento prisional|quartel)\b')
GENERIC = re.compile(r'^(pequeno campo de jogos( nr\.?)?( \d+)?|grande campo de jogos|polidesportivo( descoberto| coberto)?|pavilhao( desportivo| polivalente| gimnodesportivo)?( municipal)?|piscinas? (municipa(l|is))?( cobertas?| descobertas?)?|campo de futebol( \d+)?|campo de tenis|patio desportivo|complexo desportivo( municipal)?|ringue|campo de jogos)$')
def get(url, raw=False):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=180) as r:
        b = r.read()
    return b if raw else json.loads(b.decode('utf-8-sig'))
rows = [json.loads(l) for l in get(URL, raw=True).decode('utf-8-sig').splitlines() if l.strip()]
seen = set(); out = []; reasons = collections.Counter()
for r in rows:
    name = (r.get('name') or '').strip()
    f = fold(name)
    if len(f) < 6: reasons['no-name'] += 1; continue
    if SKIP.search(f): reasons['school-or-equipment'] += 1; continue
    if GENERIC.match(f.strip()): reasons['generic-name'] += 1; continue
    sports = []
    for pattern, values in RULES:
        if re.search(pattern, f):
            for v in values:
                if v not in sports: sports.append(v)
    if not sports: reasons['no-sport-in-name'] += 1; continue
    try:
        loc = r['location']; loc = json.loads(loc) if isinstance(loc, str) else loc
        lon, lat = float(loc['coordinates'][0]), float(loc['coordinates'][1])
    except Exception:
        reasons['no-coords'] += 1; continue
    key = (f, round(lat, 4), round(lon, 4))
    if key in seen: reasons['duplicate'] += 1; continue
    seen.add(key)
    a = r.get('address') or {}
    out.append({'id': r.get('id'), 'name': name, 'sports': sports, 'lat': lat, 'lon': lon,
                'street': (a.get('streetAddress') or '').strip(), 'cp': (a.get('postalCode') or '').strip(),
                'district': (a.get('district') or '').strip()})
print('QA-REASONS', json.dumps(reasons, ensure_ascii=False))
print('QA-CANDIDATES', len(out))
limit = int(os.environ.get('LIMIT', '0') or 0)
if limit: out = out[:limit]
for i, c in enumerate(out):
    if GEOCODE:
        try:
            d = get('https://nominatim.openstreetmap.org/reverse?' + urllib.parse.urlencode(
                {'format': 'jsonv2', 'lat': c['lat'], 'lon': c['lon'], 'zoom': 10, 'accept-language': 'pt'}))
            ad = d.get('address') or {}
            c['municipality'] = ad.get('municipality') or ad.get('city') or ad.get('town') or ad.get('county')
            c['state'] = ad.get('state') or ad.get('county')
        except Exception as e:
            c['municipality'] = None
        time.sleep(1.05)
    print('QA-ROW', json.dumps(c, ensure_ascii=False), flush=True)
