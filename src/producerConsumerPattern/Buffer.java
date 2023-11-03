package producerConsumerPattern;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {

	public Queue<Item> buffer = new LinkedList<Item>();

	public synchronized void add(Item item) {
		buffer.add(item);
		notify();
		System.out.println(buffer);
	}

	public synchronized Item remove() {
		if (buffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return buffer.remove();
	}

}
