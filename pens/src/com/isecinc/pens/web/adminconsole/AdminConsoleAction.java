package com.isecinc.pens.web.adminconsole;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;
import com.pens.utils.manual.cleardb.ClearDB;
import com.pens.utils.manual.cleardb.ClearDupDB;

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
				
				String url = env.getProperty("connection.url");
				String username = env.getProperty("connection.username");
				String password = env.getProperty("connection.password");
				configInfo += "*****Config Type:"+env.getProperty("config.type")+"************************\n";
				configInfo += "\n Printer LQ300 NAME : EPSON LQ-300+ /II ESC/P 2 (PENS_A5)\n\n";
				configInfo += " ----------------------  DataBase Config ----------------------------------------------------------------------- \n";
				configInfo +="DB IP : "+url+"\n";
				configInfo +="DB User : "+username+"\n";
				configInfo +="DB Password : "+password+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
		
				configInfo += " ----------------------  FTP Server Config ---------------------------------------------------------------------- \n";
				configInfo +="FTP IP : "+env.getProperty("ftp.ip.server")+"\n";
				configInfo +="FTP User : "+env.getProperty("ftp.username")+"\n";
				configInfo +="FTP Password: "+env.getProperty("ftp.password")+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
		
			    configInfoTest = " ";
			   if("tab_config_info".equalsIgnoreCase(action)){
					configInfoTest += "\n ----------------------  Result Test DB Connection -------------------------------------------------------------- \n";
						 try {   
							 configInfoTest += " \n "+ com.pens.test.TestALL.testDBCon();
						  } catch(Exception e) {
							  configInfoTest += " \n error:>> "+e.getMessage();
						  }
						 
						 
					configInfoTest += "\n ----------------------  Result Test FTP Connection ------------------------------------------------------------- \n";
						try {   
							 configInfoTest += " \n "+ com.pens.test.TestALL.testFTPCon();
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
						
					/*configInfoTest += "\n ----------------------  Result Test GPS Connection ------------------------------------------------------------- \n";
						try {   
							 // SerialTest2.test();
							  configInfoTest += "\n Test GPS Start ";
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
				    */
			   }		
			   
			   logger.debug(configInfo);
				
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
				      String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().process(user);
				      
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
			
			}else if(currentTab.equals("tab_clearcust_dup") && "tab_clearcust_dup".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
					  StringBuffer resultClearDB = ClearDupDB.clearDupCustDB() ;
					  logger.debug("resultClearDB.toString():"+resultClearDB.toString());
					  
					  adForm.setResultClearCustDup(resultClearDB.toString());
					  
				  } catch(Exception e) {
				     e.printStackTrace();
				  } 
			
			request.setAttribute("currentTab", currentTab);
			
			}else if(currentTab.equals("tab_add_db") && "tab_add_db".equalsIgnoreCase(action)){
				String resultBKDB = "";
				Connection conn = null;
				  try {   
					  conn = DBConnection.getInstance().getConnection();
					  String scriptDBName = Utils.isNull(request.getParameter("scriptDBName"));
					  logger.debug("scriptDBName:"+scriptDBName);
					  if( !scriptDBName.equals("")){
				         resultBKDB =RunScriptDBAction.runScriptDBByName(request, conn,scriptDBName);
				         adForm.setResultAddDB(resultBKDB);
					  }
				  } catch(Exception e) {
				     e.printStackTrace();
				  }finally{
					  conn.close();
				  }
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
