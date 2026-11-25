-- The Lisbon open data spells "Pio XXI"; the college is Colégio Universitário Pio XII (location confirmed by Miguel).
UPDATE desporto24.venue
   SET name = 'Colégio Universitário Pio XII'
 WHERE name = 'Colégio Universitário Pio XXI'
   AND ABS(latitude - 38.7490326) <= 0.002 AND ABS(longitude - (-9.1526762)) <= 0.002;
