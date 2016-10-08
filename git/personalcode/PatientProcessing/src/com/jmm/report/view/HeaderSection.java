package com.jmm.report.view;


import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jmm.report.domain.PatientInfo;

public class HeaderSection extends CommonSection {

	private PatientInfo patientInfo;

	/**
	 * 
	 * @param rowValues
	 */
	public HeaderSection(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	/**
	 * This method returns the PdfPTable containing two important cells. The
	 * cells are 1) InformationSection (containing initial patient summary
	 * information) and 2)Logo and Title Cell
	 * 
	 * @return
	 * @throws Exception
	 */
	public PdfPTable getHeaderSection() throws Exception {
		PdfPTable headerTable = new PdfPTable(new float[] { 70, 30 });

		InformationSection infoSection = new InformationSection(
				getStringOrder());
		PdfPCell infoCell = infoSection.getInfoSection();
		HeaderTitleLogoSection titleLogoSection = new HeaderTitleLogoSection();
		PdfPCell titleLogoCell = titleLogoSection.getHeaderTitleLogoSection();

		// Setting the border for the information table cell
		infoCell.setBorder(HEADER_BORDER);
		infoCell.setBorderColor(borderColor);
		infoCell.setBorderWidth(borderWidth);

		// Setting the border for the title logo table cell
		titleLogoCell.setBorder(HEADER_BORDER);
		titleLogoCell.setBorderColor(borderColor);
		titleLogoCell.setBorderWidth(borderWidth);

		headerTable.addCell(infoCell);
		headerTable.addCell(titleLogoCell);
		headerTable.setSpacingAfter(SPACING_AFTER_HEADING_TABLE);

		return headerTable;
	}

	private String[] getStringOrder() {
		String[] rowValues = new String[8];

		rowValues[0] = patientInfo.getName();
		rowValues[1] = patientInfo.getPatientID();
		rowValues[2] = patientInfo.getSurgeonName();
		rowValues[3] = patientInfo.getInstitutionName();
		rowValues[4] = String.valueOf(patientInfo.getTotalDuration());
		rowValues[5] = String.valueOf(patientInfo.getPostOperationDays());
		rowValues[6] = String.valueOf(patientInfo.getDateOfSurgery());
		rowValues[7] = patientInfo.getPreviousReportsString();

		return rowValues;

	}
}
