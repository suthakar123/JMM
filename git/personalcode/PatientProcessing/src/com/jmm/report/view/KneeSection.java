/**
 * 
 */
package com.jmm.report.view;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.jmm.report.helper.AlternatingBackgroundEvent;

/**
 * @author jayaram
 * 
 */
public class KneeSection extends CommonSection {
	private static final String GRAPH_HEADER_NAME = "Range of Motion Used";
	private static final String EXTENSION_TITLE = "Extension";
	private static final String FLEXION_TITLE = "Flexion";

	private static final String[] kneeTableHeaders = { "Name", "range",
			"value", "change" };
	private static final String[] kneeTableRowHeaders = {
			"Max Flexion (in deg)", "Max Extension (in deg)",
			"Excursions per Hour", "Modal Excursion" };

	private static final float LEFT_RECTANGLE_DIMENSION = 400;
	private static final float BOTTOM_RECTANGLE_DIMENSION = 280; //265
	private static final float RADIUS = 70;
	private static final int KNEE_FUNCTION_STRING_LENGTH = 4;
	private static final int MAX_ANGLE_OF_ARC = 140;

	PdfContentByte canvas = null;
	Rectangle rangeRectangle = null;
	int[] kneeFunctionValues;
	int[] previousKneeFunctionValues;
	int[] minKneeFunctionValues;
	int[] maxKneeFunctionValues;
	String[] normalRangeStrings;
	String[] changeStrings;

	/**
	 * 
	 * @param canvas
	 * @param rangeRectangle
	 * @throws Exception
	 */
	public KneeSection(PdfContentByte canvas, int[] kneeFunctionValues,
			int[] previousKneeFunctionValues,
			int[] minKneeFunctionValues, int[] maxKneeFunctionValues)
			throws Exception {

		if (kneeFunctionValues.length != KNEE_FUNCTION_STRING_LENGTH
				|| minKneeFunctionValues == null
				|| minKneeFunctionValues.length != KNEE_FUNCTION_STRING_LENGTH
				|| maxKneeFunctionValues == null
				|| maxKneeFunctionValues.length != KNEE_FUNCTION_STRING_LENGTH
				|| previousKneeFunctionValues == null
				|| previousKneeFunctionValues.length != KNEE_FUNCTION_STRING_LENGTH)
			throw new Exception(/* ErrorCodes.Errors[16] */);

		this.canvas = canvas;
		this.kneeFunctionValues = kneeFunctionValues;
		this.minKneeFunctionValues = minKneeFunctionValues;
		this.maxKneeFunctionValues = maxKneeFunctionValues;
		this.previousKneeFunctionValues = previousKneeFunctionValues;

		this.rangeRectangle = new Rectangle(LEFT_RECTANGLE_DIMENSION,
				BOTTOM_RECTANGLE_DIMENSION, LEFT_RECTANGLE_DIMENSION + 2
						* RADIUS, BOTTOM_RECTANGLE_DIMENSION + 2 * RADIUS);
		// Creating the range String array values
		normalRangeStrings = new String[KNEE_FUNCTION_STRING_LENGTH];
		for (int i = 0; i < KNEE_FUNCTION_STRING_LENGTH; i++) {
			normalRangeStrings[i] = String.format("(normal %d - %d)*",
					(int) minKneeFunctionValues[i],
					(int) maxKneeFunctionValues[i]);
		}

		// Creating the change String array values
		changeStrings = new String[KNEE_FUNCTION_STRING_LENGTH];
		for (int i = 0; i < KNEE_FUNCTION_STRING_LENGTH; i++) {
			int change = (int) (kneeFunctionValues[i] - previousKneeFunctionValues[i]);
			if (change < 0) {
				changeStrings[i] = "- ";
			} else {
				changeStrings[i] = "";
			}
			changeStrings[i] += String.format("%d", Math.abs(change));
		}

	}

	/**
	 * 
	 * @return
	 */
	public PdfPTable getKneeSection() {
		//PdfPTable kneeSectionTable = new PdfPTable(new float[] { 75, 25 });
		PdfPTable kneeSectionTable = new PdfPTable(new float[] { 75, 25 });
		PdfPCell innerTable = getMeasuredKneeSection();
		kneeSectionTable.addCell(innerTable);
		PdfPCell rangeCell = getGraphComponentCell();
		kneeSectionTable.addCell(rangeCell);

		return kneeSectionTable;
	}

