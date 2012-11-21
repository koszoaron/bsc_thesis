package com.github.koszoaron.maguscada.fragment;

import java.util.LinkedList;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.koszoaron.maguscada.R;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Logger;
import com.github.koszoaron.maguscada.util.Constants.Motor;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreLight;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreState;

public class ScadaFragment extends BaseFragment implements OnClickListener {
    private static Logger dlog = new Logger(ScadaFragment.class.getSimpleName());
    private static Handler handler = new Handler();
    private static ScadaFragment INSTANCE = null;
    
    private static final int FLASH_SHOW_TIME = 1500;
    private static final int FLASH_HIDE_TIME = 1000;

    private ImageView ivMain;
    private ImageView ivSemaphoreRed;
    private ImageView ivSemaphoreYellow;
    private ImageView ivSemaphoreGreen;
    
    private TextView tvMotor1Speed;
    private TextView tvMotor2Speed;
    private TextView tvCongestion1;
    private TextView tvCongestion2;
    private TextView tvServoProblem;
    private TextView tvNoPressure;
    private TextView tvServiceDoorOpen;
    private TextView tvCamera1Active;
    private TextView tvCamera2Active;
    private ProgressBar pbProgress;
    
    private TextView tvConsole;
    private Button btnConsole;
    
    private List<Button> functionButtonsList;
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
    
    private Runnable flashCongestion1SignTask = new Runnable() {
        
        @Override
        public void run() {
            if (tvCongestion1.getVisibility() == View.VISIBLE) {
                tvCongestion1.setVisibility(View.GONE);
                handler.postDelayed(this, FLASH_HIDE_TIME);
            } else if (tvCongestion1.getVisibility() == View.GONE) {
                tvCongestion1.setVisibility(View.VISIBLE);
                handler.postDelayed(this, FLASH_SHOW_TIME);
            }
        }
    };
    
    private Runnable flashCongestion2SignTask = new Runnable() {
        
        @Override
        public void run() {
            if (tvCongestion2.getVisibility() == View.VISIBLE) {
                tvCongestion2.setVisibility(View.GONE);
                handler.postDelayed(this, FLASH_HIDE_TIME);
            } else if (tvCongestion2.getVisibility() == View.GONE) {
                tvCongestion2.setVisibility(View.VISIBLE);
                handler.postDelayed(this, FLASH_SHOW_TIME);
            }
        }
    };
    
    private Runnable flashServoProblemSignTask = new Runnable() {
        
        @Override
        public void run() {
            if (tvServoProblem.getVisibility() == View.VISIBLE) {
                tvServoProblem.setVisibility(View.GONE);
                handler.postDelayed(this, FLASH_HIDE_TIME);
            } else if (tvServoProblem.getVisibility() == View.GONE) {
                tvServoProblem.setVisibility(View.VISIBLE);
                handler.postDelayed(this, FLASH_SHOW_TIME);
            }
        }
    };
    
    private Runnable flashNoPressureSignTask = new Runnable() {
        
        @Override
        public void run() {
            if (tvNoPressure.getVisibility() == View.VISIBLE) {
                tvNoPressure.setVisibility(View.GONE);
                handler.postDelayed(this, FLASH_HIDE_TIME);
            } else if (tvNoPressure.getVisibility() == View.GONE) {
                tvNoPressure.setVisibility(View.VISIBLE);
                handler.postDelayed(this, FLASH_SHOW_TIME);
            }
        }
    };
    
