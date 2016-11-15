package com.isecinc.pens.dao;

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

import com.isecinc.pens.bean.SAReportBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class SAReportDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 
   public static SAReportBean searchEmployeeDeptList(SAReportBean o ) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchEmployeeDeptListModel(conn, o);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	public static SAReportBean searchEmployeeDeptListModel(Connection conn,SAReportBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			SAReportBean h = null;
			List<SAReportBean> items = new ArrayList<SAReportBean>();
			Map<String,String> damageMap = new HashMap<String,String>();
			try {
			   sql.append(" \n select distinct E.* " );
			   sql.append(" \n ,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Region' AND M.pens_value =E.region)as region_desc" );
			   sql.append(" \n ,D.total_damage,INV.total_invoice_amt");
			   sql.append(" \n,(SELECT NVL(SUM(M.bme_amt),0) FROM sa_reward_tran M where M.emp_id = E.emp_id group by M.emp_id) as total_reward_bme" );
			   sql.append(" \n,(SELECT NVL(SUM(M.wacoal_amt),0) FROM sa_reward_tran M where M.emp_id = E.emp_id group by M.emp_id) as total_reward_wacoal" );
			   if(Utils.isNull(o.getSummaryType()).equals("Detail")){
			      sql.append("\n ,INV.TRX_NUMBER ");
			   }
			   sql.append("\n  from SA_EMPLOYEE E ");
			   sql.append("\n  LEFT OUTER JOIN " );
			   if(Utils.isNull(o.getSummaryType()).equals("Detail")){
				   sql.append("\n  ( select H.ACCOUNT_NUMBER,H.TRX_NUMBER ,nvl(sum(AMOUNT_DUE_ORIGINAL),0) as total_invoice_amt");
				   sql.append("\n    FROM SA_AR_DAMAGE H");
				   sql.append("\n    GROUP BY H.ACCOUNT_NUMBER,H.TRX_NUMBER ");
				   sql.append("\n  )INV");
			   }else{
				   sql.append("\n  ( select H.ACCOUNT_NUMBER ,nvl(sum(AMOUNT_DUE_ORIGINAL),0) as total_invoice_amt");
				   sql.append("\n    FROM SA_AR_DAMAGE H");
				   sql.append("\n    GROUP BY H.ACCOUNT_NUMBER ");
				   sql.append("\n  )INV"); 
			   }
			   sql.append("\n  ON E.oracle_ref_id = INV.ACCOUNT_NUMBER");
			   
			   sql.append("\n  LEFT OUTER JOIN " );
			   sql.append("\n  (select H.emp_id ,nvl(sum(pay_amt),0) as total_damage");
			   sql.append("\n   FROM SA_DAMAGE_HEAD H,SA_DAMAGE_TRAN T");
			   sql.append("\n   WHERE H.emp_id = T.emp_id AND H.type = T.type AND H.inv_refwal=T.inv_refwal ");
			   sql.append("\n   GROUP BY H.emp_id ");
			   sql.append("\n  )D ON E.emp_id = D.emp_id");
			   
			   sql.append("\n  WHERE 1=1");
			   
			    if( !Utils.isNull(o.getEmpId()).equals("") ){
					sql.append("\n and E.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
			    }
			    if( !Utils.isNull(o.getOracleRefId()).equals("")){
					sql.append("\n and E.oracle_ref_id ='"+o.getOracleRefId()+"'");
				}
				if( !Utils.isNull(o.getType()).equals("")){
					sql.append("\n and E.type = '"+Utils.isNull(o.getType())+"'");
				}
				if( !Utils.isNull(o.getName()).equals("")){
					sql.append("\n and E.name LIKE '%"+Utils.isNull(o.getName())+"%'");
				}
				if( !Utils.isNull(o.getSurname()).equals("")){
					sql.append("\n and E.surname LIKE '%"+Utils.isNull(o.getSurname())+"%'");
				}
				if( !Utils.isNull(o.getGroupStore()).equals("")){
					sql.append("\n and E.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
				}
			
				sql.append("\n order by E.emp_id,E.oracle_ref_id,E.name asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new SAReportBean();
				  
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				   h.setType(Utils.isNull(rst.getString("type")));
				   
				   h.setOracleRefId(Utils.isNull(rst.getString("oracle_ref_id")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurname(Utils.isNull(rst.getString("surname")));
				   h.setFullName(h.getName()+" "+h.getSurname());
				   
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));

				   if(Utils.isNull(o.getSummaryType()).equals("Detail")){
				      h.setInvoiceNo(Utils.isNull(rst.getString("TRX_NUMBER")));
				   }else{
					   h.setTotalRewardBme(Utils.decimalFormat(rst.getDouble("total_reward_bme"),Utils.format_current_2_disgit));
					   h.setTotalRewardWacoal(Utils.decimalFormat(rst.getDouble("total_reward_wacoal"),Utils.format_current_2_disgit));
				   }
				   h.setTotalInvoiceAmt(Utils.decimalFormat(rst.getDouble("total_invoice_amt"),Utils.format_current_2_disgit));
				   
				   //Case display totalDamage per oracle_ref_id in first row
				   if(damageMap.get(h.getOracleRefId()) == null){
				      h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
				      damageMap.put(h.getOracleRefId(), h.getOracleRefId());//set for check duplicate row by oracle_ref_id
				   }else{
					   h.setTotalDamage("0.00"); 
				   }
				   
				   items.add(h);

				}//while
				
				//set Result 
				o.setItems(items);
				
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
	
	 public static SAReportBean searchEmployeeList(SAReportBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchEmployeeListModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static SAReportBean searchEmployeeListModel(Connection conn,SAReportBean o) throws Exception {
				PreparedStatement ps = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				
				SAReportBean h = null;
				List<SAReportBean> items = new ArrayList<SAReportBean>();
				int r = 1;
				try {
				   sql.append(" \n select distinct E.* " );
				   sql.append(" \n,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Region' AND M.pens_value =E.region)as region_desc" );
				   sql.append(" \n ,D.total_damage ");
				   sql.append("\n  from SA_EMPLOYEE E ");
				   sql.append("\n  ,(select H.emp_id ,nvl(sum(pay_amt),0) as total_damage");
				   sql.append("\n   FROM SA_DAMAGE_HEAD H,SA_DAMAGE_TRAN T");
				   sql.append("\n   WHERE H.emp_id = T.emp_id AND H.type = T.type AND H.inv_refwal=T.inv_refwal ");
				   sql.append("\n   and T.paydate >= to_date('"+o.getMonth()+"','ddmmyyyy')");
				   sql.append("\n   GROUP BY H.emp_id ");
				   sql.append("\n  )D");
				   sql.append("\n  WHERE 1=1");
				   sql.append("\n  AND E.emp_id = D.emp_id");

				    if( !Utils.isNull(o.getEmpId()).equals("") ){
						sql.append("\n and E.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
				    }
				    if( !Utils.isNull(o.getOracleRefId()).equals("")){
						sql.append("\n and E.oracle_ref_id ='"+o.getOracleRefId()+"'");
					}
					if( !Utils.isNull(o.getType()).equals("")){
						sql.append("\n and E.type = '"+Utils.isNull(o.getType())+"'");
					}
					if( !Utils.isNull(o.getName()).equals("")){
						sql.append("\n and E.name LIKE '%"+Utils.isNull(o.getName())+"%'");
					}
					if( !Utils.isNull(o.getSurname()).equals("")){
						sql.append("\n and E.surname LIKE '%"+Utils.isNull(o.getSurname())+"%'");
					}
					if( !Utils.isNull(o.getGroupStore()).equals("")){
						sql.append("\n and E.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
					}
					if( !Utils.isNull(o.getBranch()).equals("")){
						sql.append("\n and E.branch LIKE '%"+Utils.isNull(o.getBranch())+"%'");
					}
					
					sql.append("\n order by E.emp_id asc ");
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					rst = ps.executeQuery();
					
					while(rst.next()) {
					  h = new SAReportBean();
					  
					   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
					   h.setType(Utils.isNull(rst.getString("type")));
					   
					   h.setOracleRefId(Utils.isNull(rst.getString("oracle_ref_id")));
					   h.setBranch(Utils.isNull(rst.getString("branch")));
					   h.setName(Utils.isNull(rst.getString("name")));
					   h.setSurname(Utils.isNull(rst.getString("surname")));
					   h.setFullName(h.getName()+" "+h.getSurname());
					   
					   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
					   h.setBranch(Utils.isNull(rst.getString("branch")));
					   h.setRegion(Utils.isNull(rst.getString("region")));
					   h.setRegionDesc(Utils.isNull(rst.getString("region_desc")));
	    
					   h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
					   items.add(h);
					   r++;
					   
					}//while
					
					//set Result 
					o.setItems(items);
					
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
	 public static List<SAReportBean> searchStatementReport(SAReportBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchStatementReportModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
	}
	 public static List<SAReportBean> searchStatementReportModel(Connection conn,SAReportBean cri) throws Exception {
		 double totalPayment = 0;
		 double totalDelayPayment = 0;
		 boolean addBlankRow = false;
		 List<SAReportBean> reportList = new ArrayList<SAReportBean>();
		 //Type BME
		 cri.setType("BME");
		 SAReportBean r = searchStatementReportByTypeModel(conn,cri);
		 reportList.addAll(r.getItems());
		 //Sum Grand Total
		 totalPayment += Utils.convertStrToDouble(r.getTotalPayment());
		 totalDelayPayment += Utils.convertStrToDouble(r.getTotalDelayPayment());
		 
		 if(Utils.convertStrToDouble(r.getTotalPayment()) > 0 || Utils.convertStrToDouble(r.getTotalDelayPayment()) >0){
			  //Add Summary BME
			  SAReportBean h = new SAReportBean();
			  h.setType("");
			  h.setInvRefwal("");
			  h.setTranDate("");
			  h.setTotalDamage("");
			  h.setLineId("");
			  h.setPayType("");
			  h.setPayDate("รวม BME");
			  h.setPayAmt(Utils.decimalFormat(Utils.convertStrToDouble(r.getTotalPayment()),Utils.format_current_2_disgit));
			  h.setDelayPayAmt(Utils.decimalFormat(Utils.convertStrToDouble(r.getTotalDelayPayment()),Utils.format_current_2_disgit));
			  reportList.add(h);
			  
			 //Blank row
			  h = new SAReportBean();
			  h.setType("");
			  h.setInvRefwal("");
			  h.setTranDate("");
			  h.setTotalDamage("");
			  h.setLineId("");
			  h.setPayType("");
			  h.setPayDate("");
			  h.setPayAmt("");
			  h.setDelayPayAmt("");
			  reportList.add(h);
			  addBlankRow = true;
		 }
		 
		 //TYPE WACOAL
		  cri.setType("WACOAL");
		  r = searchStatementReportByTypeModel(conn,cri);
		  reportList.addAll(r.getItems());
		  //Sum Grand Total
		  totalPayment += Utils.convertStrToDouble(r.getTotalPayment());
		  totalDelayPayment += Utils.convertStrToDouble(r.getTotalDelayPayment());
		  if(Utils.convertStrToDouble(r.getTotalPayment()) > 0 || Utils.convertStrToDouble(r.getTotalDelayPayment()) >0){
			 //Add Summary WACOAL
			  SAReportBean h = new SAReportBean();
			  h.setType("");
			  h.setInvRefwal("");
			  h.setTranDate("");
			  h.setTotalDamage("");
			  h.setLineId("");
			  h.setPayType("");
			  h.setPayDate("รวม WACOAL");
			  h.setPayAmt(Utils.decimalFormat(Utils.convertStrToDouble(r.getTotalPayment()),Utils.format_current_2_disgit));
			  h.setDelayPayAmt(Utils.decimalFormat(Utils.convertStrToDouble(r.getTotalDelayPayment()),Utils.format_current_2_disgit));
			  reportList.add(h);
			  
			 //Blank row
			  h = new SAReportBean();
			  h.setType("");
			  h.setInvRefwal("");
			  h.setTranDate("");
			  h.setTotalDamage("");
			  h.setLineId("");
			  h.setPayType("");
			  h.setPayDate("");
			  h.setPayAmt("");
			  h.setDelayPayAmt("");
			  reportList.add(h);
			  addBlankRow = true;
		  }
		  if(addBlankRow == false){
			  //Blank row
			  SAReportBean h = new SAReportBean();
			  h.setType("");
			  h.setInvRefwal("");
			  h.setTranDate("");
			  h.setTotalDamage("");
			  h.setLineId("");
			  h.setPayType("");
			  h.setPayDate("");
			  h.setPayAmt("");
			  h.setDelayPayAmt("");
			  reportList.add(h);
			  addBlankRow = true;
		  }
		 //Grand Total
		  SAReportBean h = new SAReportBean();
		  h.setType("");
		  h.setInvRefwal("");
		  h.setTranDate("");
		  h.setTotalDamage("");
		  h.setLineId("");
		  h.setPayType("");
		  h.setPayDate("ยอดรวมทั้งหมด");
		  h.setPayAmt(Utils.decimalFormat(totalPayment,Utils.format_current_2_disgit));
		  h.setDelayPayAmt(Utils.decimalFormat(totalDelayPayment,Utils.format_current_2_disgit));
		  reportList.add(h);
		  
		 return reportList;
	 }
	public static SAReportBean searchStatementReportByTypeModel(Connection conn,SAReportBean cri) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		SAReportBean h = null;
		SAReportBean re= new SAReportBean();
		List<SAReportBean> items = new ArrayList<SAReportBean>();
		int r = 1;
		double totalPayment = 0;
		double totalDelayPayment = 0;
		try {
		     sql.append("\n select distinct H.type,H.inv_refwal,H.emp_id,h.tran_date,h.total_damage");
		     sql.append("\n  FROM SA_DAMAGE_HEAD H,SA_DAMAGE_TRAN T");
		     sql.append("\n  WHERE 1=1");
		     sql.append("\n  and H.emp_id = T.emp_id AND H.type = T.type AND H.inv_refwal=T.inv_refwal ");
		     sql.append("\n  and T.paydate >= to_date('"+cri.getMonth()+"','ddmmyyyy')");
             sql.append("\n  and H.emp_id ='"+Utils.isNull(cri.getEmpId())+"'");
             sql.append("\n  and H.type ='"+cri.getType()+"'");
			 sql.append("\n  order by H.type,H.inv_refwal asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			while(rst.next()) {
			   h = new SAReportBean();
			   
			   h.setEmpId(cri.getEmpId());
			   h.setType(Utils.isNull(rst.getString("type")));
			   h.setInvRefwal(Utils.isNull(rst.getString("Inv_refwal")));
			   h.setTranDate(Utils.stringValue(rst.getDate("tran_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
			   h.setMonth(cri.getMonth());//Set 
			   
			   SAReportBean re1 = searchStatementDetailReport(conn, h);
			   totalPayment += Utils.convertStrToDouble(re1.getTotalPayment());
			   totalDelayPayment += Utils.convertStrToDouble(re1.getTotalDelayPayment());
			   
			   items.addAll(re1.getItems());
			   
			   //Blank row
			   h = new SAReportBean();
			   h.setType("");
			   h.setInvRefwal("");
			   h.setTranDate("");
			   h.setTotalDamage("");
			   h.setLineId("");
			   h.setPayType("");
			   h.setPayDate("");
			   h.setPayAmt("");
			   h.setDelayPayAmt("");
			   
			   items.add(h);
			
			   r++;
			}//while
			//Remove Blank Row Last row
			if(items.size() >= 2)
			items.remove(items.size()-1);
			
			re.setItems(items);
			re.setTotalPayment(totalPayment+"");
			re.setTotalDelayPayment(totalDelayPayment+"");
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	return re;
  }
	
	public static SAReportBean searchStatementDetailReport(Connection conn,SAReportBean h) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		SAReportBean re = new SAReportBean();
		List<SAReportBean> items = new ArrayList<SAReportBean>();
		int r = 1;
		double totalPayment = 0;
		double totalDelayPayment = 0;
		SAReportBean tempBean = new SAReportBean();
		tempBean.setType(h.getType());
		String payDateStr = "";
		long criYYYYMMLong = 0;
		long payDateYYYYMMLong = 0;
		try {
			  //01102016
			  criYYYYMMLong = new Long(h.getMonth().substring(4,8)+h.getMonth().substring(2,4)).longValue();
			  logger.debug("criYYYYMMLong:"+criYYYYMMLong);
			 
			   sql.append(" \n select line_id,paytype,paydate,pay_amt " );
			   sql.append(" \n FROM SA_DAMAGE_TRAN T");
			   sql.append("\n  WHERE 1=1");
			   sql.append("\n  and emp_id ='"+Utils.isNull(h.getEmpId())+"'");
			   sql.append("\n  and type ='"+Utils.isNull(h.getType())+"'");
			   sql.append("\n  and Inv_refwal ='"+Utils.isNull(h.getInvRefwal())+"'");
			  // sql.append("\n  and paydate >= to_date('"+h.getMonth()+"','ddmmyyyy')");
			   sql.append("\n order by line_id asc ");
				
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   if(r==1){
					   h.setLineId(Utils.isNull(rst.getString("line_id")));
					   h.setPayType(Utils.isNull(rst.getString("paytype")));
					   h.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));

					   if(rst.getDate("paydate") != null){
						   payDateStr = Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITHOUT_SLASH);
						   payDateYYYYMMLong = new Long(payDateStr.substring(4,8)+payDateStr.substring(2,4)).longValue();
						   
						   if(payDateYYYYMMLong <= criYYYYMMLong ){
							   h.setPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
							   h.setDelayPayAmt("");
					           totalPayment +=rst.getDouble("pay_amt");
						   }else{
							   h.setDelayPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
							   h.setPayAmt("");
							   totalDelayPayment +=rst.getDouble("pay_amt");
						   }
					   }else{
						   h.setDelayPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
						   h.setPayAmt("");
						   totalDelayPayment +=rst.getDouble("pay_amt");
					   }
				   }else{
					   h = new SAReportBean();
					   h.setType("");
					   h.setInvRefwal("");
					   h.setTranDate("");
					   h.setTotalDamage("");
					   
					   h.setLineId(Utils.isNull(rst.getString("line_id")));
					   h.setPayType(Utils.isNull(rst.getString("paytype")));
					   h.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   h.setPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
					   
					   if(rst.getDate("paydate") != null){
						   payDateStr = Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITHOUT_SLASH);
						   payDateYYYYMMLong = new Long(payDateStr.substring(4,8)+payDateStr.substring(2,4)).longValue();
						   
						   if(payDateYYYYMMLong <= criYYYYMMLong ){
							   h.setPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
							   h.setDelayPayAmt("");
					           totalPayment +=rst.getDouble("pay_amt");
						   }else{
							   h.setDelayPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
							   h.setPayAmt("");
							   totalDelayPayment +=rst.getDouble("pay_amt");
						   } 
					   }else{
						   h.setDelayPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
						   h.setPayAmt("");
						   totalDelayPayment +=rst.getDouble("pay_amt");
					   }
				   }
				   items.add(h);
				   r++;
				}//while
				
				re.setItems(items);
				re.setTotalPayment(totalPayment+"");
				re.setTotalDelayPayment(totalDelayPayment+"");
				
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	return re;
   }
	
	public static List<SAReportBean> searchStatementReportAll(SAReportBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchStatementReportAllModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
	}
	 
	public static List<SAReportBean> searchStatementReportAllModel(Connection conn,SAReportBean cri) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SAReportBean> items = new ArrayList<SAReportBean>();
		int r = 1;
		SAReportBean h = null;
		String payDateStr = "";
		long criYYYYMMLong = 0;
		long payDateYYYYMMLong = 0;
		try {
			 //01102016
			  criYYYYMMLong = new Long(cri.getMonth().substring(4,8)+cri.getMonth().substring(2,4)).longValue();
			  logger.debug("criYYYYMMLong:"+criYYYYMMLong);
			  
			 sql.append("\n select E.emp_id,E.name,E.surname,E.group_store,E.branch");
			 sql.append("\n ,H.type,H.inv_refwal,h.tran_date,h.total_damage");
			 sql.append("\n ,T.line_id,T.paytype,T.paydate,T.pay_amt ");
		     sql.append("\n  FROM SA_EMPLOYEE E,SA_DAMAGE_HEAD H,SA_DAMAGE_TRAN T");
		     sql.append("\n  WHERE 1=1");
		     sql.append("\n  and E.emp_id = H.emp_id ");
		     sql.append("\n  and H.emp_id = T.emp_id AND H.type = T.type AND H.inv_refwal=T.inv_refwal ");
		     sql.append("\n  and T.paydate >= to_date('"+cri.getMonth()+"','ddmmyyyy')");
		     if( !Utils.isNull(cri.getEmpId()).equals("")){
                sql.append("\n  and E.emp_id ='"+Utils.isNull(cri.getEmpId())+"'");
		     }
		     if( !Utils.isNull(cri.getGroupStore()).equals("")){
	             sql.append("\n  and E.group_store ='"+Utils.isNull(cri.getGroupStore())+"'");
			 }
		     if( !Utils.isNull(cri.getName()).equals("")){
	             sql.append("\n  and E.name LIKE '%"+Utils.isNull(cri.getName())+"%'");
			 }
		     if( !Utils.isNull(cri.getSurname()).equals("")){
	             sql.append("\n  and E.surname LIKE '%"+Utils.isNull(cri.getSurname())+"%'");
			 }
		     if( !Utils.isNull(cri.getBranch()).equals("")){
	             sql.append("\n  and E.branch LIKE '%"+Utils.isNull(cri.getBranch())+"%'");
			 }
			 sql.append("\n  order by E.emp_id,H.inv_refwal,T.line_id asc  ");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new SAReportBean();
			   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
			   h.setName(Utils.isNull(rst.getString("name")));
			   h.setSurname(Utils.isNull(rst.getString("surname")));
			   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
			   h.setBranch(Utils.isNull(rst.getString("branch")));
			   
			   h.setType(Utils.isNull(rst.getString("type")));
			   h.setInvRefwal(Utils.isNull(rst.getString("inv_refwal")));
			   h.setTranDate(Utils.stringValue(rst.getDate("tran_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
			   
			   h.setLineId(Utils.isNull(rst.getString("line_id")));
			   h.setPayType(Utils.isNull(rst.getString("paytype")));
			   h.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   payDateStr = Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITHOUT_SLASH);
			   payDateYYYYMMLong = new Long(payDateStr.substring(4,8)+payDateStr.substring(2,4)).longValue();
			   
			   if(payDateYYYYMMLong <= criYYYYMMLong ){
			      h.setPayAmt("0");
			   }else{
				  h.setPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));  
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
	 
	public static SAReportBean searchData4OrisoftReport(SAReportBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchData4OrisoftReportModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
	}
	   
	public static SAReportBean searchData4OrisoftReportModel(Connection conn,SAReportBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String asOfMonth = "";
			SAReportBean h = null;
			List<SAReportBean> items = new ArrayList<SAReportBean>();
			int r = 1;
			try {
				//Convert Month Christ = Budish Date
			    Date d = Utils.parse("01"+o.getMonth(),Utils.DD_MM_YYYY_WITHOUT_SLASH);
			    Calendar c = Calendar.getInstance();
			    c.setTime(d);
			    asOfMonth =  Utils.stringValue(c.getTime(), "MMyyyy",Utils.local_th);
				
			   sql.append(" \n select E.* ");
			   sql.append("\n ,(select (nvl(sum(bme_amt),0)+nvl(sum(wacoal_amt),0)) as reward_amt");
			   sql.append("\n   FROM SA_REWARD_TRAN T");
			   sql.append("\n   WHERE T.emp_id = E.emp_id ");
			   sql.append("\n   and TO_CHAR(T.paydate,'MMYYYY') = '"+o.getMonth()+"'");
			   sql.append("\n   GROUP BY T.emp_id ");
			   sql.append("\n  ) as reward_amt");
			   
			   sql.append("\n ,(select nvl(sum(pay_amt),0) as total_damage");
			   sql.append("\n   FROM SA_DAMAGE_TRAN T");
			   sql.append("\n   WHERE E.emp_id = T.emp_id  ");
			   sql.append("\n   and TO_CHAR(T.paydate,'MMYYYY') = '"+o.getMonth()+"'");
			   sql.append("\n   and paytype in('2. หักค่าเฝ้าตู้','3. หักเงินเดือน')");
			   sql.append("\n   GROUP BY T.emp_id ");
			   sql.append("\n  )as net_damage_amt");
			   
			   sql.append("\n ,(select nvl(sum(pay_amt),0) as total_damage");
			   sql.append("\n   FROM SA_DAMAGE_TRAN T");
			   sql.append("\n   WHERE E.emp_id = T.emp_id  ");
			   sql.append("\n   and TO_CHAR(T.paydate,'MMYYYY') = '"+o.getMonth()+"'");
			   sql.append("\n   and paytype = '4. หัก Surety bond'");
			   sql.append("\n   GROUP BY T.emp_id ");
			   sql.append("\n  )as net_surety_bond_amt");
			   
			   sql.append("\n  from SA_EMPLOYEE E ");
			   
			   sql.append("\n  WHERE 1=1");
			   sql.append("\n  AND E.leave_date is null");
			   sql.append("\n order by E.emp_id asc ");
				
			  logger.debug("sql:"+sql);
				
			  ps = conn.prepareStatement(sql.toString());
			  rst = ps.executeQuery();
				
			   while(rst.next()) {
				  h = new SAReportBean();
				  
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurname(Utils.isNull(rst.getString("surname")));
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setAsOfMonth(asOfMonth);
				   
				   h.setSuretyBondAmt(Utils.decimalFormat(rst.getDouble("surety_bond"),Utils.format_current_2_disgit));
				   h.setRewardAmt(Utils.decimalFormat(rst.getDouble("reward_amt"),Utils.format_current_2_disgit));
				   h.setNetDamageAmt(Utils.decimalFormat(rst.getDouble("net_damage_amt"),Utils.format_current_2_disgit));
				   h.setNetSuretyBondAmt(Utils.decimalFormat(rst.getDouble("net_surety_bond_amt"),Utils.format_current_2_disgit));
				   
				   items.add(h);
				   r++;
				   
				}//while
				
				//set Result 
				o.setItems(items);
				
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

}
