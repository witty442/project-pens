package com.isecinc.pens.web.modifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.BeanParameter;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Modifier;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MModifier;
import com.isecinc.pens.model.MModifierLine;
import com.pens.util.DateToolsUtil;

/**
 * Modifier Action Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierAction.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 *          Modifier : A-neak.t 04/11/2010 edit method search.
 */
public class ModifierAction extends I_Action {

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModifierForm modifierForm = (ModifierForm) form;

		try {
			ModifierCriteria criteria = getSearchCriteria(request, modifierForm.getCriteria(), this.getClass()
					.toString());
			modifierForm.setCriteria(criteria);

			User user = (User) request.getSession().getAttribute("user");

			String whereCause = "";
			if (modifierForm.getModifier().getType().trim().length() > 0) {
				whereCause += " AND TYPE LIKE '%"
						+ modifierForm.getModifier().getType().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (modifierForm.getModifier().getCode().trim().length() > 0) {
				whereCause += " AND CODE LIKE '%"
						+ modifierForm.getModifier().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%'";
			}
			if (modifierForm.getModifier().getName().trim().length() > 0) {
				whereCause += " AND NAME LIKE '%"
						+ modifierForm.getModifier().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%'";
			}
			if (modifierForm.getModifier().getStartDate().trim().length() > 0) {
				whereCause += " AND START_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(modifierForm.getModifier().getStartDate().trim()) + "'";
			}
			if (modifierForm.getModifier().getEndDate().trim().length() > 0) {
				whereCause += " AND END_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(modifierForm.getModifier().getEndDate().trim()) + "'";
			}
			if (modifierForm.getModifier().getIsActive().trim().length() > 0) {
				whereCause += " AND ISACTIVE = '" + modifierForm.getModifier().getIsActive() + "'";
			}

			whereCause += "  AND modifier_id IN (";
			whereCause += " select modifier_id from m_qualifier ";
			whereCause += " where operator = '=' ";
			whereCause += "   and isexclude = 'N' ";
			whereCause += "   and isactive = 'Y' ";
			whereCause += "   and QUALIFIER_CONTEXT = '" + BeanParameter.getQualifierContext() + "' ";
			whereCause += "   and qualifier_type = '" + BeanParameter.getQualifierType() + "' ";
			if (user.getType().equalsIgnoreCase(User.VAN))
				whereCause += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierVAN() + "' ";

			if (user.getType().equalsIgnoreCase(User.DD))
				whereCause += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierDD() + "' ";

			if (user.getType().equalsIgnoreCase(User.TT))
				whereCause += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierTT() + "' ";

			whereCause += ")";

			whereCause += " Order by modifier_id desc ";

			Modifier[] results = new MModifier().search(whereCause);
			modifierForm.setResults(results);

			if (results != null) {
				modifierForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModifierForm modifierForm = (ModifierForm) form;
		Modifier modifier = null;

		try {
			modifier = new MModifier().find(id);
			if (modifier == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			modifierForm.setModifier(modifier);
			modifierForm.setLines(new MModifierLine().lookUp(modifier.getId()));
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setNewCriteria(ActionForm form) throws Exception {
		ModifierForm modifierForm = (ModifierForm) form;
		modifierForm.setCriteria(new ModifierCriteria());
	}

}
