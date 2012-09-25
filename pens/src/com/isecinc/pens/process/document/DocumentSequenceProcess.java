package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.CustomerSequence;
import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MCustomerSequence;
import com.isecinc.pens.model.MDocSequence;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.process.SequenceProcess;

/**
 * DocumentSequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: DocumentSequenceProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 *          atiz.b : edit for dd sequence
 * 
 */
public abstract class DocumentSequenceProcess {

	protected Logger logger = Logger.getLogger("PENS");

	protected static final int CUSTOMER_NUMBER = 100;
	protected static final int MEMBER_NUMBER = 200;
	protected static final int ORDER_NUMNER = 300;
	protected static final int RECEIPT_NUMNER = 400;
	protected static final int VISIT_NUMNER = 500;
	
	protected static final int MOVE_ORDER_REQ_NUMBER = 600;
	protected static final int MOVE_ORDER_RETURN_NUMBER = 700;
	
	protected static final int MAX_SEQ_NO = 9999;
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", new Locale("th", "TH"));

	protected DocSequence docSequence;

	/**
	 * Get Next Doc No...
	 * 
	 * @param activeUserId
	 * @return
	 * @throws Exception
	 */
	public abstract String getNextDocumentNo(String salesCode, String prefix, int activeUserID, Connection conn)
			throws Exception;

	/**
	 * Get Next Sequence
	 * 
	 * @param salesCode
	 * @param docType
	 * @param activeUserId
	 * @return
	 * @throws Exception
	 * reset by day or month or year
	 */
	protected int getNextSeq(String salesCode, int docTypeId, int activeUserID, Connection conn) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		try {
			String today = df.format(new Date());
			String[] d1 = today.split("/");
			curYear = d1[0];
			curMonth = Integer.parseInt(d1[1]);
			
			User user = new MUser().find(String.valueOf(activeUserID));
			
			// get order type
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n  AND doctype_id = " + docTypeId);
			whereCause.append("\n  AND current_month = " + curMonth);
			whereCause.append("\n  AND current_year = " + curYear);
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + salesCode + "' ");
			}
			if (user.getType().equalsIgnoreCase(User.DD)) {
				whereCause.append("\n AND sales_code = '' ");
			}

			System.out.println("sql:"+whereCause.toString());
			
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
			if (seq == null){
				//throw new Exception(
						//"\u0E01\u0E23\u0E38\u0E13\u0E32\u0E2A\u0E23\u0E49\u0E32\u0E07\u0E40\u0E25\u0E02\u0E17\u0E35\u0E48\u0E40\u0E2D\u0E01\u0E2A\u0E32\u0E23\u0E01\u0E48\u0E2D\u0E19\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E23\u0E32\u0E22\u0E01\u0E32\u0E23...");
			
				System.out.println("No set update : insert new Record ");
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + salesCode + "' ");
				}
				if (user.getType().equalsIgnoreCase(User.DD)) {
					whereCause.append("\n AND sales_code = '' ");
				}
				DocSequence[] docSeqFindOrderType = new MDocSequence().search(whereCause.toString());
				
				String orderType = "MM";//Default MM(month) ->DD or MM or YY
				if(docSeqFindOrderType != null){
					docSeqFindOrderType[0].getOrderType();
				}
				
				nextValue = 1;
				DocSequence docSeq = new DocSequence();
				docSeq.setId(0);
				docSeq.setOrderType(orderType); //DEFALUT
				docSeq.setCurrentYear(curYear);
				docSeq.setCurrentMonth(String.valueOf(curMonth));
				docSeq.setStartNo(1);
				docSeq.setCurrentNext(docSeq.getStartNo()+1);
				docSeq.setDoctypeID(docTypeId);
				docSeq.setActive("Y");
				docSeq.setSalesCode(salesCode);
				
				// insert here
				System.out.println("Insert ");
				new MDocSequence().saveNew(docSeq, activeUserID, conn);
				
				docSequence = docSeq;
				
				return nextValue;
				
			}else{
			
				String orderType = seq[0].getOrderType();
	
				// reset by DD
				if (orderType.equalsIgnoreCase("DD")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
							&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				// reset by MM
				if (orderType.equalsIgnoreCase("MM")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
							&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				// reset by YY
				if (orderType.equalsIgnoreCase("YY")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				if (newSeq) {
					// start new seq with current month/year
					nextValue = seq[0].getStartNo();
					seq[0].setId(0);
					seq[0].setOrderType(orderType);
					seq[0].setCurrentYear(curYear);
					seq[0].setCurrentMonth(String.valueOf(curMonth));
					seq[0].setCurrentNext(seq[0].getStartNo()+1);//start 1
					
					// insert here
					System.out.println("Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					nextValue = seq[0].getCurrentNext();
					seq[0].setCurrentNext(seq[0].getCurrentNext() + 1);
					// update here
					System.out.println("Update ");
					new MDocSequence().update(seq[0], activeUserID, conn);
				}
	
				docSequence = seq[0];
			}
			return nextValue;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param salesCode
	 * @param docTypeId
	 * @param activeUserID
	 * @return
	 * @throws Exception
	 * desc : get Next Seq and update next Seq 
	 */
	protected int getNextSeq(String salesCode, int docTypeId, int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String today = df.format(new Date());
			String[] d1 = today.split("/");
			curYear = d1[0];
			curMonth = Integer.parseInt(d1[1]);
			
			User user = new MUser().find(String.valueOf(activeUserID));
			
			// get order type
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n  AND doctype_id = " + docTypeId);
			whereCause.append("\n  AND current_month = " + curMonth);
			whereCause.append("\n  AND current_year = " + curYear);
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + salesCode + "' ");
			}
			if (user.getType().equalsIgnoreCase(User.DD)) {
				whereCause.append("\n AND sales_code = '' ");
			}

			System.out.println("sql:"+whereCause.toString());
			
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
			if (seq == null){
				//throw new Exception(
						//"\u0E01\u0E23\u0E38\u0E13\u0E32\u0E2A\u0E23\u0E49\u0E32\u0E07\u0E40\u0E25\u0E02\u0E17\u0E35\u0E48\u0E40\u0E2D\u0E01\u0E2A\u0E32\u0E23\u0E01\u0E48\u0E2D\u0E19\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E23\u0E32\u0E22\u0E01\u0E32\u0E23...");
			
				
				System.out.println("No set update : insert new Record ");
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + salesCode + "' ");
				}
				if (user.getType().equalsIgnoreCase(User.DD)) {
					whereCause.append("\n AND sales_code = '' ");
				}
				DocSequence[] docSeqFindOrderType = new MDocSequence().search(whereCause.toString());
				String orderType = "MM";//Default MM(month) ->DD or MM or YY
				if(docSeqFindOrderType != null){
					docSeqFindOrderType[0].getOrderType();
				}
				
				nextValue = 1;
				DocSequence docSeq = new DocSequence();
				docSeq.setId(getNexSeqAndChkDuplicate(conn, "c_doctype_sequence", "doctype_sequence_id", 0));
				docSeq.setOrderType(orderType); //DEFALUT
				docSeq.setCurrentYear(curYear);
				docSeq.setCurrentMonth(String.valueOf(curMonth));
				docSeq.setStartNo(1);
				docSeq.setCurrentNext(docSeq.getStartNo()+1);
				docSeq.setDoctypeID(docTypeId);
				docSeq.setActive("Y");
				docSeq.setSalesCode(salesCode);
				
				// insert here
				System.out.println("Insert ");
				new MDocSequence().saveNew(docSeq, activeUserID, conn);
				
				docSequence = docSeq;
				
				return nextValue;
				
			}else{
			
				String orderType = seq[0].getOrderType();
	
				// reset by DD
				if (orderType.equalsIgnoreCase("DD")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
							&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				// reset by MM
				if (orderType.equalsIgnoreCase("MM")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
							&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				// reset by YY
				if (orderType.equalsIgnoreCase("YY")) {
					if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())) {
						newSeq = false;
					} else {
						newSeq = true;
					}
				}
	
				if (newSeq) {
					// start new seq with current month/year
					nextValue = seq[0].getStartNo();
					seq[0].setId(getNexSeqAndChkDuplicate(conn, "c_doctype_sequence", "doctype_sequence_id", 0));
					seq[0].setOrderType(orderType);
					seq[0].setCurrentYear(curYear);
					seq[0].setCurrentMonth(String.valueOf(curMonth));
					seq[0].setCurrentNext(seq[0].getStartNo()+1);//start 1
					
					// insert here
					System.out.println("Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					nextValue = seq[0].getCurrentNext();
					seq[0].setCurrentNext(seq[0].getCurrentNext() + 1);
					// update here
					System.out.println("Update ");
					new MDocSequence().update(seq[0], activeUserID, conn);
				}
	
				docSequence = seq[0];
			}
			return nextValue;
		} catch (Exception e) {
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
		}
	}

	
