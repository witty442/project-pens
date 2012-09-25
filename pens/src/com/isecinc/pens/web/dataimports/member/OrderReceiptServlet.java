package com.isecinc.pens.web.dataimports.member;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.process.order.OrderProcess;

public class OrderReceiptServlet extends HttpServlet {

	private static final long serialVersionUID = 5272814365651998324L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		performTask(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		performTask(request, response);
	}

	/**
	 * Perform Task
	 * 
	 * @param request
	 * @param response
	 */
	private void performTask(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		try {
			User user = (User) request.getSession().getAttribute("user");
			PrintWriter out = response.getWriter();
			String orderId = request.getParameter("orderId");
			String memberId = request.getParameter("memberId");

			Order order = new MOrder().find(orderId);
			Member member = new MMember().find(memberId);
			List<OrderLine> lines = new MOrderLine().lookUp(Integer.parseInt(orderId));
			List<ReceiptBy> bys = new ArrayList<ReceiptBy>();

			ReceiptBy by = new ReceiptBy();
			by.setPaymentMethod("CS");
			by.setCreditCardType("");
			by.setBank("");
			by.setChequeNo("");
			by.setChequeDate("");
			by.setReceiptAmount(Double.parseDouble(new DecimalFormat("###0.00").format(order.getNetAmount())));
			by.setAllBillId(String.valueOf(order.getId()));
			by.setAllPaid(new DecimalFormat("###0.00").format(order.getNetAmount()));
			by.setWriteOff("N");
			by.setSeedId("");
			bys.add(by);

			Receipt receipt = new Receipt();
			receipt.setReceiptNo(order.getOrderNo());

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			new OrderProcess().createAutoReceipt(receipt, order, lines, bys, member.getCreditcardExpired(), user, conn);

			// rollback somevalue of receipt & order
			// Is Cash
			order.setIsCash("Y");
			new MOrder().save(order, user.getId(), conn);

			conn.commit();
			out.println("S_" + receipt.getId() + "_" + receipt.getReceiptNo());
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("F");
			} catch (Exception e1) {}
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	public void destroy() {
		super.destroy();
	}

}
