package com.github.koszoaron.bthes.plcsim.util;

public class Constants {
    public static final int REGISTER_TYPE_HEX = 0;
    public static final int REGISTER_TYPE_BCD = 1;
    
    //old constants
    public static final int CONGESTION_TOLERANCE =      100;
    public static final int CONGESTION_2ND_TOLERANCE =  100;

    public static final int DIRECTION_STOP    = 0;
    public static final int DIRECTION_FORWARD = 1;
    public static final int DIRECTION_REVERSE = 3;

    public static final int MODE_REG =                      0x01;  //mod
    public static final int LIGHTS_REG =                    0x02;  //fenyek
    public static final int CYL_DISTANCE_REG =              0x03;  //hengertavolsag (mm - hex)
    public static final int STATUS_BITS_REG =               0x04;  //plc statuszbitek
    public static final int SERVO_NULL_POS_REG =            0x05;  //szervo nullpozicio (mm - hex)
    public static final int DELAY_DIAM_LED_REG =            0x06;  //atmero LED delay (10 ms - bcd)
    public static final int DELAY_LENGTH_LED_REG =          0x07;  //hosszmeres LED delay (10 ms - bcd)
    public static final int DELAY_CONGESTION_REG =          0x09;  //torlodas - a ket szenzor kozotti ido (10 ms - bcd)
    public static final int DELAY_CONGESTION_TOL_REG =      0x0A;  //torlodas - tolerancia (10 ms - bcd)
    public static final int DELAY_SENSOR_CURTAIN_REG =      0x0B;  //szenzorfuggony tureshatar (10 ms - bcd)
    public static final int DELAY_SHUT_DOWN_REG =           0x0C;  //kikapcsolasi delay (100 ms - bcd)
    public static final int DELAY_ONE_WHOLE_TURN_REG =      0x0D;  //tisztasag ell. - 1 teljes kor ideje (100 ms - bcd)
    public static final int DELAY_ONE_LENGTH_TURN_REG =     0x0E;  //urites - 1 hossz ideje (100 ms - bcd)
    public static final int DELAY_BLOWER_ONE_DELAY_REG =    0x0F;  //elso lefuvo - delay (10 ms - bcd)
    public static final int DELAY_BLOWER_ONE_TIME_REG =     0x10;  //elso lefufo - idotartam (10 ms - bcd)
    public static final int DELAY_BLOWER_TWO_DELAY_REG =    0x11;  //masodik lefuvo - delay (10 ms - bcd)
    public static final int DELAY_BLOWER_TWO_TIME_REG =     0x12;  //masodik lefuvo - idotartam (10 ms - bcd)
    public static final int DELAY_CONGESTION_2ND_REG =      0x50;  //a keresztm. exp. szenzor es a kosar kozotti torlodas - ido (10 ms - bcd)
    public static final int DELAY_CONGESTION_2ND_TOL_REG =  0x51;  //a keresztm. exp. szenzor es a kosar kozotti torlodas - tolerancia (10 ms - bcd)
    public static final int FREQCHANGER1_REG =              0x64;  //frekivalto 1 (szalag)
    public static final int FREQCHANGER2_REG =              0x78;  //frekivalto 2 (hengerek)
    public static final int DELAY_SENSOR_RELOCATION_REG =   0x82;


    public static final int MODE_OFF =              0;
    public static final int MODE_LENGTH =           1;
    public static final int MODE_DIAMETER =         2;
    public static final int MODE_CLEAN =            4;
    public static final int MODE_UNLOCK_CALIB =     8;
    public static final int MODE_CALIBRATE =        16;
    public static final int MODE_RESET =            32;
    public static final int MODE_EMPTY =            64;
    public static final int MODE_CHECKCLARITY =     128;
    public static final int MODE_SETDIAMETER =      256;
    public static final int MODE_EXP_LENGTH =       512;
    public static final int MODE_EXP_DIAMETER =     1024;
    public static final int MODE_EXP_CALIB_LIGHT =  2048;
    public static final int MODE_YELLOW_LIGHT =     4096;
    public static final int MODE_YELLOW_LIGHT_OFF = 4097;
    public static final int MODE_SHUTDOWN =         8192;
    public static final int MODE_CALIB_END =        16384;
    public static final int MODE_CALIB_DIAMETER =   32768;

    public static final int MEASURE_LENGTH =    1;
    public static final int MEASURE_DIAMETER =  2;
    public static final int MEASURE_BOTH =      4;

