package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.AdapterTravelItemBinding;
import com.company.shougo.listener.OnTravelListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.UserManager;

import java.util.List;

public class TravelItemAdapter extends RecyclerView.Adapter<TravelItemAdapter.ViewHolder> {

    private List<TravelData> list;

    private OnTravelListener onTravelListener;

    public void setOnTravelListener(OnTravelListener onTravelListener){
        this.onTravelListener = onTravelListener;
    }

    public TravelItemAdapter(List<TravelData> list){
        this.list = list;
    }

    public List<TravelData> getList(){
        return list;
    }

    public void delete(int pos){
        list.remove(list.get(pos));
        notifyItemRemoved(pos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTravelItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_travel_item
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.pos.setText(String.valueOf(position+1));
        String name = "";
        String des = "";
        switch (list.get(position).getType()){
            case 0:
                name = list.get(position).getCoupon_name();
                des = holder.binding.getRoot().getResources().getString(R.string.ticket);
                break;
            case 1:
                name = list.get(position).getVendor_name();
                des = holder.binding.getRoot().getResources().getString(R.string.type_store);
                break;
            case 2:
                name = list.get(position).getVendor_name();
                des = holder.binding.getRoot().getResources().getString(R.string.park);
                break;
        }

        float dis = 0.0f;
        double myLat = GPSManager.getInstance(holder.binding.getRoot().getContext()).getLat();
        double myLng = GPSManager.getInstance(holder.binding.getRoot().getContext()).getLng();

        if (myLat!=0 && myLng!=0){
            dis = GPSManager.getInstance(holder.binding.getRoot().getContext())
                    .getDistance(myLat, myLng, list.get(position).getLat(), list.get(position).getLng());
        }

        if (dis>0){
            dis = dis / 1000;
        }

        holder.binding.name.setText(name);
        holder.binding.type.setText(des);;
        holder.binding.location.setText(String.format("%.1f", dis) + holder.binding.getRoot().getResources().getString(R.string.miles));

        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTravelListener!=null){
                    onTravelListener.onEdit(position);
                }
            }
        });

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

        private AdapterTravelItemBinding binding;

        public ViewHolder(@NonNull AdapterTravelItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

}
