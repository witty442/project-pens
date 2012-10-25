/** Drop FK Ad_user all **/
ALTER TABLE c_trx_history 
DROP FOREIGN KEY c_trx_history_ibfk_1;

ALTER TABLE m_customer
DROP FOREIGN KEY m_customer_ibfk_2;

ALTER TABLE m_inventory_onhand
DROP FOREIGN KEY m_inventory_onhand_ibfk_1;

ALTER TABLE m_sales_inventory
DROP FOREIGN KEY m_sales_inventory_ibfk_2;

ALTER TABLE m_sales_target
DROP FOREIGN KEY m_sales_target_ibfk_1;

ALTER TABLE m_sales_target_new
DROP FOREIGN KEY m_sales_target_new_ibfk_2;

ALTER TABLE m_trip
DROP FOREIGN KEY m_trip_ibfk_2;

ALTER TABLE t_order
DROP FOREIGN KEY t_order_ibfk_2;

ALTER TABLE t_receipt
DROP FOREIGN KEY t_receipt_ibfk_1;

ALTER TABLE t_visit
DROP FOREIGN KEY t_visit_ibfk_2;