package com.company.shougo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.databinding.WidgetChangePasswordEditBinding;
import com.company.shougo.databinding.WidgetEditBinding;
import com.company.shougo.mamager.Calculation;

public class ChangePasswordEditWidget extends LinearLayout {

    private final static String TAG = "ChangePasswordEditWidget";

    public WidgetChangePasswordEditBinding binding;
    private int type = 0;
    private boolean isSuccess = false;
    private boolean isShowPassword = false;

    public ChangePasswordEditWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_change_password_edit
                , this
                , true
        );

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChangePasswordEditWidget);
        type = typedArray.getInt(R.styleable.ChangePasswordEditWidget_password_type, 0);

        binding.imageDisplayPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowPassword = !isShowPassword;
                binding.imageDisplayPassword.setImageResource(isShowPassword ? R.drawable.ic_eye_open : R.drawable.ic_eye_close);
                if (isShowPassword) {
                    binding.edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    binding.edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        setView();
    }

    private void setView(){
        switch (type){
            case 0:
                binding.title.setText(getResources().getString(R.string.current_password));
                binding.edit.setHint(getResources().getString(R.string.current_password_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 1:
                binding.title.setText(getResources().getString(R.string.new_password));
                binding.edit.setHint(getResources().getString(R.string.new_password_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 2:
                binding.title.setText(getResources().getString(R.string.check_new_password));
                binding.edit.setHint(getResources().getString(R.string.new_password_again_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }

    }






    public boolean isSuccess(){
        return isSuccess;
    }

}
