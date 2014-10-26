package com.gabiq.youbid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.gabiq.youbid.R;
import com.gabiq.youbid.fragment.BidListFragment;
import com.gabiq.youbid.fragment.CommentsFragment;
import com.gabiq.youbid.fragment.Page1Fragment;
import com.gabiq.youbid.fragment.Page2Fragment;
import com.gabiq.youbid.fragment.SubmitOfferFragment;

public class DetailsFragmentAdapter extends FragmentPagerAdapter {

    private final boolean isSeller;
    private final String itemId;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private int mCount = 3;

    public DetailsFragmentAdapter(String itemId, boolean isSeller, FragmentManager fm) {
        super(fm);
        this.itemId = itemId;
        this.isSeller = isSeller;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 1:{
                return CommentsFragment.newInstance(itemId);
            }
            case 2: {
                return BidListFragment.newInstance(itemId, isSeller);
            }
            default:{
                return SubmitOfferFragment.newInstance(itemId);
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }
}