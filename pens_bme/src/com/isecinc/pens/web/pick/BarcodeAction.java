package com.isecinc.pens.web.pick;

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

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class BarcodeAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		BarcodeForm aForm = (BarcodeForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				Barcode ad = new Barcode();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setJob(ad);
			}else if("back".equals(action)){
				aForm.setJob(BarcodeDAO.searchHead(aForm.getJobCriteria()));
				aForm.setResultsSearch(aForm.getJob().getItems());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Barcode b = aForm.getJob();
			if(b.getJobId().equals("")){
				b.setName("");
			}
			aForm.setJob(BarcodeDAO.searchHead(aForm.getJob()));
			aForm.setResultsSearch(aForm.getJob().getItems());
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setJob(new Barcode());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setJob(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		BarcodeForm aForm = (BarcodeForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setJobCriteria(aForm.getJob());
			
            String jobId = Utils.isNull(request.getParameter("jobId"));
            String boxNo = Utils.isNull(request.getParameter("boxNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(jobId) && !"".equals(boxNo)){
				logger.debug("prepare edit jobId:"+jobId +",boxNo:"+boxNo);
				Barcode c = new Barcode();
				c.setJobId(jobId);
				c.setBoxNo(boxNo);
				
				Barcode aS = BarcodeDAO.search(c);
				
				aForm.setResults(aS.getItems());
				aForm.setJob(aS);

				aForm.setMode(mode);//Mode Edit
			}else{
				
				logger.debug("prepare new documentNo");
				aForm.setResults(new ArrayList<Barcode>());
				Barcode ad = new Barcode();
				ad.setCanEdit(true);
				ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				aForm.setJob(ad);
				
				aForm.setMode(mode);//Mode Add new
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BarcodeForm summaryForm = (BarcodeForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BarcodeForm orderForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Barcode aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setJob(aS);
			
			request.setAttribute("Message", msg);
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
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {

			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Barcode h = aForm.getJob();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<Barcode> itemList = new ArrayList<Barcode>();
			//Set Item
			String[] barcode = request.getParameterValues("barcode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			String[] wholePriceBF = request.getParameterValues("wholePriceBF");
			String[] retailPriceBF = request.getParameterValues("retailPriceBF");
			
			logger.debug("barcode:"+barcode.length);
			
			//add value to Results
			if(barcode != null && barcode.length > 0){
				for(int i=0;i<barcode.length;i++){
					if( !Utils.isNull(barcode[i]).equals("") && !Utils.isNull(materialMaster[i]).equals("")){
						 Barcode l = new Barcode();
						 l.setLineId(i+1);
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						 l.setWholePriceBF(Utils.isNull(wholePriceBF[i]));
						 l.setRetailPriceBF(Utils.isNull(retailPriceBF[i]));
						
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//Validate Job in not close
			Job job = new Job();
			job.setJobId(aForm.getJob().getJobId());
			job = JobDAO.search(job);
			
			if(job != null && job.getStatus().equals(JobDAO.STATUS_CANCEL)){
				request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น CANCEL");
				return "search";
			}
			
            if(job != null && job.getStatus().equals(JobDAO.STATUS_CLOSE)){
            	request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น  CLOSE");
				return "search";
			}
            
			//save
			BarcodeDAO.save(h);
			
			//commit
			conn.commit();

			//search
			h = BarcodeDAO.search(h);
			logger.debug("h:"+h+",items:"+h.getItems().size());
			
			aForm.setJob(h);
			aForm.setResults(h.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
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
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setJob(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	public ActionForward newBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("newBox");
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			Barcode ad = new Barcode();
			ad.setJobId(aForm.getJob().getJobId());
			ad.setName(aForm.getJob().getName());
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
			ad.setStoreCode(aForm.getJob().getStoreCode());
			ad.setStoreName(aForm.getJob().getStoreName());
			ad.setStoreNo(aForm.getJob().getStoreNo());
			ad.setSubInv(aForm.getJob().getSubInv());
			ad.setRemark(aForm.getJob().getRemark());
			
			ad.setCanEdit(true);
			
			aForm.setJob(ad);
			aForm.setMode("add");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Barcode h = aForm.getJob();
			h.setUpdateUser(user.getUserName());
			h.setStatus(JobDAO.STATUS_CANCEL);
			
			BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, h);
			BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, h);
			
			conn.commit();
			aForm.setJob(h);
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		BarcodeForm reportForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			Barcode h = BarcodeDAO.searchReport(reportForm.getJob());
			if(h != null){
				//Head
				parameterMap.put("p_boxno", h.getBoxNo());
				parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
				//Gen Report
				String fileName = "boxno_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
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
