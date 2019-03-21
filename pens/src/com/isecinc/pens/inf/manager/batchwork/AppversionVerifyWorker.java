package com.isecinc.pens.inf.manager.batchwork;

import util.AppversionVerify;

public class AppversionVerifyWorker extends Thread{
	
	public AppversionVerifyWorker(){
	
	}
	
	@Override
	public void run() {
		AppversionVerify.initAppVersion();
	}
	
}
