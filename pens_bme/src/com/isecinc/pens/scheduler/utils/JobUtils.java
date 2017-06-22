package com.isecinc.pens.scheduler.utils;

import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.scheduler.manager.ScheduleVO;

public class JobUtils {
	
	private static Logger logger = Logger.getLogger(JobUtils.class);
	
	public static String genJobId(ScheduleVO param){
		String jobId ="NO_JOB_ID";
		try{
			jobId = "JOB_"+Utils.isNull(param.getProgramId())+"_"+Utils.isNull(param.getGroupId());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return jobId;
	}
	public static String genRegenJobId(ScheduleVO param){
		String jobId ="NO_JOB_ID";
		try{
			jobId = "REGENJOB_"+Utils.isNull(param.getProgramId())+"_"+Utils.isNull(param.getGroupId());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return jobId;
	}
	
	public static String genFileName(ScheduleVO param){
		String fileName ="";
		try{
			//String ddmmyyyy = DateUtil.stringValue(new Date(), "ddMMyyy");
			fileName = "Test_"+param.getUserId()+".csv";
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return fileName;
	}
	
	public static String genName(ScheduleVO param,String type){
		String folderName ="NO_NAME";
		try{
			if(type.equals(Constants.TYPE_GENFOLDER_BY_CURRENTDATE)){
			   folderName = DateUtil.stringValue(new Date(), "ddMMyyyy");
			}else if(type.equals(Constants.TYPE_GENFOLDER_BY_ENTITY)){
			 folderName = !Utils.isNull(param.getEntity()).equals("0")?param.getEntity():"ALL_ENTITY";
			}else if(type.equals(Constants.TYPE_GENFOLDER_BY_PRODUCT)){
			   folderName = !Utils.isNull(param.getProduct()).equals("0")?param.getProduct():"ALL_PRODUCT";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return folderName;
	}
	
	
	
}