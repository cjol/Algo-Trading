package naughty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;


public class Naughty implements ITradingAlgorithm {

	private int i = 1;
	@Override
	public void run(MarketView marketView) {
		int test = i++;
		switch(test) {
		
		//try get a new manager
		case 0:
			try {
				System.setSecurityManager(new SecurityManager());
				System.out.print(false);
			} catch (SecurityException e) {System.out.print(true);}
			
			
		//open the secret file
		case 1:
			File f = new File("verysecret.txt");
			BufferedWriter writer;
			//try read it
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(f));
				reader.readLine();
				System.out.print(false);
			} catch (IOException | SecurityException e) {System.out.print(true);}
			return;

			//try to write to it
		case 2:
			File f2 = new File("verysecret.txt");
			try {
				writer = new BufferedWriter(new FileWriter(f2));
				writer.write("MWA HA HA HA HA HA");
				writer.flush();
				System.out.print(false);
			} catch (IOException | SecurityException e) {System.out.print(true);}
			return;

		case 3:
			//try to delete it
			try { 
				File f3 = new File("verysecret.txt");
				f3.delete();
				System.out.print(false);
			} catch (SecurityException e) {System.out.print(true);};
			return;

			//try open a socket
		case 4:
			try {
				ServerSocket s = new ServerSocket(1337);
				s.close();
				System.out.print(false);
			} catch (IOException | SecurityException e) {System.out.print(true);}
			return;

			//try execute a horrible command
		case 5:
			try {
				Runtime.getRuntime().exec("ls");
				System.out.print(false);
			} catch (IOException | SecurityException e) {System.out.print(true);}
			return;


			//try exit
		case 6:
			try {
				System.exit(0);
			} catch (SecurityException e) {System.out.print(true);}
			//do something we are allowed to do.
		case 7:
			try {
				marketView.getAllStocks();
				System.out.print(true);
			} catch(SecurityException e) {System.out.print(false);
			}
		}
	}
}
