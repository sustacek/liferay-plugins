<%
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<portlet:defineObjects />

<liferay-ui:success key="template-broken-successfully" message="template-broken-successfully" />
<liferay-ui:success key="template-fixed-successfully" message="template-fixed-successfully" />


<div class="portlet-msg-info">
	<liferay-ui:message key="site-and-page-templates-help" />
</div>

<h3><liferay-ui:message key="site-templates" /></h3>


<liferay-ui:search-container>
	<liferay-ui:search-container-results
			results="${siteTemplates}"
	        total="${fn:length(siteTemplates)}"
	        />

	<liferay-ui:search-container-row
			className="com.liferay.portal.model.LayoutSetPrototype"
	        modelVar="siteTemplate"
	        escapedModel="true"
	        >

		<liferay-ui:search-container-column-text name="template-mergeable" cssClass="aui-w10">

			<c:choose>
				<c:when test="${siteTemplatesMergeFailCounts[siteTemplate.layoutSetPrototypeId] <= siteTemplatesMergeFailThreshold}">

					<span class="portlet-msg-success"><liferay-ui:message key="yes" /></span>

				</c:when>
				<c:otherwise>

					<span class="portlet-msg-error"><liferay-ui:message key="no" /></span>

				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text name="site-template-name" value="${siteTemplate.name}" cssClass="aui-w40"/>

		<liferay-ui:search-container-column-text name="merge-fail-count" cssClass="aui-w20">
			${siteTemplatesMergeFailCounts[siteTemplate.layoutSetPrototypeId]}
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text cssClass="aui-w30">

			<liferay-ui:icon-list>

				<portlet:renderURL var="redirectURL" />

				<portlet:actionURL var="breakSiteTemplateURL" name="breakSiteTemplate">
					<portlet:param name="siteTemplateId" value="${siteTemplate.layoutSetPrototypeId}" />
					<portlet:param name="redirect" value="${redirectURL}" />
				</portlet:actionURL>

				<portlet:actionURL var="fixSiteTemplateURL" name="fixSiteTemplate">
					<portlet:param name="siteTemplateId" value="${siteTemplate.layoutSetPrototypeId}" />
					<portlet:param name="redirect" value="${redirectURL}" />
				</portlet:actionURL>

				<liferay-ui:icon image="close" message="break-me" url="${breakSiteTemplateURL}" />

				<liferay-ui:icon image="checked" message="fix-me" url="${fixSiteTemplateURL}" />

			</liferay-ui:icon-list>

		</liferay-ui:search-container-column-text>

	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator paginate="false" />

</liferay-ui:search-container>


<h3><liferay-ui:message key="page-templates" /></h3>


<liferay-ui:search-container>
	<liferay-ui:search-container-results
			results="${pageTemplates}"
			total="${fn:length(pageTemplates)}"
			/>

	<liferay-ui:search-container-row
			className="com.liferay.portal.model.LayoutPrototype"
			modelVar="pageTemplate"
			escapedModel="true"
			>

		<liferay-ui:search-container-column-text name="template-mergeable" cssClass="aui-w10">

			<c:choose>
				<c:when test="${pageTemplatesMergeFailCounts[pageTemplate.layoutPrototypeId] <= pageTemplatesMergeFailThreshold}">

					<span class="portlet-msg-success"><liferay-ui:message key="yes" /></span>

				</c:when>
				<c:otherwise>

					<span class="portlet-msg-error"><liferay-ui:message key="no" /></span>

				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text name="page-template-name" value="${pageTemplate.name}" cssClass="aui-w40"/>

		<liferay-ui:search-container-column-text name="merge-fail-count" cssClass="aui-w20">
			${pageTemplatesMergeFailCounts[pageTemplate.layoutPrototypeId]}
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text cssClass="aui-w30">

			<liferay-ui:icon-list>

				<portlet:renderURL var="redirectURL" />

				<portlet:actionURL var="breakPageTemplateURL" name="breakPageTemplate">
					<portlet:param name="pageTemplateId" value="${pageTemplate.layoutPrototypeId}" />
					<portlet:param name="redirect" value="${redirectURL}" />
				</portlet:actionURL>

				<portlet:actionURL var="fixPageTemplateURL" name="fixPageTemplate">
					<portlet:param name="pageTemplateId" value="${pageTemplate.layoutPrototypeId}" />
					<portlet:param name="redirect" value="${redirectURL}" />
				</portlet:actionURL>

				<liferay-ui:icon image="close" message="break-me" url="${breakPageTemplateURL}" />

				<liferay-ui:icon image="checked" message="fix-me" url="${fixPageTemplateURL}" />

			</liferay-ui:icon-list>

		</liferay-ui:search-container-column-text>

	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator paginate="false" />

</liferay-ui:search-container>
