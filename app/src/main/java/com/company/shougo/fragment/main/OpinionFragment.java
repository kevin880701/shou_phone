package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.FeedbackData;
import com.company.shougo.databinding.FragmentOpinionBinding;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OpinionFragment extends MyFragment {

    private final static String TAG = "OpinionFragment";

    private FragmentOpinionBinding binding;

    private MainActivity activity;

    private List<FeedbackData> categoryList;

    private int selectPos = 0;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_opinion
                , group
                , false
        );
        return binding.getRoot();
    }

    @Override
    public void initView() {
        activity = (MainActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(getResources().getColor(R.color.bg_gray));

        binding.back.setOnClickListener(this);
        binding.submit.setOnClickListener(this);

        getFeedbackCategory();

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getFeedbackCategory(){
        final String tag = "getFeedbackCategory ";

        categoryList = new ArrayList<>();

        FeedbackData feedbackData = new FeedbackData();
        feedbackData.setFeedback_category_id(-1);
        feedbackData.setName(getResources().getString(R.string.select_category));
        categoryList.add(feedbackData);

        Execute.getFeedbackCategory(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try{
                                            categoryList.add(new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , FeedbackData.class
                                            ));
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    String[] sCategory = new String[categoryList.size()];
                                    for (int i=0;i<categoryList.size();i++){
                                        sCategory[i] = categoryList.get(i).getName();
                                    }

                                    ArrayAdapter adapter = new ArrayAdapter(
                                            activity
                                            , R.layout.spinner_item
                                            , sCategory
                                    );

                                    binding.spinner.setAdapter(adapter);

                                }catch (Exception e){
                                    Log.e(TAG,tag + "Exception : " + e.toString());
                                }

                            }
                        });
                    }
                }
        );
    }

    private void send(){
        final String tag = "send";

        String cmd = binding.edit.getText().toString();

        if (selectPos<=0 || cmd.length()<=0){
            return;
        }

        Execute.addFeedback(
                String.valueOf(categoryList.get(selectPos).getFeedback_category_id())
                , cmd
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " +e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        DialogManager.showConfirmDialog(
                                                activity
                                                , getResources().getString(R.string.feedback_title)
                                                , getResources().getString(R.string.feedback_content)
                                                , null
                                        );
                                    }
                                }catch (Exception e){
                                    Log.e(TAG,tag + "Exception : " + e.toString());
                                }
                            }
                        });

                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
            case R.id.submit:
                send();
                break;
        }
    }
}
