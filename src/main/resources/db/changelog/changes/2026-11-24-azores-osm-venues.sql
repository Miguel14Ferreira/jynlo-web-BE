-- Azores sports venues from OpenStreetMap (© OpenStreetMap contributors, ODbL).
-- Map references, not proof of public access or bookings. Municipality from OSM Nominatim.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'VivaFit Ponta Delgada', 'Ponta Delgada', 'Açores', 'Av. D.Joao III 30 AB/AC, 9500-310, Ponta Delgada',
       NULL, 'Complexo desportivo', NULL,
       37.7474967, -25.6568872, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:1531991052',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:1531991052')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('VivaFit Ponta Delgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7474967)) <= 0.00070 AND ABS(v.longitude - (-25.6568872)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Boavista Clube da Ribeirinha', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66063, -27.18024 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.6606296, -27.1802398, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2062802656',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2062802656')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Boavista Clube da Ribeirinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6606296)) <= 0.00070 AND ABS(v.longitude - (-27.1802398)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Karting', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65671, -27.20768 (morada por confirmar)',
       'Karting', 'Pista desportiva', NULL,
       38.6567082, -27.207682, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2178449487',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2178449487')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Karting'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6567082)) <= 0.00070 AND ABS(v.longitude - (-27.207682)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Naturais', 'Açores', 'Açores', 'Localização no mapa: 37.89914, -25.82159 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.8991431, -25.8215928, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:2842800805',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:2842800805')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Naturais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8991431)) <= 0.00070 AND ABS(v.longitude - (-25.8215928)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Season Challenge Center 2', 'Lagoa', 'Açores', 'Localização no mapa: 37.74145, -25.65909 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7414543, -25.6590946, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3470563472',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3470563472')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Season Challenge Center 2'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7414543)) <= 0.00070 AND ABS(v.longitude - (-25.6590946)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Season Challenge Center 3', 'Lagoa', 'Açores', 'Localização no mapa: 37.74150, -25.65829 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7415001, -25.658288, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3470563473',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3470563473')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Season Challenge Center 3'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7415001)) <= 0.00070 AND ABS(v.longitude - (-25.658288)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Seasson Challenge 1', 'Lagoa', 'Açores', 'Localização no mapa: 37.73961, -25.66596 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.739606, -25.6659605, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:3470563474',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:3470563474')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Seasson Challenge 1'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.739606)) <= 0.00070 AND ABS(v.longitude - (-25.6659605)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Shaped Body X', 'Horta (Angústias)', 'Açores', 'Parque Empresarial e Tecnológico da Ilha do Faial, Horta (Angústias)',
       'Ginásio', 'Ginásio', NULL,
       38.5301747, -28.6507351, NULL, '+351 292391440', 'OPENSTREETMAP', 'OSM:NODE:6484039284',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:6484039284')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Shaped Body X'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5301747)) <= 0.00070 AND ABS(v.longitude - (-28.6507351)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio Cultura Física do Faial', 'Horta (Conceição)', 'Açores', 'Rua da Conceição 16 R/C, Horta (Conceição)',
       'Ginásio', 'Ginásio', NULL,
       38.5426367, -28.6239593, NULL, '+351 292293001', 'OPENSTREETMAP', 'OSM:NODE:6484041205',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:6484041205')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio Cultura Física do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5426367)) <= 0.00070 AND ABS(v.longitude - (-28.6239593)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Industrial Gym', 'Vila Franca do Campo', 'Açores', 'Localização no mapa: 37.72200, -25.44142 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.722003, -25.4414163, NULL, '+351 296 581 072', 'OPENSTREETMAP', 'OSM:NODE:9158334856',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:9158334856')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Industrial Gym'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.722003)) <= 0.00070 AND ABS(v.longitude - (-25.4414163)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Zona Balnear da Calheta', 'Açores', 'Açores', 'Localização no mapa: 39.08780, -28.00823 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       39.0878013, -28.0082273, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:11199545309',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11199545309')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Zona Balnear da Calheta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.0878013)) <= 0.00070 AND ABS(v.longitude - (-28.0082273)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'HL Health Club', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74410, -25.65659 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.7441034, -25.6565863, 'https://hlclub.pt/', '+351 926 490 255', 'OPENSTREETMAP', 'OSM:NODE:11254995100',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:11254995100')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('HL Health Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7441034)) <= 0.00070 AND ABS(v.longitude - (-25.6565863)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pedras Brancas', 'Lagoa', 'Açores', 'Localização no mapa: 37.73112, -25.49631 (morada por confirmar)',
       'Escalada', 'Complexo desportivo', NULL,
       37.7311158, -25.4963064, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:12271011122',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12271011122')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pedras Brancas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7311158)) <= 0.00070 AND ABS(v.longitude - (-25.4963064)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval de Povoação', 'Povoação', 'Açores', 'Localização no mapa: 37.74606, -25.24679 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7460591, -25.2467939, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:12630502917',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12630502917')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval de Povoação'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7460591)) <= 0.00070 AND ABS(v.longitude - (-25.2467939)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Santa Bárbara Surft School', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.81533, -25.55079 (morada por confirmar)',
       'Surf', 'Complexo desportivo', NULL,
       37.8153336, -25.5507895, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:12710040761',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:12710040761')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Santa Bárbara Surft School'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8153336)) <= 0.00070 AND ABS(v.longitude - (-25.5507895)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'PDL Physique Fitness Center', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74804, -25.66691 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.7480375, -25.6669119, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13537353089',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13537353089')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('PDL Physique Fitness Center'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7480375)) <= 0.00070 AND ABS(v.longitude - (-25.6669119)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'ACTIVE CLUBE', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74199, -25.67730 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.7419926, -25.6773012, 'https://activegym.pt', '+351 960 207 033', 'OPENSTREETMAP', 'OSM:NODE:13605363454',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13605363454')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('ACTIVE CLUBE'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7419926)) <= 0.00070 AND ABS(v.longitude - (-25.6773012)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate park', 'Corvo', 'Açores', 'Localização no mapa: 39.67137, -31.12078 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       39.671366, -31.1207767, NULL, NULL, 'OPENSTREETMAP', 'OSM:NODE:13808392821',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:NODE:13808392821')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate park'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.671366)) <= 0.00070 AND ABS(v.longitude - (-31.1207767)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Furnas Golf Club', 'Furnas-Sao Miguel', 'Açores', 'Achada das Furnas, 9675, Furnas-Sao Miguel',
       'Golfe', 'Campo de golfe', NULL,
       37.777547, -25.3419123, 'https://azoresgolfislands.com/', '+351 296 584 341', 'OPENSTREETMAP', 'OSM:WAY:27578460',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:27578460')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Furnas Golf Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.777547)) <= 0.00070 AND ABS(v.longitude - (-25.3419123)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Termas do Carapacho', 'Santa Cruz da Graciosa', 'Açores', 'Localização no mapa: 39.01281, -27.95943 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       39.0128102, -27.9594288, NULL, '+351 295 714 212', 'OPENSTREETMAP', 'OSM:WAY:73694789',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:73694789')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Termas do Carapacho'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.0128102)) <= 0.00070 AND ABS(v.longitude - (-27.9594288)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da EBS de Nordeste', 'Nordeste', 'Açores', 'Rua do Rosário, 9630-179, Nordeste',
       'Futebol', 'Campo desportivo', NULL,
       37.8363827, -25.1446423, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:93058278',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:93058278')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da EBS de Nordeste'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8363827)) <= 0.00070 AND ABS(v.longitude - (-25.1446423)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo - Campo de Jogos da Salga', 'Salga', 'Açores', 'Travessa do Campo, Salga',
       'Futebol', 'Campo desportivo', NULL,
       37.842309, -25.3008489, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:93519727',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:93519727')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo - Campo de Jogos da Salga'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.842309)) <= 0.00070 AND ABS(v.longitude - (-25.3008489)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Algarvia', 'Algarvia', 'Açores', 'Rua do Atalho, 9630-222, Algarvia',
       'Futebol', 'Campo desportivo', NULL,
       37.8461153, -25.2303952, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:93670900',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:93670900')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Algarvia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8461153)) <= 0.00070 AND ABS(v.longitude - (-25.2303952)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Atletismo das Laranjeiras', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74785, -25.65115 (morada por confirmar)',
       'Corrida', 'Pista desportiva', NULL,
       37.7478503, -25.6511467, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:96849970',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:96849970')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Atletismo das Laranjeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7478503)) <= 0.00070 AND ABS(v.longitude - (-25.6511467)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo José da Silva Calisto', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.80262, -25.60772 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       37.8026184, -25.6077177, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:102729312',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:102729312')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo José da Silva Calisto'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8026184)) <= 0.00070 AND ABS(v.longitude - (-25.6077177)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal Marquês Jácome Correia', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74546, -25.66199 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       37.7454566, -25.6619857, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:125367194',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:125367194')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal Marquês Jácome Correia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7454566)) <= 0.00070 AND ABS(v.longitude - (-25.6619857)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo do Lajedo', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74714, -25.67767 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7471443, -25.6776696, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:125964065',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:125964065')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo do Lajedo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7471443)) <= 0.00070 AND ABS(v.longitude - (-25.6776696)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo dos Bombeiros Voluntários de Ponta Delgada', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.75054, -25.65532 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7505448, -25.6553192, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:126087015',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:126087015')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo dos Bombeiros Voluntários de Ponta Delgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7505448)) <= 0.00070 AND ABS(v.longitude - (-25.6553192)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo da Universidade dos Açores', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74621, -25.66227 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7462118, -25.6622725, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:126087644',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:126087644')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo da Universidade dos Açores'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7462118)) <= 0.00070 AND ABS(v.longitude - (-25.6622725)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo da Escola Secundária das Laranjeiras', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74822, -25.65247 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7482215, -25.6524704, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:126094336',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:126094336')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo da Escola Secundária das Laranjeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7482215)) <= 0.00070 AND ABS(v.longitude - (-25.6524704)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio da Grotinha', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.75829, -25.67707 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       37.758292, -25.6770744, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:126099695',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:126099695')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio da Grotinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.758292)) <= 0.00070 AND ABS(v.longitude - (-25.6770744)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo de Ribeira Grande', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.81761, -25.51953 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.8176116, -25.5195257, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:156642745',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:156642745')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo de Ribeira Grande'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8176116)) <= 0.00070 AND ABS(v.longitude - (-25.5195257)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio João Paulo II', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66059, -27.20263 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       38.6605886, -27.2026315, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169010801',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169010801')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio João Paulo II'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6605886)) <= 0.00070 AND ABS(v.longitude - (-27.2026315)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65361, -27.22475 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.6536134, -27.2247459, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169012303',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169012303')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6536134)) <= 0.00070 AND ABS(v.longitude - (-27.2247459)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Patinódromo', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.73305, -27.05675 (morada por confirmar)',
       'Skate', 'Complexo desportivo', NULL,
       38.7330494, -27.0567481, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:169221500',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:169221500')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Patinódromo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7330494)) <= 0.00070 AND ABS(v.longitude - (-27.0567481)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio João Paulo II', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65945, -27.20505 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       38.6594504, -27.2050536, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:171775999',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:171775999')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio João Paulo II'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6594504)) <= 0.00070 AND ABS(v.longitude - (-27.2050536)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Municipais', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65536, -27.22449 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.6553616, -27.2244922, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:172654301',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:172654301')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Municipais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6553616)) <= 0.00070 AND ABS(v.longitude - (-27.2244922)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Muncipal de Futebol', 'Santa Cruz das Flores', 'Açores', 'Localização no mapa: 39.46187, -31.13156 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       39.4618749, -31.1315574, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:173692751',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:173692751')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Muncipal de Futebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.4618749)) <= 0.00070 AND ABS(v.longitude - (-31.1315574)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo', 'Lajes das Flores', 'Açores', 'Avenida do Emigrante, 9960-431, Lajes das Flores',
       NULL, 'Complexo desportivo', NULL,
       39.3763421, -31.1782853, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:173697010',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:173697010')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.3763421)) <= 0.00070 AND ABS(v.longitude - (-31.1782853)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal', 'Lajes das Flores', 'Açores', 'Rua Peixoto Pimentel, 9960-431, Lajes das Flores',
       'Futebol', 'Campo desportivo', NULL,
       39.3779648, -31.1833102, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:173697071',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:173697071')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.3779648)) <= 0.00070 AND ABS(v.longitude - (-31.1833102)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65474, -27.22428 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.6547374, -27.2242776, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:179834451',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:179834451')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6547374)) <= 0.00070 AND ABS(v.longitude - (-27.2242776)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo de Povoação', 'Povoação', 'Açores', 'Localização no mapa: 37.74609, -25.24505 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.746087, -25.2450478, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:182388299',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:182388299')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo de Povoação'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.746087)) <= 0.00070 AND ABS(v.longitude - (-25.2450478)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do Águia', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.77179, -25.70568 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       37.7717949, -25.7056755, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:184074084',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:184074084')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do Águia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7717949)) <= 0.00070 AND ABS(v.longitude - (-25.7056755)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Multiusos', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65408, -27.22415 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.654076, -27.2241478, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:184159956',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:184159956')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Multiusos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.654076)) <= 0.00070 AND ABS(v.longitude - (-27.2241478)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão da Agualva', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.77189, -27.17302 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.77189, -27.1730174, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:195870368',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:195870368')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão da Agualva'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.77189)) <= 0.00070 AND ABS(v.longitude - (-27.1730174)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Sintético de São Mateus da Calheta', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65581, -27.25814 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.6558072, -27.2581405, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196272338',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196272338')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Sintético de São Mateus da Calheta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6558072)) <= 0.00070 AND ABS(v.longitude - (-27.2581405)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Ribeirinha', 'Angra do Heroísmo', 'Açores', '36, Angra do Heroísmo',
       NULL, 'Estádio', NULL,
       38.6601417, -27.1803209, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196272945',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196272945')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Ribeirinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6601417)) <= 0.00070 AND ABS(v.longitude - (-27.1803209)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo do Sport Clube Barreiro', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.64799, -27.12242 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       38.6479916, -27.122422, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196274345',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196274345')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo do Sport Clube Barreiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6479916)) <= 0.00070 AND ABS(v.longitude - (-27.122422)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina das Quatro Ribeiras', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.79252, -27.22470 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.7925242, -27.2246966, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196276670',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196276670')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina das Quatro Ribeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7925242)) <= 0.00070 AND ABS(v.longitude - (-27.2246966)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Sport CLube "Os Leões"', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.64652, -27.10918 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       38.646518, -27.10918, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196276749',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196276749')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Sport CLube "Os Leões"'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.646518)) <= 0.00070 AND ABS(v.longitude - (-27.10918)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo da Serreta', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.74728, -27.36548 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.747281, -27.3654751, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196277850',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196277850')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo da Serreta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.747281)) <= 0.00070 AND ABS(v.longitude - (-27.3654751)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Multi-Usos', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.79174, -27.21819 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.7917422, -27.2181923, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:196285756',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:196285756')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Multi-Usos'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7917422)) <= 0.00070 AND ABS(v.longitude - (-27.2181923)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Andebol', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65577, -27.21294 (morada por confirmar)',
       'Andebol', 'Campo desportivo', NULL,
       38.6557741, -27.2129382, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201100285',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201100285')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Andebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6557741)) <= 0.00070 AND ABS(v.longitude - (-27.2129382)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Basquetebol', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65554, -27.21326 (morada por confirmar)',
       'Basquetebol', 'Campo desportivo', NULL,
       38.6555351, -27.2132646, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201100286',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201100286')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Basquetebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6555351)) <= 0.00070 AND ABS(v.longitude - (-27.2132646)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Futebol', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65604, -27.21265 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.6560354, -27.2126471, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201100287',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201100287')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Futebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6560354)) <= 0.00070 AND ABS(v.longitude - (-27.2126471)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Futebol', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65497, -27.21390 (morada por confirmar)',
       'Andebol', 'Campo desportivo', NULL,
       38.6549701, -27.2139035, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201100291',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201100291')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Futebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6549701)) <= 0.00070 AND ABS(v.longitude - (-27.2139035)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do Porto Martins', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.68576, -27.05720 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.68576, -27.057202, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201381950',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201381950')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do Porto Martins'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.68576)) <= 0.00070 AND ABS(v.longitude - (-27.057202)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Lajense', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.76758, -27.10383 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.7675781, -27.1038306, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:201735909',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:201735909')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Lajense'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7675781)) <= 0.00070 AND ABS(v.longitude - (-27.1038306)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sintético do João Paulo II', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65914, -27.20162 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.6591376, -27.201619, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:202525539',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:202525539')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sintético do João Paulo II'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6591376)) <= 0.00070 AND ABS(v.longitude - (-27.201619)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini Relvado do João Paulo II', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66042, -27.20124 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.6604225, -27.2012419, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:202525587',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:202525587')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mini Relvado do João Paulo II'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6604225)) <= 0.00070 AND ABS(v.longitude - (-27.2012419)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Caixa', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65624, -27.22641 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       38.65624, -27.2264137, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:203448673',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:203448673')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Caixa'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.65624)) <= 0.00070 AND ABS(v.longitude - (-27.2264137)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Secundária Tomás de Borba', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66054, -27.24227 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.6605413, -27.2422747, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:207564267',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:207564267')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Secundária Tomás de Borba'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6605413)) <= 0.00070 AND ABS(v.longitude - (-27.2422747)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Lawn Tenis da Ilha Terceira', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65728, -27.25805 (morada por confirmar)',
       'Ténis', 'Complexo desportivo', NULL,
       38.6572762, -27.258048, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:209993224',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:209993224')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Lawn Tenis da Ilha Terceira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6572762)) <= 0.00070 AND ABS(v.longitude - (-27.258048)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Naturais', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.86269, -25.69860 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.8626899, -25.6985963, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:227752271',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:227752271')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Naturais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8626899)) <= 0.00070 AND ABS(v.longitude - (-25.6985963)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Golfe da Ilha Terceira', 'Praia da Vitória Ilha Terceira', 'Açores', 'Fajãs da Agualva, 9760-909, Praia da Vitória Ilha Terceira',
       'Golfe', 'Campo de golfe', NULL,
       38.7343176, -27.1613643, 'https://www.terceiragolf.com/', '+351 295 902 444', 'OPENSTREETMAP', 'OSM:WAY:240191164',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:240191164')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Golfe da Ilha Terceira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7343176)) <= 0.00070 AND ABS(v.longitude - (-27.1613643)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Fajã Grande', 'Fajã Grande', 'Açores', 'Estrada Muncipal da Ponta da Fajã Grande, 9960-030, Fajã Grande',
       NULL, 'Complexo desportivo', NULL,
       39.4582212, -31.2630276, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:275515109',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:275515109')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Fajã Grande'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.4582212)) <= 0.00070 AND ABS(v.longitude - (-31.2630276)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo XXI', 'Fajã Grande', 'Açores', 'Avenida Marginal, 9960-030, Fajã Grande',
       'Futebol', 'Complexo desportivo', NULL,
       39.4540297, -31.2655826, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:276149352',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:276149352')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo XXI'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.4540297)) <= 0.00070 AND ABS(v.longitude - (-31.2655826)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal das Velas', 'Velas', 'Açores', 'Localização no mapa: 38.68836, -28.21808 (morada por confirmar)',
       'Futebol;Atletismo', 'Estádio', NULL,
       38.688356, -28.2180822, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:281162072',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:281162072')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal das Velas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.688356)) <= 0.00070 AND ABS(v.longitude - (-28.2180822)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Manuel de Arriaga', 'Horta', 'Açores', 'Localização no mapa: 38.52641, -28.64137 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5264064, -28.6413716, NULL, '+351 292202380', 'OPENSTREETMAP', 'OSM:WAY:304836103',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:304836103')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Manuel de Arriaga'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5264064)) <= 0.00070 AND ABS(v.longitude - (-28.6413716)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skatepark Laranjeiras', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74704, -25.65020 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       37.7470431, -25.6502011, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:308649274',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:308649274')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skatepark Laranjeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7470431)) <= 0.00070 AND ABS(v.longitude - (-25.6502011)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Parque da Alagoa', 'Horta', 'Açores', 'Localização no mapa: 38.54395, -28.61988 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       38.5439492, -28.6198818, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:308776563',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:308776563')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Parque da Alagoa'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5439492)) <= 0.00070 AND ABS(v.longitude - (-28.6198818)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal da Luz', 'Santa Cruz da Graciosa', 'Açores', 'Localização no mapa: 39.02357, -27.98800 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       39.023568, -27.987997, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:310655695',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:310655695')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal da Luz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.023568)) <= 0.00070 AND ABS(v.longitude - (-27.987997)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'CorpuSeven Health Club', 'Ribeirinha', 'Açores', 'Estrada Regional 1-1 1A, 9900-491, Ribeirinha',
       'Ginásio', 'Ginásio', NULL,
       38.5857553, -28.613898, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:311414776',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:311414776')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('CorpuSeven Health Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5857553)) <= 0.00070 AND ABS(v.longitude - (-28.613898)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ringue do Ribeirense', 'Lajes do Pico', 'Açores', 'Localização no mapa: 38.40192, -28.19156 (morada por confirmar)',
       NULL, 'Pista desportiva', NULL,
       38.4019191, -28.1915621, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:320210243',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:320210243')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ringue do Ribeirense'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.4019191)) <= 0.00070 AND ABS(v.longitude - (-28.1915621)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Ribeirense', 'Lajes do Pico', 'Açores', 'Localização no mapa: 38.40226, -28.19081 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.4022595, -28.1908094, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:320210582',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:320210582')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Ribeirense'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.4022595)) <= 0.00070 AND ABS(v.longitude - (-28.1908094)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Guadalupe', 'Santa Cruz da Graciosa', 'Açores', 'Localização no mapa: 39.06313, -28.02228 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       39.0631306, -28.0222757, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:338322701',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:338322701')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Guadalupe'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.0631306)) <= 0.00070 AND ABS(v.longitude - (-28.0222757)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Poli-Desportivo', 'Corvo', 'Açores', 'Localização no mapa: 39.67363, -31.11321 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       39.6736265, -31.1132051, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:355983134',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:355983134')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Poli-Desportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.6736265)) <= 0.00070 AND ABS(v.longitude - (-31.1132051)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Picadeiro coberto', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.97971, -25.12082 (morada por confirmar)',
       NULL, 'Campo desportivo', NULL,
       36.9797103, -25.1208248, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:367641916',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:367641916')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Picadeiro coberto'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9797103)) <= 0.00070 AND ABS(v.longitude - (-25.1208248)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Picadeiro', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.97983, -25.12105 (morada por confirmar)',
       NULL, 'Campo desportivo', NULL,
       36.9798342, -25.1210483, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:367641918',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:367641918')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Picadeiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9798342)) <= 0.00070 AND ABS(v.longitude - (-25.1210483)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Feteira Pequena', 'Feteira Pequena - Santana', 'Açores', 'Rua da Igreja, 9630-307, Feteira Pequena - Santana',
       NULL, 'Campo desportivo', NULL,
       37.8509499, -25.2549129, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:427539395',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:427539395')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Feteira Pequena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8509499)) <= 0.00070 AND ABS(v.longitude - (-25.2549129)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol 11 do Complexo Desportivo Municipal', 'Nordeste', 'Açores', 'Estrada Regional da Tronqueira, 9630-171, Nordeste',
       'Futebol', 'Campo desportivo', NULL,
       37.8281275, -25.1469687, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:427539411',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:427539411')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol 11 do Complexo Desportivo Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8281275)) <= 0.00070 AND ABS(v.longitude - (-25.1469687)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão da Escola Básica e Secundária do Nordeste', 'Nordeste', 'Açores', 'Rua do Rosário, 9630-179, Nordeste',
       NULL, 'Complexo desportivo', NULL,
       37.8361033, -25.1455281, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:427595462',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:427595462')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão da Escola Básica e Secundária do Nordeste'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8361033)) <= 0.00070 AND ABS(v.longitude - (-25.1455281)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos das Figueiras', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.85544, -25.69986 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       37.8554435, -25.6998576, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:433352876',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:433352876')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos das Figueiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8554435)) <= 0.00070 AND ABS(v.longitude - (-25.6998576)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polideportivo dos Remédios', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.88455, -25.73604 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.8845515, -25.7360432, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:433362899',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:433362899')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polideportivo dos Remédios'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8845515)) <= 0.00070 AND ABS(v.longitude - (-25.7360432)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polideportivo', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.85913, -25.70061 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.8591328, -25.7006128, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:433367066',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:433367066')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polideportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8591328)) <= 0.00070 AND ABS(v.longitude - (-25.7006128)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Mestre José Leste', 'Lagoa', 'Açores', 'Localização no mapa: 37.72260, -25.50662 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       37.7226003, -25.5066231, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&id=1058&ismenu=1&menuleft1=0&idterceironivel=508&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:440093653',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:440093653')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Mestre José Leste'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7226003)) <= 0.00070 AND ABS(v.longitude - (-25.5066231)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo de Piscinas Viriato Madeira', 'Ribeira Grande', 'Açores', 'Rua dos Bom, Ribeira Grande',
       'Natação', 'Complexo desportivo', 'INDOOR',
       37.8135714, -25.5266347, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:445496606',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:445496606')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo de Piscinas Viriato Madeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8135714)) <= 0.00070 AND ABS(v.longitude - (-25.5266347)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina da Caldeira', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.78196, -27.12066 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.7819582, -27.1206644, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:449857312',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:449857312')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina da Caldeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7819582)) <= 0.00070 AND ABS(v.longitude - (-27.1206644)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de futebol da serra da Ribeirinha', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66877, -27.17168 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.668774, -27.171678, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:468521380',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:468521380')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de futebol da serra da Ribeirinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.668774)) <= 0.00070 AND ABS(v.longitude - (-27.171678)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina do Clube Ana', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.96865, -25.15593 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       36.9686545, -25.1559282, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:498289234',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:498289234')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina do Clube Ana'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9686545)) <= 0.00070 AND ABS(v.longitude - (-25.1559282)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Picadeiro', 'Horta', 'Açores', 'Localização no mapa: 38.55016, -28.63840 (morada por confirmar)',
       NULL, 'Campo desportivo', NULL,
       38.5501599, -28.6384039, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:502373975',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:502373975')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Picadeiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5501599)) <= 0.00070 AND ABS(v.longitude - (-28.6384039)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Fajãzinha', 'Fajãzinha', 'Açores', 'Largo do Rossio, 9960-150, Fajãzinha',
       NULL, 'Complexo desportivo', NULL,
       39.432742, -31.2533504, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:514835596',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:514835596')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Fajãzinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.432742)) <= 0.00070 AND ABS(v.longitude - (-31.2533504)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Lomba', 'Lomba', 'Açores', 'Avenida de Santo Cristo, 9960-520, Lomba',
       NULL, 'Complexo desportivo', NULL,
       39.4008409, -31.1544764, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:514838068',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:514838068')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Lomba'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.4008409)) <= 0.00070 AND ABS(v.longitude - (-31.1544764)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Fazenda', 'Fazenda', 'Açores', 'Avenida de Santo Cristo, 9960-220, Fazenda',
       NULL, 'Complexo desportivo', NULL,
       39.3887395, -31.1657123, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:514843279',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:514843279')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Fazenda'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.3887395)) <= 0.00070 AND ABS(v.longitude - (-31.1657123)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Mosteiro', 'Mosteiro', 'Açores', 'Rua Engenheiro Pinelo, 9960-630, Mosteiro',
       NULL, 'Complexo desportivo', NULL,
       39.4121309, -31.2481989, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:514857351',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:514857351')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Mosteiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.4121309)) <= 0.00070 AND ABS(v.longitude - (-31.2481989)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quinta da Terça', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.75448, -25.60637 (morada por confirmar)',
       NULL, 'Centro hípico', NULL,
       37.754477, -25.6063718, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:532811122',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:532811122')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Quinta da Terça'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.754477)) <= 0.00070 AND ABS(v.longitude - (-25.6063718)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de São Pedro de Nordestinho', 'São Pedro de Nordestinho', 'Açores', 'Caminho da Lomba d''Além, São Pedro de Nordestinho',
       'Futebol', 'Campo desportivo', NULL,
       37.851815, -25.1952114, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:538062794',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:538062794')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de São Pedro de Nordestinho'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.851815)) <= 0.00070 AND ABS(v.longitude - (-25.1952114)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Lomba da Fazenda', 'Lomba da Fazenda', 'Açores', 'Rua Dr. Vitor Cabral Macedo, 9630-127, Lomba da Fazenda',
       'Futebol', 'Campo desportivo', NULL,
       37.8464508, -25.1606013, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:539638890',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:539638890')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Lomba da Fazenda'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8464508)) <= 0.00070 AND ABS(v.longitude - (-25.1606013)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Complexo Desportivo Municipal', 'Nordeste', 'Açores', 'Estrada Regional da Tronqueira, 9630-171, Nordeste',
       'Futebol', 'Campo desportivo', NULL,
       37.8286231, -25.1459044, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:544803056',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:544803056')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Complexo Desportivo Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8286231)) <= 0.00070 AND ABS(v.longitude - (-25.1459044)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Poça da Barroca', 'Corvo', 'Açores', 'Localização no mapa: 39.67415, -31.12243 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       39.6741459, -31.1224263, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:588460503',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:588460503')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Poça da Barroca'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.6741459)) <= 0.00070 AND ABS(v.longitude - (-31.1224263)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Golfe do Faial', 'Horta', 'Açores', 'Localização no mapa: 38.53738, -28.70824 (morada por confirmar)',
       'Golfe', 'Campo de golfe', NULL,
       38.5373801, -28.7082356, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:609251918',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:609251918')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Golfe do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5373801)) <= 0.00070 AND ABS(v.longitude - (-28.7082356)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo da Lajinha', 'Horta', 'Açores', 'Localização no mapa: 38.63837, -28.69512 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.6383668, -28.6951154, 'https://www.zerozero.pt/estadio.php?id=4962', '+351292946661', 'OPENSTREETMAP', 'OSM:WAY:631310126',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:631310126')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo da Lajinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6383668)) <= 0.00070 AND ABS(v.longitude - (-28.6951154)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Castelo Branco Sport Clube', 'Horta', 'Açores', 'Localização no mapa: 38.52443, -28.72657 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5244318, -28.7265748, NULL, '+351292943314', 'OPENSTREETMAP', 'OSM:WAY:631310135',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:631310135')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Castelo Branco Sport Clube'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5244318)) <= 0.00070 AND ABS(v.longitude - (-28.7265748)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos das Canadinhas', 'Horta', 'Açores', 'Localização no mapa: 38.52490, -28.66238 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5248962, -28.6623792, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:631310137',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:631310137')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos das Canadinhas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5248962)) <= 0.00070 AND ABS(v.longitude - (-28.6623792)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval da Horta', 'Horta', 'Açores', 'Localização no mapa: 38.53097, -28.62650 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5309696, -28.6265039, 'https://cnhorta.org/', NULL, 'OPENSTREETMAP', 'OSM:WAY:631310138',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:631310138')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval da Horta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5309696)) <= 0.00070 AND ABS(v.longitude - (-28.6265039)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sporting Clube da Horta', 'Horta', 'Açores', 'Localização no mapa: 38.53691, -28.62704 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5369058, -28.6270429, NULL, '+351 292292789', 'OPENSTREETMAP', 'OSM:WAY:631310142',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:631310142')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sporting Clube da Horta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5369058)) <= 0.00070 AND ABS(v.longitude - (-28.6270429)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Natural do Cais', 'São Roque do Pico', 'Açores', 'Localização no mapa: 38.52429, -28.31672 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.5242885, -28.3167226, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:632645814',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:632645814')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Natural do Cais'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5242885)) <= 0.00070 AND ABS(v.longitude - (-28.3167226)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Circuito de Motocross dos Cedros', 'Horta', 'Açores', 'Localização no mapa: 38.63312, -28.72695 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.633123, -28.72695, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:635793565',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:635793565')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Circuito de Motocross dos Cedros'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.633123)) <= 0.00070 AND ABS(v.longitude - (-28.72695)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Circuito da Caldeira', 'Horta', 'Açores', 'Localização no mapa: 38.57166, -28.67420 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5716581, -28.6742026, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:635793594',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:635793594')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Circuito da Caldeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5716581)) <= 0.00070 AND ABS(v.longitude - (-28.6742026)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Natural do Refugo', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.64696, -27.11289 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.6469585, -27.1128899, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:649731158',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:649731158')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Natural do Refugo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6469585)) <= 0.00070 AND ABS(v.longitude - (-27.1128899)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio da Alagoa', 'Horta', 'Açores', 'Localização no mapa: 38.54326, -28.62174 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5432625, -28.621742, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:664964089',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:664964089')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio da Alagoa'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5432625)) <= 0.00070 AND ABS(v.longitude - (-28.621742)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo do Angústias Atlético Clube', 'Horta', 'Açores', 'Localização no mapa: 38.52817, -28.63163 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.5281744, -28.6316307, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:665322138',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:665322138')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo do Angústias Atlético Clube'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5281744)) <= 0.00070 AND ABS(v.longitude - (-28.6316307)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Ténis', 'Horta', 'Açores', 'Localização no mapa: 38.53148, -28.62910 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5314758, -28.6291049, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:668658678',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:668658678')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Ténis'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5314758)) <= 0.00070 AND ABS(v.longitude - (-28.6291049)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Enchente', 'Madalena', 'Açores', 'Localização no mapa: 38.55873, -28.48073 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.5587309, -28.4807327, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:681376177',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:681376177')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Enchente'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5587309)) <= 0.00070 AND ABS(v.longitude - (-28.4807327)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Gimnodesportivo da Horta', 'Horta', 'Açores', 'Localização no mapa: 38.53226, -28.62908 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.532261, -28.6290801, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:686363099',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:686363099')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Gimnodesportivo da Horta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.532261)) <= 0.00070 AND ABS(v.longitude - (-28.6290801)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol de Castelo Branco', 'Horta', 'Açores', 'Localização no mapa: 38.53269, -28.73942 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.5326936, -28.7394177, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:686363105',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:686363105')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol de Castelo Branco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5326936)) <= 0.00070 AND ABS(v.longitude - (-28.7394177)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Tentadero das Doze Ribeiras', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.72335, -27.35447 (morada por confirmar)',
       NULL, 'Campo desportivo', NULL,
       38.7233481, -27.3544659, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:688731260',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:688731260')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Tentadero das Doze Ribeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7233481)) <= 0.00070 AND ABS(v.longitude - (-27.3544659)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscinas Municipais dos Pelames', 'Povoação', 'Açores', 'Localização no mapa: 37.74648, -25.24881 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7464813, -25.2488103, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:716731580',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:716731580')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscinas Municipais dos Pelames'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7464813)) <= 0.00070 AND ABS(v.longitude - (-25.2488103)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Vila das Capelas', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.83379, -25.67338 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       37.8337869, -25.6733799, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:723891197',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:723891197')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Vila das Capelas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8337869)) <= 0.00070 AND ABS(v.longitude - (-25.6733799)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Poços de São Vicente Ferreira', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.83429, -25.66859 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.8342942, -25.668591, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:723904000',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:723904000')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Poços de São Vicente Ferreira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8342942)) <= 0.00070 AND ABS(v.longitude - (-25.668591)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vitória Futebol Clube', 'São Roque do Pico', 'Açores', 'Localização no mapa: 38.53082, -28.32701 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.5308236, -28.3270079, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:725949770',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:725949770')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Vitória Futebol Clube'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5308236)) <= 0.00070 AND ABS(v.longitude - (-28.3270079)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'AquaFit - Health & Fitness Club', 'Lagoa', 'Açores', 'Localização no mapa: 37.74188, -25.57356 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.7418842, -25.573561, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:727321469',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:727321469')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('AquaFit - Health & Fitness Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7418842)) <= 0.00070 AND ABS(v.longitude - (-25.573561)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'AquaFit - Piscina Municipal', 'Lagoa', 'Açores', 'Localização no mapa: 37.74227, -25.57354 (morada por confirmar)',
       'Natação', 'Complexo desportivo', NULL,
       37.7422657, -25.5735429, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:727321472',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:727321472')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('AquaFit - Piscina Municipal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7422657)) <= 0.00070 AND ABS(v.longitude - (-25.5735429)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio João Gualberto Borges Arruda', 'Lagoa', 'Açores', 'Localização no mapa: 37.74162, -25.58179 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       37.7416245, -25.5817922, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=Article/Article&id=1057', NULL, 'OPENSTREETMAP', 'OSM:WAY:729066094',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729066094')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio João Gualberto Borges Arruda'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7416245)) <= 0.00070 AND ABS(v.longitude - (-25.5817922)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Atalhada', 'Lagoa', 'Açores', 'Localização no mapa: 37.74360, -25.58812 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7435981, -25.5881203, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1051&ismenu=1&menuleft1=0&idterceironivel=508&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729066095',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729066095')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Atalhada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7435981)) <= 0.00070 AND ABS(v.longitude - (-25.5881203)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Nossa Senhora do Rosário', 'Lagoa', 'Açores', 'Localização no mapa: 37.74674, -25.57346 (morada por confirmar)',
       'Ténis;Futebol', 'Complexo desportivo', NULL,
       37.7467393, -25.5734597, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1053&ismenu=1&menuleft1=0&idterceironivel=508&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729070474',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729070474')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Nossa Senhora do Rosário'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7467393)) <= 0.00070 AND ABS(v.longitude - (-25.5734597)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Remédios', 'Lagoa', 'Açores', 'Localização no mapa: 37.75081, -25.53951 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       37.7508145, -25.5395075, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1054&ismenu=1&menuleft1=0&idterceironivel=508&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729071245',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729071245')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Remédios'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7508145)) <= 0.00070 AND ABS(v.longitude - (-25.5395075)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Água de Pau', 'Lagoa', 'Açores', 'Localização no mapa: 37.72120, -25.50845 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7212, -25.5084517, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1437&ismenu=1&menuleft1=0&idterceironivel=1247&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729072876',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729072876')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Água de Pau'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7212)) <= 0.00070 AND ABS(v.longitude - (-25.5084517)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Cabouco', 'Lagoa', 'Açores', 'Localização no mapa: 37.75987, -25.56694 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       37.7598691, -25.566937, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1052&ismenu=1&menuleft1=0&idterceironivel=1247&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729073901',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729073901')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Cabouco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7598691)) <= 0.00070 AND ABS(v.longitude - (-25.566937)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Ribeira Chã', 'Lagoa', 'Açores', 'Localização no mapa: 37.72130, -25.48773 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.721304, -25.4877341, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1055&ismenu=1&menuleft1=0&idterceironivel=1247&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729074517',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729074517')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Ribeira Chã'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.721304)) <= 0.00070 AND ABS(v.longitude - (-25.4877341)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo de Santa Cruz', 'Lagoa', 'Açores', 'Localização no mapa: 37.74538, -25.56345 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       37.7453814, -25.5634522, 'http://lagoa-acores.pt/site/frontoffice/default.aspx?module=article/article&ismenu=1&id=1056&ismenu=1&menuleft1=0&idterceironivel=1247&idpai=1231', NULL, 'OPENSTREETMAP', 'OSM:WAY:729075646',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729075646')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo de Santa Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7453814)) <= 0.00070 AND ABS(v.longitude - (-25.5634522)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal da Vila Nova', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.78025, -27.14319 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.7802456, -27.1431909, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729101656',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729101656')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal da Vila Nova'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7802456)) <= 0.00070 AND ABS(v.longitude - (-27.1431909)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal Doutor Durval Monteiro', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.74099, -27.10450 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.7409855, -27.1045025, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729122114',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729122114')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal Doutor Durval Monteiro'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7409855)) <= 0.00070 AND ABS(v.longitude - (-27.1045025)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol de São Brás', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.76522, -27.13784 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.7652181, -27.1378358, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729122115',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729122115')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol de São Brás'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7652181)) <= 0.00070 AND ABS(v.longitude - (-27.1378358)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Manuel Linhares Lima', 'Lajes', 'Açores', 'Rua do Picão, 9760, Lajes',
       NULL, 'Estádio', NULL,
       38.7606556, -27.1092206, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729122117',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729122117')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Manuel Linhares Lima'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7606556)) <= 0.00070 AND ABS(v.longitude - (-27.1092206)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal da Praia da Vitória', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.73264, -27.06963 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.7326422, -27.0696292, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729122138',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729122138')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal da Praia da Vitória'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7326422)) <= 0.00070 AND ABS(v.longitude - (-27.0696292)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Clube Praiense', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.73204, -27.06998 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.7320352, -27.0699756, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729122139',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729122139')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sport Clube Praiense'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7320352)) <= 0.00070 AND ABS(v.longitude - (-27.0699756)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'GYM4You', 'Lagoa', 'Açores', 'Localização no mapa: 37.74433, -25.58486 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       37.7443342, -25.5848611, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:729380868',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:729380868')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('GYM4You'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7443342)) <= 0.00070 AND ABS(v.longitude - (-25.5848611)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Municipal Vitalino Fagundes', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.69004, -27.07938 (morada por confirmar)',
       'Basquetebol', 'Complexo desportivo', NULL,
       38.6900378, -27.0793781, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:733049039',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:733049039')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Municipal Vitalino Fagundes'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6900378)) <= 0.00070 AND ABS(v.longitude - (-27.0793781)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Municipal da Calheta', 'Calheta', 'Açores', 'Localização no mapa: 38.60371, -28.02715 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       38.603708, -28.0271455, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:733270336',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:733270336')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Municipal da Calheta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.603708)) <= 0.00070 AND ABS(v.longitude - (-28.0271455)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo da Urzelina', 'Velas', 'Açores', 'Localização no mapa: 38.64747, -28.12368 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.6474735, -28.1236792, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:733272251',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:733272251')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo da Urzelina'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6474735)) <= 0.00070 AND ABS(v.longitude - (-28.1236792)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal da Madalena', 'Madalena', 'Açores', 'Localização no mapa: 38.53710, -28.52595 (morada por confirmar)',
       'Futebol', 'Estádio', NULL,
       38.5371037, -28.5259528, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:740821120',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:740821120')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal da Madalena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5371037)) <= 0.00070 AND ABS(v.longitude - (-28.5259528)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Patinódromo Municipal da Madalena', 'Madalena', 'Açores', 'Localização no mapa: 38.53668, -28.52488 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5366791, -28.5248808, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:740821121',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:740821121')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Patinódromo Municipal da Madalena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5366791)) <= 0.00070 AND ABS(v.longitude - (-28.5248808)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina natural', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65544, -27.28186 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.6554392, -27.2818649, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:789252127',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:789252127')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina natural'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6554392)) <= 0.00070 AND ABS(v.longitude - (-27.2818649)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Praça de Touros de Santa Bárbara', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.69201, -27.35364 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       38.6920135, -27.3536393, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:791835696',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:791835696')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Praça de Touros de Santa Bárbara'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6920135)) <= 0.00070 AND ABS(v.longitude - (-27.3536393)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina natural', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.67108, -27.07526 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.6710794, -27.0752607, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:794356491',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:794356491')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina natural'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6710794)) <= 0.00070 AND ABS(v.longitude - (-27.0752607)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'FitnessHome', 'Calheta', 'Açores', 'Localização no mapa: 38.60667, -27.98419 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       38.6066745, -27.984192, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:824645313',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:824645313')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('FitnessHome'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6066745)) <= 0.00070 AND ABS(v.longitude - (-27.984192)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista Simplificada Ribeira Grande', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.81740, -25.51882 (morada por confirmar)',
       'Corrida', 'Pista desportiva', NULL,
       37.817398, -25.5188169, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:829886088',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:829886088')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista Simplificada Ribeira Grande'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.817398)) <= 0.00070 AND ABS(v.longitude - (-25.5188169)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo Fontinhas', 'Praia da Vitória', 'Açores', 'Localização no mapa: 38.74089, -27.10450 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       38.7408853, -27.1045036, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:895851759',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:895851759')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo Fontinhas'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.7408853)) <= 0.00070 AND ABS(v.longitude - (-27.1045036)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo de Santa Maria', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.95757, -25.14860 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       36.9575741, -25.1486004, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:944583856',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:944583856')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo de Santa Maria'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9575741)) <= 0.00070 AND ABS(v.longitude - (-25.1486004)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Sidónio Serpa', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74713, -25.67643 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7471301, -25.6764326, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:948500003',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:948500003')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Sidónio Serpa'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7471301)) <= 0.00070 AND ABS(v.longitude - (-25.6764326)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Capelo', 'Horta', 'Açores', 'Localização no mapa: 38.58234, -28.79331 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5823449, -28.7933058, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:956395114',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:956395114')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Capelo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5823449)) <= 0.00070 AND ABS(v.longitude - (-28.7933058)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Mini-Andebol', 'Horta', 'Açores', 'Localização no mapa: 38.52633, -28.62994 (morada por confirmar)',
       NULL, 'Campo desportivo', NULL,
       38.5263334, -28.6299389, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1025930291',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1025930291')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Mini-Andebol'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5263334)) <= 0.00070 AND ABS(v.longitude - (-28.6299389)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo de Caça e Golfe do Faial', 'Capelo', 'Açores', '174, 9901-909, Capelo',
       NULL, 'Complexo desportivo', NULL,
       38.5792314, -28.7800207, 'http://juve.cmhorta.pt/index.php/estruturas-juvenis/por-freguesias/72-clube-desportivo-de-caca-e-golfe-do-faial', NULL, 'OPENSTREETMAP', 'OSM:WAY:1038391834',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1038391834')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo de Caça e Golfe do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5792314)) <= 0.00070 AND ABS(v.longitude - (-28.7800207)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Basquetebol de Santa Luzia', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65888, -27.22535 (morada por confirmar)',
       'Basquetebol', 'Campo desportivo', NULL,
       38.6588834, -27.2253522, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1060306265',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1060306265')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Basquetebol de Santa Luzia'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6588834)) <= 0.00070 AND ABS(v.longitude - (-27.2253522)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Basquetebol do Clube ANA', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.96799, -25.15508 (morada por confirmar)',
       'Basquetebol', 'Campo desportivo', NULL,
       36.9679876, -25.1550826, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1062879129',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1062879129')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Basquetebol do Clube ANA'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9679876)) <= 0.00070 AND ABS(v.longitude - (-25.1550826)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Voleibol do Clube ANA', 'Vila do Porto', 'Açores', 'Localização no mapa: 36.96760, -25.15531 (morada por confirmar)',
       'Voleibol de Praia', 'Campo desportivo', NULL,
       36.9676037, -25.1553134, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1062879132',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1062879132')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Voleibol do Clube ANA'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (36.9676037)) <= 0.00070 AND ABS(v.longitude - (-25.1553134)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Entre Morros', 'Velas', 'Açores', 'Localização no mapa: 38.68694, -28.21808 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       38.6869449, -28.2180828, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1078761781',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1078761781')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Entre Morros'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6869449)) <= 0.00070 AND ABS(v.longitude - (-28.2180828)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hípico do Capelo', 'Horta', 'Açores', 'Localização no mapa: 38.57319, -28.78331 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.57319, -28.7833114, 'https://www.urbhorta.pt/index.php/equipamentos/item/4-centro-hipico', '+351 292945055', 'OPENSTREETMAP', 'OSM:WAY:1079063833',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1079063833')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hípico do Capelo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.57319)) <= 0.00070 AND ABS(v.longitude - (-28.7833114)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Serena', 'Povoação', 'Açores', 'Localização no mapa: 37.76952, -25.32023 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7695182, -25.3202281, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1080398903',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1080398903')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Serena'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7695182)) <= 0.00070 AND ABS(v.longitude - (-25.3202281)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mística', 'Povoação', 'Açores', 'Localização no mapa: 37.76956, -25.32005 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7695602, -25.3200498, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1080398904',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1080398904')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mística'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7695602)) <= 0.00070 AND ABS(v.longitude - (-25.3200498)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Convívio Thermae', 'Povoação', 'Açores', 'Localização no mapa: 37.76953, -25.31988 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.769535, -25.319884, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1080398905',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1080398905')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Convívio Thermae'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.769535)) <= 0.00070 AND ABS(v.longitude - (-25.319884)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Meditação', 'Povoação', 'Açores', 'Localização no mapa: 37.76944, -25.31975 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7694433, -25.3197462, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1080398906',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1080398906')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Meditação'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7694433)) <= 0.00070 AND ABS(v.longitude - (-25.3197462)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ribeira', 'Povoação', 'Açores', 'Localização no mapa: 37.76959, -25.32032 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7695921, -25.3203243, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1080398907',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1080398907')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ribeira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7695921)) <= 0.00070 AND ABS(v.longitude - (-25.3203243)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina do Faial da Terra', 'Povoação', 'Açores', 'Localização no mapa: 37.74171, -25.19415 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7417136, -25.1941474, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1096119230',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1096119230')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina do Faial da Terra'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7417136)) <= 0.00070 AND ABS(v.longitude - (-25.1941474)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Polidesportivo da Achada', 'Achada', 'Açores', 'Rua do Vigário, 9630-019, Achada',
       NULL, 'Campo desportivo', NULL,
       37.851845, -25.265212, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1155757125',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1155757125')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Polidesportivo da Achada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.851845)) <= 0.00070 AND ABS(v.longitude - (-25.265212)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Pedreira', 'Pedreira', 'Açores', 'Rua do Cinzeiro, 9630-186, Pedreira',
       NULL, 'Campo desportivo', NULL,
       37.8109356, -25.1441235, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1155757233',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1155757233')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Pedreira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8109356)) <= 0.00070 AND ABS(v.longitude - (-25.1441235)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis do Faial', 'Horta', 'Açores', 'Localização no mapa: 38.53033, -28.63389 (morada por confirmar)',
       'Ténis', 'Complexo desportivo', NULL,
       38.5303302, -28.6338923, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1160228190',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1160228190')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5303302)) <= 0.00070 AND ABS(v.longitude - (-28.6338923)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio do Vale', 'Flamengos', 'Açores', 'Rua Nova Artista Flamenguense, Flamengos',
       'Futebol', 'Complexo desportivo', NULL,
       38.5483313, -28.6525786, NULL, '+351 292948176', 'OPENSTREETMAP', 'OSM:WAY:1161456391',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1161456391')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio do Vale'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5483313)) <= 0.00070 AND ABS(v.longitude - (-28.6525786)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina natural do Boqueirão', 'Açores', 'Açores', 'Localização no mapa: 39.08725, -28.00729 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       39.0872452, -28.0072858, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1177589311',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1177589311')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina natural do Boqueirão'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.0872452)) <= 0.00070 AND ABS(v.longitude - (-28.0072858)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo de Castelo Branco', 'Horta', 'Açores', 'Localização no mapa: 38.53239, -28.73706 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       38.5323929, -28.7370648, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1198406598',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1198406598')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo de Castelo Branco'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5323929)) <= 0.00070 AND ABS(v.longitude - (-28.7370648)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Karate-Do Shotokan "O Morro"', 'Castelo Branco', 'Açores', 'Lugar do Rosto Alto 293A, 9900-323, Castelo Branco',
       'Karaté', 'Complexo desportivo', NULL,
       38.5420847, -28.7442037, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1201487400',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1201487400')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Karate-Do Shotokan "O Morro"'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5420847)) <= 0.00070 AND ABS(v.longitude - (-28.7442037)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skatepark da Relva', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.74931, -25.71327 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       37.7493089, -25.7132657, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1231241050',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1231241050')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skatepark da Relva'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7493089)) <= 0.00070 AND ABS(v.longitude - (-25.7132657)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Go Gym', 'Horta (Conceição)', 'Açores', 'Estrada 5 de Outubro 7A, 9900-093, Horta (Conceição)',
       'Ginásio', 'Ginásio', NULL,
       38.5456885, -28.630581, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1263019178',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1263019178')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Go Gym'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5456885)) <= 0.00070 AND ABS(v.longitude - (-28.630581)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT '2FITU', 'Horta (Angústias)', 'Açores', 'Localização no mapa: 38.52938, -28.64861 (morada por confirmar)',
       'Ginásio', 'Ginásio', NULL,
       38.5293811, -28.648606, NULL, '+351 292292105', 'OPENSTREETMAP', 'OSM:WAY:1300054743',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1300054743')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('2FITU'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5293811)) <= 0.00070 AND ABS(v.longitude - (-28.648606)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Azores Padel Club', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.75060, -25.65444 (morada por confirmar)',
       'Padel', 'Campo desportivo', NULL,
       37.7505955, -25.6544373, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1301003309',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1301003309')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Azores Padel Club'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7505955)) <= 0.00070 AND ABS(v.longitude - (-25.6544373)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol da Calheta de Nesquim', 'Lajes do Pico', 'Açores', 'Localização no mapa: 38.40113, -28.08205 (morada por confirmar)',
       'Futebol', 'Complexo desportivo', NULL,
       38.401134, -28.0820505, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1316395529',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1316395529')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol da Calheta de Nesquim'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.401134)) <= 0.00070 AND ABS(v.longitude - (-28.0820505)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque de Arvorismo dos Açores', 'Povoação', 'Açores', 'Localização no mapa: 37.76458, -25.33676 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7645765, -25.3367646, 'https://www.azorestreetop.com/', NULL, 'OPENSTREETMAP', 'OSM:WAY:1319294769',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1319294769')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque de Arvorismo dos Açores'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7645765)) <= 0.00070 AND ABS(v.longitude - (-25.3367646)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Bom Jesus', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.80975, -25.57698 (morada por confirmar)',
       'Futebol', 'Campo desportivo', NULL,
       37.8097468, -25.57698, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1322472693',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1322472693')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Bom Jesus'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8097468)) <= 0.00070 AND ABS(v.longitude - (-25.57698)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Angústias Atlético Clube', 'Horta (Angústias)', 'Açores', 'Rua Filipe de Carvalho, 9900-052, Horta (Angústias)',
       NULL, 'Complexo desportivo', NULL,
       38.5279751, -28.6283051, NULL, '+351 292292366', 'OPENSTREETMAP', 'OSM:WAY:1329141876',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1329141876')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Angústias Atlético Clube'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5279751)) <= 0.00070 AND ABS(v.longitude - (-28.6283051)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis de São Miguel', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.76330, -25.65983 (morada por confirmar)',
       'Ténis', 'Complexo desportivo', NULL,
       37.7632989, -25.6598342, 'http://www.ctsm.pt/', NULL, 'OPENSTREETMAP', 'OSM:WAY:1343019863',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1343019863')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis de São Miguel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7632989)) <= 0.00070 AND ABS(v.longitude - (-25.6598342)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Achadinha', 'Achadinha', 'Açores', 'Rua do Ramal, 9630-044, Achadinha',
       'Futsal', 'Complexo desportivo', NULL,
       37.8521875, -25.2841981, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1387513818',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1387513818')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Achadinha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8521875)) <= 0.00070 AND ABS(v.longitude - (-25.2841981)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Ponta Garça', 'Vila Franca do Campo', 'Açores', 'Localização no mapa: 37.71746, -25.37571 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7174643, -25.3757109, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1388055906',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1388055906')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Ponta Garça'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7174643)) <= 0.00070 AND ABS(v.longitude - (-25.3757109)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Municipal da Ribeira Grande', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.81862, -25.52993 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.8186165, -25.5299251, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1395523511',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1395523511')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Municipal da Ribeira Grande'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8186165)) <= 0.00070 AND ABS(v.longitude - (-25.5299251)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Polidesportivo', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.83171, -25.67452 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.8317113, -25.6745173, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1396186396',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1396186396')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Polidesportivo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8317113)) <= 0.00070 AND ABS(v.longitude - (-25.6745173)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skatepark de Santa Cruz', 'Lagoa', 'Açores', 'Localização no mapa: 37.74568, -25.56310 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       37.7456789, -25.5630997, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1426457044',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1426457044')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skatepark de Santa Cruz'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7456789)) <= 0.00070 AND ABS(v.longitude - (-25.5630997)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pump Track Parque Urbano Ponta Delgada', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.75754, -25.65901 (morada por confirmar)',
       'BMX', 'Pista desportiva', NULL,
       37.7575354, -25.6590125, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1427199794',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1427199794')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pump Track Parque Urbano Ponta Delgada'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7575354)) <= 0.00070 AND ABS(v.longitude - (-25.6590125)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Parque do Corvo', 'Corvo', 'Açores', 'Localização no mapa: 39.67135, -31.12083 (morada por confirmar)',
       'Skate', 'Campo desportivo', NULL,
       39.6713471, -31.1208279, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1510538498',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1510538498')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Parque do Corvo'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (39.6713471)) <= 0.00070 AND ABS(v.longitude - (-31.1208279)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Pedro Pauleta', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.76436, -25.61946 (morada por confirmar)',
       NULL, 'Complexo desportivo', NULL,
       37.7643645, -25.6194574, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1558095642',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1558095642')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Pedro Pauleta'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7643645)) <= 0.00070 AND ABS(v.longitude - (-25.6194574)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio de São Miguel', 'Ponta Delgada', 'Açores', 'Localização no mapa: 37.76379, -25.62261 (morada por confirmar)',
       NULL, 'Estádio', NULL,
       37.7637931, -25.6226069, NULL, NULL, 'OPENSTREETMAP', 'OSM:WAY:1558095644',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:WAY:1558095644')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio de São Miguel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7637931)) <= 0.00070 AND ABS(v.longitude - (-25.6226069)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Tanque de Água Termal', 'Povoação', 'Açores', 'Localização no mapa: 37.77156, -25.31488 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       37.7715568, -25.3148773, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:3434773',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:3434773')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Tanque de Água Termal'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.7715568)) <= 0.00070 AND ABS(v.longitude - (-25.3148773)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Atletismo da Ilha Terceira', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.66050, -27.20250 (morada por confirmar)',
       'Corrida', 'Pista desportiva', NULL,
       38.6604979, -27.2024992, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:6610333',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:6610333')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Atletismo da Ilha Terceira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6604979)) <= 0.00070 AND ABS(v.longitude - (-27.2024992)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Praça de Touros Ilha Terceira', 'Angra Do Heroísmo', 'Açores', 'Avenida Jacome de Bruges, 9700, Angra Do Heroísmo',
       NULL, 'Estádio', NULL,
       38.6576322, -27.2074778, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:6610334',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:6610334')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Praça de Touros Ilha Terceira'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6576322)) <= 0.00070 AND ABS(v.longitude - (-27.2074778)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Golfe da Batalha', 'Fenais da Luz, S. Miguel', 'Açores', 'Rua do Bom Jesus, Aflitos, 9545-234, Fenais da Luz, S. Miguel',
       'Golfe', 'Campo de golfe', NULL,
       37.8024884, -25.6331117, 'https://www.azoresgolfislands.com/', '+351 296 498 559', 'OPENSTREETMAP', 'OSM:RELATION:7931087',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:7931087')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Golfe da Batalha'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.8024884)) <= 0.00070 AND ABS(v.longitude - (-25.6331117)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de Santa Cruz das Ribeiras', 'Lajes do Pico', 'Açores', 'Localização no mapa: 38.40470, -28.18736 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.4047049, -28.1873588, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:9924518',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:9924518')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de Santa Cruz das Ribeiras'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.4047049)) <= 0.00070 AND ABS(v.longitude - (-28.1873588)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Atletismo da Ilha do Faial', 'Horta', 'Açores', 'Localização no mapa: 38.54317, -28.62143 (morada por confirmar)',
       'Corrida', 'Pista desportiva', NULL,
       38.5431739, -28.6214334, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:10121978',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:10121978')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Atletismo da Ilha do Faial'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.5431739)) <= 0.00070 AND ABS(v.longitude - (-28.6214334)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Terceira Mar Hotel', 'Angra do Heroísmo', 'Açores', 'Localização no mapa: 38.65588, -27.23118 (morada por confirmar)',
       'Natação', 'Piscina', NULL,
       38.6558772, -27.2311837, NULL, NULL, 'OPENSTREETMAP', 'OSM:RELATION:10968262',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:10968262')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Terceira Mar Hotel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (38.6558772)) <= 0.00070 AND ABS(v.longitude - (-27.2311837)) <= 0.00100)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo de Tiro de São Miguel', 'Ribeira Grande', 'Açores', 'Localização no mapa: 37.81487, -25.56826 (morada por confirmar)',
       'Tiro', 'Complexo desportivo', NULL,
       37.814869, -25.5682627, 'http://www.cdtsm.pt/', NULL, 'OPENSTREETMAP', 'OSM:RELATION:11169141',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE (v.information_source='OPENSTREETMAP' AND v.source_reference='OSM:RELATION:11169141')
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo de Tiro de São Miguel'))
         AND (v.latitude IS NULL OR v.longitude IS NULL
           OR (ABS(v.latitude - (37.814869)) <= 0.00070 AND ABS(v.longitude - (-25.5682627)) <= 0.00100)))
);
