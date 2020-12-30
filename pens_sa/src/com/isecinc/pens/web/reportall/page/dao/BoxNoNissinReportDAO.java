package com.isecinc.pens.web.reportall.page.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.web.boxno.BoxNoBean;
import com.isecinc.pens.web.boxno.BoxNoForm;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.StockReturn;
import com.isecinc.pens.web.reportall.bean.StockReturnLine;
import com.isecinc.pens.web.reportall.page.StockReturnAction;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockConstants;
import com.isecinc.pens.web.stock.StockForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.PageingGenerate;
import com.pens.util.UserUtils;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.seq.SequenceProcessAll;

public class BoxNoNissinReportDAO {
 protected static Logger logger = Logger.getLogger("PENS");
	
 public static BoxNoBean savePrintControlBoxNo(Connection conn,User user,BoxNoBean b) throws Exception {
	PreparedStatement ps = null;
	try {
		b.setCreateUser(user.getUserName());
		b.setUpdateUser(user.getUserName());
		
		//check is Exist
		BoxNoBean beanExist = searchPrintControlBoxNo(conn, b);
		if(beanExist != null){
			b.setDocNo(beanExist.getDocNo());
			updateControlBoxNo(conn, b);
		}else{
			b = insertControlBoxNo(conn, b);
		}
	} catch (Exception ex) {
		throw ex;
	} finally {
		if(ps != null){
			ps.close();ps = null;
		}
	}
	return b;
 }
 public static BoxNoBean searchPrintControlBoxNo(Connection conn,BoxNoBean o) throws Exception {
	Statement stmt = null;
	ResultSet rst = null;
	StringBuilder sql = new StringBuilder();
	BoxNoBean returnBean = null;
	try {
		sql.append("\n  SELECT * from pensbi.XXPENS_BI_CONTROL_WASTE_BOXNO h ");
		sql.append("\n  where 1=1 ");
	    sql.append("\n  and h.period ='"+o.getPeriod()+"'");
		sql.append("\n  and h.pd_code ='"+o.getPdCode()+"'");
		
		logger.debug("sql:"+sql);
		
		stmt = conn.createStatement();
		rst = stmt.executeQuery(sql.toString());
		if (rst.next()) {
			returnBean = new BoxNoBean();
			returnBean.setDocNo(Utils.isNull(rst.getString("doc_no")));
		}//while
	} catch (Exception e) {
		throw e;
	} finally {
		try {
			rst.close();
			stmt.close();
		} catch (Exception e) {}
	}
	return returnBean;
}
 public static BoxNoBean insertControlBoxNo(Connection conn,BoxNoBean o) throws Exception{
	PreparedStatement ps = null;
	int c =1;
	try{
		//GenDocNo
		BigDecimal seq = SequenceProcessAll.getIns().getNextValue("XXPENS_BI_CONTROL_WASTE_BOXNO", new Date());
		String docNo = "B"+DateUtil.stringValue(new Date(), "yyMM",DateUtil.local_th)+Utils.decimalFormat(seq.doubleValue(),"00");
		o.setDocNo(docNo);
		logger.debug("docNo:"+o.getDocNo());
		
		StringBuffer sql = new StringBuffer("");
		sql.append(" INSERT INTO pensbi.XXPENS_BI_CONTROL_WASTE_BOXNO \n");
		sql.append(" ( period,pd_code,doc_no \n");
		sql.append("  ,print_user,print_date, print_count )  \n");
	    sql.append(" VALUES (?,?,?,?,?,?) \n");
		
		ps = conn.prepareStatement(sql.toString());
		
		ps.setString(c++,o.getPeriod());
		ps.setString(c++,o.getPdCode());
		ps.setString(c++, o.getDocNo());
		ps.setString(c++, o.getCreateUser());
		ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
		ps.setInt(c++, 1);
		
		ps.executeUpdate();
		
		return o;
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
	}
}
	 
