package org.jahia.modules.jdbcDatabaseProvider.to;

import org.jahia.modules.jdbcDatabaseProvider.JDBCDataSource;
import org.jahia.utils.DateUtils;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Juan Carlos Rodas on 6/04/2016.
 */
public class JDBCActivityTO {

    private String id;
    private String name;
    private String distance;
    private String type;
    private String movingTime;
    private String startDate;
    private SimpleDateFormat originalFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATETIME_FORMAT);


    /**
     * Instantiates a new Jdbc activity to.
     *
     * @param activityProps the activity props
     */
    public JDBCActivityTO( Map<String, String[]> activityProps){
        try { this.id = activityProps.containsKey(JDBCDataSource.ID) ? activityProps.get(JDBCDataSource.ID)[0] : null; }catch(Exception e){}
        try { this.name = activityProps.containsKey(JDBCDataSource.NAME) ? activityProps.get(JDBCDataSource.NAME)[0] : ""; }catch(Exception e){}
        try { this.distance = activityProps.containsKey(JDBCDataSource.DISTANCE) ? activityProps.get(JDBCDataSource.DISTANCE)[0] : ""; }catch(Exception e){}
        try { this.type = activityProps.containsKey(JDBCDataSource.TYPE) ? activityProps.get(JDBCDataSource.TYPE)[0] : ""; }catch(Exception e){}
        try { this.movingTime = activityProps.containsKey(JDBCDataSource.MOVING_TIME) ? activityProps.get(JDBCDataSource.MOVING_TIME)[0] : "0"; }catch(Exception e){}
        try {
            if(activityProps.containsKey(JDBCDataSource.START_DATE)) {
                this.startDate  = activityProps.get(JDBCDataSource.START_DATE)[0];
                // this.startDate = activityProps.containsKey(JDBCDataSource.START_DATE) ? activityProps.get(JDBCDataSource.START_DATE)[0].split("T")[0] : "";
            }

        }catch(Exception e){}
    }


    /**
     * Instantiates a new Jdbc activity to.
     *
     * @param resultSet the result set
     */
    public JDBCActivityTO(ResultSet resultSet){
        try { this.id = resultSet.getInt(JDBCDataSource.ID) + ""; }catch(Exception e){}
        try { this.name = resultSet.getString(JDBCDataSource.NAME); }catch(Exception e){}
        try { this.distance = resultSet.getBigDecimal(JDBCDataSource.DISTANCE) + ""; }catch(Exception e){}
        try { this.type = resultSet.getString(JDBCDataSource.TYPE)  + ""; }catch(Exception e){}
        try { this.movingTime = resultSet.getInt(JDBCDataSource.MOVING_TIME)  + ""; }catch(Exception e){}
        try { this.startDate  = originalFormat.format(resultSet.getDate(JDBCDataSource.START_DATE)); }catch(Exception e){}
    }


    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'distance'.
     *
     * @return Value for property 'distance'.
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Setter for property 'distance'.
     *
     * @param distance Value to set for property 'distance'.
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * Getter for property 'type'.
     *
     * @return Value for property 'type'.
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for property 'type'.
     *
     * @param type Value to set for property 'type'.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for property 'movingTime'.
     *
     * @return Value for property 'movingTime'.
     */
    public String getMovingTime() {
        return movingTime;
    }

    /**
     * Setter for property 'movingTime'.
     *
     * @param movingTime Value to set for property 'movingTime'.
     */
    public void setMovingTime(String movingTime) {
        this.movingTime = movingTime;
    }

    /**
     * Getter for property 'startDate'.
     *
     * @return Value for property 'startDate'.
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Setter for property 'startDate'.
     *
     * @param startDate Value to set for property 'startDate'.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    /**
     * To json string string.
     *
     * @return the string
     */
    public String toJsonString(){
        return "{" +
                "    \"id\": " + (this.id != null ? this.id : "") +",\n" +
                "    \"name\": \"" + (this.name != null ? this.name : "") + "\",\n" +
                "    \"type\": \"" + (this.type != null ? this.type : "") + "\",\n" +
                "    \"distance\": " + (this.distance != null ? this.distance : "") + ",\n" +
                "    \"moving_time\": " + (this.movingTime != null ? this.movingTime : "") + ",\n" +
                "    \"start_date\": \"" + (this.startDate != null ? this.startDate: "") + "\"\n" +
                "  }";
    }


}
