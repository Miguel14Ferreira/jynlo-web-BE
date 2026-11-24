-- Clubs Jynlo contacted about bookings: name, address, phone and email as each club publishes them on its own site.
-- Map point from OpenStreetMap Nominatim (street, postcode or name precision); never marked OFFICIAL.

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Parque Desportivo do União Desportiva Cultural e Social do Quintanilho (UDCSQ)', 'Vialonga', 'Lisboa', NULL, 'Ténis;Ténis de mesa', 'Complexo desportivo',
       38.8639311, -9.1007187, 'https://sites.google.com/site/udcsquintanilho/', 'udcsquintanilho@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:sites.google.com/site/udcsquintanilho', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:sites.google.com/site/udcsquintanilho'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Parque Desportivo do União Desportiva Cultural e Social do Quintanilho (UDCSQ)'))
     OR (ABS(v.latitude - (38.8639311)) <= 0.0004 AND ABS(v.longitude - (-9.1007187)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Real Clube de Campo Dom Carlos I Tennis', 'Cascais', 'Lisboa', 'Roundabout D. Carlos I 7 2750 - 057', 'Ténis', 'Clube de ténis',
       38.7203611, -9.4716056, 'https://www.clubedcarlos.pt/', '218 212 475 · geral@clubedcarlos.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:clubedcarlos.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:clubedcarlos.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Real Clube de Campo Dom Carlos I Tennis'))
     OR (ABS(v.latitude - (38.7203611)) <= 0.0004 AND ABS(v.longitude - (-9.4716056)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Dulupadel', 'Vila Real', 'Vila Real', 'Rua das Regueiras do Bôco 35A 5000-429', 'Padel', 'Clube de padel',
       41.3078869, -7.7211283, 'https://www.dulupadel.com/', 'geral@dulupadel.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:dulupadel.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:dulupadel.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Dulupadel'))
     OR (ABS(v.latitude - (41.3078869)) <= 0.0004 AND ABS(v.longitude - (-7.7211283)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'W Padel & Ténis Parque', 'Barreiro', 'Setúbal', 'Av. Escola dos Fuzileiros Navais 2830 2830-046', 'Padel;Ténis', 'Clube de ténis e padel',
       38.6491072, -9.0589922, NULL, '930 941 089 · geral@wpadelparque.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:w-padel-ténis-parque', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:w-padel-ténis-parque'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('W Padel & Ténis Parque'))
     OR (ABS(v.latitude - (38.6491072)) <= 0.0004 AND ABS(v.longitude - (-9.0589922)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Padel Caldas', 'Caldas da Rainha', 'Leiria', NULL, 'Padel', 'Clube de padel',
       39.4270186, -9.1583761, 'https://www.clubepadelcaldas.pt/', 'geralcpcaldas@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:clubepadelcaldas.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:clubepadelcaldas.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Padel Caldas'))
     OR (ABS(v.latitude - (39.4270186)) <= 0.0004 AND ABS(v.longitude - (-9.1583761)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sport Club Alberto de Sousa, Parque da Gandara', 'Arcozelo, Vila Nova de Gaia', 'Porto', 'Avenida Álvares Cabral 222 4410-322', 'Futebol;Ténis', 'Complexo desportivo',
       41.086146, -8.6540692, 'https://parquedagandara.com/', '227 625 755 · secretaria@parquedagandara.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:parquedagandara.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:parquedagandara.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Sport Club Alberto de Sousa, Parque da Gandara'))
     OR (ABS(v.latitude - (41.086146)) <= 0.0004 AND ABS(v.longitude - (-8.6540692)) <= 0.0005
         AND v.sports ILIKE '%Futebol%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'São João Tenis & Padel', 'Lisboa', 'Lisboa', 'Rua Lúcio de Azevedo 20 1600-149', 'Padel;Ténis', 'Clube de ténis e padel',
       38.7538302, -9.170362, 'https://saojoaoclube.pt', '917 102 210 · saojoaotc@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:saojoaoclube.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:saojoaoclube.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('São João Tenis & Padel'))
     OR (ABS(v.latitude - (38.7538302)) <= 0.0004 AND ABS(v.longitude - (-9.170362)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube de Ténis da Amadora', 'Amadora', 'Lisboa', NULL, 'Ténis', 'Clube de ténis',
       38.7492638, -9.2364601, 'https://www.ctamadora.pt/', 'ctamadora@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:ctamadora.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:ctamadora.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube de Ténis da Amadora'))
     OR (ABS(v.latitude - (38.7492638)) <= 0.0004 AND ABS(v.longitude - (-9.2364601)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Estoril Tennis Club', 'Estoril', 'Lisboa', 'Av. Condes de Barcelona, 2765-470 Estoril', 'Ténis', 'Clube de ténis',
       38.7138225, -9.3910981, 'http://www.ctestoril.pt/', '912 268 795 · escola@cte.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:ctestoril.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:ctestoril.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Estoril Tennis Club'))
     OR (ABS(v.latitude - (38.7138225)) <= 0.0004 AND ABS(v.longitude - (-9.3910981)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Atlético De Queluz', 'Queluz', 'Lisboa', 'R. Dom Pedro IV 7, 2745-190 Queluz', 'Padel', 'Clube de padel',
       38.756416, -9.2530493, 'https://www.caqueluz.pt/', '962 260 887 · secretaria@caqueluz.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:caqueluz.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:caqueluz.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Atlético De Queluz'))
     OR (ABS(v.latitude - (38.756416)) <= 0.0004 AND ABS(v.longitude - (-9.2530493)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ace Team - Ténis & Padel', 'Amadora', 'Lisboa', 'R. dos Eucaliptos, 2610-069 Amadora', 'Padel;Ténis', 'Clube de ténis e padel',
       38.7403013, -9.2239276, 'https://clubeaceteam.com/', '927 510 574 · ctalfragide@ace-team.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:clubeaceteam.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:clubeaceteam.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Ace Team - Ténis & Padel'))
     OR (ABS(v.latitude - (38.7403013)) <= 0.0004 AND ABS(v.longitude - (-9.2239276)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Indoor Padel Center', 'Carnaxide', 'Lisboa', 'R. Fernando Távora 4, 2790-256 Carnaxide', 'Padel', 'Clube de padel',
       38.7182184, -9.2230605, 'http://indoorpadelcenter.pt/', '914 440 101 · geral@indoorpadelcenter.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:indoorpadelcenter.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:indoorpadelcenter.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Indoor Padel Center'))
     OR (ABS(v.latitude - (38.7182184)) <= 0.0004 AND ABS(v.longitude - (-9.2230605)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Hub', 'Lisboa', 'Lisboa', 'R. Maestro Jaime Silva (Filho) 7A, 1500-402 Lisboa', 'Padel', 'Clube de padel',
       38.7538524, -9.1770473, 'http://www.padelhub.pt/', '936 201 411 · padelhub@absolutopadel.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padelhub.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padelhub.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel Hub'))
     OR (ABS(v.latitude - (38.7538524)) <= 0.0004 AND ABS(v.longitude - (-9.1770473)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube De Tenis Das Laranjeiras', 'Lisboa', 'Lisboa', 'Praça Nuno Rodrigues dos Santos 16, 1600-171 Lisboa', 'Ténis', 'Clube de ténis',
       38.7447528, -9.166767, 'https://www.tenislaranjeiras.com/reservas/', '935 017 996 · geral@tenislaranjeiras.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:tenislaranjeiras.com/reservas', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:tenislaranjeiras.com/reservas'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube De Tenis Das Laranjeiras'))
     OR (ABS(v.latitude - (38.7447528)) <= 0.0004 AND ABS(v.longitude - (-9.166767)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Boahora Ténis & Padel', 'Lisboa', 'Lisboa', 'R. Aliança Operária, 1300-044 Lisboa', 'Padel;Ténis', 'Clube de ténis e padel',
       38.7043091, -9.1895813, 'https://boahoratenispadel.pt/', '936 260 254 · geral@boahoratenispadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:boahoratenispadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:boahoratenispadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Boahora Ténis & Padel'))
     OR (ABS(v.latitude - (38.7043091)) <= 0.0004 AND ABS(v.longitude - (-9.1895813)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lambert Club - Tennis And Padel', 'Lisboa', 'Lisboa', 'R. Pedro Bandeira, 1750-217 Lisboa', 'Padel;Ténis', 'Clube de ténis e padel',
       38.7626236, -9.1538982, 'http://www.lambertclube.com/', '938 762 686 · info@lambertclube.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:lambertclube.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:lambertclube.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Lambert Club - Tennis And Padel'))
     OR (ABS(v.latitude - (38.7626236)) <= 0.0004 AND ABS(v.longitude - (-9.1538982)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Quinta Das Flores Tennis Club', 'Santo António dos Cavaleiros', 'Lisboa', 'R. Ary dos Santos 12, 2660-327 Santo António dos Cavaleiros', 'Ténis', 'Clube de ténis',
       38.8078588, -9.1599098, 'https://quintadasflorestenis.pt/', '934 778 412 · geral@quintadasflorestenis.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:quintadasflorestenis.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:quintadasflorestenis.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Quinta Das Flores Tennis Club'))
     OR (ABS(v.latitude - (38.8078588)) <= 0.0004 AND ABS(v.longitude - (-9.1599098)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Club 7', 'Lisboa', 'Lisboa', 'Eduardo VII Park, 1070-099 Lisboa', 'Padel', 'Clube de padel',
       38.7278133, -9.1596418, 'https://www.club7.pt/', '213 848 300 · park@club7.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:club7.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:club7.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Club 7'))
     OR (ABS(v.latitude - (38.7278133)) <= 0.0004 AND ABS(v.longitude - (-9.1596418)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Rackets Pro - Nacional Padel', 'Lisboa', 'Lisboa', 'R. de São Bento 209, 1250-219 Lisboa', 'Padel', 'Clube de padel',
       38.7158324, -9.1552019, 'http://racketspro.pt/', '925 203 553 · geral@nacionalpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:racketspro.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:racketspro.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Rackets Pro - Nacional Padel'))
     OR (ABS(v.latitude - (38.7158324)) <= 0.0004 AND ABS(v.longitude - (-9.1552019)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lisboa Racket Centre', 'Lisboa', 'Lisboa', 'R. Alferes Malheiro, 1700-025 Lisboa', 'Padel', 'Clube de padel',
       38.7591091, -9.1357741, 'http://lrc.pt/', '218 460 232 · recepcao@lrc.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:lrc.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:lrc.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Lisboa Racket Centre'))
     OR (ABS(v.latitude - (38.7591091)) <= 0.0004 AND ABS(v.longitude - (-9.1357741)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Guincho Tennis', 'Cascais', 'Lisboa', 'R. Areia, Rotunda, Av. Dom Carlos I SN, 2750-642 Cascais', 'Ténis', 'Clube de ténis',
       38.717144, -9.456255, 'http://www.guinchotennis.com/', '933 996 110 · luismatos@guinchotennis.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:guinchotennis.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:guinchotennis.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Guincho Tennis'))
     OR (ABS(v.latitude - (38.717144)) <= 0.0004 AND ABS(v.longitude - (-9.456255)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Recreativo E Desportivo Brasileiro/rouxinol', 'Seixal', 'Setúbal', 's/n, R. Ferreira de Castro', 'Padel', 'Clube de padel',
       38.6475522, -9.1436515, 'https://crdbr.com/', '212 535 933 · geral@crdbr.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:crdbr.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:crdbr.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Recreativo E Desportivo Brasileiro/rouxinol'))
     OR (ABS(v.latitude - (38.6475522)) <= 0.0004 AND ABS(v.longitude - (-9.1436515)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel4move Albufeira', 'Albufeira', 'Faro', 'Hotel Paraiso, 8200-161 Albufeira', 'Padel', 'Clube de padel',
       37.0915791, -8.237552, 'https://padel4move.com/', '928 125 890 · padel4move@outlook.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padel4move.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padel4move.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel4move Albufeira'))
     OR (ABS(v.latitude - (37.0915791)) <= 0.0004 AND ABS(v.longitude - (-8.237552)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Albufeira Padel Clube', 'Albufeira', 'Faro', 'Estr. de Ferreiras, 8200-555 Albufeira', 'Padel', 'Clube de padel',
       37.1191434, -8.2386709, 'https://albufeirapadelclube.pt/', '925 650 876 · albufeirapadel@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:albufeirapadelclube.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:albufeirapadelclube.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Albufeira Padel Clube'))
     OR (ABS(v.latitude - (37.1191434)) <= 0.0004 AND ABS(v.longitude - (-8.2386709)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vale Do Lobo Tennis Academy', 'Almancil', 'Faro', 'Av. Florida, 8135-034 Almancil', 'Ténis', 'Clube de ténis',
       37.0515721, -8.056624, 'https://www.valedolobo.com/pt/atividades/tenis-padel/', '962 105 056 · tennis@vdl.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:valedolobo.com/pt/atividades/tenis-padel', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:valedolobo.com/pt/atividades/tenis-padel'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Vale Do Lobo Tennis Academy'))
     OR (ABS(v.latitude - (37.0515721)) <= 0.0004 AND ABS(v.longitude - (-8.056624)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padelmode Verdizela Play', 'Corroios', 'Setúbal', 'Av. da Verdizela 46, 2855-621 Corroios', 'Padel', 'Clube de padel',
       38.5801781, -9.1542074, 'http://padelmode.pt/', '966 607 084 · info@padelmode.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padelmode.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padelmode.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padelmode Verdizela Play'))
     OR (ABS(v.latitude - (38.5801781)) <= 0.0004 AND ABS(v.longitude - (-9.1542074)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Best Lane Padel', 'Seixal', 'Setúbal', 'Rua Rodrigo Sarmento de Beires Lote 9A, 2840-068', 'Padel', 'Clube de padel',
       38.6051384, -9.0682792, 'https://bestlanepadel.pt/', '212 105 703 · geral@bestlanepadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:bestlanepadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:bestlanepadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Best Lane Padel'))
     OR (ABS(v.latitude - (38.6051384)) <= 0.0004 AND ABS(v.longitude - (-9.0682792)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Aroeira Tennis & Padel', 'Aroeira', 'Setúbal', 'tennis & padel, 2820-567 Aroeira', 'Padel;Ténis', 'Clube de ténis e padel',
       38.5815834, -9.1749761, 'https://www.matchpointeam.com/aroeiratennispadel', '927 239 978 · matchpointeam@outlook.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:matchpointeam.com/aroeiratennispadel', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:matchpointeam.com/aroeiratennispadel'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Aroeira Tennis & Padel'))
     OR (ABS(v.latitude - (38.5815834)) <= 0.0004 AND ABS(v.longitude - (-9.1749761)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Escola de Ténis de São Bernardo', 'Aveiro', 'Aveiro', 'Rua da Aldeia Desportiva Fonte do Rio Neto 3810-206', 'Ténis', 'Clube de ténis',
       40.6139791, -8.6280687, 'https://escolatenis.pt/', 'tenis.s.bernardo@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:escolatenis.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:escolatenis.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Escola de Ténis de São Bernardo'))
     OR (ABS(v.latitude - (40.6139791)) <= 0.0004 AND ABS(v.longitude - (-8.6280687)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Dume Padel', 'Braga', 'Braga', 'R. António Alves Rei, 4700-045 Braga', 'Padel', 'Clube de padel',
       41.5725179, -8.4314896, 'http://dumepadel.pt/', '961 303 192 · geral@dumepadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:dumepadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:dumepadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Dume Padel'))
     OR (ABS(v.latitude - (41.5725179)) <= 0.0004 AND ABS(v.longitude - (-8.4314896)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bpadel', 'Barcelos', 'Braga', '4715-213 Braga', 'Padel', 'Clube de padel',
       41.5431375, -8.4078068, 'http://www.bpadel.pt/', '938 437 688 · geral@bpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:bpadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:bpadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Bpadel'))
     OR (ABS(v.latitude - (41.5431375)) <= 0.0004 AND ABS(v.longitude - (-8.4078068)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Settepadel', 'Vila Nova de Famalicão', 'Braga', 'Central Park Lugar do Xisto, R. do Xisto, 4760-727 Vila Nova de Famalicão', 'Padel', 'Clube de padel',
       41.3686427, -8.5528731, 'http://www.settepadel.pt/', '938 689 688 · geral@settepadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:settepadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:settepadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Settepadel'))
     OR (ABS(v.latitude - (41.3686427)) <= 0.0004 AND ABS(v.longitude - (-8.5528731)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Warrior Padel Arena - Guimarães', 'Guimarães', 'Braga', 'Rua Eng.º, R. Eng. Duarte do Amaral 1650, 4800-109 Guimarães', 'Padel', 'Clube de padel',
       41.4690977, -8.3067061, 'https://warrior-padel.pt/', '911 075 644 · geral@warrior-padel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:warrior-padel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:warrior-padel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Warrior Padel Arena - Guimarães'))
     OR (ABS(v.latitude - (41.4690977)) <= 0.0004 AND ABS(v.longitude - (-8.3067061)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mini Academia Guimarães', 'Guimarães', 'Braga', 'Top Padel, R. da Cruz de Pedra, 4835-042 Guimarães', 'Padel', 'Clube de padel',
       41.4362738, -8.3027289, 'https://www.miniacademia.pt/', '935 359 807 · geral@miniacademia.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:miniacademia.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:miniacademia.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Mini Academia Guimarães'))
     OR (ABS(v.latitude - (41.4362738)) <= 0.0004 AND ABS(v.longitude - (-8.3027289)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bloko - Padel, Gym & Lounge', 'Bragança', 'Bragança', 'R. Cel. Teófilo de Morais 40, 5300-427 Bragança', 'Padel', 'Clube de padel',
       41.7882038, -6.7802039, 'https://www.bloko.com.pt/', '912 129 102 · geral@bloko.com.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:bloko.com.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:bloko.com.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Bloko - Padel, Gym & Lounge'))
     OR (ABS(v.latitude - (41.7882038)) <= 0.0004 AND ABS(v.longitude - (-6.7802039)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Bom Sucesso Tennis Academy', 'Óbidos', 'Leiria', 'Vau, R. do Comércio 27, 2510-662 Óbidos', 'Ténis', 'Clube de ténis',
       39.380644, -9.2458867, 'http://www.nmtpa.com/', '917 539 181 · contact@nmtpa.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:nmtpa.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:nmtpa.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Bom Sucesso Tennis Academy'))
     OR (ABS(v.latitude - (39.380644)) <= 0.0004 AND ABS(v.longitude - (-9.2458867)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Rey', 'Serra d'' El-Rei', 'Leiria', 'Estrada Nacional 114, 2525-801 Serra d'' El-Rei', 'Padel', 'Clube de padel',
       39.331782, -9.2694558, 'https://padelrey.pt/', '963 838 500 · padelrey@padelrey.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padelrey.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padelrey.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel Rey'))
     OR (ABS(v.latitude - (39.331782)) <= 0.0004 AND ABS(v.longitude - (-9.2694558)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Tenis Clube De Chaves', 'Chaves', 'Vila Real', 'R. Dr. João Morais, 5400-239 Chaves', 'Ténis', 'Clube de ténis',
       41.7376932, -7.4718244, 'http://www.tcchaves.pt/', '932 277 556 · socios@tcchaves.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:tcchaves.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:tcchaves.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Tenis Clube De Chaves'))
     OR (ABS(v.latitude - (41.7376932)) <= 0.0004 AND ABS(v.longitude - (-7.4718244)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'N10 Padel', 'Coimbra', 'Coimbra', 'Tv. Vale do Paraíso Norte 3020, 3020-324 Coimbra', 'Padel', 'Clube de padel',
       40.2528487, -8.4359567, 'http://www.n10.pt/', '239 496 378 · n10@n10.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:n10.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:n10.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('N10 Padel'))
     OR (ABS(v.latitude - (40.2528487)) <= 0.0004 AND ABS(v.longitude - (-8.4359567)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Off Padel', 'Perafita', 'Porto', 'R. Oriental 511, 4455-516 Perafita', 'Padel', 'Clube de padel',
       41.2242852, -8.6935382, 'http://offpadel.pt/', '960 253 883 · info@offpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:offpadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:offpadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Off Padel'))
     OR (ABS(v.latitude - (41.2242852)) <= 0.0004 AND ABS(v.longitude - (-8.6935382)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Mar Padel', 'Matosinhos', 'Porto', 'R. Veloso Salgado 1135, 4450-801 Matosinhos', 'Padel', 'Clube de padel',
       41.2044288, -8.6919677, 'http://www.marpadel.com/', '229 968 020 · geral@marpadel.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:marpadel.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:marpadel.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Mar Padel'))
     OR (ABS(v.latitude - (41.2044288)) <= 0.0004 AND ABS(v.longitude - (-8.6919677)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Norte Padel', 'Porto', 'Porto', 'Av. Fontes Pereira de Melo 246, 4100-259 Porto', 'Padel', 'Clube de padel',
       41.1785899, -8.648595, 'http://www.nortepadel.com/', '222 448 288 · geral@nortepadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:nortepadel.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:nortepadel.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Norte Padel'))
     OR (ABS(v.latitude - (41.1785899)) <= 0.0004 AND ABS(v.longitude - (-8.648595)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Proracket Squash And Padel', 'São Mamede de Infesta', 'Porto', 'Nucleo Empresarial Sarcol, R. do Tronco 375, 4465-275 São Mamede de Infesta', 'Padel', 'Clube de padel',
       41.1848865, -8.6212674, 'http://www.proracket.pt/', '223 258 104 · proracket@proracket.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:proracket.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:proracket.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Proracket Squash And Padel'))
     OR (ABS(v.latitude - (41.1848865)) <= 0.0004 AND ABS(v.longitude - (-8.6212674)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Your Padel', 'Porto', 'Porto', 'Estr. da Circunvalação 7234, 4425-420 Porto', 'Padel', 'Clube de padel',
       41.1715258, -8.5672379, 'https://www.yourpadel.pt/', '965 423 718 · geral@yourpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:yourpadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:yourpadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Your Padel'))
     OR (ABS(v.latitude - (41.1715258)) <= 0.0004 AND ABS(v.longitude - (-8.5672379)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Alley Courts', 'Porto', 'Porto', 'R. Manuel Pinto de Azevedo 64b, 4100-320 Porto', 'Padel', 'Clube de padel',
       41.1903987, -8.5822333, 'http://www.alleycourts.com/', '925 078 757 · geral@alleycourts.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:alleycourts.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:alleycourts.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Alley Courts'))
     OR (ABS(v.latitude - (41.1903987)) <= 0.0004 AND ABS(v.longitude - (-8.5822333)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Indoor Quinta Monserrate', 'Matosinhos', 'Porto', 'R. Sousa Aroso 901, 4450-291 Matosinhos', 'Padel', 'Clube de padel',
       41.1773162, -8.6790298, 'https://quintademonserrate.com/', '936 891 188 · geral@quintademonserrate.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:quintademonserrate.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:quintademonserrate.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel Indoor Quinta Monserrate'))
     OR (ABS(v.latitude - (41.1773162)) <= 0.0004 AND ABS(v.longitude - (-8.6790298)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sporting Clube Da Cruz', 'Porto', 'Porto', 'R. da Fonte do Outeiro 521, 4200-353 Porto', 'Padel', 'Clube de padel',
       41.1735327, -8.5926389, 'https://www.sportingclubdacruz.pt/', '225 508 041 · info@sportingclubdacruz.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:sportingclubdacruz.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:sportingclubdacruz.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Sporting Clube Da Cruz'))
     OR (ABS(v.latitude - (41.1735327)) <= 0.0004 AND ABS(v.longitude - (-8.5926389)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Porto Tennis Club', 'Porto', 'Porto', 'R. de Damião de Góis 405, 4050-227 Porto', 'Ténis', 'Clube de ténis',
       41.1619806, -8.613474, 'http://www.ctporto.com/', '225 028 506 · geral@ctporto.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:ctporto.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:ctporto.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Porto Tennis Club'))
     OR (ABS(v.latitude - (41.1619806)) <= 0.0004 AND ABS(v.longitude - (-8.613474)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Oporto Cricket & Lawn Tennis Club', 'Porto', 'Porto', 'Rua do Campo Alegre 532, 4050-170 Porto', 'Ténis', 'Clube de ténis',
       41.1524985, -8.629101, 'http://www.oportocricketclub.com/', '226 052 720 · oportocricketclub@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:oportocricketclub.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:oportocricketclub.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Oporto Cricket & Lawn Tennis Club'))
     OR (ABS(v.latitude - (41.1524985)) <= 0.0004 AND ABS(v.longitude - (-8.629101)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ermesinde Tennis Club', 'Ermesinde', 'Porto', 'R. Carvalhal 200, 4445-416 Ermesinde', 'Ténis', 'Clube de ténis',
       41.2103861, -8.5506417, 'http://ctenisermesinde.com/', '931 634 228 · geral@ctenisermesinde.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:ctenisermesinde.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:ctenisermesinde.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Ermesinde Tennis Club'))
     OR (ABS(v.latitude - (41.2103861)) <= 0.0004 AND ABS(v.longitude - (-8.5506417)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grupo Cultural E Desportivo Dos Empregados Do Banco Pinto & Sotto Mayor', 'Vila Nova de Gaia', 'Porto', 'R. Oliveiras 340, Vila Nova de Gaia', 'Padel', 'Clube de padel',
       41.1165102, -8.6260024, 'http://www.gcdebpsm.org/', '227 121 460 · secretaria@gcdebpsm.org',
       'COMMUNITY', 'JYNLO:CLUB-SITE:gcdebpsm.org', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:gcdebpsm.org'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Grupo Cultural E Desportivo Dos Empregados Do Banco Pinto & Sotto Mayor'))
     OR (ABS(v.latitude - (41.1165102)) <= 0.0004 AND ABS(v.longitude - (-8.6260024)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Atlântico Da Madalena', 'Vila Nova de Gaia', 'Porto', 'R. do Cerro 897, 4405-736 Vila Nova de Gaia', 'Padel', 'Clube de padel',
       41.116154, -8.6498625, 'https://atlanticodamadalena.pt/', '961 180 014 · cam@atlanticodamadalena.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:atlanticodamadalena.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:atlanticodamadalena.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Atlântico Da Madalena'))
     OR (ABS(v.latitude - (41.116154)) <= 0.0004 AND ABS(v.longitude - (-8.6498625)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Corgas Club', 'Vila Nova de Gaia', 'Porto', 'R. José Oliveira, 4430-240 Vila Nova de Gaia', 'Padel', 'Clube de padel',
       41.1127911, -8.606335, 'http://www.corgasclub.com/', '227 114 424 · directora@corgasclub.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:corgasclub.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:corgasclub.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Corgas Club'))
     OR (ABS(v.latitude - (41.1127911)) <= 0.0004 AND ABS(v.longitude - (-8.606335)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Major Padel Club Douro', 'Vila Nova de Gaia', 'Porto', 'Avenida dos, R. Arcos do Sardão 629, 4430-434 Vila Nova de Gaia', 'Padel', 'Clube de padel',
       41.0467004, -8.5436201, 'http://www.majorpadelclub.com/', '917 314 625 · info@majorpadelclub.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:majorpadelclub.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:majorpadelclub.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Major Padel Club Douro'))
     OR (ABS(v.latitude - (41.0467004)) <= 0.0004 AND ABS(v.longitude - (-8.5436201)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Inn', 'Vila Nova de Gaia', 'Porto', 'R. Seara, Zona Industrial dos Arcos do Sardão 554 Pavilhões C e D, 4430-291 Vila Nova de Gaia', 'Padel', 'Clube de padel',
       41.1126816, -8.5817542, 'http://www.padelinn.pt/', '910 561 395 · geral@padelinn.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padelinn.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padelinn.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel Inn'))
     OR (ABS(v.latitude - (41.1126816)) <= 0.0004 AND ABS(v.longitude - (-8.5817542)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Ténis Gaia', 'Canelas', 'Porto', 'R. Estádio de Canelas, 4410-276 Canelas', 'Ténis', 'Clube de ténis',
       41.0662018, -8.5937611, 'http://clubetenisgaia.com/', '910 838 355 · geral@clubetenisgaia.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:clubetenisgaia.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:clubetenisgaia.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Ténis Gaia'))
     OR (ABS(v.latitude - (41.0662018)) <= 0.0004 AND ABS(v.longitude - (-8.5937611)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Pedra Do Couto - Espectáculos', 'Santo Tirso', 'Porto', 'R. Santo André, 4780-222 Santo Tirso', 'Padel', 'Clube de padel',
       41.3273794, -8.5007227, 'https://pedradocouto.net/', '913 577 141 · info@pedradocouto.net',
       'COMMUNITY', 'JYNLO:CLUB-SITE:pedradocouto.net', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:pedradocouto.net'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Pedra Do Couto - Espectáculos'))
     OR (ABS(v.latitude - (41.3273794)) <= 0.0004 AND ABS(v.longitude - (-8.5007227)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Gym Santo Tirso Club', 'Santo Tirso', 'Porto', 'R. Misericórdia 501, 4780-501 Santo Tirso', 'Padel', 'Clube de padel',
       41.3413463, -8.4831002, 'http://www.ginasioclubesantotirso.com/', '252 858 271 · geral@ginasioclubesantotirso.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:ginasioclubesantotirso.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:ginasioclubesantotirso.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Gym Santo Tirso Club'))
     OR (ABS(v.latitude - (41.3413463)) <= 0.0004 AND ABS(v.longitude - (-8.4831002)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Além Rio Ténis Escola', 'Lama', 'Porto', 'R. Dr. Eduardo Lima Carneiro 287, 4780-300 Lama', 'Ténis', 'Clube de ténis',
       41.3564381, -8.4646237, 'https://alemriotenisescola.pt/', '964 032 331 · geral@alemriotenisescola.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:alemriotenisescola.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:alemriotenisescola.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Além Rio Ténis Escola'))
     OR (ABS(v.latitude - (41.3564381)) <= 0.0004 AND ABS(v.longitude - (-8.4646237)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Grijó Ténis Clube', 'Grijó', 'Porto', 'R. Nova da Feiteira 176, 4415-499 Grijó', 'Ténis', 'Clube de ténis',
       41.0386373, -8.5636564, 'https://grijotenisclube.blogspot.com/', '918 502 085 · grijotc@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:grijotenisclube.blogspot.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:grijotenisclube.blogspot.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Grijó Ténis Clube'))
     OR (ABS(v.latitude - (41.0386373)) <= 0.0004 AND ABS(v.longitude - (-8.5636564)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Padel Pt Ovar (arada)', 'Arada', 'Aveiro', 'R. do Sobreiro 50, 3885-133 Arada', 'Padel', 'Clube de padel',
       40.8997054, -8.5980049, 'http://www.padelovar.com/', '914 258 827 · padelptovar@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:padelovar.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:padelovar.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Padel Pt Ovar (arada)'))
     OR (ABS(v.latitude - (40.8997054)) <= 0.0004 AND ABS(v.longitude - (-8.5980049)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube De Ténis De Évora', 'Évora', 'Évora', 'R. Emb. Assis Chateaubrian 57, Évora', 'Ténis', 'Clube de ténis',
       38.5860312, -7.9145973, 'http://www.clubetenisevora.pt/', '266 744 443 · geral@clubetenisevora.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:clubetenisevora.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:clubetenisevora.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube De Ténis De Évora'))
     OR (ABS(v.latitude - (38.5860312)) <= 0.0004 AND ABS(v.longitude - (-7.9145973)) <= 0.0005
         AND v.sports ILIKE '%Ténis%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Oriental De Pechão', 'Olhão', 'Faro', '37°03''24. 7°52''20 Avenida 5 de Outubro 4"N, 8700-302 Olhão', 'Padel', 'Clube de padel',
       37.0240056, -7.8403217, 'http://www.orientalpechao.com/', 'orientalpechao@gmail.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:orientalpechao.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:orientalpechao.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Oriental De Pechão'))
     OR (ABS(v.latitude - (37.0240056)) <= 0.0004 AND ABS(v.longitude - (-7.8403217)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Ocean Padel Club Luz', 'Lagos', 'Faro', 'RUA Dr FRANSISCO GENTIL MARTIME LOT 57, PRAIA DA LUZ, 8600-164 Lagos', 'Padel', 'Clube de padel',
       37.0884671, -8.7306735, 'http://oceanpadelclub.com/', '961 410 707 · info@oceanpadelclub.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:oceanpadelclub.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:oceanpadelclub.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Ocean Padel Club Luz'))
     OR (ABS(v.latitude - (37.0884671)) <= 0.0004 AND ABS(v.longitude - (-8.7306735)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Alvor Padel Club', 'Alvor', 'Faro', 'R. Dom Sancho I, 8500-013 Alvor', 'Padel', 'Clube de padel',
       37.1287045, -8.5941346, 'http://www.alvorpadelclub.com/', '931 682 026 · info@alvorpadelclub.com',
       'COMMUNITY', 'JYNLO:CLUB-SITE:alvorpadelclub.com', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:alvorpadelclub.com'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Alvor Padel Club'))
     OR (ABS(v.latitude - (37.1287045)) <= 0.0004 AND ABS(v.longitude - (-8.5941346)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Lispadel Indoor', 'Leiria', 'Leiria', 'Rua Bailadouro, Lt. 3, Zona Industrial de Pousos, 2410-494 Leiria', 'Padel', 'Clube de padel',
       39.7301385, -8.759194, 'http://www.lispadel.pt/', '919 286 066 · reservas@lispadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:lispadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:lispadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Lispadel Indoor'))
     OR (ABS(v.latitude - (39.7301385)) <= 0.0004 AND ABS(v.longitude - (-8.759194)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Vila Padel Fátima', 'Chainça', 'Leiria', 'R. Central 192, Loureira, 2495-122 Chainça', 'Padel', 'Clube de padel',
       39.6696331, -8.6841071, 'https://vilapadel.pt/', '913 880 304 · fatima@vilapadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:vilapadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:vilapadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Vila Padel Fátima'))
     OR (ABS(v.latitude - (39.6696331)) <= 0.0004 AND ABS(v.longitude - (-8.6841071)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sporting Clube De Tomar', 'Ourém', 'Santarém', 'Tomar', 'Padel', 'Clube de padel',
       39.6036596, -8.4150492, 'http://www.sct.pt/', '249 323 473 · geral@sct.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:sct.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:sct.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Sporting Clube De Tomar'))
     OR (ABS(v.latitude - (39.6036596)) <= 0.0004 AND ABS(v.longitude - (-8.4150492)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Clube Desportivo da Póvoa - pavilhão Linhares de Castro', 'Póvoa de Varzim', 'Porto', NULL, 'Padel', 'Clube de padel',
       41.3870143, -8.7723294, 'https://cdpovoa.pt/', 'geral@cdpovoa.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:cdpovoa.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:cdpovoa.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Clube Desportivo da Póvoa - pavilhão Linhares de Castro'))
     OR (ABS(v.latitude - (41.3870143)) <= 0.0004 AND ABS(v.longitude - (-8.7723294)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Wow Padel', 'Santarém', 'Santarém', 'Praçeta António Costa Macedo, Zona Industrial', 'Padel', 'Clube de padel',
       39.2580403, -8.7137899, 'http://www.wowpadel.pt/', '936 726 165 · play@wowpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:wowpadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:wowpadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Wow Padel'))
     OR (ABS(v.latitude - (39.2580403)) <= 0.0004 AND ABS(v.longitude - (-8.7137899)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'Sporting Clube De Torres (sct)', 'Torres Vedras', 'Lisboa', 'Travessa das Oliveiras, n.º5 - Torres Vedras - Lisboa - Portugal - CP, Tv. das Oliveiras 5, 2560-683 Torres Vedras', 'Padel', 'Clube de padel',
       39.0905726, -9.2626608, 'http://www.sportingtorres.pt/', '261 321 052 · geral@sportingtorres.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:sportingtorres.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:sportingtorres.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('Sporting Clube De Torres (sct)'))
     OR (ABS(v.latitude - (39.0905726)) <= 0.0004 AND ABS(v.longitude - (-9.2626608)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);

INSERT INTO desporto24.venue
  (name, city, district, address, sports, venue_type, latitude, longitude, website, contact,
   information_source, source_reference, indoor, changing_rooms, parking, lighting, created_at)
SELECT 'New Padel Chão Da Fonte - Viseu', 'Viseu', 'Viseu', 'Largo Cruzeiro 7, 3500-622 Viseu', 'Padel', 'Clube de padel',
       40.6434031, -7.9028948, 'http://www.newpadel.pt/', '910 715 689 · geral@newpadel.pt',
       'COMMUNITY', 'JYNLO:CLUB-SITE:newpadel.pt', FALSE, FALSE, FALSE, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM desporto24.venue v
  WHERE v.source_reference = 'JYNLO:CLUB-SITE:newpadel.pt'
     OR LOWER(BTRIM(v.name)) = LOWER(BTRIM('New Padel Chão Da Fonte - Viseu'))
     OR (ABS(v.latitude - (40.6434031)) <= 0.0004 AND ABS(v.longitude - (-7.9028948)) <= 0.0005
         AND v.sports ILIKE '%Padel%')
);
