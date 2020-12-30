package com.isecinc.pens.web.popup;

public class PopupHelper {

	
	public static String[] genHeadTextPopup(String pageName){
		String headName ="";
		String codeSearchTxtName = "";
	    String descSearchTxtName = "";
		String[] headTextArr = new String[3];
		 /** Criteria Name **/
	    if("Brand".equalsIgnoreCase(pageName) 	){
	    	headName = "�ù��("+pageName+")";
	    	codeSearchTxtName = "�ù��"; 
	    	descSearchTxtName = "���� �ù��";
	    }else if("SUB_Brand".equalsIgnoreCase(pageName) 	){
 	    	headName = "�ù������("+pageName+")";
 	    	codeSearchTxtName = "SubBrand"; 
 	    	descSearchTxtName = "SubBrand Name";
	    }else  if("Customer".equalsIgnoreCase(pageName) ){
	    	
	    	if("CustomerLocNoTrip".equalsIgnoreCase(pageName)){
	    	  headName = "<span title='"+pageName+"'>��ҹ���(����ѧ����á�˹� �ش/Trip)</span>";
	    	}else{
	    	  headName = "<span title='"+pageName+"'>��ҹ���("+pageName+")</span>";
	    	}
	    	
	    	codeSearchTxtName = "������ҹ���";
	    	descSearchTxtName = "������ҹ���";
	    }else  if("PRODUCT".equalsIgnoreCase(pageName) || "PRODUCT_INFO".equalsIgnoreCase(pageName) ){
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
	    }else  if("SalesrepSales".equalsIgnoreCase(pageName)){
		 	headName = "��ѡ�ҹ���";
		 	codeSearchTxtName = "���ʾ�ѡ�ҹ���";
		 	descSearchTxtName = "���;�ѡ�ҹ���";
	    }else  if("PICKING_NO".equalsIgnoreCase(pageName)){
		 	headName = "Picking No";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Picking No";
	    }else  if("PICKING_NO_PRINT".equalsIgnoreCase(pageName)){
		 	headName = "Picking No(����ա�� ����� Picking List ����)";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Transaction Date";
	    }else  if("PICKING_NO_INVOICE".equalsIgnoreCase(pageName)){
		 	headName = "Picking No (����� INVOCIE)";
		 	codeSearchTxtName = "Picking No";
		 	descSearchTxtName = "Picking No";
	    }
	    headTextArr[0] = headName;
	    headTextArr[1] = codeSearchTxtName;
	    headTextArr[2] = descSearchTxtName;
	    return headTextArr;
	}
}
