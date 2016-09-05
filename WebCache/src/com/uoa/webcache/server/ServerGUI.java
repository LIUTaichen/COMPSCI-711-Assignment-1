package com.uoa.webcache.server;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.IOException;

import org.eclipse.swt.SWT;

public class ServerGUI {

	protected Shell shell;
	private Text txtServerIsRunning;
	private Server server;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerGUI window = new ServerGUI();
			window.server = new Server();
			window.server.start();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		txtServerIsRunning = new Text(shell, SWT.BORDER);
		txtServerIsRunning.setText("Server is running");
		txtServerIsRunning.setBounds(58, 50, 209, 21);
		
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  System.out.println("server stopping");
		    	  System.exit(0);
		      }
		    });

	}
}