public int getNexSeqAndChkDuplicate(Connection conn,String tableName,String columnSeqName ,int currSeq) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		try {
			currSeq = SequenceProcess.getNextValue(tableName);//Add Next Seq
			stmt = conn.createStatement();
			String sql = "SELECT "+columnSeqName+" FROM "+tableName;
			sql += " WHERE "+columnSeqName+"=" + currSeq + " ";
			
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				return getNexSeqAndChkDuplicate(conn,tableName,columnSeqName,currSeq);
			}else{
				return currSeq;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e) {}
		}
	}
	/**
	 * Get Next Sequence
	 * 
	 * @param territory
	 * @param province
	 * @param district
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected int getNextSeq(String territory, String province, String district, int activeUserID, Connection conn)
			throws Exception {

		int currentNext = 1;
		try {
			// User user = new MUser().find(String.valueOf(activeUserID));
			// get order type
			String whereCause = "";
			whereCause += "  AND territory = '" + territory + "' ";
			whereCause += "  AND province = '" + province + "' ";
			whereCause += "  AND district = '" + district + "' ";

			CustomerSequence[] seq = new MCustomerSequence().search(whereCause);
			boolean bnew = false;
			if (seq == null) bnew = true;

			CustomerSequence customerSequence;
			if (bnew) {
				currentNext = 1;
				customerSequence = new CustomerSequence();
				customerSequence.setTerritory(territory);
				customerSequence.setProvince(province);
				customerSequence.setDistrict(district);
				customerSequence.setCurrentNext(currentNext + 1);

				new MCustomerSequence().save(customerSequence, activeUserID, conn);
			} else {
				customerSequence = seq[0];
				currentNext = customerSequence.getCurrentNext();
				customerSequence.setCurrentNext(currentNext + 1);
				// update here
				new MCustomerSequence().update(customerSequence, activeUserID, conn);
			}

			return currentNext;
		} catch (Exception e) {
			throw e;
		}
	}
}
