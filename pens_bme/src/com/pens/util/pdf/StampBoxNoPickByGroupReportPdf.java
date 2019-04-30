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
import com.pens.util.Utils;


public class StampBoxNoPickByGroupReportPdf {
	
 
  private static Font mainBigFont =new Font(Font.FontFamily.TIMES_ROMAN, 30,Font.BOLD);
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
	    	mainFont = new Font(bf1, 20);
	    	subFont = new Font(bf2, 18);
	    	 
	    	conn = DBConnection.getInstance().getConnection();
	    	Document document = new Document(PageSize.A4, left, right, top, bottom);
	        PdfWriter.getInstance(document, out);
	        
	        document.open();
	      
	        addMetaData(document,"StampBoxNoPickAll");
	      
	       //Get BoxNo List
	       java.util.List<ScanCheckBean> boxNoList = getBoxNoList(conn, bean);
	       int no =0;
	       if(boxNoList != null && boxNoList.size() >0){
	    	  for(i=0;i<boxNoList.size();i++){
	    		  no++;
	    		 ScanCheckBean item = boxNoList.get(i);
	    		
	    		 //add box no to doc
	    		 addMainContent(conn,document,item);
	    		 
	    		 //show 4 group per page
	    		 if(no%4==0){
	    	         document.newPage();
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
	    preface.add(new Paragraph("Issue Req No : "+item.getIssueReqNo()+"           เลขที่กล่อง          : "+item.getBoxNo()+"/"+item.getTotalBox(), mainFont));
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

  public static List<ScanCheckBean> getBoxNoList(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ScanCheckBean h = null;
		List<ScanCheckBean> items = new ArrayList<ScanCheckBean>();
		int no=0;
		try {
			sql.append("\n select M.* ,L.total_box FROM(");
				sql.append("\n select h.issue_req_no ,h.store_code,i.box_no ");
				sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.store_code) as customer_name ");
				sql.append("\n  ,(select M.pens_desc3 from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.store_code) as customer_address ");
				sql.append("\n from PENSBI.PENSBME_PICK_STOCK h, PENSBI.PENSBME_PICK_STOCK_I i ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no  ");
		        sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
				sql.append("\n  group by h.issue_req_no ,h.store_code,i.box_no ");
			
			sql.append("\n )M LEFT OUTER JOIN  ");
		    sql.append("\n (");
			    sql.append("\n select issue_req_no ,count(*) as total_box FROM(");
				sql.append("\n  select  distinct i.issue_req_no, i.box_no ");
				sql.append("\n  from PENSBI.PENSBME_PICK_STOCK h, PENSBI.PENSBME_PICK_STOCK_I i ");
				sql.append("\n  where 1=1   ");
				sql.append("\n  and h.issue_req_no = i.issue_req_no  ");
				sql.append("\n  and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			sql.append("\n     ) group by issue_req_no ");
			sql.append("\n) L ON  M.issue_req_no = L.issue_req_no");
			sql.append("\n  order by M.store_code asc  ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   no++;
			   h = new ScanCheckBean();
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   h.setBoxNo(no+"");
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("customer_name")));
			   h.setAddress(Utils.isNull(rst.getString("customer_address")));
			   h.setTotalBox(rst.getInt("total_box"));
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
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
