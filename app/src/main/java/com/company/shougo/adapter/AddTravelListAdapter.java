package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.databinding.AdapterAddTravelListBinding;
import com.company.shougo.listener.OnAdapterItemListener;

import java.util.List;

public class AddTravelListAdapter extends RecyclerView.Adapter<AddTravelListAdapter.ViewHolder> {

    private List<FavoriteTravelData> list;

    private OnAdapterItemListener onAdapterItemListener;

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener){
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public AddTravelListAdapter(List<FavoriteTravelData> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAddTravelListBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_add_travel_list
                , parent
                , false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.title.setText(list.get(position).getName());

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

        private AdapterAddTravelListBinding binding;

        public ViewHolder(@NonNull AdapterAddTravelListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

}
