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

import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.document.MoveOrderReqDocumentProcess;
import com.isecinc.pens.process.document.MoveOrderReturnDocumentProcess;

public class MBillPlan {

	private Logger logger = Logger.getLogger("PENS");
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
    
	
	public boolean updateBillPlan(BillPlan head) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			StringBuffer sql = new StringBuffer("update t_bill_plan \n");
			sql.append(" set  status = ? ,bill_plan_request_date = ?   \n"); 
			sql.append(" ,updated =? ,updated_by = ?   \n"); 
			sql.append(" where bill_plan_no =?  \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, head.getStatus());//request_date
			if( !Utils.isNull(head.getBillPlanRequestDate()).equals("")){
			   ps.setTimestamp(++index, new java.sql.Timestamp( (Utils.parse(head.getBillPlanRequestDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()) );
			}else{
			   ps.setTimestamp(++index,null);	
			}
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, head.getUpdateBy());//updated_by
			ps.setString(++index, head.getBillPlanNo());//request_number

			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(conn != null){
				conn.close();conn = null;
			}
		}
		return result;
	}
	
	 public List<BillPlan> searchBillPlanList(BillPlan mCriteria,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<BillPlan> list = new ArrayList<BillPlan>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				conn = new  DBCPConnectionProvider().getConnection(conn);
				
				sql.append("\n  SELECT " );
				sql.append("\n h.bill_plan_no,h.bill_plan_date,h.bill_plan_request_date,");
				sql.append("\n h.sales_code,h.pd_code,h.description,");
				sql.append("\n h.status,h.USER_ID ,h.exported");
				sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
				sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
				sql.append("\n  from t_bill_plan h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+user.getId()+"'");
				
				if( !Utils.isNull(mCriteria.getNoBillPlan()).equals("")){
				  sql.append("\n  and ( h.bill_plan_request_date is  null or trim(bill_plan_request_date) = '') ");	
				}
						
				if( !Utils.isNull(mCriteria.getBillPlanDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getBillPlanDateTo()).equals("")	){
						
					  sql.append(" and h.bill_plan_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getBillPlanDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.bill_plan_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getBillPlanDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.bill_plan_date desc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
				  no++;
				  BillPlan m = new BillPlan();
				  m.setNo(no+"");
				  m.setBillPlanNo(rst.getString("bill_plan_no"));
				  m.setBillPlanDate(Utils.stringValue(rst.getDate("bill_plan_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  if( !Utils.isNull(rst.getString("bill_plan_request_date")).equals("")){
					  m.setBillPlanRequestDate(Utils.stringValue(rst.getDate("bill_plan_request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  }
				  m.setSalesCode(rst.getString("sales_code"));
				  m.setSalesDesc(rst.getString("sales_desc"));
				  
				  m.setPdCode(rst.getString("pd_code"));
				  m.setPdDesc(rst.getString("pd_desc"));
				  
				  m.setDescription(rst.getString("description"));
				  m.setStatus(Utils.isNull(rst.getString("status")));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())|| "".equals(m.getStatus())?"ยังไม่รับ":"รับแล้ว");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
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
	
    public BillPlan searchBillPlan(BillPlan mCriteria,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		BillPlan m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		int no = 0;
		try {
			conn = new  DBCPConnectionProvider().getConnection(conn);
			
			sql.append("\n  SELECT " );
			sql.append("\n h.bill_plan_no,h.bill_plan_date,h.bill_plan_request_date,");
			sql.append("\n h.sales_code,h.pd_code,h.description,");
			sql.append("\n h.status,h.USER_ID,h.exported ");
			sql.append("\n  ,(select p.pd_desc from m_pd p where p.pd_code = h.pd_code and p.sales_code ='"+user.getUserName()+"') as pd_desc ");
			sql.append("\n  ,(select p.name from ad_user p where p.user_name = h.sales_code) as sales_desc ");
			sql.append("\n  from t_bill_plan h ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and h.user_id ='"+user.getId()+"'");
			sql.append("\n  and h.bill_plan_no ='"+mCriteria.getBillPlanNo()+"'");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
			  m = mCriteria;
			  no++;
			  m.setNo(no+"");
			  m.setBillPlanNo(rst.getString("bill_plan_no"));
			  m.setBillPlanDate(Utils.stringValue(rst.getDate("bill_plan_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  
			  if( !Utils.isNull(rst.getString("bill_plan_request_date")).equals("")){
				  m.setBillPlanRequestDate(Utils.stringValue(rst.getDate("bill_plan_request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  }

			  m.setSalesCode(rst.getString("sales_code"));
			  m.setSalesDesc(rst.getString("sales_desc"));
			  
			  m.setPdCode(rst.getString("pd_code"));
			  m.setPdDesc(rst.getString("pd_desc"));
			  
			  m.setDescription(rst.getString("description"));
			  m.setStatus(Utils.isNull(rst.getString("status")));
			  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())|| "".equals(m.getStatus())?"ยังไม่รับ":"รับแล้ว");
			  m.setExported(Utils.isNull(rst.getString("exported")));
			  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
			  
			  if(   STATUS_EXPORTED.equalsIgnoreCase(m.getExported())
				 || STATUS_SAVE.equalsIgnoreCase(m.getStatus())
				 || "".equalsIgnoreCase(m.getPdDesc())	){ //pdCode not in m_pd (master config by sales )
				  m.setCanEdit(false);
			  }else{
				  m.setCanEdit(true);
			  }
			  
			  if(STATUS_EXPORTED.equalsIgnoreCase(m.getExported())){
				  m.setCanCancel(false);
			  }else{
				  if(STATUS_SAVE.equalsIgnoreCase(m.getStatus())){
				     m.setCanCancel(true);
				  }else{
					 m.setCanCancel(false);  
				  }
			  }
			}//while
			
			//Find Lines
			m.setBillPlanLineList(searchBillPlanLine(conn,m));
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
	  
	 public List<BillPlan> searchBillPlanLine(Connection conn,BillPlan mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<BillPlan> lineList = new ArrayList<BillPlan>();
			StringBuilder sql = new StringBuilder();
			int no = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT A.* FROM ( ");
				sql.append("\n   SELECT l.* ,p.code,p.name  from t_bill_plan_line l ,m_product p ");
				sql.append("\n   WHERE l.bill_plan_no ='"+mCriteria.getBillPlanNo()+"'");
				sql.append("\n   and l.product_id = p.product_id ");
				sql.append("\n  ) A ORDER BY A.line_no,A.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());

				while (rst.next()) {
				  BillPlan m = new BillPlan();
				  no++;
				  m.setNo(no+"");
				  m.setLineNo(rst.getInt("line_no"));
				  m.setProductId(rst.getString("product_id"));
				  m.setProductCode(rst.getString("code"));
				  m.setProductName(rst.getString("name"));
				  m.setFullUOM(rst.getString("uom"));
				  m.setFullQTY(NumberToolsUtil.decimalFormat(rst.getDouble("qty"),NumberToolsUtil.format_current_no_disgit));
				  
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
	 
}
