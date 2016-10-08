/**
 * 
 */
package com.jmm.report.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
		
		byte[] buffer=getByteArrayFromImage();
		Image logo = Image.getInstance(buffer, true);
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

	private byte[] getByteArrayFromImage() throws IOException {
		InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(CommonSection.LOGO_NAME);
		if(input==null){
			throw new IOException("Error Found in reading Logo.");
		}
		int nRead;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[16384];
		while ((nRead = input.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		
		return buffer.toByteArray();
	}
}
