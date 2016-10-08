package patientprocessing;

import gnu.jpdf.BoundingBox;
import gnu.jpdf.PDFGraphics;
import gnu.jpdf.StringTooLongException;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;

// Class responsible for drawing the alerts section of the report
public class AlertsSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;
	private final double TEXT_HORIZ_BOUNDARY = 0.142;
	private final double TEXT_VERT_BOUNDARY = 0.183;
	private final double TEXT_RIGHT_BOUNDARY = 0.9;
	private final int BULLET_DIAMETER = 3;
	private final int BULLET_HORIZ_PADDING = 1;
	private final int BULLET_VERT_PADDING = 5;
	private final String headingString = "ALERTS:";
	private final double HEADING_VERT_BOUNDARY = 0.132;

	// For now just hardcode this in but obviously this will be dynamically
	// generated later
	/*private  String[] alertStrings = { "Patient ROM decreased",
			"Walking activity decreased", "Pain rating outside normal limits",
			"Patient not performing prescribed exercises" };
*/
	public AlertsSection(PDFGraphics g, Rectangle r) throws Exception {
		if (g == null || r == null)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
	}

	public void Draw(String[] alertStrings) {
		//if(alertStrings2.length!=0){
			//alertStrings=alertStrings2;
		//}
		// First print a heading at the top
		int headingTopBoundary = (int) ((double) boundingRectangle.height * HEADING_VERT_BOUNDARY);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.setColor(CommonGraphics.DARKER_RED);
		pdfGraphics.drawString(headingString, boundingRectangle.x,
				boundingRectangle.y + headingTopBoundary);

		// Set up a bounding box for the alert strings
		int alertsLeftBoundary = (int) ((double) boundingRectangle.width * TEXT_HORIZ_BOUNDARY);
		int alertsTopBoundary = (int) ((double) boundingRectangle.height * TEXT_VERT_BOUNDARY);
		int alertsRightBoundary = (int) ((double) boundingRectangle.width * TEXT_RIGHT_BOUNDARY);

		Point topLeftCorner = new Point(boundingRectangle.x
				+ alertsLeftBoundary, boundingRectangle.y + alertsTopBoundary);
		Dimension boxSize = new Dimension(alertsRightBoundary
				- alertsLeftBoundary, boundingRectangle.height
				- alertsTopBoundary);
		BoundingBox parentBox = new BoundingBox(topLeftCorner, boxSize);

		// Now print each of the strings in this bounding box
		pdfGraphics.setFont(CommonGraphics.innerFont);
		pdfGraphics.setColor(CommonGraphics.DARKER_RED);
		FontMetrics metrics = pdfGraphics.getFontMetrics();

		for (int i = 0; i < alertStrings.length; i++) {
			try {
				BoundingBox child = parentBox.getStringBounds(alertStrings[i],
						BoundingBox.HORIZ_ALIGN_LEFT,
						BoundingBox.VERT_ALIGN_TOP, metrics, 0);
				child.drawWrappedString(pdfGraphics, metrics, 0,
						BoundingBox.HORIZ_ALIGN_LEFT);

				// We need to draw the bullet for each alert string as well
				pdfGraphics.fillOval(
						boundingRectangle.x + BULLET_HORIZ_PADDING, parentBox.y
								+ BULLET_VERT_PADDING, BULLET_DIAMETER,
						BULLET_DIAMETER);

				parentBox.subtract(child, BoundingBox.SUBTRACT_FROM_BOTTOM);
			} catch (IllegalArgumentException e) {
				// This should not happen so throw e up to the client
				throw e;
			} catch (StringTooLongException e) {
				// TODO: We need to handle this case where we have too many
				// alerts to print
			}
		}
	}
}