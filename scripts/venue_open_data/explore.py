"""Read-only: lists municipal open-data datasets about sports facilities (CKAN portals)."""
import json, sys, urllib.parse, urllib.request
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
PORTALS = {
    'Lisboa': 'https://dados.cm-lisboa.pt/api/3/action/package_search',
    'Porto': 'https://opendata.porto.digital/api/3/action/package_search',
    'dados.gov': 'https://dados.gov.pt/api/1/datasets/',
}
TERMS = ['desport', 'equipamentos desportivos', 'instalações desportivas', 'piscinas', 'pavilh']
def get(url):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=60) as r:
        return json.loads(r.read().decode())
for name, base in PORTALS.items():
    seen = set()
    for term in TERMS:
        try:
            if 'dados.gov' in name:
                data = get(base + '?' + urllib.parse.urlencode({'q': term, 'page_size': 20}))
                items = [{'name': d.get('slug'), 'title': d.get('title'), 'org': (d.get('organization') or {}).get('name'),
                          'resources': [{'f': r.get('format'), 'u': r.get('url'), 't': r.get('title')} for r in d.get('resources', [])]} for d in data.get('data', [])]
            else:
                data = get(base + '?' + urllib.parse.urlencode({'q': term, 'rows': 20}))
                items = [{'name': d.get('name'), 'title': d.get('title'), 'org': (d.get('organization') or {}).get('title'),
                          'resources': [{'f': r.get('format'), 'u': r.get('url'), 't': r.get('name')} for r in d.get('resources', [])]} for d in data['result']['results']]
        except Exception as e:
            print('QA-WARN', name, term, type(e).__name__, str(e)[:100]); continue
        for it in items:
            if it['name'] in seen: continue
            seen.add(it['name'])
            print('QA-DS', name, json.dumps(it, ensure_ascii=False)[:1500])
