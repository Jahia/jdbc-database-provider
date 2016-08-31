# Jdbc Database External Provider

### INFORMATION
This module allows you to create a mount point in your Digital Factory in order to search your data on a database table.
Use this module as a tutorial or guide to do your own implementation.

### MINIMAL REQUIREMENTS
* Digital Factory 7.1.0.1
* DF Module - External data provider V3.0.1 
* SQL Server 2014 
* **Important:** to use this module you must create yourself a table in your database before deploying it on your instance.
You will find the script to use [here](https://github.com/Jahia/jdbc-database-provider/blob/master/db/sqlserver-db.sql)

### INSTRUCTIONS
After install the module, you need to create a new "JDBC DATABASE PROVIDER" mount point
with name without blank spaces.