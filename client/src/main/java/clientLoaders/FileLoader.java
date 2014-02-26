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

import config.YamlConfig;
import clientLoaders.Visualiser;
import testHarness.clientConnection.ClassDescription;
import testHarness.clientConnection.Options;
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
	
	private static String usage = "Usage: FileLoader jarFilePath remoteAddress remotePort [configFilePath]";
	
	/**
	 * 
	 * @param jarFilename the Jar file containing user code.
	 * @param yamlFilename The yaml file containing config.
	 * @return A an object to send to the test server.
	 * @throws IOException if a file cannot be read.
	 */
	public static TestRequestDescription getRequestFromFile(String jarFilename, String yamlFilename) throws IOException {
		YamlConfig yc = null;
		if (yamlFilename != null) {
			try {
				yc = YamlConfig.loadFromFile(yamlFilename);
			} catch (IOException e) {
				System.err.println("Error reading YAML config " + yamlFilename);
				throw(e);
			}	
		}
		
		List<ClassDescription> classFiles;
		try {
			classFiles = loadClassFiles(jarFilename);
		} catch (IOException e) {
			System.err.println("Error reading JAR " + jarFilename);
			throw(e);
		}
		
		if (yamlFilename != null) {
			return new TestRequestDescription(classFiles, yc);	
		} else {
			return new TestRequestDescription(classFiles);
		}
	
	}
	
	public static TestRequestDescription getRequestFromFile(String filename) throws IOException {
		return getRequestFromFile(filename, null);
	}
	
	/**
	 * 
	 * @param jarFilename the Jar file containing user code.
	 * @return
	 * @throws IOException
	 */
	public static List<ClassDescription> loadClassFiles(String jarFilename) throws IOException {
		JarFile jar = new JarFile(jarFilename);
		
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
		return classFiles;
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
		if(args.length < 3 || args.length > 4) showUsage();
		String file = args[0];
		String address = args[1];
		int port = 0;
		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {showUsage();}
		
		//create request
		TestRequestDescription desc = null;
		
		try {
			desc = (args.length == 4) ? getRequestFromFile(file, args[3]) : getRequestFromFile(file);
		} catch (IOException e) {
			// error message printed in getRequestFromFile 
			// as it can disambiguate where IO error occurred
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
