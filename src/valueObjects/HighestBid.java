package valueObjects;

import java.math.BigDecimal;

import orderBookReconstructor.BuyOrder;

public class HighestBid implements IValued {
	//TODO: highest bid taken from the current order book
	private BuyOrder underlyingBid;
	
	public HighestBid(BuyOrder underlyingBid) {
		this.underlyingBid = underlyingBid;
	}
	
	public BuyOrder getUnderlyingBid() {return underlyingBid;}

	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		// TODO Auto-generated method stub
		return null;
	}

}