    private Runnable flashServiceDoorOpenTask = new Runnable() {
        
        @Override
        public void run() {
            if (tvServiceDoorOpen.getVisibility() == View.VISIBLE) {
                tvServiceDoorOpen.setVisibility(View.GONE);
                handler.postDelayed(this, FLASH_HIDE_TIME);
            } else if (tvServiceDoorOpen.getVisibility() == View.GONE) {
                tvServiceDoorOpen.setVisibility(View.VISIBLE);
                handler.postDelayed(this, FLASH_SHOW_TIME);
            }
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
        tvServoProblem = (TextView)v.findViewById(R.id.tvServoProblem);
        tvNoPressure = (TextView)v.findViewById(R.id.tvNoPressure);
        tvServiceDoorOpen = (TextView)v.findViewById(R.id.tvServiceDoorOpen);
        tvCamera1Active = (TextView)v.findViewById(R.id.tvCamera1Active);
        tvCamera2Active = (TextView)v.findViewById(R.id.tvCamera2Active);
        pbProgress = (ProgressBar)v.findViewById(R.id.pBar);
        
        tvConsole = (TextView)v.findViewById(R.id.tvConsole);
        btnConsole = (Button)v.findViewById(R.id.btnToggleConsole);
        btnConsole.setOnClickListener(this);
        
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
        
        functionButtonsList = new LinkedList<Button>();
        functionButtonsList.add(btnA1);
        functionButtonsList.add(btnA2);
        functionButtonsList.add(btnA3);
        functionButtonsList.add(btnA4);
        functionButtonsList.add(btnA5);
        functionButtonsList.add(btnB1);
        functionButtonsList.add(btnB2);
        functionButtonsList.add(btnB3);
        functionButtonsList.add(btnB4);
        functionButtonsList.add(btnB5);
        
        for (Button b : functionButtonsList) {
            b.setOnClickListener(this);
        }
        
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
                                
        return v;
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v == btnA1) {  //measurement
            if (getMainActivity().isMeasuring()) {
                YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.FINISH_MEASUREMENT_DIALOG_FRAGMENT, "Finish measurement?");
                dialog.show(getFragmentManager(), dialog.getFragmentTag());
            } else {
                getMainActivity().showDialog(Constants.MEASUREMENT_DIALOG_FRAGMENT);
            }
        } else if (v == btnA2) {  //cleaning
            if (getMainActivity().isCleaning()) {
                YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.FINISH_CLEANING_DIALOG_FRAGMENT, "Finish cleaning?");
                dialog.show(getFragmentManager(), dialog.getFragmentTag());
            } else {
                YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.CLEANING_DIALOG_FRAGMENT, "Check cleanliness or start cleaning?", "Start cleaning", "Check cleanliness");
                dialog.show(getFragmentManager(), dialog.getFragmentTag());
            }
        } else if (v == btnA3) {  //calibration
            if (getMainActivity().isCalibrating()) {
                YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.FINISH_CALIBRATION_DIALOG_FRAGMENT, "Finish calibration?");
                dialog.show(getFragmentManager(), dialog.getFragmentTag());
            } else {
                YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.CALIBRATION_DIALOG_FRAGMENT, "Start calibration?");
                dialog.show(getFragmentManager(), dialog.getFragmentTag());
            }
        } else if (v == btnA4) {  //warning sign
            getMainActivity().toggleYellowLight();
        } else if (v == btnA5) {  //em. stop          
            //TODO sendMessage

        } else if (v == btnB1) {  //reset
            getMainActivity().reset();
        } else if (v == btnB2) {  //track up/down
            //TODO sendMessage.. track
            toggleTrackUpDownDisplay();
        } else if (v == btnB3) {  //photo up
            if (getMainActivity().isCalibrating()) {
                getMainActivity().calibrateTop();
            } else {
                getMainActivity().photoTop();
            }
            topCameraAnimation();
        } else if (v == btnB4) {  //photo front
            if (getMainActivity().isCalibrating()) {
                getMainActivity().calibrateFront();
            } else {
                getMainActivity().photoFront();
            }
            frontCameraAnimation();
        } else if (v == btnB5) {  //shutdown
            YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(Constants.SHUTDOWN_DIALOG_FRAGMENT, "Do you want to shut down?");
            dialog.show(getFragmentManager(), Constants.SHUTDOWN_DIALOG_FRAGMENT);
        } else if (v == btnConsole) {
            if (tvConsole.getVisibility() == View.VISIBLE) {
                tvConsole.setVisibility(View.GONE);
            } else if (tvConsole.getVisibility() == View.GONE) {
                tvConsole.setVisibility(View.VISIBLE);
            }
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
    
    public void toggleMeasurementLabel(boolean enable) {
        if (enable) {
            btnA1.setText("Stop\nMeasurement");
        } else {
            btnA1.setText("Measurement");
        }
    }
    
    public void toggleCleaningLabel(boolean enable) {
        if (enable) {
            btnA2.setText("Stop\nCleaning");
        } else {
            btnA2.setText("Cleaning");
        }
    }
    
    public void toggleCalibrationLabel(boolean enable) {
        if (enable) {
            btnA3.setText("Stop\nCalibration");
        } else {
            btnA3.setText("Calibration");
        }
    }
    
    public void setCongestion1Status(boolean enabled) {
        handler.removeCallbacks(flashCongestion1SignTask);
        if (enabled) {
            handler.post(flashCongestion1SignTask);
        }
    }
    
    public void setCongestion2Status(boolean enabled) {
        handler.removeCallbacks(flashCongestion2SignTask);
        if (enabled) {
            handler.post(flashCongestion2SignTask);
        }
    }
    
    public void setServoProblemStatus(boolean enabled) {
        handler.removeCallbacks(flashServoProblemSignTask);
        if (enabled) {
            handler.post(flashServoProblemSignTask);
        }
    }
    
    public void setNoPressureStatus(boolean enabled) {
        handler.removeCallbacks(flashNoPressureSignTask);
        if (enabled) {
            handler.post(flashNoPressureSignTask);
        }
    }
    
    public void setServiceDoorOpenStatus(boolean enabled) {
        handler.removeCallbacks(flashServiceDoorOpenTask);
        if (enabled) {
            handler.post(flashServiceDoorOpenTask);
        }
    }
    
    public void setCamera1ActiveStatus(boolean enabled) {
        if (enabled) {
            tvCamera1Active.setVisibility(View.VISIBLE);
        } else {
            tvCamera1Active.setVisibility(View.GONE);
        }
    }
    
    public void setCamera2ActiveStatus(boolean enabled) {
        if (enabled) {
            tvCamera2Active.setVisibility(View.VISIBLE);
        } else {
            tvCamera2Active.setVisibility(View.GONE);
        }
    }
    
    public void setProgressBarStatus(boolean enabled) {
        if (enabled) {
            pbProgress.setVisibility(View.VISIBLE);
        } else {
            pbProgress.setVisibility(View.GONE);
        }
    }
    
    public void setButtonsStatus(boolean enabled, boolean emergencyStopEnabled) {
        for (Button btn : functionButtonsList) {
            btn.setEnabled(enabled);
        }
        
        if (!enabled && emergencyStopEnabled) {
            btnA5.setEnabled(true);
        }
    }
    
    public void setButtonsForMeasurement() {
        setButtonsStatus(false, true);
        btnA1.setEnabled(true);
        btnA4.setEnabled(true);
        btnB2.setEnabled(true);
    }
    
    public void setButtonsForCleaning() {
        setButtonsStatus(false, true);
        btnA2.setEnabled(true);
        btnA4.setEnabled(true);
        btnB3.setEnabled(true);
        btnB4.setEnabled(true);
    }
    
    public void setButtonsForCalibration() {
        setButtonsStatus(false, true);
        btnA3.setEnabled(true);
        btnA4.setEnabled(true);
        btnB3.setEnabled(true);
        btnB4.setEnabled(true);
        btnB3.setText("Calibrate Top\nCamera");
        btnB4.setText("Calibrate Front\nCamera");
    }
    
    public void resetPhotoButtonsLabel() {
        btnB3.setText("Photo: Top");
        btnB4.setText("Photo: Front");
    }
    
    public void logToConsole(String text) {
        dlog.r(text);
        if (tvConsole.getText() != null && !tvConsole.getText().equals("")) {
            tvConsole.append("\n");
        }
        tvConsole.append(text);
    }
    
    private void topCameraAnimation() {
        boolean visible = false;
        if (tvCamera1Active.getVisibility() == View.VISIBLE) {
            visible = true;
        } else {
            tvCamera1Active.setVisibility(View.VISIBLE);
        }
        final boolean visibility = visible;
        
        tvCamera1Active.setTextColor(0xffffffff);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!visibility) {
                    tvCamera1Active.setVisibility(View.GONE);
                }
                tvCamera1Active.setTextColor(0xff00ff00);
            }
        }, 1000);
    }
    
    private void frontCameraAnimation() {
        boolean visible = false;
        if (tvCamera2Active.getVisibility() == View.VISIBLE) {
            visible = true;
        } else {
            tvCamera2Active.setVisibility(View.VISIBLE);
        }
        final boolean visibility = visible;
        
        tvCamera2Active.setTextColor(0xffffffff);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!visibility) {
                    tvCamera2Active.setVisibility(View.GONE);
                }
                tvCamera2Active.setTextColor(0xff00ff00);
            }
        }, 1000);
    }
}
