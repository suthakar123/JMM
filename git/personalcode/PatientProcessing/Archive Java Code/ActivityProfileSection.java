package patientprocessing;

import gnu.jpdf.PDFGraphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

// Class responsible for drawing the activity profile section of the report
public class ActivityProfileSection {

	// Variables for drawing the PDF page
	private PDFGraphics pdfGraphics;
	private Rectangle boundingRectangle;

	// Table variables
	private final double TOP_BOUNDARY_PERCENTAGE = 0.262;
	private final double BOTTOM_BOUNDARY_PERCENTAGE = 0.948;
	private final double TABLE_WIDTH_PERCENTAGE = 0.598;
	private final int NUM_RECTANGLES = 7;
	private final int SPACING_PIXELS = 2;
	private final int ACTIVITY_NAME_PADDING = 2;
	private final double DURATION_PERCENTAGE = 0.515;
	private final String[] activityStrings = { "Lying", "Sitting", "Standing",
			"Walking", "Stairs", "Exercise Rx", "Bike" };
	private final double CHANGE_PERCENTAGE = 0.92;
	private final double HEADING_PERCENTAGE = 0.135;
	private final String headerString = "GROUP 3 - Activity Profile";
	private final int COLUMN_HEADING_PADDING = 5;
	private final double DURATION_HEADING_PERCENTAGE = 0.235;
	private final double CHANGE_HEADING_PERCENTAGE = 0.478;

	// Variables for the ring chart
	private final double RING_LEFT_PERCENTAGE = 0.63;
	private final double RING_VERT_PERCENTAGE = 0.18;
	private final double RING_RIGHT_PERCENTAGE = 0.852;
	private final double INNER_CIRCLE_PERCENTAGE = 0.64;
	private final Color[] ringColors = { new Color(0x21, 0x61, 0xA5),
			new Color(0x8C, 0xC7, 0x42), new Color(0xEF, 0x1C, 0x21),
			new Color(0x00, 0x9A, 0xBD), new Color(0x84, 0x41, 0x9C),
			new Color(0xF7, 0xAE, 0x39), new Color(0xDF, 0x1B, 0xE4) };
	private final String ringString = "Hours Spent:";
	private final double RING_STRING_LEFT_PERCENTAGE = 0.882;
	private final double RING_STRING_BOTTOM_PERCENTAGE = 0.21;
	private final double LEGEND_LEFT_PERCENTAGE = 0.884;
	private final double LEGEND_TOP_PERCENTAGE = 0.263;
	private final int BETWEEN_SQUARE_SPACING = 6;
	private final int LEGEND_SQUARE_SIZE = 8;
	private final int LEGEND_STRING_SPACING = 3;
	private final int DURATION_STRING_WIDTH = 10;
	private final int DURATION_STRING_HEIGHT = 6;
	private final int DURATION_RING_PADDING = 5;
	private final double OVERLAP_THRESHOLD = 0.5;

	// A map to represent the number of hours spent on each activity
	private Map<String, Double> activityMap = new HashMap<String, Double>();
	private String[] durationStrings;
	private String[] changeStrings;

