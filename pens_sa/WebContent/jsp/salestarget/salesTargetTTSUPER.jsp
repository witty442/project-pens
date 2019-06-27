<%@page import="util.UserUtils"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
//for test
SIdUtils.getInstance().clearInstance();
String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}	
int tabIndex = 0;
if(salesTargetForm.getBean().getItems() != null){
	tabIndex = salesTargetForm.getBean().getItems().size()*2;
}
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = salesTargetForm.getPageName();
String pageNameTemp = "";

pageNameTemp = "TTSUPER_SalesTarget";

//System.out.println("pageNameTemp:"+pageNameTemp);
List<SalesTargetBean> productMKTList = null;
List<SalesTargetBean> salesrepList = null;
Map<String, SalesTargetBean> dataMap = null;
  if(  request.getSession().getAttribute("productMKTList") != null 
    && request.getSession().getAttribute("salesrepList") != null 
    && request.getSession().getAttribute("dataMap") != null ) {
	  
	  productMKTList =(List<SalesTargetBean>)request.getSession().getAttribute("productMKTList");
	  salesrepList = (List<SalesTargetBean>)request.getSession().getAttribute("salesrepList");
	  dataMap =(Map<String, SalesTargetBean>)request.getSession().getAttribute("dataMap");
  }

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
 <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- freeze table  -->

<script type="text/javascript">
//To disable f5
 $(document).bind("keydown", disableF5);

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
} 

