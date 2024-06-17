package com.company.shougo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.R;
import com.company.shougo.databinding.AdapterLastlySearchBinding;
import com.company.shougo.listener.OnLastlySearchListener;

import java.util.List;

public class LastlySearchAdapter extends RecyclerView.Adapter<LastlySearchAdapter.ViewHolder> {

    private List<String> list;

    private OnLastlySearchListener onLastlySearchListener;

    public void setOnLastlySearchListener(OnLastlySearchListener onLastlySearchListener){
        this.onLastlySearchListener = onLastlySearchListener;
    }

    public void removeItem(int pos){
        list.remove(list.get(pos));
        notifyItemRemoved(pos);
        notifyDataSetChanged();
    }

    public LastlySearchAdapter(List<String> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLastlySearchBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_lastly_search
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.title.setText(list.get(position));

        holder.binding.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLastlySearchListener!=null){
                    onLastlySearchListener.onClick(position);
                }
            }
        });

        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLastlySearchListener!=null){
                    onLastlySearchListener.onRemove(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterLastlySearchBinding binding;

        public ViewHolder(@NonNull AdapterLastlySearchBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

}
