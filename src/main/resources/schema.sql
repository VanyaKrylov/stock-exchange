-- drop table if exists TEST;
/*drop table if exists INVESTOR;
drop table if exists BROKER;
drop table if exists COMPANY;
drop table if exists FINANCIAL_REPORT;
drop table if exists INDIVIDUAL;
drop table if exists STOCK_ORDER cascade ;
drop table if exists LIMITED_ORDER;
drop table if exists MARKET_ORDER;
drop table if exists PAYMENT;
drop table if exists STOCK CASCADE;
drop table if exists STOCKS_ORDERS;

-- create table TEST
-- (
--     id BIGSERIAL PRIMARY KEY,
--     text VARCHAR(64) NOT NULL
-- );

create table INVESTOR
(
    id BIGSERIAL PRIMARY KEY,
    bank_account_id uuid not null unique,
    balance numeric(16, 2) not null
);

create table BROKER
(
    id BIGINT PRIMARY KEY,
    fee numeric(16,2) not null,
    foreign key (id) references INVESTOR(id)
);

create table COMPANY
(
    id BIGSERIAL PRIMARY KEY,
    bank_account_id uuid not null unique,
    capital numeric(16,2) not null
);

create table FINANCIAL_REPORT
(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT not null,
    earnings numeric(16,2) not null,
    expenses numeric(16,2) not null,
    capital numeric(16,2) not null,
    foreign key (company_id) REFERENCES COMPANY(id) ON UPDATE CASCADE ON DELETE CASCADE
);

create table INDIVIDUAL
(
    id BIGINT PRIMARY KEY,
    broker_id BIGINT not null,
    foreign key (broker_id) references BROKER(id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (id) references INVESTOR(id)
);

create table STOCK_ORDER
(
    id BIGSERIAL PRIMARY KEY,
    order_status varchar(32) not null,
    investor_id BIGINT not null,
    operation_type varchar(32) not null,
    foreign key (investor_id) references INVESTOR(id) ON UPDATE CASCADE ON DELETE CASCADE
);

create table LIMITED_ORDER
(
    id BIGINT PRIMARY KEY,
    max_price numeric(16,2) NOT NULL,
    min_price numeric(16,2) NOT NULL,
    foreign key (id) references STOCK_ORDER(id)
);

create table MARKET_ORDER
(
    id BIGINT PRIMARY KEY,
    foreign key (id) references STOCK_ORDER(id)
);

create table PAYMENT
(
    id BIGSERIAL PRIMARY KEY,
    payer_id uuid not null unique,
    recipient_id uuid not null unique,
    sum numeric(16,2) not null
);

create table STOCK
(
    id BIGSERIAL PRIMARY KEY,
    type varchar(32) not null,
    price numeric(16,2) not null,
    investor_id BIGINT,
    company_id BIGINT not null,
    foreign key (investor_id) references INVESTOR(id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (company_id) references COMPANY(id) ON UPDATE CASCADE ON DELETE CASCADE
);

create table STOCKS_ORDERS
(
    stock_id BIGINT NOT NULL,
    stock_order_id BIGINT NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES STOCK(id),
    FOREIGN KEY (stock_order_id) REFERENCES STOCK_ORDER(id),
    UNIQUE (stock_id, stock_order_id)
);

create table STOCKS_INVESTORS
(
    stock_id BIGINT NOT NULL,
    individual_id BIGINT,
    broker_id BIGINT,
    FOREIGN KEY (stock_id) REFERENCES STOCK(id),
    FOREIGN KEY (individual_id) REFERENCES INDIVIDUAL(id),
    FOREIGN KEY (broker_id) REFERENCES BROKER(id)
)*/



/*DROP TABLE IF EXISTS "broker" cascade;
DROP TABLE IF EXISTS "stock" cascade;
DROP TABLE IF EXISTS "individual" cascade;
DROP TABLE IF EXISTS "order" cascade;
DROP TABLE IF EXISTS "company" cascade;
DROP TABLE IF EXISTS "individuals_stocks" cascade;
DROP TABLE IF EXISTS "stocks_owners" cascade;
DROP TABLE IF EXISTS "payment" cascade;
*/

