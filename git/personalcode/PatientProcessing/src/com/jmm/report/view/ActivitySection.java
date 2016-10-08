/**
 * 
 */
package com.jmm.report.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jmm.report.helper.AlternatingBackgroundEvent;

/**
 * @author jayaram
 * 
 */
public class ActivitySection extends CommonSection {
	// private static final double INNER_CIRCLE_PERCENTAGE = 0.64;
	private static final double INNER_RADIUS = 37;

	private PdfContentByte canvas;
	private Map<String, Double> activityListMap;
	private Map<String, Double> previousActivityDurationMap;
	private Map<String, Double> activityMap;

	/**
	 * 
	 * @param canvas
	 */
	public ActivitySection(PdfContentByte canvas,
			Map<String, Double> activityListMap,
			Map<String, Double> previousActivityDurationMap,
			Map<String, Double> activityMap) {

		this.canvas = canvas;
		this.activityListMap = new HashMap<String, Double>(activityListMap);
		this.previousActivityDurationMap = previousActivityDurationMap;
		this.activityMap = activityMap;
	}

	/**
	 * 
	 * @return
	 */
	public PdfPTable getActivitySection() {
		PdfPTable activitySectionTable = new PdfPTable(new float[] { 40, 60 });
		activitySectionTable.getDefaultCell().setBorder(
				Rectangle.BOTTOM | Rectangle.TOP);
		activitySectionTable.getDefaultCell().setBorderColor(borderColor);
		activitySectionTable.getDefaultCell().setBorderWidth(borderWidth);

		PdfPCell leftCell = new PdfPCell();

		Phrase headerSection = new Phrase(GROUP3_HEADER_TEXT, headingFont);
		// TODO: need to generalize borders
		leftCell.setBorder(ACTIVITY_SECTION_BORDER);
		leftCell.setBorderColor(borderColor);
		leftCell.setBorderWidth(borderWidth);

		PdfPTable innerTable = getActivityTable(activityListMap,
				previousActivityDurationMap);
		//headerSection.setLeading(10);
		leftCell.addElement(headerSection);
		leftCell.addElement(innerTable);
		activitySectionTable.addCell(leftCell);
		// Very important
		activitySectionTable.completeRow();

		Rectangle activityTypeRectangle = new Rectangle(255, 120, 380, 245);
		drawRingChart(activityTypeRectangle, activityMap, activityColor);

		Rectangle activityListRectangle = new Rectangle(415, 120, 540, 245);
		//Rectangle activityListRectangle = new Rectangle(460, 125, 580, 245);
		drawRingChart(activityListRectangle, activityListMap, ringColors);
		

		return activitySectionTable;
	}

	/**
	 * 
	 * @return
	 */
	private PdfPTable getActivityTable(Map<String, Double> activityTypeMap,
			Map<String, Double> previousWeekMap) {
		
		PdfPTable activityTable = new PdfPTable(new float[] { 40, 30, 30 });
		activityTable.setSpacingBefore(10);
		activityTable.setTableEvent(AlternatingBackgroundEvent.getInstance());
		activityTable.addCell(getTableCell(ACTIVITY_ROW_HEADER,
				greenTableHeadingFont));
		activityTable.addCell(getTableCell(ACTIVITY_ROW_HEADER_DURATION,
				Rectangle.ALIGN_CENTER, greenTableHeadingFont));
		activityTable.addCell(getTableCell(ACTIVITY_ROW_HEADER_CHANGE,
				Rectangle.ALIGN_CENTER, greenTableHeadingFont));
		NumberFormat formatter = new DecimalFormat("#00.00");

		for (Entry<String, Double> entry : activityTypeMap.entrySet()) {
			String key = entry.getKey();
			Double duration = entry.getValue();
			Double previousVal = previousWeekMap.get(key);
			Double change = duration - (previousVal != null ? previousVal : 0);
			activityTable.addCell(getTableCell(key, textFont));
			activityTable.addCell(getTableCell(formatter.format(duration),
					Rectangle.ALIGN_CENTER, textFont));
			activityTable.addCell(getTableCell(formatter.format(change),
					Rectangle.ALIGN_CENTER, textFont));
		}
		activityTable.setSpacingAfter(10);
		return activityTable;
	}

