-- "(11) Pavilhão…" → "Pavilhão…": list numbers left by the public map import.
UPDATE desporto24.venue
SET name = btrim(regexp_replace(name, '^\s*\(\d{1,4}\)\s*', ''))
WHERE name ~ '^\s*\(\d{1,4}\)\s*\S';
