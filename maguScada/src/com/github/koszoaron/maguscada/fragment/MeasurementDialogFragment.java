package com.github.koszoaron.maguscada.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import com.github.koszoaron.maguscada.R;
import com.github.koszoaron.maguscada.util.Constants;
import com.github.koszoaron.maguscada.util.Constants.MeasureSetting;

public class MeasurementDialogFragment extends BaseDialogFragment implements OnClickListener {

    private EditText etLength;
    private EditText etDiameter;
    private RadioButton rbLength;
    private RadioButton rbDiameter;
    private RadioButton rbBoth;
    private Button btnOk;
    private Button btnCancel;
    
    private MeasureSetting measureWhat = null;
    
    public MeasurementDialogFragment() {
    }
    
    public static MeasurementDialogFragment newInstance() {
        MeasurementDialogFragment mdf = new MeasurementDialogFragment();
        return mdf;
    }
    
    @Override
    public String getFragmentTag() {
        return Constants.MEASUREMENT_DIALOG_FRAGMENT;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measurement, container, false);
        
        getDialog().setTitle(getString(R.string.measurement_settings));
        
        etLength = (EditText)v.findViewById(R.id.etLength);
        etDiameter = (EditText)v.findViewById(R.id.etDiameter);
        rbLength = (RadioButton)v.findViewById(R.id.rbLength);
        rbDiameter = (RadioButton)v.findViewById(R.id.rbDiameter);
        rbBoth = (RadioButton)v.findViewById(R.id.rbBoth);
        btnOk = (Button)v.findViewById(R.id.btnOk);
        btnCancel = (Button)v.findViewById(R.id.btnCancel);
        
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        rbLength.setOnClickListener(this);
        rbDiameter.setOnClickListener(this);
        rbBoth.setOnClickListener(this);
        
        rbBoth.toggle();
        
        return v;
    }
    
    @Override
    public void onClick(View v) {
        if (v == btnOk) {
            String strLength = etLength.getText().toString();
            String strDiameter = etDiameter.getText().toString();
            
            int length = 0;
            if (strLength != null) {
                if (!strLength.equals("")) {                
                    try {
                        length = Integer.parseInt(strLength);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        length = 0;
                    }
                }
            }
            
            int diameter = 0;
            if (strDiameter != null) {
                if (!strDiameter.equals("")) {                
                    try {
                        diameter = Integer.parseInt(strDiameter);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        diameter = 0;
                    }
                }
            }
                        
            if (measureWhat != null && length != 0 && diameter != 0) {
                getMainActivity().beginMeasurement(measureWhat, length, diameter);
                dismiss();
            }
        } else if (v == btnCancel) {
            dismiss();
        } else if (v == rbLength) {
            measureWhat = MeasureSetting.LENGTH;
        } else if (v == rbDiameter) {
            measureWhat = MeasureSetting.DIAMETER;
        } else if (v == rbBoth) {
            measureWhat = MeasureSetting.BOTH;
        }
    };
    
    

}
