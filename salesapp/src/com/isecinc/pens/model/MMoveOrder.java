package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.MoveOrderReqDocumentProcess;
import com.isecinc.pens.process.document.MoveOrderReturnDocumentProcess;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;

public class MMoveOrder {

	private Logger logger = Logger.getLogger("PENS");
	public static String MOVE_ORDER_REQUISITION ="MoveOrderRequisition";
    public static String MOVE_ORDER_RETURN ="MoveOrderReturn";
    
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	public static String ORG_ID_DEFAULT = "84";
	
	public static Map<String, String> MOVE_ORDER_TYPE_MAP = new HashMap<String, String>();
    static{
    	MOVE_ORDER_TYPE_MAP.put(MOVE_ORDER_REQUISITION, "ใบเบิกสินค้า");
    	MOVE_ORDER_TYPE_MAP.put(MOVE_ORDER_RETURN, "ใบคืนสินค้า");
    }
    private static double totalAmount = 0;
    
	public MoveOrder save(User user,MoveOrder head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//Generate MoveOrderNo
			String requestNumber  ="";
			BigDecimal createdLong = DateUtil.getCurrentTimestampLong();
			BigDecimal updatedLong = DateUtil.getCurrentTimestampLong();
		
			if("".equals(head.getRequestNumber())){
				
				//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
				head = checkRequestDate(head);
				Date requestDate = DateUtil.parse(head.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				
				if(MOVE_ORDER_REQUISITION.equals(head.getMoveOrderType())){
				    requestNumber = new MoveOrderReqDocumentProcess().getNextDocumentNo(requestDate,user.getCode(), head.getPdCode(),user.getId(), conn);
				}else if(MOVE_ORDER_RETURN.equals(head.getMoveOrderType())){
				    requestNumber = new MoveOrderReturnDocumentProcess().getNextDocumentNo(requestDate,user.getCode(), head.getPdCode(),user.getId(), conn);
				}
				
				//prepare MoveOrder
				logger.debug("MoveOrderType["+head.getMoveOrderType()+"]requestNumber["+requestNumber+"]");
				//prepare MoveOrder Model
				head.setRequestNumber(requestNumber);

				head = insertMoveOrder(conn , head);
				
			}else{
				//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
				head = checkRequestDate(head);
				
				updateMoveOrder(conn , head);
			}
	
			if(head.getMoveOrderLineList() != null && head.getMoveOrderLineList().size() > 0){
				
				//delete lines
				//deleteOrderLineByRequestNumber(conn, head);
				// Process normal
				for(int i =0;i< head.getMoveOrderLineList().size();i++){
					MoveOrderLine line = (MoveOrderLine)head.getMoveOrderLineList().get(i);
					
					MoveOrderLine findLine = checkLineExist(conn, head.getRequestNumber(), line.getLineNo());
					if(findLine != null ){
						//Update
					    line.setQty(calcQty(line));
					    line.setPack(calcPack(line));
						line.setUpdatedLong(updatedLong);
						line.setUpdateBy(head.getCreatedBy());
						/** Check Line Change **/
						if(line.getQty() != findLine.getQty()){
						   updateMoveOrderLine(conn,head, line);
						}
						
					}else{
						
						//Insert 
						line.setLineNo(getNextLineNo(conn, head.getRequestNumber()));
						line.setQty(calcQty(line));
						line.setPack(calcPack(line));
						line.setCreatedLong(head.getCreatedLong()==null?createdLong:head.getCreatedLong());
						line.setCreatedBy(head.getCreatedBy());
		
						insertMoveOrderLine(conn,head, line);
					}
				}//for
			}
			
			if(head.getLineNoDeleteList() != null && head.getLineNoDeleteList().size() > 0){
				for(int i =0;i< head.getLineNoDeleteList().size();i++){
					int lineNo = Integer.parseInt((String)head.getLineNoDeleteList().get(i));
					logger.debug("lineNo For Delete ["+lineNo+"]");
					MoveOrderLine findLine = checkLineExist(conn, head.getRequestNumber(), lineNo);
					if(findLine != null){
						//Update Delete Line Flag
						MoveOrderLine line = new MoveOrderLine();
						line.setRequestNumber(head.getRequestNumber());
						line.setLineNo(lineNo);
						line.setStatus(STATUS_VOID);
						line.setUpdatedLong(updatedLong);
						line.setUpdateBy(head.getCreatedBy());
						
						updateMoveOrderLineCaseLineCancel(conn,head, line);
						
					}
				}//for
			}
			
			conn.commit();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return head;
	}
	
	public static void main(String[] ar){
		try{
			Calendar currentDate = Calendar.getInstance();
			
			String requestDateStr = "01/12/2555";
			Date requestDateObj = DateUtil.parse(requestDateStr, DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			Calendar requestDate = Calendar.getInstance();
			requestDate.setTime(requestDateObj);
			int dayInMonthOfRequestDate = requestDate.get(Calendar.DATE);
			
			Calendar monthEndDate = Calendar.getInstance();
			int lastDayInMonthOfCurrentDate = monthEndDate.getActualMaximum(Calendar.DATE);
			
			//diff day 
			int diffDayRequestDateToMonthEndDate = lastDayInMonthOfCurrentDate - dayInMonthOfRequestDate;
			
			System.out.println("currentDate:"+currentDate);
			System.out.println("requestDate:"+requestDate.getTime());
			System.out.println("monthEndDate:"+monthEndDate.getTime());
			System.out.println("lastDayInMonthOfCurrentDate:"+lastDayInMonthOfCurrentDate);
			System.out.println("dayInMonthOfRequestDate:"+dayInMonthOfRequestDate);
			
			System.out.println("diffDayRequestDateToMonthEndDate:"+diffDayRequestDateToMonthEndDate);
			
			//int diffDay <=1 
			if(diffDayRequestDateToMonthEndDate <= 1){
				System.out.println("set request Date to 01/nextMonth/nextYear");
				currentDate.add(Calendar.MONTH, 1);//next Month or NextYear 
				currentDate.set(Calendar.DATE, 1);//set to 01/xx/xxxx
				
				System.out.println("requestDate :"+currentDate.getTime());
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
	private MoveOrder checkRequestDate(MoveOrder head) {
		try{
			List<References> refList = InitialReferences.getReferenes().get(InitialReferences.MOVEORDER);
			//logger.info("Integer.parseInt(refList.get(0).getKey()):"+Integer.parseInt(refList.get(0).getKey()));
			int diffDayToEndConfig = refList != null? Integer.parseInt(refList.get(0).getKey())-1:1;
			
            Calendar currentDate = Calendar.getInstance();
			
			Date requestDateObj = DateUtil.parse(head.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			Calendar requestDate = Calendar.getInstance();
			requestDate.setTime(requestDateObj);
			int dayInMonthOfRequestDate = requestDate.get(Calendar.DATE);
			
			Calendar monthEndDate = Calendar.getInstance();
			int lastDayInMonthOfCurrentDate = monthEndDate.getActualMaximum(Calendar.DATE);
			
			//diff day 
			int diffDayRequestDateToMonthEndDate = lastDayInMonthOfCurrentDate - dayInMonthOfRequestDate;
			
			System.out.println("currentDate:"+currentDate);
			System.out.println("requestDate:"+requestDate.getTime());
			System.out.println("monthEndDate:"+monthEndDate.getTime());
			System.out.println("lastDayInMonthOfCurrentDate:"+lastDayInMonthOfCurrentDate);
			System.out.println("dayInMonthOfRequestDate:"+dayInMonthOfRequestDate);
			
			System.out.println("diffDayRequestDateToMonthEndDate:"+diffDayRequestDateToMonthEndDate);
			
			//int diffDay <=1 
			if(diffDayRequestDateToMonthEndDate <= diffDayToEndConfig){
				System.out.println("set request Date to 01/nextMonth/nextYear");
				currentDate.add(Calendar.MONTH, 1);//next Month or NextYear 
				currentDate.set(Calendar.DATE, 1);//set to 01/xx/xxxx
				
				String requestDateStr = DateUtil.stringValue(currentDate.getTime(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				head.setRequestDate(requestDateStr);
				System.out.println("requestDate :"+head.getRequestDate());
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return head;
	}
	
	
	private double calcQty(MoveOrderLine line) throws Exception{
		double priQty = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(line.getProduct().getId(), line.getUom1().getId());
	    UOMConversion  uc2 = null;
	    if( !Utils.isNull(line.getUom2().getId()).equals("")){
	        uc2 = new MUOMConversion().getCurrentConversion(line.getProduct().getId(), line.getUom2().getId());
	        logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
	        
	        if(uc2.getConversionRate() > 0){
	        	double qty2Temp = line.getQty2()/ (uc1.getConversionRate()/uc2.getConversionRate()) ;
	        	//convert to Str 1.666667
				String qty2Str5Digit = NumberToolsUtil.decimalFormat(qty2Temp, NumberToolsUtil.format_current_6_digit);
				//substr remove digit no 6 :"7" -> 1.66666
				qty2Str5Digit = qty2Str5Digit.substring(0,qty2Str5Digit.length()-1);
				
				double pcsQty = Double.parseDouble((qty2Str5Digit));
	        	priQty = line.getQty1()  +pcsQty;
	        }else{
	        	priQty = line.getQty1();
	        }
	    }else{
	    	//No Qty2 ,UOM2
	    	//uc2 = new MUOMConversion().getCurrentConversionNotIn(line.getProduct().getId(), line.getUom1().getId());
	    	//logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
		    priQty = line.getQty1();
	    }
	    logger.debug("result calc qty["+priQty+"]");
	    return priQty;
	}

	private double calcPack(MoveOrderLine line) throws Exception{
		double pack = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(line.getProduct().getId(), line.getUom1().getId());
	    UOMConversion  uc2 = null;
	    
	    if( !Utils.isNull(line.getUom2().getId()).equals("")){
	    	logger.debug("case 1 uom1,uom2 not null");
	        uc2 = new MUOMConversion().getCurrentConversion(line.getProduct().getId(), line.getUom2().getId());
	        pack =  uc1.getConversionRate()/uc2.getConversionRate() ;
	        
	    }else{
	    	logger.debug("case 1 uom2 is null");
	    	uc2 = new MUOMConversion().getCurrentConversionNotIn(line.getProduct().getId(), line.getUom1().getId());
	    	
	    	if(uc2 != null && uc2.getConversionRate() != 0){
	    	   pack =  uc1.getConversionRate()/uc2.getConversionRate() ;
	    	}else{
	    	   pack = uc1.getConversionRate();
	    	}
	    }
	    logger.debug("result calc pack["+pack+"]");
	    return pack;
	}
	
	public MoveOrder updateMoveOrder(MoveOrder head) throws Exception {
		Connection conn = null;
		try {
		    conn = new DBCPConnectionProvider().getConnection(conn);
		    conn.setAutoCommit(false);
		    
		    updateMoveOrder(conn,head);
		    
		    conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if(conn != null){
				conn.close();conn = null;
			}
		}
		return head;
	}
	
	private MoveOrder updateMoveOrder(Connection conn ,MoveOrder head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_move_order \n");
			sql.append(" set  request_date = ?  \n"); 
			sql.append(" ,updated_long =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(head.getRequestDate()).getTime()));//request_date
			ps.setBigDecimal(++index, head.getUpdatedLong().setScale(6));//updated
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setString(++index, head.getRequestNumber());//request_number

			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return head;
	}
	
	public MoveOrder updateCancelMoveOrder(MoveOrder head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_move_order \n");
			sql.append(" set  status = ?  ,description = ? \n"); 
			sql.append(" ,updated_long =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//status
			ps.setString(++index, head.getDescription());//status
			ps.setBigDecimal(++index, head.getUpdatedLong().setScale(6));//updated
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setString(++index, head.getRequestNumber());//request_number

			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(conn != null){
				conn.close();conn = null;
			}
		}
		return head;
	}
	
	public MoveOrder updatePrintMoveOrder(MoveOrder head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn  = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_move_order \n");
			sql.append(" set print_no = (print_no +1) ,print_date_long = ? ,  \n"); 
			sql.append(" updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setBigDecimal(++index, head.getPrintDateLong().setScale(6));//print_date
			ps.setString(++index, head.getUpdateBy());//update_dby
			ps.setString(++index, head.getRequestNumber());//request_number

			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(conn != null){
			   conn.close();conn = null;
			}
		}
		return head;
	}
	
	private MoveOrder insertMoveOrder(Connection conn ,MoveOrder model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_move_order( \n");
			sql.append(" request_number, request_date, organization_id, \n");
			sql.append(" sales_code, pd_code, description, \n");
			sql.append(" move_order_type, status, print_no,  \n");
			sql.append(" exported, USER_ID, CREATED_LONG,CREATED_BY ) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, model.getRequestNumber());//request_number
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getRequestDate()).getTime()));//request_date
			ps.setInt(++index, Utils.convertStrToInt(model.getOrganizationId()));//organization_id
			ps.setString(++index, model.getSalesCode());//sales_code
			ps.setString(++index, model.getPdCode());//pd_code 
			ps.setString(++index, model.getDescription());//description
			ps.setString(++index, model.getMoveOrderType());//move_order_type
			ps.setString(++index, STATUS_SAVE);//status
			ps.setInt(++index, Utils.convertStrToInt(model.getPrintNo()));//print_no
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, model.getUserId());
			ps.setBigDecimal(++index, model.getCreatedLong().setScale(6));
			ps.setString(++index, model.getCreatedBy());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return model;
	}
	
	private boolean deleteOrderLineByRequestNumber(Connection conn ,MoveOrder head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete from t_move_order_line where request_number =? \n");
			//logger.debug("SQL:"+sql);
			int index = 0;

			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getRequestNumber());//request_number

			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("delete:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private boolean insertMoveOrderLine(Connection conn ,MoveOrder head,MoveOrderLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_move_order_line( \n");
			sql.append(" request_number, line_number, inventory_item_id, \n"); 
			sql.append(" qty, qty1, qty2, \n"); 
			sql.append(" uom1, uom2, status,  \n");
			sql.append(" amount1, amount2, total_amount,  \n");
			sql.append(" exported, USER_ID, CREATED_LONG, CREATED_BY,pack) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			ps.setInt(++index, line.getProduct().getId());//inventory_item_id
			ps.setBigDecimal(++index, new BigDecimal(line.getQty()));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(line.getQty1()));//qty1
			ps.setBigDecimal(++index, new BigDecimal(line.getQty2()));//qty2
			ps.setString(++index, line.getUom1().getId());//uom1
			ps.setString(++index, line.getUom2().getId());//uom2
			ps.setString(++index, STATUS_SAVE);//status
			
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount1()));//amount1
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount2()));//amount2
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount1()+line.getAmount2()));//total_amount
			
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setBigDecimal(++index, line.getCreatedLong().setScale(6));
			ps.setString(++index, line.getCreatedBy());//CREATED_BY
			ps.setBigDecimal(++index, new BigDecimal(line.getPack()));//pack
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private boolean updateMoveOrderLine(Connection conn ,MoveOrder head,MoveOrderLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_move_order_line set \n");
			sql.append(" inventory_item_id =? ,pack = ? , \n"); 
			sql.append(" qty =? , qty1 = ? , qty2 =?, \n"); 
			sql.append(" uom1 =? , uom2 = ? ,  \n");
			sql.append(" amount1 =?, amount2=?, total_amount=?,  \n");
			sql.append(" USER_ID=?, UPDATED_LONG =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, line.getProduct().getId());//inventory_item_id
			ps.setBigDecimal(++index, new BigDecimal(line.getPack()));//pack
			ps.setBigDecimal(++index, new BigDecimal(line.getQty()));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(line.getQty1()));//qty1
			ps.setBigDecimal(++index, new BigDecimal(line.getQty2()));//qty2
			ps.setString(++index, line.getUom1().getId());//uom1
			ps.setString(++index, line.getUom2().getId());//uom2
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount1()));//amount1
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount2()));//amount2
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount1()+line.getAmount2()));//total_amount
			
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setBigDecimal(++index, line.getUpdatedLong().setScale(6));
			ps.setString(++index, line.getUpdateBy());//UPDATE_BY
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private boolean updateMoveOrderLineCaseLineCancel(Connection conn ,MoveOrder head,MoveOrderLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_move_order_line set \n");
			sql.append(" status = ? , \n"); 
			sql.append(" USER_ID=?, UPDATED_LONG =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, line.getStatus());//status
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setBigDecimal(++index, line.getUpdatedLong().setScale(6));
			ps.setString(++index, line.getUpdateBy());//UPDATE_BY
			
			ps.setString(++index, line.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	 public List<MoveOrder> searchMoveOrderList(MoveOrder mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<MoveOrder> list = new ArrayList<MoveOrder>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n h.request_number,h.request_date,h.organization_id, ");
				sql.append("\n h.sales_code,h.pd_code,h.description,");
				sql.append("\n h.move_order_type,h.status,h.print_no, ");
				sql.append("\n h.print_date_long,h.exported,h.USER_ID,");
				sql.append("\n h.CREATED_LONG,h.CREATED_BY,h.UPDATED_LONG,h.UPDATED_BY, ");
				sql.append("\n timestamp(h.CREATED_LONG) as CREATED, timestamp(h.UPDATED_LONG) as UPDATED, timestamp(h.print_date_long) as print_date ");
				sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
				sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
				sql.append("\n  from t_move_order h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				sql.append("\n  and  h.move_order_type ='"+mCriteria.getMoveOrderType()+"'");
				
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
						
					  sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.request_date desc ,h.request_number desc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  MoveOrder m = new MoveOrder();
				  m.setNo(no+"");
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				  m.setOrganizationId(rst.getString("organization_id"));
				  
				  m.setSalesCode(rst.getString("sales_code"));
				  m.setSalesDesc(rst.getString("sales_desc"));
				  
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
				  
				  m.setDescription(rst.getString("description"));
				  m.setMoveOrderType(rst.getString("move_order_type"));
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
				  m.setPrintNo(rst.getString("print_no"));
				  m.setUserId(rst.getString("user_id")); 
				  m.setCreatedBy(rst.getString("created_by"));
				  m.setUpdateBy(rst.getString("updated_by"));
				  
				  m.setCreated(DateUtil.stringValueSpecial(rst.getLong("created_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
				  m.setUpdated(DateUtil.stringValueSpecial(rst.getLong("updated_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
				  m.setPrintDate(DateUtil.stringValueSpecial(rst.getLong("print_date_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
				  
				  //Check canEdit
				  if((STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) 
					){
					  m.setCanEdit(true);
				  }
					  
				  list.add(m);
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					if(conn != null){
					   conn.close();conn=null;
					}
				} catch (Exception e) {}
			}
			return list;
	}
	
    public MoveOrder searchMoveOrder(MoveOrder mCriteria,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		MoveOrder m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Date currentDate = new Date();
		Date requestDate = null;
		try {
			conn = new  DBCPConnectionProvider().getConnection(conn);
			
			sql.append("\n  SELECT ");
			sql.append("\n h.request_number,h.request_date,h.organization_id, ");
			sql.append("\n h.sales_code,h.pd_code,h.description,");
			sql.append("\n h.move_order_type,h.status,h.print_no, ");
			sql.append("\n h.print_date_long,h.exported,h.USER_ID,");
			sql.append("\n h.CREATED_LONG,h.CREATED_BY,h.UPDATED_LONG,h.UPDATED_BY, ");
			sql.append("\n timestamp(h.CREATED_LONG) as CREATED, timestamp(h.UPDATED_LONG) as UPDATED, timestamp(h.print_date_long) as print_date ");
			sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
			sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
			sql.append("\n  from t_move_order h ");
			sql.append("\n  where h.request_number ='"+mCriteria.getRequestNumber()+"'");
			sql.append("\n  and  h.move_order_type ='"+mCriteria.getMoveOrderType()+"'");
			sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
			sql.append("\n  ORDER BY h.request_date desc  \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
			  m = mCriteria;
			  m.setRequestNumber(rst.getString("request_number"));
			  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  requestDate = DateUtil.parse(m.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
			  //Check 
			  if(requestDate != null){
				  if(currentDate.before(requestDate)){
					  m.setRequestDateDisabled(true);
				  }
			  }
			  
			  m.setOrganizationId(rst.getString("organization_id"));
			  
			  m.setSalesCode(rst.getString("sales_code"));
			  m.setSalesDesc(rst.getString("sales_desc"));
			  
			  m.setPdCode(rst.getString("pd_code"));
			  m.setPdDesc(rst.getString("pd_desc"));
			  m.setPdCodeDisabled(true);
			  
			  m.setDescription(rst.getString("description"));
			  m.setMoveOrderType(rst.getString("move_order_type"));
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
			  m.setPrintNo(rst.getString("print_no"));
			  m.setUserId(rst.getString("user_id"));
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
			  
			  m.setCreated(DateUtil.stringValueSpecial(rst.getLong("created_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
			  m.setUpdated(DateUtil.stringValueSpecial(rst.getLong("updated_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
			  m.setPrintDate(DateUtil.stringValueSpecial(rst.getLong("print_date_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
			  			  
			  m.setMoveOrderType(rst.getString("move_order_type"));
			  m.setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(m.getMoveOrderType()));
			  
			  //Check canEdit
			  if(  (STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) 
				){
				  m.setCanEdit(true);
			  }
			  
			}//while
			//Find Lines
			totalAmount = 0;
			m.setMoveOrderLineList(searchMoveOrderLine(conn,m));
			m.setTotalAmount(totalAmount);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e) {}
		}
		return m;
	}
	  
	  public List<MoveOrderLine> searchMoveOrderLine(Connection conn,MoveOrder mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<MoveOrderLine> lineList = new ArrayList<MoveOrderLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* , (select p.code from m_product p where p.product_id = l.inventory_item_id)as code   from t_move_order_line l ");
				sql.append("\n   WHERE l.request_number ='"+mCriteria.getRequestNumber()+"'");
				sql.append("\n   and l.status ='SV' ");
				sql.append("\n  ) A ORDER BY A.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				  MoveOrderLine m = new MoveOrderLine();
				  no++;
				  m.setNo(no);
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineNo(rst.getInt("line_number"));
				  Product mp = new MProduct().find(rst.getString("inventory_item_id"));
				  m.setProduct(mp);
				  m.setProductCode(mp.getCode());
				  m.setProductName(mp.getName());
				  
				  m.setUom1(new MUOM().find(rst.getString("uom1")));
				  m.setUom2(new MUOM().find(rst.getString("uom2")));
				  
				 // logger.debug("uom1:"+rst.getString("uom1")+",uom2:"+rst.getString("uom2"));
				 // logger.debug("uom1:"+m.getUom1().getCode()+",uom2:"+m.getUom2().getCode());
				  
				  //set FullUOM
				  String fullUom = "";
				  if(m.getUom1() != null){
					  fullUom = Utils.isNull(m.getUom1().getCode())+"/";
				  }
				  if(m.getUom2() != null){
					  fullUom += Utils.isNull(m.getUom2().getCode());
				  }
				  m.setFullUom(fullUom);
				  
				  m.setQty(rst.getDouble("qty"));
				  m.setQty1(rst.getDouble("qty1"));
				  m.setQty2(rst.getDouble("qty2"));
				  
				  /*logger.debug("uom1:"+m.getUom1().getId() +",uom2:"+m.getUom2().getId());
				  //Find Pack  By Uom2
				  UOMConversion uc = null;
				  if(m.getUom2() != null){
				     uc = new MUOMConversion().getCurrentConversion(mp.getId(), m.getUom2().getId());
				  }else{
					 uc = new MUOMConversion().getCurrentConversionNotIn(mp.getId(), m.getUom1().getId()); 
				  }
				  
				  m.setPack(0);
				  if(uc != null){
				    m.setPack(uc.getConversionRate());
				  }*/
				  m.setPack(rst.getInt("pack"));
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel("Y".equals(m.getStatus())?"ใช้งาน":"ยกเลิก");
				  m.setExported(rst.getString("exported"));
				  
				  m.setUserId(rst.getString("user_id"));
				  m.setCreatedBy(rst.getString("created_by"));
				
				  m.setAmount1(rst.getDouble("amount1"));
				  m.setAmount2(rst.getDouble("amount2"));
				  m.setTotalAmount(rst.getDouble("total_amount"));
				  
				  m.setCreated(DateUtil.stringValueSpecial(rst.getLong("created_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
				  m.setUpdated(DateUtil.stringValueSpecial(rst.getLong("updated_long"),DateUtil.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,DateUtil.local_th));
				  
				  //logger.debug("updated_long["+rst.getLong("updated_long")+"]");
				  m.setActionDate(m.getCreated());
				  if( rst.getLong("updated_long") != 0){
					  m.setActionDate(m.getUpdated());
				  }
				  //Sum TotalAMount
				  totalAmount += totalAmount +m.getTotalAmount();
				  
				//Check canEdit
				  if( (STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) ){
					  m.setCanEdit(true);
				  }
				  
				  lineList.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return lineList;
		}
	  
	  
	  /**
	   * 
	   * @param conn
	   * @param requestNumber
	   * @param lineNo
	   * @return
	   * @throws Exception
	   */
	  public MoveOrderLine checkLineExist(Connection conn,String requestNumber,int lineNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			 MoveOrderLine m = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.request_number ,l.line_number,l.qty from t_move_order_line l ");
				sql.append("\n  where l.request_number ='"+requestNumber+"'");
				sql.append("\n  and l.line_number ="+lineNo+"");
				
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				if (rst.next()) {
				  m = new MoveOrderLine();
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineNo(rst.getInt("line_number"));
				  m.setQty(rst.getDouble("qty"));
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return m;
		}
	  
	  public int getNextLineNo(Connection conn,String requestNumber) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			int nextLineNo = 1;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT max(l.line_number) as max_line_no from t_move_order_line l ");
				sql.append("\n  where l.request_number ='"+requestNumber+"'");
				
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				if (rst.next()) {
				   nextLineNo = rst.getInt("max_line_no")+1;
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return nextLineNo;
		}
	  
	  
	  /**
	   * searchMoveOrderSummary
	   * @param mCriteria
	   * @param user
	   * @return
	   * @throws Exception
	   */
	  public List<MoveOrderSummary> searchMoveOrderSummaryDetail(MoveOrderSummary mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<MoveOrderSummary> list = new ArrayList<MoveOrderSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n   h.request_number,h.request_date ");
				sql.append("\n  ,h.pd_code ,p.code as product_code ");
				sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
				sql.append("\n  ,l.qty ,l.qty1 ,l.qty2 ");
				sql.append("\n  ,l.uom1 ,l.uom2 ");
				sql.append("\n  ,l.pack,l.total_amount");
				sql.append("\n  ,h.status,h.exported");
				sql.append("\n  from t_move_order h  ");
				sql.append("\n  ,    t_move_order_line l  ");
				sql.append("\n  ,    m_product p  ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.request_number = l.request_number ");
				sql.append("\n  and  l.inventory_item_id = p.product_id ");
				sql.append("\n  and  h.user_id ='"+user.getId()+"'");
				sql.append("\n  and  h.move_order_type ='"+mCriteria.getMoveOrderType()+"'");
				
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
					 sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					 sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				
				if( !Utils.isNull(mCriteria.getProductCodeFrom()).equals("")
						&&	!Utils.isNull(mCriteria.getProductCodeTo()).equals("")	){
				     sql.append(" and p.code >= '"+mCriteria.getProductCodeFrom()+"' \n");
					 sql.append(" and p.code <= '"+mCriteria.getProductCodeTo()+"' \n");
				}
				
				if( !Utils.isNull(mCriteria.getStatus()).equals("")){
				    sql.append("\n  and  h.status ='"+mCriteria.getStatus()+"'");
			    }
			
				if( !Utils.isNull(mCriteria.getExported()).equals("")){
				    sql.append("\n  and  h.exported ='"+mCriteria.getExported()+"'");
			    }
				
				sql.append("\n  ORDER BY h.request_number asc \n");
				
				logger.info("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  MoveOrderSummary m = new MoveOrderSummary();
				  m.setNo(no);
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdCodeShow(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
                  m.setProductCode(rst.getString("product_code"));
                  m.setUom1(rst.getString("uom1"));
                  m.setQty1(NumberToolsUtil.decimalFormat(rst.getDouble("qty1"),NumberToolsUtil.format_current_no_disgit));
                  m.setUom2(rst.getString("uom2"));
                  m.setQty2(NumberToolsUtil.decimalFormat(rst.getDouble("qty2"),NumberToolsUtil.format_current_no_disgit));
                  m.setTotalAmount(NumberToolsUtil.decimalFormat(rst.getDouble("total_amount"),NumberToolsUtil.format_current_2_disgit));
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
				  list.add(m);
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if(rst != null){
						rst.close();rst = null;
					}
					if(stmt != null){
						stmt.close(); stmt = null;
					}
					if(conn != null){
					   conn.close();conn=null;
					}
				} catch (Exception e) {}
			}
			return list;
	}
	
	/**
	 * searchMoveOrderSummaryTotal
	 * @param mCriteria
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<MoveOrderSummary> searchMoveOrderSummaryTotal(MoveOrderSummary mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<MoveOrderSummary> list = new ArrayList<MoveOrderSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n   h.pd_code ,p.code as product_code ");
				sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
				sql.append("\n  ,sum(l.qty) as qty ,sum(l.qty1) as qty1 ,sum(l.qty2) as qty2 ");
				sql.append("\n  ,l.uom1 ,l.uom2 ");
				sql.append("\n  ,l.pack,sum(l.total_amount) as total_amount");
				sql.append("\n  ,h.status,h.exported");
				sql.append("\n  from t_move_order h  ");
				sql.append("\n  ,    t_move_order_line l  ");
				sql.append("\n  ,    m_product p  ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.request_number = l.request_number ");
				sql.append("\n  and  l.inventory_item_id = p.product_id ");
				sql.append("\n  and  h.user_id ='"+user.getId()+"'");
				sql.append("\n  and  h.move_order_type ='"+mCriteria.getMoveOrderType()+"'");
				
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
						
					 sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					 sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				
				if( !Utils.isNull(mCriteria.getProductCodeFrom()).equals("")
						&&	!Utils.isNull(mCriteria.getProductCodeTo()).equals("")	){
				     sql.append(" and p.code >= '"+mCriteria.getProductCodeFrom()+"' \n");
					 sql.append(" and p.code <= '"+mCriteria.getProductCodeTo()+"' \n");
				}
				
				if( !Utils.isNull(mCriteria.getStatus()).equals("")){
				    sql.append("\n  and  h.status ='"+mCriteria.getStatus()+"'");
			    }
			
				if( !Utils.isNull(mCriteria.getExported()).equals("")){
				    sql.append("\n  and  h.exported ='"+mCriteria.getExported()+"'");
			    }
				
				sql.append("\n  GROUP BY h.pd_code,h.status,h.exported ,p.code ,l.uom1 ,l.uom2,l.pack \n");
				sql.append("\n  ORDER BY h.pd_code ,p.code asc \n");
				
				logger.info("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				int i = 0;
				String tempPDCode = "";
				while (rst.next()) {
				  no++;
				  MoveOrderSummary m = new MoveOrderSummary();
				  m.setNo(no);
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
                  m.setProductCode(rst.getString("product_code"));
                  m.setUom1(rst.getString("uom1"));
                  m.setQty1(NumberToolsUtil.decimalFormat(rst.getDouble("qty1"),NumberToolsUtil.format_current_no_disgit));
                  m.setUom2(rst.getString("uom2"));
                  m.setQty2(NumberToolsUtil.decimalFormat(rst.getDouble("qty2"),NumberToolsUtil.format_current_no_disgit));
                  m.setTotalAmount(NumberToolsUtil.decimalFormat(rst.getDouble("total_amount"),NumberToolsUtil.format_current_2_disgit));
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
				  if(i==0){
					  m.setPdCodeShow(m.getPdCode());
					  tempPDCode = m.getPdCode();
				  }else{
					  if( !tempPDCode.equals(m.getPdCode())){
						  m.setPdCodeShow(m.getPdCode());
						  tempPDCode = m.getPdCode();
					  }else{
						  m.setPdCodeShow(""); 
					  }
				  }
				  
				  list.add(m);
				  i++;
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if(rst != null){
					   rst.close();rst = null;
					}
					if(stmt != null){
					   stmt.close(); stmt = null;
					}
					if(conn != null){
					   conn.close();conn=null;
					}
				} catch (Exception e) {}
			}
			return list;
	}
	  
}
