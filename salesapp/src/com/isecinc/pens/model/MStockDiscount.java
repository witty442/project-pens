package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.StockDiscount;
import com.isecinc.pens.bean.StockDiscountLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StockBeanUtils;
import com.isecinc.pens.dao.StockUtilsDAO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.StockDiscountDocumentProcess;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;


public class MStockDiscount {

	private static Logger logger = Logger.getLogger("PENS"); 
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	
    private static double totalAmount = 0;
    
    public int getTotalRowStockDiscount(Connection conn,StockDiscount bean,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int totalRow = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select count(*) as c from t_stock_discount h ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and  h.user_id ="+user.getId()+"");
			
			if( bean.getCustomerId() != 0){
			    sql.append("\n  and  h.customer_id ='"+bean.getCustomerId()+"' \n");
		    }
		
			if( !Utils.isNull(bean.getRequestDateFrom()).equals("")
				&&	!Utils.isNull(bean.getRequestDateTo()).equals("")	){
				  sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(bean.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				  sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(bean.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
			}
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				totalRow = rst.getInt("c");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return totalRow;
	}
    public List<StockDiscount> searchStockDiscountList(Connection conn ,StockDiscount mCriteria,User user,String whereCause,int start) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<StockDiscount> list = new ArrayList<StockDiscount>();
		StringBuilder sql = new StringBuilder();
		int no = 0;
		try {
			sql.append("\n select A.* from (");
			sql.append("\n  SELECT h.*, C.name as customer_name ,C.code as customer_code");
			sql.append("\n   from t_stock_discount h, m_customer C");
			sql.append("\n  where C.customer_id=h.customer_id ");
			sql.append("\n  and  h.user_id ="+user.getId()+"");
			
			if( mCriteria.getCustomerId() != 0){
			    sql.append("\n  and  h.customer_id ='"+mCriteria.getCustomerId()+"' \n");
		    }
		
			if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
				&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
					
				  sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				  sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
			}
			sql.append("\n  ORDER BY h.request_number desc \n");
			sql.append("\n )A ");
			sql.append("\n "+whereCause);
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  no++;
			  StockDiscount m = new StockDiscount();
			  m.setNo(no+"");
			  m.setRequestNumber(rst.getString("request_number"));
			  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setDescription(Utils.isNull(rst.getString("description")));
			  m.setCustomerId(rst.getInt("customer_id"));
			  m.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			  m.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			 // m.setCustomerAddress(Utils.isNull("")));
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
	          m.setDescription(Utils.isNull(rst.getString("description")));
			  m.setUserId(rst.getString("user_id")); 
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
			  
			  m.setCreated(DateUtil.stringValue(rst.getDate("created"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setUpdated(DateUtil.stringValue(rst.getDate("updated"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  
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
			} catch (Exception e) {}
		}
		return list;
    }
    public int getMaxLineNumberStockDiscountLine(Connection conn,String requestNumber) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int maxLineId = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select max(line_number) as line_number from t_stock_discount_line h ");
			sql.append("\n  where h.request_number ='"+requestNumber+"'");
	
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				maxLineId = rst.getInt("line_number");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return maxLineId;
	}
    
	public StockDiscount save(User user,StockDiscount head) throws Exception {
		Connection conn = null;
		String lineIdNodel = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//Generate requestNumber
			String requestNumber  ="";
		
			if("".equals(head.getRequestNumber())){
				
				Date requestDate = DateUtil.parse(head.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
				requestNumber = new StockDiscountDocumentProcess().getNextDocumentNo(requestDate,user.getCode(),user.getId(), conn);
				
				//prepare MoveOrder
				logger.debug("requestNumber["+requestNumber+"]");
				//prepare MoveOrder Model
				head.setRequestNumber(requestNumber);

				head = insertStockDiscount(conn , head);
				
			}else{
				updateStockDiscount(conn , head);
			}
			
	        //insert or update line
			if(head.getLineList() != null && head.getLineList().size() > 0){
				//get max lineId
				int maxLineNumber =getMaxLineNumberStockDiscountLine(conn, head.getRequestNumber());
				// Process normal
				for(int i =0;i< head.getLineList().size();i++){
					StockDiscountLine line = (StockDiscountLine)head.getLineList().get(i);
					line.setUpdateBy(head.getCreatedBy());
					
					if(line.getLineId() != 0){
						updateStockDiscountLine(conn,head, line);
					}else{
						maxLineNumber++;
				        line.setLineId(maxLineNumber);
					    insertStockDiscountLine(conn,head, line);
					}
					//set for no del line
					lineIdNodel += line.getLineId()+",";
				}//for
			}
			
			//delete lineNo
			if(head.getLineNoDeleteList() != null && head.getLineNoDeleteList().size() > 0){
				for(int i =0;i< head.getLineNoDeleteList().size();i++){
					String lineNumber = (String)head.getLineNoDeleteList().get(i);
					int r = deleteStockDiscountLine(conn, head.getRequestNumber(),Utils.convertStrToInt(lineNumber));
					
					logger.debug("delete requestNumber["+head.getRequestNumber()+"]lineId["+lineNumber+"]result["+r+"]");
				}
			}
			
			//For case Exception in above function-> delete lineId is not insert or update
			if( !Utils.isNull(lineIdNodel).equals("")){
				lineIdNodel = lineIdNodel.substring(0,lineIdNodel.length()-1);
				
				int r = deleteStockDiscountLineNotInLineId(conn, head.getRequestNumber(),lineIdNodel);
				logger.debug("Delete Step 2 result:"+r);
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
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StockDiscount updateStockDiscount(StockDiscount head) throws Exception {
		Connection conn = null;
		try {
		    conn = new DBCPConnectionProvider().getConnection(conn);
		    conn.setAutoCommit(false);
		    
		    updateStockDiscount(conn,head);
		    
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
	
	private StockDiscount updateStockDiscount(Connection conn ,StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_stock_discount \n");
			sql.append(" set  request_date = ? ,description =? ");
			sql.append(" ,line_amount =? ,vat_amount =?,net_amount =? \n"); 
			sql.append(" ,updated =? ,updated_by = ? ,vat_rate =?  \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(head.getRequestDate()).getTime()));//request_date
			ps.setString(++index, Utils.isNull(head.getDescription()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalLineAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalNetAmount()));
			ps.setDate(++index, DateUtil.getCurrentSqlDate());//updated
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setString(++index, head.getVatRate());
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
	
	public StockDiscount updateCancelStockDiscount(StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_stock_discount \n");
			sql.append(" set  status = ?  ,description = ? \n"); 
			sql.append(" ,updated =? ,updated_by = ? ,line_amount=?,vat_amount=? ,net_amount=? \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//status
			ps.setString(++index, Utils.isNull(head.getDescription()));//Description
			ps.setDate(++index, DateUtil.getCurrentSqlDate());//updated
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalLineAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalNetAmount()));
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
	
	
	private StockDiscount insertStockDiscount(Connection conn ,StockDiscount model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_discount( \n");
			sql.append(" request_number, request_date, description, \n");
			sql.append(" status,exported, USER_ID, CREATED,CREATED_BY,customer_id \n");
			sql.append(",line_amount,vat_amount,net_amount,vat_rate \n");
			sql.append(",back_date,updated) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, model.getRequestNumber());//request_number
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getRequestDate()).getTime()));//request_date
			ps.setString(++index, Utils.isNull(model.getDescription()));//description
			ps.setString(++index, STATUS_SAVE);//status
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, model.getUserId());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getCreatedBy());
			ps.setInt(++index, model.getCustomerId());
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalLineAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalNetAmount()));
			ps.setString(++index, Utils.isNull(model.getVatRate()));
			ps.setDate(++index, new java.sql.Date((DateUtil.parse(model.getBackDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
			ps.setNull(++index,java.sql.Types.TIMESTAMP);//updated 
			
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
	

	private boolean insertStockDiscountLine(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_discount_line( \n");
			sql.append(" request_number, line_number, inventory_item_id,product_name,ar_invoice_no, \n"); 
			sql.append(" pri_qty,uom1_qty,uom2_qty, uom2,uom1_pac,uom2_pac, \n");
			sql.append(" uom1_price,line_amount,vat_amount,net_amount, \n");
			sql.append(" uom1_Conv_Rate,uom2_Conv_Rate, \n");
			sql.append(" status,exported, USER_ID, CREATED, CREATED_BY,UPDATED) \n");//24
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineId());//line_number
			ps.setInt(++index, Utils.convertStrToInt(line.getInventoryItemId()));//inventory_item_id
			ps.setString(++index, Utils.isNull(line.getProductName()));//arInvoiceNo
			ps.setString(++index, Utils.isNull(line.getArInvoiceNo()));//arInvoiceNo
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPriQty()));// priQty = calc from(uom1Qty and uom2Qty)
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Qty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2Qty()));//
			ps.setString(++index, Utils.isNull(line.getUom2()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom1Pac()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom2Pac()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Price()));//uom1 price/ ctn
			ps.setDouble(++index, Utils.convertStrToDouble(line.getLineAmount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getVatAmount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getNetAmount()));//
			
		/*	ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainPriAllQty()));// 
			ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainPriQty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainSubQty()));//
*/			
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1ConvRate()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2ConvRate()));//
			
			ps.setString(++index, STATUS_SAVE);//status
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getCreatedBy());//CREATED_BY
			ps.setNull(++index,java.sql.Types.TIMESTAMP);//updated 
			
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
	
	private boolean updateStockDiscountLine(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_discount_line set \n");
			sql.append(" inventory_item_id =?,product_name =? ,ar_invoice_no =?,pri_qty =?, uom1_qty =? ,uom2_qty =? , uom2=? , \n");
			sql.append(" uom1_pac=?, uom2_pac=? ,uom1_price=? ,line_amount =? ,vat_amount=? ,net_amount =? ,");
			//sql.append(" remain_pri_all_qty =?,remain_pri_qty=?,remain_sub_qty=?, \n");
			sql.append(" uom1_Conv_Rate=?,uom2_Conv_Rate=?, \n");
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, Utils.convertStrToInt(line.getInventoryItemId()));//inventory_item_id
			ps.setString(++index, Utils.isNull(line.getProductName()));//
			ps.setString(++index, Utils.isNull(line.getArInvoiceNo()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPriQty()));// preQty = calc from(Uom1Qty and uom2Tyq)
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Qty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2Qty()));//
			ps.setString(++index, Utils.isNull(line.getUom2()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom1Pac()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom2Pac()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Price()));//uom1 price/ ctn
			ps.setDouble(++index, Utils.convertStrToDouble(line.getLineAmount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getVatAmount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getNetAmount()));//
			
			/*ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainPriAllQty()));// 
			ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainPriQty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getRemainSubQty()));//
*/			
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1ConvRate()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2ConvRate()));//
			
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));//updateDate
			ps.setString(++index, line.getUpdateBy());//UPDATE_BY
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineId());//line_number
			
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
	
	private boolean updateStockDiscountLineCaseLineCancel(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_discount_line set \n");
			sql.append(" status = ? , \n"); 
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, line.getStatus());//status
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, DateUtil.getCurrentSqlDate());
			ps.setString(++index, line.getUpdateBy());//UPDATE_BY
			
			ps.setString(++index, line.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineId());//line_number
			
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
	
	private boolean deleteStockDiscountLineByRequestNumber(Connection conn ,StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete from t_stock_discount_line   \n");
			sql.append(" WHERE request_number=? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
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
		return result;
	}
	private int deleteStockDiscountLine(Connection conn ,String requestNumber,int lineNumber) throws Exception {
		int r= 0;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from t_stock_discount_line  \n");
			sql.append(" WHERE request_number=? \n");
			sql.append(" and line_number=? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, requestNumber);//request_number
		    ps.setInt(++index, lineNumber);
		    
			r = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return r;
	}
	private int deleteStockDiscountLineNotInLineId(Connection conn ,String requestNumber,String notInLineId) throws Exception {
		int r= 0;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from t_stock_discount_line  \n");
			sql.append(" WHERE request_number=? \n");
			sql.append(" and line_number not in("+notInLineId+") \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, requestNumber);//request_number
			r = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return r;
	}
	public  Date getBackDate(Connection conn,String requestNumber)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
        Date backDate = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n  SELECT back_date from t_stock_discount h");
			sql.append("\n  where  h.request_number ='"+ requestNumber + "'");
			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				backDate = rst.getDate("back_date");
		
			}// while	
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return backDate;
	}
	
	public StockDiscount searchStockDiscount(StockDiscount mCriteria, User user)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StockDiscount m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

			sql.append("\n  SELECT h.*, C.name as customer_name ,C.code as customer_code");
			sql.append("\n  from t_stock_discount h, m_customer C");
			sql.append("\n  where C.customer_id=h.customer_id ");
			sql.append("\n  and  h.request_number ='"+ mCriteria.getRequestNumber() + "'");
			sql.append("\n  and  h.user_id ='" + mCriteria.getUserId() + "'");
			sql.append("\n  ORDER BY h.request_date desc  \n");

			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				m = mCriteria;
				m.setRequestNumber(rst.getString("request_number"));
			    m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    m.setBackDate(DateUtil.stringValue(rst.getDate("back_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    m.setDescription(rst.getString("description"));
			    m.setCustomerId(rst.getInt("customer_id"));
				m.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				m.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				
				Address address = new MAddress().findAddressByCustomerId(conn, m.getCustomerId()+"","B");
				if(address != null){
					String addressStr = address.getLine1()+" "+address.getLine2()+" "+address.getLine3()+" "+address.getLine4()+" "+address.getProvince().getName()+" "+address.getPostalCode();
				    m.setCustomerAddress(addressStr);
				}
				
			    m.setStatus(rst.getString("status"));
			    m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			    m.setExported(rst.getString("exported"));
			    m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
	            m.setDescription(Utils.isNull(rst.getString("description")));
			    m.setUserId(rst.getString("user_id")); 
			    m.setCreatedBy(rst.getString("created_by"));
			    m.setUpdateBy(rst.getString("updated_by"));
			  
			    m.setCreated(DateUtil.stringValue(rst.getDate("created"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    m.setUpdated(DateUtil.stringValue(rst.getDate("updated"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    
			    m.setTotalLineAmount(Utils.convertDoubleToStr(rst.getDouble("line_amount"),Utils.format_current_2_disgit));
			    m.setTotalVatAmount(Utils.convertDoubleToStr(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			    m.setTotalNetAmount(Utils.convertDoubleToStr(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			    m.setVatRate(Utils.isNull(rst.getString("vat_rate")));
			    
			    // Check canEdit
				if ((STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED
						.equals(m.getExported()))) {
					m.setCanEdit(true);
				}
			}// while
			
			// Find Lines
			totalAmount = 0;
			m.setLineList(searchStockDiscountLine(conn,user, m));
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return m;
	}
	  
	  public List<StockDiscountLine> searchStockDiscountLine(Connection conn,User user,StockDiscount mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<StockDiscountLine> lineList = new ArrayList<StockDiscountLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			PopupForm popupForm = new PopupForm();
			try {
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* , (select p.code from m_product p where p.product_id = l.inventory_item_id)as code ");
				sql.append("\n   from t_stock_discount_line l ");
				sql.append("\n   WHERE l.request_number ='"+mCriteria.getRequestNumber()+"'");
				sql.append("\n   and l.status ='SV' ");
				sql.append("\n  ) A ORDER BY A.line_number asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				  StockDiscountLine m = new StockDiscountLine();
				  no++;
				  m.setNo(no);
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineId(rst.getInt("line_number"));
				  m.setInventoryItemId(rst.getString("inventory_item_id"));
				  m.setArInvoiceNo(rst.getString("ar_invoice_no"));
				  //Case key By Product
				  if( !Utils.isNull(m.getInventoryItemId()).equals("0")){
					  Product mp = new MProduct().find(rst.getString("inventory_item_id"));
					  m.setProduct(mp);
					  m.setProductCode(mp.getCode());
					  m.setProductName(mp.getName());
				  }else{
					 //Case Key By Detail
					  m.setProductCode("");
					  m.setProductName(Utils.isNull(rst.getString("product_name")));
				  }
				  m.setPriQty(Utils.convertDoubleToStr(rst.getDouble("pri_qty"),Utils.format_current_5_digit));
				  m.setUom1Qty(Utils.convertDoubleToStr(rst.getDouble("uom1_qty"),Utils.format_current_2_disgit));
				  m.setUom2Qty(Utils.convertDoubleToStr(rst.getDouble("uom2_qty"),Utils.format_current_2_disgit));
				  m.setUom2(Utils.isNull(rst.getString("uom2")));
				  
				  m.setUom1ConvRate(Utils.convertDoubleToStr(rst.getDouble("uom1_conv_rate"),Utils.format_current_2_disgit));
				  m.setUom2ConvRate(Utils.convertDoubleToStr(rst.getDouble("uom2_conv_rate"),Utils.format_current_2_disgit));
				  
				  // get uom1ConvRate and uom2ConvRate for calc pri_qty
				  /*UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(rst.getInt("inventory_item_id"), "CTN");
				  UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(rst.getInt("inventory_item_id"), m.getUom2());
				  if(uc1 != null){
					  m.setUom1ConvRate(uc1.getConversionRate()+"");
				  }
				  if(uc2 != null){
					  m.setUom2ConvRate(uc2.getConversionRate()+"");
				  }*/
				  
				  //calc remain from stock_return and stock_return_init
				  popupForm.setCustomerCode(mCriteria.getCustomerCode());
				  popupForm.setUserId(mCriteria.getUserId());
				  popupForm.setProductCode(m.getProductCode());
				  popupForm.setRequestNumber(mCriteria.getRequestNumber());
				  popupForm.setCodeSearch(m.getArInvoiceNo());//wit:edit 05/05/2563 case display remainPriQty wrong
				  
				  List<PopupForm> results = searchInvoiceStockDiscount(popupForm,"",user);
				  if(results != null && results.size()>0){ 
					  PopupForm remain = results.get(0);
				      m.setRemainPriAllQty(remain.getPriAllQty());
				      m.setRemainPriQty(remain.getPriQty());
				      m.setRemainSubQty(remain.getSubQty());
				      m.setRemainAmount(remain.getRemainAmount());
				  }
				  
				  m.setUom1Pac(Utils.convertDoubleToStrDefaultNoDigit(rst.getDouble("uom1_pac"),""));
				  m.setUom2Pac(Utils.convertDoubleToStrDefaultNoDigit(rst.getDouble("uom2_pac"),""));
				  m.setUom1Price(Utils.decimalFormat(rst.getDouble("uom1_price"),Utils.format_current_2_disgit));
				  m.setLineAmount(Utils.decimalFormat(rst.getDouble("line_amount"),Utils.format_current_2_disgit));
				  m.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
				  m.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel("Y".equals(m.getStatus())?"ใช้งาน":"ยกเลิก");
				  m.setExported(rst.getString("exported"));
				  m.setUserId(rst.getString("user_id"));
				  m.setCreatedBy(rst.getString("created_by"));
				  
				  m.setCreated(DateUtil.stringValue(rst.getDate("created"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setUpdated(DateUtil.stringValue(rst.getDate("updated"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
				  //logger.debug("updated_long["+rst.getLong("updated_long")+"]");
				  m.setActionDate(m.getCreated());
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
	  public StockDiscountLine checkLineExist(Connection conn,String requestNumber,int lineNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StockDiscountLine m = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.request_number ,l.line_number,l.qty from t_stock_discount_line l ");
				sql.append("\n  where l.request_number ='"+requestNumber+"'");
				sql.append("\n  and l.line_number ="+lineNo+"");
				
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				if (rst.next()) {
				  m = new StockDiscountLine();
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineId(rst.getInt("line_number"));
				 // m.setQty(Utils.convertDoubleToStrDefault(rst.getDouble("qty"),""));
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
				sql.append("\n  SELECT max(l.line_number) as max_line_no from t_stock_discount_line l ");
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
	  
	  public boolean reCalcAmountInHead(Connection conn ,StockDiscount head) throws Exception {
			boolean result = false;
			PreparedStatement ps = null;
			try {
				StringBuffer sql = new StringBuffer("");
			
				sql.append(" update t_stock_discount h \n");
				sql.append(" set line_amount = ( \n");
				sql.append("   select ROUND(sum(line_amount),2) \n");
				sql.append("   from t_stock_discount_line l where h.request_number = l.request_number \n");
				sql.append("   and l.status ='SV' \n");
				sql.append("  ) \n");
				sql.append(",vat_amount = ( \n");
				sql.append("   select ROUND(sum(vat_amount),2) \n");
				sql.append("   from t_stock_discount_line l where h.request_number = l.request_number \n");
				sql.append("   and l.status ='SV' \n");
				sql.append(") \n");
				sql.append(",net_amount = ( \n");
				sql.append("   select ROUND(sum(net_amount),2) \n");
				sql.append("   from t_stock_discount_line l where h.request_number = l.request_number \n");
				sql.append("   and l.status ='SV' \n");
				sql.append(") \n");
				sql.append(" where  h.request_number ='"+head.getRequestNumber()+"' \n");
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
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
	  
	  /************************* Stock Discount (invoice Qty Popup) ***********************************************/
	 public static List<PopupForm> searchInvoiceStockDiscount(PopupForm c,String operation,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String backDate = "";
			StockBeanUtils stockBean = null;
			double remainAmount = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				//Case requestNumber is not null get back from stockDiscount
				if( !Utils.isNull(c.getRequestNumber()).equals("")){
					Date backDateObj = new MStockDiscount().getBackDate(conn, Utils.isNull(c.getRequestNumber()));
					backDate = DateUtil.stringValue(backDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
				}else{
					// get back_date(from c_reference) for get data
					List<References> backDateInvoiceStockDiscountList = InitialReferences.getReferenceListByCode(conn,InitialReferences.BACKDATE_INVOICE_STOCKDISCOUNT);
					if(backDateInvoiceStockDiscountList != null ){
					   References refbackDate =  backDateInvoiceStockDiscountList.get(0);
					   Calendar curdate = Calendar.getInstance();
					   curdate.add(Calendar.MONTH, -1*Integer.parseInt(refbackDate.getKey()));
					   
					   logger.debug("backDate:"+curdate.getTime());
					   backDate = DateUtil.stringValue(curdate.getTime(), DateUtil.DD_MM_YYYY_WITH_SLASH);
					   //set to 01/mm/yyyy
					   backDate = "01/"+backDate.substring(3,backDate.length());
					   logger.debug("backDate:"+backDate);
					}
				}
				
				sql.append("\n select h.ar_invoice_no,COALESCE(sum(l.qty),0) as qty" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and l.promotion <> 'Y' ");
				sql.append("\n and l.iscancel <> 'Y' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				/** can search invoice by customer ,no productCode **/
				if(!Utils.isNull(c.getProductCode()).equals("")){
				  sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				}
				sql.append("\n and order_date >= STR_TO_DATE('"+backDate+"', '%d/%m/%Y') ");
				sql.append("\n and doc_status <> 'VO' ");
			
				if("equals".equalsIgnoreCase(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no ='"+c.getCodeSearch()+"'");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no = '"+c.getDescSearch()+"'");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no LIKE '%"+c.getCodeSearch()+"%' ");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no LIKE '%"+c.getDescSearch()+"%' ");
					}
				}
				/** not in stock_discount_init **/
				sql.append("\n and h.ar_invoice_no not in(select ar_invoice_no from t_stock_discount_init) ");
				
				sql.append("\n GROUP BY h.ar_invoice_no ");
				sql.append("\n ORDER BY h.ar_invoice_no asc ");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("ar_invoice_no")));
					if(!Utils.isNull(c.getProductCode()).equals("")){
					    item.setPrice(getOrderItemPriceByArInvoiceNo(conn, c, backDate, user, Utils.isNull(rst.getString("ar_invoice_no"))));
						//calc priQty
						stockBean = calcStockDiscountPriQtyByArInvoice(conn,c.getRequestNumber(), Utils.isNull(rst.getString("ar_invoice_no")),backDate,c,user );
						if(stockBean != null){
						   item.setDesc(""+stockBean.getPriAllQty());
						   item.setPriAllQty(Utils.decimalFormat(stockBean.getPriAllQty(),Utils.format_current_5_digit));
						   item.setPriQty(Utils.decimalFormat(stockBean.getPriQty(),Utils.format_current_5_digit));
						   item.setSubQty(Utils.decimalFormat(stockBean.getSubQty(),Utils.format_current_5_digit));

						}
						
						//calc remain amount by invoice
						remainAmount = calcStockDiscountRemianAmountByInvoice(conn,c.getRequestNumber(), Utils.isNull(rst.getString("ar_invoice_no")),c.getProductCode(),backDate,c,user );
						item.setRemainAmount(Utils.decimalFormat(remainAmount,Utils.format_current_2_disgit));
						
						if(( stockBean != null && stockBean.getPriAllQty() > 0.00 )
							||(remainAmount > 0)){
						   pos.add(item);
					    }
					}else{
						/** Case in search (productCode is null) **/
						item.setDesc("0");
						item.setPriAllQty("0");
						item.setPriQty("0");
						item.setSubQty("0");
						
						//calc remain amount by invoice
						remainAmount = calcStockDiscountRemianAmountByInvoice(conn,c.getRequestNumber(), Utils.isNull(rst.getString("ar_invoice_no")),null,backDate,c,user );
						item.setRemainAmount(Utils.decimalFormat(remainAmount,Utils.format_current_2_disgit));
						if(remainAmount > 0){
						   pos.add(item);
						}
					}
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
		 
	  public static String getOrderItemPriceByArInvoiceNo(Connection conn,PopupForm c,String backDate,User user,String arInvoiceNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String price ="";
			try {
				sql.append("\n select distinct l.price" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				sql.append("\n and h.doc_status <> 'VO' ");
				sql.append("\n and l.promotion <> 'Y' ");
				sql.append("\n and h.order_date >= STR_TO_DATE('"+backDate+"', '%d/%m/%Y') ");
			    sql.append("\n and h.ar_invoice_no ='"+arInvoiceNo+"'");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					price = Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit);
	
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return price;
		}
	 
	 public static StockBeanUtils calcStockDiscountPriQtyByArInvoice(Connection conn,String curRequestNumber
			 ,String arInvoiceNo,String dateStart,PopupForm c,User user) throws Exception {
		    StockBeanUtils bean = new StockBeanUtils();
		    Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double priQtyInit = 0;
			double priQtyNotInCurrRequestNumber = 0;
			double priAllQty = 0;
			String productId = "";
			try {
				sql.append("\n select h.ar_invoice_no,l.product_id,l.uom_id,COALESCE(sum(l.qty),0) as remain_pri_qty" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and doc_status <> 'VO' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				sql.append("\n and order_date >= STR_TO_DATE('"+dateStart+"', '%d/%m/%Y') ");
				sql.append("\n and h.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n GROUP BY h.ar_invoice_no ,l.product_id,l.uom_id");
				sql.append("\n ORDER BY h.ar_invoice_no asc ");
				
				//logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					priAllQty += StockUtilsDAO.calcPriQty(conn,rst.getString("product_id"), rst.getString("uom_id"), rst.getInt("remain_pri_qty"));
                    productId = rst.getString("product_id");
				}//while
				//logger.debug("priAllQty:"+priAllQty);
				
				/** del stockReturnItem <> curRequestnumber **/
				priQtyNotInCurrRequestNumber = getPriQtyExistNotInCurrentNumber(conn, arInvoiceNo, curRequestNumber, productId);
				//logger.debug("priQtyNotInCurrRequestNumber:"+priQtyNotInCurrRequestNumber);
				
				/** PriQtyInit ***/
				priQtyInit =0;// getPriQtyInt(conn,user, arInvoiceNo, c.getCustomerCode(), productId);
				//logger.debug("priQtyInit:"+priQtyInit);		
						
				/**  = priQtyall +(neg priQtyInit) - priQty(exist <> curRequestNumber) **/
				//logger.debug("Calc priAllQty["+priAllQty+"]-priQtyInit["+priQtyInit+"]-priQtyNotInCurrReqNum["+priQtyNotInCurrRequestNumber+"]");
				priAllQty =  priAllQty + priQtyInit - priQtyNotInCurrRequestNumber;
				priAllQty = NumberToolsUtil.round(priAllQty, 5, BigDecimal.ROUND_HALF_UP); 
				//logger.debug("Result priAllQty:"+priAllQty);
				
				bean.setPriAllQty(priAllQty);
				
				/*** Calc remain priQty to pri_qty and sub_qty ***/
				String tempPriAllQty = priAllQty+"";
				double priQty = Utils.convertStrToDouble(tempPriAllQty.substring(0,tempPriAllQty.indexOf(".")));
				double subQtyTemp =Utils.convertStrToDouble(tempPriAllQty.substring(tempPriAllQty.indexOf("."),tempPriAllQty.length()));
				//logger.debug("priQty["+priQty+"]");
				//logger.debug("subQtyTemp["+subQtyTemp+"]");
				
				//get Uom2
				Product p = new MProduct().getStockReturnProduct(c.getProductCode(),user);
				double subQty = 0;
				if(p != null){
				   subQty = StockUtilsDAO.calcPriQtyToSubQty(conn, productId, p.getUom2(), subQtyTemp); 
				}
				//logger.debug("after clac subQty["+subQty+"]");
				
				bean.setPriQty(priQty);
				bean.setSubQty(subQty);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
		}

	 public static double getPriQtyExistNotInCurrentNumber(Connection conn,String arInvoiceNo,String curRequestNumber,String productId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double existPriQty = 0;
			try {
				sql.append("\n select l.inventory_item_id as product_id,COALESCE(sum(l.pri_qty),0) as existPriQty" );
				sql.append("\n from t_stock_discount_line l ");
				sql.append("\n where 1=1");
				if( !Utils.isNull(curRequestNumber).equals("")){
				  sql.append("\n and l.request_number <> '"+curRequestNumber+"'");
				}
				sql.append("\n and l.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n and l.inventory_item_id ="+productId+"");
				sql.append("\n GROUP BY l.inventory_item_id ");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					existPriQty  = rst.getDouble("existPriQty");
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return existPriQty;
		}

	 public static double getPriQtyInt1(Connection conn,User user,String arInvoiceNo,String customerCode,String productId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double existPriQty = 0;
			try {
				sql.append("\n select COALESCE(sum(l.pri_qty),0) as init_pri_Qty" );
				sql.append("\n from pens.t_stock_discount_init l ");
				sql.append("\n where 1=1");
				sql.append("\n and l.customer_code = '"+customerCode+"'");
				sql.append("\n and l.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n and l.inventory_item_id ="+productId+"");
				sql.append("\n and l.user_id ="+user.getId()+"");
				sql.append("\n GROUP BY l.inventory_item_id ");
				
				//+ current store_return (requestNumber)
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					existPriQty  = rst.getDouble("init_pri_Qty");
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return existPriQty;
		}

	 /** Calc RemainAmount By 1(Arinvoice and product) or 2(arInvoice) **/
	 public static double calcStockDiscountRemianAmountByInvoice(Connection conn,String curRequestNumber
			 ,String arInvoiceNo,String productCode,String backDate,PopupForm c,User user) throws Exception {
		    Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double remainAmount = 0;
			try {
				//Case requestNumber is not null get back from stockDiscount
				if(Utils.isNull(backDate).equals("")){
					if( !Utils.isNull(c.getRequestNumber()).equals("")){
						Date backDateObj = new MStockDiscount().getBackDate(conn, Utils.isNull(c.getRequestNumber()));
						backDate = DateUtil.stringValue(backDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
					}else{
						// get back_date(from c_reference) for get data
						List<References> backDateInvoiceStockDiscountList = InitialReferences.getReferenceListByCode(conn,InitialReferences.BACKDATE_INVOICE_STOCKDISCOUNT);
						if(backDateInvoiceStockDiscountList != null ){
						   References refbackDate =  backDateInvoiceStockDiscountList.get(0);
						   Calendar curdate = Calendar.getInstance();
						   curdate.add(Calendar.MONTH, -1*Integer.parseInt(refbackDate.getKey()));
						   
						   logger.debug("backDate:"+curdate.getTime());
						   backDate = DateUtil.stringValue(curdate.getTime(), DateUtil.DD_MM_YYYY_WITH_SLASH);
						   //set to 01/mm/yyyy
						   backDate = "01/"+backDate.substring(3,backDate.length());
						   logger.debug("backDate:"+backDate);
						}
					}
				}
				
				sql.append("\n select h.ar_invoice_no,COALESCE(sum(l.total_amount),0) as remain_amount" );
				sql.append("\n ,( select sum(line_amount) from t_stock_discount_line d");
				sql.append("\n    where d.ar_invoice_no = h.ar_invoice_no");
				if( !Utils.isNull(curRequestNumber).equals("")){
				   sql.append("\n   and d.request_number <>'"+curRequestNumber+"'");
				}
				if( !Utils.isNull(productCode).equals("")){
				  // sql.append("\n   and d.inventory_item_id in(select product_id from m_product where code='"+c.getProductCode()+"')");
				}
				sql.append("\n  ) as discount_amount ");
				sql.append("\n from t_order h ,t_order_line l  ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and h.doc_status <> 'VO' ");
				sql.append("\n and l.iscancel <> 'Y' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				if( !Utils.isNull(productCode).equals("")){
				   // sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				}
				sql.append("\n and order_date >= STR_TO_DATE('"+backDate+"', '%d/%m/%Y') ");
				sql.append("\n and h.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n GROUP BY h.ar_invoice_no");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
					logger.debug("remain_amount:"+rst.getDouble("remain_amount"));
					logger.debug("discount_amount:"+rst.getDouble("discount_amount"));
					remainAmount = rst.getDouble("remain_amount")-rst.getDouble("discount_amount");
					
				}//while
				logger.debug("remainAmount:"+remainAmount);
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return remainAmount;
		}

}
