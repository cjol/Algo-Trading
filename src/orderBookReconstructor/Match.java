package orderBookReconstructor;

import java.math.BigDecimal;

public class Match {
	public final BuyOrder buyOrder;
	public final SellOrder sellOrder;
	public final int quantity;
	public final BigDecimal price;
	
	public final boolean isUserOffer;
	public final boolean isUserBid;
	
	public Match(BuyOrder buyOrder, SellOrder sellOrder, int quantity, BigDecimal price) {
		this.buyOrder = buyOrder;
		this.sellOrder = sellOrder;
		this.quantity = quantity;
		this.price = price;
		this.isUserBid = false;
		this.isUserOffer = false;
	}
	
	public Match(BuyOrder buyOrder, SellOrder sellOrder, int quantity, BigDecimal price, boolean isUserOffer) {
		this.buyOrder = buyOrder;
		this.sellOrder = sellOrder;
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
