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
    private static Logger dlog = new Logger(PlcConnection.class.getSimpleName());
    
    private FinsConnection connection;
    private int previousModeWord = Mode.OFF.getValue();
    
    public PlcConnection(String address, int port) {
        connection = FinsConnection.newInstance(address, port);
        connection.setTesting(true);
    }

    public boolean connect() {
        boolean res = false;
        
        if (connection != null && !connection.isConnected()) {
            dlog.d("connect");
            res = connection.connect();
        }
        
        return res;
    }
    
    public boolean disconnect() {
        boolean res = false;
        
        if (connection.isConnected()) {
            dlog.d("disconnect");
            
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
            boolean r1 = setMode(Mode.OFF.getValue());
            boolean r2 = setMode(Mode.RESET.getValue());
            
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
            boolean r8 = setMode(Mode.LENGTH_AND_DIAMETER_MEASUREMENT.getValue());
            
            res = r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8;
        }
        
        return res;
    }
    
    public boolean measurementStop() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.TRACK_EMPTYING.getValue());
        }
        
        return res;
    }
    
    public boolean isTrackStopped(boolean shortTime) {
        if (connection.isConnected()) {
            for (int i = 0; i < (shortTime ? PlcConstants.SHORT_ITERATIONS : PlcConstants.NORMAL_ITERATIONS); i++) {
                try {
                    Thread.sleep(shortTime ? PlcConstants.SHORT_SLEEP_TIME : PlcConstants.NORMAL_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((getStatus() & Status.TRACK_STOPPED.getValue()) > 0) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean cleaningStart() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setCylinderDistance(PlcConstants.SERVO_MAX_POSITION);
            boolean r2 = setFrequencies(Utility.calcMotorFrequency(PlcConstants.SLOW_TRACK_SPEED), Utility.calcMotorFrequency(PlcConstants.SLOW_TRACK_SPEED));
            boolean r3 = setMode(Mode.CLEANING.getValue());
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean cleaningStop() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setDelay(Register.SINGLE_LENGTH_TURN_TIME, Utility.calcTime(PlcConstants.TRACK_LENGTH / 2, PlcConstants.SLOW_TRACK_SPEED));
            boolean r2 = setMode(Mode.TRACK_EMPTYING.getValue());
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    public boolean checkCleanliness() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setFrequencies(Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED), Utility.calcMotorFrequency(PlcConstants.TRACK_SPEED));
            
            //sleep 2s until the track is started
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            boolean r2 = setMode(Mode.CLARITY_CHECKING.getValue());
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    public boolean expositionLength() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.LENGTH_EXPOSITION.getValue());
        }
        
        return res;
    }
    
    public boolean expositionDiameter() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.DIAMETER_EXPOSITION.getValue());
        }
        
        return res;
    }
    
    public boolean calibrationStart() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setCylinderDistance(PlcConstants.CALIBRATION_SAMPLE_INSERTION);
            
            //wait 0.5 sec
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            boolean r2 = setCylinderDistanceWithoutModeChanging(PlcConstants.CALIBRATION_SAMPLE_DIAMETER);
            
            boolean r3 = setMode(Mode.CALIBRATION.getValue() | Mode.CALIBRATION_ARM_UNLOCK.getValue());            
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean calibrationStartWait() {
        if (connection.isConnected()) {
            for (int i = 0; i < PlcConstants.LONG_ITERATIONS; i++) {
                try {
                    Thread.sleep(PlcConstants.LONG_ITERATIONS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                boolean topCalibArmDown = (getStatus() & Status.CALIBRATION_ARM_L_DOWN.getValue()) > 0;
                boolean frontCalibArmDown = (getStatus() & Status.CALIBRATION_ARM_D_DOWN.getValue()) > 0;
                
                if (topCalibArmDown && frontCalibArmDown) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean setModeToOff() {
        if (connection.isConnected()) {
            return setMode(Mode.OFF.getValue());
        }
        
        return false;
    }
    
    public boolean calibrateTopCamera() {
        boolean res = false;
        
        if (connection.isConnected()) {
            if ((getStatus() & Status.CALIBRATION_ARM_L_DOWN.getValue()) == 0) {
                res = setMode(Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS.getValue());
            }
        }
        
        return res;
    }
    
    public boolean expositionCalibrationAnytime() {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = setMode(Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS.getValue());
        }
        
        return res;
    }
    
    public boolean calibrateFrontCamera() {
        boolean res = false;
        
        if (connection.isConnected()) {
            if ((getStatus() & Status.CALIBRATION_ARM_D_DOWN.getValue()) > 0) {
                boolean r1 = setMode(Mode.CALIBRATE_DIAMETER.getValue());
                boolean r2 = false;
                
                //sleep 0.5 sec
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
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
            
            boolean r2 = setMode(Mode.FINISH_CALIBRATION.getValue() | Mode.CALIBRATION.getValue());
            
            boolean r3 = setMode(Mode.RESET.getValue()); 
            
            res = r1 && r2 && r3;
        }
        
        return res;
    }
    
    public boolean calibrationStopWait() {
        if (connection.isConnected()) {
            for (int i = 0; i < PlcConstants.LONG_ITERATIONS; i++) {
                try {
                    Thread.sleep(PlcConstants.LONG_ITERATIONS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                boolean topCalibArmUp = (getStatus() & Status.CALIBRATION_ARM_L_UP.getValue()) > 0;
                boolean frontCalibArmUp = (getStatus() & Status.CALIBRATION_ARM_D_UP.getValue()) > 0;
                
                if (topCalibArmUp && frontCalibArmUp) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean shutdown() {
        boolean res = false;
        
        if (connection.isConnected()) {
            boolean r1 = setMode(Mode.SHUTDOWN.getValue());
            
            boolean r2 = disconnect();
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    public boolean yellowWarning(boolean enable) {
        boolean res = false;
        
        if (connection.isConnected()) {
            if (enable) {
                res = setMode(Mode.YELLOW_LIGHT_ENABLE.getValue());
            } else {
                res = setMode(Mode.YELLOW_LIGHT_DISABLE.getValue());
            }
        }
        
        return res;
    }
    
    public int getStatus() {
        int res = -1;
        
        if (connection.isConnected()) {
            res = connection.readRegister(MEMORY_AREA_B2, Register.STATUS_BITS.getValue());
            
            //TODO store this value somewhere and return when the method is called. retrieve the value indepently of the callings
        }
        
        return res;
    }
    
    public boolean isConnected() {
        return connection.isConnected();
    }
    
    public boolean reconnect() {
        boolean res = false;
        
        dlog.d("reconnect");
        
        disconnect();
        
        res = connect();
        
        return res;
    }
    
    private boolean setDelay(Register delayRegister, int delay) {
        boolean res = false;
        
        if (connection.isConnected()) {
            dlog.d("setDelay " + delayRegister.name() + "(" + delayRegister.getValue() + "): " + delay);
            
            res = connection.writeBcdRegister(MEMORY_AREA_B2, delayRegister.getValue(), new int[] {delay});
        }
        
        return res;
    }
    
    private boolean setFrequencies(int trackFrequency, int cylinderFrequency) {
        boolean res = false;
        
        if (connection.isConnected()) {
            dlog.d("setFrequencies: " + trackFrequency + ", " + cylinderFrequency);
            
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
            dlog.d("setCylinderDistance: " + distance);
            
            boolean r1 = connection.writeRegister(MEMORY_AREA_B2, Register.CYLINDER_DISTANCE.getValue(), new int[] {distance});
            boolean r2 = setMode(Mode.DIAMETER_SETTING.getValue());
            
            res = r1 && r2;
        }
        
        return res;
    }
    
    private boolean setCylinderDistanceWithoutModeChanging(int distance) {
        boolean res = false;
        
        if (connection.isConnected()) {
            dlog.d("setCylinderDistanceWithoutModeChanging: " + distance);
            
            res = connection.writeRegister(MEMORY_AREA_B2, Register.CYLINDER_DISTANCE.getValue(), new int[] {distance});
        }
        
        return res;
    }
    
    private boolean setServoNullPosition(int position) {
        boolean res = false;
        
        if (connection.isConnected()) { 
            dlog.d("setServoNullPosition: " + position);
            
            res = connection.writeRegister(MEMORY_AREA_B2, Register.SERVO_NULL_POSITION.getValue(), new int[] {position});
        }
        
        return res;
    }
    
    private boolean setLights(Lights l) {
        boolean res = false;
        
        if (connection.isConnected()) {
            dlog.d("setLights: " + l.getValue());
            
            res = connection.writeRegister(MEMORY_AREA_B2, Register.LIGHTS.getValue(), new int[] {l.getValue()});
        }
        
        return res;
    }
    
    private boolean setMode(int modeWord) {
        boolean res = false;
        
        if (connection.isConnected()) {             
            if (modeWord == Mode.YELLOW_LIGHT_DISABLE.getValue()) {
                modeWord = previousModeWord;
            } else if (modeWord == Mode.EXPOSITION_WITH_CALIBRATION_LIGHTS.getValue() || modeWord == Mode.DIAMETER_EXPOSITION.getValue() || modeWord == Mode.CALIBRATE_DIAMETER.getValue() || modeWord == Mode.LENGTH_EXPOSITION.getValue() || modeWord == Mode.YELLOW_LIGHT_ENABLE.getValue()) {
                if ((previousModeWord & Mode.CALIBRATION_ARM_UNLOCK.getValue()) > 0) {
                    modeWord |= Mode.CALIBRATION.getValue();
                } else {
                    modeWord |= previousModeWord;
                }
            } else {
                previousModeWord = modeWord;
            }
            
            dlog.d("setMode: " + modeWord);
            
            res = connection.writeRegister(MEMORY_AREA_B2, Register.MODE.getValue(), new int[] {modeWord});
        }
        
        return res;
    }
}