function loadMe(path){
	var validTotalCol = 'true';
	var showMsg ='false';
	<%if(request.getAttribute("save_by_ttsuper") != null){%>
	    validTotalCol = 'true'
	<%}%>
	
	<%if(request.getSession().getAttribute("dataMap") != null ) {%>
	    sumTotalByAllRow();
	    sumTotalByAllCol(validTotalCol,showMsg);
	<%}%>
}
function backForm(path){
	var form = document.salesTargetForm;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=back";
	form.submit();
	return true;
}
function save(path){
	var form = document.salesTargetForm;
	var validTotalCol = 'true';
	var showMsg = 'false';
	//valid total column
	sumTotalByAllRow();
	sumTotalByAllCol(validTotalCol,showMsg);
	
	if(validateTotalCanSave()){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/salesTargetAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
}

function salesAcceptToSalesManager(path){
	if(document.getElementById('check_save_before_accept').value =="save_before_accept"){
	    alert("มีการเปลี่ยนแปลงข้อมูล กรุณาบันทึกข้อมูลก่อน ทำการยืนยันรับเป้าหมายรายเซลล์")
		return false;
	}
	
	if(validateTotalCanAccept()){
		if(confirm("ยืนยันรับเป้าแบรนด์รายเซลล์")){
			/**Control Save Lock Screen **/
			startControlSaveLockScreen();
			
			var form = document.salesTargetForm;
			form.action = path + "/jsp/salesTargetAction.do?do=salesAcceptToSalesManager";
			form.submit();
			return true;
		}
	}
	return false;
}
function validateTotalCanAccept(){
	//validate totalAll Sales vs MKT
	var total_ctn_all_by_sales = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_sales'));
    var total_ctn_all_by_mkt = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_mkt'));
   // alert(total_ctn_all_by_sales+":"+total_ctn_all_by_mkt);
   
   if(total_ctn_all_by_sales==0){
	   alert("กรุณาระบุข้อมูลเป้าก่อน");
	   return false;
   }
   //Validate total all
   if(total_ctn_all_by_sales != total_ctn_all_by_mkt ){
	   alert("ยอดรวมหีบ ของ SKU ไม่เท่ากับ Marketing กำหนด ไม่สามารถยืนยันรับเป้าได้");
	   document.getElementById('total_ctn_all_by_sales').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_sales').className='disableNumberBoldRed';
	   
	   document.getElementById('total_ctn_all_by_mkt').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_mkt').className='disableNumberBoldRed';
	   return false;
    }
   
   //validate total col
   if(document.getElementById('valid_total_col').value=='false'){
	   alert("ยอดรวมหีบ ของ แต่ละ SKU ไม่เท่ากับ Marketing กำหนด ไม่สามารถยืนยันรับเป้าได้");
	   return false;
   }
    return true;
}

function validateTotalCanSave(){
	//validate totalAll Sales vs MKT
	var total_ctn_all_by_sales = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_sales'));
    var total_ctn_all_by_mkt = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_mkt'));
   // alert(total_ctn_all_by_sales+":"+total_ctn_all_by_mkt);
   
   if(total_ctn_all_by_sales==0){
	   alert("กรุณาระบุข้อมูลเป้าก่อน");
	   return false;
   }
   if(total_ctn_all_by_sales > total_ctn_all_by_mkt ){
	   alert("ยอดรวมหีบ ของ SKU เกินที่ Marketing กำหนด");
	   document.getElementById('total_ctn_all_by_sales').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_sales').className='disableNumberBoldRed';
	   
	   document.getElementById('total_ctn_all_by_mkt').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_mkt').className='disableNumberBoldRed';
	   return false;
    }
    return true;
}

//SummAllRow
function sumTotalByAllRow(){
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;
	
   var total_ctn_by_sales_calc = 0;
   var total_amount_by_sales_calc = 0;
   var maxColumns = $('#maxColumns').val();
   var maxRows = $('#maxRows').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var row=0;row< maxRows;row++){
	   for(var col=0;col< maxColumns;col++){
		   target_qty_obj =document.getElementById('target_qty_'+col+"_"+row);
		   target_amount_obj =document.getElementById('target_amount_'+col+"_"+row);
		   price_obj = document.getElementById('price_'+col+"_"+row);
			
		   if(target_qty_obj.value != ''){
				// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
				//calc
			 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
				price_temp = convetTxtObjToFloat(price_obj);
				target_amount_temp = target_qty_temp*price_temp; 
				 
				//set value to screen
				target_amount_obj.value= target_amount_temp;
				 
				//sum All Row By SalesrepCode
				total_ctn_by_sales_calc += target_qty_temp;
				total_amount_by_sales_calc += target_amount_temp;
			 }
	     }//for 2
	     
	   //  alert(total_ctn_by_sales_calc);
	    if(total_ctn_by_sales_calc != 0){
		   //display sumRowByRow
		 	document.getElementById('total_ctn_by_sales_'+row).value = total_ctn_by_sales_calc;
		 	document.getElementById('total_amount_by_sales_'+row).value=total_amount_by_sales_calc;
	
		 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sales_'+row));
		 	toCurreny(document.getElementById('total_amount_by_sales_'+row));
	    }
	    
	    //sum all brand by Sales
	    var total_amount_by_sales_calc = convetTxtObjToFloat(document.getElementById('total_amount_by_sales_'+row));
        var total_amount_by_sales_allbrand_o_calc = convetTxtObjToFloat(document.getElementById('total_amount_by_sales_allbrand_o_'+row));
        total_amount_by_sales_allbrand_o_calc += total_amount_by_sales_calc;
	    
        if(total_amount_by_sales_allbrand_o_calc != 0){
           document.getElementById('total_amount_by_sales_allbrand_'+row).value=total_amount_by_sales_allbrand_o_calc;
	       toCurreny(document.getElementById('total_amount_by_sales_allbrand_'+row));
        }
	    
	 	//reset by row
	 	total_ctn_by_sales_calc =0;
	 	total_amount_by_sales_calc=0;
   }//for 1
}
//SummAllCol
function sumTotalByAllCol(validTotalCol,showMsg){
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;

   //bySales
   var total_listprice_by_sku_calc = 0;
   var total_ctn_by_sku_calc = 0;
   var total_price_by_sku_calc = 0;
   //by mkt
   var total_ctn_mkt_by_sku_calc = 0;
   
   var total_ctn_all_by_sales = 0;
   var total_price_all_by_sales = 0;
   var maxColumns = $('#maxColumns').val();
   var maxRows = $('#maxRows').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var col=0;col< maxColumns;col++){
	   //init
	  total_listprice_by_sku_calc =0;
	  total_ctn_by_sku_calc =0;
	  total_price_by_sku_calc = 0;
	  
	  //init mkt ctn for check
	  total_ctn_mkt_by_sku_calc =  convetTxtObjToFloat(document.getElementById('total_ctn_mkt_by_sku_'+col));
	   
      for(var row=0;row< maxRows;row++){
		   target_qty_obj =document.getElementById('target_qty_'+col+"_"+row);
		   target_amount_obj =document.getElementById('target_amount_'+col+"_"+row);
		   price_obj = document.getElementById('price_'+col+"_"+row);
			
		   if(target_qty_obj.value != ''){
				// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
				//calc
			 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
				price_temp = convetTxtObjToFloat(price_obj);
				target_amount_temp = target_qty_temp*price_temp; 
				 
				//sum All Row By SalesrepCode
				total_listprice_by_sku_calc = price_temp;
				total_ctn_by_sku_calc +=target_qty_temp;
				total_price_by_sku_calc +=target_amount_temp;
				
				//sum all
				total_ctn_all_by_sales +=target_qty_temp;
				total_price_all_by_sales +=target_amount_temp;
			 }
	     }//for 2
	     
	    //display sumRowByCol
	    if(total_listprice_by_sku_calc != 0){
	    	//alert("col["+col+"]value["+total_listprice_by_sku_calc_x+"]");
		 	document.getElementById('total_listprice_by_sku_'+col).value = total_listprice_by_sku_calc;
		 	toCurreny(document.getElementById('total_listprice_by_sku_'+col));
	    }
	    if(total_ctn_by_sku_calc != 0){
		 	document.getElementById('total_ctn_by_sku_'+col).value = total_ctn_by_sku_calc;
		 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sku_'+col));
	    }
	    if(total_price_by_sku_calc != 0){
		 	document.getElementById('total_price_by_sku_'+col).value = total_price_by_sku_calc;
		 	toCurreny(document.getElementById('total_price_by_sku_'+col));
	    }
	    
	    //validate by column sales key vs mkt set
	    if(validTotalCol=='true' && total_ctn_by_sku_calc != total_ctn_mkt_by_sku_calc){
	    	 //for check finish valida by Sum Col
	    	 document.getElementById('valid_total_col').value="false";
	    	
	         document.getElementById('total_ctn_mkt_by_sku_'+col).className ='disableNumberRed';
	   	     document.getElementById('total_price_mkt_by_sku_'+col).className ='disableNumberRed';
	   	   
	         document.getElementById('total_ctn_by_sku_'+col).className ='disableNumberRed';
	         document.getElementById('total_price_by_sku_'+col).className ='disableNumberRed';
	    }else{
	    	 document.getElementById('total_ctn_mkt_by_sku_'+col).className ='disableNumberBlue';
		   	 document.getElementById('total_price_mkt_by_sku_'+col).className ='disableNumberBlue';
		   	   
		     document.getElementById('total_ctn_by_sku_'+col).className ='disableNumberBlue';
		     document.getElementById('total_price_by_sku_'+col).className ='disableNumberBlue';
	    }
	    
	 	//reset by col
	 	total_listprice_by_sku_calc =0;
	 	total_ctn_by_sku_calc =0;
	 	total_price_by_sku_calc = 0;
   }//for 1
   
   if(total_ctn_all_by_sales != 0){
 	  document.getElementById('total_ctn_all_by_sales').value = total_ctn_all_by_sales;
 	  toCurrenyNoDigit(document.getElementById('total_ctn_all_by_sales'));
   }
   if(total_price_all_by_sales != 0){
	  document.getElementById('total_price_all_by_sales').value = total_price_all_by_sales;
	  toCurreny(document.getElementById('total_price_all_by_sales'));
   }
   //Validate total All
   validateTotalByAllCol(validTotalCol,showMsg);
}

