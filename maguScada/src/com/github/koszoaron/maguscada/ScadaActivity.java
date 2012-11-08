package com.github.koszoaron.maguscada;

import com.github.koszoaron.jfinslib.FinsConnection;
import com.github.koszoaron.jfinslib.FinsMessage;
import com.github.koszoaron.maguscada.fragment.BaseDialogFragment;
import com.github.koszoaron.maguscada.fragment.BaseFragment;
import com.github.koszoaron.maguscada.fragment.ConnectDialogFragment;
import com.github.koszoaron.maguscada.fragment.MeasurementDialogFragment;
import com.github.koszoaron.maguscada.fragment.ScadaFragment;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreLight;
import com.github.koszoaron.maguscada.util.Constants.SemaphoreState;
import com.github.koszoaron.maguscada.util.PlcConstants.Lights;
import com.github.koszoaron.maguscada.util.PlcConstants.Mode;
import com.github.koszoaron.maguscada.util.PlcConstants.Register;
import com.github.koszoaron.maguscada.util.StatusBits;

import android.app.FragmentTransaction;
import android.os.Bundle;
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
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                connection.connect();
            }
        }).start();
        
    }
    
    public void beginMeasurement(final MeasureSetting what, final int tubeLenght, final int tubeDiameter) {
        dlog.r("begin measurement: " + what + "; " + tubeLenght + "; " + tubeDiameter);
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                connection.measurementStart(what, tubeLenght, tubeDiameter);
            }
        }).start();
        
        
        setMeasuring(true);
        getScadaFragment().toggleMeasurementLabel();
    }
    
    public void endMeasurement() {
        dlog.r("end measurement");
        
        connection.measurementStop();
        setMeasuring(false);
        getScadaFragment().toggleMeasurementLabel();
    }
    
    public void reset() {
        dlog.r("reset");
        
        new Thread(new Runnable() {    
            @Override
            public void run() {
//                boolean res = connection.reset()
                
                int res = connection.getStatus();                
                dlog.v("result: " + res);
                
                StatusBits sb = new StatusBits(res);
                dlog.v(sb.toString());
            }
        }).start();
        
        getScadaFragment().setSemaphoreState(SemaphoreLight.RED, SemaphoreState.OFF);
        getScadaFragment().setSemaphoreState(SemaphoreLight.YELLOW, SemaphoreState.OFF);
        getScadaFragment().setSemaphoreState(SemaphoreLight.GREEN, SemaphoreState.ON);
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
}
