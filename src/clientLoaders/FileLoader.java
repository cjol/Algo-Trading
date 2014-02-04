package clientLoaders;

import testHarness.clientConnection.TestRequestDescription;
import testHarness.clientConnection.TestResultDescription;

public class FileLoader {

	public TestRequestDescription getRequestFromFile() {
		return null;
		//TODO maybe include options in file, or specify on command line?
	}
	
	public TestResultDescription sendTest(TestRequestDescription testDescription) {
		return null;
		//TODO should connect to connection server, send test, recieve result.
	}
}
