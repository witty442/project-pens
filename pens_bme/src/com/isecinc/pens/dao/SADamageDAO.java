package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.SADamageBean;
import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.SequenceHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class SADamageDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 public static boolean isDuplicateInvRefWal(Connection conn,String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from SA_DAMAGE_HEAD ");
				sql.append("\n where inv_refwal = '"+Utils.isNull(invRefwal)+"'");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					if(rst.getInt("c") > 0){
						dup = true;
					}
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return dup;
		}
	
	 public static boolean isDuplicateDamageHeadPK(Connection conn,String empId,String type,String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from SA_DAMAGE_HEAD H ");
				sql.append("\n where 1=1   \n");
				sql.append("\n and H.emp_id = '"+Utils.isNull(empId)+"'");
				sql.append("\n and H.type = '"+Utils.isNull(type)+"'");
				sql.append("\n and H.inv_refwal = '"+Utils.isNull(invRefwal)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					if(rst.getInt("c") > 0){
						dup = true;
					}
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return dup;
		}
	 
	 public static int getMaxIdFromDanageTran(Connection conn,String empId,String type,String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			int id = 0;
			try {
				sql.append("\n select max(id) as id from SA_DAMAGE_TRAN H ");
				sql.append("\n where 1=1   \n");
				sql.append("\n and H.emp_id = '"+Utils.isNull(empId)+"'");
				sql.append("\n and H.type = '"+Utils.isNull(type)+"'");
				sql.append("\n and H.inv_refwal = '"+Utils.isNull(invRefwal)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					id= rst.getInt("id");
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return id;
		}
	 
	 public static SADamageBean getInvRefwalFromSaleAnalysis(String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			SADamageBean bean = null;
			try {
				sql.append("\n SELECT  ACCOUNT_NUMBER ,TRX_NUMBER ,PARTY_NAME, AMOUNT_DUE_ORIGINAL,TRX_DATE ");
				sql.append("\n  FROM  SA_AR_DAMAGE b  ");
				sql.append("\n  WHERE b.TRX_NUMBER = '"+invRefwal+"' ");
									
				logger.debug("sql:"+sql);
                conn = DBConnection.getInstance().getConnection();
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					bean = new SADamageBean();
					bean.setInvRefwal(invRefwal);
					bean.setOracleRefId(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
					bean.setOracleRefName(Utils.isNull(rst.getString("PARTY_NAME")));
					bean.setTotalDamage(Utils.decimalFormat(rst.getDouble("AMOUNT_DUE_ORIGINAL"),Utils.format_current_2_disgit));
					bean.setInvoiceDate(Utils.stringValueNull(rst.getDate("TRX_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					
					//Get EMP
					SAEmpBean em = getEmpByOracleRefId(conn,bean.getOracleRefId());
					if(em != null){
						bean.setEmpId(em.getEmpId());
						bean.setName(em.getName());
						bean.setSurname(em.getSurName());
						bean.setBranch(em.getBranch());
						bean.setGroupStore(em.getGroupStore());
						
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					conn.close();
				} catch (Exception e) {}
			}
		return bean;
	}
	 
	 public static SADamageBean getInvRefwalFromSaleAnalysis_OLD(String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			SADamageBean bean = null;
			try {
				sql.append("\nSELECT  a.customer_code  , a.customer_desc ");
				sql.append("\n, (( sum(invoiced_amt) - sum(discount_amt) ) * 1.07 ) as total_damage ");
				sql.append("\n   FROM  XXPENS_BI_MST_CUSTOMER a,XXPENS_BI_SALES_ANALYSIS b ");
				sql.append("\n   WHERE b.invoice_no = '"+invRefwal+"' ");
				sql.append("\n   AND   b.customer_id = a.customer_id ");
				sql.append("\n   group by a.customer_code  , a.customer_desc ");
									
				logger.debug("sql:"+sql);
                conn = DBConnection.getInstance().getConnection();
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					bean = new SADamageBean();
					bean.setInvRefwal(invRefwal);
					bean.setOracleRefId(Utils.isNull(rst.getString("customer_code")));
					bean.setOracleRefName(Utils.isNull(rst.getString("customer_desc")));
					bean.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
					
					//Get EMP
					SAEmpBean em = getEmpByOracleRefId(conn,bean.getOracleRefId());
					if(em != null){
						bean.setEmpId(em.getEmpId());
						bean.setName(em.getName());
						bean.setSurname(em.getSurName());
						bean.setBranch(em.getBranch());
						bean.setGroupStore(em.getGroupStore());
					}
				}else{
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					conn.close();
				} catch (Exception e) {}
			}
		return bean;
	}
	 
	 public static SAEmpBean getEmpByOracleRefId(Connection conn,String oracleRefId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			SAEmpBean bean = null;
			try {
				sql.append("\nSELECT  emp_id,name,surname,branch,group_store ");
				sql.append("\n FROM  SA_EMPLOYEE ");
				sql.append("\n WHERE oracle_ref_id = '"+oracleRefId+"' ");
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					bean = new SAEmpBean();
					bean.setEmpId(Utils.isNull(rst.getString("emp_id")));
					bean.setName(Utils.isNull(rst.getString("name")));
					bean.setSurName(Utils.isNull(rst.getString("surname")));
					bean.setBranch(Utils.isNull(rst.getString("branch")));
					bean.setGroupStore(Utils.isNull(rst.getString("group_store")));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();

				} catch (Exception e) {}
			}
			return bean;
	 }
	 
	 public static String genDummyInvRefWal(String empId,String transDate){
		 
		 return empId+"-"+transDate.replaceAll("/", "");
	 }
	 
	 public static String getInvRefwalInDamageHead(String invRefwal) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String inv_refwal = "";
			try {
				sql.append("\n SELECT inv_refwal  FROM  SA_DAMAGE_HEAD");
				sql.append("\n  WHERE inv_refwal = '"+invRefwal+"' ");
	
									
				logger.debug("sql:"+sql);
                conn = DBConnection.getInstance().getConnection();
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					inv_refwal = Utils.isNull(rst.getString("inv_refwal"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					conn.close();
				} catch (Exception e) {}
			}
			return inv_refwal;
		}
	 
	 public static SADamageBean insertHeadModel(Connection conn,SADamageBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;
			try{
		
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO SA_DAMAGE_HEAD \n");
				sql.append("(EMP_ID, TYPE, INV_REFWAL, tran_date, oracle_ref_id" +
						", oracle_ref_name, name,surname,group_store,branch" +
						",check_stock_date,total_damage,remark, CREATE_DATE, CREATE_USER,INVOICE_DATE) \n");
				sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? ,?,? ,?) \n");//15
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getType())); //2
				ps.setString(c++, Utils.isNull(o.getInvRefwal()));//3
				if( !Utils.isNull(o.getTranDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getTranDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				
				ps.setString(c++, Utils.isNull(o.getOracleRefId()));//5
				ps.setString(c++, Utils.isNull(o.getOracleRefName()));//6
				ps.setString(c++, Utils.isNull(o.getName()));//7
				ps.setString(c++, Utils.isNull(o.getSurname()));//8
				ps.setString(c++, Utils.isNull(o.getGroupStore()));//9
				ps.setString(c++, Utils.isNull(o.getBranch()));//10
				
				if( !Utils.isNull(o.getCheckStockDate()).equals("")){//11
				   ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getCheckStockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getTotalDamage()));//12
				ps.setString(c++, Utils.isNull(o.getRemark()));//13
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//14
				ps.setString(c++, o.getCreateUser());//15
				
				if( !Utils.isNull(o.getInvoiceDate()).equals("")){//16
					 ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getInvoiceDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return o;
		}
	 
	 public static SADamageBean updateHeadModel(Connection conn,SADamageBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateHeadModel");
			int c =1;
			try{
		
				StringBuffer sql = new StringBuffer("");
				sql.append("UPDATE SA_DAMAGE_HEAD \n");
				sql.append("SET check_stock_date =?,total_damage=?,remark=?, UPDATE_DATE=?, UPDATE_USER=? \n");
				sql.append("WHERE EMP_ID =? AND TYPE =? AND INV_REFWAL =? \n");
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				if( !Utils.isNull(o.getCheckStockDate()).equals("")){//1
				   ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getCheckStockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getTotalDamage()));//2
				ps.setString(c++, Utils.isNull(o.getRemark()));//3
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//4
				ps.setString(c++, o.getCreateUser());//5
				
				ps.setString(c++, Utils.isNull(o.getEmpId()));//6
				ps.setString(c++, Utils.isNull(o.getType())); //7
				ps.setString(c++, Utils.isNull(o.getInvRefwal()));//8
				

				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return o;
		}
	 
	 public static int insertItemModel(Connection conn,SADamageBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO SA_DAMAGE_TRAN \n");
				sql.append("(ID,EMP_ID, TYPE, INV_REFWAL, LINE_ID, PAYTYPE, PAYDATE, PAY_AMT, CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?) \n");//15-24
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getId()));
				
				ps.setString(c++, Utils.isNull(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getType())); //2
				ps.setString(c++, Utils.isNull(o.getInvRefwal()));//3
				ps.setString(c++, Utils.isNull(o.getLineId()));//4
				
				ps.setString(c++, Utils.isNull(o.getPayType()));//5
				
				if( !Utils.isNull(o.getPayDate()).equals("")){//6
				   ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getPayDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getPayAmt()));//7
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//8
				ps.setString(c++, o.getCreateUser());//9

				c = ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return c;
		}
	 
	 public static int updateItemModelById(Connection conn,SADamageBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE SA_DAMAGE_TRAN \n");
				sql.append("SET LINE_ID =? ,PAYTYPE = ?, PAYDATE =?, PAY_AMT =?, UPDATE_DATE=?, UPDATE_USER = ? \n");
				sql.append("WHERE EMP_ID= ? AND TYPE =? AND INV_REFWAL=? AND ID= ?");
			
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getLineId()));//1
				ps.setString(c++, Utils.isNull(o.getPayType()));//1
				
				if( !Utils.isNull(o.getPayDate()).equals("")){//2
				   ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getPayDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getPayAmt()));//3
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//4
				ps.setString(c++, o.getUpdateUser());//5
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));//6
				ps.setString(c++, Utils.isNull(o.getType())); //7
				ps.setString(c++, Utils.isNull(o.getInvRefwal()));//8
				ps.setInt(c++, Utils.convertStrToInt(o.getId()));//9
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 
	 public static int deleteItemModelById(Connection conn,SADamageBean o,String lineIdDelete) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM SA_DAMAGE_TRAN \n");
				sql.append("WHERE EMP_ID=? AND  TYPE=? AND INV_REFWAL=? \n");
				sql.append("AND ID IN ("+lineIdDelete+") \n");
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getType())); //2
				ps.setString(c++, Utils.isNull(o.getInvRefwal()));//3
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 

   public static SADamageBean searchHead(Connection conn,SADamageBean o,boolean getItems,String typeSearch) throws Exception {
	  return searchHeadModel(conn,o,"",getItems,typeSearch);
	}
   
   public static SADamageBean searchHead(Connection conn,String mode,SADamageBean o,boolean getItems,String typeSearch) throws Exception {
	  return searchHeadModel(conn,o,mode,getItems,typeSearch);
	}
   public static SADamageBean searchHead(SADamageBean o ,String mode,boolean getItems,String typeSearch) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchHeadModel(conn, o,mode,getItems,typeSearch);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	public static SADamageBean searchHeadModel(Connection conn,SADamageBean o ,String mode,boolean getItems,String typeSearch) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			SADamageBean h = null;
			List<SADamageBean> items = new ArrayList<SADamageBean>();
			int r = 1;
			try {
			   sql.append(" \n select S.*" );
			 //  sql.append(" \n,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Region' AND M.pens_value =S.region)as region_desc" );
			   sql.append("\n  from SA_DAMAGE_HEAD S ,SA_EMPLOYEE E ");
			   sql.append("\n  WHERE 1=1");
			   sql.append("\n  AND E.emp_id = S.emp_id");
			    if( !Utils.isNull(o.getEmpId()).equals("") ){
					sql.append("\n and S.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
			    }
			    if( !Utils.isNull(o.getOracleRefId()).equals("")){
					sql.append("\n and S.oracle_ref_id ='"+o.getOracleRefId()+"'");
				}
				if( !Utils.isNull(o.getType()).equals("")){
					sql.append("\n and S.type = '"+Utils.isNull(o.getType())+"'");
				}
				if( !Utils.isNull(o.getName()).equals("")){
					sql.append("\n and E.name LIKE '%"+Utils.isNull(o.getName())+"%'");
				}
				if( !Utils.isNull(o.getSurname()).equals("")){
					sql.append("\n and E.surname LIKE '%"+Utils.isNull(o.getSurname())+"%'");
				}
				if( !Utils.isNull(o.getGroupStore()).equals("")){
					sql.append("\n and S.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
				}
				if( !Utils.isNull(o.getBranch()).equals("")){
					sql.append("\n and S.branch = '"+Utils.isNull(o.getBranch())+"'");
				}
				if( !Utils.isNull(o.getInvRefwal()).equals("")){
					sql.append("\n and S.inv_refwal = '"+Utils.isNull(o.getInvRefwal())+"'");
				}
				if( !Utils.isNull(o.getTranDate()).equals("")){
					Date salesDateFromParam = Utils.parse(o.getTranDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String date = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(S.tran_date) = to_date('"+date+"','dd/mm/yyyy') \n");
				}
				if( !Utils.isNull(o.getCheckStockDate()).equals("")){
					Date salesDateFromParam = Utils.parse(o.getCheckStockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String date = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(S.check_stock_date) = to_date('"+date+"','dd/mm/yyyy') \n");
				}
				
				if(typeSearch.equalsIgnoreCase("NoDamage")){
					sql.append("\n and S.oracle_ref_id is null ");
				}else{
					sql.append("\n and S.oracle_ref_id is not null ");
				}
				
				sql.append("\n order by S.tran_date desc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				  h = new SADamageBean();
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				   h.setInvRefwal(Utils.isNull(rst.getString("inv_refwal")));
				   h.setType(Utils.isNull(rst.getString("type")));
				   
				   h.setOracleRefId(Utils.isNull(rst.getString("oracle_ref_id")));
				   h.setOracleRefName(Utils.isNull(rst.getString("oracle_ref_name")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurname(Utils.isNull(rst.getString("surname")));
				   h.setFullName(h.getName()+" "+h.getSurname());
				   
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
				   
				   if(rst.getDate("tran_date") !=null){
				     h.setTranDate(Utils.stringValue(rst.getDate("tran_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					 h.setTranDate("");
				   }
				   if(rst.getDate("check_stock_date") !=null){
				     h.setCheckStockDate(Utils.stringValue(rst.getDate("check_stock_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					 h.setCheckStockDate("");
				   }
				   h.setInvoiceDate(Utils.stringValueNull(rst.getDate("invoice_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   if("edit".equalsIgnoreCase(mode)){
					   h.setDisableTextClass("disableText");
				   }
				   if(getItems){
					   h.setItems(getTrandList(conn,h));
				   }
				   
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
	 
	
	 public static  List<SADamageBean> getTrandList(Connection conn,SADamageBean h) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<SADamageBean> items = new ArrayList<SADamageBean>();
			try {
				sql.append("\n select D.* " );
				sql.append("\n from SA_DAMAGE_TRAN D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.emp_id = "+Utils.isNull(h.getEmpId())+"");
				sql.append("\n and D.type = '"+Utils.isNull(h.getType())+"'");
				sql.append("\n and D.Inv_refwal = '"+Utils.isNull(h.getInvRefwal())+"'");
				sql.append("\n order by line_id asc");
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
					SADamageBean item = new SADamageBean();
					item.setId(rst.getString("id"));
					item.setLineId(rst.getString("line_id"));
					item.setPayType(Utils.isNull(rst.getString("paytype")));
					if(rst.getDate("paydate") !=null){
					    item.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 }else{
						item.setPayDate("");
					 }
					item.setPayAmt(Utils.decimalFormat(rst.getDouble("pay_amt"),Utils.format_current_2_disgit));
		
					items.add(item);
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
	 
	
	 
	 public static List<PopupForm> getMasterListByRefCode(PopupForm c,String equals,String refCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* from PENSBME_MST_REFERENCE M");
				sql.append("\n  where 1=1 and reference_code ='"+refCode+"' ");
				if("equals".equals(equals)){
					sql.append("\n  and M.pens_value ='"+c.getCodeSearch()+"'");
				}
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_value")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					pos.add(item);
					
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
	
}
