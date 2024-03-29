CREATE TABLE datasets (
	dataset_id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

CREATE TABLE securities (
	dataset_id serial REFERENCES datasets(dataset_id),
	ticker varchar(10),
	PRIMARY KEY (dataset_id, ticker)
);

CREATE table order_books (
	dataset_id serial,
	ticker varchar(10),
	ts timestamp,
 	bid1_price integer,
 	bid1_volume integer NOT NULL,
 	bid2_price integer,
 	bid2_volume integer NOT NULL,
 	bid3_price integer,
 	bid3_volume integer NOT NULL,
 	bid4_price integer,
 	bid4_volume integer NOT NULL,
 	bid5_price integer,
 	bid5_volume integer NOT NULL,
	ask1_price integer,
 	ask1_volume integer NOT NULL,
	ask2_price integer,
 	ask2_volume integer NOT NULL,
 	ask3_price integer,
 	ask3_volume integer NOT NULL,
 	ask4_price integer,
 	ask4_volume integer NOT NULL,
 	ask5_price integer,
 	ask5_volume integer NOT NULL,
 	PRIMARY KEY (dataset_id, ticker, ts),
 	FOREIGN KEY (dataset_id, ticker) REFERENCES securities (dataset_id, ticker)
);
	
CREATE table matches (
	match_id bigserial PRIMARY KEY,
	dataset_id serial,
	ticker varchar(10),
	ts timestamp NOT NULL,
	price integer NOT NULL,
	volume integer NOT NULL,
	FOREIGN KEY (dataset_id, ticker) REFERENCES securities (dataset_id, ticker)
);

CREATE INDEX orderbook_idx ON order_books (ts);
CREATE INDEX matches_idx ON matches (ts);