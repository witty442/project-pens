package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DateToolsUtil;
import com.pens.util.excel.ExcelHeader;

public class StockReport {

	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genStockReportToHTML(HttpServletRequest request,StockForm stockForm,boolean excel) throws Exception{
		StringBuffer h = null;
		StringBuffer sql = new StringBuffer("");
		String classTRName= "";
		String className = "";
		String classNameNumber = "";
		String action ="";
		char singleQuote ='"';
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = (User) request.getSession().getAttribute("user");
		int i=0;
		int totalQty = 0;
		int totalQty2 = 0;
		int totalQty3 = 0;
		int totalSub = 0;
		int totalSub2 = 0;
		int totalSub3 = 0;
		String fullUom = "";
		try{
		    conn = DBConnection.getInstance().getConnection();
		    sql.append("\n select A.* FROM(  ");
		    sql.append("\n  select ");
		    sql.append("\n  c.code ,c.name");
		    sql.append("\n ,h.request_date ,h.request_number ,h.status ");
		    sql.append("\n ,(select code from m_product p where p.product_id=l.inventory_item_id ) as inventory_item_code ");
		    sql.append("\n ,(select name from m_product p where p.product_id=l.inventory_item_id ) as inventory_item_desc ");
		    sql.append("\n ,l.uom,l.uom2 ");
		    sql.append("\n ,l.expire_date, sum(l.qty) as qty,sum(l.sub) as sub ");
		    sql.append("\n ,l.expire_date2, sum(l.qty2) as qty2,sum(l.sub2) as sub2 ");
		    sql.append("\n ,l.expire_date3, sum(l.qty3) as qty3,sum(l.sub3) as sub3 ");
		    sql.append("\n FROM t_stock h,t_stock_line l, ");
		    sql.append("\n ( ");
		    sql.append("\n  select c.customer_id,c.code,c.name,c.TERRITORY" );
		    sql.append("\n  ,c.CUSTOMER_TYPE ,a.district_id,a.province_id ");
		    sql.append("\n  from m_customer c,m_address a ");
		    sql.append("\n  where c.customer_id = a.customer_id");
		    sql.append("\n  and a.purpose ='B'");
		    sql.append("\n ) c ");
		    sql.append("\n where h.customer_id =c.customer_id ");
		    sql.append("\n and h.request_number = l.request_number ");
		    if( !Utils.isNull(stockForm.getBean().getHaveStock()).equals("")){
		    	 sql.append("\n and ( l.qty <> 0 or l.qty2 <> 0 or l.qty3 <> 0)");
		    }
		    if( !Utils.isNull(stockForm.getBean().getStatus()).equals("")){
		    	 sql.append("\n and h.status='"+Utils.isNull(stockForm.getBean().getStatus())+"'");
		    }
		    if( !Utils.isNull(stockForm.getBean().getRequestDateFrom()).equals("")
		    	&&  !Utils.isNull(stockForm.getBean().getRequestDateTo()).equals("")
		    ){
		    	 sql.append("\n and h.REQUEST_DATE >= '"+DateToolsUtil.convertToTimeStamp(stockForm.getBean().getRequestDateFrom()) + "'");
		    	 sql.append("\n and h.REQUEST_DATE <= '"+DateToolsUtil.convertToTimeStamp(stockForm.getBean().getRequestDateTo()) + "'");
		    }
			
			sql.append("\n group by  c.code ");
		    sql.append("\n ,c.name");
		    sql.append("\n ,h.request_date ");
		    sql.append("\n ,h.request_number ");
		    sql.append("\n ,h.status ");
		    sql.append("\n ,l.inventory_item_id ");
		    sql.append("\n ,l.expire_date,l.expire_date2,l.expire_date3 ");
		    sql.append("\n ,l.uom,l.uom2 ");
		    sql.append("\n )A ");
		    sql.append("\n order by  A.code,A.request_date,A.inventory_item_code ");
		    
		    logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			//set style excel or html
			if(excel){
				className="text";
				classNameNumber="num_currency";
			}else{
				className="td_text_center";
				classNameNumber="td_number";
			}
		    while(rs.next()){
		    	i++;
		    	classTRName = (i %2 == 0)?"lineE":"lineO";
		    	if(i==1){
		    		h = new StringBuffer("");
		    		if(excel){
		    		   h.append(ExcelHeader.EXCEL_HEADER);
					   h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}else{
		    		   h.append("<table id='tblProduct' align='center' border='0' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}
					h.append("<tr>\n");
						h.append("<th rowspan='2'>รหัสร้านค้า </th> \n");
						h.append("<th rowspan='2'>ชื่อร้านค้า</th> \n");
						h.append("<th rowspan='2'>วันที่ทำรายการ</th> \n");
						h.append("<th rowspan='2'>เลขที่เอกสาร</th> \n");
						h.append("<th rowspan='2'>สถานะ</th> \n");
						h.append("<th rowspan='2'>รหัสสินค้า</th> \n");
						h.append("<th rowspan='2'>ชื่อสินค้า</th>\n");		
						h.append("<th rowspan='2'>หน่วยนับ</th>\n");	
						h.append("<th colspan='3'>กลุ่มที่ 1</th> \n");	
						h.append("<th colspan='3'>กลุ่มที่ 2</th>	\n");	
						h.append("<th colspan='3'>กลุ่มที่ 3</th>\n");	
					h.append("</tr> \n");
					h.append("<tr>\n");
						h.append("<th>หีบ</th>\n");
						h.append("<th>เศษ</th>\n");
						h.append("<th>วันหมดอายุ</th>\n");
						h.append("<th>หีบ</th>\n");
						h.append("<th>เศษ</th>\n");
						h.append("<th>วันหมดอายุ</th>\n");
						h.append("<th>หีบ</th>\n");
						h.append("<th>เศษ</th>\n");
						h.append("<th>วันหมดอายุ</th>\n");
					h.append("</tr> \n");
		    	}
		    	if( !Utils.isNull(rs.getString("UOM2")).equals("")){
		    	   fullUom = Utils.isNull(rs.getString("UOM"))+"/"+Utils.isNull(rs.getString("UOM2"));
		    	}else{
		    	   fullUom = Utils.isNull(rs.getString("UOM"));
		    	}
				h.append("<tr class='"+classTRName+"'> \n");
			    h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("CODE"))+"</td> \n");
				h.append("<td class='"+className+"' width='8%'>"+Utils.isNull(rs.getString("NAME"))+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValue(rs.getDate("REQUEST_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("REQUEST_NUMBER"))+"</td> \n");
				h.append("<td class='"+className+"' width='3%'>"+Utils.isNull(rs.getString("STATUS"))+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("inventory_item_code"))+"</td> \n");
				h.append("<td class='"+className+"' width='10%'>"+Utils.isNull(rs.getString("inventory_item_desc"))+"</td> \n");
				h.append("<td class='"+className+"' width='3%'>"+fullUom+"</td> \n");
				
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("QTY"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("sub"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValueDefault(rs.getDate("EXPIRE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,"")+"</td> \n");
				
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("QTY2"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("sub2"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValueDefault(rs.getDate("EXPIRE_DATE2"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,"")+"</td> \n");
				
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("QTY3"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+classNameNumber+"' width='4%'>"+Utils.decimalFormat(rs.getDouble("sub3"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValueDefault(rs.getDate("EXPIRE_DATE3"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,"")+"</td> \n");
				
				h.append("</tr> \n");
				
				//summary Total
				totalQty += rs.getInt("QTY");
				totalQty2 += rs.getInt("QTY2");
				totalQty3 += rs.getInt("QTY3");
				totalSub += rs.getInt("SUB");
				totalSub2 += rs.getInt("SUB2");
				totalSub3 += rs.getInt("SUB3");
		    }//for
		    
		    classNameNumber = "td_number_bold";
		    if(excel){
		    	classNameNumber ="num_currency_bold";
		    }
		    if(h != null){
			 h.append("<tr class='hilight_text'> \n");
				h.append("<td class='hilight_text' align='right' colspan='8'> \n");
				h.append("  <B> Total</B> \n");
				h.append("</td> \n");
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B>  "+Utils.decimalFormat(totalQty,Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(totalSub,Utils.format_current_no_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B>  "+Utils.decimalFormat(totalQty2,Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(totalSub2,Utils.format_current_no_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B>  "+Utils.decimalFormat(totalQty3,Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='"+classNameNumber+"' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(totalSub3,Utils.format_current_no_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				
			 h.append("</tr> \n");
			 h.append("</table> \n");
		    }
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
}
