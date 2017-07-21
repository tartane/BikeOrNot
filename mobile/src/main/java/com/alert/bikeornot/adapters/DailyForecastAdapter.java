package com.alert.bikeornot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.zetterstrom.com.forecast.models.DataPoint;

import com.alert.bikeornot.BikeManager;
import com.alert.bikeornot.R;
import com.alert.bikeornot.models.BikeOrNotResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecastAdapter extends RecyclerView.Adapter {

    public static final int TYPE_ENABLED = 1;
    public static final int TYPE_DISABLED = 2;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<DataPointItem> mItems;
    private OnItemClickListener mItemClickListener;

    public DailyForecastAdapter(Context context, ArrayList<DataPoint> dataPoints) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItems = new ArrayList<>();
        addItems(dataPoints);
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_ENABLED:
                v = mInflater.inflate(R.layout.row_forecast, parent, false);
                return new DailyForecastAdapter.ForeCastViewHolder(v);
            case TYPE_DISABLED:
            default:
                v = mInflater.inflate(R.layout.row_forecast, parent, false);
                return new DailyForecastAdapter.ForeCastViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ForeCastViewHolder foreCastHolder = (ForeCastViewHolder) holder;
        DataPoint dataPoint = getItem(position);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataPoint.getTime());
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        //TODO is this possible in other languages?
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(cal.getTime());
        foreCastHolder.lblDate.setText(dayOfWeek + ", " + formattedDate);


        BikeOrNotResponse response = BikeManager.BikeOrNotDaily(dataPoint);

        foreCastHolder.imgBikeStatus.setBackground(response.getBikeDrawable());
        foreCastHolder.lblStatusText.setText(response.getText());
        //TODO Fahrenheit or celsius should come from prefs.
        String temperature = "Max. " + String.valueOf(Math.round(dataPoint.getTemperatureMax())) + "°C";
        foreCastHolder.lblTemperature.setText(temperature);

    }

    @Override
    public int getItemViewType(int position) {
        if(mItems.get(position).isEnabled)
            return TYPE_ENABLED;
        else
            return TYPE_DISABLED;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public DataPoint getItem(int position) {
        return mItems.get(position).dataPoint;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, DataPoint dataPoint, int position);
    }

    public void setOnItemClickListener(DailyForecastAdapter.OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class ForeCastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imgBikeStatus)
        ImageView imgBikeStatus;

        @BindView(R.id.lblDate)
        TextView lblDate;

        @BindView(R.id.lblStatusText)
        TextView lblStatusText;

        @BindView(R.id.lblTemperature)
        TextView lblTemperature;

        public ForeCastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                int position = getAdapterPosition();
                mItemClickListener.onItemClick(view, getItem(position), position);
            }
        }
    }

    public void addItems(ArrayList<DataPoint> items) {
        if (items != null) {
            for (DataPoint item : items) {
                mItems.add(new DataPointItem(item));
            }
            notifyDataSetChanged();
        }
    }

    public void setItems(ArrayList<DataPoint> items) {
        mItems.clear();
        addItems(items);
    }

    public void clear(){
        mItems.clear();
        notifyDataSetChanged();
    }

    class DataPointItem {
        DataPoint dataPoint;
        boolean isEnabled = false;

        DataPointItem(DataPoint dataPoint) {
            this.dataPoint = dataPoint;
        }

        DataPointItem(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }
}
