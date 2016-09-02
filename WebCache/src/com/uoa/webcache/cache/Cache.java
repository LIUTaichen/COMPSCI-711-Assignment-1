package com.uoa.webcache.cache;

public class Cache {
	
	private String[] cacheList;
	
	private boolean enabled;

	public String[] getCacheList() {
		return cacheList;
	}

	public void setCacheList(String[] cacheList) {
		this.cacheList = cacheList;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
