package com.kxf.userlibrary.register;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kxf.mvvm.router.RouterActivityPath;
import com.kxf.userlibrary.BR;

import com.kxf.baselibrary.base.BaseActivity;
import com.kxf.userlibrary.R;
import com.kxf.userlibrary.databinding.ActivityRegisterBinding;

@Route(path = RouterActivityPath.PAGER_REGISTER)
public class ActivityRegister extends BaseActivity<ActivityRegisterBinding, RegisterViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_register;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
