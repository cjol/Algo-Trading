package valueObjects;

import orderBookReconstructor.SellOrder;

public class LowestOffer implements IValued {
	//TODO: lowest offer taken from the current book.
	private SellOrder underlyingOffer;
	
	public LowestOffer(SellOrder underlyingOffer) {
		this.underlyingOffer = underlyingOffer;
	}
	
	public SellOrder getUnderlyingOffer() {return underlyingOffer;}

	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		// TODO Auto-generated method stub
		return 0;
	}

}
