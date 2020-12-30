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
	    	headName = "�ù��("+pageName+")";
	    	codeSearchTxtName = "�ù��";
	    	descSearchTxtName = "���� �ù��";
	    }else  if("Customer".equalsIgnoreCase(pageName) || "CustomerStock".equalsIgnoreCase(pageName)
	      || "CustomerVanProdShow".equalsIgnoreCase(pageName) || "CustomerLocation".equalsIgnoreCase(pageName)
	      || "CustomerCreditPromotion".equalsIgnoreCase(pageName)|| "CustomerLocNoTrip".equalsIgnoreCase(pageName) 
	      || "CustomerStockMC".equalsIgnoreCase(pageName) || "CustomerMaster".equalsIgnoreCase(pageName)){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>��ҹ���(����ѧ����á�˹� �ش/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>��ҹ���("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "������ҹ���";
	    	descSearchTxtName = "������ҹ���";
	    }else  if("ItemStock".equalsIgnoreCase(pageName) || "ItemCreditPromotion".equalsIgnoreCase(pageName)
	    	       || "ItemCreditPromotion".equalsIgnoreCase(pageName)|| "ItemStockVan".equalsIgnoreCase(pageName)  	
	    	       || "ItemSalesTargetPD".equalsIgnoreCase(pageName)){
	    	headName = "�����Թ���("+pageName+")";
	    	codeSearchTxtName = "�����Թ���";
	    	descSearchTxtName = "�����Թ���";
	    }else  if("PDStockVan".equalsIgnoreCase(pageName) ){
		 	headName = "PD/˹���ö("+pageName+")";
		 	codeSearchTxtName = "PD/˹���ö";
		 	descSearchTxtName = "���� PD/˹���ö";
	    }else  if("PD".equalsIgnoreCase(pageName) || "PDProvince".equalsIgnoreCase(pageName)
	    		|| "PDBoxNo".equalsIgnoreCase(pageName)){
		 	headName = "PD("+pageName+")";
		 	codeSearchTxtName = "PD";
		 	descSearchTxtName = "���� PD";
	    }else  if("SubInvOnhand".equalsIgnoreCase(pageName)){
		 	headName = "Sub Inv (Stock Onhand)";
		 	codeSearchTxtName = "SubInv";
		 	descSearchTxtName = "SubInv Name";
	    }else  if("Item".equalsIgnoreCase(pageName) ){
		 	headName = "�Թ���";
		 	codeSearchTxtName = "�����Թ���";
		 	descSearchTxtName = "�����Թ���";
	    }else  if("CustomerCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "��ҹ��� �ôԵ(Credit Sales)";
		 	codeSearchTxtName = "������ҹ���";
		 	descSearchTxtName = "������ҹ���";
	    }else  if("SalesrepCreditSales".equalsIgnoreCase(pageName)){
		 	headName = "��ѡ�ҹ���(Credit Sales)";
		 	codeSearchTxtName = "���ʾ�ѡ�ҹ���";
		 	descSearchTxtName = "���� ��ѡ�ҹ���";
	    }else  if("CustomerProjectC".equalsIgnoreCase(pageName)){
		 	headName = "��ҹ��� (Credit Sales-Project C)";
		 	codeSearchTxtName = "������ҹ���";
		 	descSearchTxtName = "������ҹ���";
	    }else  if("BranchProjectC".equalsIgnoreCase(pageName)){
		 	headName = "�Ң���ҹ��� (Credit Sales-Project C)";
		 	codeSearchTxtName = "Branch Id";
		 	descSearchTxtName = "Branch Name";
	    }else  if("BranchStockMC".equalsIgnoreCase(pageName)){
		 	headName = "�Ң���ҹ��� (StockMC)";
		 	codeSearchTxtName = "Branch Name";
		 	descSearchTxtName = "Branch Name";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
