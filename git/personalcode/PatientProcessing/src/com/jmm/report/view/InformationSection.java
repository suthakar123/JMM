/**
 * 
 */
package com.jmm.report.view;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.jmm.report.helper.AlternatingBackgroundEvent;

/**
 * 
 * @author jayaram
 * 
 */
public class InformationSection extends CommonSection {

	String rowHeader[] = { "Patient Name", "Patient ID", "Surgeon",
			"Institution", /* "Report Date", */"Duration", "Status",
			"Measurement Date", "Prior Reports" };
	String rowValues[];
	

	/**
	 * 
	 * @param rowValues
	 * @throws Exception
	 */
	public InformationSection(String[] rowValues) throws Exception {
		if (rowValues == null || rowValues.length != rowHeader.length) {
			throw new Exception();
		}
		this.rowValues = rowValues;
	}

	/**
	 * 
	 * @return PdfPCell: returns the Cell containing the entire information
	 *         table that is present at the top of the report
	 */
	public PdfPCell getInfoSection() {
		PdfPTable infoSection = new PdfPTable(2);

		try {
			infoSection.setWidths(new int[] { 40, 60 });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < rowHeader.length; i++) {
			infoSection.addCell(getTableCell(rowHeader[i], textBoldFont));
			infoSection.addCell(getTableCell(rowValues[i], textFont));
		}
		// This is the event which is used when setting alternate colors
		PdfPTableEvent event = AlternatingBackgroundEvent.getInstance();
		infoSection.setTableEvent(event);

		PdfPCell infoCell = new PdfPCell(infoSection);

		return infoCell;
	}
}
