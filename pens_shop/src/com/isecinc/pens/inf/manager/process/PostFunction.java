package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Utils;

public class PostFunction {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String[] process(Connection conn ,TableBean tableBean,User user){
        String[] errors =new String[2];
		String sqlManualExe = "";
		PreparedStatement psManualExe = null;
  	     try{
  	    	 logger.debug("Start Process Post Function["+tableBean.getPostFunction()+"]");
  	    	
  	    	 if("Y".equalsIgnoreCase(tableBean.getPostFunction())){
     		    logger.debug("process Postfunction Name:"+tableBean.getTableName());
     		    
     		    if("ad_user".equalsIgnoreCase(tableBean.getTableName())){
     	   	    	    try{
     	   	    	    	List<String> sqlManualList = new ArrayList<String>();
     	    	    	    sqlManualExe = "update ad_user set isactive ='N' where user_name not in('admin','"+user.getUserName()+"');";
     	    	    	    sqlManualList.add(sqlManualExe);
     	    	    	    
     	    	    	    //delete salestarget_new <> UserId Login
     	    	    	    sqlManualExe = "delete from m_sales_target_new where user_id <> "+user.getId()+";";
     	    	    	    sqlManualList.add(sqlManualExe);
     	    	    	    
     	    	    	    //delete m_trip  <> UserId Login
     	    	    	    sqlManualExe = "delete from m_trip where user_id <> "+user.getId()+";";
     	    	    	    sqlManualList.add(sqlManualExe);
     	    	    	    
     	    	    	    //delete m_trip  <> UserId Login
     	    	    	    sqlManualExe = "delete from c_trx_history where user_id <> "+user.getId()+";";
     	    	    	    sqlManualList.add(sqlManualExe);
     	    	    	    
     	    	    	    //delete salestarget_new <> UserId Login
     	    	    	    //sqlManualExe = "delete from ad_user where user_name not in('admin','"+user.getUserName()+"');";
     	    	    	    sqlManualExe = "delete from ad_user where 1=1 and ( user_name not in('admin') and user_id not in("+user.getId()+") );";
     	    	    	    sqlManualList.add(sqlManualExe);
     	    	    	    
     	    	    	    for(int i = 0;i< sqlManualList.size();i++){
     		    		    	sqlManualExe = Utils.isNull(sqlManualList.get(i));
     		    		    	
     		    		    	if(!"".equals(sqlManualExe)){
     						       psManualExe = conn.prepareStatement(sqlManualExe);
     						 
     						       int r = psManualExe.executeUpdate();
     						       logger.info("sqlManualExe :"+sqlManualExe +":result>>"+r);
     		    		    	}
     		    		    }
     	 	    		  }catch(Exception e){
     	                       logger.error(e.getMessage(),e);
     	 	    		  }
     	   	    	  
     		    }else{
     		    	
	   	    	    List<String> sqlManualList = FunctionHelper.readSQlExternalFunction(conn,"post-import-master",tableBean.getTableName());
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
