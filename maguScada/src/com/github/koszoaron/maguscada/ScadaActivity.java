package com.github.koszoaron.maguscada;

import com.github.koszoaron.maguscada.callback.OnFrequencyChangedListener;
import com.github.koszoaron.maguscada.communication.PlcConnection;
import com.github.koszoaron.maguscada.communication.PlcConstants;
import com.github.koszoaron.maguscada.communication.PlcConstants.Semaphore;
import com.github.koszoaron.maguscada.communication.StatusBits;
import com.github.koszoaron.maguscada.fragment.BaseDialogFragment;
import com.github.koszoaron.maguscada.fragment.BaseFragment;
import com.github.koszoaron.maguscada.fragment.ConnectDialogFragment;
import com.github.koszoaron.maguscada.fragment.MeasurementDialogFragment;
import com.github.koszoaron.maguscada.fragment.ScadaFragment;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Constants.Motor;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreLight;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreState;
import com.github.koszoaron.maguscada.util.Logger;
import com.github.koszoaron.maguscada.util.Utility;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

public class ScadaActivity extends Activity implements OnFrequencyChangedListener {
    private static Logger dlog = new Logger(ScadaActivity.class.getSimpleName());
    
    private static final int FL_FRAGMENT_HOLDER = R.id.flMainFragmentHolder;
    private static final int CHECK_STATUS_BITS_REPEAT_DELAY = 1000;
    
    private boolean measuring = false;
    private boolean calibrating = false;
    private boolean cleaning = false;
    private boolean yellowLightOn = false;
        
    private PlcConnection connection;
    private static Handler handler = new Handler();
    
    private Runnable checkStatusBitsTask = new Runnable() {
        @Override
        public void run() {
            new GetStatusTask().execute();
            new GetSemaphoreStatusTask().execute();
            
            handler.postDelayed(this, CHECK_STATUS_BITS_REPEAT_DELAY);
        }
    };

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        //TODO uml diagram
        
