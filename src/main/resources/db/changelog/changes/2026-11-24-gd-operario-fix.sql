-- Grupo Desportivo Operário (Campo Pequeno): synthetic football pitch with changing rooms, bookings by phone (Time Out Lisboa).
UPDATE desporto24.venue
   SET sports = CASE WHEN sports ILIKE '%Futebol;%' OR sports ILIKE '%;Futebol' OR sports = 'Futebol' THEN sports ELSE 'Futebol;' || COALESCE(NULLIF(sports, ''), 'Futsal') END,
       contact = COALESCE(NULLIF(contact, ''), '+351 919 199 089'),
       address = 'Rua de Entrecampos, Campo Pequeno, 1000-306 Lisboa',
       changing_rooms = TRUE
 WHERE source_reference = 'CML:DESPORTO:3061'
    OR (LOWER(BTRIM(name)) = 'grupo desportivo operário' AND ABS(latitude - 38.7421212) <= 0.003 AND ABS(longitude - (-9.1442734)) <= 0.004);
