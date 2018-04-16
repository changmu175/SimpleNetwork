package com.ycm.network.networkexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ycm.simple.network.NetworkClient;
import com.ycm.simple.network.callback.RequestListener;
import com.ycm.simple.network.exception.BaseException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkClient
                .post("user")
                .params("username", "changmu175")
                .execute(new RequestListener<UserResult>() {
                    @Override
                    public void onFailure(BaseException e) {

                    }

                    @Override
                    public void onSuccess(UserResult apiResult) {

                    }
                });
    }
}
