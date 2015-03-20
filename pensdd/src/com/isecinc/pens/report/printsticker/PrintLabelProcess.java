package com.isecinc.pens.report.printsticker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import com.isecinc.pens.inf.helper.Utils;

/**
 * PrintSticker Process
 * 
 * @author Aneak.t
 * @version $Id: PrintStickerProcess.java,v 1.0 19/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class PrintLabelProcess {
	
	public List<PrintLabel> doReport(Connection conn, String memberCodeFrom,String memberCodeTo) throws Exception{
		List<PrintLabel> lstData = new ArrayList<PrintLabel>();
		PrintLabel sticker = null;
		StringBuilder sql = new StringBuilder();
		ResultSet rs = null;
		Statement stmt = null;
	
		try {
			sql.delete(0, sql.length());
			sql.append(" select A.* from( \n");
			sql.append(" select \n");
			sql.append(" m.code \n");
			sql.append(" ,m.name \n");
			sql.append(" ,m.name2 \n");
			//sql.append(" ,concat(a.line1,concat(concat(' ' ,a.line2),concat(concat(' ',a.line3),concat(' ',line4)) )) as address \n");
			sql.append(", a.line1 as address1 \n");
			sql.append(" ,concat(a.line2,concat(concat(' ',a.line3),concat(' ',line4) )) as address2 \n");
			sql.append(" ,concat(a.province_name,concat(' ',a.postal_code)) as address3 \n");
			sql.append(", cast(m.code AS UNSIGNED)as code_ \n" );
			sql.append(" from m_customer m ,m_address a \n");
			sql.append(" where m.customer_id = a.customer_id \n");
			sql.append(" and a.purpose = 'S' \n");
			sql.append(" and a.isactive = 'Y' \n");
			sql.append(" )A where 1=1 \n");
			
			if( !Utils.isNull(memberCodeFrom).equals("")  && !Utils.isNull(memberCodeTo).equals("")){
			   sql.append(" and A.code_ >= "+Utils.isNull(memberCodeFrom)+" \n");
			   sql.append(" and A.code_ <= "+Utils.isNull(memberCodeTo)+" \n");
			}
			sql.append(" ORDER BY A.code_  \n");
			
			System.out.println("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			int index = 0;
			
			while(rs.next()){
				if(index % 2 == 0){
					sticker = new PrintLabel();
					sticker.setName1(Utils.isNull(rs.getString("name"))+" "+Utils.isNull(rs.getString("name2")));
					sticker.setCode1(Utils.isNull(rs.getString("code_")));
					sticker.setAddress1(Utils.isNull(rs.getString("address1")));
					sticker.setAddress11(Utils.isNull(rs.getString("address2")));
					sticker.setAddress12(Utils.isNull(rs.getString("address3")));
					
					lstData.add(sticker);
				}else{
					sticker = lstData.get(lstData.size()-1);
					sticker.setName2(Utils.isNull(rs.getString("name"))+" "+Utils.isNull(rs.getString("name2")));
					sticker.setCode2(Utils.isNull(rs.getString("code_")));
					sticker.setAddress2(Utils.isNull(rs.getString("address1")));
					sticker.setAddress21(Utils.isNull(rs.getString("address2")));
					sticker.setAddress22(Utils.isNull(rs.getString("address3")));
					lstData.set(lstData.size()-1, sticker);
				}
				index++;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
}
