package com.uoa.webcache.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class Server {
	private static final int PORT_NO = 8080;
	private static final String LIST_FILE_COMMAND = "list files";
	private static Map<String, File> fileMap = new HashMap<String, File>();
	public static void main(String args[]) {
		String data = "This will be the file list";
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT_NO);
			serverSocket.setSoTimeout(60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		loadFiles();
		
		while (!serverSocket.isClosed()) {
			try {
				log("Opening Server socket at port " +PORT_NO + " , waiting for connection from clients");

				Socket socket = serverSocket.accept();
				
				log("Server socket is listening for traffic!");
				 DataInputStream input = new DataInputStream(socket.getInputStream());
				 String commandFromClient = input.readUTF();
				 log("received command : [" + commandFromClient+"]");
				 if(LIST_FILE_COMMAND.equals(commandFromClient)){
					 log("client requested file list");
					 ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
				 }else{
					 
				 }
		    	 log(commandFromClient);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				log("Sending string: '" + data);
				out.writeUTF(data);
				out.close();
				socket.close();

			} catch (SocketTimeoutException s) {
				log("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private static void loadFiles() {
		
		File folder = new File("src/resources/files");
		log(folder.getAbsolutePath());
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles ==null){
			log("wrong directory, no files loaded");
			return;
		}
		for(File file : listOfFiles){
			log(file.getName());
			fileMap.put(file.getName(), file);
		}
	}

	private static void log(Object object){
		System.out.println(object);
	}
}
