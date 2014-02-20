package unitTests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import testHarness.clientConnection.OutputRequest;
import testHarness.clientConnection.TestRequestDescription;
import testHarness.clientConnection.TestRequestDescription.LoadClassException;
import testHarness.output.Output;
import database.OutputServer;

public class OptionRequestTest {

	@Test
	public void test() {
		List<OutputRequest> requests = new LinkedList<OutputRequest>();
		requests.add(new OutputRequest(true, true, "unitTests.TestOutput"));
		
		TestRequestDescription req = new TestRequestDescription(null, requests);
		
		try {
			List<Output> outs = TestRequestDescription.getOutputs(req, new OutputServer());
			if(outs.size() != 1) fail("output not present in list");
			if(!(outs.get(0) instanceof TestOutput)) fail("output of wrong class");
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | LoadClassException e) {
			e.printStackTrace();
			fail("failed to construct outputs");
		}

	}

}
