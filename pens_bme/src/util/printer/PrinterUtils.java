package util.printer;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.log4j.Logger;

public class PrinterUtils {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] s){
		isPrinterIsOnline("");
	}
	
	public static boolean isPrinterIsOnline(String printerName){
		boolean online = false;
		try{
			DocFlavor myFormat = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
	        PrintService[] services =PrintServiceLookup.lookupPrintServices(myFormat, aset);
	        System.out.println("The following printers are available");
	        for (int i=0;i<services.length;i++) {
	            System.out.println("  service name: "+services[i].getName());
	            if(printerName.equalsIgnoreCase(services[i].getName())){
	            	online = true;
	            	break;
	            }
	        }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return online;
	}
	/**
	 * Xerox-Payslip-1
	 * @param printerName
	 * @return
	 */
	public static List<PrinterBean> listPrinterXeroxPayslipIsOnline(){
		List<PrinterBean> listPrinter = new ArrayList<PrinterBean>();
		try{
			DocFlavor myFormat = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
	        PrintService[] services =PrintServiceLookup.lookupPrintServices(myFormat, aset);
	        System.out.println("The following printers are available");
	        for (int i=0;i<services.length;i++) {
	            //System.out.println("  service name: "+services[i].getName());
	            if(services[i].getName().startsWith("Xerox-Payslip")){
	            	PrinterBean p = new PrinterBean();
	            	p.setName(services[i].getName());
	            	if("Xerox-Payslip-1".equalsIgnoreCase(services[i].getName())){
	            	   p.setDesc("Printer 股暽 1");
	            	}else if("Xerox-Payslip-2".equalsIgnoreCase(services[i].getName())){
		               p.setDesc("Printer 股暽 2");
	            	}else if("Xerox-Payslip-3".equalsIgnoreCase(services[i].getName())){
			           p.setDesc("Printer 股暽 3");
	            	}else if("Xerox-Payslip-4".equalsIgnoreCase(services[i].getName())){
			           p.setDesc("Printer 股暽 4");
			        }
	            	listPrinter.add(p);
	            }
	        }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return listPrinter;
	}
}
