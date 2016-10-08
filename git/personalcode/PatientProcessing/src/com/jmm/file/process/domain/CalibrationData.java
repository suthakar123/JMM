package com.jmm.file.process.domain;

public class CalibrationData {
	private float[] calibrationCoefficients;
	public static final long numOfCoefficients = 12 ;
	public float[] getCalibrationCoefficients() {
		return calibrationCoefficients;
	}
	public void setCalibrationCoefficients(float[] calibrationCoefficients) {
		this.calibrationCoefficients = calibrationCoefficients;
	}
}