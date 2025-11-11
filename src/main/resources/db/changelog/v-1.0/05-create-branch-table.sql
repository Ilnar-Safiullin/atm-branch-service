CREATE TABLE IF NOT EXISTS branch
(
    id                    UUID        PRIMARY KEY,
    name                  VARCHAR(64) NOT NULL,
    bank_number           VARCHAR(4)  NOT NULL,
    address_id            UUID        NOT NULL,
    has_currency_exchange BOOLEAN     NOT NULL DEFAULT FALSE,
    phone_number          VARCHAR(12) NOT NULL,
    has_pandus            BOOLEAN     NOT NULL DEFAULT FALSE,
    is_closed             BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (address_id) REFERENCES address (id) ON UPDATE CASCADE
    );

COMMENT ON TABLE branch IS 'Таблица с информацией об отделениях банка';
COMMENT ON COLUMN branch.id IS 'Идентификатор отделения';
COMMENT ON COLUMN branch.name IS 'Наименование отделения';
COMMENT ON COLUMN branch.bank_number IS 'Номер отделения';
COMMENT ON COLUMN branch.address_id IS 'Идентификатор адреса';
COMMENT ON COLUMN branch.has_currency_exchange IS 'Оказываются ли услуги по обмену валюты?';
COMMENT ON COLUMN branch.phone_number IS 'Номер телефона отделения банка';
COMMENT ON COLUMN branch.has_pandus IS 'Есть ли пандус у отделения?';
COMMENT ON COLUMN branch.is_closed IS 'Статус работы';