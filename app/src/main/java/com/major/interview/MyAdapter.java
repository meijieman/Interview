package com.major.interview;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2018/4/7 9:49
 */
public class MyAdapter extends BaseAdapter {

    private static final String TAG = "tag_my";

    private List<Rate> mData;
    private int mPos = -1;
    private int mIndex = -1;

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (after != 0) {
                mIndex = start + after;
            }
            if (count != 0) {
                mIndex = start;
            }
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Rate rate = getItem(mPos);
            String equal1 = s.toString();
            Log.d(TAG, "onTextChanged: " + rate.getEqual() + ", " + equal1 + ", pos " + mPos);
            if (mListener != null && !rate.getEqual().equals(equal1)) {
                mListener.onTextChanged(mPos, rate.getCurrency(), equal1);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    MyAdapter(List<Rate> data) {
        mData = data;
    }

    public void setDatas(List<Rate> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void drop(int from, int to) {
        Rate rate = mData.get(from);
        if (from > to) {
            mData.add(to, rate);
            mData.remove(++from);
        } else {
            mData.add(++to, rate);
            mData.remove(from);
        }
        notifyDataSetChanged();
    }

    public List<Rate> getDatas() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }
    @Override
    public Rate getItem(int i) {
        return mData.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.item_second, null);
        }
        TextView currency = view.findViewById(R.id.tv_item);
        final EditText equal = view.findViewById(R.id.et_item);
        final Rate rate = getItem(pos);
        currency.setText(rate.getCurrency());
        equal.setText(rate.getEqual());
        equal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "onFocusChange: " + hasFocus);
                if (hasFocus) {
                    equal.addTextChangedListener(mWatcher);
                    mPos = pos;
                } else {
                    equal.removeTextChangedListener(mWatcher);
                }
            }
        });
        // 请求焦点
        if (mPos == pos) {
            equal.setFocusable(true);
            equal.setFocusableInTouchMode(true);
            equal.requestFocus();
            equal.setSelection(mIndex == -1 ? equal.getText().length() : mIndex);
        } else {
            equal.clearFocus();
        }
        return view;
    }

    private TextChangedListener mListener;

    public void setTextChangedListener(TextChangedListener listener) {
        mListener = listener;
    }

    public interface TextChangedListener {
        void onTextChanged(int pos, String currency, String equal);
    }
}
