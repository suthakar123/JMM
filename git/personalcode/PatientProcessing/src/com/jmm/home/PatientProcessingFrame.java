package com.jmm.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import KneeProject0817.KneeClass;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.jmm.database.process.DatabaseCallHelper;
import com.jmm.database.process.domain.DatabaseConfig;
import com.jmm.database.process.domain.DatabaseDomain;
import com.jmm.file.process.ReadBinaryFile;
import com.jmm.file.process.domain.CalibrationData;
import com.jmm.file.process.domain.SessionBody;
import com.jmm.file.process.domain.SessionHeader;
import com.jmm.report.controller.ReportGeneratorController;
import com.jmm.report.domain.PatientInfo;
import com.jmm.report.domain.ReportDomain;
import com.jmm.util.Constants;
import com.jmm.util.ErrorCodes;
import com.jmm.util.LoadProperties;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class PatientProcessingFrame {

	public static Logger logger = Logger.getLogger("PatientProcessingFrame");
	public static DatabaseConfig dbConfig;
	private MWNumericArray sensorData;
	private static String zipFileName;
	private static String reportPath;
	private static String reportFileName;
	private static int err;
	private DatabaseCallHelper dbObject=null;

	byte[] kneeImageBufferArray;
	private String[] responses;
	private String[] calibrationArray;
	private MWNumericArray angleCalibrationFromZip;
	private final int NUM_RESPONSES = 10;
	private final int NUM_CHANNELS = 7;

	private Map<String, Double> activityListMap;
	private Map<String, Double> activityTypeMap;
	private String sensorAddr;
	private Date measurementDate;
	private PatientInfo patientInfo;
	private String photoDate;
	private String photoTime;
	private int[] kneeFunction;
	private final int BUFFER_SIZE = 10000;
	private DateFormat outputDateFormat;
	private DateFormat outputTimeFormat;
	private SessionBody sessionBody = null;
	private CalibrationData calibrationData = null;
	private static String executionStartTime = "";
	private static boolean useSampleData = false;

	/**
	 * Entry point to the application which parses in the command line arguments
	 * and launches the GUI
	 */
	public static void main(String[] args) {
		// PatientProcessingFrame ppf = null;
		zipFileName = null;
		SimpleDateFormat simpleDateFormatOutput = new SimpleDateFormat(
				"MM-dd-yyyy-HH:mm:ss");

		executionStartTime = simpleDateFormatOutput.format(new Date());

		if (args.length == 1)
			zipFileName = args[0];
		if (args.length == 2) {
			reportPath = args[1];
			zipFileName = args[0];
		}
		if (args.length == 3) {
			reportPath = args[1];
			zipFileName = args[0];
			useSampleData = true;
		}
		try {
			new PatientProcessingFrame();
		} catch (Exception e) {
			logger.severe("Found Error in the execution");
		} finally {

			if (err < 0 || err > ErrorCodes.Errors.length - 1) {
				logger.log(Level.SEVERE, "Error Description: Unknown error"
						+ err);
			} else if (err == 0) {
				logger.log(Level.INFO, " Successfully executed ");
			} else {
				logger.log(Level.SEVERE, "Error Description: "
						+ ErrorCodes.Errors[err]);
			}

			// Save the status of the run to database
			DatabaseCallHelper.saveStatusInDatabase(executionStartTime,reportFileName,zipFileName);
		}
	}

	/**
	 * This method initializes the properties file and logger file
	 */
	private void initialize() {
		// Fetching the value from database.
		LoadProperties loadProperties = null;
		logger.log(Level.FINER, "Loading the properties file");
		try {
			loadProperties = new LoadProperties();
			dbConfig=new DatabaseConfig();
			dbConfig.setDatabaseName(loadProperties.getDatabaseName());
			dbConfig.setMachineName(loadProperties.getMachineName());
			dbConfig.setPortNumber(loadProperties.getPortNumber());
			dbConfig.setDbUserName(loadProperties.getDatabaseUserName());
			dbConfig.setDbPassword(loadProperties.getDatabasePassword());
			dbConfig.setSQLAuthenticated(loadProperties.isSqlAuthentication());	
		} catch (IOException e1) {
			logger.log(Level.SEVERE, "Properties File not correctly found");
			logger.finest("Error StackTrace is "
					+ e1.getStackTrace().toString());
			err = 55;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error Reading Properties file");
			 e.printStackTrace();
			logger.finest("Error StackTrace is " + e.getStackTrace().toString());
			err = 55;
		}
		try {
			if (loadProperties != null) {
				err = loadProperties.setLoggingConfigurations(logger);
			}
		} catch (Exception e) {
			logger.finest("Found exception " + e.getStackTrace().toString());
			logger.severe("Error Found while setting loggin configurations");
		}
	}

	/**
	 * Constructor of the project which invokes the Java application
	 */
	public PatientProcessingFrame() {
		// invoke the process
		initialize();
		if (err != 0) {
			return;
		}
		long startTime = System.currentTimeMillis();
		// System.out.println("Starting Time in Constructor " + startTime);
		responses = null;
		sensorData = null;
		kneeFunction = null;

		// Create DateFormat objects for all subsequent date operations
		outputDateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		outputTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		long startTime6 = 0;
		if (zipFileName != null) {
			long startTime1 = System.currentTimeMillis();

			logger.log(Level.FINE, "Time before in Constructor "
					+ (startTime1 - startTime));

			processPatientFile();
			long startTime2 = System.currentTimeMillis();

			logger.log(Level.FINE, "Time for processing incoming file "
					+ (startTime2 - startTime1));

			if (err == 0) {
				dbObject=new DatabaseCallHelper();
				// Given the sensor ID, we can now load the remainder of the
				// user data from the database
				DatabaseDomain dbDomain=dbObject.loadDataFromDatabase(sensorAddr,measurementDate);
				patientInfo=dbDomain.getPatientInfo();
				patientInfo.setZipFileName(zipFileName);
				
				long startTime3 = System.currentTimeMillis();

				logger.log(Level.FINE, "Time for fetching from database "
						+ (startTime3 - startTime2));

				if (err == 0) {
					// This function calls the MATLAB program to generate a list
					// of activities
					// that the patient performed. An error code is also
					// returned which is then
					// displayed to the user
					runMatlabClient(dbDomain);
					long startTime4 = System.currentTimeMillis();

					logger.log(Level.FINE, "Time taken by Matlab execution "
							+ (startTime4 - startTime3));

					if (err == 0) {
						generateFinalReport();
						long startTime5 = System.currentTimeMillis();

						logger.log(Level.FINE,
								"Time to generate the report file "
										+ (startTime5 - startTime4));

						if (err == 0) {
							dbObject.saveReportToDatabase(patientInfo, measurementDate);
							startTime6 = System.currentTimeMillis();

							logger.log(Level.FINE, "Time to save report "
									+ (startTime6 - startTime5));
						}
					}
				}
			}
		}
		logger.info("Completed the call");
		long totalTime = System.currentTimeMillis();
		logger.log(Level.FINE, "Total time taken is " + (totalTime - startTime));
		MWArray.disposeArray(sensorData);
	}

	/**
	 * @description:This method processes the incoming zip file.
	 * @author JMM
	 */
	private void processPatientFile() {
		// We expect 3 files in the zip file - data.bin, notes.txt and photo.jpg
		logger.log(Level.INFO, "Loading the Zip file...");
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(new File(zipFileName));
		} catch (Exception e) {
			// We need to make sure that the passed in file exists
			logger.severe(String.valueOf(e));
			if (e != null)
				logger.severe("Error Message:" + e.getMessage());
			err = 19;
			return;
		}
		Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

		boolean[] foundFiles = new boolean[4];

		try {
			ZipEntry entry;

			while (zipFileEntries.hasMoreElements()) {
				// First we want get all of the data for this file
				entry = (ZipEntry) zipFileEntries.nextElement();

				// If we know the size, we can add it in as a suggested capacity
				int size = (int) entry.getSize();
				ByteArrayOutputStream byteStream;
				byte[] buffer = new byte[BUFFER_SIZE];
				if (size < 0)
					byteStream = new ByteArrayOutputStream();
				else
					byteStream = new ByteArrayOutputStream(size);
				int bytesRead = 0;
				InputStream zipFileStream = zipFile.getInputStream(entry);
				while ((bytesRead = zipFileStream
						.read(buffer, 0, buffer.length)) > 0) {
					byteStream.write(buffer, 0, bytesRead);
					buffer = new byte[BUFFER_SIZE];
				}

				// Now handle this data based on the file name
				if (entry.getName().equals("data.bin")) {
					foundFiles[0] = true;
					logger.log(Level.FINE, "Loading the data.bin file...");
					try {
						if (!useSampleData) {
							sessionBody = processDataFile(new ByteArrayInputStream(
									byteStream.toByteArray()));

						} else {
							createSampleData();
							// return;
						}
						// The date when binary data is uploaded
						measurementDate = new Date(entry.getTime());
					} catch (Exception e) {
						if (err == 0)
							err = 31;
						logger.severe(e.getMessage());
						zipFile.close();
						return;
					}
					logger.log(Level.FINE, "Successfully Loaded data.bin file");
				} else if (entry.getName().equals("notes.txt")) {
					foundFiles[1] = true;
					logger.log(Level.FINE, "Loading the notes.txt file...");
					try {
						processAnswers(new ByteArrayInputStream(
								byteStream.toByteArray()));
					} catch (Exception e) {
						logger.severe(String.valueOf(e));
						err = 29;
						zipFile.close();
						return;
					}
					logger.log(Level.FINE, "Successfully Loaded notes.txt file");
				} else if (entry.getName().equals("photo.jpg")) {
					foundFiles[2] = true;
					logger.log(Level.FINE, "Loading the photo.jpg file...");
					try {
						kneeImageBufferArray = byteStream.toByteArray();
						Date zipEntryPhotoDate = new Date(entry.getTime());
						photoDate = outputDateFormat.format(zipEntryPhotoDate)
								.toString();
						photoTime = outputTimeFormat.format(zipEntryPhotoDate)
								.toString();
					} catch (Exception e) {
						// Could not parse the image so throw an error
						err = 27;
						zipFile.close();
						logger.log(Level.SEVERE, e.getMessage());
						return;
					}
					logger.log(Level.FINE, "Successfully Loaded photo.jpg file");
				} 
				else if (entry.getName().equals("cal.bin")) {
					foundFiles[3] = true;
					logger.log(Level.FINE, "Loading the cal.bin file...");
					try {
						
						ReadBinaryFile readBinaryFile = new ReadBinaryFile(logger);
						
						calibrationData = readBinaryFile.processCalibration(new ByteArrayInputStream(
								byteStream.toByteArray()));
						
						int[] dims = new int[] {1,2};
						angleCalibrationFromZip = MWNumericArray.newInstance(dims,
								MWClassID.DOUBLE, MWComplexity.REAL);
						
						angleCalibrationFromZip.set(1,calibrationData.getCalibrationCoefficients()[0]);
						angleCalibrationFromZip.set(2,calibrationData.getCalibrationCoefficients()[1]);
					} catch (Exception e) {
						logger.severe(String.valueOf(e));
						err = 29;
						zipFile.close();
						return;
					}
					logger.log(Level.FINE, "Successfully Loaded cal.bin file");
				}
				
					else {
					// Report an error if anything unnecessary is found
					err = 32;
					zipFile.close();
					return;
				}
			}
		} catch (Exception e) {
			err = 33;
			try {
				zipFile.close();
			} catch (IOException e1) {
				// Not a big deal if we can't close
			}
			return;
		}

		try {
			zipFile.close();
		} catch (IOException e) {
			// Not a big deal if we can't close
		}

		// Make sure we got all of the files
		if (!foundFiles[0]) {
			err = 30;
			return;
		}
		if (!foundFiles[1]) {
			err = 28;
			return;
		}
		if (!foundFiles[2]) {
			err = 26;
			return;
		}
		logger.log(Level.FINE, "Done");
	}

	/**
	 * TODO: To Test This method is used for local testing where we would be
	 * passing sample test data.The numbers have been taken from the Excel
	 * sheet.Please refer excel sheet "Synthethic Calibrations.xlsx"
	 */
	private void createSampleData() {
		// In the sample, we are using 1600 number of records
		int[] dims = { 1600, NUM_CHANNELS };
		int eventBit = 1;
		sensorData = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
				MWComplexity.REAL);
		int counter = 0;
		while (counter < 1600) {
			counter++;
			// +3 for gyro
			int[] index = { counter, NUM_CHANNELS };

			sensorData.set(index, eventBit);
			index[0] = counter;
			// Setting the flexValue of bits to 2048 which internally should map
			// to 70 degrees in angle
			sensorData.set(index, 2048);
			// This value will correspond to 1g in Ax
			index[1] = 2;
			sensorData.set(index, 154);
			// This value will correspond to 2g in Ay
			index[1] = 3;
			sensorData.set(index, 179);
			// This value will correspond to 3g in Az
			index[1] = 4;
			sensorData.set(index, 205);
			// This value will correspond to 300 rad/s in Wx
			index[1] = 5;
			sensorData.set(index, 42598);
			// This value will correspond to 600 rad/s in Wy
			index[1] = 6;
			sensorData.set(index, 52428);
			// This value will correspond to 900 rad/s in Wz
			index[1] = 7;
			sensorData.set(index, 62258);
			// System.out.println("sensor Data \n "+ sensorData);
		}
	}

	/**
	 * @description: This method returns MATLAB data structure which contains
	 *               the combined data structure of the incoming parameters
	 * @param clockwiseAngles
	 * @param counterClockwiseAngles
	 * @param accelerationValues
	 * @param gyroValues
	 * @return MWNumericArray a numeric array returning sensorData which
	 *         contains clockwiseAngles,counterClockwiseAngles,
	 *         accelerationValues,gyroValues
	 */
	private MWNumericArray getSensorData(int[] clockwiseAngles,
			int[] counterClockwiseAngles, double[][] accelerationValues,
			double[][] gyroValues) {
		logger.fine("Starting the phase to get SensorData");
//		for(int i = 0 ; i < clockwiseAngles.length; i ++){
//			System.out.println("clockwise "+ clockwiseAngles[i]);
//		}
//		for(int i = 0 ; i < counterClockwiseAngles.length; i ++){
//			System.out.println("counter clockwise "+ counterClockwiseAngles[i]);
//		}
//		System.out.println("clockwise "+ clockwiseAngles.length);
		//System.out.println("counter clockwise "+ counterClockwiseAngles.length);
//		System.out.println("accel1 "+ accelerationValues[0].length );
//		System.out.println("accel2 "+ accelerationValues[1].length);
//		System.out.println("accel3 "+ accelerationValues[2].length);
//		System.out.println("gyro1 "+ gyroValues[0].length);
//		System.out.println("gyro2 "+ gyroValues[1].length);
//		System.out.println("gyro3 "+ gyroValues[2].length);
		MWNumericArray sensorData = null;
		int[] dims = { clockwiseAngles.length, NUM_CHANNELS };
		try {
			sensorData = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
					MWComplexity.REAL);
			int counter = -1;
			//Band aid fix for missing angle values
			int max_sample_size = clockwiseAngles.length>accelerationValues[0].length? clockwiseAngles.length > gyroValues[0].length? clockwiseAngles.length:gyroValues[0].length:accelerationValues[0].length;  
			while (counter < clockwiseAngles.length - 1) {
				counter++;
				int[] index = { counter, NUM_CHANNELS + 1 };
				index[0] = counter + 1;
				index[1] = 1;
				sensorData.set(index, clockwiseAngles[counter]);
				//index[1] = 2;
				//sensorData.set(index, counterClockwiseAngles[counter]);

				index[1] = 2;
				sensorData.set(index, accelerationValues[0][counter]);
				index[1] = 3;
				sensorData.set(index, accelerationValues[1][counter]);
				index[1] = 4;
				sensorData.set(index, accelerationValues[2][counter]);

				index[1] = 5;
				sensorData.set(index, gyroValues[0][counter]);
				index[1] = 6;
				sensorData.set(index, gyroValues[1][counter]);
				index[1] = 7;
				sensorData.set(index, gyroValues[2][counter]);
			}
		} catch (Exception e) {
			logger.severe(String.valueOf(e));
			err = 58;
		}
		logger.fine("Completed to fetch SensorData");
		return sensorData;
	}

	/**
	 * @description: The method takes in byteArrayInputStream of the data.bin
	 *               file and returns a wrapper object of SessionBody
	 * @param byteInputStream
	 * @return SessionBody containing the resistance, acceleration and gyro
	 *         values
	 * @throws Exception
	 */
	private SessionBody processDataFile(ByteArrayInputStream byteInputStream)
			throws Exception {

		logger.info("Starting PatientProcessingFrame.processDataFile()");

		ReadBinaryFile readBinaryFile = new ReadBinaryFile(logger);
		
		//CalibrationData calibrationData = readBinaryFile.processCalibration(byteInputStream);
		//logger.info("Suthakar printing calibration data"+calibrationData.getCalibrationCoefficients());
		SessionHeader headerInformation = readBinaryFile
				.processHeader(byteInputStream);
		try {
			sessionBody = readBinaryFile.fetchSessionBody(byteInputStream,
					headerInformation);
		} catch (Exception e) {
			logger.severe("Found an exception " + e);
			err = 84;
		}
		logger.fine("Completed PatientProcessingFrame.processDataFile()");
		return sessionBody;
	}
	
	/**NOT IN USE CURRENTLY
	 * Reads the text file and processes calibration text into a String array called
	 * Calibration[]
	 * 
	 * @param answers
	 * @throws Exception
	 */
	private void processCalibration(InputStream calibration) throws Exception {
		logger.fine("Starting PatientProcessingFrame.processCalibration()");
		calibrationArray = new String[2];
		int[] dims = { 2,2 };
		angleCalibrationFromZip = MWNumericArray.newInstance(dims,
				MWClassID.DOUBLE, MWComplexity.REAL);
		for (int i = 0; i < 2; i++)
			calibrationArray[i] = "";

		Scanner calibrationScanner = new Scanner(calibration);

		// Second line should have the satisfaction rating
		String cal1 = calibrationScanner.nextLine();
		String cal2 = calibrationScanner.nextLine();
		Double ccw1 = Double.parseDouble(cal1);
		Double ccw2 = Double.parseDouble(cal2);
		
		angleCalibrationFromZip.set(1,ccw1);
		angleCalibrationFromZip.set(3,ccw2);
		angleCalibrationFromZip.set(2,ccw1);
		angleCalibrationFromZip.set(4,ccw2);
		
		
		calibrationScanner.close();
		
		logger.log(Level.FINE, "suthakar printing cal" + ccw1);
		logger.log(Level.FINE, "suthakar printing cal" + ccw2);
		
	}

	/**
	 * Reads the text file and processes answers into a String array called
	 * responses[]
	 * 
	 * @param answers
	 * @throws Exception
	 */
	private void processAnswers(InputStream answers) throws Exception {
		logger.fine("Starting PatientProcessingFrame.processAnswers()");
		responses = new String[NUM_RESPONSES];

		for (int i = 0; i < NUM_RESPONSES; i++)
			responses[i] = "";

		Scanner answerScanner = new Scanner(answers);

		// Skip the first line - don't need it for the responses
		answerScanner.nextLine();

		// Second line should have the satisfaction rating
		String satisfactionString = getNextValueString(answerScanner,
				"Satifaction: ", "/10");
		responses[0] = satisfactionString.trim();

		// Third line should have the pain rating
		String painString = getNextValueString(answerScanner, "Pain Level: ",
				"/10");
		responses[4] = painString.trim();

		// Fourth line should be fevers
		String feverString = getNextValueString(answerScanner, "Fevers: ", "");

		// Fifth line should be chills
		String chillsString = getNextValueString(answerScanner, "Chills: ", "");

		// Sixth line should be sweats
		String sweatsString = getNextValueString(answerScanner, "Sweats: ", "");

		// If any of the above were YES, then mark fever/chills/sweats string as
		// Y
		if (feverString.equals("YES") || chillsString.equals("YES")
				|| sweatsString.equals("YES"))
			responses[1] = "Y";
		else if (feverString.equals("Undecided")
				|| chillsString.equals("Undecided")
				|| sweatsString.equals("Undecided"))
			responses[1] = "Undecided";
		else
			responses[1] = "N";

		// Seventh line should be pain meds
		String painMedsString = getNextValueString(answerScanner,
				"Pain Meds: ", "");
		AssignYesNoValue(2, painMedsString);

		// Eighth line should be side effects
		String sideEffectsString = getNextValueString(answerScanner,
				"Side Effects: ", "");
		AssignYesNoValue(3, sideEffectsString);

		// Ninth line should be pain occurs
		String painOccursString = getNextValueString(answerScanner,
				"Pain Occurs: ", "");
		if (painOccursString.equals("At Rest"))
			responses[5] = "R";
		else if (painOccursString.equals("During Activity"))
			responses[5] = "A";
		else if (painOccursString.equals("All the time"))
			responses[5] = "E";
		else
			throw new Exception();

		// Tenth line should be stability walking
		String stabilityWalkingString = getNextValueString(answerScanner,
				"Stability Walking: ", "/10");
		responses[6] = stabilityWalkingString.trim();

		// Eleventh line should be falls
		String fallsString = getNextValueString(answerScanner,
				"Falls In 24 Hours: ", "");
		AssignYesNoValue(7, fallsString);

		// Twelveth line should be walking assists
		String walkingAssistsString = getNextValueString(answerScanner,
				"Using Walking Assists: ", "");
		AssignYesNoValue(8, walkingAssistsString);

		// Thirteenth line should be stairs
		String stairsString = getNextValueString(answerScanner,
				"Using Stairs: ", "");
		AssignYesNoValue(9, stairsString);

		// TODO: SensorAddress datatype needs to be changed after asking
		// PRofessor
		String deviceIdString = getNextValueString(answerScanner, "Device ID:",
				"");
		sensorAddr = deviceIdString.trim();
		//sensorAddr = "20121";
		// System.out.println("The sensor Address is " + sensorAddr);

		answerScanner.close();
		logger.fine("Ending PatientProcessingFrame.processAnswers()");
	}

	/**
	 * @desciption:Based on the input answer argument, the value of 'Y','N' or
	 *                   'NA' is assigned
	 * 
	 * @param index
	 * @param answer
	 * @throws Exception
	 */
	private void AssignYesNoValue(int index, String answer) throws Exception {
		if (answer.equals("YES"))
			responses[index] = "Y";
		else if (answer.equals("NO"))
			responses[index] = "N";
		else
			responses[index] = "NA";
	}

	/**
	 * @description:Reads a String from startTrimString till endTrimString and
	 *                    returns the String
	 * @param s
	 *            Scanner
	 * @param startTrimString
	 *            String
	 * @param endTrimString
	 *            String
	 * @return String
	 * @throws Exception
	 */
	private String getNextValueString(Scanner s, String startTrimString,
			String endTrimString) throws Exception {
		String original = s.nextLine();
		return original.substring(startTrimString.length(), original.length()
				- endTrimString.length());
	}
	
	/**
	 * @description: Runs the MATLAB client based on the data collected from
	 *               various method calls
	 */
	private void runMatlabClient(DatabaseDomain dbDomain) {

		Object[] calibrationResult = null;
		Object[] classificationResult = null;
		Object[] excursionsResult = null;
		Object[] interpolatedData = null;
		Object[] interpolatedAngleData = null;
		Object[] interpolatedAccelData = null;
		KneeClass matlabCode = null;
		MWNumericArray angleCalibrationFromDataBase=dbDomain.getAngleCalibrationFromDataBase();
		MWNumericArray gyroCalibrationFromDataBase=dbDomain.getGyroCalibrationFromDataBase();
		MWNumericArray accelerationCalibrationFromDataBase=dbDomain.getAccelerationCalibrationFromDataBase();
		MWNumericArray treeDataFromDataBase=dbDomain.getTreeDataFromDataBase();
		//String treeFileName=dbDomain.getTreeFileName();
		String treeFilePath=dbDomain.getTreeFilePath();
		//String completeTreeName=treeFilePath+File.separator+treeFileName;
		//logger.fine("completeTreeName is "+completeTreeName);
		MWNumericArray acceleration = new MWNumericArray(
				sessionBody.getAccelerationValues(), MWClassID.DOUBLE);
		MWNumericArray gyroValues = new MWNumericArray(
				sessionBody.getGyroValues(), MWClassID.DOUBLE);
		int[][] interpAngle= new int[sessionBody.getClockwiseAngles().length][3];
		for(int i = 0 ; i < sessionBody.getClockwiseAngles().length ; i++){
			interpAngle[i][0] = sessionBody.getClockwiseAngles()[i];
			interpAngle[i][1] = sessionBody.getClockwiseAngles()[i];
			interpAngle[i][2] = sessionBody.getClockwiseAngles()[i];
		}
		MWNumericArray angles = new MWNumericArray(
				interpAngle, MWClassID.DOUBLE);
		double lengthOfSession = Math.round(sessionBody.getClockwiseAngles().length * 20/26);
		try {
			matlabCode = new KneeClass();
			
			interpolatedData = (matlabCode.interpolateData(2,
					gyroValues, lengthOfSession));
			interpolatedAngleData = (matlabCode.interpolateData(2,
					angles, lengthOfSession));
			interpolatedAccelData = (matlabCode.interpolateData(2,
					acceleration, lengthOfSession));

			//byte[][] interpolatedAcceleration = new byte[3][];
		
			double[][] interpolatedGyroValues = new double[3][];
			
			//System.out.println("gyro dimensions" +gyroValues.getDimensions()[1]);
			err = ((MWNumericArray) interpolatedData[0]).getInt();
			//System.out.println("Error in interpolate " + err);
			
//			interpolatedAcceleration = (byte[][])((MWNumericArray) (acceleration)).toByteArray();
//			System.out.println("interpolated acceleration length" +interpolatedAcceleration.length );
//			System.out.println("interpolated acceleration dimenstion length" +interpolatedAcceleration[1].length );
//
//			byte[][] transposeAccelValues = new byte[3][interpolatedAcceleration.length];
//			int index = 0;
//			try{
//				for ( int i = 0; i < interpolatedAcceleration.length; i++) {
//					if (interpolatedAcceleration[i].length > 1) {
//						transposeAccelValues[0][i] = ((byte) (interpolatedAcceleration[i][0]));
//						transposeAccelValues[1][i] = ((byte) (interpolatedAcceleration[i][1]));
//						transposeAccelValues[2][i] = ((byte) (interpolatedAcceleration[i][2]));
//					}
//				}
//			
//			}
//			catch(Exception e){
//				System.out.println("Printing exception "+ e);
//			}
			//TODO : Get the values from interpolation and use them
//			interpolatedAcceleration[0] = (acceleration).getByteData();
//			interpolatedAcceleration[1] = ((MWNumericArray) interpolatedData[1])
//					.getByteData();
//			interpolatedAcceleration[2] = ((MWNumericArray) interpolatedData[2])
//					.getByteData();
			
			//short[][] gyroInterpolate = ((MWNumericArray)interpolatedData[1]).
			double[][] accelData = (double[][]) ((MWNumericArray) interpolatedAccelData[1]).toDoubleArray();
			double[][] gyroData = (double[][]) ((MWNumericArray) interpolatedData[1])
					.toDoubleArray();		
			int[][] angleData = (int[][]) ((MWNumericArray) interpolatedAngleData[1]).toIntArray();

			int count=((MWNumericArray) interpolatedData[1]).numberOfElements();
			
			interpolatedGyroValues[0] = gyroData[0];
			interpolatedGyroValues[1] = gyroData[1];
			interpolatedGyroValues[2] = gyroData[2];
			
//			System.out.println("dimensions of angle" + angleData[0].length );
//			System.out.println("dimensions of accel" + accelData[0].length );
//			System.out.println("dimensions of gyro" + interpolatedGyroValues[0].length );
			
			sensorData = getSensorData(angleData[1],
					sessionBody.getCounterClockwiseAngles(),
					accelData, interpolatedGyroValues);
			
			//System.out.println("sensor data dimensions "+ sensorData.getDimensions()[1]);
			if (err != 0) {
				System.out.println("returning at 777");
				return;
			}
			long startTime1 = System.currentTimeMillis();
			
			LoadProperties loadProperties = null;
			loadProperties = new LoadProperties();
			float sample_rate = loadProperties.getSamplingRate();
			//System.out.println("Sample rate from properties : "+ sample_rate);
			calibrationResult = matlabCode.calibrateData(2, sensorData,
					angleCalibrationFromZip, gyroCalibrationFromDataBase,
					accelerationCalibrationFromDataBase,
					patientInfo.getStudySide(), sample_rate);
			long startTime2 = System.currentTimeMillis();
			logger.fine("Time for CalibrateData " + (startTime2 - startTime1));
			err = ((MWNumericArray) calibrationResult[0]).getInt();
			if (err == 0) {
				MWNumericArray calibratedData = (MWNumericArray) calibrationResult[1];
				//System.out.println("calibrated dimenstions "+ calibratedData.getDimensions()[1]);
				//System.out.println("Printing tree file path" + treeFilePath);
				classificationResult = matlabCode.classifyData(4, treeFilePath,
						calibratedData);
				long startTime3 = System.currentTimeMillis();

				logger.fine("Time for ClassifyData "
						+ (startTime3 - startTime2));

				err = ((MWNumericArray) classificationResult[0]).getInt();
				if(err != 0){
					//System.out.println("Printing error code from matlab : "+ err);
				}
				if (err == 0) {
					double[][] durationsResult = (double[][]) ((MWNumericArray) classificationResult[1])
							.toDoubleArray();
					String activities = (String)((MWCellArray)classificationResult[2]).toString();
					String bins = (String)((MWCellArray)classificationResult[3]).toString();
					MWCellArray cellArrayBins=((MWCellArray)classificationResult[3]);
					int number_bins=cellArrayBins.numberOfElements();
					//System.out.println("number of bins: "+ number_bins );
					//MWArray arr = cellArrayBins.getCell(new int[]{1,66});
					//System.out.println(arr.toString());
//					for(int i = 1 ; i < 316 ; i++)
//					{
//						MWArray arr = cellArrayBins.getCell(new int[]{1,i});
//						System.out.println(arr.toString());
//					}
					//System.out.println(bins);
					MWCellArray cc=((MWCellArray)classificationResult[2]);
					int n=cc.numberOfElements();
					//System.out.println("number of activities: "+ n );
					//TODO Needs to be aligned in future
//					String arr2=(String)cc.toString();
//					System.out.println((String)cc.get(1));
//					System.out.println((String)cc.get(2));
//					System.out.println((String)cc.get(3));
//					System.out.println((String)cc.get(4));
//					String arr[]=arr2.trim().split("'");
//					System.out.println(arr.length);
//					System.out.println(arr);
					setDurationsResultsFromMatlab(durationsResult[0]);

					// Finally we can calculate the excursion data
					excursionsResult = matlabCode.excursions(7, calibratedData,
							sample_rate,0);
					err = ((MWNumericArray) excursionsResult[0]).getInt();

					long startTime4 = System.currentTimeMillis();
					if (err != 0) {
						logger.severe("ERROR found in Excursions");
					}
					logger.info("Time for Excursions "
							+ (startTime4 - startTime3));
					if (err == 0) {
						kneeFunction = new int[4];
						// TODO: Currently set to absolute values for now
						kneeFunction[0] = Math
								.abs(((MWNumericArray) excursionsResult[1])
										.getInt());
						logger.fine("Max Flexion " + kneeFunction[0]);
						kneeFunction[1] = Math
								.abs(((MWNumericArray) excursionsResult[2])
										.getInt());
						logger.fine("Max Extension " + kneeFunction[1]);
						kneeFunction[2] = ((MWNumericArray) excursionsResult[3])
								.getInt();
						logger.fine("Excursions per hour " + kneeFunction[2]);
						kneeFunction[3] = ((MWNumericArray) excursionsResult[4])
								.getInt();
						logger.fine("Modal Exursions " + kneeFunction[3]);
						patientInfo.setCurrentKneeValues(kneeFunction);
						// System.out.println(Arrays.toString(kneeFunction));
					}
				}
			}

		} catch (Exception e) {
			if (err == 0)
				err = 34;
			System.out.println(e);
			logger.severe("Error Found with Error code " + err);
		} finally {
			if (calibrationResult != null)
				MWArray.disposeArray(calibrationResult);
			if (classificationResult != null)
				MWArray.disposeArray(classificationResult);
			if (sensorData != null)
				MWArray.disposeArray(sensorData);
			if (angleCalibrationFromDataBase != null)
				MWArray.disposeArray(angleCalibrationFromDataBase);
			if (accelerationCalibrationFromDataBase != null)
				MWArray.disposeArray(accelerationCalibrationFromDataBase);
			if (treeDataFromDataBase != null)
				MWArray.disposeArray(treeDataFromDataBase);
			if (matlabCode != null) {
				matlabCode.dispose();
			}
		}
	}

	/**
	 * 
	 * @param durationsResult
	 */
	private void setDurationsResultsFromMatlab(double[] durationsResult) {
		/*Columns 1 through 5
	    	'Cycling'    'Knee_extensions'    'Lying'    'Sitting'    'Stairs_down'
	  	  Columns 6 through 8
	     	'Stairs_up'    'Standing'    'Walking'*/
		NumberFormat formatter = new DecimalFormat("#00.00");
		//int counter = 0;
		
		activityListMap = new HashMap<String, Double>();
		//BIKE connected to cycling
		activityListMap.put(Constants.BIKE, Double.valueOf(formatter.format(durationsResult[0])));
		activityListMap.put(Constants.EXERCISE,Double.valueOf(formatter.format(durationsResult[1])));
		activityListMap.put(Constants.LYING,Double.valueOf(formatter.format(durationsResult[2])));
		//Knee Extensions linked to Exercise
		activityListMap.put(Constants.SITTING,Double.valueOf(formatter.format(durationsResult[3])));
		activityListMap.put(Constants.STAIRS_UP,Double.valueOf(formatter.format(durationsResult[4])));
		activityListMap.put(Constants.STAIRS_DOWN,Double.valueOf(formatter.format(durationsResult[5])));
		
		activityListMap.put(Constants.STANDING, Double.valueOf(formatter.format(durationsResult[6])));		
		activityListMap.put(Constants.WALKING,Double.valueOf(formatter.format(durationsResult[7])));
		activityListMap.put(Constants.UNKNOWN,Double.valueOf(formatter.format(durationsResult[8])));
//		System.out.println("1 activity : "+ Double.valueOf(formatter.format(durationsResult[0])));
//		System.out.println("2 activity : "+ Double.valueOf(formatter.format(durationsResult[1])));
//		System.out.println("3 activity : "+ Double.valueOf(formatter.format(durationsResult[2])));
//		System.out.println("4 activity : "+ Double.valueOf(formatter.format(durationsResult[3])));
//		System.out.println("5 activity : "+ Double.valueOf(formatter.format(durationsResult[4])));
//		System.out.println("6 activity : "+ Double.valueOf(formatter.format(durationsResult[5])));
//		System.out.println("7 activity : "+ Double.valueOf(formatter.format(durationsResult[6])));
//		System.out.println("8 activity : "+ Double.valueOf(formatter.format(durationsResult[7])));
		//System.out.println("9 activity : "+ Double.valueOf(formatter.format(durationsResult[8])));
		
//		System.out.println("cycling: "+ durationsResult[0]);
//		System.out.println("knee ext : "+ durationsResult[1]);
//		System.out.println("lying : "+ durationsResult[2]);
//		System.out.println("sitting: "+ durationsResult[3]);
//		System.out.println("stairs down: "+ durationsResult[4]);
//		System.out.println("stairs up : "+ durationsResult[5]);
//		System.out.println("standing : "+ durationsResult[6]);
//		System.out.println("walking : "+ durationsResult[7]);
//		System.out.println("unknown: "+ durationsResult[8]);
		//activityListMap.put(Constants.RUNNING, 0.0);
		
		
		double activeDuration = 0, inActiveDuration = 0, totalDuration = 0;
		Set<String> activeActivities = new HashSet<String>();
		activeActivities.add(Constants.WALKING);
		activeActivities.add(Constants.EXERCISE);
		activeActivities.add(Constants.STAIRS_UP);
		activeActivities.add(Constants.STAIRS_DOWN);
		activeActivities.add(Constants.BIKE);
		activeActivities.add(Constants.UNKNOWN);

		for (Entry<String, Double> e : activityListMap.entrySet()) {
			if (activeActivities.contains(e.getKey())) {
				activeDuration += e.getValue();
			} else {
				inActiveDuration += e.getValue();
			}
		}
		//Temporarily adding unknown time to total active time
		//activeDuration = activeDuration + Double.valueOf(formatter.format(durationsResult[8]));
		totalDuration = activeDuration + inActiveDuration;
		activityTypeMap = new HashMap<String, Double>();
		activityTypeMap.put(Constants.ACTIVE, activeDuration);
		activityTypeMap.put(Constants.IN_ACTIVE, inActiveDuration);
		patientInfo.setTotalDuration(Double.valueOf(formatter.format(totalDuration)));
		patientInfo.setActivityListMap(activityListMap);
		patientInfo.setActivityMap(activityTypeMap);
	}

	private void generateFinalReport() {
		logger.info("Generating the PDF Report...");
		try {
			reportFileName = "report_"
					+ (new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss"))
							.format(new Date()) + ".pdf";
			String completeReportName = reportPath + File.separator
					+ reportFileName;
			patientInfo.setReportFileName(reportFileName);
			patientInfo.setReportFilePath(reportPath);
			
			//Document document = new Document(PageSize.LETTER, 0, 0, 25, 25);
			Document document = new Document(PageSize.LETTER, 0, 0, 15, 15);

			PdfWriter writer = null;

			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(
						completeReportName));
			} catch (DocumentException e) {
				// TODO: Need to set correct Error code
				err = 12;
			}
			document.open();

			try {
				Object[] returnObject = getAlertStringAndRangeInfo();
				String[] alertStrings = (String[]) returnObject[0];
				boolean[] withinRange = (boolean[]) returnObject[1];

				ReportDomain reportDomain = new ReportDomain();
				reportDomain.setPatientInfo(patientInfo);
				reportDomain.setAlertStrings(alertStrings);
				reportDomain.setWithinRangeArray(withinRange);
				reportDomain.setAnswerArrayFromMobileApp(responses);
				reportDomain.setKneeImageBufferArray(kneeImageBufferArray);
				String tagDateTime = "Tagged: " + photoDate + " " + photoTime;
				reportDomain.setTagDateTime(tagDateTime);
				ReportGeneratorController report = new ReportGeneratorController(
						reportDomain);
				// Invoking PDF controller
				report.invokeController(document, writer);
				System.out.println("Execution Complete!");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Found error " + e);
				e.printStackTrace(System.out);
				err = 16;
			} finally {
				document.close();
			}
		} catch (FileNotFoundException e) {
			// If we couldn't create the file, report a new error and end the
			// GUI
			err = 35;
			logger.severe("Error Found with Error code " + err);
			return;
		}
	}

	/**
	 * 
	 * @return
	 */
	private Object[] getAlertStringAndRangeInfo() {
		// you can use integer array instead of List of Integers.
		int weeksSinceSurgery = patientInfo.getWeekSinceSurgery();
		AlertNorms alertNorms = dbObject.getAlertNormValuesForWeekFromDB(weeksSinceSurgery);
		Object[] returnObject = checkAlerts(alertNorms);
		@SuppressWarnings("unchecked")
		List<Integer> alertNumbers = (List<Integer>) returnObject[0];
		boolean[] withinRange = (boolean[]) returnObject[1];
		// Send a list of String[] elements to caller
		String alertStrings[] = new String[alertNumbers.size()];
		logger.fine("Printing the alerts");
		for (int i = 0; i < alertNumbers.size(); i++) {
			alertStrings[i] = AlertStrings.alertValues[alertNumbers.get(i)];
			logger.fine(alertStrings[i]);
		}
		Object returnedObject[] = { alertStrings, withinRange };
		logger.fine("Completed Alert Strings");
		return returnedObject;
	}

	/**
	 * 
	 * @param alertNorms
	 * @return
	 */
	private Object[] checkAlerts(AlertNorms alertNorms) {
		// Need to clean incoming inputs from the response arrays.
		List<Integer> alertNumbers = new ArrayList<Integer>();
		// Setting the withinRange values
		boolean[] withinRange = { true, true, true, true, true, true, true,
				true, true, true };
		/*
		 * AlertNorms order will be first the App questions, then compare to
		 * prior reports and then check for inactivity durations
		 */
		// App Question AlertNorms Starts
		if (Float.parseFloat(responses[0]) < alertNorms
				.getMinimumSatisfaction()) {
			alertNumbers.add(4);
			withinRange[0] = false;
		}
		if (responses[1] == "Y"
				&& responses[1] != alertNorms.getIsExpectedFeverChillsSweat()) {
			alertNumbers.add(5);
			withinRange[1] = false;
		}
		if (responses[2] == "Y"
				&& responses[2] != alertNorms.getIsPainExpected()) {
			alertNumbers.add(6);
			withinRange[2] = false;
		}
		if (responses[3] == "Y"
				&& responses[3] != alertNorms.getIsExpectedSideEffects()) {
			alertNumbers.add(7);
			withinRange[3] = false;
		}
		if (Float.parseFloat(responses[4]) > alertNorms.getMaximumPainRating()) {
			alertNumbers.add(2);
			withinRange[4] = false;
			// System.out.println("The pain rating is " + responses[4]);
			// System.out.println(alertNorms.getMaximumPainRating());
		}
		if (Float.parseFloat(responses[6]) < alertNorms
				.getMinimumPerceivedStability()) {
			alertNumbers.add(8);
			withinRange[6] = false;
			// System.out.println("The stability rating is " + responses[6]);
		}
		// isFallingExpected .This should always be N. So if Y is there then add
		// alert
		if (responses[7] == "Y") {
			alertNumbers.add(9);
			withinRange[7] = false;
		}
		if (responses[8] == "Y"
				&& responses[8] != alertNorms.getIsAssistiveDeviceExpected()) {
			alertNumbers.add(10);
			withinRange[8] = false;
		}
		if (responses[9] == "N"
				&& responses[9] != alertNorms.getIsNegotiatingStairsExpected()) {
			alertNumbers.add(11);
			withinRange[9] = false;
		}
		// App Question AlertNorms End

		// Exercise hours are less then alert for that week,
		// Patient not performing prescribed exercise.
		Map<String, Double> localActivity = patientInfo.getActivityListMap();
		Map<String, Double> previousLocalActivity = patientInfo
				.getPreviousActivityDurationMap();
		if (localActivity.get(Constants.EXERCISE) < alertNorms
				.getMinimumExerciseHours()) {
			alertNumbers.add(3);
		}
		// Check for inactive hours
		double inActivityHours = patientInfo.getActivityMap().get(
				Constants.IN_ACTIVE);
		// When less physical activity noticed, then this trigger is invoked.
		if (inActivityHours >= alertNorms.getMaximumInactivitiyHours()) {
			alertNumbers.add(12);
		}
		// Prior Report Checks Start
		if (previousLocalActivity != null && !previousLocalActivity.isEmpty()) {
			// Check from [numberOfHoursWalking]
			if (localActivity.get(Constants.WALKING) < previousLocalActivity
					.get(Constants.WALKING)) {
				alertNumbers.add(13);
			}
			// Check for numberOfHoursUsingBike
			if (localActivity.get(Constants.BIKE) < previousLocalActivity
					.get(Constants.BIKE)) {
				alertNumbers.add(15);
			}
		}
		// Prior Report Checks End
		Object returnedObject[] = { alertNumbers, withinRange };
		return returnedObject;
	}
}
