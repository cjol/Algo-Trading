package valueObjects;

public class HighestBid implements IValued {
	//TODO: highest bid taken from the current order book
	private OfferBid underlyingBid;
	
	public HighestBid(OfferBid underlyingBid) {
		this.underlyingBid = underlyingBid;
	}
	
	public OfferBid getUnderlyingBid() {return underlyingBid;}

	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		// TODO Auto-generated method stub
		return 0;
	}

}
