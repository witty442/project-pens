package com.isecinc.pens.process;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.isecinc.pens.inf.helper.Utils;

public class OrderNoGenerate {
  protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
   public static String genOrderNoKEY(Connection conn ,Date orderDate,String code) throws Exception{
	   String orderNo = "";
	   try{
		   String today = df.format(orderDate);
		   String[] d1 = today.split("/");
		   int curYear = Integer.parseInt(d1[0].substring(2,4));
		   int curMonth = Integer.parseInt(d1[1]);
           
		   //get Seq
		   int seq = SequenceProcess.getNextValue(conn, "ORDER_NO_KEY", code,orderDate);
		   
		   orderNo = code+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("00").format(seq);
	   }catch(Exception e){
		   throw e;
	   }
	  return orderNo;
   }
   
   /** Next OrderNo by count 400->new Order **/
   public static String genOrderNoSplit(Connection conn ,Date orderDate,String code) throws Exception{
	   String orderNo = "";
	   try{
		   String today = df.format(orderDate);
		   String[] d1 = today.split("/");
		   int curYear = Integer.parseInt(d1[0].substring(2,4));
		   int curMonth = Integer.parseInt(d1[1]);
           
		   //get Seq
		   int seq = SequenceProcess.getNextValue(conn, "ORDER_NO", code,orderDate);
		   
		   orderNo = new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000000000").format(seq);
	   }catch(Exception e){
		   throw e;
	   }
	  return orderNo;
   }
   
   /** 13 Digit EAN13**/
   /** 911+RunningNo+CheckDitgit(EAN13)->911000000001C**/
   public static String genBarOnBox(Connection conn ,Date orderDate) throws Exception{
	   String orderNo = "";
	   try{
		   //get Seq
		   int seq = SequenceProcessAll.getNextValue(conn, "BAR_ON_BOX");
		   String barcodeInput12Digit = "911"+new DecimalFormat("000000000").format(seq);
		   int chkDigitEAN13 = BarcodeGenerate.genBarcode(barcodeInput12Digit);
		  
		   orderNo = barcodeInput12Digit+chkDigitEAN13;
	   }catch(Exception e){
		   e.printStackTrace();
		   throw e;
	   }
	  return orderNo;
   }
   
}
