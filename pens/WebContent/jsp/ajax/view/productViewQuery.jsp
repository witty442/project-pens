<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="com.isecinc.pens.bean.Product"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	User user = (User) session.getAttribute("user");

	String pName = request.getParameter("pName");
	String pBrand = request.getParameter("pBrand");
	
	System.out.println("Ajax Query pBrand:"+pBrand);
	
	String whereCause = "";
	Product[] results = null;
	try {

		PriceList pls = new MPriceList().getCurrentPriceList(user.getOrderType().getKey());
		int pricelistId = pls.getId();

		whereCause += "\n AND PRODUCT_ID IN ( ";
		whereCause += "\n SELECT T.PRODUCT_ID FROM( ";
		whereCause += "\n SELECT * FROM m_product pd ";
		whereCause += "\n WHERE pd.PRODUCT_ID IN ( ";
		whereCause += "\n SELECT DISTINCT(pp.product_id) ";
		whereCause += "\n FROM m_product_price pp , m_pricelist pl ";
		whereCause += "\n WHERE pp.pricelist_id = pl.pricelist_id ";
		whereCause += "\n AND pl.pricelist_id = " + pricelistId;
		whereCause += "\n AND pp.isactive = 'Y' ";
		whereCause += "\n )) T ";
		whereCause += "\n WHERE T.ISACTIVE = 'Y' ";
		if (pName != null && pName.trim().length() > 0) {
			pName = new String(pName.getBytes("ISO8859_1"), "UTF-8");
			whereCause += "\n AND T.CODE LIKE '%" + pName + "%' OR T.NAME LIKE '%" + pName
					+ "%' OR T.DESCRIPTION LIKE '" + pName + "%' ";
		}
		whereCause += "\n AND T.CODE NOT IN(SELECT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+user.getRole().getKey()+"') ";
		
		whereCause += "\n GROUP BY T.CODE, T.PRODUCT_ID, T.UOM_ID, T.DESCRIPTION, ";
		whereCause += "\n T.ISACTIVE, T.NAME, T.PRODUCT_CATEGORY_ID ";
		whereCause += "\n ORDER BY T.CODE ) ";
		
		if (pBrand != null && pBrand.trim().length() > 0) {
			pBrand = new String(pBrand.getBytes("ISO8859_1"), "UTF-8");
			whereCause += "\n AND PRODUCT_CATEGORY_ID IN( ";
			whereCause += "\n  select product_category_id from m_product_category  WHERE name LIKE '%" + pBrand + "%'";
			whereCause += "\n )";
		}
		
		whereCause += " ORDER BY CODE ";

		results = new MProduct().search(whereCause);

	} catch (Exception e) {
		e.printStackTrace();
	}
%>


<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MPriceList"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.PriceList"%><table align="center"
	border="0" cellpadding="3" cellspacing="1" class="result">
	<c:forEach var="results" items="<%=results %>" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO" />
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE" />
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>"
			onclick="selectProduct('${results.code}','${results.name}');"
			style="cursor: pointer; cursor: hand;">
			<td width="10%"><c:out value='${rows.index+1}' /></td>
			<td align="left">${results.code}&nbsp;&nbsp;${results.name}</td>
		</tr>
	</c:forEach>
</table>
