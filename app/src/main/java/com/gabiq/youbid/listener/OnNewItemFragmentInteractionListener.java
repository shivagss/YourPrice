package com.gabiq.youbid.listener;


import com.gabiq.youbid.model.Item;

public interface OnNewItemFragmentInteractionListener {
    public Item getItem();
    public void saveItem();
    public void nextPage(int position);
    public void showProgress(String message);
    public void dismissProgress();
}
