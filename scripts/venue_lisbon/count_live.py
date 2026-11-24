import json,urllib.request
u='https://api.jynlosports.com/api/venues'
d=json.loads(urllib.request.urlopen(urllib.request.Request(u,headers={'User-Agent':'JynloQA'}),timeout=60).read())
print('QA-TOTAL',len(d))
print('QA-LISBON-OFFICIAL',sum(1 for v in d if str(v.get('sourceReference') or v.get('source_reference') or '').startswith('CML:DESPORTO')))
print('QA-OFFICIAL',sum(1 for v in d if (v.get('informationSource') or '')=='OFFICIAL'))
print('QA-KEYS',list(d[0].keys())[:30])
