"""Read-only: Lisbon municipal sports facilities (CML Geodados) as compact QA-ROW lines, plus licence info."""
import json, urllib.parse, urllib.request
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
BASE = 'https://services.arcgis.com/1dSrzEWVQn5kHHyK/arcgis/rest/services/Desporto_Instalacoes/FeatureServer/0/query'
def get(url):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=120) as r:
        return json.loads(r.read().decode('utf-8-sig'))
offset = 0
total = 0
while True:
    q = {'where': '1=1', 'outFields': '*', 'outSR': '4326', 'f': 'json', 'resultOffset': offset, 'resultRecordCount': 1000}
    data = get(BASE + '?' + urllib.parse.urlencode(q))
    feats = data.get('features', [])
    for f in feats:
        a = f.get('attributes') or {}
        g = f.get('geometry') or {}
        row = {k: a.get(k) for k in ('OBJECTID', 'DESIGNACAO', 'MORADA', 'CODIGOPOSTAL', 'FREGUESIA', 'TIPOLOGIA', 'SUBTIPOLOGIA', 'CATEGORIA', 'ENT_GESTORA', 'CONTACTO', 'TELEFONE', 'EMAIL', 'SITE', 'WEBSITE')}
        row['lat'] = g.get('y'); row['lon'] = g.get('x')
        print('QA-ROW', json.dumps(row, ensure_ascii=False))
    total += len(feats)
    if len(feats) < 1000 and not data.get('exceededTransferLimit'):
        break
    offset += len(feats)
print('QA-TOTAL', total)
for term in ['instalações desportivas lisboa', 'desporto instalacoes lisboa']:
    try:
        d = get('https://dados.gov.pt/api/1/datasets/?' + urllib.parse.urlencode({'q': term, 'page_size': 10}))
        for ds in d.get('data', []):
            print('QA-LICENSE', ds.get('slug'), '|', ds.get('title'), '|', ds.get('license'), '|', (ds.get('organization') or {}).get('name'))
    except Exception as e:
        print('QA-WARN license', e)
