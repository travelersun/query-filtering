package com.tianzhu.filtering.utils;

import java.sql.Timestamp;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @Author: liaoyq
 * @Desrition:
 * @Created: 2018/10/9 15:44
 * @Modified: 2018/10/9 15:44
 * @Modified By: liaoyq
 */
public class Timestamps
{
    private static final String PADDED_FRACTIONAL_FORMAT_PATTERN = "0.000";
    private static final String RFC_3339_MUNGED_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String RFC_3339_PATTERN = "{0,number,0000}-{1,number,00}-{2,number,00}T{3,number,00}:{4,number,00}:{5,number,00}";

    public static boolean isTimestamp(CharSequence text)
    {
        return TIMESTAMP.matcher(text).matches();
    }

    public static String toPaddedString(long date)
    {
        return toString("0.000", date);
    }

    public static String toString(long date)
    {
        return toString("#.###", date);
    }

    public static String toString(Timestamp timestamp)
    {
        return toString(timestamp.getTime());
    }

    public static long valueOf(String date)
    {
        if (isTimestamp(date))
        {
            long millis = 0L;
            StringBuilder text = new StringBuilder(date);
            Date d = null;
            if (date.endsWith("Z")) {
                text.replace(text.length() - 1, text.length(), "-0000");
            } else {
                if(date.indexOf('-')>9 || date.indexOf('+')>0){
                    text.deleteCharAt(text.length() - 3);
                }else {
                    text.append("-0000");
                }
            }
            int fraction = date.indexOf('.');
            if (fraction > 0)
            {
                int end = text.length() - 5;

                millis = ((Double)(1000.0D * Double.parseDouble("0" + text.substring(fraction, end)))).longValue();
                text.delete(fraction, end);
            }
            String munged = text.toString();
            try
            {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if(date.indexOf("T") > 0 ){
                    formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                }

                d = formatter.parse(munged);
            }
            catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
            return d.getTime() + millis;
        }
        throw new IllegalArgumentException("Not a correctly formatted timestamp: " + date);
    }

    private static String toString(String fractionFormatPattern, long date)
    {
        DecimalFormat fractionFormat = new DecimalFormat(fractionFormatPattern, DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        Calendar c = Calendar.getInstance(UTC);
        c.setTimeInMillis(date);

        StringBuilder b = new StringBuilder();

        MessageFormat text = new MessageFormat("{0,number,0000}-{1,number,00}-{2,number,00}T{3,number,00}:{4,number,00}:{5,number,00}");
        b.append(text
                .format(new Object[] {Integer.valueOf(c.get(1)), Integer.valueOf(c.get(2) + 1),
                        Integer.valueOf(c.get(5)), Integer.valueOf(c.get(11)),
                        Integer.valueOf(c.get(12)), Integer.valueOf(c.get(13)) }));
        int millis = c.get(14);
        if (("0.000".equals(fractionFormatPattern)) || (millis > 0))
        {
            DecimalFormat fmt = fractionFormat;
            b.append(fmt.format(millis / 1000.0D).substring(1));
        }
        b.append('Z');
        return b.toString();
    }

    private static final Pattern TIMESTAMP = Pattern.compile("^\\d{4}-[01]\\d-[0123]\\d(T|\\s)[012]\\d:[0-5]\\d:[0-5]\\d(.\\d+)?((Z|([-+][012]\\d:[0-5]\\d))$)?");
    private static final TimeZone UTC = TimeZone.getTimeZone("Z");
}
