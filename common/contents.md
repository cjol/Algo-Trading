Packages used by both the client and the server:

* database: only StockHandle, the rest is only exposed to the server.
* orderBookReconstructor: user order books, buy/sell orders
* testHarness (not .clientConnection or .output) : MarketView and the algo interface
* valueObjects
