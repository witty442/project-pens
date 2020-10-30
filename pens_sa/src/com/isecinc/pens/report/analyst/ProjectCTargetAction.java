package com.isecinc.pens.report.analyst;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.projectc.ProjectCBean;
import com.isecinc.pens.web.projectc.ProjectCImageBean;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ProjectCTargetAction extends I_Action {

	
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
		Map<String,String> MONTH_MAP = new HashMap<String,String>();
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			 conn = DBConnection.getInstance().getConnectionApps();
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				//init periodList
				request.getSession().setAttribute("PERIOD_LIST", initPeriod(conn));
				
				/** init year **/
				List<References> yearL = initYearList(conn," DESC");
				request.getSession().setAttribute("yearList", yearL);
				

				for(References year:yearL){
					String yearKey = year.getKey();
					String yearShow = year.getName();
					
					MONTH_MAP.put(yearKey+"01", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"02", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"03", "��.�. "+yearShow);
					MONTH_MAP.put(yearKey+"04", "��.�. "+yearShow);
					MONTH_MAP.put(yearKey+"05", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"06", "��.�. "+yearShow);
					MONTH_MAP.put(yearKey+"07", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"08", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"09", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"10", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"11", "�.�. "+yearShow);
					MONTH_MAP.put(yearKey+"12", "�.�. "+yearShow);
				}
				
				//PROVINCE_LIST
				//add Blank Row
				dataList = new ArrayList<PopupBean>();
				PopupBean item = new PopupBean();
				item.setProvince("");
				item.setProvinceName("");
				dataList.add(item);
				
				List<PopupBean> tempList = searchProvinceList(conn,"");
				dataList.addAll(tempList);
				request.getSession().setAttribute("PROVINCE_LIST",dataList);
				
				//REPORT_TYPE_LIST
				dataList = new ArrayList<PopupBean>();
				dataList.add(new PopupBean("reportType","��ҹ���,�Ң�","CUST_GROUP,BRANCH_ID"));
				request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
				
				//COND_TYPE_LIST
				dataList = new ArrayList<PopupBean>();
				dataList.add(new PopupBean("�ʴ�੾���Թ��ҷ�辺","DISP_FOUND_PRODUCT"));
				dataList.add(new PopupBean("�ʴ�������","DISP_ALL"));
				request.getSession().setAttribute("COND_TYPE_LIST",dataList);
				
				bean.setProjectCBean(new ProjectCBean());
				reportAllForm.setBean(bean);
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
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			reportAllForm.getBean().getProjectCBean().setDataStrBuffer(null);
			reportAllForm.getBean().getProjectCBean().setItemsList(null);
			
			ProjectCBean projectCBean = searchReportModel(request.getContextPath(), reportAllForm.getBean().getProjectCBean(), false, user);
			if(projectCBean != null && projectCBean.getDataStrBuffer() != null){
			   reportAllForm.getBean().setProjectCBean(projectCBean);
			}else{
				request.setAttribute("Message", "��辺������");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
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
			
			ProjectCBean projectCBean = searchReportModel(request.getContextPath(), reportAllForm.getBean().getProjectCBean(), true, user);
			if(projectCBean != null && projectCBean.getDataStrBuffer() != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(projectCBean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", "��辺������");
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
	public ProjectCBean searchReportModel(String contextPath,ProjectCBean o,boolean excel,User user){
		ProjectCBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String[] columnNameArr = null;
		StringBuffer html = null;
		int r = 0;
		String columnAllSql = "";
		String columnAllGroupBySql = "";
		List<ProjectCBean> itemList = new ArrayList<ProjectCBean>();
		try{
			//create connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			String[] columnAll = genSelectColumnName(columnNameArr);
			columnAllSql = columnAll[0];
			columnAllGroupBySql = columnAll[1];
			
	        //add default column display
			columnAllSql +=",M.check_date,M.found,M.leg,M.remark";
			columnAllGroupBySql +=",M.check_date,M.found,M.leg,M.remark";
			 
			sql.append("\n  SELECT "+columnAllSql);
			sql.append("\n  FROM (");
			sql.append("\n    SELECT M.ORACLE_CUST_NO,B.BRANCH_ID,B.BRANCH_NAME");
			sql.append("\n    ,M.CHECK_DATE,M.CHECK_USER");
			sql.append("\n    FROM PENSBI.PENS_TT_BRANCH B");
			sql.append("\n    WHERE 1=1 ");
			
			logger.debug("startDate:"+o.getStartDate());
			String condMonth = "";
			if("MONTH".equalsIgnoreCase(o.getTypeSearch())){
				logger.debug("chkMonth:"+o.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<o.getChkMonth().length;i++){
					logger.debug("name:["+i+"]value:["+o.getChkMonth()[i]+"]");
					condMonth +="'"+o.getChkMonth()[i]+"',";
				}
				condMonth = condMonth.substring(0,condMonth.length()-1);
				//sql.append("\n and to_char(M.CHECK_DATE,'yyyymm') in("+condMonth+")");
			}//
		   
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n   and B.ORACLE_CUST_NO in( "+SQLHelper.converToTextSqlIn(o.getStoreCode())+")");
			}
			if( !Utils.isNull(o.getBranchId()).equals("")){
				sql.append("\n   and B.BRANCH_ID in( "+SQLHelper.converToTextSqlIn(o.getBranchId())+")");
			}
			
			if( !Utils.isNull(o.getProvince()).equals("")){
				sql.append("\n   and B.PROVINCE ='"+Utils.isNull(o.getProvince())+"' ");
			}
			if( !Utils.isNull(o.getAmphor()).equals("")){
				sql.append("\n   and B.AMPHOR  ='"+Utils.isNull(o.getAmphor())+"'");
			}
			sql.append("\n )M ");
			sql.append("\n GROUP BY "+columnAllGroupBySql );
			sql.append("\n ORDER BY "+columnAllGroupBySql);
			
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString());
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new ProjectCBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr));
			  }
			  
			  for(int i=0;i<columnNameArr.length;i++){
				   if("ORACLE_CUST_NO".equalsIgnoreCase(columnNameArr[i])){
						item.setStoreCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setStoreName(Utils.isNull(rst.getString("STORE_NAME")));
				   }else if("BRANCH_ID".equalsIgnoreCase(columnNameArr[i])){
						item.setBranchId(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setBranchName(Utils.isNull(rst.getString("BRANCH_NAME")));
				   }else if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])){
						item.setProductCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setProductName(Utils.isNull(rst.getString("PRODUCT_NAME")));
				   }
			  }//for
			
			  item.setCheckDate(DateUtil.stringValue(rst.getDate("CHECK_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setFound(Utils.isNull(rst.getString("found")));
			  item.setRemark(Utils.isNull(rst.getString("REMARK")));
			  item.setLeg(Utils.isNull(rst.getString("LEG")));
	
			  //add to List
			  itemList.add(item);
			  
			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item));

			}//while
			
			//Check Execute Found data
			if(r>0){
			  // Get Total
			  // html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty));
			  // gen end Table
			  html.append("</table>");
			}
			
			o.setItemsList(itemList);
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	private  String[] genSelectColumnName(String[] columnNameArr) throws Exception{
		String[] sqlAll = new String[2];
		String sql = "";
		String columnGroupBy ="";
		for(int i=0;i<columnNameArr.length;i++){
		  if("oracle_Cust_No".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.oracle_cust_no,";
			  sql +="\n (select x.party_name from apps.xxpens_ar_customer_all_v X WHERE X.account_number = M.oracle_cust_no) as STORE_NAME,";
			  columnGroupBy +="\n M.oracle_cust_no,";
		  }else if("BRANCH_ID".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.BRANCH_ID,";
			  sql +="\n M.BRANCH_NAME,";
			  columnGroupBy +="\n M.BRANCH_ID ,";
			  columnGroupBy +="\n M.BRANCH_NAME,";
		  }else if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.PRODUCT_CODE,";
			  sql +="\n M.PRODUCT_NAME,";
			  
			  columnGroupBy +="\n M.PRODUCT_CODE,";
			  columnGroupBy +="\n M.PRODUCT_NAME,";
		  }//if
		}//for
		
		//remove "," in last position
		 sql = sql.substring(0,sql.length()-1);
	     columnGroupBy = columnGroupBy.substring(0,columnGroupBy.length()-1);
		
		sqlAll[0] = sql;
		sqlAll[1] = columnGroupBy;
	  return sqlAll;
	}
	
	private  StringBuffer genHeadTable(String contextPath,ProjectCBean head,boolean excel,String[] columnNameArr) throws Exception{
		String icoZise=  "width='20px' height='20px'";
		StringBuffer h = new StringBuffer("");
		
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
		}
		String width="100%";
		if(columnNameArr.length<2){
			//width="60%";
		}
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append("<th>�ѹ�����</th> \n");
		for(int i=0;i<columnNameArr.length;i++){
		  if( excel ==false){
			   if("ORACLE_CUST_NO".equalsIgnoreCase(columnNameArr[i])){
					h.append("<th>��ҹ���</th> \n");
					h.append("<th>������ҹ���</th> \n");
				}else if("BRANCH_ID".equalsIgnoreCase(columnNameArr[i])){
					//h.append("<th>��ҹ���</th> \n");
					h.append("<th>�����Ң�</th> \n");
				}else if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<th>SKU</th> \n");
					h.append("<th>SKU Name</th> \n");
				}
			  /* if( excel ==false){
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('"+columnNameArr[i]+"','ASC') />");
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('"+columnNameArr[i]+"','DESC') />");
			   }*/
			   h.append(" </th> \n");
		  }else{
			  if("ORACLE_CUST_NO".equalsIgnoreCase(columnNameArr[i])){
					h.append("<th>��ҹ���</th> \n");
					h.append("<th>������ҹ���</th> \n");
				}else if("BRANCH_ID".equalsIgnoreCase(columnNameArr[i])){
					//h.append("<th>��ҹ���</th> \n");
					h.append("<th>�����Ң�</th> \n");
				}else if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<th>SKU</th> \n");
					h.append("<th>SKU Name</th> \n");
				}
			  //split name 
			  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
		  }
		  
		}//for
		h.append("<th>��/����� �Թ���ྐྵ��</th> \n");
		h.append("<th>�ӹǹ��</th> \n");
		h.append("<th>�����˵�</th> \n");
		if(!excel){
		  h.append("<th>���ٻ��Сͺ</th> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genRowTable(ProjectCBean head,boolean excel,String[] columnNameArr,ProjectCBean item) throws Exception{
		StringBuffer h = new StringBuffer("");
		char singleQuote ='"';
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		
		h.append("<tr> \n");
		h.append(" <td class='"+classNameCenter+"' width='5%'>"+item.getCheckDate()+"</td> \n");
		
		for(int i=0;i<columnNameArr.length;i++){
			//logger.debug("columnName["+columnNameArr[i]+"]");
			if(excel){
				className = "text";
				classNameCenter ="text";
				classNameNumber = "num_currency";
			}else{
				if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])
				 || "BRANCH_ID".equalsIgnoreCase(columnNameArr[i])
				 || "ORACLE_CUST_NO".equalsIgnoreCase(columnNameArr[i])
				 ){
					className ="td_text";
					classNameCenter="td_text_center";
				}
			}
           
			if("ORACLE_CUST_NO".equalsIgnoreCase(columnNameArr[i])){
				h.append("<td class='"+classNameCenter+"' width='5%'>"+item.getStoreCode()+"</td> \n");
				h.append("<td class='"+className+"' width='10%'>"+item.getStoreName()+"</td> \n");
			}else if("BRANCH_ID".equalsIgnoreCase(columnNameArr[i])){
				//h.append("<td class='"+className+"' width='5%'>"+item.getBranchId()+"</td> \n");
				h.append("<td class='"+className+"' width='8%'>"+item.getBranchName()+"</td> \n");
			}else if("PRODUCT_CODE".equalsIgnoreCase(columnNameArr[i])){
				h.append("<td class='"+classNameCenter+"' width='5%'>"+item.getProductCode()+"</td> \n");
				h.append("<td class='"+className+"' width='20%'>"+item.getProductName()+"</td> \n");
			}
		}//for
		h.append("<td class='"+classNameCenter+"' width='3%'>"+item.getFound()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='3%'>"+item.getLeg()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getRemark()+"</td> \n");
		if(!excel){
		    h.append("<td class='"+classNameCenter+"' width='5%'> \n");
			h.append(" <a href="+singleQuote+"javascript:openImageList('"+item.getCheckDate()+"','"+item.getStoreCode()+"')"+singleQuote+" > \n");
			h.append("  ���ٻ��Сͺ" );
			h.append("</a> \n");
		    h.append("</td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	public  List<PopupBean> initPeriod(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		String startDate = "";
		String endDate ="";
		StringBuffer sql = new StringBuffer("");
		Statement stmt = null;
		ResultSet rst = null;
		Date requestDate = null;
		try{
			sql.append("select distinct r ,r2 from( \n");
			sql.append("select to_char(check_date,'mm/yyyy') as r \n"
					+ ", to_number(to_char(check_date,'yyyymm') ) as r2 \n"
					+ " from PENSBI.PENSBME_TT_PROJECTC \n"
					+ " ) order by r2 desc \n");
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				item = new PopupBean();
				requestDate = DateUtil.parse("01/"+rst.getString("r"), DateUtil.DD_MM_YYYY_WITH_SLASH);
				cal.setTime(requestDate);
				logger.debug("Cal:"+cal.getTime());
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy",DateUtil.local_th).toUpperCase();
				logger.debug("period:"+periodName);
		        startDate  =  "01/"+DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th);
		        endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th);
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);	
				
				cal = Calendar.getInstance();
			}
			if(monthYearList.size()==0){
				item = new PopupBean();
				logger.debug("Cal:"+cal.getTime());
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy",DateUtil.local_th).toUpperCase();
				logger.debug("period:"+periodName);
		        startDate  =  "01/"+DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th);
		        endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th);
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);	
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return monthYearList;
	}
	
	public  List<PopupBean> searchProvinceList(Connection conn,String provinceName){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct PROVINCE FROM PENSBI.PENS_TT_BRANCH where 1=1 ");
			if( !Utils.isNull(provinceName).equals("")){
				sql.append("\n  and PROVINCE ='"+provinceName+"'");
			}
			sql.append("\n  ORDER BY PROVINCE asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setProvince(Utils.isNull(rst.getString("PROVINCE")));
				item.setProvinceName(Utils.isNull(rst.getString("PROVINCE")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public List<PopupBean> searchAmphorList(String provinceName) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchAmphorListModel(conn,provinceName);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
			  conn.close();
			}
		}
	}
	public  List<PopupBean> searchAmphorListModel(Connection conn,String provinceName){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct amphor FROM PENSBI.PENS_TT_BRANCH ");
			sql.append("\n  where 1=1  ");
			if( !Utils.isNull(provinceName).equals("")){
				sql.append("\n  and province ='"+provinceName+"'");
			}
			sql.append("\n  ORDER BY amphor asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setDistrict(Utils.isNull(rst.getString("amphor")));
				item.setDistrictName(Utils.isNull(rst.getString("amphor")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public  List<References> initYearList(Connection conn,String sort) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql = "select distinct to_char(check_date,'yyyy') as order_year ";
			sql += "from PENSBI.PENSBME_TT_PROJECTC ORDER BY to_char(check_date,'yyyy') "+sort;
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("ORDER_YEAR"), (rs.getInt("ORDER_YEAR")+543)+""));
			}
			
		}catch(Exception e){
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return yearList;
	}
}
