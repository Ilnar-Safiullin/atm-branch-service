ALTER TABLE address ADD COLUMN full_address VARCHAR(300);

-- Собирает колонку из текущих данных в БД
UPDATE address
SET full_address = (
    SELECT c.name || ', ' || address.street_type || ' ' || address.street || ', д. ' || address.house
    FROM city c
    WHERE c.id = address.city_id
)
WHERE city_id IS NOT NULL;

ALTER TABLE address ALTER COLUMN full_address SET NOT NULL;