CREATE TABLE IF NOT EXISTS COMPANY (
    "id" bigserial PRIMARY KEY NOT NULL,
    "bank_account_id" uuid NOT NULL UNIQUE,
    "capital" numeric(16,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS STOCK (
   "id" bigserial PRIMARY KEY NOT NULL,
   "issued" timestamp NOT NULL,
   "type" varchar(32) NOT NULL,
   "company_id" bigint NOT NULL,
   FOREIGN KEY ("company_id") REFERENCES COMPANY("id") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BROKER (
    "id" bigserial PRIMARY KEY NOT NULL,
    "bank_account_id" uuid NOT NULL UNIQUE,
    "fee" numeric(16,2) NOT NULL,
    "capital" numeric(16,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS INDIVIDUAL (
    "id" bigserial PRIMARY KEY NOT NULL,
    "broker_id" bigint NOT NULL,
    "bank_account_id" uuid NOT NULL UNIQUE,
    "capital" numeric(16,2) NOT NULL,
    FOREIGN KEY ("broker_id") REFERENCES BROKER("id") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "ORDER" (
    "id" bigserial PRIMARY KEY NOT NULL,
    "broker_id" bigint NOT NULL,
    "company_id" bigint NOT NULL,
    "individual_id" bigint NOT NULL,
    "size" bigint NOT NULL,
    "min_price" numeric(16,2),
    "max_price" numeric(16,2),
    "operation_type" varchar(32) NOT NULL,
    "order_status" varchar(32) NOT NULL,
    "public" bool NOT NULL DEFAULT FALSE,
    "timestamp" timestamp NOT NULL,
    "parent_id" bigint,
    FOREIGN KEY ("broker_id") REFERENCES BROKER("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("company_id") REFERENCES COMPANY("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("individual_id") REFERENCES INDIVIDUAL("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("parent_id") REFERENCES "ORDER"("id")
);

CREATE TABLE IF NOT EXISTS INDIVIDUALS_STOCKS (
    "id" bigserial PRIMARY KEY NOT NULL,
    "stock_id" bigint NOT NULL,
    "individual_id" bigint NOT NULL,
    "active" bool NOT NULL,
    FOREIGN KEY ("stock_id") REFERENCES STOCK("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("individual_id") REFERENCES INDIVIDUAL("id") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS STOCKS_OWNERS
(
    "id" bigserial PRIMARY KEY NOT NULL,
    "stock_id" bigint NOT NULL,
    "broker_id" bigint NOT NULL,
    "active" bool NOT NULL,
    FOREIGN KEY ("stock_id") REFERENCES STOCK("id") ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY ("broker_id") REFERENCES BROKER("id") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PAYMENT
(
    "id" bigserial PRIMARY KEY NOT NULL,
    "payer" uuid NOT NULL,
    "recipient" uuid NOT NULL,
    "amount" numeric(16,2) NOT NULL,
    "timestamp" timestamp NOT NULL
)

/*ALTER TABLE "stock" ADD CONSTRAINT "stock_fk0" FOREIGN KEY ("company_id") REFERENCES "company"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "individual" ADD CONSTRAINT "individual_fk0" FOREIGN KEY ("broker_id") REFERENCES "broker"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "order" ADD CONSTRAINT "order_fk0" FOREIGN KEY ("broker_id") REFERENCES "broker"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "order" ADD CONSTRAINT "order_fk1" FOREIGN KEY ("company_id") REFERENCES "company"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "order" ADD CONSTRAINT "order_fk2" FOREIGN KEY ("individual_id") REFERENCES "individual"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "individuals_stocks" ADD CONSTRAINT "individuals_stocks_fk0" FOREIGN KEY ("stock_id") REFERENCES "stock"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "individuals_stocks" ADD CONSTRAINT "individuals_stocks_fk1" FOREIGN KEY ("individual_id") REFERENCES "individual"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "stocks_owners" ADD CONSTRAINT "stocks_owners_fk0" FOREIGN KEY ("stock_id") REFERENCES "stock"("id") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "stocks_owners" ADD CONSTRAINT "stocks_owners_fk1" FOREIGN KEY ("broker_id") REFERENCES "broker"("id") ON UPDATE CASCADE ON DELETE CASCADE;

*/