/**
 * 
 */
package com.github.koszoaron.maguscada;

import com.github.koszoaron.jfinslib.FinsConnection;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
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
            boolean r1 = setDelay(Register.SINGLE_LENGTH_TURN_TIME, 123);  //ok
            boolean r2 = setDelay(Register.SENSOR_RELOCATION, 345);  //ok
            boolean r3 = setFrequencies(3300, 3340);  //ok
            boolean r4 = setCylinderDistance(diameter);  //ok  
            boolean r5 = setLights(Lights.LENGTH); //BOTH setting needed  //ok
            boolean r6 = setDelay(Register.CONGESTION_2_WAIT_TIME, 1111);  //ok
            boolean r7 = setDelay(Register.LENGTH_LED_DELAY, 765);  //ok  
            boolean r8 = setMode(Mode.LENGTH_AND_DIAMETER_MEASUREMENT); //ok  
            
            res = r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8;
        }
        
        return res;
    }
    
    public boolean measurementStop() {
        boolean res = false;
        
        return res;
    }
    
    public boolean cleaningStart() {
        boolean res = false;
        
        return res;
    }
    
    public boolean cleaningStop() {
        boolean res = false;
        
        return res;
    }
    
    public boolean checkCleanliness() {
        boolean res = false;
        
        return res;
    }
    
    public boolean expositionLength() {
        boolean res = false;
        
        return res;
    }
    
    public boolean expositionDiameter() {
        boolean res = false;
        
        return res;
    }
    
    public boolean calibrationStart() {
        boolean res = false;
        
        return res;
    }
    
    public boolean expositionCalibration() {
        boolean res = false;
        
        return res;
    }
    
    public boolean expositionCalibrationAnytime() {
        boolean res = false;
        
        return res;
    }
    
    public boolean calibrateDiameter() {
        boolean res = false;
        
        return res;
    }
    
    public boolean calibrationStop() {
        boolean res = false;
        
        return res;
    }
    
    public boolean shutdown() {
        boolean res = false;
        
        return res;
    }
    
    public boolean yellowWarning(boolean enable) {
        boolean res = false;
        
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
        boolean res = false;
        
        return res;
    }
    
    public boolean reconnect() {
        boolean res = false;
        
        return res;
    }
    
    private boolean setDelay(Register delayRegister, int delay) {
        boolean res = false;
        
        if (connection.isConnected()) {
            res = connection.writeBcdRegister(MEMORY_AREA_B2, delayRegister.getValue(), new int[] {delay}); //TODO bcd
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
