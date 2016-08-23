package com.uoa.webcache.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Logger;

public class Client {

	private static final String LIST_FILES_COMMAND = "list files";
	private static Logger log = Logger.getLogger(Client.class.getName());

	public static void main(String[] args) {
		System.out.println("Client starting");
		try {
			requestFileList();

			requestFileTransfer();
		} catch (IOException e) {

			System.out.println(e);
			e.printStackTrace();
		} finally {
			log.info("client stopping");
		}
	}

	private static Socket requestFileTransfer() throws UnknownHostException, IOException {

		String fileToRequest = "CurriculumVitae-Jason Liu Taichen.pdf";
		Socket clientSocket;
		clientSocket = new Socket("localhost", 8080);
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

		FileOutputStream fileOutput = new FileOutputStream(fileToRequest);
		try {
			out.writeUTF(fileToRequest);

			byte[] buffer = new byte[8132];
			int read = 0;
			while ((read = dataInputStream.read(buffer)) != -1) {
				fileOutput.write(buffer, 0, read);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
			dataInputStream.close();
			fileOutput.close();
			clientSocket.close();
		}
		return clientSocket;
	}

	private static void requestFileList() throws UnknownHostException, IOException {
		Socket clientSocket;
		clientSocket = new Socket("localhost", 8080);
		
		System.out.println("Client socket connected");
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		out.writeUTF(LIST_FILES_COMMAND);
		ObjectInputStream inputFromServer = new ObjectInputStream(clientSocket.getInputStream());
		System.out.print("Sending string: '" + LIST_FILES_COMMAND + "'\n");
		try {
			

			Set<String> fileList = null;

			fileList = (Set<String>) inputFromServer.readObject();
			for (String fileName : fileList) {
				log.info(fileName);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			inputFromServer.close();
			clientSocket.close();
		}
	}

}
