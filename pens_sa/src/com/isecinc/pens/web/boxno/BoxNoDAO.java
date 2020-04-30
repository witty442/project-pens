package com.isecinc.pens.web.boxno;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.page.StockReturnAction;
import com.isecinc.pens.web.salestarget.SalesTargetBean;
import com.isecinc.pens.web.stock.StockBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.PageingGenerate;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class BoxNoDAO {

	private Logger logger = Logger.getLogger("PENS");
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
    
	
	public boolean saveBoxNo(Connection conn,User user,BoxNoBean b) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			//check is Exist
			BoxNoBean beanExist = searchBoxNo(conn, user, b);
			if(beanExist != null){
				updateBoxNo(conn, b);
			}else{
				insertBoxNo(conn, b);
			}
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			
		}
		return result;
	}
	
	 public  void insertBoxNo(Connection conn,BoxNoBean o) throws Exception{
		PreparedStatement ps = null;
		int c =1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.XXPENS_BI_WASTE_BOXNO \n");
			sql.append(" (period,period_desc, pd_code,  \n");
			sql.append(" SALESREP_CODE, salesrep_name,  \n");
			sql.append("  salesrep_id, total_box, \n");
			sql.append("  CREATE_USER, CREATE_DATE)  \n");
		    sql.append(" VALUES (?,?,?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++,o.getPeriod());
			ps.setString(c++,o.getPeriodDesc());
			ps.setString(c++,o.getPdCode());
			ps.setString(c++, o.getSalesrepCode());
			ps.setString(c++, o.getSalesrepName());
			ps.setBigDecimal(c++, new BigDecimal(o.getSalesrepId()));
			ps.setInt(c++, Utils.convertStrToInt(o.getTotalBox()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	 
	 public void updateBoxNo(Connection conn,BoxNoBean o) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int c =1;
		try{
			sql.append(" update PENSBI.XXPENS_BI_WASTE_BOXNO \n");
			sql.append(" set pd_code =? ,total_box = ?,UPDATE_USER=?, UPDATE_DATE=? \n");
		    sql.append(" where period =? and pd_code =? and salesrep_code = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(c++,o.getPdCode());
			ps.setInt(c++, Utils.convertStrToInt(o.getTotalBox()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++,o.getPeriod());
			ps.setString(c++,o.getPdCodeKey());
			ps.setString(c++, o.getSalesrepCode());
			
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	 
	 public void updatePrintDate(Connection conn,BoxNoBean o) throws Exception{
			PreparedStatement ps = null;
			StringBuffer sql = new StringBuffer("");
			int c =1;
			try{
				sql.append(" update PENSBI.XXPENS_BI_WASTE_BOXNO \n");
				sql.append(" set PRINT_COUNT=(NVL(PRINT_COUNT,0) +1), PRINT_DATE=? \n");
			    sql.append(" where period =? and pd_code =? and salesrep_code = ? \n");
				
				ps = conn.prepareStatement(sql.toString());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++,o.getPeriod());
				ps.setString(c++,o.getPdCode());
				ps.setString(c++, o.getSalesrepCode());
				
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public BoxNoBean searchBoxNo(Connection conn,User user,BoxNoBean b) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			BoxNoBean bean = null;
			try {
				if(user.getUserGroupName().equalsIgnoreCase("Credit Sales")){
					sql.append("\n  SELECT h.* ");
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
					
					sql.append("\n  from pensbi.XXPENS_BI_WASTE_BOXNO h ");
					sql.append("\n  where 1=1 ");
					sql.append("\n  and h.salesrep_code ='"+user.getUserName().toUpperCase()+"'");
			        sql.append("\n  and h.period ='"+b.getPeriod()+"'");
			        sql.append("\n  and h.pd_code ='"+b.getPdCodeKey()+"'");
				}else{
					sql.append("\n  SELECT h.* ");
					sql.append("\n  ,(select (sn.description ||'('|| sn.attribute6 ||')') as description ");
					sql.append("\n   from apps.xxpens_inv_subinv_access s");
					sql.append("\n  ,apps.mtl_secondary_inventories sn ");
					sql.append("\n   where s.subinventory = sn.secondary_inventory_name ");
					sql.append("\n   and s.subinventory like 'P%' and h.pd_code =s.subinventory ");
					sql.append("\n   and s.code ='"+user.getUserName().toUpperCase()+"') as pd_desc");
					sql.append("\n  from pensbi.XXPENS_BI_WASTE_BOXNO h ");
					sql.append("\n  where 1=1 ");
					sql.append("\n  and h.salesrep_code ='"+user.getUserName().toUpperCase()+"'");
			        sql.append("\n  and h.period ='"+b.getPeriod()+"'");
			        sql.append("\n  and h.pd_code ='"+b.getPdCodeKey()+"'");
				}
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
				  bean = new BoxNoBean();
				  bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				  bean.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
				  bean.setPeriod(Utils.isNull(rst.getString("period")));
				  bean.setPdCodeKey(rst.getString("pd_code"));
				  bean.setPeriodDesc(Utils.isNull(rst.getString("period_desc")));
				  bean.setPdCode(Utils.isNull(rst.getString("pd_code")));
				  bean.setPdDesc(Utils.isNull(rst.getString("pd_desc")));
				  bean.setTotalBox(rst.getString("total_box"));
				  
				  //wait for May
				  /*Date periodDate = DateUtil.parse("01/"+bean.getPeriod(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				  if(DateUtil.isSamePeriodMonthYear(periodDate, new Date())){
					  bean.setCanEdit(true);
				  }*/
				  bean.setCanEdit(true);
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
	}
	 
	 public int searchTotalRecBoxNoList(Connection conn,User user,BoxNoForm aForm) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		BoxNoBean mCriteria = aForm.getBean();
		int c = 0;
		try {
			sql.append("\n  SELECT count(*) as c from pensbi.XXPENS_BI_WASTE_BOXNO h ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and h.salesrep_code ='"+user.getUserName().toUpperCase()+"'");
			if( !Utils.isNull(mCriteria.getPeriod()).equals("")){
	          sql.append("\n  and h.period ='"+mCriteria.getPeriod()+"'");
			}
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
	 
	 public BoxNoBean searchBoxNoList(Connection conn,User user,BoxNoForm aForm
			 ,boolean allRec,int currPage,int pageSize,boolean excel ) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			BoxNoBean mCriteria = aForm.getBean();
			int r = 0,no=0;
			StringBuffer html = null;
			BoxNoBean boxNoBean = new BoxNoBean();
			try {
				sql.append("\n select M.* from (");
				sql.append("\n select A.* ,rownum as r__ from (");
				
				sql.append("\n  SELECT " );
				sql.append("\n   h.period,h.period_desc,h.pd_code,h.total_box");
				sql.append("\n  ,h.salesrep_code,h.salesrep_id ,h.salesrep_name");
				sql.append("\n  ,h.create_date ");
				//pd_desc
				if(user.getUserGroupName().equalsIgnoreCase("Credit Sales")){
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
				}else{
					sql.append("\n  ,(select (sn.description ||'('|| sn.attribute6 ||')') as description from apps.xxpens_inv_subinv_access s");
					sql.append("\n  ,apps.mtl_secondary_inventories sn ");
					sql.append("\n   where s.subinventory = sn.secondary_inventory_name ");
					sql.append("\n   and s.subinventory like 'P%' and h.pd_code =s.subinventory ");
					sql.append("\n   and s.code ='"+user.getUserName().toUpperCase()+"') as pd_desc");
				}
				sql.append("\n  from pensbi.XXPENS_BI_WASTE_BOXNO h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and h.salesrep_code ='"+user.getUserName().toUpperCase()+"'");
				if( !Utils.isNull(mCriteria.getPeriod()).equals("")){
		           sql.append("\n  and h.period ='"+mCriteria.getPeriod()+"'");
				}
				sql.append("\n  ORDER BY h.period desc \n");
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
					 html.append(genHeadTable(conn,aForm,excel));
				  }
				  BoxNoBean m = new BoxNoBean();
				  m.setPeriod(rst.getString("period"));
				  m.setPeriodDesc(rst.getString("period_desc"));
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdCodeKey(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
				  m.setSalesrepCode(rst.getString("salesrep_code"));
				  m.setSalesrepName(rst.getString("salesrep_name"));
				  m.setSalesrepId(rst.getString("salesrep_id"));
				  m.setTotalBox(rst.getInt("total_box")+"");
				  m.setCreateDate(DateUtil.stringValue(rst.getDate("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				 
				  //Can Edit Period is in(currentMMYYYY)
				  //Wait May start
				 /* Date periodDate = DateUtil.parse("01/"+m.getPeriod(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				  if(DateUtil.isSamePeriodMonthYear(periodDate, new Date())){
					  m.setCanEdit(true);
				  }*/
				  m.setCanEdit(true);
				  html.append(genRowTable(excel, m, no));
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
	
	 /**
		 * 
		 * @param columnNameArr
		 * @return
		 * @throws Exception
		 */
		private static StringBuffer genHeadTable(Connection conn,BoxNoForm aForm,boolean excel) throws Exception{
			StringBuffer h = new StringBuffer("");
			BoxNoBean head = aForm.getBean();
			if(excel){
				int colspan=7;
				h.append(ExcelHeader.EXCEL_HEADER);
			}else{
				//Gen Paging
			   int totalPage = aForm.getTotalPage();
			   int totalRecord = aForm.getTotalRecord();
			   int currPage =  aForm.getCurrPage();
			   int startRec = aForm.getStartRec();
			   int endRec = aForm.getEndRec();
			   int no = startRec;
				
			  //pageing Generate
			   h.append(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));

			}
			String width="100%";
			h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr> \n");
			if( !excel){
			  h.append(" <th >พิมพ์ใบคุม</th> \n");
			  h.append(" <th >แก้ไข/ดู</th> \n");
			}
			h.append(" <th >รหัส PD</th> \n");
			h.append(" <th >ชื่อ PD</th>");
			h.append(" <th >รหัสพนักงานขาย</th>");
			h.append(" <th >ชื่อพนักงานขาย</th>");
			h.append(" <th >เดือน</th>");
			h.append(" <th >จำนวนกล่อง</th>");
			h.append("</tr> \n");
		
			return h;
		}
		private static  StringBuffer genRowTable(boolean excel,BoxNoBean item,int no) throws Exception{
			StringBuffer h = new StringBuffer("");
			String classText = "td_text";
			String classTextCenter = "td_text_center";
			String classCurrency = "td_number ";
			h.append("<tr> \n");
			if(excel){
				classText = "text";
				classTextCenter = "text";
				classCurrency = "currency";
			}
			if( !excel){
			  h.append("<td class='"+classTextCenter+"' width='8%'> \n");
			  h.append("<a href=javascript:printBoxNoReport('"+item.getPeriod()+"','"+item.getPdCode()+"','"+item.getSalesrepCode()+"')> \n");
			  h.append(" <b>พิมพ์</b></a></td> \n");
			  
			  if(item.isCanEdit()){
			     h.append("<td class='"+classTextCenter+"' width='8%'> \n");
			     h.append("<a href=javascript:view('edit','"+item.getPeriod()+"','"+item.getPdCode()+"','"+item.getSalesrepCode()+"')> \n");
			     h.append("<b> แก้ไข</b> </a></td> \n");
			  }else{
				 h.append("<td class='"+classTextCenter+"' width='8%'> \n");
				 h.append("<a href=javascript:view('view','"+item.getPeriod()+"','"+item.getPdCode()+"','"+item.getSalesrepCode()+"')> \n");
				 h.append("<b> ดูรายละเอียด </b></a></td> \n");
			  }
			}
			h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getPdCode()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='20%'>"+item.getPdDesc()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='10%'>"+item.getSalesrepCode()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='15%'>"+item.getSalesrepName()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getPeriod()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getTotalBox()+"</td> \n");
			h.append("</tr> \n");
			return h;
		}
}
