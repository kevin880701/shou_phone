package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.data.CategoryData;
import com.company.shougo.databinding.AdapterCheckBinding;
import com.company.shougo.listener.OnAdapterItemListener;

import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {

    private List<CategoryData> list;

    private OnAdapterItemListener onAdapterItemListener;

    public void setOnAdapterItemListener(OnAdapterItemListener onAdapterItemListener){
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public CheckAdapter(List<CategoryData> list){
        this.list = list;
    }

    public List<CategoryData> getList(){
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCheckBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_check
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.name.setText(list.get(position).getName());

        holder.binding.check.setChecked(list.get(position).isSelect());

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterItemListener!=null){
                    onAdapterItemListener.onItemClick(position);
                }

//                holder.binding.check.setChecked(!holder.binding.check.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterCheckBinding binding;

        public ViewHolder(@NonNull AdapterCheckBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }

}
