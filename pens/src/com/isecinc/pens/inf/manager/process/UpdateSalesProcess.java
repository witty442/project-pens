package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
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

import util.ControlCode;

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
import com.isecinc.pens.inf.manager.UpdateSalesManagerHelper;
import com.isecinc.pens.inf.manager.process.bean.KeyNoImportTransBean;
import com.isecinc.pens.inf.manager.process.bean.LineImportTransBean;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction2;
import com.isecinc.pens.model.MOrder;
import com.pens.utils.LoggerUtils;

public class UpdateSalesProcess {
  
	public static Logger logger = Logger.getLogger("PENS");
	//public static LoggerUtils logger = new LoggerUtils("UpdateSalesProcess");
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
	public  ResultImportBean importToDB(BigDecimal transId,Connection conMonitor,Connection conn
			,LinkedHashMap initConfigMap,TableBean tableBean
			,KeyNoImportTransBean keyNoBean,User userBean) throws Exception {
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
        int successRow = 0;int allRow=0;int errorRow= 0;
        String errorCode="";String errorMsg="";
        String firstErrorMsg ="";String firstErrorCode = "";
        ResultImportBean resultImportBean = null;
        TableBean childBean = null;
        UpdateSalesManagerHelper helper = new UpdateSalesManagerHelper();
        String receiptNoAll = "";
        ImportReceiptFunction importReceipt1 = new ImportReceiptFunction();
        ImportReceiptFunction2 importReceipt2 = new ImportReceiptFunction2();
        boolean useImportReceipt2 = false;
        LineImportTransBean lineBean = null;
	    try {  
	    	  /** For case new code config to use new Method **/
	    	  if(ControlCode.canExecuteMethod("UpdateSalesProcess", "ImportReceiptFunction2")){
	    		  useImportReceipt2 = true;
	    		  logger.debug("User method:ImportReceiptFunction ** 2 **");
	    	  }
	    	  
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
			  
			  totalRos = keyNoBean.getLineList().size();
			  logger.debug("totalRos:"+totalRos);
			  for(line = 0;line<totalRos;line++){
				  try{
					  lineNo++;
					  /*Count All Record */
					  allRow++;//logger.debug("allRow["+allRow+"]");
					  canExc = 0;
					  lineBean = keyNoBean.getLineList().get(line);
					  lineStr = lineBean.getLineStr();
					  
			    	  if( !Utils.isNull(lineStr).equals("")){
				    		  //**Check Header Or Line**/
			    		      logger.debug("seq["+lineBean.getSeq()+"],lineStr["+lineStr+"]");
			    		      
			    		      /** Update B (receipt only) **/
				    		  if(lineStr.startsWith("B")){  
				    			  if(tableBean.getTableName().startsWith("t_receipt")){
			    					  logger.debug("********Start Update Batch(t_receipt) *********************");
			    					  if(useImportReceipt2){
					    		         canExc = importReceipt2.processReceiptBatch(conn, keyNoBean, userBean, lineStr,canExc);
				    			      }else{
				    			    	 canExc = importReceipt1.processReceiptBatch(conn, keyNoBean, userBean, lineStr,canExc); 
				    			      }
			    					  logger.debug("********End Update Batch (t_receipt) canUpdate:"+canExc +"");
				    			  }				    		  
				    		  
			    		      /** Update H **/
			    	          }else if(lineStr.startsWith("H")){  
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
				    				  
				    				  if(tableBean.getTableName().startsWith("t_receipt")){
				    					  logger.debug("********Start Update H(t_receipt) *********************");
				    					  if(useImportReceipt2){
				    						 canExc = importReceipt2.processReceiptHead(conn, keyNoBean, userBean, lineStr,canExc);
				    					  }else{
						    		         canExc = importReceipt1.processReceiptHead(conn, keyNoBean, userBean, lineStr,canExc);
				    					  }
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
				    					  logger.debug("isOrderLineCanInsert :true");
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
					    					  if(childBean.getTableName().startsWith( "t_receipt_line") ){ 
					    						  if(useImportReceipt2){
					    							 canExc = importReceipt2.processReceiptLine(conn, keyNoBean, userBean, lineStr, psUpdateD,canExc,transId);
					    						  }else{
					    						     canExc = importReceipt1.processReceiptLine(conn, keyNoBean, userBean, lineStr, psUpdateD,canExc,transId);
					    						  }
					    						  logger.debug("canUpdate(receipt_line) Line:"+canExc);
                                              }else{
					    					     logger.debug("**********Start Update L normal ******************");
							    			     psUpdateD = ImportHelper.spiltLineArrayToUpdateStatement(conn, childBean, lineStr, psUpdateD,userBean);
									    	     canExc = psUpdateD.executeUpdate();
									    	     logger.debug("canUpdate L:"+canExc);
                                             }
					    				  }
					    				  
					    				  /** INSERT LINE **/
					    				  if(tableBean.getActionDB().indexOf("I") != -1 && !"".equals(childBean.getPrepareSqlIns())){
					    					  /** Create Receipt Line ONLY **/
                                              if(childBean.getTableName().startsWith( "t_receipt_line") ){  
					    					  }else{
						    					  if(canExc ==0){
									    			  logger.debug("**********Start Insert L normal  ******************");
									    			  if(childBean.getTableName().startsWith( "t_order_line") ){  
									    				  //validate product_id <> 0 or null position[6]
									    				  String[] lineArray = (lineStr+" ").split(Constants.delimeterPipe);
									    				  //logger.debug("iscancel:"+lineArray[5]);
									    				  //logger.debug("productId:"+lineArray[6]);
									    				  if( !Utils.isNull(lineArray[6]).equals("") && !Utils.isNull(lineArray[6]).equals("0")){
									    				      psInsD = ImportHelper.spiltLineArrayToInsertStatement(conn, childBean, lineStr, psInsD,userBean);
										    			      canExc = psInsD.executeUpdate();
												    	      logger.debug("orderLine canInsert L:"+canExc);
												    	  }else{
												    		  canExc = 1; 
												    	  }
									    			  }else{
									    				  psInsD = ImportHelper.spiltLineArrayToInsertStatement(conn, childBean, lineStr, psInsD,userBean);
										    			  canExc = psInsD.executeUpdate();
												    	  logger.debug("canInsert L:"+canExc);
									    			  }
									    			 
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
				    	      //logger.debug("errorRow["+errorRow+"]");
				    	     
				    	      // Add First Error To Save In monitor_item
				    	      if(firstErrorMsg.equals("")){
				    		     firstErrorMsg  = "NO UPDATE KEY NOT FOUND";
				    		     firstErrorCode = "NOUPDATE";
				    		  }
				    	     
				    	      //Insert Line to t_temp_import_trans
				    	      helper.updateStatusFailLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
				    	  }else{
				    		  errorCode = "";
				    		  successRow++;
				    		  //logger.debug("successRow["+successRow+"]");
				    		  //Insert Line to t_temp_import_trans
				    		  helper.updateStatusSuccessLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
				    		 
				    		  //set for return Calculate receipt Head
							  receiptNoAll += lineBean.getReceiptNo()+"|";
				    	  }
				    	
				    	  //initial
				    	  canExc = 0;
			    	  }//if 1
			       
		    	  }catch(Exception e){
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  logger.debug("errorRow["+errorRow+"]");
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    		  
		    		  logger.error(errorMsg,e);
		    		  logger.debug("firstErrorMsg:"+firstErrorMsg);
		    		  
		    		  // Add First Error To Save In monitor_item
		    		  if(firstErrorMsg.equals("")){
		    		     firstErrorMsg  = errorMsg;
		    		     firstErrorCode = errorCode;
		    		  } 
		    		  
		    		 //Insert Line to t_temp_import_trans
		    		  helper.updateStatusFailLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
		    	  }//Exception
				  
			  }//for lineStr array
			  
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
			    		        
			    		     // Re-Calculate TotalAmount,TotalAmountNonVat,vatAmount,netAmount
			    		     try{
				    		    Order orderUpdate =  new MOrder().reCalculateHeadAmountCaseImport(conn,orderId);
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
				  resultTxt.append("Total Rows :"+keyNoBean.getLineList().size()).append("\n");
				  resultTxt.append("Success Rows :"+successRow).append("\n");
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
		          	  fileBean.setFileName(keyNoBean.getFileName());
		          	  fileBean.setDataResultStr(resultTxt.toString());
		          	  dataStrBuffList.add(fileBean);
		          	  tableBean.setDataStrBuffList(dataStrBuffList);
		          }else{
		        	  FTPFileBean fileBean = new FTPFileBean();
			          fileBean.setFileName(keyNoBean.getFileName());
			          fileBean.setDataResultStr(resultTxt.toString());
		          	  tableBean.getDataStrBuffList().add(fileBean);
		          }
		          
			  }	
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
		
			 /** Return Result import **/
			 resultImportBean = new ResultImportBean();
			 resultImportBean.setFirstErrorMsg(firstErrorMsg);
			 resultImportBean.setFirstErrorCode(firstErrorCode);
			 resultImportBean.setSuccessRow(successRow);
			 resultImportBean.setErrorRow(errorRow);
			 resultImportBean.setAllRow(allRow);
			 resultImportBean.setReceiptNoAll(receiptNoAll);
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
	    return resultImportBean;
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
			
			/** Check Line is cancel All  and update order doc_status ='VO' */
			if(dao.isOrderLineCancelAll(conn, orderId)){
				logger.debug("Order Line Cancel All : doc_status ='VO'  ");
				dao.updateDocStatusInOrder(conn, orderId, "VO");
			}
				
		 return 0;
		}catch(Exception e){
			throw e;
		}
	}

}
