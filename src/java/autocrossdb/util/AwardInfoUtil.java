/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.util;

/**
 *
 * @author rmcconville
 */
public class AwardInfoUtil 
{
    //Individual Awards
    public static final String MOST_RAW_WINS_INFO = "The drivers who had the most top raw times at events throughout the year.";
    public static final String HIGHEST_RAW_PERCENT_INFO = "The drivers who had the highest percentage of top raw times. For example, if you attend 10 events"
                                                            + " and take top raw time at 5 of them you would have a raw win percent of 50%. In order to be eligible you must have attended at least 4 events during the year."
                                                            + " This criteria is to prevent people from showing up to 1 event, winning raw, and then having a 100% raw"
                                                            + " win percentage.";
    public static final String MOST_PAX_WINS_INFO = "The drivers who had the most top pax times at events throughout the year.";
    public static final String HIGHEST_PAX_PERCENT_INFO = "The drivers who had the highest percentage of top pax times. For example, if you attend 12 events"
                                                            + " and take top pax time at 3 of them you would have a pax win percent of 25%. In order to be eligible you must have attended at least 4 events during the year."
                                                            + " This criteria is to prevent people from showing up to 1 event, winning pax, and then having a 100% pax"
                                                            + " win percentage.";
    public static final String MOST_CONES_HIT_INFO = "most cones hit info";
    public static final String MOST_CONES_HIT_AVG_INFO = "most cones hit per event average info";
    public static final String MOST_EVENTS_ATTENDED_INFO = "most events attended info";
    public static final String MOST_RUNS_TAKEN_INFO = "most runs taken info";
    public static final String CLASS_JUMPER_INFO = "class jumper info";
    public static final String DIRTIEST_RUN_INFO = "dirtiest run info";
    
    //Event Awards
    public static final String LARGEST_EVENT_INFO = "largest event attendance info";
    public static final String SMALLEST_EVENT_INFO = "smallest event attendance info";
    public static final String MOST_RUNS_AT_EVENT_INFO = "most runs at event info";
    public static final String CONE_CARNAGE_INFO = "cone carnage info";
    public static final String AVG_CONES_PER_DRIVER_INFO = "average cones per driver info";
    public static final String MOST_CONFUSING_COURSE_INFO = "most confusing course info";
    public static final String NOVICE_INVASION_INFO = "novice invasion info";
    
    //Class Awards
    public static final String LARGEST_CLASS_ATTENDANCE_INFO = "largest class  attendance info";
    public static final String MOST_UNIQUE_DRIVERS_CLASS_INFO = "most unique drivers in class info";
    public static final String HIGHEST_AVG_PARTICIPATION_INFO = "highest average participation info";
    public static final String DIRTIEST_CLASS_INFO = "dirtiest class info";
    
    //Car Awards
    public static final String MOST_CLASS_WINS_CARMAKE_INFO = "most class wins by car  make info";
    public static final String HIGHEST_ATTENDANCE_CARMAKE_INFO = "highest participation by car maker info";
}
