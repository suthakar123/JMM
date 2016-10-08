/**
 * 
 */
package com.jmm.file.process.domain;

/**
 * @author jayaram
 * 
 */
/**
 * @author jayaram
 * 
 */
public class SessionBody {
	/**
	 * 
	 */
	private int[] clockwiseAngles;
	/**
	 * 
	 */
	private int[] counterClockwiseAngles;
	/**
	 * 
	 */
	private int[][] accelerationValues;
	/**
	 * 
	 */
	private int[][] gyroValues;
	/**
	 * 
	 */
	private long patientMarkerSampleIndex;
	/**
	 * 
	 */
	private long restartSampleIndex;

	/**
	 * @param clockwiseAngles
	 */
	public void setClockwiseAngles(int[] clockwiseAngles) {
		this.clockwiseAngles = clockwiseAngles;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getClockwiseAngles() {
		return clockwiseAngles;
	}

	/**
	 * @param counterClockwiseAngles
	 */
	public void setCounterClockwiseAngles(int[] counterClockwiseAngles) {
		this.counterClockwiseAngles = counterClockwiseAngles;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getCounterClockwiseAngles() {
		return counterClockwiseAngles;
	}

	/**
	 * @param accelerationValues
	 */
	public void setAccelerationValues(int[][] accelerationValues) {
		this.accelerationValues = accelerationValues;
	}

	/**
	 * 
	 * @return
	 */
	public int[][] getAccelerationValues() {
		return accelerationValues;
	}

	/**
	 * @param gyroValues
	 */
	public void setGyroValues(int[][] gyroValues) {
		this.gyroValues = gyroValues;
	}

	/**
	 * 
	 * @return
	 */
	public int[][] getGyroValues() {
		return gyroValues;

	}

	/**
	 * @param patientMarkerSampleIndex
	 */
	public void setPatientMarkerSampleIndex(long patientMarkerSampleIndex) {
		this.patientMarkerSampleIndex = patientMarkerSampleIndex;
	}

	/**
	 * @return
	 */
	public long getPatientMarkerSampleIndex() {
		return patientMarkerSampleIndex;
	}

	/**
	 * @param restartSampleIndex
	 */
	public void setRestartSampleIndex(long restartSampleIndex) {
		this.restartSampleIndex = restartSampleIndex;
	}

	/**
	 * @return
	 */
	public long getRestartSampleIndex() {
		return restartSampleIndex;
	}

}
