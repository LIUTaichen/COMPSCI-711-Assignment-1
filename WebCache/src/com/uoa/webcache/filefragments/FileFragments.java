package com.uoa.webcache.filefragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileFragments implements Serializable  {
	private static final long serialVersionUID = 1L;
	private List<String> fragmentDigestList = new ArrayList<String>();
	
	public List<String> getFragmentDigestList() {
		return fragmentDigestList;
	}
	public void setFragmentDigestList(List<String> fragmentDigestList) {
		this.fragmentDigestList = fragmentDigestList;
	}
	
}
