package com.uoa.webcache.cache;

import java.io.IOException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class CacheGUI {
	private DataBindingContext m_bindingContext;

	protected Shell shell;

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	private Cache cache;
	private List list;
	private StyledText styledText;

	public CacheGUI() throws IOException {
		this.cache = new Cache();
		this.cache.setGui(this);
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					CacheGUI window = new CacheGUI();
					window.cache.start();
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
		shell.setSize(665, 630);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());

		Label lblCachedFileList = new Label(shell, SWT.NONE);
		FormData fd_lblCachedFileList = new FormData();
		fd_lblCachedFileList.top = new FormAttachment(0, 10);
		fd_lblCachedFileList.left = new FormAttachment(0, 10);
		lblCachedFileList.setLayoutData(fd_lblCachedFileList);
		lblCachedFileList.setText("Cached File List");

		list = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		FormData fd_list = new FormData();
		fd_list.bottom = new FormAttachment(lblCachedFileList, 493, SWT.BOTTOM);
		fd_list.top = new FormAttachment(lblCachedFileList, 23);
		fd_list.left = new FormAttachment(0, 10);
		fd_list.right = new FormAttachment(0, 240);
		list.setLayoutData(fd_list);

		styledText = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		styledText.setDoubleClickEnabled(false);
		styledText.setEditable(false);
		FormData fd_styledText = new FormData();
		fd_styledText.bottom = new FormAttachment(100, -74);
		fd_styledText.right = new FormAttachment(100, -10);
		fd_styledText.left = new FormAttachment(list, 51);
		styledText.setLayoutData(fd_styledText);

		Label lblCacheLog = new Label(shell, SWT.NONE);
		fd_styledText.top = new FormAttachment(lblCacheLog, 23);
		FormData fd_lblCacheLog = new FormData();
		fd_lblCacheLog.left = new FormAttachment(lblCachedFileList, 199);
		fd_lblCacheLog.top = new FormAttachment(lblCachedFileList, 0, SWT.TOP);
		lblCacheLog.setLayoutData(fd_lblCacheLog);
		lblCacheLog.setText("Cache Log");

		Button btnClearCache = new Button(shell, SWT.NONE);
		
		FormData fd_btnClearCache = new FormData();
		fd_btnClearCache.bottom = new FormAttachment(100, -10);
		fd_btnClearCache.left = new FormAttachment(lblCachedFileList, 0, SWT.LEFT);
		btnClearCache.setLayoutData(fd_btnClearCache);
		btnClearCache.setText("Clear Cache");
		btnClearCache.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				list.setItems(new String[0]);
				getCache().getCacheList().clear();
			}
		});
		m_bindingContext = initDataBindings();

		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  System.out.println("cache ui stopping");
		    	  System.exit(0);
		    	  try {
					getCache().getCacheListeningSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		    });
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		//
		return bindingContext;
	}

	public void setLogText(String text) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!styledText.isDisposed())
					styledText.setText(text);
			}
		});
	}
	
	public void setCachedFiles(String[] fileNames){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!list.isDisposed())
					list.setItems(fileNames);
			}
		});
	}
}
