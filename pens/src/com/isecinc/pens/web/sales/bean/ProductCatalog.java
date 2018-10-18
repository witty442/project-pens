package com.isecinc.pens.web.sales.bean;

public class ProductCatalog implements Comparable{
	private String target;
	private int productId;
	private String productCode;
	private String productName;
	private String uom1;
	private String uom2;
	private double price1;
	private double price2;
	private int qty1;
	private int qty2;
	private double lineNetAmt;
    private String taxable;
   

	public String getTaxable() {
		return taxable;
	}

	public void setTaxable(String taxable) {
		this.taxable = taxable;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		try {
			return new String(this.productName.getBytes("ISO8859-1"), "TIS-620");
		} catch (Exception ex) {
			return "";
		}
	}

	public String getProductNameDisplay() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUom1() {
		return uom1;
	}

	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}

	public String getUom2() {
		return uom2;
	}

	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}

	public double getPrice1() {
		return price1;
	}

	public void setPrice1(double price1) {
		this.price1 = price1;
	}

	public double getPrice2() {
		return price2;
	}

	public void setPrice2(double price2) {
		this.price2 = price2;
	}

	public int getQty1() {
		return qty1;
	}

	public void setQty1(int qty1) {
		this.qty1 = qty1;
	}

	public int getQty2() {
		return qty2;
	}

	public void setQty2(int qty2) {
		this.qty2 = qty2;
	}

	public double getLineNetAmt() {
		return lineNetAmt;
	}

	public void setLineNetAmt(double lineNetAmt) {
		this.lineNetAmt = lineNetAmt;
	}

	public double getAmount1() {
		return this.getPrice1() * this.getQty1();
	}

	public double getAmount2() {
		return this.getPrice2() * this.getQty2();
	}

	public String convert(String str) {
		StringBuffer ostr = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			if ((ch >= 0x0020) && (ch <= 0x007e)) // Does the char need to be
													// converted to unicode?
			{
				ostr.append(ch); // No.
			} else // Yes.
			{
				ostr.append("\\u"); // standard unicode format.
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF); // Get
																			// hex
																			// value
																			// of
																			// the
																			// char.
				for (int j = 0; j < 4 - hex.length(); j++)
					// Prepend zeros because unicode requires 4 digits
					ostr.append("0");
				ostr.append(hex.toLowerCase()); // standard unicode format.
				// ostr.append(hex.toLowerCase(Locale.ENGLISH));
			}
		}

		return (new String(ostr)); // Return the stringbuffer cast as a string.

	}

	public String encodeURIComponent(String input) throws Exception {
		int l = input.length();
		StringBuilder o = new StringBuilder(l * 3);
		for (int i = 0; i < l; i++) {
			String e = input.substring(i, i + 1);
			if (EX.indexOf(e) == -1) {
				byte[] b = e.getBytes("utf-8");
				o.append(getHex(b));
				continue;
			}
			o.append(e);
		}
		return o.toString();
	}

	final String EX = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

	public String getHex(byte buf[]) {
		StringBuilder o = new StringBuilder(buf.length * 3);
		for (int i = 0; i < buf.length; i++) {
			int n = (int) buf[i] & 0xff;
			o.append("%");
			if (n < 0x10)
				o.append("0");
			o.append(Long.toString(n, 16).toUpperCase());
		}
		return o.toString();
	}

	public String getBrandCode() {
		return this.productCode.substring(0,3);
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof ProductCatalog))
		      throw new ClassCastException();

		ProductCatalog catalog = (ProductCatalog) o;

		return productCode.compareTo(catalog.getProductCode());
	}
}
