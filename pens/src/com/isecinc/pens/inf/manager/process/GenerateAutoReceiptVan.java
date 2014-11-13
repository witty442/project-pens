package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.process.order.OrderProcess;

public class GenerateAutoReceiptVan {

	public static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void genAutoReceiptCash(User user){
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("genAutoReceiptCash");
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			 //Get Order not receipt 
			List<Order> orderList = new MOrder().getOrderVanNotReceipt(conn, user) ;
			if(orderList != null && orderList.size() >0){
			  for(i=0;i<orderList.size();i++){
					 Order order = (Order)orderList.get(i);
					 
					 //Set Receipt
					 Receipt autoReceipt = new Receipt();
					 autoReceipt.setReceiptNo(order.getOrderNo());
					 autoReceipt.setReceiptAmount(order.getNetAmount());
					 autoReceipt.setInternalBank("002");//SCB-ÊÒ¢ÒÊÒ¸Ø»ÃÐ´ÔÉ°ì 068-2-81805-7
					 autoReceipt.setReceiptDate(order.getOrderDate());
					 
					// Get Lines to Create Receipt
					 List<OrderLine> orderLines = new MOrderLine().lookUp(conn,order.getId());
						
					 /** Set ReceiptBy Manual **/
					 List<ReceiptBy> receiptByList = new ArrayList<ReceiptBy>();
					 ReceiptBy receiptBy = new ReceiptBy();
					 receiptBy.setId(0);
					 //receiptBy.setPaymentMethod("PD");
					 receiptBy.setPaymentMethod("CS");
					 receiptBy.setCreditCardType("");
					 receiptBy.setBank("");
					 receiptBy.setChequeNo("");
					 receiptBy.setChequeDate("");
					 receiptBy.setReceiptAmount(order.getNetAmount());
					 receiptBy.setSeedId("");
					 receiptBy.setAllBillId(String.valueOf(order.getId()));
					 receiptBy.setAllPaid(String.valueOf(order.getNetAmount()));
					 receiptByList.add(receiptBy);
					 
					 //process auto receipt cash
					 new OrderProcess().createAutoReceipt(autoReceipt, order, orderLines, receiptByList, null, user, conn);
					 
					 //Case Cancel Receipt payment ='N'  ->update payment ='Y'
					 new MOrder().updatePaymentByOrderId(conn, order.getId(), "Y");
			  }
			  
			  conn.commit();
			}
		}catch(Exception e){
			try{
				e.printStackTrace();
				logger.error(e.getMessage(),e);
				conn.rollback();
			}catch(Exception ee){
				
			}
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception ee){
				
			}
		}
	}

}
