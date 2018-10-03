package com.pens.test.printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.MediaName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.RequestingUserName;

import com.isecinc.pens.inf.helper.Utils;


public class TestPrinterMZ320 implements Printable {

	private List<PageData> printData = null;;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//	p.setData("123456789012345678901234567890123456789012345678901234567890");
	    try{
	    	List<PageData> listData = new ArrayList<PageData>();
	    	//Calc Page
	    	LineData line1 =null;
	    	LineData line2 = null;
	    	LineData line3 =null;
	    	
	    	PageData p = new PageData();
	    	p.setPage(0);
	    	line1 = new LineData("head","                              ใบหยิบของ  ",-4);
	    	line2 = new LineData("head","     บริษัท เพนส์ มาร์เก็ตติ้ง แอนด์ ดิสทริบิวชั่น จำกัด                                                      ",-2);
	    	//LineData line3 = new LineData("normal","662/19-20 ถนนพระราม 3 แขวงบางโพงพาง เขตยานนาวา กรุงเทพฯ 10120",-3);
	    	p.getData().add(line1);
	    	p.getData().add(line2);
	    	//p.getData().add(line3);
	    	listData.add(p);
	    	
	    	p = new PageData();
	    	p.setPage(1);
	    	line1 = new LineData("normal","662/19-20 ถนนพระราม 3 แขวงบางโพงพาง เขตยานนาวา กรุงเทพฯ 10120",-4);
	    	line2 = new LineData("normal","            โทร. 0-2294-7300 โทรสาร. 0-2294-7560  ",-3);
	    	p.getData().add(line1);
	    	p.getData().add(line2);
	    	listData.add(p);
	    	
	    	p = new PageData();
	    	p.setPage(2);
	    	line1 = new LineData("normal","ลูกค้า:1490300001-ชัยเจริญ",-4);
	    	line2 = new LineData("normal","ที่อยู่ :9/1 ม.8 ตำบลม่องต่อง อำเภอ                                             วันที่:"+Utils.stringValue(new Date(), Utils.DD_MM_YYYY_HH_mm_ss_WITH_SLASH),-3);
	    	line3 = new LineData("normal","ลพบุรี 10240",-3);
	    	p.getData().add(line1);
	    	p.getData().add(line2);
	    	p.getData().add(line3);
	    	listData.add(p);
		    
	    	new TestPrinterMZ320(listData).printToPrinter();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	}

   public  void printToPrinter() {

	    PrinterJob pjob = PrinterJob.getPrinterJob();
	   // System.out.println("pf:"+pjob.get);
	    PageFormat pf = pjob.defaultPage();
	    Paper paper = pf.getPaper();
        double width = 220;
        double height = 16;
        double margin = 0;
        paper.setSize(width, height);
        paper.setImageableArea(
                margin,
                margin,
                width,
                height);
      
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
       
	    System.out.println("pf width :"+pf.getWidth());
	    System.out.println("pf height:"+pf.getHeight());
	    pjob.setPrintable(this, pf);
	    
	    HashPrintRequestAttributeSet printParams = new HashPrintRequestAttributeSet();
	    boolean ok = pjob.printDialog();
	    if (ok) {
	      try {
	    	
	    	  pjob.print();
	      } catch (PrinterException ex) {
	    	  ex.printStackTrace();
	        // The job did not successfully complete 
	      }
	    }
	 }
   
	public TestPrinterMZ320(List<PageData> data){
		this.printData = data;
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException
	{
		System.out.println("*********page:"+page +",pf_x["+pf.getImageableX()+"]pf_y["+pf.getImageableY()+"]******************");
	    // Should only have one page, and page # is zero-based.
	    if (page > printData.size()-1)
	    {
	        return NO_SUCH_PAGE;
	    }

	        // Adding the "Imageable" to the x and y puts the margins on the page.
		    // To make it safe for printing.
		    Graphics2D g2d = (Graphics2D)g;
		    int x = (int) pf.getImageableX();
		    int y = (int) pf.getImageableY();        
		    g2d.translate(x, y); 
			g2d.setColor(Color.black);
		

			Font font = new Font("Serif", Font.PLAIN, 6);//default
			int lineHeight = 0;
		    // Draw the page:
			
		    try{
		    	PageData p = (PageData)printData.get(page);
		    	int i = 0;
		    	for(LineData lines :p.getData()){
		    	   if("head".equals(lines.getType())){
		    		  font = new Font("Serif", Font.BOLD, 8);
		   		      FontMetrics metrics = g.getFontMetrics(font);
		   		      System.out.println("Font8:height["+metrics.getHeight()+"]");
		   		      lineHeight += metrics.getHeight();
		   		      lineHeight += lines.getDiff();
		    	   }else if("normal".equals(lines.getType())){
		    		  font = new Font("Serif", Font.PLAIN, 7);
		   		      FontMetrics metrics = g.getFontMetrics(font);
		   		      System.out.println("Font6:height["+metrics.getHeight()+"]");
		   		      lineHeight += metrics.getHeight();
		   		      lineHeight += lines.getDiff();
		    	 
		    	   }
		    	   System.out.println("Font:"+font.getName()+",data["+lines.getData()+"],x[0],y["+(lineHeight)+"]");
		    	   g2d.setFont(font);
		    	   g2d.drawString(lines.getData(), 0, lineHeight);
		    	   i++;
		    	}//for
		    }catch (Exception e) {
	    	   e.printStackTrace();
	        }

	    return PAGE_EXISTS;
	  }
	
	private void printToPrinter1(){
		try{
			String defaultPrinter = PrintServiceLookup.lookupDefaultPrintService().getName();
		    System.out.println("Default printer: " + defaultPrinter);
		    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
	
		    DocAttributeSet dset = new HashDocAttributeSet();
	        dset.add(new DocumentName("print doc name", Locale.US));

	        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
	        //aset.add(new Copies(3));
	        //aset.add(MediaSizeName.ISO_A7);
	        aset.add(MediaSizeName.NA_LETTER);
	        aset.add(new RequestingUserName("Test", Locale.US));
	        
	
		    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		    
		    Doc doc = new SimpleDoc(this, flavor, dset);
		    DocPrintJob job = service.createPrintJob();
		    job.print(doc, aset);
		    
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void printToPrinter3(){
		try{
			String defaultPrinter = 
		    PrintServiceLookup.lookupDefaultPrintService().getName();
		    System.out.println("Default printer: " + defaultPrinter);
		    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
	
		    //Data Test
		    String data = "";
		    data +="Head" +
		    		"\nbody";
		    //data +="";
/*		    data +="\n Body2 \n";
		    data +="\n Body3 \n";
		    data +="\n Body4 \n";
		    data +="\n Footer\n";*/
		    
		    // prints the famous hello world! plus a form feed
		    InputStream is = new ByteArrayInputStream(data.getBytes("UTF8"));
	
		    PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		    pras.add(new Copies(1));
		    pras.add(OrientationRequested.PORTRAIT);
		    pras.add(MediaSizeName.ISO_A7);
		    
		    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		    Doc doc = new SimpleDoc(is, flavor, null);
		    DocPrintJob job = service.createPrintJob();

		    job.print(doc, pras);
		    
		  /*  PrintJobWatcher pjw = new PrintJobWatcher(job);
		    job.print(doc, pras);
		    pjw.waitForDone();*/
		    is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	

}
