package com.company.shougo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.AdapterTravelBinding;
import com.company.shougo.listener.OnTravelListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.ViewHolder> {

    private List<FavoriteTravelData> list;

    private OnTravelListener onTravelListener;

    public void setOnTravelListener(OnTravelListener onTravelListener){
        this.onTravelListener = onTravelListener;
    }

    public TravelAdapter(List<FavoriteTravelData> list){
        this.list = list;
    }

    public List<FavoriteTravelData> getList(){
        return list;
    }

    public void remove(int pos){
        list.remove(list.get(pos));

        notifyItemRemoved(pos);
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTravelBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_travel
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.title.setText(list.get(position).getName());

        String date = list.get(position).getDate_added().split(" ")[0].replace("-", ".");

        holder.binding.date.setText(
                holder.binding.getRoot().getResources().getString(R.string.create_date)
                + " "
                + date
        );

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTravelListener!=null){
                    onTravelListener.onDelete(position);
                }
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTravelListener!=null){
                    onTravelListener.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterTravelBinding binding;

        public ViewHolder(@NonNull AdapterTravelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
