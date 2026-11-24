-- Lisbon municipal sports facilities (Câmara Municipal de Lisboa, Geodados "Instalações Desportivas", CC0).
-- Public schools and fitness rooms are left out. One venue per facility; skipped when any venue already sits within ~70 m.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia de Golfe de Lisboa', 'Lisboa', 'Lisboa', 'Azinhaga Galhardas s/n 1600-213',
       'Golfe', 'Complexo desportivo', NULL,
       38.7561516, -9.1654255, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1921',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1921'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7561516)) <= 0.00063 AND ABS(v.longitude - (-9.1654255)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia de Golfe de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7561516)) <= 0.0030 AND ABS(v.longitude - (-9.1654255)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia Kolmachine Fight4Fitness', 'Lisboa', 'Lisboa', 'Avenida João Crisóstomo, 51  A 1050-126',
       'Boxe', 'Complexo desportivo', NULL,
       38.7357079, -9.1498193, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1924',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1924'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7357079)) <= 0.00063 AND ABS(v.longitude - (-9.1498193)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia Kolmachine Fight4Fitness'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7357079)) <= 0.0030 AND ABS(v.longitude - (-9.1498193)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia Life Club', 'Lisboa', 'Lisboa', 'Avenida de Brasília, Armazém J 1200-109',
       'Natação', 'Piscina', NULL,
       38.7059043, -9.1531377, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1925',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1925'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7059043)) <= 0.00063 AND ABS(v.longitude - (-9.1531377)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia Life Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7059043)) <= 0.0030 AND ABS(v.longitude - (-9.1531377)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia Militar', 'Lisboa', 'Lisboa', 'Rua Gomes Freire 1169-203',
       'Futebol;Atletismo;Futsal;Basquetebol;Natação', 'Complexo desportivo', NULL,
       38.7260606, -9.1390970, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1933',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1933'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7260606)) <= 0.00063 AND ABS(v.longitude - (-9.1390970)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia Militar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7260606)) <= 0.0030 AND ABS(v.longitude - (-9.1390970)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'ACAL - Arco Clube Ar Livre', 'Lisboa', 'Lisboa', 'Parque Florestal de Monsanto, Estrada do Barcal, Monte das Perdizes 1500-000',
       'Tiro com Arco', 'Complexo desportivo', NULL,
       38.7377367, -9.1884161, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1946',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1946'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7377367)) <= 0.00063 AND ABS(v.longitude - (-9.1884161)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('ACAL - Arco Clube Ar Livre'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7377367)) <= 0.0030 AND ABS(v.longitude - (-9.1884161)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Académica da Faculdade de Direito de Lisboa - Universidade de Lisboa', 'Lisboa', 'Lisboa', 'Alameda da Universidade 1649-014',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7516080, -9.1578031, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1952',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1952'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7516080)) <= 0.00063 AND ABS(v.longitude - (-9.1578031)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Académica da Faculdade de Direito de Lisboa - Universidade de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7516080)) <= 0.0030 AND ABS(v.longitude - (-9.1578031)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Cristã da Mocidade de Lisboa', 'Lisboa', 'Lisboa', 'Rua de São Bento, 329 - 2º 1250-220',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7181183, -9.1556689, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1953',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1953'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7181183)) <= 0.00063 AND ABS(v.longitude - (-9.1556689)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Cristã da Mocidade de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7181183)) <= 0.0030 AND ABS(v.longitude - (-9.1556689)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação de Estudantes da Faculdade de Farmácia - Universidade de Lisboa', 'Lisboa', 'Lisboa', 'Avenida Professor Gama Pinto 1649-003',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7476584, -9.1552546, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1957',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1957'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7476584)) <= 0.00063 AND ABS(v.longitude - (-9.1552546)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação de Estudantes da Faculdade de Farmácia - Universidade de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7476584)) <= 0.0030 AND ABS(v.longitude - (-9.1552546)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação de Estudantes do Instituto Superior de Agronomia - Universidade de Lisboa', 'Lisboa', 'Lisboa', 'Calçada da Tapada 1349-017',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7081142, -9.1832614, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1958',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1958'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7081142)) <= 0.00063 AND ABS(v.longitude - (-9.1832614)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação de Estudantes do Instituto Superior de Agronomia - Universidade de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7081142)) <= 0.0030 AND ABS(v.longitude - (-9.1832614)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação de Estudantes do Instituto Superior de Engenharia de Lisboa - Instituto Politécnico de Lisboa', 'Lisboa', 'Lisboa', 'Rua Conselheiro Emidio Navarro, 1 1959-007',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7560998, -9.1147995, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1960',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1960'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7560998)) <= 0.00063 AND ABS(v.longitude - (-9.1147995)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação de Estudantes do Instituto Superior de Engenharia de Lisboa - Instituto Politécnico de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7560998)) <= 0.0030 AND ABS(v.longitude - (-9.1147995)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação de Estudantes do Instituto Superior Técnico - Universidade de Lisboa', 'Lisboa', 'Lisboa', 'Avenida Rovisco Pais, 1 1049-001',
       'Futsal;Basquetebol;Voleibol;Andebol;Ténis;Squash', 'Complexo desportivo', NULL,
       38.7363578, -9.1373736, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1962',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1962'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7363578)) <= 0.00063 AND ABS(v.longitude - (-9.1373736)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação de Estudantes do Instituto Superior Técnico - Universidade de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7363578)) <= 0.0030 AND ABS(v.longitude - (-9.1373736)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Desportiva e Cultural da Encarnação e Olivais', 'Lisboa', 'Lisboa', 'Rua Quinta Santa Maria 1800-325',
       'Futebol;Futsal;Basquetebol;Judo', 'Complexo desportivo', NULL,
       38.7780312, -9.1209910, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1972',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1972'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7780312)) <= 0.00063 AND ABS(v.longitude - (-9.1209910)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Desportiva e Cultural da Encarnação e Olivais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7780312)) <= 0.0030 AND ABS(v.longitude - (-9.1209910)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação dos Antigos Alunos do Instituto Superior de Agronomia', 'Lisboa', 'Lisboa', 'Calçada da Tapada 1349-017',
       'Futebol;Rugby', 'Complexo desportivo', NULL,
       38.7160564, -9.1906919, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1981',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1981'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7160564)) <= 0.00063 AND ABS(v.longitude - (-9.1906919)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação dos Antigos Alunos do Instituto Superior de Agronomia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7160564)) <= 0.0030 AND ABS(v.longitude - (-9.1906919)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Naval de Lisboa', 'Lisboa', 'Lisboa', 'Doca de Belém 1401-038',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6944971, -9.2026965, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1991',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1991'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6944971)) <= 0.00063 AND ABS(v.longitude - (-9.2026965)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Naval de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6944971)) <= 0.0030 AND ABS(v.longitude - (-9.2026965)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Associação Naval de Lisboa - Doca de Santo Amaro', 'Lisboa', 'Lisboa', 'Doca de Santo Amaro 1350-353',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6993123, -9.1759500, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1992',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1992'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6993123)) <= 0.00063 AND ABS(v.longitude - (-9.1759500)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Associação Naval de Lisboa - Doca de Santo Amaro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6993123)) <= 0.0030 AND ABS(v.longitude - (-9.1759500)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ateneu da Madre de Deus', 'Lisboa', 'Lisboa', 'Rua Nicolau Tolentino, 2 1900-356',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7318151, -9.1105275, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1993',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1993'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7318151)) <= 0.00063 AND ABS(v.longitude - (-9.1105275)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ateneu da Madre de Deus'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7318151)) <= 0.0030 AND ABS(v.longitude - (-9.1105275)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Atlético Clube de Portugal', 'Lisboa', 'Lisboa', 'Estádio da Tapadinha 1300-604',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7099885, -9.1815105, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:1994',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:1994'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7099885)) <= 0.00063 AND ABS(v.longitude - (-9.1815105)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Atlético Clube de Portugal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7099885)) <= 0.0030 AND ABS(v.longitude - (-9.1815105)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bairro Horizonte', 'Lisboa', 'Lisboa', 'Rua Engenheiro Santos Simões 1900-900',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7348965, -9.1256331, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2010',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2010'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7348965)) <= 0.00063 AND ABS(v.longitude - (-9.1256331)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Bairro Horizonte'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7348965)) <= 0.0030 AND ABS(v.longitude - (-9.1256331)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Belém Rugby Park', 'Lisboa', 'Lisboa', 'Avenida dos Bombeiros - Belenenses Rugby Park 1400-036',
       'Rugby', 'Complexo desportivo', NULL,
       38.7149303, -9.2071527, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2014',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2014'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7149303)) <= 0.00063 AND ABS(v.longitude - (-9.2071527)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Belém Rugby Park'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7149303)) <= 0.0030 AND ABS(v.longitude - (-9.2071527)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Boa - Hora Futebol Clube', 'Lisboa', 'Lisboa', 'Rua Aliança Operária, 41 C 1300-044',
       'Futsal;Basquetebol;Voleibol;Andebol;Ténis;Boxe', 'Complexo desportivo', NULL,
       38.7033258, -9.1897394, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2015',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2015'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7033258)) <= 0.00063 AND ABS(v.longitude - (-9.1897394)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Boa - Hora Futebol Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7033258)) <= 0.0030 AND ABS(v.longitude - (-9.1897394)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo Chicago', 'Lisboa', 'Lisboa', 'Rua Miguel Nogueira Júnior 1950-202',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7592117, -9.1221219, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2026',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2026'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7592117)) <= 0.00063 AND ABS(v.longitude - (-9.1221219)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo Chicago'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7592117)) <= 0.0030 AND ABS(v.longitude - (-9.1221219)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo das Amoreiras', 'Lisboa', 'Lisboa', 'Campo das Amoreiras 1750-200',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7866834, -9.1427776, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2028',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2028'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7866834)) <= 0.00063 AND ABS(v.longitude - (-9.1427776)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo das Amoreiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7866834)) <= 0.0030 AND ABS(v.longitude - (-9.1427776)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Basket (traseiras da Rua Luís Sá)', 'Lisboa', 'Lisboa', 'Rua Luís de Sá 1750-374',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7787132, -9.1483420, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2029',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2029'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7787132)) <= 0.00063 AND ABS(v.longitude - (-9.1483420)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Basket (traseiras da Rua Luís Sá)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7787132)) <= 0.0030 AND ABS(v.longitude - (-9.1483420)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Basquetebol do Largo Conde Ottolini', 'Lisboa', 'Lisboa', 'Largo Conde Ottolini 1500-201',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7429775, -9.1775424, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2030',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2030'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7429775)) <= 0.00063 AND ABS(v.longitude - (-9.1775424)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Basquetebol do Largo Conde Ottolini'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7429775)) <= 0.0030 AND ABS(v.longitude - (-9.1775424)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Praça Cidade de São Salvador', 'Lisboa', 'Lisboa', 'Praça Cidade de São Salvador 1800-126',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7647390, -9.1154734, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2031',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2031'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7647390)) <= 0.00063 AND ABS(v.longitude - (-9.1154734)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Praça Cidade de São Salvador'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7647390)) <= 0.0030 AND ABS(v.longitude - (-9.1154734)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Quinta das Laranjeiras', 'Lisboa', 'Lisboa', 'Rua Câmara Reis - Rua Manuel Mendes 1800-046',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7713304, -9.1001670, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2032',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2032'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7713304)) <= 0.00063 AND ABS(v.longitude - (-9.1001670)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Quinta das Laranjeiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7713304)) <= 0.0030 AND ABS(v.longitude - (-9.1001670)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Quinta do Lavrado', 'Lisboa', 'Lisboa', 'Avenida Marechal Francisco da Costa Gomes - Quinta do Lavrado 1900-418',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7324550, -9.1227281, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2033',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2033'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7324550)) <= 0.00063 AND ABS(v.longitude - (-9.1227281)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Quinta do Lavrado'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7324550)) <= 0.0030 AND ABS(v.longitude - (-9.1227281)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos da Verbena', 'Lisboa', 'Lisboa', 'Travessa de São Bartolomeu 1100-509',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7116951, -9.1322017, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2034',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2034'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7116951)) <= 0.00063 AND ABS(v.longitude - (-9.1322017)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos da Verbena'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7116951)) <= 0.0030 AND ABS(v.longitude - (-9.1322017)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Football Cage', 'Lisboa', 'Lisboa', 'Rua Professor Cid dos Santos 1300-477',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7127190, -9.1995683, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2037',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2037'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7127190)) <= 0.00063 AND ABS(v.longitude - (-9.1995683)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Football Cage'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7127190)) <= 0.0030 AND ABS(v.longitude - (-9.1995683)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do Bairro Alfredo Bensaúde', 'Lisboa', 'Lisboa', 'Rua Costa Malheiro - Bairro Avenida Alfredo Bensaúde 1800-412',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7808984, -9.1171614, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2038',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2038'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7808984)) <= 0.00063 AND ABS(v.longitude - (-9.1171614)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do Bairro Alfredo Bensaúde'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7808984)) <= 0.0030 AND ABS(v.longitude - (-9.1171614)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do Calhariz', 'Lisboa', 'Lisboa', 'Estrada do Calhariz de Benfica (perto do n.º 121) 1500-121',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7450085, -9.1923341, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2039',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2039'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7450085)) <= 0.00063 AND ABS(v.longitude - (-9.1923341)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do Calhariz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7450085)) <= 0.0030 AND ABS(v.longitude - (-9.1923341)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do Casal dos Machados', 'Lisboa', 'Lisboa', 'Rua Alfredo Portela Santos, Casal dos Machados 1800-910',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7725123, -9.1032573, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2040',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2040'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7725123)) <= 0.00063 AND ABS(v.longitude - (-9.1032573)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do Casal dos Machados'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7725123)) <= 0.0030 AND ABS(v.longitude - (-9.1032573)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos do Jardim dos Eucaliptos', 'Lisboa', 'Lisboa', 'Estrada dos Arneiros 1500-050',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7547377, -9.1995392, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2041',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2041'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7547377)) <= 0.00063 AND ABS(v.longitude - (-9.1995392)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos do Jardim dos Eucaliptos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7547377)) <= 0.0030 AND ABS(v.longitude - (-9.1995392)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Municipal do Campo Grande', 'Lisboa', 'Lisboa', 'Avenida dos Estados Unidos da América 1700-165',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7495097, -9.1443096, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2043',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2043'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7495097)) <= 0.00063 AND ABS(v.longitude - (-9.1443096)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Municipal do Campo Grande'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7495097)) <= 0.0030 AND ABS(v.longitude - (-9.1443096)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Mini Basquetebol do Largo Conde Bonfim', 'Lisboa', 'Lisboa', 'Largo Conde Bonfim 1500-200',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7458679, -9.1818496, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2044',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2044'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7458679)) <= 0.00063 AND ABS(v.longitude - (-9.1818496)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Mini Basquetebol do Largo Conde Bonfim'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7458679)) <= 0.0030 AND ABS(v.longitude - (-9.1818496)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Ténis Carlos Selvagem', 'Lisboa', 'Lisboa', 'Largo Carlos Selvagem 1500-506',
       'Ténis', 'Clube de ténis', NULL,
       38.7505569, -9.1873395, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2045',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2045'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7505569)) <= 0.00063 AND ABS(v.longitude - (-9.1873395)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Ténis Carlos Selvagem'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7505569)) <= 0.0030 AND ABS(v.longitude - (-9.1873395)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campolide Atlético Clube', 'Lisboa', 'Lisboa', 'Rua Marquês da Fronteira, 163 - 1º 1070-294',
       'Boxe', 'Complexo desportivo', NULL,
       38.7274704, -9.1621469, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2046',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2046'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7274704)) <= 0.00063 AND ABS(v.longitude - (-9.1621469)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campolide Atlético Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7274704)) <= 0.0030 AND ABS(v.longitude - (-9.1621469)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Jogos da Rua Cândido de Oliveira', 'Lisboa', 'Lisboa', 'Rua Cândido de Oliveira 1800-047',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7662443, -9.1083690, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2049',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2049'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7662443)) <= 0.00063 AND ABS(v.longitude - (-9.1083690)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Jogos da Rua Cândido de Oliveira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7662443)) <= 0.0030 AND ABS(v.longitude - (-9.1083690)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Jogos da Rua Olavo d''Eça Leal', 'Lisboa', 'Lisboa', 'Rua Olavo d''Eça Leal 1600-306',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7533833, -9.1718011, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2051',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2051'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7533833)) <= 0.00063 AND ABS(v.longitude - (-9.1718011)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Jogos da Rua Olavo d''Eça Leal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7533833)) <= 0.0030 AND ABS(v.longitude - (-9.1718011)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Padel do Clube Futebol Benfica', 'Lisboa', 'Lisboa', 'Parque Silva Porto, Rua Doutor José Alberto de Faria 1500-242',
       'Padel', 'Clube de padel', NULL,
       38.7480591, -9.2045709, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2054',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2054'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7480591)) <= 0.00063 AND ABS(v.longitude - (-9.2045709)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Padel do Clube Futebol Benfica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7480591)) <= 0.0030 AND ABS(v.longitude - (-9.2045709)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Ténis das Laranjeiras', 'Lisboa', 'Lisboa', 'Praça Nuno Rodrigues dos Santos 16 1600-171',
       'Ténis', 'Clube de ténis', NULL,
       38.7444874, -9.1668841, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2056',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2056'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7444874)) <= 0.00063 AND ABS(v.longitude - (-9.1668841)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Ténis das Laranjeiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7444874)) <= 0.0030 AND ABS(v.longitude - (-9.1668841)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Carnide Clube', 'Lisboa', 'Lisboa', 'Rua Neves Costa, 69-71 1600-533',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7599278, -9.1902243, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2058',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2058'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7599278)) <= 0.00063 AND ABS(v.longitude - (-9.1902243)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Carnide Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7599278)) <= 0.0030 AND ABS(v.longitude - (-9.1902243)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Casa Pia Atlético Clube', 'Lisboa', 'Lisboa', 'Estádio Pina Manique 1500-462',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7376663, -9.2034747, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2065',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2065'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7376663)) <= 0.00063 AND ABS(v.longitude - (-9.2034747)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Casa Pia Atlético Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7376663)) <= 0.0030 AND ABS(v.longitude - (-9.2034747)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Cultural e Desportivo dos Trabalhadores do Metropolitano de Lisboa - Sete Rios', 'Lisboa', 'Lisboa', 'Praça Marechal Humberto Delgado - PMO1 Metropolitano 1500-423',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7407912, -9.1667861, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2077',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2077'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7407912)) <= 0.00063 AND ABS(v.longitude - (-9.1667861)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Cultural e Desportivo dos Trabalhadores do Metropolitano de Lisboa - Sete Rios'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7407912)) <= 0.0030 AND ABS(v.longitude - (-9.1667861)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Cultural e Recreativo das Crianças do Cruzeiro e Rio Seco', 'Lisboa', 'Lisboa', 'Rua José Magro 1300-332',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7073269, -9.1897768, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2079',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2079'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7073269)) <= 0.00063 AND ABS(v.longitude - (-9.1897768)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Cultural e Recreativo das Crianças do Cruzeiro e Rio Seco'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7073269)) <= 0.0030 AND ABS(v.longitude - (-9.1897768)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Cultural e Recreativo dos Coruchéus', 'Lisboa', 'Lisboa', 'Rua Florbela Espanca, 2 º  R/C ESQ 1700-195',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7504850, -9.1448521, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2083',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2083'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7504850)) <= 0.00063 AND ABS(v.longitude - (-9.1448521)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Cultural e Recreativo dos Coruchéus'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7504850)) <= 0.0030 AND ABS(v.longitude - (-9.1448521)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Atletismo das Galinheiras', 'Lisboa', 'Lisboa', 'Estrada da Póvoa, Lote CAG 1750-224',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7919547, -9.1500127, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2084',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2084'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7919547)) <= 0.00063 AND ABS(v.longitude - (-9.1500127)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Atletismo das Galinheiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7919547)) <= 0.0030 AND ABS(v.longitude - (-9.1500127)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Cultura e Desporto de Olivais Sul', 'Lisboa', 'Lisboa', 'Rua Cidade de Tete 1800-128',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7636178, -9.1277881, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2093',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2093'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7636178)) <= 0.00063 AND ABS(v.longitude - (-9.1277881)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Cultura e Desporto de Olivais Sul'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7636178)) <= 0.0030 AND ABS(v.longitude - (-9.1277881)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Cultura Popular de Santa Engrácia', 'Lisboa', 'Lisboa', 'Calçada dos Barbadinhos, 49 1170-040',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7188294, -9.1208912, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2095',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2095'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7188294)) <= 0.00063 AND ABS(v.longitude - (-9.1208912)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Cultura Popular de Santa Engrácia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7188294)) <= 0.0030 AND ABS(v.longitude - (-9.1208912)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Recreio Popular do Bairro da Calçada dos Mestres', 'Lisboa', 'Lisboa', 'Bairro da Calçada dos Mestres, Rua 7 1070-264',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7272788, -9.1692713, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2100',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2100'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7272788)) <= 0.00063 AND ABS(v.longitude - (-9.1692713)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Recreio Popular do Bairro da Calçada dos Mestres'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7272788)) <= 0.0030 AND ABS(v.longitude - (-9.1692713)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro de Ténis de Monsanto', 'Lisboa', 'Lisboa', 'Estrada do Clube de Ténis - Instalações Municipais de Ténis de Monsanto 1300-054',
       'Ténis', 'Clube de ténis', NULL,
       38.7173957, -9.1824437, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2103',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2103'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7173957)) <= 0.00063 AND ABS(v.longitude - (-9.1824437)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro de Ténis de Monsanto'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7173957)) <= 0.0030 AND ABS(v.longitude - (-9.1824437)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Helen Keller', 'Lisboa', 'Lisboa', 'Avenida Doutor Mário Moutinho, 20 1400-136',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7124022, -9.2115528, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2118',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2118'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7124022)) <= 0.00063 AND ABS(v.longitude - (-9.2115528)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Helen Keller'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7124022)) <= 0.0030 AND ABS(v.longitude - (-9.2115528)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Português de Actividades Subaquáticas', 'Lisboa', 'Lisboa', 'Rua Alto do Duque, 45 1400-009',
       'Natação', 'Piscina', NULL,
       38.7012685, -9.2223775, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2122',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2122'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7012685)) <= 0.00063 AND ABS(v.longitude - (-9.2223775)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Português de Actividades Subaquáticas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7012685)) <= 0.0030 AND ABS(v.longitude - (-9.2223775)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Social da Musgueira', 'Lisboa', 'Lisboa', 'Rua Maria Margarida,  6 e 6 A 1750-186',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7771680, -9.1540365, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2123',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2123'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7771680)) <= 0.00063 AND ABS(v.longitude - (-9.1540365)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Social da Musgueira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7771680)) <= 0.0030 AND ABS(v.longitude - (-9.1540365)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Centro Social e Paroquial de Carnide', 'Lisboa', 'Lisboa', 'Estrada da Correia, 6 1500-210',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7606064, -9.1912789, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2124',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2124'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7606064)) <= 0.00063 AND ABS(v.longitude - (-9.1912789)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Centro Social e Paroquial de Carnide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7606064)) <= 0.0030 AND ABS(v.longitude - (-9.1912789)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Club Internacional de Foot-Ball', 'Lisboa', 'Lisboa', 'Avenida dos Bombeiros 1400-036',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Padel;Ténis', 'Complexo desportivo', NULL,
       38.7143268, -9.2107474, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2127',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2127'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7143268)) <= 0.00063 AND ABS(v.longitude - (-9.2107474)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Club Internacional de Foot-Ball'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7143268)) <= 0.0030 AND ABS(v.longitude - (-9.2107474)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Atlético de Campo de Ourique', 'Lisboa', 'Lisboa', 'Travessa do Cabo, 3 e 3 A 1250-058',
       'Futsal;Basquetebol;Voleibol;Andebol;Judo', 'Complexo desportivo', NULL,
       38.7185808, -9.1617638, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2157',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2157'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7185808)) <= 0.00063 AND ABS(v.longitude - (-9.1617638)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Atlético de Campo de Ourique'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7185808)) <= 0.0030 AND ABS(v.longitude - (-9.1617638)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Atlético Recreativo Caramão', 'Lisboa', 'Lisboa', 'Rua Hermínia Silva 1400-363',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7140984, -9.2078417, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2160',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2160'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7140984)) <= 0.00063 AND ABS(v.longitude - (-9.2078417)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Atlético Recreativo Caramão'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7140984)) <= 0.0030 AND ABS(v.longitude - (-9.2078417)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Futebol "Os Belenenses"', 'Lisboa', 'Lisboa', 'Avenida do Restelo 1449-015',
       'Futebol;Atletismo;Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7020571, -9.2092748, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2161',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2161'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7020571)) <= 0.00063 AND ABS(v.longitude - (-9.2092748)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Futebol "Os Belenenses"'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7020571)) <= 0.0030 AND ABS(v.longitude - (-9.2092748)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Futebol "Os Belenenses" - Campo das Salésias', 'Lisboa', 'Lisboa', 'Rua Alexandre de Sá Pinto 1349-003',
       'Futebol', 'Complexo desportivo', NULL,
       38.7011330, -9.1955474, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2168',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2168'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7011330)) <= 0.00063 AND ABS(v.longitude - (-9.1955474)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Futebol "Os Belenenses" - Campo das Salésias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7011330)) <= 0.0030 AND ABS(v.longitude - (-9.1955474)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Futebol "Os Unidos"', 'Lisboa', 'Lisboa', 'Rua Rio Zêzere, 2 1600-755',
       'Futebol', 'Complexo desportivo', NULL,
       38.7702224, -9.1875584, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2169',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2169'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7702224)) <= 0.00063 AND ABS(v.longitude - (-9.1875584)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Futebol "Os Unidos"'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7702224)) <= 0.0030 AND ABS(v.longitude - (-9.1875584)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Futebol de Chelas', 'Lisboa', 'Lisboa', 'Rua Oliveira Cadornega, 55 A 1950-211',
       'Futebol', 'Complexo desportivo', NULL,
       38.7538459, -9.1098106, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2170',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2170'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7538459)) <= 0.00063 AND ABS(v.longitude - (-9.1098106)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Futebol de Chelas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7538459)) <= 0.0030 AND ABS(v.longitude - (-9.1098106)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Golfe do Paço do Lumiar', 'Lisboa', 'Lisboa', 'Rua Formosinho Sanchez, 79 1600-038',
       'Golfe', 'Complexo desportivo', NULL,
       38.7775259, -9.1706938, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2173',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2173'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7775259)) <= 0.00063 AND ABS(v.longitude - (-9.1706938)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Golfe do Paço do Lumiar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7775259)) <= 0.0030 AND ABS(v.longitude - (-9.1706938)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Padel', 'Lisboa', 'Lisboa', 'Doca de Santo Amaro 1269-353',
       'Padel', 'Clube de padel', NULL,
       38.6988146, -9.1789640, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2176',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2176'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6988146)) <= 0.00063 AND ABS(v.longitude - (-9.1789640)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6988146)) <= 0.0030 AND ABS(v.longitude - (-9.1789640)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Rugby do Técnico', 'Lisboa', 'Lisboa', 'Avenida Engenheiro Arantes e Oliveira,  8 1900-222',
       'Rugby;Futebol', 'Complexo desportivo', NULL,
       38.7394803, -9.1228974, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2185',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2185'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7394803)) <= 0.00063 AND ABS(v.longitude - (-9.1228974)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Rugby do Técnico'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7394803)) <= 0.0030 AND ABS(v.longitude - (-9.1228974)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Squash das Olaias', 'Lisboa', 'Lisboa', 'Rua Robalo Gouveia, 2 1900-392',
       'Squash', 'Complexo desportivo', NULL,
       38.7417953, -9.1229749, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2188',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2188'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7417953)) <= 0.00063 AND ABS(v.longitude - (-9.1229749)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Squash das Olaias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7417953)) <= 0.0030 AND ABS(v.longitude - (-9.1229749)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis do Paço do Lumiar', 'Lisboa', 'Lisboa', 'Azinhaga da Fonte Velha, 32 1600-461',
       'Ténis', 'Clube de ténis', NULL,
       38.7751335, -9.1767010, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2192',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2192'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7751335)) <= 0.00063 AND ABS(v.longitude - (-9.1767010)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube de Ténis do Paço do Lumiar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7751335)) <= 0.0030 AND ABS(v.longitude - (-9.1767010)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo da Graça', 'Lisboa', 'Lisboa', 'Rua Senhora da Glória, 16 1170-353',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7174415, -9.1274525, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2198',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2198'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7174415)) <= 0.00063 AND ABS(v.longitude - (-9.1274525)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo da Graça'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7174415)) <= 0.0030 AND ABS(v.longitude - (-9.1274525)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo de Arroios', 'Lisboa', 'Lisboa', 'Rua Marques da Silva, 55 1170-222',
       'Boxe', 'Complexo desportivo', NULL,
       38.7299466, -9.1332278, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2201',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2201'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7299466)) <= 0.00063 AND ABS(v.longitude - (-9.1332278)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo de Arroios'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7299466)) <= 0.0030 AND ABS(v.longitude - (-9.1332278)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo dos Olivais e Moscavide', 'Lisboa', 'Lisboa', 'Rua João Pinto Ribeiro, 103 A 1800-233',
       'Futebol;Futsal;Basquetebol;Judo', 'Complexo desportivo', NULL,
       38.7741690, -9.1019708, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2202',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2202'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7741690)) <= 0.00063 AND ABS(v.longitude - (-9.1019708)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo dos Olivais e Moscavide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7741690)) <= 0.0030 AND ABS(v.longitude - (-9.1019708)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo Império do Cruzeiro', 'Lisboa', 'Lisboa', 'Rua do Cruzeiro, 166 1300-171',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7093870, -9.1942329, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2206',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2206'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7093870)) <= 0.00063 AND ABS(v.longitude - (-9.1942329)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo Império do Cruzeiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7093870)) <= 0.0030 AND ABS(v.longitude - (-9.1942329)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo Lisboa e Águias', 'Lisboa', 'Lisboa', 'Rua das Acácias, 1 - Bairro da Boavista 1500-663',
       'Futebol', 'Complexo desportivo', NULL,
       38.7312201, -9.2046339, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2207',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2207'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7312201)) <= 0.00063 AND ABS(v.longitude - (-9.2046339)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Desportivo Lisboa e Águias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7312201)) <= 0.0030 AND ABS(v.longitude - (-9.2046339)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Ferroviário de Portugal - Campo de Futebol', 'Lisboa', 'Lisboa', 'Azinhaga dos Alfinetes 1950-438',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7385527, -9.1088552, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2214',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2214'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7385527)) <= 0.00063 AND ABS(v.longitude - (-9.1088552)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Ferroviário de Portugal - Campo de Futebol'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7385527)) <= 0.0030 AND ABS(v.longitude - (-9.1088552)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Ferroviário de Portugal - Doca de Santo Amaro', 'Lisboa', 'Lisboa', 'Doca de Santo Amaro 1350-353',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.7006812, -9.1754940, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2216',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2216'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7006812)) <= 0.00063 AND ABS(v.longitude - (-9.1754940)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Ferroviário de Portugal - Doca de Santo Amaro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7006812)) <= 0.0030 AND ABS(v.longitude - (-9.1754940)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Futebol Benfica', 'Lisboa', 'Lisboa', 'Rua Olivério Serpa, 9 1500-471',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7492487, -9.2063791, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2220',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2220'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7492487)) <= 0.00063 AND ABS(v.longitude - (-9.2063791)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Futebol Benfica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7492487)) <= 0.0030 AND ABS(v.longitude - (-9.2063791)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Futebol Varejense', 'Lisboa', 'Lisboa', 'Avenida Afonso III, 86 1900-048',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7270730, -9.1181843, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2225',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2225'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7270730)) <= 0.00063 AND ABS(v.longitude - (-9.1181843)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Futebol Varejense'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7270730)) <= 0.0030 AND ABS(v.longitude - (-9.1181843)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Nacional de Natação', 'Lisboa', 'Lisboa', 'Rua de São Bento, 209 1250-219',
       'Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7160348, -9.1561476, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2227',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2227'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7160348)) <= 0.00063 AND ABS(v.longitude - (-9.1561476)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Nacional de Natação'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7160348)) <= 0.0030 AND ABS(v.longitude - (-9.1561476)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval de Lisboa - Cais do Gás', 'Lisboa', 'Lisboa', 'Rua do Cais do Gás, Armazém H 1199-009',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.7055371, -9.1478954, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2232',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2232'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7055371)) <= 0.00063 AND ABS(v.longitude - (-9.1478954)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval de Lisboa - Cais do Gás'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7055371)) <= 0.0030 AND ABS(v.longitude - (-9.1478954)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Naval de Lisboa - Doca de Belém', 'Lisboa', 'Lisboa', 'Doca de Belém 1401-038',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6942471, -9.2022731, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2233',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2233'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6942471)) <= 0.00063 AND ABS(v.longitude - (-9.2022731)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Naval de Lisboa - Doca de Belém'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6942471)) <= 0.0030 AND ABS(v.longitude - (-9.2022731)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Oriental de Lisboa - Campo de Jogos', 'Lisboa', 'Lisboa', 'Rua José do Patrocínio 1950-164',
       'Futebol', 'Complexo desportivo', NULL,
       38.7432653, -9.1099094, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2234',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2234'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7432653)) <= 0.00063 AND ABS(v.longitude - (-9.1099094)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Oriental de Lisboa - Campo de Jogos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7432653)) <= 0.0030 AND ABS(v.longitude - (-9.1099094)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Oriental de Lisboa - Ringue da sub-sede', 'Lisboa', 'Lisboa', 'Rua Gualdim Pais, 44 1900-255',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7276235, -9.1139840, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2236',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2236'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7276235)) <= 0.00063 AND ABS(v.longitude - (-9.1139840)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Oriental de Lisboa - Ringue da sub-sede'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7276235)) <= 0.0030 AND ABS(v.longitude - (-9.1139840)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Recreativo "Os Leões das Furnas"', 'Lisboa', 'Lisboa', 'Rua Raúl Carapinha 1500-542',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7409687, -9.1726648, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2238',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2238'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7409687)) <= 0.00063 AND ABS(v.longitude - (-9.1726648)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Recreativo "Os Leões das Furnas"'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7409687)) <= 0.0030 AND ABS(v.longitude - (-9.1726648)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Social e Desportivo do Bairro da Boavista', 'Lisboa', 'Lisboa', 'Rua das Azáleas 1500-669',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7350066, -9.2062347, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2240',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2240'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7350066)) <= 0.00063 AND ABS(v.longitude - (-9.2062347)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Social e Desportivo do Bairro da Boavista'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7350066)) <= 0.0030 AND ABS(v.longitude - (-9.2062347)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Sportivo de Pedrouços - Armazém Desportos Náuticos', 'Lisboa', 'Lisboa', 'Doca de Belém 1399-038',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6951155, -9.2022748, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2241',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2241'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6951155)) <= 0.00063 AND ABS(v.longitude - (-9.2022748)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Sportivo de Pedrouços - Armazém Desportos Náuticos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6951155)) <= 0.0030 AND ABS(v.longitude - (-9.2022748)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Sportivo de Pedrouços - Parque Recreativo', 'Lisboa', 'Lisboa', 'Largo Luís Alves Miguel 1400-236',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6957343, -9.2201038, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2242',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2242'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6957343)) <= 0.00063 AND ABS(v.longitude - (-9.2201038)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Sportivo de Pedrouços - Parque Recreativo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6957343)) <= 0.0030 AND ABS(v.longitude - (-9.2201038)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Tap Portugal', 'Lisboa', 'Lisboa', 'Alameda da Encarnação, 1 1800-187',
       'Futebol', 'Complexo desportivo', NULL,
       38.7710042, -9.1225637, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2246',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2246'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7710042)) <= 0.00063 AND ABS(v.longitude - (-9.1225637)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube Tap Portugal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7710042)) <= 0.0030 AND ABS(v.longitude - (-9.1225637)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube VII - Health and Fitness Club', 'Lisboa', 'Lisboa', 'Parque Eduardo VII 1070-099',
       'Ténis;Padel;Natação', 'Complexo desportivo', NULL,
       38.7271758, -9.1531545, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2247',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2247'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7271758)) <= 0.00063 AND ABS(v.longitude - (-9.1531545)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Clube VII - Health and Fitness Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7271758)) <= 0.0030 AND ABS(v.longitude - (-9.1531545)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio de Santa Doroteia', 'Lisboa', 'Lisboa', 'Avenida Marechal Craveiro Lopes, 1 1749-012',
       'Futsal;Basquetebol;Voleibol;Andebol;Atletismo', 'Complexo desportivo', NULL,
       38.7613101, -9.1523557, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2269',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2269'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7613101)) <= 0.00063 AND ABS(v.longitude - (-9.1523557)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio de Santa Doroteia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7613101)) <= 0.0030 AND ABS(v.longitude - (-9.1523557)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio de São Tomás - Sete Rios', 'Lisboa', 'Lisboa', 'Rua Professor Lima Basto, 133 1070-212',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7401836, -9.1658510, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2277',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2277'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7401836)) <= 0.00063 AND ABS(v.longitude - (-9.1658510)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio de São Tomás - Sete Rios'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7401836)) <= 0.0030 AND ABS(v.longitude - (-9.1658510)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio do Bom Sucesso', 'Lisboa', 'Lisboa', 'Rua Bartolomeu Dias, 53 1400-026',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6950941, -9.2109235, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2279',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2279'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6950941)) <= 0.00063 AND ABS(v.longitude - (-9.2109235)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio do Bom Sucesso'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6950941)) <= 0.0030 AND ABS(v.longitude - (-9.2109235)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio do Sagrado Coração de Maria', 'Lisboa', 'Lisboa', 'Avenida Manuel da Maia, 2 1000-201',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7356752, -9.1354156, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2282',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2282'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7356752)) <= 0.00063 AND ABS(v.longitude - (-9.1354156)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio do Sagrado Coração de Maria'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7356752)) <= 0.0030 AND ABS(v.longitude - (-9.1354156)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Manuel Bernardes', 'Lisboa', 'Lisboa', 'Quinta dos Azulejos, Paço do Lumiar 1600-549',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7741062, -9.1741268, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2297',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2297'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7741062)) <= 0.00063 AND ABS(v.longitude - (-9.1741268)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Manuel Bernardes'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7741062)) <= 0.0030 AND ABS(v.longitude - (-9.1741268)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Mira Rio', 'Lisboa', 'Lisboa', 'Estrada de Telheiras, 113 1600-768',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7592339, -9.1642831, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2317',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2317'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7592339)) <= 0.00063 AND ABS(v.longitude - (-9.1642831)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Mira Rio'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7592339)) <= 0.0030 AND ABS(v.longitude - (-9.1642831)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Moderno', 'Lisboa', 'Lisboa', 'Rua Doutor João Soares, 19 e 19 A 1600-016',
       'Futsal;Basquetebol;Ténis', 'Complexo desportivo', NULL,
       38.7521524, -9.1534408, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2318',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2318'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7521524)) <= 0.00063 AND ABS(v.longitude - (-9.1534408)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Moderno'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7521524)) <= 0.0030 AND ABS(v.longitude - (-9.1534408)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio O Parque', 'Lisboa', 'Lisboa', 'Avenida das Descobertas, 21 1400-091',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7082488, -9.2162216, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2331',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2331'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7082488)) <= 0.00063 AND ABS(v.longitude - (-9.2162216)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio O Parque'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7082488)) <= 0.0030 AND ABS(v.longitude - (-9.2162216)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Oriente', 'Lisboa', 'Lisboa', 'Rua Príncipe do Mónaco, 1-1 B 1990-516',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7854209, -9.0965364, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2334',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2334'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7854209)) <= 0.00063 AND ABS(v.longitude - (-9.0965364)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Oriente'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7854209)) <= 0.0030 AND ABS(v.longitude - (-9.0965364)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Pedro Arrupe', 'Lisboa', 'Lisboa', 'Passeio dos Heróis do Mar, 100 Parque das Nações 1990-529',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Ténis;Natação', 'Complexo desportivo', NULL,
       38.7935554, -9.0962743, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2336',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2336'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7935554)) <= 0.00063 AND ABS(v.longitude - (-9.0962743)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Pedro Arrupe'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7935554)) <= 0.0030 AND ABS(v.longitude - (-9.0962743)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Planalto', 'Lisboa', 'Lisboa', 'Rua Armindo Rodrigues, 28 1600-414',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7679034, -9.1660323, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2367',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2367'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7679034)) <= 0.00063 AND ABS(v.longitude - (-9.1660323)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Planalto'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7679034)) <= 0.0030 AND ABS(v.longitude - (-9.1660323)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Saint Daniel Brottier', 'Lisboa', 'Lisboa', 'Avenida Almirante Gago Coutinho, 82 a 87 1700-028',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7516322, -9.1301246, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2370',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2370'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7516322)) <= 0.00063 AND ABS(v.longitude - (-9.1301246)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Saint Daniel Brottier'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7516322)) <= 0.0030 AND ABS(v.longitude - (-9.1301246)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio São João de Brito', 'Lisboa', 'Lisboa', 'Estrada da Torre, 28 1769-004',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Ténis;Natação', 'Complexo desportivo', NULL,
       38.7727509, -9.1572436, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2372',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2372'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7727509)) <= 0.00063 AND ABS(v.longitude - (-9.1572436)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio São João de Brito'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7727509)) <= 0.0030 AND ABS(v.longitude - (-9.1572436)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio São Tomás - Quinta das Conchas', 'Lisboa', 'Lisboa', 'Avenida Maria Helena Vieira da Silva, 39 1750-184',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7676551, -9.1501832, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2382',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2382'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7676551)) <= 0.00063 AND ABS(v.longitude - (-9.1501832)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio São Tomás - Quinta das Conchas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7676551)) <= 0.0030 AND ABS(v.longitude - (-9.1501832)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Valsassina', 'Lisboa', 'Lisboa', 'Avenida Avelino Teixeira da Mota - Quinta das Teresinhas 1959-010',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Ténis', 'Complexo desportivo', NULL,
       38.7567434, -9.1246508, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2384',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2384'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7567434)) <= 0.00063 AND ABS(v.longitude - (-9.1246508)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Valsassina'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7567434)) <= 0.0030 AND ABS(v.longitude - (-9.1246508)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo da Junta de Freguesia de Benfica', 'Lisboa', 'Lisboa', 'Avenida Gomes Pereira, 17 1549-019',
       'Futsal;Basquetebol;Natação', 'Complexo desportivo', NULL,
       38.7483694, -9.1972309, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2389',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2389'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7483694)) <= 0.00063 AND ABS(v.longitude - (-9.1972309)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo da Junta de Freguesia de Benfica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7483694)) <= 0.0030 AND ABS(v.longitude - (-9.1972309)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo da Lapa', 'Lisboa', 'Lisboa', 'Rua do Quelhas, 32 1200-602',
       'Futsal;Basquetebol;Voleibol;Andebol;Judo', 'Complexo desportivo', NULL,
       38.7106784, -9.1575427, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2392',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2392'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7106784)) <= 0.00063 AND ABS(v.longitude - (-9.1575427)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo da Lapa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7106784)) <= 0.0030 AND ABS(v.longitude - (-9.1575427)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo do Bairro da Boavista', 'Lisboa', 'Lisboa', 'Rua das Azáleas 1500-669',
       'Ciclismo;Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7341698, -9.2066700, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2398',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2398'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7341698)) <= 0.00063 AND ABS(v.longitude - (-9.2066700)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo do Bairro da Boavista'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7341698)) <= 0.0030 AND ABS(v.longitude - (-9.2066700)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Municipal Casal Vistoso - CML / DAFD', 'Lisboa', 'Lisboa', 'Rua João da Silva 1900-271',
       'Escalada;Futsal;Basquetebol;Voleibol;Andebol;Natação;Ginástica', 'Complexo desportivo', NULL,
       38.7429338, -9.1286525, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2402',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2402'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7429338)) <= 0.00063 AND ABS(v.longitude - (-9.1286525)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Municipal Casal Vistoso - CML / DAFD'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7429338)) <= 0.0030 AND ABS(v.longitude - (-9.1286525)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Municipal do Alto do Lumiar - CML/DAFD', 'Lisboa', 'Lisboa', 'Rua Victor Cunha Rego 1750-265',
       'Futebol;Futsal;Basquetebol;Boxe', 'Complexo desportivo', NULL,
       38.7756678, -9.1445886, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2407',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2407'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7756678)) <= 0.00063 AND ABS(v.longitude - (-9.1445886)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Municipal do Alto do Lumiar - CML/DAFD'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7756678)) <= 0.0030 AND ABS(v.longitude - (-9.1445886)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Complexo Desportivo Municipal São João de Brito', 'Lisboa', 'Lisboa', 'Avenida do Brasil, Estrada da Portela 1700-067',
       'Rugby;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7607024, -9.1353672, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2411',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2411'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7607024)) <= 0.00063 AND ABS(v.longitude - (-9.1353672)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Complexo Desportivo Municipal São João de Brito'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7607024)) <= 0.0030 AND ABS(v.longitude - (-9.1353672)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Alemã - Associação Escola Alemã de Lisboa', 'Lisboa', 'Lisboa', 'Rua Professor Francisco Lucas Pires 1600-891',
       'Futebol;Atletismo;Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7587253, -9.1626236, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2424',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2424'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7587253)) <= 0.00063 AND ABS(v.longitude - (-9.1626236)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Alemã - Associação Escola Alemã de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7587253)) <= 0.0030 AND ABS(v.longitude - (-9.1626236)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Avé Maria', 'Lisboa', 'Lisboa', 'Rua dos Lusíadas, 49 1300-366',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7049237, -9.1800720, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2432',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2432'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7049237)) <= 0.00063 AND ABS(v.longitude - (-9.1800720)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Avé Maria'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7049237)) <= 0.0030 AND ABS(v.longitude - (-9.1800720)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Santa Teresa do Menino Jesus', 'Lisboa', 'Lisboa', 'Rua da Imprensa Nacional, 93 e 95 1250-125',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7173559, -9.1528193, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2685',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2685'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7173559)) <= 0.00063 AND ABS(v.longitude - (-9.1528193)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola de Santa Teresa do Menino Jesus'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7173559)) <= 0.0030 AND ABS(v.longitude - (-9.1528193)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Ténis Daniel Sá (Pólo de Lisboa)', 'Lisboa', 'Lisboa', 'Rua Professor Sousa da Câmara, 195 B 1070-216',
       'Ténis', 'Clube de ténis', NULL,
       38.7255139, -9.1643232, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2687',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2687'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7255139)) <= 0.00063 AND ABS(v.longitude - (-9.1643232)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola de Ténis Daniel Sá (Pólo de Lisboa)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7255139)) <= 0.0030 AND ABS(v.longitude - (-9.1643232)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Ténis Jaime Caldeira', 'Lisboa', 'Lisboa', 'Passeio Heróis do Mar 1990-061',
       'Ténis', 'Clube de ténis', NULL,
       38.7851680, -9.0945313, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2689',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2689'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7851680)) <= 0.00063 AND ABS(v.longitude - (-9.0945313)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola de Ténis Jaime Caldeira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7851680)) <= 0.0030 AND ABS(v.longitude - (-9.0945313)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Ténis José Mário Silva', 'Lisboa', 'Lisboa', 'Alameda da Encarnação, 1 1800-187',
       'Ténis', 'Clube de ténis', NULL,
       38.7709894, -9.1230186, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2695',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2695'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7709894)) <= 0.00063 AND ABS(v.longitude - (-9.1230186)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola de Ténis José Mário Silva'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7709894)) <= 0.0030 AND ABS(v.longitude - (-9.1230186)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Profissional Gustave Eiffel - Cooptécnico Gustave Eiffel', 'Lisboa', 'Lisboa', 'Alameda das Linhas de Torres, 179 1750-142',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7685842, -9.1612941, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2703',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2703'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7685842)) <= 0.00063 AND ABS(v.longitude - (-9.1612941)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Profissional Gustave Eiffel - Cooptécnico Gustave Eiffel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7685842)) <= 0.0030 AND ABS(v.longitude - (-9.1612941)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Profissional Magestil', 'Lisboa', 'Lisboa', 'Avenida Almirante Gago Coutinho, 95 1700-028',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7532697, -9.1299924, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2706',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2706'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7532697)) <= 0.00063 AND ABS(v.longitude - (-9.1299924)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Profissional Magestil'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7532697)) <= 0.0030 AND ABS(v.longitude - (-9.1299924)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola Raiz', 'Lisboa', 'Lisboa', 'Avenida Torre de Belém, 30 1400-343',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6989774, -9.2139095, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2707',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2707'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6989774)) <= 0.00063 AND ABS(v.longitude - (-9.2139095)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escola Raiz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6989774)) <= 0.0030 AND ABS(v.longitude - (-9.2139095)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Espaço de Jogo da Vila Dias', 'Lisboa', 'Lisboa', 'Beco dos Toucinheiros 1900-431',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7289974, -9.1112161, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2804',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2804'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7289974)) <= 0.00063 AND ABS(v.longitude - (-9.1112161)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Espaço de Jogo da Vila Dias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7289974)) <= 0.0030 AND ABS(v.longitude - (-9.1112161)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Espaço Ideias da Freguesia do Beato', 'Lisboa', 'Lisboa', 'Rua Alves Paiva Fragoso 1900-437',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7298467, -9.1135118, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2805',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2805'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7298467)) <= 0.00063 AND ABS(v.longitude - (-9.1135118)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Espaço Ideias da Freguesia do Beato'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7298467)) <= 0.0030 AND ABS(v.longitude - (-9.1135118)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Espaços desportivos da Rua Jorge Dias', 'Lisboa', 'Lisboa', 'Rua Rodrigo Reinel / Rua Jorge Dias 1400-350',
       'Futsal;Basquetebol;Voleibol;Atletismo', 'Complexo desportivo', NULL,
       38.7068542, -9.2059109, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2811',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2811'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7068542)) <= 0.00063 AND ABS(v.longitude - (-9.2059109)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Espaços desportivos da Rua Jorge Dias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7068542)) <= 0.0030 AND ABS(v.longitude - (-9.2059109)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Universitário de Lisboa -  Ajuda', 'Lisboa', 'Lisboa', 'Impasse à Avenida da Universidade Técnica 1349-055',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7148515, -9.1975698, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2818',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2818'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7148515)) <= 0.00063 AND ABS(v.longitude - (-9.1975698)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Universitário de Lisboa -  Ajuda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7148515)) <= 0.0030 AND ABS(v.longitude - (-9.1975698)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estádio Universitário de Lisboa - Cidade Universitária', 'Lisboa', 'Lisboa', 'Avenida Professor Egas Moniz 1600-190',
       'Futebol;Atletismo;Futsal;Basquetebol;Voleibol;Andebol;Natação;Judo', 'Complexo desportivo', NULL,
       38.7535130, -9.1628391, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2824',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2824'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7535130)) <= 0.00063 AND ABS(v.longitude - (-9.1628391)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Estádio Universitário de Lisboa - Cidade Universitária'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7535130)) <= 0.0030 AND ABS(v.longitude - (-9.1628391)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato As Descobertas', 'Lisboa', 'Lisboa', 'Rua Capitão-Mor Pedro Teixeira, 11 1400-041',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7081882, -9.2075773, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2853',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2853'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7081882)) <= 0.00063 AND ABS(v.longitude - (-9.2075773)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato As Descobertas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7081882)) <= 0.0030 AND ABS(v.longitude - (-9.2075773)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato da Luz', 'Lisboa', 'Lisboa', 'Largo da Luz, 11 1600-498',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7629257, -9.1817517, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2855',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2855'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7629257)) <= 0.00063 AND ABS(v.longitude - (-9.1817517)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato da Luz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7629257)) <= 0.0030 AND ABS(v.longitude - (-9.1817517)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato de Educação Popular', 'Lisboa', 'Lisboa', 'Rua da Capela, Bairro da Liberdade 1070-042',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7308684, -9.1743707, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2862',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2862'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7308684)) <= 0.00063 AND ABS(v.longitude - (-9.1743707)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato de Educação Popular'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7308684)) <= 0.0030 AND ABS(v.longitude - (-9.1743707)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato de São José', 'Lisboa', 'Lisboa', 'Avenida das Descobertas, 27 1400-091',
       'Atletismo;Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7110878, -9.2161931, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2864',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2864'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7110878)) <= 0.00063 AND ABS(v.longitude - (-9.2161931)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato de São José'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7110878)) <= 0.0030 AND ABS(v.longitude - (-9.2161931)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Grão Vasco', 'Lisboa', 'Lisboa', 'Avenida Grão Vasco, 40 1500-338',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7501419, -9.2042327, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2875',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2875'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7501419)) <= 0.00063 AND ABS(v.longitude - (-9.2042327)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Grão Vasco'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7501419)) <= 0.0030 AND ABS(v.longitude - (-9.2042327)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Infantil e Primário O Beiral', 'Lisboa', 'Lisboa', 'Travessa de Francisco Rezende, 57 1500-289',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7423916, -9.1960965, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2879',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2879'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7423916)) <= 0.00063 AND ABS(v.longitude - (-9.1960965)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Infantil e Primário O Beiral'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7423916)) <= 0.0030 AND ABS(v.longitude - (-9.1960965)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato João XXIII', 'Lisboa', 'Lisboa', 'Rua Corsário das Ilhas, Lote 3.27.02 1990-249',
       'Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7592572, -9.0979515, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2881',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2881'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7592572)) <= 0.00063 AND ABS(v.longitude - (-9.0979515)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato João XXIII'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7592572)) <= 0.0030 AND ABS(v.longitude - (-9.0979515)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Liceal das Casas de São Vicente de Paulo', 'Lisboa', 'Lisboa', 'Avenida Marechal Craveiro Lopes, 10 1700-284',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7594798, -9.1510430, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2885',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2885'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7594798)) <= 0.00063 AND ABS(v.longitude - (-9.1510430)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Liceal das Casas de São Vicente de Paulo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7594798)) <= 0.0030 AND ABS(v.longitude - (-9.1510430)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Marcelino Champagnat', 'Lisboa', 'Lisboa', 'Quinta de Vila Formosa - Aeroporto 1700-008',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7784330, -9.1266161, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2889',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2889'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7784330)) <= 0.00063 AND ABS(v.longitude - (-9.1266161)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Marcelino Champagnat'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7784330)) <= 0.0030 AND ABS(v.longitude - (-9.1266161)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Marista de Lisboa', 'Lisboa', 'Lisboa', 'Rua Major Neutel de Abreu, 11 1500-409',
       'Atletismo;Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7472862, -9.1821834, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2896',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2896'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7472862)) <= 0.00063 AND ABS(v.longitude - (-9.1821834)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Marista de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7472862)) <= 0.0030 AND ABS(v.longitude - (-9.1821834)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato Nossa Senhora da Penha de França', 'Lisboa', 'Lisboa', 'Travessa do Calado, 26 1170-070',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7303040, -9.1296932, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2904',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2904'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7303040)) <= 0.00063 AND ABS(v.longitude - (-9.1296932)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato Nossa Senhora da Penha de França'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7303040)) <= 0.0030 AND ABS(v.longitude - (-9.1296932)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato São Cristóvão de Benfica', 'Lisboa', 'Lisboa', 'Rua Emília das Neves, 7 e 7 A 1500-259',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7502785, -9.2038242, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2907',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2907'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7502785)) <= 0.00063 AND ABS(v.longitude - (-9.2038242)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato São Cristóvão de Benfica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7502785)) <= 0.0030 AND ABS(v.longitude - (-9.2038242)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Externato São Miguel Arcanjo', 'Lisboa', 'Lisboa', 'Avenida Dr. Alfredo Bensaúde - Olivais Norte 1849-029',
       'Futsal;Basquetebol;Natação', 'Complexo desportivo', NULL,
       38.7796592, -9.1154878, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2909',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2909'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7796592)) <= 0.00063 AND ABS(v.longitude - (-9.1154878)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Externato São Miguel Arcanjo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7796592)) <= 0.0030 AND ABS(v.longitude - (-9.1154878)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Forno do Tijolo - Arroios', 'Lisboa', 'Lisboa', 'Rua Maria da Fonte - Mercado Forno do Tijolo 1170-221',
       'Futsal;Basquetebol;Natação', 'Complexo desportivo', NULL,
       38.7210037, -9.1325316, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2954',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2954'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7210037)) <= 0.00063 AND ABS(v.longitude - (-9.1325316)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Forno do Tijolo - Arroios'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7210037)) <= 0.0030 AND ABS(v.longitude - (-9.1325316)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Fundação INATEL - Parque de Jogos 1º MAIO', 'Lisboa', 'Lisboa', 'Avenida Rio de Janeiro 1700-330',
       'Futebol;Atletismo;Futsal;Basquetebol;Voleibol;Andebol;Padel;Ténis;Natação', 'Complexo desportivo', NULL,
       38.7510272, -9.1391024, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2956',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2956'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7510272)) <= 0.00063 AND ABS(v.longitude - (-9.1391024)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Fundação INATEL - Parque de Jogos 1º MAIO'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7510272)) <= 0.0030 AND ABS(v.longitude - (-9.1391024)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio Alto do Duque', 'Lisboa', 'Lisboa', 'Estrada do Forte do Alto do Duque - Edifício Desportivo 1400-009',
       'Ténis;Futsal;Basquetebol;Squash', 'Complexo desportivo', NULL,
       38.7032312, -9.2206483, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2979',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2979'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7032312)) <= 0.00063 AND ABS(v.longitude - (-9.2206483)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio Alto do Duque'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7032312)) <= 0.0030 AND ABS(v.longitude - (-9.2206483)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio Clube Bairro do Alvito', 'Lisboa', 'Lisboa', 'Bairro do Alvito - Edifício da antiga Escola 155 (lado Norte) 1300-052',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7145516, -9.1807499, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2987',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2987'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7145516)) <= 0.00063 AND ABS(v.longitude - (-9.1807499)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio Clube Bairro do Alvito'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7145516)) <= 0.0030 AND ABS(v.longitude - (-9.1807499)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ginásio Clube Português', 'Lisboa', 'Lisboa', 'Praça Ginásio Clube Português,  1 1250-111',
       'Tiro com Arco;Futsal;Basquetebol;Ténis;Padel;Ginástica;Judo', 'Complexo desportivo', NULL,
       38.7215435, -9.1572043, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:2988',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:2988'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7215435)) <= 0.00063 AND ABS(v.longitude - (-9.1572043)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ginásio Clube Português'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7215435)) <= 0.0030 AND ABS(v.longitude - (-9.1572043)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Go Fit Campo Grande', 'Lisboa', 'Lisboa', 'Jardim do Campo Grande 1700-162',
       'Natação', 'Piscina', NULL,
       38.7527438, -9.1508316, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3024',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3024'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7527438)) <= 0.00063 AND ABS(v.longitude - (-9.1508316)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Go Fit Campo Grande'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7527438)) <= 0.0030 AND ABS(v.longitude - (-9.1508316)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Go Fit Olivais', 'Lisboa', 'Lisboa', 'Avenida de Berlim 1800-033',
       'Padel;Futsal;Basquetebol;Natação', 'Complexo desportivo', NULL,
       38.7691166, -9.1124992, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3031',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3031'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7691166)) <= 0.00063 AND ABS(v.longitude - (-9.1124992)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Go Fit Olivais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7691166)) <= 0.0030 AND ABS(v.longitude - (-9.1124992)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grémio de Instrução Liberal de Campo de Ourique - Escola', 'Lisboa', 'Lisboa', 'Largo Doutor António Viana, 4 1250-096',
       'Escalada;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7178069, -9.1615544, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3047',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3047'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7178069)) <= 0.00063 AND ABS(v.longitude - (-9.1615544)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grémio de Instrução Liberal de Campo de Ourique - Escola'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7178069)) <= 0.0030 AND ABS(v.longitude - (-9.1615544)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo da Mouraria', 'Lisboa', 'Lisboa', 'Travessa da Nazaré, 21 - 1º 1100-368',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7184808, -9.1334957, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3051',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3051'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7184808)) <= 0.00063 AND ABS(v.longitude - (-9.1334957)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo da Mouraria'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7184808)) <= 0.0030 AND ABS(v.longitude - (-9.1334957)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo de Direito', 'Lisboa', 'Lisboa', 'Estrada do Outeiro, Parque Florestal Monsanto 1500-215',
       'Rugby', 'Complexo desportivo', NULL,
       38.7299171, -9.2056542, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3055',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3055'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7299171)) <= 0.00063 AND ABS(v.longitude - (-9.2056542)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo de Direito'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7299171)) <= 0.0030 AND ABS(v.longitude - (-9.2056542)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Desportivo Operário', 'Lisboa', 'Lisboa', 'Campo Pequeno 1000-306',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7421212, -9.1442734, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3061',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3061'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7421212)) <= 0.00063 AND ABS(v.longitude - (-9.1442734)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Desportivo Operário'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7421212)) <= 0.0030 AND ABS(v.longitude - (-9.1442734)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Dramático e Escolar  "Os Combatentes" - Parque Desportivo', 'Lisboa', 'Lisboa', 'Rua Possidónio da Silva 1350-297',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7119976, -9.1696242, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3068',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3068'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7119976)) <= 0.00063 AND ABS(v.longitude - (-9.1696242)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Dramático e Escolar  "Os Combatentes" - Parque Desportivo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7119976)) <= 0.0030 AND ABS(v.longitude - (-9.1696242)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Dramático Ramiro José', 'Lisboa', 'Lisboa', 'Rua João Villaret, 11 1000-082',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7453524, -9.1404032, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3071',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3071'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7453524)) <= 0.00063 AND ABS(v.longitude - (-9.1404032)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Dramático Ramiro José'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7453524)) <= 0.0030 AND ABS(v.longitude - (-9.1404032)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Recreativo e Cultural " Os Onze Unidos"', 'Lisboa', 'Lisboa', 'Rua Doutor Manuel Espirito Santo, 11 1900-209',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7348460, -9.1149737, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3079',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3079'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7348460)) <= 0.00063 AND ABS(v.longitude - (-9.1149737)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Recreativo e Cultural " Os Onze Unidos"'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7348460)) <= 0.0030 AND ABS(v.longitude - (-9.1149737)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Recreativo e Desportivo do Bairro da Cruz Vermelha', 'Lisboa', 'Lisboa', 'Rua Maria Carlota - Largo das 3 Marias 1750-301',
       'Boxe', 'Complexo desportivo', NULL,
       38.7767024, -9.1541235, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3083',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3083'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7767024)) <= 0.00063 AND ABS(v.longitude - (-9.1541235)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Grupo Recreativo e Desportivo do Bairro da Cruz Vermelha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7767024)) <= 0.0030 AND ABS(v.longitude - (-9.1541235)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Holmes Place - Palácio Sottomayor', 'Lisboa', 'Lisboa', 'Avenida Fontes Pereira de Melo, 16 1050-121',
       'Natação', 'Piscina', NULL,
       38.7285436, -9.1471495, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3102',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3102'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7285436)) <= 0.00063 AND ABS(v.longitude - (-9.1471495)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Holmes Place - Palácio Sottomayor'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7285436)) <= 0.0030 AND ABS(v.longitude - (-9.1471495)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Holmes Place Alvalade XXI', 'Lisboa', 'Lisboa', 'Rua Professor Fernando da Fonseca, Pavilhão Multidesportivo 1600-616',
       'Natação', 'Piscina', NULL,
       38.7605207, -9.1597301, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3108',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3108'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7605207)) <= 0.00063 AND ABS(v.longitude - (-9.1597301)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Holmes Place Alvalade XXI'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7605207)) <= 0.0030 AND ABS(v.longitude - (-9.1597301)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Holmes Place Amoreiras', 'Lisboa', 'Lisboa', 'Rua Silva Carvalho, 321 - 2.º - Edifício Amoreiras Plaza 1250-212',
       'Natação', 'Piscina', NULL,
       38.7225133, -9.1605533, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3114',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3114'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7225133)) <= 0.00063 AND ABS(v.longitude - (-9.1605533)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Holmes Place Amoreiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7225133)) <= 0.0030 AND ABS(v.longitude - (-9.1605533)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Holmes Place Defensores de Chaves', 'Lisboa', 'Lisboa', 'Avenida Defensores de Chaves, 45 B 1000-112',
       'Natação', 'Piscina', NULL,
       38.7368803, -9.1444771, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3119',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3119'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7368803)) <= 0.00063 AND ABS(v.longitude - (-9.1444771)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Holmes Place Defensores de Chaves'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7368803)) <= 0.0030 AND ABS(v.longitude - (-9.1444771)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'In Time Sports', 'Lisboa', 'Lisboa', 'Avenida Marechal Gomes da Costa, 29 - Fracção B 1800-255',
       'Futsal;Basquetebol;Padel', 'Complexo desportivo', NULL,
       38.7571323, -9.1110457, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3125',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3125'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7571323)) <= 0.00063 AND ABS(v.longitude - (-9.1110457)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('In Time Sports'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7571323)) <= 0.0030 AND ABS(v.longitude - (-9.1110457)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Infraestrutura da Associação Regional de Vela do Centro', 'Lisboa', 'Lisboa', 'Doca de Belém 1399-038',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6943240, -9.2025266, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3130',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3130'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6943240)) <= 0.00063 AND ABS(v.longitude - (-9.2025266)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Infraestrutura da Associação Regional de Vela do Centro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6943240)) <= 0.0030 AND ABS(v.longitude - (-9.2025266)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Instituto Superior de Ciências Policiais e Segurança Interna', 'Lisboa', 'Lisboa', 'Rua 1º de Maio a Alcântara, 3 1349-040',
       'Futsal;Basquetebol;Voleibol;Andebol;Natação', 'Complexo desportivo', NULL,
       38.7038509, -9.1787231, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3145',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3145'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7038509)) <= 0.00063 AND ABS(v.longitude - (-9.1787231)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Instituto Superior de Ciências Policiais e Segurança Interna'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7038509)) <= 0.0030 AND ABS(v.longitude - (-9.1787231)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'ISCTE - Instituto Universitário de Lisboa', 'Lisboa', 'Lisboa', 'Avenida das Forças Armadas 1649-026',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7482491, -9.1527779, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3151',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3151'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7482491)) <= 0.00063 AND ABS(v.longitude - (-9.1527779)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('ISCTE - Instituto Universitário de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7482491)) <= 0.0030 AND ABS(v.longitude - (-9.1527779)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Espaços desportivos da Praça Bernardo Santareno', 'Lisboa', 'Lisboa', 'Praça Bernardo Santareno 1900-098',
       'Futsal;Basquetebol;Padel;Atletismo', 'Complexo desportivo', NULL,
       38.7441735, -9.1279918, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3155',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3155'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7441735)) <= 0.00063 AND ABS(v.longitude - (-9.1279918)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Espaços desportivos da Praça Bernardo Santareno'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7441735)) <= 0.0030 AND ABS(v.longitude - (-9.1279918)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardim Bento Martins', 'Lisboa', 'Lisboa', 'Rua Adelaide Cabete 1500-039',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7569019, -9.1872193, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3162',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3162'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7569019)) <= 0.00063 AND ABS(v.longitude - (-9.1872193)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardim Bento Martins'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7569019)) <= 0.0030 AND ABS(v.longitude - (-9.1872193)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardim do Campo Mártires da Pátria', 'Lisboa', 'Lisboa', 'Campo Mártires da Pátria 1169-000',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7225045, -9.1398003, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3173',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3173'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7225045)) <= 0.00063 AND ABS(v.longitude - (-9.1398003)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardim do Campo Mártires da Pátria'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7225045)) <= 0.0030 AND ABS(v.longitude - (-9.1398003)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardim Escola João de Deus - Estrela', 'Lisboa', 'Lisboa', 'Avenida Alvares Cabral, 69 A 1250-017',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7164654, -9.1579490, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3179',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3179'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7164654)) <= 0.00063 AND ABS(v.longitude - (-9.1579490)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardim Escola João de Deus - Estrela'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7164654)) <= 0.0030 AND ABS(v.longitude - (-9.1579490)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardim Fernando Pessa', 'Lisboa', 'Lisboa', 'Jardim Fernando Pessa 1000-265',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7430898, -9.1361161, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3184',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3184'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7430898)) <= 0.00063 AND ABS(v.longitude - (-9.1361161)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardim Fernando Pessa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7430898)) <= 0.0030 AND ABS(v.longitude - (-9.1361161)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Jardim Miradouro de Caselas', 'Lisboa', 'Lisboa', 'Rua José Calheiros 1400-229',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7164308, -9.2180016, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3187',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3187'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7164308)) <= 0.00063 AND ABS(v.longitude - (-9.2180016)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Jardim Miradouro de Caselas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7164308)) <= 0.0030 AND ABS(v.longitude - (-9.2180016)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Judo Clube de Portugal', 'Lisboa', 'Lisboa', 'Rua Dom Carlos de Mascarenhas, 36 A 1070-083',
       'Judo', 'Complexo desportivo', NULL,
       38.7277234, -9.1636892, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3208',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3208'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7277234)) <= 0.00063 AND ABS(v.longitude - (-9.1636892)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Judo Clube de Portugal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7277234)) <= 0.0030 AND ABS(v.longitude - (-9.1636892)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Kalorias EXPO', 'Lisboa', 'Lisboa', 'Rua Passeio dos Heróis do Mar, 14 1990-528',
       'Futebol', 'Complexo desportivo', NULL,
       38.7850698, -9.0936421, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3211',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3211'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7850698)) <= 0.00063 AND ABS(v.longitude - (-9.0936421)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Kalorias EXPO'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7850698)) <= 0.0030 AND ABS(v.longitude - (-9.0936421)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lambert Clube', 'Lisboa', 'Lisboa', 'Rua Pedro Bandeira 1750-217',
       'Padel;Ténis', 'Complexo desportivo', NULL,
       38.7622276, -9.1541323, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3216',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3216'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7622276)) <= 0.00063 AND ABS(v.longitude - (-9.1541323)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Lambert Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7622276)) <= 0.0030 AND ABS(v.longitude - (-9.1541323)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'LemonFit - Parque das Nações', 'Lisboa', 'Lisboa', 'Avenida da Peregrinação, 2 1990-382',
       'Ténis;Natação', 'Complexo desportivo', NULL,
       38.7826893, -9.0956967, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3224',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3224'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7826893)) <= 0.00063 AND ABS(v.longitude - (-9.0956967)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('LemonFit - Parque das Nações'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7826893)) <= 0.0030 AND ABS(v.longitude - (-9.0956967)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Liberdade Atlético Clube', 'Lisboa', 'Lisboa', 'Bairro da Liberdade, Rua B - 83 B 1070-017',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7344616, -9.1693022, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3234',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3234'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7344616)) <= 0.00063 AND ABS(v.longitude - (-9.1693022)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Liberdade Atlético Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7344616)) <= 0.0030 AND ABS(v.longitude - (-9.1693022)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Liceu Frances Charles Lepierre', 'Lisboa', 'Lisboa', 'Avenida Engenheiro Duarte Pacheco, 32 1071-811',
       'Escalada;Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7247300, -9.1629276, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3236',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3236'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7247300)) <= 0.00063 AND ABS(v.longitude - (-9.1629276)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Liceu Frances Charles Lepierre'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7247300)) <= 0.0030 AND ABS(v.longitude - (-9.1629276)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lisboa Ginásio Clube', 'Lisboa', 'Lisboa', 'Rua dos Anjos, 63 1150-035',
       'Ginástica', 'Complexo desportivo', NULL,
       38.7249311, -9.1363042, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3254',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3254'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7249311)) <= 0.00063 AND ABS(v.longitude - (-9.1363042)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Lisboa Ginásio Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7249311)) <= 0.0030 AND ABS(v.longitude - (-9.1363042)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lisboa Racket Center', 'Lisboa', 'Lisboa', 'Rua Alferes Malheiro 1700-025',
       'Ténis;Padel;Squash', 'Complexo desportivo', NULL,
       38.7583373, -9.1340614, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3256',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3256'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7583373)) <= 0.00063 AND ABS(v.longitude - (-9.1340614)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Lisboa Racket Center'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7583373)) <= 0.0030 AND ABS(v.longitude - (-9.1340614)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Maria Pia Sport Clube', 'Lisboa', 'Lisboa', 'Rua de São Gens, 11 1170-337',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7190666, -9.1302774, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3280',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3280'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7190666)) <= 0.00063 AND ABS(v.longitude - (-9.1302774)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Maria Pia Sport Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7190666)) <= 0.0030 AND ABS(v.longitude - (-9.1302774)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Marina Parque das Nações', 'Lisboa', 'Lisboa', 'Edificio da Capitania, Passeio de Neptuno 1989-193',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.7670233, -9.0929041, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3281',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3281'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7670233)) <= 0.00063 AND ABS(v.longitude - (-9.0929041)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Marina Parque das Nações'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7670233)) <= 0.0030 AND ABS(v.longitude - (-9.0929041)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mata da Madre de Deus', 'Lisboa', 'Lisboa', 'Largo da Madre de Deus 1000-311',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7321979, -9.1157286, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3283',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3283'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7321979)) <= 0.00063 AND ABS(v.longitude - (-9.1157286)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mata da Madre de Deus'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7321979)) <= 0.0030 AND ABS(v.longitude - (-9.1157286)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Minicampo da Quinta dos Barros', 'Lisboa', 'Lisboa', 'Largo Mário Neves 1600-815',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7519637, -9.1678937, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3284',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3284'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7519637)) <= 0.00063 AND ABS(v.longitude - (-9.1678937)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Minicampo da Quinta dos Barros'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7519637)) <= 0.0030 AND ABS(v.longitude - (-9.1678937)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Minicampo de Jogos do Bairro da Flamenga', 'Lisboa', 'Lisboa', 'Rua Ferreira de Castro 1950-136',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7537992, -9.1239708, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3285',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3285'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7537992)) <= 0.00063 AND ABS(v.longitude - (-9.1239708)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Minicampo de Jogos do Bairro da Flamenga'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7537992)) <= 0.0030 AND ABS(v.longitude - (-9.1239708)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Minicampo de Jogos do Bairro do Armador', 'Lisboa', 'Lisboa', 'Avenida François Mitterand 1950-342',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7446151, -9.1183090, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3286',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3286'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7446151)) <= 0.00063 AND ABS(v.longitude - (-9.1183090)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Minicampo de Jogos do Bairro do Armador'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7446151)) <= 0.0030 AND ABS(v.longitude - (-9.1183090)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini-campo PER 3', 'Lisboa', 'Lisboa', 'Rua Maria Carlota, 14 1750-174',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7767805, -9.1533634, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3287',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3287'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7767805)) <= 0.00063 AND ABS(v.longitude - (-9.1533634)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mini-campo PER 3'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7767805)) <= 0.0030 AND ABS(v.longitude - (-9.1533634)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini-campo PER 9', 'Lisboa', 'Lisboa', 'Rua Ferrer Trindade 1750-448',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7738608, -9.1518387, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3288',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3288'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7738608)) <= 0.00063 AND ABS(v.longitude - (-9.1518387)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Mini-campo PER 9'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7738608)) <= 0.0030 AND ABS(v.longitude - (-9.1518387)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Monsanto – Estrada do Forte de Monsanto', 'Lisboa', 'Lisboa', 'Estrada do Forte de Monsanto, Campo de Jogos 1500-277',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7323430, -9.1931964, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3289',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3289'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7323430)) <= 0.00063 AND ABS(v.longitude - (-9.1931964)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Monsanto – Estrada do Forte de Monsanto'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7323430)) <= 0.0030 AND ABS(v.longitude - (-9.1931964)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Nacional Padel', 'Lisboa', 'Lisboa', 'Rua de São Bento, 209 1250-219',
       'Padel', 'Clube de padel', NULL,
       38.7156506, -9.1558147, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3290',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3290'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7156506)) <= 0.00063 AND ABS(v.longitude - (-9.1558147)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Nacional Padel'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7156506)) <= 0.0030 AND ABS(v.longitude - (-9.1558147)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Náutico Clube Boa Esperança', 'Lisboa', 'Lisboa', 'Rua do Cais do Gás, Armazém 1 1199-109',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.7052349, -9.1467414, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3297',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3297'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7052349)) <= 0.00063 AND ABS(v.longitude - (-9.1467414)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Náutico Clube Boa Esperança'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7052349)) <= 0.0030 AND ABS(v.longitude - (-9.1467414)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Operário Futebol Clube de Lisboa', 'Lisboa', 'Lisboa', 'Rua Barão Monte Pedral - Parque Jogos 1170-039',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7216119, -9.1206461, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3301',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3301'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7216119)) <= 0.00063 AND ABS(v.longitude - (-9.1206461)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Operário Futebol Clube de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7216119)) <= 0.0030 AND ABS(v.longitude - (-9.1206461)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Campo Grande', 'Lisboa', 'Lisboa', 'Jardim do Campo Grande 1700-090',
       'Padel', 'Clube de padel', NULL,
       38.7574069, -9.1535046, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3304',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3304'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7574069)) <= 0.00063 AND ABS(v.longitude - (-9.1535046)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Campo Grande'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7574069)) <= 0.0030 AND ABS(v.longitude - (-9.1535046)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque da Bela Vista I', 'Lisboa', 'Lisboa', 'Rua Dr. Arlindo Vicente 1950-080',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7491480, -9.1231991, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3317',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3317'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7491480)) <= 0.00063 AND ABS(v.longitude - (-9.1231991)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque da Bela Vista I'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7491480)) <= 0.0030 AND ABS(v.longitude - (-9.1231991)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque da Bela Vista II - CML/DEV', 'Lisboa', 'Lisboa', 'Rua Jorge Amado - Parque da Bela Vista II (Ponte Pedonal) 1900-350',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7447450, -9.1244198, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3320',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3320'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7447450)) <= 0.00063 AND ABS(v.longitude - (-9.1244198)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque da Bela Vista II - CML/DEV'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7447450)) <= 0.0030 AND ABS(v.longitude - (-9.1244198)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque do Vale do Silêncio - CML/DEV', 'Lisboa', 'Lisboa', 'Avenida Cidade de Lourenço Marques 1800-089',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7651507, -9.1189020, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3323',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3323'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7651507)) <= 0.00063 AND ABS(v.longitude - (-9.1189020)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque do Vale do Silêncio - CML/DEV'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7651507)) <= 0.0030 AND ABS(v.longitude - (-9.1189020)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque do Vale do Silêncio - Escalada', 'Lisboa', 'Lisboa', 'Parque do Vale do Silêncio - Escalada #N/A',
       'Escalada', 'Complexo desportivo', NULL,
       38.7679113, -9.1179980, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3324',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3324'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7679113)) <= 0.00063 AND ABS(v.longitude - (-9.1179980)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque do Vale do Silêncio - Escalada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7679113)) <= 0.0030 AND ABS(v.longitude - (-9.1179980)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Florestal de Monsanto - Alameda Keil do Amaral 2 - CML/DAEAC', 'Lisboa', 'Lisboa', 'Parque Florestal de Monsanto 1300-651',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7213556, -9.1899422, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3333',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3333'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7213556)) <= 0.00063 AND ABS(v.longitude - (-9.1899422)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Florestal de Monsanto - Alameda Keil do Amaral 2 - CML/DAEAC'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7213556)) <= 0.0030 AND ABS(v.longitude - (-9.1899422)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Florestal de Monsanto - Mata de São Domingos de Benfica - Escalada', 'Lisboa', 'Lisboa', 'Mata de São Domingos de Benfica 1500-068',
       'Escalada', 'Complexo desportivo', NULL,
       38.7438364, -9.1880830, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3337',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3337'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7438364)) <= 0.00063 AND ABS(v.longitude - (-9.1880830)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Florestal de Monsanto - Mata de São Domingos de Benfica - Escalada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7438364)) <= 0.0030 AND ABS(v.longitude - (-9.1880830)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Florestal de Monsanto - Parque Recreativo do Alvito - CML/DAEAC', 'Lisboa', 'Lisboa', 'Estrada do Alvito 1300-054',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7189985, -9.1861438, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3339',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3339'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7189985)) <= 0.00063 AND ABS(v.longitude - (-9.1861438)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Florestal de Monsanto - Parque Recreativo do Alvito - CML/DAEAC'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7189985)) <= 0.0030 AND ABS(v.longitude - (-9.1861438)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Florestal de Monsanto - Polidesportivo do Parque do Calhau', 'Lisboa', 'Lisboa', 'Rua António Macedo 1500-130',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7384170, -9.1771407, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3340',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3340'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7384170)) <= 0.00063 AND ABS(v.longitude - (-9.1771407)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Florestal de Monsanto - Polidesportivo do Parque do Calhau'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7384170)) <= 0.0030 AND ABS(v.longitude - (-9.1771407)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Polivalente de Santa Catarina', 'Lisboa', 'Lisboa', 'Calçada do Combro, 82 A 1200-115',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7119699, -9.1484481, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3345',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3345'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7119699)) <= 0.00063 AND ABS(v.longitude - (-9.1484481)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Polivalente de Santa Catarina'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7119699)) <= 0.0030 AND ABS(v.longitude - (-9.1484481)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Urbano dos Moinhos de Santana', 'Lisboa', 'Lisboa', 'Rua Tristão Vaz - Parque Urbano dos Moinhos de Santana 1400-353',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7074709, -9.2049670, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3350',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3350'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7074709)) <= 0.00063 AND ABS(v.longitude - (-9.2049670)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Urbano dos Moinhos de Santana'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7074709)) <= 0.0030 AND ABS(v.longitude - (-9.2049670)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão da Polícia Municipal', 'Lisboa', 'Lisboa', 'Rua Cardeal Saraiva 1070-045',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7359211, -9.1638654, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3354',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3354'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7359211)) <= 0.00063 AND ABS(v.longitude - (-9.1638654)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão da Polícia Municipal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7359211)) <= 0.0030 AND ABS(v.longitude - (-9.1638654)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo da Ajuda - Calçada da Tapada', 'Lisboa', 'Lisboa', 'Calçada da Tapada 1300-551',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7061611, -9.1835249, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3355',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3355'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7061611)) <= 0.00063 AND ABS(v.longitude - (-9.1835249)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo da Ajuda - Calçada da Tapada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7061611)) <= 0.0030 AND ABS(v.longitude - (-9.1835249)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo da Junta de Freguesia de Avenidas Novas', 'Lisboa', 'Lisboa', 'Rua Sousa Lopes 1600-207',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7449153, -9.1557428, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3356',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3356'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7449153)) <= 0.00063 AND ABS(v.longitude - (-9.1557428)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo da Junta de Freguesia de Avenidas Novas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7449153)) <= 0.0030 AND ABS(v.longitude - (-9.1557428)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo do Bairro Padre Cruz', 'Lisboa', 'Lisboa', 'Avenida Professor Franscisco da Gama Caeiro 1600-830',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7696029, -9.1880988, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3357',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3357'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7696029)) <= 0.00063 AND ABS(v.longitude - (-9.1880988)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo do Bairro Padre Cruz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7696029)) <= 0.0030 AND ABS(v.longitude - (-9.1880988)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Desportivo Municipal do Alto da Faia', 'Lisboa', 'Lisboa', 'Rua Frederico George 1600-468',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7675507, -9.1698914, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3359',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3359'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7675507)) <= 0.00063 AND ABS(v.longitude - (-9.1698914)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Desportivo Municipal do Alto da Faia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7675507)) <= 0.0030 AND ABS(v.longitude - (-9.1698914)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Municipal da Freguesia de Alvalade', 'Lisboa', 'Lisboa', 'Rua Mem de Sá 1600-168',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7561491, -9.1691243, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3360',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3360'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7561491)) <= 0.00063 AND ABS(v.longitude - (-9.1691243)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Municipal da Freguesia de Alvalade'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7561491)) <= 0.0030 AND ABS(v.longitude - (-9.1691243)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Municipal dos Lóios', 'Lisboa', 'Lisboa', 'Rua Gabriel Constante 1950-139',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7594993, -9.1231514, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3361',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3361'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7594993)) <= 0.00063 AND ABS(v.longitude - (-9.1231514)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Municipal dos Lóios'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7594993)) <= 0.0030 AND ABS(v.longitude - (-9.1231514)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão Municipal Manuel Castelbranco', 'Lisboa', 'Lisboa', 'Rua Natália Correia, 10 1170-270',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7208473, -9.1289262, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3363',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3363'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7208473)) <= 0.00063 AND ABS(v.longitude - (-9.1289262)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão Municipal Manuel Castelbranco'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7208473)) <= 0.0030 AND ABS(v.longitude - (-9.1289262)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Penedo - CML/DAEAC', 'Lisboa', 'Lisboa', 'Estrada do Penedo, Parque Florestal de Monsanto 1300-651',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7205024, -9.1965449, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3365',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3365'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7205024)) <= 0.00063 AND ABS(v.longitude - (-9.1965449)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Penedo - CML/DAEAC'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7205024)) <= 0.0030 AND ABS(v.longitude - (-9.1965449)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Adães Bermudes', 'Lisboa', 'Lisboa', 'Rua Adães Bermudes - Parque da Rua Adães Bermudes 1950-011',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7561155, -9.1217450, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3368',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3368'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7561155)) <= 0.00063 AND ABS(v.longitude - (-9.1217450)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Adães Bermudes'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7561155)) <= 0.0030 AND ABS(v.longitude - (-9.1217450)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Álvaro Braga Júnior', 'Lisboa', 'Lisboa', 'Rua Sousa Bastos 1950-304',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7471571, -9.1079418, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3369',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3369'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7471571)) <= 0.00063 AND ABS(v.longitude - (-9.1079418)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Álvaro Braga Júnior'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7471571)) <= 0.0030 AND ABS(v.longitude - (-9.1079418)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Capitães de Abril', 'Lisboa', 'Lisboa', 'Avenida João Paulo II 1950-159',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7445337, -9.1143628, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3370',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3370'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7445337)) <= 0.00063 AND ABS(v.longitude - (-9.1143628)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Capitães de Abril'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7445337)) <= 0.0030 AND ABS(v.longitude - (-9.1143628)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos da Mata de São Domingos', 'Lisboa', 'Lisboa', 'Mata de São Domingos de Benfica 1500-554',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7438375, -9.1880062, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3371',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3371'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7438375)) <= 0.00063 AND ABS(v.longitude - (-9.1880062)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos da Mata de São Domingos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7438375)) <= 0.0030 AND ABS(v.longitude - (-9.1880062)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos da Rua André Gouveia', 'Lisboa', 'Lisboa', 'Rua André de Gouveia 1750-027',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7800218, -9.1652890, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3372',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3372'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7800218)) <= 0.00063 AND ABS(v.longitude - (-9.1652890)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos da Rua André Gouveia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7800218)) <= 0.0030 AND ABS(v.longitude - (-9.1652890)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos das Salgadas', 'Lisboa', 'Lisboa', 'Avenida Dinah Silveira Queiroz 1950-066',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7398404, -9.1121943, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3373',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3373'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7398404)) <= 0.00063 AND ABS(v.longitude - (-9.1121943)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos das Salgadas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7398404)) <= 0.0030 AND ABS(v.longitude - (-9.1121943)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos do Bairro das Furnas', 'Lisboa', 'Lisboa', 'Largo Maria de Lurdes Pais Gomes 1500-890',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7408725, -9.1741917, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3374',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3374'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7408725)) <= 0.00063 AND ABS(v.longitude - (-9.1741917)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos do Bairro das Furnas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7408725)) <= 0.0030 AND ABS(v.longitude - (-9.1741917)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos do Casalinho da Ajuda', 'Lisboa', 'Lisboa', 'Rua Ciríaco de Cardoso 1300-134',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7102893, -9.1918637, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3375',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3375'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7102893)) <= 0.00063 AND ABS(v.longitude - (-9.1918637)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos do Casalinho da Ajuda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7102893)) <= 0.0030 AND ABS(v.longitude - (-9.1918637)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos do PER 10', 'Lisboa', 'Lisboa', 'Rua Vasco da Gama Fernandes 1750-376',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7809819, -9.1439719, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3376',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3376'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7809819)) <= 0.00063 AND ABS(v.longitude - (-9.1439719)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos do PER 10'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7809819)) <= 0.0030 AND ABS(v.longitude - (-9.1439719)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos dos Alfinetes', 'Lisboa', 'Lisboa', 'Rua Carlos Gil 1950-432',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7380941, -9.1118311, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3377',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3377'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7380941)) <= 0.00063 AND ABS(v.longitude - (-9.1118311)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos dos Alfinetes'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7380941)) <= 0.0030 AND ABS(v.longitude - (-9.1118311)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Doutor Fernando Amado', 'Lisboa', 'Lisboa', 'Praça Doutor Fernando Amado 1950-089',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7470158, -9.1133115, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3378',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3378'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7470158)) <= 0.00063 AND ABS(v.longitude - (-9.1133115)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Doutor Fernando Amado'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7470158)) <= 0.0030 AND ABS(v.longitude - (-9.1133115)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Estevão Vasconcelos', 'Lisboa', 'Lisboa', 'Rua Doutor Estêvão de Vasconcelos #N/A',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7456722, -9.1020214, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3379',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3379'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7456722)) <= 0.00063 AND ABS(v.longitude - (-9.1020214)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Estevão Vasconcelos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7456722)) <= 0.0030 AND ABS(v.longitude - (-9.1020214)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Municipal da Torrinha', 'Lisboa', 'Lisboa', 'Rua Brunilde Judice 1750-045',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7894183, -9.1560158, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3380',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3380'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7894183)) <= 0.00063 AND ABS(v.longitude - (-9.1560158)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Municipal da Torrinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7894183)) <= 0.0030 AND ABS(v.longitude - (-9.1560158)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos Municipal de Telheiras', 'Lisboa', 'Lisboa', 'Rua Professor Dias Amado 1600-626',
       'Ténis', 'Clube de ténis', NULL,
       38.7611507, -9.1700088, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3381',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3381'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7611507)) <= 0.00063 AND ABS(v.longitude - (-9.1700088)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos Municipal de Telheiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7611507)) <= 0.0030 AND ABS(v.longitude - (-9.1700088)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina da Junta de Freguesia das Avenidas Novas', 'Lisboa', 'Lisboa', 'Rua Cardeal Mercier 1600-467',
       'Natação', 'Piscina', NULL,
       38.7422223, -9.1603797, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3382',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3382'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7422223)) <= 0.00063 AND ABS(v.longitude - (-9.1603797)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina da Junta de Freguesia das Avenidas Novas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7422223)) <= 0.0030 AND ABS(v.longitude - (-9.1603797)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal da Penha de França', 'Lisboa', 'Lisboa', 'Calçada do Poço dos Mouros, 2 1170-317',
       'Natação', 'Piscina', NULL,
       38.7302338, -9.1303114, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3386',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3386'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7302338)) <= 0.00063 AND ABS(v.longitude - (-9.1303114)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal da Penha de França'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7302338)) <= 0.0030 AND ABS(v.longitude - (-9.1303114)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de Alvalade - RSB/CML', 'Lisboa', 'Lisboa', 'Avenida Rio de Janeiro, Quartel do RSB 1700-331',
       'Natação', 'Piscina', NULL,
       38.7565512, -9.1405401, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3387',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3387'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7565512)) <= 0.00063 AND ABS(v.longitude - (-9.1405401)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de Alvalade - RSB/CML'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7565512)) <= 0.0030 AND ABS(v.longitude - (-9.1405401)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de Campo de Ourique - Ginásio Clube Português', 'Lisboa', 'Lisboa', 'Rua Correia Teles, 103 A 1350-105',
       'Natação', 'Piscina', NULL,
       38.7179875, -9.1709683, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3389',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3389'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7179875)) <= 0.00063 AND ABS(v.longitude - (-9.1709683)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de Campo de Ourique - Ginásio Clube Português'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7179875)) <= 0.0030 AND ABS(v.longitude - (-9.1709683)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de Santa Clara', 'Lisboa', 'Lisboa', 'Rua Professor Adelino da Palma Carlos 1750-123',
       'Natação', 'Piscina', NULL,
       38.7857420, -9.1602291, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3391',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3391'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7857420)) <= 0.00063 AND ABS(v.longitude - (-9.1602291)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de Santa Clara'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7857420)) <= 0.0030 AND ABS(v.longitude - (-9.1602291)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal de São Vicente', 'Lisboa', 'Lisboa', 'Calçada do Cascão, 39 1100-122',
       'Natação', 'Piscina', NULL,
       38.7143144, -9.1253664, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3394',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3394'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7143144)) <= 0.00063 AND ABS(v.longitude - (-9.1253664)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal de São Vicente'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7143144)) <= 0.0030 AND ABS(v.longitude - (-9.1253664)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal do Alvito', 'Lisboa', 'Lisboa', 'Rua Professor Vieira Natividade 1300-542',
       'Natação', 'Piscina', NULL,
       38.7106466, -9.1812285, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3395',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3395'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7106466)) <= 0.00063 AND ABS(v.longitude - (-9.1812285)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal do Alvito'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7106466)) <= 0.0030 AND ABS(v.longitude - (-9.1812285)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal do Oriente', 'Lisboa', 'Lisboa', 'Rua Câmara Reis 1800-046',
       'Natação', 'Piscina', NULL,
       38.7711213, -9.0995112, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3397',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3397'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7711213)) <= 0.00063 AND ABS(v.longitude - (-9.0995112)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal do Oriente'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7711213)) <= 0.0030 AND ABS(v.longitude - (-9.0995112)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal do Restelo', 'Lisboa', 'Lisboa', 'Rua Antão Gonçalves 1400-015',
       'Natação', 'Piscina', NULL,
       38.7095932, -9.2121129, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3400',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3400'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7095932)) <= 0.00063 AND ABS(v.longitude - (-9.2121129)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal do Restelo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7095932)) <= 0.0030 AND ABS(v.longitude - (-9.2121129)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Piscina Municipal do Vale Fundão - Clube Oriental de Lisboa', 'Lisboa', 'Lisboa', 'Rua Félix Bermudes 1950-292',
       'Natação', 'Piscina', NULL,
       38.7510526, -9.1060201, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3402',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3402'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7510526)) <= 0.00063 AND ABS(v.longitude - (-9.1060201)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Piscina Municipal do Vale Fundão - Clube Oriental de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7510526)) <= 0.0030 AND ABS(v.longitude - (-9.1060201)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pista de Atletismo Municipal Professor Moniz Pereira - CML/DAFD', 'Lisboa', 'Lisboa', 'Rua João Amaral nº 14 1750-423',
       'Atletismo;Judo', 'Complexo desportivo', NULL,
       38.7828478, -9.1535972, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3404',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3404'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7828478)) <= 0.00063 AND ABS(v.longitude - (-9.1535972)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pista de Atletismo Municipal Professor Moniz Pereira - CML/DAFD'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7828478)) <= 0.0030 AND ABS(v.longitude - (-9.1535972)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Alto do Chapeleiro', 'Lisboa', 'Lisboa', 'Rua Alto do Chapeleiro 1750-013',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7913933, -9.1588673, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3411',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3411'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7913933)) <= 0.00063 AND ABS(v.longitude - (-9.1588673)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Alto do Chapeleiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7913933)) <= 0.0030 AND ABS(v.longitude - (-9.1588673)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Avenida Glicínia Quartin', 'Lisboa', 'Lisboa', 'Avenida Glicínia Quartin 1750-428',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7883798, -9.1542535, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3412',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3412'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7883798)) <= 0.00063 AND ABS(v.longitude - (-9.1542535)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Avenida Glicínia Quartin'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7883798)) <= 0.0030 AND ABS(v.longitude - (-9.1542535)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Bairro Novo de Carnide', 'Lisboa', 'Lisboa', 'Rua Guiomar Torresão - Rua Adelaide Félix 1500-025',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7584697, -9.1916412, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3413',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3413'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7584697)) <= 0.00063 AND ABS(v.longitude - (-9.1916412)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Bairro Novo de Carnide'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7584697)) <= 0.0030 AND ABS(v.longitude - (-9.1916412)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Calçada do Poço', 'Lisboa', 'Lisboa', 'Rua Engenheiro Quartin Graça 1750-100',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7825689, -9.1621262, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3415',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3415'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7825689)) <= 0.00063 AND ABS(v.longitude - (-9.1621262)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Calçada do Poço'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7825689)) <= 0.0030 AND ABS(v.longitude - (-9.1621262)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Junta de Freguesia de Alvalade', 'Lisboa', 'Lisboa', 'Rua Teixeira de Pascoais 1700-364',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7474930, -9.1364174, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3417',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3417'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7474930)) <= 0.00063 AND ABS(v.longitude - (-9.1364174)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Junta de Freguesia de Alvalade'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7474930)) <= 0.0030 AND ABS(v.longitude - (-9.1364174)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Junta de Freguesia de Avenidas Novas', 'Lisboa', 'Lisboa', 'Rua Filipe da Mata 1600-069',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7409095, -9.1585490, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3418',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3418'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7409095)) <= 0.00063 AND ABS(v.longitude - (-9.1585490)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Junta de Freguesia de Avenidas Novas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7409095)) <= 0.0030 AND ABS(v.longitude - (-9.1585490)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Quinta do Cabrinha', 'Lisboa', 'Lisboa', 'Avenida de Ceuta - Quinta do Cabrinha 1300-906',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7105089, -9.1748029, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3419',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3419'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7105089)) <= 0.00063 AND ABS(v.longitude - (-9.1748029)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Quinta do Cabrinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7105089)) <= 0.0030 AND ABS(v.longitude - (-9.1748029)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Rua António Abreu', 'Lisboa', 'Lisboa', 'Rua António Abreu 1400-083',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6958177, -9.2164924, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3420',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3420'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6958177)) <= 0.00063 AND ABS(v.longitude - (-9.2164924)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Rua António Abreu'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6958177)) <= 0.0030 AND ABS(v.longitude - (-9.2164924)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Rua da Correnteza', 'Lisboa', 'Lisboa', 'Rua da Correnteza/Travessa do Galvão 1400-328',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7031442, -9.2043116, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3421',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3421'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7031442)) <= 0.00063 AND ABS(v.longitude - (-9.2043116)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Rua da Correnteza'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7031442)) <= 0.0030 AND ABS(v.longitude - (-9.2043116)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua Dom Pedro Cristo', 'Lisboa', 'Lisboa', 'Rua Dom Pedro Cristo 1700-135',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7523694, -9.1352833, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3422',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3422'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7523694)) <= 0.00063 AND ABS(v.longitude - (-9.1352833)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua Dom Pedro Cristo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7523694)) <= 0.0030 AND ABS(v.longitude - (-9.1352833)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo das Galinheiras', 'Lisboa', 'Lisboa', 'Estrada Militar 1750-194',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7903852, -9.1503665, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3424',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3424'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7903852)) <= 0.00063 AND ABS(v.longitude - (-9.1503665)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo das Galinheiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7903852)) <= 0.0030 AND ABS(v.longitude - (-9.1503665)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bairro da Horta Nova', 'Lisboa', 'Lisboa', 'Rua Sidónio Serpa 1600-476',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7667907, -9.1807718, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3425',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3425'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7667907)) <= 0.00063 AND ABS(v.longitude - (-9.1807718)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Bairro da Horta Nova'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7667907)) <= 0.0030 AND ABS(v.longitude - (-9.1807718)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Bairro Padre Cruz', 'Lisboa', 'Lisboa', 'Estrada da Circunvalação 1600-639',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7704000, -9.1937283, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3427',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3427'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7704000)) <= 0.00063 AND ABS(v.longitude - (-9.1937283)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Bairro Padre Cruz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7704000)) <= 0.0030 AND ABS(v.longitude - (-9.1937283)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo do Ralis', 'Lisboa', 'Lisboa', 'Avenida Doutor Alfredo Bensaude 1849-031',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7815031, -9.1210129, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3428',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3428'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7815031)) <= 0.00063 AND ABS(v.longitude - (-9.1210129)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo do Ralis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7815031)) <= 0.0030 AND ABS(v.longitude - (-9.1210129)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pavilhão do Vale Fundão', 'Lisboa', 'Lisboa', 'Rua Manuel Teixeira Gomes 1950-186',
       'Futsal;Basquetebol;Voleibol;Andebol', 'Pavilhão desportivo', NULL,
       38.7505598, -9.1105253, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3429',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3429'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7505598)) <= 0.00063 AND ABS(v.longitude - (-9.1105253)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pavilhão do Vale Fundão'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7505598)) <= 0.0030 AND ABS(v.longitude - (-9.1105253)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Maria Veleda', 'Lisboa', 'Lisboa', 'Rua Maria Veleda, Quinta da Luz 1500-441',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7588135, -9.1862807, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3430',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3430'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7588135)) <= 0.00063 AND ABS(v.longitude - (-9.1862807)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Maria Veleda'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7588135)) <= 0.0030 AND ABS(v.longitude - (-9.1862807)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Municipal das Torres Edifer', 'Lisboa', 'Lisboa', 'Azinhaga da Cidade 1750-065',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7810589, -9.1582030, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3431',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3431'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7810589)) <= 0.00063 AND ABS(v.longitude - (-9.1582030)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Municipal das Torres Edifer'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7810589)) <= 0.0030 AND ABS(v.longitude - (-9.1582030)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Municipal de Caselas', 'Lisboa', 'Lisboa', 'Rua da Cruz a Caselas 1400-083',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7159088, -9.2125723, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3432',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3432'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7159088)) <= 0.00063 AND ABS(v.longitude - (-9.2125723)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Municipal de Caselas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7159088)) <= 0.0030 AND ABS(v.longitude - (-9.2125723)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Municipal do Rio Seco', 'Lisboa', 'Lisboa', 'Rua Eduardo Bairrada 1300-661',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7067156, -9.1938526, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3433',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3433'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7067156)) <= 0.00063 AND ABS(v.longitude - (-9.1938526)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Municipal do Rio Seco'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7067156)) <= 0.0030 AND ABS(v.longitude - (-9.1938526)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Rua Raul Rêgo', 'Lisboa', 'Lisboa', 'Rua Raúl Rego 1750-424',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7834981, -9.1519557, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3434',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3434'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7834981)) <= 0.00063 AND ABS(v.longitude - (-9.1519557)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Rua Raul Rêgo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7834981)) <= 0.0030 AND ABS(v.longitude - (-9.1519557)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Salgueiro Maia', 'Lisboa', 'Lisboa', 'Rua Sousa Loreiro - Polidesportivo Salgueiro Maia 1500-553',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7451137, -9.1835093, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3435',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3435'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7451137)) <= 0.00063 AND ABS(v.longitude - (-9.1835093)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Salgueiro Maia'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7451137)) <= 0.0030 AND ABS(v.longitude - (-9.1835093)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Urbano Martins (Passadiço)', 'Lisboa', 'Lisboa', 'Calçada de Santo António, 3 1150-313',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7227186, -9.1436895, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3436',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3436'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7227186)) <= 0.00063 AND ABS(v.longitude - (-9.1436895)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Urbano Martins (Passadiço)'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7227186)) <= 0.0030 AND ABS(v.longitude - (-9.1436895)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivos do Bairro da GNR', 'Lisboa', 'Lisboa', 'Rua Vale Formoso de Cima 1950-276',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7528374, -9.1042997, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3437',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3437'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7528374)) <= 0.00063 AND ABS(v.longitude - (-9.1042997)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivos do Bairro da GNR'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7528374)) <= 0.0030 AND ABS(v.longitude - (-9.1042997)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Praça Cottinelli Telmo', 'Lisboa', 'Lisboa', 'Praça Cottinelli Telmo 1800-153',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7795804, -9.1193806, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3442',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3442'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7795804)) <= 0.00063 AND ABS(v.longitude - (-9.1193806)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Praça Cottinelli Telmo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7795804)) <= 0.0030 AND ABS(v.longitude - (-9.1193806)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pump Track', 'Lisboa', 'Lisboa', 'Passeio do Sapal 1990-221',
       'Ciclismo', 'Complexo desportivo', NULL,
       38.7870849, -9.0916808, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3447',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3447'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7870849)) <= 0.00063 AND ABS(v.longitude - (-9.0916808)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pump Track'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7870849)) <= 0.0030 AND ABS(v.longitude - (-9.0916808)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Queen Elizabeth School', 'Lisboa', 'Lisboa', 'Rua Filipe Magalhães, 1 1700-194',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7514183, -9.1346692, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3449',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3449'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7514183)) <= 0.00063 AND ABS(v.longitude - (-9.1346692)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Queen Elizabeth School'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7514183)) <= 0.0030 AND ABS(v.longitude - (-9.1346692)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'RacketsPro - Saldanha', 'Lisboa', 'Lisboa', 'Rua Engenheiro Vieira da Silva 1050-105',
       'Padel', 'Clube de padel', NULL,
       38.7313761, -9.1446072, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3456',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3456'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7313761)) <= 0.00063 AND ABS(v.longitude - (-9.1446072)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('RacketsPro - Saldanha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7313761)) <= 0.0030 AND ABS(v.longitude - (-9.1446072)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'RacketsPro EUL', 'Lisboa', 'Lisboa', 'Azinhaga Galhardas, 18 1600-097',
       'Ténis;Padel', 'Complexo desportivo', NULL,
       38.7533173, -9.1638124, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3461',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3461'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7533173)) <= 0.00063 AND ABS(v.longitude - (-9.1638124)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('RacketsPro EUL'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7533173)) <= 0.0030 AND ABS(v.longitude - (-9.1638124)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Real Colégio de Portugal', 'Lisboa', 'Lisboa', 'Rua Direita ao Paço do Lumiar, 7 a 9 1600-435',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7728650, -9.1712149, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3480',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3480'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7728650)) <= 0.00063 AND ABS(v.longitude - (-9.1712149)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Real Colégio de Portugal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7728650)) <= 0.0030 AND ABS(v.longitude - (-9.1712149)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Recinto de Jogo da Quinta do Chalé', 'Lisboa', 'Lisboa', 'Rua José do Patrocínio 1950-164',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7431091, -9.1069537, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3483',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3483'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7431091)) <= 0.00063 AND ABS(v.longitude - (-9.1069537)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Recinto de Jogo da Quinta do Chalé'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7431091)) <= 0.0030 AND ABS(v.longitude - (-9.1069537)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua Alferes Barrilaro Ruas', 'Lisboa', 'Lisboa', 'Rua Alferes Barrilaro Ruas (perto do nº 22) 1800-008',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7759326, -9.1129884, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3485',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3485'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7759326)) <= 0.00063 AND ABS(v.longitude - (-9.1129884)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua Alferes Barrilaro Ruas'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7759326)) <= 0.0030 AND ABS(v.longitude - (-9.1129884)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua Capitão Tenente Oliveira e Carmo', 'Lisboa', 'Lisboa', 'Rua Capitão Oliveira Carmo 1800-050',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7748553, -9.1131577, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3488',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3488'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7748553)) <= 0.00063 AND ABS(v.longitude - (-9.1131577)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua Capitão Tenente Oliveira e Carmo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7748553)) <= 0.0030 AND ABS(v.longitude - (-9.1131577)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua Cidade de Moçâmedes', 'Lisboa', 'Lisboa', 'Rua Cidade de Moçâmedes 1800-104',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7645036, -9.1136674, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3491',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3491'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7645036)) <= 0.00063 AND ABS(v.longitude - (-9.1136674)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua Cidade de Moçâmedes'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7645036)) <= 0.0030 AND ABS(v.longitude - (-9.1136674)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua do Dondo', 'Lisboa', 'Lisboa', 'Rua do Dondo 1800-167',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7598387, -9.1106278, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3495',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3495'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7598387)) <= 0.00063 AND ABS(v.longitude - (-9.1106278)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua do Dondo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7598387)) <= 0.0030 AND ABS(v.longitude - (-9.1106278)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rua Rio Mondego', 'Lisboa', 'Lisboa', 'Rua Rio Mondego 1600-727',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7709532, -9.1913612, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3505',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3505'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7709532)) <= 0.00063 AND ABS(v.longitude - (-9.1913612)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Rua Rio Mondego'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7709532)) <= 0.0030 AND ABS(v.longitude - (-9.1913612)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Salesianos de Lisboa - Oficinas de São José - Fundação Salesianos', 'Lisboa', 'Lisboa', 'Praça São João Bosco, 34 1399-007',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Ténis;Natação', 'Complexo desportivo', NULL,
       38.7140136, -9.1676949, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3509',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3509'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7140136)) <= 0.00063 AND ABS(v.longitude - (-9.1676949)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Salesianos de Lisboa - Oficinas de São José - Fundação Salesianos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7140136)) <= 0.0030 AND ABS(v.longitude - (-9.1676949)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Santana Futebol Clube', 'Lisboa', 'Lisboa', 'Calçada dos Sete Moinhos, SFC 1070-022',
       'Futsal;Basquetebol;Boxe', 'Complexo desportivo', NULL,
       38.7250137, -9.1685547, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3520',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3520'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7250137)) <= 0.00063 AND ABS(v.longitude - (-9.1685547)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Santana Futebol Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7250137)) <= 0.0030 AND ABS(v.longitude - (-9.1685547)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'São João Ténis Clube - Escola de Ténis', 'Lisboa', 'Lisboa', 'Rua Lúcio de Azevedo, 20 1600-149',
       'Ténis;Padel', 'Complexo desportivo', NULL,
       38.7538090, -9.1701551, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3525',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3525'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7538090)) <= 0.00063 AND ABS(v.longitude - (-9.1701551)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('São João Ténis Clube - Escola de Ténis'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7538090)) <= 0.0030 AND ABS(v.longitude - (-9.1701551)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Serviços de Acção Social do Instituto Politécnico de Lisboa', 'Lisboa', 'Lisboa', 'Estrada de Benfica, 529 - Campus de Benfica do Instituto Politécnico de Lisboa 1549-020',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7469509, -9.1945598, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3536',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3536'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7469509)) <= 0.00063 AND ABS(v.longitude - (-9.1945598)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Serviços de Acção Social do Instituto Politécnico de Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7469509)) <= 0.0030 AND ABS(v.longitude - (-9.1945598)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Serviços Sociais Caixa Geral de Depósitos', 'Lisboa', 'Lisboa', 'Avenida João XXI, 63 - Piso 2 1000-300',
       'Futsal;Basquetebol;Voleibol;Andebol;Judo;Squash', 'Complexo desportivo', NULL,
       38.7414587, -9.1407979, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3537',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3537'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7414587)) <= 0.00063 AND ABS(v.longitude - (-9.1407979)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Serviços Sociais Caixa Geral de Depósitos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7414587)) <= 0.0030 AND ABS(v.longitude - (-9.1407979)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sociedade de Instrução e Beneficência "A Voz do Operário"', 'Lisboa', 'Lisboa', 'Rua Voz do Operário, 13 1100-620',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7155134, -9.1295782, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3555',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3555'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7155134)) <= 0.00063 AND ABS(v.longitude - (-9.1295782)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sociedade de Instrução e Beneficência "A Voz do Operário"'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7155134)) <= 0.0030 AND ABS(v.longitude - (-9.1295782)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sociedade de Recreio Ajuda Clube', 'Lisboa', 'Lisboa', 'Rua do Cruzeiro, 237 1300-170',
       'Boxe', 'Complexo desportivo', NULL,
       38.7091578, -9.1955198, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3560',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3560'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7091578)) <= 0.00063 AND ABS(v.longitude - (-9.1955198)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sociedade de Recreio Ajuda Clube'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7091578)) <= 0.0030 AND ABS(v.longitude - (-9.1955198)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sociedade Musical Instrução Libertada', 'Lisboa', 'Lisboa', 'Calçada do Galvão, 51 1400-164',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7017089, -9.2027380, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3570',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3570'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7017089)) <= 0.00063 AND ABS(v.longitude - (-9.2027380)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sociedade Musical Instrução Libertada'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7017089)) <= 0.0030 AND ABS(v.longitude - (-9.2027380)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Solinca Health & Fitness Club - Colombo', 'Lisboa', 'Lisboa', 'Avenida Lusíada, Centro Colombo, Loja A - 201 1500-392',
       'Natação', 'Piscina', NULL,
       38.7542150, -9.1893845, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3573',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3573'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7542150)) <= 0.00063 AND ABS(v.longitude - (-9.1893845)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Solinca Health & Fitness Club - Colombo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7542150)) <= 0.0030 AND ABS(v.longitude - (-9.1893845)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Solinca Health & Fitness Club - Vasco da Gama', 'Lisboa', 'Lisboa', 'Avenida Dom João II, Lote 1.05.02 (Centro Vasco da Gama) 1990-094',
       'Natação', 'Piscina', NULL,
       38.7673656, -9.0969116, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3583',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3583'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7673656)) <= 0.00063 AND ABS(v.longitude - (-9.0969116)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Solinca Health & Fitness Club - Vasco da Gama'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7673656)) <= 0.0030 AND ABS(v.longitude - (-9.0969116)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Solinca Health and Fitness Club - Laranjeiras', 'Lisboa', 'Lisboa', 'Rua Joaquim Rocha Cabral, 26 1600-075',
       'Natação', 'Piscina', NULL,
       38.7546802, -9.1667422, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3588',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3588'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7546802)) <= 0.00063 AND ABS(v.longitude - (-9.1667422)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Solinca Health and Fitness Club - Laranjeiras'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7546802)) <= 0.0030 AND ABS(v.longitude - (-9.1667422)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Solinca Lumiar', 'Lisboa', 'Lisboa', 'Avenida David Mourão-Ferreira 62 1750-204',
       'Natação', 'Piscina', NULL,
       38.7749005, -9.1559614, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3595',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3595'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7749005)) <= 0.00063 AND ABS(v.longitude - (-9.1559614)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Solinca Lumiar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7749005)) <= 0.0030 AND ABS(v.longitude - (-9.1559614)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Algés e Dafundo - Secção de Vela', 'Lisboa', 'Lisboa', 'Doca de Belém 1400-038',
       'Vela;Canoagem', 'Complexo desportivo', NULL,
       38.6951267, -9.2022069, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3601',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3601'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6951267)) <= 0.00063 AND ABS(v.longitude - (-9.2022069)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sport Algés e Dafundo - Secção de Vela'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6951267)) <= 0.0030 AND ABS(v.longitude - (-9.2022069)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Futebol Palmense', 'Lisboa', 'Lisboa', 'Rua Direita de Palma 1600-053',
       'Futebol;Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7489128, -9.1700666, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3602',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3602'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7489128)) <= 0.00063 AND ABS(v.longitude - (-9.1700666)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sport Futebol Palmense'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7489128)) <= 0.0030 AND ABS(v.longitude - (-9.1700666)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Lisboa e Benfica', 'Lisboa', 'Lisboa', 'Avenida General Norton de Matos, Estádio Sport Lisboa e Benfica 1500-313',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol;Natação;Ginástica', 'Complexo desportivo', NULL,
       38.7519502, -9.1833432, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3606',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3606'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7519502)) <= 0.00063 AND ABS(v.longitude - (-9.1833432)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sport Lisboa e Benfica'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7519502)) <= 0.0030 AND ABS(v.longitude - (-9.1833432)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Lisboa e Olivais', 'Lisboa', 'Lisboa', 'Rua Almada Negreiros - Campo Branca Lucas 1800-070',
       'Futebol;Futsal;Basquetebol;Voleibol;Andebol', 'Complexo desportivo', NULL,
       38.7623775, -9.1082488, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3619',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3619'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7623775)) <= 0.00063 AND ABS(v.longitude - (-9.1082488)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sport Lisboa e Olivais'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7623775)) <= 0.0030 AND ABS(v.longitude - (-9.1082488)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sporting Clube de Portugal', 'Lisboa', 'Lisboa', 'Rua Professor Fernando da Fonseca 1600-616',
       'Futebol;Tiro com Arco;Futsal;Basquetebol;Voleibol;Andebol;Natação;Judo;Ginástica', 'Complexo desportivo', NULL,
       38.7618483, -9.1600591, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3621',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3621'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7618483)) <= 0.00063 AND ABS(v.longitude - (-9.1600591)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Sporting Clube de Portugal'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7618483)) <= 0.0030 AND ABS(v.longitude - (-9.1600591)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Supera Areeiro - Complexo Desportivo Municipal Supera Areeiro', 'Lisboa', 'Lisboa', 'Avenida de Roma, 26 AA 1250-017',
       'Natação', 'Piscina', NULL,
       38.7446860, -9.1383476, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3648',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3648'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7446860)) <= 0.00063 AND ABS(v.longitude - (-9.1383476)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Supera Areeiro - Complexo Desportivo Municipal Supera Areeiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7446860)) <= 0.0030 AND ABS(v.longitude - (-9.1383476)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ténis 4 you', 'Lisboa', 'Lisboa', 'Rua E1 à Quinta Dos Alcoutins ,Lote 52 - 1 1600-270',
       'Ténis', 'Clube de ténis', NULL,
       38.7802831, -9.1715873, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3657',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3657'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7802831)) <= 0.00063 AND ABS(v.longitude - (-9.1715873)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Ténis 4 you'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7802831)) <= 0.0030 AND ABS(v.longitude - (-9.1715873)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Universidade Lusófona de Humanidades e Tecnologias', 'Lisboa', 'Lisboa', 'Campo Grande, 376 1749-024',
       'Futsal;Basquetebol;Voleibol;Andebol;Judo', 'Complexo desportivo', NULL,
       38.7590992, -9.1519918, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3674',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3674'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7590992)) <= 0.00063 AND ABS(v.longitude - (-9.1519918)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Universidade Lusófona de Humanidades e Tecnologias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7590992)) <= 0.0030 AND ABS(v.longitude - (-9.1519918)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Urban Soccer Lisboa', 'Lisboa', 'Lisboa', 'Estrada do Outeiro, Parque Florestal de Monsanto 1500-472',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7288484, -9.2064098, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3678',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3678'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7288484)) <= 0.00063 AND ABS(v.longitude - (-9.2064098)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Urban Soccer Lisboa'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7288484)) <= 0.0030 AND ABS(v.longitude - (-9.2064098)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vertigo Rocódromo', 'Lisboa', 'Lisboa', 'Avenida Infante Dom Henrique, Edifício Beira Rio - Fracção S 1950-408',
       'Escalada', 'Complexo desportivo', NULL,
       38.7398792, -9.1021142, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3686',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3686'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7398792)) <= 0.00063 AND ABS(v.longitude - (-9.1021142)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Vertigo Rocódromo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7398792)) <= 0.0030 AND ABS(v.longitude - (-9.1021142)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vitória Clube de Lisboa - Campo de Jogos', 'Lisboa', 'Lisboa', 'Calçada do Carrascal - Campo de Jogos 1900-133',
       'Futebol', 'Complexo desportivo', NULL,
       38.7346885, -9.1198241, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3688',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3688'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7346885)) <= 0.00063 AND ABS(v.longitude - (-9.1198241)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Vitória Clube de Lisboa - Campo de Jogos'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7346885)) <= 0.0030 AND ABS(v.longitude - (-9.1198241)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'W Padel Country Club', 'Lisboa', 'Lisboa', 'Estrada do Outeiro, Parque Florestal de Monsanto 1500-472',
       'Padel', 'Clube de padel', NULL,
       38.7291101, -9.2062704, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3694',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3694'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7291101)) <= 0.00063 AND ABS(v.longitude - (-9.2062704)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('W Padel Country Club'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7291101)) <= 0.0030 AND ABS(v.longitude - (-9.2062704)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo da Praceta do Chinquilho', 'Lisboa', 'Lisboa', 'Praceta do Chinquilho 1300-130',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7018579, -9.1912974, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3704',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3704'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7018579)) <= 0.00063 AND ABS(v.longitude - (-9.1912974)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo da Praceta do Chinquilho'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7018579)) <= 0.0030 AND ABS(v.longitude - (-9.1912974)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Espassus 3 G', 'Lisboa', 'Lisboa', 'Rua dos Táxis Palhinhas 1601-766',
       'Ténis;Natação', 'Complexo desportivo', NULL,
       38.7605993, -9.1864691, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3705',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3705'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7605993)) <= 0.00063 AND ABS(v.longitude - (-9.1864691)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Espassus 3 G'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7605993)) <= 0.0030 AND ABS(v.longitude - (-9.1864691)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Minicampo da Rua da Quinta do Loureiro', 'Lisboa', 'Lisboa', 'Rua Quinta do Loureiro 1350-410',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7162376, -9.1744356, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3711',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3711'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7162376)) <= 0.00063 AND ABS(v.longitude - (-9.1744356)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Minicampo da Rua da Quinta do Loureiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7162376)) <= 0.0030 AND ABS(v.longitude - (-9.1744356)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Basquetebol da Frente Ribeirinha', 'Lisboa', 'Lisboa', 'Frente Ribeirinha',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.6984601, -9.1796655, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3712',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3712'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6984601)) <= 0.00063 AND ABS(v.longitude - (-9.1796655)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Basquetebol da Frente Ribeirinha'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6984601)) <= 0.0030 AND ABS(v.longitude - (-9.1796655)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Jogos Bairro Portugal Novo', 'Lisboa', 'Lisboa', 'Largo Roque Laia 1900-863',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7402003, -9.1257747, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3718',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3718'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7402003)) <= 0.00063 AND ABS(v.longitude - (-9.1257747)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Jogos Bairro Portugal Novo'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7402003)) <= 0.0030 AND ABS(v.longitude - (-9.1257747)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos da Serafina', 'Lisboa', 'Lisboa', 'Rua Carlos Conde 1070-907',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7299157, -9.1733466, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3722',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3722'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7299157)) <= 0.00063 AND ABS(v.longitude - (-9.1733466)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos da Serafina'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7299157)) <= 0.0030 AND ABS(v.longitude - (-9.1733466)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campos de Street Basket da Doca do Espanhol', 'Lisboa', 'Lisboa', 'Doca do Espanhol',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7023430, -9.1692444, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3725',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3725'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7023430)) <= 0.00063 AND ABS(v.longitude - (-9.1692444)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campos de Street Basket da Doca do Espanhol'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7023430)) <= 0.0030 AND ABS(v.longitude - (-9.1692444)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Mini Basquetebol da Rua Professor Vieira de Almeida', 'Lisboa', 'Lisboa', 'Rua Professor Vieira de Almeida 1600-664',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7622890, -9.1665605, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3729',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3729'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7622890)) <= 0.00063 AND ABS(v.longitude - (-9.1665605)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Mini Basquetebol da Rua Professor Vieira de Almeida'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7622890)) <= 0.0030 AND ABS(v.longitude - (-9.1665605)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Campo de Mini Basquetebol da Rua Professor Armindo Monteiro', 'Lisboa', 'Lisboa', 'Rua Professor Armindo Monteiro 1600-594',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7610204, -9.1632180, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3730',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3730'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7610204)) <= 0.00063 AND ABS(v.longitude - (-9.1632180)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Campo de Mini Basquetebol da Rua Professor Armindo Monteiro'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7610204)) <= 0.0030 AND ABS(v.longitude - (-9.1632180)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos da Rua Almada Negreiros', 'Lisboa', 'Lisboa', 'Rua Almada Negreiros 1800-012',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7608793, -9.1055191, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3732',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3732'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7608793)) <= 0.00063 AND ABS(v.longitude - (-9.1055191)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos da Rua Almada Negreiros'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7608793)) <= 0.0030 AND ABS(v.longitude - (-9.1055191)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Desportivo Norte', 'Lisboa', 'Lisboa', 'Rua Chen He 1990-362',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7876513, -9.0926992, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3735',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3735'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7876513)) <= 0.00063 AND ABS(v.longitude - (-9.0926992)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Parque Desportivo Norte'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7876513)) <= 0.0030 AND ABS(v.longitude - (-9.0926992)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'MiniBasket do Parque Urbano  do Vale da Ameixoeira', 'Lisboa', 'Lisboa', 'Rua Professor Adelino da Palma Carlos 1750 - 123',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7852032, -9.1604605, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3736',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3736'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7852032)) <= 0.00063 AND ABS(v.longitude - (-9.1604605)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('MiniBasket do Parque Urbano  do Vale da Ameixoeira'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7852032)) <= 0.0030 AND ABS(v.longitude - (-9.1604605)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escala 25', 'Lisboa', 'Lisboa', 'Pilar 7 da, Avenida da Índia, Ac. Pte. 25 de Abril 1349-028',
       'Escalada', 'Complexo desportivo', NULL,
       38.7000078, -9.1795527, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3738',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3738'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7000078)) <= 0.00063 AND ABS(v.longitude - (-9.1795527)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Escala 25'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7000078)) <= 0.0030 AND ABS(v.longitude - (-9.1795527)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Balance Clube - Lumiar', 'Lisboa', 'Lisboa', 'Rua Amilcar Cabral, 40 B 1750-020',
       'Natação', 'Piscina', NULL,
       38.7648640, -9.1517614, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3743',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3743'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7648640)) <= 0.00063 AND ABS(v.longitude - (-9.1517614)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Balance Clube - Lumiar'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7648640)) <= 0.0030 AND ABS(v.longitude - (-9.1517614)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pequeno Campo de Jogos do Bairro do Calhariz Velho', 'Lisboa', 'Lisboa', 'Rua da Vila de São Martinho 1500-623',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7442585, -9.2012557, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3747',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3747'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7442585)) <= 0.00063 AND ABS(v.longitude - (-9.2012557)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Pequeno Campo de Jogos do Bairro do Calhariz Velho'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7442585)) <= 0.0030 AND ABS(v.longitude - (-9.2012557)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Academia Life Club Estádio da Luz', 'Lisboa', 'Lisboa', 'Avenida Eusébio da Silva Ferreira, 25 A 1500-501',
       'Natação', 'Piscina', NULL,
       38.7517413, -9.1844422, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3761',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3761'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7517413)) <= 0.00063 AND ABS(v.longitude - (-9.1844422)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Academia Life Club Estádio da Luz'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7517413)) <= 0.0030 AND ABS(v.longitude - (-9.1844422)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Colégio Universitário Pio XXI', 'Lisboa', 'Lisboa', 'Avenida das Forças Armadas 1600-083',
       'Futsal;Basquetebol;Ténis', 'Complexo desportivo', NULL,
       38.7490326, -9.1526762, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3771',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3771'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7490326)) <= 0.00063 AND ABS(v.longitude - (-9.1526762)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Colégio Universitário Pio XXI'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7490326)) <= 0.0030 AND ABS(v.longitude - (-9.1526762)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Polidesportivo Marquês de Abrantes', 'Lisboa', 'Lisboa', 'Rua Eduarda Lapa 1950-360',
       'Futsal;Basquetebol', 'Complexo desportivo', NULL,
       38.7409312, -9.1089556, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3830',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3830'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7409312)) <= 0.00063 AND ABS(v.longitude - (-9.1089556)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Polidesportivo Marquês de Abrantes'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7409312)) <= 0.0030 AND ABS(v.longitude - (-9.1089556)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Joaquim Chave Fitness', 'Lisboa', 'Lisboa', 'Rua Luís Braille, 120 E 1400-405',
       'Natação', 'Piscina', NULL,
       38.6957818, -9.2134859, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3845',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3845'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.6957818)) <= 0.00063 AND ABS(v.longitude - (-9.2134859)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Joaquim Chave Fitness'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.6957818)) <= 0.0030 AND ABS(v.longitude - (-9.2134859)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'NAVE Padel & Pickleball', 'Lisboa', 'Lisboa', 'Rua do Açúcar, 19 10950-006',
       'Padel;Futsal', 'Complexo desportivo', NULL,
       38.7414953, -9.1017812, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3859',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3859'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7414953)) <= 0.00063 AND ABS(v.longitude - (-9.1017812)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('NAVE Padel & Pickleball'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7414953)) <= 0.0030 AND ABS(v.longitude - (-9.1017812)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Factory', 'Lisboa', 'Lisboa', 'Rua Passeio dos Heróis do Mar, 14 1990-528',
       'Padel', 'Clube de padel', NULL,
       38.7860377, -9.0933504, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3869',
       FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3869'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7860377)) <= 0.00063 AND ABS(v.longitude - (-9.0933504)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Padel Factory'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7860377)) <= 0.0030 AND ABS(v.longitude - (-9.0933504)) <= 0.0040)))
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, environment_type,
   latitude, longitude, website, contact, information_source, source_reference,
   indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lemon Fit Olaias', 'Lisboa', 'Lisboa', 'Rua  Robalo Gouveia, 2 1900-392',
       'Natação;Padel;Ténis', 'Complexo desportivo', NULL,
       38.7414835, -9.1225258, NULL, NULL, 'OFFICIAL', 'CML:DESPORTO:3882',
       TRUE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference='CML:DESPORTO:3882'
     OR (v.latitude IS NOT NULL AND v.longitude IS NOT NULL
         AND ABS(v.latitude - (38.7414835)) <= 0.00063 AND ABS(v.longitude - (-9.1225258)) <= 0.00080)
     OR (LOWER(BTRIM(v.name))=LOWER(BTRIM('Lemon Fit Olaias'))
         AND (v.latitude IS NULL OR (ABS(v.latitude - (38.7414835)) <= 0.0030 AND ABS(v.longitude - (-9.1225258)) <= 0.0040)))
);
