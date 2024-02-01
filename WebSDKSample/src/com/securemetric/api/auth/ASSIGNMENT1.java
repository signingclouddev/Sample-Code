
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class ASSIGNMENT1 {
	
	private static boolean duplicate = false;
	
	public static void main(String[] args) {
//		Date date = new Date();
//                // formatting TimeZone in z (General time zone) format like EST.
//                Calendar cal = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("zzz");
//                System.out.println("TimeZone in z format : " + sdf.format(date));
//                // formatting TimeZone in zzzz format Eastern Standard Time.
//
//                sdf = new SimpleDateFormat("zzzz");
//                System.out.println("TimeZone in zzzz format : " + sdf.format(date));
//                // formatting TimeZone in Z (RFC 822) format like -8000.
//
//                sdf = new SimpleDateFormat("Z");
//                System.out.println("TimeZone in Z format : " + sdf.format(date));
//                
//                TimeZone tz = TimeZone.getTimeZone("GMT-09:00");
//                sdf = new SimpleDateFormat("MMMM EEEE YYYY-MM-dd HH:mm:ss Z zzzz");
//                sdf.setTimeZone(tz);
//                System.out.println(tz.getID());
//                System.out.println(tz.toString());
//                 System.out.println("TimeZone in Z format : " + sdf.format(date));
//                 
//                 System.out.println(Arrays.toString(TimeZone.getAvailableIDs(TimeZone.getTimeZone("GMT+08:00").getRawOffset())));
//            
//                Locale locale = Locale.getDefault();
//                String lang = locale.getDisplayLanguage();
//                String country = locale.getDisplayCountry();
//                System.out.println(lang);
//                System.out.println(country);
//                
//                String timezoneDesc = "(GMT+08:00) Asia/Kuala_Lumpur";
//                
//                System.out.println(timezoneDesc.substring(timezoneDesc.indexOf(")")+1).trim());
            String name = "administrator@www.centagte.com";
            String[] aname = new String[]{""};
            aname = name.split("@");
            System.out.println(aname[0]);
            System.out.println(aname[1]);
            String name2=aname[1];
            String[] aname2 = new String[]{""};
            aname2 = name2.split("\\.");
            System.out.println(aname2[0]);
            System.out.println(aname2[1]);
            System.out.println(aname2.length );
            String result="CN+Users";
            for (int i=0 ;i < aname2.length ;i++) {
                System.out.println(i);
                System.out.println(aname2[i]);
                result = result + ",DC=" +aname2[i];
            }
            
            System.out.println(result);     
	}
}
