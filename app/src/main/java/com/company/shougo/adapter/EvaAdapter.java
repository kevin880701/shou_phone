package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.shougo.R;
import com.company.shougo.data.StoreCommendData;
import com.company.shougo.databinding.AdapterEvaBinding;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.mamager.UserManager;

import java.util.List;

public class EvaAdapter extends RecyclerView.Adapter<EvaAdapter.ViewHolder> {

    private List<StoreCommendData> list;

    private OnAdapterItemListener onAdapterItemListener;

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener){
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public EvaAdapter(List<StoreCommendData> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEvaBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_eva
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.score.setText(String.format("%.1f", list.get(position).getStar()));
        holder.binding.content.setText(list.get(position).getComments());
        holder.binding.name.setText(list.get(position).getCustomer_name());

        if (list.get(position).getCustomer_id()== UserManager.getInstance().getUserData().getCustomer_id()){
            holder.binding.edit.setVisibility(View.VISIBLE);
        }else{
            holder.binding.edit.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterItemListener!=null){
                    onAdapterItemListener.onItemClick(position);
                }
            }
        });

        Glide.with(holder.binding.getRoot())
            .load(list.get(position).getCustomer_image())
            .apply(
                    RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.icon_memberhoto)
                    .error(R.drawable.icon_memberhoto)
            )
            .into(holder.binding.headImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterEvaBinding binding;

        public ViewHolder(@NonNull AdapterEvaBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

}
