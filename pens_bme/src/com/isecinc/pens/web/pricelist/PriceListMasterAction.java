package com.isecinc.pens.web.pricelist;

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
import com.isecinc.pens.bean.PriceListMasterBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.ConfirmReturnWacoalDAO;
import com.isecinc.pens.dao.PriceListMasterDAO;
import com.isecinc.pens.dao.ReqReturnWacoalDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PriceListMasterAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equalsIgnoreCase(action)){
				aForm.setBean(new PriceListMasterBean());
				aForm.setBeanOLD(null);
				aForm.setResultsSearch(null);
			}else if("back".equalsIgnoreCase(action)){
				aForm.setResultsSearch(PriceListMasterDAO.searchPriceListMaster(aForm.getBeanCriteria()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		PriceListMasterForm summaryForm = (PriceListMasterForm) form;
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
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			List<PriceListMasterBean> results = PriceListMasterDAO.searchPriceListMaster(aForm.getBean());
			if(results != null && results.size() >0){
				aForm.setResultsSearch(results);
			}else{
				aForm.setResultsSearch(null);
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			aForm.setBeanCriteria(aForm.getBean());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}
	
	public ActionForward searchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchDetail");
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		try {	
			PriceListMasterBean cri = new PriceListMasterBean();
			cri.setCustGroup(Utils.isNull(request.getParameter("custGroup")));
			cri.setGroupCode(Utils.isNull(request.getParameter("groupCode")));
			cri.setPensItem(Utils.isNull(request.getParameter("pensItem")));
			cri.setProductType(Utils.isNull(request.getParameter("productType")));
			String mode =Utils.isNull(request.getParameter("mode"));
			
			if("edit".equalsIgnoreCase(mode)){
			   List<PriceListMasterBean> results = PriceListMasterDAO.searchPriceListMaster(cri);
			   aForm.setBean(results.get(0));
			   
			   PriceListMasterBean key = new PriceListMasterBean();
			   key.setCustGroup(results.get(0).getCustGroup());
			   key.setGroupCode(results.get(0).getGroupCode());
			   key.setPensItem(results.get(0).getPensItem());
			   key.setProductType(results.get(0).getProductType());
			   aForm.setBeanOLD(key);//set for update

			}else{
			   cri = new PriceListMasterBean();
			   //cri.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
			   
			   aForm.setBean(cri);
			   aForm.setMode(mode);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}

	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		boolean foundError = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//logger.debug("1 OLD GROUP_CODE:"+Utils.isNull(aForm.getBeanOLD().getGroupCode()));
			
			PriceListMasterBean data = aForm.getBean();
			data.setCreateUser(user.getUserName());
			data.setUpdateUser(user.getUserName());
			
			//Validate GroupCode interface_icc
			boolean isInterfaceICC = PriceListMasterDAO.isInterfaceICC(conn,data);
			if(isInterfaceICC==false){
				//validate pens_item
				boolean foundPensItem = PriceListMasterDAO.isFoundPensItem(conn,data);
				if(foundPensItem ==false){
					msg +="Pens Item นี้ ยังไม่มีการสร้างใน Master Reference , \n";
					foundError = true;
				}
				//validate retail price must more than whole price
				if(Utils.convertStrToDouble(data.getWholePriceBF()) > Utils.convertStrToDouble(data.getRetailPriceBF())){
					msg +="บันทึกราคาไม่ถูกต้อง กรุณาตรวจสอบ , \n";
					foundError = true;
				}
			}else{
				msg += "ไม่สามารถแก้ไขข้อมูลได้ เพราะ Group Code นี้ ได้เคยมีการส่งข้อมูลให้ ICC ไปแล้ว  ,\n";
				foundError = true;
			}
			
			if(foundError==false){
				//save
				if("add".equalsIgnoreCase(aForm.getMode())){ 
					//validate isExist
					PriceListMasterBean oldBean = PriceListMasterDAO.isExist(conn,data);
					if(oldBean == null){
					   //insert 
					   PriceListMasterDAO.insertNew(conn, data);
					}else{
					   //update 
					   PriceListMasterDAO.update(conn,oldBean, data);
					}
				}else{
					//PriceListMasterBean oldBean = PriceListMasterDAO.isExist(conn,data);
					logger.debug("OLD GROUP_CODE:"+Utils.isNull(aForm.getBeanOLD().getGroupCode()));
					
					//update 
					PriceListMasterDAO.update(conn,aForm.getBeanOLD(), data);
				}
				conn.commit();
				request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
			}else{
				conn.rollback();
				request.setAttribute("Message",msg);
			}
			
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
	
	public ActionForward clearSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		try {
			  PriceListMasterBean cri = new PriceListMasterBean();
			  //cri.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
			  aForm.setResultsSearch(null);
			  aForm.setBeanOLD(null);
			  aForm.setBean(cri);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PriceListMasterForm aForm = (PriceListMasterForm) form;
		try {
			  PriceListMasterBean cri = new PriceListMasterBean();
			  aForm.setBeanOLD(null);
			  //cri.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
			  aForm.setBean(cri);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
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
