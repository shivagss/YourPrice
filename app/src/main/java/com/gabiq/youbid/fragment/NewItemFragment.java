package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.NewItemActivity;
import com.gabiq.youbid.activity.PreviewPhotoActivity;
import com.gabiq.youbid.model.Item;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewItemFragment extends Fragment {

    private static final int TAKE_PHOTO_CODE = 1;
    private static final int AVIARY_PHOTO_CODE = 2;
    private static final int CROP_PHOTO_CODE = 3;
    private static final int POST_PHOTO_CODE = 4;

    private Bitmap photoBitmap;

    private ImageButton btnPhoto;
    private Button btnSaveItem;
    private Button btnCancelItem;
    private TextView etItemCaption;
    private TextView etItemPrice;
    private ParseFile photoFile;
    private String mItemId;
    private Item mItem;
    private ProgressDialog mProgressDialog;

    public NewItemFragment(){};

    public static NewItemFragment newInstance(String itemId) {
        NewItemFragment newItemFragment = new NewItemFragment();
        Bundle args = new Bundle();
        args.putString("item_id", itemId);
        newItemFragment.setArguments(args);
        return newItemFragment;
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

    private void updateUI(Item item) {
        etItemPrice.setText(Double.toString(item.getMinPrice()));
        etItemCaption.setText(item.getCaption());
        ParseFile itemCoverPhoto = (ParseFile) item.getPhotoFile();
        byte[] file = new byte[0];
        try {
            file = itemCoverPhoto.getData();
            Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
            photoBitmap = image;
            btnPhoto.setImageBitmap(image);
        } catch (ParseException e) {
            e.printStackTrace();
            btnPhoto.setImageResource(android.R.drawable.ic_dialog_alert);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_item, parent, false);

        etItemCaption = ((EditText) v.findViewById(R.id.etCaption));
        etItemPrice = ((EditText) v.findViewById(R.id.etMinPrice));

        btnPhoto = ((ImageButton) v.findViewById(R.id.btnPhoto));
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etItemCaption.getWindowToken(), 0);
                openImageIntent();
            }
        });

        btnSaveItem = ((Button) v.findViewById(R.id.btnSaveItem));
        btnSaveItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress(getActivity().getString(R.string.saving_item));
                Item item = mItem;
                if(item == null){
                    item = new Item();
                }

                // Save the scaled image to Parse
                photoFile = new ParseFile("item_photo.jpg", getScaledPhoto(photoBitmap));
                photoFile.saveInBackground(new SaveCallback() {

                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                item.setPhotoFile(photoFile);

                // Save the scaled image to Parse
                ParseFile photoThumbnailFile = new ParseFile("item_photo_thumnail.jpg", getThumbnailScaledPhoto(photoBitmap));
                photoThumbnailFile.saveInBackground(new SaveCallback() {

                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                item.setThumbnailFile(photoThumbnailFile);

                // When the user clicks "Save," upload the mItem to Parse
                item.setCaption(etItemCaption.getText().toString());

                // Associate the mItem with the current user
                item.setUser(ParseUser.getCurrentUser());
                //TODO: update with real value
                item.setMinPrice(Double.parseDouble(etItemPrice.getText().toString()));
                item.setHasSold(false);

                // Save the mItem and return
                item.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        dismissProgress();
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

        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etItemCaption.getWindowToken(), 0);


        mItemId =  getArguments().getString("item_id");
        if(!TextUtils.isEmpty(mItemId)){
            retrieveItem(mItemId);
        }

        return v;
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
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
//                try {
//                    photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
////                    startPreviewPhotoActivity();
//                    btnPhoto.setVisibility(View.VISIBLE);
//                    btnPhoto.setImageBitmap(photoBitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(
//                            getActivity().getApplicationContext(),
//                            "Error saving: " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                }

                Intent newIntent = new Intent( getActivity(), FeatherActivity.class );
                newIntent.setData( selectedImageUri );
                newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, getActivity().getString(R.string.aviary_api_secret));
                startActivityForResult( newIntent, AVIARY_PHOTO_CODE );

            } else if (requestCode == AVIARY_PHOTO_CODE) {
                Uri mImageUri = data.getData();
                Bundle extra = data.getExtras();
                if( null != extra ) {
                    // image has been changed by the user?
                    boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
                }

                try {
                    photoBitmap = BitmapFactory.decodeFile(mImageUri.getPath());
//                    photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
//                    startPreviewPhotoActivity();
                    btnPhoto.setVisibility(View.VISIBLE);
                    btnPhoto.setImageBitmap(photoBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }  else if (requestCode == CROP_PHOTO_CODE) {
                photoBitmap = data.getParcelableExtra("data");
                startPreviewPhotoActivity();
            } else if (requestCode == POST_PHOTO_CODE) {
                savePhotos();
            }
        }
    }

    private void savePhotos() {
        //Photo after adding effects
    }

    private void cropPhoto(Uri photoUri) {
        //call the standard crop action intent (the user device may not support it)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(photoUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 300);
        cropIntent.putExtra("outputY", 300);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_PHOTO_CODE);
    }

    private String getFileUri(Uri mediaStoreUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(mediaStoreUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String fileUri = cursor.getString(columnIndex);
        cursor.close();

        return fileUri;
    }

    private void startPreviewPhotoActivity() {
        Intent i = new Intent(getActivity(), PreviewPhotoActivity.class);
        i.putExtra("photo_bitmap", photoBitmap);
        startActivityForResult(i, POST_PHOTO_CODE);
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
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, TAKE_PHOTO_CODE);
    }

    private byte[] getScaledPhoto(Bitmap bmImage) {

        int height = 300;
        int width = 300;

        if(bmImage.getHeight() > 300){
            height = bmImage.getHeight();
            if(height > 600){
                height = 600;
            }
        }
        if(bmImage.getWidth() > 300){
            width = bmImage.getWidth();
            if(width > 600){
                width = 600;
            }
        }

        Bitmap bmImageScaled = Bitmap.createScaledBitmap(bmImage, width, height, false);

        // Override Android default landscape orientation and save portrait
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(bmImageScaled, 0,
//                0, bmImageScaled.getWidth(), bmImageScaled.getHeight(),
//                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();

    }

    private byte[] getThumbnailScaledPhoto(Bitmap bmImage) {

        Bitmap bmImageScaled = Bitmap.createScaledBitmap(bmImage, 200, 200
                * bmImage.getHeight() / bmImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(bmImageScaled, 0,
//                0, bmImageScaled.getWidth(), bmImageScaled.getHeight(),
//                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();

    }

    private void retrieveItem(String itemId){
        showProgress("Retrieving Item");
        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("objectId", itemId);
        query.getFirstInBackground(new GetCallback<Item>() {
            public void done(Item i, ParseException e) {
                if(e == null){
                    mItem = i;
                    updateUI(mItem);
                } else {
                    e.printStackTrace();
                }
                dismissProgress();
            }
        });
    }

    public void showProgress(String message){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void dismissProgress(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

}
