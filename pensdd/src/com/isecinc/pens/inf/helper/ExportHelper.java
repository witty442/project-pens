package com.isecinc.pens.inf.helper;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;

public class ExportHelper {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			//ธ.กรุงเทพ สาขาสาธุประดิษฐ์  195-4-75402-2
			String ss = "ธ.กรุงเทพ สาขาสาธุประดิษฐ์  195-4-75402-2";
			String[] strArry = ss.split("\\ ");
			
			System.out.println(ss.substring(0,ss.indexOf(" ")));
			
			ss = ss.substring(ss.indexOf(" ")+1,ss.length());
			System.out.println(ss.substring(0,ss.indexOf(" ")));
			
			ss = ss.substring(ss.lastIndexOf(" ")+1,ss.length());
			System.out.println(ss.substring(0,ss.length()));
			
			/*System.out.println("Result:["+strArry.length+"]");
			System.out.println("Result:["+strArry[0]+"]");
			System.out.println("Result:["+strArry[1]+"]");
			System.out.println("Result:["+strArry[2]+"]");
			System.out.println("Result:["+strArry[3]+"]");*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static  LinkedHashMap<String,TableBean> initExportConfig(String path,String controlFileName,Connection conn,String transactionType,User userBean,String requestTable)throws Exception {
		logger.debug("Init table Config");
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path+controlFileName); // Seq, Procedure, Source, Destination
		LinkedHashMap<String,TableBean>  tableMap = new LinkedHashMap<String,TableBean> ();
		try {
			String lineStr = null;
			while ((lineStr = br.readLine()) != null) {
				if (!Utils.isBlank(lineStr)) { // exclude blank line
					String[] cs = lineStr.split("\t");
					if (!cs[0].startsWith("Seq") && !cs[0].startsWith("#")) { // exclude header, comment
						try {
							TableBean tableBean = new TableBean();
							String[] p = ((String)cs[0]).split(",");
							tableBean.setTableName(Utils.isNull(p[1]));
							tableBean.setFileFtpName(Utils.isNull(p[2]));
							tableBean.setSource(Utils.isNull(p[3]));
							tableBean.setDestination(Utils.isNull(p[4]));
							tableBean.setTransactionType(Utils.isNull(p[5]));
							tableBean.setAuthen(Utils.isNull(p[6]));
							
						    /** Set Path To Export by Authen and Source,Destination **/
							if(tableBean.getTransactionType().equals(Constants.TRANSACTION_MASTER_TYPE)){
								if( Utils.isNull(tableBean.getSource()).equalsIgnoreCase(Constants.TYPE_SALES) && Utils.isNull(tableBean.getDestination()).equalsIgnoreCase(Constants.TYPE_CENTER)){
									/** SALES_TO_CENTER(SALES)  Export to SALE_IN  */
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.master.sales.in"));
								}else if(Utils.isNull(tableBean.getSource()).equalsIgnoreCase(Constants.TYPE_CENTER) && Utils.isNull(tableBean.getDestination()).equalsIgnoreCase(Constants.TYPE_SALES)){
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.master.sales.in"));
								}else {
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.master.sales.out"));
								}
							}else{
								if( Utils.isNull(tableBean.getSource()).equalsIgnoreCase(Constants.TYPE_SALES) && Utils.isNull(tableBean.getDestination()).equalsIgnoreCase(Constants.TYPE_CENTER)){
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.transaction.sales.in"));
								}else{
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.transaction.sales.out"));
								}
							}
							
							/** Gen File Name **/
							tableBean.setFileFtpNameFull(ExportHelper.genFileName(tableBean,userBean));
							
							boolean canAccess = isCanAccess(userBean, tableBean);
							
//							logger.debug("tableName:"+tableBean.getTableName()+",userRole:"+userBean.getType()+",canAccess:"+canAccess+",transType:"+transactionType+"");
//                          logger.debug("requestTable:"+requestTable+",transactionType:"+tableBean.getTransactionType());
                            
