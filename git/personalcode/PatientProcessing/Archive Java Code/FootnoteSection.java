package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Rectangle;

// Class responsible for drawing the footnote to the report
public class FootnoteSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;

	// Variables for the strings
	private final String startString = "* Values in red    ";
	private final int TOP_PADDING = 2;
	private final String hashString = "# Normal values adjusted for days post-op";
	private final double HASH_STRING_PERCENTAGE = 0.55;
	private final String endString = "are outside normal limits for a patient %d days post-TKA";
	private int dayDifference;

	public FootnoteSection(PDFGraphics g, Rectangle r, int d) throws Exception {
		if (g == null || r == null)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
		dayDifference = d;
	}

	public void Draw() {
		// Here we simply need to draw a couple of strings
		pdfGraphics.setFont(CommonGraphics.boldSmallInnerFont);
		pdfGraphics.setColor(CommonGraphics.DARKER_RED);
		int stringHeight = pdfGraphics.getFontMetrics().getHeight();
		pdfGraphics.drawString(startString, boundingRectangle.x,
				boundingRectangle.y + stringHeight + TOP_PADDING);
		int startStringLength = pdfGraphics.getFontMetrics().stringWidth(
				startString);

		pdfGraphics.setFont(CommonGraphics.smallInnerFont);
		pdfGraphics.setColor(Color.BLACK);
		pdfGraphics.drawString(String.format(endString, dayDifference),
				boundingRectangle.x + startStringLength, boundingRectangle.y
						+ stringHeight + TOP_PADDING);

		int hashStringOffset = (int) ((double) boundingRectangle.width * HASH_STRING_PERCENTAGE);
		pdfGraphics.setFont(CommonGraphics.smallInnerFont);
		pdfGraphics.setColor(Color.BLACK);
		pdfGraphics.drawString(hashString, boundingRectangle.x
				+ hashStringOffset, boundingRectangle.y + stringHeight
				+ TOP_PADDING);
	}
}