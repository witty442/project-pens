package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.ImageFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ExportManager;

public class ExportProcess {

	public static Logger logger = Logger.getLogger("PENS");

	/**
	 * readTableDataDB
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 * Case : Normal
	 */
	public   TableBean exportDataDB(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}
					
				}//for
				dataAppend.append(Constants.newLine);//new line
			}//while
			
			//set TotalRows
			if( !Utils.isNull(dataAppend.toString()).equals("")){
			   tableBean.setExportCount(dataAppend.toString().split("\n").length);
			}
			
			/** Set Text Data To Export By Table ****/
			tableBean.setDataStrExport(dataAppend);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 * 
	 * 1 table -> many file Export 
	 */
	public   TableBean exportMTrip(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
        int totalRows = 0;
        List fileExportList = new ArrayList();
		try{
            logger.debug("SQL Count File:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				logger.debug("USER_ID :"+rs.getString("USER_ID"));
				TableBean fileExportBean = exportMTripBySalesCode(conn, tableBean, userBean, rs.getString("USER_ID"));
				fileExportList.add(fileExportBean);
			}//while
			
			tableBean.setFileExportList(fileExportList);
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportMTripBySalesCode(Connection conn,TableBean tableBean,User userBean,String userId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        int i = 0;
        TableBean fileExportBean = new TableBean();
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{	
			/** Clone Property to Use **/
			fileExportBean.setTableName(tableBean.getTableName());
			fileExportBean.setSource(tableBean.getSource());
			fileExportBean.setDestination(tableBean.getDestination());
			fileExportBean.setTransactionType(tableBean.getTransactionType());
			fileExportBean.setExportPath(tableBean.getExportPath());
			fileExportBean.setFileFtpNameFull(ExportHelper.genFileNameMTrip(tableBean, userBean));
			
	         String sql ="	select 	\n"+
					"	m.TRIP_ID,	\n"+
					"	m.YEAR,	\n"+
					"	m.MONTH,	\n"+
					"	m.DAY,	\n"+
					"	(select max(s.code) from m_customer s where s.customer_id = m.customer_id ) as CUSTOMER_ID,	\n"+ 
					"	m.USER_ID	\n"+
					"	from m_trip	m \n"+
					"  where ( m.EXPORTED  = 'N' OR m.EXPORTED  IS NULL) \n"+
					"  and m.user_id ="+userId;
	         
            logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}
					
				}//for
				dataAppend.append(Constants.newLine);//new line
				
				/** Set Data For Update Exported Flag **/
				if("m_trip".equalsIgnoreCase(tableBean.getTableName())){
					sqlUpdateExportFlagList.add("update m_trip set exported = 'Y' WHERE trip_id="+rs.getString("trip_id"));
				}
				
			}//while
			
			//set TotalRows
			if( !Utils.isNull(dataAppend.toString()).equals("")){
				fileExportBean.setExportCount(dataAppend.toString().split("\n").length);
			}
			
			fileExportBean.setDataStrExport(dataAppend);
			fileExportBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			
			return fileExportBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * readTableCustomerDataDB
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportCustomer(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
        List<ImageFileBean> imageFileList = new ArrayList<ImageFileBean>();
        int totalRows = 0;
        InterfaceDAO dao = new InterfaceDAO();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			
			rs = ps.executeQuery();
			while(rs.next()){ 
				totalRows++;
				String ADDRESS_ID_BILL_TO = dao.isSameAddress(conn, rs.getString("customer_id"));
			
				//Get Customer ,BILL_TO AND SHIP_TO
				dataAppend.append(exportCustomerSplit2LineDataDB(conn, tableBean, userBean,rs.getString("customer_id"),ADDRESS_ID_BILL_TO));
				//add customer profile
				dataAppend.append(exportCustProfileDataDB(conn,rs.getString("customer_id"),userBean,ADDRESS_ID_BILL_TO));
				//add customer contact
				dataAppend.append(exportCustContactDataDB(conn,rs.getString("customer_id"),userBean,ADDRESS_ID_BILL_TO));

				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update m_customer set exported = 'Y' WHERE customer_id="+rs.getString("customer_id"));
		
			}//while

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			tableBean.setImageFileList(imageFileList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	public  TableBean exportCustomerLocation(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
        List<ImageFileBean> imageFileList = new ArrayList<ImageFileBean>();
        int totalRows = 0;
        InterfaceDAO dao = new InterfaceDAO();
        int c = 0;
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			
			rs = ps.executeQuery();
			while(rs.next()){ 
				
				/** Case isChange=Y check location is not null */
				if( !Utils.isNull(rs.getString("location")).equals("")){
				   dataAppend.append(exportCustomerLocationLineDataDB(conn, tableBean, userBean,rs.getString("customer_id")));
				   c =1;
				}
				/** Add imageFileName to Upload **/
				if( !Utils.isNull(rs.getString("image_file_name")).equals("")){
					ImageFileBean im = new ImageFileBean();
					im.setImageFileName(rs.getString("image_file_name"));
					String fileName = im.getImageFileName().substring(im.getImageFileName().lastIndexOf("/")+1,im.getImageFileName().length());
					im.setGenerateImageFileName(fileName);
					
					imageFileList.add(im);
					c =1;
				}
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update m_customer set is_change = 'N' WHERE customer_id="+rs.getString("customer_id"));
		
				totalRows += c;
			}//while
			
			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			tableBean.setImageFileList(imageFileList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	private  String exportCustomerSplit2LineDataDB(Connection conn,TableBean tableBean,User userBean,String customerId,String ADDRESS_ID_BILL_TO) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		StringBuffer dataAppendLine2 = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
			logger.debug("*** readTableCustomerSplit2LineDataDB ****");	
			String subSql = "";
			
			String ORIG_SYSTEM_ADDRESS_REF = " concat(concat(M.CODE , '-'), A.ADDRESS_ID) ";
			if( !Utils.isNull(ADDRESS_ID_BILL_TO).equals("")){
				ORIG_SYSTEM_ADDRESS_REF ="concat(concat(M.CODE , '-'),'"+ADDRESS_ID_BILL_TO+"' )";
			}
			
			subSql = "SELECT M.CUSTOMER_ID,A.ADDRESS_ID, \n"+
			"	'1' AS RECORD_TYPE, 	\n"+
			"  M.CODE AS ORIG_SYSTEM_CUSTOMER_REF ,\n "+
			" "+ORIG_SYSTEM_ADDRESS_REF+" AS ORIG_SYSTEM_ADDRESS_REF, \n"+ //OLD CODE
			"	M.CODE AS CUSTOMER_NUMBER , 	\n"+
			"	CASE WHEN 'DD' ='"+userBean.getType()+"' THEN CONCAT(M.NAME,CONCAT(' ' ,M.NAME2)) ELSE M.NAME END AS CUSTOMER_NAME, 	\n"+
			"	CASE WHEN 'DD' ='"+userBean.getType()+"' THEN '' ELSE M.NAME2 END AS CUSTOMER_NAME_PHONETIC , 	\n"+
			"	CASE WHEN A.PURPOSE ='B' THEN 'BILL_TO'  \n" +
			"        WHEN A.PURPOSE ='S' THEN 'SHIP_TO' \n"+
			"        ELSE ''  \n"+
			"   END AS SITE_USE_CODE, 	\n"+
			"   CASE WHEN A.PURPOSE = 'B' THEN concat(concat(M.CODE , '-'), A.ADDRESS_ID) ELSE NULL END  AS BILL_TO_ORIG_ADDRESS_REF, 	\n"+ 
			"	CASE WHEN M.ISACTIVE ='Y' THEN 'A' ELSE 'I' END AS CUSTOMER_STATUS , 	\n"+
			"	'Y' AS PRIMARY_SITE_USE_FLAG, 	\n"+
			"	A.LINE1 AS ADDRESS1, 	\n"+
			"	A.LINE2 AS ADDRESS2, 	\n"+
			"	A.LINE3 AS ADDRESS3, 	\n"+
			"	A.LINE4 AS ADDRESS4, 	\n"+
			"	P.NAME AS PROVINCE, 	\n"+
			"	A.POSTAL_CODE, 	\n"+
			"	A.COUNTRY, 	\n"+
			"	M.VAT_CODE AS CUST_TAX_CODE, 	\n"+
			"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID, 	\n"+
			"	M.CUSTOMER_TYPE AS CATEGORY_CODE, 	\n"+
			"	M.TAX_NO AS  TAX_PAYER_ID, 	\n"+
			"	M.PERSON_ID_NO, 	\n"+
			"	M.WEBSITE AS URL, 	\n"+
			"	M.TERRITORY, 	\n"+
			"	M.BUSINESS_TYPE AS CUSTOMER_CLASS_CODE, 	\n"+
			"	M.PARENT_CUSTOMER_ID, 	\n"+
			"	M.BIRTHDAY AS BIRTH_DATE, 	\n"+
			"	M.PAYMENT_METHOD, 	\n"+
			"	M.SHIPPING_METHOD AS SALES_CHANNEL, 	\n"+
			"	M.SHIPPING_ROUTE AS SHIP_ROUTE, 	\n"+
			"	M.TRANSIT_NAME, 	\n"+
			"	M.EMAIL AS EMAIL_ADDRESS, 	\n"+
			"	M.REGISTER_DATE AS ESTABLISH_DATE, 	\n"+
			"	M.ORDER_DATE, 	\n"+
			"	M.SHIPPING_DATE, 	\n"+
			"	M.USER_ID AS SALESREP_ID,	\n"+
			"	'"+tableBean.getFileFtpNameFull()+"' as FILE_NAME, 	\n"+
			"   A.address_ID as ADDRESS_ID, \n"+
			"   'O' as PARTY_TYPE, \n"+ //Default Value All By Oracle No Edit
			"   M.location, \n"+
			//"   SUBSTRING(M.location,1 , LOCATE(',', M.location)-1) as latitude, \n"+
			//"   SUBSTRING(M.location, LOCATE(',', M.location)+1,LENGTH(M.location)) as longtitude \n"+
			"   M.trip_day, \n"+
			"   M.trip_day2, \n"+
			"   M.trip_day3 \n"+
			"	FROM m_customer M 	\n"+
			"	INNER JOIN m_address A 	\n"+
			"	ON M.CUSTOMER_ID = A.CUSTOMER_ID 	\n"+
			"	LEFT OUTER JOIN m_province P 	\n"+
			"	ON A.PROVINCE_ID = P.PROVINCE_ID	\n"+
			"   WHERE M.user_id = "+userBean.getId()+
			"   AND M.CUSTOMER_ID ="+customerId +
			"   AND A.PURPOSE IS NOT NULL \n"+
			"   ORDER BY A.ADDRESS_ID \n";
			
            logger.debug("Select:"+subSql);
			ps= conn.prepareStatement(subSql);
			
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					logger.debug("colName:"+colBean.getColumnName());
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					
					//Line 1
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else if(totalRows ==1 && colBean.getColumnName().equalsIgnoreCase("PRIMARY_SITE_USE_FLAG")){
						dataAppend.append("Y").append(lastAppen);
					}else if(totalRows !=1 && colBean.getColumnName().equalsIgnoreCase("PRIMARY_SITE_USE_FLAG")){
						dataAppend.append("").append(lastAppen);
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					
					//line 2 for found 1 Address add 
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppendLine2.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else if(totalRows ==1 && colBean.getColumnName().equalsIgnoreCase("PRIMARY_SITE_USE_FLAG")){
						dataAppendLine2.append("").append(lastAppen);
					}else{
						dataAppendLine2.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				dataAppend.append(Constants.newLine);//new line
				dataAppendLine2.append(Constants.newLine);//new line
			}//while
			
			/** WAIT case found 1 Address 1 customer Add one Line to (billTO or ShipTo)*/
			/*if(totalRows ==1){
				dataAppend.append(dataAppendLine2);
			}*/
			return dataAppend.toString();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	private  String exportCustomerLocationLineDataDB(Connection conn,TableBean tableBean,User userBean,String customerId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
			logger.debug("*** exportCustomerLocationLineDataDB ****");	
			String subSql = "";
			
			subSql = "SELECT M.CUSTOMER_ID , \n"+
			"	'H'  AS RECORD_TYPE,	\n"+
			"	M.CODE AS CUSTOMER_NUMBER ,  \n"+
			"	M.NAME  AS CUSTOMER_NAME,	\n"+
			"	'"+tableBean.getFileFtpNameFull()+"' as FILE_NAME, 	\n"+
			"   M.location, \n"+
			//OLD CODE
			//"   SUBSTRING(M.location,1 , LOCATE(',', M.location)-1) as latitude, \n"+
			//"   SUBSTRING(M.location, LOCATE(',', M.location)+1,LENGTH(M.location)) as longitude \n"+
			"   lat as latitude, \n"+
			"   lng as longitude \n"+
			"	FROM m_customer M 	\n"+
			"   WHERE M.user_id = "+userBean.getId()+
			"   AND M.CUSTOMER_ID ="+customerId +
			"   ORDER BY M.CUSTOMER_ID \n";
			
            logger.debug("Select:"+subSql);
			ps= conn.prepareStatement(subSql);
			
			rs = ps.executeQuery();
			while(rs.next()){
				//validate location format
				if(Utils.isLocationValid(rs.getString("location"))){
					totalRows++;
					for(i=0;i<tableBean.getColumnBeanList().size();i++){
						ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
						logger.debug("colName:"+colBean.getColumnName());
						if(i==tableBean.getColumnBeanList().size()-1){
							lastAppen = "";
						}else{
							lastAppen = Constants.delimeterPipeStr;
						}
						if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
							dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
						}else{
						   dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
						}
					}//for
					dataAppend.append(Constants.newLine);//new line
				}
			}//while
			
			return dataAppend.toString();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	  * updateInterfaceCustomer
	  * @param conn
	  * @param model
	  * @return
	  * @throws Exception
	  */
	 public int updateExportFlag(Connection conn,User userBean,TableBean tableBean) throws Exception {
			Statement st = null;
			String sql= "";
			int countUpdate =0;
			try {
				st = conn.createStatement();
				for (int i = 0;i<tableBean.getSqlUpdateExportFlagList().size() ; i++) {
				    sql = (String)tableBean.getSqlUpdateExportFlagList().get(i);
					logger.debug("updateSQL:"+sql);
					st.addBatch(sql);
			    }
				
			    countUpdate = st.executeBatch().length;
			    logger.debug("Result Update:"+countUpdate);
			    
			} catch (Exception ex) {
				throw ex;
			} finally {
				if(st != null){
					st.close();st = null;
				}
			}
			return countUpdate;
		}

	 /**
	  * updateInterfaceCustomer
	  * @param conn
	  * @param model
	  * @return
	  * @throws Exception
	  */
	 public int updateExportFlag(Connection conn,User userBean,List<String> sqlUpdateExportFlagList) throws Exception {
			Statement st = null;
			String sql= "";
			int countUpdate =0;
			try {
				if(sqlUpdateExportFlagList != null && sqlUpdateExportFlagList.size() >0 ){
					st = conn.createStatement();
					for (int i = 0;i<sqlUpdateExportFlagList.size() ; i++) {
					    sql = (String)sqlUpdateExportFlagList.get(i);
						logger.debug("updateSQL:"+sql);
						st.addBatch(sql);
				    }
					
				    countUpdate = st.executeBatch().length;
				    logger.debug("Result Update:"+countUpdate);
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				if(st != null){
					st.close();st = null;
				}
			}
			return countUpdate;
		}
	/**
	 * readTableCustProfileDataDB
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 * 3.1,m_cust_profile,CUSTPROFILE,SALES_TO_ORACLE,MASTER,DD|TT|VAN
       3.2,m_cust_contact,CUSTCONTACT,SALES_TO_ORACLE,MASTER,DD|TT|VAN
	 */
	private   StringBuffer exportCustProfileDataDB(Connection conn,String customerId,User userBean,String ADDRESS_ID_BILL_TO) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        String whereCond = "";
		try{
			LinkedHashMap contactMap = new LinkedHashMap();
			contactMap.put("profile1", "pofile1");
			contactMap.put("profile2", "profile2");
			
			TableBean tableProfileBean = new TableBean();
			tableProfileBean.setTableName("m_cust_profile");
			List colOrderList = ExportHelper.initColumn(tableProfileBean);
			
			String ORIG_SYSTEM_ADDRESS_REF = " concat(concat(M.CODE , '-'), A.ADDRESS_ID) ";

			if( !Utils.isNull(ADDRESS_ID_BILL_TO).equals("")){
				ORIG_SYSTEM_ADDRESS_REF ="concat(concat(M.CODE , '-'),'"+ADDRESS_ID_BILL_TO+"' )";
				whereCond = " AND A.ADDRESS_ID = "+ADDRESS_ID_BILL_TO+" \n";
			}

			String sql ="	SELECT 	\n"+
			"	'2' AS RECORD_TYPE,	\n"+
			"	M.CODE AS ORIG_SYSTEM_CUSTOMER_REF,	\n"+
			"	"+ORIG_SYSTEM_ADDRESS_REF+" AS ORIG_SYSTEM_ADDRESS_REF,	\n"+
			"  'DEFAULT' AS CUSTOMER_PROFILE_CLASS_NAME,	\n"+
			"	M.CREDIT_CHECK,	\n"+
			"	'N' AS CREDIT_HOLD,	\n"+
			"	M.PAYMENT_TERM AS STANDARD_TERM_NAME,         	\n"+
			"	'THB' AS CREDIT_CURRENCY,	\n"+
			"	M.CREDIT_LIMIT AS OVERALL_CREDIT_LIMIT,         	\n"+
			"	'' AS TRX_CREDIT_LIMIT,             	\n"+
			"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	\n"+
			"	FROM m_customer M 	\n"+
			"	INNER JOIN m_address A 	\n"+
			"	ON M.CUSTOMER_ID = A.CUSTOMER_ID 	\n"+
			"   WHERE M.customer_id = "+customerId+
			"   AND A.PURPOSE IS NOT NULL \n"+
			"  "+whereCond+
			"   ORDER BY A.ADDRESS_ID \n";
            logger.debug("Select:"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				Set s = contactMap.keySet();
				Iterator it = s.iterator();
				for (int k = 1; it.hasNext(); k++) {
					String profileType = (String) it.next();
					for(i=0;i<colOrderList.size();i++){
						ColumnBean colBean = (ColumnBean)colOrderList.get(i);
						
						if(i==colOrderList.size()-1){
							lastAppen = "";
						}else{
							lastAppen = Constants.delimeterPipeStr;
						}
	
						if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
							dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
						}else{
							if("profile1".equalsIgnoreCase(profileType)){
								dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen); 	
							}else if("profile2".equalsIgnoreCase(profileType)){
								if("ORIG_SYSTEM_ADDRESS_REF".equalsIgnoreCase(colBean.getColumnName())){
									dataAppend.append("").append(lastAppen); 	
								}else{
									dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
								}
							}else{
								dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
							}
						}	
					}//for 2
				  dataAppend.append(Constants.newLine);//new line
				//  totalRows++;
				}//for 1
			}//while
			
			logger.debug("totalRows:"+totalRows);
			
		
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * Special Case Export Contact
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportCustContactDataDB(Connection conn,String customerId,User userBean,String ADDRESS_ID_BILL_TO) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        boolean addNewLine = true;
        String whereCond = "";
		try{
			LinkedHashMap contactMap = new LinkedHashMap();
			contactMap.put("phone", "phone");
			contactMap.put("phone2", "phone2");
			contactMap.put("mobile", "mobile");
			contactMap.put("mobile2", "mobile2");
			contactMap.put("fax", "fax");
			
			TableBean tableProfileBean = new TableBean();
			tableProfileBean.setTableName("m_cust_contact");
			List colConatctList = ExportHelper.initColumn(tableProfileBean);
			
			String ORIG_SYSTEM_ADDRESS_REF = " concat(concat(M.CODE , '-'), A.ADDRESS_ID) ";
			if( !Utils.isNull(ADDRESS_ID_BILL_TO).equals("")){
				ORIG_SYSTEM_ADDRESS_REF ="concat(concat(M.CODE , '-'),'"+ADDRESS_ID_BILL_TO+"' )";
				whereCond = " AND A.ADDRESS_ID = "+ADDRESS_ID_BILL_TO+" \n";
			}

			
			String sql ="	SELECT 	\n"+
			"	'3' AS RECORD_TYPE,	\n"+
			"	M.CODE AS ORIG_SYSTEM_CUSTOMER_REF,	\n"+
			"	"+ORIG_SYSTEM_ADDRESS_REF+" AS ORIG_SYSTEM_ADDRESS_REF,	\n"+
			"	'' AS CONTACT_FIRST_NAME,	\n"+
			"	'' AS CONTACT_LAST_NAME,	\n"+
			"	'' as CONTACT_TITLE,	\n"+
			"	C.RELATION as CONTACT_JOB_TITLE,	\n"+
			"	'' AS TELEPHONE_NO,	\n"+
			"	'' AS TELEPHONE_TYPE,	\n"+
			"	'' PRIMARY_PHONE_FLAG ,        	\n"+
			"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID, \n"+
			"	C.CONTACT_TO,C.phone,C.phone2,C.mobile,C.mobile2,C.fax	\n"+
			
			"	FROM m_customer M	\n"+
			"	INNER JOIN m_contact C	\n"+
			"	ON  C.CUSTOMER_ID = M.CUSTOMER_ID 	\n"+
			"	INNER JOIN  m_address A 	\n"+
			"	ON C.CUSTOMER_ID = A.CUSTOMER_ID 	\n"+
			"   WHERE M.customer_id = "+customerId+
			"  "+whereCond;
			
            logger.debug("Select:"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				Set s = contactMap.keySet();
				Iterator it = s.iterator();
				for (int k = 1; it.hasNext(); k++) {
					//Meter m = new Meter();
					String contactType = (String) it.next();
					for(i=0;i<colConatctList.size();i++){
						ColumnBean colBean = (ColumnBean)colConatctList.get(i);
						String contactFirstName ="";
						String contactLastName ="";
						String contactTitle ="";
						
						logger.debug("Contact_to:"+rs.getString("CONTACT_TO"));
						String[] contactTos = Utils.isNull(rs.getString("CONTACT_TO")).split("\\ ");
						if(contactTos != null && contactTos.length==1){
							contactTitle = "";
							contactFirstName = Utils.isNull(contactTos[0]);
							contactLastName ="";
						}else if(contactTos != null && contactTos.length==2){
							contactTitle = "";
							contactFirstName = Utils.isNull(contactTos[0]);
							contactLastName =Utils.isNull(contactTos[1]);
						}else{
							contactTitle = "";
							contactFirstName = Utils.isNull(rs.getString("CONTACT_TO"));
							contactLastName ="";
						}
						
						/*contactMap.put("phone", "phone");
						contactMap.put("phone2", "phone2");
						contactMap.put("mobile", "mobile");
						contactMap.put("mobile2", "mobile2");
						contactMap.put("fax", "fax");  not Gen field NULL*/
                        
						logger.debug("contactType:"+contactType+":rsValue:"+Utils.isNull(rs.getString(contactType)));
						
						if( !Utils.isNull(contactFirstName).equals("") && !Utils.isNull(rs.getString(contactType)).equals("") 
								&& !Utils.isNull(rs.getString(contactType)).equals("-")){
							addNewLine =true;
							if(i==colConatctList.size()-1){
								lastAppen = "";
							}else{
								lastAppen = Constants.delimeterPipeStr;
							}
							
							if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
								 dataAppend.append(Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())))); 
							}else if(colBean.getColumnName().equalsIgnoreCase("ORIG_SYSTEM_CUSTOMER_REF")){
								 dataAppend.append(Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())))).append(lastAppen); 
							}else if(colBean.getColumnName().equalsIgnoreCase("ORIG_SYSTEM_ADDRESS_REF")){
								 dataAppend.append(Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())))).append(lastAppen); 
							}else if(colBean.getColumnName().equalsIgnoreCase("CONTACT_FIRST_NAME")){
								 dataAppend.append(Utils.isNull(contactFirstName)).append(lastAppen); 
							}else if(colBean.getColumnName().equalsIgnoreCase("CONTACT_LAST_NAME")){
								 dataAppend.append(Utils.isNull(contactLastName)).append(lastAppen); 
							}else if(colBean.getColumnName().equalsIgnoreCase("CONTACT_TITLE")){
								 dataAppend.append(Utils.isNull(contactTitle)).append(lastAppen); 
							}else if(colBean.getColumnName().equalsIgnoreCase("CONTACT_JOB_TITLE")){
								 dataAppend.append(Utils.isNull(rs.getString(colBean.getColumnName()))).append(lastAppen); 
								 
							}else if(colBean.getColumnName().equalsIgnoreCase("TELEPHONE_NO")){
								 dataAppend.append(Utils.isNull(rs.getString(contactType))).append(lastAppen); 
								
							}else if(colBean.getColumnName().equalsIgnoreCase("TELEPHONE_TYPE")){
								if("phone".equalsIgnoreCase(contactType) || "phone2".equalsIgnoreCase(contactType)){
									dataAppend.append("GEN").append(lastAppen); 
								}else if("mobile".equalsIgnoreCase(contactType) || "mobile2".equalsIgnoreCase(contactType)){
									dataAppend.append("MOBILE").append(lastAppen); 
								}else if("fax".equalsIgnoreCase(contactType)){
									dataAppend.append("FAX").append(lastAppen); 
								}
								
							}else if(colBean.getColumnName().equalsIgnoreCase("PRIMARY_PHONE_FLAG")){
								if(k==1){
									dataAppend.append("Y").append(lastAppen); 
								}else{
									dataAppend.append("").append(lastAppen); 
								}
		                    }else if(colBean.getColumnName().equalsIgnoreCase("ORG_ID")){
		                    	 dataAppend.append(Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())))).append(lastAppen); 
							}
						}else{
							addNewLine = false;
						}
					}//for
					
                    if(addNewLine){
				      dataAppend.append(Constants.newLine);//new line
                    }
					//totalRows++;
				}//for
				
			}//while
			
			logger.debug("totalRows:"+totalRows);	
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	
	public   TableBean exportPDReceiptHis(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		 String lastAppen = Constants.delimeterPipeStr;
        int countTrans = 0;
        int i=0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>(); 
		try{
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** Set Data For Update ExportFlag **/
				sqlUpdateExportFlagList.add("update t_pd_receipt_his set exported = 'Y' WHERE order_no='"+rs.getString("order_no")+"'");
			}//while		
			
			tableBean.setExportCount(countTrans);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * exportMoveOrder
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception 
	 * VAN ONLY
	 */
	public  TableBean exportMoveOrder(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
/*					}else if( !colBean.getExternalFunction().equalsIgnoreCase("N")){
						dataAppend.append(ExportHelper.covertToFormatExportByFunction(colBean,rs,userBean)).append(lastAppen);*/
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** add Order Line Detail */
				dataAppend.append(exportMoveOrderLine(conn,rs.getString("request_number"),sqlUpdateExportFlagList));
				
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_move_order set exported ='Y' WHERE request_number = '"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	/**
	 * exportProdShow
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception 
	 * VAN ONLY
	 */
	public  TableBean exportProdShow(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
        List<ImageFileBean> imageFileListAll = new ArrayList<ImageFileBean>();
        String pathLocalImageProdShow = EnvProperties.getInstance().getProperty("path.image.prodshow.local");
        ExportBean exportBean = null;
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
                    }else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** add Order Line Detail */
				exportBean = exportProdShowLine(conn,rs.getString("order_no"),pathLocalImageProdShow);
				if(exportBean != null){
				   dataAppend.append(exportBean.getDataExportBuff());
				   imageFileListAll.addAll(exportBean.getImageFileList());
				}
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_prod_show set exported ='Y' WHERE order_no = '"+rs.getString("order_no")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		    tableBean.setImageFileList(imageFileListAll);
		    

			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	public  TableBean exportStock(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** add Order Line Detail */
				dataAppend.append(exportStockLine(conn,rs.getString("request_number"),sqlUpdateExportFlagList));
				
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_stock set exported ='Y' WHERE request_number = '"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	public  TableBean exportRequisitionProduct(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** add Order Line Detail */
				dataAppend.append(exportRequisitionProductLine(conn,rs.getString("request_number"),sqlUpdateExportFlagList));
				
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_requisition_product set exported ='Y' WHERE request_number = '"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public  TableBean exportBillPlan(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_bill_plan set exported ='Y' WHERE bill_plan_no = '"+rs.getString("bill_plan_no")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	private   StringBuffer exportMoveOrderLine(Connection conn,String requestNumber,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_move_order_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   REQUEST_NUMBER, \n"+
            	"	@rownum:=@rownum+1 	LINE_NUMBER,	\n"+
	            "	INVENTORY_ITEM_ID,	\n"+
	            "	QTY AS PRIMARY_QUANTITY,	\n"+
	            "	QTY1  AS CTN_QTY	,	\n"+
	            "	QTY2  AS PCS_QTY,	\n"+
	            "	UOM1  AS UOM_CODE \n"+
	            "   FROM t_move_order_line l ," +
	            "   (SELECT @rownum:=0) a" +
	            "   where request_number ='"+requestNumber+"' and status ='SV' ";
  
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}		
				}//for
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
				/** Set Data For Update Exported Flag ='Y' **/
				sqlUpdateExportFlagList.add("UPDATE t_move_order_line set exported ='Y' WHERE request_number ='"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);	
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	private   ExportBean exportProdShowLine(Connection conn,String orderNo,String pathLocalImageProdShow)
			throws Exception{
		ExportBean exportBean = new ExportBean();
		List<ImageFileBean> imageFileList = new ArrayList<ImageFileBean>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_prod_show_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   ORDER_NO, \n"+
            	"	@rownum:=@rownum+1 	LINE_NUMBER,	\n"+
	            "	brand,	\n"+
	            "	pic1,	\n"+
	            "	pic2,	\n"+
	            "	pic3	\n"+
	            "   FROM t_prod_show_line l ," +
	            "   (SELECT @rownum:=0) a" +
	            "   where order_no ='"+orderNo+"' ";
  
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					//Add Image Pic1 ,pic2 ,pic3 to ImageList
					if(colBean.getColumnName().equalsIgnoreCase("pic1")
					  || colBean.getColumnName().equalsIgnoreCase("pic2")
					  || colBean.getColumnName().equalsIgnoreCase("pic3")){
						
						if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
							ImageFileBean im = new ImageFileBean();
							im.setGenerateImageFileName(rs.getString(colBean.getColumnName()));
							im.setImageFileName(pathLocalImageProdShow+rs.getString(colBean.getColumnName()));
							imageFileList.add(im);
						}
					}
				}//for
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
			}//while
			logger.debug("totalRows:"+totalRows);	
			
			exportBean.setDataExportBuff(dataAppend);
			exportBean.setImageFileList(imageFileList);
			
			return exportBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	private   StringBuffer exportStockLine(Connection conn,String requestNumber,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_stock_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   REQUEST_NUMBER, \n"+
            	"	@rownum:=@rownum+1 	LINE_NUMBER,	\n"+
	            "	INVENTORY_ITEM_ID,	\n"+
	            "	UOM,	\n"+
	            "	UOM2,	\n"+
	            "	QTY,	\n"+
	            "	SUB,	\n"+
	            "	EXPIRE_DATE, \n"+
	            "	QTY2,	\n"+
	            "	SUB2,	\n"+
	            "	EXPIRE_DATE2, \n"+
	            "	QTY3,	\n"+
	            "	SUB3,	\n"+
	            "	EXPIRE_DATE3, \n"+
	            "	avg_order_qty \n"+
	            "   FROM t_stock_line l ," +
	            "   (SELECT @rownum:=0) a" +
	            "   where request_number ='"+requestNumber+"' and status ='SV' "+
                "   and( qty <> 0 or qty2 <> 0 or qty3 <> 0 or sub <> 0 or sub2 <> 0 or sub3 <> 0 )" ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}		
				}//for
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
				/** Set Data For Update Exported Flag ='Y' **/
				sqlUpdateExportFlagList.add("UPDATE t_move_order_line set exported ='Y' WHERE request_number ='"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);	
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	private   StringBuffer exportRequisitionProductLine(Connection conn,String requestNumber,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_move_order_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   REQUEST_NUMBER, \n"+
            	"	@rownum:=@rownum+1 	LINE_NUMBER,	\n"+
	            "	INVENTORY_ITEM_ID,	\n"+
	            "	QTY AS PRIMARY_QUANTITY,	\n"+
	            "	QTY1  AS CTN_QTY	,	\n"+
	            "	QTY2  AS PCS_QTY,	\n"+
	            "	UOM1  AS UOM_CODE \n"+
	            "   FROM t_requisition_product_line l ," +
	            "   (SELECT @rownum:=0) a" +
	            "   where request_number ='"+requestNumber+"' and status ='SV' ";
  
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}		
				}//for
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
				/** Set Data For Update Exported Flag ='Y' **/
				sqlUpdateExportFlagList.add("UPDATE t_requisition_product_line set exported ='Y' WHERE request_number ='"+rs.getString("request_number")+"'");
				
			}//while
			logger.debug("totalRows:"+totalRows);	
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportSalesVisit(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
            
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					
					logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
		        /**  add Sale_visit_line **/
				dataAppend.append(exportSalesVisitLine(conn,rs.getString("VISIT_ID"),rs.getString("VISIT_CODE")));
		        
				/** Set Data For Update Exported Flag **/
				sqlUpdateExportFlagList.add("update t_visit set exported = 'Y' WHERE visit_id="+rs.getString("visit_id"));
				
			}//while
			
			logger.debug("totalRows:"+totalRows);
			
			//set TotalRows
