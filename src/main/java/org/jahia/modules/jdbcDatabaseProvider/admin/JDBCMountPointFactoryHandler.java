/**
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *     Rooted in Open Source CMS, Jahia’s Digital Industrialization paradigm is about
 *     streamlining Enterprise digital projects across channels to truly control
 *     time-to-market and TCO, project after project.
 *     Putting an end to “the Tunnel effect”, the Jahia Studio enables IT and
 *     marketing teams to collaboratively and iteratively build cutting-edge
 *     online business solutions.
 *     These, in turn, are securely and easily deployed as modules and apps,
 *     reusable across any digital projects, thanks to the Jahia Private App Store Software.
 *     Each solution provided by Jahia stems from this overarching vision:
 *     Digital Factory, Workspace Factory, Portal Factory and eCommerce Factory.
 *     Founded in 2002 and headquartered in Geneva, Switzerland,
 *     Jahia Solutions Group has its North American headquarters in Washington DC,
 *     with offices in Chicago, Toronto and throughout Europe.
 *     Jahia counts hundreds of global brands and governmental organizations
 *     among its loyal customers, in more than 20 countries across the globe.
 *
 *     For more information, please visit http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2015 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms & Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 *
 *
 *
 *
 */
package org.jahia.modules.jdbcDatabaseProvider.admin;

import org.jahia.modules.external.admin.mount.AbstractMountPointFactoryHandler;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.utils.i18n.Messages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.RepositoryException;
import java.util.Locale;

/**
 * The type Jdbc mount point factory handler.
 */
public class JDBCMountPointFactoryHandler extends AbstractMountPointFactoryHandler<JDBCMountPointFactory>{
    private static final long serialVersionUID = -1825547537625217141L;
    private static final Logger logger = LoggerFactory.getLogger(JDBCMountPointFactoryHandler.class);
    private static final String BUNDLE = "resources.jdbc-database-provider";

    private JDBCMountPointFactory jdbcMountPointFactory;

    /**
     * Init.
     *
     * @param requestContext the request context
     */
    public void init(RequestContext requestContext) {
        jdbcMountPointFactory = new JDBCMountPointFactory();
        try {
            super.init(requestContext, jdbcMountPointFactory);
        } catch (RepositoryException e) {
            logger.error("Error retrieving mount point", e);
        }
        requestContext.getFlowScope().put("jdbcFactory", jdbcMountPointFactory);
    }

    /**
     * Gets folder list.
     *
     * @return the folder list
     */
    public String getFolderList() {
        JSONObject result = new JSONObject();
        try {
            JSONArray folders = JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<JSONArray>() {
                @Override
                public JSONArray doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    return getSiteFolders(session.getWorkspace());
                }
            });
            result.put("folders", folders);
        } catch (RepositoryException e) {
            logger.error("Error trying to retrieve local folders", e);
        } catch (JSONException e) {
            logger.error("Error trying to construct JSON from local folders", e);
        }

        return result.toString();
    }

    /**
     * Save boolean.
     *
     * @param messageContext the message context
     * @return the boolean
     */
    public Boolean save(MessageContext messageContext) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            boolean available = super.save(jdbcMountPointFactory);
            if(available) {
                return true;
            } else {
                MessageBuilder messageBuilder = new MessageBuilder().warning().defaultText(Messages.get(BUNDLE, "serverSettings.jdbcMountPointFactory.save.unavailable", locale));
                messageContext.addMessage(messageBuilder.build());
            }
        } catch (RepositoryException e) {
            logger.error("Error saving mount point " + jdbcMountPointFactory.getName(), e);
            MessageBuilder messageBuilder = new MessageBuilder().error().defaultText(Messages.get(BUNDLE, "serverSettings.jdbcMountPointFactory.save.error", locale));
            messageContext.addMessage(messageBuilder.build());
        }
        return false;
    }
}
