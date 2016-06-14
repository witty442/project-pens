package com.isecinc.pens.web.pick;

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
import com.isecinc.pens.bean.MoveStockWarehouseBean;
import com.isecinc.pens.bean.MoveWarehouse;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.MoveStockWarehoseDAO;
import com.isecinc.pens.dao.MoveWarehoseDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.pick.process.OnhandProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MoveStockWarehouseAction extends I_Action {


	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			MoveStockWarehouseBean w = new MoveStockWarehouseBean();
			//w.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//w.setCloseDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
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
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
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
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			MoveStockWarehouseBean p = MoveStockWarehoseDAO.searchHead(aForm.getBean());
			if(p.getItems() != null && p.getItems().size() >0){
				aForm.setResults(p.getItems());
				p.setCanEdit(true);
			}else{
				msg  ="ไม่พบข้อมูล ";
				aForm.setResults(null);
			}
			//set to form
			aForm.setBean(p);
			
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
	
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
		User user = (User) request.getSession().getAttribute("user");
        String msg  = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MoveStockWarehouseBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//validate Stock
			boolean canMoveStock  = false;
			MoveStockWarehouseBean reCheck = MoveStockWarehoseDAO.canMoveStockFinish(conn, h);
			
			if(reCheck != null){
				if(Utils.convertStrToInt(reCheck.getOnhandQty()) >= Utils.convertStrToInt(h.getTransferQty()) ){
					canMoveStock = true;
				}else{
					msg += "จำนวนสินค้าไม่พอ ที่จะโอนได้ กรุณาตรวจสอบ  จำนวน  onhand คงแหลือ ="+reCheck.getOnhandQty();
				}
			}else{
				msg += "ไม่่พบข้อมูล Stock รายการนี้ในระบบ";
			}
			
		    if(canMoveStock){
		    	//del Update Stock Finish warehouse from
		    	MoveStockWarehoseDAO.delStockFinishFromWarehouse(conn, h);
		    	
		    	//add Update Stock Finish warehouse To
		    	MoveStockWarehoseDAO.processUpdateStockFinishToWarehouse(conn, h);
		    	
		    	//update or insert move stock finish history
		    	MoveStockWarehoseDAO.insertMoveStockFinishHistory(conn, h);
		    	
		    	msg = "ทำการโอนยอดสต๊อกเรียบร้อยแล้ว";
		    }
		   // hide save button
		    h.setCanEdit(false);
		    
			//set to form
			aForm.setBean(h);
		
			request.setAttribute("Message",msg);
			
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
	
	/**
	 * Save
	 */
	protected String save_OLD(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
		User user = (User) request.getSession().getAttribute("user");

		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MoveStockWarehouseBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Set Item
			String[] groupCode = request.getParameterValues("groupCode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] barcode = request.getParameterValues("barcode");
			String[] onhandQty = request.getParameterValues("onhandQty");
			String[] transferQty = request.getParameterValues("transferQty");
			
			//logger.debug("lineId:"+lineId.length);
			
			//add value to Results
			if(groupCode != null && groupCode.length > 0){
				for(int i=0;i<groupCode.length;i++){
					
					if( !Utils.isNull(transferQty[i]).equals("") && !Utils.isNull(transferQty[i]).equals("0") ){
						 MoveStockWarehouseBean l = new MoveStockWarehouseBean();
						 
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setBarcode(Utils.isNull(barcode[i]));
						 
						 l.setOnhandQty(Utils.isNull(onhandQty[i]));
						 l.setTransferQty(Utils.isNull(transferQty[i]));
						 
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 //Update new Warehouse
						// BarcodeDAO.updateBarcodeNewWarehouseByPK(conn, h.getWarehouseTo(), l);
						 
					}//if
				}//for
			}//if


			///search refresh
		    //h = MoveStockWarehoseDAO.searchBarcodeUpdateWarehouse(conn,h,oldBoxNoWhereSqlIn,oldJobIdWhereSqlIn);
		   
		   // hide save button
		    h.setCanEdit(false);
		    
			//set to form
			aForm.setBean(h);
			aForm.setResults(h.getItems());
		
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อย");
			
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
		MoveStockWarehouseForm aForm = (MoveStockWarehouseForm) form;
		try {
			MoveStockWarehouseBean w = new MoveStockWarehouseBean();
			//w.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//w.setCloseDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
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
