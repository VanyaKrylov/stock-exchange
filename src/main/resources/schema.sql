-- drop table if exists TEST;
drop table if exists INVESTOR;
drop table if exists BROKER;
drop table if exists COMPANY;
drop table if exists FINANCIAL_REPORT;
drop table if exists INDIVIDUAL;
drop table if exists STOCK_ORDER;
drop table if exists LIMITED_ORDER;
drop table if exists MARKET_ORDER;
drop table if exists PAYMENT;
drop table if exists STOCK;

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
    fee NUMERIC(16,2) not null,
    foreign key (id) references INVESTOR(id)
);

create table COMPANY
(
    id BIGSERIAL PRIMARY KEY,
    bank_account_id uuid not null unique,
    capital NUMERIC(16,2) not null
);

create table FINANCIAL_REPORT
(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT not null,
    earnings NUMERIC(16,2) not null,
    expenses NUMERIC(16,2) not null,
    capital NUMERIC(16,2) not null,
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
    investor_id BIGINT not null,
    operation_type varchar(32) not null,
    foreign key (investor_id) references INVESTOR(id) ON UPDATE CASCADE ON DELETE CASCADE
);

create table LIMITED_ORDER
(
    id BIGINT PRIMARY KEY,
    max_price NUMERIC(16,2) NOT NULL,
    min_price NUMERIC(16,2) NOT NULL,
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
    sum NUMERIC(16,2) not null
);

create table STOCK
(
    id BIGSERIAL PRIMARY KEY,
    type varchar(32) not null,
    investor_id BIGINT,
    company_id BIGINT not null,
    foreign key (investor_id) references INVESTOR(id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (company_id) references COMPANY(id) ON UPDATE CASCADE ON DELETE CASCADE
);
