package orderBookReconstructor;

public class Match {
	private final Order order;
	public final int quantity;
	public Match(Order order, int quantity) {
		this.order = order;
		this.quantity = quantity;
	}
	
	public Order getOrder() {
		//FIXME do we really need a clone?
		
		try {
			return (Order) order.clone();
		} catch (CloneNotSupportedException e) {
			//Shouldn't happen (?)
			return null;
		}
	}
}
