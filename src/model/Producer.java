package model;

import java.util.Random;

public class Producer implements Runnable {

	Buffer buffer = new Buffer();
	Random random = new Random();
	boolean isRunning = true;
	private double randomInt;

	public Producer(Buffer buffer) {
		this.buffer = buffer;
		this.randomInt = random.nextInt(9001) + 1000;
	}

	@Override
	public void run() {

		while (isRunning) {
			try {

				Thread.sleep((long) randomInt);

				buffer.add(new Item("" + ((int) Math.floor((Math.random() * 101)))));

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/**
	 * En get metod för producers arbetsintervall
	 * @return
	 */
	public double getRandomInt() {
		return randomInt/1000;
	}
}
