/**
 * 
 */
package com.jmm.database.process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.jmm.database.process.domain.DatabaseConfig;
import com.jmm.database.process.domain.DatabaseDomain;
import com.jmm.home.AlertNorms;
import com.jmm.home.PatientProcessingFrame;
import com.jmm.report.domain.PatientInfo;
import com.jmm.util.Constants;
import com.jmm.util.ErrorCodes;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 * @author jayaram
 * 
 */

public class DatabaseCallHelper {

	// Setting these variables in such a way to avoid variables to be passed in
	// method calls
	private static DatabaseConfig dbConfig = PatientProcessingFrame.dbConfig;
	private static Logger logger = PatientProcessingFrame.logger;

	private MWNumericArray angleCalibrationFromDataBase;
	private MWNumericArray gyroCalibrationFromDataBase;
	private MWNumericArray accelerationCalibrationFromDataBase;
	private MWNumericArray treeDataFromDataBase;
	private Map<String, Double> previousWeekActivityListMap;

	/**
	 * A Static method which returns the connection object and err number.
	 * 
	 * @return Connection object
	 */
	private static Object[] getDataBaseConnection() {
		logger.finest("Start PatientProcessingFrame.getDataBaseConnection()");
		System.out.println("Machine name" + dbConfig.getMachineName());
		logger.finest("Db Machine Name "+dbConfig.getMachineName());
		logger.finest("Db Port Number "+dbConfig.getPortNumber());
		logger.finest("Db Database Name "+dbConfig.getMachineName());
		Object[] returnObject = new Object[2];
		int err = 0;
		Connection conn = null;
		// Now we need to connect to the database
		if (dbConfig.getMachineName().isEmpty()) {
			err = 55;
		} else {
			try {
				String connectionString = dbConfig.getConnectionProtocol()
						+ dbConfig.getMachineName() + ":"
						+ dbConfig.getPortNumber() + ";databaseName="
						+ dbConfig.getDatabaseName();
				if (!dbConfig.isSQLAuthenticated()) {
					connectionString += ";integratedSecurity=true";
				} else {
					connectionString += ";user=" + dbConfig.getDbUserName()
							+ ";password=" + dbConfig.getDbPassword();
				}
				logger.finest("The connection String is "
						+ connectionString);
				conn = DriverManager.getConnection(connectionString);
				// System.out.println("Connected to database from java");
			} catch (SQLException e) {
				err = 37;
			}
		}
		returnObject[0] = conn;
		returnObject[1] = err;
		logger.finest("End PatientProcessingFrame.getDataBaseConnection()");
		return returnObject;
	}

	public DatabaseDomain loadDataFromDatabase(String sensorId,
			Date measurementDate) {
		logger.finest("Start : PatientProcessingFrame.loadDataFromDatabase()");
		logger.info("Loading from Database for sensorID..." + sensorId);
		DatabaseDomain domainObj = new DatabaseDomain();

		// int err = 0;
		// First we need to load the JDBC driver
		try {
			Class.forName(dbConfig.getDriverName());
		} catch (ClassNotFoundException e) {
			// err = 36;
			domainObj.setErr(36);
			return domainObj;
		}
		Object[] returnObject = getDataBaseConnection();
		Connection conn = (Connection) returnObject[0];
		int err = (Integer) returnObject[1];
		if (err != 0) {
			domainObj.setErr(err);
			return domainObj;
		}

		Statement stmt;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e2) {
			err = 39;
			domainObj.setErr(err);
			return domainObj;
		}

		// Now we populate the patient info
		PatientInfo patientInfo = new PatientInfo();

		// First we need to find the sensor ID from the address
		//patientInfo.setSensorId(-1);

