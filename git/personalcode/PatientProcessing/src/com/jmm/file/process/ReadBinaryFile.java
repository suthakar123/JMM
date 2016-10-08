package com.jmm.file.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import com.jmm.file.process.domain.CalibrationData;
import com.jmm.file.process.domain.SessionBody;
import com.jmm.file.process.domain.SessionHeader;

public class ReadBinaryFile {
	private final static byte STATUS_ID = (byte) 0x0C;
	private final static byte CAL_STATUS_ID = (byte) 0x06;

	// private static final int HEADER_LENGTH = 33;
	private static Logger logger;

	public ReadBinaryFile(Logger logger) {
		ReadBinaryFile.logger = logger;
	}
	
	public CalibrationData processCalibration(ByteArrayInputStream inputStream) {
		// Using 359-003-025_Software_Design_Description.pdf as a reference
		// document
		CalibrationData cal_data = new CalibrationData();
		long[] c = new long[80];
		float[] d = new float[80];
//		if ((c = (byte) inputStream.read()) != ReadBinaryFile.STATUS_ID) {
//			logger.fine("Found error" + c);
//		}
		logger.info("Calibration command"+(byte) inputStream.read());
		long len;
		try {
			len = (readNBytesAsInt(inputStream, 4));
			logger.info("Calibration length"+ len);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i =0;
		DataInputStream dis = new DataInputStream(inputStream);
		while( i < CalibrationData.numOfCoefficients) { 
			//while( i < 64) {
			try {
				d[i] = dis.readFloat();
				//c[i] = (readNBytesAsInt(inputStream, 4));
				//inputStream.read();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("--"+i+"th coefficeint " + d[i]);
			i+=1;
		}
		//System.out.println("session command byte"+ (byte)inputStream.read());
//		while( i < 40){
//			System.out.println("--"+i+"th byte"+ (byte)inputStream.read());
//			i=i+1;
//		}
		cal_data.setCalibrationCoefficients(d);
		return cal_data;
	}
	
	/**
	 * Reads the headerStream and returns the Session Header
	 * 
	 * @param headerStream
	 * @return
	 */
	public SessionHeader processHeader(ByteArrayInputStream headerStream) {
		// Using 359-003-025_Software_Design_Description.pdf as a reference
		// document
		SessionHeader header = new SessionHeader();
		byte c;
		if ((c = (byte) headerStream.read()) != ReadBinaryFile.STATUS_ID) {
			logger.fine("Found error" + c);
		}
		//logger.fine("Found error" + c);
		header.setSessionStartHeader(c);

		header.setLengthOfSession(readNBytesAsInt(headerStream, 4));

		header.setNumOfResistiveSamples(readNBytesAsInt(headerStream, 4));
		header.setNumOfResistivePages(readNBytesAsInt(headerStream, 2));
		header.setResistivePageSize(readNBytesAsInt(headerStream, 1));
		header.setLastResistivePageSize(readNBytesAsInt(headerStream, 1));

		header.setNumOfAccelSamples(readNBytesAsInt(headerStream, 4));
		header.setNumOfAccelPages(readNBytesAsInt(headerStream, 2));
		header.setAccelPageSize(readNBytesAsInt(headerStream, 1));
		header.setLastAccelPageSize(readNBytesAsInt(headerStream, 1));

		header.setNumOfGyroSamples(readNBytesAsInt(headerStream, 4));
		header.setNumOfGyroPages(readNBytesAsInt(headerStream, 2));
		header.setGyroPageSize(readNBytesAsInt(headerStream, 1));
		header.setLastGyroPageSize(readNBytesAsInt(headerStream, 1));

		header.setPatientMarkerNum((int) readNBytesAsInt(headerStream, 1));
		header.setRestartMarker((int) readNBytesAsInt(headerStream, 1));
		header.setCrc((int) readNBytesAsInt(headerStream, 2));

		logger.fine("Session StartHeader " + header.getSessionStartHeader());
		logger.fine("lengthOfSession " + header.getLengthOfSession());
		logger.fine("Resistive Samples " + header.getNumOfResistiveSamples());
		logger.fine("Resistive Pages " + header.getNumOfResistivePages());
		logger.fine("Resistive Page Size " + header.getResistivePageSize());
		logger.fine("Last Resistive Page Size "
				+ header.getLastResistivePageSize());

		logger.fine("Acceleration Samples " + header.getNumOfAccelSamples());
		logger.fine("Acceleration Pages " + header.getNumOfAccelPages());
		logger.fine("Acceleration Page Size " + header.getAccelPageSize());
		logger.fine("Last Acceleration Page Size "
				+ header.getLastAccelPageSize());

		logger.fine("Gyro Samples " + header.getNumOfGyroSamples());
		logger.fine("Gyro Pages " + header.getNumOfGyroPages());
		logger.fine("Gyro Page Size " + header.getGyroPageSize());
		logger.fine("Last Gyro Page Size " + header.getLastGyroPageSize());

		logger.fine("Patient Marker Num " + header.getPatientMarkerNum());
		logger.fine("Restart Marker " + header.getRestartMarker());
		logger.fine("Crc " + header.getCrc());
		return header;
	}

	@SuppressWarnings("unused")
	private static void writeToFile(ByteArrayOutputStream br, File outputF) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputF);

			int count = 0;
			for (byte m : br.toByteArray()) {
				Integer val = (m & 0xff); //
				// logger.fine(Integer.toHexString(val) + " "
				// +String.format("%02x", val));
				String outString = String.format("%02x", val);

				// Writing to file begins
				writer.write(outString /* + " " */);
				writer.flush();

				count++;
				if (count > 50) {
					count = 0;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 
	 * @param byteArrayInputStream
	 * @param header
	 * @throws Exception
	 */
	public SessionBody fetchSessionBody(
			ByteArrayInputStream byteArrayInputStream, SessionHeader header)
			throws Exception {
		// Angles Started
		byte[] resistiveTotalData = new byte[(int) (header
				.getNumOfResistivePages() * (header.getResistivePageSize()) + header
				.getLastResistivePageSize())];

		byteArrayInputStream.read(resistiveTotalData, 0,
				resistiveTotalData.length);

		int clockwiseAngles[] = this.getCompleteResistiveData(
				new ByteArrayInputStream(resistiveTotalData), header);
		//byteArrayInputStream.read(resistiveTotalData, 0,
			//	resistiveTotalData.length);
		//System.out.println("printing resistoive total data length" + resistiveTotalData.length*2/3);

		int counterClockwiseAngles[] = this.getCompleteResistiveData(
				new ByteArrayInputStream(resistiveTotalData), header);
		//System.out.println("resistive array lenght" + clockwiseAngles.length); 
		// display(clockwiseAngles);
		// System.out.println("First 3 angles" + clockwiseAngles[0] + " "
		// + clockwiseAngles[1] + " " + clockwiseAngles[2]);
		// display(counterClockwiseAngles);
		// System.out.println("First 3 angles" + counterClockwiseAngles[0] + " "
		// + counterClockwiseAngles[1] + " " + counterClockwiseAngles[2]);
		// Angles done

		// Acceleration started
		byte[] accelerationTotalData = new byte[(int) (header
				.getNumOfAccelPages() * (header.getAccelPageSize()) + header
				.getLastAccelPageSize())];
		byteArrayInputStream.read(accelerationTotalData, 0,
				accelerationTotalData.length);
		int[][] accelerationValues = getCompleteAccelerationData(
				new ByteArrayInputStream(accelerationTotalData), header);
		//System.out.println("acceleratino array lenght" + accelerationValues.length); 
		// Acceleration done
		
		// Gyro Started
		byte[] gyroTotalData = new byte[(int) (header.getNumOfGyroPages()
				* (header.getGyroPageSize()) + header.getLastGyroPageSize())];
		byteArrayInputStream.read(gyroTotalData, 0, gyroTotalData.length);
		int[][] gyroValues = getCompleteGyroData(new ByteArrayInputStream(
				gyroTotalData), header);
		//System.out.println("gyro array lenght" + gyroValues.length); 
		// TODO:Currently the value is set to 4, but in the document it is
		// mentioned as PatientMarker*4.Need to check with Peter
		long patientMarkerSampleIndex = readNBytesAsInt(byteArrayInputStream, 4);
		logger.fine("patientMarkerSampleIndex" + patientMarkerSampleIndex);
		// TODO:Currently the value is set to 4, but in the document it is
		// mentioned as RestartSampleIndex*4.Need to check with Peter
		long restartSampleIndex = readNBytesAsInt(byteArrayInputStream, 4);
		logger.fine("restartSampleIndex" + restartSampleIndex);

		SessionBody sessionBody = new SessionBody();
		sessionBody.setClockwiseAngles(clockwiseAngles);
		sessionBody.setCounterClockwiseAngles(counterClockwiseAngles);
		sessionBody.setAccelerationValues(accelerationValues);
		sessionBody.setGyroValues(gyroValues);
		sessionBody.setPatientMarkerSampleIndex(patientMarkerSampleIndex);
		sessionBody.setRestartSampleIndex(restartSampleIndex);
		return sessionBody;
	}

	/**
	 * This method takes in the complete byteArrayInputStreamData along with the
	 * session header and returns a 2D byte array consisting of Gyro values. Wx
	 * -- byte[][0] Wy -- byte[][1] Wz -- byte[][2]
	 * 
	 * @param byteArrayInputStream
	 * @param header
	 * @return 2D array consisting of the Gyro values.
	 */
	private int[][] getCompleteGyroData(
			ByteArrayInputStream byteArrayInputStream, SessionHeader header) {

		long gyroLastPageSize = 0;
		if (header.getLastGyroPageSize() != 0) {
			// Removing last 2 crc bytes.
			gyroLastPageSize = header.getLastGyroPageSize() - 2;
		}
		long calculatedNumberOfGyroBytes = (header.getNumOfGyroPages()
				* (header.getGyroPageSize() - 2) + gyroLastPageSize);

		logger.fine("checking gyro data match success: "
				+ (header.getNumOfGyroSamples() == calculatedNumberOfGyroBytes / 3 / 2));
		// This array is the data collected from the byteStream;
		byte[] gyroDataBufferArray = new byte[(int) calculatedNumberOfGyroBytes];
		getEntireDataFromBuffer(byteArrayInputStream,
				header.getNumOfGyroPages(), header.getGyroPageSize(),
				gyroLastPageSize, gyroDataBufferArray);
		// 2D array consisting of Wx,Wy and Wz stored in 0,1,2 location
		// This short array is desired to be of signed format.
		int[][] gyroValues = new int[(int) header.getNumOfGyroSamples()][3];
		int index = 0;
		ByteArrayInputStream gyroConsolidatedByteStream = new ByteArrayInputStream(
				gyroDataBufferArray);
		while (index < header.getNumOfGyroSamples()) {
			gyroValues[index][0] = (short) readNBytesAsInt(
					gyroConsolidatedByteStream, 2);
			gyroValues[index][1] = (short) readNBytesAsInt(
					gyroConsolidatedByteStream, 2);
			gyroValues[index][2] = (short) readNBytesAsInt(
					gyroConsolidatedByteStream, 2);
			index++;
		}
		return gyroValues;

	}

	/**
	 * This method takes in the complete byteArrayInputStreamData along with the
	 * session header and returns a 2D byte array consisting of Acceleration
	 * values. Ax -- byte[][0] Ay -- byte[][1] Az -- byte[][2]
	 * 
	 * @param byteArrayInputStream
	 * @param header
	 * @return 2D array consisting of the Acceleration values. These values are
	 *         not scaled as per those given by Matlab code.
	 */
	private int[][] getCompleteAccelerationData(
			ByteArrayInputStream byteArrayInputStream, SessionHeader header) {
		long accelLastPageSize = 0;
		if (header.getLastAccelPageSize() != 0) {
			// Removing last 2 crc bytes.
			accelLastPageSize = header.getLastAccelPageSize()-2;
		}
		long calculatedNumberOfAccelerationBytes = (header.getNumOfAccelPages()
				* (header.getAccelPageSize() - 2) + accelLastPageSize);
		logger.fine("Actual acceleration page size: " + calculatedNumberOfAccelerationBytes / 3);
		logger.fine("checking Acceleration data match success: " +calculatedNumberOfAccelerationBytes / 3);
		// This array is the data collected from the byteStream;
		byte[] accelerationDataBufferArray = new byte[(int) calculatedNumberOfAccelerationBytes];
		getEntireDataFromBuffer(byteArrayInputStream,
				header.getNumOfAccelPages(), header.getAccelPageSize(),
				accelLastPageSize, accelerationDataBufferArray);
		// 2D array consisting of Ax,Ay and Az stored in 0,1,2 location
		// This byte array is desired to be of signed format.
		int[][] accelerationValues = new int[(int) header
				.getNumOfAccelSamples()][3];
		int index = 0;
		float accelScaleFactor = 1;
		for (int i = 0; i + 3 <= accelerationDataBufferArray.length; index++) {
			accelerationValues[index][0] = (int) ((byte) (accelerationDataBufferArray[i++]) * accelScaleFactor);
			accelerationValues[index][1] = (int) ((byte) (accelerationDataBufferArray[i++]) * accelScaleFactor);
			accelerationValues[index][2] = (int) ((byte) (accelerationDataBufferArray[i++]) * accelScaleFactor);
		}
		return accelerationValues;
	}

	/**
	 * Displays the collected values
	 * 
	 * @param clockwiseAngles
	 */
	public void display(int[] clockwiseAngles) {
		// Displaying the resistive samples
		int linebreak = 0;
		for (int i = 0; i < clockwiseAngles.length; i++) {
			System.out.print(clockwiseAngles[i] + " ");
			linebreak++;
			if (linebreak > 50) {
				// logger.fine();
				linebreak = 0;
			}
		}
		System.out.println();
	}

	/**
	 * Reads the byteArrayInputStream and puts the resistive(angle) data into
	 * the resistive array and returns Analog values of the resistive values in
	 * the return of the method
	 * 
	 * @param byteArrayInputStream
	 * @param header
	 * @param resistiveArray
	 * @return integer array of the angle values after merging 3 bytes to get 2
	 *         12 bit Analog numbers
	 * @throws Exception
	 */
	public int[] getCompleteResistiveData(
			ByteArrayInputStream byteArrayInputStream, SessionHeader header)
			throws Exception {
		long lastPageSize = 0;
		if (header.getLastResistivePageSize() != 0) {
			lastPageSize = header.getLastResistivePageSize();
			// Removing the CRC code.
			//If odd number of samples, then remove only 1 byte for CRC, else 2 bytes for CRC.
			//Change made as per request by Professor on 5_10_2016
//			if (header.getNumOfResistiveSamples() % 2 == 1) {
//				lastPageSize -= 1;
//			} else {
//				lastPageSize -= 2;
//			}
			lastPageSize-=2;
			//lastPageSize=header.getLastResistivePageSize()-2;
		}
		long calculatedNumberOfResistiveBytes = (header
				.getNumOfResistivePages() * (header.getResistivePageSize() - 2) + lastPageSize);
		boolean flag = true;
		// Changing this on 5/8/2016 to accept odd resistive samples
		logger.fine("checking clockwise Resistive data match success: "
				+ (flag = header.getNumOfResistiveSamples() <= calculatedNumberOfResistiveBytes * 2 / 3));
		logger.fine("calculated number of resistives" + calculatedNumberOfResistiveBytes*2/3);
		if (!flag) {
			throw new Exception("File has incorrect resistive data");
		}
		byte[] resistiveArray = new byte[(int) calculatedNumberOfResistiveBytes];
		getEntireDataFromBuffer(byteArrayInputStream,
				header.getNumOfResistivePages(), header.getResistivePageSize(),
				lastPageSize, resistiveArray);
		return getAngleValues(new ByteArrayInputStream(resistiveArray),
				header.getNumOfResistiveSamples());
	}
	
	public byte[] getCompleteResistiveDataNew(
			ByteArrayInputStream byteArrayInputStream, SessionHeader header)
			throws Exception {
		long lastPageSize = 0;
		if (header.getLastResistivePageSize() != 0) {
			lastPageSize = header.getLastResistivePageSize();
			// Removing the CRC code.
			//If odd number of samples, then remove only 1 byte for CRC, else 2 bytes for CRC.
			//Change made as per request by Professor on 5_10_2016
			if (header.getNumOfResistiveSamples() % 2 == 1) {
				lastPageSize -= 1;
			} else {
				lastPageSize -= 2;
			}
		}
		long calculatedNumberOfResistiveBytes = (header
				.getNumOfResistivePages() * (header.getResistivePageSize() - 2) + lastPageSize);
		boolean flag = true;
		// Changing this on 5/8/2016 to accept odd resistive samples
		logger.fine("checking clockwise Resistive data match success: "
				+ (flag = header.getNumOfResistiveSamples() <= calculatedNumberOfResistiveBytes * 2 / 3));
		if (!flag) {
			throw new Exception("File has incorrect resistive data");
		}
		byte[] resistiveArray = new byte[(int) calculatedNumberOfResistiveBytes];
		getEntireDataFromBuffer(byteArrayInputStream,
				header.getNumOfResistivePages(), header.getResistivePageSize(),
				lastPageSize, resistiveArray);
		return resistiveArray;
	}

	/**
	 * Reads the byteArrayInputStream to gather the complete data. This data is
	 * stored in the dataArray.
	 * 
	 * @param byteArrayInputStream
	 * @param numberOfPages
	 * @param pageSize
	 * @param lastPageSize
	 * @param dataArray
	 */
	public void getEntireDataFromBuffer(
			ByteArrayInputStream byteArrayInputStream, long numberOfPages,
			long pageSize, long lastPageSize, byte[] dataArray) {
		int offset = 0;
		// To get Data if there are any pages.
		// This code is assuming that for incomplete pagesize present in the
		// first one, the number of Pages will be 0
		for (int i = 0; i < numberOfPages; i++) {
			// To avoid the last 2 bytes
			byteArrayInputStream.read(dataArray, offset, (int) (pageSize - 2));
			// The below 2 are for checksums
			byteArrayInputStream.read();
			byteArrayInputStream.read();
			offset += pageSize - 2;
		}
		if (lastPageSize != 0) {
			// Remove the CRC code. commenting as the last page size already fetched from previous calls
		//	lastPageSize -= 2;
			// to get the remaining resistive pages
			byteArrayInputStream.read(dataArray, offset, (int) lastPageSize);

		}
	}

	/**
	 * Used to create an array of the angle values based on the 12 bits
	 * representation TODO: need to create junit test case and test TODO:
	 * Currently coded without CRC check created in code.
	 * 
	 * @param byteArrayInputStream
	 * @param numOfResistiveSamples
	 * @return integer array consisting of the 12 bit Angle values collected
	 *         from the sensor.This method will be used for clockwise as well as
	 *         counterclockwise
	 */
	public static int[] getAngleValues(
			ByteArrayInputStream byteArrayInputStream,
			long numOfResistiveSamples) {
		int i = 0;
		int angleValues[] = new int[(int) (numOfResistiveSamples)];
		do {
			int val[] = getTwoAngleValues(byteArrayInputStream);
			angleValues[i++] = val[0];
			if (i < numOfResistiveSamples) {
				angleValues[i++] = val[1];
			}
		} while (i < numOfResistiveSamples);
		return angleValues;
	}

	/**
	 * This method takes in 3 bytes and returns 2 12 bit ADC values which are
	 * basically acceleration values
	 * 
	 * @param byteArrayInputStream
	 * @return integer array of length 2 containing 2 12 bit numbers
	 */
	public static int[] getTwoAngleValues(
			ByteArrayInputStream byteArrayInputStream) {
		int val1 = 0;
		val1 = byteArrayInputStream.read();
		int temp = byteArrayInputStream.read();
		val1 = (((temp & 0xF0) >> 4) | (val1 << 4));
		int val2 = (temp & 0x0F) << 8 | byteArrayInputStream.read();
		return new int[] { val1, val2 };
	}

	/**
	 * This method takes in a byteArrayInputStream and based on the value of the
	 * variable number, the long representation of the number is returned.The
	 * long value is returned since if 4 is the number accepted, then 32 bit
	 * value int would change the values of the sign bit
	 * 
	 * @param byteArrayInputStream
	 * @param number
	 * @return long representation of the 'number' of the bytes.
	 */
	private static long readNBytesAsInt(
			ByteArrayInputStream byteArrayInputStream, int number) {
		if (number < 1 || number > 4) {
			logger.fine("ERROR");
			// throw new Exception();
		}
		long val = 0;
		for (int i = 0; i < number; i++) {
			int interVal = (byteArrayInputStream.read() & 0xFF);
			// logger.fine(interVal);
			val <<= 8;
			// interVal= (interVal<<(8*i));
			val |= interVal;
		}
		// logger.fine(val);
		return val;
	}

	/**
	 * Not used anymore - kept here for reference
	 * 
	 * @param data
	 * @param n
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private int ReadNBytesAsInt(InputStream data, int n) throws Exception {
		// We need to read n bytes and convert that to an int
		if (n > 4 || n < 1)
			throw new Exception();
		int val = 0;
		for (int i = 0; i < n; i++) {
			int newVal = data.read() << 8 * i;
			val |= newVal;
		}
		return val;
	}

}
