/**
 * 
 */
package com.jmm.database.process.domain;

/**
 * @author jayaram
 * 
 */
public class DatabaseConfig {

	private static String CONNECTION_PROTOCOL = "jdbc:sqlserver://";
	private static String DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private String databaseName;
	private String machineName;
	private String portNumber;
	private String dbUserName;
	private String dbPassword;
	private boolean isSQLAuthenticated;
	
	/**
	 * 
	 * @return
	 */
	public String getConnectionProtocol(){
		return CONNECTION_PROTOCOL;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDriverName(){
		return DRIVER_NAME;
	}
	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName
	 *            the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}

	/**
	 * @param machineName
	 *            the machineName to set
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	/**
	 * @return the portNumber
	 */
	public String getPortNumber() {
		return portNumber;
	}

	/**
	 * @param portNumber
	 *            the portNumber to set
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * @return the dbUserName
	 */
	public String getDbUserName() {
		return dbUserName;
	}

	/**
	 * @param dbUserName
	 *            the dbUserName to set
	 */
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	/**
	 * @return the dbPassword
	 */
	public String getDbPassword() {
		return dbPassword;
	}

	/**
	 * @param dbPassword
	 *            the dbPassword to set
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	/**
	 * @return the isSQLAuthenticated
	 */
	public boolean isSQLAuthenticated() {
		return isSQLAuthenticated;
	}

	/**
	 * @param isSQLAuthenticated
	 *            the isSQLAuthenticated to set
	 */
	public void setSQLAuthenticated(boolean isSQLAuthenticated) {
		this.isSQLAuthenticated = isSQLAuthenticated;
	}
}
