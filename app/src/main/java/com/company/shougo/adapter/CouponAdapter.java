package com.company.shougo.adapter;

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
import com.company.shougo.data.CouponData;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.AdapterImageBinding;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.Calculation;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private List<CouponData> list;

    private OnFavItemListener onFavItemListener;

    private boolean isHor = false, isCheckUsed = false;

    public void setOnFavItemListener(OnFavItemListener onFavItemListener){
        this.onFavItemListener = onFavItemListener;
    }

    public CouponAdapter(List<CouponData> list, boolean isHor, boolean isCheckUsed){
        this.list = list;
        this.isHor = isHor;
        this.isCheckUsed = isCheckUsed;
    }

    public void update(int pos, CouponData couponData){
        list.set(pos, couponData);

        notifyItemChanged(pos);
    }

    public void remove(int pos){
        list.remove(list.get(pos));

        notifyItemRemoved(pos);
        notifyItemChanged(pos);
    }

    public List<CouponData> getList(){
        return list;
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

        holder.binding.usedImg.setVisibility(View.GONE);
        holder.binding.overImg.setVisibility(View.GONE);

        if (isCheckUsed) {
            if (list.get(position).getStatus() == 0) {
                holder.binding.usedImg.setVisibility(View.VISIBLE);
            } else if (list.get(position).getUse_status() == 0) {
                holder.binding.overImg.setVisibility(View.VISIBLE);
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getProfile_image())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transform(new CenterCrop(), new RoundedCorners(10))
                .into(holder.binding.img);

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
