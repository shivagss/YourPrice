package com.gabiq.youbid.itemMenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gabiq.youbid.R;

public class ItemMenu extends RelativeLayout {
    private ItemMenuLayout mItemMenuLayout;

    private ImageView mHintView;

    public ItemMenu(Context context) {
        super(context);
        init(context);
    }

    public ItemMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttrs(attrs);
    }

    private void init(Context context) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.item_menu, this);

        mItemMenuLayout = (ItemMenuLayout) findViewById(R.id.item_layout);

        final ViewGroup controlLayout = (ViewGroup) findViewById(R.id.control_layout);
        controlLayout.setClickable(true);
        controlLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mHintView.startAnimation(createHintSwitchAnimation(mItemMenuLayout.isExpanded()));
                    mItemMenuLayout.switchState(true);
                }

                return false;
            }
        });

        mHintView = (ImageView) findViewById(R.id.control_hint);
    }


    public void openMenu() {
//        mHintView.startAnimation(createHintSwitchAnimation(mItemMenuLayout.isExpanded()));
        mItemMenuLayout.toState(true, true);
    }

    public void closeMenu() {
//        mHintView.startAnimation(createHintSwitchAnimation(mItemMenuLayout.isExpanded()));
        mItemMenuLayout.toState(false, true);
    }

    public void toState(boolean onState, boolean showAnimation) {
        mItemMenuLayout.toState(onState, showAnimation);
    }

    private void applyAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ItemMenuLayout, 0, 0);

            float fromDegrees = a.getFloat(R.styleable.ItemMenuLayout_fromDegrees, ItemMenuLayout.DEFAULT_FROM_DEGREES);
            float toDegrees = a.getFloat(R.styleable.ItemMenuLayout_toDegrees, ItemMenuLayout.DEFAULT_TO_DEGREES);
            mItemMenuLayout.setArc(fromDegrees, toDegrees);

            int defaultChildSize = mItemMenuLayout.getChildSize();
            int newChildSize = a.getDimensionPixelSize(R.styleable.ItemMenuLayout_childSize, defaultChildSize);
            mItemMenuLayout.setChildSize(newChildSize);

            a.recycle();
        }
    }

    public void addItem(View item, OnClickListener listener) {
        mItemMenuLayout.addView(item);
        item.setOnClickListener(getItemClickListener(listener));
    }

    private OnClickListener getItemClickListener(final OnClickListener listener) {
        return new OnClickListener() {

            @Override
            public void onClick(final View viewClicked) {
                Animation animation = bindItemAnimation(viewClicked, true, 400);
                animation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                itemDidDisappear();
                            }
                        }, 0);
                    }
                });

                final int itemCount = mItemMenuLayout.getChildCount();
                for (int i = 0; i < itemCount; i++) {
                    View item = mItemMenuLayout.getChildAt(i);
                    if (viewClicked != item) {
                        bindItemAnimation(item, false, 300);
                    }
                }

                postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mItemMenuLayout.toState(false, false);
//                        mItemMenuLayout.setVisibility(View.INVISIBLE);
                    }
                }, 300);

                mItemMenuLayout.invalidate();
//                mHintView.startAnimation(createHintSwitchAnimation(true));

                if (listener != null) {
                    listener.onClick(viewClicked);
                }
            }
        };
    }

    private Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
        Animation animation = createItemDisapperAnimation(duration, isClicked);
        child.setAnimation(animation);

        return animation;
    }

    private void itemDidDisappear() {
        final int itemCount = mItemMenuLayout.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View item = mItemMenuLayout.getChildAt(i);
            item.clearAnimation();
        }

        mItemMenuLayout.toState(false, false);
//        mItemMenuLayout.switchState(false);
//        setVisibility(View.INVISIBLE);
    }

    private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

        animationSet.setDuration(duration);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        return animationSet;
    }


    private Animation createHintSwitchAnimation(final boolean expanded) {
        AnimationSet animationSet = new AnimationSet(true);

        Animation animation = new RotateAnimation(expanded ? 45 : 0, expanded ? 0 : 45, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animationSet.addAnimation(animation);

//        if (expanded) {
//            animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
//        } else {
//            animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
//        }

        animationSet.setDuration(400);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if (expanded) {
                    setVisibility(View.INVISIBLE);
                    toState(false, false);
                }
            }
        });

        return animationSet;
    }
}


