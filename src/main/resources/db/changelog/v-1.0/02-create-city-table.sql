CREATE TABLE IF NOT EXISTS city
(
    id          UUID        PRIMARY KEY,
    location_id UUID        NOT NULL,
    name        VARCHAR(30) NOT NULL,
    is_popular  BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (location_id) REFERENCES location (id) ON UPDATE CASCADE
    );

COMMENT ON TABLE city IS 'Таблица с информацией о городе';
COMMENT ON COLUMN city.id IS 'Идентификатор города';
COMMENT ON COLUMN city.location_id IS 'Идентификатор страны';
COMMENT ON COLUMN city.name IS 'Название города';
COMMENT ON COLUMN city.is_popular IS 'Является ли город миллионником';
