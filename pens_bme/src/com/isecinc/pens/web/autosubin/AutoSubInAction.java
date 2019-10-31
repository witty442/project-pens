package com.isecinc.pens.web.autosubin;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AutoSubInDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AutoSubInAction extends I_Action {

	public static int pageSize = 25;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		AutoSubInForm aForm = (AutoSubInForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("prepare2 action["+action+"]");
			
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				AutoSubInBean ad = new AutoSubInBean();
				ad.setJobStatus("CLOSE");
				//get cutt job date
				ad.setCuttOffDate(ControlConstantsDB.getValueByConCode(ControlConstantsDB.JOB_CUTT_DATE_BIGC_REF_CODE, ControlConstantsDB.JOB_CUTT_DATE_BIGC_REF_CODE));
				ad.setCustGroup(PickConstants.STORE_TYPE_BIGC_CODE);
				aForm.setBean(ad);
				
				List<Master> custGroupList = new ArrayList<Master>();
				//Master refP = new Master(); 
				//custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.getCustGroupAutoSubList());
				request.getSession().setAttribute("custGroupList",custGroupList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		AutoSubInForm aForm = (AutoSubInForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(AutoSubInDAO.searchTotalRecJobList(conn,aForm.getBean()));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
				List<AutoSubInBean> items = AutoSubInDAO.searchJobList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResultsSearch(null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
				List<AutoSubInBean> items = AutoSubInDAO.searchJobList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		AutoSubInForm aForm = (AutoSubInForm) form;
		User user = (User) request.getSession().getAttribute("user");
		AutoSubInBean bean = new AutoSubInBean();
		logger.debug("prepare");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());

            String jobId = Utils.isNull(request.getParameter("jobId"));
            String subInv = Utils.isNull(request.getParameter("subInv"));
            String custGroup = Utils.isNull(request.getParameter("custGroup"));
            String storeCode = Utils.isNull(request.getParameter("storeCode"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit jobId:"+jobId+"action:"+action+",custGroup:"+custGroup+",subInv:"+subInv);
	
			//Clear groupStoreMapError Session
			request.getSession().setAttribute("groupStoreMapError", null);
			
			//Verify
			if("edit".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
				
				//Find By JobId
				bean.setJobId(jobId);
				//get JobName
				bean.setJobName(GeneralDAO.getJobNameModel(conn,bean.getJobId(),""));
				bean.setCustGroup(custGroup);
				bean.setStoreCode(storeCode);
				bean.setSubInv(subInv);
				// getStoreName
				bean.setStoreName(GeneralDAO.getStoreNameModel(conn,bean.getStoreCode()));
				bean.setMode("edit");
				
				bean = AutoSubInDAO.searchItemListCaseView(conn,bean);
				if(bean != null && bean.getItems() != null && bean.getItems().size() >0){
					//Get Date Exist
					aForm.setResultsSearch(bean.getItems());
					
					//Can Save ,Cancel status = APPROVED
					if(bean.getStatus().equalsIgnoreCase("ERROR") || bean.getStatus().equalsIgnoreCase("APPROVED")){
					   bean.setCanCancel(true);
					   bean.setCanSave(true);
					}
				}else{
					// Get New Data
					bean = AutoSubInDAO.searchItemListCaseNew(conn,bean);
					aForm.setResultsSearch(bean.getItems());
					bean.setCanSave(true);
				}
				aForm.setBean(bean);
			}else if("view".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
				
				//view
				//Find By JobId
				bean.setJobId(jobId);
				//get JobName
				bean.setJobName(GeneralDAO.getJobNameModel(conn,bean.getJobId(),""));
				bean.setCustGroup(custGroup);
				bean.setStoreCode(storeCode);
				bean.setSubInv(subInv);
				// getStoreName
				bean.setStoreName(GeneralDAO.getStoreNameModel(conn,bean.getStoreCode()));
				bean.setMode("edit");
				bean = AutoSubInDAO.searchItemListCaseView(conn,bean);
				aForm.setResultsSearch(bean.getItems());
				
				//Can Save ,Cancel status = APPROVED
				if(bean.getStatus().equalsIgnoreCase("ERROR") || bean.getStatus().equalsIgnoreCase("APPROVED")){
				   bean.setCanCancel(true);
				   bean.setCanSave(true);
				}
				aForm.setBean(bean);
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

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		AutoSubInForm aForm = (AutoSubInForm) form;
		try {
			aForm.setResultsSearch(null);
			AutoSubInBean bean = new AutoSubInBean();
			aForm.setBean(bean);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AutoSubInForm summaryForm = (AutoSubInForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AutoSubInForm orderForm = (AutoSubInForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
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
		AutoSubInForm aForm = (AutoSubInForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i= 0;
		AutoSubInBean item = null;
		List<AutoSubInBean> items = new ArrayList<AutoSubInBean>();
		try {
			request.getSession().setAttribute("groupStoreMapError",null);
			
			//init Connection
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			AutoSubInBean head = aForm.getBean();
			head.setStatus("APPROVED");
			head.setUserName(user.getUserName());
			
			logger.debug("totalQty:"+head.getTotalQty());
			
			//get Parameter Values
			String[] keyDatas = request.getParameterValues("keyData");
			String[] pensItems = request.getParameterValues("pensItem");
			String[] inventoryItemIds = request.getParameterValues("inventoryItemId");
			String[] unitPrices = request.getParameterValues("unitPrice");
			String[] qtys = request.getParameterValues("qty");
			String[] amounts = request.getParameterValues("amount");
			
			logger.debug("keyDelete length:"+keyDatas.length);
			
			for(i=0;i<keyDatas.length;i++){
				logger.debug("keyData:"+keyDatas[i]);
				if( !Utils.isNull(pensItems[i]).equals("") 
					&& !Utils.isNull(keyDatas[i]).equals("CANCEL") ){
					
				   item = new AutoSubInBean();
				   item.setPensItem(pensItems[i]);
				   item.setInventoryItemId(inventoryItemIds[i]);
				   item.setUnitPrice(unitPrices[i]);
				   item.setQty(qtys[i]);
				   item.setAmount(amounts[i]);
				   item.setUserName(head.getUserName());
				   item.setStatus(head.getStatus());
				   items.add(item);
				}
			}
			head.setItems(items);
			
			//save db
			AutoSubInDAO.save(conn, head);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
			
		    conn.commit();
		    
		    //search new Case View
		    AutoSubInBean bean = AutoSubInDAO.searchItemListCaseView(conn,aForm.getBean());
			aForm.setResultsSearch(bean.getItems());

		} catch (Exception e) {
			logger.debug("Conn Rollback");
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
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
	
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		User user = (User) request.getSession().getAttribute("user");
		AutoSubInForm aForm = (AutoSubInForm) form;
		Connection conn = null;
		try {
			//init connection
			conn = DBConnection.getInstance().getConnection();
			
			AutoSubInBean bean = aForm.getBean();
			bean.setStatus("CANCEL");
			bean.setUserName(user.getUserName());
			
			//Cancel Job
			AutoSubInDAO.cancel(conn, bean);
			
			request.setAttribute("Message", "ยกเลิกรายการเรียบร้อย");
			
		    conn.commit();
		    
		    //search new Case View
		    bean = AutoSubInDAO.searchItemListCaseView(conn,aForm.getBean());
			aForm.setResultsSearch(bean.getItems());
			bean.setCanSave(false);
			bean.setCanCancel(false);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportToExcel");
		AutoSubInForm aForm = (AutoSubInForm) form;
		String fileName ="data.xls";
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			List<AutoSubInBean> items = AutoSubInDAO.searchJobList(conn,aForm.getBean(),true,0,pageSize);
			if(items != null && items.size() >0){
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'> \n");
				h.append("<tr  class='column_head'> \n");
				  h.append("<td>Job Id</td> \n");
				  h.append("<td>Job Name</td> \n");
				  h.append("<td>Store Code</td> \n");
				  h.append("<td>Store Name</td> \n");
				  h.append("<td>Sub Inventory</td> \n");
				  h.append("<td>Total Box</td> \n");
				  h.append("<td>Total Qty</td> \n");
				  h.append("<td>AutoCN Status</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					AutoSubInBean s = (AutoSubInBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getJobId()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getJobName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreCode())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getSubInv())+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalBox()+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalQty()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStatus())+"</td> \n");
					h.append("</tr>");
				}//for
				h.append("</table> \n");
			
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(h.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
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
