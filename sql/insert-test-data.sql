INSERT INTO datasets(dataset_id,name) VALUES (0,"test");

INSERT INTO securities(dataset_id,ticker) VALUES (0,"FOO");
INSERT INTO securities(dataset_id,ticker) VALUES (0,"BAR");

INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",9,200);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",10,100);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:01","A",11,100);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:02","B",11,10);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:03","A",10,100);

INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",9,200);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",10,100);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:01","A",11,100);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:02","B",11,10);
INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:03","A",10,100);

