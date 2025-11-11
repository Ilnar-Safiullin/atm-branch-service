CREATE TABLE IF NOT EXISTS work_schedule
(
    id           UUID       PRIMARY KEY,
    entity_type  VARCHAR(6) NOT NULL,
    entity_id    UUID       NOT NULL,
    week_day     SMALLINT    NOT NULL,
    opening_time TIME       NOT NULL,
    closing_time TIME       NOT NULL
    );

COMMENT ON TABLE work_schedule IS 'Таблица с информацией о времени работы';
COMMENT ON COLUMN work_schedule.id IS 'Идентификатор расписания';
COMMENT ON COLUMN work_schedule.entity_type IS 'Тип: банкомат или отделение';
COMMENT ON COLUMN work_schedule.entity_id IS 'Идентификатор банкомата или отделения';
COMMENT ON COLUMN work_schedule.week_day IS 'День недели';
COMMENT ON COLUMN work_schedule.opening_time IS 'Время начала работы';
COMMENT ON COLUMN work_schedule.closing_time IS 'Время завершения работы';