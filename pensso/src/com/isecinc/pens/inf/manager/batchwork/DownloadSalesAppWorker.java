package com.isecinc.pens.inf.manager.batchwork;

import com.pens.util.AppversionVerify;

public class DownloadSalesAppWorker extends Thread{

	public DownloadSalesAppWorker(){
	}
	
	@Override
	public void run() {
		AppversionVerify.getApp().downloadSalesAppWar();
	}

}
