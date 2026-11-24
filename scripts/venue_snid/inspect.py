"""Read-only: licence and data quality of the national sports facilities dataset (SNID)."""
import json, urllib.parse, urllib.request, collections, math, random
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
URL = 'https://laqsa01.blob.core.windows.net/downloads/snid-localizacao-de-instalacoes-desportivas-20260901.json'
def get(url, raw=False):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=180) as r:
        b = r.read()
    return b if raw else json.loads(b.decode('utf-8-sig'))
for term in ['snid', 'localização de instalações desportivas', 'instalações desportivas', 'carta desportiva']:
    try:
        d = get('https://dados.gov.pt/api/1/datasets/?' + urllib.parse.urlencode({'q': term, 'page_size': 12}))
        for ds in d.get('data', []):
            res = [r.get('url') for r in ds.get('resources', [])][:3]
            print('QA-LICENSE', term, '|', ds.get('slug'), '|', ds.get('title'), '|', ds.get('license'), '|', (ds.get('organization') or {}).get('name'), '|', ds.get('page'), '|', res)
    except Exception as e:
        print('QA-WARN', term, e)
text = get(URL, raw=True).decode('utf-8-sig')
rows = [json.loads(l) for l in text.splitlines() if l.strip()]
print('QA-COUNT', len(rows))
print('QA-KEYS', json.dumps(collections.Counter(k for r in rows for k in r).most_common(40), ensure_ascii=False))
for r in random.Random(7).sample(rows, 8):
    print('QA-SAMPLE', json.dumps(r, ensure_ascii=False)[:700])
def loc(r):
    l = r.get('location')
    if isinstance(l, str):
        try: l = json.loads(l)
        except Exception: return None
    if isinstance(l, dict) and l.get('coordinates'):
        lon, lat = l['coordinates'][:2]
        return float(lat), float(lon)
    return None
pts = [loc(r) for r in rows]
ok = [p for p in pts if p]
print('QA-WITH-COORDS', len(ok))
mainland = [p for p in ok if 36.8 <= p[0] <= 42.3 and -9.6 <= p[1] <= -6.0]
islands = [p for p in ok if (32.3 <= p[0] <= 33.2 and -17.4 <= p[1] <= -16.2) or (36.8 <= p[0] <= 39.8 and -31.5 <= p[1] <= -24.9)]
print('QA-MAINLAND', len(mainland), 'QA-ISLANDS', len(islands), 'QA-OUTSIDE', len(ok) - len(mainland) - len(islands))
dist = collections.Counter(((r.get('address') or {}).get('district') or '').strip() for r in rows)
print('QA-DISTRICTS', json.dumps(dist.most_common(30), ensure_ascii=False))
# District vs coordinates sanity: rough district centroids from the data itself (median).
by = collections.defaultdict(list)
for r, p in zip(rows, pts):
    d = ((r.get('address') or {}).get('district') or '').strip()
    if p and d: by[d].append(p)
med = {d: (sorted(x[0] for x in v)[len(v)//2], sorted(x[1] for x in v)[len(v)//2]) for d, v in by.items() if len(v) > 20}
far = 0
for d, v in by.items():
    if d not in med: continue
    c = med[d]
    far += sum(1 for p in v if math.hypot((p[0]-c[0])*111, (p[1]-c[1])*85) > 120)
print('QA-FAR-FROM-DISTRICT(>120km)', far)
names = collections.Counter((r.get('name') or '').strip().lower() for r in rows)
print('QA-TOP-NAMES', json.dumps(names.most_common(25), ensure_ascii=False))
types = collections.Counter(str(r.get(k)) for r in rows for k in ('type', 'tipo', 'category', 'facilityType') if k in r)
print('QA-TYPES', json.dumps(types.most_common(30), ensure_ascii=False))
