package com.github.koszoaron.maguscada.fragment;

import com.github.koszoaron.maguscada.R;
import com.github.koszoaron.maguscada.util.Constants;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectDialogFragment extends BaseDialogFragment implements OnClickListener {
    
    private EditText etAddress;
    private EditText etPort;
    private Button btnOk;
    private Button btnCancel;
    
    public ConnectDialogFragment() {
    }
    
    public static ConnectDialogFragment newInstance() {
        ConnectDialogFragment cdf = new ConnectDialogFragment();
        return cdf;
    }

    @Override
    public String getFragmentTag() {
        return Constants.CONNECT_DIALOG_FRAGMENT;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect, container, false);
        
        getDialog().setTitle(getString(R.string.connect_to_plc));
        
        etAddress = (EditText)v.findViewById(R.id.etAddress);
        etPort = (EditText)v.findViewById(R.id.etPort);
        btnOk = (Button)v.findViewById(R.id.btnOk);
        btnCancel = (Button)v.findViewById(R.id.btnCancel);
        
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v == btnOk) {
            String address = etAddress.getText().toString();
            String strPort = etPort.getText().toString();
            
            int port = 9600;
            if (strPort != null) {
                if (!strPort.equals("")) {                
                    try {
                        port = Integer.parseInt(strPort);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        port = 9600;
                    }
                }
            }
            
            if (address != null) {
                getMainActivity().establishConnection(address, port);
                dismiss();
            }
        } else if (v == btnCancel) {
            dismiss();
        }
    }

}