function validateTotalByAllCol(validTotalCol,showMsg){
	//no validate totalCol 
	if(validTotalCol=='false'){
		return true;
	}
	
   //validate totalAll Sales vs MKT
   var total_ctn_all_by_sales = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_sales'));
   var total_ctn_all_by_mkt = convetTxtObjToFloat(document.getElementById('total_ctn_all_by_mkt'));
   
   document.getElementById('total_ctn_all_by_sales').className='disableNumberBoldBlue';
   document.getElementById('total_price_all_by_sales').className='disableNumberBoldBlue';
   document.getElementById('total_ctn_all_by_mkt').className='disableNumberBoldBlue';
   document.getElementById('total_price_all_by_mkt').className='disableNumberBoldBlue';
   
   //alert(total_ctn_all_by_sales+":"+total_ctn_all_by_mkt);
   if(total_ctn_all_by_sales != total_ctn_all_by_mkt){
	   if(total_ctn_all_by_sales > total_ctn_all_by_mkt){
		   if(showMsg=='true'){
	         alert("ยอดรวมหีบ ของ SKU เกินที่ Marketing กำหนด");
		   }
	   }else if(total_ctn_all_by_sales != total_ctn_all_by_mkt){ 
		   if(showMsg=='true'){
		      alert("ยอดรวมหีบ ของ SKU ไม่เท่ากับ Marketing กำหนด");
		   }
	   }
	   
	   document.getElementById('total_ctn_all_by_sales').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_sales').className='disableNumberBoldRed';
	   
	   document.getElementById('total_ctn_all_by_mkt').className='disableNumberBoldRed';
	   document.getElementById('total_price_all_by_mkt').className='disableNumberBoldRed';
   }
}

