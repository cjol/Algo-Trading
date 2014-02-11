package orderBookReconstructor;

public class Match {
	public final BuyOrder buyOrder;
	public final SellOrder sellOrder;
	public final int quantity;
	public final int price;
	public Match(BuyOrder buyOrder, SellOrder sellOrder, int quantity, int price) {
		this.buyOrder = buyOrder;
		this.sellOrder = sellOrder;
		this.quantity = quantity;
		this.price = price;
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
