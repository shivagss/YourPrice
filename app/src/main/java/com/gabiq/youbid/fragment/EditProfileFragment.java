package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.LocationActivity;
import com.gabiq.youbid.utils.Utils;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditProfileFragment extends Fragment {

    private static final int TAKE_PHOTO_CODE = 1;
    private static final int AVIARY_PHOTO_CODE = 2;
    private final int REQUEST_MAP_CODE = 20;
    private ProgressDialog mProgressDialog;
    private Bitmap photoBitmap;
    private ParseImageView ivProfilePic;
    private EditText etFullName;
    private EditText etScreenName;
    private EditText etEmail;
//    private EditText etLocation;
    private EditText etDescription;
    private EditText etWebsite;
    private boolean mPhotoChanged;
    private ParseUser mUser;
    private Button mLocation;
    private ParseGeoPoint userLocation;


    public EditProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        dismissProgress();
        super.onPause();
    }

    private void updateUI() {
        getActivity().getActionBar().setTitle(getActivity().getString(R.string.title_activity_edit_profile));
//        etItemPrice.setText(Double.toString(item.getMinPrice()));
//        etItemCaption.setText(item.getCaption());
//        ParseFile itemCoverPhoto = (ParseFile) item.getPhotoFile();
//        byte[] file = new byte[0];
//        try {
//            file = itemCoverPhoto.getData();
//            Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
//            photoBitmap = image;
//            btnPhoto.setImageBitmap(image);
//            btnPhoto.setScaleType(ImageView.ScaleType.FIT_START);
//            btnEditPhoto.setVisibility(View.VISIBLE);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            btnPhoto.setImageResource(android.R.drawable.ic_dialog_alert);
//            btnPhoto.setScaleType(ImageView.ScaleType.CENTER);
//            btnEditPhoto.setVisibility(View.GONE);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, parent, false);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        showProgress("Retrieving profile...");

        setupViews(v);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    mUser = (ParseUser) objects.get(0);
                    updateUI(mUser);
                } else {
                    // Something went wrong.
                }
                dismissProgress();
            }
        });

        return v;
    }

    private void setupViews(View v) {

        ivProfilePic = (ParseImageView) v.findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });

        etFullName = (EditText) v.findViewById(R.id.etFullName);
        etScreenName = (EditText) v.findViewById(R.id.etScreenName);
        etEmail = (EditText) v.findViewById(R.id.etEmail);
