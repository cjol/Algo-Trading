package clientLoaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import clientLoaders.Visualiser;
import testHarness.clientConnection.ClassDescription;
import testHarness.clientConnection.OutputRequest;
import testHarness.clientConnection.TestRequestDescription;
import testHarness.clientConnection.TestResultDescription;
import testHarness.output.result.Result;

/**
 * 
 * @author Lawrence Esswood
 */
public class FileLoader {

	private FileLoader() {}
	
	private static String usage = "Usage: FileLoader filePath remoteAddress remotePort output1 output2 output3 ...";
	
	/**
	 * 
	 * @param filename The path to the jar file containing user code
	 * @return A an object to send to the test server
	 * @throws IOException if the jar cannot be read
	 */
	public static TestRequestDescription getRequestFromFile(String filename, List<OutputRequest> outs, String dataset) throws IOException {
		//TODO options
		JarFile jar = new JarFile(filename);
		
		Enumeration<JarEntry> entries = jar.entries();
		List<ClassDescription> classFiles = new LinkedList<ClassDescription>();
		
		//read all class files into byte arrays
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			//ignore anything that is a directory or not a class
			if(entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
			InputStream inStream = jar.getInputStream(entry);
			byte[] classFile = new byte[(int) entry.getSize()];
			int read = inStream.read(classFile);
			if(read != entry.getSize()) throw new IOException("Error reading " + entry.getName());
			
			//name without .class extension
			String name = entry.getName().substring(0, entry.getName().length() - 6).replaceAll("/", ".");
			classFiles.add(new ClassDescription(name, classFile));
		}
		
		jar.close();
		return new TestRequestDescription(classFiles,outs, dataset);
	}
	
	public static TestRequestDescription getRequestFromFile(String filename) throws IOException {
		return FileLoader.getRequestFromFile(filename, null, "unittests");
	}
	
	/**
	 * 
	 * @param testDescription The request object to send to the test server.
	 * @param address The IP address of the test server.
	 * @param port The port number of the server.
	 * @return A result object from the server.
	 * @throws UnknownHostException.
	 * @throws IOException.
	 * @throws ClassNotFoundException.
	 * @throws WrongResponseException if the server response in an unexpected way.
	 */
	public static TestResultDescription sendTest(TestRequestDescription testDescription, String address, int port) throws UnknownHostException, IOException, ClassNotFoundException, WrongResponseException {
		
		Socket s = new Socket(address, port);
		
		ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());
		outStream.writeObject(testDescription);
		
		ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
		Object o = inStream.readObject();
		
		s.close();
		
		if(o instanceof TestResultDescription) return (TestResultDescription) o;
		throw new WrongResponseException();
	}
	
	/**
	 * Displays Usage.
	 */
	private static void showUsage() {
		System.out.println(usage);
		System.exit(1);
	}
	
	public static void main(String[] args) {
		//TODO add options to arguments
		
		if(args.length < 3) showUsage();
		String dataset = "small";
		String file = args[0];
		String address = args[1];
		int port = 0;
		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {showUsage();}
		
		//create request
		TestRequestDescription desc = null;
		List<OutputRequest> outs =  new LinkedList<OutputRequest>();

		for (int i=3; i<args.length; i++) {
			// args[3+] are outputs which the user would like to see
			// TODO: sendResults and saveResults should be specified by the user
			boolean sendResults = true;
			boolean saveResults = false;
			outs.add(new OutputRequest(sendResults, saveResults, args[i]));
		}
		try {
			desc = getRequestFromFile(file, outs, dataset);
		} catch (IOException e) {
			System.err.println("Error reading in jar file.");
			System.exit(2);
		}
		
		
		//send and get result
		List<Result> results = null;
		try {
			TestResultDescription resultDesc = sendTest(desc, address, port);
			if (!resultDesc.testFinished) {
				System.err.println(resultDesc.errorMessage);
				System.exit(7);
			}
			results = resultDesc.outputs;
		} catch (UnknownHostException e) {
			System.err.println("Could not connect to host");
			System.exit(3);
		} catch (ClassNotFoundException e) {
			System.err.println("Response from test server was of unknown type");
			System.exit(4);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Socket error");
			System.exit(5);
		} catch (WrongResponseException e) {
			System.err.println("Response from test server was of incorrect type");
			System.exit(6);
		}
		
		//TODO display result on command line
		if (results != null && results.size() > 0) {
			for (Result result : results) {
				System.out.println(result.getName() + ": ");
				System.out.println(result.asJSON());
				System.out.println();	
			}
			
			Visualiser vis = new Visualiser(results);
			
		}
		System.exit(0);
	}
	
	private static class WrongResponseException extends Exception {
		private static final long serialVersionUID = 1L;}
}
