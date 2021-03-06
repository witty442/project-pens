package com.isecinc.pens.web.autocn;

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
import com.isecinc.pens.dao.AutoCNDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AutoCNAction extends I_Action {

	public static int pageSize = 25;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		AutoCNForm aForm = (AutoCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("prepare2 action["+action+"]");
			
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				AutoCNBean ad = new AutoCNBean();
				ad.setJobStatus("CLOSE");
				//get cutt job date
				ad.setCuttOffDate(ControlConstantsDB.getValueByConCode(ControlConstantsDB.JOB_CUTT_DATE_LOTUS_REF_CODE, ControlConstantsDB.JOB_CUTT_DATE_LOTUS_REF_CODE));
				ad.setCustGroup(PickConstants.STORE_TYPE_LOTUS_CODE);
				aForm.setBean(ad);
				
				List<PopupForm> custGroupList = new ArrayList<PopupForm>();
				PopupForm refP = new PopupForm(); 
				custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","Store_AutoCN"));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		AutoCNForm aForm = (AutoCNForm) form;
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
				aForm.setTotalRecord(AutoCNDAO.searchTotalRecJobList(conn,aForm.getBean()));
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
				List<AutoCNBean> items = AutoCNDAO.searchJobList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "��辺������");
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
				List<AutoCNBean> items = AutoCNDAO.searchJobList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
		AutoCNForm aForm = (AutoCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		AutoCNBean bean = new AutoCNBean();
		logger.debug("prepare");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());

            String jobId = Utils.isNull(request.getParameter("jobId"));
            String rtnNo = Utils.isNull(request.getParameter("rtnNo"));
            String custGroup = Utils.isNull(request.getParameter("custGroup"));
            String storeCode = Utils.isNull(request.getParameter("storeCode"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit jobId:"+jobId+"action:"+action+",custGroup:"+custGroup);
	
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
				bean.setRtnNo(rtnNo);
				bean.setCustGroup(custGroup);
				bean.setStoreCode(storeCode);
				// getStoreName
				bean.setStoreName(GeneralDAO.getStoreNameModel(conn,bean.getStoreCode()));
				bean.setMode("edit");
				
				bean = AutoCNDAO.searchItemListCaseView(conn,bean);
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
					bean = AutoCNDAO.searchItemListCaseNew(conn,bean);
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
				bean.setRtnNo(rtnNo);
				bean.setCustGroup(custGroup);
				bean.setStoreCode(storeCode);
				// getStoreName
				bean.setStoreName(GeneralDAO.getStoreNameModel(conn,bean.getStoreCode()));
				bean.setMode("edit");
				bean = AutoCNDAO.searchItemListCaseView(conn,bean);
				aForm.setResultsSearch(bean.getItems());
				
				//Can Save ,Cancel status = APPROVED
				if(bean.getStatus().equalsIgnoreCase("ERROR") || bean.getStatus().equalsIgnoreCase("APPROVED")){
				   bean.setCanCancel(true);
				   bean.setCanSave(true);
				}
				aForm.setBean(bean);
				
			}else if("viewCN".equalsIgnoreCase(action)){
				//init connection User Apps
				conn = DBConnection.getInstance().getConnectionApps();
				
				//view CN
				//Find By JobId
				bean.setJobId(jobId);
				//get JobName
				bean.setJobName(GeneralDAO.getJobNameModel(conn,bean.getJobId(),""));
				bean.setRtnNo(rtnNo);
				bean.setCustGroup(custGroup);
				bean.setStoreCode(storeCode);
				// getStoreName
				bean.setStoreName(GeneralDAO.getStoreNameModel(conn,bean.getStoreCode()));
				bean.setMode("edit");
				
				bean = AutoCNDAO.searchItemListCaseViewCN(conn,bean);
				aForm.setResultsSearch(bean.getItems());
				aForm.setBean(bean);
				
				forward = "detailView";
			}
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

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		AutoCNForm aForm = (AutoCNForm) form;
		try {
			aForm.setResultsSearch(null);
			AutoCNBean bean = new AutoCNBean();
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
		AutoCNForm summaryForm = (AutoCNForm) form;
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
		AutoCNForm orderForm = (AutoCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
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
		Connection conn = null;
		AutoCNForm aForm = (AutoCNForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i= 0;
		AutoCNBean item = null;
		List<AutoCNBean> items = new ArrayList<AutoCNBean>();
		try {
			request.getSession().setAttribute("groupStoreMapError",null);
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			AutoCNBean head = aForm.getBean();
			head.setStatus("APPROVED");
			head.setUserName(user.getUserName());
			
			logger.debug("totalQty:"+head.getTotalQty());
			
			//get Parametervalues
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
					
				   item = new AutoCNBean();
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
			AutoCNDAO.save(conn, head);
			
			request.setAttribute("Message", "�ѹ�֡���������º����");
			
		    conn.commit();
		    
		    //search new Case View
		    AutoCNBean bean = AutoCNDAO.searchItemListCaseView(conn,aForm.getBean());
			aForm.setResultsSearch(bean.getItems());

		} catch (Exception e) {
			logger.debug("Conn Rollback");
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","�������ö�ѹ�֡�������� \n"+ e.getMessage());
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
		AutoCNForm aForm = (AutoCNForm) form;
		Connection conn = null;
		try {
			//init connection
			conn = DBConnection.getInstance().getConnection();
			
			AutoCNBean bean = aForm.getBean();
			bean.setStatus("CANCEL");
			bean.setUserName(user.getUserName());
			
			//Cancel Job
			AutoCNDAO.cancel(conn, bean);
			
			request.setAttribute("Message", "¡��ԡ��¡�����º����");
			
		    conn.commit();
		    
		    //search new Case View
		    bean = AutoCNDAO.searchItemListCaseView(conn,aForm.getBean());
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
		AutoCNForm aForm = (AutoCNForm) form;
		String fileName ="data.xls";
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			List<AutoCNBean> items = AutoCNDAO.searchJobList(conn,aForm.getBean(),true,0,pageSize);
			if(items != null && items.size() >0){
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>Job Id</td> \n");
				  h.append("<td>Job Name</td> \n");
				  h.append("<td>Store Code</td> \n");
				  h.append("<td>Store Name</td> \n");
				  h.append("<td>RTN NO</td> \n");
				  h.append("<td>Total Box</td> \n");
				  h.append("<td>Total Qty</td> \n");
				  h.append("<td>AutoCN Status</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					AutoCNBean s = (AutoCNBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getJobId()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getJobName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreCode())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getRtnNo())+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalBox()+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalQty()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStatus())+"</td> \n");
					h.append("</tr> \n");
				}//for
				h.append("</table> \n");
			    
				//logger.debug("table str: \n"+h.toString());
				
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
				request.setAttribute("Message","��辺������");
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
