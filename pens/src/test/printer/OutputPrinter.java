package test.printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class OutputPrinter implements Printable 
{
    private String printData;

    public OutputPrinter(String printDataIn) {
    	this.printData = printDataIn;
    }


public int print(Graphics g, PageFormat pf, int page) throws PrinterException
{
	System.out.println("page:"+page +"pf_x["+pf.getImageableX()+"]pf_y["+pf.getImageableY()+"]");
    // Should only have one page, and page # is zero-based.
    if (page > 0)
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
	
	    // Calculate the line height
	    Font font = new Font("Serif", Font.PLAIN, 1);
	    FontMetrics metrics = g.getFontMetrics(font);
	    int lineHeight = metrics.getHeight();
	    System.out.println("lineHeight["+lineHeight+"]");
	    
	    BufferedReader br = new BufferedReader(new StringReader(printData));
	
	    // Draw the page:
	    try
	    {
	        String line;
	        // Just a safety net in case no margin was added.
	        x = 0;
	        y = 5;
	        while ((line = br.readLine()) != null)
	        {
	        	
	           // y += lineHeight;
	            System.out.println("Line:"+line+",x["+x+"]y["+y+"]");
	            g2d.drawString(line, x, y);
	            
	            y += lineHeight+10;;
	        }
	    }
    catch (IOException e)
    {
        // 
    	e.printStackTrace();
    }

    return PAGE_EXISTS;
  }


public int print1(Graphics g,PageFormat pf,int pageIndex) {
	System.out.println("page:"+pageIndex +"pf_x["+pf.getImageableX()+"]pf_y["+pf.getImageableY()+"]");
	if (pageIndex == 0) {
		Graphics2D g2d= (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY()); 
		g2d.setColor(Color.black);
		g2d.drawString("example string", 0, 50);
		//g2d.fillRect(0, 0, 200, 200);
		return Printable.PAGE_EXISTS;					
	} else {
		return Printable.NO_SUCH_PAGE;
	}
}

}
