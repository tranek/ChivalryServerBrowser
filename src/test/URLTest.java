package test;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JTextPane;


public class URLTest {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					URLTest window = new URLTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public URLTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTextPane textArea = new JTextPane();
		
		textArea.setEditable(false);
		//textArea.setContentType("text/html");
		//textArea.setText("http://www.google.com");
		textArea.setText( "steam://run/219640/en/85.236.100.3:9477&-windowed" );
		frame.getContentPane().add(textArea, BorderLayout.NORTH);
		
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Desktop dt = Desktop.getDesktop();
				URI url = null;
				try {
					url = new URI(textArea.getText());
					dt.browse(url);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

}
