package com.gabiq.youbid.activity;

import com.parse.ui.ParseLoginDispatchActivity;

public class LoginDispatchActivity extends ParseLoginDispatchActivity {

    @Override
    protected Class<?> getTargetClass() {
        return HomeActivity.class;
    }
}