		// Next get the patient ID
		long patientId = -1;
		// TODO: Currently Hard coding till data is present in the database
		//sensorId = "20120";
		patientInfo.setSensorId(sensorId);
		String query = "select patientId,sensorId from patientSensor where sensorId="
				+"'"+ sensorId+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (patientId != -1) {
					err = 42;
					domainObj.setErr(err);
					logger.severe("Error Found with Error code " + err);
					return domainObj;
				}
				patientId = rs.getInt("patientId");
				patientInfo.setSensorId(rs.getString("sensorId"));
			}
		} catch (SQLException e1) {
			err = 41;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}
		if (patientId < 0) {
			err = 41;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		query = "select patientFirstName, doctorFirstName, doctorPractiseName, plannedSurgeryDate,patientStudySide from patient where patientId="
				+ patientId;
		try {
			ResultSet rs = stmt.executeQuery(query);
			boolean gotData = false;
			while (rs.next()) {
				if (gotData) {
					err = 44;
					domainObj.setErr(err);
					return domainObj;
				}
				patientInfo.setPatientID(String.valueOf(patientId));
				patientInfo.setName(rs.getString("patientFirstName"));
				patientInfo.setSurgeonName(rs.getString("doctorFirstName"));
				patientInfo.setInstitutionName(rs
						.getString("doctorPractiseName"));
				patientInfo.setDateOfSurgery(rs.getDate("plannedSurgeryDate"));
				patientInfo.setStudySide(rs.getString("patientStudySide"));
				gotData = true;
			}
			if (!gotData) {
				err = 43;
				domainObj.setErr(err);
				logger.severe("Error Found with Error code " + err);
				return domainObj;
			}
		} catch (SQLException e1) {
			err = 43;
			domainObj.setErr(err);
			return domainObj;
		}

		// Calculate the weeks since surgery
		// System.out.println("measurementDate " + measurementDate);
		// System.out.println("patientInfo.dateOfSurgery "
		// + patientInfo.dateOfSurgery);
		long timeDifference = measurementDate.getTime()
				- patientInfo.getDateOfSurgery().getTime();
		patientInfo
				.setDaysSinceSurgery((int) (timeDifference / (1000 * 60 * 60 * 24)));
		/*
		 * System.out .println("Days since surgery " +
		 * patientInfo.daysSinceSurgery);
		 */
		int weeksSinceSurgery = (int) Math.ceil((double) patientInfo
				.getDaysSinceSurgery() / 7.0);

		// TODO: Currently setting value to accept at most 6
		logger.fine("Weeks" + weeksSinceSurgery);
		if (weeksSinceSurgery > 6) {
			weeksSinceSurgery = 6;
		}
		patientInfo.setWeekSinceSurgery(weeksSinceSurgery);
		if (patientInfo.getDaysSinceSurgery() > 0) {
			String postOperationDays = patientInfo.getDaysSinceSurgery()
					+ " days post TKA";
			patientInfo.setPostOperationDays(postOperationDays);
		} else {
			String postOperationDays = patientInfo.getDaysSinceSurgery()
					+ " days pre TKA";
			patientInfo.setPostOperationDays(postOperationDays);
		}
		// TODO: Need to find database values for weeks less than 1
		query = "select treeId from WeekTree where week=" + weeksSinceSurgery;

		try {
			ResultSet rs = stmt.executeQuery(query);
			boolean gotData = false;
			while (rs.next()) {
				if (gotData) {
					err = 46;
					domainObj.setErr(err);
					logger.severe("Error Found with Error code " + err);
					return domainObj;
				}
				patientInfo.setTreeId(rs.getString("treeId"));
				gotData = true;
			}
			if (!gotData) {
				err = 45;
				domainObj.setErr(err);
				logger.severe("Error Found with Error code " + err);
				return domainObj;
			}
		} catch (SQLException e1) {
			err = 45;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		query = "select reportDate, maxFlexion, maxExtension, excursionsPerHour, modalExcursion,"
				+ "numberOfHoursLying, numberOfHoursSitting, numberOfHoursStanding, numberOfHoursWalking, numberOfHoursUsingStairsUp,numberOfHoursUsingStairsDown, numberOfHoursExercise, numberOfHoursUsingBike "
				+ "from Report where patientId="
				+ patientId
				+ " and week="
				+ (weeksSinceSurgery - 1);
		// Initializing
		previousWeekActivityListMap = new HashMap<String, Double>();
		previousWeekActivityListMap.put(Constants.LYING, (double) 0);
		previousWeekActivityListMap.put(Constants.SITTING, (double) 0);
		previousWeekActivityListMap.put(Constants.STANDING, (double) 0);
		previousWeekActivityListMap.put(Constants.WALKING, (double) 0);
		previousWeekActivityListMap.put(Constants.STAIRS_UP, (double) 0);
		previousWeekActivityListMap.put(Constants.STAIRS_DOWN, (double) 0);
		previousWeekActivityListMap.put(Constants.EXERCISE, (double) 0);
		previousWeekActivityListMap.put(Constants.BIKE, (double) 0);
		previousWeekActivityListMap.put(Constants.UNKNOWN, (double) 0);
		patientInfo
				.setPreviousKneeValues(new int[patientInfo.KNEE_MEASUREMENTS]);

		try {
			ResultSet rs = stmt.executeQuery(query);
			Date latestDate = null;
			while (rs.next()) {
				Date reportDate = rs.getDate("reportDate");
				// Need to check the below condition and see if this needs to be
				// modified.
				if (latestDate == null || latestDate.before(reportDate)) {
					latestDate = reportDate;
					previousWeekActivityListMap.put(Constants.LYING,
							rs.getDouble("numberOfHoursLying"));
					previousWeekActivityListMap.put(Constants.SITTING,
							rs.getDouble("numberOfHoursSitting"));
					previousWeekActivityListMap.put(Constants.STANDING,
							rs.getDouble("numberOfHoursStanding"));
					previousWeekActivityListMap.put(Constants.WALKING,
							rs.getDouble("numberOfHoursWalking"));
					previousWeekActivityListMap.put(Constants.STAIRS_UP,
							rs.getDouble("numberOfHoursUsingStairsUp"));
					previousWeekActivityListMap.put(Constants.STAIRS_UP,
							rs.getDouble("numberOfHoursUsingStairsDown"));
					previousWeekActivityListMap.put(Constants.EXERCISE,
							rs.getDouble("numberOfHoursExercise"));
					previousWeekActivityListMap.put(Constants.BIKE,
							rs.getDouble("numberOfHoursUsingBike"));
					patientInfo.getPreviousKneeValues()[0] = (int) (rs
							.getDouble("maxFlexion"));
					patientInfo.getPreviousKneeValues()[1] = (int) rs
							.getDouble("maxExtension");
					patientInfo.getPreviousKneeValues()[2] = (int) rs
							.getDouble("excursionsPerHour");
					patientInfo.getPreviousKneeValues()[3] = (int) rs
							.getDouble("modalExcursion");
				}
			}
		} catch (SQLException e1) {
			err = 47;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		query = "select flexionMin, flexionMax, extensionMin, extensionMax,"
				+ "excursionsMin, excursionsMax, modalMin, modalMax "
				+ "from DefaultKneeValues where week=" + weeksSinceSurgery;

		patientInfo.setMinKneeValues(new int[patientInfo.KNEE_MEASUREMENTS]);
		patientInfo.setMaxKneeValues(new int[patientInfo.KNEE_MEASUREMENTS]);

		try {
			ResultSet rs = stmt.executeQuery(query);
			boolean gotData = false;
			while (rs.next()) {
				if (gotData) {
					err = 49;
					domainObj.setErr(err);
					logger.severe("Error Found with Error code " + err);
					return domainObj;
				}

				patientInfo.getMinKneeValues()[0] = (int) rs
						.getDouble("flexionMin");
				patientInfo.getMaxKneeValues()[0] = (int) rs
						.getDouble("flexionMax");
				patientInfo.getMinKneeValues()[1] = (int) rs
						.getDouble("extensionMin");
				patientInfo.getMaxKneeValues()[1] = (int) rs
						.getDouble("extensionMax");
				patientInfo.getMinKneeValues()[2] = (int) rs
						.getDouble("excursionsMin");
				patientInfo.getMaxKneeValues()[2] = (int) rs
						.getDouble("excursionsMax");
				patientInfo.getMinKneeValues()[3] = (int) rs
						.getDouble("modalMin");
				patientInfo.getMaxKneeValues()[3] = (int) rs
						.getDouble("modalMax");

				gotData = true;
			}
			if (!gotData) {
				err = 48;
				domainObj.setErr(err);
				logger.severe("Error Found with Error code " + err);
				return domainObj;
			}
		} catch (SQLException e1) {
			err = 48;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		query = "select reportDate from Report where patientId = " + patientId;

		patientInfo.setPreviousReportsString("");
		;

		List<Integer> daysSinceReports = new ArrayList<Integer>();

		try {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Date reportDate = rs.getDate("reportDate");

				if (!patientInfo.getPreviousReportsString().equals(""))
					patientInfo.setPreviousReportsString(patientInfo
							.getPreviousReportsString() + ", ");

				patientInfo.setPreviousReportsString(patientInfo
						.getPreviousReportsString()
						+ (new SimpleDateFormat("MM/dd")).format(reportDate));

				long reportTimeDifference = reportDate.getTime()
						- patientInfo.getDateOfSurgery().getTime();
				daysSinceReports
						.add(((int) (reportTimeDifference / (1000 * 60 * 60 * 24))));
			}
		} catch (SQLException e1) {
			err = 50;
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		if (daysSinceReports.size() > 0) {
			patientInfo.setPreviousReportsString(patientInfo
					.getPreviousReportsString() + " (");

			for (int i = 0; i < daysSinceReports.size(); i++) {
				if (i > 0)
					patientInfo.setPreviousReportsString(patientInfo
							.getPreviousReportsString() + "/");
				patientInfo.setPreviousReportsString(patientInfo
						.getPreviousReportsString() + daysSinceReports.get(i));
			}

			patientInfo.setPreviousReportsString(patientInfo
					.getPreviousReportsString() + " days post-op)");
		}
		err = getCalibrationSpecificDataFromDataBase(stmt, patientInfo);
		String treeFilePath = "";

		if (err == 0) {
			Object returnObj[] = getTreeDataFromDataBase(stmt, patientInfo);
			treeFilePath = (String) returnObj[0];
			err = (Integer) returnObj[1];
		} else {
			domainObj.setErr(err);
			logger.severe("Error Found with Error code " + err);
			return domainObj;
		}

		domainObj
				.setAccelerationCalibrationFromDataBase(accelerationCalibrationFromDataBase);
		domainObj.setAngleCalibrationFromDataBase(angleCalibrationFromDataBase);
		domainObj.setGyroCalibrationFromDataBase(gyroCalibrationFromDataBase);
		domainObj.setTreeDataFromDataBase(treeDataFromDataBase);
		domainObj.setTreeFilePath(treeFilePath);
		
		patientInfo.setPreviousActivityDurationMap(previousWeekActivityListMap);
		domainObj.setPatientInfo(patientInfo);
		domainObj.setErr(err);
		try {
			conn.close();
		} catch (SQLException e) {
			// This is not a big deal for the report so just move on
		}
		// System.out.println("Done");
		logger.finest("End : PatientProcessingFrame.loadDataFromDatabase()");
		return domainObj;
	}

	/**
	 * 
	 * @param stmt
	 * @param patientInfo
	 * @return int error code
	 */
	private int getCalibrationSpecificDataFromDataBase(Statement stmt,
			PatientInfo patientInfo) {
		// TODO below 2 lines needs to removed after initial testing.
		String temp = patientInfo.getSensorId();
		patientInfo.setSensorId("20121");

		int err = 0;
		String queryStringForCalibration = "select clockwiseKneeAngleC1, clockwiseKneeAngleC2, counterClockwiseKneeAngleC1,counterClockwiseKneeAngleC2, accelerationXOffset,"
				+ "accelerationXGain, accelerationYOffset, accelerationYGain, accelerationZOffset, accelerationZGain,gyroXGain,gyroXOffset, gyroYGain, gyroYOffset, gyroZGain, gyroZOffset from Calibration where sensorId = '"
				+ String.valueOf(patientInfo.getSensorId())
				+ "' and calibrationDate >= ALL (select calibrationDate from Calibration where sensorId = '"
				+ String.valueOf(patientInfo.getSensorId()) + "');";
		// TODO: needs to be removed once testing is done
		patientInfo.setSensorId(temp);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(queryStringForCalibration);
		} catch (SQLException e) {
			err = 52;
			logger.severe("Error Found with Error code " + err);
		}
		try {
			while (rs.next()) {
				int[] dims = { 2, 2 };
				angleCalibrationFromDataBase = MWNumericArray.newInstance(dims,
						MWClassID.DOUBLE, MWComplexity.REAL);
				int[] dims2 = { 3, 2 };
				accelerationCalibrationFromDataBase = MWNumericArray
						.newInstance(dims2, MWClassID.DOUBLE, MWComplexity.REAL);
				gyroCalibrationFromDataBase = MWNumericArray.newInstance(dims2,
						MWClassID.DOUBLE, MWComplexity.REAL);
				angleCalibrationFromDataBase.set(1,
						rs.getDouble("clockwiseKneeAngleC1"));
				angleCalibrationFromDataBase.set(3,
						rs.getDouble("clockwiseKneeAngleC2"));
				angleCalibrationFromDataBase.set(2,
						rs.getDouble("counterClockwiseKneeAngleC1"));
				angleCalibrationFromDataBase.set(4,
						rs.getDouble("counterClockwiseKneeAngleC2"));
				accelerationCalibrationFromDataBase.set(1,
						rs.getDouble("accelerationXGain"));
				accelerationCalibrationFromDataBase.set(2,
						rs.getDouble("accelerationYGain"));
				accelerationCalibrationFromDataBase.set(3,
						rs.getDouble("accelerationZGain"));
				accelerationCalibrationFromDataBase.set(4,
						rs.getDouble("accelerationXOffset"));
				accelerationCalibrationFromDataBase.set(5,
						rs.getDouble("accelerationYOffset"));
				accelerationCalibrationFromDataBase.set(6,
						rs.getDouble("accelerationZOffset"));
				gyroCalibrationFromDataBase.set(1, rs.getDouble("gyroXGain"));
				gyroCalibrationFromDataBase.set(2, rs.getDouble("gyroYGain"));
				gyroCalibrationFromDataBase.set(3, rs.getDouble("gyroZGain"));
				gyroCalibrationFromDataBase.set(4, rs.getDouble("gyroXOffset"));
				gyroCalibrationFromDataBase.set(5, rs.getDouble("gyroYOffset"));
				gyroCalibrationFromDataBase.set(6, rs.getDouble("gyroZOffset"));
				

			}
		} catch (SQLException e) {
			err = 52;
			logger.severe("Error Found with Error code " + err);
		}
		return err;
	}

	/**
	 * 
	 * @param stmt
	 * @param patientInfo
	 * @return
	 */
	private Object[] getTreeDataFromDataBase(Statement stmt,
			PatientInfo patientInfo) {
		//TODO Need to remove samplingRate, windowSize,saxWindowWidth,saxCardinality as it is fetched from tree File
		String queryStringForTree = "select filePath, samplingRate, windowSize,saxWindowWidth,saxCardinality "
				+ "from Tree where treeId='" + patientInfo.getTreeId() + "'";
		int err = 0;
		String treeFilePath = "";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(queryStringForTree);
			while (rs.next()) {
				int[] dims = { 1, 4 };
				treeFilePath = rs.getString("filePath");
				treeDataFromDataBase = MWNumericArray.newInstance(dims,
						MWClassID.DOUBLE, MWComplexity.REAL);
				treeDataFromDataBase.set(1, rs.getDouble("samplingRate"));
				treeDataFromDataBase.set(2, rs.getDouble("windowSize"));
				treeDataFromDataBase.set(3, rs.getDouble("saxWindowWidth"));
				treeDataFromDataBase.set(4, rs.getDouble("saxCardinality"));
			}
		} catch (SQLException e) {
			err = 53;
			logger.severe("Error Found with Error code " + err);
		}
		Object[] returnObject = new Object[2];
		returnObject[0] = treeFilePath;
		returnObject[1] = err;
		return returnObject;
	}

	/**
	 * This method fetches the AlertNormValues for the week weeksSincSurgery.
	 * 
	 * @param weeksSinceSurgery
	 * @return AlertNorms object which is a Model object containing the values
	 *         from the database
	 */
	public AlertNorms getAlertNormValuesForWeekFromDB(int weeksSinceSurgery) {
		Object[] returnObject = getDataBaseConnection();
		Connection conn = (Connection) returnObject[0];
		int err = (Integer) returnObject[1];
		if (conn == null) {
			logger.warning("Cannot fetch from Database");
			return null;
		}
		Statement stmt;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e2) {
			err = 39;
			logger.severe("Error Found with Error code " + err);
			return null;
		}
		AlertNorms alertNormValues = new AlertNorms();
		try {
			ResultSet rs = stmt
					.executeQuery("select * from AlertNorms where week ="
							+ weeksSinceSurgery);
			while (rs.next()) {
				alertNormValues.setWeek(weeksSinceSurgery);
				alertNormValues.setMaximumInactivitiyHours(rs
						.getFloat("maximumInactivitiyHours"));
				alertNormValues.setMinimumExerciseHours(rs
						.getFloat("minimumExerciseHours"));
				alertNormValues.setMinimumSatisfaction(rs
						.getFloat("minimumSatisfaction"));
				alertNormValues.setIsExpectedFeverChillsSweat(rs
						.getString("isExpectedFeverChillsSweat"));
				alertNormValues.setIsExpectedPainInMedication(rs
						.getString("isExpectedPainInMedication"));
				alertNormValues.setIsExpectedSideEffects(rs
						.getString("isExpectedSideEffects"));
				alertNormValues.setMaximumPainRating(rs
						.getFloat("maximumPainRating"));
				// This Field is removed.
				// alertNormValues.setIsPainExpected(rs.getString("isPainExpected"));
				alertNormValues.setMinimumPerceivedStability(rs
						.getFloat("minimumPerceivedStability"));
				alertNormValues.setIsFallingExpected(rs
						.getString("isFallingExpected"));
				alertNormValues.setIsAssistiveDeviceExpected(rs
						.getString("isAssistiveDeviceExpected"));
				alertNormValues.setIsNegotiatingStairsExpected(rs
						.getString("isNegotiatingStairsExpected"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			err = 57;
			logger.severe("Error Found with Error code " + err);
		}
		logger.fine("AlertNorms Values " + alertNormValues);
		return alertNormValues;

	}

	/**
 * 
 */
	public void saveReportToDatabase(PatientInfo patientInfo,
			Date measurementDate) {
		// Connect to the database again
		Object[] returnObject = getDataBaseConnection();
		Connection conn = (Connection) returnObject[0];
		int err = (Integer) returnObject[1];
		if (err != 0) {
			logger.severe("Error Found while getting database connection");
			return;
		}
		Statement stmt = null;
		try {
			if (conn != null) {
				stmt = conn.createStatement();
			}
		} catch (SQLException e2) {
			err = 39;
			logger.severe("Error Found with Error code " + err);
			return;
		}

		String dateString = (new SimpleDateFormat("yyyy-MM-dd"))
				.format(measurementDate);
		
		// Currently checking with previous week
		String query = "select * from Report where patientId="
				+ patientInfo.getPatientID() + " and week = "
				+ patientInfo.getWeekSinceSurgery() + "";

		boolean gotData = false;

		try {
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				gotData = true;
			}
		} catch (SQLException e1) {
			err = 50;
			logger.severe("Error Found with Error code " + err);
			return;
		}

		if (!gotData) {

			Map<String, Double> listMap = patientInfo.getActivityListMap();
			int[] kneeFunction = patientInfo.getCurrentKneeValues();
			// TODO: The below query has db field for using stairs but not
			// running. Need to be fixed.
			query = "insert into report(patientId,week,reportDate,fileName,filePath,zipFileName,maxFlexion,maxExtension,excursionsPerHour,"
					+ "modalExcursion,numberOfHoursLying,numberOfHoursSitting,numberOfHoursStanding,"
					+ "numberOfHoursWalking,"
					+ "numberOfHoursUsingStairsUp,numberOfHoursUsingStairsDown,numberOfHoursExercise,numberOfHoursUsingBike"
					+ ", studyDuration) values ("
					+ patientInfo.getPatientID()
					+ ", "
					+ patientInfo.getWeekSinceSurgery()
					+ ", '"
					+ dateString
					+ "', '"
					+ patientInfo.getReportFileName()
					+ "', '"
					+ patientInfo.getReportFilePath()
					+ "', '"
					+ patientInfo.getZipFileName()
					+ "',"
					+ kneeFunction[0]
					+ ", "
					+ kneeFunction[1]
					+ ", "
					+ kneeFunction[2]
					+ ", "
					+ kneeFunction[3]
					+ ", "
					+ listMap.get(Constants.LYING)
					+ ", "
					+ listMap.get(Constants.SITTING)
					+ ", "
					+ listMap.get(Constants.STANDING)
					+ ", "
					+ listMap.get(Constants.WALKING)
					+ ", "
					+ listMap.get(Constants.STAIRS_UP)
					+ ", "
					+ listMap.get(Constants.STAIRS_DOWN)
					+ ", "
					+ listMap.get(Constants.EXERCISE)
					+ ", "
					+ listMap.get(Constants.BIKE)
					+ ", "
					+ patientInfo.getTotalDuration() + ")";
			// System.out.println("The query is " + query);
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e1) {
				logger.severe("e1 is " + e1);
				err = 51;
				logger.severe("Error Found with Error code " + err);
				return;
			}
		} else {
			logger.info("Not saving the report as previous report of the same week exists");
		}

		try {
			conn.close();
		} catch (SQLException e) {
			// This is not a big deal for the report so just move on
		}
	}

	/**
	 * This method stores the status of run and the report generation. The
	 * result of the database is helpful for audit purposes.
	 */
	public static void saveStatusInDatabase(String executionStartTime,
			String reportFileName, String zipFileName) {
		// Connect to the database again
		Object[] returnedObject = DatabaseCallHelper.getDataBaseConnection();
		Connection conn = (Connection) returnedObject[0];
		int err = (Integer) returnedObject[1];
		if (conn == null) {
			logger.severe("Cannot Save to Database due to err " + err);
			logger.severe(ErrorCodes.Errors[err]);
			return;
		}
		Statement stmt;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e2) {
			err = 39;
			logger.severe("Error Found with Error code " + err);
			return;
		}
		String errorMessage = "";
		if (err < ErrorCodes.Errors.length)
			errorMessage = ErrorCodes.Errors[err];
		else {
			errorMessage = "Error Description: Unknown error";
		}
		SimpleDateFormat simpleDateFormatOutput = new SimpleDateFormat(
				Constants.DATE_TIME_FORMAT);
		String executionEndTime = simpleDateFormatOutput.format(new Date());
		String statusOfRun = err == 0 ? "Success" : "Failure";
		String query = "insert into Audit(runStartTime,runEndTime,reportName,zipFileName,statusOfRun"
				+ ",errorCause, errorCode) values ('"
				+ executionStartTime
				+ "', '"
				+ executionEndTime
				+ "', '"
				+ reportFileName
				+ "', '"
				+ zipFileName
				+ "', '"
				+ statusOfRun
				+ "', '"
				+ errorMessage
				+ "'," + err + ");";
		// System.out.println("The query is " + query);
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e1) {
			logger.severe("e1 is " + e1);
			err = 56;
			logger.severe("Error Found with Error code " + err);
			return;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				err = 56;
				logger.severe("Error Found with Error code " + err);
			}
		}
	}

}
