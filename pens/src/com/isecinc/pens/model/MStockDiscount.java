package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.StockDiscount;
import com.isecinc.pens.bean.StockDiscountLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PopupDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.document.StockReturnDocumentProcess;
import com.isecinc.pens.web.popup.PopupForm;


public class MStockDiscount {

	private Logger logger = Logger.getLogger("PENS"); 
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	public static String ORG_ID_DEFAULT = "84";
	
    private static double totalAmount = 0;
    
    public int getTotalRowStockReturn(Connection conn,StockDiscount bean,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int totalRow = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select count(*) as c from t_stock_return h ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and  h.user_id ="+user.getId()+"");
			
			if( bean.getCustomerId() != 0){
			    sql.append("\n  and  h.customer_id ='"+bean.getCustomerId()+"' \n");
		    }
		
			if( !Utils.isNull(bean.getRequestDateFrom()).equals("")
				&&	!Utils.isNull(bean.getRequestDateTo()).equals("")	){
				  sql.append(" and h.request_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(bean.getRequestDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				  sql.append(" and h.request_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(bean.getRequestDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
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
    public List<StockDiscount> searchStockReturnList(Connection conn ,StockDiscount mCriteria,User user,String whereCause,int start) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<StockDiscount> list = new ArrayList<StockDiscount>();
		StringBuilder sql = new StringBuilder();
		int no = 0;
		try {
			sql.append("\n select A.* from (");
			sql.append("\n  SELECT h.*, C.name as customer_name ,C.code as customer_code");
			sql.append("\n   from t_stock_return h, m_customer C");
			sql.append("\n  where C.customer_id=h.customer_id ");
			sql.append("\n  and  h.user_id ="+user.getId()+"");
			
			if( mCriteria.getCustomerId() != 0){
			    sql.append("\n  and  h.customer_id ='"+mCriteria.getCustomerId()+"' \n");
		    }
		
			if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
				&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
					
				  sql.append(" and h.request_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				  sql.append(" and h.request_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
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
			  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setDescription(Utils.isNull(rst.getString("description")));
			  m.setCustomerId(rst.getInt("customer_id"));
			  m.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			  m.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			 // m.setCustomerAddress(Utils.isNull("")));
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"¡��ԡ":"��ҹ");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"�觢���������":"�ѧ����觢�����");
			  
	          m.setDescription(Utils.isNull(rst.getString("description")));
			  m.setUserId(rst.getString("user_id")); 
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
			  
			  m.setCreated(Utils.stringValue(rst.getDate("created"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setUpdated(Utils.stringValue(rst.getDate("updated"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  
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
    public int getMaxLineNumberStockReturnLine(Connection conn,String requestNumber) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int maxLineId = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select max(line_number) as line_number from t_stock_return_line h ");
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
				
				Date requestDate = Utils.parse(head.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
				requestNumber = new StockReturnDocumentProcess().getNextDocumentNo(requestDate,user.getCode(),user.getId(), conn);
				
				//prepare MoveOrder
				logger.debug("requestNumber["+requestNumber+"]");
				//prepare MoveOrder Model
				head.setRequestNumber(requestNumber);

				head = insertStockReturn(conn , head);
				
			}else{
				updateStockReturn(conn , head);
			}
			
	        //insert or update line
			if(head.getLineList() != null && head.getLineList().size() > 0){
				//get max lineId
				int maxLineNumber =getMaxLineNumberStockReturnLine(conn, head.getRequestNumber());
				// Process normal
				for(int i =0;i< head.getLineList().size();i++){
					StockDiscountLine line = (StockDiscountLine)head.getLineList().get(i);
					line.setUpdateBy(head.getCreatedBy());
					
					if(line.getLineId() != 0){
						updateStockReturnLine(conn,head, line);
					}else{
						maxLineNumber++;
				        line.setLineId(maxLineNumber);
					    insertStockReturnLine(conn,head, line);
					}
					//set for no del line
					lineIdNodel += line.getLineId()+",";
				}//for
			}
			
			//delete lineNo
			if(head.getLineNoDeleteList() != null && head.getLineNoDeleteList().size() > 0){
				for(int i =0;i< head.getLineNoDeleteList().size();i++){
					String lineNumber = (String)head.getLineNoDeleteList().get(i);
					int r = deleteStockReturnLine(conn, head.getRequestNumber(),Utils.convertStrToInt(lineNumber));
					
					logger.debug("delete requestNumber["+head.getRequestNumber()+"]lineId["+lineNumber+"]result["+r+"]");
				}
			}
			
			//For case Exception in above function-> delete lineId is not insert or update
			if( !Utils.isNull(lineIdNodel).equals("")){
				lineIdNodel = lineIdNodel.substring(0,lineIdNodel.length()-1);
				
				int r = deleteStockReturnLineNotInLineId(conn, head.getRequestNumber(),lineIdNodel);
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
	
	public StockDiscount updateStockReturn(StockDiscount head) throws Exception {
		Connection conn = null;
		try {
		    conn = new DBCPConnectionProvider().getConnection(conn);
		    conn.setAutoCommit(false);
		    
		    updateStockReturn(conn,head);
		    
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
	
	private StockDiscount updateStockReturn(Connection conn ,StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_stock_return \n");
			sql.append(" set  request_date = ? ,description =? ");
			sql.append(" ,total_nonvat_amount =? ,total_vat_amount =?,total_amount =? \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(head.getRequestDate()).getTime()));//request_date
			ps.setString(++index, Utils.isNull(head.getDescription()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllNonVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllAmount()));
			ps.setDate(++index, Utils.getCurrentSqlDate());//updated
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
	
	public StockDiscount updateCancelStockReturn(StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_stock_return \n");
			sql.append(" set  status = ?  ,description = ? \n"); 
			sql.append(" ,updated =? ,updated_by = ? ,total_nonvat_amount=?,total_vat_amount=? ,total_amount=? \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//status
			ps.setString(++index, Utils.isNull(head.getDescription()));//Description
			ps.setDate(++index, Utils.getCurrentSqlDate());//updated
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllNonVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(head.getTotalAllAmount()));
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
	
	
	private StockDiscount insertStockReturn(Connection conn ,StockDiscount model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_return( \n");
			sql.append(" request_number, request_date, description, \n");
			sql.append(" status,exported, USER_ID, CREATED,CREATED_BY,customer_id \n");
			sql.append(",total_nonvat_amount,total_vat_amount,total_amount \n");
			sql.append(",back_date,updated) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
			  
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
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalAllNonVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalAllVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getTotalAllAmount()));
			ps.setDate(++index, new java.sql.Date((Utils.parse(model.getBackDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
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
	

	private boolean insertStockReturnLine(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_return_line( \n");
			sql.append(" request_number, line_number, inventory_item_id,ar_invoice_no, \n"); 
			sql.append(" pri_qty,uom1_qty,uom2_qty, uom2,uom1_pac,uom2_pac, \n");
			sql.append(" uom1_price,discount,total_amount, \n");
			//sql.append(" remain_pri_all_qty,remain_pri_qty,remain_sub_qty, \n");
			sql.append(" uom1_Conv_Rate,uom2_Conv_Rate, \n");
			sql.append(" status,exported, USER_ID, CREATED, CREATED_BY,UPDATED) \n");//24
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");//,?,?,?) \n");

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineId());//line_number
			ps.setInt(++index, Utils.convertStrToInt(line.getInventoryItemId()));//inventory_item_id
			ps.setString(++index, Utils.isNull(line.getArInvoiceNo()));//arInvoiceNo
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPriQty()));// priQty = calc from(uom1Qty and uom2Qty)
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Qty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2Qty()));//
			ps.setString(++index, Utils.isNull(line.getUom2()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom1Pac()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom2Pac()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Price()));//uom1 price/ ctn
			ps.setDouble(++index, Utils.convertStrToDouble(line.getDiscount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getTotalAmount()));//
			
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
	
	private boolean updateStockReturnLine(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_return_line set \n");
			sql.append(" inventory_item_id =?,ar_invoice_no =?,pri_qty =?, uom1_qty =? ,uom2_qty =? , uom2=? , \n");
			sql.append(" uom1_pac=?, uom2_pac=? ,uom1_price=? ,discount =? ,total_amount =? ,");
			//sql.append(" remain_pri_all_qty =?,remain_pri_qty=?,remain_sub_qty=?, \n");
			sql.append(" uom1_Conv_Rate=?,uom2_Conv_Rate=?, \n");
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, Utils.convertStrToInt(line.getInventoryItemId()));//inventory_item_id
			ps.setString(++index, Utils.isNull(line.getArInvoiceNo()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPriQty()));// preQty = calc from(Uom1Qty and uom2Tyq)
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Qty()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom2Qty()));//
			ps.setString(++index, Utils.isNull(line.getUom2()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom1Pac()));//
			ps.setInt(++index, Utils.convertStrToInt(line.getUom2Pac()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getUom1Price()));//uom1 price/ ctn
			ps.setDouble(++index, Utils.convertStrToDouble(line.getDiscount()));//
			ps.setDouble(++index, Utils.convertStrToDouble(line.getTotalAmount()));//
			
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
	
	private boolean updateStockReturnLineCaseLineCancel(Connection conn ,StockDiscount head,StockDiscountLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_return_line set \n");
			sql.append(" status = ? , \n"); 
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, line.getStatus());//status
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, Utils.getCurrentSqlDate());
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
	
	private boolean deleteStockReturnLineByRequestNumber(Connection conn ,StockDiscount head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete from t_stock_return_line   \n");
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
	private int deleteStockReturnLine(Connection conn ,String requestNumber,int lineNumber) throws Exception {
		int r= 0;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from t_stock_return_line  \n");
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
	private int deleteStockReturnLineNotInLineId(Connection conn ,String requestNumber,String notInLineId) throws Exception {
		int r= 0;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from t_stock_return_line  \n");
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
			sql.append("\n  SELECT back_date from t_stock_return h");
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
	
	public StockDiscount searchStockReturn(StockDiscount mCriteria, User user)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StockDiscount m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

			sql.append("\n  SELECT h.*, C.name as customer_name ,C.code as customer_code");
			sql.append("\n  from t_stock_return h, m_customer C");
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
			    m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    m.setBackDate(Utils.stringValue(rst.getDate("back_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
			    m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"¡��ԡ":"��ҹ");
			    m.setExported(rst.getString("exported"));
			    m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"�觢���������":"�ѧ����觢�����");
			  
	            m.setDescription(Utils.isNull(rst.getString("description")));
			    m.setUserId(rst.getString("user_id")); 
			    m.setCreatedBy(rst.getString("created_by"));
			    m.setUpdateBy(rst.getString("updated_by"));
			  
			    m.setCreated(Utils.stringValue(rst.getDate("created"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    m.setUpdated(Utils.stringValue(rst.getDate("updated"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    
			    m.setTotalAllNonVatAmount(Utils.convertDoubleToStr(rst.getDouble("total_nonvat_amount"),Utils.format_current_2_disgit));
			    m.setTotalAllVatAmount(Utils.convertDoubleToStr(rst.getDouble("total_vat_amount"),Utils.format_current_2_disgit));
			    m.setTotalAllAmount(Utils.convertDoubleToStr(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			    
			    // Check canEdit
				if ((STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED
						.equals(m.getExported()))) {
					m.setCanEdit(true);
				}
			}// while
			
			// Find Lines
			totalAmount = 0;
			m.setLineList(searchStockReturnLine(conn,user, m));
			
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
	  
	  public List<StockDiscountLine> searchStockReturnLine(Connection conn,User user,StockDiscount mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<StockDiscountLine> lineList = new ArrayList<StockDiscountLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			PopupForm popupForm = new PopupForm();
			try {
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* , (select p.code from m_product p where p.product_id = l.inventory_item_id)as code ");
				sql.append("\n   from t_stock_return_line l ");
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
				  Product mp = new MProduct().find(rst.getString("inventory_item_id"));
				  m.setProduct(mp);
				  m.setProductCode(mp.getCode());
				  m.setProductName(mp.getName());
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
				  List<PopupForm> results = PopupDAO.searchInvoiceStockReturn(popupForm,"",user);
				  if(results != null && results.size()>0){ 
					  PopupForm remain = results.get(0);
				      m.setRemainPriAllQty(remain.getPriAllQty());
				      m.setRemainPriQty(remain.getPriQty());
				      m.setRemainSubQty(remain.getSubQty());
				  }
				  m.setUom1Pac(Utils.convertDoubleToStrDefaultNoDigit(rst.getDouble("uom1_pac"),""));
				  m.setUom2Pac(Utils.convertDoubleToStrDefaultNoDigit(rst.getDouble("uom2_pac"),""));
				  m.setUom1Price(Utils.decimalFormat(rst.getDouble("uom1_price"),Utils.format_current_2_disgit));
				  m.setDiscount(Utils.decimalFormat(rst.getDouble("discount"),Utils.format_current_2_disgit));
				  m.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel("Y".equals(m.getStatus())?"��ҹ":"¡��ԡ");
				  m.setExported(rst.getString("exported"));
				
				  m.setUserId(rst.getString("user_id"));
				  m.setCreatedBy(rst.getString("created_by"));
				  
				  m.setCreated(Utils.stringValue(rst.getDate("created"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setUpdated(Utils.stringValue(rst.getDate("updated"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
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
				sql.append("\n  SELECT l.request_number ,l.line_number,l.qty from t_stock_return_line l ");
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
				sql.append("\n  SELECT max(l.line_number) as max_line_no from t_stock_return_line l ");
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
			
				sql.append(" update t_stock_return h \n");
				sql.append(" set total_nonvat_amount = ( \n");
				sql.append("   select ROUND(sum(total_amount),2) \n");
				sql.append("   from t_stock_return_line l where h.request_number = l.request_number \n");
				sql.append("   and l.status ='SV' \n");
				sql.append("  ) \n");
				sql.append(",total_vat_amount = ( \n");
				sql.append("   select ROUND(sum(total_amount) *0.07,2) \n");
				sql.append("   from t_stock_return_line l where h.request_number = l.request_number \n");
				sql.append("   and l.status ='SV' \n");
				sql.append(") \n");
				sql.append(",total_amount = ( \n");
				sql.append("   select ROUND( (sum(total_amount) +sum(total_amount) *0.07 ),2) \n");
				sql.append("   from t_stock_return_line l where h.request_number = l.request_number \n");
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
}
