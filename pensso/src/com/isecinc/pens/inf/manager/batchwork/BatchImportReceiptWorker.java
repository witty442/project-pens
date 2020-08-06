package com.isecinc.pens.inf.manager.batchwork;

import javax.servlet.http.HttpServletRequest;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.receipt.InterfaceReceiptProcess;

public class BatchImportReceiptWorker extends Thread{
	private User user;
	private HttpServletRequest request;
	public BatchImportReceiptWorker(User user,HttpServletRequest request){
	  this.user = user;
	  this.request = request;
	}
	
	@Override
	public void run() {
		try{
		 
		  //Run BatchTask Import Receipt By SalesCode
		  InterfaceReceiptProcess.processImportReceipt(user, request);
		 
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
