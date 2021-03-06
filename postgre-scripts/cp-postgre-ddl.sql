CREATE DATABASE demodb;
\connect demodb;
CREATE SCHEMA IF NOT EXISTS public;
set search_path to public;

  CREATE TABLE IF NOT EXISTS CUSTOMER (
      ID INTEGER NOT NULL,
      SALUTATION VARCHAR(6),
      FIRST_NAME VARCHAR(10),
      LAST_NAME VARCHAR(10),
      MOBILE VARCHAR(20),
      PRIMARY KEY (ID)
  );

  CREATE TABLE IF NOT EXISTS CUSTOMER_PLAN (
      CUSTOMER_ID INTEGER NOT NULL,
      PLAN_ID INTEGER NOT NULL,
      PRIMARY KEY (CUSTOMER_ID)
  );

  CREATE TABLE IF NOT EXISTS PLAN (
      ID INTEGER NOT NULL,
      NAME VARCHAR(20),
      DESCRIPTION VARCHAR(100),
      DATA_LIMIT INTEGER,
      COST INTEGER,
      PRIMARY KEY (ID)
  );

  CREATE TABLE IF NOT EXISTS BILLING (
      CUSTOMER_ID INTEGER NOT NULL,
      AMOUNT INTEGER,
      USAGE INTEGER,
      BILL_DATE DATE NOT NULL,
      PRIMARY KEY (CUSTOMER_ID, BILL_DATE)
  );