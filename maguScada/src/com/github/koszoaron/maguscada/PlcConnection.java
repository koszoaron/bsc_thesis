/**
 * 
 */
package com.github.koszoaron.maguscada;

import com.github.koszoaron.jfinslib.FinsConnection;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
import com.github.koszoaron.maguscada.util.PlcConstants;
import com.github.koszoaron.maguscada.util.PlcConstants.Status;
import com.github.koszoaron.maguscada.util.Utility;
import com.github.koszoaron.maguscada.util.PlcConstants.Lights;
import com.github.koszoaron.maguscada.util.PlcConstants.Mode;
import com.github.koszoaron.maguscada.util.PlcConstants.Register;
import com.github.koszoaron.maguscada.util.PlcConstants.TrackDirection;

/**
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class PlcConnection {
    
    private static final int MEMORY_AREA_B2 = 0xb2;
    
    private FinsConnection connection;
    private int previousModeWord = Mode.OFF.getValue();
    
    public PlcConnection(String address, int port) {
        connection = FinsConnection.newInstance(address, port);
        connection.setTesting(false);
    }

    public boolean connect() {
        boolean res = false;
        
        if (connection != null && !connection.isConnected()) {
            res = connection.connect();
        }
        
        return res;
    }
    
    public boolean disconnect() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = connection.disconnect();
        }
        
        return res;
    }
    
    public boolean init() {
        boolean res = false;
        
        if (connection.isConnected()) {
            //TODO get configuration (this can be put somewhere else, too)
            
            //set delays and time variables
            boolean r1 = setDelay(Register.DIAMETER_LED_DELAY, PlcConstants.DIAMETER_LED_DELAY);
            boolean r2 = setDelay(Register.CONGESTION_1_WAIT_TIME, Utility.calcCongestion1Time(PlcConstants.CONGESTION1_SENSING_LENGTH, PlcConstants.TRACK_SPEED));
            boolean r3 = setDelay(Register.CONGESTION_1_TOLERANCE, PlcConstants.CONGESTION_TOLERANCE);
            boolean r4 = setDelay(Register.CONGESTION_2_TOLERANCE, PlcConstants.CONGESTION_TOLERANCE_2ND);
            boolean r5 = setDelay(Register.SHUT_DOWN_DELAY, PlcConstants.SHUTDOWN_DELAY_SECS);
            boolean r6 = setDelay(Register.SINGLE_WHOLE_TURN_TIME, Utility.calcTime(PlcConstants.TRACK_LENGTH, PlcConstants.TRACK_SPEED));
            boolean r7 = setDelay(Register.BLOWER_ONE_DELAY, Utility.calcTime(PlcConstants.FRONT_BLOWER_ON, PlcConstants.TRACK_SPEED));
            boolean r8 = setDelay(Register.BLOWER_ONE_TIME, Utility.calcTime(PlcConstants.FRONT_BLOWER_OFF, PlcConstants.TRACK_SPEED));
            boolean r9 = setDelay(Register.BLOWER_TWO_DELAY, Utility.calcTime(PlcConstants.REAR_BLOWER_ON, PlcConstants.TRACK_SPEED));
            boolean r10 = setDelay(Register.BLOWER_TWO_TIME, Utility.calcTime(PlcConstants.REAR_BLOWER_RUNNING_TIME, PlcConstants.TRACK_SPEED));
            //set the servo null position
            boolean r11 = setServoNullPosition(PlcConstants.SERVO_NULL_POSITION);
            
            res = r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8 && r9 && r10 && r11;
        }
        
        return res;
    }
    
    public boolean reset() {
        boolean res = false;
        
        if (connection.isConnected()) { 
            boolean r1 = setMode(Mode.OFF);
            boolean r2 = setMode(Mode.RESET);
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    public boolean measurementStart(MeasureSetting mode, int length, int diameter) {
        boolean res = false;
        
        if (connection.isConnected()) {
            Lights l = null;
            switch (mode) {
                case DIAMETER:
                    l = Lights.DIAMETER;
                    break;
                case LENGTH:
                    l = Lights.LENGTH;
                    break;
                case BOTH:
                    l = Lights.BOTH;
                    break;
            }
            
            boolean r1 = setDelay(Register.SINGLE_LENGTH_TURN_TIME, Utility.calcTime(PlcConstants.TRACK_LENGTH / 2, PlcConstants.TRACK_SPEED) + 1);
            boolean r2 = setDelay(Register.SENSOR_RELOCATION, Utility.calcSensorDelay(PlcConstants.SENSOR_DISTANCE, PlcConstants.TRACK_SPEED, PlcConstants.LIGHTING_CYCLE_TIME));
            boolean r3 = setFrequencies(Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED), Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED));
            boolean r4 = setCylinderDistance(diameter);  
            boolean r5 = setLights(l);
            boolean r6 = setDelay(Register.CONGESTION_2_WAIT_TIME, Utility.calcCongestion2Time(PlcConstants.CONGESTION2_SENSING_LENGTH, length, PlcConstants.TRACK_SPEED));
            boolean r7 = setDelay(Register.LENGTH_LED_DELAY, Utility.calcTriggerDelayFromLength(length, PlcConstants.EXPOSITION_LENGTH, PlcConstants.TRACK_SPEED));  
            boolean r8 = setMode(Mode.LENGTH_AND_DIAMETER_MEASUREMENT);
            
            res = r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8;
        }
        
        return res;
    }
    
    public boolean measurementStop() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.TRACK_EMPTYING);
            
            //TODO launch delayed (and repeating) task in handler to check if track has stopped. if true -> notify UI
        }
        
        return res;
    }
    
    public boolean cleaningStart() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setCylinderDistance(PlcConstants.SERVO_MAX_POSITION);
            boolean r2 = setFrequencies(Utility.calcMotorFrequency(PlcConstants.SLOW_TRACK_SPEED), Utility.calcMotorFrequency(PlcConstants.SLOW_TRACK_SPEED));
            boolean r3 = setMode(Mode.CLEANING);
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean cleaningStop() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setDelay(Register.SINGLE_LENGTH_TURN_TIME, Utility.calcTime(PlcConstants.TRACK_LENGTH / 2, PlcConstants.SLOW_TRACK_SPEED));
            boolean r2 = setMode(Mode.TRACK_EMPTYING);
            
            res = r1 && r2;
            
            //TODO launch delayed (and repeating) task in handler to check if track has stopped. if true -> notify UI
        }
        
        return res;
    }
    
    public boolean checkCleanliness() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setFrequencies(Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED), Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED));
            
            //TODO sleep 2s until the track is started
            
            boolean r2 = setMode(Mode.CLARITY_CHECKING);
            
            int triggeringNum = Utility.calcTriggerNumbersForCheckings(PlcConstants.TRACK_LENGTH);
            int triggeringDelay = Utility.calcTriggerDelayForChecking(PlcConstants.TRACK_SPEED);
            //TODO do this triggeringNum times and wait triggeringDelay between each run:
            setMode(Mode.LENGTH_EXPOSITION);
            //TODO convert to handler task
            
            //TODO launch delayed (and repeating) task in handler to check if track has stopped. if true -> notify UI
            
            res = r1 && r2; //TODO consider the repetitive task too
        }
        
        return res;
    }
    
    public boolean expositionLength() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.LENGTH_EXPOSITION);
        }
        
        return res;
    }
    
    public boolean expositionDiameter() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.DIAMETER_EXPOSITION);
        }
        
        return res;
    }
    
    public boolean calibrationStart() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setCylinderDistance(PlcConstants.CALIBRATION_SAMPLE_INSERTION);
            
            //TODO wait 0.5 sec
            
            boolean r2 = setCylinderDistanceWithoutModeChanging(PlcConstants.CALIBRATION_SAMPLE_DIAMETER);
            
            boolean r3 = setMode(Mode.CALIBRATION);  //TODO also Mode.CALIBRATION_ARM_UNLOCK
            
            //TODO wait until both calibration arms are down (~2 min). if not then timeouterror
            
            //TODO if there is a problem then set mode to Mode.OFF
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean expositionCalibration() {
        boolean res = false;
        
        if (connection.isConnected()) {
            if ((getStatus() & Status.CALIBRATION_ARM_L_DOWN.getValue()) == 0) {
                res = setMode(Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS);
            }
        }
        
        return res;
    }
    
    public boolean expositionCalibrationAnytime() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS);
        }
        
        return res;
    }
    
    public boolean calibrateDiameter() {
        boolean res = false;
        
        if (connection.isConnected()) {
            if ((getStatus() & Status.CALIBRATION_ARM_D_DOWN.getValue()) == 0) {
                boolean r1 = setMode(Mode.CALIBRATE_DIAMETER);
                boolean r2 = false;
                
                //TODO sleep 0.5 sec
                if ((getStatus() & Status.EXPOSITION_SENSOR_TRIGGERED.getValue()) == 0) {
                    r2 = true; //if the sensor was not triggered, then it's OK
                }
                
                res = r1 && r2;
            }
        }
        
        return res;
    }
    
    public boolean calibrationStop() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setCylinderDistanceWithoutModeChanging(PlcConstants.CALIBRATION_SAMPLE_INSERTION);
            
            boolean r2 = setMode(Mode.FINISH_CALIBRATION); //TODO and Mode.CALIBRATION to keep that bit active
            
            //TODO wait until both calibration arms are up (~2 min). if not then timeouterror
            
            boolean r3 = setMode(Mode.RESET); 
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean shutdown() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setMode(Mode.SHUTDOWN);
            
            boolean r2 = disconnect();
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    public boolean yellowWarning(boolean enable) {
        boolean res = false;
        
        if (connection.isConnected()) {
            if (enable) {
                res = setMode(Mode.YELLOW_LIGHT_ENABLE);
            } else {
                res = setMode(Mode.YELLOW_LIGHT_DISABLE);
            }
        }
        
        return res;
    }
    
    public int getStatus() {
        int res = -1;
        
        if (connection.isConnected()) {
            res = connection.readRegister(MEMORY_AREA_B2, Register.STATUS_BITS.getValue());
        }
        
        return res;
    }
    
    public boolean isConnected() {
        return connection.isConnected();
    }
    
    public boolean reconnect() {
        boolean res = false;
        
        disconnect();
        
        res = connect();
        
        return res;
    }
    
    private boolean setDelay(Register delayRegister, int delay) {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = connection.writeBcdRegister(MEMORY_AREA_B2, delayRegister.getValue(), new int[] {delay});
        }
        
        return res;
    }
    
    private boolean setFrequencies(int trackFrequency, int cylinderFrequency) {
        boolean res = false;
        
        if (connection.isConnected()) {
            TrackDirection trackDir = (trackFrequency > 0 ? TrackDirection.FORWARD : TrackDirection.STOP);
            boolean r1 = connection.writeRegister(MEMORY_AREA_B2, Register.FREQCHANGER1.getValue(), new int[] {trackDir.getValue(), trackFrequency});
                    
            TrackDirection cylinderDir = (cylinderFrequency > 0 ? TrackDirection.FORWARD : TrackDirection.STOP);
            boolean r2 = connection.writeRegister(MEMORY_AREA_B2, Register.FREQCHANGER2.getValue(), new int[] {cylinderDir.getValue(), cylinderFrequency});
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    private boolean setCylinderDistance(int distance) {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = connection.writeRegister(MEMORY_AREA_B2, Register.CYLINDER_DISTANCE.getValue(), new int[] {distance});
            boolean r2 = setMode(Mode.DIAMETER_SETTING);
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    private boolean setCylinderDistanceWithoutModeChanging(int distance) {
        boolean res = false;
        
        if (connection.isConnected()) { 
            res = connection.writeRegister(MEMORY_AREA_B2, Register.CYLINDER_DISTANCE.getValue(), new int[] {distance});
        }
        
        return res;
    }
    
    private boolean setServoNullPosition(int position) {
        boolean res = false;
        
        if (connection.isConnected()) { 
            res = connection.writeRegister(MEMORY_AREA_B2, Register.SERVO_NULL_POSITION.getValue(), new int[] {position});
        }
        
        return res;
    }
    
    private boolean setLights(Lights l) {
        boolean res = false;
        
        if (connection.isConnected()) { 
            res = connection.writeRegister(MEMORY_AREA_B2, Register.LIGHTS.getValue(), new int[] {l.getValue()});
        }
        
        return res;
    }
    
    private boolean setMode(Mode m) {
        boolean res = false;
        
        if (connection.isConnected()) { 
            int modeWord = m.getValue();
            
            if (m == Mode.YELLOW_LIGHT_DISABLE) {
                modeWord = previousModeWord;
            } else if (m == Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS || m == Mode.DIAMETER_EXPOSITION || m == Mode.CALIBRATE_DIAMETER || m == Mode.LENGTH_EXPOSITION || m == Mode.YELLOW_LIGHT_ENABLE) {
                if ((previousModeWord & Mode.CALIBRATION_ARM_UNLOCK.getValue()) > 0) {
                    modeWord |= Mode.CALIBRATION.getValue();
                } else {
                    modeWord |= previousModeWord;
                }
            } else {
                previousModeWord = modeWord;
            }
            
            res = connection.writeRegister(MEMORY_AREA_B2, Register.MODE.getValue(), new int[] {modeWord});
        }
        
        return res;
    }
}
