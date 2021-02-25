package com.pens.test;

import java.util.ArrayList;
import java.util.List;

import com.pens.util.FileUtil;

public class testGenScript {

	/*static String dataSql = "update t_order \n"+
		"  set payment = 'Y' \n"+
		" ,remark ='BugAutoReceipt' \n"+
		" where 1=1 \n"+
		" and doc_status ='SV'  \n"+
		" and payment ='N'  \n"+
		" and date(created) <= '2014-02-27' \n"+
		" and order_no not in(  \n"+
		" select receipt_no from t_receipt where 1=1  \n"+
		" ); \n";*/
	
	/*static String dataSql = "update t_order \n"+
			"  set payment = 'Y' \n"+
			" ,remark ='BugLineNoPayment' \n"+
			" where 1=1 \n"+
			" and doc_status ='SV'  \n"+
			" and interfaces ='Y'  \n"+
			" and exported ='Y'  \n"+
			" and ar_invoice_no is not null  \n"+
			" and payment ='N'  \n"+
			" and order_date >= '2014-03-01' and order_date <= '2014-03-03'\n";*/
	
	static String dataSql = "delete from m_sales_target_new;";
			
	
	static List<String> vanList = new ArrayList<String>();
	static{
		vanList.add("V001");
		vanList.add("V002");
		vanList.add("V003");
		vanList.add("V051");
		vanList.add("V054");
		vanList.add("V055");
		
		vanList.add("V101");
		vanList.add("V102"); 
		vanList.add("V103");
		vanList.add("V104");
		vanList.add("V105"); 
		vanList.add("V106");
		vanList.add("V107"); 
		vanList.add("V108");
		vanList.add("V109");

		vanList.add("V201");
		vanList.add("V202");
		vanList.add("V203");
		vanList.add("V204");
		vanList.add("V205");
		vanList.add("V206");
		vanList.add("V207");
		
		vanList.add("V301");
		vanList.add("V302");
		vanList.add("V303");
		vanList.add("V304");
		vanList.add("V305");
		vanList.add("V306");
		vanList.add("V307");
		vanList.add("V308");
		vanList.add("V309");
		vanList.add("V310");
		vanList.add("V311");
		
		vanList.add("V401");
		vanList.add("V402");
		vanList.add("V403");
		vanList.add("V404");
		vanList.add("V405");
		vanList.add("V406");
		vanList.add("V407");

	}
	
	static List<String> creditList = new ArrayList<String>();
	static{
		creditList.add("S001");
		creditList.add("S002");
		creditList.add("S003");
		creditList.add("S004");
		creditList.add("S005");
		
		creditList.add("S101");
		creditList.add("S102"); 
		creditList.add("S103");
		creditList.add("S104");
		
		creditList.add("S201");
		creditList.add("S202");
		creditList.add("S203");
		creditList.add("S204");
		creditList.add("S205");
	
		creditList.add("S301");
		creditList.add("S302");
		creditList.add("S303");
		creditList.add("S304");
	
		creditList.add("S401");
		creditList.add("S402");
		creditList.add("S403");
		creditList.add("S404");

	}
	static String pathVan = "D:/Work_ISEC/A-TEMP-DB-C4/GenScript/ScriptVan/";
	static String pathCredit = "D:/Work_ISEC/A-TEMP-DB-C4/GenScript/ScriptCredit/";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		genVanScriptFile();
		//genCreditScriptFile();
		
	}
	
	public static void genVanScriptFile(){
		for(int i=0;i<vanList.size();i++){
			String saleCode = (String)vanList.get(i);
			FileUtil.writeFile(pathVan+"script_"+saleCode+".sql",dataSql);
		}
	}
	
	public static void genCreditScriptFile(){
		for(int i=0;i<creditList.size();i++){
			String saleCode = (String)creditList.get(i);
			FileUtil.writeFile(pathCredit+"script_"+saleCode+".sql",dataSql);
		}
	}

}
