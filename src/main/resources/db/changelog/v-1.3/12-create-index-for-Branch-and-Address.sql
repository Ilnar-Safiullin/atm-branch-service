CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_branch_name_lower_trgm ON branch USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_branch_banknumber_lower_trgm ON branch USING gin (lower(bank_number) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_address_fulladdress_lower_trgm ON address USING gin (lower(full_address) gin_trgm_ops);
