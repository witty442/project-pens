package com.isecinc.pens.inf.manager.batchwork;


import javax.servlet.http.HttpServletRequest;


import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;


/*
 * thread to run MasterImport
 */
public class BatchExportBackUPDBWorker extends BatchWorker {

	private HttpServletRequest request;
	private User user;
	
    
	public BatchExportBackUPDBWorker(HttpServletRequest request,User user) {
        this.request = request;
        this.user = user;
	}

	@Override
	public void run() {

		try {
			DBBackUpManager.processToFTPServer(request,user);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
  
	
}
