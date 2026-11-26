-- Padel clubs found in local news and club pages. Three have no reliable map pin yet and are listed without coordinates.
-- Europadel and Mesh Padel Center sit next to other venues, so only the same name or source skips them.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Castro Padel', 'Castro Verde', 'Beja', 'Zona de Atividades Económicas, Rua das Tecedeiras, Lote 2, 7780 Castro Verde',
       'Padel', 'Clube de padel', NULL,
       NULL, NULL, NULL, '967810945', 'COMMUNITY', 'JYNLO:NEWS:castro-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:NEWS:castro-padel'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Castro Padel')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('Castro Verde')))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bulldogs Padel Club', 'Carregado', 'Lisboa', 'Rua Manuel Conceição Graça, 2580-347 Carregado',
       'Padel;Pickleball', 'Clube de padel', NULL,
       NULL, NULL, NULL, '965040151', 'COMMUNITY', 'JYNLO:NEWS:bulldogs-padel-carregado',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:NEWS:bulldogs-padel-carregado'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Bulldogs Padel Club')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('Carregado')))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis e Padel de São Brás de Alportel', 'São Brás de Alportel', 'Faro', 'Complexo Municipal de Ténis, Parque de Desporto e Lazer, São Brás de Alportel',
       'Ténis;Padel', 'Clube de ténis', NULL,
       NULL, NULL, NULL, '965258861', 'COMMUNITY', 'JYNLO:NEWS:ctp-sao-bras-de-alportel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:NEWS:ctp-sao-bras-de-alportel'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis e Padel de São Brás de Alportel')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('São Brás de Alportel')))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Burgau Sports Centre', 'Budens', 'Faro', 'Monte Novo, Budens, Vila do Bispo',
       'Padel;Ténis', 'Clube de padel', NULL,
       37.08200, -8.77802, NULL, NULL, 'COMMUNITY', 'JYNLO:OSM-NAME:burgau-sports-centre',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:OSM-NAME:burgau-sports-centre'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Burgau Sports Centre')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('Budens')))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Europadel', 'Elvas', 'Portalegre', 'Antiga Estrada N4 2, 7350-311 Elvas',
       'Padel', 'Clube de padel', NULL,
       38.87805, -7.15509, NULL, NULL, 'COMMUNITY', 'JYNLO:TIEPADEL:europadel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:europadel'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Europadel')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('Elvas')))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mesh Padel Center', 'Braga', 'Braga', 'Rua dos Bombeiros Voluntários, 4700-025 Braga',
       'Padel', 'Clube de padel', NULL,
       41.54525, -8.42821, NULL, '912793853', 'COMMUNITY', 'JYNLO:TIEPADEL:meshpadelcenter',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:meshpadelcenter'
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mesh Padel Center')) AND LOWER(BTRIM(v.city))=LOWER(BTRIM('Braga')))
);
