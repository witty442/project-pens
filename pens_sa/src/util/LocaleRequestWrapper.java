package util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class LocaleRequestWrapper extends HttpServletRequestWrapper {
	public LocaleRequestWrapper(HttpServletRequest req) {
		super(req);
	}

	@SuppressWarnings("unchecked")
	public Enumeration getLocales() {
		Vector v = new Vector(1);
		v.add(getLocale());
		return v.elements();
	}

	public Locale getLocale() {
		return new Locale("th", "TH");
	}
}