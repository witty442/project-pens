package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.dao.SalesrepZoneDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.projectc.ProjectCBean;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.CustomerNissin;
import com.isecinc.pens.web.reportall.bean.OrderNissin;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * OrderNissinReport Action
 * 
 * @author WITTY
 * 
 */
public class OrderNissinReportAction extends I_Action {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reportAll";
		ReportAllForm reportAllForm = (ReportAllForm) form;
		ReportAllBean bean = new ReportAllBean();
		Connection conn = null;
		List<PopupBean> dataList = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			 conn = DBConnection.getInstance().getConnectionApps();
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				bean.setOrderNissin(new OrderNissin());
				reportAllForm.setBean(bean);
				
				//SALESZONE_LIST
				//add Blank Row
				dataList = new ArrayList<PopupBean>();
				PopupBean item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				dataList.add(item);
				
				List<PopupBean> salesZoneList = SalesrepZoneDAO.searchSalesrepZoneListModel(conn,user,"ROLE_CR_STOCK");
				dataList.addAll(salesZoneList);
				request.getSession().setAttribute("SALES_ZONE_LIST",dataList);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			conn.close();
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "reportAll";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			reportAllForm.getBean().getOrderNissin().setDataStrBuffer(null);
			reportAllForm.getBean().getOrderNissin().setItemsList(null);
			reportAllForm.getBean().getOrderNissin().setUser(user);
			
