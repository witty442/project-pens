package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.stockonhand.StockOnhandBean;

import util.Utils;


public class StockCloseVanReport {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static StockCloseVanBean searchStockCloseVanList(Connection conn,StockBean o,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<StockCloseVanBean> dataList = new ArrayList<StockCloseVanBean>();
		StockCloseVanBean bean = null;
		int no= 0;
		double begin_pri_qty =0;
		double begin_sec_char=0;
		double sale_pri_qty =0;
		double sale_sec_char=0;
		double prem_pri_qty=0; 
		double prem_sec_char=0;
		double total_sale_pri_qty =0;
		double total_sale_sec_char=0;
		double rma_pri_qty=0;
		double rma_sec_char=0;
		double receipt_pri_qty =0;
		double receipt_sec_char=0;
		double return_pri_qty =0;
		double return_sec_char=0;
		double adj_issue_pri_qty =0;
		double adj_issue_sec_char=0;
		double adj_receipt_pri_qty =0;
		double adj_receipt_sec_char=0;
		double end_pri_qty =0;
		double end_sec_char=0;
		
		try {
			sql.append("\n  SELECT subinventory ");
			sql.append("\n  ,p.segment1 ,p.description,p.uom_code ");
			sql.append("\n  ,p.begin_pri_qty ,p.begin_sec_char");
			sql.append("\n  ,p.sale_pri_qty ,p.sale_sec_char");
			sql.append("\n  ,p.prem_pri_qty,p.prem_sec_char");
			sql.append("\n  ,p.total_sale_pri_qty,p.total_sale_sec_char");
			sql.append("\n  ,p.rma_pri_qty,p.rma_sec_char");
			sql.append("\n  ,p.receipt_pri_qty ,p.receipt_sec_char");
			sql.append("\n  ,p.return_pri_qty ,p.return_sec_char");
			sql.append("\n  ,p.adj_issue_pri_qty ,p.adj_issue_sec_char");
			sql.append("\n  ,p.adj_receipt_pri_qty ,p.adj_receipt_sec_char");
			sql.append("\n  ,p.end_pri_qty ,p.end_sec_char");
			sql.append("\n from( ");
			sql.append("\n   select * from ");
			sql.append("\n   table(xxpens_inv_closevan_pkg.get_qty('"+o.getPeriod()+"', '"+user.getUserName().toUpperCase()+"'))");
			sql.append("\n )p ");
			
			sql.append("\n  ORDER BY p.segment1 ");

			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				no++;
				bean = new StockCloseVanBean();
				bean.setNo(no);
				bean.setSegment1(Utils.isNull(rst.getString("segment1")));
				bean.setDescription(Utils.isNull(rst.getString("description")));
				bean.setUom_code(Utils.isNull(rst.getString("uom_code")));
				bean.setBegin_pri_qty(Utils.decimalFormat(rst.getDouble("begin_pri_qty"), Utils.format_current_no_disgit));
				bean.setBegin_sec_char(Utils.decimalFormat(rst.getDouble("begin_sec_char"), Utils.format_current_no_disgit));
				bean.setSale_pri_qty(Utils.decimalFormat(rst.getDouble("sale_pri_qty"), Utils.format_current_no_disgit));
				bean.setSale_sec_char(Utils.decimalFormat(rst.getDouble("sale_sec_char"), Utils.format_current_no_disgit));
				bean.setPrem_pri_qty(Utils.decimalFormat(rst.getDouble("prem_pri_qty"), Utils.format_current_no_disgit));
				bean.setPrem_sec_char(Utils.decimalFormat(rst.getDouble("prem_sec_char"), Utils.format_current_no_disgit));
				bean.setTotal_sale_pri_qty(Utils.decimalFormat(rst.getDouble("total_sale_pri_qty"), Utils.format_current_no_disgit));
				bean.setTotal_sale_sec_char(Utils.decimalFormat(rst.getDouble("total_sale_sec_char"), Utils.format_current_no_disgit));
				bean.setRma_pri_qty(Utils.decimalFormat(rst.getDouble("rma_pri_qty"), Utils.format_current_no_disgit));
				bean.setRma_sec_char(Utils.decimalFormat(rst.getDouble("rma_sec_char"), Utils.format_current_no_disgit));
				bean.setReceipt_pri_qty(Utils.decimalFormat(rst.getDouble("receipt_pri_qty"), Utils.format_current_no_disgit));
				bean.setReceipt_sec_char(Utils.decimalFormat(rst.getDouble("receipt_sec_char"), Utils.format_current_no_disgit));
				bean.setReturn_pri_qty(Utils.decimalFormat(rst.getDouble("return_pri_qty"), Utils.format_current_no_disgit));
				bean.setReturn_sec_char(Utils.decimalFormat(rst.getDouble("return_sec_char"), Utils.format_current_no_disgit));
				bean.setAdj_issue_pri_qty(Utils.decimalFormat(rst.getDouble("adj_issue_pri_qty"), Utils.format_current_no_disgit));
				bean.setAdj_issue_sec_char(Utils.decimalFormat(rst.getDouble("adj_issue_sec_char"), Utils.format_current_no_disgit));
				bean.setAdj_receipt_pri_qty(Utils.decimalFormat(rst.getDouble("adj_receipt_pri_qty"), Utils.format_current_no_disgit));
				bean.setAdj_receipt_sec_char(Utils.decimalFormat(rst.getDouble("adj_receipt_sec_char"), Utils.format_current_no_disgit));
				
				bean.setEnd_pri_qty(Utils.decimalFormat(rst.getDouble("end_pri_qty"), Utils.format_current_no_disgit));
				bean.setEnd_sec_char(Utils.decimalFormat(rst.getDouble("end_sec_char"), Utils.format_current_no_disgit));
				dataList.add(bean);
				
				//summary
				 begin_pri_qty +=rst.getDouble("begin_pri_qty");
				 begin_sec_char+=rst.getDouble("begin_sec_char");
				 sale_pri_qty +=rst.getDouble("sale_pri_qty");
				 sale_sec_char+=rst.getDouble("sale_sec_char");
				 prem_pri_qty+=rst.getDouble("prem_pri_qty"); 
				 prem_sec_char+=rst.getDouble("prem_sec_char");
				 total_sale_pri_qty +=rst.getDouble("total_sale_pri_qty");
				 total_sale_sec_char+=rst.getDouble("total_sale_sec_char");
				 rma_pri_qty+=rst.getDouble("rma_pri_qty");
				 rma_sec_char+=rst.getDouble("rma_sec_char");
				 receipt_pri_qty +=rst.getDouble("receipt_pri_qty");
				 receipt_sec_char+=rst.getDouble("receipt_sec_char");
				 return_pri_qty +=rst.getDouble("return_pri_qty");
				 return_sec_char+=rst.getDouble("return_sec_char");
				 adj_issue_pri_qty +=rst.getDouble("adj_issue_pri_qty");
				 adj_issue_sec_char+=rst.getDouble("adj_issue_sec_char");
				 adj_receipt_pri_qty +=rst.getDouble("adj_receipt_pri_qty");
				 adj_receipt_sec_char+=rst.getDouble("adj_receipt_sec_char");
				 end_pri_qty +=rst.getDouble("end_pri_qty");
				 end_sec_char+=rst.getDouble("end_sec_char");
			}//while
			
			//sum
			bean  = new StockCloseVanBean();
			bean.setBegin_pri_qty(Utils.decimalFormat(begin_pri_qty, Utils.format_current_no_disgit));
			bean.setBegin_sec_char(Utils.decimalFormat(begin_sec_char, Utils.format_current_no_disgit));
			bean.setSale_pri_qty(Utils.decimalFormat(sale_pri_qty, Utils.format_current_no_disgit));
			bean.setSale_sec_char(Utils.decimalFormat(sale_sec_char, Utils.format_current_no_disgit));
			bean.setPrem_pri_qty(Utils.decimalFormat(prem_pri_qty, Utils.format_current_no_disgit));
			bean.setPrem_sec_char(Utils.decimalFormat(prem_sec_char, Utils.format_current_no_disgit));
			bean.setTotal_sale_pri_qty(Utils.decimalFormat(total_sale_pri_qty, Utils.format_current_no_disgit));
			bean.setTotal_sale_sec_char(Utils.decimalFormat(total_sale_sec_char, Utils.format_current_no_disgit));
			bean.setRma_pri_qty(Utils.decimalFormat(rma_pri_qty, Utils.format_current_no_disgit));
			bean.setRma_sec_char(Utils.decimalFormat(rma_sec_char, Utils.format_current_no_disgit));
			bean.setReceipt_pri_qty(Utils.decimalFormat(receipt_pri_qty, Utils.format_current_no_disgit));
			bean.setReceipt_sec_char(Utils.decimalFormat(receipt_sec_char, Utils.format_current_no_disgit));
			bean.setReturn_pri_qty(Utils.decimalFormat(return_pri_qty, Utils.format_current_no_disgit));
			bean.setReturn_sec_char(Utils.decimalFormat(return_sec_char, Utils.format_current_no_disgit));
			bean.setAdj_issue_pri_qty(Utils.decimalFormat(adj_issue_pri_qty, Utils.format_current_no_disgit));
			bean.setAdj_issue_sec_char(Utils.decimalFormat(adj_issue_sec_char, Utils.format_current_no_disgit));
			bean.setAdj_receipt_pri_qty(Utils.decimalFormat(adj_receipt_pri_qty, Utils.format_current_no_disgit));
			bean.setAdj_receipt_sec_char(Utils.decimalFormat(adj_receipt_sec_char, Utils.format_current_no_disgit));
			bean.setEnd_pri_qty(Utils.decimalFormat(end_pri_qty, Utils.format_current_no_disgit));
			bean.setEnd_sec_char(Utils.decimalFormat(end_sec_char, Utils.format_current_no_disgit));
			bean.setItemsList(dataList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return bean;
	}
}
