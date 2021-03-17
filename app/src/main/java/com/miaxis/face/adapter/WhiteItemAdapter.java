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
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.util.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xu.nan on 2017/5/26.
 */

public class WhiteItemAdapter extends BaseAdapter {

    private List<WhiteItem> whiteItemList;
    private Context context;
    private OnDelListener delListener;

    public WhiteItemAdapter(Context context) {
        this.context = context;
    }

    public void setWhiteItemList(List<WhiteItem> whiteItemList) {
        this.whiteItemList = whiteItemList;
    }

    public void setDelListener(OnDelListener delListener) {
        this.delListener = delListener;
    }

    @Override
    public int getCount() {
        if (whiteItemList == null) {
            return 0;
        }
        return whiteItemList.size();
    }

    @Override
    public WhiteItem getItem(int i) {
        if (whiteItemList == null) {
            return null;
        }
        return whiteItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_white, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        WhiteItem item = whiteItemList.get(i);
        if (item != null) {
            holder.tvName.setText(item.getName());
            holder.tvCardNo.setText(item.getCardNo());
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
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_cardNo)
        TextView tvCardNo;
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
