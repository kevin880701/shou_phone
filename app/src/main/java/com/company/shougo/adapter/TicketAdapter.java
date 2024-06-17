package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.AdapterTicketBinding;
import com.company.shougo.listener.OnAdapterItemListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<CouponData> list;

    private OnAdapterItemListener onAdapterItemListener;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener){
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public TicketAdapter(List<CouponData> list){
        this.list = list;
    }

    public List<CouponData> getList(){
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTicketBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_ticket
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.name.setText(list.get(position).getName());
        try {
            holder.binding.date.setText(
                    sdf.format(sdf.parse(list.get(position).getDate_end()))
                    + holder.binding.getRoot().getResources().getString(R.string.date_end)
            );
        }catch (Exception e){
            e.toString();
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

        private AdapterTicketBinding binding;

        public ViewHolder(@NonNull AdapterTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
