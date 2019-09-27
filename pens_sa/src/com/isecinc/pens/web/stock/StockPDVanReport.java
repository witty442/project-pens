package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.stockonhand.StockOnhandBean;
import com.pens.util.Utils;


public class StockPDVanReport {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static StockPDVanBean searchStockPDVanList(Connection conn,StockBean o,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<StockPDVanBean> dataList = new ArrayList<StockPDVanBean>();
		StockPDVanBean bean = null;
		String pdDesc = "";
		int no= 0;
		double begin_pri_qty =0;
		double begin_sec_char=0;
		double receipt_pri_qty =0;
		double receipt_sec_char=0;
		double transact_pri_qty =0;
		double transact_sec_char=0;
		double return_pri_qty =0;
		double return_sec_char=0;
		double van_pri_qty =0;
		double van_sec_char=0;
		double end_pri_qty =0;
		double end_sec_char=0;
		try {
			sql.append("\n  SELECT subinventory,name ");
			sql.append("\n  ,p.segment1 ,p.description,p.uom_code ");
			sql.append("\n  ,p.begin_pri_qty ,p.begin_sec_char");
			sql.append("\n  ,p.receipt_pri_qty ,p.receipt_sec_char");
			sql.append("\n  ,p.transact_pri_qty,p.transact_sec_char");
			sql.append("\n  ,p.return_pri_qty ,p.return_sec_char");
			sql.append("\n  ,p.van_pri_qty ,p.van_sec_char");
			sql.append("\n  ,p.end_pri_qty ,p.end_sec_char");
			sql.append("\n from( ");
			sql.append("\n   select * from ");
			sql.append("\n   table(xxpens_inv_closepd_pkg.get_qty('"+o.getPeriod()+"', '"+o.getPdCode()+"'))");
			sql.append("\n )p ");
			sql.append("\n  ORDER BY p.segment1 ");

			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				no++;
				bean = new StockPDVanBean();
				pdDesc = Utils.isNull(rst.getString("name"));
				bean.setNo(no);
				bean.setSegment1(Utils.isNull(rst.getString("segment1")));
				bean.setDescription(Utils.isNull(rst.getString("description")));
				bean.setUom_code(Utils.isNull(rst.getString("uom_code")));
				bean.setBegin_pri_qty(Utils.decimalFormat(rst.getDouble("begin_pri_qty"), Utils.format_current_no_disgit));
				bean.setBegin_sec_char(Utils.decimalFormat(rst.getDouble("begin_sec_char"), Utils.format_current_no_disgit));
				bean.setReceipt_pri_qty(Utils.decimalFormat(rst.getDouble("receipt_pri_qty"), Utils.format_current_no_disgit));
				bean.setReceipt_sec_char(Utils.decimalFormat(rst.getDouble("receipt_sec_char"), Utils.format_current_no_disgit));
				bean.setTransact_pri_qty(Utils.decimalFormat(rst.getDouble("transact_pri_qty"), Utils.format_current_no_disgit));
				bean.setTransact_sec_char(Utils.decimalFormat(rst.getDouble("transact_sec_char"), Utils.format_current_no_disgit));
				bean.setReturn_pri_qty(Utils.decimalFormat(rst.getDouble("return_pri_qty"), Utils.format_current_no_disgit));
				bean.setReturn_sec_char(Utils.decimalFormat(rst.getDouble("return_sec_char"), Utils.format_current_no_disgit));
				bean.setVan_pri_qty(Utils.decimalFormat(rst.getDouble("van_pri_qty"), Utils.format_current_no_disgit));
				bean.setVan_sec_char(Utils.decimalFormat(rst.getDouble("van_sec_char"), Utils.format_current_no_disgit));
				bean.setEnd_pri_qty(Utils.decimalFormat(rst.getDouble("end_pri_qty"), Utils.format_current_no_disgit));
				bean.setEnd_sec_char(Utils.decimalFormat(rst.getDouble("end_sec_char"), Utils.format_current_no_disgit));
				dataList.add(bean);
				
				//summary
				begin_pri_qty +=rst.getDouble("begin_pri_qty");
				begin_sec_char+=rst.getDouble("begin_sec_char");
				
				receipt_pri_qty +=rst.getDouble("receipt_pri_qty");
				receipt_sec_char+=rst.getDouble("receipt_sec_char");
				transact_pri_qty +=rst.getDouble("transact_pri_qty");
				transact_sec_char+=rst.getDouble("transact_sec_char");
				return_pri_qty +=rst.getDouble("return_pri_qty");
				return_sec_char+=rst.getDouble("return_sec_char");
				van_pri_qty +=rst.getDouble("van_pri_qty");
				van_sec_char+=rst.getDouble("van_sec_char");
				
				end_pri_qty +=rst.getDouble("end_pri_qty");
				end_sec_char+=rst.getDouble("end_sec_char");
			}//while
			
			//sum
			bean  = new StockPDVanBean();
			bean.setPdDesc(pdDesc);
			bean.setBegin_pri_qty(Utils.decimalFormat(begin_pri_qty, Utils.format_current_no_disgit));
			bean.setBegin_sec_char(Utils.decimalFormat(begin_sec_char, Utils.format_current_no_disgit));
			bean.setReceipt_pri_qty(Utils.decimalFormat(receipt_pri_qty, Utils.format_current_no_disgit));
			bean.setReceipt_sec_char(Utils.decimalFormat(receipt_sec_char, Utils.format_current_no_disgit));
			bean.setTransact_pri_qty(Utils.decimalFormat(transact_pri_qty, Utils.format_current_no_disgit));
			bean.setTransact_sec_char(Utils.decimalFormat(transact_sec_char, Utils.format_current_no_disgit));
			bean.setReturn_pri_qty(Utils.decimalFormat(return_pri_qty, Utils.format_current_no_disgit));
			bean.setReturn_sec_char(Utils.decimalFormat(return_sec_char, Utils.format_current_no_disgit));
			bean.setVan_pri_qty(Utils.decimalFormat(van_pri_qty, Utils.format_current_no_disgit));
			bean.setVan_sec_char(Utils.decimalFormat(van_sec_char, Utils.format_current_no_disgit));
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
