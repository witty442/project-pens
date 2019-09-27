package com.isecinc.pens.web.b2b;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.ImportB2BMakroFromExcelTask;
import com.lowagie.text.pdf.BaseFont;
import com.pens.util.BeanParameter;
import com.pens.util.CConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class B2BAction extends I_Action {

	public static int pageSize = 90;
	 public static final String PAGE_B2B_MAKRO = "B2BMakro";
	 
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		B2BForm aForm = (B2BForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		String forward ="search";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String popup = Utils.isNull(request.getParameter("popup")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().removeAttribute("BATCH_TASK_RESULT");
				
				B2BBean sales = new B2BBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
					B2BControlPage.prepareB2BMakro(request, conn, user,pageName);
				}
				aForm.setBean(sales);
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
			}
			logger.debug("forward:"+forward);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("importExcel :pageName["+pageName+"]");
	
			 //Search Report
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				//Prepare Parameter to BatchTask
				Map<String, String> batchParaMap = new HashMap<String, String>();
				batchParaMap.put(ImportB2BMakroFromExcelTask.PARAM_DATA_TYPE, aForm.getBean().getDataType());
				
				request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
				request.getSession().setAttribute("DATA_FILE", aForm.getDataFormFile());
				request.setAttribute("action","submitedImport");//set to popup page to BatchTask
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("exportExcel :pageName["+pageName+"]");
	
			 //Search Report
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				//Prepare Parameter to BatchTask
				Map<String, String> batchParaMap = new HashMap<String, String>();
				request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
				request.setAttribute("action","submitedExport");//set to popup page to BatchTask
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward loadExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("loadExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		String fileName = ""; 
		String pathFile = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			logger.debug("loadExcel :pageName["+pageName+"]");
	
			//Load Excel
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 pathFile = env.getProperty("path.temp");
		    	 fileName = Utils.isNull(request.getParameter("fileName"));
		    	 if( !Utils.isNull(fileName).equals("")){
		    		 pathFile +=fileName; 
			    	 logger.debug("pathFile:"+pathFile);
			    	  
		    		//read file from temp file
					 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFile));
					 
					response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
					response.setContentType("application/excel");
					
					ServletOutputStream servletOutputStream = response.getOutputStream();
					servletOutputStream.write(bytes, 0, bytes.length);
					servletOutputStream.flush();
					servletOutputStream.close();
		    	 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("searchBatch :pageName["+pageName+"]");
	
			 //searchBatch
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
				 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
				 
				 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 
				 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
				 logger.debug("fileName:"+batchTaskForm.getMonitorItem().getFileName());
				 
				 //Case file name is not null Is Case Export set fileName to User download
				 if(batchTaskForm.getResults()[0].getName().equalsIgnoreCase(BatchTaskConstants.EXPORT_B2B_MAKRO_TO_EXCEL)){
					 if(batchTaskForm.getResults()[0].getStatus() != -1){
					    request.setAttribute("LOAD_EXPORT_FILE_NAME", batchTaskForm.getMonitorItem().getFileName());
					 }
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		B2BForm aForm = (B2BForm) form;
		String pageName = aForm.getPageName();
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		B2BForm orderForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
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
