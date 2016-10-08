/**
 * 
 */
package com.jmm.database.process.domain;

import com.jmm.report.domain.PatientInfo;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 * @author jayaram
 * 
 */
public class DatabaseDomain {

	private PatientInfo patientInfo;
	private int err;
	private MWNumericArray angleCalibrationFromDataBase;
	private MWNumericArray gyroCalibrationFromDataBase;
	private MWNumericArray accelerationCalibrationFromDataBase;
	private MWNumericArray treeDataFromDataBase;
	private String treeFilePath;

	/**
	 * @return the patientInfo
	 */
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	/**
	 * @param patientInfo
	 *            the patientInfo to set
	 */
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	/**
	 * @return the err
	 */
	public int getErr() {
		return err;
	}

	/**
	 * @param err
	 *            the err to set
	 */
	public void setErr(int err) {
		this.err = err;
	}

	/**
	 * @return the angleCalibrationFromDataBase
	 */
	public MWNumericArray getAngleCalibrationFromDataBase() {
		return angleCalibrationFromDataBase;
	}

	/**
	 * @param angleCalibrationFromDataBase
	 *            the angleCalibrationFromDataBase to set
	 */
	public void setAngleCalibrationFromDataBase(
			MWNumericArray angleCalibrationFromDataBase) {
		this.angleCalibrationFromDataBase = angleCalibrationFromDataBase;
	}

	/**
	 * @return the gyroCalibrationFromDataBase
	 */
	public MWNumericArray getGyroCalibrationFromDataBase() {
		return gyroCalibrationFromDataBase;
	}

	/**
	 * @param gyroCalibrationFromDataBase
	 *            the gyroCalibrationFromDataBase to set
	 */
	public void setGyroCalibrationFromDataBase(
			MWNumericArray gyroCalibrationFromDataBase) {
		this.gyroCalibrationFromDataBase = gyroCalibrationFromDataBase;
	}

	/**
	 * @return the accelerationCalibrationFromDataBase
	 */
	public MWNumericArray getAccelerationCalibrationFromDataBase() {
		return accelerationCalibrationFromDataBase;
	}

	/**
	 * @param accelerationCalibrationFromDataBase
	 *            the accelerationCalibrationFromDataBase to set
	 */
	public void setAccelerationCalibrationFromDataBase(
			MWNumericArray accelerationCalibrationFromDataBase) {
		this.accelerationCalibrationFromDataBase = accelerationCalibrationFromDataBase;
	}

	/**
	 * @return the treeDataFromDataBase
	 */
	public MWNumericArray getTreeDataFromDataBase() {
		return treeDataFromDataBase;
	}

	/**
	 * @param treeDataFromDataBase
	 *            the treeDataFromDataBase to set
	 */
	public void setTreeDataFromDataBase(MWNumericArray treeDataFromDataBase) {
		this.treeDataFromDataBase = treeDataFromDataBase;
	}

	/**
	 * @return the treeDataFromDataBase
	 */
	public String getTreeFilePath() {
		return treeFilePath;
	}

	/**
	 * 
	 * @param treeFilePath
	 */
	public void setTreeFilePath(String treeFilePath) {
		this.treeFilePath = treeFilePath;
	}
}
