package org.jahia.modules.jdbcDatabaseProvider;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.modules.external.ExternalContentStoreProvider;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRStoreProvider;
import org.jahia.services.content.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import javax.jcr.RepositoryException;
import java.util.Arrays;

/**
 * The type Jdbc provider factory.
 */
public class JDBCProviderFactory implements ProviderFactory, ApplicationContextAware, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(JDBCProviderFactory.class);

    private ApplicationContext applicationContext;

    private EhCacheProvider ehCacheProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeTypeName() {
        return "jnt:jdbcSqlMountPoint";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCRStoreProvider mountProvider(JCRNodeWrapper mountPoint) throws RepositoryException {
        if (log.isDebugEnabled()) {
            log.info("JDBC MOUNT POINT INITIATING ... ");
            log.info("mountPoint.getPath() - " + mountPoint.getPath() + ", name - " + mountPoint.getName());
        }

        //Define the provider basing on the mount point parameters
        ExternalContentStoreProvider provider = (ExternalContentStoreProvider) SpringContextSingleton.getBean("ExternalStoreProviderPrototype");
        provider.setKey(mountPoint.getIdentifier());
        provider.setMountPoint(mountPoint.getPath());

        //Define the datasource using the credentials defined in the mount point
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDbUser(mountPoint.getProperty("user").getString());
        dataSource.setDbPassword(mountPoint.getProperty("password").getString());
        dataSource.setDbUrlConnectionString(mountPoint.getProperty("url").getString());
        dataSource.setCacheProvider(ehCacheProvider);

        //Start the datasource
        dataSource.start();
        //Finalize the provider setup with datasource and some JCR parameters
        provider.setDataSource(dataSource);
        provider.setOverridableItems(Arrays.asList("jmix:description.*", "jmix:i18n.*"));
        provider.setReservedNodes(Arrays.asList("j:acl", "j:workflowRules"));
        provider.setNonExtendableMixins(Arrays.asList("jmix:image"));
        provider.setDynamicallyMounted(true);
        provider.setSessionFactory(JCRSessionFactory.getInstance());
        try {
            provider.start();
        } catch (JahiaInitializationException e) {
            throw new RepositoryException("Exception on provider start !", e);
        }
        if (log.isDebugEnabled()) {
            log.info("***************** JDBC MOUNT POINT INITIATED **********************");
        }
        return provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * Sets cache provider.
     *
     * @param ehCacheProvider the eh cache provider
     */
    public void setCacheProvider(EhCacheProvider ehCacheProvider) {
        this.ehCacheProvider = ehCacheProvider;
    }
}
