package com.uoa.webcache.cache;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Cache extends Thread {

	private List<String> cacheList = new ArrayList<String>();

	private boolean enabled = true;

	private static Logger log = Logger.getLogger(Cache.class.getName());
	private static final int CLIENT_TO_CACHE_PORT_NO = 8081;
	private static final int CACHE_TO_SERVER_PORT_NO = 8080;
	private static final String LIST_FILES_COMMAND = "list files";
	private String cacheLog = new String();
	private CacheGUI gui;
	private ServerSocket cacheListeningSocket;


	public static void main(String args[]) throws IOException {
		ServerSocket cacheListeningSocket = null;
		Socket socket = null;
		cacheListeningSocket = new ServerSocket(CLIENT_TO_CACHE_PORT_NO);
		cacheListeningSocket.setSoTimeout(600000);
		Cache cache = new Cache();

		while (true) {
			try {
				log.info("Opening new socket at port " + CLIENT_TO_CACHE_PORT_NO
						+ " , waiting for connection from clients");

				socket = cacheListeningSocket.accept();

				DataInputStream input = new DataInputStream(socket.getInputStream());
				OutputStream outputStream = socket.getOutputStream();

				String commandFromClient = input.readUTF();
				log.info("received command : [" + commandFromClient + "]");
				if (LIST_FILES_COMMAND.equals(commandFromClient)) {
					cache.handleFileListRequest(outputStream);

				} else {
					cache.hanldeFileTransferRequest(outputStream, commandFromClient);
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

		cacheListeningSocket.close();
		log.info("Server stopping");
	}


	private void hanldeFileTransferRequest(OutputStream outputStream, String fileName)
			throws FileNotFoundException, IOException {
		File requestedFile;
		boolean cached = cacheList.contains(fileName);
		if (!cached) {

			Socket cacheToServerSocket;
			cacheToServerSocket = new Socket("localhost", CACHE_TO_SERVER_PORT_NO);
			DataOutputStream out = new DataOutputStream(cacheToServerSocket.getOutputStream());
			DataInputStream dataInputStream = new DataInputStream(cacheToServerSocket.getInputStream());

			FileOutputStream fileOutput = new FileOutputStream(fileName);
			try {
				out.writeUTF(fileName);

				byte[] buffer = new byte[8132];
				int read = 0;
				while ((read = dataInputStream.read(buffer)) != -1) {
					fileOutput.write(buffer, 0, read);

				}
				cacheList.add(fileName);
				gui.setCachedFiles((String[]) cacheList.toArray(new String[0]));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				out.close();
				dataInputStream.close();
				fileOutput.close();
				cacheToServerSocket.close();
			}
		}
		requestedFile = new File(fileName);
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
		writeLog(fileName, cached);

	}

	private void handleFileListRequest(OutputStream outputStream) throws IOException {
		log.info("client requested file list");
		ObjectOutputStream outToClient = new ObjectOutputStream(outputStream);
		Set<String> fileList = requestFileListFromServer();

		outToClient.writeObject(fileList);

	}

	public Set<String> requestFileListFromServer() throws UnknownHostException, IOException {
		Socket clientSocket;
		clientSocket = new Socket("localhost", CACHE_TO_SERVER_PORT_NO);

		log.info("Cache to server socket connected");
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		out.writeUTF(LIST_FILES_COMMAND);
		ObjectInputStream inputFromServer = new ObjectInputStream(clientSocket.getInputStream());
		System.out.print("Sending string: '" + LIST_FILES_COMMAND + "'\n");
		try {

			Set<String> fileList = null;
			fileList = (Set<String>) inputFromServer.readObject();
			return fileList;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			inputFromServer.close();
			clientSocket.close();
		}
		return null;
	}

	public void setCachelog(String cacheLog) {
		this.cacheLog = cacheLog;
	}

	@Override
	public void run() {
		log.info("calling start cache service ");
		ServerSocket cacheListeningSocket = null;
		Socket socket = null;
		try {
			cacheListeningSocket = new ServerSocket(CLIENT_TO_CACHE_PORT_NO);

			try {
				cacheListeningSocket.setSoTimeout(600000);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (enabled) {
				try {
					log.info("Cache is opening new socket at port " + CLIENT_TO_CACHE_PORT_NO
							+ " , waiting for connection from clients");

					socket = cacheListeningSocket.accept();

					DataInputStream input = new DataInputStream(socket.getInputStream());
					OutputStream outputStream = socket.getOutputStream();

					String commandFromClient = input.readUTF();
					log.info("received command : [" + commandFromClient + "]");
					if (LIST_FILES_COMMAND.equals(commandFromClient)) {
						this.handleFileListRequest(outputStream);

					} else {
						this.hanldeFileTransferRequest(outputStream, commandFromClient);
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

			cacheListeningSocket.close();
			log.info("Cache stopping");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public CacheGUI getGui() {
		return gui;
	}

	public void setGui(CacheGUI gui) {
		this.gui = gui;
	}

	public List<String> getCacheList() {
		return cacheList;
	}

	public void setCacheList(List<String> cacheList) {
		this.cacheList = cacheList;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCachelog() {
		return cacheLog;
	}

	private void writeLog(String fileName, boolean cached) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
		cacheLog += "user request: file " + fileName + " at " + df.format(date) + "\n";
		if (cached) {
			cacheLog += "response: cached file " + fileName + "\n";;
		} else {
			cacheLog += "response: file " + fileName + " downloaded from the server"+ "\n";;
		}
		gui.setLogText(cacheLog);
	}
	

	public ServerSocket getCacheListeningSocket() {
		return cacheListeningSocket;
	}

	public void setCacheListeningSocket(ServerSocket cacheListeningSocket) {
		this.cacheListeningSocket = cacheListeningSocket;
	}


	public void clearCache() {
		for(String fileName : getCacheList()){
			File cachedFile = new File(fileName);
			cachedFile.delete();
		}
		getCacheList().clear();
	}
}
