CREATE TABLE IF NOT EXISTS currencies
(
    currency_id      bigint  NOT NULL,
    currency_code    varchar NOT NULL,
    oldest_price     numeric,
    newest_price     numeric,
    min_price        numeric,
    max_price        numeric,
    normalized_price numeric,
    CONSTRAINT currencies_pkey PRIMARY KEY (currency_id)
);

ALTER TABLE currencies
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS currency_daily_prices
(
    price_id         bigint  NOT NULL,
    currency_code    varchar NOT NULL,
    date             date    NOT NULL,
    oldest_price     numeric,
    newest_price     numeric,
    min_price        numeric,
    max_price        numeric,
    normalized_price numeric,
    CONSTRAINT daily_price_pkey PRIMARY KEY (price_id)
);

ALTER TABLE currency_daily_prices
    OWNER TO postgres;


CREATE INDEX currency_code_idx ON currency_daily_prices (currency_code);

CREATE INDEX date_idx ON currency_daily_prices (date);
