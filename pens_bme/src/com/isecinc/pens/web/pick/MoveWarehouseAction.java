package com.isecinc.pens.web.pick;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.MoveWarehouse;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.MoveWarehoseDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MoveWarehouseAction extends I_Action {


	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		MoveWarehouseForm aForm = (MoveWarehouseForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			MoveWarehouse w = new MoveWarehouse();
			w.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			w.setCloseDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
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
		MoveWarehouseForm aForm = (MoveWarehouseForm) form;
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
		MoveWarehouseForm aForm = (MoveWarehouseForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			MoveWarehouse p = MoveWarehoseDAO.searchHead(aForm.getBean());
			if(p.getItems() != null && p.getItems().size() >0){
				aForm.setResults(p.getItems());
			}else{
				msg  ="ไม่พบข้อมูล ";
				aForm.setResults(null);
			}
			//set to form
			aForm.setBean(p);
			
			logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
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
		MoveWarehouseForm aForm = (MoveWarehouseForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String, List<MoveWarehouse>> groupJobMap = new HashMap<String, List<MoveWarehouse>>();
		String oldJobIdWhereSqlIn = "";
		String oldBoxNoWhereSqlIn = "";
		String newJobIdWhereSqlIn = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MoveWarehouse h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<MoveWarehouse> itemList = new ArrayList<MoveWarehouse>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] jobId = request.getParameterValues("jobIdItem");
			String[] boxNo = request.getParameterValues("boxNo");
			String[] qty = request.getParameterValues("qty");
			
			logger.debug("lineId:"+lineId.length);
			
			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
					if( !Utils.isNull(lineId[i]).equals("")){
						 MoveWarehouse l = new MoveWarehouse();
						 l.setLineId(i+1);
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setJobId(jobId[i]);
						 l.setQty(qty[i]);
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 l.setRemark(h.getRemark());
						 
						//For Search 
						 oldJobIdWhereSqlIn +="'"+l.getJobId()+"',";
						 oldBoxNoWhereSqlIn +="'"+l.getBoxNo()+"',";
						 
						 String jobIdKey = String.valueOf(l.getJobId());
						 
						 //First
						 if(groupJobMap.isEmpty()){
							 itemList = new ArrayList<MoveWarehouse>();
							 itemList.add(l);
							 groupJobMap.put(jobIdKey, itemList);
						 }else{
							 if(groupJobMap.get(jobIdKey) != null){
								 itemList = groupJobMap.get(jobIdKey);
								 itemList.add(l); 
								 groupJobMap.put(jobIdKey, itemList);
							 }else{
								 itemList = new ArrayList<MoveWarehouse>();
								 itemList.add(l); 
								 groupJobMap.put(jobIdKey, itemList);
							 }
						 }
						 
					}//if
				}//for
			}//if
			
			int no =0;
			int totalQty =0;
			int totalBox = 0;
			List<MoveWarehouse> AllItems = new ArrayList<MoveWarehouse>();
			
			if(groupJobMap != null ){
				Iterator<String> its = groupJobMap.keySet().iterator();
				while(its.hasNext()){
					String jobIdKey = its.next();
					
					//Copy some data to new job
					Job oldJob = new Job();
					oldJob.setJobId(jobIdKey);
					Job newJob = JobDAO.search(conn,oldJob);
					
				    //Set new Data new job
					newJob.setJobId("");
					newJob.setOpenDate(h.getOpenDate());
					newJob.setCloseDate(h.getCloseDate());
				    newJob.setWareHouse(h.getWarehouseTo());//new warehouse 
				    newJob.setCreateUser(user.getUserName());
				    newJob.setUpdateUser(user.getUserName());
                    //save new job
				    newJob = JobDAO.saveCaseMoveWarehouse(conn,newJob, user);
				    //For Search 
				    newJobIdWhereSqlIn +="'"+newJob.getJobId()+"',";
				    
				    itemList = groupJobMap.get(jobIdKey);
				    
				    //Move barcode to new job id
				    MoveWarehouse re = MoveWarehoseDAO.moveBarcodeToNewWarehouseNoNewBoxNo(conn,newJob, itemList,no);
				    
				    no = re.getNo();
				    totalQty +=Utils.convertStrToInt(re.getTotalQty());
				    totalBox +=Utils.convertStrToInt(re.getTotalBox());
				    
				    AllItems.addAll(re.getItems());
				}
				
				h.setTotalBox(totalBox+"");
				h.setTotalQty(totalQty+"");
				h.setItems(AllItems);
			}
			
			if(oldJobIdWhereSqlIn.length() >0){
				oldJobIdWhereSqlIn = oldJobIdWhereSqlIn.substring(0,oldJobIdWhereSqlIn.length()-1);
			}
			
			if(oldBoxNoWhereSqlIn.length() >0){
				oldBoxNoWhereSqlIn = oldBoxNoWhereSqlIn.substring(0,oldBoxNoWhereSqlIn.length()-1);
			}
			
			if(newJobIdWhereSqlIn.length() >0){
				newJobIdWhereSqlIn = newJobIdWhereSqlIn.substring(0,newJobIdWhereSqlIn.length()-1);
			}
			
		    ///search refresh
		  //  h = MoveWarehoseDAO.searchHeadForNewJob(conn,h,oldBoxNoWhereSqlIn,newJobIdWhereSqlIn);
		    
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
		MoveWarehouseForm aForm = (MoveWarehouseForm) form;
		try {
			MoveWarehouse w = new MoveWarehouse();
			w.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			w.setCloseDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
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
