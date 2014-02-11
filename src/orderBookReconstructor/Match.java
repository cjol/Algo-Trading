package orderBookReconstructor;

public class Match {
	private final Order order;
	
	public Match(Order order) {
		this.order = order;
	}
	
	public Order getOrder() {
		try {
			return (Order) order.clone();
		} catch (CloneNotSupportedException e) {
			//Shouldn't happen (?)
			return null;
		}
	}
}
