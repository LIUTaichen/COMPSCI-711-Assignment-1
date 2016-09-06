package com.uoa.webcache.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.uoa.webcache.cache.Cache;
import com.uoa.webcache.filefragments.FileFragments;
import com.uoa.webcache.util.FileFragmenter;

public class Server extends Thread {
	private Logger log = Logger.getLogger(Server.class.getName());
	private final int PORT_NO = 8080;
	private final String LIST_FILES_COMMAND = "list files";
	private Map<String, File> fileMap = new HashMap<String, File>();
	private Map<String, FileFragments> fileFragmentsMap = new HashMap<String, FileFragments>();
	private Map<String, byte[]> digestToPartsMap = new HashMap<String, byte[]>();
	private FileFragmenter fragmenter = new FileFragmenter();
	// private static Map<String, ArrayList<>>

	public static void main(String args[]) throws IOException {
		Server server = new Server();
		server.run();
	}

	public void run() {
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

	private void hanldeFileTransferRequest(OutputStream outputStream, String digestString)
			throws FileNotFoundException, IOException {
		byte[]  filePart= digestToPartsMap.get(digestString);
		byte[] tempByteArray = new byte[2048];
		ByteArrayInputStream bis = new ByteArrayInputStream(filePart);
		DataInputStream dis = new DataInputStream(bis);
		DataOutputStream outToClient = new DataOutputStream(outputStream);
		int read;
		while ((read = dis.read(tempByteArray)) != -1) {
			outToClient.write(tempByteArray, 0, read);
			outToClient.flush();
		}

		bis.close();
		dis.close();
	}

	private void handleFileListRequest(OutputStream outputStream) throws IOException {
		log.info("client requested file list");
		ObjectOutputStream outToClient = new ObjectOutputStream(outputStream);
		outToClient.writeObject(fileFragmentsMap);
	}

	private void loadFiles() {
		MessageDigest md;
	
		try {
			md = MessageDigest.getInstance("MD5");
			File folder = new File("files");
			log.info(folder.getAbsolutePath());
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles == null) {
				
				log.info("wrong directory, no files loaded");
				folder = new File("src/resources/files");
				listOfFiles = folder.listFiles();
				log.info(folder.getAbsolutePath());
			}
			for (File file : listOfFiles) {
				log.info(file.getName());
				fileMap.put(file.getName(), file);
				List<byte[]> fragmentList = fragmenter.fragment(file.getAbsolutePath());
				FileFragments fileFragments = new FileFragments();
				for (byte[] filePart : fragmentList) {
					
					byte[] thedigest = md.digest(filePart);
					byte[] encoded = Base64.getEncoder().encode(thedigest);
					String base64Digest = new String(encoded);
					digestToPartsMap.put(base64Digest, filePart);
					fileFragments.getFragmentDigestList().add(base64Digest);
					log.info("digestToPartsMap map size : " +digestToPartsMap.size() +"");
				}
				fileFragmentsMap.put(file.getName(), fileFragments);
			}
			
			log.info("digestToPartsMap map size : " +digestToPartsMap.size() +"");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
