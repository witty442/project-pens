package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.LockItemOrderBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.web.lockitem.LockItemOrderErrorBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcessAll;

public class LockItemOrderDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 
		public static Map<String ,String> getStoreCodeCanOrderGroupCode(Connection conn) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Map<String,String> dataMap = new HashMap<String,String>();
			String key = "";
			String value = "";
			String oldValue = "";
			try {
				sql.append("\n select group_code, group_store ,store_no from PENSBME_LOCK_ITEM where 1=1");
				sql.append("\n and ( unlock_date is null ");
				sql.append("\n       AND sysdate >= lock_date ) ");
				sql.append("\n OR  ( lock_date is not null  ");
				sql.append("\n       AND sysdate >= lock_date  ");
				sql.append("\n       AND sysdate <= unlock_date  ) ");
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				    key = Utils.isNull(rst.getString("group_code"))+"_"+Utils.isNull(rst.getString("group_store"));
				    value = Utils.isNull(rst.getString("store_no"));
				    if(value.equalsIgnoreCase("ALL")){
				    	value = Utils.isNull(rst.getString("group_store"))+"|ALL";
				    }
				    
					if(dataMap.get(key) == null){
						dataMap.put(key, value);
					}else{
						oldValue = dataMap.get(key);
						dataMap.put(key, oldValue+","+value);
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
		return dataMap;
	 }
		

	   public static LockItemOrderBean searchHead(Connection conn,LockItemOrderBean o) throws Exception {
		  return searchHeadModel(conn,o);
		}
	   
	   public static LockItemOrderBean searchHead(LockItemOrderBean o) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchHeadModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static LockItemOrderBean searchHeadModel(Connection conn,LockItemOrderBean o) throws Exception {
				PreparedStatement ps = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				LockItemOrderBean h = null;
				List<LockItemOrderBean> items = new ArrayList<LockItemOrderBean>();
				int r = 1;
				String lockDateStr  ="";
				try {
					if( !Utils.isNull(o.getLockDate()).equals("") ){
					   Date lockDate = DateUtil.parse(Utils.isNull(o.getLockDate()), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					   lockDateStr = DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					}
				   sql.append("\n select E.* " );
				   sql.append("\n ,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Customer' AND M.pens_value =E.group_store)as group_store_name" );
				   sql.append("\n ,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Store' AND M.pens_value =E.store_no)as store_name" );
				   sql.append("\n  from PENSBME_LOCK_ITEM E ");
				   sql.append("\n  WHERE 1=1");
				   if( !Utils.isNull(o.getGroupCode()).equals("") ){
						sql.append("\n and E.group_code ='"+Utils.isNull(o.getGroupCode())+"'");
				   }
				   if( !Utils.isNull(o.getGroupStore()).equals("") ){
						sql.append("\n and E.group_store ='"+Utils.isNull(o.getGroupStore())+"'");
				   }
				   if( !Utils.isNull(o.getStoreCode()).equals("") ){
						sql.append("\n and E.store_no in("+Utils.converToTextSqlIn(o.getStoreCode())+")");
				   }
				   if( !Utils.isNull(o.getLockDate()).equals("") ){
				      sql.append("\n  and lock_date = to_date('"+lockDateStr+"','dd/mm/yyyy') ");
				   }
				   sql.append("\n order by E.group_code,E.group_store,E.lock_date asc ");
				   logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					rst = ps.executeQuery();
					
					while(rst.next()) {
					   h = new LockItemOrderBean();
					   h.setId(rst.getBigDecimal("id"));
					   h.setGroupCode(Utils.isNull(rst.getString("group_code")));
					   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
					   h.setGroupStoreName(Utils.isNull(rst.getString("group_store_name")));
					   h.setStoreCode(Utils.isNull(rst.getString("store_no")));
					   if( !"ALL".equalsIgnoreCase(h.getStoreCode())){
					      h.setStoreName(Utils.isNull(rst.getString("store_name")));
					   }else{
						  h.setStoreName("ทุกร้านค้า");
					   }
					   h.setLockDate(Utils.isNull(DateUtil.stringValue(rst.getDate("lock_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)));
					   h.setUnlockDate(Utils.isNull(DateUtil.stringValue(rst.getDate("unlock_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)));
					   
					   if( "ALL".equalsIgnoreCase(h.getStoreCode())){
						 h.setAllStore("true");  
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
		
		public static List<LockItemOrderBean> getStoreExistList(Connection conn,LockItemOrderBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			LockItemOrderBean h = null;
			List<LockItemOrderBean> items = new ArrayList<LockItemOrderBean>();
			int r = 1;
			try {
				sql.append("\n select pens_value,pens_desc ");
				sql.append("\n ,(select E.store_no from PENSBME_LOCK_ITEM E ");
				sql.append("\n   WHERE E.group_code ='"+Utils.isNull(o.getGroupCode())+"'");
				sql.append("\n   and E.group_store ='"+Utils.isNull(o.getGroupStore())+"'");
				sql.append("\n   and E.store_no = M.pens_value ) as store_no");
				sql.append("\n FROM ");
				sql.append("\n PENSBME_MST_REFERENCE M WHERE 1=1 ");
				sql.append("\n and reference_code ='Store' ");
				sql.append("\n and pens_value like '"+o.getGroupStore()+"%' ");
				sql.append("\n order by M.pens_value asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new LockItemOrderBean();
				   h.setStoreCode(Utils.isNull(rst.getString("pens_value")));
				   h.setStoreName(Utils.isNull(rst.getString("pens_desc")));
				   if( !Utils.isNull(rst.getString("store_no")).equals("")){
					   h.setChkFlag("checked");
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
		 
		public static Map<String ,String> setStoreCodeToMap(Connection conn,LockItemOrderBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Map<String,String> dataMap = new HashMap<String,String>();
			String key = "";
			String oldValue = "";
			try {
				Date lockDate = DateUtil.parse(Utils.isNull(o.getLockDate()), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String lockDateStr = DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select group_code, group_store ,store_no from PENSBME_LOCK_ITEM where 1=1");
				sql.append("\n and group_code = '"+o.getGroupCode()+"' \n");
				sql.append("\n and lock_date = to_date('"+lockDateStr+"','dd/mm/yyyy') \n");

				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				    key = Utils.isNull(rst.getString("group_code"))+"_"+Utils.isNull(rst.getString("group_store"));
					if(dataMap.get(key) == null){
						dataMap.put(key, Utils.isNull(rst.getString("store_no")));
					}else{
						oldValue = dataMap.get(key);
						dataMap.put(key, oldValue+","+Utils.isNull(rst.getString("store_no")));
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
		return dataMap;
	}
	 
   public static Map<String ,String> setDataBeforeSaveStoreCodeToMap(LockItemOrderBean bean, List<Master> custGroupList,HttpServletRequest request) throws Exception {
			int i=0;
			Map<String,String> dataMap = new HashMap<String,String>();
			String key = "";
			String chkStore = "";
			Master m = null;
			String storeNo = "";
			try {
				 for(i=0;i<custGroupList.size();i++){
					 m = custGroupList.get(i);
					 chkStore = request.getParameter("chkStore_"+m.getPensValue()); 
					 if("allStore".equalsIgnoreCase(chkStore)){
						 storeNo ="ALL";
					 }else{
						 storeNo =Utils.isNull(request.getParameter("store_"+m.getPensValue()));
					 }
					 
				     key = Utils.isNull(bean.getGroupCode())+"_"+Utils.isNull(m.getPensValue());
					 dataMap.put(key, storeNo);
					
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					
				} catch (Exception e) {}
			}
		return dataMap;
	}
		
	 public static LockItemOrderBean insertModel(Connection conn,LockItemOrderBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO PENSBME_LOCK_ITEM \n");
				sql.append("(ID,Group_code,Lock_date, Unlock_date, Group_store,Store_no,CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES (?,?,?, ?, ?, ?, ?, ?) \n");//7
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setInt(c++, SequenceProcessAll.getNextValue(conn,"PENSBME_LOCK_ITEM"));
				
				ps.setString(c++, Utils.isNull(o.getGroupCode()));//1
				if( !Utils.isNull(o.getLockDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				if( !Utils.isNull(o.getUnlockDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getUnlockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				ps.setString(c++, Utils.isNull(o.getGroupStore()));//1
				ps.setString(c++, Utils.isNull(o.getStoreCode())); //2
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//13
				ps.setString(c++, o.getCreateUser());//15
				
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
	 
	 public static int updateModel(Connection conn,LockItemOrderBean o) throws Exception{
			PreparedStatement ps = null;
			Date lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			logger.debug("updateHeadModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("UPDATE PENSBME_LOCK_ITEM \n");
				sql.append("SET unlock_date =?,UPDATE_DATE=?, UPDATE_USER=? \n");
				sql.append("WHERE group_code=? AND group_store =? AND store_no=?  " +
						"AND lock_date = to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");//7
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				

				if( !Utils.isNull(o.getUnlockDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((DateUtil.parse(o.getUnlockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//14
				ps.setString(c++, o.getCreateUser());//15
				
				ps.setString(c++, Utils.isNull(o.getGroupCode()));//1
				ps.setString(c++, Utils.isNull(o.getGroupStore())); //2
				ps.setString(c++, Utils.isNull(o.getStoreCode()));//3
			
				return  ps.executeUpdate();

			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static boolean isDataAllStore(Connection conn,LockItemOrderBean o) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs= null;
			Date lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("select count(*) as c FROM PENSBME_LOCK_ITEM \n");
				sql.append("WHERE group_code=? AND group_store =?  AND store_no ='ALL' " +
						"AND lock_date = to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");//7
				
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(o.getGroupCode()));//1
				ps.setString(c++, Utils.isNull(o.getGroupStore())); //2
				rs = ps.executeQuery();
				
				if(rs.next()){
					if(rs.getInt("c") >0){
						return true;
					}
				}
				return false;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	  }
	 
	 public static int deleteLockItem(Connection conn,String groupCode,String groupStore,String storeCode,String lockDateStr) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs= null;
			Date lockDate = DateUtil.parse(lockDateStr, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("delete FROM PENSBME_LOCK_ITEM \n");
				sql.append("WHERE group_code= '"+Utils.isNull(groupCode)+"' \n"
						+ "AND group_store = '"+Utils.isNull(groupStore)+"' \n"
						+ "AND store_no = '"+Utils.isNull(storeCode)+"' \n"
						+ "AND lock_date = to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");
				
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
	            return ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	  }
	 
	 public static String getStoreCodeArrExist(Connection conn,LockItemOrderBean o) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs= null;
			Date lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			String storeCodeArr = "";
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" select store_no FROM PENSBME_LOCK_ITEM \n");
				sql.append(" WHERE group_code='"+Utils.isNull(o.getGroupCode())+"' AND group_store ='"+Utils.isNull(o.getGroupStore())+"' \n");
				//sql.append(" AND lock_date = to_date('"+Utils.stringValue(lockDate, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");//7
				
				//logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				
				while(rs.next()){
					storeCodeArr += Utils.isNull(rs.getString("store_no"))+",";
				}
				if(storeCodeArr.length() >0){
					storeCodeArr = storeCodeArr.substring(0,storeCodeArr.length()-1);
				}
				return storeCodeArr;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	  }
	 
	 public static int deleteModel(Connection conn,LockItemOrderBean o) throws Exception{
			PreparedStatement ps = null;
			Date lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			logger.debug("deleteModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("DELETE FROM PENSBME_LOCK_ITEM \n");
				sql.append("WHERE group_code='"+Utils.isNull(o.getGroupCode())+"' AND group_store ='"+Utils.isNull(o.getGroupStore())+"' " +
						"AND lock_date = to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");//7
				
				//logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				return  ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	  }
	 
	 public static String[] validateStore(Connection conn,LockItemOrderBean o){
			PreparedStatement ps = null;
			ResultSet rs= null;
			boolean error = false;
			Date lockDate = null;
			Date lockDateDB = null;
			Date unlockDate = null;
			Date unlockDateDB = null;
			String[] errors = new String[2];
			String storeCodeError = "";
			try{
				lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				if( !Utils.isNull(o.getUnlockDate()).equals("")){
					unlockDate = DateUtil.parse(o.getUnlockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				//Step 1 check group_store is set to AllStore
				StringBuffer sql = new StringBuffer("");
				sql.append(" select distinct group_code ,store_no,lock_date ,unlock_date FROM PENSBME_LOCK_ITEM \n");
				sql.append(" WHERE group_code='"+Utils.isNull(o.getGroupCode())+"' \n" );
				sql.append(" AND group_store ='"+Utils.isNull(o.getGroupStore())+"'\n");
				if("edit".equals(o.getMode())){
					sql.append(" and lock_date <> to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')  \n ");
				}
				sql.append(" order by unlock_date asc \n");
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				while(rs.next()){
					lockDateDB = rs.getDate("lock_date");
					unlockDateDB = rs.getDate("unlock_date");
				    storeCodeError = rs.getString("store_no");
				
					if(unlockDateDB != null && unlockDate!=null){
						logger.debug("1.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"]!=null"); 
						if(isWithinRange(lockDateDB,unlockDateDB,lockDate)){ //lockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.1 lockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
						if(isWithinRange(lockDateDB,unlockDateDB,unlockDate)){ //unlockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.2 unlockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
					}else if(unlockDateDB != null && unlockDate == null){
						logger.debug("2.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"] ==null"); 
						if(lockDate.before(lockDateDB) || lockDate.before(unlockDateDB)){
							logger.debug("2.1 lockDate["+lockDate+"] < lockDateDB["+lockDateDB+"] or lockDate["+lockDate+"] < unlockDateDB["+unlockDateDB+"]"); 
							error = true;
						}
					}else if(unlockDateDB == null && unlockDate == null){
						logger.debug("3.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] ==null"); 
						error = true;
						
					}else if(unlockDateDB == null && unlockDate != null){	
						logger.debug("4.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] !=null"); 
						if(unlockDate.after(lockDateDB)){
							logger.debug("4.1 unlockDate["+unlockDate+"] > lockDateDB["+lockDateDB+"]"); 
						    error = true;
						}
					}
			  }//while
				errors[0] = error+"";
				errors[1] = storeCodeError;
			
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}finally{
				try{
					if(ps != null){
						ps.close();ps=null;
					}
				}catch(Exception e){}
			}
			return errors;
	  }
	 
	 public static LockItemOrderErrorBean validateAllStore(Connection conn,LockItemOrderBean o,String storeNo){
			PreparedStatement ps = null;
			ResultSet rs= null;
			boolean error = false;
			Date lockDate = null;
			Date lockDateDB = null;
			Date unlockDate = null;
			Date unlockDateDB = null;
			LockItemOrderErrorBean errorBean = new LockItemOrderErrorBean();
			String storeCodeError = "";
			try{
				lockDate = DateUtil.parse(o.getLockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				if( !Utils.isNull(o.getUnlockDate()).equals("")){
					unlockDate = DateUtil.parse(o.getUnlockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				//Step 1 check group_store is set to AllStore
				StringBuffer sql = new StringBuffer("");
				sql.append(" select distinct group_code ,store_no,lock_date ,unlock_date FROM PENSBME_LOCK_ITEM \n");
				sql.append(" WHERE group_code='"+Utils.isNull(o.getGroupCode())+"' \n" );
				sql.append(" AND group_store ='"+Utils.isNull(o.getGroupStore())+"'\n");
				if( !Utils.isNull(storeNo).equals(""))
					sql.append(" AND store_no ='"+storeNo+"' \n");
				
				// Case Edit No Check Owner record
				if("edit".equals(o.getMode())){
					sql.append(" and lock_date <> to_date('"+DateUtil.stringValue(lockDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')  \n ");
				}
				sql.append(" order by unlock_date asc \n");
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				while(rs.next()){
					lockDateDB = rs.getDate("lock_date");
					unlockDateDB = rs.getDate("unlock_date");
				    storeCodeError = rs.getString("store_no");
	
					if(unlockDateDB != null && unlockDate!=null){
						logger.debug("1.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"]!=null"); 
						if(isWithinRange(lockDateDB,unlockDateDB,lockDate)){ //lockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.1 lockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
						if(isWithinRange(lockDateDB,unlockDateDB,unlockDate)){ //unlockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.2 unlockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
					}else if(unlockDateDB != null && unlockDate == null){
						logger.debug("2.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"] ==null"); 
						if(lockDate.before(lockDateDB) || lockDate.before(unlockDateDB)){
							logger.debug("2.1 lockDate["+lockDate+"] < lockDateDB["+lockDateDB+"] or lockDate["+lockDate+"] < unlockDateDB["+unlockDateDB+"]"); 
							error = true;
							break;
						}
					}else if(unlockDateDB == null && unlockDate == null){
						logger.debug("3.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] ==null"); 
						error = true;
						break;
						
					}else if(unlockDateDB == null && unlockDate != null){	
						logger.debug("4.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] !=null"); 
						if(unlockDate.after(lockDateDB)){
							logger.debug("4.1 unlockDate["+unlockDate+"] > lockDateDB["+lockDateDB+"]"); 
						    error = true;
						    break;
						}
					}
				    
			  }//while
				
				if(error){
					errorBean.setErrorCode("ERROR_1");
					String lockDateDBStr = DateUtil.stringValue(lockDateDB, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String unlokcDateDBStr = "*";
					if(unlockDateDB !=null){
						unlokcDateDBStr = DateUtil.stringValue(unlockDateDB, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
					
					if("ALL".equalsIgnoreCase(storeCodeError)){
						errorBean.setErrorMsg("<br>.ไม่สามารถ บันทึกข้อมูลได้  เนื่องจาก ช่วงเวลา ("+lockDateDBStr+" - "+unlokcDateDBStr+") ของกลุ่มร้านค้า ("+o.getGroupStore()+") มีการบันทึกข้อมูลเป็น ทั้งกลุ่มร้านค้าไปแล้ว ");
					}else{
						errorBean.setErrorMsg("<br>.ไม่สามารถ บันทึกข้อมูลได้   เนื่องจาก ช่วงเวลา ("+lockDateDBStr+" - "+unlokcDateDBStr+") ของกลุ่มร้านค้า ("+o.getGroupStore()+") มีร้านค้า ("+storeCodeError+") บันทึกข้อมูลไปแล้ว ");
					}
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}finally{
				try{
					if(ps != null){
						ps.close();ps=null;
					}
					if(rs != null){
						rs.close();rs = null;
					}
				}catch(Exception e){}
			}
			return errorBean;
	  }
	 
	 private static boolean isWithinRange(Date startDate,Date endDate ,Date checkDate) {
		   return !(checkDate.before(startDate) || checkDate.after(endDate));
	 }
	 
	/* public static LockItemOrderErrorBean validateStoreNoIsExist(Connection conn,LockItemOrderBean o){
			PreparedStatement ps = null;
			ResultSet rs= null;
			boolean error = false;
			LockItemOrderErrorBean errorBean = new LockItemOrderErrorBean();
			Date lockDate = null;
			Date lockDateDB = null;
			Date unlockDate = null;
			Date unlockDateDB = null;
			try{
				lockDate = Utils.parse(o.getLockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				if( !Utils.isNull(o.getUnlockDate()).equals("")){
					unlockDate = Utils.parse(o.getUnlockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				//Step 1 check group_store is set to AllStore
				StringBuffer sql = new StringBuffer("");
				sql.append(" select group_code ,store_no,lock_date,unlock_date \n");
				sql.append(" FROM PENSBME_LOCK_ITEM \n");
				sql.append(" WHERE group_code='"+Utils.isNull(o.getGroupCode())+"' \n");
				sql.append(" AND group_store ='"+Utils.isNull(o.getGroupStore())+"' \n");
				sql.append(" AND store_no ='"+Utils.isNull(o.getStoreCode())+"' \n");
				if("edit".equals(o.getMode())){
					sql.append("and lock_date <> to_date('"+Utils.stringValue(lockDate, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') ");
				}
				sql.append(" order by unlock_date asc \n");
				
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				while(rs.next()){
					lockDateDB = rs.getDate("lock_date");
					unlockDateDB = rs.getDate("unlock_date");
					
					if(unlockDateDB != null && unlockDate!=null){
						logger.debug("1.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"]!=null"); 
						if(isWithinRange(lockDateDB,unlockDateDB,lockDate)){ //lockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.1 lockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
						if(isWithinRange(lockDateDB,unlockDateDB,unlockDate)){ //unlockDate in range ( lockDateDB ,unlockDateDB)
							logger.debug("1.2 unlockDate["+lockDate+"] in range ( lockDateDB["+lockDateDB+"] ,unlockDateDB["+unlockDateDB+"])");
							error = true;
							break;
						}
					}else if(unlockDateDB != null && unlockDate == null){
						logger.debug("2.unlockDateDB["+unlockDateDB+"] != null && unlockDate["+unlockDate+"] ==null"); 
						if(lockDate.before(lockDateDB) || lockDate.before(unlockDateDB)){
							logger.debug("2.1 lockDate["+lockDate+"] < lockDateDB["+lockDateDB+"] or lockDate["+lockDate+"] < unlockDateDB["+unlockDateDB+"]"); 
							error = true;
							break;
						}
					}else if(unlockDateDB == null && unlockDate == null){
						logger.debug("3.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] ==null"); 
						error = true;
						break;
					}else if(unlockDateDB == null && unlockDate != null){	
						logger.debug("4.unlockDateDB["+unlockDateDB+"] == null && unlockDate["+unlockDate+"] !=null"); 
						if(unlockDate.after(lockDateDB)){
							logger.debug("4.1 unlockDate["+unlockDate+"] > lockDateDB["+lockDateDB+"]"); 
						    error = true;
						    break;
						}
					}
				}//while 
				if(error){
					errorBean.setErrorCode("ERROR_1");
					String lockDateDBStr = Utils.stringValue(lockDateDB, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String unlokcDateDBStr = "*";
					if(unlockDateDB !=null){
						unlokcDateDBStr = Utils.stringValue(unlockDateDB, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
					
					errorBean.setErrorMsg("<br>.ไม่สามารถ บันทึกข้อมูลได้   เนื่องจาก ช่วงเวลา ("+lockDateDBStr+" - "+unlokcDateDBStr+") ของกลุ่มร้านค้า ("+o.getGroupStore()+") ร้านค้า ["+o.getStoreCode()+"] มีการบันทึกข้อมูลเป็น เฉพาะบางร้านค้าไปแล้ว ");
					
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}finally{
				try{
					if(ps != null){
						ps.close();ps=null;
					}
				}catch(Exception e){
					
				}
			}
			return errorBean;
	  }*/
	
}
