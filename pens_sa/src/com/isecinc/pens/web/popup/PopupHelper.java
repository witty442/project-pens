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
	    	headName = "�ù��("+pageName+")";
	    	codeSearchTxtName = "Brand";
	    	descSearchTxtName = "Brand Name";
	    }else  if("Customer".equalsIgnoreCase(pageName) || "CustomerStock".equalsIgnoreCase(pageName)
	      || "CustomerVanProdShow".equalsIgnoreCase(pageName) || "CustomerLocation".equalsIgnoreCase(pageName)
	      || "CustomerCreditPromotion".equalsIgnoreCase(pageName)|| "CustomerLocNoTrip".equalsIgnoreCase(pageName) 
	      || "CustomerStockMC".equalsIgnoreCase(pageName)){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>��ҹ���(����ѧ����á�˹� �ش/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>��ҹ���("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "Customer Code";
	    	descSearchTxtName = "Customer Name";
	    }else  if("ItemStock".equalsIgnoreCase(pageName) || "ItemCreditPromotion".equalsIgnoreCase(pageName)
	    	       || "ItemCreditPromotion".equalsIgnoreCase(pageName)|| "ItemStockVan".equalsIgnoreCase(pageName)  	){
	    	headName = "�����Թ���("+pageName+")";
	    	codeSearchTxtName = "�����Թ���";
	    	descSearchTxtName = "�����Թ���";
	    }else  if("PDStockVan".equalsIgnoreCase(pageName)){
		 	headName = "PD/˹���ö("+pageName+")";
		 	codeSearchTxtName = "PD/˹���ö";
		 	descSearchTxtName = "���� PD/˹���ö";
	    }else  if("SubInvOnhand".equalsIgnoreCase(pageName)){
		 	headName = "Sub Inv (Stock Onhand)";
		 	codeSearchTxtName = "SubInv";
		 	descSearchTxtName = "SubInv Name";
	    }else  if("Item".equalsIgnoreCase(pageName)){
		 	headName = "�Թ���";
		 	codeSearchTxtName = "Product Code";
		 	descSearchTxtName = "Product Name";
	    }else  if("CustomerCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "��ҹ��� �ôԵ(Credit Sales)";
		 	codeSearchTxtName = "Customer Code";
		 	descSearchTxtName = "Customer Name";
	    }else  if("SalesrepCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "��ѡ�ҹ���(Credit Sales)";
		 	codeSearchTxtName = "Salesrep Code";
		 	descSearchTxtName = "Salesrep Name";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
