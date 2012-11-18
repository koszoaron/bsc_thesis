package com.github.koszoaron.maguscada.util;

public class Constants {
    
    public static final String SCADA_FRAGMENT = "ScadaFragment";
    public static final String CONNECT_DIALOG_FRAGMENT = "ConnectDialogFragment";
    public static final String MEASUREMENT_DIALOG_FRAGMENT = "MeasurementDialogFragment";
    public static final String SHUTDOWN_DIALOG_FRAGMENT = "ShutdownDialogFragment";
    public static final String FINISH_MEASUREMENT_DIALOG_FRAGMENT = "FinishMeasurementDialogFragment";
    public static final String FINISH_CLEANING_DIALOG_FRAGMENT = "FinishCleaningDialogFragment";
    public static final String FINISH_CALIBRATION_DIALOG_FRAGMENT = "FinishCalibrationDialogFragment";
    public static final String CLEANING_DIALOG_FRAGMENT = "CleaningDialogFragment";
    public static final String CALIBRATION_DIALOG_FRAGMENT = "CalibrationDialogFragment";
    
    public static final int BLINK_INTERVAL_MS = 1000;
    
    public static enum MeasureSetting {
        LENGTH,
        DIAMETER,
        BOTH
    }
    
    public static enum SemaphoreLight {
        RED,
        YELLOW,
        GREEN
    }
    
    public static enum SemaphoreState {
        ON,
        OFF,
        BLINK
    }
    
    public static enum Motor {
        MOTOR1,
        MOTOR2
    }
    
}
