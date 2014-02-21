package testHarness.clientConnection;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import testHarness.ITradingAlgorithm;
import testHarness.output.Output;
import testHarness.output.result.Result;
import database.OutputServer;

/**
 *	A communication object between the server and client that describes a test request. 
 * @author Lawrence Esswood
 *
 */
public class TestRequestDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected List<ClassDescription> classFiles;
	protected List<OutputRequest>  outputsRequested;
	
	public final String datasetName;
	
	/**
	 * 
	 * @param classFiles The class files the client wrote.
	 * @param outputsRequested The outputs the client would like to be given.
	 */
	public TestRequestDescription(List<ClassDescription> classFiles, List<OutputRequest> outputsRequested) {
		this.classFiles = classFiles;
		this. outputsRequested = outputsRequested;
		this.datasetName = null;
	}
	
	/**
	 * 
	 * @param classFiles The class files the client wrote.
	 * @param outputsRequested The outputs the client would like to be given.
	 * @param dataset The string ID for the dataset to be tested with
	 */
	public TestRequestDescription(List<ClassDescription> classFiles, List<OutputRequest> outputsRequested, String dataset) {
		this.classFiles = classFiles;
		this. outputsRequested = outputsRequested;
		this.datasetName = dataset;
	}
	
	/**
	 * Builds a response given a request and the finished outputs.
	 * @param request The original request.
	 * @param outputs The finished outputs.
	 * @return
	 */
	public static TestResultDescription filterOutputs(TestRequestDescription request, List<Output> outputs) {
		
		//build output result based on which outputRequests had the respond flag.
		List<String> requested = new LinkedList<String>();
		if(request.outputsRequested != null) {
			for(OutputRequest outputRequest : request.outputsRequested) {
				if(outputRequest.respond) requested.add(outputRequest.name);
			}
		}
		
		List<Result> results = new LinkedList<Result>();
		for(Output output : outputs) {
			if(requested.contains(output.getClass().getName())) {
				results.add(output.getResult());
			}
		}
		
		return new TestResultDescription(results);
	}
	
	/**
	 * Loads user code and performs reflection.
	 * @param request The client request
	 * @return The class with the entry point of the user code.
	 * @throws LoadClassException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static ITradingAlgorithm getAlgo(TestRequestDescription request) throws LoadClassException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Permissions permissions = new Permissions();
		ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
		netClassLoader netLoader = new netClassLoader(SecureClassLoader.getSystemClassLoader(), protectionDomain);
		
		Class<?> tradingClass = null;
		
		//loads each class and remembers the first class found with the correct inteface
		for(ClassDescription classFile : request.classFiles) {
			Class<?> newClass = netLoader.defineNewClass(classFile.name, classFile.definition);
			
			if(tradingClass == null) {
				for(Class<?> intrfce : newClass.getInterfaces()) {
					if(intrfce.equals(ITradingAlgorithm.class)){
						tradingClass = newClass;
						break;
					}
				}
			}
		}
		
		if(tradingClass == null) throw new LoadClassException("No class found with correct interface");
		
		//Construct an instance of the class we think has the trading interface. Must have a constructor with 0 arguments.
		Object o = tradingClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);
		if(o instanceof ITradingAlgorithm) return (ITradingAlgorithm)o;
		
		throw new LoadClassException("Could not cast to ITradingAlgorithm");
		

	}
	
	/**
	 * Builds outputs from a user request.
	 * @param request The users request.
	 * @param server The output server where outputs are stored.
	 * @return A list of outputs usable by a MarketView.
	 * @throws ClassNotFoundException
	 * @throws LoadClassException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static List<Output> getOutputs(TestRequestDescription request, OutputServer server) throws ClassNotFoundException, LoadClassException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LinkedList<Output> outputs = new LinkedList<Output>();
		
		//Makes the server work in Maven? 
		ClassLoader loader = TestRequestDescription.class.getClassLoader();
		
		Set<Class<?>> disjoint = new HashSet<Class<?>>();
		
		//construct outputs from requests
		if(request.outputsRequested != null) {
			for(OutputRequest outputRequest : request.outputsRequested) {
				Class<?> classRequest = loader.loadClass(outputRequest.name);
				
				//remove duplicates, FIXME: This should potentially be an error
				if(disjoint.contains(classRequest)) continue;
				
				disjoint.add(classRequest);
				
				Constructor<?> outputConstructor = classRequest.getConstructor(new Class<?>[]{OutputServer.class});
				
				//pass in server if user requested data should be committed
				Object o = outputConstructor.newInstance(outputRequest.commitToDB ? server : null);
				
				if(o instanceof Output) {
					outputs.add((Output) o);
				} else throw new LoadClassException("an output as request that was not a subclass of Output");
			}
		}
		
		return outputs;
	}
	
	/**
	 * A class loader that loads classes given a byte array.
	 * @author Lawrence Esswood
	 *
	 */
	private static class netClassLoader extends SecureClassLoader {
		private final ProtectionDomain domain;
		public netClassLoader(ClassLoader parent, ProtectionDomain pd) {
			super(parent);
			domain = pd;
		}
		
		public Class<?> defineNewClass(String name, byte[] bytes) {
			Class<?> newClass = defineClass(name, bytes, 0, bytes.length, domain);
			resolveClass(newClass);
			return newClass;
		}
	}
	
	public static class LoadClassException extends Exception {
		private static final long serialVersionUID = 1L;

		public LoadClassException(String message) { super(message);}
	}
}
