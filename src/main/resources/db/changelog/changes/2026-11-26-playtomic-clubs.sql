-- Padel, tennis and football venues listed publicly on Playtomic, not yet in the directory.
-- Name and map pin from each club's public page; municipality from OpenStreetMap. Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena Coimbra - Padel & Squash', 'Coimbra', 'Coimbra', 'R. Manuel Madeira, 3025-047 Coimbra',
       'Padel;Squash', 'Clube de padel', NULL,
       40.24512, -8.44362, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-coimbra-padel-squash',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-coimbra-padel-squash'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.24512)) <= 0.00063 AND ABS(v.longitude - (-8.44362)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena Coimbra - Padel & Squash'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.24512)) <= 0.0030 AND ABS(v.longitude - (-8.44362)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Club Figueira', 'Figueira da Foz', 'Coimbra', 'Rua 1º de Maio 50, Chã, 3080-847 Figueira da Foz',
       'Padel', 'Clube de padel', NULL,
       40.16503, -8.83295, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-club-figueira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-club-figueira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.16503)) <= 0.00063 AND ABS(v.longitude - (-8.83295)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Club Figueira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.16503)) <= 0.0030 AND ABS(v.longitude - (-8.83295)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pro Padel Academy - Torres Vedras', 'Torres Vedras', 'Lisboa', 'Rua da Quinta do Prior, 2560-629 Torres Vedras',
       'Padel', 'Clube de padel', NULL,
       39.06989, -9.24839, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:pro-padel-academy-torres-vedras',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:pro-padel-academy-torres-vedras'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.06989)) <= 0.00063 AND ABS(v.longitude - (-9.24839)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pro Padel Academy - Torres Vedras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.06989)) <= 0.0030 AND ABS(v.longitude - (-9.24839)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Leziria Fitness Padel', 'Benavente', 'Santarém', 'Azinhaga do Contador 43, 2130-017 Benavente',
       'Padel', 'Clube de padel', NULL,
       38.96251, -8.81621, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:leziria-fitness-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:leziria-fitness-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.96251)) <= 0.00063 AND ABS(v.longitude - (-8.81621)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Leziria Fitness Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.96251)) <= 0.0030 AND ABS(v.longitude - (-8.81621)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Experience Center', 'Bobadela', 'Lisboa', 'Rua D. Nuno Álvares Pereira 57, Parque Industrial Olaio, 2695-066 Bobadela',
       'Padel', 'Clube de padel', NULL,
       38.80872, -9.09584, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-experience-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-experience-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.80872)) <= 0.00063 AND ABS(v.longitude - (-9.09584)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Experience Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.80872)) <= 0.0030 AND ABS(v.longitude - (-9.09584)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel in Sado', 'Setúbal', 'Setúbal', 'Rua Principal das Praias do Sado, 2910-339 Setúbal',
       'Padel', 'Clube de padel', NULL,
       38.51890, -8.82642, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-in-sado',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-in-sado'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.51890)) <= 0.00063 AND ABS(v.longitude - (-8.82642)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel in Sado'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.51890)) <= 0.0030 AND ABS(v.longitude - (-8.82642)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel One - Perafita', 'Perafita', 'Porto', 'Rua do Progresso 236, 4455-533 Perafita',
       'Padel', 'Clube de padel', NULL,
       41.21701, -8.69442, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-one-perafita',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-one-perafita'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.21701)) <= 0.00063 AND ABS(v.longitude - (-8.69442)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel One - Perafita'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.21701)) <= 0.0030 AND ABS(v.longitude - (-8.69442)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Now Padel & Fit', 'Maia', 'Porto', 'Rua Carlos Sousa Gomes, Lote 15, Zona Industrial da Maia, 4475-297 Maia',
       'Padel', 'Clube de padel', NULL,
       41.25973, -8.63367, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:now-padel-fit',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:now-padel-fit'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.25973)) <= 0.00063 AND ABS(v.longitude - (-8.63367)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Now Padel & Fit'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.25973)) <= 0.0030 AND ABS(v.longitude - (-8.63367)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Black Padel', 'Perafita', 'Porto', 'Rua do Progresso 329, 4455-531 Perafita',
       'Padel', 'Clube de padel', NULL,
       41.21759, -8.69786, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:black-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:black-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.21759)) <= 0.00063 AND ABS(v.longitude - (-8.69786)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Black Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.21759)) <= 0.0030 AND ABS(v.longitude - (-8.69786)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'The Lob - Padel & Pickleball', 'São Mamede de Infesta', 'Porto', 'Rua de Picoutos 108, 4465-231 São Mamede de Infesta',
       'Padel;Pickleball', 'Clube de padel', NULL,
       41.19019, -8.62494, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:the-lob-padel-pickleball',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:the-lob-padel-pickleball'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.19019)) <= 0.00063 AND ABS(v.longitude - (-8.62494)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('The Lob - Padel & Pickleball'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.19019)) <= 0.0030 AND ABS(v.longitude - (-8.62494)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fourlux Padel Center - Paços de Ferreira', 'Paços de Ferreira', 'Porto', 'Rua Via do Poder Local 1066, 4590-901 Paços de Ferreira',
       'Padel', 'Clube de padel', NULL,
       41.27739, -8.35361, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fourlux-padel-center-pacos-de-ferreira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fourlux-padel-center-pacos-de-ferreira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.27739)) <= 0.00063 AND ABS(v.longitude - (-8.35361)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fourlux Padel Center - Paços de Ferreira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.27739)) <= 0.0030 AND ABS(v.longitude - (-8.35361)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Major Padel Clube Douro', 'Oliveira do Douro', 'Porto', 'Zona Industrial de Oliveira do Douro, Oliveira do Douro',
       'Padel', 'Clube de padel', NULL,
       41.11715, -8.57927, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:major-padel-clube-douro',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:major-padel-clube-douro'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.11715)) <= 0.00063 AND ABS(v.longitude - (-8.57927)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Major Padel Clube Douro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.11715)) <= 0.0030 AND ABS(v.longitude - (-8.57927)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quinta d''Anta', 'Maiorca', 'Coimbra', 'Quinta d''Anta - Hotel Rural, 3090-466 Maiorca',
       'Padel', 'Clube de padel', NULL,
       40.17052, -8.75624, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:quinta-d-anta',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:quinta-d-anta'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.17052)) <= 0.00063 AND ABS(v.longitude - (-8.75624)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Quinta d''Anta'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.17052)) <= 0.0030 AND ABS(v.longitude - (-8.75624)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Royal Óbidos by Padel Rey', 'Óbidos', 'Leiria', 'Royal Óbidos Scenic Resort, 2510-665 Óbidos',
       'Padel', 'Clube de padel', NULL,
       39.40684, -9.23815, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:royal-obidos-by-padel-rey',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:royal-obidos-by-padel-rey'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.40684)) <= 0.00063 AND ABS(v.longitude - (-9.23815)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Royal Óbidos by Padel Rey'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.40684)) <= 0.0030 AND ABS(v.longitude - (-9.23815)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Casa do Pessoal CHC - Sports Center', 'Coimbra', 'Coimbra', 'Rua Carminé de Miranda, 3045-034 Coimbra',
       'Padel', 'Clube de padel', NULL,
       40.19609, -8.45725, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:casa-do-pessoal-chc-sports-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:casa-do-pessoal-chc-sports-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.19609)) <= 0.00063 AND ABS(v.longitude - (-8.45725)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Casa do Pessoal CHC - Sports Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.19609)) <= 0.0030 AND ABS(v.longitude - (-8.45725)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Inline Padel Center', 'Lourinhã', 'Lisboa', 'Rua da Indústria, 2530-872 Lourinhã',
       'Padel', 'Clube de padel', NULL,
       39.23177, -9.25598, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:inline-padel-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:inline-padel-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.23177)) <= 0.00063 AND ABS(v.longitude - (-9.25598)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Inline Padel Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.23177)) <= 0.0030 AND ABS(v.longitude - (-9.25598)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Spot Padel Club', 'Avelãs de Caminho', 'Aveiro', 'Estrada Nacional 1 Norte, 3780-351 Avelãs de Caminho',
       'Padel', 'Clube de padel', NULL,
       40.48926, -8.44523, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:spot-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:spot-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.48926)) <= 0.00063 AND ABS(v.longitude - (-8.44523)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Spot Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.48926)) <= 0.0030 AND ABS(v.longitude - (-8.44523)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Break - WPT Indoor Courts', 'Óis da Ribeira', 'Aveiro', '3750-753 Óis da Ribeira',
       'Padel', 'Clube de padel', NULL,
       40.59446, -8.47302, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-break-wpt-indoor-courts',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-break-wpt-indoor-courts'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.59446)) <= 0.00063 AND ABS(v.longitude - (-8.47302)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Break - WPT Indoor Courts'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.59446)) <= 0.0030 AND ABS(v.longitude - (-8.47302)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Concept - Sports & Fun', 'Oliveira de Azeméis', 'Aveiro', 'Rua do Antuã, 3720-558 Oliveira de Azeméis',
       'Padel', 'Clube de padel', NULL,
       40.82044, -8.48484, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-concept-sports-fun',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-concept-sports-fun'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.82044)) <= 0.00063 AND ABS(v.longitude - (-8.48484)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Concept - Sports & Fun'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.82044)) <= 0.0030 AND ABS(v.longitude - (-8.48484)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CERCIAZ Padel', 'Oliveira de Azeméis', 'Aveiro', 'Rua Francisco Abreu e Sousa 8, 3720-340 Oliveira de Azeméis',
       'Padel;Natação', 'Clube de padel', NULL,
       40.85014, -8.46507, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:cerciaz-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:cerciaz-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.85014)) <= 0.00063 AND ABS(v.longitude - (-8.46507)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CERCIAZ Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.85014)) <= 0.0030 AND ABS(v.longitude - (-8.46507)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Kinhas Sport', 'Carregosa', 'Aveiro', '3720-024 Carregosa',
       'Padel', 'Clube de padel', NULL,
       40.88511, -8.42771, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:kinhas-sport',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:kinhas-sport'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.88511)) <= 0.00063 AND ABS(v.longitude - (-8.42771)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Kinhas Sport'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.88511)) <= 0.0030 AND ABS(v.longitude - (-8.42771)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Aquae Padel', 'Chaves', 'Vila Real', 'Rua 5 de Outubro 9, 5400-580 Chaves',
       'Padel', 'Clube de padel', NULL,
       41.73715, -7.47677, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:aquae-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:aquae-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.73715)) <= 0.00063 AND ABS(v.longitude - (-7.47677)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Aquae Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.73715)) <= 0.0030 AND ABS(v.longitude - (-7.47677)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'A Fábrica', 'Fafe', 'Braga', 'Rua José Ribeiro Vieira de Castro, 4820-273 Fafe',
       'Padel', 'Clube de padel', NULL,
       41.43665, -8.17380, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:a-fabrica',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:a-fabrica'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.43665)) <= 0.00063 AND ABS(v.longitude - (-8.17380)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('A Fábrica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.43665)) <= 0.0030 AND ABS(v.longitude - (-8.17380)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hype Padel Club', 'Sendim (Felgueiras)', 'Porto', 'Rua Quelha das Campas 56, 4610-744 Sendim',
       'Padel', 'Clube de padel', NULL,
       41.37960, -8.15879, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:hype-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:hype-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.37960)) <= 0.00063 AND ABS(v.longitude - (-8.15879)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hype Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.37960)) <= 0.0030 AND ABS(v.longitude - (-8.15879)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pro Fitness & Padel', 'Várzea (Barcelos)', 'Braga', 'Rua do Parque Industrial, 4755-539 Várzea',
       'Padel', 'Clube de padel', NULL,
       41.52288, -8.57805, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:pro-fitness-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:pro-fitness-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.52288)) <= 0.00063 AND ABS(v.longitude - (-8.57805)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pro Fitness & Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.52288)) <= 0.0030 AND ABS(v.longitude - (-8.57805)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Golden Padel - Esposende', 'Marinhas (Esposende)', 'Braga', 'Avenida Comendador Francisco Alves, 4740-010 Marinhas',
       'Padel', 'Clube de padel', NULL,
       41.53361, -8.76253, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:golden-padel-esposende',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:golden-padel-esposende'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.53361)) <= 0.00063 AND ABS(v.longitude - (-8.76253)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Golden Padel - Esposende'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.53361)) <= 0.0030 AND ABS(v.longitude - (-8.76253)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'FUT7 Braga', 'Palmeira (Braga)', 'Braga', 'Rua de Entrecampos, 4700-700 Palmeira',
       'Futebol 7;Padel', 'Campo de futebol', NULL,
       41.59193, -8.44408, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fut7-braga',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fut7-braga'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.59193)) <= 0.00063 AND ABS(v.longitude - (-8.44408)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('FUT7 Braga'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.59193)) <= 0.0030 AND ABS(v.longitude - (-8.44408)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Villae Verdasca', 'Palmeira (Braga)', 'Braga', 'Rua da Verdasca, 4700-748 Palmeira',
       'Padel', 'Clube de padel', NULL,
       41.60004, -8.43495, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:villae-verdasca',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:villae-verdasca'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.60004)) <= 0.00063 AND ABS(v.longitude - (-8.43495)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Villae Verdasca'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.60004)) <= 0.0030 AND ABS(v.longitude - (-8.43495)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Neiva Padel Club', 'Castelo do Neiva', 'Viana do Castelo', 'Avenida São Romão, 4935-546 Castelo do Neiva',
       'Padel', 'Clube de padel', NULL,
       41.62806, -8.80084, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:neiva-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:neiva-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.62806)) <= 0.00063 AND ABS(v.longitude - (-8.80084)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Neiva Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.62806)) <= 0.0030 AND ABS(v.longitude - (-8.80084)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Urban Padel - Trofa', 'Trofa', 'Porto', 'Rua Central de Cedões 384, 4785-534 Trofa',
       'Padel', 'Clube de padel', NULL,
       41.32003, -8.56965, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:urban-padel-trofa',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:urban-padel-trofa'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.32003)) <= 0.00063 AND ABS(v.longitude - (-8.56965)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Urban Padel - Trofa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.32003)) <= 0.0030 AND ABS(v.longitude - (-8.56965)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Next Padel', 'Vilarinho (Santo Tirso)', 'Porto', 'Parque Empresarial da Baiona, 4795-484 Vilarinho',
       'Padel', 'Clube de padel', NULL,
       41.37405, -8.33054, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:next-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:next-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.37405)) <= 0.00063 AND ABS(v.longitude - (-8.33054)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Next Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.37405)) <= 0.0030 AND ABS(v.longitude - (-8.33054)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Nation Vizela', 'Vizela', 'Braga', 'Rua da Lage, 4815-483 Vizela',
       'Padel', 'Clube de padel', NULL,
       41.37903, -8.31875, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-nation-vizela',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-nation-vizela'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.37903)) <= 0.00063 AND ABS(v.longitude - (-8.31875)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Nation Vizela'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.37903)) <= 0.0030 AND ABS(v.longitude - (-8.31875)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Paços Padel', 'Carvalhosa (Paços de Ferreira)', 'Porto', 'Rua Entre Muros, 4590-051 Carvalhosa',
       'Padel', 'Clube de padel', NULL,
       41.29558, -8.37325, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:pacos-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:pacos-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.29558)) <= 0.00063 AND ABS(v.longitude - (-8.37325)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Paços Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.29558)) <= 0.0030 AND ABS(v.longitude - (-8.37325)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arruda Fun Padel', 'Arruda dos Vinhos', 'Lisboa', '2630-192 Arruda dos Vinhos',
       'Padel', 'Clube de padel', NULL,
       38.98891, -9.08173, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arruda-fun-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arruda-fun-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.98891)) <= 0.00063 AND ABS(v.longitude - (-9.08173)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arruda Fun Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.98891)) <= 0.0030 AND ABS(v.longitude - (-9.08173)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jet Set Padel Club', 'Prior Velho', 'Lisboa', 'Rua C, 2685-870 Prior Velho',
       'Padel', 'Clube de padel', NULL,
       38.79784, -9.12001, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:jet-set-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:jet-set-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.79784)) <= 0.00063 AND ABS(v.longitude - (-9.12001)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jet Set Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.79784)) <= 0.0030 AND ABS(v.longitude - (-9.12001)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Inn Court', 'Alfornelos', 'Lisboa', 'Praceta Manuela Porto, 2650-191 Alfornelos',
       'Padel', 'Clube de padel', NULL,
       38.76464, -9.20679, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:inn-court',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:inn-court'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.76464)) <= 0.00063 AND ABS(v.longitude - (-9.20679)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Inn Court'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.76464)) <= 0.0030 AND ABS(v.longitude - (-9.20679)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Funsports - Alfragide', 'Alfragide', 'Lisboa', '2610-171 Alfragide',
       'Padel;Futebol', 'Complexo desportivo', NULL,
       38.73299, -9.22147, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:funsports-alfragide',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:funsports-alfragide'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.73299)) <= 0.00063 AND ABS(v.longitude - (-9.22147)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Funsports - Alfragide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.73299)) <= 0.0030 AND ABS(v.longitude - (-9.22147)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Troia Racket', 'Grândola', 'Setúbal', 'Troia, 7570-789 Grândola',
       'Padel;Ténis', 'Clube de padel', NULL,
       38.49294, -8.90075, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:troia-racket',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:troia-racket'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.49294)) <= 0.00063 AND ABS(v.longitude - (-8.90075)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Troia Racket'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.49294)) <= 0.0030 AND ABS(v.longitude - (-8.90075)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Belverde Club', 'Amora', 'Setúbal', '2845-256 Amora',
       'Padel', 'Clube de padel', NULL,
       38.60703, -9.13866, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:belverde-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:belverde-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.60703)) <= 0.00063 AND ABS(v.longitude - (-9.13866)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Belverde Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.60703)) <= 0.0030 AND ABS(v.longitude - (-9.13866)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'R8 Sports Club', 'Fajozes (Vila do Conde)', 'Porto', 'Rua 8, 4485-273 Fajozes',
       'Padel', 'Clube de padel', NULL,
       41.32742, -8.71008, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:r8-sports-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:r8-sports-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.32742)) <= 0.00063 AND ABS(v.longitude - (-8.71008)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('R8 Sports Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.32742)) <= 0.0030 AND ABS(v.longitude - (-8.71008)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Maia Padel', 'Milheirós (Maia)', 'Porto', 'Avenida Central de Milheirós 766, 4475-380 Milheirós',
       'Padel', 'Clube de padel', NULL,
       41.22618, -8.58183, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:maia-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:maia-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.22618)) <= 0.00063 AND ABS(v.longitude - (-8.58183)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Maia Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.22618)) <= 0.0030 AND ABS(v.longitude - (-8.58183)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Alley Courts II - Campo da Asprela', 'Porto', 'Porto', 'Rua Dr. Plácido da Costa, 4200-450 Porto',
       'Padel', 'Clube de padel', NULL,
       41.17871, -8.60113, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:alley-courts-ii-campo-da-asprela',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:alley-courts-ii-campo-da-asprela'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17871)) <= 0.00063 AND ABS(v.longitude - (-8.60113)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Alley Courts II - Campo da Asprela'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17871)) <= 0.0030 AND ABS(v.longitude - (-8.60113)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Your Top-Padel Industrial', 'Porto', 'Porto', 'Rua Eng. Ferreira Dias 241, 4100-246 Porto',
       'Padel', 'Clube de padel', NULL,
       41.17119, -8.64296, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:your-top-padel-industrial',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:your-top-padel-industrial'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17119)) <= 0.00063 AND ABS(v.longitude - (-8.64296)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Your Top-Padel Industrial'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17119)) <= 0.0030 AND ABS(v.longitude - (-8.64296)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'PopClub by Full Motion', 'Porto', 'Porto', 'Rua João Araújo Correia 103, 4100-433 Porto',
       'Padel', 'Clube de padel', NULL,
       41.16327, -8.63647, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:popclub-by-full-motion',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:popclub-by-full-motion'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.16327)) <= 0.00063 AND ABS(v.longitude - (-8.63647)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('PopClub by Full Motion'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.16327)) <= 0.0030 AND ABS(v.longitude - (-8.63647)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fourlux Padel Center - Lousada', 'Lousada', 'Porto', '4620-513 Lousada',
       'Padel', 'Clube de padel', NULL,
       41.27787, -8.28154, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fourlux-padel-center-lousada',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fourlux-padel-center-lousada'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.27787)) <= 0.00063 AND ABS(v.longitude - (-8.28154)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fourlux Padel Center - Lousada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.27787)) <= 0.0030 AND ABS(v.longitude - (-8.28154)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Beat Penafiel', 'Bustelo (Penafiel)', 'Porto', 'Rua Zona Industrial 2, 4560-323 Bustelo',
       'Padel', 'Clube de padel', NULL,
       41.21675, -8.28381, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-beat-penafiel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-beat-penafiel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.21675)) <= 0.00063 AND ABS(v.longitude - (-8.28381)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Beat Penafiel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.21675)) <= 0.0030 AND ABS(v.longitude - (-8.28381)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Clube de Gandra MaisGym', 'Gandra (Paredes)', 'Porto', 'Via Rota dos Móveis I, 4585-325 Gandra',
       'Padel', 'Clube de padel', NULL,
       41.18730, -8.43572, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-clube-de-gandra-maisgym',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-clube-de-gandra-maisgym'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.18730)) <= 0.00063 AND ABS(v.longitude - (-8.43572)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Clube de Gandra MaisGym'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.18730)) <= 0.0030 AND ABS(v.longitude - (-8.43572)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Nation Felgueiras', 'Felgueiras', 'Porto', 'Rua Verdial Horácio de Moura 170, 4650-328 Felgueiras',
       'Padel', 'Clube de padel', NULL,
       41.33532, -8.22655, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-nation-felgueiras',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-nation-felgueiras'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.33532)) <= 0.00063 AND ABS(v.longitude - (-8.22655)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Nation Felgueiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.33532)) <= 0.0030 AND ABS(v.longitude - (-8.22655)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia de Padel Paulo VI', 'Gondomar', 'Porto', 'Colégio Paulo VI, 4420-336 Gondomar',
       'Padel', 'Clube de padel', NULL,
       41.14718, -8.53222, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:academia-de-padel-paulo-vi',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:academia-de-padel-paulo-vi'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.14718)) <= 0.00063 AND ABS(v.longitude - (-8.53222)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia de Padel Paulo VI'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.14718)) <= 0.0030 AND ABS(v.longitude - (-8.53222)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ponto B', 'Fânzeres (Gondomar)', 'Porto', 'Parque Urbano de Fânzeres, 4510-534 Fânzeres',
       'Padel', 'Clube de padel', NULL,
       41.15945, -8.53894, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:ponto-b',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:ponto-b'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.15945)) <= 0.00063 AND ABS(v.longitude - (-8.53894)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ponto B'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.15945)) <= 0.0030 AND ABS(v.longitude - (-8.53894)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Outlier Padel Club', 'Jovim (Gondomar)', 'Porto', 'Rua D. Miguel, 4510-041 Jovim',
       'Padel', 'Clube de padel', NULL,
       41.10488, -8.50892, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:outlier-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:outlier-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.10488)) <= 0.00063 AND ABS(v.longitude - (-8.50892)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Outlier Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.10488)) <= 0.0030 AND ABS(v.longitude - (-8.50892)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Zone Padel - Aguda', 'Arcozelo (Vila Nova de Gaia)', 'Porto', 'Avenida Gomes Guerra 721, 4410-467 Arcozelo',
       'Padel', 'Clube de padel', NULL,
       41.05094, -8.65304, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:zone-padel-aguda',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:zone-padel-aguda'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.05094)) <= 0.00063 AND ABS(v.longitude - (-8.65304)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Zone Padel - Aguda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.05094)) <= 0.0030 AND ABS(v.longitude - (-8.65304)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jogos D'' Arcozelo', 'Arcozelo (Vila Nova de Gaia)', 'Porto', 'Rua Vilares, 4410-360 Arcozelo',
       'Futebol;Padel', 'Campo de futebol', NULL,
       41.04451, -8.63455, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:jogos-d-arcozelo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:jogos-d-arcozelo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.04451)) <= 0.00063 AND ABS(v.longitude - (-8.63455)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jogos D'' Arcozelo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.04451)) <= 0.0030 AND ABS(v.longitude - (-8.63455)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Selection Padel', 'Perosinho (Vila Nova de Gaia)', 'Porto', 'Rua de Sedões 17, Sermonde, 4415-117 Perosinho',
       'Padel', 'Clube de padel', NULL,
       41.05253, -8.58254, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:selection-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:selection-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.05253)) <= 0.00063 AND ABS(v.longitude - (-8.58254)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Selection Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.05253)) <= 0.0030 AND ABS(v.longitude - (-8.58254)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Cesar Padel', 'Cesar (Oliveira de Azeméis)', 'Aveiro', 'Praça da Liberdade, 3700-611 Cesar',
       'Padel', 'Clube de padel', NULL,
       40.91133, -8.43112, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:cesar-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:cesar-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.91133)) <= 0.00063 AND ABS(v.longitude - (-8.43112)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Cesar Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.91133)) <= 0.0030 AND ABS(v.longitude - (-8.43112)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel PT Arada', 'Arada (Ovar)', 'Aveiro', 'Rua do Sobreiro 50, 3885-133 Arada',
       'Padel', 'Clube de padel', NULL,
       40.89887, -8.59216, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-pt-arada',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-pt-arada'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.89887)) <= 0.00063 AND ABS(v.longitude - (-8.59216)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel PT Arada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.89887)) <= 0.0030 AND ABS(v.longitude - (-8.59216)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'iPadel', 'Lourosa (Santa Maria da Feira)', 'Aveiro', 'Zona Industrial do Casalinho, Rua II, 4535-155 Lourosa',
       'Padel', 'Clube de padel', NULL,
       40.97829, -8.55249, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:ipadel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:ipadel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.97829)) <= 0.00063 AND ABS(v.longitude - (-8.55249)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('iPadel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.97829)) <= 0.0030 AND ABS(v.longitude - (-8.55249)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fly Padel', 'Santa Maria de Lamas', 'Aveiro', 'Rua Bairro da Mata 644, 4535-404 Santa Maria de Lamas',
       'Padel', 'Clube de padel', NULL,
       40.97221, -8.57536, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fly-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fly-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.97221)) <= 0.00063 AND ABS(v.longitude - (-8.57536)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fly Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.97221)) <= 0.0030 AND ABS(v.longitude - (-8.57536)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Life Padel', 'Mesão Frio (Guimarães)', 'Braga', 'Travessa da Ribeira 629, 4810-235 Mesão Frio',
       'Padel', 'Clube de padel', NULL,
       41.44258, -8.24954, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:life-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:life-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.44258)) <= 0.00063 AND ABS(v.longitude - (-8.24954)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Life Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.44258)) <= 0.0030 AND ABS(v.longitude - (-8.24954)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Vallis', 'Pombeiro de Ribavizela (Felgueiras)', 'Porto', 'Rua de Pombeiro Ribavizela 49, 4610-642 Pombeiro de Ribavizela',
       'Padel', 'Clube de padel', NULL,
       41.37880, -8.20999, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-vallis',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-vallis'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.37880)) <= 0.00063 AND ABS(v.longitude - (-8.20999)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Vallis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.37880)) <= 0.0030 AND ABS(v.longitude - (-8.20999)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'BPM Padel Club', 'Fregim (Amarante)', 'Porto', 'Rua da Igreja, 4600-591 Fregim',
       'Padel', 'Clube de padel', NULL,
       41.26604, -8.10976, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:bpm-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:bpm-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.26604)) <= 0.00063 AND ABS(v.longitude - (-8.10976)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('BPM Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.26604)) <= 0.0030 AND ABS(v.longitude - (-8.10976)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Point Ermesinde', 'Ermesinde', 'Porto', 'Rua José Régio 100, 4445-416 Ermesinde',
       'Padel', 'Clube de padel', NULL,
       41.20613, -8.54966, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-point-ermesinde',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-point-ermesinde'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.20613)) <= 0.00063 AND ABS(v.longitude - (-8.54966)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Point Ermesinde'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.20613)) <= 0.0030 AND ABS(v.longitude - (-8.54966)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Futpark Padel', 'Cortegaça (Ovar)', 'Aveiro', 'Rua Futebol Clube Cortegaça, 3885-202 Cortegaça',
       'Padel;Futebol', 'Complexo desportivo', NULL,
       40.94283, -8.63838, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:futpark-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:futpark-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.94283)) <= 0.00063 AND ABS(v.longitude - (-8.63838)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Futpark Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.94283)) <= 0.0030 AND ABS(v.longitude - (-8.63838)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Tercena - GRT', 'Tercena (Oeiras)', 'Lisboa', '2730-163 Tercena',
       'Padel', 'Clube de padel', NULL,
       38.74673, -9.28325, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-tercena-grt',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-tercena-grt'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.74673)) <= 0.00063 AND ABS(v.longitude - (-9.28325)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Tercena - GRT'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.74673)) <= 0.0030 AND ABS(v.longitude - (-9.28325)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Funchalinho Tennis Academy', 'Costa da Caparica', 'Setúbal', 'Praceta Fernandes de Sá, 2825-834 Costa da Caparica',
       'Ténis', 'Clube de ténis', NULL,
       38.65295, -9.22045, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:funchalinho-tennis-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:funchalinho-tennis-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.65295)) <= 0.00063 AND ABS(v.longitude - (-9.22045)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Funchalinho Tennis Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.65295)) <= 0.0030 AND ABS(v.longitude - (-9.22045)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Alfragide', 'Damaia', 'Lisboa', 'Escola Secundária D. João V, 2720-364 Damaia',
       'Futebol', 'Campo de futebol', NULL,
       38.74138, -9.21630, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-alfragide',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-alfragide'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.74138)) <= 0.00063 AND ABS(v.longitude - (-9.21630)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Alfragide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.74138)) <= 0.0030 AND ABS(v.longitude - (-9.21630)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Decathlon Amadora - Parque Desportivo', 'Alfragide', 'Lisboa', '2610-017 Alfragide',
       'Futebol;Padel', 'Campo de futebol', NULL,
       38.72704, -9.21086, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:decathlon-amadora-parque-desportivo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:decathlon-amadora-parque-desportivo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.72704)) <= 0.00063 AND ABS(v.longitude - (-9.21086)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Decathlon Amadora - Parque Desportivo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.72704)) <= 0.0030 AND ABS(v.longitude - (-9.21086)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Alto do Duque - Amadora', 'Damaia', 'Lisboa', '2720-698 Damaia',
       'Futebol', 'Campo de futebol', NULL,
       38.74536, -9.22697, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:clube-alto-do-duque-amadora',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:clube-alto-do-duque-amadora'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.74536)) <= 0.00063 AND ABS(v.longitude - (-9.22697)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Alto do Duque - Amadora'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.74536)) <= 0.0030 AND ABS(v.longitude - (-9.22697)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Seixal', 'Seixal', 'Setúbal', 'Escola Secundária Alfredo dos Reis Silveira, 2840-400 Seixal',
       'Futebol', 'Campo de futebol', NULL,
       38.62658, -9.09750, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-seixal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-seixal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.62658)) <= 0.00063 AND ABS(v.longitude - (-9.09750)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Seixal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.62658)) <= 0.0030 AND ABS(v.longitude - (-9.09750)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Go Padel', 'Rio Tinto (Gondomar)', 'Porto', 'Rua do Bazar, 4435-217 Rio Tinto',
       'Padel', 'Clube de padel', NULL,
       41.18545, -8.55919, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:clube-go-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:clube-go-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.18545)) <= 0.00063 AND ABS(v.longitude - (-8.55919)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Go Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.18545)) <= 0.0030 AND ABS(v.longitude - (-8.55919)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CJD Padel Academy', 'Porto', 'Porto', 'Colégio Júlio Dinis, Porto',
       'Padel', 'Clube de padel', NULL,
       41.17657, -8.58020, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:cjd-padel-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:cjd-padel-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17657)) <= 0.00063 AND ABS(v.longitude - (-8.58020)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CJD Padel Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17657)) <= 0.0030 AND ABS(v.longitude - (-8.58020)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT '2HaveFun - Colégio Alemão do Porto', 'Porto', 'Porto', 'Rua de Guerra Junqueiro 162, 4150-386 Porto',
       'Padel', 'Clube de padel', NULL,
       41.15457, -8.63800, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:2havefun-colegio-alemao-do-porto',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:2havefun-colegio-alemao-do-porto'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.15457)) <= 0.00063 AND ABS(v.longitude - (-8.63800)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('2HaveFun - Colégio Alemão do Porto'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.15457)) <= 0.0030 AND ABS(v.longitude - (-8.63800)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fourlux Padel Center - Marco de Canaveses', 'Marco de Canaveses', 'Porto', 'Rua Cegonheira, 4635-768 Marco de Canaveses',
       'Padel', 'Clube de padel', NULL,
       41.18450, -8.14776, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fourlux-padel-center-marco-de-canaveses',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fourlux-padel-center-marco-de-canaveses'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.18450)) <= 0.00063 AND ABS(v.longitude - (-8.14776)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fourlux Padel Center - Marco de Canaveses'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.18450)) <= 0.0030 AND ABS(v.longitude - (-8.14776)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sportgreen', 'Valadares (Vila Nova de Gaia)', 'Porto', 'Rua Escola de Vila Chã 285, 4405-555 Valadares',
       'Futebol;Padel', 'Campo de futebol', NULL,
       41.09497, -8.64129, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:sportgreen',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:sportgreen'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.09497)) <= 0.00063 AND ABS(v.longitude - (-8.64129)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sportgreen'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.09497)) <= 0.0030 AND ABS(v.longitude - (-8.64129)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer Madalena', 'Madalena (Vila Nova de Gaia)', 'Porto', 'Rua Orfeão 376, 4405-726 Madalena',
       'Futebol', 'Campo de futebol', NULL,
       41.10782, -8.63965, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-madalena',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-madalena'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.10782)) <= 0.00063 AND ABS(v.longitude - (-8.63965)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer Madalena'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.10782)) <= 0.0030 AND ABS(v.longitude - (-8.63965)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer Canidelo', 'Canidelo (Vila Nova de Gaia)', 'Porto', 'Rua Emissor, 4400-395 Canidelo',
       'Futebol', 'Campo de futebol', NULL,
       41.12331, -8.64617, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-canidelo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-canidelo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.12331)) <= 0.00063 AND ABS(v.longitude - (-8.64617)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer Canidelo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.12331)) <= 0.0030 AND ABS(v.longitude - (-8.64617)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'A. J. Escola de Futebol Paulo Lima', 'Seixezelo (Vila Nova de Gaia)', 'Porto', 'Largo Padrão 102, 4415-940 Seixezelo',
       'Futebol', 'Campo de futebol', NULL,
       41.03326, -8.54974, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:a-j-escola-de-futebol-paulo-lima',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:a-j-escola-de-futebol-paulo-lima'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.03326)) <= 0.00063 AND ABS(v.longitude - (-8.54974)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('A. J. Escola de Futebol Paulo Lima'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.03326)) <= 0.0030 AND ABS(v.longitude - (-8.54974)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'LinQ Padel Monção', 'Monção', 'Viana do Castelo', 'Avenida Gândra 272, Pavilhão 1, 4950-298 Monção',
       'Padel', 'Clube de padel', NULL,
       42.06435, -8.50387, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:linq-padel-moncao',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:linq-padel-moncao'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (42.06435)) <= 0.00063 AND ABS(v.longitude - (-8.50387)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('LinQ Padel Monção'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (42.06435)) <= 0.0030 AND ABS(v.longitude - (-8.50387)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Cascais Padel & Nature', 'Cascais', 'Lisboa', 'Terceira Circular, 2755-009 Cascais',
       'Padel', 'Clube de padel', NULL,
       38.71901, -9.43121, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:cascais-padel-nature',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:cascais-padel-nature'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.71901)) <= 0.00063 AND ABS(v.longitude - (-9.43121)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Cascais Padel & Nature'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.71901)) <= 0.0030 AND ABS(v.longitude - (-9.43121)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Mem Martins', 'Rio de Mouro', 'Lisboa', 'Escola Secundária de Mem Martins, 2635-195 Rio de Mouro',
       'Futebol', 'Campo de futebol', NULL,
       38.78376, -9.33691, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-mem-martins',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-mem-martins'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.78376)) <= 0.00063 AND ABS(v.longitude - (-9.33691)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Mem Martins'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.78376)) <= 0.0030 AND ABS(v.longitude - (-9.33691)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quantum Park', 'Cacém', 'Lisboa', '2735-337 Cacém',
       'Padel', 'Clube de padel', NULL,
       38.76240, -9.30872, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:quantum-park',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:quantum-park'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.76240)) <= 0.00063 AND ABS(v.longitude - (-9.30872)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Quantum Park'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.76240)) <= 0.0030 AND ABS(v.longitude - (-9.30872)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia de Ténis de Almada', 'Sobreda', 'Setúbal', 'Rua Varela Silva, 2815-678 Sobreda',
       'Ténis', 'Clube de ténis', NULL,
       38.64673, -9.18151, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:academia-de-tenis-de-almada',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:academia-de-tenis-de-almada'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.64673)) <= 0.00063 AND ABS(v.longitude - (-9.18151)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia de Ténis de Almada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.64673)) <= 0.0030 AND ABS(v.longitude - (-9.18151)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Alto do Moinho Tennis Academy', 'Corroios', 'Setúbal', 'Rua António Silva, 2855-014 Corroios',
       'Ténis', 'Clube de ténis', NULL,
       38.63379, -9.16102, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:alto-do-moinho-tennis-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:alto-do-moinho-tennis-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.63379)) <= 0.00063 AND ABS(v.longitude - (-9.16102)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Alto do Moinho Tennis Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.63379)) <= 0.0030 AND ABS(v.longitude - (-9.16102)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Desportivo de Carenque', 'Belas', 'Lisboa', 'Rua Fonteireira 23, 2605-140 Belas',
       'Futebol', 'Campo de futebol', NULL,
       38.78002, -9.25508, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:campo-desportivo-de-carenque',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:campo-desportivo-de-carenque'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.78002)) <= 0.00063 AND ABS(v.longitude - (-9.25508)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Desportivo de Carenque'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.78002)) <= 0.0030 AND ABS(v.longitude - (-9.25508)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer Vigorosa', 'Porto', 'Porto', 'Rua Estrela e Vigorosa Sport 604, 4200-286 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.16743, -8.58956, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-vigorosa',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-vigorosa'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.16743)) <= 0.00063 AND ABS(v.longitude - (-8.58956)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer Vigorosa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.16743)) <= 0.0030 AND ABS(v.longitude - (-8.58956)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jogos D7 - Pedrouços', 'Pedrouços (Maia)', 'Porto', 'Rua Margarida Ferreira de Araújo, 4425-296 Pedrouços',
       'Futebol', 'Campo de futebol', NULL,
       41.19125, -8.58391, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:jogos-d7-pedroucos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:jogos-d7-pedroucos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.19125)) <= 0.00063 AND ABS(v.longitude - (-8.58391)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jogos D7 - Pedrouços'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.19125)) <= 0.0030 AND ABS(v.longitude - (-8.58391)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fut4us - FC Infesta', 'São Mamede de Infesta', 'Porto', 'Rua Maria Feliciana, 4465-004 São Mamede de Infesta',
       'Futebol', 'Campo de futebol', NULL,
       41.18720, -8.59878, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fut4us-fc-infesta',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fut4us-fc-infesta'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.18720)) <= 0.00063 AND ABS(v.longitude - (-8.59878)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fut4us - FC Infesta'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.18720)) <= 0.0030 AND ABS(v.longitude - (-8.59878)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer Arca D''Água', 'Porto', 'Porto', 'Rua de Nove de Abril, 4250-345 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.17006, -8.61561, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-arca-d-agua',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-arca-d-agua'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17006)) <= 0.00063 AND ABS(v.longitude - (-8.61561)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer Arca D''Água'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17006)) <= 0.0030 AND ABS(v.longitude - (-8.61561)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer Nun''Alvares', 'Porto', 'Porto', 'Rua Silva Porto 630, 4250-362 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.17327, -8.61708, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-nun-alvares',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-nun-alvares'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17327)) <= 0.00063 AND ABS(v.longitude - (-8.61708)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer Nun''Alvares'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17327)) <= 0.0030 AND ABS(v.longitude - (-8.61708)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CJD Sports Bonfim', 'Porto', 'Porto', 'Rua António Carneiro 423, 4300-027 Porto',
       'Futebol;Padel', 'Campo de futebol', NULL,
       41.15020, -8.59380, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:cjd-sports-bonfim',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:cjd-sports-bonfim'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.15020)) <= 0.00063 AND ABS(v.longitude - (-8.59380)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CJD Sports Bonfim'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.15020)) <= 0.0030 AND ABS(v.longitude - (-8.59380)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Oporto Indoor Games', 'Porto', 'Porto', 'Rua Eng. Ferreira Dias 1063, 4100-247 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.15794, -8.62910, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:oporto-indoor-games',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:oporto-indoor-games'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.15794)) <= 0.00063 AND ABS(v.longitude - (-8.62910)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Oporto Indoor Games'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.15794)) <= 0.0030 AND ABS(v.longitude - (-8.62910)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Porto (Prelada)', 'Porto', 'Porto', 'Travessa da Prelada, 4250-380 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.17081, -8.63361, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-porto-prelada',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-porto-prelada'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.17081)) <= 0.00063 AND ABS(v.longitude - (-8.63361)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Porto (Prelada)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.17081)) <= 0.0030 AND ABS(v.longitude - (-8.63361)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Gaia (Devesas)', 'Vila Nova de Gaia', 'Porto', 'Travessa José Fontana, 4400-193 Vila Nova de Gaia',
       'Futebol', 'Campo de futebol', NULL,
       41.12416, -8.61986, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-gaia-devesas',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-gaia-devesas'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.12416)) <= 0.00063 AND ABS(v.longitude - (-8.61986)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Gaia (Devesas)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.12416)) <= 0.0030 AND ABS(v.longitude - (-8.61986)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports Gaia (Oliveira do Douro)', 'Oliveira do Douro', 'Porto', 'Rua do Freixieiro, 4434-506 Oliveira do Douro',
       'Futebol', 'Campo de futebol', NULL,
       41.11527, -8.57616, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-gaia-oliveira-do-douro',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-gaia-oliveira-do-douro'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.11527)) <= 0.00063 AND ABS(v.longitude - (-8.57616)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports Gaia (Oliveira do Douro)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.11527)) <= 0.0030 AND ABS(v.longitude - (-8.57616)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fut 7 Oeiras', 'Oeiras', 'Lisboa', 'Agrupamento de Escolas Conde de Oeiras, 2780-102 Oeiras',
       'Futebol 7', 'Campo de futebol', NULL,
       38.69199, -9.31184, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fut-7-oeiras',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fut-7-oeiras'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.69199)) <= 0.00063 AND ABS(v.longitude - (-9.31184)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fut 7 Oeiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.69199)) <= 0.0030 AND ABS(v.longitude - (-9.31184)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fut 7 Paço de Arcos', 'Paço de Arcos', 'Lisboa', '2774-516 Paço de Arcos',
       'Futebol 7', 'Campo de futebol', NULL,
       38.69680, -9.29159, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fut-7-paco-de-arcos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fut-7-paco-de-arcos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.69680)) <= 0.00063 AND ABS(v.longitude - (-9.29159)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fut 7 Paço de Arcos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.69680)) <= 0.0030 AND ABS(v.longitude - (-9.29159)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Soccer São Mamede', 'São Mamede de Infesta', 'Porto', 'Rua Santo António do Telheiro 501, 4565-249 São Mamede de Infesta',
       'Futebol', 'Campo de futebol', NULL,
       41.19147, -8.61332, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:indoor-soccer-sao-mamede',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:indoor-soccer-sao-mamede'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.19147)) <= 0.00063 AND ABS(v.longitude - (-8.61332)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Indoor Soccer São Mamede'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.19147)) <= 0.0030 AND ABS(v.longitude - (-8.61332)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo Cultural e Social de Santo Eugénio', 'Porto', 'Porto', 'Rua das Andorinhas 18, 4250-036 Porto',
       'Futebol', 'Campo de futebol', NULL,
       41.18099, -8.63810, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:grupo-desportivo-cultural-e-social-de-santo-eugenio',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:grupo-desportivo-cultural-e-social-de-santo-eugenio'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.18099)) <= 0.00063 AND ABS(v.longitude - (-8.63810)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo Cultural e Social de Santo Eugénio'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.18099)) <= 0.0030 AND ABS(v.longitude - (-8.63810)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos Desportivos Decathlon Matosinhos', 'Perafita', 'Porto', 'Avenida Arq. Fernando Távora 1343, 4450-820 Perafita',
       'Futebol;Padel', 'Campo de futebol', NULL,
       41.21647, -8.68445, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:campos-desportivos-decathlon-matosinhos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:campos-desportivos-decathlon-matosinhos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.21647)) <= 0.00063 AND ABS(v.longitude - (-8.68445)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos Desportivos Decathlon Matosinhos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.21647)) <= 0.0030 AND ABS(v.longitude - (-8.68445)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grow Tennis Academy Azeitão', 'Azeitão', 'Setúbal', 'Rua Monte da Vinha, 2925-045 Azeitão',
       'Ténis', 'Clube de ténis', NULL,
       38.51080, -9.03273, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:grow-tennis-academy-azeitao',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:grow-tennis-academy-azeitao'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.51080)) <= 0.00063 AND ABS(v.longitude - (-9.03273)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grow Tennis Academy Azeitão'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.51080)) <= 0.0030 AND ABS(v.longitude - (-9.03273)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Arena PlaySports MyIndoor Sacavém', 'Prior Velho', 'Lisboa', '2685-002 Prior Velho',
       'Futebol', 'Campo de futebol', NULL,
       38.78955, -9.11183, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:arena-playsports-myindoor-sacavem',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:arena-playsports-myindoor-sacavem'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.78955)) <= 0.00063 AND ABS(v.longitude - (-9.11183)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Arena PlaySports MyIndoor Sacavém'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.78955)) <= 0.0030 AND ABS(v.longitude - (-9.11183)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Funsports - Frielas', 'Frielas (Loures)', 'Lisboa', '2660-028 Frielas',
       'Padel;Futebol', 'Complexo desportivo', NULL,
       38.82591, -9.14383, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:funsports-frielas',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:funsports-frielas'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.82591)) <= 0.00063 AND ABS(v.longitude - (-9.14383)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Funsports - Frielas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.82591)) <= 0.0030 AND ABS(v.longitude - (-9.14383)) <= 0.0040)))
);
