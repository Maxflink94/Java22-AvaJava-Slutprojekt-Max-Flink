package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import log.LogInfo;
import model.Buffer;
import model.Producer;

public class GUI implements Runnable {

	private Buffer buffer;
	private JProgressBar progressBar;
	private JTextArea logTextArea;
	private Timer timer;
	private Timer updateAvailableUnitsTimer;
	private String path = "src/log/ProductionLog.dat";
	private LogInfo log;
	private Queue<Thread> producerThreadQueue;
	private int workerAmount = 0;
	private int availableUnits = 0;
	private int previousValue = 0;

	public GUI(Buffer buffer) {
		this.buffer = buffer;
		this.producerThreadQueue = new LinkedList<>();
		this.logTextArea = new JTextArea(25, 50);
		this.log = new LogInfo(path, logTextArea);
	}

	public void createAndShowGUI(Buffer buffer) {
		
		// Instansierar GUI
		GUI gui = new GUI(buffer);

		// JFrame-fönster
		JFrame frame = new JFrame("Utbud & Efterfrågan");
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Skapar en mainPanel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// TextArea inställningar
		logTextArea = new JTextArea(25, 50);
		logTextArea.setEditable(false);
		logTextArea.setLineWrap(true);
		logTextArea.setWrapStyleWord(true);

		// Skapar en scrollPane och lägger in logTextArea
		JScrollPane scrollArea = new JScrollPane(logTextArea);
		scrollArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Skapa progress Bar
		gui.progressBar = new JProgressBar(0, 100);
		gui.progressBar.setStringPainted(true);

		// Panel för att hålla i ProgressBaren
		JPanel progressBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		progressBarPanel.add(gui.progressBar);

		// Lägger till arbetare knapp
		JButton addButton = new JButton("Lägg till");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Producer producer = new Producer(buffer);
				Thread producerThread = new Thread(producer);

				producerThread.start();

				gui.producerThreadQueue.add(producerThread);

				gui.updateProgressBar();

				gui.workerAmount++;

				// Loggar antalet arbetare och deras produktionsintervall till programloggen
				writeToLogAndAppend("Lade till en arbetare med arbetsintervallet " + producer.getRandomInt()
						+ "s. Antal arbetare: " + gui.workerAmount);
			}
		});

		// Ta bort arbetare knapp
		JButton removeButton = new JButton("Ta bort");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!gui.producerThreadQueue.isEmpty()) {
					Thread removeThread = gui.producerThreadQueue.poll();
					removeThread.interrupt();
					gui.workerAmount--;

					// Loggar antalet arbetare till programloggen
					writeToLogAndAppend("Tog bort en arbetare. Antal arbetare: " + gui.workerAmount);

				} else {
					// Pop-up för att meddela användaren att det inte finns några arbetare att ta bort
					JOptionPane.showMessageDialog(null, "Det finns inga arbetare att ta bort.", "Fel",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Skapar en panel till knapparna
		JPanel buttonPanel = new JPanel();
		logTextArea.setVisible(true);

		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);

		scrollArea.add(gui.logTextArea);

		mainPanel.add(progressBarPanel, BorderLayout.NORTH);
		mainPanel.add(scrollArea, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		frame.add(mainPanel);
		frame.setVisible(true);

		// Timer för uppdatering av Progressbar
		gui.timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.updateProgressBar();
			}
		});
		gui.timer.start();

		// Lyssnar på när värdena av progressbar ändras
		changeListenerProgressBar(gui.progressBar);

		// Timer för att titta hur många enheter som är tillgängliga var 10e sekund
		updateAvailableUnitsTimer = new Timer(10000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAvailableUnits();
			}
		});
		updateAvailableUnitsTimer.start();
	}

	@Override
	public void run() {
		createAndShowGUI(buffer);
	}
	
	/**
	 * Metod för att updatera progressbaren
	 */
	private void updateProgressBar() {
		int size = buffer.buffer.size();
		progressBar.setValue(size);
	}

	/**
	 * Metod för att skriva ut antalet enheter i "lager" var 10e sekund
	 */
	private void updateAvailableUnits() {
		availableUnits = buffer.buffer.size();
		String message = "Antal tillgängliga enheter: " + availableUnits;
		writeToLogAndAppend(message);
	}

	/**
	 * Metod för att skriva till Log och även append till textArea
	 * @param message
	 */
	public void writeToLogAndAppend(String message) {
		log.writeData(message);
		logTextArea.append(message + "\n");
	}

	/**
	 * ChangeListener + byta färg (för Windows)
	 * @param progressBar
	 */
	private void changeListenerProgressBar(JProgressBar progressBar) {
		progressBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = progressBar.getValue();
				if (value < 20 && previousValue >= 20) {
					String message = "Under 20%";
					writeToLogAndAppend(message);
					// Byta färg (Fungerar inte på Mac)
					progressBar.setForeground(UIManager.getColor(Color.RED));
				} else if (value > 80 && previousValue <= 80) {
					String message = "Över 80%";
					writeToLogAndAppend(message);
					// Byta färg (Fungerar inte på Mac)
					progressBar.setForeground(UIManager.getColor(Color.GREEN));
				} else if (value >= 20 && value <= 80) {
					progressBar.setForeground(UIManager.getColor("ProgressBar.foreground"));
				}
				previousValue = value;
			}
		});
	}
}