    public static final int LIGHT_OFF =         0;
    public static final int LIGHT_LONG =        1;
    public static final int LIGHT_CLARIFIED =   2;
    public static final int LIGHT_PARABOLIC =   4;
    public static final int LIGHT_CALIB =       8;

    public static final int ADDRESS_BYTE0 =     29; //dataToSend[29]
    public static final int ADDRESS_BYTE1 =     30; //dataToSend[30]
    public static final int NUM_OF_REGS_BYTE =  33; //dataToSend[33]
    public static final int REG1_BYTE0 =        34; //dataToSend[34]
    public static final int REG1_BYTE1 =        35; //dataToSend[35]
    public static final int REG2_BYTE0 =        36; //dataToSend[36]
    public static final int REG2_BYTE1 =        37; //dataToSend[37]
    public static final int REG3_BYTE0 =        38; //dataToSend[38]
    public static final int REG3_BYTE1 =        39; //dataToSend[39]

    public static final int PLC_ANS_LENGTH = 32;
    public static final int STATUS_EMERGENCYSTOP =              1;
    public static final int STATUS_READY =                      2;
    public static final int STATUS_CONGESTION =                 4;
    public static final int STATUS_CALIBARM_DOWN =              8;
    public static final int STATUS_CALIBARM_UP =                16;
    public static final int STATUS_WASLIT =                     32;
    public static final int STATUS_SERVO_PROBLEM =              64;
    public static final int STATUS_NO_PRESSURE =                128;
    public static final int STATUS_TRACK_STOPPED =              256;
    public static final int STATUS_CONGESTION_2ND =             512;
    public static final int STATUS_CONGESTION_SENSOR_CURTAIN =  1024;
    public static final int STATUS_CALIBARM2_DOWN =             2048;
    public static final int STATUS_CALIBARM2_UP =               4096;
    public static final int STATUS_EXP_SENSOR_TRIPPED =         8192;
    public static final int STATUS_SERVICE_DOOR_OPEN =          16384;


    public static final int ERROR_OK =                   0; //this is not an error, but a successful return message
    public static final int ERROR_GENERAL =             -1;
    public static final int ERROR_CONF_FILE =           -2;
    public static final int ERROR_WSA_STARTUP =         -3;
    public static final int ERROR_SOCKET_INIT =         -4;
    public static final int ERROR_SOCKET_CONN =         -5;
    public static final int ERROR_FINS_CONN =           -6;
    public static final int ERROR_FINS_ACK =            -7;
    public static final int ERROR_FINS_SEND =           -8;
    public static final int ERROR_FINS_RECEIVE =        -9;
    public static final int ERROR_CALC =                -10;
    public static final int ERROR_CRITICAL_TIMEOUT =    -11;
    public static final int ERROR_CALIB_SENS_TRIP =     -12;
    public static final int ERROR_CALIB1_TIMEOUT =      -13;
    public static final int ERROR_CALIB2_TIMEOUT =      -14;
    public static final int ERROR_CALIB12_TIMEOUT =     -15;
    public static final int ERROR_CALIBARM =            -16;
    public static final int ERROR_COMM_BUSY =           -17;
    public static final int ERROR_BAD_PARAMETER =       -18;
    public static final int ERROR_MUTEX =               -19;
                                                                                   //tcp msg length                                                                                                                      //   28    29    30    31    32    33    34    35    36    37
    public static final int dataConnection[] =      {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};                                            //  area   address                size  register1   register2
    public static final int dataSingleCommand[] =   {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x1C, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0xFB, 0x00, 0x01, 0x01, 0x02, 0xb2, 0x00, 0x01, 0x00, 0x00, 0x01, 0x00, 0x03};
    public static final int dataDoubleCommand[] =   {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x1E, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0xFB, 0x00, 0x01, 0x01, 0x02, 0xb2, 0x00, 0x07, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00};
    public static final int dataTripleCommand[] =   {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0xFB, 0x00, 0x01, 0x01, 0x02, 0xb2, 0x00, 0x07, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    public static final int dataReadStatus[] =      {0x46, 0x49, 0x4e, 0x53, 0x00, 0x00, 0x00, 0x1A, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0xfb, 0x00, 0x08, 0x01, 0x01, 0xb2, 0x00, 0x04, 0x00, 0x00, 0x01};
}
