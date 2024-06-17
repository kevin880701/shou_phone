package com.company.shougo.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.NotifyData;
import com.company.shougo.databinding.AdapterNotifyBinding;
import com.company.shougo.listener.OnAdapterItemListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    private List<NotifyData> list;

    private OnAdapterItemListener onAdapterItemListener;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener){
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public List<NotifyData> getList(){
        return list;
    }

    public NotifyAdapter(List<NotifyData> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterNotifyBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_notify
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.date.setText(list.get(position).getDateTime());
        holder.binding.content.setText(list.get(position).getMsg());

        if (list.get(position).getRead()==0){
            holder.binding.content.setTextColor(Color.parseColor("#000000"));
        }else{
            holder.binding.content.setTextColor(Color.parseColor("#a6a6a6"));
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterItemListener!=null){
                    onAdapterItemListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterNotifyBinding binding;

        public ViewHolder(@NonNull AdapterNotifyBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

}
