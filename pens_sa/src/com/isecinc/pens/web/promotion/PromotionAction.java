package com.isecinc.pens.web.promotion;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.DBConnection;
import util.ExcelHeader;
import util.ReportUtilServlet;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PromotionAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		PromotionForm aForm = (PromotionForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
		
			if("new".equals(action)){
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				PromotionBean bean = new PromotionBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				bean.setPeriodType("month");
				bean.setCustCatNo("S");
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user);
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				PromotionBean bean = new PromotionBean();
				bean.setPeriodType("month");
				bean.setCustCatNo("S");
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
	
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user){
		String salesrepCode = "";
		try{
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				salesrepCode = user.getUserName().toUpperCase();
			}
			
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", PromotionUtils.initPeriod(conn));
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = PromotionUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("S");
			item.setCustCatDesc("CREDIT SALES");
			custCatNoList.add(item);
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//SALESREP_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = PromotionUtils.searchSalesrepListAll(conn,"","S","",salesrepCode);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);
			
			List<PopupBean> salesZoneList_s = PromotionUtils.searchSalesZoneListModel(conn,salesrepCode);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		PromotionForm aForm = (PromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(PromotionDAO.searchPromotionListTotalRec(conn,aForm.getBean(),user));
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
			   
				List<PromotionBean> items = PromotionDAO.searchPromotionList(conn,aForm.getBean(),allRec,currPage,pageSize,user);
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
			    List<PromotionBean> items = PromotionDAO.searchPromotionList(conn,aForm.getBean(),allRec,currPage,pageSize,user);
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
		PromotionForm aForm = (PromotionForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		PromotionForm aForm = (PromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
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
	
	public ActionForward popupPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printRequestPromotion");
		Connection conn = null;
		request.getSession().setAttribute("ERROR_MSG",null);
		try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
			List<RequestPromotionLine> lstData = null;
	
			String fileName = "request_promotion_report";
            String fileJasper =  BeanParameter.getReportPath() + fileName;
			
            //Set Criteria
            PromotionBean pp = new PromotionBean();
            pp.setRequestNo(Utils.isNull(request.getParameter("requestNo")));
            
			conn = new DBConnection().getConnectionApps();
			List<RequestPromotion> mResultList = PromotionDAO.searchReqPromotionList(pp, true);

			if(mResultList != null && mResultList.size()>0){
				RequestPromotion p = mResultList.get(0);
				
				String logopath =   context.getRealPath("/images2/pens_logo_fit.jpg");//
				//logger.debug("logoPath:"+logopath);
	            
				parameterMap.put("pens_logo_fit",logopath);
				parameterMap.put("requestDate",p.getRequestDate());
				parameterMap.put("requestNo",p.getRequestNo());
				parameterMap.put("printDate",Utils.isNull(p.getPrintDate()));
				parameterMap.put("userPrint",p.getSalesCode());
				
				parameterMap.put("productCatagory",p.getProductCatagory()+"-"+p.getProductCatagoryDesc());
				parameterMap.put("productType",p.getProductType());
				parameterMap.put("salesCode",p.getSalesCode());
				parameterMap.put("salesName",p.getSalesName());
				parameterMap.put("territory",p.getTerritory());
				parameterMap.put("customerCode",p.getCustomerCode());
				parameterMap.put("customerName",p.getCustomerName());
				parameterMap.put("phone",p.getPhone());
				
				parameterMap.put("promotionStartDate",p.getPromotionStartDate());
				parameterMap.put("promotionEndDate",p.getPromotionEndDate());
				parameterMap.put("name",p.getName());
				parameterMap.put("remark",p.getRemark());
				
				//set cost Table
				if(p.getCostLineList() != null && p.getCostLineList().size()>0){
					 Map<String, RequestPromotionCost> costTableMap = new HashMap<String, RequestPromotionCost>();
					 for(int i=0;i<p.getCostLineList().size();i++){
						 RequestPromotionCost c = p.getCostLineList().get(i);
						 //logger.debug("lineNo["+c.getLineNo()+"]");
						 
						 costTableMap.put(c.getLineNo()+"", c);
					 }
	                // logger.debug("CostDetail1:"+costTableMap.get("1").getCostDetail());
					 
					parameterMap.put("costDetail1",Utils.isNull(costTableMap.get("1")!=null?costTableMap.get("1").getCostDetail():""));
					parameterMap.put("costAmount1",costTableMap.get("1")!=null?costTableMap.get("1").getCostAmount():null);
					parameterMap.put("costDetail2",Utils.isNull(costTableMap.get("2")!=null?costTableMap.get("2").getCostDetail():""));
					parameterMap.put("costAmount2",costTableMap.get("2")!=null?costTableMap.get("2").getCostAmount():null);
					parameterMap.put("costDetail3",Utils.isNull(costTableMap.get("3")!=null?costTableMap.get("3").getCostDetail():""));
					parameterMap.put("costAmount3",costTableMap.get("3")!=null?costTableMap.get("3").getCostAmount():null);
					parameterMap.put("costDetail4",Utils.isNull(costTableMap.get("4")!=null?costTableMap.get("4").getCostDetail():""));
					parameterMap.put("costAmount4",costTableMap.get("4")!=null?costTableMap.get("4").getCostAmount():null);
					parameterMap.put("costDetail5",Utils.isNull(costTableMap.get("5")!=null?costTableMap.get("5").getCostDetail():""));
					parameterMap.put("costAmount5",costTableMap.get("5")!=null?costTableMap.get("5").getCostAmount():null);
						
				}
				
				String fileNameExport = p.getRequestNo()+"_"+Utils.isNull(p.getPrintDate())+".pdf";
				//Set Lines
				lstData = p.getPromotionLineList();
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData,fileNameExport);
			
			}else{
				request.setAttribute("Message","Data not found");
			}
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.getSession().setAttribute("ERROR_MSG", e.getMessage());
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		PromotionForm aForm = (PromotionForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<PromotionBean> items = PromotionDAO.searchPromotionList(conn,aForm.getBean(),true,0,pageSize,user);
		    if(items!= null && items.size() >0){
		    	resultTable = genExcelHTMLTable(items);
				
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
			  request.setAttribute("Message","ไม่พบข้อมูล");
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
		return null;
	}
	
	private StringBuffer genExcelHTMLTable(List<PromotionBean> items) throws Exception{
	 StringBuffer h = new StringBuffer("");
	 int no = 0;
	 h.append(ExcelHeader.EXCEL_HEADER);
	 
	 h.append("<table border='1'> \n");
	 h.append("<tr><td colspan='9'><b>รายงาน  ใบอนุมัติจัดรายการร้านค้า </b></td></tr> \n");
	 h.append("</table> \n");
	 h.append("<table border='1'> \n");
	 h.append("<tr> \n");
	 h.append("<th>No.</th>\n");
	 h.append("<th>ภาคการขาย</th>\n");
	 h.append("<th>พนักงานขาย</th>\n");
	 h.append("<th>วันที่ทำรายการ</th>\n");
	 h.append("<th>เลขที่เอกสาร</th>\n");
	 h.append("<th>รหัสร้านค้า</th>\n");
	 h.append("<th>ชื่อร้านค้า</th>\n");
	 h.append("<th>แบรนด์</th>\n");
	 h.append("<th>ประเภท</th>\n");
	 h.append("</tr> \n");
	 for(int i=0;i<items.size();i++){
		 PromotionBean p = items.get(i);
		 no++;
		 h.append("<tr> \n");
		 h.append("<td class='text'> "+no+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesChannelName()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepCode()+"-"+p.getSalesrepName()+"</td>\n");
		 h.append("<td class='text'> "+p.getRequestDate()+"</td>\n");
		 h.append("<td class='text'> "+p.getRequestNo()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerName()+"</td>\n");
		 h.append("<td class='text'> "+p.getBrand()+"-"+p.getBrandName()+"</td>\n");
		 h.append("<td class='text'> "+p.getProductTypeDesc()+"</td>\n");
		 h.append("</tr> \n");
	 }
	 h.append("</table> \n");
	 return h;
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
		PromotionForm orderForm = (PromotionForm) form;
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
