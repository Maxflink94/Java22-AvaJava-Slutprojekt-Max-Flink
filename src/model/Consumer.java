package model;

import java.util.Random;

public class Consumer implements Runnable {
	Buffer buffer = new Buffer();
	Random random = new Random();
	boolean isRunning = true;

	public Consumer(Buffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void run() {
		
		int randomInt = random.nextInt(9001) + 1000;
		
		while (isRunning) {
			try {
				
				Thread.sleep(randomInt);
				System.out.println("Consumed: " + buffer.remove());
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

}
