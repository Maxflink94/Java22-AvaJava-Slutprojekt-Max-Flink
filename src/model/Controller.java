package model;

import java.util.Random;

import view.GUI;

public class Controller {

	public static void main(String[] args) {

		Buffer buffer = new Buffer();
		
		GUI GUI = new GUI(buffer);
		Thread GUIThread = new Thread(GUI);
		
		GUIThread.start();
		
		// Random consumer mellan 3 och 15
		Random random = new Random();
		int consumerAmount = 3 + random.nextInt(13);
		
		// Skapar ett random antal consumers mellan 3 och 15
		for(int i = 0; i<consumerAmount; i++) {
			Consumer consumer = new Consumer(buffer);
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();
		}
		
	}	
	// Göra klassdiagram
	// En beskrivning	
}
