-- Padel and tennis clubs gathered for venue outreach (public listings: Urban Sports Club, Playtomic, club pages).
-- Coordinates come from those listings. Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'MCPadel', 'Leiria', 'Leiria', 'Rua da Cerâmica, 2400-827 Leiria',
       'Padel', 'Clube de padel', NULL,
       39.6989686, -8.8300955, NULL, '919912189', 'COMMUNITY', 'JYNLO:USC:mcpadel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:mcpadel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (39.6989686)) <= 0.00063 AND ABS(v.longitude - (-8.8300955)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('MCPadel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (39.6989686)) <= 0.0030 AND ABS(v.longitude - (-8.8300955)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Wolf Padel Club', 'Tortosendo', 'Castelo Branco', 'Rua G, 6200-823 Tortosendo',
       'Padel', 'Clube de padel', NULL,
       40.2298777, -7.5023198, NULL, '966064518', 'COMMUNITY', 'JYNLO:USC:wolf-padel-club-tortosendo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:wolf-padel-club-tortosendo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.2298777)) <= 0.00063 AND ABS(v.longitude - (-7.5023198)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Wolf Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.2298777)) <= 0.0030 AND ABS(v.longitude - (-7.5023198)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Total Padel', 'Vila Nova de Gaia', 'Porto', 'Avenida Vasco da Gama 774, 4430-247 Vila Nova de Gaia',
       'Padel', 'Clube de padel', NULL,
       41.1185514, -8.5982042, NULL, '961311930', 'COMMUNITY', 'JYNLO:USC:total-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:total-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.1185514)) <= 0.00063 AND ABS(v.longitude - (-8.5982042)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Total Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.1185514)) <= 0.0030 AND ABS(v.longitude - (-8.5982042)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'PIC Padel Club', 'Amora', 'Setúbal', 'Rua Manuel Ribeiro 31, 2845-409 Amora, Seixal',
       'Padel', 'Clube de padel', NULL,
       38.6317523, -9.1421554, NULL, '963720136', 'COMMUNITY', 'JYNLO:USC:pic-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:pic-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6317523)) <= 0.00063 AND ABS(v.longitude - (-9.1421554)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('PIC Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6317523)) <= 0.0030 AND ABS(v.longitude - (-9.1421554)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Tribe', 'Leça da Palmeira', 'Porto', 'Rua Gonçalves Zarco 1813, 4450-685 Leça da Palmeira',
       'Padel', 'Clube de padel', NULL,
       41.2053033, -8.6844583, NULL, '221122892', 'COMMUNITY', 'JYNLO:USC:padel-tribe',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-tribe'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.2053033)) <= 0.00063 AND ABS(v.longitude - (-8.6844583)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Tribe'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.2053033)) <= 0.0030 AND ABS(v.longitude - (-8.6844583)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Riade Alvor View Padel', 'Portimão', 'Faro', 'Rua Júlio Amaro, 8500-001 Portimão',
       'Padel', 'Clube de padel', NULL,
       37.1431929, -8.5835066, NULL, '967233975', 'COMMUNITY', 'JYNLO:USC:riade-alvor-view-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:riade-alvor-view-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.1431929)) <= 0.00063 AND ABS(v.longitude - (-8.5835066)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Riade Alvor View Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.1431929)) <= 0.0030 AND ABS(v.longitude - (-8.5835066)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mr. Padel', 'Moita', 'Setúbal', 'Complexo Desportivo da Quinta do Matão, 2860-403 Moita',
       'Padel', 'Clube de padel', NULL,
       38.6492917, -8.9952005, NULL, '964138898', 'COMMUNITY', 'JYNLO:USC:mr-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:mr-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6492917)) <= 0.00063 AND ABS(v.longitude - (-8.9952005)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mr. Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6492917)) <= 0.0030 AND ABS(v.longitude - (-8.9952005)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'ProPadel Sports', 'Mozelos', 'Aveiro', 'Rua Ronocar 275, fração B, 4535-367 Mozelos',
       'Padel;Padbol', 'Clube de padel', NULL,
       41.0014465, -8.5789331, NULL, '921041257', 'COMMUNITY', 'JYNLO:USC:propadel-sports',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:propadel-sports'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.0014465)) <= 0.00063 AND ABS(v.longitude - (-8.5789331)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('ProPadel Sports'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.0014465)) <= 0.0030 AND ABS(v.longitude - (-8.5789331)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'LX Indoor Padel', 'Prior Velho', 'Lisboa', 'Rua dos Palmares, Armazém B2, 2680-159 Loures',
       'Padel', 'Clube de padel', NULL,
       38.8007358, -9.1422877, NULL, '927609220', 'COMMUNITY', 'JYNLO:USC:lx-indoor-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:lx-indoor-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.8007358)) <= 0.00063 AND ABS(v.longitude - (-9.1422877)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('LX Indoor Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.8007358)) <= 0.0030 AND ABS(v.longitude - (-9.1422877)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padcenter', 'Pontinha', 'Lisboa', 'Rua Projectada à Estrada da Paiã, 1675-076 Odivelas',
       'Padel;Padbol', 'Clube de padel', NULL,
       38.7820196, -9.1963492, NULL, '962227305', 'COMMUNITY', 'JYNLO:USC:padcenter',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padcenter'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7820196)) <= 0.00063 AND ABS(v.longitude - (-9.1963492)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padcenter'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7820196)) <= 0.0030 AND ABS(v.longitude - (-9.1963492)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CETO Padel', 'Oeiras', 'Lisboa', 'Rua Arthur Brandão, 2780-133 Oeiras',
       'Padel', 'Clube de padel', NULL,
       38.6916120, -9.3211676, NULL, '966284973', 'COMMUNITY', 'JYNLO:USC:ceto-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:ceto-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6916120)) <= 0.00063 AND ABS(v.longitude - (-9.3211676)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CETO Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6916120)) <= 0.0030 AND ABS(v.longitude - (-9.3211676)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CPA - Capital Padel Academy', 'Alcabideche', 'Lisboa', 'Rua Fonte da Carreira 144A, 2645-467 Alcabideche',
       'Padel', 'Clube de padel', NULL,
       38.7397806, -9.3766885, NULL, '214090457', 'COMMUNITY', 'JYNLO:USC:capital-padel-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:capital-padel-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7397806)) <= 0.00063 AND ABS(v.longitude - (-9.3766885)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CPA - Capital Padel Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7397806)) <= 0.0030 AND ABS(v.longitude - (-9.3766885)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Feel Padel', 'Pevidém', 'Braga', 'São Jorge de Selho, 4835 Guimarães',
       'Padel', 'Clube de padel', NULL,
       41.4359151, -8.3581489, NULL, '926812958', 'COMMUNITY', 'JYNLO:USC:feel-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:feel-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.4359151)) <= 0.00063 AND ABS(v.longitude - (-8.3581489)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Feel Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.4359151)) <= 0.0030 AND ABS(v.longitude - (-8.3581489)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT '77 Academy', 'Condeixa-a-Nova', 'Coimbra', 'Urbanização Balcões da Caneira, 3150-151 Condeixa-a-Nova',
       'Padel', 'Clube de padel', NULL,
       40.1201124, -8.4983668, NULL, '915209139', 'COMMUNITY', 'JYNLO:USC:77-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:77-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.1201124)) <= 0.00063 AND ABS(v.longitude - (-8.4983668)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('77 Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.1201124)) <= 0.0030 AND ABS(v.longitude - (-8.4983668)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Prime Padel', 'Coimbra', 'Coimbra', 'Rua de Coimbra 114, 3020-923 Coimbra',
       'Padel', 'Clube de padel', NULL,
       40.2660012, -8.4414889, NULL, '926397101', 'COMMUNITY', 'JYNLO:USC:prime-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:prime-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.2660012)) <= 0.00063 AND ABS(v.longitude - (-8.4414889)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Prime Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.2660012)) <= 0.0030 AND ABS(v.longitude - (-8.4414889)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT '4Padel Club Ílhavo', 'Ílhavo', 'Aveiro', 'Rua da Coutada 28, 3830-134 Ílhavo',
       'Padel', 'Clube de padel', NULL,
       40.6138718, -8.6631162, NULL, '917965904', 'COMMUNITY', 'JYNLO:USC:4padel-club-ilhavo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:4padel-club-ilhavo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.6138718)) <= 0.00063 AND ABS(v.longitude - (-8.6631162)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('4Padel Club Ílhavo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.6138718)) <= 0.0030 AND ABS(v.longitude - (-8.6631162)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Coimbra Rackets Club', 'Coimbra', 'Coimbra', NULL,
       'Ténis', 'Clube de ténis', NULL,
       40.1869188, -8.3979027, NULL, '910670519', 'COMMUNITY', 'JYNLO:USC:coimbra-rackets-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:coimbra-rackets-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (40.1869188)) <= 0.00063 AND ABS(v.longitude - (-8.3979027)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Coimbra Rackets Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (40.1869188)) <= 0.0030 AND ABS(v.longitude - (-8.3979027)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Game Set Padel', 'Fernão Ferro', 'Setúbal', 'Rua Seomara da Costa, 2875-339 Fernão Ferro, Seixal',
       'Padel', 'Clube de padel', NULL,
       38.5431737, -9.0683261, NULL, '932150834', 'COMMUNITY', 'JYNLO:USC:game-set-padel',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:game-set-padel'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.5431737)) <= 0.00063 AND ABS(v.longitude - (-9.0683261)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Game Set Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.5431737)) <= 0.0030 AND ABS(v.longitude - (-9.0683261)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Alverca Padel Club', 'Alverca do Ribatejo', 'Lisboa', 'Avenida Infante Dom Pedro 58, 2615 Alverca do Ribatejo',
       'Padel', 'Clube de padel', NULL,
       38.8914075, -9.0333650, NULL, '919023692', 'COMMUNITY', 'JYNLO:USC:alverca-padel-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:alverca-padel-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.8914075)) <= 0.00063 AND ABS(v.longitude - (-9.0333650)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Alverca Padel Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.8914075)) <= 0.0030 AND ABS(v.longitude - (-9.0333650)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Pedra', 'Santo Tirso', 'Porto', 'Rua Santo André 229, Santo Tirso',
       'Padel', 'Clube de padel', NULL,
       41.3285089, -8.4976638, NULL, '916530789', 'COMMUNITY', 'JYNLO:USC:padel-pedra',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-pedra'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.3285089)) <= 0.00063 AND ABS(v.longitude - (-8.4976638)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Pedra'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.3285089)) <= 0.0030 AND ABS(v.longitude - (-8.4976638)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Jardins Panorâmicos', 'Funchal', 'Madeira', 'Rua da Quinta Calaça 30, 9000-250 Funchal',
       'Padel', 'Campo de padel', NULL,
       32.6352600, -16.9396084, NULL, '291231502', 'COMMUNITY', 'JYNLO:USC:padel-jardins-panoramicos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-jardins-panoramicos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (32.6352600)) <= 0.00063 AND ABS(v.longitude - (-16.9396084)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Jardins Panorâmicos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (32.6352600)) <= 0.0030 AND ABS(v.longitude - (-16.9396084)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Sports Club', 'Sintra', 'Lisboa', 'Granja Park, Armazém 8, 2710-142 Sintra',
       'Padel;Padbol', 'Clube de padel', NULL,
       38.8224316, -9.3520287, NULL, '919941557', 'COMMUNITY', 'JYNLO:USC:padel-sports-club',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-sports-club'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.8224316)) <= 0.00063 AND ABS(v.longitude - (-9.3520287)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Sports Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.8224316)) <= 0.0030 AND ABS(v.longitude - (-9.3520287)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Factory Sintra', 'Cacém', 'Lisboa', 'Sulim Park, Alto da Bela Vista, 2735-340 Cacém',
       'Padel', 'Clube de padel', NULL,
       38.7616651, -9.3022499, NULL, '912281167', 'COMMUNITY', 'JYNLO:USC:padel-factory-sintra',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:USC:padel-factory-sintra'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7616651)) <= 0.00063 AND ABS(v.longitude - (-9.3022499)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Factory Sintra'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7616651)) <= 0.0030 AND ABS(v.longitude - (-9.3022499)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Chaves Padel Center', 'Chaves', 'Vila Real', 'Rua Dom Afonso III 44, 5400-027 Chaves',
       'Padel', 'Clube de padel', NULL,
       41.7333547, -7.4687316, NULL, '276326242', 'COMMUNITY', 'JYNLO:PLAYTOMIC:chaves-padel-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:chaves-padel-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.7333547)) <= 0.00063 AND ABS(v.longitude - (-7.4687316)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Chaves Padel Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.7333547)) <= 0.0030 AND ABS(v.longitude - (-7.4687316)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'AZ Padel Elite Barcelos', 'Barcelos', 'Braga', 'Rua de Santa Marta 118, Pav. 2, 4750-189 Barcelos',
       'Padel', 'Clube de padel', NULL,
       41.5345177, -8.6089097, NULL, '912640054', 'COMMUNITY', 'JYNLO:PLAYTOMIC:elite-padel-center',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:PLAYTOMIC:elite-padel-center'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (41.5345177)) <= 0.00063 AND ABS(v.longitude - (-8.6089097)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('AZ Padel Elite Barcelos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (41.5345177)) <= 0.0030 AND ABS(v.longitude - (-8.6089097)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Clube VRSA', 'Vila Real de Santo António', 'Faro', 'Complexo Desportivo de Vila Real de Santo António, 8900 Vila Real de Santo António',
       'Padel', 'Clube de padel', NULL,
       37.1889853, -7.4205261, NULL, 'padelclubevrsa@gmail.com', 'COMMUNITY', 'JYNLO:CLUB-SITE:tiepadel.com/padelclubevrsa',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='JYNLO:CLUB-SITE:tiepadel.com/padelclubevrsa'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (37.1889853)) <= 0.00063 AND ABS(v.longitude - (-7.4205261)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Clube VRSA'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (37.1889853)) <= 0.0030 AND ABS(v.longitude - (-7.4205261)) <= 0.0040)))
);
