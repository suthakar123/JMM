package com.jmm.report.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// This class contains all of the info for the given patient
public class PatientInfo {

	public final int NUM_ACTIVITIES = 9;
	public final int KNEE_MEASUREMENTS = 4;

	private String patientName;
	private String patientID;
	private String sensorId;
	private String treeId;
	private String surgeonName;
	private String institutionName;
	private Date dateOfSurgery;
	private int daysSinceSurgery;
	private int weekSinceSurgery;
	private String postOperationDays;
	private double totalDuration;
	private int[] currentKneeValues;
	private int[] minKneeValues;
	private int[] maxKneeValues;
	private int[] previousKneeValues;
	private String previousReportsString;
	private String studySide;
	private String reportFileName;
	private String reportFilePath;
	private String zipFileName;
	
	// A map to represent the number of hours spent on each activity
	private Map<String, Double> activityListMap = new HashMap<String, Double>();
	// A map to represent the number of hours spent on each activity in the previous week
	private Map<String, Double> previousActivityDurationMap = new HashMap<String, Double>();
	// A map to represent the number of hours spent actively or inactively.
	private Map<String, Double> activityMap = new HashMap<String, Double>();

	
	/**
	 * @return the name
	 */
	public String getName() {
		return patientName;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.patientName = name;
	}
	/**
	 * @return the sensorId
	 */
	public String getSensorId() {
		return sensorId;
	}
	/**
	 * @param sensorId the sensorId to set
	 */
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	/**
	 * @return the treeId
	 */
	public String getTreeId() {
		return treeId;
	}
	/**
	 * @param treeId the treeId to set
	 */
	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}
	/**
	 * @return the surgeonName
	 */
	public String getSurgeonName() {
		return surgeonName;
	}
	/**
	 * @param surgeonName the surgeonName to set
	 */
	public void setSurgeonName(String surgeonName) {
		this.surgeonName = surgeonName;
	}
	/**
	 * @return the institutionName
	 */
	public String getInstitutionName() {
		return institutionName;
	}
	/**
	 * @param institutionName the institutionName to set
	 */
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	/**
	 * @return the dateOfSurgery
	 */
	public Date getDateOfSurgery() {
		return dateOfSurgery;
	}
	/**
	 * @param dateOfSurgery the dateOfSurgery to set
	 */
	public void setDateOfSurgery(Date dateOfSurgery) {
		this.dateOfSurgery = dateOfSurgery;
	}
	/**
	 * @return the daysSinceSurgery
	 */
	public int getDaysSinceSurgery() {
		return daysSinceSurgery;
	}
	/**
	 * @param daysSinceSurgery the daysSinceSurgery to set
	 */
	public void setDaysSinceSurgery(int daysSinceSurgery) {
		this.daysSinceSurgery = daysSinceSurgery;
	}
	/**
	 * @return the minKneeValues
	 */
	public int[] getMinKneeValues() {
		return minKneeValues;
	}
	/**
	 * @param minKneeValues the minKneeValues to set
	 */
	public void setMinKneeValues(int[] minKneeValues) {
		this.minKneeValues = minKneeValues;
	}
	/**
	 * @return the maxKneeValues
	 */
	public int[] getMaxKneeValues() {
		return maxKneeValues;
	}
	/**
	 * @param maxKneeValues the maxKneeValues to set
	 */
	public void setMaxKneeValues(int[] maxKneeValues) {
		this.maxKneeValues = maxKneeValues;
	}
	/**
	 * @return the previousKneeValues
	 */
	public int[] getPreviousKneeValues() {
		return previousKneeValues;
	}
	/**
	 * @param previousKneeValues the previousKneeValues to set
	 */
	public void setPreviousKneeValues(int[] previousKneeValues) {
		this.previousKneeValues = previousKneeValues;
	}
	/**
	 * @return the previousReportsString
	 */
	public String getPreviousReportsString() {
		return previousReportsString;
	}
	/**
	 * @param previousReportsString the previousReportsString to set
	 */
	public void setPreviousReportsString(String previousReportsString) {
		this.previousReportsString = previousReportsString;
	}
	/**
	 * @return the studySide
	 */
	public String getStudySide() {
		return studySide;
	}
	/**
	 * @param studySide the studySide to set
	 */
	public void setStudySide(String studySide) {
		this.studySide = studySide;
	}
	/**
	 * @return the weekSinceSurgery
	 */
	public int getWeekSinceSurgery() {
		return weekSinceSurgery;
	}
	/**
	 * @param weekSinceSurgery the weekSinceSurgery to set
	 */
	public void setWeekSinceSurgery(int weekSinceSurgery) {
		this.weekSinceSurgery = weekSinceSurgery;
	}
	/**
	 * @return the patientID
	 */
	public String getPatientID() {
		return patientID;
	}
	/**
	 * @param patientID the patientID to set
	 */
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	/**
	 * @return the duration
	 */
	public double getTotalDuration() {
		return totalDuration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setTotalDuration(double duration) {
		this.totalDuration = duration;
	}
	/**
	 * @return the postOperationDays
	 */
	public String getPostOperationDays() {
		return postOperationDays;
	}
	/**
	 * @param postOperationDays the postOperationDays to set
	 */
	public void setPostOperationDays(String postOperationDays) {
		this.postOperationDays = postOperationDays;
	}
	/**
	 * @return the currentKneeValues
	 */
	public int[] getCurrentKneeValues() {
		return currentKneeValues;
	}
	/**
	 * @param currentKneeValues the currentKneeValues to set
	 */
	public void setCurrentKneeValues(int[] currentKneeValues) {
		this.currentKneeValues = currentKneeValues;
	}

	/**
	 * @return the activityListMap
	 */
	public Map<String, Double> getActivityListMap() {
		return activityListMap;
	}

	/**
	 * @param activityListMap
	 *            the activityListMap to set
	 */
	public void setActivityListMap(Map<String, Double> activityListMap) {
		this.activityListMap = activityListMap;
	}

	/**
	 * @return the previousActivityDurationMap
	 */
	public Map<String, Double> getPreviousActivityDurationMap() {
		return previousActivityDurationMap;
	}

	/**
	 * @param previousActivityDurationMap
	 *            the previousActivityDurationMap to set
	 */
	public void setPreviousActivityDurationMap(
			Map<String, Double> previousActivityDurationMap) {
		this.previousActivityDurationMap = previousActivityDurationMap;
	}

	/**
	 * @return the activityMap
	 */
	public Map<String, Double> getActivityMap() {
		return activityMap;
	}

	/**
	 * @param activityMap
	 *            the activityMap to set
	 */
	public void setActivityMap(Map<String, Double> activityMap) {
		this.activityMap = activityMap;
	}
	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}
	/**
	 * @param patientName the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	/**
	 * @return the reportFileName
	 */
	public String getReportFileName() {
		return reportFileName;
	}
	/**
	 * @param reportFileName the reportFileName to set
	 */
	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}
	/**
	 * @return the zipFileName
	 */
	public String getZipFileName() {
		return zipFileName;
	}
	/**
	 * @param zipFileName the zipFileName to set
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}
	/**
	 * @return the reportFilePath
	 */
	public String getReportFilePath() {
		return reportFilePath;
	}
	/**
	 * @param reportFilePath the reportFilePath to set
	 */
	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}
}
