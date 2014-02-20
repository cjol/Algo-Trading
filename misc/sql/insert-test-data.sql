INSERT INTO datasets(dataset_id,name) VALUES (0,'unittests');

INSERT INTO securities(dataset_id,ticker) VALUES (0,'FOO');
INSERT INTO securities(dataset_id,ticker) VALUES (0,'BAR');

-- data for FOO
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'FOO','2014-01-01 00:00:00',
			-- bids
			9,200,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'FOO','2014-01-01 00:00:00.5',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'FOO','2014-01-01 00:00:01',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,100,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'FOO','2014-01-01 00:00:02',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,90,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);	
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'FOO','2014-01-01 00:00:03',
			-- bids
			9,200,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,90,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);				

INSERT INTO matches(dataset_id,ticker,ts,price,volume) VALUES (0,'FOO','2014-01-01 00:00:02',11,10);
INSERT INTO matches(dataset_id,ticker,ts,price,volume) VALUES (0,'FOO','2014-01-01 00:00:03',10,100);

-- data for BAR. Completely identical to data for BAR except for change in ticker symbol
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'BAR','2014-01-01 00:00:00',
			-- bids
			9,200,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'BAR','2014-01-01 00:00:00.5',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'BAR','2014-01-01 00:00:01',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,100,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);		
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'BAR','2014-01-01 00:00:02',
			-- bids
			9,200,
			10,100,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,90,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);		
			
INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,
			bid2_price, bid2_volume, bid3_price, bid3_volume,
			bid4_price, bid4_volume, bid5_price, bid5_volume,
			ask1_price, ask1_volume, ask2_price, ask2_volume,
			ask3_price, ask3_volume, ask4_price, ask4_volume,
			ask5_price, ask5_volume)
			VALUES (0,'BAR','2014-01-01 00:00:03',
			-- bids
			9,200,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0,
			-- asks
			11,90,
			NULL,0,
			NULL,0,
			NULL,0,
			NULL,0
			);				

INSERT INTO matches(dataset_id,ticker,ts,price,volume) VALUES (0,'BAR','2014-01-01 00:00:02',11,10);
INSERT INTO matches(dataset_id,ticker,ts,price,volume) VALUES (0,'BAR','2014-01-01 00:00:03',10,100);