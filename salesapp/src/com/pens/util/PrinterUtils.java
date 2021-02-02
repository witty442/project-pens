package com.pens.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;
import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;

public class PrinterUtils {
	
	public static Logger logger = Logger.getLogger("PENS");
	public static Map<String, String> PRINTER_ONLINE_MAP = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		try{
		    //Map<String, String> wmiObjectProperties = WMI4Java.get().VBSEngine().getWMIObject("Win32_BIOS");
			
			//Filter results by property value
		    //Map<String, String> wmiObjectProperties = 
		    //WMI4Java.get().filters(Arrays.asList("$_.Name -eq \"java.exe\"")).getWMIObject("Win32_Process");
			/* Iterator its = wmiObjectProperties.keySet().iterator();
			    while(its.hasNext()){
			    	String key = (String)its.next();
			    	String value = wmiObjectProperties.get(key);
			    	logger.debug("key["+key+"]value["+value+"]");
			    	
			    }*/
			    
			//Example Win32_PRINTER
			// Map<String, String> wmiObjectProperties = WMI4Java.get().VBSEngine().getWMIObject("Win32_PRINTER");
			
			 //Map<String, String> wmiObjectProperties = WMI4Java.get().VBSEngine().getWMIObject("Win32_PRINTER");
			 
			 /*Iterator its = wmiObjectProperties.keySet().iterator();
			    while(its.hasNext()){
			    	String key = (String)its.next();
			    	String value = wmiObjectProperties.get(key);
			    	logger.debug("key["+key+"]value["+value+"]");
			    	
			    }*/
			
			
			/*System.out.println(
					   WMI4Java
		                .get()
		                .properties(Arrays.asList("Name", "WorkOffline"))
		                .filters(Arrays.asList("$_.WorkOffline -eq 0"))
		                .getRawWMIObjectOutput(WMIClass.WIN32_PRINTER)
		        );*/
			
			initPrinterOnlineMap();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//New Method
	public static  String selectPrinterSmallIsOnlineCheckOnline(){
		String selectPrinterName = "";
		logger.info("selectPrinterSmallIsOnlineCheckOnline");
		try{
			//init ONLINE PRINTER MAP
			initPrinterOnlineMap();
			
			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
			//PrintService[] services = PrintServiceLookup.lookupDefaultPrintService(null, null);
			if (services == null || services.length < 1) {
				throw new  Exception("printer Exception");
			}
			
			/* Scan found services to see if anyone suits our needs */
			for(int i = 0; i < services.length;i++){
			   if(   services[i].getName().toUpperCase().contains("ZDesigner MZ 320".toUpperCase())
				  || services[i].getName().toUpperCase().contains("ZDesigner iMZ320".toUpperCase())
				  || services[i].getName().toUpperCase().contains("BIXOLON SPP-R310".toUpperCase())
				  ){
			      logger.info("Check Printer Name["+services[i].getName().toUpperCase()+"]");
				  if(PRINTER_ONLINE_MAP.get(services[i].getName()) != null){
				     selectPrinterName = services[i].getName();
				     logger.info("Selected Printer["+services[i].getName()+"] IS ONLINE");
				     break;
				  }
			   }//if
			}//for
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return selectPrinterName;
	}
	public static void initPrinterOnlineMap(){
		logger.info("initPrinterOnlineMap");
		
		 String rawOutput = 
		 WMI4Java
         .get()
         .properties(Arrays.asList("Name", "WorkOffline"))
         .filters(Arrays.asList("$_.WorkOffline -eq 0"))
         .getRawWMIObjectOutput(WMIClass.WIN32_PRINTER);
		 
		String[] lineArr = rawOutput.split("(\r?\n)");
		String key = "";
		String value = "";
		for(int i=0;i<lineArr.length;i++){
			//System.out.println(lineArr[i]);
			if(lineArr[i].startsWith("Name")){
				key = Utils.isNull(lineArr[i].substring(lineArr[i].indexOf(":")+1,lineArr[i].length()));
			}else{
				value = Utils.isNull(lineArr[i].substring(lineArr[i].indexOf(":")+1,lineArr[i].length()));
			}
			if( !Utils.isNull(key).equals("") && !Utils.isNull(value).equals("") ){
				//logger.debug(" PRINTER key:"+key+",value:"+value);
				//WorkOffline :False
				if(Utils.isNull(value).equalsIgnoreCase("False")){
					logger.debug("MAP ONLINE PRINTER key:"+key+",value:"+value);
					PRINTER_ONLINE_MAP.put(key, value);
				}
				//Reset value
				key = "";value ="";
			}
		}
	}
	
	
	//OLD VERSION IS WORK
	public static  String selectPrinterSmall(){
		String selectPrinterName = "ZDesigner MZ 320";
		logger.info("selectPrinterSmall");
		try{
		
			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
			if (services == null || services.length < 1) {
				  throw new  Exception("printer Exception");
			}
			
			/* Scan found services to see if anyone suits our needs */
			for(int i = 0; i < services.length;i++){
			   if(   services[i].getName().toUpperCase().contains("ZDesigner MZ 320".toUpperCase())
				  || services[i].getName().toUpperCase().contains("ZDesigner iMZ320".toUpperCase())
				  || services[i].getName().toUpperCase().contains("BIXOLON SPP-R310".toUpperCase())
				  ){
			      logger.info("Found Printer Name["+services[i].getName().toUpperCase()+"]");
			    
				  if(services[i].getName().toUpperCase().contains("ZDesigner MZ 320".toUpperCase())){
					     selectPrinterName = "ZDesigner MZ 320";
					     break;
				  }else  if(services[i].getName().toUpperCase().contains("ZDesigner iMZ320".toUpperCase())){
					     selectPrinterName = "ZDesigner iMZ320";
					     break;  
				  }else  if(services[i].getName().toUpperCase().contains("BIXOLON SPP-R310".toUpperCase())){
					     selectPrinterName = "BIXOLON SPP-R310";
					     break;
				  }//if
			   }//if
			}//for
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return selectPrinterName;
	}
}
