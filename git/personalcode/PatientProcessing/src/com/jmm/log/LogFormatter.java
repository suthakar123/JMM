/**
 * 
 */
package com.jmm.log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author jayaram
 *
 */
public class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
        sb.append(new Date(record.getMillis()))
            .append(" ")
            .append(record.getLevel().getLocalizedName())
            .append(": ")
            .append(formatMessage(record))
            .append("\n");
		return sb.toString();
	}

}
