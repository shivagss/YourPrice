package com.gabiq.youbid.fragment;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;
import com.gabiq.youbid.R;
import com.gabiq.youbid.listener.OnNewItemFragmentInteractionListener;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Page1Fragment extends Fragment {

    private static final int TAKE_PHOTO_CODE = 1;
    private static final int AVIARY_PHOTO_CODE = 2;
    private OnNewItemFragmentInteractionListener mListener;
    private Bitmap photoBitmap;
    private EditText etItemCaption;
    private ParseImageView btnPhoto;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Page1Fragment.
     */
    public static Page1Fragment newInstance(String param1, String param2) {
        Page1Fragment fragment = new Page1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public Page1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page1, container, false);

        setupViews(v);
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.item, menu);

        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_next).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            return true;
        }
        if (id == R.id.action_next) {
            mListener.nextPage(1);
            return true;
        }
        if (id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUI(Item item){
        if(item != null){
            etItemCaption.setText(item.getCaption());
        }

        if(item != null){
            ParseFile itemCoverPhoto = (ParseFile) item.getPhotoFile();
            byte[] file = new byte[0];
            try {
                file = itemCoverPhoto.getData();
                Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                photoBitmap = image;
                btnPhoto.setParseFile(itemCoverPhoto);
                btnPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (ParseException e) {
                btnPhoto.setImageResource(android.R.drawable.ic_dialog_alert);
                btnPhoto.setScaleType(ImageView.ScaleType.CENTER);
                e.printStackTrace();
            }

            btnPhoto.loadInBackground();
        }
    }

    public ParseImageView getBtnPhoto(){
        return btnPhoto;
    }

    private void setupViews(View v) {
        etItemCaption = ((EditText) v.findViewById(R.id.etCaption));
        btnPhoto = ((ParseImageView) v.findViewById(R.id.ivImage1));
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        btnPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (photoBitmap == null) return true;
                startAviaryActivity(getImageUri(getActivity(), photoBitmap));
                return true;
            }
        });
    }

    private boolean isvalidInput() {
        if(TextUtils.isEmpty(etItemCaption.getText().toString())){
            Utils.showAlertDialog(getActivity(), "Message", getString(R.string.error_enter_caption), true);
            return false;
        }
        if(photoBitmap == null){
            Utils.showAlertDialog(getActivity(), "Message", getString(R.string.error_upload_photo), true);
            return false;
        }
        return true;
    }

    public boolean saveRequiredFields(Item item){
        if(!isvalidInput()) return false;

        ParseFile photoFile = new ParseFile("item_photo.jpg", getScaledPhoto(photoBitmap));
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving photo. Please try again later ",
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
                            "Error saving photo. Please try again later ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        item.setThumbnailFile(photoThumbnailFile);

        // When the user clicks "Save," upload the mItem to Parse
        item.setCaption(etItemCaption.getText().toString());
        return true;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewItemFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void startAviaryActivity(Uri uri) {
        if(uri == null) return;
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
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
                    // image has been changed by the user?
                    boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                }

                try {
                    photoBitmap = BitmapFactory.decodeFile(mImageUri.getPath());
                    btnPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                    btnPhoto.setImageBitmap(photoBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
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
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getActivity().getString(R.string.title_upload_photo));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, TAKE_PHOTO_CODE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "item_photo.jpg", null);
        return Uri.parse(path);
    }

}
