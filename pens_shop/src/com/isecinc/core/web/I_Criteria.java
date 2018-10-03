package com.isecinc.core.web;

import java.io.Serializable;

/**
 * Search Criteria Object Abstract Class
 * 
 * @author Atiz.b
 * @version $Id: I_Criteria.java,v 1.0 15/07/2010 00:00:00 atiz.b Exp $
 * 
 */
public class I_Criteria implements Serializable {

	private static final long serialVersionUID = -1334309884084921137L;

	/** Criteria Key */
	protected String searchKey;

	/** Search Result Size */
	protected int searchResult = 0;

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public int getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(int searchResult) {
		this.searchResult = searchResult;
	}
}