	public ActivityProfileSection(PDFGraphics g, Rectangle r,
			double[] durations, double[] oldDurations) throws Exception {
		if (g == null || r == null || durations == null
				|| durations.length != activityStrings.length
				|| oldDurations == null
				|| oldDurations.length != activityStrings.length)
			throw new Exception(ErrorCodes.Errors[16]);
		pdfGraphics = g;
		boundingRectangle = r;
		durationStrings = new String[activityStrings.length];
		changeStrings = new String[activityStrings.length];

		// Initialize the activity map
		for (int i = 0; i < activityStrings.length; i++) {
			activityMap.put(activityStrings[i], durations[i]);
			durationStrings[i] = String.format("%1.1f", durations[i]);
			double change = durations[i] - oldDurations[i];
			if (change < 0) {
				changeStrings[i] = "- ";
			} else {
				changeStrings[i] = "";
			}
			changeStrings[i] += String.format("%.1f", Math.abs(change));
		}
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
		int durationHeadingBoundary = (int) ((double) boundingRectangle.width * DURATION_HEADING_PERCENTAGE);
		int changeHeadingBoundary = (int) ((double) boundingRectangle.width * CHANGE_HEADING_PERCENTAGE);
		int topBoundary = (int) ((double) boundingRectangle.height * TOP_BOUNDARY_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.columnHeadingFont);
		pdfGraphics.drawString("Duration (hours)", boundingRectangle.x
				+ durationHeadingBoundary, boundingRectangle.y + topBoundary
				- COLUMN_HEADING_PADDING);
		pdfGraphics.drawString("Change (hours)", boundingRectangle.x
				+ changeHeadingBoundary, boundingRectangle.y + topBoundary
				- COLUMN_HEADING_PADDING);

		// Now we need to draw all of the strings. First we want to draw the
		// names of the activities
		int durationBoundary = (int) ((double) tableRectangle.width * DURATION_PERCENTAGE);
		Rectangle activityRectangle = new Rectangle(tableRectangle.x
				+ ACTIVITY_NAME_PADDING, tableRectangle.y, durationBoundary,
				tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, activityRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, activityStrings,
				CommonGraphics.innerFont, false);

		// Next draw all of the durations
		int changeBoundary = (int) ((double) tableRectangle.width * CHANGE_PERCENTAGE);
		Rectangle durationRectangle = new Rectangle(tableRectangle.x
				+ durationBoundary, tableRectangle.y, changeBoundary
				- durationBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, durationRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, durationStrings,
				CommonGraphics.innerFont, true);

		// Finally draw the change
		Rectangle changeRectangle = new Rectangle(tableRectangle.x
				+ changeBoundary, tableRectangle.y, tableRectangle.width
				- changeBoundary, tableRectangle.height);
		CommonGraphics.DrawTableStrings(pdfGraphics, changeRectangle,
				TOP_BOUNDARY_PERCENTAGE, BOTTOM_BOUNDARY_PERCENTAGE,
				NUM_RECTANGLES, SPACING_PIXELS, changeStrings,
				CommonGraphics.innerFont, true);

		// Now we need to draw the ring chart based on the reported hours
		DrawRingChart();
	}