	/**
	 * Helper method which fetches the graph component of the knee section
	 * 
	 * @param kneeSectionTable
	 * @return: Just returning the graph header cell. The other things are
	 *          directly embedded onto the canvas.
	 */
	private PdfPCell getGraphComponentCell() {
		float radius = 70;

		PdfPCell rangeCell = getTableCell(GRAPH_HEADER_NAME,
				PdfPCell.ALIGN_JUSTIFIED, greenSubImageFont);
		canvas.setLineWidth(.5f);
		canvas.setRGBColorStroke(255, 255, 255);

		// Gray color
		canvas.setRGBColorFill(200, 200, 200);

		float left = rangeRectangle.getLeft();
		float bottom = rangeRectangle.getBottom();
		float right = rangeRectangle.getRight();
		float top = rangeRectangle.getTop();
		float xMiddle = (left + right) / 2;
		float yMiddle = (top + bottom) / 2;
		ColumnText.showTextAligned(canvas, Rectangle.ALIGN_CENTER, new Phrase(
				EXTENSION_TITLE, chartTextFont), xMiddle + 30, yMiddle + 5, 0);
		// 40 because 180-140
		ColumnText.showTextAligned(canvas, Rectangle.ALIGN_CENTER, new Phrase(
				FLEXION_TITLE, chartTextFont), xMiddle - 30, yMiddle - 20, 40);
		// Complete MAX_ANGLE_ARC
		canvas.arc(left, bottom, right, top, 0, -MAX_ANGLE_OF_ARC);
		canvas.lineTo(xMiddle, yMiddle);
		canvas.closePathFillStroke();

		double extensionAngle = kneeFunctionValues[1];
		double flexionAngle = kneeFunctionValues[0];
		double sumAngle = extensionAngle + flexionAngle;

		// Light Green Color
		canvas.setRGBColorFill(16, 205, 55);

		canvas.arc(left, bottom, right, top, -extensionAngle, -flexionAngle);
		canvas.lineTo(xMiddle, yMiddle);
		canvas.closePathFillStroke();

		float x1Value = (float) (xMiddle + 70 * Math.cos(-extensionAngle
				* Math.PI / 180)) + 10;
		float y1Value = (float) (yMiddle + 70 * Math.sin(-extensionAngle
				* Math.PI / 180)) - 10;
		ColumnText.showTextAligned(canvas, Rectangle.ALIGN_RIGHT, new Phrase(
				String.valueOf((int) extensionAngle), chartTextFont), x1Value,
				y1Value, 0);
		canvas.closePathFillStroke();

		float x2Value = (float) (xMiddle + radius
				* Math.cos(-sumAngle * Math.PI / 180)) + 10;
		float y2Value = (float) (yMiddle + radius
				* Math.sin(-sumAngle * Math.PI / 180)) - 10;
		ColumnText.showTextAligned(canvas, Rectangle.ALIGN_RIGHT, new Phrase(
				String.valueOf((int) sumAngle), chartTextFont), x2Value,
				y2Value-5, 0);
		canvas.closePathFillStroke();

		return rangeCell;
	}

	/**
	 * 
	 * @return
	 */
	private PdfPCell getMeasuredKneeSection() {
		PdfPCell measuredKneeSection = new PdfPCell();
		// Setting the border for First part of response Cell.
		measuredKneeSection.setBorder(Rectangle.BOTTOM);
		measuredKneeSection.setBorderWidth(borderWidth);
		measuredKneeSection.setBorderColor(borderColor);

		Phrase kneeHeader = new Phrase(GROUP2_HEADER_TEXT, headingFont);

		measuredKneeSection.addElement(kneeHeader);
		measuredKneeSection.addElement(getKneeTable());

		return measuredKneeSection;
	}

