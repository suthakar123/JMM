package com.pdf.test.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdf.test.view.ActivitySection;
import com.pdf.test.view.AlertSection;
import com.pdf.test.view.FooterSection;
import com.pdf.test.view.HeaderSection;
import com.pdf.test.view.KneeSection;
import com.pdf.test.view.PatientResponseSection;

public class ReportGeneratorController {
	String currDate = String.valueOf(DateFormat.getDateInstance().format(
			new Date()));
	String patientDbValues[] = { "Jack", "1001", "Peter", "HealthCare",
	/* currDate, */"12.3 hours", "41 days post-TKA", "April 18 2013", "04/18" };

	String postOperationDays = "41 days post-TKA";
	String alerts[] = { "Walking has decreased over last week",
	/* "I am currently adding a lot of text.", */
	"To see how the sizing options will work",
			"Patient experiencing Unexpected SideEffects",
			"Unexpected Fevers/Chills/Sweat Noticed", "Lot of Pain Noticed" };
	// Currently Hardcoded. Later will be fetched from Mobile App
	String answerArrayFromMobileApp[] = { "9.6", "N", "N", "NA", "1.2", "A",
			"8.8", "N", "N", "Y" };
	// This value will come from the Database AlertNorms table and will be used
	// to compare with actual values to give correct colors. This needs to be
	// filled up in model
	boolean responseWithinRange[] = { true, true, false, false, true, false,
			false, true, true, true };
	String imageName = "photo.jpg";

	int[] kneeFunctionValues = { 48, 6, 524, 31 };
	double[] previousKneeFunctionValues = { 0, 0, 0, 0 };
	double[] minKneeFunctionValues = { 65, 15, 1750, 40 };
	double[] maxKneeFunctionValues = { 85, 0, 4500, 50 };
	String tagDateTime = null;
	// A map to represent the number of hours spent on each activity
	Map<String, Double> activityListMap = new HashMap<String, Double>();
	Map<String, Double> previousActivityDurationMap = new HashMap<String, Double>();
	// A map to represent the number of hours spent on each activity
	Map<String, Double> activityMap = new HashMap<String, Double>();

	public ReportGeneratorController() {
		activityMap.put("Active", (double) 3.5);
		activityMap.put("Inactive", (double) 8.1);

		activityListMap.put("Standing", (double) 2);
		activityListMap.put("Sitting", (double) 6);
		activityListMap.put("Walking", (double) .2);
		activityListMap.put("Running", (double) 2.1);
		activityListMap.put("Exercise", (double) 1.1);
		activityListMap.put("Bike", (double) 0.1);
		activityListMap.put("Lying", (double) 0.1);

		previousActivityDurationMap.put("Standing", (double) 0);
		previousActivityDurationMap.put("Sitting", (double) 0);
		previousActivityDurationMap.put("Walking", (double) 0);
		previousActivityDurationMap.put("Running", (double) 0);
		previousActivityDurationMap.put("Exercise", (double) 0);
		previousActivityDurationMap.put("Bike", (double) 0);
		previousActivityDurationMap.put("Lying", (double) 0);
		tagDateTime = "Tagged: April 18, 2013 9:11 PM";

	}

	public void invokeController(Document document, PdfWriter writer)
			throws Exception {

		// document.setMargins(2, 2, 2, 2);
		/*
		 * System.out.println("Bottom" + document.bottom());
		 * System.out.println("Top" + document.top());
		 * System.out.println("Right" + document.right());
		 * System.out.println("Left" + document.left());
		 */
		PdfContentByte canvas = writer.getDirectContent();
		// System.out.println("Writer " + writer.getVerticalPosition(true));

		HeaderSection header = new HeaderSection(patientDbValues);
		PdfPTable headerSection = header.getHeaderSection();
		document.add(headerSection);
		// InfoAlertSection infoAlertSection = new InfoAlertSection();
		// PdfPTable middle1Table = infoAlertSection.getInfoAlertSection();
		// document.add(middle1Table);

		AlertSection alertSection = new AlertSection(alerts);
		PdfPTable alertSectionTable = alertSection.getAlertSection();
		document.add(alertSectionTable);

		PatientResponseSection prs = new PatientResponseSection(
				answerArrayFromMobileApp, responseWithinRange, imageName,
				tagDateTime);
		PdfPTable patientResponseTable = prs.getPatientResponseSection();
		document.add(patientResponseTable);

		// Rectangle rangeRectangle=new Rectangle(400,280,540,420);
		KneeSection kneeSection = new KneeSection(canvas, kneeFunctionValues,
				previousKneeFunctionValues, minKneeFunctionValues,
				maxKneeFunctionValues);
		PdfPTable kneeSectionTable = kneeSection.getKneeSection();
		document.add(kneeSectionTable);

		/*
		 * PdfPCell kneeCell=new PdfPCell(kneeSectionTable);
		 * kneeCell.setBorder(Rectangle.BOTTOM);
		 * kneeCell.setBorderColor(CommonSection.borderColor);
		 * kneeCell.setBorderWidth(CommonSection.borderWidth);
		 * document.add(kneeCell);
		 */
		ActivitySection activitySection = new ActivitySection(canvas,
				activityListMap, previousActivityDurationMap, activityMap);
		PdfPTable activityTable = activitySection.getActivitySection();
		document.add(activityTable);
		FooterSection footerSection = new FooterSection(postOperationDays);
		PdfPTable footerData = footerSection.getFooterData();
		document.add(footerData);

		/*
		 * document.top(2); document.left(2); document.right(2);
		 */
		/*
		 * Image img=Image.getInstance("photo.jpg");
		 * //img.setAlignment(Image.LEFT | Image.TEXTWRAP);
		 * img.setBorder(Image.BOX); img.setBorderWidth(100);
		 * img.setBorderColor(BaseColor.GRAY); img.scaleAbsolute(100, 700);
		 * document.add(img);
		 */}
}
