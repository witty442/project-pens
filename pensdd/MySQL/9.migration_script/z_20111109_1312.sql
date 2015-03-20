UPDATE T_Receipt_Line , T_Order ,T_RECEIPT
SET T_Receipt_Line.AR_Invoice_No = T_Order.AR_Invoice_No
   , T_Receipt_Line.Sales_Order_No =T_Order.Sales_Order_No
WHERE T_Receipt_Line.ORDER_ID =T_Order.ORDER_ID
AND T_RECEIPT.RECEIPT_ID = T_Receipt_Line.RECEIPT_ID
AND T_RECEIPT.Interfaces = 'Y';