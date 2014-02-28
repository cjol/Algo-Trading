package sampleAlgos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import orderBooks.BuyOrder;
import orderBooks.Match;
import orderBooks.Order;
import orderBooks.OrderBook;
import orderBooks.SellOrder;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import valueObjects.TickOutOfRangeException;
import database.StockHandle;

public class Depth implements ITradingAlgorithm {
	@Override
	public void run(MarketView market, Options options) {
		new DepthImpl(market, options).run();
	}
	
	private class DepthImpl {
		private final double decayFactor;
		private final double tradeThreshold;
		private final int volumePercentage;
		private final MarketView market;
		private final List<String> configuredStocks;
		private List<StockHandle> stocks;
		private Map<StockHandle,Double> pressure;
		private Map<StockHandle,Integer> volume;
		private List<StockDouble> normalizedPressure;
		
		public DepthImpl(MarketView market, Options options) {
			decayFactor = Double.parseDouble(options.getParam("decayFactor"));
			tradeThreshold = Double.parseDouble(options.getParam("tradeThreshold"));
			volumePercentage = Integer.parseInt(options.getParam("volumePercentage"));
			
			String rawUserStocks = options.getParam("stocks");
			if (rawUserStocks != null) {
				List<String> userStocks;
				userStocks = Arrays.asList(rawUserStocks.split(","));
				configuredStocks = Collections.unmodifiableList(userStocks);
			} else {
				configuredStocks = null;
			}
			
			this.market = market;
		}
		
		public void run() {
			List<StockHandle> marketStocks = market.getAllStocks();
			if (configuredStocks == null) {
				stocks = marketStocks;
			} else {
				stocks = new LinkedList<StockHandle>();
				for (StockHandle s : marketStocks) {
					if (configuredStocks.contains(s.getTicker())) {
						stocks.add(s);
					}
				}
			}
			
			while (!market.isFinished()) {
				tick(market.tick());
			}
		}
		
		private void tick(Iterator<Match> matches) {
			updatePressures();
			updateVolumes();
			updateNormalizedPressures();
			trade();
		}
		
		private double orderPressure(Iterator<? extends Order> orders) {
			LinkedList<Integer> priceVolume = new LinkedList<Integer>();		
			
			if (orders.hasNext()) {
				int bestPrice = 0;
				Order o;
				o = orders.next();
				bestPrice = o.getPrice();
				
				 while (orders.hasNext()) {
					o = orders.next();
					int deltaPrice = Math.abs(bestPrice - o.getPrice());
					priceVolume.addFirst(deltaPrice * o.getVolume());	
				}
			}
			
			double pressure = 0;
			for (int pv : priceVolume) {
				pressure = pressure * decayFactor;
				pressure += pv;
			}
			
			return pressure;
		}
		
		private double calculatePressure(StockHandle s) {
			OrderBook ob = market.getOrderBook(s);
			
			Iterator<BuyOrder> bids = ob.getAllBids();
			Iterator<SellOrder> offers = ob.getAllOffers();
			
			return orderPressure(bids) - orderPressure(offers);
		}
		
		private void updatePressures() {
			pressure = new HashMap<StockHandle, Double>();
			for (StockHandle s : stocks) {
				pressure.put(s, calculatePressure(s));
			}
		}
		
		private int orderVolume(Iterator<? extends Order> orders) {
			int volume = 0;
			while (orders.hasNext()) {
				volume += orders.next().getVolume();
			}
			return volume;
		}
		
		private int calculateVolume(StockHandle s) {
			OrderBook ob = market.getOrderBook(s);
			
			Iterator<BuyOrder> bids = ob.getAllBids();
			Iterator<SellOrder> offers = ob.getAllOffers();
			
			return orderVolume(bids) + orderVolume(offers);
		}
		
		private void updateVolumes() {
			volume = new HashMap<StockHandle, Integer>();
			for (StockHandle s : stocks) {
				volume.put(s, calculateVolume(s));
			}
		}
		
		private class StockDouble implements Comparable<StockDouble> {
			public final StockHandle stock;
			public final double val;
			
			public StockDouble(StockHandle stock, double val) {
				this.stock = stock;
				this.val = val;
			}
			
			public int compareTo(StockDouble sv) {
				return Double.compare(Math.abs(val), Math.abs(sv.val));
			}
		}
		
		private void updateNormalizedPressures() {
			normalizedPressure = new ArrayList<StockDouble>();
			for (StockHandle s : stocks) {
				int v = volume.get(s);
				double p = pressure.get(s);
				double normalized = p / v;
				StockDouble sd = new StockDouble(s, normalized);
				normalizedPressure.add(sd);
			}
			
			Collections.sort(normalizedPressure);
		}

		private void trade() {
			// iterate over stocks in order of descending pressure
			// make an aggressive trade on the first one we can that is
			// above threshold
			for (StockDouble sd : normalizedPressure) {
				if (Math.abs(sd.val) < tradeThreshold) {
					break;
				}
				StockHandle s = sd.stock;
				OrderBook o = market.getOrderBook(s);
				
				int totalVolume = volume.get(s);
				int orderVolume = totalVolume * volumePercentage / 100;
				
				try {
					if (sd.val > 0) {
						int bestOffer = (int)o.getLowestOffer().getValue(0);
						int totalPrice = bestOffer * orderVolume;
						// buy at most what we have the capital to purchase
						totalPrice = Math.min(totalPrice, market.getFundsLessCommission().intValue());
						orderVolume = totalPrice / bestOffer;
						if (!market.buy(s, bestOffer, orderVolume)) {
							System.err.println("error buying");
						}
					} else {
						int bestBid = (int)o.getHighestBid().getValue(0);
						
						Iterator<Entry<StockHandle, Integer>> portfolio = market.getPortfolio();
						int amountWeOwn = 0;
						while (portfolio.hasNext()) {
							Entry<StockHandle,Integer> position = portfolio.next();
							if (position.getKey().equals(s)) {
								amountWeOwn = position.getValue();
							}
						}
						if (amountWeOwn > 0) {
							orderVolume = Math.min(orderVolume, amountWeOwn);
							if (!market.sell(s, bestBid, orderVolume)) {
								System.err.println("error selling");
							}
						}
					}	
					break;
				} catch (TickOutOfRangeException e) {
					// no data in order book -- try again
					continue; 
				}
			}
		}
	}
}