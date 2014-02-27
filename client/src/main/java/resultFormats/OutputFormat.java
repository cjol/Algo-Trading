package resultFormats;


public interface OutputFormat {
	public void display(boolean delayDisplay);
	public void finishDisplay();
	public void save(String filename);
}
