package com.pdf.test.view;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class HeaderSection extends CommonSection {
	
	String rowValues[];

	/**
	 * 
	 * @param rowValues
	 */
	public HeaderSection(String[] rowValues) {
		this.rowValues = rowValues;
	}

	/**
	 * This method returns the PdfPTable containing two important cells. The
	 * cells are 
	 * 1) InformationSection (containing initial patient summary information) and 
	 * 2)Logo and Title Cell 
	 * 
	 * @return
	 * @throws Exception
	 */
	public PdfPTable getHeaderSection() throws Exception {
		PdfPTable headerTable = new PdfPTable(new float[] { 70, 30 });
		
		InformationSection infoSection = new InformationSection(rowValues);
		PdfPCell infoCell = infoSection.getInfoSection();
		HeaderTitleLogoSection titleLogoSection = new HeaderTitleLogoSection();
		PdfPCell titleLogoCell = titleLogoSection.getHeaderTitleLogoSection();

		//Setting the border for the information table cell
		infoCell.setBorder(HEADER_BORDER);
		infoCell.setBorderColor(borderColor);
		infoCell.setBorderWidth(borderWidth);
		
		//Setting the border for the title logo table cell
		titleLogoCell.setBorder(HEADER_BORDER);
		titleLogoCell.setBorderColor(borderColor);
		titleLogoCell.setBorderWidth(borderWidth);
		
		headerTable.addCell(infoCell);
		headerTable.addCell(titleLogoCell);
		headerTable.setSpacingAfter(SPACING_AFTER_HEADING_TABLE);
		
		return headerTable;
	}
}
