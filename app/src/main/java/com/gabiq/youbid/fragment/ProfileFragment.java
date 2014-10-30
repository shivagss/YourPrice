package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
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
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.EditProfileActivity;
import com.gabiq.youbid.activity.UserListActivity;
import com.gabiq.youbid.model.Followers;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.RoundTransform;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";

    private static final int TAKE_PHOTO_CODE = 1;
    private static final int AVIARY_PHOTO_CODE = 2;

    private String userId;
    private UserStoreFragment storeFragment;
    private ParseUser mUser;
    private ImageView ivProfilePic;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvLocation;
    private TextView tvWebsite;
    private TextView tvDescription;
    private Button btnEditProfile;
    private Button btnFollow;
    private Button btnUnFollow;
    private Bitmap photoBitmap;
    private ProgressDialog mProgressDialog;
    private Followers mFollower;
    private int mItemsCount;
    private int mFollowingCount;
    private int mFollowersCount;
    private Button btnFollowingCount;
    private Button btnFollowersCount;
    private Button btnItems;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId mUser id.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        } else {
            userId = ParseUser.getCurrentUser().getObjectId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        storeFragment = UserStoreFragment.newInstance(userId);
        Bundle args = storeFragment.getArguments();
        args.putInt("headerVisibility", View.GONE);
        storeFragment.setArguments(args);

//        showProgress("Fetching profile...");

        setupViews(v);

        updateUI();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flStoreContainer, storeFragment).commit();
        return v;

    }

    private void updateUI() {
        btnFollow.setVisibility(View.GONE);
        btnUnFollow.setVisibility(View.GONE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
        public void done(List<ParseUser> objects, ParseException e) {
            if (e == null) {
                mUser = (ParseUser) objects.get(0);
                if(mUser.getObjectId().equalsIgnoreCase(ParseUser.getCurrentUser().getObjectId())){
                    btnEditProfile.setVisibility(View.VISIBLE);
                }else{
                    btnEditProfile.setVisibility(View.GONE);
                }
                queryUserData();

                tvUserName.setText(mUser.getString("name"));
                tvScreenName.setText(mUser.getString("username"));
                tvLocation.setText(mUser.getString("locationText"));
                tvWebsite.setText(mUser.getString("website"));
                tvDescription.setText(mUser.getString("about"));
                ParseFile photoFile = mUser.getParseFile("photo");

                if (photoFile != null) {
                    Picasso.with(getActivity())
                            .load(photoFile.getUrl())
                            .transform(new RoundTransform())
                            .into(ivProfilePic);
                }

            } else {
                // Something went wrong.
            }
//            dismissProgress();
        }
    });
}

    private void setupViews(View v) {
        ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId.equalsIgnoreCase(ParseUser.getCurrentUser().getObjectId())) {
                    openImageIntent();
                }
            }
        });
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvWebsite = (TextView) v.findViewById(R.id.tvDisplayURL);
        tvDescription = (TextView) v.findViewById(R.id.tvUserDescription);
        btnEditProfile = (Button) v.findViewById(R.id.btnEditProfile);
        btnFollow = (Button) v.findViewById(R.id.btnFollowIcon);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser follower = ParseUser.getCurrentUser();
                ParseUser following = mUser;
                Followers followers = new Followers(follower, following);
                followers.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(getActivity(), "Error following user. Please try again later.", Toast.LENGTH_LONG).show();
                            btnFollow.setVisibility(View.VISIBLE);
                            btnUnFollow.setVisibility(View.GONE);
                        }else{
                            btnFollow.setVisibility(View.GONE);
                            btnUnFollow.setVisibility(View.VISIBLE);
                            queryUserData();
                        }
                    }
                });
            }
        });
        btnUnFollow = (Button) v.findViewById(R.id.btnFollowingIcon);
        btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFollower == null){
                    Toast.makeText(getActivity(), "Error while unfollowing user. Please try again later.", Toast.LENGTH_LONG).show();
                    return;
                }
                mFollower.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(getActivity(), "Error following user. Please try again later.", Toast.LENGTH_LONG).show();
                            btnFollow.setVisibility(View.GONE);
                            btnUnFollow.setVisibility(View.VISIBLE);
                        }else{
                            btnFollow.setVisibility(View.VISIBLE);
                            btnUnFollow.setVisibility(View.GONE);
                            queryUserData();
                        }
                    }
                });
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnItems = (Button) v.findViewById(R.id.btnItems);
        btnFollowersCount = (Button) v.findViewById(R.id.btnFollowers);
        btnFollowingCount = (Button) v.findViewById(R.id.btnFollowing);
        btnFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserListActivity.class);
                intent.putExtra("type", "following");
                intent.putExtra("userId", mUser.getObjectId());
                startActivity(intent);
            }
        });

        btnFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserListActivity.class);
                intent.putExtra("type", "followers");
                intent.putExtra("userId", mUser.getObjectId());
                startActivity(intent);
            }
        });

    }

    private void queryUserData(){
        if(!mUser.getObjectId().equalsIgnoreCase(ParseUser.getCurrentUser().getObjectId())) {
            ParseQuery<Followers> isFollowingParseQuery = ParseQuery.getQuery(Followers.class);
            isFollowingParseQuery.whereEqualTo("follower", ParseUser.getCurrentUser());
            isFollowingParseQuery.whereEqualTo("following", mUser);
            isFollowingParseQuery.getFirstInBackground(new GetCallback<Followers>() {
                @Override
                public void done(Followers followers, ParseException e) {
                    if (e == null) {
                        mFollower = followers;
                        if (mFollower != null) {
                            btnUnFollow.setVisibility(View.VISIBLE);
                            btnFollow.setVisibility(View.GONE);
                        }
                    } else {
                        btnFollow.setVisibility(View.VISIBLE);
                        btnUnFollow.setVisibility(View.GONE);
                    }
                }
            });
        }

        ParseQuery<Item> itemParseQuery = ParseQuery.getQuery(Item.class);
        itemParseQuery.whereEqualTo("createdBy", mUser);
        itemParseQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if(e != null){
                    Toast.makeText(getActivity(), "Error getting user items. Please try again later.", Toast.LENGTH_LONG).show();
                }else{
                    mItemsCount = i;
                    btnItems.setText(i+"\nITEMS");
                }
            }
        });

        ParseQuery<Followers> followersParseQuery = ParseQuery.getQuery(Followers.class);
        followersParseQuery.whereEqualTo("following", mUser);
        followersParseQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if(e != null){
                    Toast.makeText(getActivity(), "Error getting user items. Please try again later.", Toast.LENGTH_LONG).show();
                }else{
                    mFollowersCount = i;
                    btnFollowersCount.setText(i+"\nFOLLOWERS");
                }
            }
        });

        ParseQuery<Followers> followingParseQuery = ParseQuery.getQuery(Followers.class);
        followingParseQuery.whereEqualTo("follower", mUser);
        followingParseQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if(e != null){
                    Toast.makeText(getActivity(), "Error getting user items. Please try again later.", Toast.LENGTH_LONG).show();
                }else{
                    mFollowingCount = i;
                    btnFollowingCount.setText(i+"\nFOLLOWING");
                }
            }
        });
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
                    boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                }

                try {
                    photoBitmap = BitmapFactory.decodeFile(mImageUri.getPath());
                    ivProfilePic.setImageBitmap(photoBitmap);
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
                    mUser.put("photo", photoThumbnailFile);
                    mUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                e.printStackTrace();
                            }
                        }
                    });
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

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
    }

    public void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }
//        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.app_progress);
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
