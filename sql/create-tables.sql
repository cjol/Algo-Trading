CREATE TABLE datasets (
	dataset_id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

CREATE TABLE securities (
	dataset_id serial REFERENCES datasets(dataset_id),
	ticker varchar(10) REFERENCES trades(ticker),
	PRIMARY KEY (dataset_id, ticker)
);
	
CREATE table trades (
	trade_id bigserial PRIMARY KEY,
	dataset_id serial REFERENCES datasets(dataset_id) NOT NULL,
	ticker varchar(10) NOT NULL,
	ts timestamp NOT NULL,
	bid_or_ask char(1) NOT NULL CHECK (bid_or_ask = 'A' OR bid_or_ask = 'B'),
	price integer NOT NULL,
	volume integer NOT NULL
);

CREATE INDEX trade_idx ON trades (ts);