 public static void updateControlBoxNo(Connection conn,BoxNoBean o) throws Exception{
	PreparedStatement ps = null;
	StringBuffer sql = new StringBuffer("");
	int c =1;
	try{
		sql.append(" update pensbi.XXPENS_BI_CONTROL_WASTE_BOXNO \n");
		sql.append(" set print_count=(nvl(print_count,0)+1), PRINT_DATE=? ,print_user = ? \n");
	    sql.append(" where period =? and pd_code =? and doc_no = ? \n");
		
		ps = conn.prepareStatement(sql.toString());
		ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
		ps.setString(c++, o.getUpdateUser());
		ps.setString(c++,o.getPeriod());
		ps.setString(c++,o.getPdCode());
		ps.setString(c++,o.getDocNo());
		ps.executeUpdate();
		
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
	}
}
 public static int searchTotalRecBoxNoList(Connection conn,User user,BoxNoBean mCriteria) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int c = 0;
		try {
			sql.append("\n  SELECT count(*) as c from pensbi.XXPENS_BI_WASTE_BOXNO h ");
			sql.append("\n  where 1=1 ");
			//Gen Where SQL
			sql.append(genWhereCondSql(conn, mCriteria, user,false));
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
			  c = rst.getInt("c");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return c;
	}
	 
 public static BoxNoBean searchBoxNoList(Connection conn,User user,ReportAllForm aForm
		 ,boolean allRec,int currPage,int pageSize,boolean excel ) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int r = 0,no=0;
		StringBuffer html = null;
		BoxNoBean mCriteria = aForm.getBean().getBoxNoBean();
		BoxNoBean boxNoBean = new BoxNoBean();
		Map<String, String> pdCodeMap = new HashMap<String, String>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n  SELECT " );
			sql.append("\n   h.period ,h.period_desc ,h.pd_code ");
			sql.append("\n  ,h.salesrep_code ,h.salesrep_id ,h.salesrep_name");
			sql.append("\n  ,h.total_box ,h.create_date");
			
			/*sql.append("\n  ,(select (sn.description ||'('|| sn.attribute6 ||')') as description ");
			sql.append("\n   from apps.mtl_secondary_inventories sn ");
			sql.append("\n    where sn.secondary_inventory_name = h.pd_code) as pd_desc");*/
			
			sql.append("\n ,(select A.description from(");
			sql.append("\n   select sn.secondary_inventory_name as pd_code ");
			sql.append("\n   ,(sn.description ||'('|| sn.attribute6 ||')') as description ");
			sql.append("\n   from apps.mtl_secondary_inventories sn ");
			sql.append("\n   where sn.secondary_inventory_name like 'P%'  ");
			sql.append("\n   union all");
			sql.append("\n   select pd_code, pd_desc as description from PENSBI.XXPENS_BI_MST_PD_EXTERNAL ");
			sql.append("\n   where pd_code like 'P%'");
			sql.append("\n   )A where A.pd_code = h.pd_code");
			sql.append("\n ) as pd_desc");
			
