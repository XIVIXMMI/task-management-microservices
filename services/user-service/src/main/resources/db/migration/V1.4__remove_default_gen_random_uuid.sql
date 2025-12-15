-- Remove DEFAULT gen_random_uuid() to let JPA handle UUID generation
-- Keep TIMESTAMPTZ as-is for PostgreSQL benefits

ALTER TABLE password_reset_tokens
    ALTER COLUMN id DROP DEFAULT;