package com.isecinc.pens.web.summary.groupCode;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.SummaryByGroupCodeDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.gendate.OrderDateUtils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.managepath.ManagePath;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SummaryByGroupCodeAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		SummaryForm summaryForm = (SummaryForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 logger.debug("prepare");
			 
			 //Validate Page Order can access 1 user;
			 String userLastAccessPath = ManagePath.canAccessPath(user, request.getServletPath());
			 logger.debug("userLastAccessPath:"+userLastAccessPath);
			 if( !userLastAccessPath.equals("")){
				// request.setAttribute("Message", "หน้า Order B'me to Wacoal มี  User ["+userLastAccessPath+"] กำลังใช้งานอยู่  ท่านแน่ใจที่จะทำรายการต่อไป หรือตรวจสอบกับ Userที่ใช้งานอยู่ ว่าออกจากหน้านี้หรือยัง");
			 }
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("storeList",null);
				 request.getSession().setAttribute("itemErrorMap", null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 summaryForm.setOrder(order);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"");
				 request.getSession().setAttribute("storeTypeList",storeTypeList);
				
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);

			 }
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
		SummaryForm summaryForm = (SummaryForm) form;
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
		SummaryForm orderForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SummaryByGroupCodeDAO orderDAO = new SummaryByGroupCodeDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		List<StoreBean> storeList = null;
		String action = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("search action:"+action);

			
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				
				//init store type
				//new SummaryByGroupCodeDAO().initStoreTypeMap();
				
				pageNumber = 1;
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("storeList",null);
				
				totalRow = orderDAO.getTotalRowItem(conn,orderForm.getOrder());
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);
				
				storeList = orderDAO.getStoreListSaleOut(conn,orderForm.getOrder());
				request.getSession().setAttribute("storeList",storeList);
				
				if(storeList==null || (storeList != null && storeList.size()==0)){
					request.setAttribute("Message", "ไม่พบข่อมูล สาขา");
				}
				
			}else{
				totalRow = (Integer)request.getSession().getAttribute("totalRow");
				totalPage = (Integer)request.getSession().getAttribute("totalPage");
				
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			logger.debug("totalRow["+totalRow+"]totalPage["+totalPage+"]pageNumber["+pageNumber+"]");
			
            //** Search Data and Display **/
			List<Order> results = new SummaryByGroupCodeDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize);
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
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
		return "search";
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SummaryForm orderForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int row = 0;
		int col = 0;
		boolean haveError = false;
		try {
			
			 
		} catch (Exception e) {
			//conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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


	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export ToExcel");
		SummaryForm orderForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer data = new StringBuffer("");
		Connection conn = null;
		try {
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.xls");
			response.setContentType("application/vnd.ms-excel");
			
			data = genExcel(orderForm.getOrder(),request);
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(data.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("exportToExcel");
	}
	
	public StringBuffer genExcel(Order o, HttpServletRequest request) {
		StringBuffer h = new StringBuffer("");
		String a= "@";
		int colSpan = 0;
		String title ="Query Sale Out by Group code";
		List<StoreBean> storeList = null;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try{
			if(request.getSession().getAttribute("storeList") != null){
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
			}
			colSpan = storeList.size()+4;
			 
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>"+title+" </td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Sale Date:"+o.getSalesDateFrom()+"-"+o.getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >กล่มร้านค้า:"+o.getStoreType()+"-"+GeneralDAO.searchCustGroupDesc(o.getStoreType())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Group Code :"+o.getGroupCode()+" </td>\n");
			h.append("</tr> \n");

		    h.append("</table> \n");

			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td>No.</td> \n");
			h.append("<td>Group Code</td> \n");
			h.append("<td>Size</td> \n");
			h.append("<td>Color</td> \n");
			for(int k=0;k<storeList.size();k++){
                StoreBean s = (StoreBean)storeList.get(k);
			    h.append("<td>"+s.getStoreDisp()+"</td> \n");
			}
			h.append("</tr>");
			
			conn = DBConnection.getInstance().getConnection();
			List<Order> results = new SummaryByGroupCodeDAO().prepareNewOrder(conn,o,storeList, user,1,999999);
			if(results != null){

				int no= 0;
				for(int i=0;i<results.size();i++){
					no++;
				    Order item = (Order) results.get(i);
				    h.append("<tr>");
				    h.append("<td>"+no+"</td> \n");
					h.append("<td>"+item.getGroupCode()+"</td> \n");
					h.append("<td>"+item.getSize()+"</td> \n");
					h.append("<td>"+item.getColor()+"</td> \n");
					if(item.getStoreItemList() != null && item.getStoreItemList().size()>0){ 
			        	for(int c=0;c<item.getStoreItemList().size();c++){
			              StoreBean storeItem = (StoreBean)item.getStoreItemList().get(c);
			              h.append("<td>"+storeItem.getQty()+"</td> \n");
			        	}
					}
					h.append("</tr>");
				}
			}
			h.append("</table> \n");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception e){
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
				logger.error(e.getMessage(),e);
			}
		}
		return h;
	}
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 request.getSession().setAttribute("storeList",null);
			 request.getSession().setAttribute("itemErrorMap", null);
			 
			 Order order = new Order();
			 order.setOrderDate(DateUtil.stringValue(OrderDateUtils.getOrderDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 summaryForm.setOrder(order);
			
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
