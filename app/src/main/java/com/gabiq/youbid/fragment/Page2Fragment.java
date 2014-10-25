package com.gabiq.youbid.fragment;



import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.ItemTagsAdapter;
import com.gabiq.youbid.listener.OnNewItemFragmentInteractionListener;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Keyword;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Page2Fragment extends Fragment {

    private OnNewItemFragmentInteractionListener mListener;
    private EditText etItemMinPrice;
    private EditText etItemDescription;
    private EditText etItemTags;
    private GridView gvTags;
    private ArrayList<Keyword> mTagsList;
    private ArrayAdapter<Keyword> mTagsAdapter;
    private Button btnAddTag;

    public static Page2Fragment newInstance(String param1, String param2) {
        Page2Fragment fragment = new Page2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public Page2Fragment() {
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
        View v = inflater.inflate(R.layout.fragment_page2, container, false);
        setupViews(v);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.item, menu);
        menu.findItem(R.id.action_save).setVisible(true);
        menu.findItem(R.id.action_next).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            mListener.saveItem();
            return true;
        }
        if (id == R.id.action_next) {
            return true;
        }
        if (id == android.R.id.home) {
            mListener.nextPage(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUI(Item item){
        if(item != null){
            if(item.getMinPrice() > 0) {
                etItemMinPrice.setText(Double.toString(item.getMinPrice()));
            }else{
                etItemMinPrice.setText("");
            }
            etItemDescription.setText(item.getDescription());
            mTagsAdapter.clear();
            mTagsAdapter.addAll(item.getKeywords());
        }
    }

    private void setupViews(View v) {
        etItemMinPrice = ((EditText) v.findViewById(R.id.etMinPrice));
        etItemDescription = (EditText) v.findViewById(R.id.etDescription);
        etItemTags = (EditText) v.findViewById(R.id.etTags);

        gvTags = (GridView) v.findViewById(R.id.gvTags);
        btnAddTag = (Button) v.findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etItemTags.getText().toString();
                if(!TextUtils.isEmpty(tag)){
                    Keyword keyword = new Keyword();
                    keyword.setKeyword(tag);
                    mTagsAdapter.add(keyword);
                    etItemTags.setText("");
                }
            }
        });

        mTagsList = new ArrayList<Keyword>();
        mTagsAdapter = new ItemTagsAdapter(getActivity(), mTagsList);
        gvTags.setAdapter(mTagsAdapter);

        gvTags.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTagsList.remove(i);
                mTagsAdapter.notifyDataSetChanged();
                return true;
            }
        });
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

    public void saveOptionalFields(Item item) {

        try {

//            List<Keyword> removeList = new ArrayList<Keyword>();
//            for(Keyword remove : item.getKeywords()){
//                if(!mTagsList.contains(remove)){
//                    removeList.add(remove);
//                }
//            }

//            ParseObject.deleteAllInBackground(removeList, new DeleteCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if(e != null){
//                        e.printStackTrace();
//                    }
//                }
//            });

            item.setMinPrice(Double.parseDouble(etItemMinPrice.getText().toString()));
            item.setDescription(etItemDescription.getText().toString());
            item.setKeywords(mTagsList);
        }catch (NumberFormatException e){
            //Do nothing
            item.setMinPrice(0);
        }
    }
}
