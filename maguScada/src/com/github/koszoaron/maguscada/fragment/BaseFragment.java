package com.github.koszoaron.maguscada.fragment;

import com.github.koszoaron.maguscada.ScadaActivity;
import android.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected final ScadaActivity getMainActivity() {
        return (ScadaActivity) getActivity();
    }
    
    public abstract String getFragmentTag();

}
