-- Cascais sports facilities (Câmara Municipal de Cascais, dados abertos "Equipamentos Desportivos", CC BY).
-- Schools, gyms, club headquarters and fitness circuits are left out. Skipped when a venue already sits within ~70 m or has the same name nearby.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Autódromo Fernanda Pires da Silva', 'Cascais', 'Lisboa', 'Avenida Alfredo César Torres, 2645-544 Alcabideche',
       'Automobilismo', 'Complexo desportivo', NULL,
       38.7489765, -9.3936225, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:autodromo-fernanda-pires-da-silva',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:autodromo-fernanda-pires-da-silva'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7489765)) <= 0.00063 AND ABS(v.longitude - (-9.3936225)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Autódromo Fernanda Pires da Silva'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7489765)) <= 0.0030 AND ABS(v.longitude - (-9.3936225)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Ténis de Sassoeiros', 'Cascais', 'Lisboa', 'Rua Ilha das Flores, 2775-802 Carcavelos',
       'Ténis', 'Clube de ténis', NULL,
       38.6995090, -9.3222558, NULL, '214574313', 'OFFICIAL', 'CMC:DESPORTO:clube-tenis-de-sassoeiros',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:clube-tenis-de-sassoeiros'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6995090)) <= 0.00063 AND ABS(v.longitude - (-9.3222558)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Ténis de Sassoeiros'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6995090)) <= 0.0030 AND ABS(v.longitude - (-9.3222558)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo dos Lombos', 'Cascais', 'Lisboa', 'Rua da Feitoria, 2775-568 Carcavelos',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.6875930, -9.3260633, NULL, '214585830', 'OFFICIAL', 'CMC:DESPORTO:pavilhao-desportivo-dos-lombos',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:pavilhao-desportivo-dos-lombos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6875930)) <= 0.00063 AND ABS(v.longitude - (-9.3260633)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo dos Lombos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6875930)) <= 0.0030 AND ABS(v.longitude - (-9.3260633)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Carcavelos Ténis', 'Cascais', 'Lisboa', 'Rua Pedro Álvares Cabral 2, 2775-615 Carcavelos',
       'Ténis', 'Clube de ténis', NULL,
       38.6854377, -9.3417206, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:carcavelos-tenis',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:carcavelos-tenis'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6854377)) <= 0.00063 AND ABS(v.longitude - (-9.3417206)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Carcavelos Ténis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6854377)) <= 0.0030 AND ABS(v.longitude - (-9.3417206)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hipico da Costa do Estoril', 'Cascais', 'Lisboa', 'Avenida da Charneca 186, 2750-530 Cascais',
       'Hipismo', 'Complexo desportivo', NULL,
       38.7346135, -9.4490887, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:centro-hipico-da-costa-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:centro-hipico-da-costa-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7346135)) <= 0.00063 AND ABS(v.longitude - (-9.4490887)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hipico da Costa do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7346135)) <= 0.0030 AND ABS(v.longitude - (-9.4490887)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hípico da Quinta da Marinha', 'Cascais', 'Lisboa', 'Rua dos Cedros, 2750-004 Cascais',
       'Hipismo', 'Complexo desportivo', NULL,
       38.7129454, -9.4584431, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:centro-hipico-da-quinta-da-marinha',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:centro-hipico-da-quinta-da-marinha'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7129454)) <= 0.00063 AND ABS(v.longitude - (-9.4584431)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hípico da Quinta da Marinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7129454)) <= 0.0030 AND ABS(v.longitude - (-9.4584431)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Golfe Quinta da Marinha', 'Cascais', 'Lisboa', 'Rua dos Plátanos, 2750-002 Cascais',
       'Golfe', 'Campo de golfe', NULL,
       38.7012280, -9.4546947, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-golfe-quinta-da-marinha',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-golfe-quinta-da-marinha'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7012280)) <= 0.00063 AND ABS(v.longitude - (-9.4546947)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Golfe Quinta da Marinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7012280)) <= 0.0030 AND ABS(v.longitude - (-9.4546947)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Playbowling', 'Cascais', 'Lisboa', 'Rua Chesol 35, 2750-024 Cascais',
       'Bowling', 'Complexo desportivo', NULL,
       38.7248298, -9.4440749, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:playbowling',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:playbowling'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7248298)) <= 0.00063 AND ABS(v.longitude - (-9.4440749)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Playbowling'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7248298)) <= 0.0030 AND ABS(v.longitude - (-9.4440749)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Oitavos Golf', 'Cascais', 'Lisboa', 'Quinta da Marinha, 2750-006 Cascais',
       'Golfe', 'Campo de golfe', NULL,
       38.7062084, -9.4656777, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:oitavos-golf',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:oitavos-golf'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7062084)) <= 0.00063 AND ABS(v.longitude - (-9.4656777)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Oitavos Golf'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7062084)) <= 0.0030 AND ABS(v.longitude - (-9.4656777)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Hipódromo Manuel Possolo', 'Cascais', 'Lisboa', 'Avenida da República 389, 2750-475 Cascais',
       'Hipismo', 'Complexo desportivo', NULL,
       38.6932747, -9.4250309, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:hipodromo-manuel-possolo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:hipodromo-manuel-possolo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6932747)) <= 0.00063 AND ABS(v.longitude - (-9.4250309)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Hipódromo Manuel Possolo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6932747)) <= 0.0030 AND ABS(v.longitude - (-9.4250309)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Futebol Bicuda', 'Cascais', 'Lisboa', 'Rua das Galinholas, 2750-693 Cascais',
       'Futebol', 'Campo de futebol', NULL,
       38.7076976, -9.4490000, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-futebol-bicuda',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-futebol-bicuda'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7076976)) <= 0.00063 AND ABS(v.longitude - (-9.4490000)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Futebol Bicuda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7076976)) <= 0.0030 AND ABS(v.longitude - (-9.4490000)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Golfe do Estoril', 'Cascais', 'Lisboa', 'Avenida da República, 2765-273 Estoril',
       'Golfe', 'Campo de golfe', NULL,
       38.7217908, -9.3954494, NULL, '214681376', 'OFFICIAL', 'CMC:DESPORTO:campo-golfe-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-golfe-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7217908)) <= 0.00063 AND ABS(v.longitude - (-9.3954494)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Golfe do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7217908)) <= 0.0030 AND ABS(v.longitude - (-9.3954494)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo de Piscinas da Alapraia', 'Cascais', 'Lisboa', 'Rua António da Costa Carvalho 247, 2765-15 Estoril',
       'Natação', 'Piscina', NULL,
       38.7085211, -9.3779300, NULL, '214685990', 'OFFICIAL', 'CMC:DESPORTO:complexo-de-piscinas-da-alapraia',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:complexo-de-piscinas-da-alapraia'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7085211)) <= 0.00063 AND ABS(v.longitude - (-9.3779300)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo de Piscinas da Alapraia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7085211)) <= 0.0030 AND ABS(v.longitude - (-9.3779300)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro 25 de Abril', 'Cascais', 'Lisboa', 'Rua Francisco de Sousa Tavares, 2785-320 São Domingos de Rana',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7271344, -9.3402278, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-bairro-25-de-abril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-bairro-25-de-abril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7271344)) <= 0.00063 AND ABS(v.longitude - (-9.3402278)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro 25 de Abril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7271344)) <= 0.0030 AND ABS(v.longitude - (-9.3402278)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis do Estoril', 'Cascais', 'Lisboa', 'Avenida Condes de Barcelona, 2765-470 Estoril',
       'Ténis', 'Clube de ténis', NULL,
       38.7153934, -9.3924934, NULL, '214662770', 'OFFICIAL', 'CMC:DESPORTO:clube-de-tenis-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:clube-de-tenis-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7153934)) <= 0.00063 AND ABS(v.longitude - (-9.3924934)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7153934)) <= 0.0030 AND ABS(v.longitude - (-9.3924934)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Tamariz', 'Cascais', 'Lisboa', 'Praia do Tamariz',
       'Natação', 'Piscina', NULL,
       38.7032818, -9.4012578, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:piscina-tamariz',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:piscina-tamariz'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7032818)) <= 0.00063 AND ABS(v.longitude - (-9.4012578)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Tamariz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7032818)) <= 0.0030 AND ABS(v.longitude - (-9.4012578)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini-golf Jardins da Parede', 'Cascais', 'Lisboa', 'Avenida do Jardim, 2775-343 Parede',
       'Golfe', 'Campo de golfe', NULL,
       38.6964675, -9.3671238, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:mini-golf-jardins-da-parede',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:mini-golf-jardins-da-parede'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6964675)) <= 0.00063 AND ABS(v.longitude - (-9.3671238)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mini-golf Jardins da Parede'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6964675)) <= 0.0030 AND ABS(v.longitude - (-9.3671238)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo e Recreativo de Talaíde - Campo de Futebol Fernando Sabido', 'Cascais', 'Lisboa', 'Estrada da Pedreira, 2785-723 São Domingos de Rana',
       'Futebol', 'Campo de futebol', NULL,
       38.7398139, -9.3154218, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:grupo-desportivo-e-recreativo-de-talaide-campo-de-futebol-fe',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:grupo-desportivo-e-recreativo-de-talaide-campo-de-futebol-fe'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7398139)) <= 0.00063 AND ABS(v.longitude - (-9.3154218)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo e Recreativo de Talaíde - Campo de Futebol Fernando Sabido'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7398139)) <= 0.0030 AND ABS(v.longitude - (-9.3154218)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Musical e Desportivo 9 de Abril de Trajouce - Campo de Futebol', 'Cascais', 'Lisboa', 'Rua Sabino Luis Pedroso, 2785-673 São Domingos de Rana',
       'Futebol', 'Campo de futebol', NULL,
       38.7381738, -9.3435072, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:grupo-musical-e-desportivo-9-de-abril-de-trajouce-campo-de-f',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:grupo-musical-e-desportivo-9-de-abril-de-trajouce-campo-de-f'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7381738)) <= 0.00063 AND ABS(v.longitude - (-9.3435072)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Musical e Desportivo 9 de Abril de Trajouce - Campo de Futebol'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7381738)) <= 0.0030 AND ABS(v.longitude - (-9.3435072)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Surf de São Pedro do Estoril', 'Cascais', 'Lisboa', 'Avenida Marginal - Praia de S. Pedro',
       'Surf', 'Complexo desportivo', NULL,
       38.6934944, -9.3685101, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:centro-de-surf-de-sao-pedro-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:centro-de-surf-de-sao-pedro-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6934944)) <= 0.00063 AND ABS(v.longitude - (-9.3685101)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Surf de São Pedro do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6934944)) <= 0.0030 AND ABS(v.longitude - (-9.3685101)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Petanca de S. Pedro do Estoril', 'Cascais', 'Lisboa', 'Rua do Parque de Petanca, 2765 541 S. Pedro do Estoril',
       'Petanca', 'Complexo desportivo', NULL,
       38.7006189, -9.3725737, NULL, '214522393', 'OFFICIAL', 'CMC:DESPORTO:clube-de-petanca-de-s-pedro-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:clube-de-petanca-de-s-pedro-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7006189)) <= 0.00063 AND ABS(v.longitude - (-9.3725737)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Petanca de S. Pedro do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7006189)) <= 0.0030 AND ABS(v.longitude - (-9.3725737)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval de Cascais', 'Cascais', 'Lisboa', 'Esplanada Príncipe D. Luís Filipe, 2750-411 Cascais',
       'Vela', 'Complexo desportivo', NULL,
       38.6941721, -9.4184595, NULL, '214830125', 'OFFICIAL', 'CMC:DESPORTO:clube-naval-de-cascais',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:clube-naval-de-cascais'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6941721)) <= 0.00063 AND ABS(v.longitude - (-9.4184595)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval de Cascais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6941721)) <= 0.0030 AND ABS(v.longitude - (-9.4184595)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo de Instrução Musical e Desportivo da Abóboda - Campo de Futebol', 'Cascais', 'Lisboa', 'Estrada de Polima 1183 ABCD, 2785-543 S. Domingos de Rana',
       'Futebol', 'Campo de futebol', NULL,
       38.7260234, -9.3292774, NULL, '214440319', 'OFFICIAL', 'CMC:DESPORTO:grupo-de-instrucao-musical-e-desportivo-da-aboboda-campo-de-',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:grupo-de-instrucao-musical-e-desportivo-da-aboboda-campo-de-'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7260234)) <= 0.00063 AND ABS(v.longitude - (-9.3292774)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo de Instrução Musical e Desportivo da Abóboda - Campo de Futebol'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7260234)) <= 0.0030 AND ABS(v.longitude - (-9.3292774)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Recreativo e Dramático 1º de Maio de Tires - Pavilhão Desportivo Serafim Tomé dos Santos', 'Cascais', 'Lisboa', 'Rua do Mercado, 156 156, 2785-618 S. Domingos de Rana',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7138689, -9.3479908, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:grupo-recreativo-e-dramatico-1o-de-maio-de-tires-pavilhao-de',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:grupo-recreativo-e-dramatico-1o-de-maio-de-tires-pavilhao-de'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7138689)) <= 0.00063 AND ABS(v.longitude - (-9.3479908)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Recreativo e Dramático 1º de Maio de Tires - Pavilhão Desportivo Serafim Tomé dos Santos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7138689)) <= 0.0030 AND ABS(v.longitude - (-9.3479908)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Desportivo Abel Viegas Lopes (Grupo Desportivo e Recreativo das Fontainhas, Cascais)', 'Cascais', 'Lisboa', 'Geraldo Sem Pavor 9, 2755-049 Cascais',
       'Futebol', 'Campo de futebol', NULL,
       38.7130906, -9.4194847, NULL, '214835100', 'OFFICIAL', 'CMC:DESPORTO:parque-desportivo-abel-viegas-lopes-grupo-desportivo-e-recre',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:parque-desportivo-abel-viegas-lopes-grupo-desportivo-e-recre'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7130906)) <= 0.00063 AND ABS(v.longitude - (-9.4194847)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Desportivo Abel Viegas Lopes (Grupo Desportivo e Recreativo das Fontainhas, Cascais)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7130906)) <= 0.0030 AND ABS(v.longitude - (-9.4194847)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Murtalense - Campo de jogos', 'Cascais', 'Lisboa', 'Varela Silva, 2775-102',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7033184, -9.3633022, NULL, '214531639', 'OFFICIAL', 'CMC:DESPORTO:murtalense-campo-de-jogos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:murtalense-campo-de-jogos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7033184)) <= 0.00063 AND ABS(v.longitude - (-9.3633022)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Murtalense - Campo de jogos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7033184)) <= 0.0030 AND ABS(v.longitude - (-9.3633022)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo da Abóboda Piscinas Municipais', 'Cascais', 'Lisboa', 'Rua Mouzinho de Albuquerque 186, 2785-032',
       'Natação', 'Piscina', NULL,
       38.7263298, -9.3300450, NULL, '21 448 97 20', 'OFFICIAL', 'CMC:DESPORTO:complexo-desportivo-da-aboboda-piscinas-municipais',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:complexo-desportivo-da-aboboda-piscinas-municipais'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7263298)) <= 0.00063 AND ABS(v.longitude - (-9.3300450)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo da Abóboda Piscinas Municipais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7263298)) <= 0.0030 AND ABS(v.longitude - (-9.3300450)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Street Basket Bairro das Faceiras', 'Cascais', 'Lisboa', 'Rua Planalto, 2785-613 São Domingos de Rana',
       'Basquetebol', 'Complexo desportivo', NULL,
       38.7141532, -9.3413462, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-street-basket-bairro-das-faceiras',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-street-basket-bairro-das-faceiras'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7141532)) <= 0.00063 AND ABS(v.longitude - (-9.3413462)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Street Basket Bairro das Faceiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7141532)) <= 0.0030 AND ABS(v.longitude - (-9.3413462)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Street Basket Bairro da Assunção', 'Cascais', 'Lisboa', 'Rua António da Silva, 2750-101 Cascais',
       'Basquetebol', 'Complexo desportivo', NULL,
       38.7042550, -9.4317510, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-street-basket-bairro-da-assuncao',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-street-basket-bairro-da-assuncao'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7042550)) <= 0.00063 AND ABS(v.longitude - (-9.4317510)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Street Basket Bairro da Assunção'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7042550)) <= 0.0030 AND ABS(v.longitude - (-9.4317510)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Street Basket Alto da Peça', 'Cascais', 'Lisboa', 'Rua Dr. João António Gonçalves Amaral, 2645-617 Alcabideche',
       'Basquetebol', 'Complexo desportivo', NULL,
       38.7362322, -9.4025990, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-street-basket-alto-da-peca',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-street-basket-alto-da-peca'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7362322)) <= 0.00063 AND ABS(v.longitude - (-9.4025990)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Street Basket Alto da Peça'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7362322)) <= 0.0030 AND ABS(v.longitude - (-9.4025990)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Park da Torre', 'Cascais', 'Lisboa', 'Rua Pesqueiros, 2750-204 Cascais',
       'Skate', 'Complexo desportivo', NULL,
       38.7019057, -9.4415423, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-da-torre',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-da-torre'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7019057)) <= 0.00063 AND ABS(v.longitude - (-9.4415423)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Park da Torre'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7019057)) <= 0.0030 AND ABS(v.longitude - (-9.4415423)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Park da Torre Aguilha', 'Cascais', 'Lisboa', 'Estrada Mata da Torre, 2785-599 São Domingos de Rana',
       'Skate', 'Complexo desportivo', NULL,
       38.7105234, -9.3317286, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-da-torre-aguilha',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-da-torre-aguilha'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7105234)) <= 0.00063 AND ABS(v.longitude - (-9.3317286)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Park da Torre Aguilha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7105234)) <= 0.0030 AND ABS(v.longitude - (-9.3317286)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol do Grupo Desportivo Malveira da Serra', 'Cascais', 'Lisboa', 'Estrada Nacional 247',
       'Futebol', 'Campo de futebol', NULL,
       38.7433523, -9.4547893, NULL, '214871160', 'OFFICIAL', 'CMC:DESPORTO:campo-de-futebol-do-grupo-desportivo-malveira-da-serra',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-futebol-do-grupo-desportivo-malveira-da-serra'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7433523)) <= 0.00063 AND ABS(v.longitude - (-9.4547893)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol do Grupo Desportivo Malveira da Serra'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7433523)) <= 0.0030 AND ABS(v.longitude - (-9.4547893)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro Alcaide', 'Cascais', 'Lisboa', 'Rua de Faro, 2755-281 Alcabideche',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7121778, -9.4154404, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-bairro-alcaide',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-bairro-alcaide'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7121778)) <= 0.00063 AND ABS(v.longitude - (-9.4154404)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro Alcaide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7121778)) <= 0.0030 AND ABS(v.longitude - (-9.4154404)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Polima', 'Cascais', 'Lisboa', 'Rua Raul Solnado, 2785-350 São Domingos de Rana',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7263558, -9.3205872, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-polima',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-polima'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7263558)) <= 0.00063 AND ABS(v.longitude - (-9.3205872)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Polima'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7263558)) <= 0.0030 AND ABS(v.longitude - (-9.3205872)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro da Adroana', 'Cascais', 'Lisboa', 'Rua Dom Bosco, 2645-622 Alcabideche',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7466680, -9.3780180, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-bairro-da-adroana',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-bairro-da-adroana'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7466680)) <= 0.00063 AND ABS(v.longitude - (-9.3780180)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro da Adroana'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7466680)) <= 0.0030 AND ABS(v.longitude - (-9.3780180)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro do Zambujal', 'Cascais', 'Lisboa', 'Rua dos Pirilampos, 2785-805 São Domingos de Rana',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7032752, -9.3447736, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-bairro-do-zambujal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-bairro-do-zambujal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7032752)) <= 0.00063 AND ABS(v.longitude - (-9.3447736)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro do Zambujal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7032752)) <= 0.0030 AND ABS(v.longitude - (-9.3447736)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Galiza', 'Cascais', 'Lisboa', 'Rua Martin Luther King, 2765-343 Estoril',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7079130, -9.3876336, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-galiza',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-galiza'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7079130)) <= 0.00063 AND ABS(v.longitude - (-9.3876336)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Galiza'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7079130)) <= 0.0030 AND ABS(v.longitude - (-9.3876336)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Manique', 'Cascais', 'Lisboa', 'Rua dos Bem Lembrados, 2645-471 Alcabideche',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7392564, -9.3746996, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-manique',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-manique'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7392564)) <= 0.00063 AND ABS(v.longitude - (-9.3746996)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Manique'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7392564)) <= 0.0030 AND ABS(v.longitude - (-9.3746996)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro Irene', 'Cascais', 'Lisboa', 'Rua Pedro Álvaro de Sousa, 2755-048 Alcabideche',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7173060, -9.4219886, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-bairro-irene',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-bairro-irene'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7173060)) <= 0.00063 AND ABS(v.longitude - (-9.4219886)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro Irene'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7173060)) <= 0.0030 AND ABS(v.longitude - (-9.4219886)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Quinta das Romanzeiras', 'Cascais', 'Lisboa', 'Rua das Naus, 2750-762 Cascais',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7032848, -9.4396230, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-quinta-das-romanzeiras',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-quinta-das-romanzeiras'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7032848)) <= 0.00063 AND ABS(v.longitude - (-9.4396230)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Quinta das Romanzeiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7032848)) <= 0.0030 AND ABS(v.longitude - (-9.4396230)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Street Basket da Quinta da Bicuda', 'Cascais', 'Lisboa', 'Rua Chapim, 2750-696 Cascais',
       'Basquetebol', 'Complexo desportivo', NULL,
       38.7066996, -9.4449032, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-street-basket-da-quinta-da-bicuda',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-street-basket-da-quinta-da-bicuda'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7066996)) <= 0.00063 AND ABS(v.longitude - (-9.4449032)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Street Basket da Quinta da Bicuda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7066996)) <= 0.0030 AND ABS(v.longitude - (-9.4449032)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo São Pedro do Estoril', 'Cascais', 'Lisboa', 'Praceta Branca Gonta Collaço, 2765-541 Estoril',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6989756, -9.3719836, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-sao-pedro-do-estoril',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-sao-pedro-do-estoril'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6989756)) <= 0.00063 AND ABS(v.longitude - (-9.3719836)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo São Pedro do Estoril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6989756)) <= 0.0030 AND ABS(v.longitude - (-9.3719836)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Areia Quinta da Carreira', 'Cascais', 'Lisboa', 'Estrada Alapraia, 2765-361 Estoril',
       'Voleibol de Praia', 'Complexo desportivo', NULL,
       38.7030736, -9.3826306, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campos-de-areia-quinta-da-carreira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campos-de-areia-quinta-da-carreira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7030736)) <= 0.00063 AND ABS(v.longitude - (-9.3826306)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Areia Quinta da Carreira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7030736)) <= 0.0030 AND ABS(v.longitude - (-9.3826306)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Blooming Padel Academy', 'Cascais', 'Lisboa', 'Rua Fonte da Carreira, 2645-467 Alcabideche',
       'Padel', 'Clube de padel', NULL,
       38.7403238, -9.3767084, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:blooming-padel-academy',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:blooming-padel-academy'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7403238)) <= 0.00063 AND ABS(v.longitude - (-9.3767084)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Blooming Padel Academy'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7403238)) <= 0.0030 AND ABS(v.longitude - (-9.3767084)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Voleibol Praia da Ribeira', 'Cascais', 'Lisboa', 'Passeio D. Luis I, 2750-642 Cascais',
       'Voleibol de Praia', 'Complexo desportivo', NULL,
       38.6968402, -9.4199794, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-voleibol-praia-da-ribeira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-voleibol-praia-da-ribeira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6968402)) <= 0.00063 AND ABS(v.longitude - (-9.4199794)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Voleibol Praia da Ribeira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6968402)) <= 0.0030 AND ABS(v.longitude - (-9.4199794)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Voleibol Praia de Carcavelos', 'Cascais', 'Lisboa', 'Av. Marginal, 2775-604 Carcavelos',
       'Voleibol de Praia', 'Complexo desportivo', NULL,
       38.6790866, -9.3331012, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campos-de-voleibol-praia-de-carcavelos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campos-de-voleibol-praia-de-carcavelos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6790866)) <= 0.00063 AND ABS(v.longitude - (-9.3331012)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Voleibol Praia de Carcavelos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6790866)) <= 0.0030 AND ABS(v.longitude - (-9.3331012)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Futebol Praia Carcavelos', 'Cascais', 'Lisboa', 'Avenida Marginal, 2775-604 Carcavelos',
       'Futebol', 'Campo de futebol', NULL,
       38.6788738, -9.3325542, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-futebol-praia-carcavelos',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-futebol-praia-carcavelos'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6788738)) <= 0.00063 AND ABS(v.longitude - (-9.3325542)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Futebol Praia Carcavelos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6788738)) <= 0.0030 AND ABS(v.longitude - (-9.3325542)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Beach Tennis', 'Cascais', 'Lisboa', 'Av. Marginal, 2775-604 Carcavelos',
       'Ténis de praia', 'Complexo desportivo', NULL,
       38.6783586, -9.3314932, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campos-de-beach-tennis',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campos-de-beach-tennis'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6783586)) <= 0.00063 AND ABS(v.longitude - (-9.3314932)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Beach Tennis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6783586)) <= 0.0030 AND ABS(v.longitude - (-9.3314932)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Hípico Cascais', 'Cascais', 'Lisboa', 'Rua da Areia, 2750-642 Cascais',
       'Hipismo', 'Complexo desportivo', NULL,
       38.7212016, -9.4616514, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:centro-hipico-cascais',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:centro-hipico-cascais'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7212016)) <= 0.00063 AND ABS(v.longitude - (-9.4616514)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Hípico Cascais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7212016)) <= 0.0030 AND ABS(v.longitude - (-9.4616514)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Quinta da Carreira', 'Cascais', 'Lisboa', 'R. Fernando Pessoa, 2765-069 Estoril',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7015260, -9.3813866, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-quinta-da-carreira',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-quinta-da-carreira'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7015260)) <= 0.00063 AND ABS(v.longitude - (-9.3813866)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Quinta da Carreira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7015260)) <= 0.0030 AND ABS(v.longitude - (-9.3813866)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Penedo', 'Cascais', 'Lisboa', 'Rua Feliciano Castilho, 2785-365 São Domingos de Rana',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7062058, -9.3614728, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-penedo',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-penedo'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7062058)) <= 0.00063 AND ABS(v.longitude - (-9.3614728)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Penedo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7062058)) <= 0.0030 AND ABS(v.longitude - (-9.3614728)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mountain Bike Outeiro da Vela', 'Cascais', 'Lisboa', 'Rua do Miradouro, 1688 - 083 Cascais',
       'Ciclismo', 'Complexo desportivo', NULL,
       38.7066039, -9.4204843, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:mountain-bike-outeiro-da-vela',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:mountain-bike-outeiro-da-vela'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7066039)) <= 0.00063 AND ABS(v.longitude - (-9.4204843)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mountain Bike Outeiro da Vela'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7066039)) <= 0.0030 AND ABS(v.longitude - (-9.4204843)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'ADN Atividades Desportivas Náuticas', 'Cascais', 'Lisboa', 'Passeio Rainha Dona Maria Pia, 2755-010 Alcabideche',
       'Vela', 'Complexo desportivo', NULL,
       38.6952545, -9.4190215, NULL, '916732737', 'OFFICIAL', 'CMC:DESPORTO:adn-atividades-desportivas-nauticas',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:adn-atividades-desportivas-nauticas'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6952545)) <= 0.00063 AND ABS(v.longitude - (-9.4190215)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('ADN Atividades Desportivas Náuticas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6952545)) <= 0.0030 AND ABS(v.longitude - (-9.4190215)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Padel da Parede', 'Cascais', 'Lisboa', 'Avenida Melvin Jones',
       'Padel', 'Clube de padel', NULL,
       38.6948594, -9.3513168, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:campo-de-padel-da-parede',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:campo-de-padel-da-parede'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6948594)) <= 0.00063 AND ABS(v.longitude - (-9.3513168)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Padel da Parede'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6948594)) <= 0.0030 AND ABS(v.longitude - (-9.3513168)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate park Murtal', 'Cascais', 'Lisboa', 'Rua Varela Silva',
       'Skate', 'Complexo desportivo', NULL,
       38.7032956, -9.3630368, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-murtal',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-murtal'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7032956)) <= 0.00063 AND ABS(v.longitude - (-9.3630368)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate park Murtal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7032956)) <= 0.0030 AND ABS(v.longitude - (-9.3630368)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate park Penhas do Marmeleiro', 'Cascais', 'Lisboa', 'Rua Marquês de Pombal',
       'Skate', 'Complexo desportivo', NULL,
       38.7306472, -9.4329796, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-penhas-do-marmeleiro',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-penhas-do-marmeleiro'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7306472)) <= 0.00063 AND ABS(v.longitude - (-9.4329796)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate park Penhas do Marmeleiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7306472)) <= 0.0030 AND ABS(v.longitude - (-9.4329796)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate park aldeia de Juso', 'Cascais', 'Lisboa', 'Praceta Michel Giacometti',
       'Skate', 'Complexo desportivo', NULL,
       38.7278366, -9.4491170, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-aldeia-de-juso',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-aldeia-de-juso'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7278366)) <= 0.00063 AND ABS(v.longitude - (-9.4491170)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate park aldeia de Juso'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7278366)) <= 0.0030 AND ABS(v.longitude - (-9.4491170)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parede Foot-Ball Clube', 'Cascais', 'Lisboa', 'Avenida Comandante Gilberto Duarte e Duarte, 2775-200 Parede',
       'Futebol', 'Campo de futebol', NULL,
       38.6951495, -9.3516398, NULL, '214532068', 'OFFICIAL', 'CMC:DESPORTO:parede-foot-ball-clube',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:parede-foot-ball-clube'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6951495)) <= 0.00063 AND ABS(v.longitude - (-9.3516398)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parede Foot-Ball Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6951495)) <= 0.0030 AND ABS(v.longitude - (-9.3516398)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Carrascalense', 'Cascais', 'Lisboa', 'Rua de Santa Maria',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7250332, -9.4220664, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-carrascalense',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-carrascalense'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7250332)) <= 0.00063 AND ABS(v.longitude - (-9.4220664)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Carrascalense'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7250332)) <= 0.0030 AND ABS(v.longitude - (-9.4220664)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Martinha', 'Cascais', 'Lisboa', 'Auto-Estrada A5',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7184096, -9.3900116, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-martinha',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-martinha'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7184096)) <= 0.00063 AND ABS(v.longitude - (-9.3900116)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Martinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7184096)) <= 0.0030 AND ABS(v.longitude - (-9.3900116)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Largo Alice Cruz', 'Cascais', 'Lisboa', 'Largo Alice Cruz',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7178118, -9.3292812, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-largo-alice-cruz',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-largo-alice-cruz'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7178118)) <= 0.00063 AND ABS(v.longitude - (-9.3292812)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Largo Alice Cruz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7178118)) <= 0.0030 AND ABS(v.longitude - (-9.3292812)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Cabeço de Mouro', 'Cascais', 'Lisboa', 'Rua Jorge de Sena, 2785-008',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7175518, -9.3319012, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:polidesportivo-cabeco-de-mouro',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:polidesportivo-cabeco-de-mouro'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7175518)) <= 0.00063 AND ABS(v.longitude - (-9.3319012)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Cabeço de Mouro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7175518)) <= 0.0030 AND ABS(v.longitude - (-9.3319012)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate park cruz vermelha norte', 'Cascais', 'Lisboa', 'Rua das Fisgas, 2645-265',
       'Skate', 'Complexo desportivo', NULL,
       38.7474854, -9.3903902, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-park-cruz-vermelha-norte',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-park-cruz-vermelha-norte'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7474854)) <= 0.00063 AND ABS(v.longitude - (-9.3903902)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate park cruz vermelha norte'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7474854)) <= 0.0030 AND ABS(v.longitude - (-9.3903902)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Skate Parque das Gerações', 'Cascais', 'Lisboa', 'Rua António Ferro, 2765-218',
       'Skate', 'Complexo desportivo', NULL,
       38.6984826, -9.3810500, NULL, NULL, 'OFFICIAL', 'CMC:DESPORTO:skate-parque-das-geracoes',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CMC:DESPORTO:skate-parque-das-geracoes'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6984826)) <= 0.00063 AND ABS(v.longitude - (-9.3810500)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Skate Parque das Gerações'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6984826)) <= 0.0030 AND ABS(v.longitude - (-9.3810500)) <= 0.0040)))
);
