package com.isecinc.pens.web.popup;

public class PopupHelper {

	
	public static String[] genHeadTextPopup(String pageName){
		String headName ="";
		String codeSearchTxtName = "";
	    String descSearchTxtName = "";
		String[] headTextArr = new String[3];
		 /** Criteria Name **/
	    if("Brand".equalsIgnoreCase(pageName) || "BrandStock".equalsIgnoreCase(pageName)
	       || "BrandProdShow".equalsIgnoreCase(pageName)|| "BrandStockVan".equalsIgnoreCase(pageName)	
	       || "BrandSalesTargetPD".equalsIgnoreCase(pageName) ){
	    	headName = "แบรนด์("+pageName+")";
	    	codeSearchTxtName = "แบรนด์";
	    	descSearchTxtName = "ชื่อ แบรนด์";
	    }else  if("Customer".equalsIgnoreCase(pageName) || "CustomerStock".equalsIgnoreCase(pageName)
	      || "CustomerVanProdShow".equalsIgnoreCase(pageName) || "CustomerLocation".equalsIgnoreCase(pageName)
	      || "CustomerCreditPromotion".equalsIgnoreCase(pageName)|| "CustomerLocNoTrip".equalsIgnoreCase(pageName) 
	      || "CustomerStockMC".equalsIgnoreCase(pageName) || "CustomerMaster".equalsIgnoreCase(pageName)){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>ร้านค้า(ที่ยังไม่การกำหนด จุด/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>ร้านค้า("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "รหัสร้านค้า";
	    	descSearchTxtName = "ชื่อร้านค้า";
	    }else  if("ItemStock".equalsIgnoreCase(pageName) || "ItemCreditPromotion".equalsIgnoreCase(pageName)
	    	       || "ItemCreditPromotion".equalsIgnoreCase(pageName)|| "ItemStockVan".equalsIgnoreCase(pageName)  	
	    	       || "ItemSalesTargetPD".equalsIgnoreCase(pageName)){
	    	headName = "รหัสสินค้า("+pageName+")";
	    	codeSearchTxtName = "รหัสสินค้า";
	    	descSearchTxtName = "ชื่อสินค้า";
	    }else  if("PDStockVan".equalsIgnoreCase(pageName) ){
		 	headName = "PD/หน่วยรถ("+pageName+")";
		 	codeSearchTxtName = "PD/หน่วยรถ";
		 	descSearchTxtName = "ชื่อ PD/หน่วยรถ";
	    }else  if("PD".equalsIgnoreCase(pageName) || "PDProvince".equalsIgnoreCase(pageName)
	    		|| "PDBoxNo".equalsIgnoreCase(pageName)){
		 	headName = "PD("+pageName+")";
		 	codeSearchTxtName = "PD";
		 	descSearchTxtName = "ชื่อ PD";
	    }else  if("SubInvOnhand".equalsIgnoreCase(pageName)){
		 	headName = "Sub Inv (Stock Onhand)";
		 	codeSearchTxtName = "SubInv";
		 	descSearchTxtName = "SubInv Name";
	    }else  if("Item".equalsIgnoreCase(pageName) ){
		 	headName = "สินค้า";
		 	codeSearchTxtName = "รหัสสินค้า";
		 	descSearchTxtName = "ชื่อสินค้า";
	    }else  if("CustomerCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "ร้านค้า เครดิต(Credit Sales)";
		 	codeSearchTxtName = "รหัสร้านค้า";
		 	descSearchTxtName = "ชื่อร้านค้า";
	    }else  if("SalesrepCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "พนักงานขาย(Credit Sales)";
		 	codeSearchTxtName = "รหัสพนักงานขาย";
		 	descSearchTxtName = "ชื่อ พนักงานขาย";
	    }else  if("CustomerProjectC".equalsIgnoreCase(pageName)){
		 	headName = "ร้านค้า (Credit Sales-Project C)";
		 	codeSearchTxtName = "รหัสร้านค้า";
		 	descSearchTxtName = "ชื่อร้านค้า";
	    }else  if("BranchProjectC".equalsIgnoreCase(pageName)){
		 	headName = "สาขาร้านค้า (Credit Sales-Project C)";
		 	codeSearchTxtName = "Branch Id";
		 	descSearchTxtName = "Branch Name";
	    }else  if("BranchStockMC".equalsIgnoreCase(pageName)){
		 	headName = "สาขาร้านค้า (StockMC)";
		 	codeSearchTxtName = "Branch Name";
		 	descSearchTxtName = "Branch Name";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
