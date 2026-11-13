-- =====================================================================
-- Family Expense Tracker - Database Setup
-- =====================================================================
-- This script is OPTIONAL. Spring Boot + Hibernate will automatically
-- create the database (via createDatabaseIfNotExist=true in the JDBC
-- URL) and all required tables (via spring.jpa.hibernate.ddl-auto=update)
-- the first time the backend application starts.
--
-- Run this script manually only if you prefer to create the database
-- ahead of time, or if your MySQL user does not have permission to
-- create databases automatically.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS family_expense_tracker
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE family_expense_tracker;

-- Tables below are created automatically by Hibernate on first run.
-- They are documented here for reference only.

-- users
--   id BIGINT PK
--   username VARCHAR(50) UNIQUE
--   password VARCHAR(255)      -- BCrypt hash
--   full_name VARCHAR(100)
--   avatar_color VARCHAR(20)
--   role VARCHAR(20)           -- ADMIN | MEMBER
--   enabled BOOLEAN
--   created_at DATETIME

-- categories
--   id BIGINT PK
--   name VARCHAR(60)
--   description VARCHAR(255)
--   color_code VARCHAR(20)
--   icon VARCHAR(40)
--   default_category BOOLEAN

-- expenses
--   id BIGINT PK
--   title VARCHAR(150)
--   notes VARCHAR(500)
--   amount DECIMAL(12,2)
--   expense_date DATE
--   payment_method VARCHAR(20) -- CASH | CARD | UPI | BANK_TRANSFER | OTHER
--   category_id BIGINT FK -> categories.id
--   user_id BIGINT FK -> users.id
--   created_at DATETIME
--   updated_at DATETIME

-- The backend's DataInitializer will automatically seed:
--   * 4 family member accounts (member1..member4 / ChangeMe123!, BCrypt-hashed)
--   * 10 default expense categories
-- on first application startup, if the users/categories tables are empty.
