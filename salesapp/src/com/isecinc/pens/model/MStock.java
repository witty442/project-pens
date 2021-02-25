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

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.StockLine;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.StockDocumentProcess;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;


public class MStock {

	private Logger logger = Logger.getLogger("PENS"); 
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	public static String ORG_ID_DEFAULT = "84";
	
	public static Map<String, String> MOVE_ORDER_TYPE_MAP1= new HashMap<String, String>();
    
    private static double totalAmount = 0;
    
	public Stock save(User user,Stock head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//Generate requestNumber
			String requestNumber  ="";
		
			if("".equals(head.getRequestNumber())){
				
				Date requestDate = DateUtil.parse(head.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			
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
				    line.setLineId((i+1));
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
			Date requestDateObj = DateUtil.parse(requestDateStr, DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
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
			ps.setDate(++index, DateUtil.getCurrentSqlDate());//updated
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
			ps.setDate(++index, DateUtil.getCurrentSqlDate());//updated
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
			sql.append(" status,exported, USER_ID, CREATED,CREATED_BY,customer_id,back_avg_month) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?) \n");
			  
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
			ps.setLong(++index, model.getCustomerId());
			ps.setInt(++index, Utils.convertStrToInt(model.getBackAvgMonth()));
			
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
			sql.append(" uom,uom2, status,amount,exported, USER_ID,  \n");
			sql.append(" CREATED, CREATED_BY,CREATE_DATE, \n");
			sql.append(" qty, qty2, qty3, sub,sub2,sub3 ,expire_date,expire_date2,expire_date3,avg_order_qty) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNumber());//request_number
			ps.setInt(++index, line.getLineId());//line_number
			ps.setInt(++index, Utils.convertStrToInt(line.getInventoryItemId()));//inventory_item_id
			
			String[] fullUom = line.getFullUom().split("\\/");
			ps.setString(++index, fullUom[0]);//uom1
			if(fullUom.length >1){
			   ps.setString(++index, fullUom[1]);//uom2
			}else{
			   ps.setString(++index, "");//uom2
			}
			ps.setString(++index, STATUS_SAVE);//status
			ps.setBigDecimal(++index, new BigDecimal(line.getAmount()));//amount
			
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, DateUtil.getCurrentSqlDate());
			ps.setString(++index, line.getCreatedBy());//CREATED_BY
			if( !Utils.isNull(line.getCreateDate()).equals("")){
			   ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getCreateDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			}else{
			   ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getQty())));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getQty2())));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getQty3())));//primary_quantity
			
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getSub())));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getSub2())));//primary_quantity
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getSub3())));//primary_quantity
			//expire 1
			if( !Utils.isNull(line.getExpireDate()).equals("")){
				logger.debug("expirDate:"+line.getExpireDate());
				ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
				logger.debug("expirDate:"+DateUtil.parse(line.getExpireDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			}else{
				ps.setNull(++index,java.sql.Types.DATE);
			}
			//expire 2
			if( !Utils.isNull(line.getExpireDate2()).equals("")){
				ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate2(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			}else{
				ps.setNull(++index,java.sql.Types.DATE);
			}
			//expire 3
			if( !Utils.isNull(line.getExpireDate3()).equals("")){
				ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate3(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			}else{
				ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setBigDecimal(++index, new BigDecimal(Utils.convertStrToDouble(line.getAvgOrderQty())));//abg_order_qty
			
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
			
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getCreateDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			
			ps.setString(++index, head.getUserId());//USER_ID
			ps.setDate(++index, DateUtil.getCurrentSqlDate());
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
						
					  sql.append(" and h.request_date >= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.request_date <= str_to_date('"+DateUtil.format(DateUtil.parseToBudishDate(mCriteria.getRequestDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
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
				  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setDescription(rst.getString("description"));

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
			  m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  requestDate = DateUtil.parse(m.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			  //Check 
			  if(requestDate != null){
				  if(currentDate.before(requestDate)){
					  m.setRequestDateDisabled(true);
				  }
			  }
			  
			  m.setDescription(rst.getString("description"));
			  m.setBackAvgMonth(Utils.isNull(rst.getString("back_avg_month")));
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
			  m.setUserId(rst.getString("user_id"));
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
			  
			  m.setCreated(DateUtil.stringValue(rst.getDate("created"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setUpdated(DateUtil.stringValue(rst.getDate("updated"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));			  
			  
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
				sql.append("\n  ) A ORDER BY A.line_number asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				  StockLine m = new StockLine();
				  no++;
				  m.setNo(no);
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setLineId(rst.getInt("line_number"));
				  m.setInventoryItemId(rst.getString("inventory_item_id"));
				  Product mp = new MProduct().find(rst.getString("inventory_item_id"));
				  m.setProduct(mp);
				  m.setProductCode(mp.getCode());
				  m.setProductName(mp.getName());
				  m.setAvgOrderQty(Utils.convertIntToStrDefault(rst.getInt("avg_order_qty"),""));
				  //set FullUOM
				  String  uom1 = Utils.isNull(Utils.isNull(rst.getString("uom")));
				  String  uom2 = Utils.isNull(Utils.isNull(rst.getString("uom2")));
				  
				  m.setFullUom(uom1+"/"+uom2);
				  m.setConversionRate(new MProduct().getConversionRate(Utils.convertStrToInt(m.getInventoryItemId()),uom1,uom2));
				  
				  m.setQty(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("qty"),""));
				  m.setQty2(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("qty2"),""));
				  m.setQty3(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("qty3"),""));
				  
				  m.setSub(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("sub"),""));
				  m.setSub2(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("sub2"),""));
				  m.setSub3(Utils.convertDoubleToStrDefaultNoDigit(rst.getInt("sub3"),""));
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel("Y".equals(m.getStatus())?"ใช้งาน":"ยกเลิก");
				  m.setExported(rst.getString("exported"));
				  
				  m.setExpireDate(DateUtil.stringValueDefault(rst.getDate("expire_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th,""));
				  m.setExpireDate2(DateUtil.stringValueDefault(rst.getDate("expire_date2"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th,""));
				  m.setExpireDate3(DateUtil.stringValueDefault(rst.getDate("expire_date3"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th,""));
				  
				  m.setUserId(rst.getString("user_id"));
				  m.setCreatedBy(rst.getString("created_by"));
				  
				  m.setCreated(DateUtil.stringValue(rst.getDate("created"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setUpdated(DateUtil.stringValue(rst.getDate("updated"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
				  m.setCreateDate(DateUtil.stringValue(rst.getDate("create_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setExpireDate(DateUtil.stringValue(rst.getDate("expire_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
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
	  
	  
	  public List<StockLine> searchAvgStockLine(Stock mCriteria,User user) throws Exception {
		    Connection conn = null;
			Statement stmt = null;
			ResultSet rst = null;
			List<StockLine> lineList = new ArrayList<StockLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			String arInvoieNoMonth = "";
			Calendar cal = Calendar.getInstance();
			try {
				//Connection
				conn = DBConnection.getInstance().getConnection();
				
				// Calc Back Avg Month
				String yyMM = "";
				arInvoieNoMonth ="";
				for(int i=0;i<Utils.convertStrToInt(mCriteria.getBackAvgMonth());i++){
					cal.add(Calendar.MONTH, -1);
					yyMM = DateUtil.stringValue(cal.getTime(), DateUtil.YY_MM,Utils.local_th);
					arInvoieNoMonth +="'"+yyMM+"',";
				}
				if( !Utils.isNull(arInvoieNoMonth).equals("")){
					arInvoieNoMonth = arInvoieNoMonth.substring(0,arInvoieNoMonth.length()-1);
				}

				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n  SELECT ");
				sql.append("\n  t.product_id ,m.product_code ,m.product_name"); 
				sql.append("\n  ,m.uom1 ,m.uom2 "); 
				sql.append("\n  ,CEIL((sum(qty))/"+mCriteria.getBackAvgMonth()+") as avg_order_qty");
				sql.append("\n  FROM t_order_line t ");
				sql.append("\n ,( ");
				sql.append("\n  SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE");
				sql.append("\n  , pp1.UOM_ID as UOM1 , pp2.UOM_ID as UOM2 ");
				sql.append("\n  FROM M_Product pd ");
				
				sql.append("\n  INNER JOIN (");
				sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE from M_Product_Price ");
				sql.append("\n    where ISACTIVE = 'Y' ");
				sql.append("\n  )pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
				
				sql.append("\n  LEFT JOIN (");
				sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE from M_Product_Price ");
				sql.append("\n    where ISACTIVE = 'Y' ");
				sql.append("\n  )pp2 ON pd.Product_ID = pp2.Product_ID AND pp2.UOM_ID <> pd.UOM_ID ");
			
				sql.append("\n  WHERE 1=1");
				sql.append("\n  AND ( ");
				sql.append("\n    pp1.UOM_ID IN ( ");
				sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
				sql.append("\n     ) ");
				sql.append("\n     OR");
				sql.append("\n     pp2.UOM_ID IN ( ");
				sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
				sql.append("\n      ) ");
				sql.append("\n    )");
				sql.append("\n   AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+user.getRole().getKey()+"') ");
				sql.append("\n  )m");
				
				sql.append("\n   WHERE t.product_id = m.product_id ");
				sql.append("\n   and t.promotion <> 'Y' ");
				sql.append("\n   and t.iscancel <> 'Y' ");
				sql.append("\n   and t.ar_invoice_no is not null ");
				sql.append("\n   and t.ar_invoice_no <> '' ");
				sql.append("\n   and ( ");
				sql.append("\n       t.order_id in( ");
				sql.append("\n         select order_id from t_order where doc_status ='SV' ");
				sql.append("\n         and (    substr(ar_invoice_no,3,4) in ("+arInvoieNoMonth+") ");
				sql.append("\n               or substr(ar_invoice_no,4,4) in ("+arInvoieNoMonth+") )");
				sql.append("\n         and user_id ="+user.getId());
				sql.append("\n         and customer_id ="+mCriteria.getCustomerId());
				sql.append("\n       ) ");
				sql.append("\n       and (   substr(t.ar_invoice_no,3,4) in ("+arInvoieNoMonth+")");
				sql.append("\n            or substr(t.ar_invoice_no,4,4) in ("+arInvoieNoMonth+") )");
				sql.append("\n    ) ");
				sql.append("\n   group by t.product_id ,m.product_code ,m.product_name,m.uom1 ,m.uom2 "); 
				sql.append("\n  ) A ORDER BY A.product_code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				  StockLine m = new StockLine();
				  no++;
				  m.setNo(no);
				  m.setRequestNumber("");
				  m.setLineId(no);
				  m.setInventoryItemId(rst.getString("product_id"));
				  m.setProductCode(rst.getString("product_code"));
				  m.setProductName(rst.getString("product_name"));
				  m.setAvgOrderQty(Utils.convertIntToStrDefault(rst.getInt("avg_order_qty"),""));
				  
				  //set FullUOM
				  String  uom1 = Utils.isNull(Utils.isNull(rst.getString("uom1")));
				  String  uom2 = Utils.isNull(Utils.isNull(rst.getString("uom2")));
				   
				  m.setFullUom(uom1+"/"+uom2);
				  m.setConversionRate(new MProduct().getConversionRate(Utils.convertStrToInt(m.getInventoryItemId()),uom1,uom2));
				  m.setStatus("SV");
				  m.setStatusLabel("ใช้งาน");
				  m.setExported("N");
				  m.setUserId(user.getId()+"");
				  m.setCreatedBy(mCriteria.getCreatedBy());
				  m.setCreated(DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setActionDate(m.getCreated());
			      m.setCanEdit(true);

				  lineList.add(m);
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
				  m.setLineId(rst.getInt("line_number"));
				  m.setQty(Utils.convertDoubleToStrDefaultNoDigit(rst.getDouble("qty"),""));
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
