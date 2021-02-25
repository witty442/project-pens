package com.isecinc.pens.report.moveorder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.process.order.OrderProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;

/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MoveOrderReportProcess extends I_ReportProcess<MoveOrderReport> {

	/**
	 * Search for performance report.
	 */
	public List<MoveOrderReport> doReport(MoveOrderReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<MoveOrderReport> pos = new ArrayList<MoveOrderReport>();
		try {
			sql.append("\n SELECT ");
			sql.append("\n h.request_number,h.request_date,h.sales_code,h.pd_code,");
			sql.append("\n h.move_order_type,h.status,h.exported,h.USER_ID");
			sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
			sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
			sql.append("\n  ,(select sum(qty1) from t_move_order_line p where p.status='SV' and p.request_number = h.request_number group by p.request_number) as qty1 ");
			sql.append("\n  ,(select sum(qty2) from t_move_order_line p where p.status='SV' and  p.request_number = h.request_number group by p.request_number) as qty2 ");
			sql.append("\n  ,(select sum(total_amount) from t_move_order_line p where p.status='SV' and  p.request_number = h.request_number group by p.request_number) as total_amount");
			sql.append("\n  from t_move_order h ");
			sql.append("\n  where 1=1");
			
			if( !Utils.isNull(t.getOrderDateFrom()).equals("")
					&&	!Utils.isNull(t.getOrderDateTo()).equals("")	){	
				sql.append("\n and h.request_date >='"+DateToolsUtil.convertToTimeStamp(t.getOrderDateFrom())+"'");
				sql.append("\n and h.request_date <='"+DateToolsUtil.convertToTimeStamp(t.getOrderDateTo())+"' ");
			}
			
			sql.append("\n  and  h.user_id ='"+user.getId()+"'");
			sql.append("\n  ORDER BY h.move_order_type ,h.request_number asc  \n");
			
			logger.debug("sql: "+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());

			while (rst.next()) {	
				MoveOrderReport item = new MoveOrderReport();
				item.setMoveOrderType(rst.getString("move_order_type").equals("MoveOrderRequisition")?"เบิก":"คืน");
				if(rst.getString("move_order_type").equals("MoveOrderRequisition")){
					item.setPdCodeFrom(rst.getString("pd_code"));
					item.setPdCodeTo(rst.getString("sales_code"));
				}else{
					item.setPdCodeFrom(rst.getString("sales_code"));
					item.setPdCodeTo(rst.getString("pd_code"));
				}
				item.setQty1(rst.getDouble("qty1"));
				item.setQty2(rst.getDouble("qty2"));
				  
				item.setRequestNumber(rst.getString("request_number"));
				item.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setDocStatus("VO".equals(rst.getString("status"))?"ยกเลิก":"ใช้งาน");
				item.setInterfaces("Y".equals(rst.getString("exported"))?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				item.setTotalAmount(rst.getDouble("total_amount"));
				
				pos.add(item);
			
			}//while
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			
		}
		return pos;
	}
	
}
