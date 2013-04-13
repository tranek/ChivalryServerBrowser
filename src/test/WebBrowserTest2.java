package test;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
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
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.swing.JFrame;

import netscape.javascript.JSObject;

public class WebBrowserTest2 {

	private static final int PANEL_WIDTH_INT = 800;
    private static final int PANEL_HEIGHT_INT = 650;
	private JFrame frame;
	private static JFXPanel browserFxPanel;
	private Pane browser;
	public WebEngine eng;
	public WebBrowserTest2 wbt2;
	public chivserver cs1 = new chivserver("Official Classic 15", "32.929900000000004", "-96.835300000000004");
	public chivserver cs2 = new chivserver("Official TDM 4", "51.514200000000002", "-0.093100000000000002");
	public chivserver cs3 = new chivserver("Official FFA 1", "52.350000000000001", "4.9166999999999996");
	public chivserver cs4 = new chivserver("Official Classic 19", "1.3413999999999999", "103.7392");
	public chivserver cs5 = new chivserver("Official FFA 3", "43.666699999999999", "-79.416700000000006");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WebBrowserTest2 window = new WebBrowserTest2();
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
	public WebBrowserTest2() {
		wbt2 = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 // create javafx panel for browser
        browserFxPanel = new JFXPanel();
        
        frame.add(browserFxPanel);
        
        // create JavaFX scene
        Platform.runLater(new Runnable() {
            public void run() {
                createScene();
                getLocation();
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
        eng = view.getEngine();
        final Label warningLabel = new Label("Do you need to specify web proxy information?");
        
        eng.setOnAlert(new EventHandler<WebEvent<String>>() {
            public void handle(WebEvent<String> ev) {
            	System.out.println(ev.getData());
            	if ( ev.getData().equals("jquery works!") ) {
            		eng.executeScript("javacall('helloworld')");
            	}
            	if ( ev.getData().equals("loaded") ) {
            		//TODO
            		String ip = getLocation();
            		LocationRIPE l = new LocationRIPE();
            		HashMap<String, String> loc = l.getLocation(ip);
            		String lat = loc.get("latitude");
            		String lon = loc.get("longitude");
            		String func = "addUserLoc('" + lat + "', '" + lon + "')";
            		//System.out.println(func);
            		if ( !lat.equals("") && !lon.equals("") ) {
            			eng.executeScript(func);
            		}
            		func = "addMarker('" + cs1.lat + "', '" + cs1.lon + "', '" + cs1.name + "')";
            		//System.out.println(func);
            		eng.executeScript(func);
            		func = "addMarker('" + cs2.lat + "', '" + cs2.lon + "', '" + cs2.name + "')";
            		eng.executeScript(func);
            		func = "addMarker('" + cs3.lat + "', '" + cs3.lon + "', '" + cs3.name + "')";
            		eng.executeScript(func);
            		func = "addMarker('" + cs4.lat + "', '" + cs4.lon + "', '" + cs4.name + "')";
            		eng.executeScript(func);
            		func = "addMarker('" + cs5.lat + "', '" + cs5.lon + "', '" + cs5.name + "')";
            		eng.executeScript(func);
            	}
            }
        });
        
        /*String jquery = "";
        try {
			jquery = readLargerTextFile("html/jquery-1.9.1.min.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
        eng.executeScript(jquery);*/
        
        //eng.load("http://www.oracle.com/us/index.html");
        /*String html = "";
        try {
			html = readLargerTextFile("html/map.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
        eng.loadContent(html);*/
        String url = getClass().getResource("map.html").toExternalForm();
        eng.load(url);
        
        ChangeListener handler = new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (warningLabel.isVisible()) {
                    warningLabel.setVisible(false);
                }
            }
        
        };
        eng.getLoadWorker().progressProperty().addListener(handler);
        
        eng.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>(){
                    
                    @Override
                    public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                        if(newState == State.SUCCEEDED){
                            JSObject window = (JSObject)eng.executeScript("window");
                            window.setMember("app", wbt2);
                        }
                    }
                });
        JSObject window = (JSObject)eng.executeScript("window");
        window.setMember("app", wbt2);

        //final TextField locationField = new TextField("http://www.oracle.com/us/index.html");
        final TextField locationField = new TextField("html/map.html");
        locationField.setMaxHeight(Double.MAX_VALUE);
        Button goButton = new Button("Go");
        goButton.setDefaultButton(true);
        EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                /*eng.load(locationField.getText().startsWith("http://") ? locationField.getText()
                        : "http://" + locationField.getText());*/
            	String html = "";
                try {
        			html = readLargerTextFile(locationField.getText());
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
                //eng.loadContent(html);
                //eng.executeScript("javacall()");
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
	
	public String getLocation() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
			System.out.println(ip);
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// JavaScript interface object
    public class JavaApp {
 
        public void test() {
            System.out.println("Hello World!");
        }
        
        public void exit() {
        	//Platform.exit();
        	System.exit(0);
        }
    }
    
    public void hello() {
    	System.out.println("Errmmmaaagghheerrrdddzzz this works.");
    }
    
    public class chivserver {
    	public String name;
    	public String lat;
    	public String lon;
    	public chivserver(String name, String lat, String lon) {
    		this.name = name;
    		this.lat = lat;
    		this.lon = lon;
    	}
    }

}
