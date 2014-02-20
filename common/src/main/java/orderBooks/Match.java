package orderBooks;

import database.StockHandle;

public class Match {
	public final StockHandle stockHandle;
	
	public final int quantity;
	public final int price;
	
	public final boolean isUserOffer;
	public final boolean isUserBid;
	
	public Match(StockHandle stock,int price, int quantity) {
		this.stockHandle = stock;
		this.quantity = quantity;
		this.price = price;
		this.isUserBid = false;
		this.isUserOffer = false;
	}
	
	public Match(StockHandle stock, int price, int quantity, boolean isUserOffer) {
		this.stockHandle = stock;
		this.quantity = quantity;
		this.price = price;
		this.isUserOffer = isUserOffer;
		this.isUserBid = ! isUserOffer;
	}
	
	/*
	public Order getOrder() {
		//FIXME do we really need a clone?
		
		try {
			return (Order) order.clone();
		} catch (CloneNotSupportedException e) {
			//Shouldn't happen (?)
			return null;
		}
	}
	*/
}
