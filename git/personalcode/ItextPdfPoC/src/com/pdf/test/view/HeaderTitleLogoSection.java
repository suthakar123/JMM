/**
 * 
 */
package com.pdf.test.view;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @author jayaram
 * 
 */
public class HeaderTitleLogoSection extends CommonSection {

	/**
	 * 
	 */
	HeaderTitleLogoSection() {

	}

	/**
	 * 
	 * @return
	 * @throws BadElementException
	 * @throws IOException
	 */
	public PdfPCell getHeaderTitleLogoSection() throws BadElementException,
			IOException {

		PdfPCell titleLogoCell = new PdfPCell();
		PdfPTable titleLogoTable = new PdfPTable(1);
		
		// TODO: Will need to pick up from config file
		Image logo = Image.getInstance("JM_logo_final.bmp", true);
		PdfPCell imageCell = new PdfPCell(logo, true);
		imageCell.setBorder(Rectangle.NO_BORDER);
		PdfPCell headerCell = getTableCell(HEADER_TITLE, PdfPCell.ALIGN_CENTER,
				headingFont);
		PdfPCell emailCell = getTableCell(JMM_EMAIL_ID, PdfPCell.ALIGN_LEFT, textFont);
		PdfPCell telephoneCell = getTableCell(JMM_TELEPHONE, PdfPCell.ALIGN_LEFT,
				textFont);
		PdfPCell faxCell = getTableCell(JMM_FAX, PdfPCell.ALIGN_LEFT,
				textFont);
		String currDate = String.valueOf(DateFormat.getDateInstance().format(
				new Date()));
		PdfPCell reportDateCell = getTableCell(currDate, PdfPCell.ALIGN_LEFT,
				textFont);
		 imageCell.setRowspan(4);
		// titleLogoCell.addElement(imageCell);

		// titleLogoCell.addElement(getCell("date", PdfPCell.ALIGN_CENTER,
		// textFont));
		titleLogoTable.addCell(imageCell);
		titleLogoTable.addCell(getTableCell("",textFont));
		titleLogoTable.addCell(headerCell);
		titleLogoTable.addCell(emailCell);
		titleLogoTable.addCell(telephoneCell);
		titleLogoTable.addCell(faxCell);
		titleLogoTable.addCell(reportDateCell);
		
		titleLogoCell.addElement(titleLogoTable);

		return titleLogoCell;
	}
}
