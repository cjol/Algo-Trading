package orderBookReconstructor;

import java.util.HashSet;
import java.util.PriorityQueue;

/*
 * Encapsulates all orders in an order book at the same price level.
 */
public class PriceLevel<O extends Order> {
	private PriorityQueue<O> orders;
	private int price;
	
	public PriceLevel(int price) {
		this.orders = new PriorityQueue<>();
		this.price = price;
	}
	
	public PriorityQueue<O> getOrders() {
		return orders;
	}

	public int getPrice() {
		return price;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PriceLevel<?>)) return false;
		PriceLevel<Order> that = (PriceLevel<Order>)o;
		return (that.price == this.price && 
				new HashSet<>(this.orders).equals(new HashSet<>(that.orders)));
	}
}
