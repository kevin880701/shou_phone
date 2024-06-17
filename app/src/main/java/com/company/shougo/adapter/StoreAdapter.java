package com.company.shougo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.company.shougo.R;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.AdapterImageBinding;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.Calculation;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<StoreData> list;

    private OnFavItemListener onFavItemListener;

    private boolean isHor = false;

    public void setOnFavItemListener(OnFavItemListener onFavItemListener){
        this.onFavItemListener = onFavItemListener;
    }

    public StoreAdapter(List<StoreData> list, boolean isHor){
        this.list = list;
        this.isHor = isHor;
    }

    public List<StoreData> getList(){
        return list;
    }

    public void update(int pos, StoreData storeData){
        list.set(pos, storeData);

        notifyItemChanged(pos);
    }

    public void remove(int pos){
        list.remove(list.get(pos));

        notifyItemRemoved(pos);
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterImageBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_image
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (isHor) {
            ViewGroup.LayoutParams params = holder.binding.getRoot().getLayoutParams();
            params.width = (int) Calculation.convertDpToPixel(140, holder.binding.getRoot().getContext());
            holder.binding.getRoot().setLayoutParams(params);
        }

        if (list.get(position).getStatus()==1){
            holder.binding.storeClose.setVisibility(View.GONE);
        }else {
            if (list.get(position).getStatus()==2) {
                holder.binding.storeCloseText.setText(holder.binding.getRoot().getResources().getString(R.string.already_close));
            }

            holder.binding.storeClose.setVisibility(View.VISIBLE);
        }

        holder.binding.usedImg.setVisibility(View.GONE);
        holder.binding.overImg.setVisibility(View.GONE);
        holder.binding.content.setVisibility(View.GONE);

        if(list.get(position).getDesc() != null){
            if(list.get(position).getDesc().size()>0){
                if(list.get(position).getDesc().get(0).getImage() !=null){

                    Glide.with(holder.itemView.getContext())
                            .load(list.get(position).getDesc().get(0).getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .transform(new CenterCrop(), new RoundedCorners(10))
                            .into(holder.binding.img);

                }
            }
        }


        holder.binding.title.setText(list.get(position).getStore_name());
        holder.binding.content.setText(list.get(position).getDescription());

        if (list.get(position).isFavorites()){
            holder.binding.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
        }else {
            holder.binding.favorite.setImageResource(R.drawable.home_btn_collect_default);
        }

        holder.binding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFavItemListener!=null){
                    onFavItemListener.onItemClick(position);
                }
            }
        });

        holder.binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFavItemListener!=null){
                    onFavItemListener.onFavClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterImageBinding binding;

        public ViewHolder(@NonNull AdapterImageBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
