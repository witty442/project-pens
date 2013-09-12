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
									//Pens Center To Sale App
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.master.sales.in"));
								}else {
								    //Sale App To Oracle
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.master.sales.out"));
								}
							// Tansaction Type
							}else{
								if( Utils.isNull(tableBean.getSource()).equalsIgnoreCase(Constants.TYPE_SALES) && Utils.isNull(tableBean.getDestination()).equalsIgnoreCase(Constants.TYPE_CENTER)){
									//From Pens center to Sale App
									tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.transaction.sales.in"));
								}else{
									//From Sale App to Oracle 
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
			    tableBean.getTableName().equalsIgnoreCase("t_order_rec") ||
			    tableBean.getTableName().equalsIgnoreCase("t_receipt") ||
			    tableBean.getTableName().equalsIgnoreCase("t_receipt_rec") ||
			    tableBean.getTableName().equalsIgnoreCase("t_visit") ||
			    tableBean.getTableName().equalsIgnoreCase("m_sales_inventory") ||
			    tableBean.getTableName().equalsIgnoreCase("m_trip") ||
			    tableBean.getTableName().equalsIgnoreCase("m_member_health") || 
			    tableBean.getTableName().equalsIgnoreCase("t_move_order") ||
			    tableBean.getTableName().equalsIgnoreCase("t_bill_plan")
			    ) {
					
				selectSql = ExportSQL.genSpecialSQL(tableBean,userBean);
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
	
	
	public static String genFileName(TableBean tableBean,User user) throws Exception{
		String strGen = "";
		String dateStr = Utils.format(new Date(), "yyyyMMddHHmm");

		if(tableBean.getTransactionType().equals(Constants.TRANSACTION_MASTER_TYPE)){
			if("CUST".equalsIgnoreCase(tableBean.getFileFtpName())){
				strGen = dateStr+"-"+user.getUserName()+"-"+tableBean.getFileFtpName()+".txt";
			}else{
			    strGen = dateStr+"-"+user.getUserName()+"-"+tableBean.getFileFtpName()+".txt";
			}
		}else{
			if(User.TT.equalsIgnoreCase(user.getType()) && "SALESRECEIPT".equalsIgnoreCase(tableBean.getFileFtpName())){
		        strGen = dateStr+"-"+user.getUserName()+"-TTLOCKBOX.txt";
			}else if(User.VAN.equalsIgnoreCase(user.getType()) && "MOVEORDERS".equalsIgnoreCase(tableBean.getFileFtpName())){
				strGen = dateStr+"-"+user.getUserName()+"-"+tableBean.getFileFtpName()+".txt";
			}else{
				strGen = dateStr+"-"+user.getUserName()+"-"+tableBean.getFileFtpName()+".txt";
			}
		}
		
		return strGen;
	}
	
	public static String genFileNameMTrip(TableBean tableBean,User user) throws Exception{
		String strGen = "";
		String dateStr = Utils.format(new Date(), "yyyyMMddHHmm");
		strGen = dateStr+"-"+user.getUserName()+"-"+tableBean.getFileFtpName()+".txt";
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
	public static String getSubInventory2(Connection conn,User userBean) throws Exception{
		InterfaceDAO dao = new InterfaceDAO();
		return "";//dao.getSunInventory(conn, userBean);
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
				logger.debug("colBean.getColumnName():"+colBean.getColumnName());
				
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
					   dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), colBean.getDefaultValue());
					}
				}else{
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
				       dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyy");
					}
				}
			}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
					   dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss");
					}
				}else{
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
				      dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss");
					}
				}
			}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP_NO_SS")){
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
					   dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmm");
					}
				}else{
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
				       dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmm");
					}
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
		if("t_order_rec".equalsIgnoreCase(tableName)){
			tableName = "t_order";
		}else if("t_order_line_rec".equalsIgnoreCase(tableName)){
		    tableName = "t_order_line";
		}else if("t_receipt_rec".equalsIgnoreCase(tableName)){
			tableName = "t_receipt";
		}/*else if("t_receipt_line_rec".equalsIgnoreCase(tableName)){
			tableName = "t_receipt_line_rec";
		}*/
		return tableName;
	}
	
	
}
