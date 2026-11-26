-- Padel clubs with a public Tiepadel page (name, address, phone and map pin from the page), not yet in the directory.
-- Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia Padel Setúbal', 'Setúbal', 'Setúbal', 'Parque Verde de Vanicelos, 2900 Setúbal',
       'Padel', 'Clube de padel', NULL,
       38.53479, -8.89777, NULL, '916035615', 'COMMUNITY', 'JYNLO:TIEPADEL:academiapadelsetubal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:academiapadelsetubal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.53479)) <= 0.00063 AND ABS(v.longitude - (-8.89777)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia Padel Setúbal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.53479)) <= 0.0030 AND ABS(v.longitude - (-8.89777)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Dream Padel Center Setúbal', 'Setúbal', 'Setúbal', 'Estrada Nacional 10, Pontes, Imapark, 2910-828 Setúbal',
       'Padel', 'Clube de padel', NULL,
       38.55191, -8.80801, NULL, '967658209', 'COMMUNITY', 'JYNLO:TIEPADEL:dreampadelcentersetubal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:dreampadelcentersetubal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.55191)) <= 0.00063 AND ABS(v.longitude - (-8.80801)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Dream Padel Center Setúbal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.55191)) <= 0.0030 AND ABS(v.longitude - (-8.80801)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Club Al Sakia', 'Quarteira', 'Faro', 'Urbanização Al-Sakia Village, Fonte Santa, 8125-020 Quarteira',
       'Padel', 'Clube de padel', NULL,
       37.07903, -8.07945, NULL, '914391943', 'COMMUNITY', 'JYNLO:TIEPADEL:padelclubalsakia',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:padelclubalsakia'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.07903)) <= 0.00063 AND ABS(v.longitude - (-8.07945)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Club Al Sakia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.07903)) <= 0.0030 AND ABS(v.longitude - (-8.07945)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sines Padel Club', 'Sines', 'Setúbal', 'ZIL 2, Rua K, Lote B3, 7520-309 Sines',
       'Padel', 'Clube de padel', NULL,
       37.95872, -8.85300, NULL, '961332031', 'COMMUNITY', 'JYNLO:TIEPADEL:sinespadelclub',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:sinespadelclub'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.95872)) <= 0.00063 AND ABS(v.longitude - (-8.85300)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sines Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.95872)) <= 0.0030 AND ABS(v.longitude - (-8.85300)) <= 0.0040)))
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
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.87805)) <= 0.00063 AND ABS(v.longitude - (-7.15509)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Europadel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.87805)) <= 0.0030 AND ABS(v.longitude - (-7.15509)) <= 0.0040)))
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
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.54525)) <= 0.00063 AND ABS(v.longitude - (-8.42821)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mesh Padel Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.54525)) <= 0.0030 AND ABS(v.longitude - (-8.42821)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel nas Pirâmides', 'Coina', 'Setúbal', 'Quinta das Pirâmides, Estrada dos 4 Castelos, 2830-481 Coina',
       'Padel', 'Clube de padel', NULL,
       38.57449, -9.02922, NULL, '919273247', 'COMMUNITY', 'JYNLO:TIEPADEL:padelnaspiramides',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:padelnaspiramides'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.57449)) <= 0.00063 AND ABS(v.longitude - (-9.02922)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel nas Pirâmides'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.57449)) <= 0.0030 AND ABS(v.longitude - (-9.02922)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lagoa Station', 'Lagoa', 'Faro', 'Rua de Alagoas Brancas, 8400-424 Lagoa',
       'Padel;Ténis', 'Clube de padel', NULL,
       37.12738, -8.45057, NULL, '962291060', 'COMMUNITY', 'JYNLO:TIEPADEL:lagoastation',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:lagoastation'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.12738)) <= 0.00063 AND ABS(v.longitude - (-8.45057)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Lagoa Station'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.12738)) <= 0.0030 AND ABS(v.longitude - (-8.45057)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Club Torres Novas', 'Torres Novas', 'Santarém', 'Ilha Sêca, Avenida do Bom Amor, 2350-649 Torres Novas',
       'Padel', 'Clube de padel', NULL,
       39.46760, -8.52350, NULL, '917276120', 'COMMUNITY', 'JYNLO:TIEPADEL:padelclubtorresnovas',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:padelclubtorresnovas'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.46760)) <= 0.00063 AND ABS(v.longitude - (-8.52350)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Club Torres Novas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.46760)) <= 0.0030 AND ABS(v.longitude - (-8.52350)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Tondela Padel Club', 'Tondela', 'Viseu', 'Rua Jerónimo Vieira da Silva 181, 3460-062 Tondela',
       'Padel', 'Clube de padel', NULL,
       40.52906, -8.08336, NULL, '966158350', 'COMMUNITY', 'JYNLO:TIEPADEL:tondelapadelclub',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:tondelapadelclub'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.52906)) <= 0.00063 AND ABS(v.longitude - (-8.08336)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Tondela Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.52906)) <= 0.0030 AND ABS(v.longitude - (-8.08336)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'MPadel', 'Castro Daire', 'Viseu', 'Parque Industrial da Senhora da Ouvida, Lote 45, 3600-476 Castro Daire',
       'Padel', 'Clube de padel', NULL,
       40.94451, -7.89916, NULL, '933544467', 'COMMUNITY', 'JYNLO:TIEPADEL:mpadel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:mpadel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.94451)) <= 0.00063 AND ABS(v.longitude - (-7.89916)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('MPadel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.94451)) <= 0.0030 AND ABS(v.longitude - (-7.89916)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel XXL', 'Leiria', 'Leiria', 'Rua José de Sousa Ribeiro 13, Jardoeira, 2440-387 Batalha',
       'Padel', 'Clube de padel', NULL,
       39.66965, -8.83857, NULL, '911160945', 'COMMUNITY', 'JYNLO:TIEPADEL:padelxxl',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:TIEPADEL:padelxxl'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.66965)) <= 0.00063 AND ABS(v.longitude - (-8.83857)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel XXL'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.66965)) <= 0.0030 AND ABS(v.longitude - (-8.83857)) <= 0.0040)))
);