//        etLocation = (EditText) v.findViewById(R.id.etLocation);
//        etLocation.setText((TextUtils.isEmpty(location)?"":location));
        etWebsite = (EditText) v.findViewById(R.id.etWebsite);
        etDescription = (EditText) v.findViewById(R.id.etDescription);

        mLocation = (Button)v.findViewById(R.id.btnNewLocation);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapDialog();
            }
        });
    }

    private void showMapDialog() {
        Intent i = new Intent(getActivity(), LocationActivity.class);
        startActivityForResult(i, REQUEST_MAP_CODE);
    }

    private void updateUI(ParseUser user) {

        String name = user.getString("name");
        String username = user.getString("username");
        String email = user.getString("email");
        String location = user.getString("locationText");
        String website = user.getString("website");
        String about = user.getString("about");

        etFullName.setText((TextUtils.isEmpty(name)?"":name));
        etScreenName.setText((TextUtils.isEmpty(username)?"":username));
        etEmail.setText((TextUtils.isEmpty(email)?"":email));
//        etLocation = (EditText) v.findViewById(R.id.etLocation);
//        etLocation.setText((TextUtils.isEmpty(location)?"":location));
        etWebsite.setText((TextUtils.isEmpty(website)?"":website));
        etDescription.setText((TextUtils.isEmpty(about)?"":about));
        mLocation.setText(TextUtils.isEmpty(location)?
               getResources().getString(R.string.hint_tap_to_select): location);

        ParseFile photoFile = user.getParseFile("photo");
        if (photoFile != null) {
            ivProfilePic.setParseFile(photoFile);
            ivProfilePic.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

    }

    private void startAviaryActivity(Uri uri) {
        if (uri == null) return;
        Intent newIntent = new Intent(getActivity(), FeatherActivity.class);
        newIntent.setData(uri);
        newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, getActivity().getString(R.string.aviary_api_secret));
        startActivityForResult(newIntent, AVIARY_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO_CODE) {

                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                startAviaryActivity(selectedImageUri);

            } else if (requestCode == AVIARY_PHOTO_CODE) {
                Uri mImageUri = data.getData();
                Bundle extra = data.getExtras();
                if (null != extra) {
                   mPhotoChanged = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                }

                try {
                    photoBitmap = BitmapFactory.decodeFile(mImageUri.getPath());
                    ivProfilePic.setImageBitmap(photoBitmap);
//                    btnPhoto.setScaleType(ImageView.ScaleType.FIT_START);
//                    btnEditPhoto.setVisibility(View.VISIBLE);
//                    btnPhoto.setImageBitmap(photoBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == REQUEST_MAP_CODE)
            {
                double[] location = data.getDoubleArrayExtra("location");
                Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location[0], location[1], 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0)
                {
                    StringBuilder sbLoc = new StringBuilder();
                    if(addresses.get(0).getLocality() != null)
                        sbLoc.append(addresses.get(0).getLocality());
                    if(addresses.get(0).getAdminArea() != null)
                        sbLoc.append(",").append(addresses.get(0).getAdminArea());
                    mLocation.setText(sbLoc.toString());

                    userLocation = new ParseGeoPoint();
                    userLocation.setLatitude(location[0]);
                    userLocation.setLongitude(location[1]);
                }

            }
        }
    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "youbid");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return null;
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private Uri outputFileUri;

    private void openImageIntent() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = getOutputMediaFile().getName();//Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getActivity().getString(R.string.title_upload_photo));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, TAKE_PHOTO_CODE);
    }

    private byte[] getScaledPhoto(Bitmap bmImage) {

        int height = 300;
        int width = 300;

        if (bmImage.getHeight() > 300) {
            height = bmImage.getHeight();
            if (height > 600) {
                height = 600;
            }
        }
        if (bmImage.getWidth() > 300) {
            width = bmImage.getWidth();
            if (width > 600) {
                width = 600;
            }
        }

        Bitmap bmImageScaled = Bitmap.createScaledBitmap(bmImage, width, height, false);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();

    }

    private byte[] getThumbnailScaledPhoto(Bitmap bmImage) {

        Bitmap bmImageScaled = Bitmap.createScaledBitmap(bmImage, 200, 200
                * bmImage.getHeight() / bmImage.getWidth(), false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_save) {
            save();
            getActivity().finish();
            return true;
        }
        if (id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void save() {
        if (!isvalidInput()) return;
        showProgress(getActivity().getString(R.string.saving));

        String name = etFullName.getText().toString();
        String username = etScreenName.getText().toString();
        String email = etEmail.getText().toString();
//        String location = etLocation.getText().toString();
        String website = etWebsite.getText().toString();
        String about = etDescription.getText().toString();

        ParseUser user = ParseUser.getCurrentUser();
        user.put("name", name);
        user.put("username", username);
        user.put("email",email);
//        user.put("location", location);
        user.put("website", website);
        user.put("about", about);


        if(mPhotoChanged && photoBitmap != null) {
            ParseFile photoThumbnailFile = new ParseFile("profile_photo_thumbnail.jpg", getThumbnailScaledPhoto(photoBitmap));
            photoThumbnailFile.saveInBackground(new SaveCallback() {

                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(),
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            user.put("photo", photoThumbnailFile);
        }

        if(userLocation != null)
        {
            user.put("location",userLocation);
            user.put("locationText", mLocation.getText());
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    e.printStackTrace();
                }
                dismissProgress();
            }
        });



    }

    private boolean isvalidInput() {
        if(TextUtils.isEmpty(etFullName.getText())){
            Utils.showAlertDialog(getFragmentManager(), getResources().getString(R.string.alert_header_generic), getActivity().getString(R.string.error_enter_name), true);
            return false;
        }
        return true;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "item_photo.jpg", null);
        return Uri.parse(path);
    }

    public void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
