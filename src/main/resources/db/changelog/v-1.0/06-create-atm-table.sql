CREATE TABLE IF NOT EXISTS atm
(
    id                    UUID        PRIMARY KEY,
    branch_id             UUID        NOT NULL,
    address_id            UUID        NOT NULL,
    number                VARCHAR(4)  UNIQUE NOT NULL,
    inventory_number      VARCHAR(10) UNIQUE NOT NULL,
    installation_location VARCHAR(64) NOT NULL,
    construction          VARCHAR(11) NOT NULL,
    has_cash_deposit      BOOLEAN     NOT NULL DEFAULT FALSE,
    has_NFC               BOOLEAN     NOT NULL DEFAULT FALSE,
    is_closed             BOOLEAN     NOT NULL DEFAULT FALSE,
    FOREIGN KEY (branch_id) REFERENCES branch (id) ON UPDATE CASCADE,
    FOREIGN KEY (address_id) REFERENCES address (id) ON UPDATE CASCADE
    );

COMMENT ON TABLE atm IS 'Таблица с информацией о банкоматах';
COMMENT ON COLUMN atm.id IS 'Идентификатор банкомата';
COMMENT ON COLUMN atm.branch_id IS 'Идентификатор отделения';
COMMENT ON COLUMN atm.address_id IS 'Идентификатор адреса';
COMMENT ON COLUMN atm.number IS 'Порядковый номер банкомата';
COMMENT ON COLUMN atm.inventory_number IS 'Инвентарный номер банкомата';
COMMENT ON COLUMN atm.installation_location IS 'Описание места установки';
COMMENT ON COLUMN atm.construction IS 'Тип конструкции банкомата';
COMMENT ON COLUMN atm.has_cash_deposit IS 'внесение наличных';
COMMENT ON COLUMN atm.has_NFC IS 'бесконтактная оплата';
COMMENT ON COLUMN atm.is_closed IS 'Статус работы';