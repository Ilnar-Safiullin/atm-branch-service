ALTER TABLE branch ADD COLUMN type VARCHAR(30);

UPDATE branch
SET type =
        CASE
            WHEN bank_number = '101' THEN 'HEAD_OFFICE'
            WHEN bank_number = '102' THEN 'ADDITIONAL_OFFICE'
            WHEN bank_number = '103' THEN 'OPERATIONS_OFFICE'
            WHEN bank_number = '104' THEN 'CREDIT_AND_CASH_OFFICE'
            WHEN bank_number = '105' THEN 'BANKING_SERVICE_OFFICE'
            WHEN bank_number = '106' THEN 'INTERNAL_BANKING_UNIT'
            WHEN bank_number = '107' THEN 'SUPPORTIVE_BANKING_UNIT'
            ELSE 'BRANCH'
            END;

ALTER TABLE branch ALTER COLUMN type SET NOT NULL;