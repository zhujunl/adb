package com.miaxis.face.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.miaxis.face.R;
import com.miaxis.face.view.custom.PreviewPictureEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ZJL
 * @date 2022/5/18 10:19
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PreviewPageAdapter extends BaseAdapter {
   private Context context;
   private List<PreviewPictureEntity> pathList;
   private PreViewListner preViewListner;
   private final String TAG="PreviewPageAdapter";

   public PreviewPageAdapter(Context context, List<PreviewPictureEntity> pathList, PreviewPageAdapter.PreViewListner preViewListner) {
      this.context = context;
      this.pathList = pathList;
      this.preViewListner = preViewListner;


   }

   @Override
   public int getCount() {
      if (pathList==null){
         return 0;
      }
      return pathList.size();
   }

   @Override
   public Object getItem(int i) {
      if (pathList==null){
         return null;
      }
      return pathList.get(i);
   }

   @Override
   public long getItemId(int i) {
      return i;
   }

   @Override
   public View getView(int i, View convertView, ViewGroup viewGroup) {
      ViewHolder holder;
      if (convertView == null) {
         convertView = LayoutInflater.from(context).inflate(R.layout.item_preview_page, null);
         holder = new ViewHolder(convertView);
         convertView.setTag(holder);
      } else {
         holder = (ViewHolder)convertView.getTag();
      }
      PreviewPictureEntity item = pathList.get(i);
      if (item != null) {
         final int pos=i;
         Glide.with(context).load(pathList.get(i).getPath()).into(holder.img);
         holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               preViewListner.onLongClick(pos);
               return false;
            }
         });

         holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               preViewListner.onClick(pos);
            }
         });
      }

      return convertView;
   }

   public List<PreviewPictureEntity> getPathList() {
      return pathList;
   }


   static class ViewHolder{
      @BindView(R.id.img)
      ImageView img;

      ViewHolder(View view) {
         ButterKnife.bind(this, view);
      }
   }

   public interface PreViewListner{
      void onClick(int position);
      void onLongClick(int pos);
   }
}
