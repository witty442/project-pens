package com.isecinc.pens.web.popup;

public class PopupHelper {

	
	public static String[] genHeadTextPopup(String pageName){
		String headName ="";
		String codeSearchTxtName = "";
	    String descSearchTxtName = "";
		String[] headTextArr = new String[3];
		 /** Criteria Name **/
	    if("Brand".equalsIgnoreCase(pageName) || "BrandStock".equalsIgnoreCase(pageName)
	       || "BrandProdShow".equalsIgnoreCase(pageName)|| "BrandStockVan".equalsIgnoreCase(pageName)	){
	    	headName = "แบรนด์("+pageName+")";
	    	codeSearchTxtName = "Brand";
	    	descSearchTxtName = "Brand Name";
	    }else  if("Customer".equalsIgnoreCase(pageName) || "CustomerStock".equalsIgnoreCase(pageName)
	      || "CustomerVanProdShow".equalsIgnoreCase(pageName) || "CustomerLocation".equalsIgnoreCase(pageName)
	      || "CustomerCreditPromotion".equalsIgnoreCase(pageName)|| "CustomerLocNoTrip".equalsIgnoreCase(pageName) 
	      || "CustomerStockMC".equalsIgnoreCase(pageName)){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>ร้านค้า(ที่ยังไม่การกำหนด จุด/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>ร้านค้า("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "Customer Code";
	    	descSearchTxtName = "Customer Name";
	    }else  if("ItemStock".equalsIgnoreCase(pageName) || "ItemCreditPromotion".equalsIgnoreCase(pageName)
	    	       || "ItemCreditPromotion".equalsIgnoreCase(pageName)|| "ItemStockVan".equalsIgnoreCase(pageName)  	){
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
	    }else  if("SalesrepCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "พนักงานขาย(Credit Sales)";
		 	codeSearchTxtName = "Salesrep Code";
		 	descSearchTxtName = "Salesrep Name";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
