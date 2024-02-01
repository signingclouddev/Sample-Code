/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;


/**
 *
 * @author User
 */
public class DateCalculator
{

    public static final int PERCISE_IN_MIN = 1;
    public static final int PERCISE_IN_HOUR = 2;
    public static final int PERCISE_IN_DAY = 3;
    public static final int PERCISE_IN_WEEK = 4;
    public static final int PERCISE_IN_MONTH = 5;
    public static final int PERCISE_IN_YEAR = 6;

    public static final long SEC_IN_MIN = 60;
    public static final long SEC_IN_HOUR = 60 * 60;
    public static final long SEC_IN_DAY = SEC_IN_HOUR * 24;
    public static final long SEC_IN_WEEK = SEC_IN_DAY * 7;
    public static final long SEC_IN_MONTH = SEC_IN_DAY * 30;
    public static final long SEC_IN_YEAR = SEC_IN_DAY * 365;

    public static String convertToCreated ( long unixTime )
    {

        try
        {

            String startDateConvert = "";
            String startDateGet = "";
            SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd" );
            SimpleDateFormat formatter1 = new SimpleDateFormat ( "MMM dd,yyyy hh:mm:ss a" );
            Date startDate;
            Date startFormat;

//            long startDateConvertLong = 0L;
//            startDateGet = unixTime;
//            startDateConvertLong = Long.valueOf(startDateGet);
//             System.out.println("username="+result.get(i).getEmail()+"\tTimeZone="+result.get(i).getUserTimeZoneId().getValue());
//            java.util.TimeZone tz = java.util.TimeZone.getTimeZone(result.get(i).getUserTimeZoneId().getValue());
//            
//            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            df.setTimeZone(tz);
//            java.util.Date dt = new java.util.Date(now);
//            System.out.println("\t\t"+df.format(dt));
//            
     
            //java.util.TimeZone tz = java.util.TimeZone.getTimeZone((sessionMap.get(LoginController.AUTH_TIMEZONE)).toString());
            java.util.TimeZone tz = java.util.TimeZone.getDefault ();


            // TimeZone tz = TimeZone.getTimeZone("GMT+08:00");
            formatter1.setTimeZone ( tz );
            formatter.setTimeZone ( tz );

            startFormat = new Date ( unixTime );
            startDateConvert = formatter1.format ( startFormat );

            Date currentDateCopy = new Date ();
            int days = 0;
            //startDate = formatter.parse(formatter1.format(startFormat));
            //startDate = formatter.parse((formatter1.format(startFormat)));
            Calendar cal3 = Calendar.getInstance ();
            cal3.setTime ( startFormat );

            Date currentDate = formatter.parse ( formatter.format ( currentDateCopy ) );
            Calendar cal4 = Calendar.getInstance ();
            cal4.setTime ( currentDate );

            int[] monthDay =
            {
                31 , -1 , 31 , 30 , 31 , 30 , 31 , 31 , 30 , 31 , 30 , 31
            };
            Calendar fromDate;
            Calendar toDate;
            int increment = 0;
            int[] ageDiffArr = new int[ 3 ];

            int year = 0;
            int month = 0;
            int day = 0;
            int week = 0;
            String countCreated = " ";

            Calendar d1 = new GregorianCalendar ().getInstance ();
            d1.setTime ( startFormat );

            Calendar d2 = new GregorianCalendar ().getInstance ();
            d2.setTime ( currentDate );

            fromDate = d1;
            toDate = d2;

            if ( fromDate.get ( Calendar.DAY_OF_MONTH ) > toDate.get ( Calendar.DAY_OF_MONTH ) )
            {
                increment = monthDay[ fromDate.get ( Calendar.MONTH ) ];
            }

            GregorianCalendar cal = new GregorianCalendar ();
            boolean isLeapYear = cal.isLeapYear ( fromDate.get ( Calendar.YEAR ) );

            if ( increment == -1 )
            {
                if ( isLeapYear )
                {
                    increment = 29;
                }
                else
                {
                    increment = 28;
                }
            }

// DAY CALCULATION
            if ( increment != 0 )
            {
                day = ( toDate.get ( Calendar.DAY_OF_MONTH ) + increment ) - fromDate.get ( Calendar.DAY_OF_MONTH );
                increment = 1;
            }
            else
            {
                day = toDate.get ( Calendar.DAY_OF_MONTH ) - fromDate.get ( Calendar.DAY_OF_MONTH );
            }

// MONTH CALCULATION
            if ( ( fromDate.get ( Calendar.MONTH ) + increment ) > toDate.get ( Calendar.MONTH ) )
            {
                month = ( toDate.get ( Calendar.MONTH ) + 12 ) - ( fromDate.get ( Calendar.MONTH ) + increment );
                increment = 1;
            }
            else
            {
                month = ( toDate.get ( Calendar.MONTH ) ) - ( fromDate.get ( Calendar.MONTH ) + increment );
                increment = 0;
            }

// YEAR CALCULATION
            year = toDate.get ( Calendar.YEAR ) - ( fromDate.get ( Calendar.YEAR ) + increment );

            if ( year > 0 )
            {
                if ( year == 1 )
                {
                    countCreated += year + " year ago";
                }
                else
                {
                    countCreated += year + " years ago";
                }
            }
            else if ( year == 0 && month > 0 )
            {
                if ( month == 1 )
                {
                    countCreated += month + " month ago";
                }
                else
                {
                    countCreated += month + " months ago";
                }
            }
            else if ( year == 0 && month == 0 && day > 0 )
            {
                if ( day < 7 )
                {
                    if ( day == 1 )
                    {
                        countCreated += day + " day ago";
                    }
                    else
                    {
                        countCreated += day + " days ago";
                    }
                }
                else
                {
                    week = day / 7;
                    if ( week == 1 )
                    {
                        countCreated += week + " week ago";
                    }
                    else
                    {
                        countCreated += week + " weeks ago";
                    }
                }

            }
            else
            {
                countCreated += " Today";
            }
            return countCreated;

        }
        catch ( ParseException ex )
        {
            ex.printStackTrace ();
        }
        return null;
    }

