package com.liferay.templatesbreaker;


import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutPrototype;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.model.LayoutSetPrototype;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplatesBreakerPortlet extends MVCPortlet {

	private static final String _MERGE_FAIL_COUNT = "merge-fail-count";

	@Override
	public void doView(RenderRequest renderRequest, 
	                   RenderResponse renderResponse) 
			throws IOException, PortletException {

		try {
			
			List<LayoutSetPrototype> siteTemplates  = 
				LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototypes(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			List<LayoutPrototype> pageTemplates  = 
				LayoutPrototypeLocalServiceUtil.getLayoutPrototypes(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			renderRequest.setAttribute("siteTemplates", siteTemplates);
			renderRequest.setAttribute("siteTemplatesMergeFailCounts",
				getSiteTemplatesMergeFailCounts(siteTemplates));
			renderRequest.setAttribute("siteTemplatesMergeFailThreshold",
				getSiteMergeFailThreshold(renderRequest));


			renderRequest.setAttribute("pageTemplates", pageTemplates);
			renderRequest.setAttribute("pageTemplatesMergeFailCounts",
				getPageTemplatesMergeFailCounts(pageTemplates));

			renderRequest.setAttribute("pageTemplatesMergeFailThreshold",
				getPageMergeFailThreshold(renderRequest));


		} catch (Exception e) {
			throw new PortletException(
				"Cannot fetch site or page templates", e);
		}
		
		super.doView(renderRequest, renderResponse);    
	}



	public void breakSiteTemplate(ActionRequest request,
	                              ActionResponse response)
			throws PortletException {

		long siteTemplateId = ParamUtil.getLong(request, "siteTemplateId");

		try {

			LayoutSetPrototype siteTemplate =
				LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototype(
					siteTemplateId);

			int failThreshold = getSiteMergeFailThreshold(request);

			setMergeFailCount(siteTemplate, failThreshold + 1);

			SessionMessages.add(request, "template-broken-successfully");

			sendRedirect(request, response);

		} catch (Exception e) {
			_log.error("Cannot break site template " + siteTemplateId, e);

			throw new PortletException(e);
		}
	}

	public void fixSiteTemplate(ActionRequest request,
	                              ActionResponse response)
			throws PortletException {

		long siteTemplateId = ParamUtil.getLong(request, "siteTemplateId");

		try {
			LayoutSetPrototype siteTemplate =
				LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototype(
					siteTemplateId);

			setMergeFailCount(siteTemplate, 0);

			SessionMessages.add(request, "template-fixed-successfully");

			sendRedirect(request, response);

		} catch (Exception e) {
			_log.error("Cannot fix site template " + siteTemplateId, e);

			throw new PortletException(e);
		}
	}

	public void breakPageTemplate(ActionRequest request,
	                              ActionResponse response)
			throws PortletException {

		long pageTemplateId = ParamUtil.getLong(request, "pageTemplateId");

		try {
			LayoutPrototype pageTemplate =
				LayoutPrototypeLocalServiceUtil.getLayoutPrototype(
					pageTemplateId);

			int failThreshold = getPageMergeFailThreshold(request);

			setMergeFailCount(pageTemplate, failThreshold + 1);

			SessionMessages.add(request, "template-broken-successfully");

			sendRedirect(request, response);

		} catch (Exception e) {
			_log.error("Cannot break page template " + pageTemplateId, e);

			throw new PortletException(e);
		}
	}

	public void fixPageTemplate(ActionRequest request,
	                              ActionResponse response)
			throws PortletException {

		long pageTemplateId = ParamUtil.getLong(request, "pageTemplateId");

		try {

			LayoutPrototype pageTemplate =
				LayoutPrototypeLocalServiceUtil.getLayoutPrototype(
					pageTemplateId);

			setMergeFailCount(pageTemplate, 0);

			SessionMessages.add(request, "template-fixed-successfully");

			sendRedirect(request, response);

		} catch (Exception e) {
			_log.error("Cannot fix page template " + pageTemplateId, e);

			throw new PortletException(e);
		}
	}

	private int getPageMergeFailThreshold(PortletRequest request)
			throws SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)
			request.getAttribute(WebKeys.THEME_DISPLAY);

		return PrefsPropsUtil.getInteger(
			themeDisplay.getCompanyId(),
			PropsKeys.LAYOUT_PROTOTYPE_MERGE_FAIL_THRESHOLD);

	}

	private int getSiteMergeFailThreshold(PortletRequest request)
			throws SystemException {


		ThemeDisplay themeDisplay = (ThemeDisplay)
			request.getAttribute(WebKeys.THEME_DISPLAY);

		return PrefsPropsUtil.getInteger(
			themeDisplay.getCompanyId(),
			PropsKeys.LAYOUT_SET_PROTOTYPE_MERGE_FAIL_THRESHOLD);

	}

	private Map<Long, Integer> getSiteTemplatesMergeFailCounts(
										List<LayoutSetPrototype> siteTemplates)
			throws PortalException, SystemException {

		Map<Long, Integer> mergeFailCounts = new HashMap<Long, Integer>(siteTemplates.size());

		for(LayoutSetPrototype siteTemplate: siteTemplates) {
			mergeFailCounts.put(siteTemplate.getLayoutSetPrototypeId(),
				getMergeFailCount(siteTemplate));
		}

		return mergeFailCounts;
	}

	private int getMergeFailCount(LayoutSetPrototype siteTemplate)
			throws SystemException, PortalException {

		LayoutSet layoutSetPrototypeLayoutSet =
			siteTemplate.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			layoutSetPrototypeSettingsProperties.getProperty(_MERGE_FAIL_COUNT));

		return mergeFailCount;
	}

	private void setMergeFailCount(LayoutSetPrototype siteTemplate, int newCount)
			throws SystemException, PortalException {

		LayoutSet layoutSetPrototypeLayoutSet =
			siteTemplate.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();


		layoutSetPrototypeSettingsProperties.setProperty(_MERGE_FAIL_COUNT,
			String.valueOf(newCount));

		LayoutSetLocalServiceUtil.updateLayoutSet(layoutSetPrototypeLayoutSet);
	}

	private void setMergeFailCount(LayoutPrototype pageTemplate, int newCount)
			throws SystemException, PortalException {

		Layout layoutPrototypeLayout = pageTemplate.getLayout();


		UnicodeProperties prototypeTypeSettingsProperties =
			layoutPrototypeLayout.getTypeSettingsProperties();

		prototypeTypeSettingsProperties.setProperty(_MERGE_FAIL_COUNT,
			String.valueOf(newCount));

		LayoutLocalServiceUtil.updateLayout(layoutPrototypeLayout);
	}

	private Map<Long, Integer> getPageTemplatesMergeFailCounts(
										List<LayoutPrototype> pageTemplates)
			throws PortalException, SystemException {

		Map<Long, Integer> mergeFailCounts =
			new HashMap<Long, Integer>(pageTemplates.size());

		for(LayoutPrototype pageTemplate: pageTemplates) {
			mergeFailCounts.put(pageTemplate.getLayoutPrototypeId(),
				getMergeFailCount(pageTemplate));
		}

		return mergeFailCounts;
	}

	private int getMergeFailCount(LayoutPrototype pageTemplate)
			throws SystemException, PortalException {

		Layout layoutPrototypeLayout = pageTemplate.getLayout();


		UnicodeProperties prototypeTypeSettingsProperties =
			layoutPrototypeLayout.getTypeSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			prototypeTypeSettingsProperties.getProperty(_MERGE_FAIL_COUNT));

		return mergeFailCount;
	}

	public static final Log _log =
		LogFactoryUtil.getLog(TemplatesBreakerPortlet.class);
}
