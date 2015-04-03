package com.isecinc.pens.web.adminconsole;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import manual.cleardb.ClearDB;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.cfg.Configuration;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Member Receipt Action
 * 
 * @author atiz.b
 * @version $Id: MemberReceiptAction.java,v 1.0 07/02/2011 00:00:00 atiz.b Exp $
 * 
 */
public class AdminConsoleAction extends I_Action {

	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare1");
		return "process";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare2");
		return "process";
	}

	/**
	 * Search
	 */
	public ActionForward process(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("process");
        AdminConsoleForm adForm =(AdminConsoleForm)form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String currentTab = Utils.isNull(request.getParameter("currentTab"));
			if("".equals(currentTab)){
				currentTab = Utils.isNull(request.getAttribute("currentTab"));
			}
			String action = Utils.isNull(request.getParameter("action"));
			
			System.out.println("Servlet:currentTab:"+request.getParameter("currentTab"));

			if(currentTab.equals("tab_config_info") || "".equals(currentTab)){
				String configInfo  ="";
				String configInfoTest ="";
				
				EnvProperties env = EnvProperties.getInstance();
				Configuration hibernateConfig = new Configuration();
				hibernateConfig.configure();
		
				String url = hibernateConfig.getProperty("connection.url");
				String username = hibernateConfig.getProperty("connection.username");
				String password = hibernateConfig.getProperty("connection.password");
		
				configInfo += " ----------------------  DataBase Config ----------------------------------------------------------------------- \n";
				configInfo +="DB IP : "+url+"\n";
				configInfo +="DB User : "+username+"\n";
				configInfo +="DB Password : "+password+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
		
				configInfo += " ----------------------  FTP Server Config ---------------------------------------------------------------------- \n";
				configInfo +="FTP IP : "+env.getProperty("ftp.ip.server")+"\n";
				configInfo +="FTP IP TEMP : "+env.getProperty("ftp.ip.server.temp")+"\n";
				configInfo +="FTP User : "+env.getProperty("ftp.username")+"\n";
				configInfo +="FTP Password: "+env.getProperty("ftp.password")+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
		
			    configInfoTest = " ";
			   if("tab_config_info".equalsIgnoreCase(action)){
					configInfoTest += "\n ----------------------  Result Test DB Connection -------------------------------------------------------------- \n";
						 try {   
							 configInfoTest += " \n "+ test.TestALL.testDBCon();
						  } catch(Exception e) {
							  configInfoTest += " \n error:>> "+e.getMessage();
						  }
						 
						 
					configInfoTest += "\n ----------------------  Result Test FTP Connection ------------------------------------------------------------- \n";
						try {   
							 configInfoTest += " \n "+ test.TestALL.testFTPCon();
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
			   }		
			   
			   logger.debug(configInfo);
				
			   logger.debug("adForm:"+adForm);
			   
			   adForm.setConfigInfo(configInfo);
			   adForm.setConfigInfoTest(configInfoTest);
			   
			}else if(currentTab.equals("tab_query") && "tab_query".equalsIgnoreCase(action)){
				String resultQ1 = "";
				String resultQ2 ="";
				System.out.println("tabQuery");
				String q1 = Utils.isNull(adForm.getQ1());
				String q2 = Utils.isNull(adForm.getQ2());
				System.out.println("textQSql1:"+q1);
				System.out.println("textQSql2:"+q2);
				
				 if( !q1.equals("")){
				    System.out.println("Query");
				    resultQ1 =  Utils.excQuery(q1);
				 } 
				 if( !q2.equals("")){
					 resultQ2 =  Utils.excQuery(q2);
				 }
				
				 adForm.setResultQ1(resultQ1);
				 adForm.setResultQ2(resultQ2);
				 
			}else if(currentTab.equals("tab_execute") && "tab_execute".equalsIgnoreCase(action)){
				String eSQL = "";
				String eOutput = "";
				
				eSQL = Utils.isNull(adForm.geteSQL());
	            
				System.out.println("eSQL:"+eSQL);
				
			    if( !eSQL.equals("")){
			    	eOutput =  Utils.excUpdate(eSQL);
			    	
			    	System.out.println("eOutput:"+eOutput);
			    }	
			    
			    adForm.seteOutput(eOutput);
			    
			}else if(currentTab.equals("tab_backupdb") && "tab_backupdb".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
				      String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().process(request,user);
				      
				      resultBKDB +="\n ----------------- Result---------------------------- \n";
				      resultBKDB +="\n Path Local To>> "+path[0];
				      resultBKDB +="\n Path FTP Server To>> "+path[1];
				      
				      adForm.setResultBKDB(resultBKDB);
				      
				  } catch(Exception e) {
				     e.printStackTrace();
				  }
			}else if(currentTab.equals("tab_cleardb") && "tab_cleardb".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
				      
				      
					  StringBuffer resultClearDB = ClearDB.clearDB() ;
					  
					  adForm.setResultClearDB(resultClearDB.toString());
					  
				  } catch(Exception e) {
				     e.printStackTrace();
				  } 
			
			request.setAttribute("currentTab", currentTab);
		   }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mapping.findForward("process");
	}
	
    protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "";
	}

	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "";
	}

	@Override
	protected void setNewCriteria(ActionForm form) {
		
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
