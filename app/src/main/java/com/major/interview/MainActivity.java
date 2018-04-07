package com.major.interview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hongfans.common.api.exception.ApiException;
import com.hongfans.common.rx.RxSchedulers;
import com.hongfans.common.rx.RxSubscriber;
import com.major.interview.api.ApiFactory;
import com.major.interview.util.RegExUtil;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "tag_ma";

    private EditText mInput;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInput = findViewById(R.id.et_main_input);
        mImage = findViewById(R.id.iv_main);
        findViewById(R.id.btn_main_jump).setOnClickListener(this);
        findViewById(R.id.btn_main_parse).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_jump:
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.btn_main_parse:
                String url = mInput.getText().toString();
                if (!RegExUtil.isUrl(url)) {
                    Toast.makeText(MainActivity.this, R.string.label_tip_illegal_url, Toast.LENGTH_SHORT).show();
                    return;
                }
                Subscription subscribe = ApiFactory.getInstance().getApiService()
                        .request(url)
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                String[] imgs = RegExUtil.getImgs(s);
                                Log.i(TAG, "call: imgs " + Arrays.toString(imgs));
                                if (imgs != null && imgs.length > 0) {
                                    return Observable.just(imgs[0]);
                                }
                                return Observable.empty();
                            }
                        })
                        .compose(RxSchedulers.<String>switchThird())
                        .subscribe(new RxSubscriber<String>() {
                            @Override
                            public void onNext(String s) {
                                Glide.with(MainActivity.this).load(s).into(mImage);
                            }
                            @Override
                            public void onError(ApiException e) {
                                Log.e(TAG, "onError: " + e);
                                Toast.makeText(MainActivity.this, "onError " + e, Toast.LENGTH_LONG).show();
                            }
                        });
                break;
            default:
                break;
        }
    }

}
