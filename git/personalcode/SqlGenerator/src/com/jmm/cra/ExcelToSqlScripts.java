package com.jmm.cra;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToSqlScripts {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ExcelToSqlScripts excelToSqlScripts = new ExcelToSqlScripts();
		excelToSqlScripts
				.read("C:\\Users\\jayaram\\Desktop\\Documents\\DBValues.xlsx");
	}

	// ,"AlertNorms"
	String tableNames[] = { "DefaultKneeValues", "AlertNorms","Calibration"/*,"Tree", "WeekTree",
			"Calibration"*/ };
	int[] rowNumbers = { 17, 17,1 /*13, 13, 2 */};
	int[] columnNumbers = { 9, 12,26/*,7,2,26 */};

	public void read(String file) throws IOException {
		XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));

		StringBuilder queryString = new StringBuilder();
		for (int tableNum = 0; tableNum < tableNames.length; tableNum++) {
		XSSFSheet myExcelSheet = myExcelBook
					.getSheet(tableNames[tableNum]);
			System.out.println("The sheet is "+myExcelSheet);
			System.out.println("Sheet selected is "+tableNames[tableNum]);
			System.out.println("rowNumbers[tableNum] "+rowNumbers[tableNum]);
			System.out.println("columnNumbers[tableNum]"+columnNumbers[tableNum]);
			//Row starts from 1 since we are not concerned with the header column
			for (int i = 1; i <= rowNumbers[tableNum]; i++) {				
				XSSFRow row = myExcelSheet.getRow(i);
				queryString.append("insert into " + tableNames[tableNum]
						+ " values(");
				//Column Starts from 0.
				for (int j = 0; j < columnNumbers[tableNum]; j++) {
			//		System.out.println("query String is:"+queryString);
					String name="";
					if(row.getCell(j).getCellType()==HSSFCell.CELL_TYPE_STRING){
					 name = "'" + row.getCell(j).getStringCellValue() + "'";
					}
					else
					{
						double v=row.getCell(j).getNumericCellValue();
						
						int vv=(int)v;
						if(vv==v)
							name = "'" +vv  + "'";
						else 
							name = "'" +v  + "'";
					}
					// System.out.println("NAME : " + name);
					queryString.append(name);
					if (j != columnNumbers[tableNum]-1) {
						queryString.append(",");
					} else {
						queryString.append(");\n");
					}
				}
			}
		}
		System.out.println(queryString);
		myExcelBook.close();
	}
}
