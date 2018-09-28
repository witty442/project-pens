package util;

import java.util.Locale;
import java.util.ResourceBundle;

public class BundleUtil {

	public static ResourceBundle getBundle(String bundlefilename, Locale locale) {
		ResourceBundle bundle = null;
		try {
			if (locale == null) {
				locale = new Locale("th", "TH");
			}
			bundle = ResourceBundle.getBundle(bundlefilename, locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bundle;
	}
}
