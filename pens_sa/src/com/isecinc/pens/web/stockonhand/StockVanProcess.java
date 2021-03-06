package com.isecinc.pens.web.stockonhand;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
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
public class StockVanProcess extends I_Action {

	public static int pageSize = 99999;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if("new".equals(action)){
				request.setAttribute("stockOnhandForm_RESULT",null);
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setDispPlan("");
				bean.setDispHaveQty("true");
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user,pageName);
				
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setDispPlan("");
				bean.setDispHaveQty("true");
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = StockVanUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);

			List<PopupBean> salesZoneList_s = StockVanUtils.searchSalesZoneListModel(conn);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}

			   //get max column by product or PD
				List<StockOnhandBean> columnList = StockVanDAO.searchColumnList(conn, aForm.getBean());
				request.getSession().setAttribute("COLUMN_LIST", columnList);
				
				//get Items Show by Page Size
				List<StockOnhandBean> items = StockVanDAO.searchStockVanList(conn,aForm.getBean(),columnList);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0 || columnList.size()<=0){
				   request.setAttribute("Message", "��辺������");
				   request.getSession().setAttribute("stockOnhandForm_RESULT",null);
				   aForm.setResultsSearch(null);
				}else{
					if( !Utils.isNull(aForm.getBean().getDispType()).equals("3")){
					   request.setAttribute("stockOnhandForm_RESULT", StockVanExport.genResultStockVanPD(aForm, request,"HTML"));
					}else{
					   request.setAttribute("stockOnhandForm_RESULT", StockVanExport.genResultStockVanPDBySalesZone(aForm, request,"HTML"));
					}
				}
			}else{
			   //get max column by product or PD
				List<StockOnhandBean> columnList = (List<StockOnhandBean>)request.getSession().getAttribute("COLUMN_LIST");
				
				//get Items Show by Page Size
			    List<StockOnhandBean> items = StockVanDAO.searchStockVanList(conn,aForm.getBean(),columnList);
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
		User user = (User) request.getSession().getAttribute("user");
		StockOnhandForm aForm = (StockOnhandForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		StockOnhandForm aForm = (StockOnhandForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","�������ö�ѹ�֡�������� \n"+ e.getMessage());
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
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockOnhandForm stockVanForm = (StockOnhandForm) form;
		StringBuffer resultTable = null;
		String pageName = stockVanForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<StockOnhandBean> items = stockVanForm.getResultsSearch();
		    if(items!= null && items.size() >0){
		    	if( !Utils.isNull(stockVanForm.getBean().getDispType()).equals("3")){
		    	   resultTable = StockVanExport.genResultStockVanPD(stockVanForm, request,"EXCEL");
		    	}else{
		    	   resultTable = StockVanExport.genResultStockVanPDBySalesZone(stockVanForm, request,"EXCEL");
		    	}
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","��辺������");
			  return mapping.findForward("search");
		   }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;//
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
		StockOnhandForm orderForm = (StockOnhandForm) form;
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
