"""Sports venues of Madeira and the Azores from OpenStreetMap (ODbL), read-only.

The national importer only covers mainland districts. This walks each island
municipality through the Overpass API, applies the same filters and mapping as
scripts/import_osm_venues_national.py, and prints one QA-ROW JSON line per venue.
Nothing is written anywhere.
"""
import json, sys, time, urllib.parse, urllib.request
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import import_osm_venues_national as nat  # noqa: E402

UA = 'JynloVenueResearch/1.0 (+https://jynlosports.com; geral@jynlosports.com)'
OVERPASS = ['https://overpass-api.de/api/interpreter', 'https://overpass.kumi.systems/api/interpreter',
            'https://maps.mail.ru/osm/tools/overpass/api/interpreter']
# Bounding boxes (south, west, north, east) of each archipelago.
REGIONS = {'Madeira': (32.3, -17.4, 33.2, -16.1), 'Açores': (36.8, -31.6, 39.9, -24.8)}


def overpass(query):
    for attempt in range(6):
        try:
            data = urllib.parse.urlencode({'data': query}).encode()
            req = urllib.request.Request(OVERPASS[attempt % len(OVERPASS)], data=data, headers={'User-Agent': UA})
            with urllib.request.urlopen(req, timeout=180) as r:
                return json.loads(r.read().decode())
        except Exception as e:  # rate limit or timeout: wait and retry
            print('QA-WARN', type(e).__name__, str(e)[:120], file=sys.stderr)
            time.sleep(10 * (attempt + 1))
    return {'elements': []}


def municipalities(bbox):
    s, w, n, e = bbox
    q = f'''[out:json][timeout:120];
rel["boundary"="administrative"]["admin_level"="7"]({s},{w},{n},{e});
out tags;'''
    return [(e['id'], e['tags'].get('name')) for e in overpass(q)['elements'] if e.get('tags', {}).get('name')]


def venues(rel_id):
    area_id = 3600000000 + rel_id
    q = f'''[out:json][timeout:180];
area({area_id})->.m;
(
  nwr["leisure"~"^(sports_centre|pitch|stadium|track|swimming_pool|fitness_centre|golf_course|horse_riding|ice_rink)$"]["name"](area.m);
  nwr["building"="sports_hall"]["name"](area.m);
);
out center tags;'''
    return overpass(q)['elements']


seen = set()
for district, region in REGIONS.items():
    munis = municipalities(region)
    if not munis:
        print('QA-WARN no municipalities for', district, file=sys.stderr)
    print('QA-INFO', district, len(munis), 'municipalities')
    for rel_id, muni in munis:
        time.sleep(2)
        n = 0
        for e in venues(rel_id):
            key = (e['type'], e['id'])
            if key in seen:
                continue
            seen.add(key)
            tags = e.get('tags', {})
            if e['type'] == 'node':
                coords = (e.get('lat'), e.get('lon'))
            else:
                c = e.get('center') or {}
                coords = (c.get('lat'), c.get('lon')) if c else None
            if not coords or coords[0] is None:
                continue
            name = (tags.get('name:pt') or tags.get('name') or '').strip()
            if not nat.is_identifiable_name(name):
                continue
            if (tags.get('access') or '').lower() in nat.DENY_ACCESS or any(tags.get(k) for k in nat.DENY_STATUS):
                continue
            leisure = (tags.get('leisure') or '').lower()
            kind = nat.LEISURE.get(leisure) or ('Pavilhão desportivo' if tags.get('building') == 'sports_hall' else None)
            if not kind:
                continue
            raw = (tags.get('sport') or '').replace(',', ';')
            sports = list(dict.fromkeys(nat.SPORTS[k.strip().lower()] for k in raw.split(';') if k.strip().lower() in nat.SPORTS))
            if not sports and leisure in nat.INFER:
                sports = [nat.INFER[leisure]]
            street = ' '.join(str(tags.get(k) or '').strip() for k in ('addr:street', 'addr:housenumber')).strip()
            postal = (tags.get('addr:postcode') or '').strip()
            indoor = (tags.get('indoor') or '').lower()
            env = 'INDOOR' if indoor in ('yes', 'true', '1') or tags.get('building') == 'sports_hall' else ('OUTDOOR' if indoor in ('no', 'false', '0') else None)
            row = {
                'ref': f"OSM:{e['type'].upper()}:{e['id']}", 'name': name[:140], 'city': muni, 'district': district,
                'address': ', '.join(v for v in (street, postal, muni) if v) if street else None,
                'sports': ';'.join(sports) or None, 'type': kind, 'env': env,
                'lat': round(coords[0], 7), 'lon': round(coords[1], 7),
                'website': (tags.get('website') or tags.get('contact:website') or '')[:255] or None,
                'phone': (tags.get('phone') or tags.get('contact:phone') or '')[:60] or None,
            }
            print('QA-ROW', json.dumps(row, ensure_ascii=False))
            n += 1
        print('QA-INFO', district, muni, n)
