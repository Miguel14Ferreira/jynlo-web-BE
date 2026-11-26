-- More padel and tennis venues with a public Playtomic page (full club sitemap, Portugal only), not yet in the directory.
-- Name and map pin from each club's public page; municipality from OpenStreetMap. Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ala Nun''Álvares de Gondomar', 'Gondomar', 'Porto', 'Rua Ala Nun''Álvares de Gondomar, 4420-014 Gondomar',
       'Padel', 'Clube de padel', NULL,
       41.13446, -8.53360, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:ala-nun-alvares-de-gondomar',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:ala-nun-alvares-de-gondomar'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.13446)) <= 0.00063 AND ABS(v.longitude - (-8.53360)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ala Nun''Álvares de Gondomar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.13446)) <= 0.0030 AND ABS(v.longitude - (-8.53360)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Azores Fusion Club', 'Angra do Heroísmo', 'Açores', 'Portões de São Pedro 1, 9700-097 Angra do Heroísmo',
       'Padel', 'Clube de padel', NULL,
       38.65729, -27.23077, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:azores-fusion-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:azores-fusion-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.65729)) <= 0.00063 AND ABS(v.longitude - (-27.23077)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Azores Fusion Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.65729)) <= 0.0030 AND ABS(v.longitude - (-27.23077)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'BFresh Hotel', 'Porto', 'Porto', 'Rua dos Castelos 96, 4250-115 Porto',
       'Padel', 'Clube de padel', NULL,
       41.16748, -8.62817, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:bfresh-hotel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:bfresh-hotel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.16748)) <= 0.00063 AND ABS(v.longitude - (-8.62817)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('BFresh Hotel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.16748)) <= 0.0030 AND ABS(v.longitude - (-8.62817)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Padel Igrejinha', 'Igrejinha (Arraiolos)', 'Évora', 'Rua Sol Nascente, 7040-223 Igrejinha',
       'Padel', 'Clube de padel', NULL,
       38.71212, -7.89431, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:campo-de-padel-igrejinha',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:campo-de-padel-igrejinha'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.71212)) <= 0.00063 AND ABS(v.longitude - (-7.89431)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Padel Igrejinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.71212)) <= 0.0030 AND ABS(v.longitude - (-7.89431)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Padel e Lazer', 'Funchal', 'Madeira', 'Rua das Hortas, 9050-024 Funchal',
       'Padel', 'Clube de padel', NULL,
       32.65354, -16.90853, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:centro-de-padel-e-lazer',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:centro-de-padel-e-lazer'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (32.65354)) <= 0.00063 AND ABS(v.longitude - (-16.90853)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Padel e Lazer'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (32.65354)) <= 0.0030 AND ABS(v.longitude - (-16.90853)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis e Padel de Lagos', 'Lagos', 'Faro', 'Rua General Humberto Delgado, 8600-707 Lagos',
       'Ténis;Padel', 'Clube de ténis', NULL,
       37.10972, -8.68167, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:clube-de-tenis-e-padel-de-lagos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:clube-de-tenis-e-padel-de-lagos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.10972)) <= 0.00063 AND ABS(v.longitude - (-8.68167)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis e Padel de Lagos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.10972)) <= 0.0030 AND ABS(v.longitude - (-8.68167)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Magnólia Mar', 'Lagos', 'Faro', 'Praia do Porto de Mós, 8600-262 Lagos',
       'Padel', 'Clube de padel', NULL,
       37.08807, -8.68666, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:clube-magnolia-mar',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:clube-magnolia-mar'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.08807)) <= 0.00063 AND ABS(v.longitude - (-8.68666)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Magnólia Mar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.08807)) <= 0.0030 AND ABS(v.longitude - (-8.68666)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Duna Parque Padel Court', 'Vila Nova de Milfontes', 'Beja', 'Rua Ruy de Menezes, Eira da Pedra, 7645-291 Vila Nova de Milfontes',
       'Padel', 'Clube de padel', NULL,
       37.72608, -8.78980, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:duna-parque-padel-court',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:duna-parque-padel-court'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.72608)) <= 0.00063 AND ABS(v.longitude - (-8.78980)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Duna Parque Padel Court'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.72608)) <= 0.0030 AND ABS(v.longitude - (-8.78980)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fazenda Viegas Padel', 'Lagos', 'Faro', 'Rua dos Lagos, Atalaia, 8600-382 Lagos',
       'Padel', 'Clube de padel', NULL,
       37.10279, -8.67303, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:fazenda-viegas-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:fazenda-viegas-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.10279)) <= 0.00063 AND ABS(v.longitude - (-8.67303)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fazenda Viegas Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.10279)) <= 0.0030 AND ABS(v.longitude - (-8.67303)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Golden Tulip Porto-Gaia', 'Canidelo (Vila Nova de Gaia)', 'Porto', 'Rua da Bélgica 86, 4400-044 Canidelo',
       'Padel', 'Clube de padel', NULL,
       41.13078, -8.66817, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:golden-tulip-porto-gaia',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:golden-tulip-porto-gaia'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.13078)) <= 0.00063 AND ABS(v.longitude - (-8.66817)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Golden Tulip Porto-Gaia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.13078)) <= 0.0030 AND ABS(v.longitude - (-8.66817)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Lawn Ténis de Carreiros', 'Porto', 'Porto', 'Rua Dr. Sousa Rosa, 4150-719 Porto',
       'Ténis;Padel', 'Clube de ténis', NULL,
       41.15715, -8.67832, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:grupo-lawn-tenis-de-carreiros',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:grupo-lawn-tenis-de-carreiros'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.15715)) <= 0.00063 AND ABS(v.longitude - (-8.67832)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Lawn Ténis de Carreiros'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.15715)) <= 0.0030 AND ABS(v.longitude - (-8.67832)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hotel Belavista da Luz', 'Luz (Lagos)', 'Faro', 'Urbanização Marluz, 8600-147 Luz',
       'Padel', 'Clube de padel', NULL,
       37.09104, -8.73302, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:hotel-belavista-da-luz',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:hotel-belavista-da-luz'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.09104)) <= 0.00063 AND ABS(v.longitude - (-8.73302)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hotel Belavista da Luz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.09104)) <= 0.0030 AND ABS(v.longitude - (-8.73302)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Maia Sport', 'Maia', 'Porto', 'Rua António Herculano Pereira, 4470-566 Maia',
       'Padel', 'Clube de padel', NULL,
       41.22789, -8.62105, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:maia-sport',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:maia-sport'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.22789)) <= 0.00063 AND ABS(v.longitude - (-8.62105)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Maia Sport'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.22789)) <= 0.0030 AND ABS(v.longitude - (-8.62105)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Nexo Padel Club', 'Câmara de Lobos', 'Madeira', 'Pavilhão Marcos Freitas, 9325-051 Câmara de Lobos',
       'Padel', 'Clube de padel', NULL,
       32.67070, -16.97744, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:nexo-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:nexo-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (32.67070)) <= 0.00063 AND ABS(v.longitude - (-16.97744)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Nexo Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (32.67070)) <= 0.0030 AND ABS(v.longitude - (-16.97744)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Porto de Recreio da Calheta', 'Calheta', 'Madeira', 'Porto de Recreio da Calheta, 9370-135 Calheta',
       'Padel', 'Clube de padel', NULL,
       32.71771, -17.17057, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-porto-de-recreio-da-calheta',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-porto-de-recreio-da-calheta'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (32.71771)) <= 0.00063 AND ABS(v.longitude - (-17.17057)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Porto de Recreio da Calheta'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (32.71771)) <= 0.0030 AND ABS(v.longitude - (-17.17057)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Vila Baleira', 'Porto Santo', 'Madeira', '9400-909 Porto Santo',
       'Padel', 'Clube de padel', NULL,
       33.03733, -16.36275, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:padel-vila-baleira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:padel-vila-baleira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (33.03733)) <= 0.00063 AND ABS(v.longitude - (-16.36275)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Vila Baleira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (33.03733)) <= 0.0030 AND ABS(v.longitude - (-16.36275)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Urbano do Vimieiro', 'Vimieiro (Arraiolos)', 'Évora', 'Parque Urbano do Rossio, 7040-652 Vimieiro',
       'Padel', 'Clube de padel', NULL,
       38.82998, -7.84419, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:parque-urbano-do-vimieiro',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:parque-urbano-do-vimieiro'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.82998)) <= 0.00063 AND ABS(v.longitude - (-7.84419)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Urbano do Vimieiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.82998)) <= 0.0030 AND ABS(v.longitude - (-7.84419)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Porto Santo Racket Center', 'Porto Santo', 'Madeira', '9400-242 Porto Santo',
       'Ténis;Padel', 'Clube de ténis', NULL,
       33.04692, -16.35985, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:porto-santo-racket-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:porto-santo-racket-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (33.04692)) <= 0.00063 AND ABS(v.longitude - (-16.35985)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Porto Santo Racket Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (33.04692)) <= 0.0030 AND ABS(v.longitude - (-16.35985)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Six Senses Douro Valley', 'Samodães (Lamego)', 'Viseu', 'Quinta Vale de Abraão, 5100-758 Samodães',
       'Padel;Ténis', 'Clube de padel', NULL,
       41.14476, -7.81146, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:six-senses-douro-valley',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:six-senses-douro-valley'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.14476)) <= 0.00063 AND ABS(v.longitude - (-7.81146)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Six Senses Douro Valley'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.14476)) <= 0.0030 AND ABS(v.longitude - (-7.81146)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vilalara Racquet Club by LUX Tennis', 'Porches (Lagoa)', 'Faro', 'Praia das Gaivotas, 8400-450 Porches',
       'Ténis;Padel', 'Clube de ténis', NULL,
       37.10151, -8.38534, NULL, NULL, 'COMMUNITY', 'JYNLO:PLAYTOMIC:vilalara-racquet-club-by-lux-tennis',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:vilalara-racquet-club-by-lux-tennis'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.10151)) <= 0.00063 AND ABS(v.longitude - (-8.38534)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Vilalara Racquet Club by LUX Tennis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.10151)) <= 0.0030 AND ABS(v.longitude - (-8.38534)) <= 0.0040)))
);
