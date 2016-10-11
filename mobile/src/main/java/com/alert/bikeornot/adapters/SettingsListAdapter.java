package com.alert.bikeornot.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alert.bikeornot.R;
import com.alert.bikeornot.preferences.PrefItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mItems;
    final int NORMAL = 0, HEADER = 1;

    public SettingsListAdapter(List<Object> items) {
        mItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subheader, parent, false);
                return new SettingsListAdapter.HeaderHolder(v);
            case NORMAL:
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_icon_twoline_item, parent, false);
                return new SettingsListAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == NORMAL) {
            ViewHolder itemViewHolder = (ViewHolder) viewHolder;
            PrefItem item = (PrefItem) mItems.get(position);
            itemViewHolder.itemView.setClickable(item.isClickable());
            itemViewHolder.icon.setImageResource(item.getIconResource());
            itemViewHolder.text1.setText(item.getTitle() + (item.isRequired() ? "*" : ""));
            itemViewHolder.text2.setText(item.getSubTitle());

            if (item.getDefaultValue() instanceof Boolean) {
                itemViewHolder.checkBox.setVisibility(View.VISIBLE);
                itemViewHolder.checkBox.setChecked((boolean) item.getValue());
            } else {
                itemViewHolder.checkBox.setVisibility(View.GONE);
            }
        } else if (getItemViewType(position) == HEADER) {
            HeaderHolder headerViewHolder = (HeaderHolder) viewHolder;
            headerViewHolder.itemView.setText((String) mItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof String) {
            return HEADER;
        }
        return NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView;
        @Bind(android.R.id.icon)
        ImageView icon;
        @Bind(android.R.id.text1)
        TextView text1;
        @Bind(android.R.id.text2)
        TextView text2;
        @Bind(android.R.id.checkbox)
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getPosition();
            PrefItem item = (PrefItem) mItems.get(position);
            item.onClick();
        }

    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        TextView itemView;

        public HeaderHolder(View itemView) {
            super(itemView);
            this.itemView = (TextView) itemView;
        }

    }

}