package com.isecinc.pens.thread;


public class ControlSubThreadWorker extends Thread{
	private String userName;
	private String threadName;
	public ControlSubThreadWorker(String threadName,String userName){
	   this.threadName =threadName;
	   this.userName = userName;
	}
	
	@Override
	public void run() {
		ControlSubThread.startThread(threadName, userName);
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	
}
