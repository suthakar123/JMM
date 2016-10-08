package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

// A class full of helper functions that can be used by each of the section classes
public class CommonGraphics {

	// Some fonts to use throughout the document
	public static Font innerFont = new Font("Helvetica Neue", Font.PLAIN, 10);
	public static Font boldInnerFont = new Font("Helvetica Neue", Font.BOLD, 10);
	public static Font italicInnerFont = new Font("Helvetica Neue",
			Font.ITALIC, 10);
	public static Font headingFont = new Font("Helvetica Neue Medium",
			Font.PLAIN, 16);
	public static Font mediumInnerFont = new Font("Helvetica Neue Medium",
			Font.PLAIN, 10);
	public static Font columnHeadingFont = new Font("Helvetica Neue",
			Font.ITALIC, 8);
	public static Font smallInnerFont = new Font("Helvetica Neue", Font.PLAIN,
			8);
	public static Font boldSmallInnerFont = new Font("Helvetica Neue Medium",
			Font.PLAIN, 8);
	public static Font titleFont = new Font("Helvetica Neue Medium",
			Font.PLAIN, 20);

	// Some colors to use throughout the document
	public static Color DARK_GREEN = new Color(0x43, 0x95, 0x39);
	public static Color VERY_LIGHT_GRAY = new Color(0xF0, 0xF0, 0xF0);
	public static Color DARKER_LIGHT_GRAY = new Color(0xE8, 0xE8, 0xE8);
	public static Color DARKER_RED = new Color(0xDD, 0x00, 0x00);

	// A helper function to draw a table with evenly sized rows
	public static void DrawTable(PDFGraphics pdfGraphics,
			Rectangle boundingRectangle, double topBoundaryPercentage,
			double bottomBoundaryPercentage, int numRectangles,
			int spacingPixels) throws Exception {
		// First check to make sure all the inputs make sense
		if (pdfGraphics == null || boundingRectangle == null
				|| topBoundaryPercentage < 0.0 || topBoundaryPercentage > 1.0
				|| bottomBoundaryPercentage < 0.0
				|| bottomBoundaryPercentage > 1.0
				|| topBoundaryPercentage > bottomBoundaryPercentage
				|| numRectangles < 0 || spacingPixels < 0)
			throw new Exception(ErrorCodes.Errors[16]);

		// First we need to figure out the height of each rectangle that we need
		// to draw
		int topBoundary = (int) ((double) boundingRectangle.height * topBoundaryPercentage);
		int bottomBoundary = (int) ((double) boundingRectangle.height * bottomBoundaryPercentage);
		int rectangleHeight = (bottomBoundary - topBoundary - (numRectangles - 1)
				* spacingPixels)
				/ numRectangles;

		// Now that we have the height, we can draw all of the rectangles
		for (int i = 0; i < numRectangles; i++) {
			// Switch color every time
			if (i % 2 == 0)
				pdfGraphics.setColor(VERY_LIGHT_GRAY);
			else
				pdfGraphics.setColor(DARKER_LIGHT_GRAY);
			pdfGraphics.fillRect(boundingRectangle.x, boundingRectangle.y
					+ topBoundary + i * (rectangleHeight + spacingPixels),
					boundingRectangle.width, rectangleHeight);
		}
	}

	// A helper function to draw strings within a table
	public static void DrawTableStrings(PDFGraphics pdfGraphics,
			Rectangle boundingRectangle, double topBoundaryPercentage,
			double bottomBoundaryPercentage, int numRectangles,
			int spacingPixels, String[] strings, Font stringFont,
			boolean rightAligned) throws Exception {
		// First check to make sure all the inputs make sense
		if (pdfGraphics == null || boundingRectangle == null
				|| topBoundaryPercentage < 0.0 || topBoundaryPercentage > 1.0
				|| bottomBoundaryPercentage < 0.0
				|| bottomBoundaryPercentage > 1.0
				|| topBoundaryPercentage > bottomBoundaryPercentage
				|| numRectangles < 0 || spacingPixels < 0 || strings == null
				|| strings.length != numRectangles || stringFont == null)
			throw new Exception(ErrorCodes.Errors[16]);

		// First we need to figure out the height of each rectangle where we
		// need to draw a string
		int topBoundary = (int) ((double) boundingRectangle.height * topBoundaryPercentage);
		int bottomBoundary = (int) ((double) boundingRectangle.height * bottomBoundaryPercentage);
		int rectangleHeight = (bottomBoundary - topBoundary - (numRectangles - 1)
				* spacingPixels)
				/ numRectangles;

		// Now that we have the height, we can draw all of the strings
		pdfGraphics.setFont(stringFont);
		for (int i = 0; i < numRectangles; i++) {
			// We want to draw the string in red if it ends in '*'
			if (strings[i].endsWith("*")) {
				pdfGraphics.setColor(CommonGraphics.DARKER_RED);
				pdfGraphics.setFont(stringFont.deriveFont(Font.BOLD));
			} else {
				pdfGraphics.setColor(Color.BLACK);
				pdfGraphics.setFont(stringFont);
			}
			int topString = boundingRectangle.y + topBoundary + i
					* (rectangleHeight + spacingPixels);
			int bottomString = topString + rectangleHeight;
			if (rightAligned) {
				// If we have an asterisk then we want to draw that past the
				// right-align
				if (strings[i].endsWith("*")) {
					String nonAsteriskStr = strings[i].substring(0,
							strings[i].length() - 1);
					pdfGraphics.drawString(
							nonAsteriskStr,
							boundingRectangle.x
									- pdfGraphics.getFontMetrics().stringWidth(
											nonAsteriskStr),
							(topString + bottomString) / 2
									+ pdfGraphics.getFontMetrics().getHeight()
									/ 4);
					pdfGraphics.drawString("*", boundingRectangle.x,
							(topString + bottomString) / 2
									+ pdfGraphics.getFontMetrics().getHeight()
									/ 4);
				} else {
					pdfGraphics.drawString(
							strings[i],
							boundingRectangle.x
									- pdfGraphics.getFontMetrics().stringWidth(
											strings[i]),
							(topString + bottomString) / 2
									+ pdfGraphics.getFontMetrics().getHeight()
									/ 4);
				}
			} else {
				pdfGraphics.drawString(strings[i], boundingRectangle.x,
						(topString + bottomString) / 2
								+ pdfGraphics.getFontMetrics().getHeight() / 4);
			}
		}
	}
}