	private void DrawRingChart() {
		int ringLeftBoundary = (int) ((double) boundingRectangle.width * RING_LEFT_PERCENTAGE);
		int ringRightBoundary = (int) ((double) boundingRectangle.width * RING_RIGHT_PERCENTAGE);
		int ringTopBoundary = (int) ((double) boundingRectangle.height * RING_VERT_PERCENTAGE);
		int ringDiameter = ringRightBoundary - ringLeftBoundary;
		int innerCircleLeftBoundary = (int) ((double) ringDiameter
				* ((1.0 - INNER_CIRCLE_PERCENTAGE) / 2.0) + (double) ringLeftBoundary);
		int innerCircleTopBoundary = (int) ((double) ringDiameter
				* ((1.0 - INNER_CIRCLE_PERCENTAGE) / 2.0) + (double) ringTopBoundary);
		int innerCircleDiameter = (int) ((double) ringDiameter * INNER_CIRCLE_PERCENTAGE);

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

		// At the same time we want to draw the legend
		int ringStringLeftBoundary = (int) ((double) boundingRectangle.width * RING_STRING_LEFT_PERCENTAGE);
		int ringStringBottomBoundary = (int) ((double) boundingRectangle.height * RING_STRING_BOTTOM_PERCENTAGE);
		int legendLeftBoundary = (int) ((double) boundingRectangle.width * LEGEND_LEFT_PERCENTAGE);
		int legendTopBoundary = (int) ((double) boundingRectangle.height * LEGEND_TOP_PERCENTAGE);
		pdfGraphics.setColor(CommonGraphics.DARK_GREEN);
		pdfGraphics.setFont(CommonGraphics.boldInnerFont);
		pdfGraphics.drawString(ringString, boundingRectangle.x
				+ ringStringLeftBoundary, boundingRectangle.y
				+ ringStringBottomBoundary);

		// We need to draw the proper ring section for each activity
		int startAngle = 0;
		for (int i = sortedActivities.length - 1; i >= 0; i--) {
			int range = 0;
			if (i == 0) {
				// We want to make sure that the ring is connected in the end
				range = 360 - startAngle;
			} else {
				range = (int) ((sortedDurations[i] / totalDuration) * 360.0);
			}
			pdfGraphics.setColor(ringColors[i]);
			pdfGraphics.fillArc(boundingRectangle.x + ringLeftBoundary,
					boundingRectangle.y + ringTopBoundary, ringDiameter,
					ringDiameter, startAngle, range);

			// Draw the accompanying legend for the activity
			if (range == 0) {
				pdfGraphics.setColor(CommonGraphics.VERY_LIGHT_GRAY);
				pdfGraphics
						.drawRect(
								boundingRectangle.x + legendLeftBoundary,
								boundingRectangle.y
										+ legendTopBoundary
										+ i
										* (LEGEND_SQUARE_SIZE + BETWEEN_SQUARE_SPACING),
								LEGEND_SQUARE_SIZE, LEGEND_SQUARE_SIZE);
			} else {
				pdfGraphics
						.fillRect(
								boundingRectangle.x + legendLeftBoundary,
								boundingRectangle.y
										+ legendTopBoundary
										+ i
										* (LEGEND_SQUARE_SIZE + BETWEEN_SQUARE_SPACING),
								LEGEND_SQUARE_SIZE, LEGEND_SQUARE_SIZE);
			}
			pdfGraphics.setFont(CommonGraphics.smallInnerFont);
			pdfGraphics.setColor(Color.BLACK);
			pdfGraphics.drawString(sortedActivities[i], boundingRectangle.x
					+ legendLeftBoundary + LEGEND_SQUARE_SIZE
					+ LEGEND_STRING_SPACING, boundingRectangle.y
					+ legendTopBoundary + i
					* (LEGEND_SQUARE_SIZE + BETWEEN_SQUARE_SPACING)
					+ LEGEND_SQUARE_SIZE - 1);

			startAngle += range + 1;
		}

		pdfGraphics.setColor(Color.WHITE);
		pdfGraphics.fillOval(boundingRectangle.x + innerCircleLeftBoundary + 1,
				boundingRectangle.y + innerCircleTopBoundary + 1,
				innerCircleDiameter, innerCircleDiameter);

		startAngle = 0;
		int smallDurations = 0;
		for (int i = sortedActivities.length - 1; i >= 0; i--) {
			int range = 0;
			if (i == 0) {
				// We want to make sure that the ring is connected in the end
				range = 360 - startAngle;
			} else {
				range = (int) ((sortedDurations[i] / totalDuration) * 360.0);
			}

			// Finally, we need to draw the duration on the ring
			// There is a small special case here that we need to worry about.
			// Durations that are less than 0.5
			// can overlap so we need to put them inside of the ring in
			// alternating fashion
			if (range != 0) {
				double midAngle = (double) (startAngle + range / 2) * 2.0
						* Math.PI / 360.0;
				double radius = 0.0;
				if (sortedDurations[i] >= OVERLAP_THRESHOLD
						|| (smallDurations % 2) == 0) {
					radius = ((double) ringDiameter / 2)
							+ (double) (DURATION_STRING_WIDTH / 2)
							+ (double) DURATION_RING_PADDING;
					if (sortedDurations[i] < OVERLAP_THRESHOLD)
						smallDurations++;
				} else {
					radius = ((double) innerCircleDiameter / 2)
							- ((double) (DURATION_STRING_WIDTH / 2) + (double) DURATION_RING_PADDING);
					smallDurations++;
				}
				int durationXOffset = (int) (radius * Math.cos(midAngle));
				int durationYOffset = (int) (radius * Math.sin(midAngle));
				pdfGraphics.setFont(CommonGraphics.boldSmallInnerFont);
				pdfGraphics.setColor(Color.BLACK);
				pdfGraphics.drawString(
						String.format("%1.1f", sortedDurations[i]),
						boundingRectangle.x + ringLeftBoundary + ringDiameter
								/ 2 + durationXOffset - DURATION_STRING_WIDTH
								/ 2, boundingRectangle.y + ringTopBoundary
								+ ringDiameter / 2 - durationYOffset
								+ DURATION_STRING_HEIGHT / 2);
			}
			startAngle += range + 1;
		}
	}
}
