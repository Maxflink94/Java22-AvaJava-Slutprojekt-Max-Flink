package log;

import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class LogInfo {
	private BufferedWriter writer;
	private JTextArea logLabel;
	
	public LogInfo(String path, JTextArea logLabel) {
		this.logLabel = logLabel;
        try {
            writer = new BufferedWriter(new FileWriter(path, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void writeData(String data) {
	    if (writer != null) {
	        try {
	            writer.write(data);
	            writer.newLine();
	            writer.flush();

	            if (logLabel != null) {
	                SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                        logLabel.append(data + "\n");
	                    }
	                });
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
