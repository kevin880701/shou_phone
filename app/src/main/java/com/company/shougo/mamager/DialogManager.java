package com.company.shougo.mamager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.BaseActivity;
import com.company.shougo.adapter.AddTravelListAdapter;
import com.company.shougo.adapter.EvaAdapter;
import com.company.shougo.adapter.TicketAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.StoreCommendData;
import com.company.shougo.databinding.BottomInfoDetailBinding;
import com.company.shougo.databinding.BottomPrivacyBinding;
import com.company.shougo.databinding.DialogAddTravelBinding;
import com.company.shougo.databinding.DialogConfirmBinding;
import com.company.shougo.databinding.DialogEditBinding;
import com.company.shougo.databinding.DialogEvaListBinding;
import com.company.shougo.databinding.DialogGiveEvaBinding;
import com.company.shougo.databinding.DialogSelectBinding;
import com.company.shougo.databinding.DialogSelectTicketBinding;
import com.company.shougo.databinding.DialogShowQrcodeBinding;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.listener.OnCommendListener;
import com.company.shougo.listener.OnEditListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DialogManager {

    private static AlertDialog dialog,loadDialog;

    /**
     * 提示對話框
     *
     * @param message     訊息
     * @param posBtn      右邊按鍵文字
     * @param posListener 右邊按鍵事件
     * @param negBtn      中間按鍵文字
     * @param negListener 中間按鍵事件
     * @param neuBtn      左邊按鍵文字
     * @param neuListener 左邊按鍵事件
     */
    public static void showBaseDialog(
            final AppCompatActivity activity
            , final String message
            , final String posBtn, final DialogInterface.OnClickListener posListener
            , final String negBtn, final DialogInterface.OnClickListener negListener
            , final String neuBtn, final DialogInterface.OnClickListener neuListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDialog(activity);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(posBtn, posListener)
                        .setNegativeButton(negBtn, negListener)
                        .setNeutralButton(neuBtn, neuListener);
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static void showBaseDialog(
            final AppCompatActivity activity
            , final String message
            , final String posBtn, final DialogInterface.OnClickListener posListener
            , final String negBtn, final DialogInterface.OnClickListener negListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDialog(activity);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(posBtn, posListener)
                        .setNegativeButton(negBtn, negListener);
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static void showBaseDialog(
            final Activity activity
            , final String message
            , final String posBtn
            , final DialogInterface.OnClickListener posListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDialog(activity);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(posBtn, posListener);
                dialog = builder.create();
                dialog.show();
            }
        });
    }


    public static void showConfirmDialog(
            final AppCompatActivity activity
            , final String title
            , final String content
            , final View.OnClickListener onClickListener
            ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogConfirmBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_confirm
                        , null
                        , false
                );

                binding.title.setText(title);
                binding.content.setText(content);
                binding.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onClickListener!=null) {
                            onClickListener.onClick(v);
                        }
                    }
                });

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.7);
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public static void showTicketDialog(
            final AppCompatActivity activity
            , final String title
            , final List<CouponData> list
            , final OnAdapterItemListener onAdapterItemListener
            ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogSelectTicketBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_select_ticket
                        , null
                        , false
                );

                binding.title.setText(title);
                binding.content.setText(String.valueOf(list.size()) + binding.getRoot().getResources().getString(R.string.s_ticket));

                binding.list.setLayoutManager(new LinearLayoutManager(activity));
                TicketAdapter adapter = new TicketAdapter(list);
                binding.list.setAdapter(adapter);

                adapter.setOnAdapterItemListener(new OnAdapterItemListener() {
                    @Override
                    public void onItemClick(int pos) {
                        dialog.dismiss();

                        if (onAdapterItemListener!=null){
                            onAdapterItemListener.onItemClick(pos);
                        }
                    }
                });

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.7);
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public static void showTravelList(
            final AppCompatActivity activity
            , final List<FavoriteTravelData> list
            , final OnAdapterItemListener onAdapterItemListener
    ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogAddTravelBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_add_travel
                        , null
                        , false
                );

                binding.list.setLayoutManager(new LinearLayoutManager(activity));
                AddTravelListAdapter adapter = new AddTravelListAdapter(list);
                binding.list.setAdapter(adapter);

                adapter.setOnAdapterItemListener(new OnAdapterItemListener() {
                    @Override
                    public void onItemClick(int pos) {
                        dialog.dismiss();

                        if (onAdapterItemListener!=null){
                            onAdapterItemListener.onItemClick(pos);
                        }
                    }
                });

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });
    }

    public static void showEditDialog(
            final AppCompatActivity activity
            , final String title
            , final OnEditListener onClickListener
    ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogEditBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_edit
                        , null
                        , false
                );

                binding.title.setText(title);

                binding.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onClickListener!=null) {
                            onClickListener.onEdit(binding.content.getText().toString());
                        }
                    }
                });

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.7);
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public static void showSelectDialog(
            final AppCompatActivity activity
            , final String title
            , final String content
            , final View.OnClickListener onClickListener
            , final View.OnClickListener onCancelListener
            ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogSelectBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_select
                        , null
                        , false
                );

                binding.title.setText(title);
                binding.content.setText(content);
                binding.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onClickListener!=null) {
                            onClickListener.onClick(v);
                        }
                    }
                });

                binding.no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onCancelListener!=null){
                            onCancelListener.onClick(v);
                        }
                    }
                });

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.7);
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public static void showEvaListDialog(
            final AppCompatActivity activity
            , final List<StoreCommendData> list
            , final String title
            , final OnCommendListener onCommendListener
    ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogEvaListBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_eva_list
                        , null
                        , false
                );

                binding.title.setText(title);

                binding.allEva.setText(
                        String.valueOf(list.size())
                        + binding.getRoot().getResources().getString(R.string.s_eva)
                        );

                double score = 0;
                for (int i=0;i<list.size();i++){
                    score+=list.get(i).getStar();
                }
                if (list.size()>0){
                    score = score / list.size();
                }

                binding.score.setText(String.format("%.1f", score));

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                EvaAdapter evaAdapter = new EvaAdapter(list);
                binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                binding.list.setAdapter(evaAdapter);

                evaAdapter.setOnAdapterItemListener(new OnAdapterItemListener() {
                    @Override
                    public void onItemClick(int pos) {
                        dialog.dismiss();

                        SystemClock.sleep(300);

                        showEvaDialog(
                                activity
                                , title
                                , list.get(pos)
                                , onCommendListener
                        );
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.8);
                int height = (int)(dm.heightPixels * 0.7);
                if (binding.getRoot().getHeight()>height) {
                    lp.height = height;
                }
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public static void showQrcodeDialog(
            final AppCompatActivity activity
            , final String code
    ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                DialogShowQrcodeBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_show_qrcode
                        , null
                        , false
                );

                try {
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bm = encoder.encodeBitmap(
                            code + Parameter.QRCODE_SPLITE + UserManager.getInstance().getTokenData().getToken()
                            , BarcodeFormat.QR_CODE, (int)Calculation.convertDpToPixel(180, activity)
                            , (int)Calculation.convertDpToPixel(180, activity)
                    );

                    if(bm != null) {
                        binding.img.setImageBitmap(bm);
                    }
                } catch (Exception e) {
                    Log.e("showQrcodeDialog", e.toString());
                }

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });
    }


    public static void showEvaDialog(
            final AppCompatActivity activity
            , final String title
            , final StoreCommendData commendData
            , final OnCommendListener onCommendListener
            ) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);

                final int[] star = {0};

                DialogGiveEvaBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity)
                        , R.layout.dialog_give_eva
                        , null
                        , false
                );

                binding.title.setText(String.format(activity.getResources().getString(R.string.give_who_eva), title));

                binding.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                binding.score1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        star[0] = 1;
                        setScore(star[0], binding);
                        return false;
                    }
                });

                binding.score2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        star[0] = 2;
                        setScore(star[0], binding);
                        return false;
                    }
                });

                binding.score3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        star[0] = 3;
                        setScore(star[0], binding);
                        return false;
                    }
                });

                binding.score4.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        star[0] = 4;
                        setScore(star[0], binding);
                        return false;
                    }
                });

                binding.score5.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        star[0] = 5;
                        setScore(star[0], binding);
                        return false;
                    }
                });

                if (commendData!=null) {
                    star[0] = (int) commendData.getStar();

                    binding.edit.setText(commendData.getComments());

                    setScore(commendData.getStar(), binding);
                }

                binding.send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCommendListener!=null){

                            dialog.dismiss();

                            onCommendListener.onSend(
                                    binding.edit.getText().toString()
                                    , star[0]
                            );
                        }
                    }
                });

                builder.setView(binding.getRoot());
                dialog = builder.create();
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = (int)(dm.widthPixels * 0.8);
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    private static void setScore(double score, DialogGiveEvaBinding binding){
        binding.score1.setImageResource(R.drawable.btn_score_default);
        binding.score2.setImageResource(R.drawable.btn_score_default);
        binding.score3.setImageResource(R.drawable.btn_score_default);
        binding.score4.setImageResource(R.drawable.btn_score_default);
        binding.score5.setImageResource(R.drawable.btn_score_default);

        switch ((int) score){
            case 5:
                binding.score5.setImageResource(R.drawable.btn_score_clicked);
            case 4:
                binding.score4.setImageResource(R.drawable.btn_score_clicked);
            case 3:
                binding.score3.setImageResource(R.drawable.btn_score_clicked);
            case 2:
                binding.score2.setImageResource(R.drawable.btn_score_clicked);
            case 1:
                binding.score1.setImageResource(R.drawable.btn_score_clicked);
                break;
        }
    }

    public static void showPhotoDialog(final BaseActivity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDialog(activity);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.select_camera_album))
                        .setSingleChoiceItems(
                                new String[]{
                                        activity.getString(R.string.camera)
                                        , activity.getString(R.string.album)
                                }
                                , -1
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancelDialog(activity);

                                        switch (which){
                                            case 0:
                                                CameraManager.checkCameraPer(activity);
                                                break;
                                            case 1:
                                                CameraManager.checkAlbumPer(activity);
                                                break;
                                        }
                                    }
                                }
                        );

                dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * 關閉對話框
     */
    public static void cancelDialog(Activity activity) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 顯示LOADING畫面
     */
    public static void showLoadDialog(final AppCompatActivity activity) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                cancelLoadDialog(activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                ProgressBar bar = new ProgressBar(activity);
                bar.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
                builder.setView(bar);
                loadDialog = builder.create();
                Objects.requireNonNull(loadDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                Objects.requireNonNull(loadDialog.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                loadDialog.show();
            }
        });
    }

    /**
     * 關閉LOADING畫面
     */
    public static void cancelLoadDialog(AppCompatActivity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadDialog != null) loadDialog.dismiss();
            }
        });
    }

    private static BottomSheetDialog bottomSheetDialog;
    public static void showBottomSheet(
            BaseActivity activity
            , View v
    ){

        cancelBottomSheet(activity);

        bottomSheetDialog = new BottomSheetDialog(activity);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        bottomSheetDialog.getBehavior().setPeekHeight(height);

        bottomSheetDialog.setContentView(v);

        bottomSheetDialog.show();
    }

    public static void cancelBottomSheet(
            BaseActivity activity
    ){
        if (bottomSheetDialog!=null && bottomSheetDialog.isShowing()){
            bottomSheetDialog.dismiss();
        }
    }
}
