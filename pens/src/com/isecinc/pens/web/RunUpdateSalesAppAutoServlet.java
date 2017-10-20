package com.isecinc.pens.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import util.ControlCode;

import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;


public class RunUpdateSalesAppAutoServlet  extends HttpServlet{

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
		logger.info("Start startDeploySalesAppAuto Servlet...");
		String localSalesAppPath ="D:/SalesApp/";
		try {
			 //ServletContext sc =  getServletConfig().getServletContext();
			  String install = FileUtil.readFile(localSalesAppPath+"install.txt", "UTF-8");
			  String controlDeploySalesAppAtStartWindow = FileUtil.readFile(localSalesAppPath+"controlDeploySalesAppAtStartWindow.txt", "UTF-8");
              
			  logger.info( "install ["+install+"]");
			  logger.info( "controlDeploySalesAppAtStartWindow ["+controlDeploySalesAppAtStartWindow+"]");
			 
			  if(    Utils.isNull(install).equalsIgnoreCase("runinstall") 
			      && Utils.isNull(controlDeploySalesAppAtStartWindow).equalsIgnoreCase("true")){
				  logger.info("startDeploySalesAppAuto()");
				  
				  if(ControlCode.canExecuteMethod("RunUpdateSalesAppAutoServlet", "startDeploySalesAppAuto")){
				      startDeploySalesAppAuto();
				  }
			  }
		} catch (Exception e) {
			logger.error(e.getMessage());
		} 
	}
	
	public static void startDeploySalesAppAuto() throws Exception{
        String line;
        OutputStream stdin = null;
        InputStream stderr = null;
        InputStream stdout = null;
        String command = "";
        try{
            System.out.println("startTomcat");
            command = "cmd /c start d:/salesApp/SalesAppUpdater/DeploySalesAppAuto.bat_sh.lnk";
             
            Process process = Runtime.getRuntime().exec(command);
            
           // System.out.println("startTomcat:"+child.exitValue());
            stdin = process.getOutputStream ();
            stderr = process.getErrorStream ();
            stdout = process.getInputStream ();

            // "write" the params into stdin
            line = "param1" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

            line = "param2" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

            line = "param3" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

           // System.out.println("stdin:"+stdin.toString());
            stdin.close();

            // clean up if any output in stdout
            BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
            while ((line = brCleanUp.readLine ()) != null) {
              System.out.println ("[Stdout] " + line);
            }
            brCleanUp.close();

            // clean up if any output in stderr
            brCleanUp =new BufferedReader (new InputStreamReader (stderr));
            while ((line = brCleanUp.readLine ()) != null) {
              System.out.println ("[Stderr] " + line);
            }
            brCleanUp.close();
        }catch(Exception e){
           throw e;
        }
   }
	
}
