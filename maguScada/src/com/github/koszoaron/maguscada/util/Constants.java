package com.github.koszoaron.maguscada.util;

public class Constants {
    
    public static final String SCADA_FRAGMENT = "ScadaFragment";
    public static final String CONNECT_DIALOG_FRAGMENT = "ConnectDialogFragment";
    public static final String MEASUREMENT_DIALOG_FRAGMENT = "MeasurementDialogFragment";
    
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
