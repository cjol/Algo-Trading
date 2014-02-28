package testHarness.output.result;

import java.io.Serializable;

import org.json.JSONObject;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String jsonData;
	private transient JSONObject jsonObject = null;
	private String name;
	private String slug;

	public String getName() {
		return name;
	}
	
	public JSONObject getJsonObject() {
		if (jsonObject == null) {
			jsonObject = new JSONObject(jsonData);
		}
		return jsonObject;
	}

	public String getSlug() {
		return slug;
	}
	
	/**
	 * 
	 * @param slug the identifier for the output which generated this result, as specified in the YAML config file
	 * @param name the human-readable name for this result
	 * @param d the data for this result
	 */
	public Result(String slug, String name, JSONObject d) {
		this.name = name;
		this.slug = slug;
		this.jsonObject = d;
		this.jsonData = d.toString();
	}
	
	public String asJSON() {
		return jsonData;
	}
}
