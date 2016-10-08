package com.jmm.report.domain;


public class ReportDomain {
	private PatientInfo patientInfo;
	private String alertStrings[];
	private String answerArrayFromMobileApp[];
	private boolean responseWithinRange[];
	private byte[] kneeImageBufferArray;
	private String tagDateTime;
	private boolean[] withinRange;

	/**
	 * @return the patientDbValues
	 */
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	/**
	 * @param patientDbValues
	 *            the patientDbValues to set
	 */
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	/**
	 * @return the alertStrings
	 */
	public String[] getAlertStrings() {
		return alertStrings;
	}

	/**
	 * @param alertStrings
	 *            the alertStrings to set
	 */
	public void setAlertStrings(String[] alertStrings) {
		this.alertStrings = alertStrings;
	}

	/**
	 * @return the answerArrayFromMobileApp
	 */
	public String[] getAnswerArrayFromMobileApp() {
		return answerArrayFromMobileApp;
	}

	/**
	 * @param answerArrayFromMobileApp
	 *            the answerArrayFromMobileApp to set
	 */
	public void setAnswerArrayFromMobileApp(String[] answerArrayFromMobileApp) {
		this.answerArrayFromMobileApp = answerArrayFromMobileApp;
	}

	/**
	 * @return the responseWithinRange
	 */
	public boolean[] getResponseWithinRange() {
		return responseWithinRange;
	}

	/**
	 * @param responseWithinRange
	 *            the responseWithinRange to set
	 */
	public void setResponseWithinRange(boolean[] responseWithinRange) {
		this.responseWithinRange = responseWithinRange;
	}

	/**
	 * @return the kneeImageBufferArray
	 */
	public byte[] getKneeImageBufferArray() {
		return kneeImageBufferArray;
	}

	/**
	 * @param kneeImageBufferArray
	 *            the kneeImageBufferArray to set
	 */
	public void setKneeImageBufferArray(byte[] kneeImageBufferArray) {
		this.kneeImageBufferArray = kneeImageBufferArray;
	}

	/**
	 * @return the kneeFunctionValues
	 *//*
	public int[] getKneeFunctionValues() {
		return kneeFunctionValues;
	}

	*//**
	 * @param kneeFunctionValues
	 *            the kneeFunctionValues to set
	 *//*
	public void setKneeFunctionValues(int[] kneeFunctionValues) {
		this.kneeFunctionValues = kneeFunctionValues;
	}

	*//**
	 * @return the previousKneeFunctionValues
	 *//*
	public int[] getPreviousKneeValues() {
		return previousKneeValues;
	}

	*//**
	 * @param previousKneeFunctionValues
	 *            the previousKneeFunctionValues to set
	 *//*
	public void setPreviousKneeValues(
			int[] previousKneeFunctionValues) {
		this.previousKneeValues = previousKneeFunctionValues;
	}

	*//**
	 * @return the minKneeFunctionValues
	 *//*
	public int[] getMinKneeValues() {
		return minKneeValues;
	}

	*//**
	 * @param minKneeFunctionValues
	 *            the minKneeFunctionValues to set
	 *//*
	public void setMinKneeValues(int[] minKneeFunctionValues) {
		this.minKneeValues = minKneeFunctionValues;
	}

	*//**
	 * @return the maxKneeFunctionValues
	 *//*
	public int[] getMaxKneeValues() {
		return maxKneeValues;
	}

	*//**
	 * @param maxKneeFunctionValues
	 *            the maxKneeFunctionValues to set
	 *//*
	public void setMaxKneeValues(int[] maxKneeFunctionValues) {
		this.maxKneeValues = maxKneeFunctionValues;
	}
*/
	/**
	 * @return the tagDateTime
	 */
	public String getTagDateTime() {
		return tagDateTime;
	}

	/**
	 * @param tagDateTime
	 *            the tagDateTime to set
	 */
	public void setTagDateTime(String tagDateTime) {
		this.tagDateTime = tagDateTime;
	}

	/**
	 * @return the withinRange
	 */
	public boolean[] getWithinRange() {
		return withinRange;
	}
	/**
	 * @param withinRange
	 *            the withinRange to set
	 */
	public void setWithinRangeArray(boolean[] withinRange) {
		this.withinRange=withinRange;
	}


}
