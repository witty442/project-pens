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
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class SAEmpDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 public static SAEmpBean getEmp(String empId) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getEmpModel(conn,empId);
		 }catch(Exception e){
			 throw e;
		 } finally {
				try {
					conn.close();
				} catch (Exception e) {}
			}
	 }
	 
	 public static SAEmpBean getEmp(Connection conn,String empId) throws Exception {
		 try{
			 return getEmpModel(conn,empId);
		 }catch(Exception e){
			 throw e;
		 } finally {
			}
	 }
	 
	 public static SAEmpBean getEmpModel(Connection conn,String empId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			SAEmpBean bean = null;
			try {
				sql.append("\nSELECT  name,surname,branch,group_store,reward_bme,reward_wacoal ");
				sql.append("\n FROM  SA_EMPLOYEE ");
				sql.append("\n WHERE emp_id = '"+empId+"' ");
							
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					bean = new SAEmpBean();
				
					bean.setName(Utils.isNull(rst.getString("name")));
					bean.setSurName(Utils.isNull(rst.getString("surname")));
					bean.setBranch(Utils.isNull(rst.getString("branch")));
					bean.setGroupStore(Utils.isNull(rst.getString("group_store")));
					bean.setRewardBme(Utils.decimalFormat(rst.getDouble("reward_bme"),Utils.format_current_2_disgit));
					bean.setRewardWacoal(Utils.decimalFormat(rst.getDouble("reward_wacoal"),Utils.format_current_2_disgit));
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
	 
	 public static boolean isDuplicateEmployeeId(Connection conn,String newEmpId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from SA_EMPLOYEE H ");
				sql.append("\n where 1=1   \n");
				sql.append("\n and H.emp_id = '"+Utils.isNull(newEmpId)+"'");
			    
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
	 
	 public static boolean isDuplicateOracleRefId(Connection conn,String empId,String oracleRefId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from SA_EMPLOYEE H ");
				sql.append("\n where 1=1   \n");
				if( !Utils.isNull(empId).equals(""))
				sql.append("\n and H.emp_id <>'"+empId+"' \n");
				sql.append("\n and H.oracle_ref_id ='"+Utils.isNull(oracleRefId)+"'");
			    
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
	 
	 public static SAEmpBean insertModel(Connection conn,SAEmpBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertMCEmpModel");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO SA_EMPLOYEE( \n");
				sql.append("  EMP_REF_ID, EMP_ID, ORACLE_REF_ID \n");//1-3
				sql.append(", TYPE, NAME, SURNAME \n");//4-6
				sql.append(", MOBILE_NO, EMAIL, BANK_ACCOUNT \n");//7-9
				sql.append(", IDCARD, REGION, GROUP_STORE \n");//10 -12
				sql.append(", BRANCH, START_WORKING_DATE, LEAVE_DATE \n");//13 -15
				sql.append(", LEAVE_REASON, REWARD_BME, START_REWARD_BME_DATE \n");//16-18
				sql.append(", REWARD_WACOAL, START_REWARD_WACOAL_DATE, SURETY_BOND \n");//19-21
				sql.append(", START_SURETY_BOND_DATE, CREATE_DATE, CREATE_USER) \n");//22-24
				
				sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");//15-24
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				//Gen EmpRefId(seq id)
				int empRefId = SequenceProcess.getNextValue("SA_EMPLOYEE");
				o.setEmpRefId(empRefId+"");
				
				int c =1;
				ps.setInt(c++, empRefId); //1
				ps.setString(c++, o.getEmpId());//2
				ps.setString(c++, o.getOracleRefId());//3
				ps.setString(c++, Utils.isNull(o.getEmpType())); //4
				ps.setString(c++, Utils.isNull(o.getName()));//5
				ps.setString(c++, Utils.isNull(o.getSurName()));//6
				ps.setString(c++, Utils.isNull(o.getMobile()));//7
				ps.setString(c++, Utils.isNull(o.getEmail()));//8
				ps.setString(c++, Utils.isNull(o.getBankAccount()));//9
				ps.setString(c++, Utils.isNull(o.getIdCard()));//10
				ps.setString(c++, Utils.isNull(o.getRegion()));//11
				ps.setString(c++, Utils.isNull(o.getGroupStore()));//12
				ps.setString(c++, Utils.isNull(o.getBranch()));//13
				if( !Utils.isNull(o.getStartDate()).equals("")){//14
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				if( !Utils.isNull(o.getLeaveDate()).equals("")){//15
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getLeaveDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setString(c++, Utils.isNull(o.getLeaveReason()));//16
				ps.setInt(c++, Utils.convertStrToInt(o.getRewardBme()));//17
				
				if( !Utils.isNull(o.getStartRewardBmeDate()).equals("")){//18
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartRewardBmeDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
                ps.setInt(c++, Utils.convertStrToInt(o.getRewardWacoal()));//19
				
				if( !Utils.isNull(o.getStartRewardWacoalDate()).equals("")){//20
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartRewardWacoalDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
				ps.setInt(c++, Utils.convertStrToInt(o.getSuretyBond()));//21
					
				if( !Utils.isNull(o.getStartSuretyBondDate()).equals("")){//22
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartSuretyBondDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//23
				ps.setString(c++, o.getCreateUser());//24
	

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
	 
	 public static int updateModel(Connection conn,SAEmpBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("  UPDATE SA_EMPLOYEE SET   \n");
				sql.append("  ORACLE_REF_ID =?,TYPE=?, NAME=?, SURNAME=? \n");//1-4
				sql.append(", MOBILE_NO=?, EMAIL=?, BANK_ACCOUNT=?, IDCARD=? \n");//5-8
				sql.append(", REGION=?, GROUP_STORE=? , BRANCH=?, LEAVE_DATE=?  \n");//9 -12
				sql.append(", LEAVE_REASON=?, REWARD_BME=?, START_REWARD_BME_DATE=? , REWARD_WACOAL=? \n");//13-16
				sql.append(", START_REWARD_WACOAL_DATE=?, SURETY_BOND=?, START_SURETY_BOND_DATE=?, UPDATE_DATE=?, UPDATE_USER=? ,START_WORKING_DATE = ? \n");//17-21
				
				sql.append(" WHERE EMP_ID = ?  \n" );//22

				ps = conn.prepareStatement(sql.toString());
			
				ps.setString(c++, o.getOracleRefId());//1
				ps.setString(c++, Utils.isNull(o.getEmpType())); //2
				ps.setString(c++, Utils.isNull(o.getName()));//3
				ps.setString(c++, Utils.isNull(o.getSurName()));//4
				ps.setString(c++, Utils.isNull(o.getMobile()));//5
				ps.setString(c++, Utils.isNull(o.getEmail()));//6
				ps.setString(c++, Utils.isNull(o.getBankAccount()));//7
				ps.setString(c++, Utils.isNull(o.getIdCard()));//8
				ps.setString(c++, Utils.isNull(o.getRegion()));//9
				ps.setString(c++, Utils.isNull(o.getGroupStore()));//10
				ps.setString(c++, Utils.isNull(o.getBranch()));//11
				if( !Utils.isNull(o.getLeaveDate()).equals("")){//12
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getLeaveDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				   ps.setTimestamp(c++,null);
				}
				ps.setString(c++, Utils.isNull(o.getLeaveReason()));//13
				ps.setInt(c++, Utils.convertStrToInt(o.getRewardBme()));//14
				
				if( !Utils.isNull(o.getStartRewardBmeDate()).equals("")){//15
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartRewardBmeDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
                ps.setInt(c++, Utils.convertStrToInt(o.getRewardWacoal()));//16
				
				if( !Utils.isNull(o.getStartRewardWacoalDate()).equals("")){//17
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartRewardWacoalDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
				ps.setInt(c++, Utils.convertStrToInt(o.getSuretyBond()));//18
					
				if( !Utils.isNull(o.getStartSuretyBondDate()).equals("")){//19
				   ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartSuretyBondDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//20
				ps.setString(c++, o.getCreateUser());//21
				
				if( !Utils.isNull(o.getStartDate()).equals("")){//21
					ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					ps.setTimestamp(c++,null);
				}
				
				//key
				ps.setString(c++,o.getEmpId());//22
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 

   public static SAEmpBean searchHead(Connection conn,SAEmpBean o) throws Exception {
	  return searchHeadModel(conn,o,"");
	}
   
   public static SAEmpBean searchHead(SAEmpBean o ,String mode) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchHeadModel(conn, o,mode);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	public static SAEmpBean searchHeadModel(Connection conn,SAEmpBean o ,String mode) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			SAEmpBean h = null;
			List<SAEmpBean> items = new ArrayList<SAEmpBean>();
			int r = 1;
			try {
			   sql.append(" \n select S.*" );
			   sql.append(" \n,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Region' AND M.pens_value =S.region)as region_desc" );
			   
			   sql.append(" \n,(SELECT NVL(SUM(M.amt),0) FROM sa_reward_tran M where M.type='BME' AND M.emp_id = S.emp_id group by M.emp_id) as total_reward_bme" );
			   sql.append(" \n,(SELECT NVL(SUM(M.amt),0) FROM sa_reward_tran M where M.type='WACOAL' AND M.emp_id = S.emp_id group by M.emp_id) as total_reward_wacoal" );
			   
			   if( !Utils.isNull(o.getDispDamage()).equals("")){
				   sql.append(" \n,(SELECT NVL(SUM(M.total_damage),0) FROM sa_damage_head M where M.emp_id = S.emp_id group by M.emp_id) as total_damage" );
				   
				   sql.append(" \n,(SELECT NVL(SUM(t.pay_amt),0) FROM sa_damage_tran t" );
				   sql.append(" \n  where t.emp_id = S.emp_id " );
				   sql.append(" \n  and t.paydate <= sysdate "); 
				   sql.append(" \n  group by t.emp_id ");
				   sql.append(" \n )as total_payment" );
				   
				   sql.append(" \n,(SELECT NVL(SUM(t.pay_amt),0) FROM sa_damage_tran t" );
				   sql.append(" \n  where t.emp_id = S.emp_id  " );
				   sql.append(" \n  and t.paydate > sysdate "); 
				   sql.append(" \n  group by t.emp_id ");
				   sql.append(" \n )as total_delay_payment" );
				}
			   sql.append("\n  from SA_EMPLOYEE S ");
			   sql.append("\n  WHERE 1=1");
			   if( !Utils.isNull(o.getEmpId()).equals("") ){
					sql.append("\n and S.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
			   }
			   if( !Utils.isNull(o.getEmpRefId()).equals("")){
					sql.append("\n and S.emp_ref_id ="+o.getEmpRefId());
				}
			   if( !Utils.isNull(o.getOracleRefId()).equals("")){
					sql.append("\n and S.oracle_ref_id ='"+o.getOracleRefId()+"'");
				}
				if( !Utils.isNull(o.getEmpType()).equals("")){
					sql.append("\n and S.type = '"+Utils.isNull(o.getEmpType())+"'");
				}
				if( !Utils.isNull(o.getRegion()).equals("")){
					sql.append("\n and S.region = '"+Utils.isNull(o.getRegion())+"'");
				}
				if( !Utils.isNull(o.getName()).equals("")){
					sql.append("\n and S.name LIKE '%"+Utils.isNull(o.getName())+"%'");
				}
				if( !Utils.isNull(o.getSurName()).equals("")){
					sql.append("\n and S.surname LIKE '%"+Utils.isNull(o.getSurName())+"%'");
				}
				if( !Utils.isNull(o.getGroupStore()).equals("")){
					sql.append("\n and S.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
				}
				if( !Utils.isNull(o.getBranch()).equals("")){
					sql.append("\n and S.branch = '"+Utils.isNull(o.getBranch())+"'");
				}
				if( !Utils.isNull(o.getDispDamage()).equals("")){
					
				}
				
				sql.append("\n order by S.name asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new SAEmpBean();
				   h.setNo(r);
				   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				   h.setOrgEmpId(Utils.isNull(rst.getString("emp_id")));
				  
				   h.setOracleRefId(Utils.isNull(rst.getString("oracle_ref_id")));
				   h.setEmpType(Utils.isNull(rst.getString("type")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurName(Utils.isNull(rst.getString("surname")));
				   h.setMobile(Utils.isNull(rst.getString("mobile_no")));
				   h.setRegion(Utils.isNull(rst.getString("region")));
				   h.setRegionDesc(Utils.isNull(rst.getString("region_desc")));
				   h.setBankAccount(Utils.isNull(rst.getString("bank_account")));
				   h.setIdCard(Utils.isNull(rst.getString("idcard")));
				   h.setEmail(Utils.isNull(rst.getString("email")));
				   h.setStartDate(DateUtil.stringValue(rst.getDate("start_working_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   
				   if(rst.getDate("leave_date") !=null){
				     h.setLeaveDate(DateUtil.stringValue(rst.getDate("leave_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					 h.setLeaveDate("");
				   }
				   h.setLeaveReason(Utils.isNull(rst.getString("leave_reason")));
				   if(rst.getDate("start_reward_bme_date") !=null){
				      h.setStartRewardBmeDate(DateUtil.stringValue(rst.getDate("start_reward_bme_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					  h.setStartRewardBmeDate("");
				   }
				   if(rst.getInt("reward_bme") > 0){
				      h.setRewardBme(Utils.isNull(rst.getString("reward_bme")));
				   }else{
					  h.setRewardBme("");
				   }
				   if(rst.getDate("start_reward_wacoal_date") !=null){
				      h.setStartRewardWacoalDate(DateUtil.stringValue(rst.getDate("start_reward_wacoal_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					  h.setStartRewardWacoalDate("");
				   }
				   if(rst.getInt("reward_wacoal") > 0){
				       h.setRewardWacoal(Utils.isNull(rst.getString("reward_wacoal")));
				   }else{
					   h.setRewardWacoal("");  
				   }
				   if(rst.getDate("start_surety_bond_date") !=null){
				      h.setStartSuretyBondDate(DateUtil.stringValue(rst.getDate("start_surety_bond_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					  h.setStartSuretyBondDate("");
				   }
				   if(rst.getInt("surety_bond") > 0){
				      h.setSuretyBond(Utils.isNull(rst.getString("surety_bond")));
				   }else{
					  h.setSuretyBond("");
				   }
					   
				   if("edit".equalsIgnoreCase(mode)){
					   h.setDisableTextClass("disableText");
				   }
				   
				   if( !Utils.isNull(o.getDispDamage()).equals("")){
						h.setTotalDamage(Utils.decimalFormat(rst.getDouble("total_damage"),Utils.format_current_2_disgit));
						h.setTotalPayment(Utils.decimalFormat(rst.getDouble("total_payment"),Utils.format_current_2_disgit));
						h.setTotalDelayPayment(Utils.decimalFormat(rst.getDouble("total_delay_payment"),Utils.format_current_2_disgit));
					}
				   
				   h.setTotalRewardBme(Utils.decimalFormat(rst.getDouble("total_reward_bme"),Utils.format_current_2_disgit));
				   h.setTotalRewardWacoal(Utils.decimalFormat(rst.getDouble("total_reward_wacoal"),Utils.format_current_2_disgit));
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
	 
	
	 public static  String getTransDetail(Connection conn,String staffId,String monthTrip,String dayOfWeek) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			try {
				sql.append("\n select D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(monthTrip)+"'");
				sql.append("\n and D.day_of_week = '"+Utils.isNull(dayOfWeek)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				  detail = Utils.isNull(rst.getString("detail"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return detail;
		}
	 
	 public static  String getTransDetailByDay(Connection conn,String staffId,String monthTrip,String day) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			try {
				sql.append("\n select D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(monthTrip)+"'");
				sql.append("\n and D.day = '"+Utils.isNull(day)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				  detail = Utils.isNull(rst.getString("detail"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return detail;
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
