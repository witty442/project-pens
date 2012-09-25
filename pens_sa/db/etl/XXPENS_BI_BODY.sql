create or replace
PACKAGE BODY XXPENS_BI IS
	-- 11-Jan-11 , Ank, Initial
	--07-Feb-11, Ank, exc free goods from invoiced amt and ordered_amt
	--                 Ank, promotion amt, get item cost
	-- 25-Jul-11, Ank, bugfix promotion amount
	--15-Aug-11, Ank, add one more feature about 2 dimensions: customer-ship to address and customer-bill to address
  -- 10-JAN-2012 , pasuwatw@gmail.com ; FIXED Wrong Cost Amount To Calculate Promotion
  -- 16-JAN-2012 , pasuwatw@gmail.com ; FIXED Promotion Amount Should Be Sum From Material Transaction
	v_debug VARCHAR2(1) := 'Y';
	-- debug
	PROCEDURE debug(p_msg_code varchar2) IS
		v_msg varchar2(255);
	BEGIN
		if v_debug = 'Y' then
			v_msg := substrb(P_msg_code, 1, 255);
			fnd_file.put_line(fnd_file.log, v_msg);
		end if;
	END debug;

  procedure running_status_update(pio_running_id  IN OUT NUMBER)
  IS pragma autonomous_transaction;
  BEGIN
    IF pio_running_id = 0 THEN
        SELECT XXPENS_BI_RUNNING_STATUS_SEQ.NEXTVAL
        INTO pio_running_id
        FROM DUAL;

        INSERT INTO XXPENS_BI_RUNNING_STATUS(RUNNING_ID,START_DATE) VALUES (pio_running_id,SYSDATE);
    ELSE
      UPDATE XXPENS_BI_RUNNING_STATUS
      SET END_DATE = SYSDATE
      WHERE RUNNING_ID = pio_running_id;
    END IF;
    COMMIT;
  END;

	-- get target info
	PROCEDURE get_target_info(p_salesrep_id       in number,
														p_inventory_item_id in number,
														p_ordered_date      in date,
														x_target_qty        out number,
														x_target_amount     out number) IS
	BEGIN
		select NVL(ts.sale_target_qty, 0) target_qty,
					 NVL(ts.sale_target_amount, 0) target_amount
			into x_target_qty, x_target_amount
			from xxpens_target_lines    tl,
					 xxpens_target_salesrep ts,
					 xxpens_target_headers  th
		 where 1 = 1
			 and ts.salesrep_id = p_salesrep_id
			 and tl.inventory_item_id = p_inventory_item_id
			 and p_ordered_date between th.active_start_date and
					 th.active_end_date
			 and ts.sale_target_id = tl.sale_target_id
			 and ts.sale_target_line_id = tl.sale_target_line_id
			 and tl.sale_target_id = th.sale_target_id;
	exception
		when others then
			-- debug( 'get_target_info (salesrep_id='||p_salesrep_id||',item_id='||p_inventory_item_id||',order date='||to_char(p_ordered_date,'DD-MON-RR')||' : '||sqlerrm) ;
			x_target_qty    := 0;
			x_target_amount := 0;
	END get_target_info;

	-- insert record
	PROCEDURE insert_record(p_rec xxpens_bi_sales_analysis%rowtype) IS
		v_salesrep_code            varchar2(30);
		v_salesrep_desc            varchar2(240);
		v_customer_code            varchar2(30);
		v_customer_desc            varchar2(360);
		v_item_code                varchar2(40);
		v_item_desc                varchar2(240);
		v_brand_desc               varchar2(240);
		v_cust_cat_desc            varchar(240);
		v_sales_channel_code       varchar2(30);
		v_sales_channel_desc       varchar2(240);
		v_division_desc            varchar2(240);
		v_tambol                   varchar2(240);
		v_amphur                   varchar2(240);
		v_province                 varchar2(240);
		v_tambol_bill              varchar2(240);
		v_amphur_bill              varchar2(240);
		v_province_bill            varchar2(240);
		v_target_qty               number;
		v_target_amount            number;
		v_customer_ship_to_address varchar2(2000);
		v_customer_bill_to_address varchar2(2000);
	BEGIN

		--XXPENS_BI_MST_CUST_CAT
		begin
			select description
				into v_cust_cat_desc
				from fnd_flex_values_vl
			 where flex_value_set_id = 1014235
				 and flex_value = p_rec.customer_category;
		exception
			when others then
				debug('cust_cat (' || p_rec.customer_category || ')' || sqlerrm);
		end;
		begin
			insert into xxpens_bi_mst_cust_cat
				(cust_cat_no, cust_cat_desc, create_date)
			values
				(p_rec.customer_category, v_cust_cat_desc, sysdate);
		exception
			when dup_val_on_index then
				begin
					update xxpens_bi_mst_cust_cat
						 set cust_cat_desc = v_cust_cat_desc
					 where cust_cat_no = p_rec.customer_category;
				end;
			when others then
				debug('xxpens_bi_mst_cust_cat : ' || sqlerrm);
		end;
		-- XXPENS_BI_MST_DIVISION
		begin
			if p_rec.division = 'A' then
				v_division_desc := 'Division A';
			elsif p_rec.division = 'B' then
				v_division_desc := 'Division B';
			elsif p_rec.division = 'C' then
				v_division_desc := 'Division C';
			else
				v_division_desc := 'Undefined';
			end if;
			insert into xxpens_bi_mst_division
				(div_no, div_desc, create_date)
			values
				(p_rec.division, v_division_desc, sysdate);
		exception
			when dup_val_on_index then
				null;
			when others then
				debug('xxpens_bi_mst_division : ' || sqlerrm);
		end;
		--XXPENS_BI_MST_BRAND
		begin
			select nvl(description, flex_value_meaning)
				into v_brand_desc
				from fnd_flex_values_vl
			 where flex_value_set_id = 1014221
				 and flex_value = p_rec.brand;
		exception
			when others then
				debug('v_brand_desc (' || p_rec.brand || ')' || sqlerrm);
		end;
		begin
			insert into xxpens_bi_mst_brand
				(brand_no, brand_desc, create_date)
			values
				(p_rec.brand, v_brand_desc, sysdate);
		exception
			when dup_val_on_index then
				begin
					update xxpens_bi_mst_brand
						 set brand_desc = v_brand_desc
					 where brand_no = p_rec.brand;
				end;
			when others then
				debug('xxpens_bi_mst_brand : ' || sqlerrm);
		end;
		--XXPENS_BI_MST_CUSTOMER
		begin
			-- customer info
			begin
				select account_number, hp.party_name, hca.sales_channel_code
					into v_customer_code, v_customer_desc, v_sales_channel_code
					from hz_cust_accounts hca, hz_parties hp
				 where hca.cust_account_id = p_rec.customer_id
					 and hca.party_id = hp.party_id;
			exception
				when others then
					debug('customer info (' || p_rec.customer_id || ')' || sqlerrm);
			end;
			begin
				insert into xxpens_bi_mst_customer
					(customer_id, customer_code, customer_desc, create_date)
				values
					(p_rec.customer_id, v_customer_code, v_customer_desc, sysdate);
			exception
				when dup_val_on_index then
					begin
						update xxpens_bi_mst_customer
							 set customer_desc = v_customer_desc
						 where customer_id = p_rec.customer_id;
					end;
				when others then
					debug(' xxpens_bi_mst_customer : ' || sqlerrm);
			end;
		end;
		-- get address info
		begin
			SELECT --csu.attribute7 cust_group,
			-- Start to adjust by Athiwat (27 Jan 11) --
			/*loc.address3 tambol,
      loc.address4 amphur,
      loc.province province*/ -- Original --
			 isec_custom.address_conversion(loc.address3,
																			loc.province,
																			'ADDRESS3') tambol,
			 isec_custom.address_conversion(loc.address4,
																			loc.province,
																			'ADDRESS4') amphur,
			 isec_custom.address_conversion(loc.province,
																			loc.province,
																			'PROVINCE') province,
			 v_customer_desc || '-' || csu.location || '_' ||
			 party_site.party_site_name || ' (Ship-To)'
				INTO v_tambol, v_amphur, v_province, v_customer_ship_to_address
				FROM hz_cust_acct_sites raad,
						 hz_party_sites     party_site,
						 hz_locations       loc,
						 hz_cust_site_uses  csu
			 WHERE 1 = 1
				 and raad.cust_account_id = p_rec.customer_id
						--and  csu.site_use_code = 'BILL_TO'
				 and raad.party_site_id = party_site.party_site_id
				 and loc.location_id = party_site.location_id
				 and raad.cust_acct_site_id = csu.cust_acct_site_id
				 and csu.site_use_id = p_rec.ship_to_site_use_id;
		EXCEPTION
			when others then
				debug(p_rec.sales_order_no ||
							':get address info customer_id, site_to_site_use_id (' ||
							p_rec.customer_id || ',' || p_rec.ship_to_site_use_id || ')' ||
							sqlerrm);
				v_tambol   := '';
				v_amphur   := '';
				v_province := '';
		end;
		begin
			insert into xxpens_bi_mst_cust_ship_addr
				(ship_to_site_use_id, customer_ship_to_address, creation_date)
			values
				(p_rec.ship_to_site_use_id, v_customer_ship_to_address, sysdate);
		exception
			when dup_val_on_index then
				begin
					update xxpens_bi_mst_cust_ship_addr
						 set customer_ship_to_address = v_customer_ship_to_address
					 where ship_to_site_use_id = p_rec.ship_to_site_use_id;
				end;
		end;
		begin
			SELECT v_customer_desc || '-' || csu.location || '_' ||
						 party_site.party_site_name || ' (Bill-To)'
				INTO v_customer_bill_to_address
				FROM hz_cust_acct_sites raad,
						 hz_party_sites     party_site,
						 hz_cust_site_uses  csu
			 WHERE 1 = 1
				 and raad.cust_account_id = p_rec.customer_id
				 and raad.party_site_id = party_site.party_site_id
				 and raad.cust_acct_site_id = csu.cust_acct_site_id
				 and csu.site_use_id = p_rec.bill_to_site_use_id;
		EXCEPTION
			when others then
				debug(p_rec.sales_order_no ||
							':get address info customer_id,bill_to_site_use_id (' ||
							p_rec.customer_id || ',' || p_rec.ship_to_site_use_id || ')' ||
							sqlerrm);
				v_customer_bill_to_address := '';
		end;
		begin
			insert into xxpens_bi_mst_cust_bill_addr
				(bill_to_site_use_id, customer_bill_to_address, creation_date)
			values
				(p_rec.bill_to_site_use_id, v_customer_bill_to_address, sysdate);
		exception
			when dup_val_on_index then
				begin
					update xxpens_bi_mst_cust_bill_addr
						 set customer_bill_to_address = v_customer_bill_to_address
					 where bill_to_site_use_id = p_rec.bill_to_site_use_id;
				end;
		end;
		--XXPENS_BI_MST_PROVINCE
		begin
			if v_province is not null then
				begin
					insert into xxpens_bi_mst_province
						(province, create_date)
					values
						(v_province, sysdate);
				exception
					when dup_val_on_index then
						null;
					when others then
						debug('xxpens_bi_mst_province : ' || sqlerrm);
				end;
			end if;
		end;
		--XXPENS_BI_MST_AMPHOR
		begin
			if v_amphur is not null then
				begin
					insert into xxpens_bi_mst_amphor
						(amphor, create_date)
					values
						(v_amphur, sysdate);
				exception
					when dup_val_on_index then
						null;
					when others then
						debug('xxpens_bi_mst_amphor : ' || sqlerrm);
				end;
			end if;
		end;
		-- XXPENS_BI_MST_TAMBOL
		begin
			if v_tambol is not null then
				begin
					insert into xxpens_bi_mst_tambol
						(tambol, create_date)
					values
						(v_tambol, sysdate);
				exception
					when dup_val_on_index then
						null;
					when others then
						debug('xxpens_bi_mst_tambol : ' || sqlerrm);
				end;
			end if;
		end;
		--XXPENS_BI_MST_SALESREP
		begin
			--salesrep info
			if nvl(p_rec.salesrep_id, 0) != 0 then
				begin
					SELECT srp.salesrep_number code,
								 substr(rd.resource_name, instr(rd.resource_name, ',') + 2) || ' ' ||
								 substr(rd.resource_name,
												1,
												instr(rd.resource_name, ',') - 1) name
						INTO v_salesrep_code, v_salesrep_desc
						FROM jtf_rs_defresources_srp_v srp, jtf_rs_defresources_v rd
					--,   jtf_rs_defresroles_vl rdv
					 WHERE srp.salesrep_id = p_rec.salesrep_id
						 and srp.resource_id = rd.resource_id
								-- EMPLOYEE only
						 and category = 'EMPLOYEE';
					-- SALES only --
					-- and rdv.role_resource_id = srp.resource_id
					-- and  NVL (rdv.delete_flag, 'N') <> 'Y'
					-- AND rdv.role_resource_type = 'RS_INDIVIDUAL' ;
				exception
					when others then
						debug(p_rec.sales_order_no || ' : salesrep info (' ||
									p_rec.salesrep_id || ')' || sqlerrm);
				end;
				begin
					insert into xxpens_bi_mst_salesrep
						(salesrep_id, salesrep_code, salesrep_desc, create_date)
					values
						(p_rec.salesrep_id, v_salesrep_code, v_salesrep_desc, sysdate);
				exception
					when dup_val_on_index then
						begin
							update xxpens_bi_mst_salesrep
								 set salesrep_code = v_salesrep_code,
										 salesrep_desc = v_salesrep_desc
							 where salesrep_id = p_rec.salesrep_id;
						end;
					when others then
						debug('xxpens_bi_mst_salesrep : ' || sqlerrm);
				end;

			end if;
		end;
		--XXPENS_BI_MST_SALES_CHANNEL
		begin
			select nvl(description, meaning)
				into v_sales_channel_desc
				from fnd_lookup_values_vl
			 where lookup_type = 'SALES_CHANNEL'
				 and lookup_code = v_sales_channel_code;
		exception
			when others then
				debug('v_sales_channel_desc (' || v_sales_channel_code || ')' ||
							sqlerrm);
				v_sales_channel_desc := null;
		end;
		begin
			if v_sales_channel_code is not null then
				begin
					insert into xxpens_bi_mst_sales_channel
						(sales_channel_no, sales_channel_desc, create_date)
					values
						(v_sales_channel_code, v_sales_channel_desc, sysdate);
				exception
					when dup_val_on_index then
						begin
							update xxpens_bi_mst_sales_channel
								 set sales_channel_desc = v_sales_channel_desc
							 where sales_channel_no = p_rec.sales_channel;
						end;
					when others then
						debug('xxpens_bi_mst_sales_channel : ' || sqlerrm);
				end;
			end if;
		end;

		--XXPENS_BI_MST_ITEM
		begin
			-- item info
			begin
				select segment1, description
					into v_item_code, v_item_desc
					from mtl_system_items_b
				 where inventory_item_id = p_rec.inventory_item_id
					 and organization_id = 82; -- master
			exception
				when others then
					debug('item info : ' || sqlerrm);
			end;
			begin
				insert into xxpens_bi_mst_item
					(inventory_item_id,
					 inventory_item_code,
					 inventory_item_desc,
					 create_date)
				values
					(p_rec.inventory_item_id, v_item_code, v_item_desc, sysdate);
			exception
				when dup_val_on_index then
					begin
						update xxpens_bi_mst_item
							 set inventory_item_desc = v_item_desc
						 where inventory_item_id = p_rec.inventory_item_id;
					end;
				when others then
					debug('xxpens_bi_mst_item : ' || sqlerrm);
			end;
		end;
		--XXPENS_BI_MST_INVOICE_DATE
		begin
			if p_rec.invoice_date is not null then
				begin
					insert into xxpens_bi_mst_invoice_date
						(invoice_date, invoice_month, invoice_quarter, invoice_year)
					values
						(p_rec.invoice_date,
						 to_char(p_rec.invoice_date, 'MM'),
						 to_char(p_rec.invoice_date, 'Q'),
						 to_char(p_rec.invoice_date, 'RRRR'));
				exception
					when dup_val_on_index then
						null;
					when others then
						debug('xxpens_bi_mst_invoice_date : ' || sqlerrm);
				end;
			end if;
		end;
		-- XXPENS_BI_MST_ORDER_DATE
		begin
			if p_rec.ordered_date is not null then
				begin
					insert into xxpens_bi_mst_order_date
						(order_date, order_month, order_quarter, order_year)
					values
						(p_rec.ordered_date,
						 to_char(p_rec.ordered_date, 'MM'),
						 to_char(p_rec.ordered_date, 'Q'),
						 to_char(p_rec.ordered_date, 'RRRR'));
				exception
					when dup_val_on_index then
						null;
					when others then
						debug('xxpens_bi_mst_order_date : ' || sqlerrm);
				end;
			end if;
		end;

		-- get_target_info(p_rec.salesrep_id, p_rec.inventory_item_id, p_rec.ordered_date, v_target_qty, v_target_amount)  ;

		--INSERT XXPENS_BI_SALES_ANALYSIS
		begin
			insert into xxpens_bi_sales_analysis
				(invoice_date,
				 invoice_no,
				 customer_category,
				 division,
				 salesrep_id,
				 sales_channel,
				 customer_group,
				 customer_id,
				 brand,
				 inventory_item_id,
				 province,
				 amphur,
				 tumbol,
				 sales_order_no,
				 target_amt,
				 target_qty,
				 invoiced_amt,
				 invoiced_qty,
				 ordered_amt,
				 ordered_qty,
				 returned_amt,
				 returned_qty,
				 discount_amt,
				 discount_qty,
				 promotion_amt,
				 promotion_qty,
				 backordered_amt,
				 backordered_qty,
				 outstanding_amt,
				 outstanding_qty,
				 ordered_date,
				 order_line_number,
				 ship_to_site_use_id,
				 bill_to_site_use_id)
			values
				(p_rec.invoice_date,
				 p_rec.invoice_no,
				 p_rec.customer_category,
				 p_rec.division,
				 p_rec.salesrep_id,
				 v_sales_channel_code,
				 p_rec.customer_group,
				 p_rec.customer_id,
				 p_rec.brand,
				 p_rec.inventory_item_id,
				 v_province,
				 v_amphur,
				 v_tambol,
				 p_rec.sales_order_no,
				 v_target_amount,
				 v_target_qty,
				 p_rec.invoiced_amt,
				 p_rec.invoiced_qty,
				 p_rec.ordered_amt,
				 p_rec.ordered_qty,
				 p_rec.returned_amt,
				 p_rec.returned_qty,
				 p_rec.discount_amt,
				 p_rec.discount_qty,
				 p_rec.promotion_amt,
				 p_rec.promotion_qty,
				 p_rec.backordered_amt,
				 p_rec.backordered_qty,
				 p_rec.outstanding_amt,
				 p_rec.outstanding_qty,
				 p_rec.ordered_date,
				 p_rec.order_line_number,
				 p_rec.ship_to_site_use_id,
				 p_rec.bill_to_site_use_id);
		exception
			when dup_val_on_index then
				null;
			when others then
				debug('xxpens_bi_sales_analysis : ' || sqlerrm);
		end;
	END insert_record;

	--get_back_ordered
	FUNCTION get_back_ordered(p_order_line_id in number) RETURN varchar2 IS
		v_return varchar2(30);
	BEGIN
		SELECT wd.released_status_name -- 'Awaiting Shipping (Not backorder)'
			INTO v_return
			FROM wsh_deliverables_v wd
		 WHERE wd.source_line_id = p_order_line_id;
		return v_return;
	EXCEPTION
		WHEN NO_DATA_FOUND THEN
			return NULL;
		WHEN OTHERS THEN
			debug('get_back_ordered ' || sqlerrm);
			return NULL;
	END get_back_ordered;
	-- is hold
	FUNCTION is_hold(p_header_id in number) RETURN varchar2 IS
		--  06-Sep-11, ank, add hold_release_id is null
		v_tmp varchar2(1);
	BEGIN
		SELECT 'Y' --hd.name
			INTO v_tmp
			FROM oe_order_holds_all oha
		 WHERE oha.header_id = p_header_id
			 AND hold_release_id is null;
		RETURN 'Y';
	EXCEPTION
		when no_data_found then
			RETURN 'N';
		when others then
			RETURN 'Y';
	END is_hold;
	-- get item cost
	FUNCTION get_item_cost(p_inventory_item_id in number,
												 p_organization_id   in number) RETURN number IS
		v_ret number;
	BEGIN
		select item_cost
			into v_ret
			from cst_item_costs
		 where inventory_item_id = p_inventory_item_id
			 and organization_id = p_organization_id;
		return v_ret;
	exception
		when others then
			debug('get_item_cost (inventory_item_id=' || p_inventory_item_id ||
						', organization_id=' || p_organization_id || ') ' || sqlerrm);
			return(0);
	END get_item_cost;

  FUNCTION get_item_cost_mt(p_order_line_id  in number) RETURN number IS
		v_ret number;
	BEGIN
		SELECT mt.actual_cost into v_ret
    FROM mtl_material_transactions mt
    where mt.trx_source_line_id = p_order_line_id
    AND SOURCE_CODE = 'ORDER ENTRY';

		return v_ret;
	exception
		when others then
			debug('get_item_cost_mt (p_order_line_id=' || p_order_line_id || ') ' || sqlerrm);
			return(0);
	END get_item_cost_mt;
  
  FUNCTION get_promotion_amt(p_order_line_id  in number) RETURN number IS
		v_ret number;
	BEGIN
		SELECT SUM(mt.actual_cost * abs(mt.primary_quantity)) into v_ret
    FROM mtl_material_transactions mt
    where mt.trx_source_line_id = p_order_line_id
    AND SOURCE_CODE = 'ORDER ENTRY';

		return v_ret;
	exception
		when others then
			debug('get_promotion_amt (p_order_line_id=' || p_order_line_id || ') ' || sqlerrm);
			return(0);
	END get_promotion_amt;

	-- get invoice info
	PROCEDURE get_invoice_info(p_order_category_code in varchar2,
														 p_order_no            in varchar2,
														 p_line_no             in varchar2,
														 p_inventory_item_id   in number,
														 p_ordered_qty         in number,
                             p_line_id             in varchar2,
														 x_invoice_no          out varchar2,
														 x_invoice_date        out date,
														 x_invoiced_amt        out number,
														 x_invoiced_qty        out number,
														 x_credited_amt        out number,
														 x_credited_qty        out number,
														 x_discount_amt        out number,
														 x_discount_qty        out number,
														 x_promotion_amt       out number,
														 x_promotion_qty       out number) IS
		-- var --
		v_organization_id     number;
		v_inventory_item_id   number;
		v_unit_selling_price  number;
		v_unit_standard_price number;
	BEGIN
		SELECT rct.trx_number,
					 rct.trx_date,
					 -- Start to adjust by Athiwat (27 Jan 11) --
					 -- DECODE(p_order_category_code,'ORDER', NVL(rctl.quantity_invoiced,0)*NVL(rctl.unit_selling_price,0),0), -- Original --
					 DECODE(p_order_category_code,
									'ORDER',
									NVL(rctl.quantity_invoiced, 0) *
									NVL(rctl.unit_standard_price, 0),
									0) invoiced_amt,
					 -- End to adjust --
					 DECODE(p_order_category_code,
									'ORDER',
									inv_convert.inv_um_convert(rctl.inventory_item_id,
																						 5,
																						 rctl.quantity_invoiced,
																						 rctl.uom_code,
																						 'CTN',
																						 '',
																						 ''),
									0) invoiced_qty,
					 -- Start to adjust by Athiwat (27 Jan 11) --
					 -- ABS(DECODE(p_order_category_code,'RETURN', NVL(rctl.quantity_credited,0)*NVL(rctl.unit_selling_price,0),0)), -- Original --
					 ABS(DECODE(p_order_category_code,
											'RETURN',
											DECODE(NVL(rctl.unit_selling_price, 0),
														 0,
														 0,
														 NVL(rctl.quantity_credited, 0) *
														 NVL(rctl.unit_standard_price, 0)),
											0)) returned_amt,
					 -- End to adjust --
					 ABS(DECODE(p_order_category_code,
											'RETURN',
											DECODE(NVL(rctl.unit_selling_price, 0),
														 0,
														 0,
														 inv_convert.inv_um_convert(rctl.inventory_item_id,
																												5,
																												rctl.quantity_credited,
																												rctl.uom_code,
																												'CTN',
																												'',
																												'')),
											0)) returned_qty,
					 -- discount
					 DECODE(sign(NVL(rctl.unit_selling_price, 0)),
									0,
									0,
									DECODE(p_order_category_code,
												 'ORDER',
												 NVL(rctl.quantity_invoiced, 0),
												 NVL(rctl.quantity_credited, 0)) *
									(NVL(rctl.unit_standard_price, 0) -
									 NVL(rctl.unit_selling_price, 0))) discount_amt,
					 0 discount_qty,
					 DECODE(NVL(rctl.unit_selling_price, 0),
									0,
									DECODE(p_order_category_code,
												 'ORDER',
												 NVL(rctl.quantity_invoiced, 0) *
												 NVL(rctl.unit_standard_price, 0),
												 NVL(rctl.quantity_credited, 0) *
												 NVL(rctl.unit_standard_price, 0)),
									0) promotion_amt,
					 DECODE(NVL(rctl.unit_selling_price, 0),
									0,
									DECODE(p_order_category_code,
												 'ORDER',
												 inv_convert.inv_um_convert(rctl.inventory_item_id,
																										5,
																										rctl.quantity_invoiced,
																										rctl.uom_code,
																										'CTN',
																										'',
																										''),
												 inv_convert.inv_um_convert(rctl.inventory_item_id,
																										5,
																										rctl.quantity_credited,
																										rctl.uom_code,
																										'CTN',
																										'',
																										'')),
									0) promotion_qty,
					 rctl.inventory_item_id,
					 rctl.warehouse_id,
					 NVL(rctl.unit_selling_price, 0) unit_selling_price,
					 NVL(rctl.unit_standard_price, 0) unit_standard_price
			into x_invoice_no,
					 x_invoice_date,
					 x_invoiced_amt,
					 x_invoiced_qty,
					 x_credited_amt,
					 x_credited_qty,
					 x_discount_amt,
					 x_discount_qty,
					 x_promotion_amt,
					 x_promotion_qty,
					 v_inventory_item_id,
					 v_organization_id,
					 v_unit_selling_price,
					 v_unit_standard_price
			from ra_customer_trx       rct,
					 ra_customer_trx_lines rctl,
					 ar_payment_schedules  ps
		 where rct.ct_reference = p_order_no
			 and rctl.sales_order_line = p_line_no
			 and rctl.inventory_item_id = p_inventory_item_id
			 and rct.customer_trx_id = rctl.customer_trx_id
			 and rctl.line_type = 'LINE'
			 and ps.customer_trx_id = rct.customer_trx_id
			 and ps.class = decode(p_order_category_code, 'ORDER', 'INV', 'CM')
       and coalesce(rctl.interface_line_attribute6,p_line_id) = p_line_id;

		-- if split line
		if nvl(p_ordered_qty, 0) != nvl(x_invoiced_qty, 0) then
			x_invoiced_qty := nvl(p_ordered_qty, 0);
			x_invoiced_amt := nvl(p_ordered_qty, 0) *
												nvl(v_unit_standard_price, 0);
		end if;

		if nvl(v_unit_selling_price, 0) = 0 then
			-- promotion amt
			/* FIXED : WRONG COST AMT FOR FIFO COSTING METHOD
        x_promotion_amt := nvl(x_promotion_qty, 0) *
												 get_item_cost
                         (v_inventory_item_id,
																			 v_organization_id);
      */
      -- x_promotion_amt := nvl(x_promotion_qty, 0) * get_item_cost_mt(p_line_id);
      x_promotion_amt := get_promotion_amt(p_line_id);

			x_invoiced_amt  := 0;
			x_invoiced_qty  := 0;
			x_discount_amt  := 0; -- Not discount --
			x_discount_qty  := 0; -- Not discount --
		end if;

	exception
		when no_data_found then
			null;
		when others then
			debug('get_invoice_info : p_order_category_code,p_order_no,p_line_no,p_inventory_item_id=>' ||
						p_order_category_code || ',' || p_order_no || ',' || p_line_no || ',' ||
						p_inventory_item_id || sqlerrm);
	END get_invoice_info;

	FUNCTION IS_ORDERED(p_order_id      NUMBER,
											p_header_status VARCHAR2,
											p_line_status   VARCHAR2) RETURN VARCHAR2 IS
	BEGIN
		IF p_line_status = 'CANCELLED' THEN
			RETURN 'N';
		END IF;

		IF p_line_status = 'ENTERED' AND IS_HOLD(p_order_id) = 'N' THEN
			RETURN 'N';
		END IF;

		RETURN 'Y';
	END IS_ORDERED;

	-- target fact --
	PROCEDURE gen_sales_target(p_cut_off_date in date) IS
		CURSOR c_data IS
			SELECT TO_CHAR(th.active_start_date, 'MM') target_month,
						 TO_CHAR(th.active_start_date, 'Q') target_quater,
						 TO_CHAR(th.active_start_date, 'RRRR') target_year,
						 ts.salesrep_id salesrep_id,
						 tl.inventory_item_id inventory_item_id,
						 th.actual_order_type customer_category,
						 tl.brand brand,
						 case
							 when NVL(ott.attribute4, 'N/A') = 'MT' then
								'A'
							 when NVL(ott.attribute4, 'N/A') IN ('CV', 'CT') then
								'B'
							 when NVL(ott.attribute4, 'N/A') = 'DD' then
								'C'
							 else
								'N/A'
						 end division,
						 tl.sales_channel_code sales_channel,
						 apps.inv_convert.inv_um_convert(tl.inventory_item_id,
																						 5,
																						 NVL(ts.sale_target_qty, 0),
																						 NVL(tl.uom_code,
																								 msi.primary_uom_code),
																						 'CTN',
																						 '',
																						 '') target_qty,
						 NVL(ts.sale_target_amount, 0) target_amount
				FROM apps.xxpens_target_lines    tl,
						 apps.xxpens_target_salesrep ts,
						 apps.xxpens_target_headers  th,
						 apps.mtl_system_items_b     msi,
						 oe_transaction_types_v      ott
			 WHERE 1 = 1
				 AND ts.sale_target_id = tl.sale_target_id
				 AND ts.sale_target_line_id = tl.sale_target_line_id
				 AND tl.sale_target_id = th.sale_target_id
				 AND tl.inventory_item_id = msi.inventory_item_id
				 and msi.organization_id = 82
						-- ott --
				 and tl.transaction_type_id = ott.transaction_type_id
						-- Athiwat S. (8 Mar 2011) --
				 and th.enabled_flag = 'Y'
			--and th.current_flag = 'Y'
			;
	BEGIN
		begin
			-- delete from xxpens_bi_sales_target ;
			execute immediate 'truncate table pensbi.xxpens_bi_sales_target';
		end;
		FOR x IN c_data LOOP
			begin
				insert into xxpens_bi_sales_target
					(target_month,
					 target_quater,
					 target_year,
					 salesrep_id,
					 inventory_item_id,
					 target_qty,
					 target_amount,
					 customer_category,
					 brand,
					 division,
					 sales_channel)
				values
					(x.target_month,
					 x.target_quater,
					 x.target_year,
					 x.salesrep_id,
					 x.inventory_item_id,
					 x.target_qty,
					 x.target_amount,
					 x.customer_category,
					 x.brand,
					 x.division,
					 x.sales_channel);
			exception
				--when dup_val_on_index then null ;
				when others then
					debug('insert XXPENS_BI_SALES_TARGET : ' || sqlerrm);
			end;
		END LOOP;
	END gen_sales_target;

	-- etl
	PROCEDURE etl(errbuf             OUT varchar2,
								retcode            OUT number,
								p_cut_off_datetime in varchar2,
								p_cut_off_datetimeFrom IN varchar2,
                p_cut_off_datetimeTo IN varchar2,
								p_debug            in varchar2) IS
		v_cut_off_datetime date := to_date(p_cut_off_datetime,
																			 'YYYY/MM/DD HH24:MI:SS');

		v_cut_off_datetimeFrom date := to_date(p_cut_off_datetimeFrom,'YYYY/MM/DD HH24:MI:SS');
		v_cut_off_datetimeTo date := to_date(p_cut_off_datetimeTo,'YYYY/MM/DD HH24:MI:SS')+0.99999;

    v_running_id  NUMBER :=0 ;
		-- main data
		CURSOR c_data IS
			SELECT h.salesrep_id salesrep_id,
						 l.sold_to_org_id customer_id,
						 cv.segment1 brand,
						 l.inventory_item_id inventory_item_id,
						 NVL(ott.attribute2, 'N/A') customer_category,
						 case
							 when NVL(ott.attribute4, 'N/A') = 'MT' then
								'A'
							 when NVL(ott.attribute4, 'N/A') IN ('CV', 'CT') then
								'B'
							 when NVL(ott.attribute4, 'N/A') = 'DD' then
								'C'
							 else
								'N/A'
						 end division,
						 h.order_number sales_order_number,
						 -- Start to adjust by Athiwat (27 Jan 11) --
						 -- NVL (l.ordered_quantity, 0) * NVL (l.unit_selling_price, 0) ordered_amt, -- Original --
						 NVL(l.ordered_quantity, 0) * NVL(l.unit_list_price, 0) ordered_amt, -- fulled order amount --
						 -- End to adjust --
						 inv_convert.inv_um_convert(l.inventory_item_id,
																				5,
																				l.ordered_quantity,
																				nvl(l.order_quantity_uom,
																						xxpens_bi.get_primary_uom(l.inventory_item_id)),
																				'CTN',
																				'',
																				'') ordered_qty,
						 DECODE(SIGN(NVL(l.unit_selling_price, 0)),
										1,
										NVL(l.ordered_quantity, 0) *
										(NVL(l.unit_list_price, 0) -
										 NVL(l.unit_selling_price, 0)),
										0) discount_amt,
						 0 discount_qty,
						 DECODE(NVL(l.unit_selling_price, 0),
										0,
										NVL(l.ordered_quantity, 0) * NVL(l.unit_list_price, 0),
										0) promotion_amt,
						 DECODE(NVL(l.unit_selling_price, 0),
										0,
										inv_convert.inv_um_convert(l.inventory_item_id,
																							 5,
																							 l.ordered_quantity,
																							 nvl(l.order_quantity_uom,
																									 xxpens_bi.get_primary_uom(l.inventory_item_id)),
																							 'CTN',
																							 '',
																							 ''),
										0) promotion_qty,
						 ott.order_category_code,
						 h.ordered_date ordered_date,
						 NVL(l.ship_to_org_id, h.ship_to_org_id) ship_to_site_use_id,
						 NVL(l.invoice_to_org_id, h.invoice_to_org_id) bill_to_site_use_id,
						 l.line_number,
						 h.header_id,
						 l.line_id,
						 l.flow_status_code,
						 h.flow_status_code h_flow_status_code,
						 nvl(l.unit_selling_price, 0) unit_selling_price,
						 l.credit_invoice_line_id
				FROM oe_order_lines         l,
						 oe_order_headers       h,
						 oe_transaction_types_v ott,
						 -- brand
						 mtl_categories_v  CV,
						 mtl_categories_tl ct,
						 -- seg1--
						 fnd_flex_values_vl  s1,
						 mtl_system_items_b  msi,
						 mtl_item_categories mic
			 WHERE 1 = 1
				 AND h.ordered_date >= COALESCE(v_cut_off_datetimeFrom, v_cut_off_datetime)
				 AND h.ordered_date <= COALESCE(v_cut_off_datetimeTo, h.ordered_date)
				 AND h.header_id = l.header_id
						--mu AND h.booked_flag = 'Y'
				 AND NVL(h.cancelled_flag, 'N') = 'N'
				 AND NVL(l.cancelled_flag, 'N') = 'N'
				 AND h.sold_to_org_id is not null -- Must have customer --
						-- order type --
				 AND h.order_type_id = ott.transaction_type_id
						-- brand
				 AND CV.structure_id = 50329
				 AND CV.category_id = ct.category_id
						-- seg1
				 AND CV.segment1 = s1.flex_value
				 AND s1.flex_value_set_id = 1014221
						-- get item cate id
				 AND l.inventory_item_id = msi.inventory_item_id
				 AND l.ship_from_org_id = msi.organization_id
				 AND mic.category_id = ct.category_id
				 AND msi.inventory_item_id = mic.inventory_item_id
				 AND msi.organization_id = mic.organization_id;
		-- var --
		v_rec                      xxpens_bi_sales_analysis%rowtype;
		v_sign                     number;
		v_awaiting_shipping_reason varchar2(30);
		v_so_line                  varchar2(30);

	BEGIN
    RUNNING_STATUS_UPDATE(v_running_id);

		-- debug
		v_debug := NVL(p_debug, 'N');
		--clear data
		begin
			--execute immediate  'truncate table xxpens_bi_sales_analysis' ;
			delete from xxpens_bi_sales_analysis
			 where ordered_date >= COALESCE(v_cut_off_datetimeFrom, v_cut_off_datetime)
			 AND ordered_date <= COALESCE(v_cut_off_datetimeTo, ordered_date);

		exception
			when others then
				debug('clear data : ' || sqlerrm);
		end;

		FOR x IN c_data LOOP
			-- Check Sales Order Is Not Hold and Line Status Is Just Enter Then
			IF IS_ORDERED(x.header_id, x.h_flow_status_code, x.flow_status_code) = 'Y' THEN
				IF x.order_category_code = 'RETURN' THEN
					--mu v_sign := -1 ;
					v_sign := 0;
					IF nvl(x.credit_invoice_line_id, 0) != 0 THEN
						begin
							select sales_order_line
								into v_so_line
								from ra_customer_trx_lines_all
							 where customer_trx_line_id = x.credit_invoice_line_id;
						exception
							when others then
								debug('sales_order_line ' || sqlerrm);
								v_so_line := null;
						end;
					else
						v_so_line := null;
					end if;
				else
					v_sign    := 1;
					v_so_line := to_char(x.line_number);
				end if;
				--procedure to get invoice date, invoice no, invoice_amount, invoice_qty
				v_rec.ordered_qty := v_sign * x.ordered_qty;
				get_invoice_info(x.order_category_code,
												 x.sales_order_number,
												 v_so_line,
												 x.inventory_item_id,
												 v_rec.ordered_qty,
                         x.line_id,
												 v_rec.invoice_no,
												 v_rec.invoice_date,
												 v_rec.invoiced_amt,
												 v_rec.invoiced_qty,
												 v_rec.returned_amt,
												 v_rec.returned_qty,
												 v_rec.discount_amt,
												 v_rec.discount_qty,
												 v_rec.promotion_amt,
												 v_rec.promotion_qty);

				v_rec.customer_category := x.customer_category;
				v_rec.division          := x.division;
				v_rec.salesrep_id       := x.salesrep_id;
				--v_rec.customer_group, -- awaiting DFF
				v_rec.customer_id       := x.customer_id;
				v_rec.brand             := x.brand;
				v_rec.inventory_item_id := x.inventory_item_id;
				v_rec.sales_order_no    := x.sales_order_number;
				if x.unit_selling_price = 0 then
					v_rec.ordered_amt := 0;
				else
					v_rec.ordered_amt := v_sign * x.ordered_amt;
				end if;
				v_rec.ordered_qty := v_sign * x.ordered_qty;
				--  v_rec.discount_amt := v_sign*x.discount_amt ;
				--  v_rec.discount_qty  := v_sign*x.discount_qty ;
				--  v_rec.promotion_amt  := v_sign*x.promotion_amt ;
				--  v_rec.promotion_qty   := v_sign*x.promotion_qty ;

				-- backordered
				/* Comment Out : Pasuwat Wang-arryagul
           Change Concept of Back Order Quantity  Quantity
        if x.flow_status_code = 'AWAITING_SHIPPING' then
          v_awaiting_shipping_reason := get_back_ordered(x.line_id);
          if v_awaiting_shipping_reason = 'Backordered' then
            v_rec.backordered_amt := v_sign * x.ordered_amt;
            v_rec.backordered_qty := v_sign * x.ordered_qty;
          end if;
        else
          v_rec.backordered_amt := 0;
          v_rec.backordered_qty := 0;
        end if;

        -- outstanding
        IF (x.flow_status_code = 'AWAITING_SHIPPING' AND
           NVL(v_awaiting_shipping_reason, 'N/A') != 'Backordered') OR
           (x.flow_status_code = 'AWAITING_RECEIPT') THEN
          v_rec.outstanding_amt := v_sign * x.ordered_amt;
          v_rec.outstanding_qty := v_sign * x.ordered_qty;
        ELSE
          IF is_hold(x.header_id) = 'Y' AND
             x.h_flow_status_code <> 'CLOSED' THEN
            v_rec.outstanding_amt := v_sign * x.ordered_amt;
            v_rec.outstanding_qty := v_sign * x.ordered_qty;
          ELSE
            v_rec.outstanding_amt := 0;
            v_rec.outstanding_qty := 0;
          END IF;
        END IF;

        */

				/** How Order Is Called Backed Order
           1. Order Line Status Is Awaiting Shipping and Shiping Line Status is Ready To Release or Backordered
           2. Order Line Status is Pick Partial and Shipping Line Status is Backordered

           How Order Is Called OutStanding
           1. Order Line Status Is Awaiting Shipping and Shiping Line Status is Picked Confirm/Staged
        */

				v_rec.backordered_amt := 0;
				v_rec.backordered_qty := 0;

				v_rec.outstanding_amt := 0;
				v_rec.outstanding_qty := 0;

				IF x.flow_status_code = 'AWAITING_SHIPPING' THEN
					BEGIN
						SELECT SUM(DECODE(released_status,
															'Y',
															inv_convert.inv_um_convert(dl.inventory_item_id,
																												 5,
																												 dl.requested_quantity,
																												 nvl(dl.requested_quantity_uom,
																														 xxpens_bi.get_primary_uom(dl.inventory_item_id)),
																												 'CTN',
																												 '',
																												 ''),
															0)) as outstanding_qty,
									 SUM(CASE
												 WHEN released_status IN ('B', 'R') THEN
													inv_convert.inv_um_convert(dl.inventory_item_id,
																										 5,
																										 dl.requested_quantity,
																										 nvl(dl.requested_quantity_uom,
																												 xxpens_bi.get_primary_uom(dl.inventory_item_id)),
																										 'CTN',
																										 '',
																										 '')
												 ELSE
													0
											 END) as backordered_amt
							INTO v_rec.outstanding_qty, v_rec.backordered_qty
							FROM wsh_deliverables_v dl
						 WHERE dl.source_line_id = x.line_id ;
				  EXCEPTION WHEN OTHERS THEN
						v_rec.backordered_amt := 0;
						v_rec.backordered_qty := 0;

						v_rec.outstanding_amt := 0;
						v_rec.outstanding_qty := 0;
					END;

					IF v_rec.outstanding_qty > 0 THEN
						v_rec.outstanding_amt := v_sign *
																		 (x.ordered_amt * v_rec.outstanding_qty /
																		 v_rec.ordered_qty);
					END IF;

					IF v_rec.backordered_qty > 0 THEN
						v_rec.backordered_amt := v_sign *
																		 (x.ordered_amt * v_rec.backordered_qty /
																		 v_rec.ordered_qty);
					END IF;
				END IF;

				v_rec.ordered_date        := x.ordered_date;
				v_rec.ship_to_site_use_id := x.ship_to_site_use_id;
				v_rec.bill_to_site_use_id := x.bill_to_site_use_id;
				v_rec.order_line_number   := x.line_number;
				-- insert record --
				insert_record(v_rec);
			END IF;
		END LOOP;
		debug('Start to gen target : ' ||
					to_char(sysdate, 'DD-MON-RR HH24:MI'));
		gen_sales_target(v_cut_off_datetime);
		debug('Finish to gen target : ' ||
					to_char(sysdate, 'DD-MON-RR HH24:MI'));

    RUNNING_STATUS_UPDATE(v_running_id);
	END etl;

	FUNCTION Get_OutStanding_Reason(p_flow_status_code Varchar2,
																	p_org_id           Number,
																	p_header_id        Number,
																	p_line_id          Number) RETURN Varchar2 IS
		v_outstanding_reason VARCHAR2(100);

	BEGIN
		-- Only stuck case --
		IF p_flow_status_code != 'CLOSED' THEN
			-- Group 1 : Awaiting Shipping without Backorder --
			IF p_flow_status_code = 'AWAITING_SHIPPING' THEN
				BEGIN
					SELECT 'Awaiting Shipping (Not backorder)'
						INTO v_outstanding_reason
						FROM OE_ORDER_LINES_ALL ol, WSH_DELIVERABLES_V wd
					 WHERE ol.header_id = wd.source_header_id
						 AND ol.line_id = wd.source_line_id
						 AND ol.flow_status_code = p_flow_status_code
						 AND wd.released_status_name != 'Backordered';

				EXCEPTION
					WHEN NO_DATA_FOUND THEN
						v_outstanding_reason := NULL;
					WHEN OTHERS THEN
						v_outstanding_reason := NULL;
				END;
				-- Group 2 : Drop ship --
			ELSIF p_flow_status_code = 'AWAITING_RECEIPT' THEN
				v_outstanding_reason := 'Drop Ship Case';
				-- Group 3 : Any hold --
			ELSE
				-- Any status code --
				BEGIN
					SELECT hd.name
						INTO v_outstanding_reason
						FROM OE_ORDER_HOLDS_ALL  oha,
								 OE_HOLD_SOURCES_ALL hs,
								 OE_HOLD_DEFINITIONS hd
					 WHERE oha.header_id = p_header_id
						 AND oha.hold_source_id = hs.hold_source_id
						 AND hs.hold_id = hd.hold_id;

				EXCEPTION
					WHEN NO_DATA_FOUND THEN
						v_outstanding_reason := NULL;
					WHEN OTHERS THEN
						v_outstanding_reason := NULL;
				END;
			END IF;
		END IF;

		RETURN v_outstanding_reason;

	END Get_OutStanding_Reason;

	FUNCTION Get_Primary_UOM(p_inventory_item_id Number) RETURN Varchar2 IS
		v_primary_uom_code VARCHAR2(3);

	BEGIN
		BEGIN
			SELECT primary_uom_code
				INTO v_primary_uom_code
				FROM MTL_SYSTEM_ITEMS_B
			 WHERE organization_id = 82
				 AND inventory_item_id = p_inventory_item_id;

		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				v_primary_uom_code := NULL;
			WHEN OTHERS THEN
				v_primary_uom_code := NULL;
		END;

		RETURN v_primary_uom_code;

	END Get_Primary_UOM;

END XXPENS_BI;