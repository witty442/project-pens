package com.isecinc.pens.inf.manager.batchwork;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;

public class UploadDatabaseBackupWorker extends Thread{
	private User user;
	public UploadDatabaseBackupWorker(User user){
		this.user = user;
	}
	
	@Override
	public void run() {
		new DBBackUpManager().process(this.getUser());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
}
