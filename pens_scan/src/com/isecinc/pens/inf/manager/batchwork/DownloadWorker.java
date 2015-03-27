package com.isecinc.pens.inf.manager.batchwork;

import util.AppversionVerify;

public class DownloadWorker extends Thread{
	
	@Override
	public void run() {
		AppversionVerify.downloadSoftware();
	}
}