							if(    ( Utils.isNull(requestTable).equals(tableBean.getTableName()) || Utils.isNull(requestTable).equals(""))
								&&	canAccess 
								&& (transactionType.equals(tableBean.getTransactionType())) ){
							    
								logger.debug("CanExport :"+tableBean.getTableName());
								/** init table properties  */
								tableBean.setColumnBeanList(initColumn(path,tableBean));
								/** Gen SQL Insert And Update***/
								tableBean = genPrepareSQL(tableBean,userBean);
								/** Store Data Map **/
						        tableMap.put(tableBean.getFileFtpName(),tableBean);
							}
							
						} catch (Exception e) {
							//log.debug(lineStr);
							throw e;
						}
					}//if 2
				}//if 1
			}//while
		}catch(Exception e){
            throw e;
		} finally {
			FileUtil.close(br);
		}
		return tableMap;
	}
	
	public static  List readConfigTableExport() throws Exception {
		List r = readConfigTableExport("inf-config/table-mapping-export/control_export.csv");
		return r;
	}
	
	private static  List readConfigTableExport(String path) throws Exception {
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path); // Seq, Procedure, Source, Destination
		try {
			String lineStr = null;
			List<References> tableList = new ArrayList<References>();
			References  r = null;
			while ((lineStr = br.readLine()) != null) {
				if (!Utils.isBlank(lineStr)) { // exclude blank line
					String[] cs = lineStr.split("\t");
					if (!cs[0].startsWith("Seq") && !cs[0].startsWith("#")) { // exclude header, comment
						try {
							TableBean tableBean = new TableBean();
							String[] p = ((String)cs[0]).split(",");
							tableBean.setTableName(Utils.isNull(p[1]));
							tableBean.setFileFtpName(Utils.isNull(p[2]));
							tableBean.setSource(Utils.isNull(p[3]));
							tableBean.setDestination(Utils.isNull(p[4]));
							tableBean.setTransactionType(Utils.isNull(p[5]));
							tableBean.setAuthen(Utils.isNull(p[6]));
                            
							r = new References(tableBean.getTableName()+"|"+tableBean.getTransactionType(), tableBean.getTableName());
							tableList.add(r);
                            
						}catch(Exception e){
							throw e;
						}
					}//if 2
				}//if 1
			}//while
			
			return tableList;
		}catch(Exception e){
			throw e;
		} finally {
			FileUtil.close(br);
		}

	}
	/**
	 * check User CanExport
	 * @param userBean
	 * @param tableBean
	 * @return
	 */
	public static boolean isCanAccess(User userBean,TableBean tableBean){
		boolean canAccess = false;
		//logger.debug("User Role:"+Utils.isNull(userBean.getRole().getKey()));
		
		if(Utils.isNull(userBean.getType()).equals(User.ADMIN)){ //ADMIN Access ALL
			if(tableBean.getAuthen().indexOf(User.ADMIN) != -1){
        		canAccess = true;  
            }
        }else if(Utils.isNull(userBean.getType()).equals(User.DD)){//SALE_CENTER
        	if(tableBean.getAuthen().indexOf(User.DD) != -1){
        		canAccess = true;  
            }
		}else if(Utils.isNull(userBean.getType()).equals(User.TT)){//SALES GENERAL   IMPORT FROM ORACLE AND SALES_CENTER
			if(tableBean.getAuthen().indexOf(User.TT) != -1){
				canAccess = true;  
            } 
		}else if(Utils.isNull(userBean.getType()).equals(User.VAN)){//SALES VAN IMPORT FROM ORACLE AND SALES_CENTER
			if(tableBean.getAuthen().indexOf(User.VAN) != -1){
				canAccess = true;  
            } 
		}
		return canAccess;
	}
	
	public static  List<ColumnBean> initColumn(TableBean tableBean) throws Exception {
		String pathControl ="inf-config/table-mapping-export/";
        return initColumn(pathControl,tableBean);
	}
	public static  List<ColumnBean> initColumn(String path ,TableBean tableBean) throws Exception {
		logger.debug("init Column TableName["+tableBean.getTableName()+"]");
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path+getRealNameTable(tableBean.getTableName())+".csv"); // Seq, Procedure, Source, Destination
		List<ColumnBean> columnList = new ArrayList<ColumnBean>();
		try {
			String lineStr = null;
			while ((lineStr = br.readLine()) != null) {
				//logger.debug("lineStr:"+lineStr);
				if (!Utils.isBlank(lineStr)) { // exclude blank line
					String[] cs = lineStr.split("\t");
					if (!cs[0].startsWith("#")) { // exclude header, comment
						try {
							//#ColName,PK,TEXT POSITION,TYPES
							ColumnBean col = new ColumnBean();
							String[] p = ((String)cs[0]).split(Constants.delimeterComma);
							//logger.debug("p size:"+p.length);
							col.setColumnName(Utils.isNull(p[0]));
							col.setAction(Utils.isNull(p[1]));
							col.setTextPosition(ConvertUtils.convertToInt(Utils.isNull(p[2])));
							col.setColumnType(Utils.isNull(p[3]));
							col.setDefaultValue(Utils.isNull(p[4]));
							col.setExternalFunction(Utils.isNull(p[5]));
							if(p.length > 6){
								col.setStartPosition(ConvertUtils.convertToInt(Utils.isNull(p[6])));
							}
							if(p.length > 7){
								col.setEndPosition(ConvertUtils.convertToInt(Utils.isNull(p[7])));
							}
							if(p.length > 8){
								col.setFillSymbol(Utils.isNull(p[8]));
							}
							columnList.add(col);
						} catch (Exception e) {
							//log.debug(lineStr);
							throw e;
						}
					}//if 2
				}//if 1
			}//while
			
		
			return columnList;
		} finally {
			FileUtil.close(br);
			br= null;
		}
	}
	
	/**
	 * genPrepareSQL
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public static TableBean  genPrepareSQL(TableBean tableBean,User userBean) throws Exception{
		Statement st = null;
		ResultSet rs = null;
	    String selectSql = "";
		try{
			if(tableBean.getTableName().equalsIgnoreCase("m_customer") ||
				tableBean.getTableName().equalsIgnoreCase("m_cust_profile") ||
			    tableBean.getTableName().equalsIgnoreCase("m_cust_contact") ||
			    tableBean.getTableName().equalsIgnoreCase("t_order") ||
			    tableBean.getTableName().equalsIgnoreCase("t_order_dd") ||
			    tableBean.getTableName().equalsIgnoreCase("t_receipt") ||
			    tableBean.getTableName().equalsIgnoreCase("t_visit") ||
			    tableBean.getTableName().equalsIgnoreCase("m_sales_inventory") ||
			    tableBean.getTableName().equalsIgnoreCase("m_trip") ||
			    tableBean.getTableName().equalsIgnoreCase("m_member_health") // edit by tutiya
			    ) {
					
				selectSql = ExportHelper.genSpecialSQL(tableBean,userBean);
			}else{
				selectSql += "select ";
			    for ( int i = 0; i < tableBean.getColumnBeanList().size(); i++) {   
			    	ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
			    	if(i ==tableBean.getColumnBeanList().size()-1){
			    	   selectSql += colBean.getColumnName()+" \n";	
			    	}else{
			    	   selectSql += colBean.getColumnName()+", \n";
			    	}
			    }
			    selectSql += "from "+getRealNameTable(tableBean.getTableName()) +" where 1=1 \n";
			}
			
			/** Add Where Condition  */
			if(tableBean.getTableName().equalsIgnoreCase("ad_user")){
				selectSql += " and role is not null and role <> '' \n";
			}
		   
			//set select SQL
			tableBean.setPrepareSqlSelect(selectSql);

		    return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(st != null){
				st.close();st =null;
			}
			if(rs != null){
				rs.close();rs =null;
			}
		}
	}
	
	
	public static String genFileName(TableBean tableBean,User userBean) throws Exception{
		String strGen = "";
		String dateStr = Utils.format(new Date(), "yyyyMMddHHmm");

		if(tableBean.getTransactionType().equals(Constants.TRANSACTION_MASTER_TYPE)){
			if("CUST".equalsIgnoreCase(tableBean.getFileFtpName())){
				strGen = dateStr+"-"+userBean.getId()+"-"+tableBean.getFileFtpName()+".txt";
			}else{
			    strGen = dateStr+"-"+tableBean.getFileFtpName()+".txt";
			}
		}else{
			if(User.TT.equalsIgnoreCase(userBean.getType()) && "SALESRECEIPT".equalsIgnoreCase(tableBean.getFileFtpName())){
		        strGen = dateStr+"-"+userBean.getId()+"-TTLOCKBOX.txt";
			}else{
				strGen = dateStr+"-"+userBean.getId()+"-"+tableBean.getFileFtpName()+".txt";
			}
		}
		
		return strGen;
	}
	
	public static String genFileNameMTrip(TableBean tableBean,String userId) throws Exception{
		String strGen = "";
		String dateStr = Utils.format(new Date(), "yyyyMMddHHmm");
		strGen = dateStr+"-"+userId+"-"+tableBean.getFileFtpName()+".txt";
		return strGen;
	}
	
   /**
    * 
    * @param userBean
    * @return
    * a.	ในกรณีที่เป็น VAN ให้ใช้รหัส Sales ในการไปค้นหาชื่อ sub Inbventory
    * b.	ในกรณีที่เป็น TT แบ่งออกเป็น 2 กรณี
        i.	ถ้า Item ใด มี Item Category (Sales) Segment ที่ 3 เป็น ?สินค้าคงคลัง? ให้ระบุ Sub Inventory เป็น ?G001?
        ii.	ถ้า Item ใด มี Item Category (Sales) Segmentที่ 3 เป็น ?สินค้าพรีเมี่ยม? ให้ระบุ Sub Inventory เป็น ?G001-PRM? 

    */
	public static String getSubInventory(Connection conn,User userBean) throws Exception{
		InterfaceDAO dao = new InterfaceDAO();
		return dao.getSunInventory(conn, userBean);
	}
	
	/**
	 * covertDataToExport
	 * @param colBean
	 * @param rs
	 * @return
	 * @throws Exception
	 * DESC : Convert To String to Export
	 */
	public static String covertToFormatExport(ColumnBean colBean,ResultSet rs) throws Exception{
		String dataConvertStr = "";
		
		if(Constants.EXPORT_FILL_SYMBOL_ZERO.equalsIgnoreCase(colBean.getFillSymbol())){
			dataConvertStr = Constants.EXPORT_STRING_SYMBOL_ZERO_DEFALUE;
			return dataConvertStr;
		}else{
			
			if(colBean.getColumnName().equalsIgnoreCase("Remittance_Bank_Name")){ //position 0
				String[] bankFullName = Utils.isNull(rs.getString(colBean.getColumnName())).split("\\|");
				if(bankFullName != null && bankFullName.length > 0){
					 return bankFullName[0];
				}
			}
            if(colBean.getColumnName().equalsIgnoreCase("Remittance_Bank_Branch_Name")){ //position 1
            	String[] bankFullName = Utils.isNull(rs.getString(colBean.getColumnName())).split("\\|");
				if(bankFullName != null && bankFullName.length > 0){
					 return bankFullName[1];
				}
			}

			if(colBean.getColumnName().equalsIgnoreCase("Destination_Account")){ //position 2
				String[] bankFullName = Utils.isNull(rs.getString(colBean.getColumnName())).split("\\|");
				if(bankFullName != null && bankFullName.length > 0){
					 return Utils.getNumberOnly(bankFullName[2]);
				}
			}
			
			/** Case Normal  **/
			if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					 dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), colBean.getDefaultValue());
				}else{
				     dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyy");
				}
			}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyyhhmmss");
				}else{
				    dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyyhhmmss");
				}
			}else{
				 dataConvertStr = Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())));
			}
		}
	   return dataConvertStr;
	}
	
	

	
	public static String covertToFormatExportByFunction(Connection conn,ColumnBean colBean ,String value) throws Exception{
		String dataConvertStr = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
		   if("GET_REF_ADDRESS_ID".equalsIgnoreCase(colBean.getExternalFunction())){
			   ps = conn.prepareStatement("select address_id,reference_id from m_address where address_id = "+value);
			   rs= ps.executeQuery();
			   if(rs.next()){
				   
			   }
		   }
       }catch(Exception e){
    	   logger.error(e.getMessage(),e);
       }finally{
    	   if(ps != null){
    		   ps.close();ps=null;
    	   }
    	   if(rs != null){
    		   rs.close();rs=null;
    	   }
       }
	   return dataConvertStr;
	}
	
	public static String getOrderType(User user) throws Exception{
		if(User.DD.equalsIgnoreCase(user.getRole().getKey())){
			return "DD";
		}else if(User.TT.equalsIgnoreCase(user.getRole().getKey())){
			return "CR";
		}else if(User.VAN.equalsIgnoreCase(user.getRole().getKey())){
			return "CS";
		}
		return "";
	}
	
	/**
	 * Get SQL By Special Case Export
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static String genSpecialSQL(TableBean tableBean,User userBean) throws Exception{
		String str = "";
		try{
			if(tableBean.getTableName().equalsIgnoreCase("M_CUSTOMER")){
				str = " SELECT M.CUSTOMER_ID ,M.CODE AS CUSTOMER_NUMBER ,M.NAME AS CUSTOMER_NAME \n"+
				" FROM m_customer M  \n"+
				" INNER JOIN m_address A 	\n"+
				" ON M.CUSTOMER_ID = A.CUSTOMER_ID 	\n"+
				" WHERE  M.user_id = "+userBean.getId() +"\n"+
				" AND   M.reference_id is null \n"+
				" AND   A.PURPOSE IS NOT NULL \n"+
				" AND (M.EXPORTED  ='N' OR M.EXPORTED IS NULL) \n"+
				" GROUP BY M.CUSTOMER_ID ";

			}else if(tableBean.getTableName().equalsIgnoreCase("t_order")){
				str ="	select 	order_id,			\n"+
				"	'H'	AS	RECORD_TYPE	,	\n"+
				"	t.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
				"	t.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
				"	t.ORDER_DATE	AS	ORDER_DATE	,	\n"+
				"	t.ORDER_TIME	AS	ORDER_TIME	,	\n"+
				"	t.CUSTOMER_ID	AS	CUSTOMER_ID	,	\n"+
				"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
				"	m.NAME	AS	CUSTOMER_NAME	,	\n"+
				
				/** OLD CODE **/
				"	t.SHIP_ADDRESS_ID	AS	SHIP_TO_SITE_USE_ID ,\n"+
				"	t.BILL_ADDRESS_ID	AS	BILL_TO_SITE_USE_ID	,\n"+
				
				/** NEW CODE **/
				/*"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID),0) <> 0 \n"+
				"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID)) \n"+ 
				"	     ELSE CONCAT(CONCAT(m.code,'-') , t.SHIP_ADDRESS_ID) \n"+ 
				"	END AS SHIP_TO_SITE_USE_ID, \n"+
				
				"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID),0) <> 0 \n"+
				"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID)) \n"+ 
				"	     ELSE CONCAT(CONCAT(m.code,'-') , t.BILL_ADDRESS_ID) \n"+ 
				"	END AS BILL_TO_SITE_USE_ID, \n"+*/
				
				"	t.PAYMENT_TERM	AS	PAYMENT_TERM	,	\n"+
				"	t.USER_ID	AS	SALESREP_ID	,	\n"+
				"	t.PRICELIST_ID	AS	PRICELIST_ID	,	\n"+
				"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	,	\n"+
				"	t.VAT_CODE	AS	VAT_CODE	,	\n"+
				"	t.VAT_RATE	AS	VAT_RATE 	,	\n"+
				"	t.PAYMENT_METHOD	AS	PAYMENT_METHOD	,	\n"+
				"	t.SHIPPING_DAY	AS	SHIPPING_DAY	,	\n"+
				"	t.SHIPPING_TIME	AS	SHIPPING_TIME	,	\n"+
				"	t.TOTAL_AMOUNT	AS	TOTAL_AMOUNT	,	\n"+
				"	t.VAT_AMOUNT	AS	VAT_AMOUNT	,	\n"+
				"	t.NET_AMOUNT	AS	NET_AMOUNT	,	\n"+
				"	t.PAYMENT	AS	PAYMENT	,	\n"+
				"	t.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
				"	t.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
				"	t.DOC_STATUS	AS	DOC_STATUS	,	\n"+
				"	t.ORA_BILL_ADDRESS_ID 	AS	ORA_BILL_ADDRESS_ID	,	\n"+
				"	t.ORA_SHIP_ADDRESS_ID	AS	ORA_SHIP_ADDRESS_ID	,	\n"+
				"	'"+tableBean.getFileFtpNameFull()+"'	AS	FILE_NAME		\n"+

				"	FROM t_order t ,m_customer m	\n"+
				"	where t.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
				"   and  m.user_id = "+userBean.getId()+" \n"+
				"   and  t.DOC_STATUS = 'SV' \n"+
				"   and ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL) \n"+
				"   ORDER BY t.ORDER_NO \n";
			
			}else if(tableBean.getTableName().equalsIgnoreCase("t_visit")){
				str ="	select			\n"+
				"	t_visit.visit_id	AS 	VISIT_ID,	\n"+
				/** TEXT FORMAL **/
				"	'H'	AS 	RECORD_TYPE,	\n"+
				"	t_visit.CODE	AS 	VISIT_CODE,	\n"+
				"	t_visit.CODE	AS 	CODE,	\n"+
				"	t_visit.VISIT_DATE	AS 	VISIT_DATE,	\n"+
				"	t_visit.VISIT_TIME	AS 	VISIT_TIME,	\n"+
				"	m_customer.code	AS 	CUSTOMER_ID,	\n"+
				"	t_visit.SALES_CLOSED	AS 	SALES_CLOSED,	\n"+
				"	t_visit.UNCLOSED_REASON	AS 	UNCLOSED_REASON,	\n"+
				"	t_visit.USER_ID	AS 	USER_ID,	\n"+
				"	t_visit.ISACTIVE	AS 	ISACTIVE,	\n"+
				"	t_visit.INTERFACES	AS 	INTERFACES,	\n"+
				"	m_customer.code	AS 	CUSTOMER_NUMBER,	\n"+
				"	m_customer.name	AS 	CUSTOMER_NAME ,	\n"+
				"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	,	\n"+
				"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME	\n"+
			
				"   from t_visit ,m_customer "+
				"   where t_visit.user_id ="+userBean.getId()+ " \n"+
				"   and m_customer.customer_id = t_visit.customer_id \n"+
				"   and t_visit.ISACTIVE ='Y'  \n"+
				"   and ( t_visit.EXPORTED  = 'N' OR t_visit.EXPORTED  IS NULL) \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("m_sales_inventory")){
				str ="	select 	\n"+
				"	(select name from m_sub_inventory s1 where s.sub_inventory_id = s1.sub_inventory_id)AS 	sub_inventory_id,	\n"+
				"	 s.user_id as user_id	\n"+
				"	 from m_sales_inventory	s \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("m_trip")){
				str ="	select distinct	\n"+
				"	m.USER_ID	\n"+
				"	from m_trip	m \n"+
				"   where ( m.EXPORTED  = 'N' OR m.EXPORTED  IS NULL) \n";
			}
			return str;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 * 
	 * H export =Y   and L   need_export ='Y' and exported ='N' ->one record
	 */
	public  static String genSqlOrderCaseUserTypeDD(TableBean tableBean,User userBean) throws Exception{
		
		String str = ""+
		"SELECT * FROM ( \n"+
		/** Case1 Export Order Normal  H -Exported =N  ,D need_export ='Y' ,exported ='N' **/
		"	select 	h.order_id,			\n"+
		"	h.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
		"	h.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
		"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
		"	m.NAME	AS	CUSTOMER_NAME ,		\n"+
		"	h.NET_AMOUNT	AS	NET_AMOUNT		\n"+
		"	FROM t_order h ,m_customer m	\n"+
		"	where h.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
		"   and  m.user_id = "+userBean.getId()+" \n"+
		"   and  h.DOC_STATUS = 'SV' \n"+
		"   and  h.EXPORTED  = 'N'  \n"+
		"   and  h.order_id in " +
		"       ( \n"+  /** Check on line is Exported ='N' */
		"          select distinct h.order_id	 \n"+
		"          FROM t_order h ,t_order_line l,m_customer m	 \n"+
		"          where h.CUSTOMER_ID = m.CUSTOMER_ID	 \n"+
		"          and h.order_id = l.order_id \n"+
		"          and m.user_id = "+userBean.getId()+" \n"+
		"          and h.DOC_STATUS = 'SV'  \n"+
		"          and h.EXPORTED  = 'N'  \n"+
		"          and l.need_export ='Y' \n"+
		"          and l.exported ='N' \n"+
		"       ) \n"+
		"   UNION  \n"+
		
		/** Case2 Export Order Line  H = Y , D = need_export = Y ,exported =N **/
		
		"	select 	h.order_id,			\n"+
		"	h.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
		"	h.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
		"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
		"	m.NAME	AS	CUSTOMER_NAME ,		\n"+
		"	h.NET_AMOUNT	AS	NET_AMOUNT		\n"+
		"	FROM t_order h ,m_customer m	\n"+
		"	where h.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
		"   and  m.user_id = "+userBean.getId()+" \n"+
		"   and  h.DOC_STATUS = 'SV' \n"+
		"   and  h.EXPORTED  = 'Y'  \n"+
		"   and  h.order_id in " +
		"       ( \n"+  /** Check on line is Exported ='N' */
		"          select distinct h.order_id	 \n"+
		"          FROM t_order h ,t_order_line l,m_customer m	 \n"+
		"          where h.CUSTOMER_ID = m.CUSTOMER_ID	 \n"+
		"          and h.order_id = l.order_id \n"+
		"          and m.user_id = "+userBean.getId()+" \n"+
		"          and h.DOC_STATUS = 'SV'  \n"+
		"          and h.EXPORTED  = 'Y'  \n"+
		"          and l.need_export ='Y' \n"+
		"          and l.exported ='N' \n"+
		"       ) \n"+
		
		"  )A ORDER BY A.ORDER_NUMBER \n";
		return str;
	}
	
	/** 
	 * genSqlCountCaseReceiptLockBoxPayment
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public  static String genSqlCountCaseReceiptLockBoxPayment(TableBean tableBean,User userBean) throws Exception{
		String sqlSelect = "" +
		"	SELECT		\n"+
		"	H.receipt_no as receipt_no,		\n"+
		"	H.code as customer_number,		\n"+
		"	H.name as customer_name,		\n"+
		"	H.receipt_amount as amount      \n"+
		"	FROM(		\n"+
		"		select	\n"+
		"		 r.RECEIPT_DATE	\n"+
		"		,r.receipt_no	\n"+
		"		,m.code	\n"+
		"		,m.name	\n"+
		"		,r.receipt_amount	\n"+
		"		,r.RECEIPT_ID as receipt_id	\n"+
		"		from t_receipt r , m_customer m , t_receipt_by rb	\n"+
		"		where 1=1	\n"+
		"		and r.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
		"		and r.RECEIPT_ID = rb.RECEIPT_ID	\n"+
		"		and r.DOC_STATUS = 'SV'	\n"+
		"		and r.ORDER_TYPE = '"+getOrderType(userBean)+"'	\n"+
		"		and r.USER_ID ="+userBean.getId()+"	\n"+
		"		and ( r.EXPORTED  = 'N' OR r.EXPORTED  IS NULL)	\n"+
		"       and  r.DOC_STATUS = 'SV' \n"+
		"		group by r.receipt_id, r.receipt_date,m.code	\n"+
		"	  ) H		\n"+
		"		INNER JOIN	\n"+
		"	 (	\n"+
		"		  select rl.receipt_id as receipt_id	\n"+
		"	      from  t_receipt_line rl, t_receipt_match rm, t_receipt_by rb		\n"+
		"	      where  1=1		\n"+
		"         and rl.RECEIPT_LINE_ID = rm.RECEIPT_LINE_ID	\n"+
		"         and rm.RECEIPT_BY_ID = rb.RECEIPT_BY_ID 	\n"+
		" 	 )D	\n"+
		"	ON H.receipt_id = D.receipt_id  	\n";
		
		
		return sqlSelect;
	}
	
	/**
	 * Gen SQl Receipt Type User Van
	 * @param tableBean
	 * @param userBean
	 * @return
	 */
	public static String genSqlSalesReceiptVan(TableBean tableBean,User userBean) throws Exception {
		String sql ="	select t_receipt.receipt_id,\n"+
		"		'H'	AS	RECORD_TYPE ,	\n"+
		"		t_receipt.receipt_no	AS	RECEIPT_NO,	\n"+
		"		t_receipt.receipt_date	AS	RECEIPT_DATE,	\n"+
		"		t_receipt.order_type	AS	ORDER_TYPE,	\n"+
		"		t_receipt.customer_id	AS	CUSTOMER_ID,	\n"+
		"		m_customer.code	AS	CUSTOMER_NUMBER,	\n"+
		"		t_receipt.customer_name	AS	CUSTOMER_NAME,	\n"+
		
		/** Don't use ****/
		"		''	AS	PAYMENT_METHOD,	\n"+
		"		''	AS	BANK,	\n"+
		"		''	AS	CHEQUE_NO,	\n"+
		"		null	AS	CHEQUE_DATE,	\n"+
		"		''	AS	CREDIT_CARD_TYPE,	\n"+
		/** Don't use ****/
		
		"		t_receipt.description	AS	DESCRIPTION,	\n"+
		"		(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
		"		'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,		\n"+
		"       t_receipt.INTERNAL_BANK AS INTERNAL_BANK , \n"+
		/** Optional **/
		"      t_receipt.receipt_amount AS amount \n"+
		"	   from t_receipt ,m_customer			\n"+
		"	   where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 				\n"+
		"      and t_receipt.ORDER_TYPE = '"+getOrderType(userBean)+"' \n"+
		"	   and m_customer.user_id = "+userBean.getId() +" \n"+
		"      and  t_receipt.DOC_STATUS = 'SV' \n"+
		"      and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL)     \n";
        return sql;
	}
	
	
	/**
	 * genSqlSalesReceiptLineCaseUserTypeDD
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String genSqlSalesReceiptLineCaseUserTypeDD(TableBean tableBean,User userBean) throws Exception {
		String sql ="	select distinct t_receipt.receipt_id,\n"+
		"		t_receipt.receipt_no	AS	RECEIPT_NO,	\n"+
		"		t_receipt.receipt_date	AS	RECEIPT_DATE,	\n"+
		"		t_receipt.order_type	AS	ORDER_TYPE,	\n"+
		"		t_receipt.customer_id	AS	CUSTOMER_ID,	\n"+
		"		m_customer.code	AS	CUSTOMER_NUMBER,	\n"+
		"		t_receipt.customer_name	AS	CUSTOMER_NAME,	\n"+
		"       t_receipt.receipt_amount AS amount \n"+
		
		"	   from t_receipt ,m_customer ,t_receipt_line	\n"+
		"	   where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID   \n"+
		"      and t_receipt.ORDER_TYPE = '"+getOrderType(userBean)+"' \n"+
		"	   and m_customer.user_id = "+userBean.getId() +" \n"+
		"      and t_receipt.DOC_STATUS = 'SV' \n"+
		"      and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL)     \n"+
		"      and t_receipt.receipt_id = t_receipt_line.receipt_id  \n"+
		"      and t_receipt_line.ORDER_LINE_ID IS NOT NULL \n";
        return sql;
	}
	
	/**
	 * 
	 * @param value
	 * @param fixLength
	 * @return
	 * @throws Exception
	 * Ex: value:100 ,FixLength :10
	 * Result:[100XXXXXXX]
	 */
	public static String appendLeft(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			value +=cAppend;
		}
		return value;
	}
	
	public static String appendRight(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			value +=cAppend;
		}
		return value;
	}
	
	public static String appendRightBK(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		String blankTemp ="";
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			blankTemp +=cAppend;
		}
		value = blankTemp+value;
		
		return value;
	}
	
	
	public static String appendByLength(String value,String cAppend ,int fixLength) throws Exception{
		int i = 0;
		for(i=0;i<fixLength;i++){
			value +=cAppend;
		}
		//logger.debug("Append:length:"+fixLength+",:value["+value+"]");
		return value;
	}
	
	/**
	 * Maping Config Export to Real Name of Table
	 * @param tableName
	 * @return
	 */
	public static String getRealNameTable(String tableName){
		if("t_order_dd".equalsIgnoreCase(tableName)){
			tableName = "t_order";
		}else if("t_order_line_dd".equalsIgnoreCase(tableName)){
		    tableName = "t_order_line";
		}else if("t_receipt_dd".equalsIgnoreCase(tableName)){
			tableName = "t_receipt";
		}else if("t_receipt_line_dd".equalsIgnoreCase(tableName)){
			tableName = "t_receipt_line";
		}
		return tableName;
	}

}
