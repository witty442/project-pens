package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.UpdateSalesManager;

public class UpdateSalesProcess {
  
	public static Logger logger = Logger.getLogger("PENS");
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
	public  String[] importToDB(Connection conn,LinkedHashMap initConfigMap,TableBean tableBean,FTPFileBean ftpFileBean,User userBean) throws Exception {
	    PreparedStatement psUpdateH = null;
	    PreparedStatement psInsH = null;
	    PreparedStatement psUpdateD = null;
	    PreparedStatement psUpdateDCS = null;
	    PreparedStatement psInsD = null;
	    StringBuffer resultTxt = new StringBuffer("");
	    List dataStrBuffList = new ArrayList();
	    List orderList = new ArrayList();
	    String lineStr = "";
        int line=0;int lineNo = 0;
        int canExc = 0;int totalRos = 0;
        int completeRow = 0;int errorRow= 0;
        String errorCode="";String errorMsg="";
        String firstErrorMsg ="";
        String firstErrorCode = "";
        String[] dataTextLineArr = null;
        String[] results = new String[3];
        TableBean childBean = null;
	    try {
	    	  logger.debug("ImportToDB");
	    	  dataTextLineArr = ftpFileBean.getDataLineText();
	    	 	    	  
	    	  /**  init prepareStatment Ins H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(tableBean.getPrepareSqlIns()).equals("")){
			     logger.debug("sqlIns_H:"+tableBean.getPrepareSqlIns());
			     psInsH = conn.prepareStatement(tableBean.getPrepareSqlIns());
	          }
			  /**  init prepareStatment Update H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(tableBean.getPrepareSqlUpd()).equals("")){
				  logger.debug("sqlUpd_H:"+tableBean.getPrepareSqlUpd());
				  psUpdateH = conn.prepareStatement(tableBean.getPrepareSqlUpd());
	          }
			  if(!"N".equalsIgnoreCase(tableBean.getChildTable())){
				  /** init ChildBean  **/
				  childBean = new TableBean(); 
				  childBean.setTransactionType(Constants.TRANSACTION_UTS_TRANS_TYPE);
				  childBean.setTableName(tableBean.getChildTable());
				  childBean = ImportHelper.initColumn(UpdateSalesManager.PATH_CONTROL,childBean);
				  childBean = ImportHelper.genPrepareSQLUTS(childBean,userBean);

				  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(childBean.getPrepareSqlUpd()).equals("")){
					  logger.debug("sqlUpd_D:"+childBean.getPrepareSqlUpd());
					  psUpdateD = conn.prepareStatement(childBean.getPrepareSqlUpd());
				  }
				  
				  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(childBean.getPrepareSqlUpdCS()).equals("")){
					  logger.debug("sqlUpdateD_CS:"+childBean.getPrepareSqlUpdCS());
				      psUpdateDCS = conn.prepareStatement(childBean.getPrepareSqlUpdCS());
				  }

				  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(childBean.getPrepareSqlIns()).equals("")){
					  logger.debug("sqlIns_D:"+childBean.getPrepareSqlIns());
					  psInsD = conn.prepareStatement(childBean.getPrepareSqlIns());
				  }
			  }
			  
			  totalRos = dataTextLineArr.length;
			  logger.debug("strTxts length:"+totalRos);
			  for(line = 0;line<totalRos;line++){
				  try{
					  lineNo++;
					  canExc = 0;
					  lineStr = Utils.isNull(dataTextLineArr[line]);
			    		  
			    	  if( !Utils.isNull(lineStr).equals("")){
				    		  //**Check Header Or Line**/
				    		  if(lineStr.startsWith("H")){  
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
					    			  logger.debug("**********Start Update H ******************");
					    		      psUpdateH = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdateH,userBean);
						    	      canExc = psUpdateH.executeUpdate();
						    	      logger.debug("canUpdate H:"+canExc);
						    	      
						    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
						    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
							    	      orderList.add(lineStr);
							    	   }
				    			  }
				    			  
				    			  if(canExc == 0 && tableBean.getActionDB().indexOf("I") != -1 && !"".equals(tableBean.getPrepareSqlIns()) ){
				    				  logger.debug("**********Start Insert H ******************");
					    		      psInsH = ImportHelper.spiltLineArrayToInsertStatement(conn, tableBean, lineStr, psInsH,userBean);
						    	      canExc = psInsH.executeUpdate();
						    	      logger.debug("canIns H:"+canExc); 
						    	      
						    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
						    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
							    	      orderList.add(lineStr);
							    	  }
				    			  }  
				    			  
				    		  }else  if( !lineStr.startsWith("L")){ // Case t_visit No  String :'H'
				    			  // end if Header
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
					    			  logger.debug("**********Start Update H ******************");
					    		      psUpdateH = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdateH,userBean);
						    	      canExc = psUpdateH.executeUpdate();
						    	      logger.debug("canUpdate H:"+canExc);
				    			  }
				    		  }
				    		  
				    		  /** Line **/
				    		  if(lineStr.startsWith("L")){
				    			  if(!"".equalsIgnoreCase(tableBean.getChildTable())){
				    				  
				    				  /** Case Special **/
				    				  if(isOrderLineCanInsert(tableBean, childBean, userBean, lineStr)){
					    				  /** Update Line **/
					    				  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(childBean.getPrepareSqlUpdCS())){
						    				  logger.debug("**********Start Update L CS ******************");
							    			  psUpdateDCS = ImportHelper.spiltLineArrayToUpdateStatementCS(conn, childBean, lineStr, psUpdateDCS,userBean);
									    	  canExc = psUpdateDCS.executeUpdate();
									    	  logger.debug("canUpdateCS L:"+canExc);
					    				  }
	                                      /** INSERT LINE **/
							    		  if(canExc ==0){
							    			  logger.debug("**********Start Insert L CS ******************");
							    			  psInsD = ImportHelper.spiltLineArrayToInsertStatement(conn, childBean, lineStr, psInsD,userBean);
							    			  canExc = psInsD.executeUpdate();
									    	  logger.debug("canIns L:"+canExc);
							    		 } 
				    				  }else{
				    					  /** Update Line  normal**/
					    				  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(childBean.getPrepareSqlUpd())){
						    				  logger.debug("**********Start Update L ******************");
							    			  psUpdateD = ImportHelper.spiltLineArrayToUpdateStatement(conn, childBean, lineStr, psUpdateD,userBean);
									    	  canExc = psUpdateD.executeUpdate();
									    	  logger.debug("canUpdate L:"+canExc);
					    				  }
				    				  }
						    		  
				    			  }//if 4 
				    		  }//if Line
				    		  
				    	  
				    	  //stamp log result
				    	  if(canExc == 0){
				    		 errorMsg  = "NO UPDATE KEY NOT FOUND";
				    		 errorCode = "NOUPDATE";
				    		 resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->WARNING:NO UPDATE CANNOT FIND KEY UPDATE 001]").append(Constants.newLine);
				    	     errorRow++;
				    	     
				    	     // Add First Error To Save In monitor_item
				    	     if(firstErrorMsg.equals("")){
				    		     firstErrorMsg  = "NO UPDATE KEY NOT FOUND";
				    		     firstErrorCode = "NOUPDATE";
				    		  }
				    	  }else{
				    		 completeRow++;
				    	  }
				    	  //initail 
				    	  canExc = 0;
			    	  }//if 1
			       
		    	  }catch(Exception e){
		    		  logger.error(e.getMessage(),e);
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    		  
		    		  logger.debug("firstErrorMsg:"+firstErrorMsg);
		    		  // Add First Error To Save In monitor_item
		    		  if(firstErrorMsg.equals("")){
		    		     firstErrorMsg  = errorMsg;
		    		     firstErrorCode = errorCode;
		    		  }
		    	  }
		    	  
			  }//for
			  
			  
			  if(errorRow <=0){
	    		  /** Get Order From List from Text ORDER.txt **/
				  InterfaceDAO dao = new InterfaceDAO();
				  String orderNoAll = "";
				  for(int n=0;n < orderList.size();n++){
	    			 String lineTextStr = (String)orderList.get(n);
    				 String[] lineArray = (lineTextStr+" ").split(Constants.delimeterPipe);
    				 
//    				 logger.debug("lineTextStr:"+lineTextStr);
//    				 logger.debug("lineArray:"+lineArray.length);
    				 
    		         if(lineArray != null && lineArray.length>0){
	    		    	 if(tableBean.getTableName().equalsIgnoreCase("t_order")){
	    		    		 orderNoAll +="'"+ImportHelper.getReplcaeNewOrderNo(Utils.isNull(lineArray[1]))+"',";//ORDER_NO POSITION TEXT
	    		    	 }else if(tableBean.getTableName().equalsIgnoreCase("t_order_orcl")){ //order manual from ORCL
	    		    		 orderNoAll +="'"+ImportHelper.getReplcaeNewOrderNo(Utils.isNull(lineArray[2]))+"',";//ORDER_NO POSITION TEXT
	    		    	 }
    		         }
				  }
	    		  
	    		  /** Check All Order  By Order List **/
				  if( !"".equals(orderNoAll)){
					  orderNoAll =orderNoAll.substring(0,orderNoAll.length()-1);
		    		  orderList = dao.getOrderIdListByOrderNo(conn, orderNoAll);
		    		  for(int n=0;n< orderList.size();n++){
		    			  String  orderId =(String)orderList.get(n);
		    			  if( !Utils.isNull(orderId).equals("")){
			    		        updateOrderFlag(conn,orderId, userBean);
		    			   }
		    		  }
				  }
		    	  
			  }else if(errorRow > 0){
				  /**Optional add Result Log */
				  resultTxt.append("----------------------------------").append("\n");
				  resultTxt.append("Total Rows :"+dataTextLineArr.length).append("\n");
				  resultTxt.append("Success Rows :"+completeRow).append("\n");
				  resultTxt.append("Error Rows :"+errorRow).append("\n");
				  StringBuffer columnHeader = new StringBuffer("");
				  columnHeader.append("H -"+tableBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  if(childBean != null){
				     columnHeader.append("L -"+childBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  }
				  resultTxt = columnHeader.append(resultTxt);
				  
			      /**Optional Log File Case one table have more one file **/
		          if(tableBean.getDataStrBuffList() ==null){
		          	  dataStrBuffList = new ArrayList();
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
			 
			 results[0] = firstErrorMsg;
			 results[1] = completeRow+"";
			 results[2] = firstErrorCode;
			 
	    } catch(Exception e){
		     throw e;
	    }finally{
	    	if(psInsH != null){
		    	psInsH.close();psInsH=null;
		    }
		    if(psUpdateH != null){
		    	psUpdateH.close();psUpdateH=null;
		    }
		    if(psUpdateD != null){
		    	psUpdateD.close();psUpdateD=null;
		    }
		    if(psUpdateDCS != null){
		    	psUpdateDCS.close();psUpdateDCS=null;
		    }
		    if(psInsD != null){
		    	psInsD.close();psInsD=null;
		    }
	    }
	    return results;
	}
  	
	
	private boolean isOrderLineCanInsert(TableBean tableBean ,TableBean chileBean,User user ,String lineStr) throws Exception{
		boolean canAccess = false;
		try{
			if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(chileBean.getPrepareSqlIns()).equals("")){
				
				if(chileBean.getTableName().equalsIgnoreCase("t_order_line")){
					if( !Utils.isNull(lineStr).equals("")){
						String[] lineArray = (lineStr+" ").split(Constants.delimeterPipe);
						/** validate product_id in lineText position = 6  */
						int product_id_text_position = 6;
						if( !Utils.isNull(lineArray[product_id_text_position]).equals("")){
							canAccess = true;
						}
					}
				}else{
					canAccess = true;
				}
			}
			logger.debug("CanAccess:"+chileBean.getTableName()+":"+canAccess);
			
		}catch(Exception e){
			throw e;
		}
		return canAccess;
	}
	
	/**
	 * updateOrderPaymentFlagAll
	 * @param conn
	 * @param tableBean
	 * @param lineStr
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * * Case 1 Update Payment Flag โดย
      (1) ถ้าที่ header payment  = 'Y' ที่ระดับ line จะเท่ากับ   "Y" ด้วย
      (2) ถ้าที่ header payment  = 'N' ที่ระดับ line จะเท่ากับ   "N" ด้วย
     
     * * Case 2 Order_line PAYMENT ='Y' ALL Receord  
      (1) update header  payment = 'Y'
     * * Case3 Order Line iscancel =Y ALL Record 
      (1) update doc_status = 'VO'
	 */
	private int updateOrderFlag(Connection conn,String orderId,User user) throws Exception{
		InterfaceDAO dao = new InterfaceDAO();
		try{
			logger.debug("**Start updateOrderFlag ******");
			logger.debug("ORDER_NO:"+orderId);
			
			/** Check Found iscancel=Y in order_line */
			if(dao.isOrderLineCancelOne(conn, orderId)){
				logger.debug("Is Found Order Line Cancel");
		        /** Update order_line payment =(select payment from order) **/
				dao.updatePaymentInOrderLine(conn, orderId);
				
				/** Calc Sum order.total_amount = sum(line.net_amount) , order.vat_amount = sum(line.vat_amount) ,order.net_amount = sum(line.total_amount) **/
				/** Calc line iscancel=N  */
				dao.updateRecalcAmountInOrder(conn, orderId);
			}

			/** Check Line is Paymented all  and update paymented = Y in order */
			if(dao.isOrderLinePaymentAll(conn, orderId)){
				logger.debug("Order Line Payment All : Update t_order set payment = 'Y'  ");
				dao.updatePaymentInOrder(conn, orderId, Constants.PAYMENT_FLAG_Y);
			}
			
			/** Check Line is cencel All  and update order doc_status ='VO' */
			if(dao.isOrderLineCancelAll(conn, orderId)){
				logger.debug("Order Line Cancel All : doc_status ='VO'  ");
				dao.updateDocStatusInOrder(conn, orderId, "VO");
			}
				
		 return 0;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
}