	private void drawRingChart(Rectangle rectangle,
			Map<String, Double> activityMap, BaseColor[] colorList) {

		// First we want to sort all of the activities
		double totalDuration = 0.0;
		String[] sortedActivities = new String[activityMap.keySet().size()];
		double[] sortedDurations = new double[sortedActivities.length];
		for (int i = 0; i < sortedActivities.length; i++) {
			double maxDuration = -0.1;
			String maxActivity = "";
			for (String activity : activityMap.keySet()) {
				double duration = activityMap.get(activity);
				if (duration > maxDuration) {
					maxDuration = duration;
					maxActivity = activity;
				}
			}
			sortedActivities[i] = maxActivity;
			sortedDurations[i] = maxDuration;
			totalDuration += maxDuration;
			activityMap.remove(maxActivity);
		}
		float originalLlx = rectangle.getLeft();
		// float originalUlx = rectangle.getRight();
		float originalLly = rectangle.getBottom();
		// float originalUly = rectangle.getTop();
		int xFactor = 0, yFactor = 0;
		int row = 1, column = 1;
		float newLLy = originalLly - 20;

		// We need to draw the proper ring section for each activity
		int startAngle = 0;
		/* for (int i = sortedActivities.length - 1; i >= 0; i--) { */
		
		for (int i = 0; i < sortedActivities.length; i++) {
			float range = 0;
			float offset =0;
			if (i == 0) {
				// We want to make sure that the ring is connected
				// in the end
				range = 360 - startAngle;
				offset = (float) ((sortedDurations[i] / totalDuration) * 360.0);
			} else {
				range = (float) ((sortedDurations[i] / totalDuration) * 360.0);
			}
			
			//Fill numbers in the ring
			if(range!=0){
				drawArc(rectangle, startAngle, range, colorList[i]);
			}
			float angle =0;
			if(i==0){
				angle = startAngle-offset/2;
			}
			else{
				angle = startAngle+range/2;
			}
			float left = rectangle.getLeft();
			float bottom = rectangle.getBottom();
			float right = rectangle.getRight();
			float top = rectangle.getTop();
			float xMiddle = (left + right) / 2;
			float yMiddle = (top + bottom) / 2;
			float x2Value = (float) (xMiddle + 51
					* Math.cos((angle) * Math.PI / 180)) +10;
			float y2Value = (float) (yMiddle + 51
					* Math.sin
					((angle) * Math.PI / 180));
			float xoffset = 0;
			float yoffset = 0;
			if(angle < 490 && angle > 0){
				xoffset = 5;
				yoffset = 5;
			}
			System.out.println("Angle"+ (int)(angle-360) + "      " + "value" + sortedDurations[i]);
			if(sortedDurations[i] != 0.0){
			ColumnText.showTextAligned(canvas, Rectangle.ALIGN_RIGHT, new Phrase(
					String.valueOf((float) sortedDurations[i]), ringTextFont), x2Value-xoffset,
					y2Value-yoffset, 0);
			}
			Rectangle rectangle2 = new Rectangle(originalLlx + xFactor, newLLy
					- yFactor, originalLlx + 10 + xFactor, newLLy + 10
					- yFactor);

			drawLegend(rectangle2, sortedActivities[i], colorList[i]);
			if (column == 1) {
				xFactor = 80;
				column++;
			} else if (column == 2) {
				yFactor = 12 * (row);
				row++;
				column = 1;
				xFactor = 0;
			}
			/*
			 * // Draw the accompanying legend for the activity if (range == 0)
			 * { canvas.setColor(CommonGraphics.VERY_LIGHT_GRAY); canvas
			 * .drawRect( boundingRectangle.x + legendLeftBoundary,
			 * boundingRectangle.y + legendTopBoundary + i (LEGEND_SQUARE_SIZE +
			 * BETWEEN_SQUARE_SPACING), LEGEND_SQUARE_SIZE, LEGEND_SQUARE_SIZE);
			 * } else { canvas .fillRect( boundingRectangle.x +
			 * legendLeftBoundary, boundingRectangle.y + legendTopBoundary + i
			 * (LEGEND_SQUARE_SIZE + BETWEEN_SQUARE_SPACING),
			 * LEGEND_SQUARE_SIZE, LEGEND_SQUARE_SIZE); }
			 * canvas.setFont(CommonGraphics.smallInnerFont);
			 * canvas.setColor(Color.BLACK);
			 * canvas.drawString(sortedActivities[i], boundingRectangle.x +
			 * legendLeftBoundary + LEGEND_SQUARE_SIZE + LEGEND_STRING_SPACING,
			 * boundingRectangle.y + legendTopBoundary + i (LEGEND_SQUARE_SIZE +
			 * BETWEEN_SQUARE_SPACING) + LEGEND_SQUARE_SIZE - 1);
			 */
			startAngle += range;
		}
		double xMid = (rectangle.getLeft() + rectangle.getRight()) / 2.0;
		double yMid = (rectangle.getBottom() + rectangle.getTop()) / 2.0;
		canvas.setColorFill(BaseColor.WHITE);
		canvas.circle(xMid, yMid, INNER_RADIUS);
		canvas.closePathFillStroke();

	}

	/**
	 * 
	 * @param rectangle
	 * @param startAngle
	 * @param range
	 * @param color
	 */
	void drawArc(Rectangle rectangle, double startAngle, double range,
			BaseColor color) {
		// 260, 100, 380, 220
		canvas.setLineWidth(.001f);

		double lx = rectangle.getLeft();
		double ly = rectangle.getBottom();
		double ux = rectangle.getRight();
		double uy = rectangle.getTop();

		/*//System.out.println(rectangle.getLeft());
		System.out.println(rectangle.getBottom());
		System.out.println(rectangle.getRight());
		System.out.println(rectangle.getTop());*/
		canvas.setColorFill(color);
		canvas.arc(lx, ly, ux, uy, startAngle, range);
		double xMid = (lx + ux) / 2.0;
		double yMid = (ly + uy) / 2.0;
		canvas.lineTo(xMid, yMid);
		canvas.closePathFillStroke();
	}

	/**
	 * 
	 * @param rectangle
	 * @param str
	 * @param color
	 */
	private void drawLegend(Rectangle rectangle, String str, BaseColor color) {
		// Rectangle rect = new Rectangle(250, 85, 260, 95);
		rectangle.setBackgroundColor(color);
		canvas.setColorFill(BaseColor.BLACK);
		canvas.rectangle(rectangle);
		canvas.closePathFillStroke();
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(str,
				textFont), rectangle.getRight() + 3, rectangle.getBottom(), 0);

	}

}
