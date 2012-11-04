package com.github.koszoaron.maguscada.fragment;

import com.github.koszoaron.maguscada.ScadaActivity;
import android.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {

    protected final ScadaActivity getMainActivity() {
        return (ScadaActivity) getActivity();
    }
    
    public abstract String getFragmentTag();
    
}
