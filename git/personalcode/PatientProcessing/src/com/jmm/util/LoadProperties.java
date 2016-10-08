/**
 * 
 */
package com.jmm.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jmm.log.LogFormatter;


/**
 * @author jayaram
 * 
 */
public class LoadProperties {

	/**
	 * 
	 */
	private String machineName;
	private String databaseName;
	private String portNumber;
	private String databaseUserName;
	private String databasePassword;
	private boolean isSqlAuthentication;
	private String logFilePath;
	private String loggingLevel;
	//private final String CONFIG_PROPERTIES_PATH="resources/config.properties";
	private final String CONFIG_PROPERTIES_PATH="config.properties";
	private boolean isLogFileAppend;
	private float samplingRate;
	
	/**
	 * This constructor will help in loading the properties file. By default,we
	 * would be picking from "config.properties." This file must be present in
	 * the classpath.
	 * 
	 * @throws IOException
	 */
	public LoadProperties() throws IOException {

		
		Properties prop = new Properties();
		InputStream input = null;
		// Logger logger = Logger.getLogger("LoadProperties");

		try {
			input = this.getClass().getClassLoader()
                    .getResourceAsStream(CONFIG_PROPERTIES_PATH);
			//input = new FileInputStream(CONFIG_PROPERTIES_PATH);
		/*	if(input==null){
				System.out.println("Error loading the configuration file.Skipping");
				return;
			}*/
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			
			String logFilePath=prop.getProperty("logFilePath");
			String logFileName = "log_"
					+ (new SimpleDateFormat("MM_dd_yyyy"))
							.format(new Date()) + ".log";
			String completeLogFileName=logFilePath+File.separator+logFileName;
			this.setLogFilePath(completeLogFileName);
			this.setLoggingLevel(prop.getProperty("loggingLevel"));
			this.setLoggingLevelAppend(("yes".equalsIgnoreCase(prop.getProperty("logFileAppend"))) ? true : false);
			// logger.finest("LoggingLevel set to "+getLoggingLevel());
			this.setMachineName(prop.getProperty("machineName"));
			// logger.finest("Machine name is "+getMachineName());
			this.setPortNumber(prop.getProperty("portNumber"));
			// logger.finest("Port number is "+getPortNumber());
			this.setDatabaseName(prop.getProperty("databaseName"));
			// logger.finest("The Database name is "+getDatabaseName());
			this.setDatabaseUserName(prop.getProperty("dbuser"));
			// logger.finest("The Database userName is "+getDatabaseUserName());
			this.setDatabasePassword(prop.getProperty("dbpassword"));
			// logger.finest("The Database Password is "+getDatabasePassword());
			this.setSqlAuthentication(("yes".equalsIgnoreCase(prop
					.getProperty("sqlAuthentication")) ? true : false));
			this.setSamplingRate(Float.parseFloat(prop.getProperty("samplingRate")));
			// logger.finest("SqlAuthentication set to "+isSqlAuthentication);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public float getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(float samplingRate) {
		this.samplingRate = samplingRate;
	}

	/**
	 * @description: Sets the initial configurations which are required to set the 
	 * 	Logging properties 
	 * @param logger
	 * @return int err: the error code is returned back to the caller
	 */
	public int setLoggingConfigurations(Logger logger) {
		int err = 0;
		try {
			FileHandler fh = new FileHandler(logFilePath,isLogFileAppend);
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			LogFormatter formatter = new LogFormatter();
			// SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			Level level;
			try {
				level = Level.parse(loggingLevel.toUpperCase());
			} catch (Exception e) {
				level = Level.INFO;
			}
			logger.setLevel(level);
		} catch (SecurityException e2) {
			logger.severe("Error Found in security");
			err = 54;
		} catch (IOException e2) {
			logger.severe("Error Found in accesing Log file");
			err = 54;
		}
		return err;
	}

	/**
	 * @param machineName
	 *            the machineName to set
	 */
	private void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	/**
	 * @param databaseName
	 *            the databaseName to set
	 */
	private void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @param portNumber
	 *            the portNumber to set
	 */
	private void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * @param databaseUserName
	 *            the databaseUserName to set
	 */
	private void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}

	/**
	 * @param databasePassword
	 *            the databasePassword to set
	 */
	private void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}

	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @return the portNumber
	 */
	public String getPortNumber() {
		return portNumber;
	}

	/**
	 * @return the databaseUserName
	 */
	public String getDatabaseUserName() {
		return databaseUserName;
	}

	/**
	 * @return the databasePassword
	 */
	public String getDatabasePassword() {
		return databasePassword;
	}

	/**
	 * @return the isSqlAuthentication
	 */
	public boolean isSqlAuthentication() {
		return isSqlAuthentication;
	}

	/**
	 * @param isSqlAuthentication
	 *            the isSqlAuthentication to set
	 */
	private final void setSqlAuthentication(boolean isSqlAuthentication) {
		this.isSqlAuthentication = isSqlAuthentication;
	}

	/**
	 * @return the logFilePath
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @param logFilePath
	 *            the logFilePath to set
	 */
	private void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @return the loggingLevel
	 */
	public String getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * @param loggingLevel
	 *            the loggingLevel to set
	 */
	private void setLoggingLevel(String loggingLevel) {
		this.loggingLevel = loggingLevel;
	}
	

	private void setLoggingLevelAppend(boolean isLogFileAppend) {
		this.isLogFileAppend=isLogFileAppend;
		
	}
}