			sql.append("\n  ,(select doc_no from pensbi.XXPENS_BI_CONTROL_WASTE_BOXNO sn");
			sql.append("\n    where sn.period = h.period ");
			sql.append("\n    and sn.pd_code = h.pd_code) as doc_no");
			sql.append("\n   from pensbi.XXPENS_BI_WASTE_BOXNO h ");
			sql.append("\n   where 1=1 ");
			//Gen Where SQL
			sql.append(genWhereCondSql(conn, mCriteria, user,false));
			sql.append("\n  ORDER BY h.pd_code ,h.salesrep_code asc  \n");
			sql.append("\n )A  ");
			// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			
			//Cal No start by Page
			no = Utils.calcStartNoInPage(currPage, StockReturnAction.pageSize);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(conn,aForm,mCriteria,excel));
			  }
			  BoxNoBean m = new BoxNoBean();
			  m.setDocNo(Utils.isNull(rst.getString("doc_no")));
			  m.setPeriod(rst.getString("period"));
			  m.setPeriodDesc(rst.getString("period_desc"));
			  m.setPdCode(rst.getString("pd_code"));
			  m.setSalesrepCode(rst.getString("salesrep_code"));
			  m.setPdDesc(rst.getString("pd_desc"));
			  m.setSalesrepName(rst.getString("salesrep_name"));
			  m.setSalesrepId(rst.getString("salesrep_id"));
			  m.setTotalBox(rst.getInt("total_box")+"");
			  m.setCreateDate(DateUtil.stringValue(rst.getDate("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
		
			  //Gen Row Table
			  html.append(genRowTable(mCriteria,excel, m, no,pdCodeMap));
			  
			  pdCodeMap.put(m.getPdCode(), m.getPdCode());//set for check dup pdCode line
		
			}//while
			if(html != null){
			   html.append("</table>");
			}
			boxNoBean.setDataStrBuffer(html);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return boxNoBean;
}
	
 public static BoxNoBean searchBoxNoReportList(Connection conn,User user,BoxNoBean mCriteria) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalBox = 0;
		List<BoxNoBean> itemsList = new ArrayList<BoxNoBean>();
		try {
			sql.append("\n  SELECT " );
			sql.append("\n   h.period,h.period_desc,h.pd_code,h.total_box");
			sql.append("\n  ,h.salesrep_code,h.salesrep_id ,h.salesrep_name");
			sql.append("\n  ,h.create_date ");
			sql.append("\n  from pensbi.XXPENS_BI_WASTE_BOXNO h ");
			sql.append("\n  where 1=1 ");
			//Gen Where SQL
			sql.append(genWhereCondSql(conn, mCriteria, user,true));
			
			sql.append("\n  ORDER BY h.salesrep_code asc  \n");
			logger.debug("sql:"+sql);
		
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  BoxNoBean m = new BoxNoBean();
			  m.setPeriod(rst.getString("period"));
			  m.setPeriodDesc(rst.getString("period_desc"));
			  m.setPdCode(rst.getString("pd_code"));
			  m.setSalesrepCode(rst.getString("salesrep_code"));
			  m.setSalesrepName(rst.getString("salesrep_name"));
			  m.setSalesrepId(rst.getString("salesrep_id"));
			  m.setTotalBox(rst.getInt("total_box")+"");
			  m.setCreateDate(DateUtil.stringValue(rst.getDate("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			 
			  totalBox += Utils.convertStrToInt(m.getTotalBox());
		      itemsList.add(m);
			}//while
			mCriteria.setItemsList(itemsList);
			mCriteria.setTotalBox(Utils.decimalFormat(totalBox,Utils.format_current_no_disgit));
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	return mCriteria;
}
	 
 public static BoxNoBean searchBoxNoByZoneReportList(Connection conn,User user,BoxNoBean mCriteria) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalBox = 0;
		List<BoxNoBean> itemsList = new ArrayList<BoxNoBean>();
		try {
			sql.append("\n SELECT " );
			sql.append("\n  h.period,h.period_desc,h.pd_code");
			sql.append("\n ,( select A.description from(");
			sql.append("\n   select sn.secondary_inventory_name as pd_code ");
			sql.append("\n   ,(sn.description ||'('|| sn.attribute6 ||')') as description ");
			sql.append("\n    from apps.mtl_secondary_inventories sn ");
			sql.append("\n    where sn.secondary_inventory_name like 'P%'  ");
			sql.append("\n    union all");
			sql.append("\n    select pd_code, pd_desc as description from PENSBI.XXPENS_BI_MST_PD_EXTERNAL ");
			sql.append("\n    where pd_code like 'P%'");
			sql.append("\n   )A where A.pd_code = h.pd_code");
			sql.append("\n  ) as pd_desc");
			sql.append("\n  ,sum(h.total_box) as total_box" );
			sql.append("\n  from pensbi.XXPENS_BI_WASTE_BOXNO h ");
			sql.append("\n  where 1=1 ");
			//Gen Where SQL
			sql.append(genWhereCondSql(conn, mCriteria, user,true));
			sql.append("\n  GROUP BY h.period,h.period_desc,h.pd_code  ");
			sql.append("\n  ORDER BY h.pd_code asc ");
			logger.debug("sql:"+sql);
		
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  BoxNoBean m = new BoxNoBean();
			  m.setPeriod(rst.getString("period"));
			  m.setPeriodDesc(rst.getString("period_desc"));
			  m.setPdCode(rst.getString("pd_code")+" "+rst.getString("pd_desc"));
			  m.setTotalBox(rst.getInt("total_box")+"");
			 
			  totalBox += Utils.convertStrToInt(m.getTotalBox());
		      itemsList.add(m);
			}//while
			mCriteria.setItemsList(itemsList);
			mCriteria.setTotalBox(Utils.decimalFormat(totalBox,Utils.format_current_no_disgit));
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	return mCriteria;
}
	public static StringBuffer genWhereCondSql(Connection conn,BoxNoBean o,User user,boolean report) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getPeriod()).equals("")){
	        sql.append("\n  and h.period ='"+o.getPeriod()+"'");
		}
		if( !Utils.isNull(o.getPdCode()).equals("")){
	        sql.append("\n  and h.pd_code ='"+o.getPdCode()+"'");
		}
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and substr(H.salesrep_code,2,1) = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
	
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			//pd sales_zone
			sql.append("\n  and h.pd_code in(");
			sql.append("\n    select pd_code from pensbi.XXPENS_BI_MST_PD_ZONE");
			sql.append("\n    where zone = "+Utils.isNull(o.getSalesZone()) );
			sql.append("\n  )");
		}
		
		/** ROLE_VANDOC =ALL and admin pass all **/
		if(!report){
	        if(user.getRoleVanDoc().indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
		       boolean isSetCustMapSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
				if (isSetCustMapSalesTT){
					 sql.append("\n  and H.salesrep_code in( ");
					 sql.append("\n   select Z.salesrep_code from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT C ");
					 sql.append("\n   ,pensbi.XXPENS_BI_MST_SALES_ZONE Z ");
					 sql.append("\n   where C.zone = Z.zone");
					 sql.append("\n   and  C.user_name='"+user.getUserName()+"'");
					 sql.append("\n  )");
				
				}else{
					//check user login is Sales (S404)
					SalesrepBean salesrepBean = SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
					if( salesrepBean != null && !Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
						 sql.append("\n  and H.salesrep_code = '"+user.getUserName().toUpperCase()+"' ");
					}else{
						// not permit
						sql.append("\n  and 1=2 /** for not permit query **/");
					}
				}
	        }//if
		}
		return sql;
	}
	
	public static String getPdDescXXXXXX(Connection conn,String salesrepCode,String pdCode){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String pdDesc = "";
		try{
			//pd_desc
			if(salesrepCode.startsWith("V")){//VanSales
				sql.append("\n  select (sn.description ||'('|| sn.attribute6 ||')') as description ");
				sql.append("\n  from apps.xxpens_inv_subinv_access s");
				sql.append("\n  ,apps.mtl_secondary_inventories sn ");
				sql.append("\n  where s.subinventory = sn.secondary_inventory_name ");
				sql.append("\n  and s.subinventory ='"+pdCode+"' ");
				sql.append("\n  and s.code = '"+salesrepCode+"'");
				
			}else if(salesrepCode.startsWith("S")){//CreditSales
				sql.append("\n  select (sn.description ||'('|| sn.attribute6 ||')') as description ");
				sql.append("\n  from apps.mtl_secondary_inventories sn ");
				sql.append("\n  where sn.secondary_inventory_name ='"+pdCode+"' ");
			}
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				pdDesc = Utils.isNull(rst.getString("description"));
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pdDesc;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(Connection conn,ReportAllForm aForm,BoxNoBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
		}else{
			//Gen Paging
		   int totalPage = aForm.getTotalPage();
		   int totalRecord = aForm.getTotalRecord();
		   int currPage =  aForm.getCurrPage();
		   int startRec = aForm.getStartRec();
		   int endRec = aForm.getEndRec();
		   int no = Utils.calcStartNoInPage(currPage, aForm.getPageSize());
			
		  //pageing Generate
		   h.append(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));

		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		if( !excel){
		  h.append(" <th >ใบอนุมัติให้ส่งคืน</th> \n");
		}
		h.append(" <th >เลขที่เอกสาร</th> \n");
		h.append(" <th >รหัส PD</th> \n");
		h.append(" <th >ชื่อ PD</th>");
		h.append(" <th >รหัสพนักงานขาย</th>");
		h.append(" <th >ชื่อพนักงานขาย</th>");
		h.append(" <th >เดือน</th>");
		h.append(" <th >จำนวนกล่อง</th>");
		h.append("</tr> \n");
	
		return h;
	}
	private static  StringBuffer genRowTable(BoxNoBean head,boolean excel,BoxNoBean item,int no,Map<String, String> pdCodeMap) throws Exception{
		StringBuffer h = new StringBuffer("");
		String trCLass = no %2==0?"lineE":"lineO";
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number ";
		h.append("<tr class='"+trCLass+"'> \n");
		if(excel){
			classText = "text";
			classTextCenter = "text";
			classCurrency = "num";
		}
		if(pdCodeMap.get(item.getPdCode()) ==null){
		   if( !excel){
			  h.append("<td class='"+classTextCenter+"' width='8%'> \n");
			  h.append("<a href=javascript:printControlBoxNoReport('"+item.getPeriod()+"','"+item.getPdCode()+"')> \n");
			  h.append(" <b>พิมพ์</b></a></td> \n");
		   }
		   h.append("<td class='"+classTextCenter+"' width='7%'>"+item.getDocNo()+"</td> \n");
		   h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getPdCode()+"</td> \n");
		   h.append("<td class='"+classText+"' width='15%'>"+item.getPdDesc()+"</td> \n");
		}else{
		   if( !excel){
		       h.append("<td class='"+classTextCenter+"' width='8%'> </td>\n");
		   } 
		   h.append("<td class='"+classTextCenter+"' width='7%'></td> \n");
		   h.append("<td class='"+classTextCenter+"' width='5%'></td> \n");
		   h.append("<td class='"+classText+"' width='15%'></td> \n");
		}
		h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getSalesrepCode()+"</td> \n");
		h.append("<td class='"+classText+"' width='15%'>"+item.getSalesrepName()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getPeriod()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='8%'>"+item.getTotalBox()+"</td> \n");
		h.append("</tr> \n");
		return h;
	}
	public static String getPdDesc(Connection conn,String salesrepCode,String pdCode){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String pdDesc = "";
		try{
			//pd_desc
			if(salesrepCode.startsWith("V")){//VanSales
				sql.append("\n  select (sn.description ||'('|| sn.attribute6 ||')') as description ");
				sql.append("\n  from apps.xxpens_inv_subinv_access s");
				sql.append("\n  ,apps.mtl_secondary_inventories sn ");
				sql.append("\n  where s.subinventory = sn.secondary_inventory_name ");
				sql.append("\n  and s.subinventory ='"+pdCode+"' ");
				sql.append("\n  and s.code = '"+salesrepCode+"'");
				
			}else if(salesrepCode.startsWith("S")){//CreditSales
				sql.append("\n select A.description from(");
				sql.append("\n   select sn.secondary_inventory_name as pd_code ");
				sql.append("\n   ,(sn.description ||'('|| sn.attribute6 ||')') as description ");
				sql.append("\n   from apps.mtl_secondary_inventories sn ");
				sql.append("\n   where sn.secondary_inventory_name like 'P%'  ");
				sql.append("\n   union all");
				sql.append("\n   select pd_code, pd_desc as description from PENSBI.XXPENS_BI_MST_PD_EXTERNAL ");
				sql.append("\n   where pd_code like 'P%'");
				sql.append("\n  )A where A.pd_code ='"+pdCode+"'");
				
			}
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				pdDesc = Utils.isNull(rst.getString("description"));
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pdDesc;
	}
}
