"""Read-only: shape of the official sports-facility datasets (fields, counts, samples)."""
import json, urllib.request, collections
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
SOURCES = {
    'lisboa': 'https://services.arcgis.com/1dSrzEWVQn5kHHyK/arcgis/rest/services/Desporto_Instalacoes/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson',
    'snid': 'https://laqsa01.blob.core.windows.net/downloads/snid-localizacao-de-instalacoes-desportivas-20260901.json',
    'apptiva': 'https://laqsa01.blob.core.windows.net/downloads/apptiva-localizacao-de-instalacoes-desportivas-20260830.json',
}
for name, url in SOURCES.items():
    try:
        req = urllib.request.Request(url, headers={'User-Agent': UA})
        with urllib.request.urlopen(req, timeout=120) as r:
            raw = r.read()
        data = json.loads(raw.decode('utf-8-sig'))
    except Exception as e:
        print('QA-WARN', name, type(e).__name__, str(e)[:200]); continue
    print('QA-SIZE', name, len(raw))
    if isinstance(data, dict) and 'features' in data:
        feats = data['features']
        print('QA-COUNT', name, len(feats), 'features; exceededTransferLimit=', data.get('exceededTransferLimit') or (data.get('properties') or {}).get('exceededTransferLimit'))
        for f in feats[:3]:
            print('QA-SAMPLE', name, json.dumps(f, ensure_ascii=False)[:900])
        keys = collections.Counter(k for f in feats for k in (f.get('properties') or {}))
        print('QA-KEYS', name, json.dumps(keys.most_common(60), ensure_ascii=False))
    else:
        items = data if isinstance(data, list) else next((v for v in data.values() if isinstance(v, list)), [])
        print('QA-COUNT', name, len(items), 'items; top keys', list(data.keys())[:10] if isinstance(data, dict) else 'list')
        for it in items[:3]:
            print('QA-SAMPLE', name, json.dumps(it, ensure_ascii=False)[:1200])
        keys = collections.Counter(k for it in items if isinstance(it, dict) for k in it)
        print('QA-KEYS', name, json.dumps(keys.most_common(60), ensure_ascii=False))
