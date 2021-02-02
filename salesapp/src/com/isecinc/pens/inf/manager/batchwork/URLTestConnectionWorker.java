package com.isecinc.pens.inf.manager.batchwork;

import com.isecinc.pens.process.testconn.TestURLConnection;

public class URLTestConnectionWorker extends Thread{
	private String url;
	private String threadName;
	public URLTestConnectionWorker(String threadName,String url){
	   this.threadName =threadName;
	   this.url = url;
	}
	
	@Override
	public void run() {
		TestURLConnection.testURLConnection(threadName,url);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	
}
