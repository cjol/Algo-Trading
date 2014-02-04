package valueObjects;

public class HighestBid implements IValued {
	private OfferBid underlyingBid;
	
	public HighestBid(OfferBid underlyingBid) {
		this.underlyingBid = underlyingBid;
	}
	
	public OfferBid getUnderlyingBid() {return underlyingBid;}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
