package com.isecinc.pens.web.adminconsole;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
		//process(mapping, form, request, response);
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
			
			//System.out.println("Servlet:currentTab:"+request.getParameter("currentTab") +"action:"+action);

			if(currentTab.equals("tab_config_info") || "".equals(currentTab)){
				String configInfo  ="";
				String configInfoTest ="";
				
				EnvProperties env = EnvProperties.getInstance();

				String url = env.getProperty("db.url");
				String username = env.getProperty("db.username");
				String password = env.getProperty("db.password");
				
		
				
				configInfo += " ----------------------  Database Config ----------------------------------------------------------------------- \n";
				configInfo +="DB IP : "+url+"\n";
				configInfo +="DB User : "+username+"\n";
				configInfo +="DB Password : "+password+"\n";
				//configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
		
				configInfo += " ----------------------  FTP Server(PENS) Config ---------------------------------------------------------------------- \n";
				configInfo +="FTP IP : "+env.getProperty("ftp.ip.server")+"\n";
				configInfo +="FTP User : "+env.getProperty("ftp.username")+"\n";
				configInfo +="FTP Password: "+env.getProperty("ftp.password")+"\n";
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
				
				configInfo +="HOST DD: "+env.getProperty("host.dd.server")+"\n";
				
				configInfo += " ----------------------  FTP Server(ICC) Config ---------------------------------------------------------------------- \n";
				configInfo +="FTP ICC IP : "+env.getProperty("ftp.icc.ip.server")+"\n";
				configInfo +="FTP ICC User : "+env.getProperty("ftp.icc.username")+"\n";
				configInfo +="FTP ICC Password: "+env.getProperty("ftp.icc.password")+"\n";
				
				configInfo +="path.icc.hisher.export.master.txt(twstock) : "+env.getProperty("path.icc.hisher.export.master.txt")+"\n";
				configInfo +="path.icc.hisher.export.txt(twstock) : "+env.getProperty("path.icc.hisher.export.txt")+"\n";
				configInfo +="path.icc.hisher.import.dlyr(dlyr) : "+env.getProperty("path.icc.hisher.import.dlyr")+"\n";
				configInfo +="path.icc.hisher.export.iccbill(aosvat) : "+env.getProperty("path.icc.hisher.export.iccbill")+"\n";
				
				configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
				
				configInfo += " ----------------------  Host PayInReport Server @Pens ---------------------------------------------------------------------- \n";
				configInfo +="IP PayInReport : "+env.getProperty("host.payinreport")+"\n";
				//configInfo += " -------------------------------------------------------------------------------------------------------------------- \n";
				
				
				
			    configInfoTest = " ";
			   if("tab_config_info".equalsIgnoreCase(action)){
					configInfoTest += " ----------------------  Result Test DB Connection -------------------------------------------------------------- \n";
						 try {   
							 configInfoTest += " \n "+ test.TestALL.testDBCon();
							 
							
						  } catch(Exception e) {
							  configInfoTest += " \n error:>> "+e.getMessage();
						  }
						 
						 
					configInfoTest += " ----------------------  Result Test FTP(PENS) Connection ------------------------------------------------------------- \n";
						try {   
							 configInfoTest += " \n "+ test.TestALL.testFTPPensCon();
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
						
				   configInfoTest += "----------------------  Result Test FTP(ICC) Connection ------------------------------------------------------------- \n";
						try {   
							 configInfoTest += " \n "+ test.TestALL.testFTPICCCon();
						  } catch(Exception e) {
							 configInfoTest += " \n error :>> "+e.getMessage();
						  }
				   configInfoTest += "----------------------  Result Test DataSource Connection ------------------------------------------------------------- \n";
					 
						try {   
							 configInfoTest += " \n "+  test.TestALL.testDataSource();
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
				     /* String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().process(request,user);
				      
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
