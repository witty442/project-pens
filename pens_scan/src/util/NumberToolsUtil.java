package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Number Format Utility
 * 
 * @author Aneak.t
 * @version $Id: NumberToolsUtil.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class NumberToolsUtil {
	
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat("#,##0.00");
		return formatter.format(num);
	}
}
