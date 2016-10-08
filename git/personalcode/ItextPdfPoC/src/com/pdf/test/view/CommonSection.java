package com.pdf.test.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class CommonSection {
	// Colors
	BaseColor DARK_GREEN = new BaseColor(0x43, 0x95, 0x39);
	BaseColor activityColor[] = { BaseColor.BLUE, BaseColor.RED };
	BaseColor ringColors[] = { BaseColor.BLUE, BaseColor.GREEN,
			BaseColor.MAGENTA, BaseColor.RED, BaseColor.YELLOW, BaseColor.CYAN,
			BaseColor.ORANGE };

	// Fonts
	Font headingFont = new Font(FontFamily.HELVETICA, 16, 0, new BaseColor(0,
			150, 20));
	Font textFont = new Font(FontFamily.HELVETICA, 10, 0, BaseColor.BLACK);
	Font alertFont = new Font(FontFamily.HELVETICA, 10, 0, BaseColor.RED);
	Font textBoldFont = new Font(FontFamily.HELVETICA, 10, 1, BaseColor.BLACK);
	Font alertHeadingFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD,
			BaseColor.RED);
	Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 6, 0, BaseColor.RED);
	Font greenSubImageFont = new Font(FontFamily.HELVETICA, 10, 1, DARK_GREEN);
	Font greenTableHeadingFont = new Font(FontFamily.HELVETICA, 8, 1,
			DARK_GREEN);

	Font chartTextFont = new Font(FontFamily.HELVETICA, 11, 1,
			BaseColor.LIGHT_GRAY);
	Font footerFont = new Font(FontFamily.HELVETICA, 7, 0, BaseColor.BLACK);
	Font footerRedFont = new Font(FontFamily.HELVETICA, 7, 0, BaseColor.RED);

	protected static final String JMM_EMAIL_ID = "email@jmm.com";
	protected static final String JMM_TELEPHONE = "206-412-XXXX";
	protected static final String JMM_FAX = "FAX-NUM-1234";
	protected static final String HEADER_TITLE = "KNEE REPORT";
	protected static final String GROUP1_HEADER_TEXT = "GROUP 1 - Patient Subjective Responses";
	protected static final String GROUP2_HEADER_TEXT = "GROUP 2 - Measured Knee Function";
	protected static final String GROUP3_HEADER_TEXT = "GROUP 3 - Activity Profile";

	// Alerts Headings
	protected static final String ALERT_TITLE = "ALERTS:";
	protected static final String ALERT_NO_ALERTS_MESSAGE = "Congratulations, you have no Alerts";

	// Patient Response Headings
	protected static final String IMAGE_FOOTER = "Patient Supplied Image";

	// Activity Section Table Header
	protected static final String ACTIVITY_ROW_HEADER = "Activity";
	protected static final String ACTIVITY_ROW_HEADER_DURATION = "Duration (hours)";
	protected static final String ACTIVITY_ROW_HEADER_CHANGE = "Change (hours)";

	// Border Parameters
	public static float borderWidth = 1.5f;
	public static BaseColor borderColor = BaseColor.GRAY;

	// Setting Borders
	public static int HEADER_BORDER = PdfPCell.TOP | PdfPCell.BOTTOM;
	public static int INFORMATION_SECTION_BORDER = PdfPCell.TOP
			| PdfPCell.BOTTOM;
	public static int ACTIVITY_SECTION_BORDER = Rectangle.BOTTOM;

	// Spacing Parameters
	protected static final float SPACING_AFTER_HEADING_TABLE = 5;
	// Do not try changing this much. Can impact other fields
	protected static final float CELL_MINIMUM_HEIGHT_IN_ALERT = 28;
	// User Accessible Attributes
	protected static final String COLON = ": ";

	/**
	 * 
	 * @param text
	 * @param font
	 * @return
	 */
	public PdfPCell getCell(String text, Font font) {
		return getCell(text, 0, font);
	}

	/**
	 * 
	 * @param text
	 * @param alignment
	 * @param font
	 * @return
	 */
	public PdfPCell getCell(String text, int alignment, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
		cell.setBorderWidthBottom(5);
		cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	/**
	 * 
	 * @param text
	 * @param alignment
	 * @param vertical
	 *            alignment
	 * @param font
	 * @return
	 */
	public PdfPCell getCell(String text, int horizontalAlignment,
			int verticalAlignment, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
		cell.setBorderWidthBottom(5);
		cell.setVerticalAlignment(verticalAlignment);
		cell.setHorizontalAlignment(horizontalAlignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	/**
	 * 
	 * @param text
	 * @param font
	 * @return
	 */
	public PdfPCell getTableCell(String text, Font font) {
		return getTableCell(text, PdfPCell.LEFT, font);
	}

	/**
	 * 
	 * @param text
	 * @param horizontal
	 *            alignment
	 * @param vertical
	 *            alignment
	 * @param font
	 * @return
	 */
	public PdfPCell getTableCell(String text, int horizontalAlignment,
			int verticalAlignment, Font font) {
		PdfPCell cell = getCell(text, horizontalAlignment, verticalAlignment,
				font);
		cell.setPaddingBottom(4);
		cell.setPaddingTop(2);
		return cell;
	}

	/**
	 * 
	 * @param text
	 * @param alignment
	 * @param font
	 * @return
	 */
	public PdfPCell getTableCell(String text, int alignment, Font font) {
		PdfPCell cell = getCell(text, alignment, font);
		cell.setPaddingBottom(4);
		cell.setPaddingTop(2);
		return cell;
	}

	/**
	 * This is a helper method which can be used to return the bulleted data.
	 * The string data will be returned as a bulleted data. This data will be
	 * returned in a PdfPTable format, which can be,if required be further
	 * wrapped into PdfPCell.
	 * 
	 * @param data
	 * @param bulletFont
	 * @return PdfPTable data containing the a single row Table with bullet and
	 *         data in it.
	 */
	public PdfPTable getBulletedDataInTable(String data, Font bulletFont) {
		PdfPTable subTable = new PdfPTable(2);
		subTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		try {
			subTable.setTotalWidth(new float[] { 10, 90 });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		zapfdingbats.setColor(bulletFont.getColor());
		PdfPCell cell = getTableCell(String.valueOf((char) 108),
				PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_JUSTIFIED, zapfdingbats);
		cell.setPaddingTop(2);
		subTable.addCell(cell);
		subTable.addCell(getTableCell(data, PdfPCell.ALIGN_LEFT, bulletFont));
		return subTable;
	}

	/**
	 * This is a helper method which can be used to return the bulleted data.
	 * The string data will be returned as a bulleted data in PdfPCell Format.
	 * 
	 * @param data
	 * @param bulletFont
	 * @return PdfPCell data containing the a single row Table with bullet and
	 *         data in it.
	 */
	public PdfPCell getBulletedCellData(String data, Font bulletFont) {
		PdfPCell cell = new PdfPCell(getBulletedDataInTable(data, bulletFont));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
}
