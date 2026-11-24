"""Read-only: find more open sports-facility datasets (municipal open data, IPDJ Apptiva) and describe them."""
import json, urllib.parse, urllib.request, collections, random
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
def get(url, raw=False):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=120) as r:
        b = r.read()
    return b if raw else json.loads(b.decode('utf-8-sig'))
seen = set()
for term in ['equipamentos desportivos', 'instalações desportivas', 'desporto equipamentos', 'campos de jogos',
             'piscinas', 'pavilhões desportivos', 'parques desportivos', 'equipamentos de utilização coletiva desporto',
             'carta desportiva municipal', 'polidesportivos']:
    for page in (1, 2):
        try:
            d = get('https://dados.gov.pt/api/1/datasets/?' + urllib.parse.urlencode({'q': term, 'page_size': 40, 'page': page}))
        except Exception as e:
            print('QA-WARN', term, e); break
        for ds in d.get('data', []):
            if ds['id'] in seen: continue
            seen.add(ds['id'])
            res = [(r.get('format'), r.get('url')) for r in ds.get('resources', [])][:4]
            print('QA-DS', '|', ds.get('title'), '|', ds.get('license'), '|', (ds.get('organization') or {}).get('name'), '|', ds.get('page'), '|', json.dumps(res, ensure_ascii=False)[:500])
try:
    text = get('https://laqsa01.blob.core.windows.net/downloads/apptiva-localizacao-de-instalacoes-desportivas-20260830.json', raw=True).decode('utf-8-sig')
    rows = [json.loads(l) for l in text.splitlines() if l.strip()]
    print('QA-APPTIVA-COUNT', len(rows))
    print('QA-APPTIVA-KEYS', json.dumps(collections.Counter(k for r in rows for k in r).most_common(30), ensure_ascii=False))
    print('QA-APPTIVA-TYPES', json.dumps(collections.Counter(str(r.get('type')) for r in rows).most_common(30), ensure_ascii=False))
    print('QA-APPTIVA-PROVIDERS', json.dumps(collections.Counter(str(r.get('dataProvider')) for r in rows).most_common(10), ensure_ascii=False))
    for r in random.Random(3).sample(rows, 6):
        print('QA-APPTIVA-SAMPLE', json.dumps(r, ensure_ascii=False)[:900])
except Exception as e:
    print('QA-WARN apptiva', e)
