-- Madeira and Azores sports venues from OpenStreetMap (© OpenStreetMap contributors, ODbL).
-- Map references, not proof of public access or bookings. Municipality from OSM Nominatim.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Faial', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7870461, -16.8524195, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1493350081',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1493350081')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7870461)) <= 0.00070 AND ABS(v.longitude - (-16.8524195)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Ilha', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.8112336, -16.9120531, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1493437586',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1493437586')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Ilha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8112336)) <= 0.00070 AND ABS(v.longitude - (-16.9120531)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'União Desportiva de Santana', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.8047917, -16.8786691, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1509193521',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1509193521')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('União Desportiva de Santana'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8047917)) <= 0.00070 AND ABS(v.longitude - (-16.8786691)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Manta Diving Center', 'Caniço', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.641106, -16.8329227, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1513093575',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1513093575')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Manta Diving Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.641106)) <= 0.00070 AND ABS(v.longitude - (-16.8329227)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Onda Revital Fitness Club', 'Caniço', 'Madeira', NULL,
       'Ginástica', 'Complexo desportivo', NULL,
       32.6422868, -16.8315317, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1744086379',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1744086379')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Onda Revital Fitness Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6422868)) <= 0.00070 AND ABS(v.longitude - (-16.8315317)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Porto da Cruz', 'Machico', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7718899, -16.8312125, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1767442147',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1767442147')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Porto da Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7718899)) <= 0.00070 AND ABS(v.longitude - (-16.8312125)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Desportiva de Machico', 'Machico', 'Madeira', 'Rua Estacada 9, 9200-091, Machico',
       NULL, 'Complexo desportivo', NULL,
       32.7189761, -16.7671614, 'https://www.admachico.com/', '+351 291 965 597', 'OPENSTREETMAP', 'OSM:NODE:1777061606',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1777061606')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Desportiva de Machico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7189761)) <= 0.00070 AND ABS(v.longitude - (-16.7671614)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Desportivo multiusos', 'Machico', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7435933, -16.7106677, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2119505238',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2119505238')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Desportivo multiusos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7435933)) <= 0.00070 AND ABS(v.longitude - (-16.7106677)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Big Body Gym', 'Funchal', 'Madeira', 'Rua da Carne Azeda 2 DD, Funchal',
       'Judo', 'Complexo desportivo', NULL,
       32.6539626, -16.9110592, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2167984612',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2167984612')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Big Body Gym'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6539626)) <= 0.00070 AND ABS(v.longitude - (-16.9110592)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Living Funchal Health Club', 'Funchal', 'Madeira', 'Rua Engenheiro Francisco Perry Vidal nº5 Bloco VI, Piso-1 BH, 9050-054, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6563841, -16.9087842, 'http://livingfunchalhealthclub.com/', '+351 291 244 116', 'OPENSTREETMAP', 'OSM:NODE:2167984616',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2167984616')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Living Funchal Health Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6563841)) <= 0.00070 AND ABS(v.longitude - (-16.9087842)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pistas de Machico', 'Machico', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7264124, -16.7711548, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2267193553',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2267193553')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pistas de Machico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7264124)) <= 0.00070 AND ABS(v.longitude - (-16.7711548)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Atalaia Diving', 'Madeira', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6418949, -16.8298615, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2649557695',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2649557695')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Atalaia Diving'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6418949)) <= 0.00070 AND ABS(v.longitude - (-16.8298615)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hípico da Madeira', 'Funchal', 'Madeira', NULL,
       NULL, 'Centro hípico', NULL,
       32.6675779, -16.8780739, 'https://www.facebook.com/RestPuroLusitano', NULL, 'OPENSTREETMAP', 'OSM:NODE:2973889140',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2973889140')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hípico da Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6675779)) <= 0.00070 AND ABS(v.longitude - (-16.8780739)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Baia dos Juncos - Surf Zone', 'São Vicente', 'Madeira', NULL,
       'Surf', 'Campo desportivo', NULL,
       32.8115201, -17.0389572, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3530101528',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3530101528')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Baia dos Juncos - Surf Zone'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8115201)) <= 0.00070 AND ABS(v.longitude - (-17.0389572)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Futebol Estrela', 'Calheta', 'Madeira', NULL,
       'Futebol', 'Complexo desportivo', NULL,
       32.7299868, -17.1781869, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3851560209',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3851560209')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Futebol Estrela'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7299868)) <= 0.00070 AND ABS(v.longitude - (-17.1781869)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo e Recreativo dos Prazeres', 'Calheta', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.754308, -17.201333, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3851831339',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3851831339')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo e Recreativo dos Prazeres'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.754308)) <= 0.00070 AND ABS(v.longitude - (-17.201333)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque de actividades "outdoor"', 'Calheta', 'Madeira', NULL,
       NULL, 'Pista desportiva', NULL,
       32.751019, -17.2031701, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3851831355',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3851831355')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque de actividades "outdoor"'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.751019)) <= 0.00070 AND ABS(v.longitude - (-17.2031701)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginasio Keep Fit', 'Calheta', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7246275, -17.1814262, NULL, '+351 965 446 229', 'OPENSTREETMAP', 'OSM:NODE:3851851765',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3851851765')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginasio Keep Fit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7246275)) <= 0.00070 AND ABS(v.longitude - (-17.1814262)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo São Roque', 'Funchal', 'Madeira', NULL,
       'Hóquei em Campo', 'Complexo desportivo', NULL,
       32.670156, -16.9218996, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3862480874',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3862480874')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo São Roque'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.670156)) <= 0.00070 AND ABS(v.longitude - (-16.9218996)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Crossfit FNC', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.667277, -16.901046, 'https://www.facebook.com/crossfitfnc/', NULL, 'OPENSTREETMAP', 'OSM:NODE:3870794293',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3870794293')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Crossfit FNC'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.667277)) <= 0.00070 AND ABS(v.longitude - (-16.901046)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pico da Cruz', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6421133, -16.9392611, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3871870903',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3871870903')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pico da Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6421133)) <= 0.00070 AND ABS(v.longitude - (-16.9392611)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio Bodyfit', 'Funchal', 'Madeira', 'Rua de São Pedro 2, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6497245, -16.9116322, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3886994422',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3886994422')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio Bodyfit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6497245)) <= 0.00070 AND ABS(v.longitude - (-16.9116322)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Be-Fit', 'Funchal', 'Madeira', 'Rua Doutor João Brito Câmara 9, 9000-039, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6474835, -16.9133846, 'https://www.be-fit.pt/befit/plaza-madeira', NULL, 'OPENSTREETMAP', 'OSM:NODE:3897534731',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3897534731')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Be-Fit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6474835)) <= 0.00070 AND ABS(v.longitude - (-16.9133846)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo da Escola das Figueirinhas', 'Caniço', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.6531274, -16.8538815, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:4358131531',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:4358131531')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo da Escola das Figueirinhas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6531274)) <= 0.00070 AND ABS(v.longitude - (-16.8538815)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio da Escola das Figueirinhas', 'Caniço', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6535141, -16.8541346, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:4374746328',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:4374746328')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio da Escola das Figueirinhas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6535141)) <= 0.00070 AND ABS(v.longitude - (-16.8541346)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Madeira Race Park', 'Câmara de Lobos', 'Madeira', NULL,
       'Corrida', 'Pista desportiva', NULL,
       32.6776064, -16.9996944, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:4570653419',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:4570653419')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Madeira Race Park'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6776064)) <= 0.00070 AND ABS(v.longitude - (-16.9996944)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Equilibrium', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6373792, -16.9409104, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:4825215603',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:4825215603')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Equilibrium'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6373792)) <= 0.00070 AND ABS(v.longitude - (-16.9409104)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Madeira Racing Park', 'Câmara de Lobos', 'Madeira', NULL,
       'Corrida', 'Pista desportiva', NULL,
       32.6803459, -17.0007472, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:5223697555',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:5223697555')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Madeira Racing Park'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6803459)) <= 0.00070 AND ABS(v.longitude - (-17.0007472)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval do Funchal', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6479516, -16.9172723, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:5307178095',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:5307178095')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval do Funchal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6479516)) <= 0.00070 AND ABS(v.longitude - (-16.9172723)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hipismo', 'Santa Cruz', 'Madeira', NULL,
       NULL, 'Centro hípico', NULL,
       32.7197705, -16.8022097, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:5898667225',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:5898667225')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hipismo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7197705)) <= 0.00070 AND ABS(v.longitude - (-16.8022097)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT '24/7 ginásio', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6580901, -16.9223101, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:6467960000',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:6467960000')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('24/7 ginásio'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6580901)) <= 0.00070 AND ABS(v.longitude - (-16.9223101)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Summerboard Diving Center', 'Ponta do Sol', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6791903, -17.1044258, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:7020274900',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:7020274900')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Summerboard Diving Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6791903)) <= 0.00070 AND ABS(v.longitude - (-17.1044258)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fitness Factory', 'Caniço', 'Madeira', 'Estrada do Aeroporto, Caniço',
       'Ginásio', 'Ginásio', NULL,
       32.653351, -16.8446034, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:10929455222',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:10929455222')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fitness Factory'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.653351)) <= 0.00070 AND ABS(v.longitude - (-16.8446034)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fitness Factory - Funchal', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6409131, -16.9450169, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:10936880824',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:10936880824')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fitness Factory - Funchal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6409131)) <= 0.00070 AND ABS(v.longitude - (-16.9450169)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fitness Dream', 'Funchal', 'Madeira', 'Caminho de Santo António 80A, 9020-001, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6568516, -16.9266627, NULL, '+351 291 762 204', 'OPENSTREETMAP', 'OSM:NODE:11167379926',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11167379926')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fitness Dream'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6568516)) <= 0.00070 AND ABS(v.longitude - (-16.9266627)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ascensão Gym', 'Ribeira Brava', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6777419, -17.0614318, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:11500425002',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11500425002')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ascensão Gym'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6777419)) <= 0.00070 AND ABS(v.longitude - (-17.0614318)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo Da Ribeira Brava', 'Ribeira Brava', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6734871, -17.0641033, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:11500714293',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11500714293')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo Da Ribeira Brava'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6734871)) <= 0.00070 AND ABS(v.longitude - (-17.0641033)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hatha Yoga', 'Funchal', 'Madeira', 'Rua da Estalagem 23, 9060-415, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.657738, -16.8667193, 'https://www.roel-heremans.com', '+351 913 00 00 48', 'OPENSTREETMAP', 'OSM:NODE:11693627293',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11693627293')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hatha Yoga'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.657738)) <= 0.00070 AND ABS(v.longitude - (-16.8667193)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio 24/7 Anadia', 'Funchal', 'Madeira', 'Rua do Visconde Anadia, Funchal',
       'Ginásio', 'Ginásio', 'INDOOR',
       32.6492741, -16.9046659, 'https://ginasio24-7.pt/ginasio-anadia/', '+351 291 224 321', 'OPENSTREETMAP', 'OSM:NODE:11953467412',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11953467412')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio 24/7 Anadia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6492741)) <= 0.00070 AND ABS(v.longitude - (-16.9046659)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Platinium', 'Funchal', 'Madeira', 'Rua Brigadeiro Oudinot 2, 9060-209, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6495175, -16.9037764, 'https://ginasioplatinium.com/pt/', NULL, 'OPENSTREETMAP', 'OSM:NODE:11953469212',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11953469212')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Platinium'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6495175)) <= 0.00070 AND ABS(v.longitude - (-16.9037764)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Be-Fit', 'Funchal', 'Madeira', 'Rampa dos Barreiros 6, 9000-695, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6455633, -16.9275987, 'https://www.be-fit.pt/befit/maritimo-funchal', '+351 291 110 823', 'OPENSTREETMAP', 'OSM:NODE:12135940024',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12135940024')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Be-Fit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6455633)) <= 0.00070 AND ABS(v.longitude - (-16.9275987)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bravagym', 'Ribeira Brava', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6770114, -17.0622955, NULL, '+351 936 477 577', 'OPENSTREETMAP', 'OSM:NODE:12288650559',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12288650559')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Bravagym'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6770114)) <= 0.00070 AND ABS(v.longitude - (-17.0622955)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Evolve Studio - Fitness Center', 'Funchal', 'Madeira', 'Rua do Hospital Velho, 9060-129, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6488269, -16.9028655, 'https://evolvestudio2022.com', '+351 915 705 137', 'OPENSTREETMAP', 'OSM:NODE:12302413350',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12302413350')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Evolve Studio - Fitness Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6488269)) <= 0.00070 AND ABS(v.longitude - (-16.9028655)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Box Women', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6605193, -16.9300811, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:12779737966',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12779737966')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Box Women'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6605193)) <= 0.00070 AND ABS(v.longitude - (-16.9300811)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Viva Fit', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6604897, -16.930036, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:12779737967',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12779737967')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Viva Fit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6604897)) <= 0.00070 AND ABS(v.longitude - (-16.930036)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'MOVÉO Fitness & Training Studio', 'São Martinho', 'Madeira', 'Rua do Vale D''Ajuda 66, 9000-116, São Martinho',
       'Ginásio', 'Ginásio', NULL,
       32.6377861, -16.9436578, 'https://www.instagram.com/moveo_fitness_training_studio/', '+351 965 452 868', 'OPENSTREETMAP', 'OSM:NODE:13250442828',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13250442828')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('MOVÉO Fitness & Training Studio'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6377861)) <= 0.00070 AND ABS(v.longitude - (-16.9436578)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bamboo Fitness Studio', 'Caniço', 'Madeira', 'Rua Don Francisco Santana 2, 9125-031, Caniço',
       'Ginásio', 'Ginásio', NULL,
       32.6438459, -16.8297735, 'https://www.instagram.com/bamboofitnesstudio/', '+351 926 408 181', 'OPENSTREETMAP', 'OSM:NODE:13251686514',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13251686514')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Bamboo Fitness Studio'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6438459)) <= 0.00070 AND ABS(v.longitude - (-16.8297735)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina do Porto Moniz', 'Porto Moniz', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.8660462, -17.1749483, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13620288159',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13620288159')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina do Porto Moniz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8660462)) <= 0.00070 AND ABS(v.longitude - (-17.1749483)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina de São Vicente', 'São Vicente', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.7935308, -17.044058, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13620288160',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13620288160')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina de São Vicente'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7935308)) <= 0.00070 AND ABS(v.longitude - (-17.044058)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal da Ponta do Sol', 'Ponta do Sol', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.6847777, -17.1002177, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13620288163',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13620288163')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal da Ponta do Sol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6847777)) <= 0.00070 AND ABS(v.longitude - (-17.1002177)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de Santa Cruz', 'Santa Cruz', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.6910351, -16.7973914, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13620288164',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13620288164')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de Santa Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6910351)) <= 0.00070 AND ABS(v.longitude - (-16.7973914)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal da Camacha', 'Santa Cruz', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.6780078, -16.8500397, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13620288165',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13620288165')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal da Camacha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6780078)) <= 0.00070 AND ABS(v.longitude - (-16.8500397)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'TopFit', 'Machico', 'Madeira', 'Rua Doutor João Abel de Freitas 22, 9200-130, Machico',
       'Ginásio', 'Ginásio', NULL,
       32.7192442, -16.7684989, 'https://www.topfit.fitness/pt/machico/', '+351 291 111 690', 'OPENSTREETMAP', 'OSM:NODE:13893664506',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13893664506')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('TopFit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7192442)) <= 0.00070 AND ABS(v.longitude - (-16.7684989)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Be-Fit', 'Funchal', 'Madeira', 'Rua da Ponta da Cruz 6, 9000-103, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6347252, -16.9468254, 'https://be-fit.pt/befit/centromar-funchal-3', NULL, 'OPENSTREETMAP', 'OSM:NODE:13914244433',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13914244433')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Be-Fit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6347252)) <= 0.00070 AND ABS(v.longitude - (-16.9468254)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Madeira Surf Center', 'São Vicente', 'Madeira', NULL,
       'Surf', 'Campo desportivo', NULL,
       32.8108245, -17.038869, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:14175125475',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:14175125475')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Madeira Surf Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8108245)) <= 0.00070 AND ABS(v.longitude - (-17.038869)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CrossfitFNC HYBRID LIFE', 'Ponta do Sol', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.7026399, -17.1344753, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:14196549873',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:14196549873')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CrossfitFNC HYBRID LIFE'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7026399)) <= 0.00070 AND ABS(v.longitude - (-17.1344753)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Maxximus', 'Funchal', 'Madeira', NULL,
       'Ginásio', 'Ginásio', NULL,
       32.6392579, -16.9468902, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:14226661847',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:14226661847')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Maxximus'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6392579)) <= 0.00070 AND ABS(v.longitude - (-16.9468902)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polivalente', 'Santa Cruz', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6751982, -16.8138462, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:26746905',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:26746905')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polivalente'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6751982)) <= 0.00070 AND ABS(v.longitude - (-16.8138462)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Golf Santo da Serra', 'Machico', 'Madeira', 'Santo António da Serra, 9200-152, Machico',
       'Golfe', 'Campo de golfe', NULL,
       32.7217377, -16.8020014, 'https://www.santodaserragolf.com/', '+351 291 550 100', 'OPENSTREETMAP', 'OSM:WAY:26778626',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:26778626')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Golf Santo da Serra'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7217377)) <= 0.00070 AND ABS(v.longitude - (-16.8020014)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Golfe Porto Santo', 'Vila Baleira, Porto Santo', 'Madeira', 'Sitio das Marinhas, 9400-162, Vila Baleira, Porto Santo',
       'Golfe', 'Campo de golfe', NULL,
       33.0520691, -16.3687347, 'https://portosantogolfe.com/', '+351 291 983 778', 'OPENSTREETMAP', 'OSM:WAY:26946926',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:26946926')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Golfe Porto Santo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0520691)) <= 0.00070 AND ABS(v.longitude - (-16.3687347)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol do Caniçal', 'Machico', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.7372881, -16.7447433, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:27298723',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:27298723')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol do Caniçal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7372881)) <= 0.00070 AND ABS(v.longitude - (-16.7447433)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sintético 2', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.671549, -16.8823727, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:54733426',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:54733426')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sintético 2'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.671549)) <= 0.00070 AND ABS(v.longitude - (-16.8823727)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Treino', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6714486, -16.8835718, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:54733429',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:54733429')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Treino'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6714486)) <= 0.00070 AND ABS(v.longitude - (-16.8835718)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de futebol Adelino Rodrigues', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6493979, -16.9003711, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:97950105',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:97950105')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de futebol Adelino Rodrigues'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6493979)) <= 0.00070 AND ABS(v.longitude - (-16.9003711)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do CAB', 'Funchal', 'Madeira', NULL,
       'Basquetebol', 'Complexo desportivo', NULL,
       32.6455147, -16.9324125, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:116818959',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:116818959')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do CAB'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6455147)) <= 0.00070 AND ABS(v.longitude - (-16.9324125)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Jaime Moniz', 'Funchal', 'Madeira', NULL,
       'Voleibol', 'Campo desportivo', NULL,
       32.649409, -16.8996606, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:120844501',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:120844501')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Jaime Moniz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.649409)) <= 0.00070 AND ABS(v.longitude - (-16.8996606)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola EB1', 'Calheta', 'Madeira', NULL,
       'Basquetebol;Andebol;Futebol', 'Campo desportivo', NULL,
       32.7583095, -17.2307397, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:123897121',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:123897121')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola EB1'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7583095)) <= 0.00070 AND ABS(v.longitude - (-17.2307397)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal dos Prazeres', 'Calheta', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.7517953, -17.2039904, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:123897126',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:123897126')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal dos Prazeres'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7517953)) <= 0.00070 AND ABS(v.longitude - (-17.2039904)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Crianças', 'Funchal', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6344596, -16.9408767, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:123974229',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:123974229')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Crianças'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6344596)) <= 0.00070 AND ABS(v.longitude - (-16.9408767)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Adultos', 'Funchal', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6344152, -16.9407215, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:123974232',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:123974232')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Adultos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6344152)) <= 0.00070 AND ABS(v.longitude - (-16.9407215)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polo Desportivo', 'Calheta', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7365274, -17.1857955, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:124839967',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:124839967')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polo Desportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7365274)) <= 0.00070 AND ABS(v.longitude - (-17.1857955)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal da Ribeira Brava', 'Ribeira Brava', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6752118, -17.0630735, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:130397934',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:130397934')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal da Ribeira Brava'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6752118)) <= 0.00070 AND ABS(v.longitude - (-17.0630735)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Clube de Futebol Andorinha', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6748216, -16.9346193, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:130682133',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:130682133')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Clube de Futebol Andorinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6748216)) <= 0.00070 AND ABS(v.longitude - (-16.9346193)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol da Francisco Franco', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6517475, -16.9042583, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:130700497',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:130700497')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol da Francisco Franco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6517475)) <= 0.00070 AND ABS(v.longitude - (-16.9042583)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Kartódromo do Faial', 'Faial', 'Madeira', 'Estrada da Praia do Faial, Faial',
       'Karting', 'Complexo desportivo', NULL,
       32.7882196, -16.8509091, NULL, '+351 926 345 624', 'OPENSTREETMAP', 'OSM:WAY:131905639',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:131905639')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Kartódromo do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7882196)) <= 0.00070 AND ABS(v.longitude - (-16.8509091)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Braúlio França', 'Santa Cruz', 'Madeira', NULL,
       'Futebol', 'Complexo desportivo', NULL,
       32.7036145, -16.791087, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:132950278',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:132950278')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Braúlio França'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7036145)) <= 0.00070 AND ABS(v.longitude - (-16.791087)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal Tristão Vaz Teixeira', 'Machico', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.7270992, -16.7718289, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:134072362',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:134072362')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal Tristão Vaz Teixeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7270992)) <= 0.00070 AND ABS(v.longitude - (-16.7718289)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Municipais de Machico', 'Machico', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.7262833, -16.7715892, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:134072378',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:134072378')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Municipais de Machico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7262833)) <= 0.00070 AND ABS(v.longitude - (-16.7715892)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal Manuel Marques da Trindade', 'Santana', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.793064, -16.8831404, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:134576139',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:134576139')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal Manuel Marques da Trindade'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.793064)) <= 0.00070 AND ABS(v.longitude - (-16.8831404)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal Carlos Sé', 'São Vicente', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.8184519, -16.9755289, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:134640554',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:134640554')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal Carlos Sé'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8184519)) <= 0.00070 AND ABS(v.longitude - (-16.9755289)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal', 'Ponta do Sol', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.7059258, -17.1171552, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:134683796',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:134683796')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7059258)) <= 0.00070 AND ABS(v.longitude - (-17.1171552)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Desportivo Coberto da EB1Ciclo do São Roque do Faial', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.777717, -16.8477631, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:136093487',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:136093487')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Desportivo Coberto da EB1Ciclo do São Roque do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.777717)) <= 0.00070 AND ABS(v.longitude - (-16.8477631)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Zona de Lazer da Foz da Ribeira do Faial', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7917294, -16.8503794, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:136100439',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:136100439')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Zona de Lazer da Foz da Ribeira do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7917294)) <= 0.00070 AND ABS(v.longitude - (-16.8503794)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista da Cancela', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6467121, -16.8674094, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:136399997',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:136399997')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista da Cancela'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6467121)) <= 0.00070 AND ABS(v.longitude - (-16.8674094)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Palheiro Golf', 'Funchal', 'Madeira', 'Rua do Balancal 29, 9060-414, Funchal',
       'Golfe', 'Campo de golfe', NULL,
       32.6542975, -16.8660303, 'https://www.palheirogolf.com', '+351 291 790 120', 'OPENSTREETMAP', 'OSM:WAY:136516313',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:136516313')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Palheiro Golf'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6542975)) <= 0.00070 AND ABS(v.longitude - (-16.8660303)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio José Lino Pestana', 'Porto Santo', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       33.0441948, -16.3591256, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:142721449',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:142721449')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio José Lino Pestana'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0441948)) <= 0.00070 AND ABS(v.longitude - (-16.3591256)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo da Associação Desportiva da Camacha', 'Santa Cruz', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       32.6720386, -16.8525194, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:143026247',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:143026247')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo da Associação Desportiva da Camacha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6720386)) <= 0.00070 AND ABS(v.longitude - (-16.8525194)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do 1ª de Maio - Palheiro Ferreiro', 'Santa Cruz', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6650986, -16.8654729, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:143407372',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:143407372')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do 1ª de Maio - Palheiro Ferreiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6650986)) <= 0.00070 AND ABS(v.longitude - (-16.8654729)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Doca do Cavacas', 'Funchal', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6353086, -16.9480128, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:160923259',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:160923259')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Doca do Cavacas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6353086)) <= 0.00070 AND ABS(v.longitude - (-16.9480128)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Praia das Palmeiras', 'Santa Cruz', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.688017, -16.788841, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:162812889',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:162812889')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Praia das Palmeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.688017)) <= 0.00070 AND ABS(v.longitude - (-16.788841)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Naval Box - Crossfit', 'Funchal', 'Madeira', 'Rua dos Estados Unidos da America 55, 9000-096, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6447357, -16.9363606, 'https://www.clubenavaldofunchal.com/navalbox', NULL, 'OPENSTREETMAP', 'OSM:WAY:164762662',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:164762662')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Naval Box - Crossfit'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6447357)) <= 0.00070 AND ABS(v.longitude - (-16.9363606)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Sidónio Fernandes', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6574606, -16.9425478, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:164965385',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:164965385')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Sidónio Fernandes'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6574606)) <= 0.00070 AND ABS(v.longitude - (-16.9425478)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo Salesiano', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6570925, -16.8963484, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:165551911',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:165551911')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo Salesiano'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6570925)) <= 0.00070 AND ABS(v.longitude - (-16.8963484)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio da Imaculada Conceição', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       32.6706149, -16.9362509, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:165784028',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:165784028')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio da Imaculada Conceição'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6706149)) <= 0.00070 AND ABS(v.longitude - (-16.9362509)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do Marítimo', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6716624, -16.9355069, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:165784029',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:165784029')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do Marítimo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6716624)) <= 0.00070 AND ABS(v.longitude - (-16.9355069)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo de Ponta do Sol', 'Ponta do Sol', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6833605, -17.1008017, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:166556818',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:166556818')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo de Ponta do Sol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6833605)) <= 0.00070 AND ABS(v.longitude - (-17.1008017)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do Arco da Calheta', 'Calheta', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7171349, -17.1501091, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168383577',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168383577')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do Arco da Calheta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7171349)) <= 0.00070 AND ABS(v.longitude - (-17.1501091)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão dos Prazeres', 'Calheta', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7522681, -17.2029293, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168383578',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168383578')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão dos Prazeres'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7522681)) <= 0.00070 AND ABS(v.longitude - (-17.2029293)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Náutica da Madeira', 'Machico', 'Madeira', NULL,
       'Vela', 'Complexo desportivo', NULL,
       32.7349631, -16.7458108, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168383596',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168383596')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Náutica da Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7349631)) <= 0.00070 AND ABS(v.longitude - (-16.7458108)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo balnear de Ponta Delgada', 'São Vicente', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.8277525, -16.9844703, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168570637',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168570637')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo balnear de Ponta Delgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8277525)) <= 0.00070 AND ABS(v.longitude - (-16.9844703)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'São Vicente', 'São Vicente', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.8065763, -17.0467414, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168570660',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168570660')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('São Vicente'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8065763)) <= 0.00070 AND ABS(v.longitude - (-17.0467414)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Kartódromo do Porto Santo', 'Porto Santo', 'Madeira', NULL,
       'Karting', 'Complexo desportivo', NULL,
       33.0636167, -16.3088138, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168612297',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168612297')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Kartódromo do Porto Santo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0636167)) <= 0.00070 AND ABS(v.longitude - (-16.3088138)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Municipais', 'Machico', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.7739651, -16.8268985, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:168959380',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:168959380')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Municipais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7739651)) <= 0.00070 AND ABS(v.longitude - (-16.8268985)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal de Câmara de Lobos', 'Câmara de Lobos', 'Madeira', NULL,
       NULL, 'Estádio', NULL,
       32.6551065, -16.9823486, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169107145',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169107145')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal de Câmara de Lobos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6551065)) <= 0.00070 AND ABS(v.longitude - (-16.9823486)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio da Madeira', 'Funchal', 'Madeira', NULL,
       NULL, 'Estádio', NULL,
       32.6706231, -16.8835636, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169116953',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169116953')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio da Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6706231)) <= 0.00070 AND ABS(v.longitude - (-16.8835636)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Formação da Madeira', 'Santa Cruz', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6841187, -16.8230596, 'http://www.afmadeira.com/portal/page/portal/PORTAL_FUTEBOL/MADEIRA/MADEIRA_ASSOCIACAO/INFRAESTRUTURAS/COMPLEXO/INSTALACOES', NULL, 'OPENSTREETMAP', 'OSM:WAY:169135590',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169135590')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Formação da Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6841187)) <= 0.00070 AND ABS(v.longitude - (-16.8230596)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal do Porto da Cruz', 'Machico', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.7685753, -16.847296, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169142529',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169142529')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal do Porto da Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7685753)) <= 0.00070 AND ABS(v.longitude - (-16.847296)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Municipais', 'Santana', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.8040525, -16.8768188, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169850110',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169850110')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Municipais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8040525)) <= 0.00070 AND ABS(v.longitude - (-16.8768188)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Motocross das Carreiras', 'Santa Cruz', 'Madeira', NULL,
       NULL, 'Pista desportiva', NULL,
       32.7076031, -16.8829221, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:180308684',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:180308684')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Motocross das Carreiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7076031)) <= 0.00070 AND ABS(v.longitude - (-16.8829221)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista Daniel Pombo e Filhos', 'Ponta do Sol', 'Madeira', NULL,
       NULL, 'Pista desportiva', NULL,
       32.7140728, -17.1149814, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:181255761',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:181255761')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista Daniel Pombo e Filhos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7140728)) <= 0.00070 AND ABS(v.longitude - (-17.1149814)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do Caniçal', 'Machico', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7381197, -16.7451351, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:181811755',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:181811755')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do Caniçal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7381197)) <= 0.00070 AND ABS(v.longitude - (-16.7451351)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pool', 'Machico', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.7421751, -16.7102844, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:194483242',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:194483242')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pool'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7421751)) <= 0.00070 AND ABS(v.longitude - (-16.7102844)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Natural Swimming Pool', 'Machico', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.7418683, -16.7088949, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201983043',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201983043')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Natural Swimming Pool'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7418683)) <= 0.00070 AND ABS(v.longitude - (-16.7088949)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'BTT', 'Porto Moniz', 'Madeira', NULL,
       'Ciclismo', 'Pista desportiva', NULL,
       32.847965, -17.1741222, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:204550828',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:204550828')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('BTT'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.847965)) <= 0.00070 AND ABS(v.longitude - (-17.1741222)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hotel Buganvilia Swimming Pool', 'Funchal', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6396126, -16.9301552, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:204583974',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:204583974')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hotel Buganvilia Swimming Pool'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6396126)) <= 0.00070 AND ABS(v.longitude - (-16.9301552)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hotel Mimosa Swimming Pool', 'Funchal', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6402699, -16.9306714, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:204583978',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:204583978')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hotel Mimosa Swimming Pool'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6402699)) <= 0.00070 AND ABS(v.longitude - (-16.9306714)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio dos Juncos', 'São Vicente', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       32.8103516, -17.0435798, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:205126095',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:205126095')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio dos Juncos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8103516)) <= 0.00070 AND ABS(v.longitude - (-17.0435798)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio de Desportos de Praia', 'Porto Santo', 'Madeira', NULL,
       NULL, 'Estádio', NULL,
       33.0632368, -16.3074608, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:205706330',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:205706330')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio de Desportos de Praia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0632368)) <= 0.00070 AND ABS(v.longitude - (-16.3074608)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo sintético das Covas', 'Santana', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7875568, -16.8549445, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:214393461',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:214393461')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo sintético das Covas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7875568)) <= 0.00070 AND ABS(v.longitude - (-16.8549445)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Desportivo da Madeira', 'Ribeira Brava', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6907063, -17.0485186, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:286838934',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:286838934')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Desportivo da Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6907063)) <= 0.00070 AND ABS(v.longitude - (-17.0485186)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão da Serra de Água', 'Ribeira Brava', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7270833, -17.0254029, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:286838962',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:286838962')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão da Serra de Água'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7270833)) <= 0.00070 AND ABS(v.longitude - (-17.0254029)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Desportivo da Água de Pena', 'Machico', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.7044443, -16.7658143, 'https://www.cm-machico.pt/index.php?pag=parque_desportivo_agua_pena', NULL, 'OPENSTREETMAP', 'OSM:WAY:286841759',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:286841759')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Desportivo da Água de Pena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7044443)) <= 0.00070 AND ABS(v.longitude - (-16.7658143)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Park Água de Pena', 'Machico', 'Madeira', NULL,
       'Skate', 'Campo desportivo', NULL,
       32.7031972, -16.7678518, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:286841835',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:286841835')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Park Água de Pena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7031972)) <= 0.00070 AND ABS(v.longitude - (-16.7678518)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio do Marítimo', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       32.645554, -16.9283764, 'www.csmaritimo.org.pt/', NULL, 'OPENSTREETMAP', 'OSM:WAY:378676204',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:378676204')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio do Marítimo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.645554)) <= 0.00070 AND ABS(v.longitude - (-16.9283764)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina de Água Salgada', 'Madeira', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.6342122, -16.941202, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:379760659',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:379760659')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina de Água Salgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6342122)) <= 0.00070 AND ABS(v.longitude - (-16.941202)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Escola da APEL', 'Funchal', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.6570162, -16.9142894, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:380788052',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:380788052')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Escola da APEL'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6570162)) <= 0.00070 AND ABS(v.longitude - (-16.9142894)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de futebol da antiga escola', 'Calheta', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.738261, -17.2110076, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:382134556',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:382134556')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de futebol da antiga escola'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.738261)) <= 0.00070 AND ABS(v.longitude - (-17.2110076)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Desportivo Penteada', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6650322, -16.9279687, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:383753348',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:383753348')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Desportivo Penteada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6650322)) <= 0.00070 AND ABS(v.longitude - (-16.9279687)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Futebol Carvalheiro', 'Funchal', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.6656964, -16.9087472, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:384017108',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:384017108')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Futebol Carvalheiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6656964)) <= 0.00070 AND ABS(v.longitude - (-16.9087472)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Francisco Franco', 'Funchal', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.6520657, -16.9058871, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:384241728',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:384241728')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Francisco Franco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6520657)) <= 0.00070 AND ABS(v.longitude - (-16.9058871)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol do Ribeiro Real', 'Câmara de Lobos', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6610645, -16.9688953, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:432817598',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:432817598')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol do Ribeiro Real'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6610645)) <= 0.00070 AND ABS(v.longitude - (-16.9688953)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Galeão', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6785207, -16.9292077, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:438789294',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:438789294')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Galeão'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6785207)) <= 0.00070 AND ABS(v.longitude - (-16.9292077)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Park do Funchal', 'Funchal', 'Madeira', NULL,
       'Skate', 'Campo desportivo', NULL,
       32.6471821, -16.9019402, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:439681059',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:439681059')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Park do Funchal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6471821)) <= 0.00070 AND ABS(v.longitude - (-16.9019402)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão de desportos da EB1/ PE da Boaventura', 'São Vicente', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       32.817076, -16.9762379, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:449144475',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:449144475')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão de desportos da EB1/ PE da Boaventura'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.817076)) <= 0.00070 AND ABS(v.longitude - (-16.9762379)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'On Water Academy', 'Porto Santo', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       33.0374039, -16.3600019, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:502402227',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:502402227')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('On Water Academy'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0374039)) <= 0.00070 AND ABS(v.longitude - (-16.3600019)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardins da Graça Skatepark', 'Machico', 'Madeira', NULL,
       'Skate', 'Campo desportivo', NULL,
       32.7214678, -16.7689322, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:557467935',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:557467935')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardins da Graça Skatepark'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7214678)) <= 0.00070 AND ABS(v.longitude - (-16.7689322)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Cristiano Ronaldo Campus Futebol', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Estádio', NULL,
       32.6707873, -16.8823771, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:622029721',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:622029721')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Cristiano Ronaldo Campus Futebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6707873)) <= 0.00070 AND ABS(v.longitude - (-16.8823771)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Children Pool', 'Santana', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.7929057, -16.849345, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:651583740',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:651583740')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Children Pool'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7929057)) <= 0.00070 AND ABS(v.longitude - (-16.849345)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Motocross da Fajã da Ovelha', 'Calheta', 'Madeira', NULL,
       NULL, 'Pista desportiva', NULL,
       32.7768743, -17.2189506, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:657422055',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:657422055')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Motocross da Fajã da Ovelha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7768743)) <= 0.00070 AND ABS(v.longitude - (-17.2189506)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina da Camacha', 'Santa Cruz', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.6779608, -16.8499963, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:661602684',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:661602684')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina da Camacha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6779608)) <= 0.00070 AND ABS(v.longitude - (-16.8499963)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hotel Quinta da Serra', 'Câmara de Lobos', 'Madeira', NULL,
       'Natação;Ginásio', 'Complexo desportivo', NULL,
       32.6936041, -16.9947279, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:726782164',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:726782164')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hotel Quinta da Serra'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6936041)) <= 0.00070 AND ABS(v.longitude - (-16.9947279)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pumptrack Parque do Garajau', 'Caniço', 'Madeira', NULL,
       'BMX', 'Pista desportiva', NULL,
       32.6466199, -16.8477685, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:736909674',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:736909674')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pumptrack Parque do Garajau'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6466199)) <= 0.00070 AND ABS(v.longitude - (-16.8477685)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal do Curral das Freiras', 'Câmara de Lobos', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.7228433, -16.9659527, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:859100555',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:859100555')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal do Curral das Freiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7228433)) <= 0.00070 AND ABS(v.longitude - (-16.9659527)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Patinagem dos Prazeres', 'Calheta', 'Madeira', NULL,
       'Skate', 'Campo desportivo', NULL,
       32.7515931, -17.2025159, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:925740671',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:925740671')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Patinagem dos Prazeres'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7515931)) <= 0.00070 AND ABS(v.longitude - (-17.2025159)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hotel Putting Green', 'São Vicente', 'Madeira', NULL,
       'Golfe', 'Campo de golfe', NULL,
       32.8287192, -16.9913459, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:971281177',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:971281177')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hotel Putting Green'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8287192)) <= 0.00070 AND ABS(v.longitude - (-16.9913459)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Ponta Delgada', 'São Vicente', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.824024, -16.9886706, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:973300629',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:973300629')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Ponta Delgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.824024)) <= 0.00070 AND ABS(v.longitude - (-16.9886706)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quinta do Padel', 'Funchal', 'Madeira', 'Rampa da Quinta do Leme 12, Funchal',
       'Padel', 'Campo desportivo', NULL,
       32.6575913, -16.9370137, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1095034877',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1095034877')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Quinta do Padel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6575913)) <= 0.00070 AND ABS(v.longitude - (-16.9370137)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CR7', 'Funchal', 'Madeira', NULL,
       'Futebol', 'Campo desportivo', NULL,
       32.6676043, -16.9380748, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1106953484',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1106953484')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CR7'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6676043)) <= 0.00070 AND ABS(v.longitude - (-16.9380748)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Multiusos', 'Porto Santo', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       33.0614888, -16.3383921, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1108168816',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1108168816')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Multiusos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0614888)) <= 0.00070 AND ABS(v.longitude - (-16.3383921)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Madeira Climbing Center', 'Funchal', 'Madeira', 'R. Conde Carvalhal 26E, 9060-182, Funchal',
       'Escalada', 'Complexo desportivo', NULL,
       32.651257, -16.8986112, 'https://www.madeiraclimbingcenter.com', '+351 291 220 322', 'OPENSTREETMAP', 'OSM:WAY:1108191862',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1108191862')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Madeira Climbing Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.651257)) <= 0.00070 AND ABS(v.longitude - (-16.8986112)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Portela Takeoff', 'Machico', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.7470087, -16.8269891, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1109046563',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1109046563')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Portela Takeoff'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7470087)) <= 0.00070 AND ABS(v.longitude - (-16.8269891)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Desportivo da Cidade de Câmara de Lobos', 'Câmara de Lobos', 'Madeira', 'Avenida Nova Cidade, Câmara de Lobos',
       NULL, 'Complexo desportivo', NULL,
       32.6514984, -16.9792982, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1116776469',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1116776469')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Desportivo da Cidade de Câmara de Lobos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6514984)) <= 0.00070 AND ABS(v.longitude - (-16.9792982)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina de Santa Cruz', 'Santa Cruz', 'Madeira', NULL,
       'Natação', 'Complexo desportivo', NULL,
       32.6909941, -16.7974088, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1203990416',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1203990416')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina de Santa Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6909941)) <= 0.00070 AND ABS(v.longitude - (-16.7974088)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de padel', 'Machico', 'Madeira', NULL,
       NULL, 'Campo desportivo', NULL,
       32.7181222, -16.7636999, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1235066861',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1235066861')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de padel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7181222)) <= 0.00070 AND ABS(v.longitude - (-16.7636999)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Ténis Porto Santo', 'Porto Santo', 'Madeira', NULL,
       NULL, 'Complexo desportivo', NULL,
       33.0465887, -16.3581144, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1282421927',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1282421927')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Ténis Porto Santo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0465887)) <= 0.00070 AND ABS(v.longitude - (-16.3581144)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hípico', 'Porto Santo', 'Madeira', NULL,
       NULL, 'Centro hípico', NULL,
       33.0369452, -16.3717152, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1282997073',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1282997073')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hípico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (33.0369452)) <= 0.00070 AND ABS(v.longitude - (-16.3717152)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Do Grupo Recreativo Cruzado Canicense', 'Caniço', 'Madeira', NULL,
       NULL, 'Estádio', NULL,
       32.660749, -16.8355801, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1317735632',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1317735632')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Do Grupo Recreativo Cruzado Canicense'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.660749)) <= 0.00070 AND ABS(v.longitude - (-16.8355801)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini-Campo de Basquet da Francisco Franco', 'Funchal', 'Madeira', NULL,
       'Basquetebol', 'Campo desportivo', NULL,
       32.6520371, -16.9055923, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1460492707',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1460492707')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mini-Campo de Basquet da Francisco Franco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6520371)) <= 0.00070 AND ABS(v.longitude - (-16.9055923)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'EuroGymnico', 'Funchal', 'Madeira', 'Rua do Bom Jesus 8-10, 9050-028, Funchal',
       'Ginásio', 'Ginásio', NULL,
       32.6506897, -16.9076594, 'https://www.eurogymnico.pt/', NULL, 'OPENSTREETMAP', 'OSM:WAY:1528630254',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1528630254')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('EuroGymnico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6506897)) <= 0.00070 AND ABS(v.longitude - (-16.9076594)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Paintball Madeira', 'Santa Cruz', 'Madeira', 'Estrada Regional 102 260, 9100-749, Santa Cruz',
       NULL, 'Campo desportivo', NULL,
       32.6761103, -16.8448516, 'https://paintballmadeira.com/', NULL, 'OPENSTREETMAP', 'OSM:WAY:1548006674',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1548006674')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Paintball Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.6761103)) <= 0.00070 AND ABS(v.longitude - (-16.8448516)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal de Machico', 'Machico', 'Madeira', NULL,
       NULL, 'Estádio', NULL,
       32.7288759, -16.7734244, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:2245844',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:2245844')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal de Machico'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.7288759)) <= 0.00070 AND ABS(v.longitude - (-16.7734244)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Naturais do Aquário', 'Porto Moniz', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.8679908, -17.1661484, 'https://www.portomoniz.pt/pt/visitantes/pontos-de-interesse/praias', NULL, 'OPENSTREETMAP', 'OSM:RELATION:10847974',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:10847974')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Naturais do Aquário'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8679908)) <= 0.00070 AND ABS(v.longitude - (-17.1661484)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Naturais do Porto Moniz', 'Porto Moniz', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.8686508, -17.1706528, 'https://www.portomoniz.pt/pt/visitantes/pontos-de-interesse/praias', NULL, 'OPENSTREETMAP', 'OSM:RELATION:15951991',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:15951991')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Naturais do Porto Moniz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8686508)) <= 0.00070 AND ABS(v.longitude - (-17.1706528)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Naturais do Seixal', 'Porto Moniz', 'Madeira', NULL,
       'Natação', 'Piscina', NULL,
       32.8273469, -17.1106708, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:19727012',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:19727012')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Naturais do Seixal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (32.8273469)) <= 0.00070 AND ABS(v.longitude - (-17.1106708)) <= 0.00100)))
);
