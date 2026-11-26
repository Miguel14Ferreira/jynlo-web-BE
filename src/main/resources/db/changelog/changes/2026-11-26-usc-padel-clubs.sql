-- More padel and tennis clubs listed publicly on Urban Sports Club, not yet in the directory.
-- Coordinates from those listings. Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vive Padel', 'Lisboa', 'Lisboa', 'Avenida Marechal Gomes da Costa 7, Edifício Decathlon Lisboa Oriente, Lisboa',
       'Padel', 'Clube de padel', NULL,
       38.7553510, -9.1038530, NULL, NULL, 'COMMUNITY', 'JYNLO:USC:vive-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:vive-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7553510)) <= 0.00063 AND ABS(v.longitude - (-9.1038530)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Vive Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7553510)) <= 0.0030 AND ABS(v.longitude - (-9.1038530)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Beat Freamunde', 'Freamunde', 'Porto', 'Rua de São Martinho, 4590 Freamunde',
       'Padel', 'Clube de padel', NULL,
       41.1752598, -8.3489705, NULL, '910328773', 'COMMUNITY', 'JYNLO:USC:padel-beat-freamunde',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-beat-freamunde'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.1752598)) <= 0.00063 AND ABS(v.longitude - (-8.3489705)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Beat Freamunde'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.1752598)) <= 0.0030 AND ABS(v.longitude - (-8.3489705)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pure Sports', 'Porto', 'Porto', 'Rua São Francisco Xavier 64, 4150-673 Porto',
       'Padel', 'Clube de padel', NULL,
       41.1562946, -8.6719775, NULL, '932575351', 'COMMUNITY', 'JYNLO:USC:pure-sports',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:pure-sports'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.1562946)) <= 0.00063 AND ABS(v.longitude - (-8.6719775)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pure Sports'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.1562946)) <= 0.0030 AND ABS(v.longitude - (-8.6719775)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Novo Padel Club Setúbal', 'Setúbal', 'Setúbal', 'Rua Alexandre O''Neill (Novotel), 2910-509 Setúbal',
       'Padel', 'Clube de padel', NULL,
       38.5346067, -8.8738634, NULL, NULL, 'COMMUNITY', 'JYNLO:USC:novo-padel-club-setubal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:novo-padel-club-setubal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.5346067)) <= 0.00063 AND ABS(v.longitude - (-8.8738634)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Novo Padel Club Setúbal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.5346067)) <= 0.0030 AND ABS(v.longitude - (-8.8738634)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Masters Premium Padel', 'Setúbal', 'Setúbal', 'EM536-1 119, 2910 Setúbal',
       'Padel', 'Clube de padel', NULL,
       38.5263356, -8.8284766, NULL, '931866844', 'COMMUNITY', 'JYNLO:USC:masters-premium-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:masters-premium-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.5263356)) <= 0.00063 AND ABS(v.longitude - (-8.8284766)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Masters Premium Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.5263356)) <= 0.0030 AND ABS(v.longitude - (-8.8284766)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Ténis Casas de Azeitão', 'Azeitão', 'Setúbal', 'Rua das Tipuanas, Lote 30, 2925-135 Azeitão',
       'Ténis', 'Clube de ténis', NULL,
       38.5386015, -9.0037606, NULL, '919324573', 'COMMUNITY', 'JYNLO:USC:escola-de-tenis-casas-de-azeitao',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:escola-de-tenis-casas-de-azeitao'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.5386015)) <= 0.00063 AND ABS(v.longitude - (-9.0037606)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola de Ténis Casas de Azeitão'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.5386015)) <= 0.0030 AND ABS(v.longitude - (-9.0037606)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'MyCenter Padel', 'Faro', 'Faro', 'Rua Matias Fernandes de Oliva 7, 8005-264 Faro',
       'Padel', 'Clube de padel', NULL,
       37.0327772, -7.9580887, NULL, NULL, 'COMMUNITY', 'JYNLO:USC:mycenter-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:mycenter-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.0327772)) <= 0.00063 AND ABS(v.longitude - (-7.9580887)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('MyCenter Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.0327772)) <= 0.0030 AND ABS(v.longitude - (-7.9580887)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'JF79 Sports Center', 'Viseu', 'Viseu', 'Rua Príncipe Perfeito, 3500-895 Viseu',
       'Padel', 'Clube de padel', NULL,
       40.6313803, -7.9055190, NULL, '925965148', 'COMMUNITY', 'JYNLO:USC:jf79-sports-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:jf79-sports-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.6313803)) <= 0.00063 AND ABS(v.longitude - (-7.9055190)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('JF79 Sports Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.6313803)) <= 0.0030 AND ABS(v.longitude - (-7.9055190)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'A-Z Padel Sintra', 'Sintra', 'Lisboa', 'Abrunheira Park, Rua das Maçarocas 1, 2710-056 Sintra',
       'Padel', 'Clube de padel', NULL,
       38.7682758, -9.3524947, NULL, '910423735', 'COMMUNITY', 'JYNLO:USC:abc-indoor-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:abc-indoor-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7682758)) <= 0.00063 AND ABS(v.longitude - (-9.3524947)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('A-Z Padel Sintra'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7682758)) <= 0.0030 AND ABS(v.longitude - (-9.3524947)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Spin Padel', 'Fitares', 'Lisboa', 'Praça Clube, 2635-453 Rio de Mouro, Sintra',
       'Padel', 'Clube de padel', NULL,
       38.7879380, -9.3157419, NULL, '928439668', 'COMMUNITY', 'JYNLO:USC:spin-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:spin-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7879380)) <= 0.00063 AND ABS(v.longitude - (-9.3157419)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Spin Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7879380)) <= 0.0030 AND ABS(v.longitude - (-9.3157419)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Onyu Belas Padel', 'Belas', 'Lisboa', 'Rua do Vale 2, 2605-295 Belas, Sintra',
       'Padel', 'Clube de padel', NULL,
       38.8095544, -9.2737033, NULL, '939002405', 'COMMUNITY', 'JYNLO:USC:onyu-belas-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:onyu-belas-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.8095544)) <= 0.00063 AND ABS(v.longitude - (-9.2737033)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Onyu Belas Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.8095544)) <= 0.0030 AND ABS(v.longitude - (-9.2737033)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Smash Padel Almada', 'Almada', 'Setúbal', 'Avenida do Cristo Rei, 2800-057 Almada',
       'Padel', 'Clube de padel', NULL,
       38.6762076, -9.1702177, NULL, '917045760', 'COMMUNITY', 'JYNLO:USC:smash-padel-almada',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:smash-padel-almada'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6762076)) <= 0.00063 AND ABS(v.longitude - (-9.1702177)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Smash Padel Almada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6762076)) <= 0.0030 AND ABS(v.longitude - (-9.1702177)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rackets Pro Nova', 'Caparica', 'Setúbal', 'Campus FCT NOVA, Largo da Torre, 2825-149 Caparica',
       'Padel', 'Clube de padel', NULL,
       38.6583836, -9.2045255, NULL, NULL, 'COMMUNITY', 'JYNLO:USC:rackets-pro-nova',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:rackets-pro-nova'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6583836)) <= 0.00063 AND ABS(v.longitude - (-9.2045255)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rackets Pro Nova'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6583836)) <= 0.0030 AND ABS(v.longitude - (-9.2045255)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quinta de Monserrate Clube', 'Matosinhos', 'Porto', 'Rua Doutor Eduardo Torres 149, 4450-218 Matosinhos',
       'Padel', 'Clube de padel', NULL,
       41.1806445, -8.6767767, NULL, '915501423', 'COMMUNITY', 'JYNLO:USC:quinta-de-monserrate-matosinhos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:quinta-de-monserrate-matosinhos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.1806445)) <= 0.00063 AND ABS(v.longitude - (-8.6767767)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Quinta de Monserrate Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.1806445)) <= 0.0030 AND ABS(v.longitude - (-8.6767767)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'M9 Padel Center', 'Maia', 'Porto', 'Rua Ernesto São Simão, 4470-216 Maia',
       'Padel', 'Clube de padel', NULL,
       41.2323353, -8.6349075, NULL, '220933724', 'COMMUNITY', 'JYNLO:USC:m9-padel-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:m9-padel-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.2323353)) <= 0.00063 AND ABS(v.longitude - (-8.6349075)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('M9 Padel Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.2323353)) <= 0.0030 AND ABS(v.longitude - (-8.6349075)) <= 0.0040)))
);
