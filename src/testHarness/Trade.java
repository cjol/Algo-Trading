package testHarness;


public class Trade {
	// Trade objects are deliberately immutable
	private StockHandle s;
	private int v;
	private int p;
	public Trade (StockHandle stock, int volume, int price) {
		s = stock;
		v = volume;
		p = price;
	}
	public int getPrice() {
		return p;
	}
	private void setPrice(int p) {
		this.p = p;
	}
	public int getVolume() {
		return v;
	}
	private void setVolume(int v) {
		this.v = v;
	}
	public StockHandle getStockHandle() {
		return s;
	}
	private void setStockHandle(StockHandle s) {
		this.s = s;
	}
	
}
