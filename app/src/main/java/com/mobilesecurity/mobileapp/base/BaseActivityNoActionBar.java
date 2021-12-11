package com.mobilesecurity.mobileapp.base;

public abstract class BaseActivityNoActionBar extends BaseActivityForActionBar {

    @Override
    protected void onStart() {
        super.onStart();
       // WindowsUtils.hideActionBar(this);
       // StatusBarUtil.setTransparent(this);
    }

}