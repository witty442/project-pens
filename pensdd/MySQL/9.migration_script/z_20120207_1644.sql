-- Modify Table for Change Discount Column Type in Order Line 
ALTER TABLE t_order_line MODIFY DISCOUNT decimal(15,5);

ALTER TABLE t_order_line ADD COLUMN CF_SHIP_DATE TIMESTAMP;
ALTER TABLE t_order_line ADD COLUMN CF_RECEIPT_DATE TIMESTAMP;
ALTER TABLE t_order_line ADD COLUMN PREPAY VARCHAR(1) DEFAULT 'N';
