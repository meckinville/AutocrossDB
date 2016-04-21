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
    public static final String MOST_CONES_HIT_INFO = "The drivers who hit the most cones over the course of the year.";
    public static final String MOST_CONES_HIT_AVG_INFO = "The drivers who hit the most cones on average per event.";
    public static final String MOST_EVENTS_ATTENDED_INFO = "The drivers who attended the most events throughout the year.";
    public static final String MOST_RUNS_TAKEN_INFO = "The drivers who took the most runs throughout the year.";
    public static final String CLASS_JUMPER_INFO = "The drivers who participated in the most different classes at events throughout the year.";
    public static final String DIRTIEST_RUN_INFO = "The most cones hit on a single run throughout the year.";
    
    //Event Awards
    public static final String LARGEST_EVENT_INFO = "Most attendees to a single event.";
    public static final String SMALLEST_EVENT_INFO = "Least attendeeds to a single event.";
    public static final String MOST_RUNS_AT_EVENT_INFO = "Most runs given at a single event. This is not always entirely accurate because sometimes people"
                                                         + " end up taking an extra run or two. So if at an event there's 1 driver who took 7 runs and 50 drivers who took 5 runs,"
                                                         + " the award will count the 7 runs as the runs per event.";
    public static final String CONE_CARNAGE_INFO = "Most cones hit at an event.";
    public static final String AVG_CONES_PER_DRIVER_INFO = "Most cones hit on average per driver at a single event. This is a better indicator of the toughness of a course than \"Cone Carnage\".";
    public static final String MOST_CONFUSING_COURSE_INFO = "Most offcourses at an event.";
    public static final String NOVICE_INVASION_INFO = "Highest novice attendance at an event.";
    
    //Class Awards
    public static final String LARGEST_CLASS_ATTENDANCE_INFO = "Largest class at a single event throughout the year. This award is measuring the class participation"
                                                                + " at each event individually. This means it is possible for the same class to win all 5 awards.";
    public static final String MOST_UNIQUE_DRIVERS_CLASS_INFO = "The number of distinct drivers that participated in that class througout the year. <br />Example 1: "
                                                                + "<br />20 events in the year. John Doe is the lone participant in CS for every event. The unique drivers for CS for the year is 1.  "
                                                                + "Example 2: 20 events in the year. There is only one participant in CS at every event, but the same person never shows up twice. The unique drivers for CS for the year is 20.";
    public static final String HIGHEST_AVG_PARTICIPATION_INFO = "The most average drivers in class per event.";
    public static final String DIRTIEST_CLASS_INFO = "This measures the average cones hit by all drivers in a specific class at an event. It then takes the mean of these"
                                                    + " averages to create an overall yearly average. I'm pretty sure my math is incorrect at the moment though so it likely"
                                                    + " is not entirely accurate.";
    
    //Car Awards
    public static final String MOST_CLASS_WINS_CARMAKE_INFO = "Shows the car maker who had the most event class wins throughout the year.";
    public static final String HIGHEST_ATTENDANCE_CARMAKE_INFO = "Shows the total number of cars that showed up at events throughout the year.";
}
