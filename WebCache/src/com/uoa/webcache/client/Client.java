package com.uoa.webcache.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	
	public static void main(String[] args){
		System.out.println("Client starting");
		
		Socket clientSocket;
	    try {
	    	clientSocket = new Socket("localhost", 8080);
	    	System.out.println("Client socket connected");
	    	DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			System.out.print("Sending string: '" + "list file" + "'\n");
			out.writeUTF("list file");
	    	 DataInputStream input = new DataInputStream(clientSocket.getInputStream());
	    	 System.out.println(input.readUTF());
	    }
	    catch (IOException e) {
	        System.out.println(e);
	    }
	}

}
