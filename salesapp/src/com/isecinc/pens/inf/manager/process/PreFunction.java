package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.pens.util.Utils;

public class PreFunction {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String[] process(Connection conn ,TableBean tableBean,User user) {
        String[] errors =new String[2];
		String sqlManualExe = "";
		PreparedStatement psManualExe = null;
  	     try{
  	    	 logger.debug("Start Process pre Function ");

  	    	 if("Y".equalsIgnoreCase(tableBean.getPreFunction())){
   	    		    logger.debug("process prefunction Name:"+tableBean.getPreFunction());
 	  	    	    List<String> sqlManualList = FunctionHelper.readSQlExternalFunction(conn,"pre-import-master",tableBean.getTableName());
 	  	    	    try{
 	  	    	    	if(sqlManualList != null && sqlManualList.size() >0){
	 		    		    for(int i = 0;i< sqlManualList.size();i++){
	 		    		    	sqlManualExe = Utils.isNull(sqlManualList.get(i));
	 		    		    	if(!"".equals(sqlManualExe)){
	 							    psManualExe = conn.prepareStatement(sqlManualExe);
	 							 
	 							    int r = psManualExe.executeUpdate();
	 							    logger.info("sqlManualExe :"+sqlManualExe +":result>>"+r);
	 		    		    	}
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
				  psManualExe.close();psManualExe=null;
				}
			}catch(Exception ee){}
		}
  	   return errors;
	}
	
}
