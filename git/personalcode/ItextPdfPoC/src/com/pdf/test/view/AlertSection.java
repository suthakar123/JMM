/**
 * 
 */
package com.pdf.test.view;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @author jayaram
 * 
 */
public class AlertSection extends CommonSection {

	
	private String[] alerts;

	public AlertSection(String[] alerts) {
		this.alerts = alerts;
	}

	/**
	 * TODO: Check the limit of the Alert Section to allow only limited number
	 * of Alerts
	 * 
	 * @return
	 * @throws Exception
	 */
	public PdfPTable getAlertSection() throws Exception {
		// Count the number of Alerts
		int numberOfAlerts = checkLimitReturnNumberOfAlerts(alerts);
		// Get the table dimensions according to number of alerts. If more than
		// 5, only first 5 will be shown
		PdfPTable table = getPdfPTableDimensions(numberOfAlerts);
		
		PdfPCell alertTitleCell=getTableCell(ALERT_TITLE, Element.ALIGN_LEFT,
				alertHeadingFont);
		alertTitleCell.setMinimumHeight(CELL_MINIMUM_HEIGHT_IN_ALERT);
		table.addCell(alertTitleCell);
		fillRemainingTableCells(table,numberOfAlerts);
		return table;
	}

	private void fillRemainingTableCells(PdfPTable table,int numberOfAlerts) {
		int counter = 0;
		for (int i = 1; i <= 5; i++) {
			//If no alerts, then display successful message in the center
			if(numberOfAlerts==0){
				PdfPCell noAlertMessageCell=getTableCell(ALERT_NO_ALERTS_MESSAGE,Element.ALIGN_CENTER,Element.ALIGN_TOP,alertFont);
				noAlertMessageCell.setMinimumHeight(CELL_MINIMUM_HEIGHT_IN_ALERT);
				table.addCell(noAlertMessageCell);
				break;
			}
			//If only 1 alert, then alert will be shown in the next line.
			if(numberOfAlerts==1){
				i=5;
			}
			// For 2 alerts, the row of Alert title will not be
			// displayed
			if (numberOfAlerts == 2 && i == 1) {
				table.completeRow();
				i = 4;
			}
			// For 3 alerts, the row of the Alert title will not be displayed
			if (numberOfAlerts == 3 && i == 1) {
				table.completeRow();
				i = 3;
			}
			// For 4 alerts, the field below the Alert title will not be
			// displayed
			if (numberOfAlerts == 4 && i == 3) {
				i++;
				PdfPCell emptyCell=getTableCell("", alertFont);
				emptyCell.setMinimumHeight(CELL_MINIMUM_HEIGHT_IN_ALERT);
				table.addCell(emptyCell);
			}

			/*
			 * //Using number system 
			 * String display=(counter+1)+COLON+str; PdfPCell
			 * cell=getTableCell(display,alertFont); cell.setMinimumHeight(16);
			 * table.addCell(cell);
			 */
			// Using Bullets
			PdfPCell cell2 = getBulletedCellData(alerts[counter++], alertFont);
			cell2.setMinimumHeight(CELL_MINIMUM_HEIGHT_IN_ALERT);
			table.addCell(cell2);
		}
		table.completeRow();
	}

	/**
	 * 
	 * @param numberOfAlerts
	 * @return
	 * @throws Exception
	 */
	private PdfPTable getPdfPTableDimensions(int numberOfAlerts)
			throws Exception {
		if (numberOfAlerts > 5) {
			throw new Exception("Number of Alerts Exceeded");
		}
		if (numberOfAlerts == 0 || numberOfAlerts == 1) {
			return new PdfPTable(1);
		}
		if (numberOfAlerts == 2) {
			return new PdfPTable(new float[] { 50, 50 });
		}
		if (numberOfAlerts == 4) {
			return new PdfPTable(new float[] { 20, 40, 40 });
		}
		if (numberOfAlerts == 5 || numberOfAlerts == 3) {
			return new PdfPTable(3);
		} else
			return new PdfPTable(3);

	}

	/**
	 * This method will limit the number of alerts to 5 and the length of the
	 * alerts. This method will return the number of alerts in the alertString.
	 * If more than 5, then 5 is returned.
	 * 
	 * @param String
	 *            array of alerts
	 * @return number of alerts in the alertString. If size of array>5. then
	 *         return 5;
	 */
	private int checkLimitReturnNumberOfAlerts(String[] alerts2) {
		if (alerts2.length > 5)
			return 5;
		if (alerts2 == null || alerts2.length == 0) {
			return 0;
		}
		return alerts2.length;
	}
}
