package com.isecinc.pens.process.dataimports;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberProduct;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.process.SequenceProcess;

/**
 * Member Import Process
 * 
 * @author atiz.b
 * @version $Id: MemberOrderImportProcess.java,v 1.0 13/01/2011 00:00:00 atiz.b Exp $
 * 
 */
public class MemberOrderImportProcess {

	/**
	 * Create Order
	 * 
	 * @param memberIds
	 * @param orderDate
	 * @return orders
	 * @throws Exception
	 */
	public List<Order> createOrder(String[] memberIds, String orderDate, User user, Connection conn) throws Exception {
		List<Order> orderCreated = new ArrayList<Order>();
		try {
			Order order;
			OrderLine line;
			Member member;
			List<Address> addresses;
			List<ProductPrice> pps;
			MMemberProduct mMemberProduct = new MMemberProduct();
			MAddress mAddress = new MAddress();
			MProduct mProduct = new MProduct();
			MUOM muom = new MUOM();
			MProductPrice mProductPrice = new MProductPrice();
			MOrder mOrder = new MOrder();
			MOrderLine mOrderLine = new MOrderLine();

			double lineVat;
			double sumLineAmount;
			double sumLineVat;
			double sumLineNet;
			int nextId = 0;
			for (String id : memberIds) {
				member = new MMember().find(id);

				if (member != null) {
					// member product
					member.setMemberProducts(mMemberProduct.lookUp(member.getId()));
					addresses = mAddress.lookUp(member.getId());
					// create order
					order = new Order();

					// member info
					order.setCustomerId(member.getId());
					order.setCustomerName((member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
					order.setPaymentTerm(member.getPaymentTerm());
					order.setPaymentMethod(member.getPaymentMethod());
					order.setVatCode(member.getVatCode());
					order.setShippingDay(member.getShippingDate());
					order.setShippingTime(member.getShippingTime());

					// order info
					order.setOrderDate(orderDate);
					order.setOrderType(user.getOrderType().getKey());
					for (Address addr : addresses) {
						if (addr.getPurpose().equalsIgnoreCase("S")) order.setShipAddressId(addr.getId());
						if (addr.getPurpose().equalsIgnoreCase("B")) order.setBillAddressId(addr.getId());
					}
					if (order.getBillAddressId() == 0) {
						for (Address addr : addresses) {
							if (addr.getPurpose().equalsIgnoreCase("S")) {
								order.setBillAddressId(addr.getId());
								break;
							}
						}
					}
					order.setPriceListId(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId());
					order.setSalesRepresent(user);
					order.setDocStatus(Order.DOC_SAVE);
					order.setExported("N");
					order.setInterfaces("N");
					order.setPayment("N");

					// Order No
					nextId = SequenceProcess.getNextValue("member_order_import");
					String orderNo = "O5311" + new DecimalFormat("0000").format(nextId);
					order.setOrderNo(orderNo);

					// Save Order
					mOrder.saveImportOrder(order, user.getId(), conn);

					// create order line
					int lineNo = 1;
					sumLineAmount = 0;
					sumLineVat = 0;
					sumLineNet = 0;
					for (MemberProduct mp : member.getMemberProducts()) {
						pps = null;
						line = new OrderLine();
						line.setPromotion("N");
						line.setProduct(mProduct.find(String.valueOf(mp.getProduct().getId())));
						line.setQty(mp.getOrderQty());
						line.setUom(muom.find(String.valueOf(mp.getUomId())));

						// get Product Price
						pps = mProductPrice.getCurrentPrice(String.valueOf(line.getProduct().getId()), String
								.valueOf(order.getPriceListId()), line.getUom().getId());
						for (ProductPrice pp : pps) {
							line.setPrice(pp.getPrice());
						}
						line.setLineAmount(line.getQty() * line.getPrice());
						line.setDiscount(0);
						lineVat = line.getLineAmount() * Integer.parseInt(order.getVatCode()) / 100;
						line.setVatAmount(lineVat);
						line.setTotalAmount(line.getLineAmount() - line.getDiscount() + lineVat);
						line.setShippingDate(orderDate);
						line.setRequestDate(orderDate);
						line.setLineNo(lineNo++);
						line.setTripNo(1);
						line.setOrderId(order.getId());
						line.setPayment("N");
						line.setExported("N");
						line.setNeedExport("N");
						line.setInterfaces("N");

						// save line
						mOrderLine.save(line, user.getId(), conn);

						sumLineAmount += line.getLineAmount();
						sumLineVat += line.getVatAmount();
						sumLineNet += line.getTotalAmount();
					}

					// re-save
					order.setTotalAmount(sumLineAmount);
					order.setVatAmount(sumLineVat);
					order.setNetAmount(sumLineNet);

					mOrder.saveImportOrder(order, user.getId(), conn);

					// add to show
					orderCreated.add(order);

					// Trx History
					TrxHistory trx = new TrxHistory();
					trx.setTrxModule(TrxHistory.MOD_ORDER);
					trx.setTrxType(TrxHistory.TYPE_INSERT);
					trx.setRecordId(order.getId());
					trx.setUser(user);
					new MTrxHistory().save(trx, user.getId(), conn);
					// Trx History --end--
				}// if
			}
		} catch (Exception e) {
			throw e;
		}
		return orderCreated;
	}
}