	private PdfPTable getKneeTable() {
		PdfPTable kneeDataTable = new PdfPTable(new float[] { 40, 34, 13, 13 });
		kneeDataTable.setSpacingAfter(10);
		PdfPTableEvent alternatingEvent = AlternatingBackgroundEvent
				.getInstance();
		kneeDataTable.setTableEvent(alternatingEvent);
		// Header Row
		for (int i = 0; i < kneeTableHeaders.length; i++) {
			kneeDataTable.addCell(getTableCell(kneeTableHeaders[i],
					PdfPCell.ALIGN_CENTER, greenTableHeadingFont));
		}
		// Row wise iteration
		for (int i = 0; i < kneeTableHeaders.length; i++) {
			// Column 1 row heading
			kneeDataTable
					.addCell(getTableCell(kneeTableRowHeaders[i], textFont));
			// Column 2 giving range of knee values
			kneeDataTable.addCell(getTableCell(normalRangeStrings[i],
					PdfPCell.ALIGN_CENTER, textFont));
			// Column 3 giving actual value
			boolean isNotAlertFlag = isWithinRange(minKneeFunctionValues[i],
					maxKneeFunctionValues[i], kneeFunctionValues[i]);
			Font fontTypeSelected = isNotAlertFlag ? textFont : alertFont;
			kneeDataTable.addCell(getTableCell(
					String.valueOf(kneeFunctionValues[i]),
					PdfPCell.ALIGN_RIGHT, fontTypeSelected));
			// Column 4 giving difference in the values
			kneeDataTable.addCell(getTableCell(changeStrings[i],
					PdfPCell.ALIGN_RIGHT, textFont));
		}
		/*
		 * kneeDataTable.addCell(getTableCell("Max Flexion (in deg)",
		 * textFont));
		 * kneeDataTable.addCell(getTableCell(KneeSection.FLEXION_RANGE,
		 * PdfPCell.ALIGN_CENTER, textFont)); kneeDataTable
		 * .addCell(getTableCell("48", PdfPCell.ALIGN_RIGHT, textFont));
		 * kneeDataTable .addCell(getTableCell("48", PdfPCell.ALIGN_RIGHT,
		 * textFont)); // Second Row
		 * kneeDataTable.addCell(getTableCell("Max Extension (in deg)",
		 * textFont));
		 * kneeDataTable.addCell(getTableCell(KneeSection.EXTENSION_RANGE,
		 * PdfPCell.ALIGN_CENTER, textFont)); kneeDataTable
		 * .addCell(getTableCell("6", PdfPCell.ALIGN_RIGHT, textFont));
		 * kneeDataTable .addCell(getTableCell("6", PdfPCell.ALIGN_RIGHT,
		 * textFont)); // Third Row
		 * kneeDataTable.addCell(getTableCell("Excursions per Hour", textFont));
		 * kneeDataTable
		 * .addCell(getTableCell(KneeSection.TOTAL_EXCURSIONS_RANGE,
		 * PdfPCell.ALIGN_CENTER, textFont));
		 * kneeDataTable.addCell(getTableCell("524", PdfPCell.ALIGN_RIGHT,
		 * textFont)); kneeDataTable.addCell(getTableCell("524",
		 * PdfPCell.ALIGN_RIGHT, textFont)); // Fourth Row
		 * kneeDataTable.addCell(getTableCell("Modal Excursion", textFont));
		 * kneeDataTable
		 * .addCell(getTableCell(KneeSection.MODAL_EXCURSIONS_RANGE,
		 * PdfPCell.ALIGN_CENTER, textFont)); kneeDataTable
		 * .addCell(getTableCell("31", PdfPCell.ALIGN_RIGHT, textFont));
		 * kneeDataTable .addCell(getTableCell("31", PdfPCell.ALIGN_RIGHT,
		 * textFont));
		 */
		return kneeDataTable;
	}

	/**
	 * A Helper method which returns true,if currValue is less than or equal to
	 * MaxValue and greater than or equal to minValue
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param currValue
	 * @return boolean true or false;
	 */
	private boolean isWithinRange(double minValue, double maxValue,
			int currValue) {
		if (minValue > maxValue) {
			double temp = minValue;
			minValue = maxValue;
			maxValue = temp;
		}
		if (currValue >= minValue && currValue <= maxValue)
			return true;
		else
			return false;
	}
}
