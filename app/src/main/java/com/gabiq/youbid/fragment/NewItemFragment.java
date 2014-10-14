package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.NewItemActivity;
import com.gabiq.youbid.model.Item;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.SaveCallback;

/**
 * Created by sreejumon on 10/12/14.
 */
public class NewItemFragment extends Fragment {

    private ImageButton btnPhoto;
    private Button btnSaveItem;
    private Button btnCancelItem;
    private TextView etItemCaption;
    private ParseImageView itemPreview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_item, parent, false);

        etItemCaption = ((EditText) v.findViewById(R.id.etCaption));

        btnPhoto = ((ImageButton) v.findViewById(R.id.btnPhoto));
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etItemCaption.getWindowToken(), 0);
                startCamera();
            }
        });

        btnSaveItem = ((Button) v.findViewById(R.id.btnSaveItem));
        btnSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Item item = ((NewItemActivity) getActivity()).getNewItem();

                // When the user clicks "Save," upload the item to Parse
                item.setCaption(etItemCaption.getText().toString());


                //TODO: Its crashing now, need to fix
                // Associate the item with the current user
             //   item.setUser((User) ParseUser.getCurrentUser());

                // If the user added a photo, that data will be
                // added in the CameraFragment

                // Save the item and return
                item.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });

        btnCancelItem = ((Button) v.findViewById(R.id.btnCancelItem));
        btnCancelItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        // Until the user has taken a photo, hide the preview
        itemPreview = (ParseImageView) v.findViewById(R.id.ivItemPreview);
        itemPreview.setVisibility(View.INVISIBLE);

        return v;
    }


    public void startCamera() {
        Fragment cameraFragment = new CameraFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainer, cameraFragment);
        transaction.addToBackStack("NewMealFragment");
        transaction.commit();
    }

    /*
     * On resume, check and see if a item photo has been set from the
     * CameraFragment. If it has, load the image in this fragment and make the
     * preview image visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((NewItemActivity) getActivity())
                .getNewItem().getPhotoFile();
        if (photoFile != null) {
            itemPreview.setParseFile(photoFile);
            itemPreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    itemPreview.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