        showFragment(Constants.SCADA_FRAGMENT);
        showDialog(Constants.CONNECT_DIALOG_FRAGMENT);
    }

    public void showFragment(String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment f = getFragmentByTag(tag);
        
        if (f != null) {
            ft.add(FL_FRAGMENT_HOLDER, f, tag);
            ft.commit();
        }
    }
    
    public void replaceFragment(BaseFragment bf) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        if (bf != null) {
            ft.replace(FL_FRAGMENT_HOLDER, bf, bf.getFragmentTag());
            ft.commit();
        }
    }
    
    public void replaceFragment(String tag) {
        replaceFragment(getFragmentByTag(tag));
    }
    
    public void showDialog(String tag) {
        FragmentManager fm = getFragmentManager();
        BaseDialogFragment dialog = null;
        
        if (tag.equals(Constants.CONNECT_DIALOG_FRAGMENT)) {
            dialog = ConnectDialogFragment.newInstance();            
        } else if (tag.equals(Constants.MEASUREMENT_DIALOG_FRAGMENT)) {
            dialog = MeasurementDialogFragment.newInstance();
        }
        
        if (dialog != null) {
            dialog.show(fm, tag);
        }
    }
    
    public void dismissDialog(String tag) {
        //TODO
    }
    
    public void establishConnection(String address, int port) {
        dlog.v("connect to " + address + ":" + port);
        
        connection = new PlcConnection(address, port, this);
        new ConnectTask().execute();        
    }
    
    public void beginMeasurement(final MeasureSetting what, final int tubeLenght, final int tubeDiameter) {
        dlog.v("begin measurement: " + what + "; " + tubeLenght + "; " + tubeDiameter);
        getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR2, 0);
        new StartMeasurementTask().execute(what, tubeLenght, tubeDiameter);
        getScadaFragment().logToConsole("Start measurement: " + what + " (" + tubeLenght + "x" + tubeDiameter + ")");
        setMeasuring();
        getScadaFragment().toggleMeasurementLabel(true);
        if (what == MeasureSetting.BOTH) {
            getScadaFragment().setCamera1ActiveStatus(true);
            getScadaFragment().setCamera2ActiveStatus(true);
        } else if (what == MeasureSetting.LENGTH) {
            getScadaFragment().setCamera1ActiveStatus(true);
        } else if (what == MeasureSetting.DIAMETER) {
            getScadaFragment().setCamera2ActiveStatus(true);
        }
    }
    
    public void reset() {
        this.measuring = false;
        this.calibrating = false;
        this.cleaning = false;
        
        getScadaFragment().logToConsole("Reset");
        new ResetTask().execute();
    }
    
    public void photoTop() {
        getScadaFragment().logToConsole("Photo: top");
        new ExposeTopCameraTask().execute();
    }
    
    public void photoFront() {
        getScadaFragment().logToConsole("Photo: front");
        new ExposeFrontCameraTask().execute();
    }
    
    public void calibrateTop() {
        getScadaFragment().logToConsole("Calibrate: top");
        new CalibrateTopCameraTask().execute();
    }
    
    public void calibrateFront() {
        getScadaFragment().logToConsole("Calibrate: front");
        new CalibrateFrontCameraTask().execute();
    }
    
    public void toggleYellowLight() {
        yellowLightOn = !yellowLightOn;
        new ToggleYellowWarningTask().execute(yellowLightOn);
    }
    
    public void toggleTrackPosition() {
        getScadaFragment().logToConsole("Toggle track position");
        new ToggleTrackPositionTask().execute();
    }
    
    public boolean isMeasuring() {
        return measuring;
    }

    public void setMeasuring() {
        this.measuring = true;
        this.calibrating = false;
        this.cleaning = false;
    }

    public boolean isCalibrating() {
        return calibrating;
    }

    public void setCalibrating() {
        this.calibrating = true;
        this.measuring = false;
        this.cleaning = false;
    }

    public boolean isCleaning() {
        return cleaning;
    }

    public void setCleaning() {
        this.cleaning = true;
        this.measuring = false;
        this.calibrating = false;
    }
    
    public boolean isYellowLightOn() {
        return yellowLightOn;
    }
    
    public void setYellowLight(boolean on) {
        this.yellowLightOn = on;
    }
    
    public void onPositiveDialogAction(String tag) {
        if (tag.equals(Constants.SHUTDOWN_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Shutdown");
            new ShutdownTask().execute();
        } else if (tag.equals(Constants.FINISH_MEASUREMENT_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Finish measurement");
            new FinishMeasurementTask().execute();
        } else if (tag.equals(Constants.CLEANING_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Start cleaning");
            new StartCleaningTask().execute();
        } else if (tag.equals(Constants.FINISH_CLEANING_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Finish cleaning");
            new StopCleaningTask().execute();
        } else if (tag.equals(Constants.CALIBRATION_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Start calibration");
            new StartCalibrationTask().execute();
        } else if (tag.equals(Constants.FINISH_CALIBRATION_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Finish calibration");
            new StopCalibrationTask().execute();
        }
    }
    
    public void onNegativeDialogAction(String tag) {
        if (tag.equals(Constants.CLEANING_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Check cleanliness...");
            new CheckCleanlinessTask().execute();
        }
    }

    @Override
    public void onMotor1FrequencyChanged(final int freq) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR1, freq);
            }
        });
    }

    @Override
    public void onMotor2FrequencyChanged(final int freq) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR2, freq);
            }
        });
    }

    private BaseFragment getFragmentByTag(String tag) {
        if (tag.equals(Constants.SCADA_FRAGMENT)) {
            return ScadaFragment.getInstance();
        } else {
            return null;
        }
    }

    private ScadaFragment getScadaFragment() {
        return ScadaFragment.getInstance();
    }
    
    private void onTaskPreExecute(boolean emergencyStopEnabled) {
        getScadaFragment().setProgressBarStatus(true);
        getScadaFragment().setButtonsStatus(false, emergencyStopEnabled);        
    }
    
    private void onTaskPostExecute() {
        getScadaFragment().setProgressBarStatus(false);
        getScadaFragment().setButtonsStatus(true, true);
    }
    
    /*
     * Nested AsyncTask classes
     */
    
    private abstract class GenericAsyncTask extends AsyncTask<Object, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            onTaskPreExecute(true);
            handler.removeCallbacks(checkStatusBitsTask);
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                onTaskPostExecute();
                handler.post(checkStatusBitsTask);
            } else {
                dlog.e("Error executing task: " + this.getClass().getSimpleName());
                //TODO error dialog
                //disconnect ?
            }
        }
    }
    
    private class ConnectTask extends GenericAsyncTask {
        @Override
        protected void onPreExecute() {
            onTaskPreExecute(false);
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.connect() && connection.init();            
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
    
    private class ResetTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.reset();
        }
    }
    
    private class StartMeasurementTask extends GenericAsyncTask {       
        @Override
        protected Boolean doInBackground(Object... params) {
            MeasureSetting ms = (MeasureSetting)params[0];
            int l = (Integer)params[1];
            int d = (Integer)params[2];
            return connection.measurementStart(ms, l, d);
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                getScadaFragment().setButtonsForMeasurement();
            }
        }
    }
    
    private class FinishMeasurementTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.measurementStop()) {
                publishProgress(1);
                return connection.isTrackStopped(true);
            }
            
            return false;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1) {
                getScadaFragment().logToConsole("Waiting for the track to stop...");
              //TODO keep updating the UI -> get status bits (needed?)
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                measuring = false;
                getScadaFragment().toggleMeasurementLabel(false);
                getScadaFragment().setCamera1ActiveStatus(false);
                getScadaFragment().setCamera2ActiveStatus(false);
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR1, 0);
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR2, 0);
            }
        }
    }
    
    private class StartCleaningTask extends GenericAsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setCleaning();
            getScadaFragment().toggleCleaningLabel(true);
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.cleaningStart();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                getScadaFragment().setButtonsForCleaning();
            }
        }
    }
    
    private class StopCleaningTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.cleaningStop()) {
                publishProgress(1);
                return connection.isTrackStopped(true);
            }
            
            return false;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1) {
                getScadaFragment().logToConsole("Waiting for the track to stop...");
              //TODO keep updating the UI -> get status bits (needed?)
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                cleaning = false;
                getScadaFragment().toggleCleaningLabel(false);
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR1, 0);
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR2, 0);
            }
        }
    }
    
    private class CheckCleanlinessTask extends GenericAsyncTask {
        private int triggeringNum = 0;
        private int triggeringDelay = 0;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            triggeringNum = Utility.calcTriggerNumbersForCheckings(PlcConstants.TRACK_LENGTH);
            triggeringDelay = Utility.calcTriggerDelayForChecking(PlcConstants.TRACK_SPEED);
            getScadaFragment().logToConsole("Triggering the top camera " + triggeringNum + " times with a " + triggeringDelay + "ms pause");
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.checkCleanliness()) {
                publishProgress(1);
                for (int i = 0; i < triggeringNum; i++) {
                    publishProgress(i + 2);
                    if (!connection.expositionLength()) {
                        return false;
                    } else if (i < triggeringNum - 1) {
                        try {
                            Thread.sleep(triggeringDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                publishProgress(triggeringNum + 2);
                return connection.isTrackStopped(false);
            }
            
            return false;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            if (progress == 1) {
                //init done
            } else if (progress > 1 && progress < triggeringNum + 2) {
                getScadaFragment().logToConsole("Cleanliness check photo #" + (progress - 1));
            } else {
                getScadaFragment().logToConsole("Waiting for the track to stop...");
              //TODO keep updating the UI -> get status bits (needed?)
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR1, 0);
                getScadaFragment().setMotorSpeedDisplay(Motor.MOTOR2, 0);
            }
        }
    }
    
    private class ExposeTopCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.expositionLength();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    }
    
    private class ExposeFrontCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.expositionDiameter();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    }
    
    private class StartCalibrationTask extends GenericAsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setCalibrating();
            getScadaFragment().toggleCalibrationLabel(true);
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.calibrationStart()) {
                publishProgress(1);
                if (connection.calibrationStartWait()) {
                    return true;
                } else {
                    connection.setModeToOff();
                }
            }
            
            return false;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1) {
                getScadaFragment().logToConsole("Waiting for the calibration arms to be lowered...");
              //TODO keep updating the UI -> get status bits (needed?)
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                getScadaFragment().setButtonsForCalibration();
            }
        }
    }
    
    private class StopCalibrationTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.calibrationStop()) {
                publishProgress(1);
                if (connection.calibrationStopWait()) {
                    return true;
                }
            }
            
            return false;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1) {
                getScadaFragment().logToConsole("Waiting for the calibration arms to be raised...");
                //TODO keep updating the UI -> get status bits (needed?)
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                calibrating = false;
                getScadaFragment().resetPhotoButtonsLabel();
                getScadaFragment().toggleCalibrationLabel(false);
            }
        }
    }
    
    private class CalibrateTopCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrateTopCamera();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    }
    
    private class CalibrateFrontCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrateFrontCamera();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    }
    
    private class ShutdownTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            onTaskPreExecute(false);
        }
        
        @Override
        protected Boolean doInBackground(Void... params) {
            return connection.shutdown();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                dlog.w("shutdown in 1 sec..");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dlog.w("finishing activity");
                        finish();
                    }
                }, 1000);
            }
        }
        
    }
    
    private class ToggleYellowWarningTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            boolean enable = (Boolean)params[0];
            return connection.yellowWarning(enable);
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    } 
    
    private class GetStatusTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return connection.getStatus();            
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            if (result != -1) {
                StatusBits sb = new StatusBits(result);
                dlog.v(sb.toString());
                
                getScadaFragment().setServiceDoorOpenStatus(sb.isServiceDoorOpen());
                getScadaFragment().setNoPressureStatus(sb.isNoPressure());
                getScadaFragment().setServoProblemStatus(sb.isServoProblem());
                getScadaFragment().setCongestion1Status(sb.isCongestion1());
                getScadaFragment().setCongestion2Status(sb.isCongestion2());
                getScadaFragment().setCongestion2Status(sb.isCongestionSensorCurtain());
            }
        }
    }
    
    private class GetSemaphoreStatusTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return connection.getSemaphoreStatus();
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            if (result != -1) {
                if ((result & Semaphore.RED.getValue()) > 0) {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.RED, SemaphoreState.ON);
                } else {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.RED, SemaphoreState.OFF);
                }
            }
            
            if (result != -1) {
                if ((result & Semaphore.YELLOW.getValue()) > 0) {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.ON);
                } else {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.OFF);
                }
            }
            
            if (result != -1) {
                if ((result & Semaphore.GREEN.getValue()) > 0) {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.ON);
                } else {
                    getScadaFragment().setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.OFF);
                }
            }
        }
    }
    
    private class ToggleTrackPositionTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.toggleTrackPosition();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                if (isCalibrating()) {
                    getScadaFragment().setButtonsForCalibration();
                } else if (isCleaning()) {
                    getScadaFragment().setButtonsForCleaning();
                } else if (isMeasuring()) {
                    getScadaFragment().setButtonsForMeasurement();
                }
            }
        }
    }
    
    private class GetTrackPositionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return connection.getTrackPosition();
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            //TODO update track display
        }
    }
    
}
