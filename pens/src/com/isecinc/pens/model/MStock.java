package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.StockLine;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.StockDocumentProcess;


public class MStock {

	private Logger logger = Logger.getLogger("PENS"); 
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	public static String ORG_ID_DEFAULT = "84";
	
	public static Map<String, String> MOVE_ORDER_TYPE_MAP = new HashMap<String, String>();
    
    private static double totalAmount = 0;
    
	public Stock save(User user,Stock head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//Genearte MoveOrderNo
			String requestNumber  ="";
		
			if("".equals(head.getRequestNumber())){
				
				Date requestDate = Utils.parse(head.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
				requestNumber = new StockDocumentProcess().getNextDocumentNo(requestDate,user.getCode(),user.getId(), conn);
				
				//prepare MoveOrder
				logger.debug("requestNumber["+requestNumber+"]");
				//prepare MoveOrder Model
				head.setRequestNumber(requestNumber);

				head = insertStock(conn , head);
				
			}else{
				updateStock(conn , head);
			}
	
			if(head.getLineList() != null && head.getLineList().size() > 0){
				
				//delete lines
				deleteStockLineByRequestNumber(conn, head);
				
				// Process normal
				for(int i =0;i< head.getLineList().size();i++){
					StockLine line = (StockLine)head.getLineList().get(i);
				    line.setLineNo((i+1));
					line.setQty(line.getQty1());
					line.setUom(line.getUom1());
					line.setCreatedBy(head.getCreatedBy());
	
					insertStockLine(conn,head, line);
					
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
			Date requestDateObj = Utils.parse(requestDateStr, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
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
	
	public Stock updateStock(Stock head) throws Exception {
		Connection conn = null;
		try {
		    conn = new DBCPConnectionProvider().getConnection(conn);
		    conn.setAutoCommit(false);
		    
		    updateStock(conn,head);
		    
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
	
	private Stock updateStock(Connection conn ,Stock head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_stock \n");
			sql.append(" set  request_date = ? ,description =?  \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(head.getRequestDate()).getTime()));//request_date
			ps.setString(++index, Utils.isNull(head.getDescription()));
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
	
	public Stock updateCancelStock(Stock head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_stock \n");
			sql.append(" set  status = ?  ,description = ? \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//status
			ps.setString(++index, Utils.isNull(head.getDescription()));//Description
			ps.setDate(++index, Utils.getCurrentSqlDate());//updated
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
	
	
	private Stock insertStock(Connection conn ,Stock model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock( \n");
			sql.append(" request_number, request_date, description, \n");
			sql.append(" status,exported, USER_ID, CREATED,CREATED_BY,customer_id) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, model.getRequestNumber());//request_number
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getRequestDate()).getTime()));//request_date
			ps.setString(++index, Utils.isNull(model.getDescription()));//description
			ps.setString(++index, STATUS_SAVE);//status
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, model.getUserId());
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));
			ps.setString(++index, model.getCreatedBy());
			ps.setInt(++index, model.getCustomerId());
			
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
			StringBuffer sql = new StringBuffer("delete from t_stock_line where request_number =? \n");
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
	
	private boolean insertStockLine(Connection conn ,Stock head,StockLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_line( \n");
			sql.append(" request_number, line_number, inventory_item_id, \n"); 
			sql.append(" qty, uom, status,amount,   \n");
			sql.append(" exported, USER_ID, CREATED, CREATED_BY,CREATE_DATE,EXPIRE_DATE) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) \n");

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			ps.setInt(++index, line.getProduct().getId());//inventory_item_id
			ps.setBigDecimal(++index, new BigDecimal(line.getQty()));//primary_quantity
			ps.setString(++index, line.getUom().getId());//uom
			ps.setString(++index, STATUS_SAVE);//status
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount()));//amount
			
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, Utils.getCurrentSqlDate());
			ps.setString(++index, line.getCreatedBy());//CREATED_BY
			if( !Utils.isNull(line.getCreateDate()).equals("")){
			   ps.setDate(++index, new java.sql.Date(Utils.parse(line.getCreateDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
			   ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			
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
	
	private boolean updateStockLine(Connection conn ,Stock head,StockLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_line set \n");
			sql.append(" inventory_item_id =?, qty =? ,uom =? ,   \n");
			sql.append(" create_date =?,expire_date=?,  \n");
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_number=? and line_number = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, line.getProduct().getId());//inventory_item_id
			ps.setBigDecimal(++index, new BigDecimal(line.getQty()));//primary_quantity
			ps.setString(++index, line.getUom().getId());//uom
			
			ps.setDate(++index, new java.sql.Date(Utils.parse(line.getCreateDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, Utils.getCurrentSqlDate());
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
	
	private boolean updateStockLineCaseLineCancel(Connection conn ,Stock head,StockLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_line set \n");
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
	
	private boolean deleteStockLineByRequestNumber(Connection conn ,Stock head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete from t_stock_line   \n");
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
	
	 public List<Stock> searchStockList(Stock mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<Stock> list = new ArrayList<Stock>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT h.*  from t_stock h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				
				if( mCriteria.getCustomerId() != 0){
				    sql.append("\n  and  h.customer_id ='"+mCriteria.getCustomerId()+"'");
			    }
			
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
						
					  sql.append(" and h.request_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.request_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.request_number desc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  Stock m = new Stock();
				  m.setNo(no+"");
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setDescription(rst.getString("description"));

				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
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
					if(conn != null){
					   conn.close();conn=null;
					}
				} catch (Exception e) {}
			}
			return list;
	}
	
    public Stock searchStock(Stock mCriteria,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Stock m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Date currentDate = new Date();
		Date requestDate = null;
		try {
			conn = new  DBCPConnectionProvider().getConnection(conn);
			
			sql.append("\n  SELECT h.* ");
		
			sql.append("\n  from t_stock h ");
			sql.append("\n  where h.request_number ='"+mCriteria.getRequestNumber()+"'");
			sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
			sql.append("\n  ORDER BY h.request_date desc  \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
			  m = mCriteria;
			  m.setCustomerId(rst.getInt("customer_id"));
			  m.setRequestNumber(rst.getString("request_number"));
			  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  requestDate = Utils.parse(m.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			  //Check 
			  if(requestDate != null){
				  if(currentDate.before(requestDate)){
					  m.setRequestDateDisabled(true);
				  }
			  }
			  
			  m.setDescription(rst.getString("description"));
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
			  m.setUserId(rst.getString("user_id"));
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
			  
			  m.setCreated(Utils.stringValue(rst.getDate("created"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setUpdated(Utils.stringValue(rst.getDate("updated"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));			  
			  
			  //Check canEdit
			  if(  (STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) 
				){
				  m.setCanEdit(true);
			  }

			}//while
			//Find Lines
			totalAmount = 0;
			m.setLineList(searchStockLine(conn,m));
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
	  
	  public List<StockLine> searchStockLine(Connection conn,Stock mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<StockLine> lineList = new ArrayList<StockLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* , (select p.code from m_product p where p.product_id = l.inventory_item_id)as code   from t_stock_line l ");
				sql.append("\n   WHERE l.request_number ='"+mCriteria.getRequestNumber()+"'");
				sql.append("\n   and l.status ='SV' ");
				sql.append("\n  ) A ORDER BY A.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				  StockLine m = new StockLine();
				  no++;
				  m.setNo(no);
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineNo(rst.getInt("line_number"));
				  Product mp = new MProduct().find(rst.getString("inventory_item_id"));
				  m.setProduct(mp);
				  m.setProductCode(mp.getCode());
				  m.setProductName(mp.getName());
				  
				  m.setUom1(new MUOM().find(rst.getString("uom")));
				  //m.setUom2(new MUOM().find(rst.getString("uom2")));
				  
				 // logger.debug("uom1:"+rst.getString("uom1")+",uom2:"+rst.getString("uom2"));
				 // logger.debug("uom1:"+m.getUom1().getCode()+",uom2:"+m.getUom2().getCode());
				  
				  //set FullUOM
				  String  fullUom = Utils.isNull(m.getUom1().getCode());
				 
				  m.setFullUom(fullUom);
				  
				  m.setQty(rst.getDouble("qty"));
				  m.setQty1(rst.getDouble("qty"));
				  m.setQty2(0);
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel("Y".equals(m.getStatus())?"ใช้งาน":"ยกเลิก");
				  m.setExported(rst.getString("exported"));
				  
				  m.setUserId(rst.getString("user_id"));
				  m.setCreatedBy(rst.getString("created_by"));
				
				  //m.setAmount1(rst.getDouble("amount1"));
				  //m.setAmount2(rst.getDouble("amount2"));
				  //m.setTotalAmount(rst.getDouble("total_amount"));
				  
				  m.setCreated(Utils.stringValue(rst.getDate("created"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setUpdated(Utils.stringValue(rst.getDate("updated"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
				  m.setCreateDate(Utils.stringValue(rst.getDate("create_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setExpireDate(Utils.stringValue(rst.getDate("expire_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
				  //logger.debug("updated_long["+rst.getLong("updated_long")+"]");
				  m.setActionDate(m.getCreated());
				
				  //Sum TotalAMount
				//  totalAmount += totalAmount +m.getTotalAmount();
				  
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
	  public StockLine checkLineExist(Connection conn,String requestNumber,int lineNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StockLine m = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.request_number ,l.line_number,l.qty from t_stock_line l ");
				sql.append("\n  where l.request_number ='"+requestNumber+"'");
				sql.append("\n  and l.line_number ="+lineNo+"");
				
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				if (rst.next()) {
				  m = new StockLine();
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
				sql.append("\n  SELECT max(l.line_number) as max_line_no from t_stock_line l ");
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
	  
	  
}
