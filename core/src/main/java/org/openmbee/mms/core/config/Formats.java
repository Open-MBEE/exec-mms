package org.openmbee.mms.core.config;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Formats {

    public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(
        ZoneId.systemDefault());

}
