package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.WebMemberManager;

public class WebMemberProcess {
  
	protected Logger logger = Logger.getLogger("PENS");
	/**
	 * importToDB
	 * @param conn
	 * @param initConfigMap
	 * @param tableBean
	 * @param dataTextLineArr
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public  String[] importToDB(Connection conn,LinkedHashMap<String,TableBean> initConfigMap,TableBean tableBean,FTPFileBean ftpFileBean,User userBean) throws Exception {
	    PreparedStatement psInsMCust = null;
	    PreparedStatement psInsMContact = null;
	    PreparedStatement psInsMAddressShipTo = null;
	    PreparedStatement psInsMAddressBillTo = null;
	    PreparedStatement psInsMMemberProduct = null;
	    
	    PreparedStatement psUpdMCust = null;
	    PreparedStatement psUpdMContact = null;
	    PreparedStatement psUpdMAddressShipTo = null;
	    PreparedStatement psUpdMAddressBillTo = null;
	    PreparedStatement psUpdMemberProduct = null;
	    
	    StringBuffer resultTxt = new StringBuffer("");
	    List<FTPFileBean> dataStrBuffList = new ArrayList<FTPFileBean>();
	    String lineStr = "";
        int line=0;
        int canExc = 0;
        int totalRos = 0;
        int completeRow = 0;
        int errorRow= 0;
        String errorMsg ="";
        String errorCode = "";
        String[] dataTextLineArr = null;
        String[] results = new String[3];
        TableBean mCustBean = null;
        TableBean mAddressBean = null;
        TableBean mContactBean = null;
        TableBean mMemberProductBean = null;
	    try {
	    	  logger.debug("ImportToDB");
	    	  dataTextLineArr = ftpFileBean.getDataLineText();
	    	  	    	  
	    	  /**  init prepareStatment Ins H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1){
	    		  /** Init Table m_customer **/
	    		  mCustBean = new TableBean();
	    		  mCustBean.setTableName("m_customer");
	    		  mCustBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mCustBean);
	    		  mCustBean  = ImportHelper.genPrepareSQL(mCustBean,userBean ,"");
			      psInsMCust = conn.prepareStatement(mCustBean.getPrepareSqlIns());
			      logger.debug("sql ins m_customer:"+mCustBean.getPrepareSqlIns());
			      
			      mAddressBean = new TableBean();
			      mAddressBean.setTableName("m_address");
			      mAddressBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mAddressBean);
			      mAddressBean  = ImportHelper.genPrepareSQL(mAddressBean,userBean ,"");
			      psInsMAddressShipTo = conn.prepareStatement(mAddressBean.getPrepareSqlIns());   
			      psInsMAddressBillTo = conn.prepareStatement(mAddressBean.getPrepareSqlIns()); 
			      logger.debug("sql ins m_address:"+mAddressBean.getPrepareSqlIns());
			      
			      mContactBean = new TableBean();
			      mContactBean.setTableName("m_contact");
			      mContactBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mContactBean);
			      mContactBean  = ImportHelper.genPrepareSQL(mContactBean,userBean ,"");
			      psInsMContact = conn.prepareStatement(mContactBean.getPrepareSqlIns());
			      logger.debug("sql ins mContactBean:"+mContactBean.getPrepareSqlIns());
			      
			      mMemberProductBean = new TableBean();
			      mMemberProductBean.setTableName("m_member_product");
			      mMemberProductBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mMemberProductBean);
			      mMemberProductBean  = ImportHelper.genPrepareSQL(mMemberProductBean,userBean ,"");
			      psInsMMemberProduct = conn.prepareStatement(mMemberProductBean.getPrepareSqlIns());
			      logger.debug("sql ins mMemberProductBean:"+mMemberProductBean.getPrepareSqlIns());
	          }
			  /**  init prepareStatment Update H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1){
	    		  
	    		  /** Init Table m_customer **/
	    		  mCustBean = new TableBean();
	    		  mCustBean.setTableName("m_customer");
	    		  mCustBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mCustBean);
	    		  mCustBean  = ImportHelper.genPrepareSQL(mCustBean,userBean ,"");
			      psUpdMCust = conn.prepareStatement(mCustBean.getPrepareSqlUpd());
			      logger.debug("sql Update mCustBean:"+mCustBean.getPrepareSqlUpd());
			      
			      mAddressBean = new TableBean();
			      mAddressBean.setTableName("m_address");
			      mAddressBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mAddressBean);
			      mAddressBean  = ImportHelper.genPrepareSQL(mAddressBean,userBean ,"");
			      psUpdMAddressShipTo = conn.prepareStatement(mAddressBean.getPrepareSqlUpd());
			      psUpdMAddressBillTo = conn.prepareStatement(mAddressBean.getPrepareSqlUpd());
			      logger.debug("sql Update mAddressBean:"+mAddressBean.getPrepareSqlUpd());
			      
			      mContactBean = new TableBean();
			      mContactBean.setTableName("m_contact");
			      mContactBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mContactBean);
			      mContactBean  = ImportHelper.genPrepareSQL(mContactBean,userBean ,"");
			      psUpdMContact = conn.prepareStatement(mContactBean.getPrepareSqlUpd());
			      logger.debug("sql Update mContactBean:"+mContactBean.getPrepareSqlUpd());
			      
			      mMemberProductBean = new TableBean();
			      mMemberProductBean.setTableName("m_member_product");
			      mMemberProductBean  = ImportHelper.initColumn(WebMemberManager.PATH_CONTROL,mMemberProductBean);
			      mMemberProductBean  = ImportHelper.genPrepareSQL(mMemberProductBean,userBean ,"");
			      psUpdMemberProduct = conn.prepareStatement(mMemberProductBean.getPrepareSqlUpd());
			      logger.debug("sql Update mMemberProductBean:"+mMemberProductBean.getPrepareSqlUpd());
	          }

			  totalRos = dataTextLineArr.length;
			  logger.debug("strTxts length:"+totalRos);
			  for(line = 0;line<totalRos;line++){
				  try{
					  canExc = 0;
					  lineStr = Utils.isNull(dataTextLineArr[line]);
			    	  if( !Utils.isNull(lineStr).equals("")){
			    		  
		    			  if(mCustBean != null){
		    				  /** m_customer **/
		    				  if(tableBean.getActionDB().indexOf("U") != -1){
			    				  psUpdMCust = ImportHelper.spiltLineArrayToUpdateStatement(conn, mCustBean, lineStr, psUpdMCust,userBean);
			    				  canExc = psUpdMCust.executeUpdate();
			    				  logger.debug("Result Update MCustomer :"+canExc);
		    				  }
		    				  if(tableBean.getActionDB().indexOf("I") != -1 && canExc == 0){
		    					  psInsMCust = ImportHelper.spiltLineArrayToInsertStatement(conn, mCustBean, lineStr, psInsMCust,userBean);
			    				  canExc = psInsMCust.executeUpdate();  
			    				  logger.debug("Result Insert MCustomer :"+canExc);
		    				  }
		    				  canExc = 0;
		    			  }
			    			
		    			  if(mAddressBean != null){
		    				  ColumnBean[] replaceValues = new ColumnBean[1];
		    				  /** m_customer  insert 2 record ->1.shipTo,2.billTo**/
		    				  if(tableBean.getActionDB().indexOf("U") != -1){
		    					  ColumnBean col1 = new ColumnBean();
		    					  col1.setColumnName("PURPOSE");
		    					  col1.setDefaultValue("S");
		    					  replaceValues[0] = col1;
		    					  
			    				  psUpdMAddressShipTo = ImportHelper.spiltLineArrayToUpdateStatement(conn, mAddressBean, lineStr, psUpdMAddressShipTo,userBean,replaceValues);
			    				  canExc = psUpdMAddressShipTo.executeUpdate();
			    				  logger.debug("Result Update Address ShippTo :"+canExc);
			    				  
			    				  /** Duplicate set Purpose ='B' **/
			    				  col1 = new ColumnBean();
		    					  col1.setColumnName("PURPOSE");
		    					  col1.setDefaultValue("B");
		    					  replaceValues[0] = col1;
		    					  
		    					  psUpdMAddressBillTo = ImportHelper.spiltLineArrayToUpdateStatement(conn, mAddressBean, lineStr, psUpdMAddressBillTo,userBean,replaceValues);
			    				  canExc = psUpdMAddressBillTo.executeUpdate();
			    				  logger.debug("Result Update Address BillTo :"+canExc);
		    					  
		    				  }
		    				  
		    				  if(tableBean.getActionDB().indexOf("I") != -1 && canExc == 0){
		    					  ColumnBean col1 = new ColumnBean();
		    					  col1.setColumnName("PURPOSE");
		    					  col1.setDefaultValue("S");
		    					  replaceValues[0] = col1;
		    					  
		    					  psInsMAddressShipTo = ImportHelper.spiltLineArrayToInsertStatement(conn, mAddressBean, lineStr, psInsMAddressShipTo,userBean,replaceValues);
			    				  canExc = psInsMAddressShipTo.executeUpdate();  
			    				  logger.debug("Result Insert Address Ship To :"+canExc);
			    				  
			    				  col1 = new ColumnBean();
		    					  col1.setColumnName("PURPOSE");
		    					  col1.setDefaultValue("B");
		    					  replaceValues[0] = col1;
		    					  
			    				  psInsMAddressBillTo = ImportHelper.spiltLineArrayToInsertStatement(conn, mAddressBean, lineStr, psInsMAddressBillTo,userBean,replaceValues);
			    				  canExc = psInsMAddressBillTo.executeUpdate();  
			    				  logger.debug("Result Insert Address Bill To :"+canExc);
		    				  }
		    				  canExc = 0;
		    			  }
		    			  
		    			  if(mContactBean != null){
		    				  /** m_customer **/
		    				  if(tableBean.getActionDB().indexOf("U") != -1){
			    				  psUpdMContact = ImportHelper.spiltLineArrayToUpdateStatement(conn, mContactBean, lineStr, psUpdMContact,userBean);
			    				  canExc = psUpdMContact.executeUpdate();
			    				  logger.debug("Result Update Contact :"+canExc);
		    				  }
		    				  if(tableBean.getActionDB().indexOf("I") != -1 && canExc == 0){
		    					  psInsMContact = ImportHelper.spiltLineArrayToInsertStatement(conn, mContactBean, lineStr, psInsMContact,userBean);
			    				  canExc = psInsMContact.executeUpdate();  
			    				  logger.debug("Result Insert Contact :"+canExc);
		    				  }
		    				  canExc = 0;
		    			  }

		    			  if(mMemberProductBean != null){
		    				  /** m_customer **/
		    				  if(tableBean.getActionDB().indexOf("U") != -1){
			    				  psUpdMemberProduct = ImportHelper.spiltLineArrayToUpdateStatement(conn, mMemberProductBean, lineStr, psUpdMemberProduct,userBean);
			    				  canExc = psUpdMemberProduct.executeUpdate();
			    				  logger.debug("Result Update Member_product :"+canExc);
		    				  }
		    				  if(tableBean.getActionDB().indexOf("I") != -1 && canExc == 0){
		    					  psInsMMemberProduct  = ImportHelper.spiltLineArrayToInsertStatement(conn, mMemberProductBean, lineStr, psInsMMemberProduct,userBean);
			    				  canExc = psInsMMemberProduct.executeUpdate();  
			    				  logger.debug("Result Insert Member_product :"+canExc);
		    				  }
		    				  canExc = 0;
		    			  }
		    			  
				    	  //stamp log result
				    	  completeRow++;
				    	 
				    	  //initail 
				    	  canExc = 0;
			    	  }//if 1
				  
		    	  }catch(Exception e){
		    		  logger.error(e.getMessage(),e);
		    		  errorMsg ="Error:Line{"+line+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+line+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    	  }
			  }
			   
		     if(errorRow > 0){
				  /**Optional add Result Log */
				  resultTxt.append("----------------------------------").append("\n");
				  resultTxt.append("Total Rows :"+dataTextLineArr.length).append("\n");
				  resultTxt.append("Success Rows :"+completeRow).append("\n");
				  resultTxt.append("Error Rows :"+errorRow).append("\n");
				  StringBuffer columnHeader = new StringBuffer("");
				  columnHeader.append("H -"+tableBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  resultTxt = columnHeader.append(resultTxt);
				  
			      /**Optional Log File Case one table have more one file **/
		          if(tableBean.getDataStrBuffList() ==null){
		          	  dataStrBuffList = new ArrayList<FTPFileBean>();
		          	  FTPFileBean fileBean = new FTPFileBean();
		          	  fileBean.setFileName(ftpFileBean.getFileName());
		          	  fileBean.setDataResultStr(resultTxt.toString());
		          	  dataStrBuffList.add(fileBean);
		          	  tableBean.setDataStrBuffList(dataStrBuffList);
		          }else{
		        	  FTPFileBean fileBean = new FTPFileBean();
			          fileBean.setFileName(ftpFileBean.getFileName());
			          fileBean.setDataResultStr(resultTxt.toString());
		          	  tableBean.getDataStrBuffList().add(fileBean);
		          }
			  }	
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
			 
			 results[0] = errorMsg;
			 results[1] = completeRow+"";
			 results[2] = errorCode;
			 
	    } catch(Exception e){
		     throw e;
	    }finally{
	    	DBConnection.close(psInsMCust);
	    	DBConnection.close(psInsMContact);
	    	DBConnection.close(psInsMAddressShipTo);
	    	DBConnection.close(psInsMAddressBillTo);
	    	DBConnection.close(psInsMMemberProduct);
	    	DBConnection.close(psUpdMCust);
	    	DBConnection.close(psUpdMContact);
	    	DBConnection.close(psUpdMAddressShipTo);
	    	DBConnection.close(psUpdMAddressBillTo);
	    	DBConnection.close(psUpdMemberProduct);
	    }
	    return results;
	}
	
	
	public  void duplicateRecordMAddressToBillTo(Connection conn,TableBean mAddressBean,String lineStr) throws Exception{
		PreparedStatement ps =null;
		PreparedStatement psFind = null;
		ResultSet rs = null;
		int i=0;
		try{
			lineStr += " "; /** case last String | **/
			String[] lineArray = lineStr.split(Constants.delimeterPipe);
			/** Find key to Update or insert **/
			String referenceId = "";
			for(i=0;i<mAddressBean.getColumnBeanOrderUpdateList().size();i++){
	    		ColumnBean colBean = (ColumnBean)mAddressBean.getColumnBeanOrderUpdateList().get(i);
	    		if(colBean.getColumnName().equalsIgnoreCase("REFERENCE_ID")){
	    			referenceId = Utils.isNull(lineArray[colBean.getTextPosition()]);
	    			break;
	    		}
			}
			
			StringBuffer sqlFind = new StringBuffer("");
			sqlFind.append(" SELECT ADDRESS_ID FROM m_address where REFERENCE_ID = "+referenceId+" AND PURPOSE ='S' ");
			logger.debug("sql:"+sqlFind.toString());
			psFind = conn.prepareStatement(sqlFind.toString());
			rs = psFind.executeQuery();
			
			if(rs.next()){
				logger.debug("Update receord m_address SHIP_TO");
				String addressId = rs.getString("ADDRESS_ID");
				
			}else{
			    logger.debug("Insert receord m_address SHIP_TO");
				StringBuffer sqlInsert = new StringBuffer("");
				sqlInsert.append(" INSERT INTO m_address \n") ;
				sqlInsert.append("  SELECT \n");
				sqlInsert.append("  ADDRESS_ID,CUSTOMER_ID,PROVINCE_ID \n") ;
				sqlInsert.append("  DISTRICT_ID,PURPOSE,COUNTRY \n") ;
				sqlInsert.append("  ISACTIVE,CREATED,CREATED_BY \n") ;
				sqlInsert.append("  UPDATED,UPDATED_BY,REFERENCE_ID \n") ;
				sqlInsert.append("  line1,line2,line3 \n") ;
				sqlInsert.append("  line4,province_name,postal_code \n") ;
				sqlInsert.append("  FROM m_address where REFERENCE_ID = "+referenceId+" AND PURPOSE ='S' \n");
			}
			
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
}
