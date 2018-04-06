package com.major.interview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "tag_ma";

    private EditText mInput;
    private ImageView mImage;

    @SuppressLint("WrongViewCast")
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
                    Toast.makeText(MainActivity.this, "非法网址", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiFactory.getInstance().getApiService()
                        .request(url)
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                                String body = response.body();
                                Log.i(TAG, "onResponse: " + body);
                                String[] imgs = RegExUtil.getImgs(body);
                                Log.i(TAG, "onResponse: imgs " + Arrays.toString(imgs));
                                if (imgs != null && imgs.length > 0) {
                                    Glide.with(MainActivity.this).load(imgs[0]).into(mImage);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t);
                                Toast.makeText(MainActivity.this, "onFailure " + t, Toast.LENGTH_LONG).show();
                            }
                        });
                break;
            default:
                break;
        }
    }

}
