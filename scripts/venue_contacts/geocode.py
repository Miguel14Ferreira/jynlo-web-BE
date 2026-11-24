"""Geocodes the venues we contacted (their own public addresses) with OpenStreetMap
Nominatim and flags those already in the Jynlo directory. Read-only: prints QA-GEO
lines for review; nothing is written anywhere."""
import json, re, sys, time, unicodedata, urllib.parse, urllib.request

UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'

def get(url):
    req = urllib.request.Request(url, headers={'User-Agent': UA, 'Accept-Language': 'pt'})
    with urllib.request.urlopen(req, timeout=30) as r:
        return json.loads(r.read().decode())

def fold(s):
    s = unicodedata.normalize('NFD', s or '').encode('ascii', 'ignore').decode().lower()
    return re.sub(r'\s+', ' ', re.sub(r'[^a-z0-9 ]', ' ', s)).strip()

venues = get('https://api.jynlosports.com/api/venues')
print('QA-INFO venues', len(venues))
rows = json.load(open(sys.argv[1]))
for i, x in enumerate(rows):
    fn = fold(x['n'])
    dup = None
    for v in venues:
        vn = fold(v.get('name'))
        if vn and fn and (vn == fn or (len(fn) > 8 and len(vn) > 8 and (fn in vn or vn in fn))):
            dup = {'id': v['id'], 'name': v['name'], 'city': v.get('city')}
            break
    g = None
    queries = []
    a = x.get('a') or ''
    if a:
        queries.append(('address', a if re.search('portugal', a, re.I) else a + ', Portugal'))
        # Keep only the street part that has a number and the postcode + place.
        parts = [p.strip() for p in re.sub(r',?\s*Portugal\s*$', '', a, flags=re.I).split(',') if p.strip()]
        pc = re.search(r'\b(\d{4}-\d{3})\b\s*([^,]*)', a)
        street = next((p for p in parts if re.search(r'^(R\.|Rua|Av\.|Avenida|Estrada|Tv\.|Travessa|Largo|Praça|Alameda)', p, re.I)), None)
        if street and pc:
            queries.append(('street', street + ', ' + pc.group(1) + ' ' + pc.group(2) + ', Portugal'))
        if pc:
            queries.append(('postcode', pc.group(1) + ', Portugal'))
    queries.append(('name', x['n'] + (', ' + x['c'] if x.get('c') else '') + ', Portugal'))
    for how, q in queries:
        try:
            res = get('https://nominatim.openstreetmap.org/search?format=jsonv2&countrycodes=pt&limit=1&addressdetails=1&q=' + urllib.parse.quote(q))
        except Exception as e:
            res = []
        time.sleep(1.2)
        if res:
            r = res[0]
            g = {'how': how, 'lat': float(r['lat']), 'lon': float(r['lon']), 'q': q, 'type': r.get('type'), 'cls': r.get('category'),
                 'ad': {k: r['address'].get(k) for k in ('road', 'house_number', 'postcode', 'city', 'town', 'village', 'municipality', 'county', 'state', 'state_district', 'archipelago') if r['address'].get(k)}}
            break
    print('QA-GEO', json.dumps({'i': i, 'dup': dup, 'g': g}, ensure_ascii=False))