//sumTotalByCurRow
function sumTotalByCurRow(obj,curRow){
	//set flage data change must save before accept
	document.getElementById('check_save_before_accept').value="save_before_accept";
	
	//validate
    if(obj.value ==''){
    	obj.value ="0";
    }
	if( !isNumPositive(obj)){
		obj.value ='0';
	}
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
	
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;
	
   var total_ctn_by_sales_calc = 0;
   var total_amount_by_sales_calc = 0;
   var maxColumns = $('#maxColumns').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var colX=0;colX< maxColumns;colX++){
	   target_qty_obj =document.getElementById('target_qty_'+colX+"_"+curRow);
	   target_amount_obj =document.getElementById('target_amount_'+colX+"_"+curRow);
	   price_obj = document.getElementById('price_'+colX+"_"+curRow);
		
	   if(target_qty_obj.value != ''){
			// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
			//calc
		 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
			price_temp = convetTxtObjToFloat(price_obj);
			target_amount_temp = target_qty_temp*price_temp; 
			 
			//set value to screen
			target_amount_obj.value= target_amount_temp;
			 
			//sum All Row By SalesrepCode
			total_ctn_by_sales_calc += target_qty_temp;
			total_amount_by_sales_calc += target_amount_temp;
		 }
     }//for
     
   //display sumRowBySales
     if(total_ctn_by_sales_calc != 0){
 	   document.getElementById('total_ctn_by_sales_'+curRow).value = total_ctn_by_sales_calc;
 	   document.getElementById('total_amount_by_sales_'+curRow).value=total_amount_by_sales_calc;

 	   toCurrenyNoDigit(document.getElementById('total_ctn_by_sales_'+curRow));
 	   toCurreny(document.getElementById('total_amount_by_sales_'+curRow));
     }

    //summary all brand by Sales(row)
    var total_amount_by_sales_calc = convetTxtObjToFloat(document.getElementById('total_amount_by_sales_'+curRow));
 	var total_amount_by_sales_allbrand_o_calc = convetTxtObjToFloat(document.getElementById('total_amount_by_sales_allbrand_o_'+curRow));
 	total_amount_by_sales_allbrand_o_calc += total_amount_by_sales_calc;
 	
  	document.getElementById('total_amount_by_sales_allbrand_'+curRow).value=total_amount_by_sales_allbrand_o_calc;
  	toCurreny(document.getElementById('total_amount_by_sales_allbrand_'+curRow));
 	
 	//set curr qty =blank case value =0
 	if(obj.value =='0'){
 		obj.value ='';
 	}
}
//sumTotalByCurCol
function sumTotalByCurCol(obj,curCol){
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;

   //bySales
   var total_listprice_by_sku_calc = 0;
   var total_ctn_by_sku_calc = 0;
   var total_price_by_sku_calc = 0;
   //init mkt ctn for check
   var total_ctn_mkt_by_sku_calc =  convetTxtObjToFloat(document.getElementById('total_ctn_mkt_by_sku_'+curCol));
	
   var maxColumns = $('#maxColumns').val();
   var maxRows = $('#maxRows').val();
   var sumQtyInRow = 0;
   var qty = 0;
   //sum all row by dix column
     for(var row=0;row< maxRows;row++){
	   target_qty_obj =document.getElementById('target_qty_'+curCol+"_"+row);
	   target_amount_obj =document.getElementById('target_amount_'+curCol+"_"+row);
	   price_obj = document.getElementById('price_'+curCol+"_"+row);
		
	   if(target_qty_obj.value != ''){
			// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
			//calc
		 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
			price_temp = convetTxtObjToFloat(price_obj);
			target_amount_temp = target_qty_temp*price_temp; 
			 
			//sum All Row By SalesrepCode
			total_listprice_by_sku_calc = price_temp;
			total_ctn_by_sku_calc +=target_qty_temp;
			total_price_by_sku_calc +=target_amount_temp;
		 }
     }//for 2
     
    //display sumRowByCol
    if(total_listprice_by_sku_calc != 0){
    	//alert("col["+col+"]value["+total_listprice_by_sku_calc_x+"]");
	 	document.getElementById('total_listprice_by_sku_'+curCol).value = total_listprice_by_sku_calc;
	 	toCurreny(document.getElementById('total_listprice_by_sku_'+curCol));
    }
    if(total_ctn_by_sku_calc != 0){
	 	document.getElementById('total_ctn_by_sku_'+curCol).value = total_ctn_by_sku_calc;
	 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sku_'+curCol));
    }
    if(total_price_by_sku_calc != 0){
	 	document.getElementById('total_price_by_sku_'+curCol).value = total_price_by_sku_calc;
	 	toCurreny(document.getElementById('total_price_by_sku_'+curCol));
    }
    
    //validate by column sales key vs mkt set
    if(total_ctn_by_sku_calc != total_ctn_mkt_by_sku_calc){
         document.getElementById('total_ctn_mkt_by_sku_'+curCol).className ='disableNumberRed';
   	     document.getElementById('total_price_mkt_by_sku_'+curCol).className ='disableNumberRed';
   	   
         document.getElementById('total_ctn_by_sku_'+curCol).className ='disableNumberRed';
         document.getElementById('total_price_by_sku_'+curCol).className ='disableNumberRed';
    }else{
    	 document.getElementById('total_ctn_mkt_by_sku_'+curCol).className ='disableNumberBlue';
	   	 document.getElementById('total_price_mkt_by_sku_'+curCol).className ='disableNumberBlue';
	   	   
	     document.getElementById('total_ctn_by_sku_'+curCol).className ='disableNumberBlue';
	     document.getElementById('total_price_by_sku_'+curCol).className ='disableNumberBlue';
    }
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="<%=pageNameTemp%>"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
		            
						<!-- BODY -->
						<html:form action="/jsp/salesTargetAction">
						<jsp:include page="../error.jsp"/>
						
							<!-- hidden field -->
						   <html:hidden property="pageName" />
						   <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						   <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
                           <html:hidden property="bean.customerId" styleId="customerId"/>
                           <html:hidden property="bean.salesrepId" styleId="salesrepId"/>
                           <html:hidden property="bean.period" styleId="period"/>
                           <html:hidden property="bean.priceListId" styleId="priceListId"/>
                           <input type="hidden" name="maxColumns" id="maxColumns" value="<%=productMKTList!=null?productMKTList.size():0%>"/>
                           <input type="hidden" name="maxRows" id="maxRows" value="<%=salesrepList!=null?salesrepList.size():0%>"/>
                           	<!-- hidden field -->
                           	
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> เดือน</td>
									<td>					
										 <html:select property="bean.periodDesc" styleId="periodDesc" styleClass="disableText" disabled="true">
											<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
									    </html:select>
									</td>
									<td> 
									     <html:text property="bean.startDate" styleId="startDate" size="10" readonly="true" styleClass="disableText"/>
									        -
										<html:text property="bean.endDate" styleId="endDate" size="10" readonly="true" styleClass="disableText"/>
									</td>
									<td>
									 &nbsp;&nbsp;&nbsp;
									   ภาคตามสายดูแล
									    <html:select property="bean.salesZone" styleId="salesZone"  disabled="true">
											<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
									    </html:select> 
									 
									</td>
								</tr>
								<tr>
                                    <td> แบรนด์  </td>
									<td colspan="3">
									    <html:text property="bean.brand" styleId="brand" size="10" readonly="true" styleClass="disableText"/>	
									   <%--  <html:text property="bean.brandName" styleId="brandName" readonly="true" styleClass="disableText" size="50"/>	 --%>					
									    &nbsp;&nbsp;&nbsp;
									     ประเภทขาย 
									   <html:select property="bean.custCatNo" styleId="custCatNo" styleClass="disableText" disabled="true">
											<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
									    </html:select>
									</td>
								</tr>					
						   </table>
					  </div>
					<% 
					  if(request.getSession().getAttribute("productMKTList") != null 
					  && request.getSession().getAttribute("salesrepList") != null 
					  && request.getSession().getAttribute("dataMap") != null ) {
						 
					%>
					<div id="parent">
					<table id='tblProduct' align='center' border='1' width='100%' 
					 cellpadding='3' cellspacing='1' class='tableSearchNoSetTH'>
						<thead>
							<tr>
								<td class="td_text_center"><font color='#4d4dff'><b>รวบหีบ By MKT</b></font></td>
								<%
								double totalCTNMKT = 0;
								for(int col=0;col<productMKTList.size();col++){ 
									SalesTargetBean productBean = productMKTList.get(col);
									totalCTNMKT += Utils.convertStrToDouble(productBean.getTotalTargetQty());
								%>
								  <td  class="td_number">
								  <%-- <font color='#4d4dff'><b><%=productBean.getTotalTargetQty()%></b></font> --%>
									 <input type="text" class="disableNumberBlue" 
									    name="total_ctn_mkt_by_sku_<%=col%>" 
									    id="total_ctn_mkt_by_sku_<%=col%>"
									    value="<%=productBean.getTotalTargetQty()%>" 
									    size="8" readonly/>
								  </td>
								<% }%>
								<!-- Total Ctn by MKT -->
								<td  class="td_number">
									<b>
									  <input type="text" class="disableNumberBoldBlue" 
									   name="total_ctn_all_by_mkt" id="total_ctn_all_by_mkt"size="8" readonly
									   value="<%=Utils.decimalFormat(totalCTNMKT, Utils.format_current_no_disgit) %>"/>
									</b>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td class="td_text_center"><font color='#4d4dff'><b>รวบบาท By MKT</b></font></td>
								<%
								double totalAmountMKT = 0;
								for(int col=0;col<productMKTList.size();col++){ 
									SalesTargetBean productBean = productMKTList.get(col);
									totalAmountMKT += Utils.convertStrToDouble(productBean.getTotalTargetAmount());
								%>
								  <td  class="td_number">
								  <%--  <font color='#4d4dff' ><b><%=productBean.getTotalTargetAmount()%></b></font> --%>
								      <input type="text" class="disableNumberBlue" 
									    name="total_price_mkt_by_sku_<%=col%>" 
									    id="total_price_mkt_by_sku_<%=col%>"
									    value="<%=productBean.getTotalTargetAmount()%>" 
									    size="8" readonly/>
								  </td>
								<% }%>
								<!-- Total Price by MKT -->
								<td  class="td_number_bold">
								    <input type="text" class="disableNumberBoldBlue" 
									 name="total_price_all_by_mkt" id="total_price_all_by_mkt"size="8" readonly
									 value="<%=Utils.decimalFormat(totalAmountMKT, Utils.format_current_2_disgit) %>"/>
								 </td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
						 <tr>
							<th class="td_text_center"rowspan="2">พนักงาน ขาย</th>
							
							<%	
							//Loop Product
							for(int i=0;i<productMKTList.size();i++){ 
								SalesTargetBean productBean = productMKTList.get(i);
							%>
							  <th class="td_text_center">
							     <%=productBean.getItemCode()%><br/>-<%=productBean.getItemName()%>
							  </th>
							<% }%>
									
							<th class="td_text_center" rowspan="2">ยอดรวมหีบ รายเซลส์</th>
							<th class="td_text_center" rowspan="2">ยอดรวมบาท รายเซลส์</th>
							<th class="td_text_center" rowspan="2">ยอดรวมรายเซลส์<br/> ของทุกแบรนด์</th>
							</tr>
							
							<tr>
							<%	
							//Loop Product ระบุเป้า(หีบ)
							for(int i=0;i<productMKTList.size();i++){ 
								SalesTargetBean productBean = productMKTList.get(i);
							%>
							  <th  class="td_text_center">ระบุเป้า(หีบ)</th>
							<% }%>
							</tr>
						</thead>
							<!-- ***********Row Detail********************* -->
							<%
							 String keyMap = "";
							 SalesTargetBean keyBean = null;
							 String targetQty ="";
							 String targetAmount = "";
							 long lineId = 0;
							 for(int row=0;row<salesrepList.size();row++){
								 SalesTargetBean salesrep = salesrepList.get(row);
							%>
								<tr class="lineE">
									<td class="td_text_center"><%=salesrep.getSalesrepCode() %></td>
									<%	
										//Loop Product ระบุเป้า(หีบ)
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
											keyMap = "target_qty_"+productBean.getItemCode()+"_"+salesrep.getSalesrepCode();
											keyBean = dataMap.get(keyMap);
											if(keyBean != null){
												targetQty = keyBean.getTargetQty();
												targetAmount = keyBean.getTargetAmount();
												lineId= keyBean.getLineId();
											}else{
												targetQty = "";
												targetAmount = "";
												lineId=0;
											}
									%>
									     <td class="td_number" title="<%=salesrep.getSalesrepCode()%>">	
									     	 <div class="tooltip">					        
									        <input type="text" size="5"  class="enableNumber" autoComplete="off"
									           name="target_qty_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									           id="target_qty_<%=col%>_<%=row%>"
									           onblur="isNum(this);" 
                                               onchange="sumTotalByCurRow(this,<%=row%>);sumTotalByCurCol(this,<%=col%>)"
									           value="<%=targetQty%>" />
									           <span class="tooltiptext"><%=salesrep.getSalesrepCode()%></span>
									        </div>
									       <!--  ,amount:   --> 
									        <input type="hidden" size="5"
									          name="target_amount_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="target_amount_<%=col%>_<%=row%>"
									          value="<%=targetAmount %>" />  
									        <!--  ,price: -->
									        <input type="hidden" size="5"
									          name="price_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="price_<%=col%>_<%=row%>"
									          value="<%=productBean.getPrice() %>" />
									         
									           <input type="hidden" size="1"
									          name="id_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="id_<%=col%>_<%=row%>"
									          value="<%=salesrep.getId()%>" />
									        
									           <input type="hidden" size="1"
									          name="line_id_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="line_id_<%=col%>_<%=row%>"
									          value="<%=lineId%>" />
									     </td>
									<%
									   }//for product
									%>
									<!-- Sum By Sales -->
									<td class="td_number">
									    <input type="text" class="disableNumberBlue" 
									    name="total_ctn_by_sales_<%=row%>" 
									    id="total_ctn_by_sales_<%=row%>"
									    size="8" readonly/>
									</td>
									<td class="td_number">
                                         <input type="text" class="disableNumberBlue" 
                                         name="total_amount_by_sales_<%=row%>" 
                                         id="total_amount_by_sales_<%=row%>" 
                                         size="8" readonly/>
                                        
									</td>
									<td class="td_number">
                                         <input type="text" class="disableNumberBlue" 
                                         name="total_amount_by_sales_allbrand_<%=row%>" 
                                         id="total_amount_by_sales_allbrand_<%=row%>" 
                                         size="11" readonly value="<%=salesrep.getTotalAmountBrandBySale() %>"/>
                                         <input type="hidden" 
                                           name="total_amount_by_sales_allbrand_o_<%=row%>" 
                                           id="total_amount_by_sales_allbrand_o_<%=row%>" 
                                           value="<%=salesrep.getTotalAmountBrandBySale() %>"/>
									</td>
								</tr>
							<%
							 }//for salesrep
							%>
							<!-- ***********Summary by Column ********************* -->
							<tr>
								<td class="td_text_right"><b>ราคา List Price</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_listprice_by_sku_<%=col%>" 
										    id="total_listprice_by_sku_<%=col%>"
										    size="8" readonly/>
										</td>
									<%  }  %>
									<td class="td_number"></td>
									<td class="td_number"></td>
									<td class="td_number"></td>
							 </tr>
							 <tr>
								<td class="td_text_right"><b>รวมหีบ Sales By SKU</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_ctn_by_sku_<%=col%>" 
										    id="total_ctn_by_sku_<%=col%>"
										    size="8" readonly/>
										</td>
									<%  }  %>
									<td class="td_number">
									  <input type="text" class="disableNumberBoldBlue" 
									    name="total_ctn_all_by_sales" id="total_ctn_all_by_sales"size="8" readonly/>
									</td>
									<td class="td_number"></td> 
									<td class="td_number"></td>
							 </tr>
							 <tr>
								<td class="td_text_right"><b>รวมบาท Sales By SKU</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_price_by_sku_<%=col%>" 
										    id="total_price_by_sku_<%=col%>"
										    size="8" readonly/>
										</td>
									<%  }  %>
									<td class="td_number">
									  <input type="text" class="disableNumberBoldBlue" 
									    name="total_price_all_by_sales" id="total_price_all_by_sales"size="8" readonly/>
									</td>
									<td class="td_number"></td>
									<td class="td_number"></td>
							 </tr>
							<!-- ***********Summary by Column ********************* -->
							</tbody>
						</table>
						</div>
					<%} %>
					<div align="center">
					   <table  border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
								    <td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="5%">
									   <c:if test="${salesTargetForm.bean.canSet == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="   บันทึก     " class="newPosBtnLong"> 
											</a>
										</c:if>
									</td>
									<td align="left" width="5%">
									 <c:if test="${salesTargetForm.bean.canExport == true}">
											<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
											  <input type="button" value="Export To Excel" class="newPosBtnLong">
											</a>
									  </c:if>
									</td>
									<td align="left" width="5%">
									   <c:if test="${salesTargetForm.bean.canAccept == true}">
											<a href="javascript:salesAcceptToSalesManager('${pageContext.request.contextPath}')">
											  <input type="button" value=" ยืนยันรับเป้าแบรนด์รายเซลล์    " class="newPosBtnLong"> 
											</a>
										</c:if>
									</td>
									<td align="left" width="5%">
										<a href="javascript:backForm('${pageContext.request.contextPath}')">
										  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
										</a>
									</td>
								</tr>
						</table>
						<div align="center">
						   <%if("GOD".equalsIgnoreCase(user.getUserName())){ %>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Open')">
									 <input type="button" value="อัพสถานะไปเป็น Open(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Post')">
									 <input type="button" value="อัพสถานะไปเป็น Post(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Accept')">
									 <input type="button" value="อัพสถานะไปเป็น Accept(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Finish')">
									 <input type="button" value="อัพสถานะไปเป็น Finish(GOD)" class="newPosBtn">
								</a>
								
							<%} %>
						</div>
					</div>
			
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
					<!-- check_save_before_accept: --> <input type="hidden" name="check_save_before_accept" id="check_save_before_accept"/>
					<!-- valid_total_col: --> <input type="hidden" name="valid_total_col" id="valid_total_col"/>
					</html:form>
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>
<!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->