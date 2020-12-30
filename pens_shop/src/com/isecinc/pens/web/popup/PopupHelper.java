package com.isecinc.pens.web.popup;

public class PopupHelper {

	
	public static String[] genHeadTextPopup(String pageName){
		String headName ="";
		String codeSearchTxtName = "";
	    String descSearchTxtName = "";
	    String descSearchTxtName2 = "";
		String[] headTextArr = new String[4];
		 /** Criteria Name **/
	   if("ItemBarcode".equalsIgnoreCase(pageName)){
		 
		 	headName = "รหัสสินค้า("+pageName+")";
	    	codeSearchTxtName = "รหัสสินค้า";
	    	descSearchTxtName = "Material Master";
	    	descSearchTxtName2 = "Barcode";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    headTextArr[3] = descSearchTxtName2;
	    return headTextArr;
	}
}
