package com.isecinc.pens.web.cn;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.GenCNDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class GenCNAction extends I_Action {


	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		GenCNForm aForm = (GenCNForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			GenCNBean w = new GenCNBean();
			
			aForm.setBean(w);
			aForm.setResults(null);
			
		} catch (Exception e) {
			request.setAttribute("Message", "error:"+ e.getMessage());
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
		GenCNForm aForm = (GenCNForm) form;
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
		GenCNForm aForm = (GenCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			GenCNBean p = GenCNDAO.searchHead(aForm.getBean());
			boolean foundError = p.isFoundError();
			
			if(foundError ==false){
				//Check is loaded
				boolean isLoaded = GenCNDAO.isLoaded(p);
				if(isLoaded){
					msg  ="CN No นี้ ได้เคยนำมา Generate ไปแล้ว กรุณาตรวจสอบใหม่";
				}else{
				
					p.setWarehouse(PickConstants.WAREHOUSE_W3);
					if(p.getItems() != null && p.getItems().size() >0){
						aForm.setResults(p.getItems());
					}else{
						msg  ="ไม่พบข้อมูล ";
						aForm.setResults(null);
					}
					//set to form
					aForm.setBean(p);
				}
			}else{
				msg  ="ไม่พบข้อมูล Group Code ไม่สามารถ Generate ได้";
				aForm.setResults(p.getItems());
			}
			//logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn = null;
			}
		}
		return "search";
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		GenCNForm aForm = (GenCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int countQty = 0;
		int lineId = 0;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			GenCNBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Save Job
			Job job = new Job();
			job.setName(h.getJobName());
			job.setOpenDate(h.getInvoiceDate());
			job.setCloseDate(h.getInvoiceDate());
			job.setStoreCode(h.getStoreCode());
			job.setStatus(PickConstants.STATUS_CLOSE);
			job.setWareHouse(PickConstants.WAREHOUSE_W3);
			job.setSubInv("G071");
			job.setCreateUser(user.getUserName());
			StoreBean storeBean = GeneralDAO.getStoreBeanModel(conn, job.getStoreCode());
			if(storeBean !=null){
				job.setCustGroup(storeBean.getCustGroup());
				job.setStoreNo(storeBean.getStoreNo());
			}
			
			job = JobDAO.saveCaseGenCn(conn, job, user);
			
			//Save Barcode Head
			Barcode ad = new Barcode();
			ad.setBoxNo(BarcodeDAO.genBoxNo(new Date()));
			ad.setJobId(job.getJobId());
			ad.setStatus(PickConstants.STATUS_ISSUED);
			ad.setName("");
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setStoreCode(job.getStoreCode());
			ad.setStoreNo(job.getStoreNo());
			ad.setSubInv(job.getSubInv());
			ad.setRemark(h.getCnNo());
			ad.setWareHouse(job.getWareHouse());
			ad.setCreateUser(user.getUserName());
			
			BarcodeDAO.saveHeadModel(conn, ad);
		
			//Save Barcode Item
			//String[] lineId = request.getParameterValues("lineId");
			
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			String[] qty = request.getParameterValues("qty");
			
			logger.debug("groupCode:"+groupCode.length);
			
			//add value to Results
			if(groupCode != null && groupCode.length > 0){
				for(int i=0;i<groupCode.length;i++){
					logger.debug("barcode:"+groupCode[i]);
					if( !Utils.isNull(groupCode[i]).equals("") && !Utils.isNull(pensItem[i]).equals("")){
						
						 Barcode l = new Barcode();
						 l.setBoxNo(ad.getBoxNo());
						 l.setJobId(ad.getJobId());		
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						 
						 Barcode b = GeneralDAO.searchProductByGroupCodeModelBMELocked(conn, l.getGroupCode());
						 
						 l.setBarcode(Utils.isNull(b.getBarcode()));
						 l.setMaterialMaster(Utils.isNull(b.getMaterialMaster()));
						 l.setWholePriceBF(Utils.isNull(b.getWholePriceBF()));
						 l.setRetailPriceBF(Utils.isNull(b.getRetailPriceBF()));
						
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 l.setStatus(PickConstants.STATUS_ISSUED);
						 
						 countQty = Utils.convertStrToInt(qty[i]);
						 if(countQty >0){
							for(int r=0;r<countQty;r++){
								lineId++;
								l.setLineId(lineId);
								BarcodeDAO.saveItemModel(conn, l);
							}
						 }else{
							lineId++;
							l.setLineId(lineId);
						    BarcodeDAO.saveItemModel(conn, l);
						 }
					}
				}
			}

		   // hide save button
		    h.setCanEdit(false);
		    h.setJobId(job.getJobId());
		    h.setBoxNo(ad.getBoxNo());
		    
			//set to form
			request.setAttribute("Message","ทำการ Generate ข้อมูลจาก CN เรียบร้อยแล้ว");
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
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
		GenCNForm aForm = (GenCNForm) form;
		try {
			GenCNBean w = new GenCNBean();
			
			aForm.setBean(w);
			aForm.setResults(null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
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
