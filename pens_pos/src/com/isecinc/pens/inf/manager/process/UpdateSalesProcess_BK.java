package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.UpdateSalesManager;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.process.SequenceProcess;

public class UpdateSalesProcess_BK {
  
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
	    List<FTPFileBean> dataStrBuffList = new ArrayList<FTPFileBean>();
	    List<String> orderList = new ArrayList<String>();
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
        String[] lineStrArr = null;
        String fileName = "";
	    try {
	    	  dataTextLineArr = ftpFileBean.getDataLineText();
	    	  fileName = ftpFileBean.getFileName();
	    			  
	    	  //Delete Old import Error By FileName Case Copy past to path 
			  deleteTempImportErrorByFileName(conn,fileName);
				
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
					  logger.debug("sqlUpddate_Line:"+childBean.getPrepareSqlUpd());
					  psUpdateD = conn.prepareStatement(childBean.getPrepareSqlUpd());
				  }
				  
				  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(childBean.getPrepareSqlUpdCS()).equals("")){
					  logger.debug("sqlUpdate_Line_CS:"+childBean.getPrepareSqlUpdCS());
				      psUpdateDCS = conn.prepareStatement(childBean.getPrepareSqlUpdCS());
				  }

				  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(childBean.getPrepareSqlIns()).equals("")){
					  logger.debug("sqlInsert_Line:"+childBean.getPrepareSqlIns());
					  psInsD = conn.prepareStatement(childBean.getPrepareSqlIns());
				  }
			  }
			  String keyInLine = "";
			  totalRos = dataTextLineArr.length;
			  logger.debug("strTxts length:"+totalRos);
			  for(line = 0;line<totalRos;line++){
				  try{
					  lineNo++;
					  canExc = 0;
					  lineStr = Utils.isNull(dataTextLineArr[line]);
			    	  if( !Utils.isNull(lineStr).equals("")){
				    		  //**Check Header Or Line**/
			    		      logger.debug("lineStr["+lineStr+"]");
			    		      
			    		      /****** Set Data Check Line Error for import again ****/
			    		      lineStr += " "; /** case last String | **/
			    			  lineStrArr = lineStr.split(Constants.delimeterPipe);
		    				  if(tableBean.getTableName().startsWith("t_receipt")){
		    					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
		    				  }else  if(tableBean.getTableName().startsWith("t_receipt_line")){
		    					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
		    				  }else if(tableBean.getTableName().startsWith("t_order")){
		    					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
		    				  }else if(tableBean.getTableName().startsWith("t_order_line")){
		    					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
		    				  }else if(tableBean.getTableName().startsWith("t_order_orcl")){
		    					  keyInLine = Utils.isNull(lineStrArr[2])+"|"+tableBean.getTableName();
		    				  }else if(tableBean.getTableName().startsWith("t_order_line_orcl")){
		    					  keyInLine = Utils.isNull(lineStrArr[3])+"|"+tableBean.getTableName();
		    				  }else if(tableBean.getTableName().startsWith("t_bill_plan")){
			    				  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
			    			  }else if(tableBean.getTableName().startsWith("t_bill_plan_line")){
				    			  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
				    		  }
		    				  
		    				  /*******************************************************/
		    				  
			    		      /** Update H **/
				    		  if(lineStr.startsWith("H")){  
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
				    				  
				    				  if(tableBean.getTableName().startsWith("t_receipt")){
				    					  logger.debug("********Start Update H(t_receipt) *********************");
						    		      //canExc = new ImportReceiptFunction().processReceiptHead( conn, tableBean, userBean, lineStr,canExc);
							    	      logger.debug("********End Update H (t_receipt) canUpdate:"+canExc +"");
							    	      
				    				  }else{
						    			  logger.debug("********Start Update H *********************");
						    			  logger.debug("sql update:"+tableBean.getPrepareSqlUpd());
						    		      psUpdateH = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdateH,userBean);
							    	      canExc = psUpdateH.executeUpdate();
							    	      logger.debug("********End Update H canUpdate:"+canExc +"");
							    	      
							    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
							    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
								    	      orderList.add(lineStr);
								    	   }
				    				  }
				    			  }
				    			  
				    			  /** Insert Head **/
				    			  if(canExc == 0 && tableBean.getActionDB().indexOf("I") != -1 && !"".equals(tableBean.getPrepareSqlIns()) ){
				    				  if(tableBean.getTableName().startsWith("t_receipt")){
				    					 /** No action insert update only */
				    					 /*  logger.debug("********Start Insert H(t_receipt) *********************");
						    		      canExc = new ImportReceiptFunction().processReceiptHead("INSERT", conn, tableBean, userBean, lineStr,canExc);
							    	      logger.debug("********End Insert H (t_receipt) canUpdate:"+canExc +"");  */
				    				  }else{
					    				  logger.debug("**********Start Insert H ******************");
						    		      psInsH = ImportHelper.spiltLineArrayToInsertStatement(conn, tableBean, lineStr, psInsH,userBean);
							    	      canExc = psInsH.executeUpdate();
							    	      logger.debug("canIns H:"+canExc); 
							    	      
							    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
							    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
								    	      orderList.add(lineStr);
								    	  }
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
				    				  
				    				  /** Case Special t_order only**/
				    				  if(isOrderLineCanInsert(tableBean, childBean, userBean, lineStr)){
					    				  /** Update Line **/
					    				  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(childBean.getPrepareSqlUpdCS())){
						    				  logger.debug("**********Start Update L CS ******************");
							    			  psUpdateDCS = ImportHelper.spiltLineArrayToUpdateStatementCS(conn, childBean, lineStr, psUpdateDCS,userBean);
									    	  canExc = psUpdateDCS.executeUpdate();
									    	  logger.info("canUpdateCS L:"+canExc);
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
					    					  /** Create Receipt Line ONLY **/
                                              if(  "t_receipt_line".equalsIgnoreCase(childBean.getTableName()) ){  
                                            	 // canExc = new ImportReceiptFunction().processReceiptLine(conn, childBean, userBean, lineStr, psUpdateD,canExc);
                                             }else{
					    					     logger.debug("**********Start Update L normal ******************");
							    			     psUpdateD = ImportHelper.spiltLineArrayToUpdateStatement(conn, childBean, lineStr, psUpdateD,userBean);
									    	     canExc = psUpdateD.executeUpdate();
									    	     logger.debug("canUpdate Line:"+canExc);
                                             }
					    				  }
					    				  
					    				  /** INSERT LINE **/
					    				  if(tableBean.getActionDB().indexOf("I") != -1 && !"".equals(childBean.getPrepareSqlIns())){
					    					  
					    					  /** Create Receipt Line ONLY **/
                                              if(  "t_receipt_line".equalsIgnoreCase(childBean.getTableName()) ){  
                                            	  /*if(canExc ==0){
                                            	     canExc = new ImportReceiptFunction().processReceiptLine("INSERT", conn, childBean, userBean, lineStr, psInsD,canExc);
                                            	  }*/
					    					  }else{
						    					  if(canExc ==0){
									    			  logger.debug("**********Start Insert L normal  ******************");
									    			  psInsD = ImportHelper.spiltLineArrayToInsertStatement(conn, childBean, lineStr, psInsD,userBean);
									    			  canExc = psInsD.executeUpdate();
											    	  logger.debug("canInsert Line:"+canExc);
									    		 } 
					    					  }
					    				  }//if insert
				    				  }//if orderSpecial
				    			  }//if 4 childTable not null
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
				    	     
				    	     //Insert Line to t_temp_import_trans
		    				  insertLineToTempImportError(conn,fileName,tableBean.getTableName(),keyInLine,lineStr,firstErrorCode);
				    	  }else{
				    		 completeRow++;
				    		  //Insert Line to t_temp_import_trans
		    				  insertLineToTempImportError(conn,fileName,tableBean.getTableName(),keyInLine,lineStr,errorMsg);
				    	  }
				    	  //initial
				    	  canExc = 0;
			    	  }//if 1
			       
		    	  }catch(Exception e){
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    		  
		    		  logger.error(errorMsg,e);
		    		  
		    		  logger.debug("firstErrorMsg:"+firstErrorMsg);
		    		  // Add First Error To Save In monitor_item
		    		  if(firstErrorMsg.equals("")){
		    		     firstErrorMsg  = errorMsg;
		    		     firstErrorCode = errorCode;
		    		  }
		    		  
		    		  //Insert Line to t_temp_import_trans
    				  insertLineToTempImportError(conn,fileName,tableBean.getTableName(),keyInLine,lineStr,firstErrorCode);
		    	  }
		    	  
			  }//for
			  
			  if(errorRow <=0){
	    		  /** Get Order From List from Text ORDER.txt **/
				  InterfaceDAO dao = new InterfaceDAO();
				  String orderNoAll = "";
				  for(int n=0;n < orderList.size();n++){
	    			 String lineTextStr = (String)orderList.get(n);
    				 String[] lineArray = (lineTextStr+" ").split(Constants.delimeterPipe);
    				 
    				 //logger.debug("lineTextStr:"+lineTextStr);
    				 //logger.debug("lineArray:"+lineArray.length);
    				 
    		         if(lineArray != null && lineArray.length>0){
	    		    	 if(tableBean.getTableName().equalsIgnoreCase("t_order")){
	    		    		 orderNoAll +="'"+ExternalFunctionHelper.getReplcaeNewOrderNo(Utils.isNull(lineArray[1]))+"',";//ORDER_NO POSITION TEXT
	    		    	 }else if(tableBean.getTableName().equalsIgnoreCase("t_order_orcl")){ //order manual from ORCL
	    		    		 orderNoAll +="'"+ExternalFunctionHelper.getReplcaeNewOrderNo(Utils.isNull(lineArray[2]))+"',";//ORDER_NO POSITION TEXT
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
		    				 //Update Payment Flag
			    		     updateOrderFlag(conn,orderId, userBean);
			    		        
			    		     // Re-Calculate TotalAmount,vatAmount,netAmount
			    		     try{
				    		    Order orderUpdate =  new MOrder().reCalculateHeadAmount(conn,orderId);
				    		    updateAmountInOrderHead(conn,orderUpdate);
			    		     }catch(Exception eee){
			    		    	 logger.error(eee.getMessage(),eee);
			    		     }
		    			   }//if
		    		  }//for
				  }//if
				  
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
			 
			 /** Remove t_temp_import_trans No Error Case Error Msg not null**/
			 checkDeleteImportTempTrans(conn);
			 
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
  	
	public  int updateAmountInOrderHead(Connection conn,Order o) throws Exception{
	     PreparedStatement ps =null;
	     int updateInt = 0;
		 try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update t_order set total_amount ='"+o.getTotalAmount()+"' ");
			sql.append(" , vat_amount ='"+o.getVatAmount()+"' ");
			sql.append(" , net_amount ='"+o.getNetAmount()+"' ");
			sql.append(" where order_id = "+o.getId()+" \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			updateInt = ps.executeUpdate();	
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
		}
			return updateInt;
	}
	 
	private boolean isOrderLineCanInsert(TableBean tableBean ,TableBean childBean,User user ,String lineStr) throws Exception{
		boolean canAccess = false;
		try{
			if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(childBean.getPrepareSqlIns()).equals("")){
				
				if(childBean.getTableName().equalsIgnoreCase("t_order_line")){
					if( !Utils.isNull(lineStr).equals("")){
						String[] lineArray = (lineStr+" ").split(Constants.delimeterPipe);
						/** validate product_id in lineText position = 6  */
						int product_id_text_position = 6;
						if( !Utils.isNull(lineArray[product_id_text_position]).equals("")){
							canAccess = true;
						}
					}
				}else{
					canAccess = false;
				}
			}
			logger.debug("CanAccess:"+childBean.getTableName()+":"+canAccess);
			
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
     
     * * Case 2 Order_line PAYMENT ='Y' ALL Record 
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
				//dao.updateRecalcAmountInOrder(conn, orderId);
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
			throw e;
		}
	}
	/** Import LineStr to t_temp_import_trans **/
	public void  deleteTempImportErrorByFileName(Connection conn,String fileName) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n delete from t_temp_import_trans_error");
			sql.append("\n where file_name =? ");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, fileName);
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	
	public void  checkDeleteImportTempTrans(Connection conn) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n select  table_name,key_no,count(*) as total_row from t_temp_import_trans_error");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if(checkAllLineImportTempTransNoError(conn,rs.getString("table_name"),rs.getString("key_no"))){
					deleteTempImportErrorByKey(conn, "'"+rs.getString("table_name")+"'", "'"+rs.getString("key_no")+"'");
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
	}
	public boolean  checkAllLineImportTempTransNoError(Connection conn,String tableName,String keyNo) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		boolean r = true;
		try{
			sql.append("\n select count(*) as c from t_temp_import_trans_error");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n and key_no ='"+keyNo+"'");
			sql.append("\n and (error_msg is not null and error_msg <>'' )");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt("c") >0){
					r = false;
				}
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return r;
	}
	
	/** Import LineStr to t_temp_import_trans **/
	public void  deleteTempImportErrorByKey(Connection conn,String tableNameAll,String keyNoAll) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n delete from t_temp_import_trans_error");
			sql.append("\n where table_name in("+tableNameAll+")");
			sql.append("\n and  key_no in("+keyNoAll+")");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	
	/** Import LineStr to t_temp_import_trans **/
	public void  insertLineToTempImportError(Connection conn,String fileName,String tableName
			,String keyNo,String lineStr ,String errMsg) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			//keyNo = key_no|tableName
		    String[] keyNoArr = keyNo.split("\\|");
		    
		    Integer seq = SequenceProcess.getNextValue("import_trans_error");
		    
			sql.append("\n insert into t_temp_import_trans_error");
			sql.append("\n (file_name,table_name,line_str,created,key_no,seq,error_msg)");
			sql.append("\n values(?,?,?,?,?,?,?)");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, fileName);
			ps.setString(2, tableName);
			ps.setString(3, lineStr);
			ps.setTimestamp(4 , new java.sql.Timestamp(new Date().getTime()));
			ps.setString(5, keyNoArr[0]);
			ps.setInt(6, seq);
			ps.setString(7, errMsg);
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
}
