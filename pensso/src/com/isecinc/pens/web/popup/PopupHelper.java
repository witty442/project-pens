package com.isecinc.pens.web.popup;

public class PopupHelper {

	
	public static String[] genHeadTextPopup(String pageName){
		String headName ="";
		String codeSearchTxtName = "";
	    String descSearchTxtName = "";
		String[] headTextArr = new String[3];
		 /** Criteria Name **/
	    if("Brand".equalsIgnoreCase(pageName) 	){
	    	headName = "แบรนด์("+pageName+")";
	    	codeSearchTxtName = "แบรนด์"; 
	    	descSearchTxtName = "ชื่อ แบรนด์";
	    }else if("SUB_Brand".equalsIgnoreCase(pageName) 	){
 	    	headName = "แบรนด์ย่อย("+pageName+")";
 	    	codeSearchTxtName = "SubBrand"; 
 	    	descSearchTxtName = "SubBrand Name";
	    }else  if("Customer".equalsIgnoreCase(pageName) ){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>ร้านค้า(ที่ยังไม่การกำหนด จุด/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>ร้านค้า("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "รหัสร้านค้า";
	    	descSearchTxtName = "ชื่อร้านค้า";
	    }else  if("PRODUCT".equalsIgnoreCase(pageName) || "PRODUCT_INFO".equalsIgnoreCase(pageName) ){
	    	headName = "รหัสสินค้า("+pageName+")";
	    	codeSearchTxtName = "รหัสสินค้า";
	    	descSearchTxtName = "ชื่อสินค้า";
	    }else  if("PDStockVan".equalsIgnoreCase(pageName)){
		 	headName = "PD/หน่วยรถ("+pageName+")";
		 	codeSearchTxtName = "PD/หน่วยรถ";
		 	descSearchTxtName = "ชื่อ PD/หน่วยรถ";
	    }else  if("SubInvOnhand".equalsIgnoreCase(pageName)){
		 	headName = "Sub Inv (Stock Onhand)";
		 	codeSearchTxtName = "SubInv";
		 	descSearchTxtName = "SubInv Name";
	    }else  if("Item".equalsIgnoreCase(pageName)){
		 	headName = "สินค้า";
		 	codeSearchTxtName = "Product Code";
		 	descSearchTxtName = "Product Name";
	    }else  if("CustomerCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "ร้านค้า เครดิต(Credit Sales)";
		 	codeSearchTxtName = "Customer Code";
		 	descSearchTxtName = "Customer Name";
	    }else  if("SalesrepSales".equalsIgnoreCase(pageName)){
		 	headName = "พนักงานขาย";
		 	codeSearchTxtName = "รหัสพนักงานขาย";
		 	descSearchTxtName = "ชื่อพนักงานขาย";
	    }else  if("PICKING_NO".equalsIgnoreCase(pageName)){
		 	headName = "Picking No";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Picking No";
	    }else  if("PICKING_NO_PRINT".equalsIgnoreCase(pageName)){
		 	headName = "Picking No(ที่มีการ พิมพ์ Picking List แล้ว)";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Transaction Date";
	    }else  if("PICKING_NO_INVOICE".equalsIgnoreCase(pageName)){
		 	headName = "Picking No (ที่มี INVOCIE)";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Picking No";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
