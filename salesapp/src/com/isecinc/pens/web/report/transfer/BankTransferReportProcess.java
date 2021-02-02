package com.isecinc.pens.web.report.transfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.ReportHelper;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MSalesTargetNew;
import com.pens.util.DateToolsUtil;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class BankTransferReportProcess extends I_ReportProcess<BankTransferReport> {

	public List<BankTransferReport> doReport(BankTransferReport t, User user, Connection conn) throws Exception {
		return null;
	}
	/**
	 * Search for performance report.
	 */
	public BankTransferReport getData(BankTransferReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		BankTransferReport head = new BankTransferReport();
		List<BankTransferReport> pos = new ArrayList<BankTransferReport>();
		BankTransferReport m = null;
		StringBuilder sql = new StringBuilder();
        double totalAmount = 0;
		try {
			sql.delete(0, sql.length());
			sql.append("\n SELECT od.* ");
			sql.append("\n  ,( SELECT r.name  from c_reference r");
			sql.append("\n     where R.CODE='"+InitialReferences.TRANSFER_BANK_VAN+"'" );
			sql.append("\n     and r.value = od.transfer_bank) as transfer_bank_label ");
			sql.append("\n  FROM t_bank_transfer od ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  AND od.CREATE_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getCreateDate()) + "' ");
			sql.append("\n  AND od.USER_ID = " + user.getId());
			sql.append("\n  ORDER BY od.line_ID ASC ");
			logger.info("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			int i = 1;
			while (rst.next()) {
			  m = new BankTransferReport();
			  m.setTransferType(Utils.isNull(rst.getString("transfer_type")));
			  if("CS".equalsIgnoreCase(m.getTransferType())){
				  m.setTransferType("à§Ô¹Ê´");
			  }else{
				  m.setTransferType("àªç¤"); 
			  }
			  m.setTransferBank(Utils.isNull(rst.getString("transfer_bank_label")));
			  m.setTransferDate(Utils.stringValue(rst.getDate("transfer_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setTransferTime(Utils.isNull(rst.getString("transfer_time")));
			  
			  m.setAmount(Utils.decimalFormat(rst.getDouble("amount"),Utils.format_current_2_disgit));
			  m.setAmountDouble(rst.getDouble("amount"));
			  m.setChequeNo(Utils.isNull(rst.getString("cheque_no")));
			  m.setChequeDate(Utils.stringValueNull(rst.getDate("cheque_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setCreateDate(Utils.stringValue(rst.getDate("create_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  totalAmount += rst.getDouble("amount");
			  pos.add(m);
			}
			
			head.setTotalAmount(Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit));
			head.setLstData(pos);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}

		return head;
	}

	
}
