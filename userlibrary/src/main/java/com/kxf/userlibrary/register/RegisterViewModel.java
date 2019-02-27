package com.kxf.userlibrary.register;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.kxf.baselibrary.base.BaseViewModel;
import com.kxf.baselibrary.binding.command.BindingAction;
import com.kxf.baselibrary.binding.command.BindingCommand;

public class RegisterViewModel extends BaseViewModel {
    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }


    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> smsCode = new ObservableField<>();

    public BindingCommand sendSms = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

           Toast.makeText(getApplication(), name.get(),Toast.LENGTH_SHORT).show();
        }
    });
}
