package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Rectangle;

// Class responsible for drawing the info section of the report
public class InfoSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;

	// Table variables
	private final double TOP_BOUNDARY_PERCENTAGE = 0.065;
	private final double BOTTOM_BOUNDARY_PERCENTAGE = 0.925;
	private final int NUM_RECTANGLES = 7;
	private final int SPACING_PIXELS = 2;
	private final String[] headerStrings = { "Surgeon:", "Institution:",
			"Report Date:", "Duration:", "Status:", "Measurement Date:",
			"Prior Report(s):" };
	private final int HEADER_STRING_PADDING = 2;
	private final double INFO_STRING_PERCENTAGE = 0.276;
	private String[] infoStrings;

	public InfoSection(PDFGraphics g, Rectangle r, String[] i) throws Exception {
		if (g == null || r == null || i == null)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
		infoStrings = i;
	}

	public void Draw() throws Exception {
		// Draw the rectangle table
		CommonGraphics.DrawTable(pdfGraphics, boundingRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS);

		// Draw the headings for each of the info boxes
		int leftBoundary = (int) ((double) boundingRectangle.width * INFO_STRING_PERCENTAGE);
		Rectangle headerStringRectangle = new Rectangle(boundingRectangle.x
				+ HEADER_STRING_PADDING, boundingRectangle.y, leftBoundary,
				boundingRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, headerStringRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, headerStrings,
				CommonGraphics.boldInnerFont, false);

		// Now draw the actual info strings that correspond to the headers
		Rectangle infoStringRectangle = new Rectangle(boundingRectangle.x
				+ leftBoundary, boundingRectangle.y, boundingRectangle.width
				- leftBoundary, boundingRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, infoStringRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, infoStrings,
				CommonGraphics.innerFont, false);
	}
}
