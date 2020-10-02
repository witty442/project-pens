
package com.isecinc.pens.web.stockinv;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StockUtilsDAO;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.seq.SequenceProcessAll;

public class StockInvDAO{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	public static int getTotalRecList(Connection conn,StockInvBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from(");
			sql.append("\n select t.* ");
			sql.append("\n  from pensso.so_initial_stock t \n");
			sql.append("\n  where 1=1   \n");
			sql.append(genWhereSql(o));
			sql.append("\n )");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				totalRec = rst.getInt("c");
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();

			} catch (Exception e) {}
		}
		return totalRec;
	}
	
	public static List<StockInvBean> searchHeadList(Connection conn,StockInvBean o ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockInvBean h = null;
		List<StockInvBean> items = new ArrayList<StockInvBean>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
				sql.append("\n select t.* ,m.code as product_code ,m.name as product_name,sysdate");
				sql.append("\n from pensso.so_initial_stock t ,pensso.m_product m \n");
				sql.append("\n where t.inventory_item_id = m.product_id  \n");
				sql.append(genWhereSql(o));
				sql.append("\n order by t.header_id desc,m.code asc ");
            sql.append("\n   )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new StockInvBean();
			   h.setNo(r);
			   h.setHeaderId(rst.getLong("header_id"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setTransType(Utils.isNull(rst.getString("type"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setTransTypeDesc(Utils.isNull(rst.getString("type")).equalsIgnoreCase("IN")?"เข้าคลัง":"ออกจากคลัง"); 
			   h.setProductId(Utils.isNull(rst.getString("inventory_item_id")));
			   h.setProductCode(Utils.isNull(rst.getString("product_code"))); 
			   h.setProductName(Utils.isNull(rst.getString("product_name"))); 
			   h.setQty( Utils.decimalFormat(rst.getDouble("TRANSACTION_QTY"),Utils.format_current_2_disgit)); 
			   h.setQty1(Utils.decimalFormat(rst.getDouble("PRI_QTY"),Utils.format_current_no_disgit));
			   h.setQty2(Utils.decimalFormat(rst.getDouble("SEC_QTY"),Utils.format_current_no_disgit));
			   h.setUom1(Utils.isNull(rst.getString("TRANSACTION_UOM")));
			   h.setUom2(Utils.isNull(rst.getString("SEC_UOM")));
			   h.setUom(h.getUom1()+"/"+h.getUom2());
			   if(Utils.isNull(rst.getString("intflag")).equals("S")){
			       h.setStatus("SUCCESS"); 
			   }else if(Utils.isNull(rst.getString("intflag")).equals("E")){
				   h.setStatus("FAIL"); 
			   }else {
				   h.setStatus("NEW"); 
			   }
			   
			/*   logger.debug("transaction_date:"+rst.getDate("transaction_date"));
			   logger.debug("CurrentDate:"+rst.getDate("sysdate"));
			   logger.debug("transaction_date.after(sysdate):"+new Date().after(rst.getDate("transaction_date")));
			   */
			   h.setCanEdit(false); 
			   h.setCanConfirm(false); 
			   if( rst.getDate("sysdate").after(rst.getDate("transaction_date"))){
				   if( !"SUCCESS".equalsIgnoreCase(h.getStatus())){
					  h.setCanEdit(true);  
					  h.setCanConfirm(true); 
				   }else{
				      h.setCanEdit(false); 
				      h.setCanConfirm(false); 
				   }
			   }else{
				   if( !"SUCCESS".equalsIgnoreCase(h.getStatus())){
				     h.setCanEdit(true);  
				     h.setCanConfirm(true);  
				   }
			   }
			   items.add(h);
			   r++;
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	public static StockInvBean searchInitDetailList(Connection conn,StockInvBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockInvBean h = null;
		List<StockInvBean> items = new ArrayList<StockInvBean>();
		int r = 0;
		int totalQty1=0,totalQty2 =0;
		boolean canEdit = false;
		boolean canConfirm = false;
		String remark ="";
		try {
			sql.append("\n select t.* ,m.code as product_code ,m.name as product_name ,sysdate ");
			sql.append("\n from pensso.so_initial_stock t ,pensso.m_product m \n");
			sql.append("\n where t.inventory_item_id = m.product_id  \n");
			sql.append(genWhereSql(o));
			sql.append("\n order by t.transaction_date,t.line_id asc ");
         
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new StockInvBean();
			 
			   h.setNo(r);
			   h.setHeaderId(rst.getLong("header_id"));
			   h.setLineId(rst.getLong("line_id"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getTimestamp("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setTransType(Utils.isNull(rst.getString("type"))); 
			   h.setTransTypeDesc(Utils.isNull(rst.getString("type")).equalsIgnoreCase("IN")?"เข้าคลัง":"ออกจากคลัง"); 
			   h.setStatus(Utils.isNull(rst.getString("intflag"))); 
			   h.setProductId(Utils.isNull(rst.getString("inventory_item_id")));
			   h.setProductCode(Utils.isNull(rst.getString("product_code"))); 
			   h.setProductName(Utils.isNull(rst.getString("product_name"))); 
			   h.setQty( Utils.decimalFormat(rst.getDouble("TRANSACTION_QTY"),Utils.format_current_2_disgit)); 
			   h.setQty1(Utils.decimalFormat(rst.getDouble("PRI_QTY"),Utils.format_current_no_disgit,""));
			   h.setQty2(Utils.decimalFormat(rst.getDouble("SEC_QTY"),Utils.format_current_no_disgit,""));
			   h.setUom1(Utils.isNull(rst.getString("TRANSACTION_UOM")));
			   h.setUom2(Utils.isNull(rst.getString("SEC_UOM")));
			   h.setUom(h.getUom1()+"/"+h.getUom2());
			   
			   if(Utils.isNull(rst.getString("intflag")).equals("S")){
			       h.setStatus("SUCCESS"); 
			       h.setQty1Readonly("readonly");
				   h.setQty1Style("disableNumber");
				   h.setQty2Readonly("readonly");
				   h.setQty2Style("disableNumber");
				   
			   }else if(Utils.isNull(rst.getString("intflag")).equals("E")){
				   h.setStatus("FAIL"); 
				   h.setQty1Readonly("");
				   h.setQty1Style("enableNumber");
				   h.setQty2Readonly("");
				   h.setQty2Style("enableNumber");
                   if(Utils.isNull(h.getUom2()).equals("")){
                	  h.setQty2Readonly("readonly");
   				      h.setQty2Style("disableNumber");
				   }
			   }else {
				   h.setStatus("NEW"); 
				   h.setQty1Readonly("");
				   h.setQty1Style("enableNumber");
				   h.setQty2Readonly("");
				   h.setQty2Style("enableNumber");
				   if(Utils.isNull(h.getUom2()).equals("")){
                	  h.setQty2Readonly("readonly");
   				      h.setQty2Style("disableNumber");
				   }
			   }
			   if( rst.getDate("sysdate").after(rst.getDate("transaction_date"))){
				   if( !"SUCCESS".equalsIgnoreCase(h.getStatus())){
					  canEdit=true;  
					  canConfirm = true; 
				   }
			   }else{
				   if( !"SUCCESS".equalsIgnoreCase(h.getStatus())){
					  canEdit=true;  
					  canConfirm = true; 
				   }
			   }
			   
               if(r==0){
				   //set for return
            	   o.setHeaderId(rst.getLong("header_id"));
    			   o.setLineId(rst.getLong("line_id"));
    			   o.setTransactionDate(DateUtil.stringValue(rst.getTimestamp("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
    			   o.setTransType(Utils.isNull(rst.getString("type"))); 
    			   o.setTransTypeDesc(Utils.isNull(rst.getString("type")).equalsIgnoreCase("IN")?"เข้าคลัง":"ออกจากคลัง"); 
    			   o.setCanEdit(h.isCanEdit());
    			   o.setCanConfirm(h.isCanConfirm());
			   }
			   items.add(h);
			   r++;
			   
			   //sum
			   totalQty1 +=rst.getDouble("PRI_QTY");
			   totalQty2 +=rst.getDouble("SEC_QTY");
			   
			   remark = Utils.isNull(rst.getString("remark"));
			}//while
			
			o.setCanEdit(canEdit);
			o.setCanConfirm(canConfirm);
            o.setItems(items);
            o.setRemark(remark);
            o.setQty1(Utils.decimalFormat(totalQty1,Utils.format_current_no_disgit,""));
			o.setQty2(Utils.decimalFormat(totalQty2,Utils.format_current_no_disgit,""));
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return o;
	}
	public static StringBuilder genWhereSql(StockInvBean o ) throws Exception {
		StringBuilder sql = new StringBuilder();
		try {
			if( o.getHeaderId() != 0){
				sql.append("\n and t.header_id = "+o.getHeaderId()+"");
			}
			if(!Utils.isNull(o.getTransType()).equals("")){
				sql.append("\n and t.TYPE = '"+o.getTransType()+"'");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and t.TRANSACTION_DATE = to_date('"+dateStr+"','dd/mm/yyyy') ");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				
			} catch (Exception e) {}
		}
		return sql;
	}
	public static StringBuffer genExportStockInitDetail(StockInvBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String width="100%",border="1";
	    Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class=''> \n");
			h.append("<tr><td colspan='6' align='center'><b> <font size='2'>เอกสารการเบิกสินค้า เข้า / ออก จากคลัง</font></b></td> </tr>\n");
			h.append("<tr><td align='right'><b> ประเภทเอกสาร:&nbsp;</b><td colspan='5' class='text'>"+o.getTransTypeDesc()+"</td> </tr>\n");
			h.append("<tr><td align='right'><b> วันที่ทำรายการ:&nbsp;</b><td colspan='5' class='text'>"+o.getTransactionDate()+"</td></tr>\n");
			h.append("<tr><td align='right'><b> เลขที่เอกสาร:&nbsp;</b><td colspan='5' class='text'>"+o.getHeaderId()+"</td></tr>\n");
			h.append("<tr><td align='right'><b> หมายเหตุ:&nbsp;</b><td colspan='5' class='text'>"+o.getRemark()+"</td></tr>\n");
			h.append("</table>\n");
			
			h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class=''> \n");
			h.append("<tr> \n");
			h.append("<th>รหัสสินค้า</th> \n");
			h.append("<th>ชื่อสินค้า</th> \n");
			h.append("<th>หน่วยนับ</th> \n");
			h.append("<th>สถานะ</th> \n");
			h.append("<th>จำนวน(เต็ม)</th> \n");
			h.append("<th>จำนวน(เศษ)</th> \n");
			h.append("</tr> \n");
			
			StockInvBean r = searchInitDetailList(conn, o);
			if(r.getItems() != null && r.getItems().size() >0){
				for(int i=0;i<r.getItems().size();i++){
					StockInvBean item = r.getItems().get(i);
					h.append("<tr> \n");
					h.append("<td class='text_center'>"+item.getProductCode()+"</td> \n");
					h.append("<td class='text'>"+item.getProductName()+"</td> \n");
					h.append("<td class='text_center'>"+item.getUom()+"</td> \n");
					h.append("<td class='text_center'>"+item.getStatus()+"</td> \n");
					h.append("<td class='num'>"+item.getQty1()+"</td> \n");
					h.append("<td class='num'>"+item.getQty2()+"</td> \n");
					h.append("</tr> \n");
				}
				
				h.append("<tr> \n");
				h.append("<td class='text_center'></td> \n");
				h.append("<td class='text'></td> \n");
				h.append("<td class='text_center'></td> \n");
				h.append("<td class='colum_head'>รวม</td> \n");
				h.append("<td class='num_bold'>"+r.getQty1()+"</td> \n");
				h.append("<td class='num_bold'>"+r.getQty2()+"</td> \n");
				h.append("</tr> \n");
				h.append("</table> \n");
				
				h.append("<table id='tblProduct' align='center' border='0' width='"+width+"' cellpadding='3' cellspacing='1' class=''> \n");
				h.append("<tr> \n");
				h.append("<td></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("</tr> \n");
				h.append("<tr> \n");
				h.append("<td></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("</tr> \n");
				h.append("<tr> \n");
				h.append("<td></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("<td ></td> \n");
				h.append("</tr> \n");
				
				h.append("<tr> \n");
				h.append("<td colspan='2'>พิมพ์วันที่ :"+DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,DateUtil.local_th)+"</td> \n");
				h.append("<td></td> \n");
				h.append("<td colspan='3' align='right'>พิมพ์โดย : "+user.getName()+"</td> \n");
				h.append("</tr> \n");
				h.append("</table> \n");
			}
			return h;
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
		
	}
	public static long saveInitStock(Connection conn,StockInvBean h) throws Exception{
		try{
		   return saveInitStockModel(conn, h);
		}catch(Exception e){
			throw e;
		}
	}
	
	public static long saveInitStock(StockInvBean h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		   return saveInitStockModel(conn, h);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	public static long saveInitStockModel(Connection conn,StockInvBean h) throws Exception{
		long id = 0;
		try{
			conn.setAutoCommit(false);
			logger.debug("headerId:"+h.getHeaderId());
			logger.debug("h.getItems():"+h.getItems().size());
            if(h.getHeaderId()==0){
            	id = SequenceProcessAll.getIns().getNextValueBySeq("pensso.so_initial_stock_s.nextval").longValue();
                h.setHeaderId(id);
                
            }else{
    			//delete by lineIds
    			deleteStockInitModel(conn, h);
    			id = h.getHeaderId();
            }
                
            if(h.getItems() != null && h.getItems().size()>0){
 			   for(int i=0;i<h.getItems().size();i++){
 				   StockInvBean l = (StockInvBean)h.getItems().get(i);
 				   l.setHeaderId(id);
 				   l.setTransactionDate(h.getTransactionDate());
 				   l.setTransType(h.getTransType());
 				   l.setRemark(h.getRemark());
 				   //convert to Stock Qty (qty 1,qty2 10) to (1.8)
 				   if( !Utils.isNull(l.getQty2()).equals("")){
 				       //String subQty = StockUtilsDAO.convertStockQty(conn, l.getProductId(), l.getUom2(), l.getUom1(), l.getQty2());
 					  
 					   String subQty = StockUtilsDAO.convertSecQtyToPriQty(conn, l.getProductId(), l.getUom1(), l.getUom2(), l.getQty2());
 				       logger.debug("subQty:"+subQty);
 				       l.setQty(String.valueOf(Utils.convertStrToDouble(l.getQty1())+Utils.convertStrToDouble(subQty)));
 				   }else{
 					   l.setQty(String.valueOf(Utils.convertStrToDouble(l.getQty1())));
 				   }
 				  /* logger.debug("headerId:"+l.getHeaderId());
 				   logger.debug("transType:"+l.getTransType());
 				   logger.debug("transDate:"+l.getTransactionDate());
 				   logger.debug("lineId:"+l.getLineId());
 				   logger.debug("productId:"+l.getProductId());
 				   logger.debug("l.getQty:"+l.getQty());
 				   logger.debug("l.getQty1:"+l.getQty1());
 				   logger.debug("l.getQty2:"+l.getQty2());
 				   logger.debug("l.getUom:"+l.getUom());
 				   logger.debug("l.getUom1:"+l.getUom1());
 				   logger.debug("l.getUom2:"+l.getUom2());
 				   logger.debug("created_by:"+l.getCreateUser());*/
 				   
 				   if(l.getLineId()==0){
	 				   l.setLineId(SequenceProcessAll.getIns().getNextValueBySeq("pensso.so_initial_stock_s.nextval").longValue());
	 				   l.setStatus("NEW");
	 				   
	 				   insertStockInitModel(conn, l);
 				   }else {
 					  if( !l.getStatus().equals("SUCCESS")){
 						  //update
 						  updateStockInitModel(conn, l); 
 					  }
 				   }
 				   
 			   }//for
 			}//if
			
			conn.commit();
			
			return id;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			
		}
	}
	public static boolean confirmInitStock(Connection conn,long headerId) throws Exception{
		boolean r = true;
		CallableStatement  cs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("{ call xxpens_om_sales_online_pkg.initial_stock(?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setLong(1, headerId);
			cs.execute();
			
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			cs.close();
		}
	}
	 public static void insertStockInitModel(Connection conn,StockInvBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertStockInitModel");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO pensso.so_initial_stock \n");
				sql.append(" (header_ID,line_id, TRANSACTION_DATE,TYPE,inventory_item_id,  \n");
				sql.append("  transaction_qty,transaction_uom,sec_uom, \n");
				sql.append("  pri_qty,sec_qty, CREATION_DATE, CREATED_BY ,remark )  \n");
			    sql.append("  VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?) \n");
				ps = conn.prepareStatement(sql.toString());
					
				Date transDate = DateUtil.parse( o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				
				ps.setLong(c++, o.getHeaderId());
				ps.setLong(c++, o.getLineId());
				ps.setTimestamp(c++, new java.sql.Timestamp(transDate.getTime()));
				ps.setString(c++, o.getTransType());
				ps.setString(c++, o.getProductId());
				ps.setDouble(c++, Utils.convertStrToDouble(o.getQty()));
				ps.setString(c++, o.getUom1());
				ps.setString(c++, o.getUom2());
				ps.setDouble(c++, Utils.convertStrToDouble(o.getQty1()));
				ps.setDouble(c++, Utils.convertStrToDouble(o.getQty2()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 public static void updateStockInitModel(Connection conn,StockInvBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStockInitModel");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" update pensso.so_initial_stock \n");
				sql.append(" set transaction_qty = "+Utils.convertStrToDouble(o.getQty())+" ,transaction_uom ='"+o.getUom1()+"' \n");
				sql.append(" ,pri_qty ="+Utils.convertStrToDouble(o.getQty1())+",sec_qty="+Utils.convertStrToDouble(o.getQty2())+"\n");
				sql.append(" ,remark ='"+Utils.isNull(o.getRemark())+"'");
				sql.append(" where header_Id="+o.getHeaderId() +"\n");
			    sql.append(" and line_Id="+o.getLineId() +"\n");
			    logger.debug("sql:"+sql.toString());
			    
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static List<String> checkStockInitStatus(Connection conn,StockInvBean o) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("checkStockInitStatus");
			List<String> msgList = new ArrayList<String>();
			String msg = "";
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" select m.code,m.name  from pensso.so_initial_stock t ,pensso.m_product m");
				sql.append(" where m.product_id = t.inventory_item_id ");
				sql.append(" and t.header_id =? and t.intflag ='E' \n");
				ps = conn.prepareStatement(sql.toString());
	
				ps.setLong(1, o.getHeaderId());
				rs = ps.executeQuery();
				while(rs.next()){
					msg = Utils.isNull(rs.getString("code"))+"-"+Utils.isNull(rs.getString("name"))+ " : Cannot init Stock";
					msgList.add(msg);
				}
				return msgList;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void deleteStockInitModel(Connection conn,StockInvBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("deleteStockInitModel by lineIds");
			try{
				StringBuffer sql = new StringBuffer("");
				if( !Utils.isNull(o.getDeleteLineIds()).equals("")){
					sql.append(" delete from pensso.so_initial_stock where header_id ="+o.getHeaderId()+" \n");
					sql.append(" and line_id in("+SQLHelper.converToTextSqlIn(o.getDeleteLineIds())+") \n");
					
					logger.debug("sql :"+sql.toString());
					ps = conn.prepareStatement(sql.toString());
					ps.executeUpdate();
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void clearStatusStockInitModel(Connection conn,long headerId) throws Exception{
			PreparedStatement ps = null;
			logger.debug("clearStatusStockInitModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" update pensso.so_initial_stock set intflag =null ,intmessage=null where header_id =? and intflag <> 'S' \n");
				ps = conn.prepareStatement(sql.toString());
	
				ps.setLong(c++, headerId);
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
}
