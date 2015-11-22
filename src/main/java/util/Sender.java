package util;

import org.apache.commons.cli.ParseException;

import flexsc.Flag;

public class Sender<T> extends network.Client implements Runnable {

	public void run() {
		try {
			connect("localhost", 54321);
			System.out.println("connected");

			while(true) {
				double t = System.nanoTime();
				readBytes(65536);
				double t2 = System.nanoTime();
				System.out.println((t2-t)/1000000000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ParseException, ClassNotFoundException {
		Sender r = new Sender();
		r.run();
		Flag.sw.print();

	}
}
