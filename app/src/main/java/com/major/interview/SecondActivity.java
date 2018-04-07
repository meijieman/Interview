package com.major.interview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobeta.android.dslv.DragSortListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "tag_sa";
    public static final String SP_LIST = "sp_list";
    public static final String SP_RATES = "sp_rates";
    public static final String SP_TMP = "sp_tmp";

    private MyAdapter mAdapter;
    private SharedPreferences mSp;
    private Map<String, String> mRates;
    private DragSortListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setTitle(getString(R.string.label_title));
        mListView = findViewById(R.id.dslv_second);
        mSp = getSharedPreferences(SP_TMP, MODE_PRIVATE);
        init();
        initListener();
    }

    private void init() {
        // 获取汇率
        String rates = mSp.getString(SP_RATES, "");
        if (!rates.isEmpty()) {
            mRates = new Gson().fromJson(rates, new TypeToken<Map<String, String>>() {}.getType());
            Log.i(TAG, "onCreate: rates " + mRates);
        }
        List<Rate> datas = null;
        String json = mSp.getString(SP_LIST, "");
        if (!json.isEmpty()) {
            datas = new Gson().fromJson(json, new TypeToken<List<Rate>>() {}.getType());
        } else {
            getRates();
        }
        mAdapter = new MyAdapter(datas);
        mListView.setAdapter(mAdapter);
    }

    private void initListener() {
        mListView.setDragSortListener(new DragSortListView.DragSortListener() {
            @Override
            public void drag(int from, int to) {
            }
            @Override
            public void drop(int from, int to) {
                Log.i(TAG, "drop: " + from + ", " + to);
                // 排序
                mAdapter.drop(from, to);
                List<Rate> datas = mAdapter.getDatas();
                String json = new Gson().toJson(datas);
                mSp.edit().putString(SP_LIST, json).apply();
                Log.i(TAG, "drop: sort " + datas);
            }
            @Override
            public void remove(int which) {
            }
        });
        mAdapter.setTextChangedListener(new MyAdapter.TextChangedListener() {
            @Override
            public void onTextChanged(EditText et, int pos, String currency, String equal) {
                Log.i(TAG, "onTextChanged: " + pos + ", " + equal);
                List<Rate> datas = calcRate(currency, equal);
                mAdapter.setDatas(datas);
            }
        });
    }

    // 计算汇率
    private List<Rate> calcRate(String currency, String equal) {
        List<Rate> datas = new ArrayList<>(mAdapter.getDatas());

        if (equal.isEmpty()) {
            equal = "0";
        }
        String rate = mRates.get(currency);
        BigDecimal equalbd = new BigDecimal(equal);
        BigDecimal ratebd = new BigDecimal(rate);
        BigDecimal divide = equalbd.divide(ratebd, RoundingMode.HALF_DOWN).setScale(5, RoundingMode.HALF_DOWN); // 获取到的 USD
        for (Rate rate1 : datas) {
            if (currency.equals(rate1.getCurrency())) {
                rate1.setEqual(equal);
            } else {
                BigDecimal multiply = divide.multiply(new BigDecimal(mRates.get(rate1.getCurrency())));
                rate1.setEqual(multiply.setScale(5, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
            }
        }
        return datas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                mAdapter.setDatas(null);
                getRates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRates() {
        Call<String> stringCall = ApiFactory.getInstance().getApiService()
                .request("https://api.fixer.io/latest?base=USD");
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.i(TAG, "onResponse: " + body);
                Map<String, String> map = parseJson(body);
                // 保存汇率
                mSp.edit().putString(SP_RATES, new Gson().toJson(map)).apply();
                List<Rate> list = transform(map);
                mAdapter.setDatas(list);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
            }
        });
    }

    @NonNull
    private List<Rate> transform(Map<String, String> map) {
        // 更新 adapter
        List<Rate> list = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            list.add(new Rate(next.getKey(), next.getValue()));
        }
        return list;
    }

    @NonNull
    private Map<String, String> parseJson(String body) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("rates")) {
                JSONObject rates = obj.getJSONObject("rates");
                String[] split = rates.toString().replace("{", "").replace("}", "").replace("\"", "").split(",");
                for (String s : split) {
                    String[] split1 = s.split(":");
                    map.put(split1[0], split1[1]);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "onResponse: " + e);
            e.printStackTrace();
        }
        return map;
    }
}
