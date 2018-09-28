package com.isecinc.pens.web.other;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.ConfirmReturnWacoal;
import com.isecinc.pens.bean.ControlReturnReport;
import com.isecinc.pens.bean.GenVanScriptBean;
import com.isecinc.pens.bean.PriceListMasterBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.ConfirmReturnWacoalDAO;
import com.isecinc.pens.dao.PriceListMasterDAO;
import com.isecinc.pens.dao.ReqReturnWacoalDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.FileUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class GenVanScriptAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		GenVanScriptForm aForm = (GenVanScriptForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equalsIgnoreCase(action)){
				aForm.setBean(new GenVanScriptBean());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GenVanScriptForm summaryForm = (GenVanScriptForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		GenVanScriptForm aForm = (GenVanScriptForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		GenVanScriptForm aForm = (GenVanScriptForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer script = new StringBuffer("");
		try {
			  if( !Utils.isNull(aForm.getBean().getScriptSQL()).equals("")){
				  String[] scriptSQlArr = Utils.isNull(aForm.getBean().getScriptSQL()).split("\\,");
				  for(int i=0;i<scriptSQlArr.length;i++){
					  String[] custArr = scriptSQlArr[i].split("\\-");
					  script.append("insert into pens.c_customer_receipt_credit values('"+custArr[0]+"','Y');\n");
					  script.append("insert into pens.c_customer_receipt_cheque values('"+custArr[0]+"','Y');\n");
				  }
			  }
			  //upload to FTP
			  if(script.length() >0){
				  EnvProperties env = EnvProperties.getInstance();
				  FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
				  String pathFtpFull = "/Manual-script/BySales/"+aForm.getBean().getPrefix()+"/"+"script_"+aForm.getBean().getSaleCode()+".sql";
				  
				  //Write File Temp to Local
                  String localFile = request.getRealPath("temps")+"/"+"script_"+aForm.getBean().getSaleCode()+".sql";
				  FileUtil.writeFile(localFile, script.toString(), "TIS-620");
				  
				  logger.debug("script:\n "+script.toString());
				  logger.debug("upload From Local Path:\n "+localFile);
				  logger.debug("upload Ftp to Path:\n "+pathFtpFull);
				  
				  //ftpManager.uploadFileToFTP(pathFtpFull, script);
				  ftpManager.uploadFileFromLocal(pathFtpFull, localFile);
				  
				  //delete temp
				  FileUtil.deleteFile(localFile);
			  }

			  request.setAttribute("Message","ส่งข้อมูลเรียบร้อยแล้ว");
			  //clear Form
			  aForm.setBean(new GenVanScriptBean());
		} catch (Exception e) {

            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถส่งข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "search";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}
	
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		GenVanScriptForm aForm = (GenVanScriptForm) form;
		try {
			GenVanScriptBean cri = new GenVanScriptBean();
			aForm.setBean(cri);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
