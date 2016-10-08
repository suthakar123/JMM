package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Rectangle;

// Class responsible for drawing the knee function section of the report
public class KneeFunctionSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;

	// Table variables
	private final double TOP_BOUNDARY_PERCENTAGE = 0.38;
	private final double BOTTOM_BOUNDARY_PERCENTAGE = 0.93;
	private final double TABLE_WIDTH_PERCENTAGE = 0.702;
	private final int NUM_RECTANGLES = 4;
	private final int SPACING_PIXELS = 2;
	private final int KNEE_FUNCTION_PADDING = 2;
	private final double NORMAL_RANGE_PERCENTAGE = 0.407;
	private final String[] kneeFunctionStrings = { "Max Flexion",
			"Max Extension", "Total Excursions per Hour", "Modal Excursion" };
	private final double VALUE_PERCENTAGE = 0.797;
	private final double CHANGE_PERCENTAGE = 0.975;
	private final double HEADING_PERCENTAGE = 0.19;
	private final String headerString = "GROUP 2 - Measured Knee Function";
	private final int COLUMN_HEADING_PADDING = 5;
	private final double VALUE_HEADING_PERCENTAGE = 0.504;
	private final double CHANGE_HEADING_PERCENTAGE = 0.625;

	// Arc chart variables
	private final double ARC_LEFT_PERCENTAGE = 0.681;
	private final double ARC_RIGHT_PERCENTAGE = 0.981;
	private final double ARC_VERT_PERCENTAGE = -0.515;
	private final int MAX_EXTENSION = 0;
	private final int MAX_FLEXION = 120;
	private final String arcString = "Range of Motion Used";
	private final double ARC_STRING_LEFT_PERCENTAGE = 0.765;
	private final double ARC_STRING_BOTTOM_PERCENTAGE = 0.94;
	private final String extensionString = "Extension";
	private final double EXTENSION_STRING_LEFT_PERCENTAGE = 0.857;
	private final double EXTENSION_STRING_BOTTOM_PERCENTAGE = 0.12;
	private final String flexionString = "Flexion";
	private final int FLEXION_STRING_X_OFFSET = 435;
	private final int FLEXION_STRING_Y_OFFSET = 1040;
	private final double ARC_EXTENSION_RADIUS_OFFSET = 1.5;
	private final double ARC_FLEXION_RADIUS_OFFSET = 0.5;
	private final double ARC_MIN_ANGLE_OFFSET = 2.0 * Math.PI / 360.0;
	private final double ARC_MAX_ANGLE_OFFSET = 30.0 * Math.PI / 360.0;
	private final double ARC_STRING_HEIGHT = 4.0;
	private final double ARC_STRING_WIDTH = 16.0;
	private final int MIN_FLEXION_ANGLE = 55;
	private final int MAX_FLEXION_ANGLE = 120;
	private final int KNEE_FUNCTION_STRING_LENGTH = 4;
	private String[] valueStrings;
	private String[] normalRangeStrings;
	private String[] changeStrings;
	private int patientExtension;
	private int patientFlexion;

	public KneeFunctionSection(PDFGraphics g, Rectangle r, int[] kneeFunction,
			double[] minValues, double[] maxValues, double[] previousValues)
			throws Exception {
		if (g == null || r == null || kneeFunction == null
				|| kneeFunction.length != KNEE_FUNCTION_STRING_LENGTH
				|| minValues == null
				|| minValues.length != KNEE_FUNCTION_STRING_LENGTH
				|| maxValues == null
				|| maxValues.length != KNEE_FUNCTION_STRING_LENGTH
				|| previousValues == null
				|| previousValues.length != KNEE_FUNCTION_STRING_LENGTH)
			throw new Exception(ErrorCodes.Errors[16]);

		pdfGraphics = g;
		boundingRectangle = r;

		valueStrings = new String[KNEE_FUNCTION_STRING_LENGTH];
		for (int i = 0; i < 2; i++)
			valueStrings[i] = "" + kneeFunction[i] + " deg";
		for (int i = 2; i < 4; i++)
			valueStrings[i] = "" + kneeFunction[i];

		normalRangeStrings = new String[KNEE_FUNCTION_STRING_LENGTH];
		for (int i = 0; i < KNEE_FUNCTION_STRING_LENGTH; i++) {
			normalRangeStrings[i] = String.format("(normal %.1f - %.1f)#",
					minValues[i], maxValues[i]);
		}

		changeStrings = new String[KNEE_FUNCTION_STRING_LENGTH];
		for (int i = 0; i < KNEE_FUNCTION_STRING_LENGTH; i++) {
			double change = kneeFunction[i] - previousValues[i];
			if (change < 0) {
				changeStrings[i] = "- ";
			} else {
				changeStrings[i] = "";
			}
			changeStrings[i] += String.format("%.1f", Math.abs(change));
			if (i < 2)
				changeStrings[i] += " deg";
		}

		patientFlexion = kneeFunction[0];
		patientExtension = kneeFunction[1];
	}

	public void Draw() throws Exception {
		// Draw the heading for this section
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.headingFont);
		int headingBoundary = (int) ((double) boundingRectangle.height * HEADING_PERCENTAGE);
		pdfGraphics.drawString(headerString, boundingRectangle.x,
				boundingRectangle.y + headingBoundary);

		// Draw the rectangle table. We need to shorten our boundingRectangle
		// since the table does not
		// stretch across the length of the page
		int tableWidth = (int) ((double) boundingRectangle.width * TABLE_WIDTH_PERCENTAGE);
		Rectangle tableRectangle = new Rectangle(boundingRectangle.x,
				boundingRectangle.y, tableWidth, boundingRectangle.height);
		CommonGraphics.DrawTable(pdfGraphics, tableRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS);

		// Now we need to add the column heading strings
		int valueHeadingBoundary = (int) ((double) boundingRectangle.width * VALUE_HEADING_PERCENTAGE);
		int changeHeadingBoundary = (int) ((double) boundingRectangle.width * CHANGE_HEADING_PERCENTAGE);
		int topBoundary = (int) ((double) boundingRectangle.height * TOP_BOUNDARY_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.columnHeadingFont);
		pdfGraphics.drawString("Value", boundingRectangle.x
				+ valueHeadingBoundary, boundingRectangle.y + topBoundary
				- COLUMN_HEADING_PADDING);
		pdfGraphics.drawString("Change", boundingRectangle.x
				+ changeHeadingBoundary, boundingRectangle.y + topBoundary
				- COLUMN_HEADING_PADDING);

		// Now we need to draw all of the strings. First we want to draw the
		// names of the knee functions
		int normalRangeBoundary = (int) ((double) tableRectangle.width * NORMAL_RANGE_PERCENTAGE);
		Rectangle kneeFunctionRectangle = new Rectangle(tableRectangle.x
				+ KNEE_FUNCTION_PADDING, tableRectangle.y, normalRangeBoundary,
				tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, kneeFunctionRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, kneeFunctionStrings,
				CommonGraphics.innerFont, false);

		// Next draw all of the normal ranges
		int valueBoundary = (int) ((double) tableRectangle.width * VALUE_PERCENTAGE);
		Rectangle normalRangeRectangle = new Rectangle(tableRectangle.x
				+ normalRangeBoundary, tableRectangle.y, valueBoundary
				- normalRangeBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, normalRangeRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, normalRangeStrings,
				CommonGraphics.innerFont, false);

		// Next draw all of the values
		int changeBoundary = (int) ((double) tableRectangle.width * CHANGE_PERCENTAGE);
		Rectangle valueRectangle = new Rectangle(tableRectangle.x
				+ valueBoundary, tableRectangle.y, changeBoundary
				- valueBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, valueRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, valueStrings,
				CommonGraphics.innerFont, true);

		// Finally draw the change
		Rectangle changeRectangle = new Rectangle(tableRectangle.x
				+ changeBoundary, tableRectangle.y, tableRectangle.width
				- changeBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, changeRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, changeStrings,
				CommonGraphics.innerFont, true);

		// The last thing that needs to be drawn is the range of motion graph

		// First we want to draw the arc that shows the full range of motion
		int arcLeftBoundary = (int) ((double) boundingRectangle.width * ARC_LEFT_PERCENTAGE);
		int arcRightBoundary = (int) ((double) boundingRectangle.width * ARC_RIGHT_PERCENTAGE);
		int arcTopBoundary = (int) ((double) boundingRectangle.height * ARC_VERT_PERCENTAGE);
		int arcDiameter = arcRightBoundary - arcLeftBoundary;
		int fullArcStartAngle = 360 - MAX_FLEXION;
		int fullArcRange = MAX_FLEXION - MAX_EXTENSION;
		pdfGraphics.setColor(Color.LIGHT_GRAY);
		pdfGraphics.fillArc(boundingRectangle.x + arcLeftBoundary,
				boundingRectangle.y + arcTopBoundary, arcDiameter, arcDiameter,
				fullArcStartAngle, fullArcRange);

		// Now draw the arc showing only the patient's range of motion
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		int patientStartAngle = 360 - patientFlexion;
		int patientRange = patientFlexion - patientExtension;
		pdfGraphics.fillArc(boundingRectangle.x + arcLeftBoundary,
				boundingRectangle.y + arcTopBoundary, arcDiameter, arcDiameter,
				patientStartAngle, patientRange);

		// Draw the string below the arc
		int arcStringLeftBoundary = (int) ((double) boundingRectangle.width * ARC_STRING_LEFT_PERCENTAGE);
		int arcStringBottomBoundary = (int) ((double) boundingRectangle.height * ARC_STRING_BOTTOM_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.drawString(arcString, boundingRectangle.x
				+ arcStringLeftBoundary, boundingRectangle.y
				+ arcStringBottomBoundary);

		// Draw the extension string
		int extensionStringLeftBoundary = (int) ((double) boundingRectangle.width * EXTENSION_STRING_LEFT_PERCENTAGE);
		int extensionStringBottomBoundary = (int) ((double) boundingRectangle.height * EXTENSION_STRING_BOTTOM_PERCENTAGE);
		pdfGraphics.setColor(Color.LIGHT_GRAY);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.drawString(extensionString, boundingRectangle.x
				+ extensionStringLeftBoundary, boundingRectangle.y
				+ extensionStringBottomBoundary);

		// Draw the flexion sting
		pdfGraphics.setColor(Color.LIGHT_GRAY);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.translate(FLEXION_STRING_X_OFFSET, FLEXION_STRING_Y_OFFSET);
		pdfGraphics.rotate(-Math.PI / 3.0);
		pdfGraphics.drawString(flexionString, 0, 0);
		pdfGraphics.rotate(Math.PI / 3.0);
		pdfGraphics.translate(-FLEXION_STRING_X_OFFSET,
				-FLEXION_STRING_Y_OFFSET);

		// Now we need to draw the numbers corresponding to patient flexion and
		// extension in such a way that they don't overlap the bottom
		// text or each other
		double arcRadius = (double) arcDiameter / 2.0; // Increase the radius a
														// bit to keep the text
														// away from the arc
		double extensionAngle = (double) patientExtension * 2.0 * Math.PI
				/ 360.0 + ((double) patientExtension / 120.0)
				* (ARC_MAX_ANGLE_OFFSET - ARC_MIN_ANGLE_OFFSET)
				+ ARC_MIN_ANGLE_OFFSET;
		int correctedPatientFlexion = patientFlexion;
		if (patientFlexion > MIN_FLEXION_ANGLE && patientFlexion < 90)
			correctedPatientFlexion = MIN_FLEXION_ANGLE;
		if (patientFlexion >= 90)
			correctedPatientFlexion = MAX_FLEXION_ANGLE;
		double flexionAngle = (double) correctedPatientFlexion * 2.0 * Math.PI
				/ 360.0 + ((double) patientFlexion / 120.0)
				* (ARC_MAX_ANGLE_OFFSET - ARC_MIN_ANGLE_OFFSET)
				+ ARC_MIN_ANGLE_OFFSET;
		int[] extensionOffsets = CalculateAngleStringOffsets(arcRadius
				+ ARC_EXTENSION_RADIUS_OFFSET, extensionAngle, patientExtension);
		int[] flexionOffsets = CalculateAngleStringOffsets(arcRadius
				+ ARC_FLEXION_RADIUS_OFFSET, flexionAngle,
				correctedPatientFlexion);
		pdfGraphics.setColor(Color.BLACK);
		pdfGraphics.setFont(CommonGraphics.innerFont);
		pdfGraphics.drawString("" + patientExtension, boundingRectangle.x
				+ arcLeftBoundary + extensionOffsets[0], boundingRectangle.y
				+ arcTopBoundary + extensionOffsets[1]);
		pdfGraphics.drawString("" + patientFlexion, boundingRectangle.x
				+ arcLeftBoundary + flexionOffsets[0], boundingRectangle.y
				+ arcTopBoundary + flexionOffsets[1]);
	}

	private int[] CalculateAngleStringOffsets(double radius, double angle,
			int integerAngle) {
		// First we want to make sure that the string does not intersect with
		// the arc
		boolean arcIntersect = true;
		double stringRadius = radius;
		double numCharacters = 0.0;
		if (integerAngle / 100 > 0)
			numCharacters = 3.0;
		else if (integerAngle / 10 > 0)
			numCharacters = 2.0;
		else
			numCharacters = 1.0;
		while (arcIntersect) {
			// We need to check all 4 corners of the string. If any of them
			// intersect then we increase the radius
			double lowerLeftX = stringRadius * Math.cos(angle);
			double lowerLeftY = stringRadius * Math.sin(angle);
			double upperLeftX = lowerLeftX;
			double upperLeftY = lowerLeftY - ARC_STRING_HEIGHT;
			double lowerRightX = lowerLeftX + ARC_STRING_WIDTH
					* (numCharacters / 3.0);
			double lowerRightY = lowerLeftY;
			double upperRightX = lowerRightX;
			double upperRightY = upperLeftY;
			if (Math.sqrt(Math.pow(lowerLeftX, 2.0) + Math.pow(lowerLeftY, 2.0)) < radius
					|| Math.sqrt(Math.pow(upperLeftX, 2.0)
							+ Math.pow(upperLeftY, 2.0)) < radius
					|| Math.sqrt(Math.pow(lowerRightX, 2.0)
							+ Math.pow(lowerRightY, 2.0)) < radius
					|| Math.sqrt(Math.pow(upperRightX, 2.0)
							+ Math.pow(upperRightY, 2.0)) < radius)
				stringRadius += 0.01;
			else
				arcIntersect = false;
		}
		// Now we need to make sure that the string is not too low
		return new int[] {
				(int) (stringRadius + stringRadius * Math.cos(angle)),
				(int) (stringRadius + stringRadius * Math.sin(angle)) };
	}
}