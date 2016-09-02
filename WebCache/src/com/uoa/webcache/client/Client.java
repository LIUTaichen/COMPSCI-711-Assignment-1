package com.uoa.webcache.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Client {

	private static final String LIST_FILES_COMMAND = "list files";
	private static Logger log = Logger.getLogger(Client.class.getName());
	private List<String> serverFileList;
	private String selectedFileName;
	private List<String> downloadedFileList;

	public Client() {
		this.serverFileList = new ArrayList<String>();
		this.downloadedFileList = new ArrayList<String>();
	}

	public static void main(String[] args) {
		System.out.println("Client starting");
		Client client = new Client();
		try {
			client.requestFileList();

			client.requestFileTransfer("CurriculumVitae-Jason Liu Taichen.pdf");
		} catch (IOException e) {

			System.out.println(e);
			e.printStackTrace();
		} finally {
			log.info("client stopping");
		}
	}

	public Socket requestFileTransfer(String fileName) throws UnknownHostException, IOException {

		Socket clientSocket;
		clientSocket = new Socket("localhost", 8080);
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

		FileOutputStream fileOutput = new FileOutputStream(fileName);
		try {
			out.writeUTF(fileName);

			byte[] buffer = new byte[8132];
			int read = 0;
			while ((read = dataInputStream.read(buffer)) != -1) {
				fileOutput.write(buffer, 0, read);

			}
			if (!downloadedFileList.contains(fileName)) {
				downloadedFileList.add(fileName);
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

	public void requestFileList() throws UnknownHostException, IOException {
		Socket clientSocket;
		clientSocket = new Socket("localhost", 8080);

		System.out.println("Client socket connected");
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		out.writeUTF(LIST_FILES_COMMAND);
		ObjectInputStream inputFromServer = new ObjectInputStream(clientSocket.getInputStream());
		System.out.print("Sending string: '" + LIST_FILES_COMMAND + "'\n");
		try {

			Set<String> fileList = null;
			this.getServerFileList().clear();
			fileList = (Set<String>) inputFromServer.readObject();
			for (String fileName : fileList) {
				log.info(fileName);
				this.getServerFileList().add(fileName);
			}
			for (String item : this.getServerFileList()) {
				log.info("printing " + item);
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

	public String getFileContent(String fileName) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}

	public List<String> getServerFileList() {
		return serverFileList;
	}

	public void setServerFileList(List<String> serverFileList) {
		this.serverFileList = serverFileList;
	}

	public String getSelectedFileName() {
		return selectedFileName;
	}

	public void setSelectedFileName(String selectedFileName) {
		this.selectedFileName = selectedFileName;
	}

	public List<String> getDownloadedFileList() {
		return downloadedFileList;
	}

	public void setDownloadedFileList(List<String> downloadedFileList) {
		this.downloadedFileList = downloadedFileList;
	}

}
