package com.jmm.report.helper;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;

public class AlternatingBackgroundEvent implements PdfPTableEvent {
	private static AlternatingBackgroundEvent obj = null;

	private AlternatingBackgroundEvent() {

	}

	public static AlternatingBackgroundEvent getInstance() {
		if (obj == null) {
			obj = new AlternatingBackgroundEvent();
		}
		return obj;
	}

	public void tableLayout(PdfPTable table, float[][] widths, float[] heights,
			int headerRows, int rowStart, PdfContentByte[] canvases) {
		int columns;
		Rectangle rect;
		int footer = widths.length - table.getFooterRows();
		BaseColor lightGrayColor=new BaseColor(230,230,230);
		// int header = table.getHeaderRows() - table.getFooterRows() + 1;
		int counter = 0;
		for (int row = 0; row < footer; row++, counter++) {
			columns = widths[row].length - 1;
			rect = new Rectangle(widths[row][0], heights[row],
					widths[row][columns], heights[row + 1]);
			if (counter % 2 == 0) {
				rect.setBackgroundColor(BaseColor.WHITE);
			} else {
				
				rect.setBackgroundColor(lightGrayColor);
			}
			rect.setBorder(Rectangle.TOP);
			canvases[PdfPTable.BASECANVAS].rectangle(rect);
		}
	}
}