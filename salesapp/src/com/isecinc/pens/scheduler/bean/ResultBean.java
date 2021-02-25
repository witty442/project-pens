package com.isecinc.pens.scheduler.bean;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 2 fields on this form:
 * <ul>
 * <li>user - [your comment here]
 * <li>name - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class ResultBean extends ActionForm
{
	private String id;
	private int no; 
	private String product;
	private String productId;
	private String userId;
	private String appNo;
	private String firstName;
	private String lastName;
	private String idNo;
	private String tranDate;
	private String tranTime;
	private String imgCL;
	private String imgID;
	private String consentType;
	private String appIn;
	private List enityList;
	private List productList;
	private String entityId;
	private String entityName;
	private String productName;
	private String consentId;
	private String consentName;
	private String inqRef;
	private String memberRef;
	private String memberShortName;
	private String inquirySource;
	private String typeOfInquiry;
	private String searchUserId;
	private String inquiryIdType;
	private String seachResult;
	private String userName;
	private String note;
	private String consentStatusId;
	private String consentStatusName;
	
	
    public void reset(ActionMapping mapping, HttpServletRequest  request) {

        // Reset values are provided as samples only. Change as appropriate.

    }
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        return errors;

    }
    
    
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppNo() {
		return appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getTranDate() {
		return tranDate;
	}

	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}

	public String getTranTime() {
		return tranTime;
	}

	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
	}

	public String getImgCL() {
		return imgCL;
	}

	public void setImgCL(String imgCL) {
		this.imgCL = imgCL;
	}

	public String getImgID() {
		return imgID;
	}

	public void setImgID(String imgID) {
		this.imgID = imgID;
	}

	public String getConsentType() {
		return consentType;
	}

	public void setConsentType(String consentType) {
		this.consentType = consentType;
	}

	public String getAppIn() {
		return appIn;
	}

	public void setAppIn(String appIn) {
		this.appIn = appIn;
	}
	public List getEnityList() {
		return enityList;
	}
	public void setEnityList(List enityList) {
		this.enityList = enityList;
	}
	public List getProductList() {
		return productList;
	}
	public void setProductList(List productList) {
		this.productList = productList;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String enityId) {
		this.entityId = enityId;
	}
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getConsentId() {
		return consentId;
	}
	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}
	public String getConsentName() {
		return consentName;
	}
	public void setConsentName(String consentName) {
		this.consentName = consentName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInqRef() {
		return inqRef;
	}
	public void setInqRef(String inqRef) {
		this.inqRef = inqRef;
	}
	public String getMemberRef() {
		return memberRef;
	}
	public void setMemberRef(String memberRef) {
		this.memberRef = memberRef;
	}
	public String getMemberShortName() {
		return memberShortName;
	}
	public void setMemberShortName(String memberShortName) {
		this.memberShortName = memberShortName;
	}
	public String getInquirySource() {
		return inquirySource;
	}
	public void setInquirySource(String inquirySource) {
		this.inquirySource = inquirySource;
	}
	public String getTypeOfInquiry() {
		return typeOfInquiry;
	}
	public void setTypeOfInquiry(String typeOfInquiry) {
		this.typeOfInquiry = typeOfInquiry;
	}
	public String getSearchUserId() {
		return searchUserId;
	}
	public void setSearchUserId(String searchUserId) {
		this.searchUserId = searchUserId;
	}
	public String getInquiryIdType() {
		return inquiryIdType;
	}
	public void setInquiryIdType(String inquiryIdType) {
		this.inquiryIdType = inquiryIdType;
	}
	public String getSeachResult() {
		return seachResult;
	}
	public void setSeachResult(String seachResult) {
		this.seachResult = seachResult;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getConsentStatusName() {
		return consentStatusName;
	}
	public void setConsentStatusName(String consentStatusName) {
		this.consentStatusName = consentStatusName;
	}
	public String getConsentStatusId() {
		return consentStatusId;
	}
	public void setConsentStatusId(String consentStatusId) {
		this.consentStatusId = consentStatusId;
	}
}
