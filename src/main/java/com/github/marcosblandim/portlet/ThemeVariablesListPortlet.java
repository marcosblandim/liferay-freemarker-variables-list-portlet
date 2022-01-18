package com.github.marcosblandim.portlet;

import com.github.marcosblandim.constants.ThemeVariablesListPortletKeys;

import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.ServletContextUtil;
import com.liferay.portal.kernel.template.*;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.*;
import com.liferay.taglib.util.ThemeUtil;
import org.osgi.service.component.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author marco
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=ThemeVariablesList",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ThemeVariablesListPortletKeys.THEMEVARIABLESLIST,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ThemeVariablesListPortlet extends MVCPortlet {
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		// ThemeUtil.doIncludeFTL
		try {
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);
		ThemeDisplay themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String path = "portlet.jsp";

		String servletContextName = GetterUtil.getString(
				theme.getServletContextName());
		ServletContext servletContext = ServletContextPool.get(servletContextName);

		if (ServletContextPool.get(servletContextName) == null) {

			ServletContextPool.put(servletContextName, servletContext);
		}
		String portletId = ThemeUtil.getPortletId(httpServletRequest);

		String resourcePath = theme.getResourcePath(
				servletContext, portletId, path);

		if (Validator.isNotNull(portletId) &&
				PortletIdCodec.hasInstanceId(portletId) &&
				!TemplateResourceLoaderUtil.hasTemplateResource(
						TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

			String rootPortletId = PortletIdCodec.decodePortletName(portletId);

			resourcePath = theme.getResourcePath(
					servletContext, rootPortletId, path);
		}

		if (Validator.isNotNull(portletId) &&
				!TemplateResourceLoaderUtil.hasTemplateResource(
						TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

			resourcePath = theme.getResourcePath(servletContext, null, path);
		}

			if (!TemplateResourceLoaderUtil.hasTemplateResource(
					TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

	//			_log.error(resourcePath + " does not exist");


				super.doView(renderRequest, renderResponse);
			}

			resourcePath = theme.getResourcePath(servletContext, null, path).replace("/" + servletContextName, "");
//			resourcePath = "saa-agrosp-theme_SERVLET_CONTEXT_/templates/portlet.ftl";
//			resourcePath = "saa-agrosp-theme_SERVLET_CONTEXT_/templates/portal_normal.ftl";

			boolean restricted =false;

			TemplateResource templateResource =
				TemplateResourceLoaderUtil.getTemplateResource(
						TemplateConstants.LANG_TYPE_FTL, resourcePath);

			Template template = TemplateManagerUtil.getTemplate(
				TemplateConstants.LANG_TYPE_FTL, templateResource, restricted);

			ArrayList<String> themeVariablesNames = new ArrayList<String>(template.keySet());
			renderRequest.setAttribute("themeVariablesNames", themeVariablesNames);
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		super.doView(renderRequest, renderResponse);
	}
}