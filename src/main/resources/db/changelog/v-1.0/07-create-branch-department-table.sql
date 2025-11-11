CREATE TABLE IF NOT EXISTS branch_department
(
    branch_id     UUID NOT NULL,
    department_id UUID NOT NULL,
    PRIMARY KEY (branch_id, department_id),
    FOREIGN KEY (branch_id) REFERENCES branch(id)
    );

COMMENT ON TABLE branch_department IS 'Таблица, связывающая отделения банков с их отделами.';
COMMENT ON COLUMN branch_department.branch_id IS 'Идентификатор отделения банка.';
COMMENT ON COLUMN branch_department.department_id IS 'Идентификатор отдела.';