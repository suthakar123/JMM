package com.jmm.report.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jmm.report.domain.PatientInfo;
import com.jmm.report.domain.ReportDomain;
import com.jmm.report.view.ActivitySection;
import com.jmm.report.view.AlertSection;
import com.jmm.report.view.FooterSection;
import com.jmm.report.view.HeaderSection;
import com.jmm.report.view.KneeSection;
import com.jmm.report.view.PatientResponseSection;

public class ReportGeneratorController {

	private ReportDomain reportDomain;

	/*
	 * String currDate = String.valueOf(DateFormat.getDateInstance().format( new
	 * Date()));
	 */
	/*
	 * String patientDbValues[] = { "Jack", "1001", "Peter", "HealthCare",
	 * currDate, "12.3 hours", "41 days post-TKA", "April 18 2013", "04/18" };
	 */
	// String postOperationDays = "41 days post-TKA";
	/*
	 * String alerts[] = { "Walking has decreased over last week",
	 * "I am currently adding a lot of text.",
	 * "To see how the sizing options will work",
	 * "Patient experiencing Unexpected SideEffects",
	 * "Unexpected Fevers/Chills/Sweat Noticed", "Lot of Pain Noticed" };
	 */
	// Currently Hardcoded. Later will be fetched from Mobile App
	/*
	 * String answerArrayFromMobileApp[] = { "9.6", "N", "N", "NA", "1.2", "A",
	 * "8.8", "N", "N", "Y" };
	 */
	// This value will come from the Database AlertNorms table and will be used
	// to compare with actual values to give correct colors. This needs to be
	// filled up in model
	/*
	 * boolean responseWithinRange[] = { true, true, false, false, true, false,
	 * false, true, true, true };
	 */
	// byte[] kneeImageBufferArray = null;
	/* int[] kneeFunctionValues = { 48, 6, 524, 31 }; */
	/*
	 * double[] previousKneeFunctionValues = { 0, 0, 0, 0 }; double[]
	 * minKneeFunctionValues = { 65, 15, 1750, 40 }; double[]
	 * maxKneeFunctionValues = { 85, 0, 4500, 50 };
	 */
	/* String tagDateTime = null; */
	/*
	 * // A map to represent the number of hours spent on each activity
	 * Map<String, Double> activityListMap = new HashMap<String, Double>();
	 * Map<String, Double> previousActivityDurationMap = new HashMap<String,
	 * Double>(); // A map to represent the number of hours spent on each
	 * activity Map<String, Double> activityMap = new HashMap<String, Double>();
	 */
	public ReportGeneratorController(ReportDomain reportDomain) {

		this.reportDomain = reportDomain;
		/*
		 * activityMap.put("Active", (double) 3.5); activityMap.put("Inactive",
		 * (double) 8.1);
		 * 
		 * activityListMap.put("Standing", (double) 2);
		 * activityListMap.put("Sitting", (double) 6);
		 * activityListMap.put("Walking", (double) .2);
		 * activityListMap.put("Running", (double) 2.1);
		 * activityListMap.put("Exercise", (double) 1.1);
		 * activityListMap.put("Bike", (double) 0.1);
		 * activityListMap.put("Lying", (double) 0.1);
		 * 
		 * previousActivityDurationMap.put("Standing", (double) 0);
		 * previousActivityDurationMap.put("Sitting", (double) 0);
		 * previousActivityDurationMap.put("Walking", (double) 0);
		 * previousActivityDurationMap.put("Running", (double) 0);
		 * previousActivityDurationMap.put("Exercise", (double) 0);
		 * previousActivityDurationMap.put("Bike", (double) 0);
		 * previousActivityDurationMap.put("Lying", (double) 0);
		 */
		/* tagDateTime = "Tagged: April 18, 2013 9:11 PM"; */

	}

	public void invokeController(Document document, PdfWriter writer)
			throws Exception {
		PdfContentByte canvas = writer.getDirectContent();

		PatientInfo patientInfo = reportDomain.getPatientInfo();

		HeaderSection header = new HeaderSection(patientInfo);
		PdfPTable headerSection = header.getHeaderSection();
		document.add(headerSection);

		AlertSection alertSection = new AlertSection(
				reportDomain.getAlertStrings());
		PdfPTable alertSectionTable = alertSection.getAlertSection();
		document.add(alertSectionTable);

		PatientResponseSection prs = new PatientResponseSection(
				reportDomain.getAnswerArrayFromMobileApp(),
				reportDomain.getWithinRange(),
				reportDomain.getKneeImageBufferArray(),
				reportDomain.getTagDateTime());
		PdfPTable patientResponseTable = prs.getPatientResponseSection();
		document.add(patientResponseTable);

		KneeSection kneeSection = new KneeSection(canvas,
				patientInfo.getCurrentKneeValues(),
				patientInfo.getPreviousKneeValues(),
				patientInfo.getMinKneeValues(), patientInfo.getMaxKneeValues());
		PdfPTable kneeSectionTable = kneeSection.getKneeSection();
		document.add(kneeSectionTable);

		ActivitySection activitySection = new ActivitySection(canvas,
				patientInfo.getActivityListMap(),
				patientInfo.getPreviousActivityDurationMap(),
				patientInfo.getActivityMap());
		PdfPTable activityTable = activitySection.getActivitySection();
		document.add(activityTable);
		FooterSection footerSection = new FooterSection(reportDomain
				.getPatientInfo().getPostOperationDays());
		PdfPTable footerData = footerSection.getFooterData();
		document.add(footerData);

	}
}
