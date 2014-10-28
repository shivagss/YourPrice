package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
public class OfferConfirmation extends DialogFragment {

    public interface AlertDialogListener {
        public void onDialogPositiveClick(double amount, String itemId);
    }

    AlertDialogListener mListener;
    private String itemId;
    private double amount;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AlertDialogListener) activity;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public void setItemId(String id)
    {
        this.itemId = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.offer_confirmation, null);
        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button)v.findViewById(R.id.btnConfirm);
        TextView tvOfferText = (TextView)v.findViewById(R.id.tvOfferConfirmBody);
        tvOfferText.setText(getResources().getString(R.string.offer_confirm_body)+" $" + Double.toString(amount));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDialogPositiveClick(amount, itemId);
                dismiss();
            }
        });

        builder.setView(v);
        AlertDialog dialog  = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SlideWindow;
        return dialog;
    }
}
