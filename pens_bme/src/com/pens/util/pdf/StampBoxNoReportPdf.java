package com.pens.util.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.dao.ScanCheckDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.pens.util.BeanParameter;


public class StampBoxNoReportPdf {
	
 
  private static Font mainFont =new Font(Font.FontFamily.TIMES_ROMAN, 18,Font.BOLD);
  private static Font subFont =new Font(Font.FontFamily.TIMES_ROMAN, 16,Font.BOLD);
  protected static Logger logger = Logger.getLogger("PENS");
  private static int page = 0;
  private static int lineNumber = 0;
  private static int maxRowInPage = 33;
  private static int maxGroupCountInRow = 4;
  
  public static void main(String[] args) {
    try {
    	ScanCheckBean bean = new ScanCheckBean();
    	bean.setIssueReqNo("");
    	
        generate(null,bean);
      
        
        /*ByteArrayOutputStream out = new ByteArrayOutputStream();            
        try (Document doc = new Document(PageSize.A4, 50, 50, 50, 50)) {
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell = new PdfPCell(new Phrase("First PDF"));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
            table.addCell(cell);
            doc.add(table);
        }
        return new ByteArrayInputStream(out.toByteArray());*/
        
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
  
  public static InputStream  generate(HttpServletRequest request,ScanCheckBean bean)  throws Exception{
	  Connection conn= null;
	  int i= 0;
	  ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	  BaseFont bf1;
	  BaseFont bf2;
	  float left = 50;
      float right = 50;
      float top = 10;
      float bottom = 10;
      lineNumber = 0;
      page = 0;
      page++;
	    try {
	    	String absPath ="fonts/";// BeanParameter.getReportPath()+"fonts/";
	    	logger.debug("absPath:"+absPath);
	    	//Create BaseFont
	    	//bf1 = BaseFont.createFont(absPath+"ANGSAUB.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	//bf2 = BaseFont.createFont(absPath+"ANGSAU.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	
	    	//bf1 = BaseFont.createFont(absPath+"TAHOMABD.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	//bf2 = BaseFont.createFont(absPath+"TAHOMA.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	
	    	bf1 = BaseFont.createFont(absPath+"BROWAB.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	bf2 = BaseFont.createFont(absPath+"BROWA.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	
	    	mainFont = new Font(bf1, 20);
	    	subFont = new Font(bf2, 18);
	    	 
	    	conn = DBConnection.getInstance().getConnection();
	    	Document document = new Document(PageSize.A4, left, right, top, bottom);
	        PdfWriter.getInstance(document, out);
	        
	        document.open();
	      
	        addMetaData(document,"StampBoxNo");
	      
	       //Get BoxNo List
	       java.util.List<ScanCheckBean> boxNoList = ScanCheckDAO.getBoxNoList(conn, bean);
	       if(boxNoList != null && boxNoList.size() >0){
	    	  for(i=0;i<boxNoList.size();i++){
	    		 ScanCheckBean item = boxNoList.get(i);
	    		 //add box no to doc
	    		 addMainContent(conn,document,item);
	    	 }//for
	       }
	       document.close();
	      
	       return new ByteArrayInputStream(out.toByteArray());
	    } catch (Exception e) {
	        throw e;
	    }finally{
	    	if(conn != null){
	    		conn.close();
	    	}
	    }
	  }
  
  private static void addMainContent(Connection conn, Document document,ScanCheckBean item) throws Exception {
	    int i=0;
	    String groupCodeSplit = "";
	    java.util.List<ScanCheckBean> groupCodeList = null;
	    ScanCheckBean r = ScanCheckDAO.getGroupCodeListByBoxNo(conn, item);
	    groupCodeList = r.getItems();
	    
	      if(groupCodeList != null && groupCodeList.size() >0){
	    	  
	    	  lineNumber +=4 ;//add head line
	    	  //Calc new page
	    	  lineNumber += (groupCodeList.size()/maxGroupCountInRow)+1; 
	    	  logger.debug("page["+page+"]boxno["+item.getBoxNo()+"]lineNumber["+lineNumber+"]");
		      // Start a new page
		      if(lineNumber >= maxRowInPage){
		    	  page++;
		    	  logger.debug("new page["+page+"]:boxNo["+item.getBoxNo()+"]");
		          document.newPage();
		          lineNumber = 0;
		      }
		      
		    Paragraph preface = new Paragraph();
		    // We add one empty line
		    addEmptyLine(preface, 1);
		    // Lets write a big header
		    preface.add(new Paragraph("Issue Req No  : "+item.getIssueReqNo()+"           เลขที่กล่อง          : "+item.getBoxNo()+"/"+item.getTotalBox(), mainFont));
		    preface.add(new Paragraph("ร้านค้า              : "+item.getStoreCode()+" - "+item.getStoreName(), mainFont));
		    preface.add(new Paragraph("ยอดรวม   "+r.getTotalQty()+"  ชิ้น", mainFont));
		   //add to doc
		    document.add(preface);
		    
		    //add blank line
		    preface = new Paragraph("",subFont);
		    addEmptyLine(preface, 1);lineNumber++;
		    //add to doc
		    document.add(preface);
		      
	    	 int count = 0;
	    	 for(i=0;i<groupCodeList.size();i++){
	    		 ScanCheckBean group = groupCodeList.get(i);
	    		 count++;
	    		 //logger.debug("count["+count+"]mode["+count % maxGroupCountInRow+"]");
	    		 if(count % maxGroupCountInRow ==0){
	    			 groupCodeSplit += " "+group.getGroupCode()+" [ "+group.getTotalQty()+" ] /";
	    			 
	    			 groupCodeSplit = groupCodeSplit.substring(0,groupCodeSplit.length()-1);
	    			 addContentGroupCodeModel(document,groupCodeSplit);
	    		     groupCodeSplit = "";

	    		 }else{
	    			 groupCodeSplit += " "+group.getGroupCode()+" [ "+group.getTotalQty()+" ] /";
	    		 }
	    	 }//for
	    	 
	    	 if(!"".equals(groupCodeSplit)){
	    		  groupCodeSplit = groupCodeSplit.substring(0,groupCodeSplit.length()-1);
	    		  addContentGroupCodeModel(document,groupCodeSplit);
	    		  groupCodeSplit = "";
	    	 }
	    	
	      }//if
	      
	      //add blank line
	      Paragraph preface = new Paragraph("",subFont);
		  addEmptyLine(preface, 1);lineNumber++;
		  //add to doc
		  document.add(preface);
  }
  
  private static Paragraph addContentGroupCodeModel(Document document,String groupCodeSplit) throws DocumentException {
		 
	    Paragraph preface = new Paragraph();
	    // Lets write a big header
	    preface.add(new Paragraph(groupCodeSplit, subFont));
	    
	    //add blank line
	    //addEmptyLine(preface, 1);lineNumber++;
	    //add to document
	    document.add(preface);
	    
	    return preface;
 }

  // iText allows to add metadata to the PDF which can be viewed in your Adobe
  // Reader
  // under File -> Properties
  private static void addMetaData(Document document,String title) {
    document.addTitle(title);
    document.addSubject("witty");
    document.addKeywords("Witty");
    document.addAuthor("Witty");
    document.addCreator("Witty");
  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
	//float s = 10;
    //float m = 0;
    //paragraph.setLeading(s, m);
	  
    for (int i = 0; i < number; i++) {
       paragraph.add(new Paragraph(" "));
    }
  }//function
} 
