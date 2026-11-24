"""Read-only: download municipal open sports-facility data (Cascais CC BY, Águeda CC BY) and print rows."""
import json, urllib.request, re, collections, zipfile, io
UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
def get(url):
    req = urllib.request.Request(url, headers={'User-Agent': UA})
    with urllib.request.urlopen(req, timeout=180) as r:
        return r.read()
# Cascais: resolve current resources via the CKAN API (the file name carries a date)
try:
    meta = json.loads(get('https://dadosabertos.cascais.pt/api/3/action/package_show?id=d7097be7-8982-4be5-b28f-88eca119fdcc'))
    res = [r for r in meta['result']['resources'] if (r.get('format') or '').lower() == 'geojson']
    print('QA-CASCAIS-LICENSE', meta['result'].get('license_id'), meta['result'].get('license_title'))
    url = res[0]['url']
except Exception as e:
    print('QA-WARN cascais meta', e)
    url = 'https://dadosabertos.cascais.pt/dataset/d7097be7-8982-4be5-b28f-88eca119fdcc/resource/de595b77-b9b3-45d7-abd5-cf1a0ab98439/download/geocascais-equipamentodesportivo_20261123_171116.geojson'
try:
    g = json.loads(get(url))
    feats = g.get('features', [])
    print('QA-CASCAIS-COUNT', len(feats))
    print('QA-CASCAIS-KEYS', json.dumps(collections.Counter(k for f in feats for k in (f.get('properties') or {})).most_common(40), ensure_ascii=False))
    print('QA-CASCAIS-GEOM', json.dumps(collections.Counter((f.get('geometry') or {}).get('type') for f in feats).most_common(), ensure_ascii=False))
    for f in feats:
        pts = []
        def walk(c):
            if isinstance(c[0], (int, float)): pts.append(c)
            else:
                for x in c: walk(x)
        walk((f.get('geometry') or {}).get('coordinates') or [])
        lon = sum(p[0] for p in pts) / len(pts) if pts else None
        lat = sum(p[1] for p in pts) / len(pts) if pts else None
        print('QA-CASCAIS', json.dumps({'p': f.get('properties'), 'lat': lat, 'lon': lon}, ensure_ascii=False))
except Exception as e:
    print('QA-WARN cascais', e)
try:
    meta = json.loads(get('https://dadosabertos.cm-agueda.pt/api/3/action/package_show?id=fe9ae891-844e-4b02-9667-67440d13e9db'))
    print('QA-AGUEDA-LICENSE', meta['result'].get('license_id'))
    for r in meta['result']['resources']:
        print('QA-AGUEDA-RES', r.get('format'), r.get('url'))
    kml = [r['url'] for r in meta['result']['resources'] if (r.get('format') or '').lower() in ('kml', 'kmz', 'geojson', 'json', 'csv')]
    if kml:
        b = get(kml[0])
        if b[:2] == b'PK':
            b = zipfile.ZipFile(io.BytesIO(b)).read([n for n in zipfile.ZipFile(io.BytesIO(b)).namelist() if n.endswith('.kml')][0])
        t = b.decode('utf-8', 'replace')
        for pm in re.findall(r'<Placemark>(.*?)</Placemark>', t, re.S):
            name = re.search(r'<name>(.*?)</name>', pm, re.S)
            data = dict(re.findall(r'<SimpleData name="([^"]+)">(.*?)</SimpleData>', pm, re.S)) or dict(re.findall(r'<Data name="([^"]+)">\s*<value>(.*?)</value>', pm, re.S))
            coords = re.search(r'<coordinates>\s*([-\d.]+),([-\d.]+)', pm)
            print('QA-AGUEDA', json.dumps({'name': name.group(1).strip() if name else None, 'data': data,
                  'lon': float(coords.group(1)) if coords else None, 'lat': float(coords.group(2)) if coords else None}, ensure_ascii=False)[:1200])
except Exception as e:
    print('QA-WARN agueda', e)
