package util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SetCharacterEncodingFilterUtil implements Filter {

	protected String encoding;
	protected FilterConfig filterConfig;
	protected boolean useencode;

	public SetCharacterEncodingFilterUtil() {
		encoding = null;
		filterConfig = null;
		useencode = true;
	}

	public void destroy() {
		encoding = null;
		filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (useencode || request.getCharacterEncoding() == null) {
			String encoding = this.encoding;
			if (encoding != null) {
				request.setCharacterEncoding(encoding);
			}
		}
		HttpServletRequest request2 = (HttpServletRequest) request;
		LocaleRequestWrapper wrapper = new LocaleRequestWrapper(request2);
		chain.doFilter(wrapper, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		encoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("useencode");
		if (value == null) {
			useencode = true;
		} else if (value.equalsIgnoreCase("true")) {
			useencode = true;
		} else {
			useencode = false;
		}
	}
}
