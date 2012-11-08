/**
 * 
 */
package com.github.koszoaron.maguscada.util;

import com.github.koszoaron.maguscada.util.PlcConstants.Status;

public class StatusBits {
    private boolean emergencyStop;
    private boolean ready;
    private boolean congestion1;
    private boolean calibrationArmLDown;
    private boolean calibrationArmLUp;
    private boolean wasLit;
    private boolean servoProblem;
    private boolean noPressure;
    private boolean trackStopped;
    private boolean congestion2;
    private boolean congestionSensorCurtain;
    private boolean calibrationArmDDown;
    private boolean calibrationArmDUp;
    private boolean expositionSensorTriggered;
    private boolean serviceDoorOpen;
    
    public StatusBits(int numericStatus) {
        this.setBits(numericStatus);
    }
    
    public void setBits(int numericStatus) {
        emergencyStop = ((numericStatus & Status.EMERGENCY_STOP.value) > 0) ? true : false;
        ready = ((numericStatus & Status.READY.value) > 0) ? true : false;
        congestion1 = ((numericStatus & Status.CONGESTION_1.value) > 0) ? true : false;
        calibrationArmLDown = ((numericStatus & Status.CALIBRATION_ARM_L_DOWN.value) > 0) ? true : false;
        calibrationArmLUp = ((numericStatus & Status.CALIBRATION_ARM_L_UP.value) > 0) ? true : false;
        wasLit = ((numericStatus & Status.WAS_LIT.value) > 0) ? true : false;
        servoProblem = ((numericStatus & Status.SERVO_PROBLEM.value) > 0) ? true : false;
        noPressure = ((numericStatus & Status.NO_PRESSURE.value) > 0) ? true : false;
        trackStopped = ((numericStatus & Status.TRACK_STOPPED.value) > 0) ? true : false;
        congestion2 = ((numericStatus & Status.CONGESTION_2.value) > 0) ? true : false;
        congestionSensorCurtain = ((numericStatus & Status.CONGESTION_SENSOR_CURTAIN.value) > 0) ? true : false;
        calibrationArmDDown = ((numericStatus & Status.CALIBRATION_ARM_D_DOWN.value) > 0) ? true : false;
        calibrationArmDUp = ((numericStatus & Status.CALIBRATION_ARM_D_UP.value) > 0) ? true : false;
        expositionSensorTriggered = ((numericStatus & Status.EXPOSITION_SENSOR_TRIGGERED.value) > 0) ? true : false;
        serviceDoorOpen = ((numericStatus & Status.SERVICE_DOOR_OPEN.value) > 0) ? true : false;
    }

    public boolean isEmergencyStop() {
        return emergencyStop;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isCongestion1() {
        return congestion1;
    }

    public boolean isCalibrationArmLDown() {
        return calibrationArmLDown;
    }

    public boolean isCalibrationArmLUp() {
        return calibrationArmLUp;
    }

    public boolean isWasLit() {
        return wasLit;
    }

    public boolean isServoProblem() {
        return servoProblem;
    }

    public boolean isNoPressure() {
        return noPressure;
    }

    public boolean isTrackStopped() {
        return trackStopped;
    }

    public boolean isCongestion2() {
        return congestion2;
    }

    public boolean isCongestionSensorCurtain() {
        return congestionSensorCurtain;
    }

    public boolean isCalibrationArmDDown() {
        return calibrationArmDDown;
    }

    public boolean isCalibrationArmDUp() {
        return calibrationArmDUp;
    }

    public boolean isExpositionSensorTriggered() {
        return expositionSensorTriggered;
    }

    public boolean isServiceDoorOpen() {
        return serviceDoorOpen;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Status bits: ");
        
        if (emergencyStop) {
            builder.append("EMERGENCY_STOP ");
        }
        if (ready) {
            builder.append("READY ");
        }
        if (congestion1) {
            builder.append("CONGESTION_1 ");
        }
        if (calibrationArmLDown) {
            builder.append("CALIBRATION_ARM_L_DOWN ");
        }
        if (calibrationArmLUp) {
            builder.append("CALIBRATION_ARM_L_UP ");
        }
        if (wasLit) {
            builder.append("WAS_LIT ");
        }
        if (servoProblem) {
            builder.append("SERVO_PROBLEM ");
        }
        if (noPressure) {
            builder.append("NO_PRESSURE ");
        }
        if (trackStopped) {
            builder.append("TRACK_STOPPED ");
        }
        if (congestion2) {
            builder.append("CONGESTION_2 ");
        }
        if (congestionSensorCurtain) {
            builder.append("CONGESTION_SENSOR_CURTAIN ");
        }
        if (calibrationArmDDown) {
            builder.append("CALIBRATION_ARM_D_DOWN ");
        }
        if (calibrationArmDUp) {
            builder.append("CALIBRATION_ARM_D_UP ");
        }
        if (expositionSensorTriggered) {
            builder.append("EXPOSITION_SENSOR_TRIGGERED ");
        }
        if (serviceDoorOpen) {
            builder.append("SERVICE_DOOR_OPEN ");
        }
        
        return builder.toString();
    }
}