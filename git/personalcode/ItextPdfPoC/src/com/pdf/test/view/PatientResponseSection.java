/**
 * 
 */
package com.pdf.test.view;

import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.pdf.test.helper.AlternatingBackgroundEvent;

/**
 * @author jayaram
 * 
 */
public class PatientResponseSection extends CommonSection {
	/**
	 * 
	 * @return
	 */
	String questionArray[] = { "Satisfaction with Progress (0 low - 10 high)",
			"Fever/Chills/Night Sweats [Y/N]",
			"Prescription Pain Med Use [Y/N]", "Medication Side Effects [Y/N]",
			"Pain Rating (0 none - 10 worst ever)",
			"Pain Timing (Rest/Activity/Entire day)",
			"Perceived Stability (0 unstable - 10 very stable)", "Falls [Y/N]",
			"Using Assistive Device [Y/N]", "Using Stairs [Y/N]" };

	private String answerArray[];
	private boolean[] withinRange;
	private String imageName;
	private String tagDateTimeForImage;

	/**
	 * 
	 * @param answerArray
	 * @param 
	 * @param imageName
	 * @throws Exception
	 */
	public PatientResponseSection(String[] answerArray,
			boolean[] withinRange, String imageName,String tagDateTime) throws Exception {
		this.answerArray = answerArray;
		this.imageName = imageName;
		this.withinRange = withinRange;
		this.tagDateTimeForImage=tagDateTime;
		if (answerArray == null || answerArray.length != questionArray.length
				|| withinRange == null
				|| withinRange.length != questionArray.length ) {
			throw new Exception("Invalid input");
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws BadElementException
	 */
	public PdfPTable getPatientResponseSection() throws BadElementException,
			MalformedURLException, IOException {
		PdfPTable response = new PdfPTable(new float[] { 75, 25 });

		PdfPCell patientResponseCell = getResponseCell();
		PdfPCell patientImageCell = getImageCell();

		// Setting borders for Response and Image Cell
		patientResponseCell.setBorder(INFORMATION_SECTION_BORDER);
		patientResponseCell.setBorderColor(borderColor);
		patientResponseCell.setBorderWidth(borderWidth);

		patientImageCell.setBorder(INFORMATION_SECTION_BORDER);
		patientImageCell.setBorderColor(borderColor);
		patientImageCell.setBorderWidth(borderWidth);

		response.addCell(patientResponseCell);
		response.addCell(patientImageCell);

		return response;
	}

	/**
	 * 
	 * @return PdfPCell in tabular format
	 */
	private PdfPCell getResponseCell() {
		PdfPCell responseCell = new PdfPCell();
		Chunk responseHeader = new Chunk(GROUP1_HEADER_TEXT, headingFont);

		PdfPTable appQuestions = new PdfPTable(new float[] { 8, 86, 8 });

		PdfPTableEvent alternatingEvent = AlternatingBackgroundEvent
				.getInstance();
		appQuestions.setTableEvent(alternatingEvent);
		getResponseTableData(appQuestions);

		responseCell.addElement(responseHeader);
		responseCell.addElement(appQuestions);
		return responseCell;
	}

	/**
	 * Helper function for getResponseCell()
	 * 
	 * @param appQuestions
	 */
	private void getResponseTableData(PdfPTable appQuestions) {
		for (int i = 0; i < 10; i++) {
			appQuestions.addCell(getTableCell(String.valueOf(i + 1) + ".",
					textFont));
			appQuestions.addCell(getTableCell(questionArray[i], textFont));
			appQuestions.addCell(getTableCell(answerArray[i], withinRange[i]?textFont:alertFont));
		}
	}

	/**
	 * 
	 * @return
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private PdfPCell getImageCell() throws BadElementException,
			MalformedURLException, IOException {

		PdfPCell imageCell = new PdfPCell();

		Image img = Image.getInstance(imageName);

		//System.out.println("Image width" + img.getWidth());
		//System.out.println("Image height" + img.getHeight());
		img.setAlignment(Image.ALIGN_CENTER | Image.TEXTWRAP);
		img.setBorder(Image.BOX);
		img.setRotationDegrees(90);
		img.setBorderWidth(5);
		// img.setScaleToFitHeight(true);
		img.setBorderColor(BaseColor.LIGHT_GRAY);
		img.scaleToFit(100, 600);
		Chunk imageFooter = new Chunk(IMAGE_FOOTER, greenSubImageFont);
		Chunk tagDate = new Chunk(tagDateTimeForImage, textFont);
		// Chunk test3=new Chunk(Chunk.NEWLINE);
		imageCell.setPaddingTop(20);
		imageCell.addElement(img);
		imageCell.addElement(imageFooter);
		imageCell.addElement(tagDate);
		// imageCell.addElement(test3);
		imageCell.setExtraParagraphSpace(10);
		return imageCell;

	}

}
