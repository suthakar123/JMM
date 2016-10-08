package com.jmm.util;

/*
 * Class to keep track of all the possible error codes
 */
public class ErrorCodes {

	public static final String[] Errors = {
			"",//100 - 199 Interpolate
			"",//200 - 299 Calibrate
			"", // 300 - 399 Classify
			"", // 400 - 499 Excursions
			"No errors", // 
			"Could not connect to the database to get tree info", // 1
			"Could not get tree info from database", // 2
			"Specified tree does not exist", // 3
			"Specified tree file could not be imported", // 4
			"Loaded tree file has improper format", // 5
			"Subject data has improper format", // 6
			"Improper sampling rate loaded", // 7
			"Improper window width loaded", // 8
			"Error during feature calculation", // 9
			"Ran out of memory for a feature matrix", // 10
			"Ran out of memory for the derivative data matrix", // 11
			"Ran out of memory for the full feature matrix", // 12
			"Error within the predict function", // 13
			"Improper ClassificationVector format", // 14
			"Unknown activity classified", // 15
			"Error while creating the PDF report", // 16
			"Error closing the output file", // 17
			"Passed in filename is not a string", // 18
			"Could not open passed in knee brace file", // 19
			"Could not read in patient ID from sensor file", // 20
			"Could not read in data matrix from sensor file", // 21
			"Could not allocate enough memory to store patient data", // 22
			"Could not open the patients database", // 23
			"Could not parse in patients database", // 24
			"Specified patient does not exist", // 25
			"Could not load image file from knee brace file", // 26
			"Could not parse in the loaded image file", // 27
			"Could not load answers file from knee brace file", // 28
			"Could not parse in the loaded answers file", // 29
			"Could not load data file from knee brace file", // 30
			"Could not parse in the loaded data file", // 31
			"Extraneous files in knee brace zip file", // 32
			"Error in parsing knee brace file", // 33
			"Error running the MATLAB code", // 34
			"Error creating the PDF report file", // 35
			"Could not load the JDBC driver", // 36
			"Could not connect to the database", // 37
			"Could not get sensor ID from the sensor address", // 38
			"Could not create a Statement for the connection", // 39
			"Multiple sensor IDs associated to given sensor address", // 40
			"Could not get patient ID from the sensor ID", // 41
			"Multiple patient IDs associated to given sensor ID", // 42
			"Could not get patient info from the database",// 43
			"Mupltiple patients correspond to patient ID in database", // 44
			"Could not get tree ID for the number of weeks since surgery",// 45
			"Mupltiple tree IDs for the number of weeks since surgery", // 46
			"Could not get previous knee/activity data for patient", // 47
			"Could not get min/max knee activity values for the number of weeks since surgery", // 48
			"Multiple min/max knee activity values for the number of weeks since surgery", // 49
			"Could not get previous reports for patient", // 50
			"Could not insert report data into database", // 51
			"Error Getting Calibration Values", //52
			"Error loading Tree Data ", //53
			"Error Reading Log File", //54
			"Error Reading Properties File", //55
			"Could not insert Audit data into database", // 56
			"Could not get values from the AlertNorms table", //57
			"Error Setting SensorData",// 58
			"",// 59
			"",// 60
			"",// 61
			"",// 62
			"",// 63
			"",// 64
			"",// 65
			"",// 66
			"",// 67
			"",// 68
			"",// 69
			"",// 70
			"",// 71
			"",// 72
			"",// 73
			"",// 74
			"",// 75
			"",// 76
			"",// 77
			"",// 78
			"",// 79
			"",// 80
			"",// 81
			"Error Found in CalibrateData:Size Mismatch",// 82
			"",// 83
			"Incoming Zip File has incorrect values",// 84
			"",// 85
			"",// 86
			"",// 87
			"",// 88
			"",// 89
			"",// 90
			"",// 91
			"",// 92
			"",// 93
			"",// 94
			"",// 95
			"",// 96
			"",// 97
			"",// 98
			"",// 99
			"",// 100
			"Data less than minimum number of points in excursions",// 101
			"No peaks found in Excursions",// 102
			"",// 103
			"",// 104
			"",// 105
			"",// 106
			"",// 107
			"",// 108
	};
}