    /**
     * Compare the given date and compare it with today. return into human
     * friendly msg eg: within a minute, 2 weeks ago, 6 months ago, 10 years ago
     * Please note that this return msg is <b>not exactly accurate</b>
     * 5 min ago can mean 1-5 ago, 30 min ago can mean 16 - 30 min
     *
     * @param unixTime different between the "date" with today
     * @param prefix   action. eg: Created, Last loggin, modified,
     * @param percise  1=min, 2=hour, 3=day, 4=week, 5=month, 6=year
     *
     * @return the human friendly msg
     */
    public static String dateDiffInText ( long unixTime , String prefix , int percise )
    {

        String returnDay = "Unknown";
        long now = System.currentTimeMillis ();
        long diff = now - unixTime;
        if ( diff >= 0 )
        {
            returnDay = "";
            String unit = "";
            String units = "";

            //convert diff to seconds
            diff = diff / 1000;
            if ( percise == PERCISE_IN_MIN || ( percise < PERCISE_IN_MIN && returnDay.isEmpty () ) )
            {
                unit = "minute";
                units = "minutes";
                if ( diff < SEC_IN_MIN )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_MIN * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 5 ) )
                {
                    returnDay = returnDay.concat ( "4 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 6 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 7 ) )
                {
                    returnDay = returnDay.concat ( "6 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 8 ) )
                {
                    returnDay = returnDay.concat ( "7 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 9 ) )
                {
                    returnDay = returnDay.concat ( "8 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 10 ) )
                {
                    returnDay = returnDay.concat ( "9 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 11 ) )
                {
                    returnDay = returnDay.concat ( "10 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 16 ) )
                {
                    returnDay = returnDay.concat ( "15 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MIN * 31 ) )
                {
                    returnDay = returnDay.concat ( "30 " + units + " ago" );
                }
            }
            if ( percise == PERCISE_IN_HOUR || ( percise < PERCISE_IN_HOUR && returnDay.isEmpty () ) )
            {
                unit = "hour";
                units = "hours";
                if ( diff < ( SEC_IN_HOUR ) )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_HOUR * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 5 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 6 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 7 ) )
                {
                    returnDay = returnDay.concat ( "6 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_HOUR * 13 ) )
                {
                    returnDay = returnDay.concat ( "12 " + units + " ago" );
                }
            }

            if ( percise == PERCISE_IN_DAY || ( percise < PERCISE_IN_DAY && returnDay.isEmpty () ) )
            {
                unit = "day";
                units = "days";
                if ( diff < ( SEC_IN_DAY ) )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_DAY * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_DAY * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_DAY * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_DAY * 5 ) )
                {
                    returnDay = returnDay.concat ( "4 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_DAY * 6 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_DAY * 7 ) )
                {
                    returnDay = returnDay.concat ( "6 " + units + " ago" );
                }
            }

            if ( percise == PERCISE_IN_WEEK || ( percise < PERCISE_IN_WEEK && returnDay.isEmpty () ) )
            {
                unit = "week";
                units = "weeks";
                if ( diff < ( SEC_IN_WEEK ) )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_WEEK * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_WEEK * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_WEEK * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
            }

            if ( percise == PERCISE_IN_MONTH || ( percise < PERCISE_IN_MONTH && returnDay.isEmpty () ) )
            {
                unit = "month";
                units = "months";
                if ( diff < ( SEC_IN_MONTH ) )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_MONTH * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_MONTH * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MONTH * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MONTH * 5 ) )
                {
                    returnDay = returnDay.concat ( "4 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MONTH * 6 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_MONTH * 7 ) )
                {
                    returnDay = returnDay.concat ( "6 " + units + " ago" );
                }
            }

            if ( percise == PERCISE_IN_YEAR || ( percise < PERCISE_IN_YEAR && returnDay.isEmpty () ) )
            {
                unit = "year";
                units = "years";
                if ( diff < ( SEC_IN_YEAR ) )
                {
                    returnDay = returnDay.concat ( "in 1 " + unit );
                }
                else if ( diff < ( SEC_IN_YEAR * 2 ) )
                {
                    returnDay = returnDay.concat ( "1 " + unit + " ago" );
                }
                else if ( diff < ( SEC_IN_YEAR * 3 ) )
                {
                    returnDay = returnDay.concat ( "2 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_YEAR * 4 ) )
                {
                    returnDay = returnDay.concat ( "3 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_YEAR * 5 ) )
                {
                    returnDay = returnDay.concat ( "4 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_YEAR * 6 ) )
                {
                    returnDay = returnDay.concat ( "5 " + units + " ago" );
                }
                else if ( diff < ( SEC_IN_YEAR * 7 ) )
                {
                    returnDay = returnDay.concat ( "6 " + units + " ago" );
                }
            }

            if ( returnDay.isEmpty () )
            {
                returnDay = "more than 6 years ago";
            }
            if (  ! prefix.isEmpty () )
            {
                returnDay = prefix + " " + returnDay;
            }
        }
        else
        {
            if (  ! prefix.isEmpty () )
            {
                returnDay = prefix + " in future";
            }
            else
            {
                returnDay = "In future";
            }
        }

        return returnDay;
    }

    public String convertToDate ( long unixTime )
    {
        return this.convertToDate ( unixTime , "MMM dd,yyyy hh:mm:ss a" );
    }

    public String convertToDate ( long unixTime , String format )
    {
        String startDateConvert = "";
        SimpleDateFormat formatter = new SimpleDateFormat ( format );
        Date startFormat;

        java.util.TimeZone tz = java.util.TimeZone.getDefault ();

        formatter.setTimeZone ( tz );

        startFormat = new Date ( unixTime );
        startDateConvert = formatter.format ( startFormat );

        return startDateConvert;
    }

    public String buildSecondString ( long seconds )
    {
        String secondString = "";

        if ( seconds > 0 )
        {
            if ( seconds == 1 )
            {
                secondString = seconds + " second";
            }
            else
            {
                secondString = seconds + " seconds";
            }
        }

        return secondString;
    }

    public String buildMinuteString ( long seconds )
    {
        String minuteString = "";

        if ( seconds > 0 )
        {
            long minute = seconds / 60;

            if ( minute > 0 )
            {
                if ( minute == 1 )
                {
                    minuteString = minute + " minute";
                }
                else
                {
                    minuteString = minute + " minutes";
                }
            }

            seconds = seconds % 60;

            if ( seconds > 0 )
            {
                minuteString += " " + buildSecondString ( seconds );
            }
        }

        return minuteString;
    }

    public String buildHourString ( long seconds )
    {
        String hourString = "";

        if ( seconds > 0 )
        {
            long hour = seconds / 3600;

            if ( hour > 0 )
            {
                if ( hour == 1 )
                {
                    hourString += hour + " hour";
                }
                else
                {
                    hourString += hour + " hours";
                }
            }

            seconds = seconds % 3600;

            if ( seconds > 0 )
            {
                hourString += " " + buildMinuteString ( seconds );
            }
        }

        return hourString;
    }

    //this is for contextual time
    public String buildHour ( long dateInput )
    {

        String hours = "";
        SimpleDateFormat formatter3 = new SimpleDateFormat ( "HH" );

        java.util.TimeZone tz = java.util.TimeZone.getDefault ();

        try
        {
            formatter3.setTimeZone ( tz );

            Date startFormat = new Date ( dateInput );

            hours = formatter3.format ( startFormat );

        }
        catch ( Exception ex )
        {
        }

        return hours;
    }

    public String buildMinute ( long dateInput )
    {
        String minute = "";

        SimpleDateFormat formatter4 = new SimpleDateFormat ( "mm" );

        java.util.TimeZone tz = java.util.TimeZone.getDefault ();
  
        try
        {

            formatter4.setTimeZone ( tz );

            Date startFormat = new Date ( dateInput );

            minute = formatter4.format ( startFormat );

        }
        catch ( Exception ex )
        {
        }

        return minute;
    }

    public String convertToDateOnly ( long unixTime )
    {

        String dateConvert = "";

        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd" );
        SimpleDateFormat formatter1 = new SimpleDateFormat ( "MM/dd/yyyy" );
        SimpleDateFormat formatter2 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );

        java.util.TimeZone tz = java.util.TimeZone.getDefault ();

        formatter1.setTimeZone ( tz );
        formatter.setTimeZone ( tz );
        formatter2.setTimeZone ( tz );

        try
        {

            Date startFormat = new Date ( unixTime );
            Date startDate1 = formatter.parse ( formatter2.format ( startFormat ) );
            dateConvert = formatter1.format ( startDate1 );

        }
        catch ( ParseException ex )
        {
        }
        catch ( Exception ex )
        {
        }
        return dateConvert;
    }

    public static void main ( String[] args )
    {

        Date[] date = new Date[ 11 ];
        date[ 0 ] = new Date ( ( 2000 - 1900 ) , 0 , 1 );
        date[ 1 ] = new Date ( ( 2010 - 1900 ) , 0 , 1 );
        date[ 2 ] = new Date ( ( 2012 - 1900 ) , 0 , 1 );
        date[ 3 ] = new Date ( ( 2014 - 1900 ) , 0 , 1 );
        date[ 4 ] = new Date ( ( 2014 - 1900 ) , 1 , 10 );
        date[ 5 ] = new Date ( ( 2014 - 1900 ) , 3 , 15 );
        date[ 6 ] = new Date ( ( 2014 - 1900 ) , 6 , 30 );
        date[ 7 ] = new Date ( ( 2014 - 1900 ) , 7 , 13 , 8 , 30 );
        date[ 8 ] = new Date ( ( 2014 - 1900 ) , 7 , 13 , 11 , 12 );
        date[ 9 ] = new Date ( ( 2014 - 1900 ) , 8 , 13 , 11 , 12 );
        date[ 10 ] = new Date ( ( 2014 - 1900 ) , 11 , 8 , 12 , 02 );

        for ( int i = 0 ; i < date.length ; i ++ )
        {
            System.out.println ( date[ i ].toLocaleString () + "\t" + dateDiffInText ( date[ i ].getTime () , "last login" , PERCISE_IN_MIN ) );
            System.out.println ( date[ i ].toLocaleString () + "\t" + dateDiffInText ( date[ i ].getTime () , "created" , PERCISE_IN_HOUR ) );
            System.out.println ( date[ i ].toLocaleString () + "\t" + dateDiffInText ( date[ i ].getTime () , "created" , PERCISE_IN_DAY ) );
            System.out.println ( date[ i ].toLocaleString () + "\t" + dateDiffInText ( date[ i ].getTime () , "created" , PERCISE_IN_WEEK ) );
            System.out.println ( date[ i ].toLocaleString () + "\t" + dateDiffInText ( date[ i ].getTime () , "created" , PERCISE_IN_YEAR ) );
        }

    }
}
