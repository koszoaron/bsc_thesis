package com.github.koszoaron.maguscada.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.koszoaron.maguscada.Logger;
import com.github.koszoaron.maguscada.R;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Constants.Motor;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreLight;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreState;

public class ScadaFragment extends BaseFragment implements OnClickListener {
    private static Logger dlog = new Logger(ScadaFragment.class.getSimpleName());
    private static Handler handler = new Handler();
    private static ScadaFragment INSTANCE = null;

    private ImageView ivMain;
    private ImageView ivSemaphoreRed;
    private ImageView ivSemaphoreYellow;
    private ImageView ivSemaphoreGreen;
    
    private TextView tvMotor1Speed;
    private TextView tvMotor2Speed;
    private TextView tvCongestion1;
    private TextView tvCongestion2;
    
    private Button btnA1;
    private Button btnA2;
    private Button btnA3;
    private Button btnA4;
    private Button btnA5;
    private Button btnB1;
    private Button btnB2;
    private Button btnB3;
    private Button btnB4;
    private Button btnB5;
    
    private int trackImageLevel = 0;
    
    private int semaphoreRed = 0;
    private int semaphoreYellow = 0;
    private int semaphoreGreen = 0;
    private SemaphoreState redState = SemaphoreState.OFF;
    private SemaphoreState yellowState = SemaphoreState.OFF;
    private SemaphoreState greenState = SemaphoreState.OFF;
    
    private Runnable blinkSemaphoreLightsTask = new Runnable() {        
        @Override
        public void run() {
            if (redState == SemaphoreState.BLINK) {
                if (semaphoreRed == 0) {
                    semaphoreRed = 1;
                } else if (semaphoreRed == 1){
                    semaphoreRed = 0;
                }
                ivSemaphoreRed.setImageLevel(semaphoreRed);
            }
            
            if (yellowState == SemaphoreState.BLINK) {
                if (semaphoreYellow == 0) {
                    semaphoreYellow = 1;
                } else if (semaphoreYellow == 1){
                    semaphoreYellow = 0;
                }
                ivSemaphoreYellow.setImageLevel(semaphoreYellow);
            }
            
            if (greenState == SemaphoreState.BLINK) {
                if (semaphoreGreen == 0) {
                    semaphoreGreen = 1;
                } else if (semaphoreGreen == 1){
                    semaphoreGreen = 0;
                }
                ivSemaphoreGreen.setImageLevel(semaphoreGreen);
            }
            
            handler.postDelayed(this, Constants.BLINK_INTERVAL_MS);
        }
    };
    
    private Runnable flashCongestion1Sign = new Runnable() {
        
        @Override
        public void run() {
            
        }
    };
    
    public ScadaFragment() {}
    
