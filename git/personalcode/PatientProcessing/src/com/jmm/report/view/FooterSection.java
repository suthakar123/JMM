/**
 * 
 */
package com.jmm.report.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @author jayaram
 * 
 */
public class FooterSection extends CommonSection {

	Chunk emptyData = new Chunk("               ", footerFont);
	private String postOperationDays;

	public FooterSection(String postOperationDays) {
		this.postOperationDays = postOperationDays;
	}

	/**
	 * @return
	 */
	public PdfPTable getFooterData() {
		PdfPTable footerTable = new PdfPTable(1);
		Paragraph footerData = new Paragraph();
		footerData.add(new Chunk("Values in Red", footerRedFont));
		String footerData2 = " are outside normal limits for a patient "
				+ postOperationDays;
		footerData.add(new Chunk(footerData2, footerFont));
		// Didnt add this as impacting the formatting.
		// footerData.add(new Chunk(Chunk.SPACETABBING));
		footerData.add(emptyData);
		Chunk secondFooter = new Chunk(
				"* Normal values adjusted for days post-op ", footerFont);
		footerData.add(secondFooter);
		PdfPCell cell1 = new PdfPCell(footerData);
		cell1.setBorder(Rectangle.NO_BORDER);
		footerTable.addCell(cell1);
		return footerTable;
	}

}
