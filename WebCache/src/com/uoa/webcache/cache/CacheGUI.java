package com.uoa.webcache.cache;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.List;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;

public class CacheGUI {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	
	private Cache cache;
	private List list;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					CacheGUI window = new CacheGUI();
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
		
		list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_list = new FormData();
		fd_list.bottom = new FormAttachment(lblCachedFileList, 374, SWT.BOTTOM);
		fd_list.right = new FormAttachment(0, 240);
		fd_list.top = new FormAttachment(lblCachedFileList, 23);
		fd_list.left = new FormAttachment(0, 10);
		list.setLayoutData(fd_list);
		m_bindingContext = initDataBindings();

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
		IObservableValue observeEnabledListObserveWidget = WidgetProperties.enabled().observe(list);
		IObservableValue enabledCacheObserveValue = PojoProperties.value("enabled").observe(cache);
		bindingContext.bindValue(observeEnabledListObserveWidget, enabledCacheObserveValue, null, null);
		//
		return bindingContext;
	}
}
