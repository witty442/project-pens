package com.isecinc.pens.web.clearinvoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ClearInvoiceDAO {

	private Logger logger = Logger.getLogger("PENS");
	public static String codeControl = "1";

	  public List<ClearInvoice> search(ClearInvoice c,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<ClearInvoice> pos = new ArrayList<ClearInvoice>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
				sql.append("\n  select M.* from ( ");
				sql.append("\n  select  ");
				sql.append("\n  A.customer_id ,A.customer_code ,A.customer_name");
				sql.append("\n  , A.order_id , A.ar_invoice_no ");
				sql.append("\n  , A.net_amount ,A.paid_amount ,A.total_amount ,A.vat_amount");
				sql.append("\n  , A.remain_amount as receipt_remain_amount ");
				sql.append("\n  , (A.net_amount - A.paid_amount) as remain_amount ");
				sql.append("\n  from( ");
				sql.append("\n  	select o.order_id ,o.customer_id ");
				sql.append("\n      , (select m.code from m_customer m where m.customer_id = o.customer_id ) as customer_code ");
				sql.append("\n      , (select m.name from m_customer m where m.customer_id = o.customer_id ) as customer_name ");
				sql.append("\n  	,o.ar_invoice_no,o.total_amount ");
				sql.append("\n  	,o.vat_amount, o.net_amount ");
				sql.append("\n 	    ,( select SUM(rl.PAID_AMOUNT) as PAID_AMOUNT  ");
				sql.append("\n  	   from t_receipt_line rl ");
				sql.append("\n  	   where rl.order_id = o.order_id ");
				sql.append("\n  	   and o.DOC_STATUS = 'SV'  ");
				sql.append("\n  	   and rl.receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' )  ");
				sql.append("\n  	) as paid_amount ");
				sql.append("\n 	    ,( select SUM(rl.REMAIN_AMOUNT) as REMAIN_AMOUNT  ");
				sql.append("\n  	   from t_receipt_line rl ");
				sql.append("\n  	   where rl.order_id = o.order_id ");
				sql.append("\n  	   and o.DOC_STATUS = 'SV'  ");
				sql.append("\n  	   and rl.receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' )  ");
				sql.append("\n  	) as remain_amount ");
				sql.append("\n  	from t_order o  ");
				sql.append("\n  	where 1=1 ");
				sql.append("\n  	and interfaces = 'Y'  ");
				sql.append("\n  	and ar_invoice_no is not null   ");
				sql.append("\n  	and ar_invoice_no <> ''  "); 
				sql.append("\n  	and order_type = 'CR'   ");
				sql.append("\n  	and doc_status <> 'VO'  "); 
				sql.append("\n  	and customer_id in( ");
				sql.append("\n  		select customer_id from m_customer where isactive ='Y' ");
				sql.append("\n  	) ");
				
				if( !Utils.isNull(c.getOrderDateFrom()).equals("")
						&&	!Utils.isNull(c.getOrderDateTo()).equals("")	){
						
					sql.append("\n 		and order_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') ");
					sql.append("\n 		and order_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') ");
				}
				sql.append("\n  		and user_id ="+user.getId());
				
				if( !Utils.isNull(c.getOrderIdSqlIn()).equals("")){
					sql.append("\n     and order_id in ("+c.getOrderIdSqlIn()+")");
				}
				if( !Utils.isNull(c.getArInvoiceNo()).equals("")){
					sql.append("\n     and ar_invoice_no= '"+c.getArInvoiceNo()+"'");
				}
				
				sql.append("\n  )A ");
				sql.append("\n  where 1=1 ");
				//sql.append("\n  and  (A.net_amount - A.paid_amount) < 100  and (A.net_amount - A.paid_amount)  >1  ");
				if( !Utils.isNull(c.getCustomerCode()).equals("")){
					sql.append("\n  and A.customer_code ='"+c.getCustomerCode()+"'");
			    }
				sql.append("\n )M ");
				sql.append("\n WHERE 1=1");
				if( !Utils.isNull(c.getCondition()).equals("")){
					sql.append("\n and "+c.getCondition());
				}
				sql.append("\n  order by customer_id ");

				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					no++;
					ClearInvoice item = new ClearInvoice();
					item.setNo(no);
					item.setCustomerId( Utils.isNull(rst.getString("customer_id")));
					item.setCustomerCode( Utils.isNull(rst.getString("customer_code")));
					item.setCustomerName( Utils.isNull(rst.getString("customer_name")));
					item.setOrderId(Utils.isNull(rst.getString("order_id")));
					item.setArInvoiceNo( Utils.isNull(rst.getString("ar_invoice_no")));
					item.setTotalAmount(String.valueOf(rst.getDouble("total_amount")));
					item.setVatAmount(String.valueOf(rst.getDouble("vat_amount")));
					item.setNetAmount(String.valueOf(rst.getDouble("net_amount")));
					item.setPaidAmount(String.valueOf(rst.getDouble("paid_amount")));
					item.setRemainAmount(String.valueOf(rst.getDouble("receipt_remain_amount")));
					item.setRemainAmountCalc(String.valueOf(rst.getDouble("remain_amount")));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  public static int updatePaidAmountCheck(Connection conn,ClearInvoice item) throws Exception{
		  int r = 0;
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  StringBuffer sql = new StringBuffer("");
		  try{
			  /** Update record latest **/
			   sql.append("\n select max(receipt_line_id) as receipt_line_id from t_receipt_line  ");
			   sql.append("\n where order_id = "+item.getOrderId());
			   
			   ps =conn.prepareStatement(sql.toString());
			   rs = ps.executeQuery();
			   if(rs.next()){
				   item.setReceiptLineId(Utils.isNull(rs.getString("receipt_line_id")));
			   }
			   
			   r = updatePaidAmount(conn,item);
			   return r;
		  }catch(Exception e){
			  throw e;
		  }finally{
			  ps.close();
		  }
	  }
	  
	  public static int updatePaidAmount(Connection conn,ClearInvoice item) throws Exception{
		  int r = 0;
		  PreparedStatement ps = null;
		  StringBuffer sql = new StringBuffer("");
		  try{
			   sql.append("\n UPDATE t_receipt_line  set REMAIN_AMOUNT =0 ,paid_amount = (paid_amount+?) ");
			   sql.append("\n ,updated = ? ,updated_by = ?");
			   sql.append("\n where order_id = "+item.getOrderId());
			   sql.append("\n and receipt_line_id = "+item.getReceiptLineId());
			   sql.append("\n and receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' )  ");
			   
			   ps =conn.prepareStatement(sql.toString());
			   ps.setDouble(1, Utils.convertStrToDouble(item.getRemainAmountCalc()));
			   ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			   ps.setInt(3, 99);
			   
			   r = ps.executeUpdate();
			   
			   return r;
		  }catch(Exception e){
			  throw e;
		  }finally{
			  ps.close();
		  }
	  }
	 
}
