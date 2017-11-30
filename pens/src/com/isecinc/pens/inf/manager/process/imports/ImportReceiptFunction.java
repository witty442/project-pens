package com.isecinc.pens.inf.manager.process.imports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import util.ConvertNullUtil;

import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptCN;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.ReceiptMatchCN;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.CnNoNotFoundException;
import com.isecinc.pens.inf.exception.FindCustomerException;
import com.isecinc.pens.inf.exception.FindReceiptIdException;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportBean;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.helper.ValidateImportHelper;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptCN;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MReceiptMatchCN;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.ReceiptDocumentProcess;

public class ImportReceiptFunction {
	protected static  Logger logger = Logger.getLogger("PENS");
	public static Map<String, Receiptbatch> RECEIPT_BATCH_MAP = new HashMap<String, Receiptbatch>();
	public static Map<String, ReceiptFunctionBean> RECEIPT_HEAD_MAP = new HashMap<String, ReceiptFunctionBean>();
	public static Map<String, Double> RECEIPT_LINE_MAP = new HashMap<String, Double>();//<ReceipNo-arInvoiceNo,paidAmount>
	
	public int  processReceiptBatch(Connection conn,TableBean tableBean,User user,String lineStr,int canExc) throws Exception{
		//0 line type
		String batchNo = "";//1
		String receiptNo = "";//2
		double totalReceiptAmount = 0;//4
		String docStatus = "";
		logger.debug("fileName:"+tableBean.getFileFtpName());
		logger.debug("fileNameFull:"+tableBean.getFileFtpNameFull());
		try{
			String[] lineStrArray = (lineStr+" ").split(Constants.delimeterPipe);
			
			batchNo = Utils.isNull(lineStrArray[1]);
			receiptNo = Utils.isNull(lineStrArray[2]);
			totalReceiptAmount = Utils.convertStrToDouble(lineStrArray[4]);
			docStatus = Utils.isNull(lineStrArray[5]);
			
			Receiptbatch receiptBatch = new Receiptbatch();
			receiptBatch.setBatchNo(batchNo);
			receiptBatch.setReceiptNo(receiptNo);
			receiptBatch.setTotalReceiptAmount(totalReceiptAmount);
			receiptBatch.setDocStatus(docStatus);
			
			if("VO".equalsIgnoreCase(receiptBatch.getDocStatus())){
				logger.debug("#Case 0 VO all Batch#");
		
				/** Find Receipt Detail **/
				ReceiptFunctionBean receipt = findReceiptByReceiptNo(conn,receiptNo,""); 
				if(receipt ==null){
					throw new FindReceiptIdException("Cannot find ReceiptId By Receipt no["+receiptNo+"]");
				}
				logger.debug("#start update doc_status(VO) t_receipt #");
				updateCancelReceipt(conn, receipt,receiptBatch.getDocStatus());
			}else{
				//Check all line is vo all
				boolean isCancelAllReceipt = ImportReceiptHelper.isReceiptCancelAll(conn, receiptNo, tableBean.getFileFtpNameFull());
				
				if(isCancelAllReceipt){
					receiptBatch.setDocStatus("VO");
					/** Find Receipt Detail **/
					ReceiptFunctionBean receipt = findReceiptByReceiptNo(conn,receiptNo,""); 
					if(receipt ==null){
						throw new FindReceiptIdException("Cannot find ReceiptId By Receipt no["+receiptNo+"]");
					}
					logger.debug("#start update doc_status(VO) t_receipt #");
					updateCancelReceipt(conn, receipt,receiptBatch.getDocStatus());
				}
			}
			//Set Data To Receipt Batch Map (key =batchNo)
			RECEIPT_BATCH_MAP.put(receiptBatch.getBatchNo(), receiptBatch);
		    return 1;
		}catch(Exception e){
			throw e;
		}
	}
	public int  processReceiptHead(Connection conn,TableBean tableBean,User user,String lineStr,int canExc) throws Exception{
		ReceiptFunctionBean receipt = null;
		double totalReceiptAmountOracle =0;
		//0 Type line
		String batchNo = "";  //1
		String receiptNo = "";//2
		String interfaces = "";//3
		String docStatus = "";//4
		String customerCode = "";//5
		String receiptDate = "";//6
		double paidAmount = 0;//7
		String paymentMethod = "";//8
		String chequeNo = "";
		try{
			
			String[] lineStrArray = (lineStr+" ").split(Constants.delimeterPipe);
			
			/**Get Receipt For Set data To Receipt Line **/
			batchNo = Utils.isNull(lineStrArray[1]);
			receiptNo = Utils.isNull(lineStrArray[2]);
			interfaces =  Utils.isNull(lineStrArray[3]);
			docStatus =  Utils.isNull(lineStrArray[4]);
			customerCode =  Utils.isNull(lineStrArray[5]);
			receiptDate =  Utils.isNull(lineStrArray[6]);
			paidAmount =  Utils.convertStrToDouble(lineStrArray[7]);
			paymentMethod = Utils.isNull(lineStrArray[8]);
			
			logger.debug("paymentMethod["+paymentMethod+"]");
			
			/** Get Total Form Batch Line */
			Receiptbatch receiptBatch = RECEIPT_BATCH_MAP.get(batchNo); 
			totalReceiptAmountOracle = receiptBatch.getTotalReceiptAmount();
			
			if(paymentMethod.equalsIgnoreCase("à§Ô¹Ê´")){
				paymentMethod = "CS";
			}else if(paymentMethod.equalsIgnoreCase("Bank Transfer")){
				paymentMethod = "TR";
			}else if(paymentMethod.equalsIgnoreCase("àªç¤")){
				paymentMethod = "CH";
				chequeNo = receiptNo;
			}else{
				paymentMethod = "OT";
			}
			
			/** Find Receipt Detail **/
			receipt = findReceiptByReceiptNo(conn,receiptNo,paymentMethod); 
			
			/** receipt Not found Create New **/
			if(receipt != null && receipt.getReceiptId() != 0){
				logger.debug("DocStatus["+docStatus+"]");
				logger.debug("ReceiptAmountOracle["+totalReceiptAmountOracle+"]:["+receipt.getReceiptAmount()+"]ReceiptAmount(SalesApp)");
				logger.debug("CountReceiptBy["+receipt.getCountReceiptBy()+"]");
				
				/* Case 1 cancel all batch */
				if("VO".equalsIgnoreCase(receiptBatch.getDocStatus()) ){
					logger.debug("# Case 1 VO Cancel All Batch #");
					//Update Receipt docStatus VO
					canExc = updateCancelReceipt(conn,receipt,receiptBatch.getDocStatus());
					
					receipt.setCaseType("CANCEL_ALL_BATCH");
					
					//Clear ar_invoice_no for reuse
					int r =clearCNCaseCancelAllBatch(conn, receipt);
					logger.debug("result clear cn:"+r);
					
					canExc = 1;
					
				/* Case 2 Normal SV Receipt By =1	*/
			   }else if("VO".equalsIgnoreCase(docStatus) && receipt.getCountReceiptBy() ==1){
				   logger.debug("# Case 2 VO ReceiptBy = 1 #");
					//Update Receipt docStatus VO
					canExc = updateCancelReceipt(conn,receipt,docStatus);
					receipt.setCaseType("CANCEL_NORMAL");
					canExc = 1;
					
					//Clear ar_invoice_no for reuse
					int r = clearCNCaseCancelAllBatch(conn, receipt);
					logger.debug("result clear cn:"+r);
					
				/* Case 3 VO ReceiptBy > 1 	*/
				}else if("VO".equalsIgnoreCase(docStatus) && receipt.getCountReceiptBy() >1){
					logger.debug("# Case 3 VO ReceiptBy > 1 #");
					 receipt.setCaseType("CANCEL_MORE_RECEIPT");
					 
					 //move to receipt_history
					 logger.debug("# Start move to receipt_line_his By receipy_by_id["+receipt.getReceiptById()+"] #");
					 copyReceiptLineToReceiptLineHisByReceiptById(conn, receipt);
					   
					 logger.debug("# Start delete receipt_line By receipy_by_id["+receipt.getReceiptById()+"]#");
					 //delete t_receipt_cn
					 //delete t_receipt_match_cn
					 //delete t_receipt_line
					 //delete t_receipt_match
					 //delete t_receipt_by
					 deleteReceiptByReceiptByIdCaseVOReceiptByMoreOne(conn,receipt.getReceiptId(),receipt.getReceiptById());
					
					//Clear ar_invoice_no for reuse Case Receipt more 1
					int r =clearCNCaseCancelByReceiptById(conn, receipt);
					logger.debug("result clear cn:"+r);
					
					canExc = 1;
					 
				 /* Case 4 SV ReceiptBy > 1 	*/
				}else if("SV".equalsIgnoreCase(docStatus) && receipt.getCountReceiptBy() >1){
					logger.debug("# Case 4 SV ReceiptBy > 1 #");
					if(totalReceiptAmountOracle==receipt.getReceiptAmount()){
						 receipt.setCaseType("RECEIPT_EQUAL_AMOUNT");
						 logger.debug("Case4.1 (SalesApp)Amount = Amount(oracle");
					
						 logger.debug("# Start delete receipt_line By receipy_by_id["+receipt.getReceiptById()+"]#");
						 //delete t_receipt_cn
						 //delete t_receipt_match_cn
						 //delete t_receipt_line
						 //delete t_receipt_match
						 deleteReceiptByReceiptByIdCaseSVReceiptByMoreOne(conn,receipt.getReceiptId(),receipt.getReceiptById());
						
						 //Update t_receipt (by receiptNo)
						 logger.debug("#start update amount t_receipt #");
						 updateReceipt(conn, receipt, paidAmount);
						 
						 logger.debug("#start update or insert amount receipt by #");
						  if(receipt.getReceiptById() ==0){
							  receipt.setPaymentMethod(paymentMethod);
							  receipt.setChequeNo(chequeNo);
							  createReceiptByModel(conn, receipt, paidAmount);
						  }else{
						      updateReceiptByModel(conn, receipt, paidAmount);
						  }
						 canExc = 1;
					
					//Case 4.2 NOT_EQUAL_AMOUNT
					}else if(totalReceiptAmountOracle != receipt.getReceiptAmount()){
					   logger.debug("Case4.2 (SalesApp)Amount<> Amount(oracle");
					   receipt.setCaseType("RECEIPT_NOT_EQUAL_AMOUNT");
					   
					   //move to receipt_history
						logger.debug("# Start move to receipt_line By receipy_by_id["+receipt.getReceiptById()+"] #");
						copyReceiptLineToReceiptLineHisByReceiptById(conn, receipt);
						   
						logger.debug("# Start delete receipt_line By receipy_by_id["+receipt.getReceiptById()+"]#");
						//delete t_receipt_cn
						//delete t_receipt_match_cn
						//delete t_receipt_line
						//delete t_receipt_match
						//delete t_receipt_by
						deleteReceiptByReceiptByIdCaseSVReceiptByMoreOne(conn,receipt.getReceiptId(),receipt.getReceiptById());
						
						//Update t_receipt (by receiptNo)
						logger.debug("#start update amount t_receipt #");
						updateReceipt(conn, receipt, paidAmount);
						   
						logger.debug("#start update or insert amount receipt by receipy_by_id["+receipt.getReceiptById()+"] #");
					     if(receipt.getReceiptById() ==0){
						    receipt.setPaymentMethod(paymentMethod);
						    receipt.setChequeNo(chequeNo);
						    createReceiptByModel(conn, receipt, paidAmount);
					     }else{
					        updateReceiptByModel(conn, receipt, paidAmount);
					     }
						canExc = 1;
				    }
				 /** Case SV Normal ReceiptBy = 1 */
				}else{
					logger.debug("# Case SV ReceiptBy = 1 #");
					//Case 5 EQUAL_AMOUNT 
					if(totalReceiptAmountOracle==receipt.getReceiptAmount()){
					   receipt.setCaseType("RECEIPT_EQUAL_AMOUNT");
					   logger.debug("#Case5 (SalesApp)Amount = Amount(oracle)#");
					   
					   logger.debug("# Start delete receipt_line By receipy_by_id["+receipt.getReceiptById()+"]#");
					   //delete t_receipt_cn
					   //delete t_receipt_match_cn
					   //delete t_receipt_line
					   //delete t_receipt_match
					   deleteReceiptByReceiptByIdCaseSVReceiptByMoreOne(conn,receipt.getReceiptId(),receipt.getReceiptById());
						
					   //Update t_receipt (by receiptNo)
					   logger.debug("#start update amount t_receipt #");
					   updateReceipt(conn, receipt, totalReceiptAmountOracle);
					   
					   //Update or insert t_receipt_by (by receiptNo)
					   if(receipt.getReceiptById() ==0){
						   receipt.setPaymentMethod(paymentMethod);
						   receipt.setChequeNo(chequeNo);
						   createReceiptByModel(conn, receipt, paidAmount);
					   }else{
					       updateReceiptByModel(conn, receipt, paidAmount);
					   }
					   canExc = 1;
					   
					//Case 6 NOT_EQUAL_AMOUNT
					}else if(totalReceiptAmountOracle != receipt.getReceiptAmount()){
					   logger.debug("#Case6 (SalesApp)Amount<> Amount(oracle) #");
					   receipt.setCaseType("RECEIPT_NOT_EQUAL_AMOUNT");
					   
					   //move to receipt_history
					   logger.debug("# Start move to receipt_line By receipy_by_id["+receipt.getReceiptById()+"] #");
					   copyReceiptLineToReceiptLineHisByReceiptById(conn, receipt);
						   
					   logger.debug("# Start delete receipt_line By receipy_by_id["+receipt.getReceiptById()+"]#");
					   //delete t_receipt_cn
					   //delete t_receipt_match_cn
				       //delete t_receipt_line
					   //delete t_receipt_match
					   deleteReceiptByReceiptByIdCaseSVReceiptByMoreOne(conn,receipt.getReceiptId(),receipt.getReceiptById());
						
					   //Update t_receipt (by receiptId)
					   logger.debug("#start update amount t_receipt #");
					   updateReceipt(conn, receipt, totalReceiptAmountOracle);
					   
					   //Update or insert t_receipt_by (by receiptNo)
					   if(receipt.getReceiptById() ==0){
						   receipt.setPaymentMethod(paymentMethod);
						   receipt.setChequeNo(chequeNo);
						   createReceiptByModel(conn, receipt, paidAmount);
					   }else{
					       updateReceiptByModel(conn, receipt, paidAmount);
					   }
					   canExc = 1;
					}
					
					logger.debug("*********processReceiptHead********");
					logger.debug("CaseType:"+receipt.getCaseType());
					logger.debug("ReceiptId:"+receipt.getReceiptId());
					logger.debug("ReceiptLineId:"+receipt.getReceiptLineId());
					logger.debug("ReceiptById:"+receipt.getReceiptById());
					logger.debug("ReceiptByIdWriteOff:"+receipt.getReceiptByIdWriteOff());
					logger.debug("ReceiptMatchIdWriteOff:"+receipt.getReceiptMatchIdWriteOff());
					logger.debug("*********/processReceiptHead*******");
				  }
			}else{ 
				
				logger.debug("Create New Receipt");
				logger.debug("#Start Create t_receipt #");
				Receipt receiptSave = new Receipt();
				//Gen Receipt No New
				receiptSave.setId(0);
				receiptSave.setReceiptNo(receiptNo);
				receiptSave.setOrderType("CR");
				receiptSave.setExported("Y");
				receiptSave.setPrepaid("N");
				receiptSave.setInterfaces(interfaces);
				receiptSave.setDocStatus(docStatus);
				logger.debug("fileName:"+tableBean.getFileFtpNameFull());
				if(Utils.isNull(tableBean.getFileFtpName()).indexOf("RECEIPTORCL") != -1){
				   receiptSave.setDescription(receiptNo);
				}
				//Get Customer
				Customer cust = findCustomer(conn,customerCode);
				if(cust != null){
				   receiptSave.setCustomerId(cust.getId());
				   receiptSave.setCustomerName(cust.getName());
				}else{
				   throw new FindCustomerException("Customer Code["+customerCode+"] not found in m_customer");
				}
				receiptSave.setReceiptDate(Utils.stringValue(Utils.parse(Utils.isNull(receiptDate), Utils.DD_MM_YYYY_WITHOUT_SLASH), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				receiptSave.setReceiptAmount(totalReceiptAmountOracle);
				receiptSave.setApplyAmount(totalReceiptAmountOracle);
				receiptSave.setPaymentMethod(paymentMethod);
				receiptSave.setSalesRepresent(user);
				try{
			      //save to DB
				  new MReceipt().save(receiptSave, 999, conn);
				}catch(Exception ee){
					ee.printStackTrace();
				}
				logger.debug("#Start Create t_receipt_by #");
				ReceiptBy receiptBySave = new ReceiptBy();
				receiptBySave.setReceiptId(receiptSave.getId());
				receiptBySave.setPaymentMethod(paymentMethod);
				receiptBySave.setChequeNo(chequeNo);
				receiptBySave.setChequeDate("");
				receiptBySave.setBank("");
				receiptBySave.setReceiptAmount(paidAmount);// Get from Line H
				receiptBySave.setPaidAmount(paidAmount);// Get from Line H
				//save to DB
				new MReceiptBy().save(receiptBySave, 999, conn);
				
				//Set data for Receipt Line (Map)
				receipt = new ReceiptFunctionBean();
				receipt.setCaseType("RECEIPT_NEW");
				receipt.setReceiptNo(receiptSave.getReceiptNo());
				receipt.setReceiptId(receiptSave.getId());
				receipt.setReceiptById(receiptBySave.getId());
				
				canExc = 1;
				
				logger.debug("After Create New Receipt ReceiptId["+receiptSave.getId()+"]");
			}//if
				
			//Set Detail For Receipt line (receiptNo ,cheueNo)
			logger.debug("PutToMap ReceiptId:"+receipt.getReceiptId());
			RECEIPT_HEAD_MAP.put(receiptNo, receipt);
		
		}catch(Exception e){
			throw e;
		}
		return canExc;
	}
	
	public int  processReceiptLine(Connection conn,TableBean tableBean,User user,String lineStr,PreparedStatement ps,int canExc) throws Exception{
		String batchNo = "";
		String receiptNo = "";
		String arInvoiceNo = "";
		String salesOrderNo = "";
		double paidAmount = 0;
		try{
			 String[] lineStrArray = (lineStr+" ").split(Constants.delimeterPipe);
			 //0 :L
			 batchNo = lineStrArray[1];
			 receiptNo = lineStrArray[2];
			 arInvoiceNo = Utils.isNull(lineStrArray[3]);
			 salesOrderNo = Utils.isNull(lineStrArray[4]);
			 paidAmount = Utils.convertStrToDouble(lineStrArray[5]);
			 
			 /** Get Receipt From Line Head By ReceiptNo**/
			 logger.debug("keyNo["+batchNo+"]");
			 logger.debug("receiptNo["+receiptNo+"]");
			 ReceiptFunctionBean receiptHead = RECEIPT_HEAD_MAP.get(receiptNo);
			
			 logger.debug("CaseType:"+receiptHead.getCaseType());
			 
			 /** Case Cancel VO and cancel some receiotBy No insert Or update Receipt line **/
			 if(    receiptHead.getCaseType().equalsIgnoreCase("CANCEL_MORE_RECEIPT")
			     || receiptHead.getCaseType().equalsIgnoreCase("CANCEL_NORMAL")
				 || receiptHead.getCaseType().equalsIgnoreCase("CANCEL_ALL_BATCH")){
				 
				 canExc = 1; 
				 logger.debug("Case Cancel canExe ="+canExc);
	         }else{
				
	        	 //Case 1 Write Off
				 if(Utils.isNull(lineStrArray[3]).indexOf("Receipt Write-off") != -1){
					 //insert receipt_line
					 logger.debug("**********L (Case Write Off) **********");
					 canExc = new ImportReceiptFunction().createReceiptLineWriteOff(conn,user,lineStrArray,receiptHead);
					 logger.debug("can insert Write Off:"+canExc);
	
	        	 //Case 2 Receipt Match CN = (paid_amount < 0(negative)) (lineStr position =4
				 }else if(Utils.convertStrToDouble(lineStrArray[5]) < 0){
					 logger.debug("****** Match CN :PaidAmount["+paidAmount+"]");
				     canExc= new ImportReceiptFunction().createReceiptLineMatchCN(conn,user,lineStrArray,receiptHead);
					 logger.debug("can insert Match CN:"+canExc);
				 }else {
					 //Case 3 Normal
					 logger.debug("Start Insert Receipt Line and Match Case normal");
					 logger.debug("ReceiptId:"+receiptHead.getReceiptId());
					 logger.debug("ReceiptById:"+receiptHead.getReceiptById());
					 
					 //Get Old detail
					 if(receiptHead != null && receiptHead.getReceiptId() != 0 && receiptHead.getReceiptById()!=0){
						 if(   receiptHead.getCaseType().equalsIgnoreCase("RECEIPT_EQUAL_AMOUNT")
							|| receiptHead.getCaseType().equalsIgnoreCase("RECEIPT_NOT_EQUAL_AMOUNT")	
							|| receiptHead.getCaseType().equalsIgnoreCase("RECEIPT_MORE_RECEIPT") 
							|| receiptHead.getCaseType().equalsIgnoreCase("RECEIPT_NEW") 
								 ){
							    //Case ArInvoceNo 1 2 paymentMethod (paidAmount=1+2)
							     
							     double sumPaidAmountByArInvoiceNo = 0;
							     String key = receiptHead.getReceiptNo()+"-"+arInvoiceNo;
							     logger.debug("Key SumPaidAmount:"+key);
								 if(RECEIPT_LINE_MAP.get(key) == null){
									 sumPaidAmountByArInvoiceNo = paidAmount;
									 RECEIPT_LINE_MAP.put(key, sumPaidAmountByArInvoiceNo);
								 }else{
									 if(RECEIPT_LINE_MAP.get(key) != null){
									    sumPaidAmountByArInvoiceNo = RECEIPT_LINE_MAP.get(key)+paidAmount;
									    RECEIPT_LINE_MAP.put(key, sumPaidAmountByArInvoiceNo);
									 }
								 }
							 
							     //Calc Amount By Ar invoice
							     ReceiptDataLine receiptDataLine = calcReceiptLineAmount(conn,receiptHead.getReceiptId(),arInvoiceNo, paidAmount);
						    	 //insert receipt_line
						    	 ReceiptLine receiptLine = new ReceiptLine();
						    	 receiptLine.setReceiptId(receiptHead.getReceiptId());
						    	 receiptLine.setLineNo(findLineNoReceiptLine(conn,receiptHead.getReceiptId()));
						    	 receiptLine.setArInvoiceNo(arInvoiceNo);
						    	 receiptLine.setSalesOrderNo(salesOrderNo);
						    	 receiptLine.setInvoiceAmount(receiptDataLine.getInvoiceAmount());
						    	 receiptLine.setCreditAmount(receiptDataLine.getCreditAmount());
						    	 receiptLine.setPaidAmount(sumPaidAmountByArInvoiceNo);
						    	 receiptLine.setRemainAmount(receiptDataLine.getRemainAmount());
						    	 Order order = new Order();
						    	 order.setId(findOrderId(conn, arInvoiceNo));//ar_invoice_no
						    	 receiptLine.setOrder(order);
						    	 
						    	 OrderLine orderLine = new OrderLine();
						    	 orderLine.setId(findOrderLineId(conn, arInvoiceNo));
						    	 receiptLine.setOrderLine(orderLine);
						    	 boolean saveLine = new MReceiptLine().save(receiptLine, 999, conn);
						    	 
						    	 //insert receipt_match
						    	 ReceiptMatch match = new ReceiptMatch();
								 match.setReceiptLineId(receiptLine.getId());
								 match.setReceiptById(receiptHead.getReceiptById());
								 match.setReceiptId(receiptHead.getReceiptId());
								 match.setPaidAmount(paidAmount);
							
								boolean saveMatch =  new MReceiptMatch().save(match,999, conn);
						        if(saveLine && saveMatch){
						        	canExc = 1;
						        }
						        logger.debug("Can Insert Normal :"+canExc);
						 }
					 }
				 }
	         }
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return canExc;
	}
	
	public int  createReceiptLineWriteOff(Connection conn,User user,String[] lineStrArr,ReceiptFunctionBean receiptHead) throws Exception{
		int canExc = 0;
		double amount  =0;
		try{
			//get Min ReceiptLineId for apply Match Write Off
			receiptHead = findReceiptLine(conn,receiptHead);
			
			logger.debug("*** Start createReceiptLineWriteOff **");
			logger.debug("CaseType:"+receiptHead.getCaseType());
			logger.debug("receiptId:"+receiptHead.getReceiptId());
			logger.debug("receiptByIdWriteOff:"+receiptHead.getReceiptByIdWriteOff());
			logger.debug("receiptMatchIdWriteOff:"+receiptHead.getReceiptMatchIdWriteOff());
			logger.debug("receiptLineId:"+receiptHead.getReceiptLineId());
			logger.debug("*********************************************");
			
			amount = Utils.convertStrToDouble(lineStrArr[5]);
		
			 if( receiptHead.getReceiptLineId() != 0 ){ 
			  if(receiptHead.getReceiptByIdWriteOff() != 0
				  && receiptHead.getReceiptMatchIdWriteOff() != 0){
					logger.debug("Update Write Off");
			    	
					ReceiptBy receiptBy = new ReceiptBy();
					receiptBy.setId(receiptHead.getReceiptByIdWriteOff());
					receiptBy.setReceiptAmount(amount);
					receiptBy.setPaidAmount(amount);
					receiptBy.setRemainAmount(0);
					receiptBy.setReceiptId(receiptHead.getReceiptId());
					receiptBy.setWriteOff("Y");
					receiptBy.setPaymentMethod("CS");
					receiptBy.setChequeDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					
					boolean saveReceiptBy = new MReceiptBy().save(receiptBy, 999, conn);	
					logger.debug("receiptById:"+receiptBy.getId());
					
					ReceiptMatch receiptMatch = new ReceiptMatch();
					receiptMatch.setId(receiptHead.getReceiptMatchIdWriteOff());
					receiptMatch.setReceiptId(receiptBy.getReceiptId());
					receiptMatch.setReceiptLineId(receiptHead.getReceiptLineId());
					receiptMatch.setReceiptById(receiptBy.getId());
					receiptMatch.setPaidAmount(amount);
					boolean saveReceiptMatch = new MReceiptMatch().save(receiptMatch, 999, conn);
					logger.debug("receiptLineId:"+receiptHead.getReceiptLineId());
					
					if(saveReceiptBy && saveReceiptMatch){
						canExc = 1;
					}
					logger.debug("canUpdate Writeoff Line:"+canExc);
			  }else{
				   logger.debug("Insert Write Off");
			    	
					ReceiptBy receiptBy = new ReceiptBy();
					receiptBy.setReceiptAmount(amount);
					receiptBy.setPaidAmount(amount);
					receiptBy.setRemainAmount(0);
					receiptBy.setReceiptId(receiptHead.getReceiptId());
					receiptBy.setWriteOff("Y");
					receiptBy.setPaymentMethod("CS");
					receiptBy.setChequeDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					
					boolean saveReceiptBy = new MReceiptBy().save(receiptBy, 999, conn);	
					logger.debug("receiptById:"+receiptBy.getId());
					
					ReceiptMatch receiptMatch = new ReceiptMatch();
					receiptMatch.setId(receiptHead.getReceiptMatchIdWriteOff());
					receiptMatch.setReceiptId(receiptBy.getReceiptId());
					receiptMatch.setReceiptLineId(receiptHead.getReceiptLineId());
					receiptMatch.setReceiptById(receiptBy.getId());
					receiptMatch.setPaidAmount(amount);
					boolean saveReceiptMatch = new MReceiptMatch().save(receiptMatch, 999, conn);
					
					if(saveReceiptBy && saveReceiptMatch){
						canExc = 1;
					}
					logger.debug("canInsert Writoff Line:"+canExc);
			  }
			  
			  // Update paidAmount (t_receipt_line) by sum(paid_amount) t_receipt_match (receipt_line_id)
			  ImportReceiptHelper.updatePaidAmountRecepitLine(conn, receiptHead);
		 }
			
		}catch(Exception e){
			throw e;
		}
		return canExc;
	}
	
	public int  createReceiptLineMatchCN(Connection conn,User user ,String[] lineStrArr,ReceiptFunctionBean receiptHead) throws Exception{
		int canExc=0;
		String cnNo = "";
		double amount = 0;
		try{
			cnNo = Utils.isNull(lineStrArr[3]);
			
			//Find ID Exist
			ReceiptFunctionBean receiptExist = findReceiptMatchCNDetail(conn,receiptHead.getReceiptId(),cnNo);   
			if(receiptExist !=null && receiptExist.getReceiptById()==0){
				receiptExist.setReceiptById(receiptHead.getReceiptById());
			}
			logger.debug("*** Start createReceiptLineMatchCN **");
			logger.debug("CaseType:"+receiptHead.getCaseType());
			logger.debug("receiptId:"+receiptExist.getReceiptId());
			logger.debug("receiptById:"+receiptExist.getReceiptById());
			logger.debug("CreditNoteNo:"+cnNo);
			logger.debug("creditNoteId:"+receiptExist.getCreditNoteId());
			logger.debug("*********************************************");

			amount = Utils.convertStrToDouble(lineStrArr[5]);
			
			if(receiptExist.getCreditNoteId()==0){
				throw new CnNoNotFoundException("CreditNoteNo["+cnNo+"]not found in master");
			}
			
			if(    receiptExist.getReceiptId() != 0
				&& receiptExist.getReceiptById() != 0
				&& receiptExist.getCreditNoteId() != 0){
				
				logger.debug("Insert Receipt CN");
				ReceiptCN receiptCN = new ReceiptCN();
				receiptCN.setId(0);
				receiptCN.setReceiptId(receiptExist.getReceiptId());
				receiptCN.setPaidAmount(amount);
				receiptCN.setCreditAmount(amount);
				//set credit Note id
				CreditNote creditNote = new CreditNote();
				creditNote.setId(receiptExist.getCreditNoteId());
				receiptCN.setCreditNote(creditNote);
				boolean saveReceiptBy = new MReceiptCN().save(receiptCN, user.getId(), conn);

				logger.debug("Insert Receipt Match CN");
				ReceiptMatchCN receiptMatchCN = new ReceiptMatchCN();
				receiptMatchCN.setReceiptId(receiptExist.getReceiptId());
				receiptMatchCN.setReceiptCNId(receiptCN.getId());
				receiptMatchCN.setReceiptById(receiptExist.getReceiptById());
				receiptMatchCN.setPaidAmount(amount);
				boolean saveReceiptMatch = new MReceiptMatchCN().save(receiptMatchCN, user.getId(), conn);
				
				//Update ar_invoice_no in t_credit_note By credit_note_no
				//Case Apply CN in Receipt set ar_invoice_no = receipt_no 
				updateArInvoiceNoInCreditNote(conn, receiptHead, cnNo);
				
				if(saveReceiptBy && saveReceiptMatch){
					canExc = 1;
				}
			}
			
		}catch(Exception e){
			throw e;
		}
		return canExc;
	}
	
	public ReceiptFunctionBean findReceiptMatchCNDetail(Connection conn,int receiptId,String creditNoteNo) 
			throws Exception{
		ReceiptFunctionBean receipt = new ReceiptFunctionBean();
		PreparedStatement ps =null;
		ResultSet rs = null;
		String sql  ="";
		try{
		   sql  = "\n select receipt_id ";
		   sql += "\n ,(";
		   sql += "\n    select mcn.receipt_by_id from t_receipt_cn rcn ,t_receipt_match_cn mcn";
		   sql += "\n    where rcn.receipt_cn_id = mcn.receipt_cn_id";
		   sql += "\n    and rcn.receipt_id = "+receipt.getReceiptId();
		   sql += "\n    and rcn.credit_note_id in(";
		   sql += "\n       select credit_note_id from t_credit_note where credit_note_no ='"+creditNoteNo+"'";
		   sql += "\n    )";
		   sql += "\n ) as receipt_by_id";
		   sql += "\n,(";
		   sql += "\n   select credit_note_id from t_credit_note where credit_note_no ='"+creditNoteNo+"'";
		   sql += "\n ) as credit_note_id ";
		   sql += "\n FROM t_receipt r WHERE receipt_id = '"+receiptId+"' " ;		

			logger.debug("sql:"+sql);
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				receipt.setReceiptId(rs.getInt("receipt_id"));
				receipt.setCreditNoteId(rs.getInt("credit_note_id"));
				receipt.setReceiptById(rs.getInt("receipt_by_id"));
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receipt;
	}
	public int findReceiptByIdByReceiptMatch(Connection conn,int receiptId) throws Exception{
		int receiptById = 0;
		PreparedStatement ps =null;
		ResultSet rs = null;
		String sql ="";
		try{
			 sql += "\n  select min(m.receipt_by_id) as receipt_by_id" ;
			 sql += "\n  from t_receipt r ,t_receipt_match m ,t_receipt_by b";
			 sql += "\n  where r.receipt_id = m.receipt_id";
			 sql += "\n  and b.receipt_by_id = m.receipt_by_id";
			 sql += "\n  and b.write_off <> 'Y'";
			 sql += "\n  and r.receipt_id = "+receiptId;
				   
			logger.debug("sql:"+sql);
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				receiptById = rs.getInt("receipt_by_id");
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receiptById;
	}
	
	public ReceiptFunctionBean findReceiptByReceiptNo(Connection conn,String receiptNo,String paymentMethod) throws Exception{
		ReceiptFunctionBean receipt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select A.receipt_id, A.receipt_no,A.receipt_date");
			sql.append(" ,A.order_type,A.customer_id,A.customer_name,A.receipt_amount,A.apply_amount");
			sql.append(" ,(");
			sql.append("     select count(distinct payment_method,cheque_no) from t_receipt_by where receipt_by_id in( \n");
			sql.append("     select receipt_by_id from t_receipt r , t_receipt_match m \n");
			sql.append("       where r.receipt_id = m.receipt_id \n");
			sql.append("       and r.receipt_id = A.receipt_id \n");
			sql.append("     ) and write_off <> 'Y' \n");
			sql.append("  ) count_receipt_by \n");
			sql.append(" FROM ( \n");
			sql.append(" 	select t.receipt_id,receipt_no,receipt_date");
			sql.append(" 	,t.order_type,t.customer_id,t.customer_name,t.receipt_amount,t.apply_amount");
			sql.append(" 	FROM t_receipt t WHERE t.receipt_no = '"+receiptNo+"' \n" );
			sql.append(" 	union \n" );
			sql.append(" 	select t.receipt_id,receipt_no,receipt_date");
			sql.append(" 	,t.order_type,t.customer_id,t.customer_name,t.receipt_amount,t.apply_amount");
			sql.append(" 	FROM t_receipt t WHERE t.receipt_id in( \n" );
			sql.append("   	select distinct m.receipt_id  from t_receipt_match m  \n");
			sql.append("    	where m.receipt_by_id in ( \n" );
			sql.append("  	  	  select receipt_by_id from t_receipt_by  where cheque_no ='"+receiptNo+"' \n" );
			sql.append("   	   ) \n" );
			sql.append("  	) \n" );
			sql.append(")A  \n" );
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				receipt = new ReceiptFunctionBean();
				receipt.setReceiptId(rs.getInt("receipt_id"));
				receipt.setReceiptNo(rs.getString("receipt_no"));
				receipt.setReceiptDate(Utils.stringValue(rs.getDate("receipt_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				receipt.setOrderType(Utils.isNull(rs.getString("order_type")));
				receipt.setCustomerId(rs.getInt("customer_id")+"");
				receipt.setCustomerName(Utils.isNull(rs.getString("customer_name")));
				receipt.setReceiptAmount(rs.getDouble("receipt_amount"));
				receipt.setPaidAmount(rs.getDouble("apply_amount"));
				receipt.setCountReceiptBy(rs.getInt("count_receipt_by"));
				
				if(Utils.isNull(paymentMethod).equalsIgnoreCase("CH")){
					//ReceiptNo = ChequeNo
					receipt.setChequeNo(receiptNo);
				}
				
				// getReceipt(write_off <> Y) By detail For insert new
				receipt = findReceiptByDetail(conn,receipt,paymentMethod);
				
				// getReceiptById (Write_off =Y)
				receipt = findReceiptByIdWriteOff(conn,receipt);

			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receipt;
	}
	public int updateReceipt(Connection conn,ReceiptFunctionBean receipt,double paidAmountOracle) throws Exception{
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		try{
			sql.append("\n update t_receipt set receipt_amount = "+receipt.getReceiptAmount());
			sql.append("\n ,apply_amount ="+paidAmountOracle );
			sql.append("\n ,created =sysdate(),created_by =999,updated =null ,updated_by =0 ,INTERFACES='Y' ");
			sql.append("\n where receipt_id ="+receipt.getReceiptId());
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			r = ps.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
		}
		return r;
	}
	
	//Case Apply Cn in Receipt set ar_invoice_no = receipt_no 
	public int updateArInvoiceNoInCreditNote(Connection conn,ReceiptFunctionBean receipt,String cnNo) throws Exception{
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		try{
			logger.debug("# Start updateArInvoiceNoCreditNote #");
			
			sql.append("\n update t_credit_note set ar_invoice_no = '"+receipt.getReceiptNo()+"'");
			sql.append("\n where credit_note_no ='"+cnNo+"'");
			sql.append("\n and ( ar_invoice_no is null or ar_invoice_no ='')");
			
			logger.debug("sql:"+sql.toString());
			
			ps =conn.prepareStatement(sql.toString());
			r = ps.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
		}
		return r;
	}
	
/**
 * Desc:Clear ar_invoice_no = null Case cancel all receipt
 * @param conn
 * @param receipt
 * @return
 * @throws Exception
 */
	public int clearCNCaseCancelAllBatch(Connection conn,ReceiptFunctionBean receipt) throws Exception{
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		try{
			logger.debug("# Start clearCNCaseCancelAllBatch #");
			
			sql.append("\n update t_credit_note set ar_invoice_no = ''");
			sql.append("\n where credit_note_id in(");
			sql.append("\n   select credit_note_id from t_receipt_cn ");
			sql.append("\n   where receipt_id = "+receipt.getReceiptId());
			sql.append("\n )");
			logger.debug("sql:"+sql.toString());
			
			ps =conn.prepareStatement(sql.toString());
			r = ps.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
		}
		return r;
	}
	
	/**
	 * Desc:Clear ar_invoice_no = null Case cancel all receipt
	 * @param conn
	 * @param receipt
	 * @return
	 * @throws Exception
	 */
		public int clearCNCaseCancelByReceiptById(Connection conn,ReceiptFunctionBean receipt) throws Exception{
			PreparedStatement ps =null;
			StringBuffer sql = new StringBuffer("");
			int r = 0;
			try{
				logger.debug("# Start clearCNCaseCancelAllBatch #");
				
				sql.append("\n update t_credit_note set ar_invoice_no = ''");
				sql.append("\n where credit_note_id in (");
				sql.append("\n   select cn.credit_note_id from t_receipt_cn cn,t_receipt_match_cn mcn ");
				sql.append("\n   where cn.receipt_cn_id = mcn.receipt_cn_id");
				sql.append("\n   and  mcn.receipt_by_id = "+receipt.getReceiptById());
				sql.append("\n   and  cn.receipt_id = "+receipt.getReceiptId());
				sql.append("\n )");
				logger.debug("sql:"+sql.toString());
				
				ps =conn.prepareStatement(sql.toString());
				r = ps.executeUpdate();
			}catch(Exception e){
				logger.error(e.getMessage());
			}finally{
				if(ps !=null){
					ps.close();
				}
			}
			return r;
		}
	public int updateCancelReceipt(Connection conn,ReceiptFunctionBean receipt,String docStatus) throws Exception{
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		try{
			sql.append("\n update t_receipt set doc_status ='"+docStatus+"'");
			sql.append("\n ,updated =sysdate() ,updated_by =999 ,INTERFACES='Y' ");
			sql.append("\n where receipt_id ="+receipt.getReceiptId());
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			r = ps.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
		}
		return r;
	}
	@Deprecated
	public ReceiptFunctionBean copyReceiptToReceiptHis(Connection conn,ReceiptFunctionBean receipt) throws Exception{
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		try{
			//Gen new TransId by import
			long transId = SequenceProcess.getNextValue("TEMP_RECEIPT");
			logger.debug("TransId:"+transId);
			
			/* # insert t_receipt_his # */
			logger.debug("# insert t_receipt_his #");
			sql.append("\n insert into t_receipt_his ");
			sql.append("\n SELECT");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_ID , ");
			sql.append("\n RECEIPT_NO , ");
			sql.append("\n RECEIPT_DATE , ");
			sql.append("\n ORDER_TYPE, ");
			sql.append("\n CUSTOMER_ID, ");
			sql.append("\n CUSTOMER_NAME, ");
			sql.append("\n PAYMENT_METHOD, ");
			sql.append("\n BANK, ");
			sql.append("\n CHEQUE_NO, ");
			sql.append("\n CHEQUE_DATE, ");
			sql.append("\n RECEIPT_AMOUNT, ");
			sql.append("\n INTERFACES, ");
			sql.append("\n DOC_STATUS, ");
			sql.append("\n USER_ID, ");
			sql.append("\n CREATED, ");
			sql.append("\n CREATED_BY, ");
			sql.append("\n UPDATED, ");
			sql.append("\n UPDATED_BY, ");
			sql.append("\n CREDIT_CARD_TYPE, ");
			sql.append("\n DESCRIPTION, ");
			sql.append("\n PREPAID, ");
			sql.append("\n APPLY_AMOUNT, ");
			sql.append("\n EXPORTED, ");
			sql.append("\n INTERNAL_BANK, ");
			sql.append("\n ISPDPAID, ");
			sql.append("\n PDPAID_DATE, ");
			sql.append("\n PD_PAYMENTMETHOD, ");
			sql.append("\n TEMP2_EXPORTED ");
			sql.append("\n from t_receipt ");
			sql.append("\n where receipt_id ="+receipt.getReceiptId() +";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/* # insert t_receipt_line_his # */
			logger.debug("# insert t_receipt_line_his #");
			sql = new StringBuffer();
			sql.append("\n insert into t_receipt_line_his ");
			sql.append("\n select ");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_LINE_ID,");
			sql.append("\n LINE_NO ,");
			sql.append("\n RECEIPT_ID,");
			sql.append("\n AR_INVOICE_NO,");
			sql.append("\n SALES_ORDER_NO,");
			sql.append("\n INVOICE_AMOUNT,");
			sql.append("\n CREDIT_AMOUNT,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n REMAIN_AMOUNT,");
			sql.append("\n ORDER_ID,");
			sql.append("\n ORDER_LINE_ID,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n CREATED_BY,");
			sql.append("\n CREATED,");
			sql.append("\n DESCRIPTION");
			sql.append("\n from t_receipt_line ");
			sql.append("\n where receipt_id ="+receipt.getReceiptId()+";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/** # insert t_receipt_match_his # */
			logger.debug("# insert t_receipt_match_his #");
			sql = new StringBuffer();
			sql.append("\n INSERT INTO t_receipt_match_his ");
			sql.append("\n SELECT ");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_MATCH_ID, ");
			sql.append("\n RECEIPT_BY_ID, ");
			sql.append("\n RECEIPT_LINE_ID, ");
			sql.append("\n PAID_AMOUNT, ");
			sql.append("\n CREATED, ");
			sql.append("\n CREATED_BY, ");
			sql.append("\n UPDATED, ");
			sql.append("\n UPDATED_BY, ");
			sql.append("\n RECEIPT_ID ");
			sql.append("\n FROM t_receipt_match");
			sql.append("\n where receipt_id ="+receipt.getReceiptId()+";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*t_receipt_cn_his */
			logger.debug("#insert t_receipt_cn_his#");
			sql = new StringBuffer();
			sql.append("\n INSERT INTO t_receipt_cn_his ");
			sql.append("\n SELECT");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_CN_ID,");
			sql.append("\n CREDIT_NOTE_ID,");
			sql.append("\n RECEIPT_ID,");
			sql.append("\n CREATED,");
			sql.append("\n CREATED_BY,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n REMAIN_AMOUNT,");
			sql.append("\n credit_amount");
			sql.append("\n FROM t_receipt_cn");
			sql.append("\n where receipt_id ="+receipt.getReceiptId()+";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*#insert t_receipt_match_cn_his # */
			logger.debug("#insert t_receipt_match_cn_his #");
			sql = new StringBuffer();
			sql.append("\n INSERT INTO t_receipt_match_cn_his ");
			sql.append("\n SELECT");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_MATCH_CN_ID,");
			sql.append("\n RECEIPT_BY_ID,");
			sql.append("\n RECEIPT_CN_ID,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n CREATED,");
			sql.append("\n CREATED_BY,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n RECEIPT_ID");
			sql.append("\n FROM t_receipt_match_cn");
			sql.append("\n where receipt_id ="+receipt.getReceiptId()+";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*# insert t_receipt_by_his # */
			logger.debug("# insert t_receipt_by_his #");
			sql = new StringBuffer();
			sql.append("\n insert into t_receipt_by_his ");
			sql.append("\n select ");
			sql.append("\n "+transId+" as TRANS_ID, ");
			sql.append("\n RECEIPT_BY_ID, ");
			sql.append("\n PAYMENT_METHOD, ");
			sql.append("\n BANK, ");
			sql.append("\n CHEQUE_NO, ");
			sql.append("\n CHEQUE_DATE, ");
			sql.append("\n RECEIPT_AMOUNT, ");
			sql.append("\n CREDIT_CARD_TYPE, ");
			sql.append("\n PAID_AMOUNT, ");
			sql.append("\n REMAIN_AMOUNT, ");
			sql.append("\n RECEIPT_ID, ");
			sql.append("\n CREATED_BY, ");
			sql.append("\n UPDATED, ");
			sql.append("\n UPDATED_BY, ");
			sql.append("\n sysdate() as CREATED, ");
			sql.append("\n SEED_ID, ");
			sql.append("\n CREDITCARD_EXPIRED, ");
			sql.append("\n WRITE_OFF ");
			sql.append("\n from t_receipt_by");
			sql.append("\n where receipt_id ="+receipt.getReceiptId()+";");
		
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			
		}
		return receipt;
	}
	
	/** Insert Case ReceiptBy > 1 **/
	public ReceiptFunctionBean copyReceiptLineToReceiptLineHisByReceiptById(Connection conn,ReceiptFunctionBean receipt) throws Exception{
		StringBuffer sql = new StringBuffer("");
		int r=  0;
		try{
			//Gen new TransId by import
			long transId = SequenceProcess.getNextValue("TEMP_RECEIPT");
			logger.debug("TransId:"+transId);
			/* # insert t_receipt_line_his # */
			logger.debug("# insert t_receipt_line_his #");
			
			sql.append("\n insert into t_receipt_line_his ");
			sql.append("\n select ");
			sql.append("\n "+transId+" as trans_id, ");
			sql.append("\n RECEIPT_LINE_ID,");
			sql.append("\n LINE_NO ,");
			sql.append("\n RECEIPT_ID,");
			sql.append("\n AR_INVOICE_NO,");
			sql.append("\n SALES_ORDER_NO,");
			sql.append("\n INVOICE_AMOUNT,");
			sql.append("\n CREDIT_AMOUNT,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n REMAIN_AMOUNT,");
			sql.append("\n ORDER_ID,");
			sql.append("\n ORDER_LINE_ID,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n CREATED_BY,");
			sql.append("\n CREATED,");
			sql.append("\n DESCRIPTION");
			sql.append("\n from t_receipt_line ");
			sql.append("\n where receipt_line_id in( ");
			sql.append("\n  select receipt_line_id  from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receipt.getReceiptId());
			sql.append("\n      and  receipt_by_id ="+receipt.getReceiptById());
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receipt.getReceiptId() +";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/** # insert t_receipt_match_his # */
			logger.debug("# insert t_receipt_match_his #");
			sql = new StringBuffer();
			sql.append("\n INSERT INTO t_receipt_match_his ");
			sql.append("\n SELECT ");
			sql.append("\n "+transId+" as trans_id, ");
			sql.append("\n RECEIPT_MATCH_ID, ");
			sql.append("\n RECEIPT_BY_ID, ");
			sql.append("\n RECEIPT_LINE_ID, ");
			sql.append("\n PAID_AMOUNT, ");
			sql.append("\n CREATED, ");
			sql.append("\n CREATED_BY, ");
			sql.append("\n UPDATED, ");
			sql.append("\n UPDATED_BY, ");
			sql.append("\n RECEIPT_ID ");
			sql.append("\n FROM t_receipt_match");
			sql.append("\n where receipt_match_id in( ");
			sql.append("\n  select receipt_match_id  from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receipt.getReceiptId());
			sql.append("\n      and  receipt_by_id ="+receipt.getReceiptById());
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receipt.getReceiptId() +";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*t_receipt_cn_his */
			logger.debug("#insert t_receipt_cn_his#");
			sql = new StringBuffer();
			sql.append("\n INSERT INTO t_receipt_cn_his ");
			sql.append("\n SELECT");
			sql.append("\n "+transId+" as trans_id, ");
			sql.append("\n RECEIPT_CN_ID,");
			sql.append("\n CREDIT_NOTE_ID,");
			sql.append("\n RECEIPT_ID,");
			sql.append("\n CREATED,");
			sql.append("\n CREATED_BY,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n REMAIN_AMOUNT,");
			sql.append("\n credit_amount");
			sql.append("\n FROM t_receipt_cn");
			sql.append("\n where receipt_cn_id in( ");
			sql.append("\n  select receipt_cn_id  from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receipt.getReceiptId());
			sql.append("\n      and  receipt_by_id ="+receipt.getReceiptById());
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receipt.getReceiptId() +";");
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*#insert t_receipt_match_cn_his # */
			logger.debug("#insert t_receipt_match_cn_his #");
			sql = new StringBuffer("");
			sql.append("\n INSERT INTO t_receipt_match_cn_his ");
			sql.append("\n SELECT");
			sql.append("\n "+transId+" as trans_id, ");
			sql.append("\n RECEIPT_MATCH_CN_ID,");
			sql.append("\n RECEIPT_BY_ID,");
			sql.append("\n RECEIPT_CN_ID,");
			sql.append("\n PAID_AMOUNT,");
			sql.append("\n CREATED,");
			sql.append("\n CREATED_BY,");
			sql.append("\n UPDATED,");
			sql.append("\n UPDATED_BY,");
			sql.append("\n RECEIPT_ID");
			sql.append("\n FROM t_receipt_match_cn");
			sql.append("\n where receipt_match_cn_id in( ");
			sql.append("\n  select receipt_match_cn_id  from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receipt.getReceiptId());
			sql.append("\n      and  receipt_by_id ="+receipt.getReceiptById());
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receipt.getReceiptId() +";");
			
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
			/*# insert t_receipt_by_his # */
			logger.debug("# insert t_receipt_by_his #");
			sql = new StringBuffer("");
			sql.append("\n insert into t_receipt_by_his ");
			sql.append("\n select ");
			sql.append("\n "+transId+" as trans_id, ");
			sql.append("\n RECEIPT_BY_ID, ");
			sql.append("\n PAYMENT_METHOD, ");
			sql.append("\n BANK, ");
			sql.append("\n CHEQUE_NO, ");
			sql.append("\n CHEQUE_DATE, ");
			sql.append("\n RECEIPT_AMOUNT, ");
			sql.append("\n CREDIT_CARD_TYPE, ");
			sql.append("\n PAID_AMOUNT, ");
			sql.append("\n REMAIN_AMOUNT, ");
			sql.append("\n RECEIPT_ID, ");
			sql.append("\n CREATED_BY, ");
			sql.append("\n UPDATED, ");
			sql.append("\n UPDATED_BY, ");
			sql.append("\n sysdate() as CREATED, ");
			sql.append("\n SEED_ID, ");
			sql.append("\n CREDITCARD_EXPIRED, ");
			sql.append("\n WRITE_OFF, ");
			sql.append("\n receive_Cash_Date ");
			sql.append("\n from t_receipt_by");
			sql.append("\n where receipt_by_id ="+receipt.getReceiptById()+";");
		
			r = Utils.excUpdateReInt(conn, sql.toString());
			logger.debug("Result execute:"+r);
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			
		}
		return receipt;
	}

	public ReceiptFunctionBean createReceiptByModel(Connection conn,ReceiptFunctionBean receipt, double paidAmountOracle) throws Exception{
		logger.debug("#createReceiptByModel#");
		try{
			ReceiptBy receiptBySave = new ReceiptBy();
			receiptBySave.setReceiptId(receipt.getReceiptId());
			receiptBySave.setPaymentMethod(receipt.getPaymentMethod());
			receiptBySave.setChequeNo(receipt.getChequeNo());
			receiptBySave.setChequeDate("");
			receiptBySave.setBank("");
			receiptBySave.setReceiptAmount(paidAmountOracle);// Get from Line B
			receiptBySave.setRemainAmount(receiptBySave.getReceiptAmount()- paidAmountOracle);// Get from Line B
			receiptBySave.setPaidAmount(paidAmountOracle);// Get from Line B
			//save to DB
			new MReceiptBy().save(receiptBySave, 999, conn);
			
			receipt.setReceiptById(receiptBySave.getId());
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		return receipt;
	}
	public int updateReceiptByModel(Connection conn,ReceiptFunctionBean receipt, double paidAmountRacle) throws Exception{
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int r = 0;
		 logger.debug("#start update amount receipt by #");
		try{
			sql.append("\n update t_receipt_by set receipt_amount ="+paidAmountRacle);
			sql.append("\n ,paid_amount ="+paidAmountRacle);
			sql.append("\n ,remain_amount = "+(paidAmountRacle-paidAmountRacle)+"");
			sql.append("\n ,created =sysdate(),created_by =999,updated =null ,updated_by =0 ");
			sql.append("\n where receipt_by_id ="+receipt.getReceiptById());
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			r = ps.executeUpdate();
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			
		}
		return r;
	}
	
	public int findLineNoReceiptLine(Connection conn,int receiptId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		int lineNo = 1;
		try{
			sql = new StringBuffer("");
			sql.append(" select (max(l.line_no)+1) as next_line_no FROM t_receipt_line l WHERE 1=1 \n" );	
			sql.append("\n and receipt_id ="+receiptId);
			
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("next_line_no") >0){
				  lineNo = rs.getInt("next_line_no");
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return lineNo;
	}
	
	public double sumReceiptAmoutInTemp(Connection conn,String receiptNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		double amount = 0;
		try{
			sql = new StringBuffer("");
			sql.append(" select sum(amount) as amount FROM t_temp_import_trans l WHERE 1=1 \n" );	
			sql.append("\n and receipt_no ='"+receiptNo+"'");
			sql.append("\n and line_str like 'H|%'");
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amount = rs.getDouble("amount");
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return amount;
	}

   public ReceiptFunctionBean findReceiptLine(Connection conn,ReceiptFunctionBean receipt) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String sql = "";
		try{
			 sql  = "\n select min(receipt_line_id) as receipt_line_id ";
			 sql += "\n from t_receipt_line l ";
			 sql += "\n where l.receipt_id = "+receipt.getReceiptId();
			 logger.debug("sql:"+sql);
			 ps =conn.prepareStatement(sql);
			 rs = ps.executeQuery();
			 if(rs.next()){
				receipt.setReceiptLineId(rs.getInt("receipt_line_id"));
			 }
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receipt;
	}
	  
	public ReceiptFunctionBean findReceiptByDetail(Connection conn,ReceiptFunctionBean receipt,String paymentMethod) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n select receipt_by_id,payment_method,cheque_no,cheque_date");
			sql.append("\n from t_receipt_by where 1=1 ");
			sql.append("\n and receipt_id ="+receipt.getReceiptId());
			sql.append("\n and write_off <> 'Y' ");
			
			//Get Receipt By ChequeNo 
			if( Utils.isNull(paymentMethod).equals("CH")){
			 sql.append("\n and payment_method ='CH' ");
			   sql.append("\n and cheque_no='"+Utils.isNull(receipt.getChequeNo())+"'");
			}else if( Utils.isNull(paymentMethod).equals("CS")){
			   sql.append("\n and payment_method ='CS' ");
			}
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				receipt.setReceiptById(rs.getInt("receipt_by_id"));
				receipt.setPaymentMethod(Utils.isNull(rs.getString("payment_method")));
				receipt.setChequeNo(rs.getString("cheque_no"));
				receipt.setChequeDate(Utils.stringValueDefault(rs.getDate("cheque_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th,""));
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receipt;
	}
	public ReceiptFunctionBean findReceiptByIdWriteOff(Connection conn,ReceiptFunctionBean receipt) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n select bys.receipt_by_id , m.receipt_match_id ");
			sql.append("\n from t_receipt_match m,t_receipt_by bys");
			sql.append("\n where m.receipt_by_id = bys.receipt_by_id");
			sql.append("\n and bys.receipt_id ="+receipt.getReceiptId());
			sql.append("\n and bys.write_off = 'Y' ");

			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				receipt.setReceiptByIdWriteOff(rs.getInt("receipt_by_id"));
				receipt.setReceiptMatchIdWriteOff(rs.getInt("receipt_match_id"));
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receipt;
	}
	
	public Customer findCustomer(Connection conn,String customerCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		Customer customer = null;
		try{
			sql.append("\n select customer_id ,name from m_customer ");
			sql.append("\n where code ='"+customerCode+"'");
			sql.append("\n and isactive ='Y'");
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				customer = new Customer();
				customer.setId(rs.getInt("customer_id"));
				customer.setName(Utils.isNull(rs.getString("name")));
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return customer;
	}
	public int findOrderId(Connection conn,String arInvoiceNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		int orderId =0;
		try{
			sql.append("\n select order_id from t_order where ar_invoice_no ='"+arInvoiceNo+"'");
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				orderId = rs.getInt("order_id");
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return orderId;
	}
	
	public int findOrderLineId(Connection conn,String arInvoiceNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		int order_line_id =0;
		try{
			sql.append("\n select order_line_id from t_order_line where ar_invoice_no ='"+arInvoiceNo+"'");
			   
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				order_line_id = rs.getInt("order_line_id");
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return order_line_id;
	}
	
	public boolean isFoundReciptLineMatch2ReceitpByCaseVO(Connection conn,int receiptId,int receiptById) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		boolean found =false;
		try{
			sql.append("\n   select count(*) as c from t_receipt_match where receipt_id ="+receiptId);
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >1){
					found = true;
				}
			}
			logger.debug("isFoundReciptLineMatch2ReceitpByCaseVO found["+found+"]");
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return found;
	}
	
	/** Delete Case VO and ReveiptBy > 1 **/
	public int deleteReceiptByReceiptByIdCaseVOReceiptByMoreOne(Connection conn,int receiptId,int receiptById) throws Exception{
		StringBuffer sql = new StringBuffer("");
		int r=0;
		try{
			/** Delete Receipt_CN **/
			sql.append("\n delete from t_receipt_cn where receipt_cn_id in(");
			sql.append("\n   select receipt_cn_id from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n     select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n     and  receipt_by_id ="+receiptById);
			sql.append("\n   ) ");
			sql.append("\n )and  receipt_id ="+receiptId +";");
			
			/* delete receipt_match_cn **/
			sql.append("\n  delete from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n     select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n     and  receipt_by_id ="+receiptById);
			sql.append("\n )and  receipt_id ="+receiptId +";");
			
			
			/** Delete Receipt **/
			//Check receiptLine is apply by other receipt_by no delete 
			if(isFoundReciptLineMatch2ReceitpByCaseVO(conn,receiptId,receiptById)){
				logger.debug("receipt line match 2 receiptBy found: no delete receipt line");
				
			}else{
				sql.append("\n delete  from t_receipt_line where receipt_line_id in( ");
				sql.append("\n  select receipt_line_id  from t_receipt_match where receipt_by_id  in( ");
				sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
				sql.append("\n      and  receipt_by_id ="+receiptById);
				sql.append("\n  ) ");
				sql.append("\n )and  receipt_id ="+receiptId +";");
			}
			
			/** Delete receipt_line wrong data not found in Match **/
			/*sql.append("\n delete  from t_receipt_line where receipt_line_id not in( ");
			sql.append("\n  select receipt_line_id  from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n      and  receipt_by_id ="+receiptById);
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receiptId +";");*/
			
			sql.append("\n delete from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n    select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n    and  receipt_by_id ="+receiptById);
			sql.append("\n )and  receipt_id ="+receiptId +";");
		
			sql.append("\n delete from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n and receipt_by_id ="+receiptById +";");
			
			logger.debug("sql:"+sql.toString());
			Utils.excUpdateReInt(conn,sql.toString());
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			
		}
		return r;
	}
	
	/** Delete Case VO and ReveiptBy > 1 **/
	public int deleteReceiptByReceiptByIdCaseSVReceiptByMoreOne(Connection conn,int receiptId,int receiptById) throws Exception{
		StringBuffer sql = new StringBuffer("");
		int r=0;
		try{
			/** Delete Receipt_CN **/
			sql.append("\n delete from t_receipt_cn where receipt_cn_id in(");
			sql.append("\n   select receipt_cn_id from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n     select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n     and  receipt_by_id ="+receiptById);
			sql.append("\n   ) ");
			sql.append("\n )and  receipt_id ="+receiptId +";");
			
			/* delete receipt_match_cn **/
			sql.append("\n  delete from t_receipt_match_cn where receipt_by_id  in( ");
			sql.append("\n     select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n     and  receipt_by_id ="+receiptById);
			sql.append("\n )and  receipt_id ="+receiptId +";");
			
			
			/** Delete Receipt **/
			sql.append("\n delete  from t_receipt_line where receipt_line_id in( ");
			sql.append("\n  select receipt_line_id  from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n      and  receipt_by_id ="+receiptById);
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receiptId +";");
			
			/** Delete receipt_line wrong data not found in Match **/
			/*sql.append("\n delete  from t_receipt_line where receipt_line_id not in( ");
			sql.append("\n  select receipt_line_id  from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n      select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n      and  receipt_by_id ="+receiptById);
			sql.append("\n  ) ");
			sql.append("\n )and  receipt_id ="+receiptId +";");*/
			
			sql.append("\n delete from t_receipt_match where receipt_by_id  in( ");
			sql.append("\n    select receipt_by_id from t_receipt_by where receipt_id ="+receiptId);
			sql.append("\n    and  receipt_by_id ="+receiptById);
			sql.append("\n )and  receipt_id ="+receiptId +";");
		
			logger.debug("sql:"+sql.toString());
			Utils.excUpdateReInt(conn,sql.toString());
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			
		}
		return r;
	}
	
	public String findReceiptByKeyNo(Connection conn,String keyNo) throws Exception{
		String  receiptNo = "";
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			if( !Utils.isNull(keyNo).equals("")){
				sql.append(" 	select t.receipt_id,receipt_no,receipt_date");
				sql.append(" 	,t.order_type,t.customer_id,t.customer_name,t.receipt_amount");
				sql.append(" 	FROM t_receipt t WHERE t.receipt_no = '"+keyNo+"' \n" );
				sql.append(" 	and t.doc_status ='SV'" );
				sql.append(" 	union \n" );
				sql.append(" 	select t.receipt_id,receipt_no,receipt_date");
				sql.append(" 	,t.order_type,t.customer_id,t.customer_name,t.receipt_amount");
				sql.append(" 	FROM t_receipt t WHERE t.receipt_id in( \n" );
				sql.append("   	select distinct m.receipt_id  from t_receipt_match m  \n");
				sql.append("    	where m.receipt_by_id in ( \n" );
				sql.append("  	  	  select receipt_by_id from t_receipt_by  where cheque_no ='"+keyNo+"' \n" );
				sql.append("   	   ) \n" );
				sql.append("  	) \n" );
				sql.append(" 	and t.doc_status ='SV'" );
			
				logger.debug("sql:"+sql.toString());
				ps =conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					receiptNo= rs.getString("receipt_no");
				}
				//Case RECEIPTORCL keyNo = receiptNo
				if(Utils.isNull(receiptNo).equals("")){
					receiptNo = keyNo;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receiptNo;
	}
	
	public ReceiptDataLine calcReceiptLineAmount(Connection conn,int receiptId,String arInvoiceNo,double paidAmountOracle) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		ReceiptDataLine receiptLine = new ReceiptDataLine();
		double invoiceAmount = 0;
		try{
			if( !Utils.isNull(arInvoiceNo).equals("")){
				sql.append(" select receipt_line_id,t.invoice_amount ,t.credit_amount,t.remain_amount \n");
				sql.append(" FROM t_receipt_line t WHERE t.ar_invoice_no = '"+arInvoiceNo+"' \n" );
				sql.append(" and t.receipt_id < "+receiptId +"\n"); /** < receuptId */
				sql.append(" and t.receipt_id in( \n" );
				sql.append(" 	  select receipt_id from t_receipt where doc_status ='SV' \n" );
				sql.append(" )" );
				sql.append(" order by t.receipt_line_id desc" );
				
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					receiptLine.setInvoiceAmount(rs.getDouble("invoice_amount"));
					receiptLine.setCreditAmount(rs.getDouble("remain_amount"));
					receiptLine.setRemainAmount(rs.getDouble("remain_amount")-paidAmountOracle);
					
					invoiceAmount = rs.getDouble("invoice_amount");
				}
			}
			
			if(invoiceAmount <= 0.0 ){
				// Not found is first Receipt
				sql = new StringBuffer();
				sql.append(" select t.net_amount \n");
				sql.append(" FROM t_order t WHERE t.ar_invoice_no = '"+arInvoiceNo+"' \n" );
				sql.append(" and doc_status ='SV' \n" );
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
				   receiptLine.setInvoiceAmount(rs.getDouble("net_amount"));
				   receiptLine.setCreditAmount(rs.getDouble("net_amount"));
				   receiptLine.setRemainAmount(rs.getDouble("net_amount")-paidAmountOracle);
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs !=null){
				rs.close();
			}
		}
		return receiptLine;
	}
	
	class ReceiptDataLine{
		private double invoiceAmount;
		private double creditAmount;
		private double remainAmount;
		public double getInvoiceAmount() {
			return invoiceAmount;
		}
		public void setInvoiceAmount(double invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}
		public double getCreditAmount() {
			return creditAmount;
		}
		public void setCreditAmount(double creditAmount) {
			this.creditAmount = creditAmount;
		}
		public double getRemainAmount() {
			return remainAmount;
		}
		public void setRemainAmount(double remainAmount) {
			this.remainAmount = remainAmount;
		}
		
		
	}
	class Receiptbatch{
		private String batchNo;
		private String receiptNo;
		private double totalReceiptAmount;
		private String docStatus;
		
		
		public String getReceiptNo() {
			return receiptNo;
		}
		public void setReceiptNo(String receiptNo) {
			this.receiptNo = receiptNo;
		}
		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public double getTotalReceiptAmount() {
			return totalReceiptAmount;
		}
		public void setTotalReceiptAmount(double totalReceiptAmount) {
			this.totalReceiptAmount = totalReceiptAmount;
		}
		public String getDocStatus() {
			return docStatus;
		}
		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}
		
		
	}
	class ReceiptFunctionBean{
		private int receiptId;
		private int receiptCNId;
		private int receiptMatchCNId;
		private int receiptLineId;
		private int creditNoteId;
		private double receiptAmount;
		private double paidAmount;
		private double remainAmount;
		private String caseType;
		private String customerId;
		private String customerName;
		private String receiptNo;
		private String receiptDate;
		private String orderType;
		private int countReceiptBy;
		private int receiptById;
		private int receiptByIdWriteOff;
		private int receiptMatchIdWriteOff;
		private String paymentMethod;
		private String bank;
		private String chequeNo;
		private String chequeDate;
		private String description;
		
        
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getPaidAmount() {
			return paidAmount;
		}
		public void setPaidAmount(double paidAmount) {
			this.paidAmount = paidAmount;
		}
		public double getRemainAmount() {
			return remainAmount;
		}
		public void setRemainAmount(double remainAmount) {
			this.remainAmount = remainAmount;
		}
		public int getReceiptMatchIdWriteOff() {
			return receiptMatchIdWriteOff;
		}
		public void setReceiptMatchIdWriteOff(int receiptMatchIdWriteOff) {
			this.receiptMatchIdWriteOff = receiptMatchIdWriteOff;
		}
		public int getReceiptByIdWriteOff() {
			return receiptByIdWriteOff;
		}
		public void setReceiptByIdWriteOff(int receiptByIdWriteOff) {
			this.receiptByIdWriteOff = receiptByIdWriteOff;
		}
		public int getCountReceiptBy() {
			return countReceiptBy;
		}
		public void setCountReceiptBy(int countReceiptBy) {
			this.countReceiptBy = countReceiptBy;
		}
		public int getReceiptById() {
			return receiptById;
		}
		public void setReceiptById(int receiptById) {
			this.receiptById = receiptById;
		}
		public String getPaymentMethod() {
			return paymentMethod;
		}
		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
		public String getBank() {
			return bank;
		}
		public void setBank(String bank) {
			this.bank = bank;
		}
		public String getChequeNo() {
			return chequeNo;
		}
		public void setChequeNo(String chequeNo) {
			this.chequeNo = chequeNo;
		}
		public String getChequeDate() {
			return chequeDate;
		}
		public void setChequeDate(String chequeDate) {
			this.chequeDate = chequeDate;
		}
		public int getReceiptId() {
			return receiptId;
		}
		public void setReceiptId(int receiptId) {
			this.receiptId = receiptId;
		}
		public int getReceiptCNId() {
			return receiptCNId;
		}
		public void setReceiptCNId(int receiptCNId) {
			this.receiptCNId = receiptCNId;
		}
		public int getReceiptMatchCNId() {
			return receiptMatchCNId;
		}
		public void setReceiptMatchCNId(int receiptMatchCNId) {
			this.receiptMatchCNId = receiptMatchCNId;
		}
		public int getReceiptLineId() {
			return receiptLineId;
		}
		public void setReceiptLineId(int receiptLineId) {
			this.receiptLineId = receiptLineId;
		}
		
		public int getCreditNoteId() {
			return creditNoteId;
		}
		public void setCreditNoteId(int creditNoteId) {
			this.creditNoteId = creditNoteId;
		}
		public double getReceiptAmount() {
			return receiptAmount;
		}
		public void setReceiptAmount(double receiptAmount) {
			this.receiptAmount = receiptAmount;
		}
		public String getCaseType() {
			return caseType;
		}
		public void setCaseType(String caseType) {
			this.caseType = caseType;
		}
		public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getReceiptNo() {
			return receiptNo;
		}
		public void setReceiptNo(String receiptNo) {
			this.receiptNo = receiptNo;
		}
		public String getReceiptDate() {
			return receiptDate;
		}
		public void setReceiptDate(String receiptDate) {
			this.receiptDate = receiptDate;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		
	
	}
}
