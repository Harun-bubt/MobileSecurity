package com.mobilesecurity.mobileapp.base;

public abstract class BaseActivityUpEnableWithMenu extends BaseActivityUpEnable {
    private int menuId;

    public BaseActivityUpEnableWithMenu(int actionBarTitleId, int menuId) {
        super(actionBarTitleId);
       this.menuId = menuId;
    }
}
