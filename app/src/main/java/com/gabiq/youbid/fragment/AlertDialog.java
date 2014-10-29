package com.gabiq.youbid.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gabiq.youbid.R;

/**
 * Created by sreejumon on 10/27/14.
 */
public class AlertDialog extends DialogFragment {

    private String header;
    private String message;

    public void initialize(String header, String message)
    {
        this.message = message;
        this.header = header;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.alert_dialog, null);
        Button btnOk = (Button)v.findViewById(R.id.btnAlertOkay);
        TextView tvMessage = (TextView)v.findViewById(R.id.tvAlertBody);
        tvMessage.setText(message);

        TextView tvHeader = (TextView)v.findViewById(R.id.tvAlertHeader);
        tvHeader.setText(header);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(v);
        android.app.AlertDialog dialog  = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }
}
