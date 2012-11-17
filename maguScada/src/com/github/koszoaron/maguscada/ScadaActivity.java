package com.github.koszoaron.maguscada;

import com.github.koszoaron.maguscada.fragment.BaseDialogFragment;
import com.github.koszoaron.maguscada.fragment.BaseFragment;
import com.github.koszoaron.maguscada.fragment.ConnectDialogFragment;
import com.github.koszoaron.maguscada.fragment.MeasurementDialogFragment;
import com.github.koszoaron.maguscada.fragment.ScadaFragment;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreLight;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreState;
import com.github.koszoaron.maguscada.util.StatusBits;

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

    private int imageLevel = 0;
    private int semaphoreRed = 0;
    private int semaphoreYellow = 0;
    private int semaphoreGreen = 0;

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
        dlog.r("connect to " + address + ":" + port);
        
        connection = new PlcConnection(address, port);
        new ConnectTask().execute();        
    }
    
    public void beginMeasurement(final MeasureSetting what, final int tubeLenght, final int tubeDiameter) {
        dlog.r("begin measurement: " + what + "; " + tubeLenght + "; " + tubeDiameter);
        
        new StartMeasurementTask().execute(what, tubeLenght, tubeDiameter);
        
        setMeasuring(true);
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
    
    public void endMeasurement() {
        dlog.r("end measurement");
        
        connection.measurementStop();
        setMeasuring(false);
        getScadaFragment().toggleMeasurementLabel();
        getScadaFragment().setCamera1ActiveStatus(false);
        getScadaFragment().setCamera2ActiveStatus(false);
    }
    
    public void reset() {
        dlog.r("reset");

//        getScadaFragment().setSemaphoreState(SemaphoreLight.RED, SemaphoreState.OFF);
//        getScadaFragment().setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.OFF);
//        getScadaFragment().setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.ON);
        
        setMeasuring(false);
        setCalibrating(false);
        setCleaning(false);
        
        new ResetTask().execute();
    }
    
    public void shutdown() {
        new ShutdownTask().execute();
    }
    
    public boolean isMeasuring() {
        return measuring;
    }

    public void setMeasuring(boolean measuring) {
        this.measuring = measuring;
    }

    public boolean isCalibrating() {
        return calibrating;
    }

    public void setCalibrating(boolean calibrating) {
        this.calibrating = calibrating;
    }

    public boolean isCleaning() {
        return cleaning;
    }

    public void setCleaning(boolean cleaning) {
        this.cleaning = cleaning;
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
    
    private abstract class GenericAsyncTask extends AsyncTask<Object, Void, Boolean> {
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
            return connection.measurementStop();
        }
    }
    
    private class StartCleaningTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.cleaningStart();
        }
    }
    
    private class StopCleaningTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.cleaningStop();
        }
    }
    
    private class CheckCleanlinessTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.checkCleanliness();
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
            return connection.calibrationStart();
        }
    }
    
    private class StopCalibrationTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrationStop();
        }
    }
    
    private class ExposeCamerasForCalibrationTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.expositionCalibration();
        }
    }
    
    private class CalibrateFrontCameraTask extends GenericAsyncTask {
        @Override
        protected Boolean doInBackground(Object... params) {
            return connection.calibrateDiameter();
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
            dlog.d("getStatus: " + result);
            
            //stop progressbar
            getScadaFragment().setProgressBarStatus(false);
            
            //update UI
            //enable buttons
            
            
        }
        
    }
    
    
}
