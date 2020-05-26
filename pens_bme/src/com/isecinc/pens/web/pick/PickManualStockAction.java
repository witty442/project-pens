package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AutoCNDAO;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.autocn.AutoCNBean;
import com.isecinc.pens.web.autocn.AutoCNForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PickManualStockAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		PickManualStockForm aForm = (PickManualStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setJobCriteria(aForm.getJob());
			//New Code
			//aForm.setResultsSearchPrev(aForm.getResultsSearch());
			
			logger.debug("prepare new documentNo");
			aForm.setResults(new ArrayList<Barcode>());
			Barcode ad = new Barcode();
			ad.setCanEdit(true);
			ad.setCanEditGrNo(true);
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setJob(ad);
			
			aForm.setMode("new");//Mode Add new
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
		PickManualStockForm summaryForm = (PickManualStockForm) form;
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
		PickManualStockForm orderForm = (PickManualStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Barcode aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setJob(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PickManualStockForm aForm = (PickManualStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
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
			job = JobDAO.searchJobDetail(job);
			
			if(job != null && job.getStatus().equals(JobDAO.STATUS_CANCEL)){
				request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น CANCEL");
				return "search";
			}
			
            if(job != null && job.getStatus().equals(JobDAO.STATUS_CLOSE)){
            	request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น  CLOSE");
				return "search";
			}
            
			//save
			String boxNo = BarcodeDAO.save(h);
			h.setBoxNo(boxNo);
			
			//search
			h = BarcodeDAO.search(h);
			logger.debug("h:"+h+",items:"+h.getItems().size());
			
			aForm.setJob(h);
			aForm.setResults(h.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//Set Criteria for search
			aForm.getJobCriteria().setJobId(h.getJobId());
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "prepare";
		} finally {
		}
		return "search";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PickManualStockForm aForm = (PickManualStockForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setJob(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		PickManualStockForm aForm = (PickManualStockForm) form;
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
