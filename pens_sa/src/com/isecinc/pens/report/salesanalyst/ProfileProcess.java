package com.isecinc.pens.report.salesanalyst;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.GroupRole;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class ProfileProcess {

	private static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static boolean saveProfile(User u,SABean b) throws Exception{
		Connection conn = null;
		boolean s = false;
		String allCond = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(b.getTypeSearch())){
				logger.debug("chkMonth:"+b.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<b.getChkMonth().length;i++){
					logger.debug("name:["+i+"]value:["+b.getChkMonth()[i]+"]");
					allCond +=""+b.getChkMonth()[i]+",";
				}
			}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(b.getTypeSearch())){
				logger.debug("chkQuarter:"+b.getChkQuarter().length);
				/** Set Group Display  **/
				for(int i=0;i<b.getChkQuarter().length;i++){
					logger.debug("name:["+i+"]value:["+b.getChkQuarter()[i]+"]");
					allCond +=""+b.getChkQuarter()[i]+",";
				}
				
			}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(b.getTypeSearch())){
				logger.debug("chkYear:"+b.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<b.getChkYear().length;i++){
					logger.debug("name:["+i+"]value:["+b.getChkYear()[i]+"]");
					allCond +=""+b.getChkYear()[i]+",";
				}
			}
			
			boolean update = updateProfileModel(conn, u, b,allCond);
			if( !update){
				insertProfileModel(conn, u, b,allCond);
			}
			s = true;
			conn.commit();
		}catch(Exception e){
			s = false;
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
		return s;
	}
	
	public static  SABean getProfile(String userId, String profileId)throws Exception{
		String sql = "";
		Connection conn = null;
		SABean b =  new SABean();
		PreparedStatement ps = null;
		ResultSet rs= null;
	    try{
	    	sql =" select * from PENSBI.C_USER_PROFILE where user_id='"+userId+"'and profile_id = '"+profileId+"'" ;
	    	conn = DBConnection.getInstance().getConnection();
	    	ps = conn.prepareStatement(sql);
	    	rs = ps.executeQuery();
	    	
	    	b.setUserId(userId);
    		b.setProfileId(profileId);
    		
	    	if(rs.next()){
	    		b.setTypeSearch(Utils.isNull(rs.getString("TYPE_SEARCH")));
	    		b.setGroupBy(Utils.isNull(rs.getString("GROUP_BY")));
	    		b.setDay(Utils.isNull(rs.getString("DATE_FROM")));
	    		b.setDayTo(Utils.isNull(rs.getString("DATE_TO")));
	    		
	    		if( !Utils.isNull(rs.getString("MONTH")).equals("")){
	    		   b.setChkMonth(Utils.isNull(rs.getString("MONTH")).split(","));
	    		}
	    		
	    		if( !Utils.isNull(rs.getString("QUARTER")).equals("")){
		    	   b.setChkQuarter(Utils.isNull(rs.getString("QUARTER")).split(","));
		    	}
	    		
	    		if( !Utils.isNull(rs.getString("YEAR")).equals("")){
		    		b.setChkYear(Utils.isNull(rs.getString("YEAR")).split(","));
		    	}

	    		b.setCondName1(Utils.isNull(rs.getString("COND_NAME1")));
	    		b.setCondName2(Utils.isNull(rs.getString("COND_NAME2")));
	    		b.setCondName3(Utils.isNull(rs.getString("COND_NAME3")));
	    		b.setCondName4(Utils.isNull(rs.getString("COND_NAME4")));
	    		
	    		b.setCondCode1(Utils.isNull(rs.getString("COND_CODE1")));
	    		b.setCondCode2(Utils.isNull(rs.getString("COND_CODE2")));
	    		b.setCondCode3(Utils.isNull(rs.getString("COND_CODE3")));
	    		b.setCondCode4(Utils.isNull(rs.getString("COND_CODE4")));
	    		
	    		b.setCondValue1(Utils.isNull(rs.getString("COND_VALUE1")));
	    		b.setCondValue2(Utils.isNull(rs.getString("COND_VALUE2")));
	    		b.setCondValue3(Utils.isNull(rs.getString("COND_VALUE3")));
	    		b.setCondValue4(Utils.isNull(rs.getString("COND_VALUE4")));
	    		
	    		b.setCondValueDisp1(Utils.isNull(rs.getString("COND_VALUE_DISP1")));
	    		b.setCondValueDisp2(Utils.isNull(rs.getString("COND_VALUE_DISP2")));
	    		b.setCondValueDisp3(Utils.isNull(rs.getString("COND_VALUE_DISP3")));
	    		b.setCondValueDisp4(Utils.isNull(rs.getString("COND_VALUE_DISP4")));
	    		
	    		b.setColNameDisp1(Utils.isNull(rs.getString("COL_NAME1")));
	    		b.setColNameDisp2(Utils.isNull(rs.getString("COL_NAME2")));
	    		b.setColNameDisp3(Utils.isNull(rs.getString("COL_NAME3")));
	    		b.setColNameDisp4(Utils.isNull(rs.getString("COL_NAME4")));
	    		
	    		b.setColNameUnit1(Utils.isNull(rs.getString("COL_UNIT1")));
	    		b.setColNameUnit2(Utils.isNull(rs.getString("COL_UNIT2")));
	    		b.setColNameUnit3(Utils.isNull(rs.getString("COL_UNIT3")));
	    		b.setColNameUnit4(Utils.isNull(rs.getString("COL_UNIT4")));
	    		
	    		b.setCompareDisp1(Utils.isNull(rs.getString("COMPARE_DISP1")));
	    		b.setCompareDisp2(Utils.isNull(rs.getString("COMPARE_DISP2")));
	    		
	    		b.setSummaryType(Utils.isNull(rs.getString("SUMMARY_TYPE")));
	    		
	    	}
	    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(conn != null){
	    	   conn.close();conn=null;
	    	}
	    }
	    return b;
	}
	
	
	public static boolean insertProfileModel(Connection conn,User u,SABean b,String allCond) throws Exception{
		PreparedStatement psIns = null;
		try{
			String sqlInsert = " INSERT INTO PENSBI.C_USER_PROFILE \n"
					+" (USER_ID, PROFILE_ID, TYPE_SEARCH, DATE_FROM, DATE_TO,  \n"
					+" MONTH, QUARTER, YEAR, COND_NAME1,  \n"
					+" COND_NAME2, COND_NAME3, COND_NAME4, COND_CODE1,  \n"
					+" COND_CODE2, COND_CODE3, COND_CODE4, COND_VALUE1,  \n"
					+" COND_VALUE2, COND_VALUE3, COND_VALUE4, COND_VALUE_DISP1,  \n"
					+" COND_VALUE_DISP2, COND_VALUE_DISP3, COND_VALUE_DISP4, COL_NAME1,  \n"
					+" COL_NAME2, COL_NAME3, COL_NAME4, COL_UNIT1,  \n"
					+" COL_UNIT2, COL_UNIT3, COL_UNIT4, COMPARE_DISP1,  \n"
					+" COMPARE_DISP2, SUMMARY_TYPE, CREATED, CREATED_BY,GROUP_BY) \n"
				    +" VALUES  \n"
				    +" (?,?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?," 
				    +" ?,?,?,?" 
				    +" ,? ) \n";
			
			psIns = conn.prepareStatement(sqlInsert);
			int index = 0;
			
			psIns.setString(++index, u.getId()+"");//1 USER_ID
			psIns.setString(++index, b.getProfileId());//2 PROFILE_ID
			psIns.setString(++index, b.getTypeSearch());//3 TYPE_SEARCH
			psIns.setString(++index, Utils.isNull(b.getDay()));//4 DATE_FROM
			psIns.setString(++index, Utils.isNull(b.getDayTo()));//5 DATE_TO
			
			psIns.setString(++index, SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//6 MONTH
			psIns.setString(++index, SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//7 QUARTER
			psIns.setString(++index, SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//8 YEAR
			psIns.setString(++index, Utils.isNull(b.getCondName1()));//9 COND_NAME1
			
			psIns.setString(++index, Utils.isNull(b.getCondName2()));//10 COND_NAME2
			psIns.setString(++index, Utils.isNull(b.getCondName3()));//11 COND_NAME3
			psIns.setString(++index, Utils.isNull(b.getCondName4()));//12 COND_NAME4
			psIns.setString(++index, Utils.isNull(b.getCondCode1()));//13 COND_CODE1
			
			psIns.setString(++index, Utils.isNull(b.getCondCode2()));//14 COND_CODE2
			psIns.setString(++index, Utils.isNull(b.getCondCode3()));//15 COND_CODE3
			psIns.setString(++index, Utils.isNull(b.getCondCode4()));//16 COND_CODE4
			psIns.setString(++index, Utils.isNull(b.getCondValue1()));//17 COND_VALUE1
			
			psIns.setString(++index, Utils.isNull(b.getCondValue2()));//18 COND_VALUE2
			psIns.setString(++index, Utils.isNull(b.getCondValue3()));//19 COND_VALUE3
			psIns.setString(++index, Utils.isNull(b.getCondValue4()));//20 COND_VALUE4
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp1()));//21 COND_VALUE_DISP1
			
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp2()));//22 COND_VALUE_DISP2
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp3()));//23 COND_VALUE_DISP3
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp4()));//24 COND_VALUE_DISP4
			psIns.setString(++index, Utils.isNull(b.getColNameDisp1()));//25 COL_NAME1
			
			psIns.setString(++index, Utils.isNull(b.getColNameDisp2()));//26 COL_NAME2
			psIns.setString(++index, Utils.isNull(b.getColNameDisp3()));//27 COL_NAME3
			psIns.setString(++index, Utils.isNull(b.getColNameDisp4()));//28 COL_NAME4
			psIns.setString(++index, Utils.isNull(b.getColNameUnit1()));//29 COL_UNIT1
			
			psIns.setString(++index, Utils.isNull(b.getColNameUnit2()));//30 COL_UNIT2
			psIns.setString(++index, Utils.isNull(b.getColNameUnit3()));//31 COL_UNIT3
			psIns.setString(++index, Utils.isNull(b.getColNameUnit4()));//32 COL_UNIT4
			psIns.setString(++index, Utils.isNull(b.getCompareDisp1()));//33 COMPARE_DISP1
			
			psIns.setString(++index, Utils.isNull(b.getCompareDisp2()));//34 COMPARE_DISP2
			psIns.setString(++index, Utils.isNull(b.getSummaryType()));//35 SummaryType
			psIns.setTimestamp(++index, new java.sql.Timestamp(System.currentTimeMillis()));//36 CREATED
			psIns.setString(++index, u.getId()+"");//37 CREATED_BY
			psIns.setString(++index, Utils.isNull(b.getGroupBy()));//38 Group BY
			
			psIns.executeUpdate();
			
			
		}catch(Exception e){
            throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, psIns, null);
			
		}
		return true;
	}
	
	public static boolean updateProfileModel(Connection conn,User u,SABean b,String allCond) throws Exception{
		PreparedStatement psIns = null;
		boolean update = false;
		try{

			String sqlUpdate = " UPDATE PENSBI.C_USER_PROFILE \n"
					+" SET TYPE_SEARCH =? , DATE_FROM=?, DATE_TO=?,  \n"
					+" MONTH=?, QUARTER=?, YEAR=?, COND_NAME1=?,  \n"
					+" COND_NAME2=?, COND_NAME3=?, COND_NAME4=?, COND_CODE1=?,  \n"
					+" COND_CODE2=?, COND_CODE3=?, COND_CODE4=?, COND_VALUE1=?,  \n"
					+" COND_VALUE2=?, COND_VALUE3=?, COND_VALUE4=?, COND_VALUE_DISP1=?,  \n"
					+" COND_VALUE_DISP2=?, COND_VALUE_DISP3=?, COND_VALUE_DISP4=?, COL_NAME1=?,  \n"
					+" COL_NAME2=?, COL_NAME3=?, COL_NAME4=?, COL_UNIT1=?,  \n"
					+" COL_UNIT2=?, COL_UNIT3=?, COL_UNIT4=?, COMPARE_DISP1=?,  \n"
					+" COMPARE_DISP2=?, SUMMARY_TYPE=?, UPDATED=?, UPDATED_BY=? ,GROUP_BY =? \n"
			        +" WHERE USER_ID =? AND PROFILE_ID = ? \n";
				   
			
			psIns = conn.prepareStatement(sqlUpdate);
			int index = 0;
			
			psIns.setString(++index, b.getTypeSearch());//3 TYPE_SEARCH
			
			psIns.setString(++index, Utils.isNull(b.getDay()));//4 DATE_FROM
			psIns.setString(++index, Utils.isNull(b.getDayTo()));//5 DATE_TO
			
			psIns.setString(++index, SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//6 MONTH
			psIns.setString(++index, SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//7 QUARTER
			psIns.setString(++index, SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(b.getTypeSearch())?allCond:"");//8 YEAR
			
			psIns.setString(++index, Utils.isNull(b.getCondName1()));//9 COND_NAME1
			psIns.setString(++index, Utils.isNull(b.getCondName2()));//10 COND_NAME2
			psIns.setString(++index, Utils.isNull(b.getCondName3()));//11 COND_NAME3
			psIns.setString(++index, Utils.isNull(b.getCondName4()));//12 COND_NAME4
			
			psIns.setString(++index, Utils.isNull(b.getCondCode1()));//13 COND_CODE1
			psIns.setString(++index, Utils.isNull(b.getCondCode2()));//14 COND_CODE2
			psIns.setString(++index, Utils.isNull(b.getCondCode3()));//15 COND_CODE3
			psIns.setString(++index, Utils.isNull(b.getCondCode4()));//16 COND_CODE4
			
			psIns.setString(++index, Utils.isNull(b.getCondValue1()));//17 COND_VALUE1
			psIns.setString(++index, Utils.isNull(b.getCondValue2()));//18 COND_VALUE2
			psIns.setString(++index, Utils.isNull(b.getCondValue3()));//19 COND_VALUE3
			psIns.setString(++index, Utils.isNull(b.getCondValue4()));//20 COND_VALUE4
			
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp1()));//21 COND_VALUE_DISP1
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp2()));//22 COND_VALUE_DISP2
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp3()));//23 COND_VALUE_DISP3
			psIns.setString(++index, Utils.isNull(b.getCondValueDisp4()));//24 COND_VALUE_DISP4
			
			psIns.setString(++index, Utils.isNull(b.getColNameDisp1()));//25 COL_NAME1
			psIns.setString(++index, Utils.isNull(b.getColNameDisp2()));//26 COL_NAME2
			psIns.setString(++index, Utils.isNull(b.getColNameDisp3()));//27 COL_NAME3
			psIns.setString(++index, Utils.isNull(b.getColNameDisp4()));//28 COL_NAME4
			
			psIns.setString(++index, Utils.isNull(b.getColNameUnit1()));//29 COL_UNIT1
			psIns.setString(++index, Utils.isNull(b.getColNameUnit2()));//30 COL_UNIT2
			psIns.setString(++index, Utils.isNull(b.getColNameUnit3()));//31 COL_UNIT3
			psIns.setString(++index, Utils.isNull(b.getColNameUnit4()));//32 COL_UNIT4
			
			psIns.setString(++index, Utils.isNull(b.getCompareDisp1()));//33 COMPARE_DISP1
			psIns.setString(++index, Utils.isNull(b.getCompareDisp2()));//34 COMPARE_DISP2
			psIns.setString(++index, Utils.isNull(b.getSummaryType()));//35 SummaryType
			psIns.setTimestamp(++index, new java.sql.Timestamp(System.currentTimeMillis()));//36 UPDATED
			psIns.setString(++index, u.getId()+"");//37 UPDATED_BY
			psIns.setString(++index, Utils.isNull(b.getGroupBy()));//38 Group BY
			
			psIns.setString(++index, u.getId()+"");//1 USER_ID
			psIns.setString(++index, b.getProfileId());//2 PROFILE_ID
			
			int c = psIns.executeUpdate();
			
			if(c > 0){
				update = true;
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, psIns, null);
		}
		return update;
	}

}
