package com.github.koszoaron.maguscada;

import com.github.koszoaron.maguscada.fragment.BaseDialogFragment;
import com.github.koszoaron.maguscada.fragment.BaseFragment;
import com.github.koszoaron.maguscada.fragment.ConnectDialogFragment;
import com.github.koszoaron.maguscada.fragment.MeasurementDialogFragment;
import com.github.koszoaron.maguscada.fragment.ScadaFragment;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.PlcConstants;
import com.github.koszoaron.maguscada.util.Utility;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

public class ScadaActivity extends Activity {
    private static Logger dlog = new Logger(ScadaActivity.class.getSimpleName());
    
    private boolean measuring = false;
    private boolean calibrating = false;
    private boolean cleaning = false;
    
    private static final int FL_FRAGMENT_HOLDER = R.id.flMainFragmentHolder;
    private PlcConnection connection;
    private static Handler handler = new Handler();

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
        
        connection = new PlcConnection(address, port);
        new ConnectTask().execute();        
    }
    
    public void beginMeasurement(final MeasureSetting what, final int tubeLenght, final int tubeDiameter) {
        dlog.v("begin measurement: " + what + "; " + tubeLenght + "; " + tubeDiameter);
        
        new StartMeasurementTask().execute(what, tubeLenght, tubeDiameter);
        
        setMeasuring();
        getScadaFragment().toggleMeasurementLabel();
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
        dlog.v("reset");

        this.measuring = false;
        this.calibrating = false;
        this.cleaning = false;
        
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
    
    public void onPositiveDialogAction(String tag) {
        if (tag.equals(Constants.SHUTDOWN_DIALOG_FRAGMENT)) {
            getScadaFragment().logToConsole("Shutdown.");
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
        } else {
            //...
        }
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
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            //TODO if result is false then warn
            onTaskPostExecute();
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
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                measuring = false;
                getScadaFragment().toggleMeasurementLabel();
                getScadaFragment().setCamera1ActiveStatus(false);
                getScadaFragment().setCamera2ActiveStatus(false);
            }
        }
    }
    
    private class StartCleaningTask extends GenericAsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            setCleaning();
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.cleaningStart();
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
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            
            if (result) {
                cleaning = false;
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
            }
        }
    }
    
    private class ExposeTopCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.expositionLength();
        }
    }
    
    private class ExposeFrontCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.expositionDiameter();
        }
    }
    
    private class StartCalibrationTask extends GenericAsyncTask {
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
            }
        }
    }
    
    private class StopCalibrationTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            if (connection.calibrationStopWait()) {
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
            }
        }
    }
    
    private class CalibrateTopCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrateTopCamera();
        }
    }
    
    private class CalibrateFrontCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrateFrontCamera();
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
    }
    
    private class GetStatusTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            //start progressbar
            getScadaFragment().setProgressBarStatus(true);
                        
        }
        
        @Override
        protected Integer doInBackground(Void... params) {
            return connection.getStatus();
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            dlog.v("getStatus: " + result);
            
            //stop progressbar
            getScadaFragment().setProgressBarStatus(false);
            
            //update UI
            //enable buttons
            
            
        }
        
    }
    
    
}
