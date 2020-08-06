package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.RequestPromotion;
import com.isecinc.pens.bean.RequestPromotionCost;
import com.isecinc.pens.bean.RequestPromotionLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.ReqPromotionDocumentProcess;
import com.pens.util.DBCPConnectionProvider;

public class MRequestPromotion {

	private Logger logger = Logger.getLogger("PENS");
    
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
    
	public RequestPromotion save(User user,RequestPromotion head) throws Exception {
		Connection conn = null;
		String requestNo  ="";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);

			if("".equals(head.getRequestNo())){
			    logger.debug("-->Insert Db -->");
			    
				requestNo = new ReqPromotionDocumentProcess().getNextDocumentNo(new Date(),user.getCode(),user.getId(), conn);
				head.setRequestNo(requestNo);
                
				//insert head
				head = insertRequestPromotion(conn , head);
				
				//insert cost line
				if(head.getCostLineList() != null &&  head.getCostLineList().size() >0){
					for(int i=0;i<head.getCostLineList().size();i++){
						RequestPromotionCost c =  head.getCostLineList().get(i);
						c.setRequestNo(head.getRequestNo());
						c.setCreatedBy(head.getCreatedBy());
						
						if( !Utils.isNull(c.getCostDetail()).equals("")){
						   insertPromotionCost(conn, c);
						}
					}
				}
				
				//insert product line
				if(head.getPromotionLineList() != null &&  head.getPromotionLineList().size() >0){
					for(int i=0;i<head.getPromotionLineList().size();i++){
						RequestPromotionLine c =  head.getPromotionLineList().get(i);
						c.setRequestNo(head.getRequestNo());
						c.setCreatedBy(head.getCreatedBy());
						
						if( !Utils.isNull(c.getProductCode()).equals("")){
						  insertReqPromotionLine(conn,head,c);
						}
					}
				}
				
			}else{
				logger.debug("-->Update Db -->");

				updateReqPromotion(conn,head);
				
				//insert cost line
				if(head.getCostLineList() != null &&  head.getCostLineList().size() >0){
					for(int i=0;i<head.getCostLineList().size();i++){
						RequestPromotionCost c =  head.getCostLineList().get(i);
						c.setRequestNo(head.getRequestNo());
						c.setCreatedBy(head.getCreatedBy());
						
						//if( !Utils.isNull(c.getCostDetail()).equals("")){
							//update
							boolean r = updateReqPromotionCost(conn, head, c);
							if(r==false){
							   insertPromotionCost(conn, c);
							}
						//}
					}
				}
				
				//insert product line
				if(head.getPromotionLineList() != null &&  head.getPromotionLineList().size() >0){
					for(int i=0;i<head.getPromotionLineList().size();i++){
						RequestPromotionLine c =  head.getPromotionLineList().get(i);
						c.setRequestNo(head.getRequestNo());
						c.setCreatedBy(head.getCreatedBy());
						
						if( !Utils.isNull(c.getProductCode()).equals("")){
							//update
							boolean r = updateReqPromotionLine(conn, head, c);
							if(r==false){
							   insertReqPromotionLine(conn,head,c);
							}
						}
					}
				}
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

	/** case Update set exported ='N' allway can edit alltime */
	public RequestPromotion updateReqPromotion(Connection conn,RequestPromotion head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer("update t_req_promotion \n");
			sql.append(" set  name=? ,remark = ? \n"); 
			sql.append(" ,updated =? ,updated_by = ? ,product_catagory =? ,product_type =? ," +
					" customer_code =?,phone =? ,promotion_start_date =? ,promotion_end_date =? ,exported ='N' \n"); 
			sql.append(" where request_no =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getName());//status
			ps.setString(++index, head.getRemark());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, head.getCreatedBy());
			ps.setString(++index, head.getProductCatagory());//
			ps.setString(++index, head.getProductType());//
			ps.setString(++index, head.getCustomerCode());//
			ps.setString(++index, head.getPhone());//
			
			if( !Utils.isNull(head.getPromotionStartDate()).equals("")){
				Date d = Utils.parse(head.getPromotionStartDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
				ps.setDate(++index,new java.sql.Date(d.getTime()));//
			}
			if( !Utils.isNull(head.getPromotionEndDate()).equals("")){
				Date d = Utils.parse(head.getPromotionEndDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
				ps.setDate(++index,new java.sql.Date(d.getTime()));//
			}
			
			ps.setString(++index, head.getRequestNo());//request_number

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
	
	public RequestPromotion updateCancelRequestPromotion(RequestPromotion head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_req_promotion \n");
			sql.append(" set  status = ?  ,remark = ? \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where request_no =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//status
			ps.setString(++index, head.getRemark());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, head.getCreatedBy());
			ps.setString(++index, head.getRequestNo());//request_number

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
	
	public RequestPromotion updatePrintDateRequestPromotion(RequestPromotion head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("update t_req_promotion \n");
			sql.append(" set  print_date = ?  \n"); 
			sql.append("   \n"); 
			sql.append(" where request_no =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, head.getRequestNo());//request_number

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
	
	private RequestPromotionCost insertPromotionCost(Connection conn ,RequestPromotionCost model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_req_promotion_cost( \n");
			sql.append(" request_no, line_no, \n");
			sql.append(" cost_detail,cost_amount,CREATED,CREATED_BY) \n");
			sql.append(" VALUES (?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, model.getRequestNo());//request_number
			ps.setInt(++index, model.getLineNo());//
			ps.setString(++index, model.getCostDetail());//
			ps.setBigDecimal(++index, model.getCostAmount());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
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
	  
	private RequestPromotion insertRequestPromotion(Connection conn ,RequestPromotion model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_req_promotion( \n");
			sql.append(" request_no, request_date, product_catagory, \n");
			sql.append(" product_type, customer_code, phone, \n");
			sql.append(" name, remark, promotion_start_date,promotion_end_date,  \n");
			sql.append(" status,USER_ID, CREATED,CREATED_BY ) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, model.getRequestNo());//request_number
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getRequestDate()).getTime()));//request_date
			ps.setString(++index, model.getProductCatagory());//
			ps.setString(++index, model.getProductType());//
			ps.setString(++index, model.getCustomerCode());//
			ps.setString(++index, model.getPhone());//
			ps.setString(++index, model.getName());//
			ps.setString(++index, model.getRemark());//
			
			if( !Utils.isNull(model.getPromotionStartDate()).equals("")){
				Date d = Utils.parse(model.getPromotionStartDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
				ps.setDate(++index,new java.sql.Date(d.getTime()));//
			}
			if( !Utils.isNull(model.getPromotionEndDate()).equals("")){
				Date d = Utils.parse(model.getPromotionEndDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
				ps.setDate(++index,new java.sql.Date(d.getTime()));//
			}
			ps.setString(++index, model.getStatus());//
			ps.setString(++index, model.getUserId());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
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
	

	private boolean insertReqPromotionLine(Connection conn ,RequestPromotion head,RequestPromotionLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_req_promotion_line( \n");
			sql.append(" request_no, line_no, product_code, \n"); 
			sql.append(" NEW_CTN, NEW_AMOUNT, STOCK_CTN, \n"); 
			sql.append(" STOCK_QTY, BORROW_CTN,  \n");
			sql.append(" BORROW_QTY, BORROW_AMOUNT, CREATED,CREATED_BY,INVOICE_NO ) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) \n");

			//logger.debug("SQL:"+sql);
			logger.debug("Insert DB request_no["+line.getRequestNo()+"]line_no["+line.getLineNo()+"]productCode["+line.getProductCode()+"]");
			
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, head.getRequestNo());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			ps.setString(++index, line.getProductCode());
			ps.setBigDecimal(++index, line.getNewCtn());//
			ps.setBigDecimal(++index, line.getNewAmount());//
			ps.setBigDecimal(++index, line.getStockCtn());//
			ps.setBigDecimal(++index, line.getStockQty());//
			ps.setBigDecimal(++index, line.getBorrowCtn());//
			ps.setBigDecimal(++index, line.getBorrowQty());//
			ps.setBigDecimal(++index, line.getBorrowAmount());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getCreatedBy());
			ps.setString(++index, Utils.isNull(line.getInvoiceNo()));
			
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
	
	private boolean updateReqPromotionCost(Connection conn ,RequestPromotion head,RequestPromotionCost line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_req_promotion_cost set \n");
			sql.append(" COST_DETAIL =? ,COST_AMOUNT = ? ,  \n");
			sql.append(" UPDATED =?, UPDATED_BY =?  \n");
			sql.append(" WHERE request_no=? and line_no = ? \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
	
			ps.setString(++index, line.getCostDetail());//
			ps.setBigDecimal(++index, line.getCostAmount());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getCreatedBy());
			
			ps.setString(++index, head.getRequestNo());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("update:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private boolean updateReqPromotionLine(Connection conn ,RequestPromotion head,RequestPromotionLine line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("update t_req_promotion_line set \n");
			sql.append(" product_code =? ,NEW_CTN = ? , \n"); 
			sql.append(" NEW_AMOUNT =? , STOCK_CTN = ? , STOCK_QTY =?, \n"); 
			sql.append(" BORROW_CTN =? ,BORROW_QTY =? , BORROW_AMOUNT = ? ,  \n");
			sql.append(" UPDATED =?, UPDATED_BY =?  ,INVOICE_NO = ? \n");
			sql.append(" WHERE request_no=? and line_no = ? \n");
			
			logger.debug("Update DBL request_no["+line.getRequestNo()+"]line_no["+line.getLineNo()+"]productCode["+line.getProductCode()+"]");
			
			
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, line.getProductCode());//
			ps.setBigDecimal(++index, line.getNewCtn());//
			ps.setBigDecimal(++index, line.getNewAmount());//
			ps.setBigDecimal(++index, line.getStockCtn());//
			ps.setBigDecimal(++index, line.getStockQty());//
			ps.setBigDecimal(++index, line.getBorrowCtn());//
			ps.setBigDecimal(++index, line.getBorrowQty());//
			ps.setBigDecimal(++index, line.getBorrowAmount());//
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getCreatedBy());
			ps.setString(++index, Utils.isNull(line.getInvoiceNo()));
			
			ps.setString(++index, head.getRequestNo());//request_number
			ps.setInt(++index, line.getLineNo());//line_number
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("update:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private boolean deleteCostLine(Connection conn ,RequestPromotion head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete  from t_req_promotion_cost  \n");
			sql.append(" WHERE request_no=?  \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getRequestNo());//request_number
			
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
	
	private boolean deleteProductLine(Connection conn ,RequestPromotion head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("delete  from t_req_promotion_line  \n");
			sql.append(" WHERE request_no=?  \n");
			
			logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getRequestNo());//request_number
			
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

	 public List<RequestPromotion> searchReqPromotionList(RequestPromotion mCriteria,User user,boolean getItem) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<RequestPromotion> list = new ArrayList<RequestPromotion>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n h.request_no,h.request_date,h.product_catagory, ");
				sql.append("\n h.product_type,h.customer_code,h.phone,");
				sql.append("\n h.name,h.status,h.remark,h.user_id, h.print_date ,");
				sql.append("\n h.CREATED,h.CREATED_BY,h.UPDATED,h.UPDATED_BY,promotion_start_date,promotion_end_date, ");
				sql.append("\n (select p.name from ad_user p where p.user_id = h.user_id) as sales_desc ");
				sql.append("\n ,(select p.name from m_customer p where p.code = h.customer_code) as customer_name ");
				sql.append("\n ,(select p.brand_desc from m_brand p where p.brand_no = h.product_catagory) as product_catagory_name ");
				sql.append("\n ,h.exported ");
				sql.append("\n  from t_req_promotion h ");
				sql.append("\n  where 1=1 ");
				if(!Utils.isNull(mCriteria.getRequestNo()).equals("")){
				  sql.append("\n  and  h.request_no ='"+mCriteria.getRequestNo()+"'");
				}
				
				if( !Utils.isNull(mCriteria.getUserId()).equals("")){
				  sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				}
				
				if( !Utils.isNull(mCriteria.getRequestDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getRequestDateTo()).equals("")	){
						
					  sql.append(" and h.request_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.request_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getRequestDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.request_no desc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  RequestPromotion m = new RequestPromotion();
				  m.setNo(no+"");
				  m.setRequestNo(rst.getString("request_no"));
				  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setUser(user);
				  m.setStatus(Utils.isNull(rst.getString("status")));
				  m.setStatusDesc(getStatusDesc(m.getStatus()));
				  
				  m.setProductCatagory(Utils.isNull(rst.getString("product_catagory")));
				  m.setProductCatagoryDesc(Utils.isNull(rst.getString("product_catagory_name")));
				  
				  m.setProductType(Utils.isNull(rst.getString("product_type")));
				  m.setName(Utils.isNull(rst.getString("name")));
				  m.setPhone(Utils.isNull(rst.getString("phone")));
				  m.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				  m.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				  
				  m.setPromotionStartDate(Utils.stringValue(rst.getDate("promotion_start_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setPromotionEndDate(Utils.stringValue(rst.getDate("promotion_end_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setRemark(Utils.isNull(rst.getString("remark")));
				  m.setExported(Utils.isNull(rst.getString("exported")));
				  
				  List<References> ref = InitialReferences.getReferenes(InitialReferences.TERRITORY,user.getTerritory());
				  String territory = ref != null && ref.size() >0?(ref.get(0).getName()):"";
				  m.setTerritory(territory);
				  
				  if(rst.getDate("print_date") != null)
				     m.setPrintDate(Utils.stringValue(rst.getTimestamp("print_date"),Utils.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,Utils.local_th));
				  
				  //Check canEdit
				  if(STATUS_VOID.equals(m.getStatus()) ){
					  m.setCanEdit(false);
					  m.setCanGenFile(false);
					  m.setCanCancel(false);
				  }else{
					  if(!"Y".equalsIgnoreCase(m.getExported())){
					     m.setCanEdit(true);
					     m.setCanGenFile(true);
					     m.setCanCancel(true);
					  }else{
						 m.setCanEdit(false);
						 m.setCanGenFile(true);
						 m.setCanCancel(false);
					  }
				  }
				  
				  if(getItem){
					  //get Cost Line
					  m.setCostLineList(getCostLineList(conn,mCriteria.getRequestNo()));
					  //file table to 5 row
					  if(m.getCostLineList() != null && m.getCostLineList().size() >0 && m.getCostLineList().size() <5){
						  RequestPromotionCost lastCostLine = m.getCostLineList().get(m.getCostLineList().size()-1);
						  
						  int diffRow = 5 - m.getCostLineList().size();
						  int lastLineNo = lastCostLine.getLineNo();
						  for(int r=0;r<diffRow;r++){
							  lastLineNo++;
							  RequestPromotionCost newCostLine = new RequestPromotionCost();
							  newCostLine.setLineNo(lastLineNo);
							  newCostLine.setCostDetail("");
							  newCostLine.setCostAmount(null);
							  
							  m.getCostLineList().add(newCostLine);
						  }
					  }else if(m.getCostLineList().size() ==0){
						  int diffRow = 5;
						  int lastLineNo = 0;
						  for(int r=0;r<diffRow;r++){
							  lastLineNo++;
							  RequestPromotionCost newCostLine = new RequestPromotionCost();
							  newCostLine.setLineNo(lastLineNo);
							  newCostLine.setCostDetail("");
							  newCostLine.setCostAmount(null);
							  
							  m.getCostLineList().add(newCostLine);
						  }
					  }
					  
					  //get product Line
					  m.setPromotionLineList(getPromotionLineList(conn,mCriteria.getRequestNo()));
					 //file table to 7 row
					  if(m.getPromotionLineList() != null && m.getPromotionLineList().size()> 0 && m.getPromotionLineList().size() < 7 ){
						  RequestPromotionLine lastCostLine = m.getPromotionLineList().get(m.getPromotionLineList().size()-1);
						  
						  int diffRow = 7 - m.getPromotionLineList().size();
						  int lastLineNo = lastCostLine.getLineNo();
						  for(int r=0;r<diffRow;r++){
							  lastLineNo++;
							  RequestPromotionLine newCostLine = new RequestPromotionLine();
							  newCostLine.setLineNo(lastLineNo);
							  newCostLine.setProductCode("");
							  newCostLine.setProductName(" \n");
							  newCostLine.setNewCtn(null);
							  
							  m.getPromotionLineList().add(newCostLine);
						  }//for
					  }else if(m.getPromotionLineList() ==null || (m.getPromotionLineList() != null & m.getPromotionLineList().size()==0)){
						  int diffRow = 7;
						  int lastLineNo = 0;
						  for(int r=0;r<diffRow;r++){
							  lastLineNo++;
							  RequestPromotionLine newCostLine = new RequestPromotionLine();
							  newCostLine.setLineNo(lastLineNo);
							  newCostLine.setProductCode("");
							  newCostLine.setProductName(" \n");
							  newCostLine.setNewCtn(null);
							  
							  m.getPromotionLineList().add(newCostLine);
						  }//for
					  }
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
	
	 
	 public List<RequestPromotionLine> getPromotionLineList(Connection conn,String requestNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			RequestPromotionLine line = null;
			List<RequestPromotionLine> productLineList = new ArrayList<RequestPromotionLine>();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.* " );
				sql.append("\n ,p.product_id,p.name ");
                sql.append("\n from t_req_promotion_line l ,m_product p  ");
				sql.append("\n  where p.code = l.product_code and l.request_no ='"+requestNo+"'  order by line_no asc ");
			
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				    line = new RequestPromotionLine();
				 
				    line.setLineNo(rst.getInt("line_no"));
					line.setProductCode(rst.getString("product_code"));
					line.setNewCtn(rst.getBigDecimal("new_ctn"));
					line.setNewAmount(rst.getBigDecimal("new_amount"));
					line.setStockCtn(rst.getBigDecimal("stock_ctn"));
					line.setStockQty(rst.getBigDecimal("stock_qty"));
					
					line.setBorrowCtn(rst.getBigDecimal("borrow_ctn"));
					line.setBorrowQty(rst.getBigDecimal("borrow_qty"));
					line.setBorrowAmount(rst.getBigDecimal("borrow_amount"));
					
					String productName =Utils.isNull(rst.getString("name"));
					logger.debug("productName length:"+productName.length());
					if(productName.length() >70){
						productName = productName.substring(0,35) +" "+productName.substring(35,70)+"...";
					}
					
					line.setProductName(productName);
					line.setProductId(Utils.isNull(rst.getString("product_id")));
					line.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
					
					//line.setUom1(Utils.isNull(uom1[i]));
					//line.setUom2(Utils.isNull(uom2[i]));
					//line.setPrice1(Utils.isNull(price1[i]));
					//line.setPrice2(Utils.isNull(price2[i]));
				
				  
				  productLineList.add(line);
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return productLineList;
		}
	 
	 public List<RequestPromotionCost> getCostLineList(Connection conn,String requestNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			RequestPromotionCost line = null;
			List<RequestPromotionCost> productLineList = new ArrayList<RequestPromotionCost>();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.* " );
                sql.append("\n  from t_req_promotion_cost l  ");
				sql.append("\n  where  l.request_no ='"+requestNo+"' order by line_no asc ");
			
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				    line = new RequestPromotionCost();
				 
				    line.setLineNo(rst.getInt("line_no"));
					line.setCostDetail(rst.getString("cost_detail"));
					line.setCostAmount(rst.getBigDecimal("cost_amount"));

				  productLineList.add(line);
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return productLineList;
		}
	 
	 private String getStatusDesc(String status){
		 String desc ="";
		 if(STATUS_SAVE.equalsIgnoreCase(status)){
			 desc ="บันทึกแล้ว";
		 }else if(STATUS_VOID.equalsIgnoreCase(status)){
			 desc ="ยกเลิกรายการ";
		 }
		 return desc;
	 }
	  
	  
	  
}
