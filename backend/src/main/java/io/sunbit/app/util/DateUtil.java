package io.sunbit.app.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	public static LocalDateTime formattingDate(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00:000");
		String formattedStringDate = formatter.format(date);
		// //Test.
		// System.out.println(formattedStringDate);
		LocalDateTime parsedDate = LocalDateTime.parse(formattedStringDate, formatter);

		return parsedDate;
	}
}
