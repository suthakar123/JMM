package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

// Class responsible for drawing the patient responses section of the report
public class PatientResponsesSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;
	private BufferedImage kneeImage;
	private String[] answerStrings;

	// Table variables
	private final double TOP_BOUNDARY_PERCENTAGE = 0.135;
	private final double BOTTOM_BOUNDARY_PERCENTAGE = 0.84;
	private final double TABLE_WIDTH_PERCENTAGE = 0.701;
	private final int NUM_RECTANGLES = 10;
	private final int SPACING_PIXELS = 2;
	private final int NUMBER_STRING_PADDING = 2;
	private final double QUESTION_STRING_PERCENTAGE = 0.076;
	private final double ANSWER_STRING_PERCENTAGE = 0.96;
	private final String[] questionStrings = {
			"Satisfaction with Progress (0 low - 10 high)",
			"Fever/Chills/Night Sweats [Y/N]",
			"Prescription Pain Med Use [Y/N]", "Medication Side Effects [Y/N]",
			"Pain Rating (0 none - 10 worst ever)",
			"Pain Timing (Rest/Activity/Entire day)",
			"Perceived Stability (0 unstable - 10 very stable)", "Falls [Y/N]",
			"Using Assistive Device [Y/N]", "Using Stairs [Y/N]" };
	private final double HEADING_PERCENTAGE = 0.1;
	private final String headerString = "GROUP 1 - Patient Subjective Responses";
	private final double SECURE_MESSAGE_PERCENTAGE = 0.847;
	private final String secureMessageString = "Secure message from the patient:";
	private final double IMAGE_STRING_PERCENTAGE = 0.76;
	private final String imageString = "Patient Supplied Image";
	private final int BETWEEN_LINE_PADDING = -1;
	private final double IMAGE_SCALING_PERCENTAGE = 0.9;
	private String userMessageString;
	private String dateString;
	private String timeString;

	// Picture variables
	private final double PICTURE_LEFT_BOUNDARY_PERCENTAGE = 0.758;
	private final double PICTURE_TOP_BOUNDARY_PERCENTAGE = 0.058;
	private final double PICTURE_BOTTOM_BOUNDARY_PERCENTAGE = 0.753;

	public PatientResponsesSection(PDFGraphics g, Rectangle r, BufferedImage i,
			String[] aS, String d, String t) throws Exception {
		if (g == null || r == null || i == null || aS == null || d == null
				|| t == null)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
		kneeImage = i;
		answerStrings = aS;
		dateString = d;
		timeString = t;
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

		// Next we want to draw the rectangle that will surround the patient
		// provided picture
		int pictureLeftBoundary = (int) ((double) boundingRectangle.width * PICTURE_LEFT_BOUNDARY_PERCENTAGE);
		int pictureTopBoundary = (int) ((double) boundingRectangle.height * PICTURE_TOP_BOUNDARY_PERCENTAGE);
		int pictureBottomBoundary = (int) ((double) boundingRectangle.height * PICTURE_BOTTOM_BOUNDARY_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.VERY_LIGHT_GRAY);
		pdfGraphics.fillRect(pictureLeftBoundary + boundingRectangle.x,
				pictureTopBoundary + boundingRectangle.y,
				boundingRectangle.width - pictureLeftBoundary,
				pictureBottomBoundary - pictureTopBoundary);

		// Now we need to draw all of the strings. First we want to draw the
		// numbering of the questions
		int questionBoundary = (int) ((double) tableRectangle.width * QUESTION_STRING_PERCENTAGE);
		Rectangle numberStringRectangle = new Rectangle(tableRectangle.x
				+ NUMBER_STRING_PADDING, tableRectangle.y, questionBoundary,
				tableRectangle.height);
		String[] numberStrings = new String[NUM_RECTANGLES];
		for (int i = 0; i < NUM_RECTANGLES; i++) {
			numberStrings[i] = "" + (i + 1) + ".";
		}
		CommonGraphics.DrawTableStrings(pdfGraphics, numberStringRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, numberStrings,
				CommonGraphics.innerFont, false);

		// Now draw the actual question strings that correspond to the numbers
		int answerBoundary = (int) ((double) tableRectangle.width * ANSWER_STRING_PERCENTAGE);
		Rectangle questionStringRectangle = new Rectangle(tableRectangle.x
				+ questionBoundary, tableRectangle.y, answerBoundary
				- questionBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, questionStringRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, questionStrings,
				CommonGraphics.innerFont, false);

		// Finally we need to draw the answer strings
		Rectangle answerStringRectangle = new Rectangle(tableRectangle.x
				+ answerBoundary, tableRectangle.y, tableRectangle.width
				- answerBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, answerStringRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, answerStrings,
				CommonGraphics.innerFont, true);

		// The table is done so now we need to add a couple last strings
		// Add the secure message string
		int secureMessageBoundary = (int) ((double) boundingRectangle.height * SECURE_MESSAGE_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.mediumInnerFont);
		pdfGraphics.drawString(secureMessageString, boundingRectangle.x,
				boundingRectangle.y + secureMessageBoundary
						+ pdfGraphics.getFontMetrics().getHeight());

		pdfGraphics.setColor(Color.BLACK);
		pdfGraphics.setFont(CommonGraphics.italicInnerFont);
		pdfGraphics.drawString(userMessageString, boundingRectangle.x,
				boundingRectangle.y + secureMessageBoundary + 2
						* pdfGraphics.getFontMetrics().getHeight()
						+ BETWEEN_LINE_PADDING);

		// Add the timestamp below the image
		int imageStringBoundary = (int) ((double) boundingRectangle.height * IMAGE_STRING_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.drawString(imageString, boundingRectangle.x
				+ pictureLeftBoundary, boundingRectangle.y
				+ imageStringBoundary
				+ pdfGraphics.getFontMetrics().getHeight());

		pdfGraphics.setColor(Color.BLACK);
		pdfGraphics.setFont(CommonGraphics.innerFont);
		pdfGraphics.drawString("Tagged: " + dateString, boundingRectangle.x
				+ pictureLeftBoundary, boundingRectangle.y
				+ imageStringBoundary + 2
				* pdfGraphics.getFontMetrics().getHeight()
				+ BETWEEN_LINE_PADDING);

		pdfGraphics.drawString(timeString, boundingRectangle.x
				+ pictureLeftBoundary, boundingRectangle.y
				+ imageStringBoundary + 3
				* pdfGraphics.getFontMetrics().getHeight() + 2
				* BETWEEN_LINE_PADDING);

		// Finally display the actual image in the image space
		int boundaryWidth = boundingRectangle.width - pictureLeftBoundary;
		int boundaryHeight = pictureBottomBoundary - pictureTopBoundary;
		int imageWidth = (int) ((double) IMAGE_SCALING_PERCENTAGE * boundaryWidth);
		int imageHeight = (int) ((double) IMAGE_SCALING_PERCENTAGE * boundaryHeight);
		// If the image is landscape we need to first rotate it
		if (kneeImage.getWidth() > kneeImage.getHeight()) {
			BufferedImage rotatedImage = new BufferedImage(
					kneeImage.getHeight(), kneeImage.getWidth(),
					kneeImage.getType());
			for (int x = 0; x < kneeImage.getWidth(); x++) {
				for (int y = 0; y < kneeImage.getHeight(); y++) {
					rotatedImage.setRGB(y, rotatedImage.getHeight() - x - 1,
							kneeImage.getRGB(x, y));
				}
			}
			kneeImage = rotatedImage;
		}
		kneeImage = Scalr.resize(kneeImage, Scalr.Method.ULTRA_QUALITY,
				Scalr.Mode.AUTOMATIC, imageWidth, imageHeight);
		int xOffset = (boundaryWidth - kneeImage.getWidth()) / 2;
		int yOffset = (boundaryHeight - kneeImage.getHeight()) / 2;
		pdfGraphics.drawImage(kneeImage, boundingRectangle.x
				+ pictureLeftBoundary + xOffset, boundingRectangle.y
				+ pictureTopBoundary + yOffset, null);
	}
}