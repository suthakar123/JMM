package com.jmm.file.process.domain;

/**
 * @author jayaram
 *
 */
/**
 * @author jayaram
 *
 */
public class SessionHeader {
	private byte sessionStartHeader;
	private long lengthOfSession;
	private long numOfResistiveSamples;
	private long numOfResistivePages;
	private long resistivePageSize;
	private long lastResistivePageSize;

	private long numOfAccelSamples;
	private long numOfAccelPages;
	private long accelPageSize;
	private long lastAccelPageSize;

	private long numOfGyroSamples;
	private long numOfGyroPages;
	private long gyroPageSize;
	private long lastGyroPageSize;

	private int patientMarkerNum;
	private int restartMarker;
	private int crc;
	/**
	 * @return the sessionStartHeader
	 */
	public byte getSessionStartHeader() {
		return sessionStartHeader;
	}
	/**
	 * @param sessionStartHeader the sessionStartHeader to set
	 */
	public void setSessionStartHeader(byte sessionStartHeader) {
		this.sessionStartHeader = sessionStartHeader;
	}
	/**
	 * @return the lengthOfSession
	 */
	public long getLengthOfSession() {
		return lengthOfSession;
	}
	/**
	 * @param lengthOfSession the lengthOfSession to set
	 */
	public void setLengthOfSession(long lengthOfSession) {
		this.lengthOfSession = lengthOfSession;
	}
	/**
	 * @return the numOfResistiveSamples
	 */
	public long getNumOfResistiveSamples() {
		return numOfResistiveSamples;
	}
	/**
	 * @param numOfResistiveSamples the numOfResistiveSamples to set
	 */
	public void setNumOfResistiveSamples(long numOfResistiveSamples) {
		this.numOfResistiveSamples = numOfResistiveSamples;
	}
	/**
	 * @return the numOfResistivePages
	 */
	public long getNumOfResistivePages() {
		return numOfResistivePages;
	}
	/**
	 * @param numOfResistivePages the numOfResistivePages to set
	 */
	public void setNumOfResistivePages(long numOfResistivePages) {
		this.numOfResistivePages = numOfResistivePages;
	}
	/**
	 * @return the resistivePageSize
	 */
	public long getResistivePageSize() {
		return resistivePageSize;
	}
	/**
	 * @param resistivePageSize the resistivePageSize to set
	 */
	public void setResistivePageSize(long resistivePageSize) {
		this.resistivePageSize = resistivePageSize;
	}
	/**
	 * @return the lastResistivePageSize
	 */
	public long getLastResistivePageSize() {
		return lastResistivePageSize;
	}
	/**
	 * @param lastResistivePageSize the lastResistivePageSize to set
	 */
	public void setLastResistivePageSize(long lastResistivePageSize) {
		this.lastResistivePageSize = lastResistivePageSize;
	}
	/**
	 * @return the numOfAccelSamples
	 */
	public long getNumOfAccelSamples() {
		return numOfAccelSamples;
	}
	/**
	 * @param numOfAccelSamples the numOfAccelSamples to set
	 */
	public void setNumOfAccelSamples(long numOfAccelSamples) {
		this.numOfAccelSamples = numOfAccelSamples;
	}
	/**
	 * @return the numOfAccelPages
	 */
	public long getNumOfAccelPages() {
		return numOfAccelPages;
	}
	/**
	 * @param numOfAccelPages the numOfAccelPages to set
	 */
	public void setNumOfAccelPages(long numOfAccelPages) {
		this.numOfAccelPages = numOfAccelPages;
	}
	/**
	 * @return the accelPageSize
	 */
	public long getAccelPageSize() {
		return accelPageSize;
	}
	/**
	 * @param accelPageSize the accelPageSize to set
	 */
	public void setAccelPageSize(long accelPageSize) {
		this.accelPageSize = accelPageSize;
	}
	/**
	 * @return the lastAccelPageSize
	 */
	public long getLastAccelPageSize() {
		return lastAccelPageSize;
	}
	/**
	 * @param lastAccelPageSize the lastAccelPageSize to set
	 */
	public void setLastAccelPageSize(long lastAccelPageSize) {
		this.lastAccelPageSize = lastAccelPageSize;
	}
	/**
	 * @return the numOfGyroSamples
	 */
	public long getNumOfGyroSamples() {
		return numOfGyroSamples;
	}
	/**
	 * @param numOfGyroSamples the numOfGyroSamples to set
	 */
	public void setNumOfGyroSamples(long numOfGyroSamples) {
		this.numOfGyroSamples = numOfGyroSamples;
	}
	/**
	 * @return the numOfGyroPages
	 */
	public long getNumOfGyroPages() {
		return numOfGyroPages;
	}
	/**
	 * @param numOfGyroPages the numOfGyroPages to set
	 */
	public void setNumOfGyroPages(long numOfGyroPages) {
		this.numOfGyroPages = numOfGyroPages;
	}
	/**
	 * @return the gyroPageSize
	 */
	public long getGyroPageSize() {
		return gyroPageSize;
	}
	/**
	 * @param gyroPageSize the gyroPageSize to set
	 */
	public void setGyroPageSize(long gyroPageSize) {
		this.gyroPageSize = gyroPageSize;
	}
	/**
	 * @return the lastGyroPageSize
	 */
	public long getLastGyroPageSize() {
		return lastGyroPageSize;
	}
	/**
	 * @param lastGyroPageSize the lastGyroPageSize to set
	 */
	public void setLastGyroPageSize(long lastGyroPageSize) {
		this.lastGyroPageSize = lastGyroPageSize;
	}
	/**
	 * @return the patientMarkerNum
	 */
	public int getPatientMarkerNum() {
		return patientMarkerNum;
	}
	/**
	 * @param patientMarkerNum the patientMarkerNum to set
	 */
	public void setPatientMarkerNum(int patientMarkerNum) {
		this.patientMarkerNum = patientMarkerNum;
	}
	/**
	 * @return the restartMarker
	 */
	public int getRestartMarker() {
		return restartMarker;
	}
	/**
	 * @param restartMarker the restartMarker to set
	 */
	public void setRestartMarker(int restartMarker) {
		this.restartMarker = restartMarker;
	}
	/**
	 * @return the crc
	 */
	public int getCrc() {
		return crc;
	}
	/**
	 * @param crc the crc to set
	 */
	public void setCrc(int crc) {
		this.crc = crc;
	}
}
