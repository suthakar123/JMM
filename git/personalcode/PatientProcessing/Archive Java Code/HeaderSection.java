package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;

import javax.imageio.ImageIO;

// Class responsible for drawing the header to the report
public class HeaderSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;
	private final String imagePath = "resources\\JM_logo_final.bmp";
	private String[] patientStrings;

	// Variables for all of the strings
	private final String headerString = "KNEE REPORT";
	private final String emailString = "reports@JointMetrixMedical.com";
	private final String[] phoneStrings = { "Tel: ", "206.555.5555", "Fax: ",
			"206.555.5556" };
	private final int BETWEEN_LINE_PADDING = -1;
	private final double PATIENT_INFO_PERCENTAGE = 0.4;
	private final int PATIENT_INFO_TOP_PADDING = 6;
	private final double LOGO_PERCENTAGE = 0.654;
	private final double LOGO_TOP_PERCENTAGE = 0.13;
	private final double LOGO_BOTTOM_PERCENTAGE = 0.8;

	public HeaderSection(PDFGraphics g, Rectangle r, String[] p)
			throws Exception {
		if (g == null || r == null || p == null)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
		patientStrings = p;
	}

	public void Draw() throws Exception {
		// First we need to draw the title on the left
		pdfGraphics.setFont(CommonGraphics.titleFont);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		int titleLowerBound = boundingRectangle.y
				+ pdfGraphics.getFontMetrics().getHeight();
		pdfGraphics.drawString(headerString, boundingRectangle.x,
				titleLowerBound);

		// Now draw all of the information below the title
		pdfGraphics.setFont(CommonGraphics.innerFont);
		pdfGraphics.setColor(Color.BLACK);
		int emailLowerBound = titleLowerBound
				+ pdfGraphics.getFontMetrics().getHeight()
				+ BETWEEN_LINE_PADDING;
		pdfGraphics.drawString(emailString, boundingRectangle.x,
				emailLowerBound);

		// The phone string alternates colors so take advantage of that
		pdfGraphics.setFont(CommonGraphics.innerFont);
		int phoneLowerBound = emailLowerBound
				+ pdfGraphics.getFontMetrics().getHeight()
				+ BETWEEN_LINE_PADDING;
		int phoneStringX = boundingRectangle.x;
		for (int i = 0; i < phoneStrings.length; i++) {
			if (i % 2 == 0)
				pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
			else
				pdfGraphics.setColor(Color.BLACK);
			pdfGraphics.drawString(phoneStrings[i], phoneStringX,
					phoneLowerBound);
			phoneStringX += pdfGraphics.getFontMetrics().stringWidth(
					phoneStrings[i]);
		}

		// Now we need to draw all of the patient info
		int patientInfoBoundary = (int) ((double) boundingRectangle.width * PATIENT_INFO_PERCENTAGE);
		pdfGraphics.setFont(CommonGraphics.innerFont);
		pdfGraphics.setColor(Color.BLACK);
		int patientLowerBound = boundingRectangle.y
				+ pdfGraphics.getFontMetrics().getHeight()
				+ PATIENT_INFO_TOP_PADDING;
		for (int i = 0; i < patientStrings.length; i++) {
			pdfGraphics.drawString(patientStrings[i], boundingRectangle.x
					+ patientInfoBoundary, patientLowerBound);
			patientLowerBound += pdfGraphics.getFontMetrics().getHeight()
					+ BETWEEN_LINE_PADDING;
		}

		// Finally draw the logo
		// TODO: Ask Peter to provide an image that is the proper size - that is
		// the only way to get a clear logo
		int logoTopBoundary = (int) ((double) boundingRectangle.height * LOGO_TOP_PERCENTAGE);
		int logoBottomBoundary = (int) ((double) boundingRectangle.height * LOGO_BOTTOM_PERCENTAGE);
		int logoLeftBoundary = (int) ((double) boundingRectangle.width * LOGO_PERCENTAGE);
		int logoWidth = boundingRectangle.width - logoLeftBoundary;
		int logoHeight = logoBottomBoundary - logoTopBoundary;
		Image logoImage = ImageIO.read(new File(imagePath)).getScaledInstance(
				logoWidth, logoHeight, Image.SCALE_AREA_AVERAGING);
		// BufferedImage logoImage = Scalr.resize(ImageIO.read(new
		// File(imagePath)), Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC,
		// logoWidth, logoHeight);
		pdfGraphics.drawImage(logoImage,
				boundingRectangle.x + logoLeftBoundary, boundingRectangle.y
						+ logoTopBoundary, null);
	}
}
