package com.alert.bikeornot.preferences;

import android.content.Context;

import com.alert.bikeornot.utilities.PrefUtils;


public class PrefItem {

    private Context mContext;
    private int mIconRes;
    private int mTitleRes;
    private String mPrefKey;
    private Object mDefaultValue;
    private OnClickListener mOnClickListener;
    private SubTitleGenerator mSubTitleGenerator;
    private boolean mRequired;

    public PrefItem(Context context, int iconRes, int titleRes, String prefKey, Object defaultValue, boolean required, OnClickListener clickListener, SubTitleGenerator subTitleGenerator) {
        this(context, iconRes, titleRes, prefKey, defaultValue, required);
        mOnClickListener = clickListener;
        mSubTitleGenerator = subTitleGenerator;
    }

    public PrefItem(Context context, int iconRes, int titleRes, String prefKey, Object defaultValue, boolean required, SubTitleGenerator subTitleGenerator) {
        this(context, iconRes, titleRes, prefKey, defaultValue, required);
        mSubTitleGenerator = subTitleGenerator;
    }

    public PrefItem(Context context, int iconRes, int titleRes, String prefKey, Object defaultValue, boolean required) {
        mContext = context;
        mIconRes = iconRes;
        mTitleRes = titleRes;
        mPrefKey = prefKey;
        mDefaultValue = defaultValue;
        mRequired = required;
    }

    public Object getValue() {
        if (mDefaultValue instanceof Integer) {
            return PrefUtils.get(mContext, mPrefKey, (Integer) mDefaultValue);
        } else if (mDefaultValue instanceof Long) {
            return PrefUtils.get(mContext, mPrefKey, (Long) mDefaultValue);
        } else if (mDefaultValue instanceof Boolean) {
            return PrefUtils.get(mContext, mPrefKey, (Boolean) mDefaultValue);
        } else {
            return PrefUtils.get(mContext, mPrefKey, mDefaultValue.toString());
        }
    }

    public void saveValue(Object value) {
        if (mDefaultValue instanceof Integer) {
            PrefUtils.save(mContext, mPrefKey, (Integer) value);
        } else if (mDefaultValue instanceof Long) {
            PrefUtils.save(mContext, mPrefKey, (Long) value);
        } else if (mDefaultValue instanceof Boolean) {
            PrefUtils.save(mContext, mPrefKey, (Boolean) value);
        } else {
            PrefUtils.save(mContext, mPrefKey, value.toString());
        }
    }

    public boolean isRequired() {
        return mRequired;
    }

    public void setRequired(boolean mRequired) {
        this.mRequired = mRequired;
    }

    public void clearValue() {
        PrefUtils.remove(mContext, mPrefKey);
    }

    public int getIconResource() {
        return mIconRes;
    }

    public String getTitle() {
        return mContext.getResources().getString(mTitleRes);
    }

    public String getPrefKey() {
        return mPrefKey;
    }

    public Object getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        mDefaultValue = defaultValue;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        mOnClickListener = clickListener;
    }

    public void setSubTitleGenerator(SubTitleGenerator subTitleGenerator) {
        mSubTitleGenerator = subTitleGenerator;
    }

    public String getSubTitle() {
        if (mSubTitleGenerator != null) {
            return mSubTitleGenerator.get(this);
        }
        return "";
    }

    public boolean isClickable() {
        return mOnClickListener != null;
    }

    public void onClick() {
        if (mOnClickListener != null)
            mOnClickListener.onClick(this);
    }

    public interface OnClickListener {
        public void onClick(PrefItem item);
    }

    public interface SubTitleGenerator {
        public String get(PrefItem item);
    }

}
