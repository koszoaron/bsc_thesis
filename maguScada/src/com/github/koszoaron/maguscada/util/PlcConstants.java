package com.github.koszoaron.maguscada.util;

public class PlcConstants {
    
    public static final int CONGESTION_TOLERANCE = 100;
    public static final int CONGESTION_TOLERANCE_2ND = 100;
    
    /**
     * Directions in which the track can go.
     */
    public static enum TrackDirection {
        /** Stop the track */
        STOP (0),
        /** The track goes in forward direction */
        FORWARD (1),
        /** The track goes in reverse direction */
        REVERSE (3);
        
        private int value;
        
        private TrackDirection(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * Registers of the PLC.<br>
     * Note: all length registers (marked with [mm]) are hexadecimal and all timing registers (marked with [n ms] where n is 10 or 100) are BCD registers 
     */
    public static enum Register {
        /** Working mode of the machine */
        MODE (0x01),
        /** Enabled lights */
        LIGHTS (0x02),
        /** Distance between the cylinders [mm] */
        CYLINDER_DISTANCE (0x03),
        /** Status bits of the PLC */
        STATUS_BITS (0x04),
        /** Null position of the servo [mm] */
        SERVO_NULL_POSITION (0x05),
        /** Delay for diameter lighting [10 ms] */
        DIAMETER_LED_DELAY (0x06),
        /** Delay for length lighting [10 ms] */
        LENGTH_LED_DELAY (0x07),
        /** Time distance between the first congestion detecting sensors [10 ms] */
        CONGESTION_1_WAIT_TIME (0x09),
        /** Tolerance for the first congestion detection [10 ms] */
        CONGESTION_1_TOLERANCE (0x0a),
        /** Tolerance for the sensor curtain [10 ms] */
        SENSOR_CURTAIN_TOLERANCE (0x0b),
        /** Time between the shutdown command is issued and the power is turned off [100 ms] */
        SHUT_DOWN_DELAY (0x0c),
        /** Time of a single turn of the track [100 ms] */
        SINGLE_WHOLE_TURN_TIME (0x0d),
        /** Time of a single length turn of the track [100 ms] */
        SINGLE_LENGTH_TURN_TIME (0x0e),
        /** Delay until the first blower is activated [10 ms] */
        BLOWER_ONE_DELAY (0x0f),
        /** Length of the activation of the first blower [10 ms] */
        BLOWER_ONE_TIME (0x10),
        /** Delay until the second blower is activated [10 ms] */
        BLOWER_TWO_DELAY (0x11),
        /** Length of the activation of the second blower [10 ms] */
        BLOWER_TWO_TIME (0x12),
        /** Time distance between the second congestion detecting sensors [10 ms] */
        CONGESTION_2_WAIT_TIME (0x50),
        /** Tolerance for the second congestion detection [10 ms] */
        CONGESTION_2_TOLERANCE (0x51),
        /** Frequency of the first motor [0.01 Hz] */
        FREQCHANGER1 (0x64),
        /** Frequency of the second motor [0.01 Hz] */
        FREQCHANGER2 (0x78),
        /** Time shift for the diameter exponating sensor [10 ms] */
        SENSOR_RELOCATION (0x82);
        
        private int value;
        
        private Register(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    public static enum Lights {
        /** All lights are off */
        OFF (0),
        /** The lights for lenght measurement are enabled */
        LENGTH (1),
        /** The lights for diameter measurement are enabled */
        DIAMETER (6),
        /** The lights for calibration are enabled */
        CALIBRATION (8);
        
        private int value;
        
        private Lights(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public static enum Mode {
        /** All mode bits are cleared */
        OFF (0),
        /** Measure length */
        LENGTH_MEASUREMENT (1),
        /** Measure diameter */
        DIAMETER_MEASUREMENT (2),
        /** Measure both length and diameter */
        LENGTH_AND_DIAMETER_MEASUREMENT (3),
        /** Track cleaning mode */
        CLEANING (4),
        /** Unlock the calibration arms */
        CALIBRATION_ARM_UNLOCK (8),
        /** Enter calibration mode */
        CALIBRATION (16),
        /** Reset the machine */
        RESET (32),
        /** Empty the track */
        TRACK_EMPTYING (64),
        /** Check if the track is empty */
        CLARITY_CHECKING (128),
        /** Set the cylinder distance */
        DIAMETER_SETTING (256),
        /** Exponate the top camera */
        LENGTH_EXPOSITION (512),
        /** Exponate the front camera */
        DIAMETER_EXPOSITION (1024),
        /** Exponate the top camera with the calibration lights */
        EXPOSITION_WITH_CALIBRATION_LIGHTS (2048),
        /** Enable the warning light */
        YELLOW_LIGHT_ENABLE (4096),
        /** Disable the warning light */
        YELLOW_LIGHT_DISABLE (4097),
        /** Shut down the machine */
        SHUTDOWN (8192),
        /** End calibration mode */
        FINISH_CALIBRATION (16384),
        /** Calibrate the front camera */
        CALIBRATE_DIAMETER (32768);
        
        private int value;
        
        private Mode(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public static enum Status {
        /** Emergency stop */
        EMERGENCY_STOP (1),
        /** Machine ready */
        READY (2),
        /** Congestion on the length measurement stage */
        CONGESTION_1 (4),
        /** The calibration sample for the top camera is lowered */
        CALIBRATION_ARM_L_DOWN (8),
        /** The calibration sample for the top camera is raised */
        CALIBRATION_ARM_L_UP (16),
        /** The last exposition had lighting */
        WAS_LIT (32),
        /** Problem with the servo */
        SERVO_PROBLEM (64),
        /** Low air pressure */
        NO_PRESSURE (128),
        /** The track is stopped */
        TRACK_STOPPED (256),
        /** Congestion on the diameter measurement stage */
        CONGESTION_2 (512),
        /** Congestion sensed by the sensor curtain (the bin is full) */
        CONGESTION_SENSOR_CURTAIN (1024),
        /** The calibration sample for the front camera is lowered */
        CALIBRATION_ARM_D_DOWN (2048),
        /** The calibration sample for the front camera is raised */
        CALIBRATION_ARM_D_UP (4096),
        /** The sensor exponating the front camera is triggered */
        EXPOSITION_SENSOR_TRIGGERED (8192),
        /** The service doors are open */
        SERVICE_DOOR_OPEN (16384);
        
        int value;
        
        private Status(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
}
