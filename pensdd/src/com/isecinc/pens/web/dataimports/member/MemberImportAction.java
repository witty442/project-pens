package com.isecinc.pens.web.dataimports.member;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.Paginator;
import util.UploadXLSUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.DeliveryGroup;
import com.isecinc.pens.bean.District;
import com.isecinc.pens.bean.MapProvince;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberProduct;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.Province;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dataimports.bean.IAddressBean;
import com.isecinc.pens.dataimports.bean.IContactBean;
import com.isecinc.pens.dataimports.bean.ICustomerBean;
import com.isecinc.pens.dataimports.bean.IMemberBean;
import com.isecinc.pens.dataimports.bean.IMemberProductBean;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.IAddress;
import com.isecinc.pens.model.IContact;
import com.isecinc.pens.model.ICustomer;
import com.isecinc.pens.model.IMemberProduct;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MContact;
import com.isecinc.pens.model.MDeliveryGroup;
import com.isecinc.pens.model.MDistrict;
import com.isecinc.pens.model.MMapProvince;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProvince;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.order.OrderProcess;

public class MemberImportAction extends I_Action {

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
	// TODO Auto-generated method stub

	}

	public ActionForward memberUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String memberCode = null;
		String errorDesc = null;
		String oldMemberCode = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
		// int errorCount = 0;
		String forward = "view";
		MemberImportForm actionForm = (MemberImportForm) form;

		try {
			request.getSession().removeAttribute("PAGINATOR");
			User user = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			FormFile dataFile = actionForm.getMemberFile();
			if (dataFile != null) {
				logger.debug("newImport: " + actionForm.getNewImport());
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 3; // row of begin data
				int maxRowNo = 0; // row of end data is optional, default is 0 depend on keyColumn
				int maxColumnNo = 60; // max column of data per row
				int keyColumn = 3; // must be not null column

				UploadXLSUtil uploadXLSUtil = new UploadXLSUtil();
				ArrayList datas = uploadXLSUtil.loadXls(dataFile, sheetNo, rowNo, maxRowNo, maxColumnNo, keyColumn);

				if (datas != null && datas.size() > 0) {
					allCount = datas.size();
					ArrayList<IMemberBean> members = new ArrayList<IMemberBean>();
					for (Object rows : (ArrayList) datas) {
						if (rows != null && ((ArrayList) rows).get(keyColumn) != null) {
							ICustomerBean customer = new ICustomerBean();
							IAddressBean address = new IAddressBean();
							IContactBean contact = new IContactBean();
							ArrayList<IMemberProductBean> productList = new ArrayList<IMemberProductBean>();
							IMemberProductBean product = null;
							int columnNo = 0;
							for (Object column : (ArrayList) rows) {
								if (column != null && !column.toString().startsWith("java.lang.Object")) {
									logger.debug(columnNo + " : " + column);
									switch (columnNo) {
									case 0:
										// วันที่สมัคร
										break;
									case 1:
										// เดือนที่สมัคร
										break;
									case 2:
										// ปีที่สมัคร
										break;
									case 3:
										// รหัสสมาชิก
										// customer.setId(uploadXLSUtil.getIntValue(column));
										customer.setCode(uploadXLSUtil.getIntFormat(column));
										customer.setCustomerType(user.getCustomerType().getKey());
										customer.setIsVip("N");
										customer.setPartyType("O");
										customer.setVatCode("7");
										memberCode = customer.getCode();
										break;
									case 4:
										// ชื่อ
										customer.setName(column.toString());
										break;
									case 5:
										// นามสกุล
										customer.setName2(column.toString());
										break;
									case 7:
										// เบอร์มือถือ 1
										contact.setMobile(column.toString());
										break;
									case 8:
										// เบอร์มือถือ2
										contact.setMobile2(column.toString());
										break;
									case 9:
										// เบอร์โทร 1
										contact.setPhone(column.toString());
										break;
									case 10:
										// เบอร์โทร 1(ต่อ)
										contact
												.setPhone(contact.getPhone() + " #"
														+ uploadXLSUtil.getIntFormat(column));
										break;
									case 11:
										// เบอร์โทร 2
										contact.setPhone2(column.toString());
										break;
									case 12:
										// เบอร์โทร 2(ต่อ)
										contact.setPhone2(contact.getPhone2() + " #"
												+ uploadXLSUtil.getIntFormat(column));
										break;
									case 13:
										// เบอร์ Fax ของผู้ติดต่อ
										contact.setFax(column.toString());
										break;
									case 14:
										// อีเมล์
										customer.setEmail(column.toString());
										break;
									case 15:
										// ผุ้ติดต่อ
										contact.setContactTo(column.toString());
										break;
									case 16:
										// ตำแหน่ง/ความสัมพันธ์
										contact.setRelation(column.toString());
										break;
									case 17:
										// เลขที่บัตรประชาชน
										customer.setPersonIDNo(column.toString());
										break;
									case 18:
										// เลขที่บัตรเครดิต
										break;
									case 19:
										// อายุ
										break;
									case 20:
										// เพศ
										break;
									case 25:
										// เงินสด
										String paymentMethod = column.toString();
										String paymentMethodKey = "";
										if (paymentMethod.indexOf("โอน") != -1 || paymentMethod.indexOf("เงิน") != -1
												|| paymentMethod.indexOf("สด") != -1) {
											paymentMethodKey = "CS";
										}
										if (paymentMethod.indexOf("บัตร") != -1 || paymentMethod.indexOf("เค") != -1
												|| paymentMethod.indexOf("ดิต") != -1) {
											paymentMethodKey = "CR";
										}
										if (paymentMethod.indexOf("เช็ค") != -1) {
											paymentMethodKey = "CH";
										}
										customer.setPaymentMethod(paymentMethodKey);
										break;
									case 26:
										// เครดิต
										break;
									case 27:
										// ธนาคาร กรณีเป็นบัตรเครดิต
										break;
									case 28:
										// เทอมการจ่ายเงิน
										String paymentTerm = column.toString().toLowerCase();
										String paymentTermKey = "";
										if (paymentTerm.indexOf("month") != -1) {
											paymentTermKey = "30";
										}
										if (paymentTerm.indexOf("cash") != -1 || paymentTerm.indexOf("pay") != -1) {
											paymentTermKey = "IM";
										}
										customer.setPaymentTerm(paymentTermKey);
										break;
									case 29:
										// กลุ่มส่ง
										break;
									case 30:
										// ที่อยู่
										address.setLine1(column.toString().replace("\'", "\\\'").replace("\"", "\\\"")
												.replace("\\", "\\\\"));
										break;
									case 31:
										// ที่อยู่ (ต่อ)
										address.setLine2(column.toString().replace("\'", "\\\'").replace("\"", "\\\"")
												.replace("\\", "\\\\"));
										break;
									case 32:
										// แขวง/ตำบล
										address.setLine3(column.toString().replace("\'", "\\\'").replace("\"", "\\\"")
												.replace("\\", "\\\\"));
										break;
									case 33:
										// เขต/อำเภอ (เขตการขาย)
										address.setLine4(column.toString().replace("\'", "\\\'").replace("\"", "\\\"")
												.replace("\\", "\\\\"));
										break;
									case 34:
										// จังหวัด
										whereCause = new StringBuffer();
										whereCause.append(" AND NAME LIKE '%");
										whereCause.append(column.toString().trim().replace("\'", "\\\'").replace("\"",
												"\\\"").replace("ฯ", ""));
										whereCause.append("%'");
										Province[] provinces = new MProvince().search(whereCause.toString());
										if (provinces != null && provinces.length > 0) {
											logger.debug("provinces.length: " + provinces.length);
											address.setProvince(provinces[0]);

											// รายละเอียด เขต/อำเภอ (เขตการขาย)
											if (address.getProvince() != null && address.getLine4() != null
													&& !address.getLine4().equals("")) {
												whereCause = new StringBuffer();
												whereCause
														.append(" AND PROVINCE_ID = " + address.getProvince().getId());
												whereCause.append(" AND NAME LIKE '%");
												whereCause.append(address.getLine4().trim().replace("\'", "\\\'")
														.replace("\"", "\\\"").replace("ฯ", ""));
												whereCause.append("%'");
												District[] districts = new MDistrict().search(whereCause.toString());
												if (districts != null && districts.length > 0) {
													logger.debug("districts.length: " + districts.length);
													address.setDistrict(districts[0]);

													// delivery group
													MDeliveryGroup mDeliveryGroup = new MDeliveryGroup();
													DeliveryGroup deliveryGroup = mDeliveryGroup.find(address
															.getDistrict().getId()
															+ "");
													if (deliveryGroup != null) {
														customer.setDeliveryGroup(mDeliveryGroup.getDeliveryGroupId(
																conn, deliveryGroup.getReferenceId()));
													}
												}
											}

											// territory
											MMapProvince mMapProvince = new MMapProvince();
											MapProvince mapProvince = mMapProvince.find(address.getProvince().getId()
													+ "");
											if (mapProvince != null) {
												customer.setTerritory(mMapProvince.getTerritoryID(conn, mapProvince
														.getReferenceId()));
											}
										}
										address.setProvinceName(column.toString());
										break;
									case 35:
										// รหัสไปรษณีย์
										address.setPostalCode(uploadXLSUtil.getIntFormat(column));
										break;
									case 36:
										// Bill to/Ship to
										if (column.toString().indexOf("to") != -1) {
											address.setBillToShipTo(column.toString().toLowerCase().replace(" ", "")
													.replace("billto", "B").replace("shipto", "S"));
										}
										break;
									case 37:
										// วันที่สมัคร
										if (column.toString().indexOf("00:00:00") != -1) {
											customer.setRegisterDate(DateToolsUtil.convertToString((Date) column));
										} else {
											// atiz.b
											if (column.toString().trim().split("/").length == 3) {
												String tempDate = "";
												tempDate += new DecimalFormat("00").format(Integer.parseInt(column
														.toString().trim().split("/")[0]));
												tempDate += "/";
												tempDate += new DecimalFormat("00").format(Integer.parseInt(column
														.toString().trim().split("/")[1]));
												tempDate += "/";
												tempDate += new DecimalFormat("0000").format(Integer.parseInt(column
														.toString().trim().split("/")[2]));
												customer.setRegisterDate(tempDate);
											}
										}
										break;
									case 38:
										// จำนวนเงินสิ้นสุดปี xx
										break;
									case 39:
										// ประเภทสมาชิก
										customer.setMemberType(column.toString().replace("สมาชิก ", "").replace(
												" เดือน", ""));
										if (!customer.getMemberType().equals("")) {
											customer.setAgeMonth(Integer.parseInt(customer.getMemberType()));

											if (customer.getRegisterDate() != null) {
												Calendar calendar = Calendar.getInstance();
												String[] dates = customer.getRegisterDate().split("/");
												logger.debug("date: " + dates[0] + ", month: " + dates[1] + ", year: "
														+ dates[2]);
												calendar.set(Integer.parseInt(dates[2]),
														Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[0]));
												calendar.add(Calendar.MONTH, customer.getAgeMonth() + 1);
												logger.debug("date after " + customer.getAgeMonth() + " months : "
														+ (calendar.get(Calendar.DATE) + 1) + "/"
														+ calendar.get(Calendar.MONTH) + "/"
														+ calendar.get(Calendar.YEAR));
												customer.setExpiredDate((calendar.get(Calendar.DATE) + 1) + "/"
														+ calendar.get(Calendar.MONTH) + "/"
														+ calendar.get(Calendar.YEAR));
											}
										}
										break;
									case 40:
										// รอบการส่งสินค้าทุกๆ
										customer.setRoundTrip(column.toString().replace(" ", "").replace("วัน", "")
												.replace("เดือน", "30").replace("ไม่ระบุ", ""));
										break;
									case 41:
										// รสชาด (สตรอเบอร์รี่)
										product = new IMemberProductBean();

										whereCause = new StringBuffer();
										whereCause.append(" AND CODE = '302010' ");
										whereCause.append(" AND ISACTIVE = 'Y'");
										Product[] product1s = new MProduct().search(whereCause.toString());
										if (product1s != null && product1s.length > 0) {
											logger.debug("product1s.length: " + product1s.length);
											product.setProduct(product1s[0]);
											product.setOrderQty(uploadXLSUtil.getIntValue(column));
											if (product.getOrderQty() != 0) productList.add(product);
										}
										break;
									case 42:
										// รสชาด (ส้ม)
										product = new IMemberProductBean();

										whereCause = new StringBuffer();
										whereCause.append(" AND CODE = '302009' ");
										whereCause.append(" AND ISACTIVE = 'Y'");
										Product[] product2s = new MProduct().search(whereCause.toString());
										if (product2s != null && product2s.length > 0) {
											logger.debug("product2s.length: " + product2s.length);
											product.setProduct(product2s[0]);
											product.setOrderQty(uploadXLSUtil.getIntValue(column));
											if (product.getOrderQty() != 0) productList.add(product);
										}
										break;
									case 43:
										// รสชาด (มิกซ์)
										product = new IMemberProductBean();

										whereCause = new StringBuffer();
										whereCause.append(" AND CODE = '302011' ");
										whereCause.append(" AND NAME LIKE '%มิกซ์%'");
										whereCause.append(" AND ISACTIVE = 'Y'");
										Product[] product3s = new MProduct().search(whereCause.toString());
										if (product3s != null && product3s.length > 0) {
											logger.debug("product3s.length: " + product3s.length);
											product.setProduct(product3s[0]);
											product.setOrderQty(uploadXLSUtil.getIntValue(column));
											if (product.getOrderQty() != 0) productList.add(product);
										}
										break;
									case 44:
										// รวม
										customer.setOrderAmountPeriod(uploadXLSUtil.getIntValue(column));
										break;
									case 45:
										// วันจัดส่ง
										String shippingDate = column.toString();
										String shippingDateKey = "";
										if (shippingDate.indexOf("อา") != -1) {
											shippingDateKey = "Sun";
										}
										if (shippingDate.indexOf("จัน") != -1) {
											shippingDateKey = "Mon";
										}
										if (shippingDate.indexOf("อัง") != -1) {
											shippingDateKey = "Tue";
										}
										if (shippingDate.indexOf("พุธ") != -1) {
											shippingDateKey = "Wed";
										}
										if (shippingDate.indexOf("พฤ") != -1) {
											shippingDateKey = "Thu";
										}
										if (shippingDate.indexOf("ศุก") != -1) {
											shippingDateKey = "Fri";
										}
										if (shippingDate.indexOf("เสา") != -1) {
											shippingDateKey = "Sat";
										}
										customer.setShippingDate(shippingDateKey);
										break;
									case 46:
										// เวลาจัดส่ง
										String time = column.toString().replace(" ", "").replace("น.", "").replace(".",
												":");
										if (time.indexOf("-") != -1) {
											String[] times = time.split("-");
											customer.setShippingTime(times[0]);
											customer.setShippingTimeTo(times[1]);
										}
										break;
									case 47:
										// วันเกิดสมาชิก
										if (column.toString().indexOf("00:00:00") != -1) {
											customer.setBirthDay(DateToolsUtil.convertToString((Date) column));
										}
										break;
									case 48:
										// อาชีพ
										customer.setOccupation(column.toString());
										break;
									case 49:
										// รายได้ต่อเดือน
										customer.setMonthlyIncome(uploadXLSUtil.getDecimalValue(column));
										break;
									case 50:
										// อายุสมาชิก
										break;
									case 51:
										// ระดับคอเลสเตอรอล
										customer.setChrolesterol(uploadXLSUtil.getDecimalValue(column));
										break;
									case 52:
										// ระดับสมาชิก
										customer.setMemberLevel(column.toString().equalsIgnoreCase("Regular") ? "R"
												: null);
										break;
									case 53:
										// รหัสสมาชิก (ผู้แนะนำ)
										customer.setRecommendedId(Integer.parseInt(column.toString().replace(" ", "")
												.replace("HB", "").replace("HD", "").replace(".0", "")));
										customer.setRecommendedType("O");
										if (customer.getRecommendedId() != 0) {
											customer.setRecommendedType("M");
										}
										break;
									case 54:
										// ชื่อ (ผู้แนะนำ)
										customer.setRecommendedBy(column.toString());
										break;
									case 55:
										// สถานะสมาชิก
										break;
									case 56:
										// เหตุผลในการยกเลิกสมาชิก
										customer.setCancelReason(column.toString());
										break;
									case 57:
										// จำนวนงวดส่งที่เหลือ
										customer.setOrderLineRemain(uploadXLSUtil.getIntValue(column));
										break;
									case 58:
										// วันที่เริ่ม ปี xxxx
										if (column.toString().indexOf("00:00:00") != -1) {
											customer.setStartNextYear(DateToolsUtil.convertToString((Date) column));
										}
										break;
									case 59:
										// จำนวนเงินรับล่วงหน้า ปี xxxx
										customer.setPrepaidNextYear(uploadXLSUtil.getDecimalValue(column));
										break;
									default:
										break;
									}
								}

								columnNo++;
							}

							errorDesc = "";

							Locale locale = new Locale("th", "TH");

							// insert/update
							if (oldMemberCode != null && oldMemberCode.equals(customer.getCode())) {
								errorDesc = "รหัสสมาชิก " + customer.getCode() + " ซ้ำ";
								logger.debug(errorDesc);
								// errorCount++;
							} else if (address.getLine1() != null && address.getLine1().length() > 100) {
								errorDesc = SystemMessages.getCaption("Line1MaxLength", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (address.getProvince().getId() == 0) {
								errorDesc = SystemMessages.getCaption("ProvinceNotFound", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (address.getDistrict().getId() == 0) {
								errorDesc = SystemMessages.getCaption("DistrictNotFound", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (address.getPostalCode() != null && address.getPostalCode().length() > 5) {
								errorDesc = SystemMessages.getCaption("PostCodeMaxLength", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (customer.getMemberType() == null || customer.getMemberType().equals("")) {
								errorDesc = SystemMessages.getCaption("MemberTypeNotFound", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (customer.getRegisterDate() == null || customer.getRegisterDate().equals("")) {
								errorDesc = SystemMessages.getCaption("RegisterDateNotFound", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else if (customer.getExpiredDate() == null || customer.getExpiredDate().equals("")) {
								errorDesc = SystemMessages.getCaption("ExpireDateNotFound", locale);
								logger.debug(errorDesc);
								// errorCount++;
							} else {
								// check customer
								customer.setImported("N");
								whereCause = new StringBuffer();
								whereCause.append(" AND CODE = " + customer.getCode());
								ICustomerBean[] iCustomer = new ICustomer().search(whereCause.toString());
								if (iCustomer != null && iCustomer.length > 0) {
									// update
									customer.setId(iCustomer[0].getId());
									if (iCustomer[0].getImported().equals("N")) {
										new ICustomer().save(customer, user.getId(), conn);
									} else {
										if (actionForm.getNewImport() != null && actionForm.getNewImport().equals("Y")) {
											new ICustomer().save(customer, user.getId(), conn);
										}
									}
								} else {
									// insert
									customer.setId(SequenceProcess.getNextValue("i_customer"));
									new ICustomer().save(customer, user.getId(), conn);
								}

								// check address
								if (address.getBillToShipTo() != null && !address.getBillToShipTo().equals("")) {
									String[] billToShipTos = address.getBillToShipTo().split("/");
									for (int i = 0; i < billToShipTos.length; i++) {
										address.setImported("N");
										address.setCustomerId(customer.getId());
										whereCause = new StringBuffer();
										whereCause.append(" AND ICUSTOMER_ID = " + customer.getId());
										whereCause.append(" AND PURPOSE = '" + billToShipTos[i] + "'");
										IAddressBean[] iAddress = new IAddress().search(whereCause.toString());
										if (iAddress != null && iAddress.length > 0) {
											// update
											address.setId(iAddress[0].getId());
											address.setPurpose(billToShipTos[i]);
											if (iAddress[0].getImported().equals("N")) {
												new IAddress().save(address, user.getId(), conn);
											} else {
												if (actionForm.getNewImport() != null
														&& actionForm.getNewImport().equals("Y")) {
													new IAddress().save(address, user.getId(), conn);
												}
											}
										} else {
											// insert
											address.setId(SequenceProcess.getNextValue("i_ddress"));
											address.setPurpose(billToShipTos[i]);
											new IAddress().save(address, user.getId(), conn);
										}
									}
								} else {
									address.setImported("N");
									address.setCustomerId(customer.getId());
									whereCause = new StringBuffer();
									whereCause.append(" AND ICUSTOMER_ID = " + customer.getId());
									IAddressBean[] iAddress = new IAddress().search(whereCause.toString());
									if (iAddress != null && iAddress.length > 0) {
										// update
										address.setId(iAddress[0].getId());
										if (iAddress[0].getImported().equals("N")) {
											new IAddress().save(address, user.getId(), conn);
										} else {
											if (actionForm.getNewImport() != null
													&& actionForm.getNewImport().equals("Y")) {
												new IAddress().save(address, user.getId(), conn);
											}
										}
									} else {
										// insert
										address.setId(SequenceProcess.getNextValue("i_ddress"));
										new IAddress().save(address, user.getId(), conn);
									}
								}

								// check contact
								contact.setImported("N");
								contact.setCustomerId(customer.getId());
								whereCause = new StringBuffer();
								whereCause.append(" AND ICUSTOMER_ID = " + customer.getId());
								IContactBean[] iContact = new IContact().search(whereCause.toString());
								if (iContact != null && iContact.length > 0) {
									// update
									contact.setId(iContact[0].getId());
									if (iContact[0].getImported().equals("N")) {
										new IContact().save(contact, user.getId(), conn);
									} else {
										if (actionForm.getNewImport() != null && actionForm.getNewImport().equals("Y")) {
											new IContact().save(contact, user.getId(), conn);
										}
									}
								} else {
									// insert
									contact.setId(SequenceProcess.getNextValue("i_contact"));
									new IContact().save(contact, user.getId(), conn);
								}

								// check product
								// delete all
								new IMemberProduct().deleteAllLines(customer.getId(), conn);
								if (productList != null && productList.size() > 0) {
									for (IMemberProductBean memberProduct : productList) {
										memberProduct.setImported("N");
										memberProduct.setCustomerId(customer.getId());
										whereCause = new StringBuffer();
										whereCause.append(" AND ICUSTOMER_ID = " + customer.getId());
										whereCause.append(" AND PRODUCT_ID = " + memberProduct.getProduct().getId());
										whereCause.append(" AND UOM_ID = '"
												+ memberProduct.getProduct().getUom().getId() + "'");
										IMemberProductBean[] iMemberProduct = new IMemberProduct().search(whereCause
												.toString());
										if (iMemberProduct != null && iMemberProduct.length > 0) {
											// update
											memberProduct.setId(iMemberProduct[0].getId());
											if (iMemberProduct[0].getImported().equals("N")) {
												new IMemberProduct().save(memberProduct, user.getId(), conn);
											} else {
												if (actionForm.getNewImport() != null
														&& actionForm.getNewImport().equals("Y")) {
													new IMemberProduct().save(memberProduct, user.getId(), conn);
												}
											}
										} else {
											// insert
											memberProduct.setId(SequenceProcess.getNextValue("i_member_product"));
											new IMemberProduct().save(memberProduct, user.getId(), conn);
										}
									}
								}

								oldMemberCode = customer.getCode();
								successCount++;
							}

							// send to view with status
							IMemberBean member = new IMemberBean();
							member.setId(++id);
							member.setCustomer(customer);
							member.setAddress(address);
							member.setContact(contact);
							member.setProductList(productList);
							member.setErrorDesc(errorDesc);
							members.add(member);

						}
					}

					datas.clear();

					// start paginator
					IMemberBean.setMemberList(members);
					IMemberBean.setMemberCount(members.size());
					Paginator page = new Paginator(IMemberBean.class, IMemberBean.GET_PAGING_MEMBER_LIST,
							IMemberBean.GET_PAGING_MEMBER_COUNT, new IMemberBean());
					request.getSession().setAttribute("PAGINATOR", page);
					// end paginator

				}
			}

			actionForm.setNextYear(Integer.parseInt(DateToolsUtil.getCurrentDateTime("yyyy")) + 1);

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc()
					+ " ข้อมูล " + allCount + " รายการ, สำเร็จ " + successCount + " รายการ, ไม่สำเร็จ "
					+ (allCount - successCount) + " รายการ");

			conn.commit();
		} catch (Exception e) {
			forward = "clearform";
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ " ข้อมูล " + allCount + " รายการ, พบข้อมูลผิดปกติของสมาชิกเลขที่ " + memberCode + " "
					+ e.toString());

			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward(forward);
	}

	public ActionForward memberUploadEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		MemberImportForm actionForm = (MemberImportForm) form;

		try {
			User user = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			logger.debug("currentId: " + actionForm.getCurrentId());
			logger.debug("currentOrderLineRemain: " + actionForm.getCurrentOrderLineRemain());
			logger.debug("currentStartNextYear: " + actionForm.getCurrentStartNextYear());

			Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");

			for (IMemberBean member : (List<IMemberBean>) ((List) page.getPage())) {
				if (member.getId() == actionForm.getCurrentId()) {
					// update
					logger.debug("update id: " + member.getId());

					member.getCustomer().setOrderLineRemain(
							actionForm.getCurrentOrderLineRemain() == null
									|| actionForm.getCurrentOrderLineRemain().equals("") ? 0 : Integer
									.parseInt(actionForm.getCurrentOrderLineRemain()));
					member.getCustomer().setStartNextYear(actionForm.getCurrentStartNextYear());
					new ICustomer().save(member.getCustomer(), user.getId(), conn);
				}
			}

			request.getSession().setAttribute("PAGINATOR", page);

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			conn.commit();
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc());

			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("view");
	}

	public ActionForward memberImport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		String forward = "clearform";
		MemberImportForm actionForm = (MemberImportForm) form;

		try {
			User user = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			if (actionForm.getCheckAll() != null && actionForm.getCheckAll().equals("Y")) {
				// import all valid data to m_member_xxx table
				logger.debug("checkAll: " + actionForm.getCheckAll());

				Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
				page.first();

				while (true) {
					for (IMemberBean member : (List<IMemberBean>) ((List) page.getPage())) {
						if (member.getErrorDesc() == null || member.getErrorDesc().equals("")) {
							// import
							logger.debug("import id: " + member.getId());

							save(member, user, conn);
						}
					}

					// check has next page
					if (page.isHasNext()) {
						page.next();
					} else {
						break;
					}
				}
			} else {
				// manual import valid data to m_member_xxx table by id
				logger.debug("selected ids: " + actionForm.getTempSelectedIds());

				String[] ids = actionForm.getTempSelectedIds().split(",");

				for (int i = 0; i < ids.length; i++) {
					String id = ids[i].replace("[", "").replace("]", "");
					// logger.debug("ids[" + i + "]: " + id);

					Paginator page = (Paginator) request.getSession().getAttribute("PAGINATOR");
					page.first();

					boolean isFound = false;
					while (true) {
						for (IMemberBean member : (List<IMemberBean>) ((List) page.getPage())) {
							// logger.debug("compare member.getId(): " + member.getId() + " with " + id);
							if (member.getId() == Integer.parseInt(id)) {
								// import
								logger.debug("import id: " + member.getId());

								save(member, user, conn);

								isFound = true;
								break;
							}
						}
						if (isFound) {
							break;
						}

						// check has next page
						if (page.isHasNext()) {
							page.next();
						} else {
							break;
						}
					}
				}
			}

			request.getSession().removeAttribute("PAGINATOR");
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			conn.commit();
		} catch (Exception e) {
			forward = "view";
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc() + " "
					+ e.toString());

			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward(forward);
	}

	private boolean save(IMemberBean member, User user, Connection conn) throws Exception {

		int nextId = 0;

		StringBuffer whereCause = null;
		Member mMember = null;
		Address mAddress = null;
		Contact mContact = null;
		MemberProduct mMemberProduct = null;
		try {
			// customer
			whereCause = new StringBuffer();
			whereCause.append(" AND CODE = '" + member.getCustomer().getCode() + "'");
			Member[] members = new MMember().search(whereCause.toString());
			if (members != null && members.length > 0) {
				// update
				logger.debug("update member code: " + member.getCustomer().getCode());

				mMember = members[0];
				mMember = (Member) UploadXLSUtil.cloneAttributes(member.getCustomer(), mMember);
				mMember.setId(members[0].getId());
				mMember.setCode(members[0].getCode());
				mMember.setName(member.getCustomer().getName());
				mMember.setName2(member.getCustomer().getName2());

				mMember.setCustomerType(member.getCustomer().getCustomerType());
				mMember.setVatCode(member.getCustomer().getVatCode());
				mMember.setTerritory(member.getCustomer().getTerritory());
				mMember.setBusinessType(member.getCustomer().getBusinessType());
				mMember.setMemberType(member.getCustomer().getMemberType());
				mMember.setMemberLevel(member.getCustomer().getMemberLevel());
				mMember.setIsvip(member.getCustomer().getIsVip());
				mMember.setPersonIDNo(member.getCustomer().getPersonIDNo());
				mMember.setEmail(member.getCustomer().getEmail());
				mMember.setPaymentTerm(member.getCustomer().getPaymentTerm());
				mMember.setPaymentMethod(member.getCustomer().getPaymentMethod());
				mMember.setOccupation(member.getCustomer().getOccupation());
				mMember.setBirthDay(ConvertNullUtil.convertToString(member.getCustomer().getBirthDay()));
				mMember.setRegisterDate(ConvertNullUtil.convertToString(member.getCustomer().getRegisterDate()));
				mMember.setExpiredDate(ConvertNullUtil.convertToString(member.getCustomer().getExpiredDate()));
				mMember.setMonthlyIncome(member.getCustomer().getMonthlyIncome());
				mMember.setChrolesterol(member.getCustomer().getChrolesterol());
				mMember.setRecommendedBy(member.getCustomer().getRecommendedBy());
				mMember.setRecommendedId(member.getCustomer().getRecommendedId());
				mMember.setRecommendedType(member.getCustomer().getRecommendedType());
				mMember.setCreditcardExpired(ConvertNullUtil.convertToString(member.getCustomer()
						.getCreditcardExpired()));
				mMember.setOrderAmountPeriod(member.getCustomer().getOrderAmountPeriod());
				mMember.setShippingDate(ConvertNullUtil.convertToString(member.getCustomer().getShippingDate()));
				mMember.setShippingTime(member.getCustomer().getShippingTime());
				mMember.setShippingTimeTo(member.getCustomer().getShippingTimeTo());
				mMember.setShippingMethod(member.getCustomer().getShippingMethod());
				mMember.setRoundTrip(member.getCustomer().getRoundTrip());
				mMember.setAgeMonth(member.getCustomer().getAgeMonth());
				mMember.setPartyType(member.getCustomer().getPartyType());
				mMember.setDeliveryGroup(member.getCustomer().getDeliveryGroup());
				mMember.setCancelReason(member.getCustomer().getCancelReason());

				mMember.setIsActive(members[0].getIsActive());
				new MMember().save(mMember, user.getId(), conn);
			} else {
				// insert
				logger.debug("insert member code: " + member.getCustomer().getCode());

				mMember = new Member();
				mMember = (Member) UploadXLSUtil.cloneAttributes(member.getCustomer(), mMember);
				mMember.setId(0);
				mMember.setCode(member.getCustomer().getCode());
				mMember.setName(member.getCustomer().getName());
				mMember.setName2(member.getCustomer().getName2());

				mMember.setCustomerType(member.getCustomer().getCustomerType());
				mMember.setVatCode(member.getCustomer().getVatCode());
				mMember.setTerritory(member.getCustomer().getTerritory());
				mMember.setBusinessType(member.getCustomer().getBusinessType());
				mMember.setMemberType(member.getCustomer().getMemberType());
				mMember.setMemberLevel(member.getCustomer().getMemberLevel());
				mMember.setIsvip(member.getCustomer().getIsVip());
				mMember.setPersonIDNo(member.getCustomer().getPersonIDNo());
				mMember.setEmail(member.getCustomer().getEmail());
				mMember.setPaymentTerm(member.getCustomer().getPaymentTerm());
				mMember.setPaymentMethod(member.getCustomer().getPaymentMethod());
				mMember.setOccupation(member.getCustomer().getOccupation());
				mMember.setBirthDay(ConvertNullUtil.convertToString(member.getCustomer().getBirthDay()));
				mMember.setRegisterDate(ConvertNullUtil.convertToString(member.getCustomer().getRegisterDate()));
				mMember.setExpiredDate(ConvertNullUtil.convertToString(member.getCustomer().getExpiredDate()));
				mMember.setMonthlyIncome(member.getCustomer().getMonthlyIncome());
				mMember.setChrolesterol(member.getCustomer().getChrolesterol());
				mMember.setRecommendedBy(member.getCustomer().getRecommendedBy());
				mMember.setRecommendedId(member.getCustomer().getRecommendedId());
				mMember.setRecommendedType(member.getCustomer().getRecommendedType());
				mMember.setCreditcardExpired(ConvertNullUtil.convertToString(member.getCustomer()
						.getCreditcardExpired()));
				mMember.setOrderAmountPeriod(member.getCustomer().getOrderAmountPeriod());
				mMember.setShippingDate(ConvertNullUtil.convertToString(member.getCustomer().getShippingDate()));
				mMember.setShippingTime(member.getCustomer().getShippingTime());
				mMember.setShippingTimeTo(member.getCustomer().getShippingTimeTo());
				mMember.setShippingMethod(member.getCustomer().getShippingMethod());
				mMember.setRoundTrip(member.getCustomer().getRoundTrip());
				mMember.setAgeMonth(member.getCustomer().getAgeMonth());
				mMember.setPartyType(member.getCustomer().getPartyType());
				mMember.setDeliveryGroup(member.getCustomer().getDeliveryGroup());
				mMember.setCancelReason(member.getCustomer().getCancelReason());

				mMember.setIsActive("Y");
				new MMember().save(mMember, user.getId(), conn);
			}

			// address
			int billToAddressId = 0;
			int shipToAddressId = 0;
			if (member.getAddress().getBillToShipTo() != null && !member.getAddress().getBillToShipTo().equals("")) {
				String[] billToShipTos = member.getAddress().getBillToShipTo().split("/");
				for (int i = 0; i < billToShipTos.length; i++) {
					whereCause = new StringBuffer();
					whereCause.append(" AND CUSTOMER_ID = '" + mMember.getId() + "'");
					whereCause.append(" AND PURPOSE = '" + billToShipTos[i] + "'");
					Address[] addresss = new MAddress().search(whereCause.toString());
					if (addresss != null && addresss.length > 0) {
						// update
						mAddress = addresss[0];
						mAddress = (Address) UploadXLSUtil.cloneAttributes(member.getAddress(), mAddress);
						mAddress.setId(addresss[0].getId());
						mAddress.setCustomerId(addresss[0].getCustomerId());
						mAddress.setLine1(member.getAddress().getLine1());
						mAddress.setLine2(member.getAddress().getLine2());
						mAddress.setLine3(member.getAddress().getLine3());
						mAddress.setLine4(member.getAddress().getLine4());
						mAddress.setDistrict(member.getAddress().getDistrict());
						mAddress.setProvince(member.getAddress().getProvince());
						mAddress.setPostalCode(member.getAddress().getPostalCode());
						mAddress.setPurpose(billToShipTos[i]);
						mAddress.setIsActive(addresss[0].getIsActive());
						new MAddress().save(mAddress, user.getId(), conn);
					} else {
						// insert
						mAddress = new Address();
						mAddress = (Address) UploadXLSUtil.cloneAttributes(member.getAddress(), mAddress);
						mAddress.setId(0);
						mAddress.setCustomerId(mMember.getId());
						mAddress.setLine1(member.getAddress().getLine1());
						mAddress.setLine2(member.getAddress().getLine2());
						mAddress.setLine3(member.getAddress().getLine3());
						mAddress.setLine4(member.getAddress().getLine4());
						mAddress.setDistrict(member.getAddress().getDistrict());
						mAddress.setProvince(member.getAddress().getProvince());
						mAddress.setPostalCode(member.getAddress().getPostalCode());
						mAddress.setPurpose(billToShipTos[i]);
						mAddress.setIsActive("Y");
						new MAddress().save(mAddress, user.getId(), conn);
					}

					// for order
					if (billToShipTos[i].equals("B")) {
						billToAddressId = mAddress.getId();
					}
					if (billToShipTos[i].equals("S")) {
						shipToAddressId = mAddress.getId();
					}
				}
			} else {
				whereCause = new StringBuffer();
				whereCause.append(" AND CUSTOMER_ID = '" + mMember.getId() + "'");
				Address[] addresss = new MAddress().search(whereCause.toString());
				if (addresss != null && addresss.length > 0) {
					// update
					mAddress = addresss[0];
					mAddress = (Address) UploadXLSUtil.cloneAttributes(member.getAddress(), mAddress);
					mAddress.setId(addresss[0].getId());
					mAddress.setCustomerId(addresss[0].getCustomerId());
					mAddress.setLine1(member.getAddress().getLine1());
					mAddress.setLine2(member.getAddress().getLine2());
					mAddress.setLine3(member.getAddress().getLine3());
					mAddress.setLine4(member.getAddress().getLine4());
					mAddress.setDistrict(member.getAddress().getDistrict());
					mAddress.setProvince(member.getAddress().getProvince());
					mAddress.setPostalCode(member.getAddress().getPostalCode());
					mAddress.setPurpose(member.getAddress().getPurpose());
					mAddress.setIsActive(addresss[0].getIsActive());
					new MAddress().save(mAddress, user.getId(), conn);
				} else {
					// insert
					mAddress = new Address();
					mAddress = (Address) UploadXLSUtil.cloneAttributes(member.getAddress(), mAddress);
					mAddress.setId(0);
					mAddress.setCustomerId(mMember.getId());
					mAddress.setLine1(member.getAddress().getLine1());
					mAddress.setLine2(member.getAddress().getLine2());
					mAddress.setLine3(member.getAddress().getLine3());
					mAddress.setLine4(member.getAddress().getLine4());
					mAddress.setDistrict(member.getAddress().getDistrict());
					mAddress.setProvince(member.getAddress().getProvince());
					mAddress.setPostalCode(member.getAddress().getPostalCode());
					mAddress.setPurpose(member.getAddress().getPurpose());
					mAddress.setIsActive("Y");
					new MAddress().save(mAddress, user.getId(), conn);
				}
			}

			// contact
			whereCause = new StringBuffer();
			whereCause.append(" AND CUSTOMER_ID = '" + mMember.getId() + "'");
			Contact[] contacts = new MContact().search(whereCause.toString());
			if (contacts != null && contacts.length > 0) {
				// update
				mContact = contacts[0];
				mContact = (Contact) UploadXLSUtil.cloneAttributes(member.getContact(), mContact);
				mContact.setId(contacts[0].getId());
				mContact.setCustomerId(contacts[0].getCustomerId());
				mContact.setContactTo(member.getContact().getContactTo());
				mContact.setRelation(member.getContact().getRelation());
				mContact.setMobile(member.getContact().getMobile());
				mContact.setMobile2(member.getContact().getMobile2());
				mContact.setPhone(member.getContact().getPhone());
				mContact.setPhone2(member.getContact().getPhone2());
				mContact.setFax(member.getContact().getFax());
				mContact.setIsActive(contacts[0].getIsActive());
				new MContact().save(mContact, user.getId(), conn);
			} else {
				// insert
				mContact = new Contact();
				mContact = (Contact) UploadXLSUtil.cloneAttributes(member.getContact(), mContact);
				mContact.setId(0);
				mContact.setCustomerId(mMember.getId());
				mContact.setContactTo(member.getContact().getContactTo());
				mContact.setRelation(member.getContact().getRelation());
				mContact.setMobile(member.getContact().getMobile());
				mContact.setMobile2(member.getContact().getMobile2());
				mContact.setPhone(member.getContact().getPhone());
				mContact.setPhone2(member.getContact().getPhone2());
				mContact.setFax(member.getContact().getFax());
				mContact.setIsActive("Y");
				new MContact().save(mContact, user.getId(), conn);
			}

			// product
			// delete member product
			new MMemberProduct().deleteAllLines(mMember.getId(), conn);
			List<MemberProduct> mMemberProducts = new ArrayList<MemberProduct>();
			for (IMemberProductBean memberProduct : member.getProductList()) {
				whereCause = new StringBuffer();
				whereCause.append(" AND CUSTOMER_ID = " + mMember.getId());
				whereCause.append(" AND PRODUCT_ID = " + memberProduct.getProduct().getId());
				whereCause.append(" AND UOM_ID = '" + memberProduct.getProduct().getUom().getId() + "'");
				MemberProduct[] memberProducts = new MMemberProduct().search(whereCause.toString());
				if (memberProducts != null && memberProducts.length > 0) {
					// update
					mMemberProduct = memberProducts[0];
					mMemberProduct = (MemberProduct) UploadXLSUtil.cloneAttributes(memberProduct, mMemberProduct);
					mMemberProduct.setId(memberProducts[0].getId());
					mMemberProduct.setCustomerId(memberProducts[0].getCustomerId());
					mMemberProduct.setProduct(memberProduct.getProduct());
					mMemberProduct.setUomId(memberProduct.getProduct().getUom().getId());
					mMemberProduct.setOrderQty(memberProduct.getOrderQty());
					new MMemberProduct().save(mMemberProduct, user.getId(), conn);
				} else {
					// insert
					mMemberProduct = new MemberProduct();
					mMemberProduct = (MemberProduct) UploadXLSUtil.cloneAttributes(memberProduct, mMemberProduct);
					mMemberProduct.setId(0);
					mMemberProduct.setCustomerId(mMember.getId());
					mMemberProduct.setProduct(memberProduct.getProduct());
					mMemberProduct.setUomId(memberProduct.getProduct().getUom().getId());
					mMemberProduct.setOrderQty(memberProduct.getOrderQty());
					new MMemberProduct().save(mMemberProduct, user.getId(), conn);
				}
				mMemberProducts.add(mMemberProduct);
			}

			// delete old order
			whereCause = new StringBuffer();
			whereCause.append(" AND CUSTOMER_ID = '" + mMember.getId() + "'");
			Order[] orders = new MOrder().search(whereCause.toString());
			if (orders != null && orders.length > 0) {
				for (Order order : orders) {
					// Delete All Line
					new MOrderLine().deleteAllLines(order.getId(), conn);
					// // delete old order line
					// whereCause = new StringBuffer();
					// whereCause.append(" AND ORDER_ID = '" + order.getId() + "'");
					// OrderLine[] orderLines = new MOrderLine().search(whereCause.toString());
					// if (orderLines != null && orderLines.length > 0) {
					// for (OrderLine orderLine : orderLines) {
					// new MOrderLine().delete(orderLine.getId() + "", conn);
					// }
					// }

					// delete old receipt line
					whereCause = new StringBuffer();
					whereCause.append(" AND ORDER_ID = '" + order.getId() + "'");
					ReceiptLine[] receiptLines = new MReceiptLine().search(whereCause.toString());
					if (receiptLines != null && receiptLines.length > 0) {
						for (ReceiptLine receiptLine : receiptLines) {
							// delete old receipt match
							whereCause = new StringBuffer();
							whereCause.append(" AND RECEIPT_LINE_ID = '" + receiptLine.getId() + "'");
							ReceiptMatch[] receiptMatchs = new MReceiptMatch().search(whereCause.toString());
							if (receiptMatchs != null && receiptMatchs.length > 0) {
								for (ReceiptMatch receiptMatch : receiptMatchs) {
									new MReceiptMatch().delete(receiptMatch.getId() + "", conn);
								}
							}

							new MReceiptLine().delete(receiptLine.getId() + "", conn);
						}
					}

					new MOrder().delete(order.getId() + "", conn);
				}
			}

			// insert new order
			if (member.getCustomer().getStartNextYear() != null && !member.getCustomer().getStartNextYear().equals("")) {
				// mMember = new MMember().find(String.valueOf(mMember.getId()));
				// mMember.setMemberProducts(new MMemberProduct().lookUp(mMember.getId()));
				mMember.setMemberProducts(mMemberProducts);

				Order order = new Order();
				order.setOrderDate(member.getCustomer().getStartNextYear());
				// order.setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));
				order.setOrderTime(DateToolsUtil.getCurrentDateTime("HH:mm"));

				// get current price list from user’s order type
				order.setPriceListId(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId());

				// user & order type
				order.setSalesRepresent(user);
				order.setOrderType(user.getOrderType().getKey());

				// default value from member
				order.setCustomerId(mMember.getId());
				order.setCustomerName((mMember.getCode() + "-" + mMember.getName() + " " + mMember.getName2()).trim());
				order.setPaymentTerm(mMember.getPaymentTerm());
				order.setPaymentMethod(mMember.getPaymentMethod());
				order.setVatCode(mMember.getVatCode());

				if (order.getVatCode() != null && !order.getVatCode().equals("")) {
					order.setVatRate(new Double(order.getVatCode()));
				}

				order.setShippingDay(mMember.getShippingDate());
				order.setShippingTime(mMember.getShippingTime());
				order.setRoundTrip(new Double(mMember.getRoundTrip()).intValue());

				List<OrderLine> tempOrderLines = new OrderProcess().generateLine(mMember, order, member.getCustomer()
						.getStartNextYear());
				List<OrderLine> orderLines = new ArrayList<OrderLine>();
				double vatAmount = 0.00;
				double orderTotalAmount = 0.00;
				double orderVatAmount = 0.00;
				double orderNetAmount = 0.00;
				double lineAmount = 0;
				if (tempOrderLines != null && tempOrderLines.size() > 0) {
					for (OrderLine orderLine : tempOrderLines) {
						logger.debug("compare trip no.: " + orderLine.getTripNo() + " with order line remain: "
								+ member.getCustomer().getOrderLineRemain());
						if (orderLine.getTripNo() <= member.getCustomer().getOrderLineRemain()) {
							// calculate
							logger.debug("calculate");
							lineAmount = orderLine.getPrice() * orderLine.getQty();
							vatAmount = (orderLine.getPrice() * orderLine.getQty()) * (order.getVatRate() / 100);
							orderLine.setLineAmount(lineAmount);
							orderLine.setVatAmount(vatAmount);
							orderLine.setTotalAmount(orderLine.getLineAmount() + orderLine.getVatAmount());
							orderLines.add(orderLine);

							// for order
							orderTotalAmount += orderLine.getLineAmount();
							orderVatAmount += orderLine.getVatAmount();
							orderNetAmount += orderLine.getTotalAmount();
						} else {
							// exit loop
							break;
						}
					}
				}

				order.setTotalAmount(orderTotalAmount);
				order.setVatAmount(orderVatAmount);
				order.setNetAmount(orderNetAmount);
				order.setDocStatus("SV");
				order.setInterfaces("N");
				order.setBillAddressId(billToAddressId == 0 ? shipToAddressId : billToAddressId);
				order.setShipAddressId(shipToAddressId);
				order.setPayment("N");

				// Order No
				nextId = SequenceProcess.getNextValue("i_member_order");
				String orderNo = "O5312" + new DecimalFormat("0000").format(nextId);
				order.setOrderNo(orderNo);

				new MOrder().saveImportOrder(order, user.getId(), conn);

				if (orderLines != null && orderLines.size() > 0) {
					for (OrderLine orderLine : orderLines) {
						orderLine.setOrderId(order.getId()); // FK
						orderLine.setPayment("N");
						orderLine.setPromotionFrom("N");
						orderLine.setExported("N");
						orderLine.setNeedExport("N");
						orderLine.setInterfaces("N");
						new MOrderLine().save(orderLine, user.getId(), conn);
					}
				}

				// trx history
				TrxHistory trx = new TrxHistory();
				trx.setTrxModule(TrxHistory.MOD_ORDER);
				trx.setTrxType(TrxHistory.TYPE_INSERT);
				trx.setRecordId(order.getId());
				trx.setUser(user);
				new MTrxHistory().save(trx, user.getId(), conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
}
