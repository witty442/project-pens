package com.isecinc.pens.inf.helper;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import meter.MonitorTime;

import org.apache.log4j.Logger;


import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.manager.FTPManager;

public class ImportHelper {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			String ss ="H|1084|210154010001|02012011|07:00|CR|13111002|∫√‘…—∑  È¡ ´ÿª‡ªÕ√Ï ‚µ√Ï ®”°—¥ |||10011|30|7|7|CH|||112000|7840|119840|Y|N|210154010001|200001|100000070|SV|01012011|100000070|01012011|100000070|Y";
			String[] ssArr = ss.split("\\|");
			System.out.println(""+ssArr.length);
			System.out.println("["+ssArr[8]+"]");
			System.out.println("["+ssArr[9]+"]");
			
			//System.out.println("OrderNo:"+getReplcaeNewOrderNo("210154010001"));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * initTableConfig
	 * @param conn
	 * @param transType
	 * @param pathImport
	 * @param userBean
	 * @param requestTable
	 * @return
	 * @throws Exception
	 * DESC :
	 */
	public static  int initImportConfig(String path,String controlFileName,LinkedHashMap<String,TableBean> tableMap,Connection conn,String pathImport,String transType,User userBean ,String requestTable,boolean importAll) throws Exception {
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path+controlFileName); // Seq, Procedure, Source, Destination
		EnvProperties env = EnvProperties.getInstance();
		InterfaceDAO dao = new InterfaceDAO();
		try {
			MonitorTime monitorTime = new MonitorTime("initImportConfig>>read import config");
			
			String lineStr = null;
			while ((lineStr = br.readLine()) != null) {
				if (!Utils.isBlank(lineStr)) { // exclude blank line
					String[] cs = lineStr.split("\t");
					if (!cs[0].startsWith("Seq") && !cs[0].startsWith("#")) { // exclude header, comment
						TableBean tableBean = new TableBean();
						try {
							String[] p = ((String)cs[0]).split(",");
							tableBean.setTableName(Utils.isNull(p[1].toLowerCase()));
							tableBean.setFileFtpName(Utils.isNull(p[2]));
							tableBean.setSource(Utils.isNull(p[3]));
							tableBean.setDestination(Utils.isNull(p[4]));
							tableBean.setTransactionType(Utils.isNull(p[5]));
							tableBean.setAuthen(Utils.isNull(p[6]));
							tableBean.setChildTable(Utils.isNull(p[7]));
							tableBean.setActionDB(Utils.isNull(p[8]));
							tableBean.setPreFunction(Utils.isNull(p[9]));
							tableBean.setPostFunction(Utils.isNull(p[10]));
							tableBean.setCheckDupFile(Utils.isNull(p[11]));
							
							//** case Transaction set file_ftp_name =user_id **/
							if(tableBean.getTableName().equals("m_inventory_onhand")){
								tableBean = dao.getSunInvNameMap(conn,tableBean, userBean);
							}
	
							/** add  by Type  **/
							boolean canAccess = isCanAccess(userBean, tableBean);
							
							logger.debug("tableName["+tableBean.getTableName()+":"+requestTable+"]");
							logger.debug("transType:"+transType+":"+tableBean.getTransactionType());
							logger.debug("userRole:"+userBean.getType()+",CanAccess:"+canAccess);
							
							if( ( Utils.isNull(requestTable).equals(tableBean.getTableName()) || Utils.isNull(requestTable).equals(""))
									&& canAccess && transType.equalsIgnoreCase(tableBean.getTransactionType())){
								
								if(!transType.equalsIgnoreCase(Constants.TRANSACTION_WEB_MEMBER_TYPE)){ //WEB-MEMBER NO INIT COLUMN
									/** init table properties  */
									tableBean = initColumn(path,tableBean);
									/** Gen SQL Insert And Update***/
									tableBean = genPrepareSQL(tableBean,userBean ,transType);
								}
								
								/** Store Data Map **/
						        tableMap.put(tableBean.getTableName(),tableBean);
							}
						} catch (Exception e) {
							logger.info("TabelName Error:"+tableBean.getTableName());
							throw e;
						}
					}//if 2
				}//if 1
			}//while
			
			
			monitorTime.debugUsedTime();
			
			monitorTime = new MonitorTime("initImportConfig>>Select Table Last import:[mapFileNameLastImportToTableMap]");
			/** Map Table and last File Name Import **/
			tableMap = dao.mapFileNameLastImportToTableMap(conn,tableMap,userBean,importAll);
			monitorTime.debugUsedTime();
			
			monitorTime = new MonitorTime("initImportConfig>>Download file from Ftp Server");
			/** Load File From FTP Server To Table Map By Table**/
		    FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
	        int countFileMap = ftpManager.downloadFileMappingToTable(userBean,tableMap,pathImport,userBean,transType,importAll);
	        
	        monitorTime.debugUsedTime();
	        return countFileMap;
		  
		}catch(Exception e){
			throw e;
		} finally {
			FileUtil.close(br);
		}
	}
	
	public static  List<References> readConfigTableImport() throws Exception {
		return readConfigTableImport("inf-config/table-mapping-import/control_import.csv");
	}
	
	public static  List<References> readConfigTableUpdateSalesImport() throws Exception {
		return readConfigTableImport("inf-config/table-mapping-transaction/control_transaction.csv");
	}
	
	public static  List<References> readConfigTableWebMemberImport() throws Exception {
		return readConfigTableImport("inf-config/table-mapping-webmember/control_import.csv");
	}
	
	private static  List<References> readConfigTableImport(String path) throws Exception {
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
							tableBean.setTableName(Utils.isNull(p[1].toLowerCase()));
							tableBean.setFileFtpName(Utils.isNull(p[2]));
							tableBean.setSource(Utils.isNull(p[3]));
							tableBean.setDestination(Utils.isNull(p[4]));
							tableBean.setTransactionType(Utils.isNull(p[5]));
							tableBean.setAuthen(Utils.isNull(p[6]));
							tableBean.setChildTable(Utils.isNull(p[7]));
							tableBean.setActionDB(Utils.isNull(p[8]));

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
	 * isCanAccess
	 * @param userBean
	 * @param tableBean
	 * @return
	 * DESC : check user can import table
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
	

	/**
	 * initColumn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 * Desc: Init SQL ,Column List
	 */
	public static  TableBean initColumn(String path ,TableBean tableBean) throws Exception {
		logger.debug("InitColumn tableName:["+tableBean.getTableName()+"]");
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path+tableBean.getTableName()+".csv"); // Seq, Procedure, Source, Destination
		List<ColumnBean> columnList = new ArrayList<ColumnBean>();
		String columnTableAll = "";
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
							col.setKey(Utils.isNull(p[6]));
                            if(p.length >=8){
                            	col.setRoleAction(Utils.isNull(p[7]));
							}
                            if(p.length >=9){
                            	col.setRequireField(Utils.isNull(p[8]));
							}
                            if(p.length >=10){
                            	col.setValidateFunc(Utils.isNull(p[9]));
							}
							columnList.add(col);
							
							if(col.getTextPosition() !=99){
							   columnTableAll += col.getColumnName()+",";
							}
						} catch (Exception e) {
							//log.debug(lineStr);
							throw e;
						}
					}//if 2
				}//if 1
			}//while
			
			tableBean.setColumnBeanList(columnList);
			
			/** Set Display Column Log Result **/
			columnTableAll = Utils.isNull(columnTableAll);
			tableBean.setColumnTableAll(!columnTableAll.equals("")? columnTableAll.substring(0,columnTableAll.length()-1):"");
			
			return tableBean;
		} finally {
			FileUtil.close(br);
		}
	}
	
	
  public static TableBean genPrepareSQL(TableBean tableBean,User userBean,String transType) throws Exception{
	  if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
		   tableBean = genPrepareSQLUTS(tableBean,userBean);
	  }else  if(Constants.TRANSACTION_WEB_MEMBER_TYPE.equals(transType)){
		   tableBean = genPrepareWebMemberSQL(tableBean,userBean);
	  }else { 
		   tableBean = genPrepareSQL(tableBean,userBean);
	  }
	  return tableBean;
  }
  
	/**
	 * Get PrepareSQL
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static TableBean  genPrepareSQL(TableBean tableBean,User userBean) throws Exception{
	    String columnInsSql = "";
	    String valueInsSQL = "";
	    String updateSQL = "";
	    String deleteSQL = "";
        List<ColumnBean> orderUpdateList = new ArrayList<ColumnBean>();
        List<ColumnBean> columnKeyList = new ArrayList<ColumnBean>();
        List<ColumnBean> columnDeleteKeyList = new ArrayList<ColumnBean>();
		try{
			String tableName = tableBean.getTableName();
			logger.debug("********GenSQL*********************");
			/** Case User SALES IMPORT AD_USER FROM SALE_CENTER **/
			if(tableName.equalsIgnoreCase("authen")){
				tableName = "ad_user";
			}
		
		    for ( int i = 0; i < tableBean.getColumnBeanList().size(); i++) {   
		    	ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
	    	   
		    	if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y'),";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y'),";
				      }

		    	}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
				      }
		        }else{
			           columnInsSql += colBean.getColumnName() +",";
					   valueInsSQL +=" ?,";
					   if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
					       updateSQL += colBean.getColumnName()+" = ? ,";
					   }
		        }
		    	/** Column Key Update **/
		    	if(colBean.getKey().equals("Y")){
		    		columnKeyList.add(colBean); 	
		        }
		    	/*** Column Can be Update **/
		    	if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
		    		//logger.debug("Add UpdateList");
	    	    	orderUpdateList.add(colBean);
	    	    }
		    	/** Column Delete key **/
		    	if(colBean.getKey().equals("Y") && colBean.getAction().indexOf("D") !=-1){
		    		columnDeleteKeyList.add(colBean); 	
		        }
		    	
		    }//for
		    
		    
		    if( !Utils.isNull(columnInsSql).equals("")){
		    	columnInsSql = columnInsSql.substring(0,columnInsSql.length()-1);
		    }
		    if( !Utils.isNull(valueInsSQL).equals("")){
		    	valueInsSQL = valueInsSQL.substring(0,valueInsSQL.length()-1);
		    }
		    
		    /** prepare Insert Statement **/
		    String prepareSqlIns = "INSERT INTO "+tableName+"("+columnInsSql+") VALUES ("+valueInsSQL+")";
		  
		    
		    if( !Utils.isNull(updateSQL).equals("")){
		    	updateSQL = "UPDATE "+tableName+" SET "+updateSQL.substring(0,updateSQL.length()-1) +" WHERE 1=1 ";
		    }
		    
		    /** Add Key Column to Where Update SQL **/ 
		    if(columnKeyList != null && columnKeyList.size() >0){
			    for ( int i = 0; i < columnKeyList.size(); i++) {   
			    	ColumnBean colBean = (ColumnBean)columnKeyList.get(i);
		    	    if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
		    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
		    	    }else{
					    updateSQL += " AND "+ colBean.getColumnName()+" = ? ";
			        }
		    	    orderUpdateList.add(colBean);
			    }//for 
		    }//if
		    
		    
		    /** Gen Delete SQL **/
		    if(Utils.isNull(tableBean.getActionDB()).indexOf("D") != -1){
			    deleteSQL = "DELETE FROM "+tableBean.getTableName() +" WHERE 1=1";
			    if(columnDeleteKeyList != null && columnDeleteKeyList.size() >0 ){
			    	  for ( int i = 0; i < columnDeleteKeyList.size(); i++) {   
			    		  ColumnBean colBean = (ColumnBean)columnDeleteKeyList.get(i);
			    		  if(colBean.getColumnType().equalsIgnoreCase("DATE")){
			    			  deleteSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
				    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				    	    	deleteSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
				    	    }else{
				    	    	deleteSQL += " AND "+ colBean.getColumnName()+" = ? ";
					        }
			    	  }
			    }
		    }
		    
		    
		    tableBean.setPrepareSqlIns(prepareSqlIns);
		    tableBean.setPrepareSqlUpd(updateSQL);
		    tableBean.setPrepareSqlDelete(deleteSQL);
		    
		    /** Set For Order Update SQL */
		    tableBean.setColumnBeanOrderUpdateList(orderUpdateList);
		    /** Set For Order Delete SQL */
		    tableBean.setColumnBeanDeleteList(columnDeleteKeyList);
		    
		    return tableBean;
		}catch(Exception e){

			throw e;
		}finally{
			
		}
	}
	
	public static TableBean  genPrepareWebMemberSQL(TableBean tableBean,User userBean) throws Exception{
	    String columnInsSql = "";
	    String valueInsSQL = "";
	    String updateSQL = "";
	    String deleteSQL = "";
        List<ColumnBean> orderUpdateList = new ArrayList<ColumnBean>();
        List<ColumnBean> columnKeyList = new ArrayList<ColumnBean>();
        List<ColumnBean> columnDeleteKeyList = new ArrayList<ColumnBean>();
		try{
			String tableName = tableBean.getTableName();
			logger.debug("********GenSQL*********************");
		    for ( int i = 0; i < tableBean.getColumnBeanList().size(); i++) {   
		    	ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
	    	   
		    	if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y'),";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y'),";
				      }

		    	}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
				      }
		        }else{
			           columnInsSql += colBean.getColumnName() +",";
					   valueInsSQL +=" ?,";
					   if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
					       updateSQL += colBean.getColumnName()+" = ? ,";
					   }
		        }
		    	/** Column Key Update **/
		    	if(colBean.getKey().equals("Y")){
		    		columnKeyList.add(colBean); 	
		        }
		    	/*** Column Can be Update **/
		    	if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
		    		//logger.debug("Add UpdateList");
	    	    	orderUpdateList.add(colBean);
	    	    }
		    	/** Column Delete key **/
		    	if(colBean.getKey().equals("Y") && colBean.getAction().indexOf("D") !=-1){
		    		columnDeleteKeyList.add(colBean); 	
		        }
		    	
		    }//for
		    
		    
		    if( !Utils.isNull(columnInsSql).equals("")){
		    	columnInsSql = columnInsSql.substring(0,columnInsSql.length()-1);
		    }
		    if( !Utils.isNull(valueInsSQL).equals("")){
		    	valueInsSQL = valueInsSQL.substring(0,valueInsSQL.length()-1);
		    }
		    
		    /** prepare Insert Statement **/
		    String prepareSqlIns = "INSERT INTO "+tableName+"("+columnInsSql+") VALUES ("+valueInsSQL+")";
		  
		    if( !Utils.isNull(updateSQL).equals("")){
		    	updateSQL = "UPDATE "+tableName+" SET "+updateSQL.substring(0,updateSQL.length()-1) +" WHERE 1=1 ";
		    }
		    
		    /** Add Key Column to Where Update SQL **/ 
		    if(columnKeyList != null && columnKeyList.size() >0){
			    for ( int i = 0; i < columnKeyList.size(); i++) {   
			    	ColumnBean colBean = (ColumnBean)columnKeyList.get(i);
		    	    if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
		    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
		    	    }else{
					    updateSQL += " AND "+ colBean.getColumnName()+" = ? ";
			        }
		    	    orderUpdateList.add(colBean);
			    }//for 
		    }//if
		    
		    
		    /** Gen Delete SQL **/
		    if(Utils.isNull(tableBean.getActionDB()).indexOf("D") != -1){
			    deleteSQL = "DELETE FROM "+tableBean.getTableName() +" WHERE 1=1";
			    if(columnDeleteKeyList != null && columnDeleteKeyList.size() >0 ){
			    	  for ( int i = 0; i < columnDeleteKeyList.size(); i++) {   
			    		  ColumnBean colBean = (ColumnBean)columnDeleteKeyList.get(i);
			    		  if(colBean.getColumnType().equalsIgnoreCase("DATE")){
			    			  deleteSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
				    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				    	    	deleteSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
				    	    }else{
				    	    	deleteSQL += " AND "+ colBean.getColumnName()+" = ? ";
					        }
			    	  }
			    }
		    }
		    
		    
		    tableBean.setPrepareSqlIns(prepareSqlIns);
		    tableBean.setPrepareSqlUpd(updateSQL);
		    tableBean.setPrepareSqlDelete(deleteSQL);
		    
		    /** Set For Order Update SQL */
		    tableBean.setColumnBeanOrderUpdateList(orderUpdateList);
		    /** Set For Order Delete SQL */
		    tableBean.setColumnBeanDeleteList(columnDeleteKeyList);
		    
		    return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	/**
	 * Get PrepareSQL
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static TableBean  genPrepareSQLUTS(TableBean tableBean,User user) throws Exception{
	    String columnInsSql = "";
	    String valueInsSQL = "";
	    String updateSQL = "";
	    String updateSQLCS = "";
	    List<ColumnBean> insertColumnList = new ArrayList<ColumnBean>();
        List<ColumnBean> orderUpdateList = new ArrayList<ColumnBean>();
        List<ColumnBean> orderUpdateCSList = new ArrayList<ColumnBean>();
        List<ColumnBean> columnKeyList = new ArrayList<ColumnBean>();
		try{
			String tableName = tableBean.getTableName();
			if(tableName.equalsIgnoreCase("t_order_orcl")){
				tableName = "t_order";
			}
			if(tableName.equalsIgnoreCase("t_order_line_orcl")){
				tableName = "t_order_line";
			}
			
			logger.debug("********GenSQL*********************");
		    for ( int i = 0; i < tableBean.getColumnBeanList().size(); i++) {   
		    	ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
		    	if(colBean.getRoleAction().indexOf(user.getType()) != -1){
			    	if(colBean.getColumnType().equalsIgnoreCase("DATE")){
			    		  if(colBean.getAction().indexOf("I") !=-1){
			    		    columnInsSql += colBean.getColumnName() +",";
					        valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y'),";
			    		  }
					      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
					         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y'),";
					      }
					      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("S") !=-1){
						      updateSQLCS += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y'),";
						  }
			    	}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
			    		  if(colBean.getAction().indexOf("I") !=-1){
			    		    columnInsSql += colBean.getColumnName() +",";
					        valueInsSQL +=" STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
			    		  }
					      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
					         updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
					      }
					      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("S") !=-1){
						     updateSQLCS += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
						  }
			        }else{
			        	 if(colBean.getAction().indexOf("I") !=-1){
		        	       columnInsSql += colBean.getColumnName() +",";
					       valueInsSQL +=" ?,";
			        	 }
			        	 
					     if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
					        updateSQL += colBean.getColumnName()+" = ? ,";
					     }
					     
					     if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("S") !=-1){
						    updateSQLCS += colBean.getColumnName()+" = ? ,";
						 }
			        }
			    	
			    	if(colBean.getAction().indexOf("I") !=-1){
			    		insertColumnList.add(colBean);
			    	}
			    	
			    	if(colBean.getKey().equals("Y")){
			    		columnKeyList.add(colBean); 	
			        }
			    	if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
		    	    	orderUpdateList.add(colBean);
		    	    }
			    	if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("S") !=-1){
		    	    	orderUpdateCSList.add(colBean);
		    	    }
		    	}// if Roles	
		    }//for
		    
		    if( !Utils.isNull(columnInsSql).equals("")){
		    	columnInsSql = columnInsSql.substring(0,columnInsSql.length()-1);
		    }
		    if( !Utils.isNull(valueInsSQL).equals("")){
		    	valueInsSQL = valueInsSQL.substring(0,valueInsSQL.length()-1);
		    }
		    
		    /** prepare Insert Statement **/
		    String prepareSqlIns = "";
		    if( !"".equals(columnInsSql)){
		        prepareSqlIns = "INSERT INTO "+tableName+"("+columnInsSql+") VALUES ("+valueInsSQL+")";
		    }
		    
		    if( !Utils.isNull(updateSQL).equals("")){
		    	updateSQL =  "UPDATE "+tableName+" SET UPDATED = curdate(),updated_by ="+user.getId()+" ,"+updateSQL.substring(0,updateSQL.length()-1) +" WHERE 1=1 ";
		    }else{
		    	updateSQL =  "UPDATE "+tableName+" SET UPDATED = curdate(),updated_by ="+user.getId()+"  WHERE 1=1 ";
		    }
		    
		    if( !Utils.isNull(updateSQLCS).equals("")){
		    	updateSQLCS =  "UPDATE "+tableName+" SET UPDATED = curdate(),updated_by ="+user.getId()+" ,"+updateSQLCS.substring(0,updateSQLCS.length()-1) +" WHERE 1=1 ";
		    }else{
		    	updateSQLCS =  "UPDATE "+tableName+" SET UPDATED = curdate(),updated_by ="+user.getId()+"  WHERE 1=1 ";
		    }
		    
		    /** Add Key Column to Where Update SQL **/ 
		    if(columnKeyList != null && columnKeyList.size() >0){
			    for ( int i = 0; i < columnKeyList.size(); i++) {   
			    	ColumnBean colBean = (ColumnBean)columnKeyList.get(i);
		    	    if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
				        updateSQLCS += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y')";
		    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				        updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
				        updateSQLCS += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
		    	    }else{
					    updateSQL += " AND "+ colBean.getColumnName()+" = ? ";
					    updateSQLCS += " AND "+ colBean.getColumnName()+" = ? ";
			        }
		    	   // logger.debug("Add Keylist");
		    	    orderUpdateList.add(colBean);
		    	    orderUpdateCSList.add(colBean);
			    }//for 
		    }//if
		   
		    tableBean.setPrepareSqlUpd(updateSQL);
            tableBean.setPrepareSqlIns(prepareSqlIns);
            /** set Order Insert Column List */
            tableBean.setColumnBeanList(insertColumnList);
		    /** Set For Order Update SQL */
		    tableBean.setColumnBeanOrderUpdateList(orderUpdateList);
		    
		    /** Set For Order Update Case Special (t_order_line) **/
		    tableBean.setColumnBeanOrderUpdateCSList(orderUpdateCSList);
		    tableBean.setPrepareSqlUpdCS(updateSQLCS);
		    
		    return tableBean;
		}catch(Exception e){

			throw e;
		}finally{
			
		}
	}
	
	/**
	 * spiltLineStrToInsertStatement
	 * @param conn
	 * @param tableBean
	 * @param lineStr
	 * @param ps
	 * @throws Exception
	 * DESC : Split LineStr and Set to PrepareStatment Object Insert
	 */
	public static  PreparedStatement spiltLineArrayToInsertStatement(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean) throws Exception{
		int i=0;
		try{
			String[] lineArray = (lineStr+" ").split(Constants.delimeterPipe);
			int parameterIndex = 1;
			for(i=0;i<tableBean.getColumnBeanList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
	    		
	    		//DEBUG 
	    		if(colBean.getTextPosition() >= lineArray.length ){
	    			//logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					//logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}		
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
			    	String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	    	
			    	//logger.info("External Function["+colBean.getExternalFunction()+"] Result ID["+idFind+"]");
			    	ps =setObjectPS(ps,colBean,parameterIndex,idFind);
			    }else{
			    	if(colBean.getTextPosition() >= lineArray.length ){/** Text Position > Array Index  set To NUll **/
			    	   ps =setObjectPS(ps,colBean,parameterIndex,"");
			    	}else{
			    	   ps =setObjectPS(ps,colBean,parameterIndex,Utils.isNull(lineArray[colBean.getTextPosition()]));
			    	}
			    }
	    		parameterIndex++;
			}//for 

		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	
	public static  PreparedStatement spiltLineArrayToInsertStatement(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean,ColumnBean[] replaceValues) throws Exception{
		int i=0;
		try{
			String[] lineArray = (lineStr+" ").split(Constants.delimeterPipe);
			int parameterIndex = 1;
			for(i=0;i<tableBean.getColumnBeanList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
	    		
	    		//DEBUG 
	    		if(colBean.getTextPosition() >= lineArray.length ){
	    			//logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					//logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}		
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
			    	String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	    	
	    			logger.debug("External Function Result ID["+idFind+"]");
			    	ps =setObjectPS(ps,colBean,parameterIndex,idFind);
			    }else{
			    	if(colBean.getTextPosition() >= lineArray.length ){/** Text Position > Array Index  set To NUll **/
			    		String value = "";
				        for(int r=0;r<replaceValues.length;r++){
				    	  ColumnBean replaceBean = replaceValues[r];
				    	  if(replaceBean.getColumnName().equalsIgnoreCase(colBean.getColumnName())){
				    		  value =  replaceBean.getDefaultValue();
				    		  break;
				    	  }
				        }//for
			    		ps =setObjectPS(ps,colBean,parameterIndex,value);
			    	}else{
			    	   /** Replace Value By Manaual **/
				       String value = Utils.isNull(lineArray[colBean.getTextPosition()]);
				       for(int r=0;r<replaceValues.length;r++){
				    	  ColumnBean replaceBean = replaceValues[r];
				    	  if(replaceBean.getColumnName().equalsIgnoreCase(colBean.getColumnName())){
				    		  value =  replaceBean.getDefaultValue();
				    		  break;
				    	  }
				       }//for
				       
				       ps = setObjectPS(ps,colBean,parameterIndex,value);
			    	}
			    }
	    		parameterIndex++;
			}//for 

		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	
	/**
	 * spiltlineArraytrToUpdateStatement
	 * @param conn
	 * @param tableBean
	 * @param lineStr
	 * @param ps
	 * @throws Exception
	 * DESC : Split LineStr and Set to PrepareStatment Object Fro Update
	 */
	public static  PreparedStatement spiltLineArrayToUpdateStatement(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean) throws Exception{
		int i=0;
		try{
			lineStr += " "; /** case last String | **/
			String[] lineArray = lineStr.split(Constants.delimeterPipe);
			int parameterIndex = 1;
			//logger.debug("UpdateList Size:"+tableBean.getColumnBeanOrderUpdateList().size());
			for(i=0;i<tableBean.getColumnBeanOrderUpdateList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanOrderUpdateList().get(i);
	    		
	    		//DEBUG 
	    		if(colBean.getTextPosition() >= lineArray.length ){
	    			//logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					//logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}	
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	  
	    			//logger.info("External Function["+colBean.getExternalFunction()+"] Result ID["+idFind+"]");
			    	ps = setObjectPS(ps,colBean,parameterIndex,idFind);
				}else{
					if(colBean.getTextPosition() >= lineArray.length ){/** Text Position > Array Index OF LineStr  set To NULL **/
				       ps =setObjectPS(ps,colBean,parameterIndex,"");
				    }else{
				       ps =setObjectPS(ps,colBean,parameterIndex,Utils.isNull(lineArray[colBean.getTextPosition()]));
				    }
				 }
		        parameterIndex++;
			}//for
		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	
	
	
	public static  PreparedStatement spiltLineArrayToUpdateStatement(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean,ColumnBean[] replaceValues) throws Exception{
		int i=0;
		try{
			lineStr += " "; /** case last String | **/
			String[] lineArray = lineStr.split(Constants.delimeterPipe);
			int parameterIndex = 1;
			for(i=0;i<tableBean.getColumnBeanOrderUpdateList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanOrderUpdateList().get(i);
	    		
	    		//DEBUG 
	    		if(colBean.getTextPosition() >= lineArray.length ){
	    			//logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					//logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}	
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	  
			    	ps = setObjectPS(ps,colBean,parameterIndex,idFind);
				}else{
					if(colBean.getTextPosition() >= lineArray.length ){/** Text Position > Array Index OF LineStr  set To NULL **/
						String value = "";
				        for(int r=0;r<replaceValues.length;r++){
				    	  ColumnBean replaceBean = replaceValues[r];
				    	  if(replaceBean.getColumnName().equalsIgnoreCase(colBean.getColumnName())){
				    		  value =  replaceBean.getDefaultValue();
				    		  break;
				    	  }
				        }//for
			    		ps =setObjectPS(ps,colBean,parameterIndex,value);
				    }else{
				    	/** Replace Value By Manaual **/
				       String value = Utils.isNull(lineArray[colBean.getTextPosition()]);
				       for(int r=0;r<replaceValues.length;r++){
				    	  ColumnBean replaceBean = replaceValues[r];
				    	  if(replaceBean.getColumnName().equalsIgnoreCase(colBean.getColumnName())){
				    		  value =  replaceBean.getDefaultValue();
				    		  break;
				    	  }
				       }//for
				       
				       ps = setObjectPS(ps,colBean,parameterIndex,value);
				    }
				 }
		        parameterIndex++;
			}//for
		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	
	/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @param lineStr
	 * @param ps
	 * @param userBean
	 * @return
	 * @throws Exception
	 * Case Update Special
	 */
	public static  PreparedStatement spiltLineArrayToUpdateStatementCS(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean) throws Exception{
		int i=0;
		try{
			lineStr += " "; /** case last String | **/
			String[] lineArray = lineStr.split(Constants.delimeterPipe);
			int parameterIndex = 1;
			//logger.debug("UpdateList Size:"+tableBean.getColumnBeanOrderUpdateList().size());
			for(i=0;i<tableBean.getColumnBeanOrderUpdateCSList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanOrderUpdateCSList().get(i);
	    		
	    		//DEBUG 
	    		if(colBean.getTextPosition() >= lineArray.length ){
	    			logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					logger.debug("i["+i+"]Update Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}	
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	  
			    	ps = setObjectPS(ps,colBean,parameterIndex,idFind);
				}else{
				    ps = setObjectPS(ps,colBean,parameterIndex,Utils.isNull(lineArray[colBean.getTextPosition()]));
				 }
		        parameterIndex++;
			}//for
		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	/**
	 * spiltlineArraytrToDeleteStatement
	 * @param conn
	 * @param tableBean
	 * @param lineStr
	 * @param ps
	 * @param userBean
	 * @throws Exception
	 */
	public static  PreparedStatement spiltLineArrayToDeleteStatement(Connection conn,TableBean tableBean,String lineStr,PreparedStatement ps,User userBean) throws Exception{
		int i=0;
		try{
			lineStr += " "; /** case last String | **/
			String[] lineArray = lineStr.split(Constants.delimeterPipe);
			int parameterIndex = 1;
			for(i=0;i<tableBean.getColumnBeanDeleteList().size();i++){
	    		ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanDeleteList().get(i);
	    		
	    		/*if(colBean.getTextPosition() >= lineArray.length ){
	    			logger.debug("i["+i+"]Delete Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					logger.debug("i["+i+"]Delete Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]Ext["+colBean.getExternalFunction()+"]valid["+colBean.getValidate()+"]");
				}	*/
	    		
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn, colBean,lineArray,userBean);	 
	    			//logger.debug("External Function Result ID["+idFind+"]");
			    	ps =setObjectPS(ps,colBean,parameterIndex,idFind);
				}else{
				    ps =setObjectPS(ps,colBean,parameterIndex,Utils.isNull(lineArray[colBean.getTextPosition()]));
				 }
		        parameterIndex++;
			}//for
		}catch(Exception e){
			throw e;
		}
		return ps;
	}
	
	/**
	 * setObjectPS
	 * @param ps
	 * @param colBean
	 * @param parameterIndex
	 * @param value
	 * @throws Exception
	 * Desc: Set Object By Type of Column Value
	 */
	private static PreparedStatement setObjectPS(PreparedStatement ps ,ColumnBean colBean,int parameterIndex, String value) throws Exception{
		
		if(colBean.getColumnType().equalsIgnoreCase("DATE")){
			if(!Utils.isNull(value).equals("")){
		        ps.setString(parameterIndex,Utils.isNull(value));
			}else{
				ps.setString(parameterIndex,null);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("DECIMAL")){
			if(!Utils.isNull(value).equals("")){
				ps.setDouble(parameterIndex, ConvertUtils.convertToDouble(value));
			}else{
			//	logger.debug("NULL DECIMAL:"+ConvertUtils.convertToBigDecimal(value));
				ps.setNull(parameterIndex,java.sql.Types.DECIMAL);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("INTEGER")){
			if(!Utils.isNull(value).equals("")){
				ps.setInt(parameterIndex,ConvertUtils.convertToInt(value));
			}else{
			    ps.setNull(parameterIndex,java.sql.Types.INTEGER);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
			if(!Utils.isNull(value).equals("")){ 
				ps.setString(parameterIndex,value);
			}else{
			    ps.setString(parameterIndex,null);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("VARCHAR")){
			if(!Utils.isNull(value).equals("")){
				//ps.setString(parameterIndex, Utils.ASCIIToUnicode(ConvertUtils.convertToString(value)));
				ps.setString(parameterIndex, ConvertUtils.convertToString(value));
			}else{
				if( !Utils.isNull(colBean.getDefaultValue()).equals("")){
					if (Utils.isNull(colBean.getDefaultValue()).equals(Constants.COLUMN_BLANK)){
				        ps.setString(parameterIndex,Constants.INSERT_STR_DEFAULT_BLANK);	
					}else{
						ps.setString(parameterIndex,colBean.getDefaultValue());	
					}
				}else{
				   ps.setNull(parameterIndex,java.sql.Types.VARCHAR);	
				  // ps.setString(parameterIndex,"");	
				}
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("CHAR")){
			if(!Utils.isNull(value).equals("")){
				//ps.setString(parameterIndex, Utils.ASCIIToUnicode(ConvertUtils.convertToString(value)) );
				ps.setString(parameterIndex, ConvertUtils.convertToString(value));
			}else{
				
				if( !Utils.isNull(colBean.getDefaultValue()).equals("")){
					if (Utils.isNull(colBean.getDefaultValue()).equals(Constants.COLUMN_BLANK)){
				        ps.setString(parameterIndex,Constants.INSERT_STR_DEFAULT_BLANK);	
					}else{
						ps.setString(parameterIndex,colBean.getDefaultValue());	
					}
				}else{
				   ps.setNull(parameterIndex,java.sql.Types.CHAR);	
				  // ps.setString(parameterIndex,"");	
				}
			}
		}else{
			logger.debug("Nothing");
			ps.setString(parameterIndex,null);	
		}
		return ps;
	}
	
	
	
	
	
	/**
	 * canGetFtpFile
	 * @param user
	 * @param transType
	 * @param tableBean
	 * @param fileFtpFullName
	 * @param importAll
	 * @return
	 * @throws Exception
	 * DESC: Compare file Name with FTP Server 
	 * tableBean.getFileFtpName() -> name file on FTP Server
	 */
	public static boolean canGetFtpFile(User user,String transType,TableBean tableBean,String fileFtpFullName,boolean importAll) {
		boolean canGetFtpFile = false;
		try{
			//logger.debug("TransType:"+transType);
			if(Constants.TRANSACTION_UTS_TRANS_TYPE.equalsIgnoreCase(transType)){
				if( fileFtpFullName.indexOf(tableBean.getFileFtpName()) != -1 ){
					if(ImportHelper.isCanGetFtpFileBySalesCode(user, tableBean, fileFtpFullName,importAll)){
						canGetFtpFile = true;
					}else{
						canGetFtpFile = false;
					}
				}
			}else if(Constants.TRANSACTION_WEB_MEMBER_TYPE.equalsIgnoreCase(transType)){
					if( fileFtpFullName.indexOf(tableBean.getFileFtpName()) != -1 ){
						if(ImportHelper.isCanGetFtpFileBySalesCode(user, tableBean, fileFtpFullName,importAll)){
							canGetFtpFile = true;
						}else{
							canGetFtpFile = false;
						}
					}
			}else{
				
				if(tableBean.getTableName().equalsIgnoreCase("m_inventory_onhand")){
				   if(    fileFtpFullName.indexOf("VISIT") == -1
					   && fileFtpFullName.indexOf("ORDER") == -1
					   && fileFtpFullName.indexOf("RECEIPT") == -1 
					   && fileFtpFullName.indexOf("CN") == -1  ){
						
				    	if(ImportHelper.isCanGetFtpFileCaseSubInv(tableBean, fileFtpFullName,importAll)){
		            		canGetFtpFile = true;
						}else{
							canGetFtpFile = false;
						}
				    }
				}else 	if(tableBean.getTableName().equalsIgnoreCase("t_credit_note")){
					if(fileFtpFullName.indexOf(tableBean.getFileFtpName()) != -1){
						if(ImportHelper.isCanGetFtpFileBySalesCode(user, tableBean, fileFtpFullName,importAll)){
							canGetFtpFile = true;
						}else{
							canGetFtpFile = false;
						}
				    }
				}else if(tableBean.getTableName().equalsIgnoreCase("m_customer")
						|| tableBean.getTableName().equalsIgnoreCase("m_address")
						|| tableBean.getTableName().equalsIgnoreCase("m_contact")
						|| tableBean.getTableName().equalsIgnoreCase("m_trip")
						|| tableBean.getTableName().equalsIgnoreCase("m_sales_target_new")
						|| tableBean.getTableName().equalsIgnoreCase("m_member_health")){
					
					//logger.debug("fileFtpFullName:"+fileFtpFullName);
					if( fileFtpFullName.indexOf(tableBean.getFileFtpName()) != -1 ){
						if(User.ADMIN.equals(user.getType())){
							if(ImportHelper.isCanGetFtpFileBySalesCodeCaseAdmin(user, tableBean, fileFtpFullName,importAll)){
								canGetFtpFile = true;
							}else{
								canGetFtpFile = false;
							}
						}else{
							if(ImportHelper.isCanGetFtpFileBySalesCode(user, tableBean, fileFtpFullName,importAll)){
								canGetFtpFile = true;
							}else{
								canGetFtpFile = false;
							}
						}
					}//if
					/** Case Normal Master not in( CUST,CUSTADDR,CUSTCONTACT,TRIP,CN,m_sales_target_new)**/
				}else{
					if( fileFtpFullName.indexOf(tableBean.getFileFtpName()) != -1 ){
						    if(ImportHelper.isCanGetFtpFile(tableBean, fileFtpFullName,importAll)){
			        		    canGetFtpFile = true;
				        	}else{
				        		canGetFtpFile = false;
				        	}
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return canGetFtpFile;
	}
	
	/**
	 * 
	 * @param lastDateFileImport
	 * @param tableName
	 * @param ftpFileFullName
	 * @return
	 * @throws Exception
	 * lastDateFileImport :for check read ftp file
	 * tableName : for mapping ftp file
	 * ftpFileFullName : file name full in FTP Server ex, -> 1). 201010311010-UOM.txt  ,2).201012081631-PROLINE-ALL.txt
	 */
	public static boolean isCanGetFtpFile(TableBean tableBean, String ftpFileFullName,boolean importAll){
		//logger.debug("isCanGetFtpFile");
		boolean map = false;
		try{
			long ftpFileVesrionInt1 = 0;
			long salesFileLastVersionInt = 0;
			
			String salesFileNameLastImport = tableBean.getFileNameLastImport(); //for checkDate
			String salesFileMappingName = tableBean.getFileNameMapping();
			if( !Utils.isNull(salesFileNameLastImport).equals("")){
				salesFileLastVersionInt = Long.parseLong(salesFileNameLastImport.split("\\-")[0]);
			}
			
			/** Case Import File ALL  */
			if(importAll && ftpFileFullName.indexOf("-ALL") != -1){
				/** 201012081631-PROLINE-ALL.txt  */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]);
				String ftpFileTableName2 = ftpFileFullNameArr[1];
				String ftpFileTableName3 = "";
				if(ftpFileFullNameArr != null && ftpFileFullNameArr.length ==3){
					ftpFileTableName3 = ftpFileFullNameArr[2];
					if(ftpFileTableName3.indexOf(".") != -1){
						ftpFileTableName3 = ftpFileTableName3.substring(0,ftpFileTableName3.indexOf("."));
					}
				}
				/*logger.debug("*******Compare File Get FTP FIle Name Case Import ALL*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName2);
				logger.debug("All:ALL:"+ftpFileTableName3);	
				logger.debug("********************************************************");*/
				if(     ftpFileVesrionInt1 >salesFileLastVersionInt
						&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName2)
						&& Constants.FTP_FILE_NAME_ALL.equalsIgnoreCase(ftpFileTableName3) ){
					map = true;
				}
			}else if( !importAll && ftpFileFullName.indexOf("-ALL") == -1){
				/**201010311010-UOM.txt  */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]); //201010311010 
				String ftpFileTableName2 = ftpFileFullNameArr[1];//UOM
				
				if(ftpFileTableName2.indexOf(".") != -1){
					ftpFileTableName2 = ftpFileTableName2.substring(0,ftpFileTableName2.indexOf("."));
				}
				/*logger.debug("*******Compare File Get FTP FIle Name*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName2);
				logger.debug("********************************************************");*/
				if(ftpFileVesrionInt1 >salesFileLastVersionInt 
					&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName2)){
					map = true;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return map;

	}
	
	/**
	 * 
	 * @param lastDateFileImport
	 * @param tableName
	 * @param ftpFileFullName
	 * @return
	 * @throws Exception
	 * lastDateFileImport :for check read ftp file
	 * tableName : for mapping ftp file
	 * ftpFileFullName : file name full in FTP Server ex, -> Case 1) 201010311010-100040-RECEIPT.txt ,Case 2).201010311010-100040-RECEIPT-ALL.txt
	 */
	public static boolean isCanGetFtpFileBySalesCode(User user,TableBean tableBean, String ftpFileFullName,boolean importAll) {
		//logger.debug("isCanGetFtpFileBySalesCode");
		boolean map = false;
		try{
			long ftpFileVesrionInt1 = 0;
			long salesFileLastVersionInt = 0;
			String salesFileNameLastImport = tableBean.getFileNameLastImport(); //for checkDate
			String salesFileMappingName = tableBean.getFileNameMapping();
			if( !Utils.isNull(salesFileNameLastImport).equals("")){
				salesFileLastVersionInt = Long.parseLong(salesFileNameLastImport.split("\\-")[0]); //201010311010
			}
			
			/** Case Import File ALL  */
			if(importAll && ftpFileFullName.indexOf("-ALL") != -1){
				/** 201010311010-100040-CUST-ALL.txt */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]);//201010311010
				String ftpFileTableName2 = ftpFileFullNameArr[1];//100040 userId
				String ftpFileTableName3 = ftpFileFullNameArr.length>=3?ftpFileFullNameArr[2]:"";//CUST tableName
				String ftpFileTableName4 = "";//ALL
				if(ftpFileFullNameArr != null && ftpFileFullNameArr.length ==4){
					ftpFileTableName4 = ftpFileFullNameArr[3];//ALL
					if(ftpFileTableName4.indexOf(".") != -1){
						ftpFileTableName4 = ftpFileTableName4.substring(0,ftpFileTableName4.indexOf("."));
					}
				}
				String userId = user.getUserName();//user.getId();//Chang to use SalesCode V101
				
				/*logger.debug("*******Compare File Get FTP FIle Name Case Import ALL*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("userId:"+user.getId()+":"+ftpFileTableName2);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName3);
				logger.debug("All:ALL:"+ftpFileTableName4);	
				logger.debug("********************************************************");*/
				if(     ftpFileVesrionInt1 >salesFileLastVersionInt
						&& (userId).equals(ftpFileTableName2)
						&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName3)
						&& Constants.FTP_FILE_NAME_ALL.equalsIgnoreCase(ftpFileTableName4) ){
					map = true;
				}
			}else if( !importAll && ftpFileFullName.indexOf("-ALL") == -1){
				/**201010311010-100040-CUST.txt */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]);//201010311010
				String ftpFileTableName2 = ftpFileFullNameArr[1];//100040 userId
				String ftpFileTableName3 = ftpFileFullNameArr.length>=3?ftpFileFullNameArr[2]:"";//CUST tableName
				if(ftpFileTableName3.indexOf(".") != -1){
					ftpFileTableName3 = ftpFileTableName3.substring(0,ftpFileTableName3.indexOf("."));
				}
				String userId = user.getUserName();//user.getId();//Chang to use SalesCode V101
				
				/*logger.debug("*******Compare File Get FTP FIle Name*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("userId:"+user.getId()+":"+ftpFileTableName2);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName3);
				logger.debug("********************************************************");*/
				if(ftpFileVesrionInt1 > salesFileLastVersionInt 
					&& (userId).equals(ftpFileTableName2)
					&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName3) ){
					map = true;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return map;
	}
	
	/**
	 * 
	 * @param user
	 * @param tableBean
	 * @param ftpFileFullName
	 * @param importAll
	 * @return
	 * @throws Exception
	 * 
	 * Case User Admin Not Check User_ID (customer,address,contact,trip)
	 */
	public static boolean isCanGetFtpFileBySalesCodeCaseAdmin(User user,TableBean tableBean, String ftpFileFullName,boolean importAll){
		logger.debug("isCanGetFtpFileBySalesCodeCaseAdmin");
		boolean map = false;
		try{
			long ftpFileVesrionInt1 = 0;
			long salesFileLastVersionInt = 0;
			String salesFileNameLastImport = tableBean.getFileNameLastImport(); //for checkDate
			String salesFileMappingName = tableBean.getFileNameMapping();
			if( !Utils.isNull(salesFileNameLastImport).equals("")){
				salesFileLastVersionInt = Long.parseLong(salesFileNameLastImport.split("\\-")[0]); //201010311010
			}
			
			logger.debug("importAll:"+importAll+",indexOF ALL :"+ftpFileFullName.indexOf("-ALL"));
			/** Case Import File ALL  */
			if(importAll && ftpFileFullName.indexOf("-ALL") != -1){
				/** 201010311010-100040-CUST-ALL.txt */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]);//201010311010
				String ftpFileTableName2 = ftpFileFullNameArr[1];//100040 userId
				String ftpFileTableName3 = ftpFileFullNameArr.length>=3?ftpFileFullNameArr[2]:"";//CUST tableName
				String ftpFileTableName4 = "";//ALL
				if(ftpFileFullNameArr != null && ftpFileFullNameArr.length ==4){
					ftpFileTableName4 = ftpFileFullNameArr[3];//ALL
					if(ftpFileTableName4.indexOf(".") != -1){
						ftpFileTableName4 = ftpFileTableName4.substring(0,ftpFileTableName4.indexOf("."));
					}
				}
				int userId = user.getId();
				
				logger.debug("*******Compare File Get FTP FIle Name Case Import ALL*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("userId:"+user.getId()+":"+ftpFileTableName2);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName3);
				logger.debug("All:ALL:"+ftpFileTableName4);	
				logger.debug("********************************************************");
				if(     ftpFileVesrionInt1 >salesFileLastVersionInt
						&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName3)
						&& Constants.FTP_FILE_NAME_ALL.equalsIgnoreCase(ftpFileTableName4) ){
					map = true;
				}
			}else if( !importAll && ftpFileFullName.indexOf("-ALL") == -1){
				/**201010311010-100040-CUST.txt */
				String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
				ftpFileVesrionInt1 = Long.parseLong(ftpFileFullNameArr[0]);//201010311010
				String ftpFileTableName2 = ftpFileFullNameArr[1];//100040 userId
				String ftpFileTableName3 = ftpFileFullNameArr.length>=3?ftpFileFullNameArr[2]:"";//CUST tableName
				if(ftpFileTableName3.indexOf(".") != -1){
					ftpFileTableName3 = ftpFileTableName3.substring(0,ftpFileTableName3.indexOf("."));
				}
				int userId = user.getId();
				
				logger.debug("*******Compare File Get FTP FIle Name*******************");
				logger.debug("lastImportInt:"+salesFileLastVersionInt+":"+ftpFileVesrionInt1);
				logger.debug("userId:"+user.getId()+":"+ftpFileTableName2);
				logger.debug("lastImportTableName:"+salesFileMappingName+":"+ftpFileTableName3);
				logger.debug("********************************************************");
				if(ftpFileVesrionInt1 > salesFileLastVersionInt 
					&& salesFileMappingName.equalsIgnoreCase(ftpFileTableName3) ){
					map = true;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return map;
	}
	
	/**
	 * isCanGetFtpFileCaseSubInv
	 * @param tableBean
	 * @param ftpFileFullName
	 * @return
	 * @throws Exception
	 * ‡∏™‡∏£. YYYYMMDDHHMM-SubInv-Sorlor.txt
       Van YYYYMMDDHHMM-SubInv-SalesCode.txt
	 * //201011011435-R001-Sorlor.txt
	   //201011031542-C644-C644.txt
	   //201011031542-R001-03-Sorlor-ALL.txt	
	   and check lastImport < ftpName[201011031542]
	 */
	public static boolean isCanGetFtpFileCaseSubInv(TableBean tableBean , String ftpFileFullName,boolean importAll){
		//logger.info("isCanGetFtpFileCaseSubInv");
		boolean map = false;
		try{
			long saleFileVersion1 = 0;
			long ftpFileVersion1 = 0;
			
			String saleFileName = tableBean.getFileNameLastImport(); //for version Date
			Map subInvMap = tableBean.getSubInvMap();
			Map userCodeMap = tableBean.getUserCodeMap();
			
			//logger.debug("subInvMap:"+subInvMap);
			//logger.debug("userCodeMap:"+userCodeMap);
			
	        if(importAll && ftpFileFullName.indexOf("-ALL") != -1){
	        	// 201011031542-R001-Sorlor-ALL.txt  Case1 length =4
	        	// 201011031542-R001-03-Sorlor-ALL.txt Case2 length =5
	        	//FTP FIle
	        	String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
	        	ftpFileVersion1 = Long.parseLong(ftpFileFullNameArr[0]);
				
				String subInvName2 = "";
				String userCodeName3 = "";
				String all4 = "";
				if(ftpFileFullNameArr.length ==4){	
					subInvName2 = ftpFileFullNameArr[1];//R001
					userCodeName3 = ftpFileFullNameArr[2];
					all4 = ftpFileFullNameArr[3];
				}else{
					subInvName2 = ftpFileFullNameArr[1]+"-"+ftpFileFullNameArr[2];//R001,Solor
					userCodeName3 = ftpFileFullNameArr[3];
					all4 = ftpFileFullNameArr[4];
			     }
			
				if(all4.indexOf(".") != -1){
					all4 = all4.substring(0,all4.indexOf("."));
				}
				
	            // Sales File
				if( !Utils.isNull(saleFileName).equals("")){
					saleFileVersion1 = Long.parseLong(saleFileName.split("\\-")[0]);
				}
				/*logger.debug("version:"+saleFileVersion1+":"+ftpFileVersion1);
				logger.debug("subInvName:"+subInvMap.get(subInvName2)+":"+subInvName2);
				logger.debug("userCodeName:"+userCodeMap.get(userCodeName3)+":"+userCodeName3);
				logger.debug("ALL:ALL:"+all4);*/
				/** Case ‡∏™‡∏£. YYYYMMDDHHMM-SubInv-Sorlor-ALL.txt */
				if(ftpFileFullName.indexOf(Constants.SUB_INV_SOLOR) != -1){
					if(        ftpFileVersion1 > saleFileVersion1 
							&& subInvMap.get(subInvName2) != null 
							&& userCodeName3.equals(Constants.SUB_INV_SOLOR)
							&& Constants.FTP_FILE_NAME_ALL.equals(all4)
							){
						
						map = true;
					}
				}else{
				/** Van YYYYMMDDHHMM-SubInv-SalesCode-ALL.txt */
					if( ftpFileVersion1 > saleFileVersion1 && subInvMap.get(subInvName2) != null ){
						if(userCodeMap.get(userCodeName3) != null && Constants.FTP_FILE_NAME_ALL.equals(all4)){
						  map = true;
						}
					}
				}
				logger.info("ResultMap:"+map);
				
	        }else if( !importAll && ftpFileFullName.indexOf("-ALL") == -1){
	        	// 201011031542-R001-Sorlor.txt  Case 1 length =3
	        	// 201011031542-R001-03-Sorlor.txt Case 2 length =4
	        	
	        	//FTP FIle
	        	String[] ftpFileFullNameArr = ftpFileFullName.split("\\-");
	        	ftpFileVersion1 = Long.parseLong(ftpFileFullNameArr[0]);
	        	String subInvName2 = "";
				String userCodeName3 = "";
				if(ftpFileFullNameArr.length ==3){	
					subInvName2 = ftpFileFullNameArr[1];//R001
					userCodeName3 = ftpFileFullNameArr[2];//Solor Or SalesCode
				}else{
					subInvName2 = ftpFileFullNameArr[1]+"-"+ftpFileFullNameArr[2];//R001-03
					userCodeName3 = ftpFileFullNameArr[3];//Solor Or SalesCode
			     }
			
				if(userCodeName3.indexOf(".") != -1){
					userCodeName3 = userCodeName3.substring(0,userCodeName3.indexOf("."));
				}
				
	            // Sales File
				if( !Utils.isNull(saleFileName).equals("")){
					saleFileVersion1 = Long.parseLong(saleFileName.split("\\-")[0]);
				}
				
			/*	logger.debug("version:"+saleFileVersion1+":"+ftpFileVersion1);
				logger.debug("subInvName:"+subInvMap.get(subInvName2)+":"+subInvName2);
				logger.debug("userCodeName:"+userCodeMap.get(userCodeName3)+":"+userCodeName3);*/
				
				/** Case ‡∏™‡∏£. YYYYMMDDHHMM-SubInv-Sorlor.txt */
				if(ftpFileFullName.indexOf(Constants.SUB_INV_SOLOR) != -1){
					if(        ftpFileVersion1 > saleFileVersion1 
							&& subInvMap.get(subInvName2) != null 
							&& userCodeName3.equals(Constants.SUB_INV_SOLOR)){
						
						map = true;
					}
				}else{
				/** Van YYYYMMDDHHMM-SubInv-SalesCode.txt */
					if( ftpFileVersion1 > saleFileVersion1 && subInvMap.get(subInvName2) != null ){
						if(userCodeMap.get(userCodeName3) != null){
						  map = true;
						}
					}
				}
				//logger.debug("ResultMap:"+map);
	        }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return map;
	}
	
}
