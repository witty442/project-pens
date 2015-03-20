package com.isecinc.pens.report.member;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jfree.util.Log;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;


/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MemberExpirationReportProcess extends I_ReportProcess<MemberExpirationReport> {
	/**
	 * Search for performance report.
	 */
	public List<MemberExpirationReport> doReport(MemberExpirationReport report, User user, Connection conn) throws Exception {
		PreparedStatement ppstmt = null;
		ResultSet rset = null;
		List<MemberExpirationReport> pos = new ArrayList<MemberExpirationReport>();
		try{
			int i =0;
			// Get Sales Target Match With Sales Order
			
			String p_dateFrom =report.getDateFrom();
			String p_dateTo = report.getDateTo();
			
			StringBuffer sql = new StringBuffer("SELECT t.* FROM ( SELECT cm.CUSTOMER_ID , cm.CODE as MEMBER_CODE , od.CUSTOMER_NAME as MEMBER_NAME , od.ORDER_NO \n");
			sql.append(", concat(  \n")
			     .append("case when cmc.phone is not null and trim(cmc.phone) <>'' and trim(cmc.phone) <>'-' then cmc.phone else '' end, \n")
				 .append("case when cmc.phone_sub1 is not null and trim(cmc.phone_sub1) <>'' and trim(cmc.phone_sub1) <>'-' then concat('ต่อ',cmc.phone_sub1) else '' end, \n")
				 .append("case when cmc.phone2 is not null and trim(cmc.phone2) <>'' and trim(cmc.phone2) <>'-' then concat(', ', cmc.phone2) else '' end, \n")
				 .append("case when cmc.phone_sub2 is not null and trim(cmc.phone_sub2) <>''  and trim(cmc.phone_sub2) <>'-' then   concat('ต่อ',cmc.phone_sub2) else '' end, \n")
				 .append("case when cmc.mobile is not null and trim(cmc.mobile) <>'' and trim(cmc.mobile) <>'-' then concat(', ', cmc.mobile) else '' end, \n")
				 .append(" case when cmc.mobile2 is not null and trim(cmc.mobile2) <>'' and trim(cmc.mobile2) <>'-' then concat(', ', cmc.mobile2) else '' end  \n")
				.append(")as tel_no  \n")
				.append(" , MAX(ol.SHIPPING_DATE) as max_ship_date \n")
				.append(", MAX(IF(ol.SHIPPING_DATE <= NOW() , ol.TRIP_NO , 0)) as max_trip_no \n")
				.append(", SUM(IF(ol.SHIPPING_DATE <= NOW() , ol.qty , 0)) as total_qty_sent \n")
				.append("FROM t_order_line ol INNER JOIN t_order od ON ol.ORDER_ID = od.ORDER_ID \n")
				.append("INNER JOIN m_customer cm ON cm.CUSTOMER_ID = od.CUSTOMER_ID \n")
				.append("LEFT JOIN m_contact cmc ON cm.CUSTOMER_ID = cmc.CUSTOMER_ID \n")
				.append("WHERE ol.ISCANCEL = 'N' \n")
				.append("and cmc.isactive = 'Y' \n")
				.append("GROUP BY cm.CUSTOMER_ID,cm.CODE ,od.CUSTOMER_NAME , od.ORDER_NO \n")
				.append("HAVING MAX(ol.SHIPPING_DATE) <= ? AND MAX(ol.SHIPPING_DATE) >= ? \n")
				.append(") t ORDER BY t.max_ship_date , t.member_code ");
			
			logger.debug(sql.toString());
			
			ppstmt = conn.prepareStatement(sql.toString());
			Timestamp expireDateFrom = DateToolsUtil.convertToTimeStamp(p_dateFrom);
			Timestamp expireDateTo = DateToolsUtil.convertToTimeStamp(p_dateTo);
			
			ppstmt.setTimestamp(1, expireDateTo);
			ppstmt.setTimestamp(2, expireDateFrom);
			
			SimpleDateFormat sdFmt = new SimpleDateFormat("EEEEE ที่ dd MMMMM yyyy",new Locale("th", "TH"));
			
			rset = ppstmt.executeQuery();
			while(rset.next()){
				MemberExpirationReport result = new MemberExpirationReport();
				result.setExpireDate(rset.getTimestamp("max_ship_date"));
				result.setMemberCode(rset.getString("MEMBER_CODE"));
				result.setMemberName(rset.getString("MEMBER_NAME"));
				result.setMemberTel(rset.getString("tel_no"));
				result.setTotalQtySent(rset.getBigDecimal("total_qty_sent"));
				result.setTripNo(rset.getInt("max_trip_no"));
				result.setExpFrom(expireDateFrom);
				result.setExpTo(expireDateTo);
				
				result.setDateFrom(sdFmt.format(new Date(expireDateFrom.getTime())));
				result.setDateTo(sdFmt.format(new Date(expireDateTo.getTime())));
				
				pos.add(result);
			}
		
		}finally{
			if(ppstmt != null){
				ppstmt.close();ppstmt=null;
			}
			if(rset != null){
				rset.close();rset=null;
			}
		}
		return pos;
	}
}
