package company.bigtree.bigtree;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import company.bigtree.bigtree.entity.User;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG="SplashActivity";

    SharedPreferences userInfo;//用于保存用户登录成功后的用户信息：密码(userPassword)，学号(userNumber) 姓名（userName）
    Global global;
    FinalHttp finalHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        global=(Global)getApplication();

        userInfo=getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String userNumber=userInfo.getString("userNumber","0");
        if (userNumber.equals("0")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, AnswerActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 1500);
        /*停1500毫秒*/
        }
        else {
            final String password=userInfo.getString("userPassword","0");
            finalHttp=new FinalHttp();
            AjaxParams params = new AjaxParams();
            params.put("method","login");
            params.put("userNumber",userNumber);
            params.put("userPassword", password);
            finalHttp.get(global.getServerUrl()
                    , params
                    , new AjaxCallBack<String>() {

                @Override
                public AjaxCallBack<String> progress(boolean progress, int rate) {
                    return super.progress(progress, rate);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                }

                @Override
                public void onSuccess(String s) {
                    super.onSuccess(s);
                    Gson gson=new Gson();
                    User user=gson.fromJson(s,User.class);
                    if (user.isSuccess()){
                        /*登录成功*/
                        SharedPreferences.Editor editor=userInfo.edit();
                        editor.putString("userNumber",userNumber);
                        editor.putString("userPassword",password);
                        editor.commit();
                    }
                    Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }
}
