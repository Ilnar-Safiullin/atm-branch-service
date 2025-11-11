CREATE TABLE IF NOT EXISTS location
(
    id            UUID        PRIMARY KEY,
    county_name   VARCHAR(64) NOT NULL
    );

COMMENT ON TABLE location IS 'Таблица c информацией о стране';
COMMENT ON COLUMN location.id IS 'Идентификатор страны';
COMMENT ON COLUMN location.county_name IS 'Название страны';