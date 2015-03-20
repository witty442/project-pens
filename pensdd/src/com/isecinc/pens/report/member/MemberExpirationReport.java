package com.isecinc.pens.report.member;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Member Expiration Report
 * 
 * @author PasuwatW
 * 
 */

public class MemberExpirationReport implements Serializable /*, Comparable*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3891917242278626563L;
	// Defind Parameter
    private String dateFrom;
    private String dateTo;
    
    //Define Report Field
    private Timestamp expFrom;
    private Timestamp expTo;
    
    private String memberCode ;
    private String memberName ;
    private String memberTel ;
    private BigDecimal totalQtySent ;
    private int tripNo;
    private Timestamp expireDate;
    
	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberTel() {
		return memberTel;
	}

	public void setMemberTel(String memberTel) {
		this.memberTel = memberTel;
	}

	public BigDecimal getTotalQtySent() {
		return totalQtySent;
	}

	public void setTotalQtySent(BigDecimal totalQtySent) {
		this.totalQtySent = totalQtySent;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public Timestamp getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Timestamp expireDate) {
		this.expireDate = expireDate;
	}

	public Timestamp getExpFrom() {
		return expFrom;
	}

	public void setExpFrom(Timestamp expFrom) {
		this.expFrom = expFrom;
	}

	public Timestamp getExpTo() {
		return expTo;
	}

	public void setExpTo(Timestamp expTo) {
		this.expTo = expTo;
	}

	/*public int compareTo(Object o) {
		if (!(o instanceof MemberExpirationReport))
		      throw new ClassCastException();

		MemberExpirationReport report = (MemberExpirationReport) o;

		return this.tripNo .compareTo(report.getProductCode());
		return 1;
	}*/
}
