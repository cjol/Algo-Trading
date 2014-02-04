package valueObjects;

public class LowestOffer implements IValued {
	private OfferBid underlyingOffer;
	
	public LowestOffer(OfferBid underlyingOffer) {
		this.underlyingOffer = underlyingOffer;
	}
	
	public OfferBid getUnderlyingOffer() {return underlyingOffer;}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