			OrderNissin bean = searchReportModel(reportAllForm.getBean().getOrderNissin());
			if(bean != null && bean.getDataStrBuffer() != null){
			    reportAllForm.getBean().setOrderNissin(bean);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return "reportAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			reportAllForm.getBean().getProjectCBean().setDataStrBuffer(null);
			reportAllForm.getBean().getProjectCBean().setItemsList(null);
			
			OrderNissin bean = searchReportModel(reportAllForm.getBean().getOrderNissin());
			if(bean != null && bean.getDataStrBuffer() != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(bean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
		
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
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
	
	public static StringBuffer genReportHtmlToBuffer(OrderNissin orderBean,boolean excel) throws Exception{
		StringBuffer h = null;
		StringBuffer sql = new StringBuffer("");
		String className = "";
		String classNameNumber = "";
		//User user = (User) request.getSession().getAttribute("user");
		try{
			//set style excel or html
			if(excel){
				className="text";
				classNameNumber="num_currency";
			}else{
				className="td_text_center";
				classNameNumber="td_number";
			}
			
			if(orderBean.getItemsList()==null ||(orderBean.getItemsList() != null && orderBean.getItemsList().size()==0)){
			    orderBean = searchReportModel(orderBean);
			}
			
		    for(int i=0;i<orderBean.getItemsList().size();i++){
		    	OrderNissin item = orderBean.getItemsList().get(i);
		 
		    	if(i==0){
		    		h = new StringBuffer("");
		    		if(excel){
		    		   h.append(ExcelHeader.EXCEL_HEADER);
					   h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}else{
		    		   h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}
					h.append("<tr>\n");
					h.append("<th >Order Id</th> \n");
					h.append("<th >วันที่ Nissin คีย์ สั่งซื้อ</th> \n");
					h.append("<th >Ref Cust ID</th> \n");
					h.append("<th >ชื่อร้านค้า</th> \n");
					h.append("<th >จังหวัด </th> \n");
					h.append("<th >อำเภอ</th> \n");
					h.append("<th >สถานะ </th> \n");
					h.append("<th >รหัสเซลล์ PENS</th> \n");
					h.append("<th >ผู้บันทึก Order</th> \n");
					h.append("<th >Invoice No</th> \n");
					h.append("<th >Invoice Date</th> \n");
					h.append("<th >Oracle Cust No</th> \n");
					h.append("<th >ผู้บันทึก Order</th> \n");
					h.append("</tr> \n");
		    	}
				h.append("<tr class='"+(i%2==0?"lineE":"lineO")+"'> \n");
			    h.append("<td class='"+className+"' >"+item.getId()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getOrderDate()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getId()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getProvinceName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getDistrictName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getDocStatus()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getSalesrepCode()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getNisCreateUser()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getInvoiceNo()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getInvoiceDate()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getOraCustomerCode()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getNisCreateUser()+"</td> \n");
			  
				h.append("</tr> \n");
		    }//for
		   h.append("</table> \n");
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	public static OrderNissin searchReportModel(OrderNissin o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		OrderNissin h = null;
		List<OrderNissin> items = new ArrayList<OrderNissin>();
		int r = 1;
		CustomerNissin custNis = null;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append("\n select A.* from (");
			sql.append("\n    SELECT t.order_id,t.order_date ,t.doc_status");
			sql.append("\n    ,t.salesrep_code,t.invoice_no,t.invoice_date ,t.ora_customer_code ");
			sql.append("\n    ,c.customer_id ,c.name as customer_name,t.nis_create_user");
			sql.append("\n    FROM pensso.t_order_nis t ,pensso.m_customer_nis c ");
			sql.append("\n    WHERE t.customer_id = c.customer_id");
			
			//Where Condition
			sql.append(genSearchHeadWhereSQL(o));
			
			sql.append("\n   order by t.order_id desc ");
            sql.append("\n  )A ");
      
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new OrderNissin();
			   h.setId(Utils.isNull(rst.getString("order_id")));
			   h.setDocStatus(Utils.isNull(rst.getString("doc_status")));
			   h.setCustomerId(rst.getLong("customer_id"));
			   h.setOrderDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setSalesrepCode(Utils.isNull(rst.getString("SALESREP_CODE")));
			   h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
			   h.setInvoiceDate(DateUtil.stringValueChkNull(rst.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setOraCustomerCode(Utils.isNull(rst.getString("ora_customer_code")));
			   h.setNisCreateUser(Utils.isNull(rst.getString("nis_create_user")));
			   
			   //Get Customer Nissin Detail
			   custNis = findCustomerNis(conn,h.getCustomerId());
			   h.setCustomerNis(custNis);
			   
			   items.add(h);
			   r++;
			}//while
			
			//set Result 
			o.setItemsList(items);
			o.setDataStrBuffer(genReportHtmlToBuffer(o, false));
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	return o;
}
	
	public static StringBuffer genSearchHeadWhereSQL(OrderNissin o) throws Exception{
		StringBuffer sql = new StringBuffer();
		
		if( o.getCustomerId() !=0){
			sql.append("\n and c.customer_id ="+o.getCustomerId()+"");
		}
		if( !Utils.isNull(o.getId()).equals("0") && !Utils.isNull(o.getId()).equals("")){
			sql.append("\n and t.order_id ="+o.getId()+"");
		}
		if( !Utils.isNull(o.getDocStatus()).equals("")){
			sql.append("\n and t.doc_status = '"+Utils.isNull(o.getDocStatus())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getName()).equals("")){
			sql.append("\n and c.name like '%"+Utils.isNull(o.getCustomerNis().getName())+"%'");
		}
		if( !Utils.isNull(o.getOrderDateFrom()).equals("") &&  !Utils.isNull(o.getOrderDateTo()).equals("")){
			sql.append("\n and t.order_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			sql.append("\n and t.order_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
		}else{
			if( !Utils.isNull(o.getOrderDateFrom()).equals("")){
			  sql.append("\n and t.order_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			if( !Utils.isNull(o.getOrderDateTo()).equals("")){
			  sql.append("\n and t.order_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
		}
		
		if( !Utils.isNull(o.getCustomerNis().getProvinceId()).equals("")){
			sql.append("\n and c.province_id = '"+Utils.isNull(o.getCustomerNis().getProvinceId())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getDistrictId()).equals("")){
			sql.append("\n and c.district_id = '"+Utils.isNull(o.getCustomerNis().getDistrictId())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getCustomerType()).equals("")){
			sql.append("\n and c.customer_type = '"+Utils.isNull(o.getCustomerNis().getCustomerType())+"'");
		}
		
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and t.salesrep_code ='"+Utils.isNull(o.getSalesrepCode())+"'");
		}
		
		if(o.getUser().getRoleVanDoc().indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(o.getUser().getUserName())){
	        boolean isSetCustMapSalesTT = GeneralDAO.isUserMapCustSalesTT(o.getUser());
			if (isSetCustMapSalesTT){
				 sql.append("\n  and t.salesrep_code in( ");
				 sql.append("\n   select Z.salesrep_code from ");
				 sql.append("\n    PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT C ");
				 sql.append("\n   ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
				 sql.append("\n   where C.zone = Z.zone");
				 sql.append("\n   and  C.user_name='"+o.getUser().getUserName()+"'");
				 sql.append("\n  )");
			}
        }//if
		return sql;
	}
	
	public static CustomerNissin findCustomerNis(Connection conn ,long customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		CustomerNissin p = null;
		try{
			String sql  ="\n select c.* ";
			       sql +="\n ,(select name from pensso.m_district m where m.district_id = c.district_id) as district_name ";
			       sql +="\n ,(select name from pensso.m_province m where m.province_id = c.province_id) as province_name ";
			       sql +="\n ,(select count(*)  from pensso.t_order_nis n"
			       		+ "    where n.order_id = ("
			       		+ "     select max(order_id) from pensso.t_order_nis ns where "
			       		+ "     ns.customer_id = c.customer_id and ns.salesrep_code is not null)"
			       		+ "   ) as order_c ";
			       sql +="\n from pensso.m_customer_nis c where 1=1" ;
			if(customerId != 0){
			   sql +="\n and c.customer_id ="+customerId;
			}
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new CustomerNissin();
				p.setId(rst.getLong("CUSTOMER_ID"));
				p.setName(Utils.isNull(rst.getString("name")));
				p.setCustomerType(Utils.isNull(rst.getString("customer_type")));
				p.setInterfaces(rst.getString("INTERFACES").trim());
				p.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
				p.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
				p.setAddressLine3(Utils.isNull(rst.getString("address_line3")));
				p.setDistrictId(Utils.isNull(rst.getString("district_id")));
				p.setProvinceId(Utils.isNull(rst.getString("province_id")));
				p.setMobile(Utils.isNull(rst.getString("mobile")));
				p.setExported(rst.getString("EXPORTED"));
				p.setDistrictName(Utils.isNull(rst.getString("district_name")));
				p.setProvinceName(Utils.isNull(rst.getString("province_name")));
				p.setAddressSummary(p.getAddressLine1()+" "+p.getAddressLine2()+" "+p.getAddressLine3());
				p.setAddressSummary(p.getAddressSummary()+" "+p.getDistrictName()+ " "+p.getProvinceName());
				
				//set can edit customer or new order
                if(rst.getInt("order_c") >0){
                	p.setCanEdit(false);
                }else{
                	p.setCanEdit(true);
                }
			}
			return p;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
	}
}
