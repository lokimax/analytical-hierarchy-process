-- Initialize PostgreSQL database for AHP application
-- This script runs automatically when the container starts

-- Create application user if not exists
-- DO $$
-- BEGIN
--   IF NOT EXISTS (SELECT 1 FROM pg_user WHERE usename = 'ahp_user') THEN
--     CREATE USER ahp_user WITH PASSWORD 'ahp_password';
--   END IF;
-- END $$;

-- Grant privileges
-- GRANT ALL PRIVILEGES ON DATABASE ahp_db TO ahp_user;

-- Create extensions (if needed)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- The actual tables will be created by Hibernate
