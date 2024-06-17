package com.company.shougo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.databinding.WidgetEditBinding;
import com.company.shougo.mamager.Calculation;

public class EditWidget extends LinearLayout {

    private final static String TAG = "EditWidget";

    private WidgetEditBinding binding;
    private int type = 0;
    private boolean isSuccess = false;

    public EditWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_edit
                , this
                , true
        );

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditWidget);
        type = typedArray.getInt(R.styleable.EditWidget_type, 0);

        setView();

    }

    private void setView(){
        switch (type){
            case 0:
                binding.title.setText(getResources().getString(R.string.email));
                binding.img.setImageResource(R.drawable.login_ic_email);
                binding.edit.setHint(getResources().getString(R.string.email_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 1:
                binding.title.setText(getResources().getString(R.string.name));
                binding.img.setImageResource(R.drawable.login_ic_user_name);
                binding.edit.setHint(getResources().getString(R.string.name_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 2:
                binding.title.setText(getResources().getString(R.string.pwd));
                binding.img.setImageResource(R.drawable.login_ic_password);
                binding.edit.setHint(getResources().getString(R.string.pwd_hint));
                binding.edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }

        binding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = s.toString();
                checkString(ss);
            }
        });
    }

    private void checkString(String ss){
        switch (type){
            case 0:
                isSuccess = Calculation.isEmailValid(ss);
                break;
            case 1:
                isSuccess = Calculation.isNameValid(ss);
                break;
            case 2:
                isSuccess = Calculation.isPwdValid(ss);
                break;
        }

        setEditType();
    }

    private void setEditType(){
        if (isSuccess){
            binding.line.setBackgroundColor(getResources().getColor(R.color.line_gray));
            binding.error.setText("");
        }else{
            binding.line.setBackgroundColor(getResources().getColor(R.color.line_red));
            String err = "";
            switch (type){
                case 0:
                    err = getResources().getString(R.string.email_err);
                    break;
                case 1:
                    err = getResources().getString(R.string.name_err);
                    break;
                case 2:
                    err = getResources().getString(R.string.pwd_err);
                    break;
            }
            binding.error.setText(err);
        }
    }

    public String getText(){
        return binding.edit.getText().toString();
    }

    public void setText(String text){
        binding.edit.setText(text);
    }

    public boolean isSuccess(){
        checkString(getText());
        return isSuccess;
    }

}
