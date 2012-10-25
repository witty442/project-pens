package com.isecinc.pens.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;


public class AdminConsoleServlet  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1406874613983725131L;
	private Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void init() throws ServletException {
		logger.debug("Start ManualRunScriptDBServlet...");
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		doPost(request, response);
		
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		try{
			response.setCharacterEncoding("TIS-620");
			request.setCharacterEncoding("tis-620");
			
			String currentTab = Utils.isNull(request.getParameter("currentTab"));
			if("".equals(currentTab)){
				currentTab = Utils.isNull(request.getAttribute("currentTab"));
			}
			String action = Utils.isNull(request.getParameter("action"));
			
			System.out.println("Servlet:currentTab:"+request.getParameter("currentTab"));
	
			String configInfo  ="";
			String configInfoTest ="";
	
			String q1 = Utils.isNull(request.getParameter("q1"));
			String q2 = Utils.isNull(request.getParameter("q2"));
	
			String resultQ1 = "";
			String resultQ2 ="";
	
			String eSQL = "";
			String eOutput = "";
	
			String resultBKDB = "";
	
			if(currentTab.equals("tab_config_info") ){
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
				 request.getSession().setAttribute("configInfo", configInfo);
			     request.getSession().setAttribute("configInfoTest", configInfoTest);
			    
			     request.getSession().setAttribute("q1", null);
				 request.getSession().setAttribute("q2", null);
				 request.getSession().setAttribute("resultQ1", null);
				 request.getSession().setAttribute("resultQ2", null);
				 
				 request.getSession().setAttribute("eSQL", null);
				 request.getSession().setAttribute("eOutput", null);
				 request.getSession().setAttribute("resultBKDB", null);
				
			}else if(currentTab.equals("tab_query") && "tab_query".equalsIgnoreCase(action)){
				System.out.println("tabQuery");
				System.out.println("textQSql1:"+q1);
				System.out.println("textQSql2:"+q2);
				
				 if( !q1.equals("")){
				    System.out.println("Query");
				    resultQ1 =  Utils.excQuery(q1);
				 } 
				 if( !q2.equals("")){
					 resultQ2 =  Utils.excQuery(q2);
				 }
				 request.getSession().setAttribute("q1", q1);
				 request.getSession().setAttribute("q2", q2);
				 request.getSession().setAttribute("resultQ1", resultQ1);
				 request.getSession().setAttribute("resultQ2", resultQ2);
				 
				 
				 request.getSession().setAttribute("configInfo", null);
			     request.getSession().setAttribute("configInfoTest", null);
				 
				 request.getSession().setAttribute("eSQL", null);
				 request.getSession().setAttribute("eOutput", null);
				 request.getSession().setAttribute("resultBKDB", null);
				 
			}else if(currentTab.equals("tab_execute") && "tab_execute".equalsIgnoreCase(action)){
				eSQL = Utils.isNull(request.getParameter("eSQL"));
	            
				System.out.println("eSQL:"+eSQL);
				
			    if( !eSQL.equals("")){
			    	eOutput =  Utils.excUpdate(eSQL);
			    	
			    	System.out.println("eOutput:"+eOutput);
			    }	
			    
			     request.getSession().setAttribute("eSQL", eSQL);
			     request.getSession().setAttribute("eOutput", eOutput);
			    
			     request.getSession().setAttribute("configInfo", null);
			     request.getSession().setAttribute("configInfoTest", null);
			    
			     request.getSession().setAttribute("q1", null);
				 request.getSession().setAttribute("q2", null);
				 request.getSession().setAttribute("resultQ1", null);
				 request.getSession().setAttribute("resultQ2", null);
				 
				 request.getSession().setAttribute("resultBKDB", null);
				 
			}else if(currentTab.equals("tab_backupdb") && "tab_backupdb".equalsIgnoreCase(action)){
				  try {   
					  User user = (User)request.getSession().getAttribute("user");
				      String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().process(request,user);
				      
				      resultBKDB +="\n ----------------- Result---------------------------- \n";
				      resultBKDB +="\n Path Local To>> "+path[0];
				      resultBKDB +="\n Path FTP Server To>> "+path[1];
				      
				  } catch(Exception e) {
				     e.printStackTrace();
				  }
				  request.getSession().setAttribute("resultBKDB", resultBKDB);
				  
				  request.getSession().setAttribute("configInfo", null);
			      request.getSession().setAttribute("configInfoTest", null);
			    
			     request.getSession().setAttribute("q1", null);
				 request.getSession().setAttribute("q2", null);
				 request.getSession().setAttribute("resultQ1", null);
				 request.getSession().setAttribute("resultQ2", null);
				 
				 request.getSession().setAttribute("eSQL", null);
				 request.getSession().setAttribute("eOutput", null);
	
			 }
			
			request.setAttribute("currentTab", currentTab);
			
			response.sendRedirect(request.getContextPath()+"/jsp/adminConsole/adminConsole.jsp?currentTab="+currentTab);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
