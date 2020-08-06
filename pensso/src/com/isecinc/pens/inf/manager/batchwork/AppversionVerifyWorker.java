package com.isecinc.pens.inf.manager.batchwork;

import java.util.Date;

import util.AppversionVerify;

public class AppversionVerifyWorker extends Thread{
	
	public AppversionVerifyWorker(){
	
	}
	
	@Override
	public void run() {
		try{
		 /* long start = System.currentTimeMillis();
	      System.out.println("Wait  AppversionVerify.initAppVersion:"+new Date());
		  Thread.sleep(30000);
		  System.out.println("Start AppversionVerify.initAppVersion:"+new Date());
		  System.out.println("Sleep time in ms = "+(System.currentTimeMillis()-start));
		  */
		 // AppversionVerify.initAppVersion();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
