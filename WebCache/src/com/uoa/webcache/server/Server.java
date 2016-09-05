package com.uoa.webcache.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.uoa.webcache.cache.Cache;

public class Server extends Thread {
	private static Logger log = Logger.getLogger(Server.class.getName());
	private static final int PORT_NO = 8080;
	private static final String LIST_FILES_COMMAND = "list files";
	private static Map<String, File> fileMap = new HashMap<String, File>();
	

	public static void main(String args[]) throws IOException {
		loadFiles();
		ServerSocket serverSocket = null;
		Socket socket = null;
		serverSocket = new ServerSocket(PORT_NO);
		serverSocket.setSoTimeout(600000);

		while (true) {
			try {
				log.info("Server opening new socket at port " + PORT_NO + " , waiting for connection from cache");

				log.info("Server socket is listening for traffic!");
				socket = serverSocket.accept();

				DataInputStream input = new DataInputStream(socket.getInputStream());
				OutputStream outputStream = socket.getOutputStream();

				String commandFromClient = input.readUTF();
				log.info("received command : [" + commandFromClient + "]");
				if (LIST_FILES_COMMAND.equals(commandFromClient)) {
					handleFileListRequest(outputStream);

				} else {
					hanldeFileTransferRequest(outputStream, commandFromClient);
				}

			} catch (SocketTimeoutException s) {
				log.info("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			} finally {
				socket.close();
			}

		}

		serverSocket.close();
		log.info("Server stopping");
	}

	
	public  void run()  {
		loadFiles();
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(PORT_NO);
			serverSocket.setSoTimeout(600000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		while (true) {
			try {
				log.info("Server opening new socket at port " + PORT_NO + " , waiting for connection from cache");

				log.info("Server socket is listening for traffic!");
				socket = serverSocket.accept();

				DataInputStream input = new DataInputStream(socket.getInputStream());
				OutputStream outputStream = socket.getOutputStream();

				String commandFromClient = input.readUTF();
				log.info("received command : [" + commandFromClient + "]");
				if (LIST_FILES_COMMAND.equals(commandFromClient)) {
					handleFileListRequest(outputStream);

				} else {
					hanldeFileTransferRequest(outputStream, commandFromClient);
				}

			} catch (SocketTimeoutException s) {
				log.info("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Server stopping");
	}
	private static void hanldeFileTransferRequest(OutputStream outputStream, String fileName)
			throws FileNotFoundException, IOException {
		File requestedFile = fileMap.get(fileName);
		byte[] tempByteArray = new byte[8132];
		FileInputStream fis = new FileInputStream(requestedFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);
		DataOutputStream outToClient = new DataOutputStream(outputStream);
		int read;
		while ((read = dis.read(tempByteArray)) != -1) {
			outToClient.write(tempByteArray, 0, read);
			outToClient.flush();
		}

		fis.close();
		bis.close();
		dis.close();
	}

	private static void handleFileListRequest(OutputStream outputStream) throws IOException {
		log.info("client requested file list");
		ObjectOutputStream outToClient = new ObjectOutputStream(outputStream);
		Set<String> fileList = new HashSet<String>(fileMap.keySet());
		outToClient.writeObject(fileList);
	}

	private static void loadFiles() {

		File folder = new File("src/resources/files");
		log.info(folder.getAbsolutePath());
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			log.info("wrong directory, no files loaded");
			return;
		}
		for (File file : listOfFiles) {
			log.info(file.getName());
			fileMap.put(file.getName(), file);
		}
	}

}
