package config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlConfig {
	
	public List<YamlOutput> outputs;
	public List<YamlParam> params;
	
	public String dataset;
	public int tickSize;
	public int maxTicks;
	public int timeout;
	public int startingFunds;
	
	public static YamlConfig loadFromFile(String s) throws FileNotFoundException {
		Yaml y = new Yaml(new Constructor(YamlConfig.class));
		
		YamlConfig c = (YamlConfig) y.load(new FileReader(new File(s)));
		return c;
	}
}
