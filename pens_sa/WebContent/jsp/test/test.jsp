
<%@page import="java.io.IOException"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<%
try {
	URL url = new URL("https://www.google.com");
	HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	urlConn.connect();
	out.print("Output Connection:"+urlConn.toString());
} catch (IOException e) {
    System.err.println("Error creating HTTP connection");
    out.print("Output Connection:"+e.getMessage());
    e.printStackTrace();
    throw e;
}
%>
