package com.miaxis.face.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.bean.LocalFeature;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xu.nan on 2017/5/26.
 */

public class LocalFeatureItemAdapter extends BaseAdapter {

    private List<LocalFeature> localFeatureList;
    private Context context;
    private OnDelListener delListener;

    public LocalFeatureItemAdapter(Context context) {
        this.context = context;
    }

    public void setLocalFeatureList(List<LocalFeature> localFeatureList) {
        this.localFeatureList = localFeatureList;
    }

    public void setDelListener(OnDelListener delListener) {
        this.delListener = delListener;
    }

    @Override
    public int getCount() {
        if (localFeatureList == null) {
            return 0;
        }
        return localFeatureList.size();
    }

    @Override
    public LocalFeature getItem(int i) {
        if (localFeatureList == null) {
            return null;
        }
        return localFeatureList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_local_feature, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        LocalFeature item = localFeatureList.get(i);
        if (item != null) {
            holder.tvFileName.setText(item.getName());
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delListener != null) {
                        delListener.onDelete(i);
                    }
                }
            });

        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_file_name)
        TextView tvFileName;
        @BindView(R.id.btn_delete)
        Button btnDelete;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnDelListener {
        void onDelete(int position);
    }
}
