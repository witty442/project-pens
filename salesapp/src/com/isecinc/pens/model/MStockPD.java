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
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.StockPD;
import com.isecinc.pens.bean.StockPDLine;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.MoveOrderReqDocumentProcess;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;
import com.pens.util.NumberToolsUtil;

public class MStockPD {

	private Logger logger = Logger.getLogger("PENS");
    
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
	public static String ORG_ID_DEFAULT = "84";
	
    private static double totalAmount = 0;
    
	public StockPD save(User user,StockPD head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//Genearte MoveOrderNo
			String requestNumber  ="";
			if("".equals(head.getRequestNumber())){
				
				//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
				head = checkRequestDate(head);
				Date requestDate = Utils.parse(head.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				requestNumber = new MoveOrderReqDocumentProcess().getNextDocumentNo(requestDate,user.getCode(), head.getPdCode(),user.getId(), conn);
				
				//prepare MoveOrder
				//logger.debug("MoveOrderType["+head.getMoveOrderType()+"]requestNumber["+requestNumber+"]");
				//prepare MoveOrder Model
				head.setRequestNumber(requestNumber);

				head = insertStockPD(conn , head);
				
			}else{
				//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
				head = checkRequestDate(head);
				
				updateStockPD(conn , head);
			}
	
			if(head.getStockPDLineList() != null && head.getStockPDLineList().size() > 0){
				
				//delete lines
				deleteLineByRequestNumber(conn, head);
				// Process normal
				for(int i =0;i< head.getStockPDLineList().size();i++){
					StockPDLine line = (StockPDLine)head.getStockPDLineList().get(i);
					//Insert 
					line.setLineNo(getNextLineNo(conn, head.getRequestNumber()));
					line.setQty(calcQty(line));
					line.setPack(calcPack(line));
					line.setCreatedBy(head.getCreatedBy());
					insertStockPDLine(conn,head, line);
					
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
	
	//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
	private StockPD checkRequestDate(StockPD head) {
		try{
			List<References> refList = InitialReferences.getReferenes().get(InitialReferences.MOVEORDER);
			//logger.info("Integer.parseInt(refList.get(0).getKey()):"+Integer.parseInt(refList.get(0).getKey()));
			int diffDayToEndConfig = refList != null? Integer.parseInt(refList.get(0).getKey())-1:1;
			
            Calendar currentDate = Calendar.getInstance();
			
			Date requestDateObj = Utils.parse(head.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
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
				
				String requestDateStr = Utils.stringValue(currentDate.getTime(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				head.setRequestDate(requestDateStr);
				System.out.println("requestDate :"+head.getRequestDate());
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return head;
	}
	
	
	private double calcQty(StockPDLine line) throws Exception{
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

	private double calcPack(StockPDLine line) throws Exception{
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
	
	public StockPD updateStockPD(StockPD head) throws Exception {
		Connection conn = null;
		try {
		    conn = new DBCPConnectionProvider().getConnection(conn);
		    conn.setAutoCommit(false);
		    
		    updateStockPD(conn,head);
		    
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
	
	private StockPD updateStockPD(Connection conn ,StockPD head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_stock_pd \n");
			sql.append(" set  request_date = ?  \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where request_number =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(head.getRequestDate()).getTime()));//request_date
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));//updated
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
	
	
	
	private StockPD insertStockPD(Connection conn ,StockPD model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_pd( \n");
			sql.append(" request_number, request_date, organization_id, \n");
			sql.append(" sales_code, pd_code, description, \n");
			sql.append(" status,   \n");
			sql.append(" exported, USER_ID, CREATED,CREATED_BY ) \n");
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
			ps.setString(++index, STATUS_SAVE);//status
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, model.getUserId());
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));
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
	
	private boolean deleteLineByRequestNumber(Connection conn ,StockPD head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete from t_stock_pd_line where request_number =? \n");
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
	
	private boolean insertStockPDLine(Connection conn ,StockPD head,StockPDLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_stock_pd_line( \n");
			sql.append(" request_number, line_number, inventory_item_id, \n"); 
			sql.append(" qty, qty1, qty2, \n"); 
			sql.append(" uom1, uom2, status,  \n");
			sql.append(" amount1, amount2, total_amount,  \n");
			sql.append(" exported, USER_ID, CREATED, CREATED_BY,pack) \n");
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
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));
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
	
	private boolean updateStockPDLine(Connection conn ,StockPD head,StockPDLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_stock_pd_line set \n");
			sql.append(" inventory_item_id =? ,pack = ? , \n"); 
			sql.append(" qty =? , qty1 = ? , qty2 =?, \n"); 
			sql.append(" uom1 =? , uom2 = ? ,  \n");
			sql.append(" amount1 =?, amount2=?, total_amount=?,  \n");
			sql.append(" USER_ID=?, UPDATED =?, UPDATED_BY =?  \n");
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
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));
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
	
	 public List<StockPD> searchStockPDList(StockPD mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<StockPD> list = new ArrayList<StockPD>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n h.request_number,h.request_date,h.organization_id, ");
				sql.append("\n h.sales_code,h.pd_code,h.description,");
				sql.append("\n h.status, ");
				sql.append("\n h.print_date_long,h.exported,h.USER_ID,");
				sql.append("\n h.CREATED,h.CREATED_BY,h.UPDATED,h.UPDATED_BY, ");
				sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
				sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
				sql.append("\n  from t_stock_pd h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
						
					  sql.append(" and h.request_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.request_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.request_date desc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  StockPD m = new StockPD();
				  m.setNo(no+"");
				  m.setRequestNumber(rst.getString("request_number"));
				  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setOrganizationId(rst.getString("organization_id"));
				  
				  m.setSalesCode(rst.getString("sales_code"));
				  m.setSalesDesc(rst.getString("sales_desc"));
				  
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
				  
				  m.setDescription(rst.getString("description"));
				 
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
				  m.setUserId(rst.getString("user_id")); 
				  m.setCreatedBy(rst.getString("created_by"));
				  m.setUpdateBy(rst.getString("updated_by"));
				  
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
	
    public StockPD searchStockPD(StockPD mCriteria,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StockPD m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Date currentDate = new Date();
		Date requestDate = null;
		try {
			conn = new  DBCPConnectionProvider().getConnection(conn);
			
			sql.append("\n  SELECT ");
			sql.append("\n h.request_number,h.request_date,h.organization_id, ");
			sql.append("\n h.sales_code,h.pd_code,h.description,");
			sql.append("\n h.status,");
			sql.append("\n h.exported,h.USER_ID,");
			sql.append("\n h.CREATED_BY,h.UPDATED_BY, ");
			sql.append("\n h.CREATED, h.UPDATE ");
			sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
			sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
			sql.append("\n  from t_stock_pd h ");
			sql.append("\n  where h.request_number ='"+mCriteria.getRequestNumber()+"'");
			sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
			sql.append("\n  ORDER BY h.request_date desc  \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
			  m = mCriteria;
			  m.setRequestNumber(rst.getString("request_number"));
			  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  requestDate = Utils.parse(m.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
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
			  
			  m.setStatus(rst.getString("status"));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
			  m.setExported(rst.getString("exported"));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
			  m.setUserId(rst.getString("user_id"));
			  m.setCreatedBy(rst.getString("created_by"));
			  m.setUpdateBy(rst.getString("updated_by"));
						  
			  //Check canEdit
			  if(  (STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) 
				){
				  m.setCanEdit(true);
			  }
			  
			  

			}//while
			//Find Lines
			totalAmount = 0;
			m.setStockPDLineList(searchStockPDLine(conn,m));
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
	  
	  public List<StockPDLine> searchStockPDLine(Connection conn,StockPD mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<StockPDLine> lineList = new ArrayList<StockPDLine>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* , (select p.code from m_product p where p.product_id = l.inventory_item_id)as code   from t_stock_pd_line l ");
				sql.append("\n   WHERE l.request_number ='"+mCriteria.getRequestNumber()+"'");
				sql.append("\n   and l.status ='SV' ");
				sql.append("\n  ) A ORDER BY A.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				StockPDLine m = new StockPDLine();
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
	  public StockPDLine checkLineExist(Connection conn,String requestNumber,int lineNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StockPDLine m = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.request_number ,l.line_number,l.qty from t_stock_pd_line l ");
				sql.append("\n  where l.request_number ='"+requestNumber+"'");
				sql.append("\n  and l.line_number ="+lineNo+"");
				
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				if (rst.next()) {
				  m = new StockPDLine();
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
				sql.append("\n  SELECT max(l.line_number) as max_line_no from t_stock_pd_line l ");
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
