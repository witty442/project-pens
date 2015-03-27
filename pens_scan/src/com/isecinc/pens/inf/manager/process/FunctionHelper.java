package com.isecinc.pens.inf.manager.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class FunctionHelper {

	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param path
	 * @param tableBean
	 * @return
	 * @throws Exception
	 * readSQlExternalFunc  case read manual script delete fro delete promotion
	 */
	public static  List<String> readSQlExternalFunction(String function ,String tableName) throws Exception {
		logger.debug("readSQlExternalFunc function["+function+"] Table Name:["+tableName+"]");
		EnvProperties env = EnvProperties.getInstance();		
		List<String> sqlList = null;
		try {
			String pathFullName = getPathFullName(function,tableName);
			//logger.info("pathFullName["+pathFullName+"]");
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			String sqlAll = ftpManager.getDownloadFTPFileByName(pathFullName);
			//logger.info("SQLALL:"+sqlAll);
			
			sqlList = new ArrayList<String>();
			
			String[] splitSql = sqlAll.split("\\;");
			if(splitSql != null && splitSql.length > 0){
				for(int i=0;i<splitSql.length;i++){
					if( !Utils.isNull(splitSql[i]).equals("")){
				       sqlList.add(splitSql[i]);
					}
				}
			}
			
			logger.info("sqlList Size:"+sqlList.size());
			/** Case not found   Read Pre sql  from Code **/
			if(sqlList != null && sqlList.size()==0){
	          // sqlList = getPreSQL(function,tableName);
			}
		}catch(Exception e){
           logger.error(e.getMessage(),e);
           /** Case not found or error  Read from Code **/
           //sqlList = getPreSQL(function,tableName);
		} finally {
			//FileUtil.close(br);
		}
		return sqlList;
	}
	
	/*public static List<String> getPreSQL(String function ,String tableName){
		List<String> sqlList = new ArrayList<String>();
		if("Pre".equalsIgnoreCase(function)){
			if("m_uom_class".equals(tableName)){
				sqlList.add("delete from m_uom_class;");
			}else if("m_uom".equals(tableName)){
				sqlList.add("delete from m_uom;");
			}else if("m_product_category".equals(tableName)){
				sqlList.add("delete from m_product_category;");
			}else if("m_product".equals(tableName)){
				sqlList.add("delete from m_product;");
			}else if("m_uom_conversion".equals(tableName)){
				sqlList.add("delete from m_uom_conversion;");
			}else if("m_uom_class_conversion".equals(tableName)){
				sqlList.add("delete from m_uom_class_conversion;");
			}else if("m_pricelist".equals(tableName)){
				sqlList.add("delete from m_pricelist;");
			}else if("m_product_price".equals(tableName)){
				sqlList.add("delete from m_product_price;");
			}else if("m_modifier".equals(tableName)){
	
				sqlList.add("delete from m_relation_modifier ;");
				sqlList.add("delete from m_qualifier;");
				sqlList.add("delete from m_modifier_attr;");
				sqlList.add("delete from m_modifier_line;");
				sqlList.add("delete from m_modifier;");
			}
		}
		return sqlList;
	}*/
	
	public static String getPathFullName(String function ,String tableName){
		String pathManualScript ="";
		if("Pre".equalsIgnoreCase(function)){
		   pathManualScript = "/Manual-script/Script-Pre-Import/"+tableName+".sql";
		}else if("Post".equalsIgnoreCase(function)){
		   pathManualScript = "/Manual-script/Script-Post-Import/"+tableName+".sql";
		}
		return pathManualScript;
	}
}
