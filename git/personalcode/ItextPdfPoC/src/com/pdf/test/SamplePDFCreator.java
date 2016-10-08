package com.pdf.test;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.pdf.test.controller.ReportGeneratorController;
import com.pdf.test.helper.AlternatingBackgroundEvent;

public class SamplePDFCreator {

	/**
	 * @param args
	 */
	static String DEST = "Test.pdf";
	Font headingFont = new Font(FontFamily.HELVETICA, 16, 0, new BaseColor(0,
			220, 10));
	Font textFont = new Font(FontFamily.HELVETICA, 10, 0, BaseColor.BLACK);
	Font textBoldFont = new Font(FontFamily.HELVETICA, 10, 1, BaseColor.BLACK);
	Font alertFont = new Font(FontFamily.HELVETICA, 10, 0, BaseColor.RED);
	Font alertHeadingFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED);
	Font zapfdingbats = new Font(FontFamily.ZAPFDINGBATS, 6, 0, BaseColor.RED);

	public static void main(String[] args) throws Exception {

		// file.getParentFile().mkdirs();
		new SamplePDFCreator().createPdf(DEST);
	}

	// This is using tables
	public void createPdf(String dest) throws Exception {
		//Rectangle rect =new Rectangle(5,5,1000,1000);
		//Document document = new Document(rect);
		Document document = new Document(PageSize.LETTER,0,0,25,30);
		//Document document = new Document(PageSize.A4);
		
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		
		
		new ReportGeneratorController().invokeController(document,writer);
		
		//
		//buildUsingTables(document);
		document.close();
	}

	@Deprecated
	public void buildHeader(Document document) throws DocumentException {
		Chunk tab1 = new Chunk(new VerticalPositionMark(), 250, true);
		Phrase p = new Phrase("KNEE Report", new Font(FontFamily.HELVETICA, 20,
				Font.NORMAL, BaseColor.GREEN));
		p.add(tab1);
		p.add(new Chunk("Jimmy"));
		p.add(new Chunk(Chunk.NEWLINE));
		p.setLeading(12);
		p.add(new Chunk("emailID@email.com", new Font(FontFamily.HELVETICA, 12,
				Font.NORMAL, BaseColor.BLACK)));
		p.add(new Chunk(Chunk.NEWLINE));
		p.add(new Chunk("Tel: 201 112 1558  Fax:206.555.5556", new Font(
				FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
		Paragraph para = new Paragraph();
		para.add(p);
		para.add(tab1);
		para.add(new Chunk("Hi There"));
		document.add(para);
	}

	public void buildUsingTables(Document document) throws DocumentException,
			IOException {
		// document.setMargins(2, 2, 2, 2);
		PdfPTable headerSection = getHeaderSection();
		document.add(headerSection);
		PdfPTable middle1Table = getMiddleOneSection();
		document.add(middle1Table);
	}

	public PdfPTable getHeaderSection() throws BadElementException, IOException {
		PdfPTable table = new PdfPTable(3);
		table.setSpacingAfter(10);
		PdfPCell headerCell = getCell("KNEE REPORT", PdfPCell.ALIGN_LEFT,
				headingFont);
		headerCell.setMinimumHeight(10);
		table.addCell(headerCell);
		table.addCell(getCell("Name Of Patient", PdfPCell.ALIGN_CENTER,
				textFont));
		Image logo = Image.getInstance("JM_logo_final.bmp", true);
		PdfPCell imageCell = new PdfPCell(logo, true);
		imageCell.setBorder(Rectangle.NO_BORDER);
		imageCell.setRowspan(3);
		table.addCell(imageCell);
		table.addCell(getCell("emailId", PdfPCell.ALIGN_LEFT, textFont));
		table.addCell(getCell("sensorID", PdfPCell.ALIGN_CENTER, textFont));

		table.addCell(getCell("Telephone", PdfPCell.ALIGN_LEFT, textFont));
		table.addCell(getCell("date", PdfPCell.ALIGN_CENTER, textFont));
		table.completeRow();

		return table;
	}

	public PdfPTable getMiddleOneSection() {
		PdfPTable middle1Table = new PdfPTable(2);
		middle1Table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		try {
			middle1Table.setWidths(new int[] { 25, 75 });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		PdfPCell alertCell = getAlertSection();
		middle1Table.addCell(alertCell);
		PdfPCell infoCell = getInfoSection();
		alertCell.setCalculatedHeight(infoCell.getCalculatedHeight());
		middle1Table.addCell(infoCell);
		return middle1Table;
	}

	private PdfPCell getInfoSection() {
		PdfPTable infoSection = new PdfPTable(2);
		infoSection.getDefaultCell().setBorder(Rectangle.TOP);
		infoSection.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
		try {
			infoSection.setWidths(new int[] { 40, 60 });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		PdfPCell cell1 = getTableCell("Surgeon:", textBoldFont);
		PdfPCell cell2 = getTableCell("Peter", textFont);
		PdfPCell cell3 = getTableCell("Institution:", textBoldFont);
		PdfPCell cell4 = getTableCell("HealthCare", textFont);
		PdfPCell cell5 = getTableCell("Report Date:", textBoldFont);
		PdfPCell cell6 = getTableCell("February 9, 2016", textFont);
		PdfPCell cell7 = getTableCell("Duration:", textBoldFont);
		PdfPCell cell8 = getTableCell("12.3 hours", textFont);
		PdfPCell cell9 = getTableCell("Status:", textBoldFont);
		PdfPCell cell10 = getTableCell("41 days post-TKA", textFont);
		PdfPCell cell11 = getTableCell("Measurement Date:", textBoldFont);
		PdfPCell cell12 = getTableCell("April 18, 2013", textFont);
		PdfPCell cell13 = getTableCell("Prior Reports:", textBoldFont);
		PdfPCell cell14 = getTableCell("04/18, 04/18 (40/40 days post-op)",
				textFont);

		infoSection.addCell(cell1);
		infoSection.addCell(cell2);
		infoSection.addCell(cell3);
		infoSection.addCell(cell4);
		infoSection.addCell(cell5);
		infoSection.addCell(cell6);
		infoSection.addCell(cell7);
		infoSection.addCell(cell8);
		infoSection.addCell(cell9);
		infoSection.addCell(cell10);
		infoSection.addCell(cell11);
		infoSection.addCell(cell12);
		infoSection.addCell(cell13);
		infoSection.addCell(cell14);
		// This is the event which is used when setting alternate colors
		PdfPTableEvent event =  AlternatingBackgroundEvent.getInstance();
		infoSection.setTableEvent(event);

		PdfPCell infoCell = new PdfPCell(infoSection);
		infoCell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
		infoCell.setBorderColor(BaseColor.GRAY);
		infoCell.setBorderWidth(1.5f);

		return infoCell;
	}

	/**
	 * TODO: Check the limit of the Alert Section to allow only limited number of Alerts
	 * 
	 * @return
	 */
	private PdfPCell getAlertSection() {
		Chunk headerChunk = new Chunk("ALERTS: ", alertHeadingFont);
		String a[] = { "This is the dummy text",
				"I am currently adding a lot of text.",
				"To see how the sizing options will work" };
		PdfPTable subTable = new PdfPTable(2);
		subTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		try {
			subTable.setTotalWidth(new float[] { 15, 85 });
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		for (String str : a) {
			PdfPCell cell = getCell(String.valueOf((char) 108),
					PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_CENTER, zapfdingbats);
			cell.setPaddingTop(2);
			subTable.addCell(cell);
			subTable.addCell(getCell(str, PdfPCell.ALIGN_LEFT, alertFont));
		}

		PdfPCell cell = new PdfPCell();
		cell.addElement(headerChunk);
		cell.addElement(subTable);
		cell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);
		cell.setBorderColor(BaseColor.RED);
		cell.setBorderWidth(1.5f);
		return cell;

	}

	/**
	 * 
	 * @return
	 */
	public Phrase gotoNextLineWithBullet() {
		Phrase p = new Phrase(Chunk.NEWLINE);
		p.add(getCharacterBullet());
		return p;
	}

	private Chunk getCharacterBullet() {
		Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);
		bullet.setCharacterSpacing(12);
		return bullet;

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
	 * 
	 * @param text
	 * @param font
	 * @return
	 */
	public PdfPCell getCell(String text, Font font) {
		return getCell(text, 0, font);
	}

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
}
