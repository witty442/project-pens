package com.isecinc.pens.inf.manager.batchwork;

import com.isecinc.pens.bean.User;

import util.AppversionVerify;

public class DownloadWorker extends Thread{
	private User user;
	
	public DownloadWorker(User user){
		this.user = user;
	}
	
	@Override
	public void run() {
		AppversionVerify.downloadSoftware(this.user);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
