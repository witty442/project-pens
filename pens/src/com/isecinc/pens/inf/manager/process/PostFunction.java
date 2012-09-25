package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Utils;

public class PostFunction {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String[] process(Connection conn ,TableBean tableBean){
        String[] errors =new String[2];
		String sqlManualExe = "";
		PreparedStatement psManualExe = null;
  	     try{
  	    	 logger.info("Start Process Post Function["+tableBean.getPostFunction()+"]");
  	    	
  	    	 if("after_import_product".equalsIgnoreCase(tableBean.getPostFunction())){
     		    logger.info("process Postfunction Name:"+tableBean.getPostFunction());
   	    	    List<String> sqlManualList = FunctionHelper.readSQlExternalFunction(tableBean.getPostFunction());
   	    	    try{
 	    		    for(int i = 0;i< sqlManualList.size();i++){
 	    		    	sqlManualExe = Utils.isNull(sqlManualList.get(i));
 	    		    	
 	    		    	if(!"".equals(sqlManualExe)){
 					       psManualExe = conn.prepareStatement(sqlManualExe);
 					 
 					       int r = psManualExe.executeUpdate();
 					       logger.info("sqlManualExe :"+sqlManualExe +":result>>"+r);
 	    		    	}
 	    		    }
 	    		  }catch(Exception e){
 	    			  errors[0] = "Error External Function:sql{"+sqlManualExe+"}:{ErrorMsg:"+e.getMessage()+"}";
 		    		  errors[1] = ExceptionHandle.getExceptionCode(e);
                       logger.error(e.getMessage(),e);
 	    		  }
   	    	    
   	    	 }else  if("after_import_promotion".equalsIgnoreCase(tableBean.getPostFunction())){
    		    logger.info("process Postfunction Name:"+tableBean.getPostFunction());
  	    	    List<String> sqlManualList = FunctionHelper.readSQlExternalFunction(tableBean.getPostFunction());
  	    	    try{
	    		    for(int i = 0;i< sqlManualList.size();i++){
	    		    	sqlManualExe = Utils.isNull(sqlManualList.get(i));
	    		    	
	    		    	if(!"".equals(sqlManualExe)){
					       psManualExe = conn.prepareStatement(sqlManualExe);
					 
					       int r = psManualExe.executeUpdate();
					       logger.info("sqlManualExe :"+sqlManualExe +":result>>"+r);
	    		    	}
	    		    }
	    		  }catch(Exception e){
	    			  errors[0] = "Error External Function:sql{"+sqlManualExe+"}:{ErrorMsg:"+e.getMessage()+"}";
		    		  errors[1] = ExceptionHandle.getExceptionCode(e);
                      logger.error(e.getMessage(),e);
	    		  }
  	    	 }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(psManualExe != null){
				  psManualExe.close();
				  psManualExe=null;
				}
			}catch(Exception ee){}
		}
  	   return errors;
	}
	
}
