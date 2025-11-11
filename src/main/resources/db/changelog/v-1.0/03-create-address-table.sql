CREATE TABLE IF NOT EXISTS address
(
    id            UUID         PRIMARY KEY,
    city_id       UUID         NOT NULL,
    street_type   VARCHAR(6)  NOT NULL,
    street        VARCHAR(64)  NOT NULL,
    house         VARCHAR(10)  NOT NULL,
    latitude      DECIMAL(8,6),
    longitude     DECIMAL(9,6),
    metro_station VARCHAR(25),
    FOREIGN KEY (city_id) REFERENCES city (id) ON UPDATE CASCADE
    );

COMMENT ON TABLE address IS 'Таблица с информацией об адресе';
COMMENT ON COLUMN address.id IS 'Идентификатор адреса';
COMMENT ON COLUMN address.city_id IS 'Идентификатор города';
COMMENT ON COLUMN address.street_type IS 'Тип улицы';
COMMENT ON COLUMN address.street IS 'Наименование улицы';
COMMENT ON COLUMN address.house IS 'Номер дома';
COMMENT ON COLUMN address.latitude IS 'Широта';
COMMENT ON COLUMN address.longitude IS 'Долгота';
COMMENT ON COLUMN address.metro_station IS 'Название ближайшей станции метро';
