package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.DateToolsUtil;
import util.ExcelHeader;

import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class StockReport {

	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genResultToHtml(HttpServletRequest request,StockForm stockForm,boolean excel) throws Exception{
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
		try{
		    conn = DBConnection.getInstance().getConnection();
		    sql.append("\n select A.* FROM(  ");
		    sql.append("\n  select ");
		    sql.append("\n  c.code ,c.name");
		    sql.append("\n ,h.request_date ,h.request_number ,h.status ");
		    sql.append("\n ,(select code from m_product p where p.product_id=l.inventory_item_id ) as inventory_item_code ");
		    sql.append("\n ,(select name from m_product p where p.product_id=l.inventory_item_id ) as inventory_item_desc ");
		    sql.append("\n ,l.create_date,l.expire_date ");
		    sql.append("\n ,l.uom ");
		    sql.append("\n ,sum(l.qty) as qty ");
		    sql.append("\n ,sum(l.amount) as amount ");
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
		    sql.append("\n ,l.create_date,l.expire_date ");
		    sql.append("\n ,l.uom ");
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
				classNameNumber="td_text_center";
			}
		    while(rs.next()){
		    	i++;
		    	classTRName = (i %2 == 0)?"lineE":"lineO";
		    	if(i==1){
		    		h = new StringBuffer("");
		    		h.append(ExcelHeader.EXCEL_HEADER);
		    		if(excel){
					   h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}else{
		    		   h.append("<table id='tblProduct' align='center' border='0' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}
					h.append("<tr>\n");
					h.append("<th >รหัสร้านค้า </th> \n");
					h.append("<th >ชื่อร้านค้า</th> \n");
					h.append("<th >วันที่ทำรายการ</th> \n");
					h.append("<th >เลขที่เอกสาร</th> \n");
					h.append("<th >สถานะ</th> \n");
					h.append("<th >รหัสสินค้า</th> \n");
					h.append("<th >ชื่อสินค้า</th>\n");		
					h.append("<th >หน่วยนับ</th>\n");	
					h.append("<th >จำนวน</th>	 \n");	
					h.append("<th >วันที่หมดอายุ</th>	\n");	
					h.append("<th >วันที่ผลิต</th>\n");	
					h.append("</tr> \n");
		    	}
		    	
				h.append("<tr class='"+classTRName+"'> \n");
			    h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("CODE"))+"</td> \n");
				h.append("<td class='"+className+"' width='10%'>"+Utils.isNull(rs.getString("NAME"))+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValue(rs.getDate("REQUEST_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("REQUEST_NUMBER"))+"</td> \n");
				h.append("<td class='"+className+"' width='3%'>"+Utils.isNull(rs.getString("STATUS"))+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.isNull(rs.getString("inventory_item_code"))+"</td> \n");
				h.append("<td class='"+className+"' width='15%'>"+Utils.isNull(rs.getString("inventory_item_desc"))+"</td> \n");
				h.append("<td class='"+className+"' width='3%'>"+Utils.isNull(rs.getString("UOM"))+"</td> \n");
				h.append("<td class='"+classNameNumber+"' width='7%'>"+Utils.decimalFormat(rs.getDouble("QTY"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValueDefault(rs.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,"")+"</td> \n");
				h.append("<td class='"+className+"' width='5%'>"+Utils.stringValueDefault(rs.getDate("EXPIRE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,"")+"</td> \n");
				
				h.append("</tr> \n");
		    }
			/*h.append("<tr class='hilight_text'> \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class='' align='right'> \n");
				h.append("  <B> Total By Brand </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B>  "+o.getTotalTargetQty()+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+o.getTotalTargetAmount()+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
			h.append("</tr> \n");*/
			h.append("</table> \n");
			
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
