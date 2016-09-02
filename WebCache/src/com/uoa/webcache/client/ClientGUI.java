package com.uoa.webcache.client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ClientGUI {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	
	private  Client client;
	private List serverFileList;
	
	public ClientGUI(){
		this.client = new Client();
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		shell.setSize(865, 816);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());
		
		Button btnDownload = new Button(shell, SWT.NONE);
		
		FormData fd_btnDownload = new FormData();
		fd_btnDownload.bottom = new FormAttachment(100, -439);
		fd_btnDownload.right = new FormAttachment(0, 171);
		btnDownload.setLayoutData(fd_btnDownload);
		btnDownload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnDownload.setText("Download");
		
		
		
		
		StyledText fileContent = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP |  SWT.V_SCROLL);
		FormData fd_fileContent = new FormData();
		fd_fileContent.bottom = new FormAttachment(100);
		fd_fileContent.right = new FormAttachment(0, 813);
		fd_fileContent.left = new FormAttachment(0, 340);
		fileContent.setLayoutData(fd_fileContent);
		
		Button btnRefresh = new Button(shell, SWT.NONE);
		FormData fd_btnRefresh = new FormData();
		fd_btnRefresh.right = new FormAttachment(fileContent, -6);
		fd_btnRefresh.left = new FormAttachment(btnDownload, 6);
		btnRefresh.setLayoutData(fd_btnRefresh);
		btnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					client.requestFileList();
					System.out.println("count      " +serverFileList.getItemCount() );
					System.out.println("count    client  " +client.getServerFileList().size() );
					
					serverFileList.setItems((String[]) client.getServerFileList().toArray(new String[0]));
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnRefresh.setText("Refresh");
		
		List downloadedFileList = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		fd_btnRefresh.bottom = new FormAttachment(downloadedFileList, -68);
		downloadedFileList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String fileToBeDisplayed = downloadedFileList.getSelection()[0];
				fileContent.setText(client.getFileContent(fileToBeDisplayed));
				
			}
		});
		FormData fd_downloadedFileList = new FormData();
		fd_downloadedFileList.top = new FormAttachment(btnDownload, 68);
		fd_downloadedFileList.bottom = new FormAttachment(100);
		fd_downloadedFileList.right = new FormAttachment(fileContent, -6);
		fd_downloadedFileList.left = new FormAttachment(0);
		downloadedFileList.setLayoutData(fd_downloadedFileList);
		
		serverFileList = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		fd_btnRefresh.top = new FormAttachment(serverFileList, 6);
		fd_btnDownload.top = new FormAttachment(serverFileList, 6);
		fd_btnDownload.left = new FormAttachment(serverFileList, 0, SWT.LEFT);
		FormData fd_serverFileList = new FormData();
		fd_serverFileList.right = new FormAttachment(fileContent, -6);
		fd_serverFileList.left = new FormAttachment(0);
		fd_serverFileList.top = new FormAttachment(0, 39);
		fd_serverFileList.bottom = new FormAttachment(0, 303);
		serverFileList.setLayoutData(fd_serverFileList);
		
		Label lblListOfFiles = new Label(shell, SWT.NONE);
		lblListOfFiles.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblListOfFiles = new FormData();
		fd_lblListOfFiles.top = new FormAttachment(serverFileList, -21, SWT.TOP);
		fd_lblListOfFiles.bottom = new FormAttachment(serverFileList, -6);
		fd_lblListOfFiles.left = new FormAttachment(0);
		fd_lblListOfFiles.right = new FormAttachment(0, 204);
		lblListOfFiles.setLayoutData(fd_lblListOfFiles);
		lblListOfFiles.setText("List of Files on Server");
		
		Label lblFileContent = new Label(shell, SWT.NONE);
		fd_fileContent.top = new FormAttachment(lblFileContent, 6);
		FormData fd_lblFileContent = new FormData();
		fd_lblFileContent.bottom = new FormAttachment(100, -745);
		fd_lblFileContent.left = new FormAttachment(fileContent, 0, SWT.LEFT);
		lblFileContent.setLayoutData(fd_lblFileContent);
		lblFileContent.setText("File Content");
		
		Label lblListOfDownloaded = new Label(shell, SWT.NONE);
		FormData fd_lblListOfDownloaded = new FormData();
		fd_lblListOfDownloaded.bottom = new FormAttachment(downloadedFileList, -6);
		fd_lblListOfDownloaded.left = new FormAttachment(btnDownload, 0, SWT.LEFT);
		lblListOfDownloaded.setLayoutData(fd_lblListOfDownloaded);
		lblListOfDownloaded.setText("List of Downloaded Files");
		m_bindingContext = initDataBindings();
		
		
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(serverFileList.getSelection().length == 0){
					System.out.println("No file selected");
				}else{
					String requestedFileName = serverFileList.getSelection()[0];
					System.out.println("selected file : " + requestedFileName);
					try {
						client.requestFileTransfer(requestedFileName);
						downloadedFileList.setItems((String[]) client.getDownloadedFileList().toArray(new String[0]));
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