//			if( !Utils.isNull(dataAppend.toString()).equals("")){
//			   tableBean.setExportCount(dataAppend.toString().split("\n").length);
//			}
			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	/**
	 * 
	 * @param conn
	 * @param lineId
	 * @return
	 * @throws Exception
	 */
	private StringBuffer exportSalesVisitLine(Connection conn, String visitId,
			String visitCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		int i = 0;
		String lastAppen = Constants.delimeterPipeStr;
		int totalRows = 0;
		try {
			/** Get Column Detail Order **/
			TableBean orderDBean = new TableBean();
			orderDBean.setTableName("t_visit_line");
			List colOrderList = ExportHelper.initColumn(orderDBean);
			String sql = "	select		\n"
					+ "	'D'	AS 	RECORD_TYPE,	\n"
					+ "	'"
					+ visitCode
					+ "'	AS 	VISIT_CODE,	\n"
					+ "	@rownum:=@rownum+1	AS 	LINE_NO,	\n"
					+ "	PRODUCT_ID	AS 	PRODUCT_ID,	\n"
					+ "	UOM_ID	AS 	UOM_ID,	\n"
					+ "	AMOUNT	AS 	AMOUNT,	\n"
					+ "   ISACTIVE	AS 	ISACTIVE, \n"
					+ "	(select max(value) from c_reference where code ='OrgID') AS ORG_ID,		\n"
					+ "   AMOUNT2	AS 	AMOUNT2 \n" + "   FROM t_visit_line, \n"
					+ "   (SELECT @rownum:=0) a" + "   WHERE visit_id ="
					+ visitId;

			logger.debug("SQL:" + sql);

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				totalRows++;
				for (i = 0; i < colOrderList.size(); i++) {
					ColumnBean colBean = (ColumnBean) colOrderList.get(i);
					if (i == colOrderList.size() - 1) {
						lastAppen = "";
					} else {
						lastAppen = Constants.delimeterPipeStr;
					}
					if (colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")) {
						dataAppend.append(ExportHelper.covertToFormatExport(colBean, rs));
					} else {
						dataAppend.append(ExportHelper.covertToFormatExport(colBean, rs)).append(lastAppen);
					}
				}// for

				/** Gen New Line **/
				dataAppend.append(Constants.newLine);

			}// while

			logger.debug("totalRows:" + totalRows);
			return dataAppend;
		} catch (Exception e) {
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
		}
	}

	// Export Sales Order To Temp2
	public   TableBean exportSaleOrderTemp2(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** add Order Line Detail */
				dataAppend.append(exportSalesOrderLineTemp2(conn,rs.getString("order_id"),sqlUpdateExportFlagList));
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_order set TEMP2_EXPORTED ='Y' WHERE order_id = "+rs.getString("order_id"));
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
		
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	private   StringBuffer exportSalesOrderLineTemp2(Connection conn,String orderId,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_order_line_rec");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   h.ORDER_NO	AS	ORDER_NUMBER, \n"+
            	"	d.LINE_NO	AS	LINE_NO,	\n"+
	            "	d.PRODUCT_ID	AS	PRODUCT_ID,	\n"+
	            "	d.UOM_ID	AS	UOM_ID,	\n"+
	            "	d.QTY	AS	QTY,	\n"+
	            "	d.PRICE	AS	PRICE,	\n"+
	            "	d.LINE_AMOUNT	AS	LINE_AMOUNT, \n"+
	            "	d.DISCOUNT	AS	DISCOUNT,	\n"+
	            "	d.TOTAL_AMOUNT	AS	TOTAL_AMOUNT, \n"+
	            "	(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
	            "	''	AS	WAREHOUSE,	\n"+
	            "	a.CODE	AS	SUBINVENTORY,	\n"+
	            "	d.REQUEST_DATE	AS	REQUEST_DATE,	\n"+
	            "	d.SHIPPING_DATE	AS	SHIPPING_DATE,	\n"+
	            "	d.PROMOTION	AS	PROMOTION,	\n"+
	            "	d.VAT_AMOUNT	AS	VAT_AMOUNT,	\n"+
	            "   d.ORDER_LINE_ID AS ORDER_LINE_ID, \n"+
	            "   d.ORG AS ORG, \n"+
	            "   d.SUB_INV AS SUB_INV \n"+
	            "	FROM t_order_line d 	\n"+
	            "	inner join t_order h	\n"+
	            "	on d.ORDER_ID = h.ORDER_ID	\n"+
	            "	left outer join ad_user a	\n"+
	            "	on h.USER_ID = a.USER_ID	\n"+
	            "   WHERE d.order_id ="+orderId+" \n";
  
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}
				}//for
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
				/** Set Data For Update Exported Flag ='Y' **/
				sqlUpdateExportFlagList.add("UPDATE t_order_line set TEMP2_EXPORTED ='Y' WHERE ORDER_LINE_ID ="+rs.getString("ORDER_LINE_ID"));
				
			}//while
			logger.debug("totalRows:"+totalRows);	
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	public TableBean exportSalesReceiptHeaderCredit(Connection conn,TableBean tableBean, User userRequest) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        int countTrans = 0;
        String lastAppen = Constants.delimeterPipeStr;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>(); 
		try{
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				//add Receipt Line Detail
				dataAppend.append(exportSalesReceiptItemCredit(conn,countTrans,rs.getString("receipt_id"),rs.getString("receipt_no")));
				
				/** Set Data For Update ExportFlag **/
				sqlUpdateExportFlagList.add("update t_receipt set temp2_exported = 'Y' WHERE receipt_id="+rs.getString("receipt_id"));
			}//while		
			
			tableBean.setExportCount(countTrans);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}

	private Object exportSalesReceiptItemCredit(Connection conn,int countTrans,String receiptId,String receiptNo) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        int no = 0;
		try{
            /** Get Column Detail Receipt **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_receipt_line_rec");
            List colOrderList = ExportHelper.initColumn(orderDBean);
   
         // Pasuwat Wang-arrayagul 
			// Create Credit Note Line Sent To Oracle
            String sql ="	Select 		\n"+
            "	   'D'	AS	RECORD_TYPE	,	\n"+
            "		@rownum:=@rownum+1  	AS	LINE_NO ,	\n"+
            "		'"+receiptNo+"'	AS	RECEIPT_NUMBER,		\n"+
            "		t_credit_note.credit_note_no	AS	AR_INVOICE_NO	,	\n"+
            "		null	AS	SALES_ORDER_NO	,		\n"+
            "		t_receipt_by.RECEIPT_AMOUNT	AS	RECEIPT_BY_AMOUNT	,	\n"+
            "		t_credit_note.total_amount	AS	INVOICE_AMOUNT	,	\n"+
            "		t_receipt_cn.CREDIT_AMOUNT	AS	CREDIT_AMOUNT	,	\n"+
            "		t_receipt_match_cn.PAID_AMOUNT	AS	PAID_AMOUNT	,	\n"+
            "		t_receipt_cn.REMAIN_AMOUNT	AS	REMAIN_AMOUNT	,	\n"+
            "		null	AS	DESCRIPTION	,	\n"+
            "	    (select max(value) from c_reference where code ='OrgID') AS ORG_ID, 		\n"+
            "	    null as order_number, \n"+
            
            
            "	    IF(AD_USER.PD_PAID='Y', IF(T_RECEIPT.ISPDPAID IS NULL ,'PD','PD_CR'),t_receipt_by.PAYMENT_METHOD)  AS PAYMENT_METHOD ,\n"+
            "       /* CASE WHEN t_receipt_by.PAYMENT_METHOD ='CS' THEN 'N' ELSE '' END AS CASH_FLAG, */ \n"+
            "       t_receipt_by.WRITE_OFF AS WRITE_OFF, \n"+ 
            /******** new Requirement ************************************/
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.bank)	AS	BANK, \n"+	
            "       ''	AS	BANK_BRANCH, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.cheque_no)	AS	CHEQUE_NO, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.cheque_date)	AS	CHEQUE_DATE, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.credit_card_type)	AS	CREDIT_CARD_TYPE ,	 \n"+
            /******** new Requirement ************************************/
            /** WIT Add 07/03/2011  **/
            "       '' AS ORDER_LINE_ID \n"+
            
            "		FROM 		\n"+
            "		t_receipt_cn ,	\n"+
            "		t_receipt_match_cn, 	\n"+
            "		t_receipt_by,		\n"+
            "	    t_credit_note,            \n"+
            "       t_receipt ,			\n"+	
            "		ad_user,			\n"+
            "	    (SELECT @rownum:=0) rowsnum    \n"+
            "		where 1=1		    \n"+
            "		and t_receipt.receipt_id = t_receipt_cn.receipt_id and t_receipt.user_id = ad_user.user_id \n"+
            "		and t_receipt_match_cn.receipt_cn_ID = t_receipt_cn.receipt_cn_ID	\n"+
            "	    and t_receipt_match_cn.receipt_by_ID = t_receipt_by.receipt_by_ID		\n"+
            "       and t_credit_note.credit_note_id = t_receipt_cn.credit_note_id  \n"+
            "	    and t_receipt_cn.receipt_id ="+receiptId;
			
			logger.debug("SQL for Credit Note:"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					no++;
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					
				}//for
				
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
			}//while
			
            sql ="	Select 		\n"+
            "	   'D'	AS	RECORD_TYPE	,	\n"+
            "		@rownum:=@rownum+1  	AS	LINE_NO ,	\n"+
            "		'"+receiptNo+"'	AS	RECEIPT_NUMBER,		\n"+
            "		t_receipt_line.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
            "		t_receipt_line.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
            "		t_receipt_by.RECEIPT_AMOUNT	AS	RECEIPT_BY_AMOUNT	,	\n"+
            "		t_receipt_line.INVOICE_AMOUNT	AS	INVOICE_AMOUNT	,	\n"+
            "		t_receipt_line.CREDIT_AMOUNT	AS	CREDIT_AMOUNT	,	\n"+
            "		t_receipt_match.PAID_AMOUNT	AS	PAID_AMOUNT	,	\n"+
            "		t_receipt_line.REMAIN_AMOUNT	AS	REMAIN_AMOUNT	,	\n"+
            "		t_receipt_line.DESCRIPTION	AS	DESCRIPTION	, 	\n"+
            "	    (select max(value) from c_reference where code ='OrgID') AS ORG_ID, 		\n"+
            "	    t_order.order_no as order_number, \n"+
            "	    IF(AD_USER.PD_PAID='Y', IF(T_RECEIPT.ISPDPAID IS NULL ,'PD','PD_CR'),t_receipt_by.PAYMENT_METHOD)  AS PAYMENT_METHOD ,\n"+
            "       /* CASE WHEN t_receipt_by.PAYMENT_METHOD ='CS' THEN 'N' ELSE '' END AS CASH_FLAG, */ \n"+
            "       t_receipt_by.WRITE_OFF AS WRITE_OFF, \n"+ 
            /******** new Requirement ************************************/
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.bank)	AS	BANK, \n"+	
            "       ''	AS	BANK_BRANCH, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.cheque_no)	AS	CHEQUE_NO, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.cheque_date)	AS	CHEQUE_DATE, \n"+	
            "       IF(AD_USER.PD_PAID='Y',null,t_receipt_by.credit_card_type)	AS	CREDIT_CARD_TYPE ,	 \n"+
            /******** new Requirement ************************************/
            /** WIT Add 07/03/2011  **/
            "       '' AS ORDER_LINE_ID \n"+
            "		FROM 		\n"+
            "		t_receipt_line ,	\n"+
            "		t_receipt_match, 	\n"+
            "		t_receipt_by,		\n"+
            "	    t_order,             \n"+
            "       t_receipt ,			\n"+	
            "		ad_user,			\n"+
            "	    (SELECT @rownum:= "+totalRows+") rowsnum    \n"+
            "		where 1=1		    \n"+
            "		and t_receipt.receipt_id = t_receipt_line.receipt_id and t_receipt.user_id = ad_user.user_id \n"+
            "		and t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID	\n"+
            "	    and t_receipt_match.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID		\n"+
            "       and t_receipt_line.order_id = t_order.order_id  \n"+
            "	    and t_receipt_line.receipt_id ="+receiptId;

            logger.debug("SQL:"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					no++;
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					
				}//for
				
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
			}//while
			
			countTrans += totalRows;
			logger.debug("countTrans:"+countTrans);	
			
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
}
