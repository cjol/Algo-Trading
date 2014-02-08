package testHarness.clientConnection;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;
import testHarness.IOutput;
import testHarness.ITradingAlgorithm;

public class TestRequestDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<ClassDescription> classFiles = new LinkedList<ClassDescription>();
	
	public TestRequestDescription(List<ClassDescription> classFiles) {
		this.classFiles = classFiles;
	}
	
	
	public ITradingAlgorithm getAlgo() throws LoadClassException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Permissions permissions = new Permissions();
		ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
		netClassLoader netLoader = new netClassLoader(SecureClassLoader.getSystemClassLoader(), protectionDomain);
		
		Class<?> tradingClass = null;
		
		//loads each class and remembers the first class found with the correct inteface
		for(ClassDescription classFile : classFiles) {
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
	
	public List<IOutput> getOutputs(OutputServer server) {
		//TODO
		return null;
	}
	
	
	private class netClassLoader extends SecureClassLoader {
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
