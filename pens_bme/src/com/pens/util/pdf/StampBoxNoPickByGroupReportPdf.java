package com.pens.util.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.dao.ScanCheckDAO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnection;
import com.pens.util.Utils;


public class StampBoxNoPickByGroupReportPdf {
	
 
  private static Font mainBigFont =new Font(Font.FontFamily.TIMES_ROMAN, 30,Font.BOLD);
  private static Font mainFont =new Font(Font.FontFamily.TIMES_ROMAN, 26,Font.BOLD);
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
    	
       // generate(null,bean);
      
        
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
  
  public static InputStream  generate(HttpServletRequest request,PickStock bean)  throws Exception{
	  Connection conn= null;
	  int i= 0;
	  ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	  BaseFont bf1;
	  BaseFont bf2;
	  BaseFont bfMainBigFont;
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
	    	
	    	bfMainBigFont =BaseFont.createFont(absPath+"BROWAB.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	bf1 = BaseFont.createFont(absPath+"BROWAB.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	bf2 = BaseFont.createFont(absPath+"BROWA.TTF", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
	    	
	    	mainBigFont = new Font(bfMainBigFont, 30);
	    	mainFont = new Font(bf1, 30);
	    	subFont = new Font(bf2, 18);
	    	 
	    	conn = DBConnection.getInstance().getConnection();
	    	Document document = new Document(PageSize.A4, left, right, top, bottom);
	        PdfWriter.getInstance(document, out);
	        
	        document.open();
	      
	        addMetaData(document,"StampBoxNoPickAll");
	      
	       //Get BoxNo List
	       ScanCheckBean pickStockBean = getPickStockGroup(conn, bean);
	       int noTemp =0;
	       int no =0;
	       if(pickStockBean != null ){
	    	  for(i=0;i<pickStockBean.getTotalBox();i++){
	    		  no++;
	    		  noTemp++;
	    		 //add box no to doc
	    		 pickStockBean.setBoxNo(no+"");
	    		 addMainContent(conn,document,pickStockBean);
	    		 
	    		 //show 4 group per page
	    		 if(noTemp%4==0){
	    	         document.newPage();
	    	         noTemp=0;
	    	     }
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
		 
	    Paragraph preface = new Paragraph();
	    // We add one empty line
	    addEmptyLine(preface, 1);
	    // Lets write a big header
	    preface.add(new Paragraph("Issue Req No : "+item.getIssueReqNo()+"      เลขที่กล่อง    : "+item.getBoxNo()+"/"+item.getTotalBox(), mainFont));
	    preface.add(new Paragraph("ร้านค้า   : "+item.getStoreCode()+" - "+item.getStoreName(), mainBigFont));
	    preface.add(new Paragraph("ที่อยู่   : "+item.getAddress(), subFont));
	   //add to doc
	    document.add(preface);
	    
	    //add blank line
	    preface = new Paragraph("",subFont);
	    addEmptyLine(preface, 1);lineNumber++;
	    //add to doc
	    document.add(preface);
	
	   //add blank line
	   Paragraph prefaceBlank = new Paragraph("",subFont);
	   addEmptyLine(prefaceBlank, 1);lineNumber++;
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

  public static ScanCheckBean getPickStockGroup(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ScanCheckBean h = null;
		int no=0;
		try {
			sql.append("\n select h.issue_req_no ,h.store_code,h.total_box ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.store_code) as customer_name ");
			sql.append("\n  ,(select M.pens_desc3 from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.store_code) as customer_address ");
			sql.append("\n from PENSBI.PENSBME_PICK_STOCK h ");
			sql.append("\n where 1=1   ");
	        sql.append("\n and h.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
	        
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   no++;
			   h = new ScanCheckBean();
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   h.setBoxNo(no+"");
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("customer_name")));
			   h.setAddress(Utils.isNull(rst.getString("customer_address")));
			   h.setTotalBox(rst.getInt("total_box"));
			   
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return h;
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
