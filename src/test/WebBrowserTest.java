package test;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WebBrowserTest {

	private JFrame frame;
	private Pane browser;
	private static JFXPanel browserFxPanel;
	private static final int PANEL_WIDTH_INT = 800;
    private static final int PANEL_HEIGHT_INT = 800;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WebBrowserTest window = new WebBrowserTest();
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
	public WebBrowserTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel browserPanel = new JPanel();
		frame.getContentPane().add(browserPanel, BorderLayout.CENTER);
		
		// create javafx panel for browser
        browserFxPanel = new JFXPanel();
        browserPanel.add(browserFxPanel);
        
        // create JavaFX scene
        Platform.runLater(new Runnable() {
            public void run() {
                createScene();
            }
        });
	}
	
	private void createScene() {
        browser = createBrowser();
        browserFxPanel.setScene(new Scene(browser));
    }
	
	private Pane createBrowser() {
        Double widthDouble = new Integer(PANEL_WIDTH_INT).doubleValue();
        Double heightDouble = new Integer(PANEL_HEIGHT_INT).doubleValue();
        WebView view = new WebView();
        view.setMinSize(widthDouble, heightDouble);
        view.setPrefSize(widthDouble, heightDouble);
        final WebEngine eng = view.getEngine();
        final Label warningLabel = new Label("Do you need to specify web proxy information?");
        //eng.load("http://www.oracle.com/us/index.html");
        /*File file = null;
        file = new File("html/map.html");
        System.out.println("file:\\\\" + file.getAbsolutePath());
        eng.load(file.getAbsolutePath());*/
        String html = "";
        try {
			html = readLargerTextFile("html/map.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
        eng.loadContent(html);

        ChangeListener handler = new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (warningLabel.isVisible()) {
                    warningLabel.setVisible(false);
                }
            }
        };
        eng.getLoadWorker().progressProperty().addListener(handler);

        //final TextField locationField = new TextField("http://www.oracle.com/us/index.html");
        final TextField locationField = new TextField("html/map.html");
        locationField.setMaxHeight(Double.MAX_VALUE);
        Button goButton = new Button("Go");
        goButton.setDefaultButton(true);
        EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                /*eng.load(locationField.getText().startsWith("http://") ? locationField.getText()
                        : "http://" + locationField.getText());*/
            	eng.load(locationField.getText());
            }
        };
        goButton.setOnAction(goAction);
        locationField.setOnAction(goAction);
        eng.locationProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                locationField.setText(newValue);
            }
        });
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setVgap(5);
        grid.setHgap(5);
        GridPane.setConstraints(locationField, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES);
        GridPane.setConstraints(goButton, 1, 0);
        GridPane.setConstraints(view, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(warningLabel, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES);
        grid.getColumnConstraints().addAll(
                new ColumnConstraints(widthDouble - 200, widthDouble - 200, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true),
                new ColumnConstraints(40, 40, 40, Priority.NEVER, HPos.CENTER, true));
        grid.getChildren().addAll(locationField, goButton, warningLabel, view);
        return grid;
    }
	
	public String readLargerTextFile(String aFileName) throws IOException {
		String html = "";
	    Path path = Paths.get(aFileName);
	    try (Scanner scanner =  new Scanner(path)){
	    	while (scanner.hasNextLine()){
	    	  html += scanner.nextLine();
	    	}      
	    }
	    return html;
	}

}
