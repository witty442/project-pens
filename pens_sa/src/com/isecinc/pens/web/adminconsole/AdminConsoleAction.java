package com.isecinc.pens.web.adminconsole;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

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
			String export = Utils.isNull(request.getParameter("export"));
			
			System.out.println("Servlet:currentTab:"+request.getParameter("currentTab") +",action="+action+",export="+export);

			if(currentTab.equals("tab_config_info") || "".equals(currentTab)){
				String configInfo  ="";
				String configInfoTest ="";
				
				EnvProperties env = EnvProperties.getInstance();
				
				String url = env.getProperty("db.url");
				String username = env.getProperty("db.username");
				String password = env.getProperty("db.password");
				configInfo += "*****product.type:"+env.getProperty("product.type")+"************************\n";
				
				configInfo += " ----------------------  DataBase Config ----------------------------------------------------------------------- \n";
				configInfo +="DB IP : "+url+"\n";
				//configInfo +="DB User : "+username+"\n";
				//configInfo +="DB Password : "+password+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
	
			    configInfoTest = " ";
			   if("tab_config_info".equalsIgnoreCase(action)){
					configInfoTest += "\n ----------------------  Result Test DB Connection -------------------------------------------------------------- \n";
						 try {   
							 //configInfoTest += " \n "+ com.pens.test.TestALL.testDBCon();
						  } catch(Exception e) {
							  configInfoTest += " \n error:>> "+e.getMessage();
						  }
						  
					configInfoTest += "\n ----------------------  Result Test FTP Connection ------------------------------------------------------------- \n";
						try {   
							// configInfoTest += " \n "+ com.pens.test.TestALL.testFTPCon();
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
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
				if( !"true".equalsIgnoreCase(export)){
					 if( !q1.equals("")){
					    System.out.println("Query");
					    resultQ1 =  SQLHelper.excQuery(q1);
					    //FileUtil.writeFile("C:\\Users\\WITTY-LENOVO\\Desktop\\jojo\\temp.xls", resultQ1.toString(),"TIS-620");
					 } 
					 if( !q2.equals("")){
						 resultQ2 =  SQLHelper.excQuery(q2);
					 }
					
					 adForm.setResultQ1(resultQ1);
					 adForm.setResultQ2(resultQ2);
				}else{
					logger.debug("Submit Export To Excel");
					q1 = Utils.isNull(adForm.getQ1());
					try{
						 String eOutput =  SQLHelper.excQuery(q1);
						 
					     java.io.OutputStream out = response.getOutputStream();
						 response.setHeader("Content-Disposition", "attachment; filename=data.xls");
						 response.setContentType("application/vnd.ms-excel");
						 Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
						 w.write(eOutput);
					     w.flush();
					     w.close();
					
					     out.flush();
					     out.close();
					 }catch(Exception e){
					    e.printStackTrace();
					 }
				}
				 
			}else if(currentTab.equals("tab_execute") && "tab_execute".equalsIgnoreCase(action)){
				String eSQL = "";
				String eOutput = "";
				
				eSQL = Utils.isNull(adForm.geteSQL());
	            
				System.out.println("eSQL:"+eSQL);
				
			    if( !eSQL.equals("")){
			    	eOutput =  SQLHelper.excUpdate(eSQL);
			    	
			    	System.out.println("eOutput:"+eOutput);
			    }	
			    
			    adForm.seteOutput(eOutput);
			    
			}else if(currentTab.equals("tab_backupdb") && "tab_backupdb".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
				    /*  String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().process(user);
				      
				      resultBKDB +="\n ----------------- Result---------------------------- \n";
				      resultBKDB +="\n Path Local To>> "+path[0];
				      resultBKDB +="\n Path FTP Server To>> "+path[1];
				      
				      adForm.setResultBKDB(resultBKDB);*/
				      
				  } catch(Exception e) {
				     e.printStackTrace();
				  }
			}else if(currentTab.equals("tab_cleardb") && "tab_cleardb".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
					/*  StringBuffer resultClearDB = ClearDB.clearDB() ;
					  
					  adForm.setResultClearDB(resultClearDB.toString());*/
					  
				  } catch(Exception e) {
				     e.printStackTrace();
				  } 
			
			request.setAttribute("currentTab", currentTab);
			
			}else if(currentTab.equals("tab_clearcust_dup") && "tab_clearcust_dup".equalsIgnoreCase(action)){
				String resultBKDB = "";
				  try {   
					  /*StringBuffer resultClearDB = ClearDupDB.clearDupCustDB() ;
					  logger.debug("resultClearDB.toString():"+resultClearDB.toString());
					  
					  adForm.setResultClearCustDup(resultClearDB.toString());*/
					  
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
				       /*  resultBKDB =RunScriptDBAction.runScriptDBByName(request, conn,scriptDBName);
				         adForm.setResultAddDB(resultBKDB);*/
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
