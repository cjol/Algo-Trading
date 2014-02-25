package sampleAlgos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import orderBooks.BuyOrder;
import orderBooks.Match;
import orderBooks.Order;
import orderBooks.OrderBook;
import orderBooks.SellOrder;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;
import valueObjects.TickOutOfRangeException;
import database.StockHandle;

public class Depth implements ITradingAlgorithm {
	private static final double DECAY_FACTOR = 0.5; // TODO
	private static final double TRADE_THRESHOLD = 1; // TODO
	private static final int VOLUME_PERCENTAGE = 10;
	
	@Override
	public void run(MarketView market, Options options) {
		new DepthImpl(market).run();
	}
	
	private class DepthImpl {
		private MarketView market;
		private List<StockHandle> stocks;
		private Map<StockHandle,Double> pressure;
		private Map<StockHandle,Integer> volume;
		private List<StockDouble> normalizedPressure;
		
		public DepthImpl(MarketView market) {
			this.market = market;
		}
		
		public void run() {
			stocks = market.getAllStocks();
			
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
				
				do {
					o = orders.next();
					int deltaPrice = Math.abs(bestPrice - o.getPrice());
					priceVolume.addFirst(deltaPrice * o.getVolume());	
				} while (orders.hasNext());
			}
			
			double pressure = 0;
			for (int pv : priceVolume) {
				pressure = pressure * DECAY_FACTOR;
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
				if (sd.val < TRADE_THRESHOLD) {
					break;
				}
				StockHandle s = sd.stock;
				OrderBook o = market.getOrderBook(s);
				
				int totalVolume = volume.get(s);
				int orderVolume = totalVolume * 100 / VOLUME_PERCENTAGE;
				
				try {
					if (sd.val > 0) {
						int bestBid = (int)new HighestBid(o).getValue(0);
						// TODO: Check if we have sufficient funds
						market.buy(s, bestBid, orderVolume);
						break;
					} else {
						int bestOffer = (int)new LowestOffer(o).getValue(0);
						// TODO: Check if we already own stock/cap volume?
						market.sell(s, bestOffer, orderVolume);
					}	
				} catch (TickOutOfRangeException e) {
					// cannot occur: only looking at present values
					throw new RuntimeException(e);
				}
			}
		}
	}
}