package com.company.shougo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.AdapterDescriptionBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder>{

    private final static String TAG = "DescriptionAdapter";

    private List<StoreData.Desc> list;

    private MainActivity activity;

    public DescriptionAdapter(List<StoreData.Desc> list, MainActivity activity){
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public DescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterDescriptionBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.adapter_description
                , parent
                , false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DescriptionAdapter.ViewHolder holder, int position) {
        if (list.get(position).getDescription()!=null && list.get(position).getDescription().length()>0) {
            holder.binding.description.setVisibility(View.VISIBLE);
            holder.binding.description.setText(list.get(position).getDescription());
        }else {
            holder.binding.description.setVisibility(View.GONE);
        }

        if (list.get(position).getImage()!=null && list.get(position).getImage().length()>0) {
            holder.binding.img.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(list.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .fitCenter()
                    .transform(new RoundedCorners(10))
                    .into(holder.binding.img);
        } else {
            holder.binding.img.setVisibility(View.GONE);
        }

        if (list.get(position).getYoutube()!=null && list.get(position).getYoutube().length()>0) {
            holder.binding.youtubePlayerView.setVisibility(View.VISIBLE);

            String url = list.get(position).getYoutube();

            activity.getLifecycle().addObserver(holder.binding.youtubePlayerView);

            holder.binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    super.onReady(youTubePlayer);
//                    Log.d(TAG,getYoutubeVideoId(url));
                    youTubePlayer.cueVideo(getYoutubeVideoId(url), 0);
                }
            });
        } else {
            holder.binding.youtubePlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private AdapterDescriptionBinding binding;

        public ViewHolder(@NonNull AdapterDescriptionBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }

    String getYoutubeVideoId(String url){
        String videoId = "1";
//        try {
//            if(url.contains("www.youtube.com")){
//                if(url.contains("/embed/")){
//                    url = url.replaceAll("/embed/", "/watch?v=");
//                }
//
//                URI uri = URI.create(url);
//                videoId = uri.getQuery();
//                videoId = videoId.substring(videoId.indexOf("v=")+2);
//
//                int ind = videoId.indexOf("?");
//                if(ind >0){
//                    videoId = videoId.substring(0,ind);
//                }
//
//                ind = videoId.indexOf("&");
//                if(ind >0){
//                    videoId = videoId.substring(0,ind);
//                }
//
//                ind = videoId.indexOf("\"");
//                if(ind >0){
//                    videoId = videoId.substring(0,ind);
//                }
//            }else if(url.contains("youtu.be")){
//                URI uri = URI.create(url);
//                videoId = uri.getPath().replaceAll("/", "");
//            }else {
//                return "1"; //錯誤的ID，會讓youtube顯示"這部影片無法使用"
//            }
//            return videoId;
//        } catch (Exception e) {
//            Log.e(TAG, "URL error:"+e.toString());
//            return "1";
//        }
        //使用regex抓取ID, 測試用網站regex101.com
        // 使用這組要取group1
        String regex = "^.*(?:http(?:s)?://)?(?:www.)?(?:m.)?(?:youtu.be/|youtube.com/(?:(?:watch)?\\?(?:.*&)?v(?:i)?=|(?:embed|v|vi|user)/))([^?&\"'> ]+)";
        // 使用這組要取group2
//        String regex = "^.*(youtu.be/|v/|u/\\w/|embed/|watch\\?v=|&v=)([^#&?\"' ]*).*";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(url);
        while(matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }
}
