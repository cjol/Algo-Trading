package valueObjects;

import testHarness.OfferBid;

public class LowestOffer implements IValued {
	//TODO: lowest offer taken from the current book.
	private OfferBid underlyingOffer;
	
	public LowestOffer(OfferBid underlyingOffer) {
		this.underlyingOffer = underlyingOffer;
	}
	
	public OfferBid getUnderlyingOffer() {return underlyingOffer;}

	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		// TODO Auto-generated method stub
		return 0;
	}

}
