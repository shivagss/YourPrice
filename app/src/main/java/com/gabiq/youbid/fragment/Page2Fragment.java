package com.gabiq.youbid.fragment;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Page2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Page2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnNewItemFragmentInteractionListener mListener;
    private EditText etItemMinPrice;
    private EditText etItemDescription;
    private EditText etItemTags;
    private GridView gvTags;
    private ArrayList<Keyword> mTagsList;
    private ArrayAdapter<Keyword> mTagsAdapter;
    private Button btnAddTag;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Page2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Page2Fragment newInstance(String param1, String param2) {
        Page2Fragment fragment = new Page2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page2, container, false);
        setupViews(v);

        return v;
    }

    public void updateUI(Item item){
        if(item != null){
            etItemMinPrice.setText(Double.toString(item.getMinPrice()));
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
            item.setMinPrice(Double.parseDouble(etItemMinPrice.getText().toString()));
            item.setDescription(etItemDescription.getText().toString());
            item.setKeywords(mTagsList);
        }catch (NumberFormatException e){
            //Do nothing
        }
    }
}
