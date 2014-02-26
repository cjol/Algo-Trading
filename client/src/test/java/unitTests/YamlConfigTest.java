package unitTests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Test;

import config.YamlConfig;
import config.YamlOutput;
import config.YamlParam;

/**
 * @author Lawrence Esswood
 *
 */
public class YamlConfigTest {

	/**
	 * Checks to see if YAML is read correctly from file into a config object
	 * @throws FileNotFoundException
	 * @throws URISyntaxException 
	 */
	@Test
	public void test() throws FileNotFoundException, URISyntaxException {
		YamlConfig config = YamlConfig.loadFromFile(new File(getClass().getResource("/testConfig.yaml").toURI()).getPath());
		
		assertEquals(config.dataset, "small");
		assertEquals(config.startingFunds, 10000);
		assertEquals(config.tickSize, 100);
		assertEquals(config.timeout, 60000);
		
		assertTrue(config.outputs.size() == 2);
		YamlOutput o1 = config.outputs.get(0);
		YamlOutput o2 = config.outputs.get(1);
		
		assertEquals(o1.name, "out1");
		assertEquals(o1.respond, true);
		assertEquals(o1.commit, false);
		
		assertEquals(o2.name, "out2");
		assertEquals(o2.respond, false);
		assertEquals(o2.commit, true);
		
		assertTrue(config.params.size() == 3);
		YamlParam p1 = config.params.get(0);
		YamlParam p2 = config.params.get(1);
		YamlParam p3 = config.params.get(2);
		
		assertEquals(p1.name, "myParam1");
		assertEquals(p1.value, "Hello World");
		
		assertEquals(p2.name, "myParam2");
		assertEquals(p2.value, "foobar");
		
		assertEquals(p3.name, "myParam3");
		assertEquals(p3.value, "123");
	}

}
