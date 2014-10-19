package com.gabiq.youbid.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.User;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BidActionDialogFragment  extends DialogFragment {
    String bidId;

    public BidActionDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static BidActionDialogFragment newInstance(String bidId, String message) {
        BidActionDialogFragment frag = new BidActionDialogFragment();
        Bundle args = new Bundle();
        args.putString("bidId", bidId);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bidId = getArguments().getString("bidId");
        String message = getArguments().getString("message");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("BID ACTION");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ACCEPT",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bidAction(dialog, true);
            }
        });
        alertDialogBuilder.setNegativeButton("REJECT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bidAction(dialog, false);
            }
        });

        return alertDialogBuilder.create();
    }

    private void bidAction(final DialogInterface dialog, final boolean isAccept) {
         ParseQuery<ParseObject> query = ParseQuery.getQuery("Bid");
         query.getInBackground(bidId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                Bid bid = (Bid) object;
                if (e == null && bid != null) {
                    if (isAccept) {
                        bid.setState("accepted");
                    } else {
                        bid.setState("rejected");
                    }
                    bid.saveInBackground();

                } else {
                    // Display Error dialog?
                }
                dialog.dismiss();
            }
        });

    }
}