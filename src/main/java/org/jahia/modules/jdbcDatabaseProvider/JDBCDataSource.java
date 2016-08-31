package org.jahia.modules.jdbcDatabaseProvider;

import com.google.common.collect.Sets;
import net.sf.ehcache.Ehcache;
import org.jahia.modules.external.ExternalData;
import org.jahia.modules.external.ExternalDataSource;
import org.jahia.modules.external.ExternalQuery;
import org.jahia.modules.external.query.QueryHelper;
import org.jahia.modules.jdbcDatabaseProvider.to.JDBCActivityTO;
import org.jahia.modules.jdbcDatabaseProvider.utils.JDBCUtils;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


/**
 * The type Jdbc data source.
 */
public class JDBCDataSource implements ExternalDataSource, ExternalDataSource.Writable, ExternalDataSource.Searchable, ExternalDataSource.Initializable {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCDataSource.class);

    // Jdbc Parameters
    private static final String DB_JDBC_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    private String dbUrlConnectionString;
    private String dbUser;
    private String dbPassword;

    // SQL
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    // Cache
    private EhCacheProvider ehCacheProvider;
    private static Ehcache ecache;
    private static final String CACHE_NAME              = "jdbc-cache";
    private static final String CACHE_JDBC_ACTVITIES  = "cacheJDBCActivities";

    // Node types
    private static final String JNT_JDBC_ACTIVITY = "jnt:jdbcSqlActivity";
    private static final String JNT_CONTENT_FOLDER  = "jnt:contentFolder";

    /**
     * The constant ID.
     */

    // Properties : activity
    public static final String ID          = "id";
    /**
     * The constant NAME.
     */
    public static final String NAME        = "name";
    /**
     * The constant DISTANCE.
     */
    public static final String DISTANCE    = "distance";
    /**
     * The constant TYPE.
     */
    public static final String TYPE        = "type";
    /**
     * The constant MOVING_TIME.
     */
    public static final String MOVING_TIME = "moving_time";
    /**
     * The constant START_DATE.
     */
    public static final String START_DATE  = "start_date";

    // Properties : JCR
    private static final String ROOT  = "root";

    // Constants
    private static final String ACTIVITY             = "activity";
    private static final String NB_ACTIVITIES_LOADED = "20";


    private SimpleDateFormat originalFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATETIME_FORMAT);
    private SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * CONSTRUCTOR
     *
     * Instantiates a new Jdbc data source.
     */

    public JDBCDataSource() {}

    /** {@inheritDoc} */ // METHODS
    @Override
    public void start() {}

    /** {@inheritDoc} */
    @Override
    public void stop() {
        try {
            if (ehCacheProvider != null && !ehCacheProvider.getCacheManager().cacheExists(CACHE_NAME)) {
                ehCacheProvider.getCacheManager().removeCache(CACHE_NAME);
            }
            ecache = null;
            closeSqlConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for property 'cache'.
     *
     * @return Value for property 'cache'.
     */
    private Ehcache getCache(){
        if (ecache == null){
            if (!ehCacheProvider.getCacheManager().cacheExists(CACHE_NAME)) {
                ehCacheProvider.getCacheManager().addCache(CACHE_NAME);
            }
            ecache = ehCacheProvider.getCacheManager().getCache(CACHE_NAME);
        }
        return ecache;
    }

    /**
     * Getter for property 'JDBCConnection'.
     *
     * @return Value for property 'JDBCConnection'.
     */
    private Connection getJDBCConnection(){
        if(connection == null) {
            try {
                Class.forName(DB_JDBC_DRIVER);
                connection = DriverManager.getConnection(dbUrlConnectionString, dbUser, dbPassword);
            } catch (ClassNotFoundException cne) {
                LOGGER.error("getJDBCConnection(), Error trying to instantiate driver[{}], {} ", DB_JDBC_DRIVER, cne.toString());
            } catch (SQLException sqle) {
                LOGGER.error("getJDBCConnection(), Error trying to get the connection for url[{}], {} ", dbUrlConnectionString, sqle.toString());
            }
        }
        return connection;
    }

    /**
     * closeSqlConnection
     */
    private void closeSqlConnection(){
        try {
            if (statement != null)  { statement.close();  statement  = null; }
            if (resultSet != null)  { resultSet.close();  resultSet  = null; }
            if (connection != null) { connection.close(); connection = null; }
        } catch (SQLException e) {
            LOGGER.error("closeSqlConnection(), Error trying to close the connection, {} ", e.toString());
        }
    }

    /**
     * Getter for property 'activityFromDB'.
     *
     * @return Value for property 'activityFromDB'.
     */
    private Map<String, JDBCActivityTO> getActivityFromDB(){
        Map<String, JDBCActivityTO> dataMap = new HashMap<String, JDBCActivityTO>();
        JDBCActivityTO to;
        String query = "SELECT id, name, distance, type, moving_time, start_date FROM dbo.Activity";
        try {
            statement = getJDBCConnection().createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                to = new JDBCActivityTO(resultSet);
                dataMap.put(to.getId(), to);
            }
        } catch (SQLException e ) {
            LOGGER.error("getActivityFromDB(), Error trying to get the activities, {} ", e.toString());
        } finally {
            closeSqlConnection();
        }
        return dataMap;
    }

    /**
     * addUpdateActivityDB
     *
     * @param activity
     */
    private void addUpdateActivityDB(JDBCActivityTO activity){
        Map<String, JDBCActivityTO> dataMap = new HashMap<String, JDBCActivityTO>();
        JDBCActivityTO to;
        String queryInsert = " INSERT INTO [dbo].[Activity]" +
                             " (name, distance, type, moving_time, start_date) " +
                             " VALUES " +
                             " ('" + activity.getName() + "', " + activity.getDistance() +
                             ", '" + activity.getType() + "', " + JDBCUtils.getMovingTime(activity.getMovingTime()) +
                             ", '" + activity.getStartDate() + "')" ;

        String queryUpdate = " UPDATE [dbo].[Activity]" +
                             " SET " +
                             " name = '" + activity.getName() + "' , " +
                             " distance = " + activity.getDistance() + ", " +
                             " type = '" + activity.getType() + "', " +
                             " moving_time = " + JDBCUtils.getMovingTime(activity.getMovingTime()) +  ", " +
                             " start_date = '" + activity.getStartDate() + "'" +
                             " WHERE " +
                             " id = " + activity.getId();

        try {
            statement = getJDBCConnection().createStatement();

            if(activity.getId() != null) {
                LOGGER.info("addUpdateActivityDB(), query update[{}] ", queryUpdate);
                statement.executeUpdate(queryUpdate);
            }else{
                LOGGER.info("addUpdateActivityDB(), query insert[{}] ", queryInsert);
                statement.execute(queryInsert);
            }

            getCacheJCDBActivities(true);
        } catch (Exception e ) {
            LOGGER.error("addUpdateActivityDB(), Error trying to get the activities, ", e);
        } finally {
            closeSqlConnection();
        }
    }


    /**
     * Getter for property 'activityMap'.
     *
     * @return Value for property 'activityMap'.
     */
    private Map<String, JDBCActivityTO>  getActivityMap() {
        StringBuilder jsonData = new StringBuilder();
        Map<String, JDBCActivityTO> cacheDataMap = getActivityFromDB();
        try {
            getCache().put(new net.sf.ehcache.Element(CACHE_JDBC_ACTVITIES, cacheDataMap));
            return cacheDataMap;
        }catch(Exception je){
            LOGGER.error("getActivityJsonArray(), Error trying to get the activities map, {} ", je.toString());
        }
        return null;
    }

    /***
     * getCacheJCDBActivities
     *
     * @param deleteCache
     * @return cacheMap
     * @throws RepositoryException
     */
    private Map<String, JDBCActivityTO>  getCacheJCDBActivities(boolean deleteCache) throws RepositoryException {
        Map<String, JDBCActivityTO> activities = new HashMap<String, JDBCActivityTO>();
        if (getCache().get(CACHE_JDBC_ACTVITIES) != null && !deleteCache) {
            activities = (Map<String, JDBCActivityTO>) getCache().get(CACHE_JDBC_ACTVITIES).getObjectValue();
        } else {
            LOGGER.info("Refresh the activities");
            activities = getActivityMap();
        }
        return activities;
    }


    // IMPLEMENTS : ExternalDataSource

    /** {@inheritDoc} */
    @Override
    public List<String> getChildren(String path) throws RepositoryException {
        List<String> r = new ArrayList<>();
        if (path.equals("/")) {
            try {
                Map<String, JDBCActivityTO> activities = getCacheJCDBActivities(true);//getCacheJCDBActivities(false);

                for (String key: activities.keySet()) {
                    //JDBCActivityTO to = activities.get(key);
                    r.add(key + "-" + ACTIVITY);
                }
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
        return r;
    }

    /** {@inheritDoc} */
    @Override
    public ExternalData getItemByIdentifier(String identifier) throws ItemNotFoundException {
        if (identifier.equals(ROOT)) {
            return new ExternalData(identifier, "/", JNT_CONTENT_FOLDER, new HashMap<String, String[]>());
        }
        Map<String, String[]> properties = new HashMap<>();
        String[] idActivity = identifier.split("-");
        if (idActivity.length == 2) {
            try {
                Map<String, JDBCActivityTO> activities = getCacheJCDBActivities(false);
                // Find the activity by its identifier
                String numActivity = idActivity[0];
                JDBCActivityTO activity = activities.get(numActivity);

                // Add some properties
                properties.put(ID,          new String[]{ activity.getId()   });
                properties.put(NAME,        new String[]{ activity.getName() });
                properties.put(TYPE,        new String[]{ activity.getType() });
                properties.put(DISTANCE,    new String[]{ JDBCUtils.displayDistance(activity.getDistance())      });
                properties.put(MOVING_TIME, new String[]{ JDBCUtils.displayMovingTime(activity.getMovingTime()) });
                properties.put(START_DATE,  new String[]{ activity.getStartDate()});
                // Return the external data (a node)
                ExternalData data = new ExternalData(identifier, "/" + identifier, JNT_JDBC_ACTIVITY, properties);
                return data;
            } catch (Exception e) {
                throw new ItemNotFoundException(identifier);
            }
        } else {
            // Node not again created
            throw new ItemNotFoundException(identifier);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ExternalData getItemByPath(String path) throws PathNotFoundException {
        String[] splitPath = path.split("/");
        try {
            if (splitPath.length <= 1) {
                return getItemByIdentifier(ROOT);
            } else {
                return getItemByIdentifier(splitPath[1]);
            }
        } catch (ItemNotFoundException e) {
            throw new PathNotFoundException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getSupportedNodeTypes() {
        return Sets.newHashSet(JNT_CONTENT_FOLDER, JNT_JDBC_ACTIVITY);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportsHierarchicalIdentifiers() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportsUuid() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean itemExists(String path) {
        return false;
    }

    // Implements : ExternalDataSource.Searchable

    /** {@inheritDoc} */
    @Override
    public List<String> search(ExternalQuery query) throws RepositoryException {
        List<String> paths = new ArrayList<>();
        String nodeType = QueryHelper.getNodeType(query.getSource());
        if (NodeTypeRegistry.getInstance().getNodeType(JNT_JDBC_ACTIVITY).isNodeType(nodeType)) {
            try {
                Map<String, JDBCActivityTO> activities = getCacheJCDBActivities(false);
                for (String key: activities.keySet()) {
                    String path = "/" + key + "-" + ACTIVITY ;
                    paths.add(path);
                }

            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
        return paths;
    }

    // Implements : ExternalDataSource.Writable

    /** {@inheritDoc} */
    @Override
    public void move(String oldPath, String newPath) throws RepositoryException {
        LOGGER.info("Move : oldPath=" + oldPath + " newPath=" + newPath);
    }

    /** {@inheritDoc} */
    @Override
    public void order(String path, List<String> children) throws RepositoryException {
        LOGGER.info("Order : path=" + path);
    }

    /** {@inheritDoc} */
    @Override
    public void removeItemByPath(String path) throws RepositoryException {
        LOGGER.info("Remove item by path : path=" + path);
    }

    /** {@inheritDoc} */
    @Override
    public void saveItem(ExternalData data) throws RepositoryException {
        try {

            Map<String, String[]> activityProps = data.getProperties();
            String activityId = activityProps.containsKey(ID) ? activityProps.get(ID)[0] : "";
            boolean isNew = true;

            JDBCActivityTO activityTO = new JDBCActivityTO(activityProps);
            addUpdateActivityDB(activityTO);
            LOGGER.info("saveItem(), Done......");

        }catch(Exception e){
            LOGGER.error("saveItem(), can't save the item, ", e);
        }
    }


    // GETTERS AND SETTERS

    /**
     * Gets db url connection string.
     *
     * @return the db url connection string
     */
    public String getDbUrlConnectionString() {
        return dbUrlConnectionString;
    }

    /**
     * Sets db url connection string.
     *
     * @param dbUrlConnectionString the db url connection string
     */
    public void setDbUrlConnectionString(String dbUrlConnectionString) {
        this.dbUrlConnectionString = dbUrlConnectionString;
    }

    /**
     * Gets db user.
     *
     * @return the db user
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Sets db user.
     *
     * @param dbUser the db user
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * Gets db password.
     *
     * @return the db password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Sets db password.
     *
     * @param dbPassword the db password
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
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