    public static ScadaFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScadaFragment();
        }
        
        return INSTANCE;
    }
    
    @Override
    public String getFragmentTag() {
        return Constants.SCADA_FRAGMENT;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scada, container, false);
        
        ivMain = (ImageView)v.findViewById(R.id.ivMain);
        ivMain.setImageLevel(trackImageLevel);
        ivSemaphoreRed = (ImageView)v.findViewById(R.id.ivSemaphoreRed);
        ivSemaphoreRed.setImageLevel(semaphoreRed);
        ivSemaphoreYellow = (ImageView)v.findViewById(R.id.ivSemaphoreYellow);
        ivSemaphoreYellow.setImageLevel(semaphoreYellow);
        ivSemaphoreGreen = (ImageView)v.findViewById(R.id.ivSemaphoreGreen);
        ivSemaphoreGreen.setImageLevel(semaphoreGreen);
        
        tvMotor1Speed = (TextView)v.findViewById(R.id.tvMotor1Speed);
        tvMotor2Speed = (TextView)v.findViewById(R.id.tvMotor2Speed);
        tvCongestion1 = (TextView)v.findViewById(R.id.tvCongestion1);
        tvCongestion2 = (TextView)v.findViewById(R.id.tvCongestion2);
        
        btnA1 = (Button)v.findViewById(R.id.btnA1);
        btnA2 = (Button)v.findViewById(R.id.btnA2);
        btnA3 = (Button)v.findViewById(R.id.btnA3);
        btnA4 = (Button)v.findViewById(R.id.btnA4);
        btnA5 = (Button)v.findViewById(R.id.btnA5);
        btnB1 = (Button)v.findViewById(R.id.btnB1);
        btnB2 = (Button)v.findViewById(R.id.btnB2);
        btnB3 = (Button)v.findViewById(R.id.btnB3);
        btnB4 = (Button)v.findViewById(R.id.btnB4);
        btnB5 = (Button)v.findViewById(R.id.btnB5);
        
        btnA1.setOnClickListener(this);
        btnA2.setOnClickListener(this);
        btnA3.setOnClickListener(this);
        btnA4.setOnClickListener(this);
        btnA5.setOnClickListener(this);
        btnB1.setOnClickListener(this);
        btnB2.setOnClickListener(this);
        btnB3.setOnClickListener(this);
        btnB4.setOnClickListener(this);
        btnB5.setOnClickListener(this);
        
        btnA1.setText("Measurement");
        btnA2.setText("Cleaning");
        btnA3.setText("Calibration");
        btnA4.setText("Warning Sign");
        btnA5.setText("Emergency Stop!");
        btnB1.setText("Reset");
        btnB2.setText("Track Up/Down");
        btnB3.setText("Photo: Up");
        btnB4.setText("Photo: Front");
        btnB5.setText("Shut down");
        
        setMotorSpeedDisplay(Motor.MOTOR1, 100);
        setMotorSpeedDisplay(Motor.MOTOR2, 1234);
        
        return v;
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v == btnA1) {  //measurement
            if (getMainActivity().isMeasuring()) {
                getMainActivity().endMeasurement();
            } else {
                getMainActivity().showDialog(Constants.MEASUREMENT_DIALOG_FRAGMENT);
            }
            //TODO stop meas. func.
        } else if (v == btnA2) {  //cleaning
            //TODO sendMessage
            //TODO dialog
        } else if (v == btnA3) {  //calibration
            //TODO sendMessage
            //TODO dialog
        } else if (v == btnA4) {  //warning sign
            //TODO sendMessage
            setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.BLINK);
        } else if (v == btnA5) {  //em. stop
            //TODO toggle button
            
            //TODO sendMessage
            setSemaphoreState(SemaphoreLight.RED, SemaphoreState.ON);
            setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.OFF);
            setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.OFF);
        } else if (v == btnB1) {  //reset
            
            setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.ON);
        } else if (v == btnB2) {  //track up/down
            //TODO sendMessage.. track
            toggleTrackUpDownDisplay();
        } else if (v == btnB3) {  //photo up
            //TODO sendMessage.. photo            
            //TODO cam anim
        } else if (v == btnB4) {  //photo front
            //TODO sendMessage.. photo
            //TODO cam anim
        } else if (v == btnB5) {  //shutdown
            //TODO dialog
            //TODO if yes then sendmessage... and finish()
        }
    }
    
    public void setSemaphoreState(SemaphoreLight semaphore, SemaphoreState state) {
        switch (semaphore) {
            case RED:
                redState = state;
                break;
            case YELLOW:
                yellowState = state;
                break;
            case GREEN:
                greenState = state;
                break;
        }
        
        if (redState == SemaphoreState.BLINK || yellowState == SemaphoreState.BLINK || greenState == SemaphoreState.BLINK) {
            handler.removeCallbacks(blinkSemaphoreLightsTask);
            handler.post(blinkSemaphoreLightsTask);
        } else {
            handler.removeCallbacks(blinkSemaphoreLightsTask);
        }
        
        if (redState == SemaphoreState.ON) {
            semaphoreRed = 1;
            ivSemaphoreRed.setImageLevel(semaphoreRed);
        } else if (redState == SemaphoreState.OFF) {
            semaphoreRed = 0;
            ivSemaphoreRed.setImageLevel(semaphoreRed);
        }
        if (yellowState == SemaphoreState.ON) {
            semaphoreYellow = 1;
            ivSemaphoreYellow.setImageLevel(semaphoreYellow);
        } else if (yellowState == SemaphoreState.OFF) {
            semaphoreYellow = 0;
            ivSemaphoreYellow.setImageLevel(semaphoreYellow);
        }
        if (greenState == SemaphoreState.ON) {
            semaphoreGreen = 1;
            ivSemaphoreGreen.setImageLevel(semaphoreGreen);
        } else if (greenState == SemaphoreState.OFF) {
            semaphoreGreen = 0;
            ivSemaphoreGreen.setImageLevel(semaphoreGreen);
        }
    }
    
    public void setMotorSpeedDisplay(Motor which, int speed) {
        if (which == Motor.MOTOR1) {
            tvMotor1Speed.setText(String.format("%04d", speed));
        } else if (which == Motor.MOTOR2) {
            tvMotor2Speed.setText(String.format("%04d", speed));
        }
    }
    
    public void toggleTrackUpDownDisplay() {
        if (trackImageLevel == 0) {
            trackImageLevel = 1;
        } else if (trackImageLevel == 1){
            trackImageLevel = 0;
        }
        ivMain.setImageLevel(trackImageLevel);
    }
    
    public void toggleMeasurementLabel() {
        if (getMainActivity().isMeasuring()) {
            btnA1.setText("Stop\nMeasurement");
        } else {
            btnA1.setText("Measurement");
        }
    }
}
