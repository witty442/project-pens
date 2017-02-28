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
import com.isecinc.pens.inf.manager.process.ExportOrderToICC;

public class InterfaceHelperWacoal extends InterfaceUtils{
	
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
	
	
	public static void initImportConfigWacoal(String path,String controlFileName,LinkedHashMap<String,TableBean> tableMap,Connection conn,String pathImport,User userBean,String[] tableNameSelects) throws Exception {
		BufferedReader br = FileUtil.getBufferReaderFromClassLoader(path+controlFileName); // Seq, Procedure, Source, Destination
		EnvProperties env = EnvProperties.getInstance();
		MonitorTime monitorTime = null;
		int count = 0;
		try {
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
							
							/** init table properties  */
							tableBean = initColumn(path,tableBean);
							/** Gen SQL Insert And Update***/
							tableBean = genPrepareSQL(tableBean,userBean);
							
							for(int i=0;i<tableNameSelects.length;i++){
								if(tableNameSelects[i].equalsIgnoreCase(tableBean.getTableName())){
									count++;
									/** Store Data Map **/
								    tableMap.put(tableBean.getTableName(),tableBean);
								    if(count ==tableNameSelects.length){
								       break;
								    }
								}
							}
						} catch (Exception e) {
							logger.info("TabelName Error:"+tableBean.getTableName());
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
							col.setTextLength(ConvertUtils.convertToInt(Utils.isNull(p[3])));
							col.setColumnType(Utils.isNull(p[4]));
							col.setDefaultValue(Utils.isNull(p[5]));
							col.setExternalFunction(Utils.isNull(p[6]));
							col.setKey(Utils.isNull(p[7]));

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
	  tableBean = genPrepareSQL(tableBean,userBean);
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
			logger.debug("********GenSQL["+tableName+"]*********************");
			
		    for ( int i = 0; i < tableBean.getColumnBeanList().size(); i++) {   
		    	ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
	    	    //logger.debug("ColumnName:"+colBean.getColumnName());
	    	    
		    	if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" TO_DATE(?,'dd/mm/yyyy'),";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				         updateSQL += colBean.getColumnName()+" = TO_DATE(?,'dd/mm/yyyy'),";
				      }

		    	}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				      columnInsSql += colBean.getColumnName() +",";
				      valueInsSQL +=" ?,";
				      
				      if(!colBean.getKey().equals("Y") && colBean.getAction().indexOf("U") !=-1){
				        // updateSQL += colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S'),";
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
				        updateSQL += " AND "+colBean.getColumnName()+" = TO_DATE(?,'dd/mm/yyyy')";
		    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				       // updateSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
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
			    			  deleteSQL += " AND "+colBean.getColumnName()+" = TO_DATE(?,'dd/mm/yyyy')";
				    	    }else  if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				    	    	//deleteSQL += " AND "+colBean.getColumnName()+" = STR_TO_DATE(?,'%d%m%Y %H%i%S')";
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
	    			logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value[]pos["+colBean.getTextPosition()+"]");
				}else{
					logger.debug("i["+i+"]Insert Col["+colBean.getColumnName()+"]Type["+colBean.getColumnType()+"]Value["+Utils.isNull(lineArray[colBean.getTextPosition()])+"]pos["+colBean.getTextPosition()+"]");
				}		
	    		
	    		/** Validate Column By Name **/
	    		if( !Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("N")){
	    			ValidateImportHelper.validate(conn, colBean, lineArray, userBean,tableBean.getTableName());
	    		}
	    		logger.debug("parameterIndex:"+parameterIndex);
	    		/** Set Value to Prepare Statement **/
	    		if( !Utils.isNull(colBean.getExternalFunction()).equals("N")){
			    	String idFind = ExternalFunctionHelper.findExternalFunc(conn,tableBean, colBean,lineArray,userBean);	    	
			    	logger.info("External Function["+colBean.getExternalFunction()+"] Result ID["+idFind+"]");
			    	
			    	ps = setObjectPS(ps,colBean,parameterIndex,idFind);
			    }else{
			    	if(colBean.getTextPosition() >= lineArray.length ){/** Text Position > Array Index  set To NUll **/
			    	   ps = setObjectPS(ps,colBean,parameterIndex,"");
			    	}else{
			    	   ps = setObjectPS(ps,colBean,parameterIndex,Utils.isNull(lineArray[colBean.getTextPosition()]));
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
			    	String idFind = ExternalFunctionHelper.findExternalFunc(conn, tableBean,colBean,lineArray,userBean);	    	
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
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn,tableBean, colBean,lineArray,userBean);	  
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
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn,tableBean, colBean,lineArray,userBean);	  
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
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn,tableBean, colBean,lineArray,userBean);	  
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
	    			String idFind = ExternalFunctionHelper.findExternalFunc(conn,tableBean, colBean,lineArray,userBean);	 
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
		        ps.setString(parameterIndex,value);
			}else{
				ps.setString(parameterIndex,null);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("DOUBLE")){
			
			if(!Utils.isNull(value).equals("")){
				ps.setDouble(parameterIndex, ConvertUtils.convertToDouble(value));
				
				//ps.setString(parameterIndex, (value));
			}else{
			//	logger.debug("NULL DECIMAL:"+ConvertUtils.convertToBigDecimal(value));
				ps.setNull(parameterIndex,java.sql.Types.DOUBLE);	
				
				//ps.setNull(parameterIndex,java.sql.Types.VARCHAR);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("INTEGER")){
			if(!Utils.isNull(value).equals("")){
				ps.setInt(parameterIndex,ConvertUtils.convertToInt(value));
			}else{
			    ps.setNull(parameterIndex,java.sql.Types.INTEGER);	
			}
		}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
			if(!Utils.isNull(value).equals("")){ 
				//logger.debug("Timestamp:"+Utils.parse(value, Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH));
				ps.setTimestamp(parameterIndex,new java.sql.Timestamp(Utils.parse(value, Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH).getTime()));
			}else{
			    ps.setTimestamp(parameterIndex,null);	
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
	
}
