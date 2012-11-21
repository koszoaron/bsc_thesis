package com.github.koszoaron.maguscada.util;

import com.github.koszoaron.maguscada.communication.PlcConstants;

/**
 * A class containing utility methods
 */
public class Utility {

    /**
     * Calculates the triggering delay for the top camera
     * 
     * @param tubeLength The length of the tube to be measured [mm]
     * @param expositionLength The distance between the triggering sensor and the camera axis [mm]
     * @param trackSpeed The speed of the track [cm/s]
     * @return The triggering delay for the camera [10 ms]
     */
    public static final int calcTriggerDelayFromLength(int tubeLength, int expositionLength, int trackSpeed) {
        // delay = (track length - tube length / 2) * trackSpeed(cm/s) * 10(mm/s) / 1000(msec) / 10(10msec)
        return ((expositionLength - tubeLength / 2) * (1000 / (10 * trackSpeed)) / 10);
    }
    
    /**
     * Calculates the motor frequency from the track speed
     * 
     * @param speed The desired track speed [cm/s]
     * @return The motor frequency needed to achieve the desired track speed
     */
    public static final int calcMotorFrequency(int trackSpeed) {
        return trackSpeed * PlcConstants.FREQUENCY_TO_CMPERSEC_RATIO;
    }

    /**
     * Calculates the triggering delay for cleanliness checking (trigger every 150 cms)
     * @param trackSpeed The speed of the track [cm/s]
     * @return The triggering delay [10 ms]
     */
    public static final int calcTriggerDelayForChecking(double trackSpeed) {
        return (int)(150 / trackSpeed * 1000);
    }
    
    /**
     * Calculates the number of triggers for cleanliness checking (trigger every 150 cms)
     * @param trackLength The length of the track
     * @return The number of times to trigger the camera
     */
    public static final int calcTriggerNumbersForCheckings(double trackLength) {
        return (int)Math.ceil(trackLength / 150);
    }
    
    /**
     * Calculates time from a given length and the track speed
     * 
     * @param length The length upon which the calculation is based [cm]
     * @param trackSpeed The speed of the track [cm/s]
     * @return The time in 10 seconds
     */
    public static final int calcTime(double length, double trackSpeed) {
        return (int)(length / trackSpeed * 10);
    }
    
    /**
     * Calculates the time for the first congestion sensing
     * 
     * @param distance The distance between the congestion sensors [mm]
     * @param trackSpeed The speed of the track [cm/s]
     * @return The time in 10 seconds
     */
    public static final int calcCongestion1Time(int distance, int trackSpeed) {
        return distance / trackSpeed * 10;
    }
    
    /**
     * Calculates the time for the second congestion sensing
     * 
     * @param distance The distance between the congestion sensors [mm]
     * @param tubeLength The length of the tube [mm]
     * @param trackSpeed The speed of the track [cm/s]
     * @return The time in 10 seconds
     */
    public static final int calcCongestion2Time(int distance, int tubeLength, int trackSpeed) {
        return (distance / 2 + tubeLength) / trackSpeed * 10;
    }
    
    /**
     * Calculates the time shift for the diameter expo
     * 
     * @param sensorDistance The distance between the sensors [mm]
     * @param trackSpeed The speed of the track [cm/s]
     * @param lightPositioningTime The time needed to position the lights
     * @return The delay for the first camera [ms]
     */
    public static final int calcSensorDelay(double sensorDistance, double trackSpeed, double lightPositioningTime) {
        return (int)(sensorDistance / trackSpeed * 1000 - lightPositioningTime);
    }
}
