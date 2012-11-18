package com.github.koszoaron.maguscada.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class YesNoDialogFragment extends BaseDialogFragment {
    private static final String MESSAGE = "message";
    private static final String YES_BTN_LABEL = "yesBtnLabel";
    private static final String NO_BTN_LABEL = "noBtnLabel";
    
    private String tag;
    
    private YesNoDialogFragment(String fragmentTag) {
        this.tag = fragmentTag;
    }
    
    public static YesNoDialogFragment newInstance(String fragmentTag, String message) {
        YesNoDialogFragment f = new YesNoDialogFragment(fragmentTag);
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        f.setArguments(args);
        return f;
    }
    
    public static YesNoDialogFragment newInstance(String fragmentTag, String message, String yesBtnLabel, String noBtnLabel) {
        YesNoDialogFragment f = new YesNoDialogFragment(fragmentTag);
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(YES_BTN_LABEL, yesBtnLabel);
        args.putString(NO_BTN_LABEL, noBtnLabel);
        f.setArguments(args);
        return f;
    }
    
    @Override
    public String getFragmentTag() {
        return tag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(MESSAGE);
        String yesBtnLabel = getArguments().getString(YES_BTN_LABEL);
        String noBtnLabel = getArguments().getString(NO_BTN_LABEL);
        
        if (yesBtnLabel == null) {
            yesBtnLabel = "Yes";
        }
        if (noBtnLabel == null) {
            noBtnLabel = "No";
        }
        
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(yesBtnLabel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMainActivity().onPositiveDialogAction(tag);
                    }
                })
                .setNegativeButton(noBtnLabel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMainActivity().onNegativeDialogAction(tag);
                    }
                })
                .create();
    }

}
