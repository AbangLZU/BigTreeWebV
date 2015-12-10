package company.bigtree.bigtree;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.w3c.dom.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements InputDialogFragment.InfoSendListener {

    private ListenFromService listenFromService;
    private Global global;

    InputDialogFragment inputDialogFragment;

    SharedPreferences userInfo;//用于保存用户登录成功后的用户信息：密码(userPassword)，学号(userNumber), IP地址(ipAddress)
    SharedPreferences classInfo; //isClassStart(boolean)目前是否上课 countTime(int)总时间 lastTime(long)离线的时间点

    ResideMenu resideMenu;
    private ResideMenuItem itemUserLogin;
    //private ResideMenuItem itemAlone;
    private ResideMenuItem itemAbourUs;
    private ResideMenuItem itemResult;
    private ResideMenuItem itemSign;
    //private ResideMenuItem itemSendResult;
    /*private ResideMenuItem itemLogout;*/

    private String[] banners={"有志始知蓬莱近，无为总觉咫尺远","志之所趋，无远勿届，穷山复海不能限也；志之所向，无坚不摧。"
            ,"在年轻人的颈项上，没有什么东西能比事业心这颗灿烂的宝珠。","器大者声必闳，志高者意必远。"
            ,"志坚者，功名之柱也。登山不以艰险而止，则必臻乎峻岭。"};
    private int currentBanner=0;



    /*************控件注解**************/

    @Bind(R.id.standard_toolbar_title)
    TextView toolbarTitle;

    @Bind(R.id.id_start_answer)
    BootstrapButton startAnswer;

    @Bind(R.id.bannerImageView)
    TextView bannerImage;

    @Bind(R.id.left_quot)
    TextView leftQuot;

    @Bind(R.id.right_quot)
    TextView rightQuot;

    @OnClick(R.id.send_result)
    public void clickSendResult(){
        sendMsgToSer("3=1");
    }

    @OnClick(R.id.id_start_answer)
    public void clickStartAnswer(){
        if (classInfo.getBoolean("isClassStart",false)){
            Intent intent=new Intent(this,AnswerActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this,"还未上课！",Toast.LENGTH_SHORT).show();
        }
    }

    /***************自定义函数**************/

    private void sendMsgToSer(String str){
        Intent intent=new Intent(global.getActivityToService());
        intent.putExtra("msg", str);
        sendBroadcast(intent);

    }

    private void initView(){
        Typeface liShu = Typeface.createFromAsset(this.getAssets(),
                "baqi.ttf");
        bannerImage.setTypeface(liShu);
        bannerImage.setText(banners[banners.length - 1]);
        TextPaint tp = bannerImage.getPaint();
        tp.setFakeBoldText(true);

        Typeface font = Typeface.createFromAsset(this.getAssets(),
                "fontawesome-webfont.ttf");
        leftQuot.setTypeface(font);
        rightQuot.setTypeface(font);
        toolbarTitle.setTypeface(font);

        toolbarTitle.setText(getResources().getString(R.string.fa_user) + " 离线");
        toolbarTitle.setTextColor(getResources().getColor(R.color.gray));

    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.test_background);
        resideMenu.attachToActivity(this);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemUserLogin  = new ResideMenuItem(this, R.drawable.user_login,     "用户登录");
        //itemAlone = new ResideMenuItem(this, R.drawable.icon_calendar, "自习模式");
        itemResult = new ResideMenuItem(this, R.drawable.get_result, "得分查询");
        itemAbourUs = new ResideMenuItem(this, R.drawable.about_us, "关于我们");
        itemSign = new ResideMenuItem(this, R.drawable.user_sign, "用户注册");
        //itemSendResult = new ResideMenuItem(this, R.drawable.icon_settings, "提交记录");
       // itemLogout = new ResideMenuItem(this, R.drawable.icon_settings,"用户登出");

        itemUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sideMenu", "用户登录");
                //resideMenu.closeMenu();

                inputDialogFragment=new InputDialogFragment();
                inputDialogFragment.show(getFragmentManager(), "loginFragment");
            }
        });

        itemResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sideMenu", "结果查询");
                Intent intent=new Intent(MainActivity.this,ShowRecord.class);
                startActivity(intent);
            }
        });

        itemAbourUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sideMenu", "关于我们");
                resideMenu.closeMenu();
                Intent intent=new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent);
            }
        });

        itemSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sideMenu", "用户注册");
                inputDialogFragment=new InputDialogFragment();
                inputDialogFragment.show(getFragmentManager(), "signFragment");
            }
        });

        resideMenu.addMenuItem(itemUserLogin, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemResult, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSign, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbourUs, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    private void flashUI(){
        if (classInfo.getBoolean("isClassStart",false)){
            toolbarTitle.setText(getResources().getString(R.string.fa_user)+" 上课");
            toolbarTitle.setTextColor(getResources().getColor(R.color.white));
        }else {
            toolbarTitle.setText(getResources().getString(R.string.fa_user)+" 下课");
            toolbarTitle.setTextColor(getResources().getColor(R.color.gray));
        }
        currentBanner++;
        if (currentBanner>=banners.length)
            currentBanner=0;
        bannerImage.setText(banners[currentBanner]);
    }

    /***************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpMenu();

        initView();

        global=(Global)getApplication();
        userInfo=getSharedPreferences("user", Activity.MODE_PRIVATE);
        classInfo=getSharedPreferences("classInfo",Activity.MODE_PRIVATE);

        /*注册广播，收听来自Activity的消息*/
        listenFromService=new ListenFromService();
        IntentFilter filter = new IntentFilter(global.getServiceToActivity());
        registerReceiver(listenFromService, filter);

        if (!global.isStartedService()){
            /*启动后台Service*/
            global.setStartedService(true);
            Intent intent=new Intent(this,AppListenerService.class);
            startService(intent);
        }

        flashUI();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy");
        unregisterReceiver(listenFromService);
        //stopService(new Intent(this, AppListenerService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        flashUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /** 上线操作：发送上线消息，在收到上线信息后同步本地状态*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (classInfo.getBoolean("isClassStart",false)){
            if (keyCode==KeyEvent.KEYCODE_HOME||keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU){
                DialogHelp.getConfirmDialog(this, "当前正在上课！退出将会离线并将记录同步至云端，是否继续？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).show();
            }
        }
        else {
            if(keyCode==KeyEvent.KEYCODE_BACK)
            {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            super.onKeyDown(keyCode,event);
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onInfoSend(String fragmentTag,String ip , String id , String password) {
        if (fragmentTag.equals("loginFragment")){
            SharedPreferences.Editor editor=userInfo.edit();
            editor.putString("ipAddress", ip);
            editor.commit();
            sendMsgToSer("0="+id+"="+password);
            //progressDialog = ProgressDialog.show(MainActivity.this, "登录中", "请稍等", true);
        }
        else {
            /*注册*/
            SharedPreferences.Editor editor=userInfo.edit();
            editor.putString("ipAddress",ip);
            editor.commit();
            sendMsgToSer("2="+id+"="+password);
        }
    }

    /**
     * 数字=消息
     * 0=返回的普通字符
     * 1= 1:outOfTime 2:loginFailure 3:loginSusses 4:logoutSusses 5:disconnected连接超时信号,上线 下线信号
     * 2=wifiOff  wifi断开
     * 3=1 定时flashUI（改变图片）
     * 4=xxxx  注册返回消息  signed!(注册成功) hassigned!(已经被注册) noinclass!(不在本班)
     * */

    private class ListenFromService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgContent=intent.getStringExtra("msg");
            String[] tem=msgContent.split("=");
            int tempInt=Integer.parseInt(tem[0]);
            switch (tempInt){
                case 1:
                    //progressDialog.dismiss();
                    int fl=Integer.parseInt(tem[1]);
                    switch (fl){
                        case 1:
                            DialogHelp.getReconnectConfirmDialog(MainActivity.this, "连接超时！是否重连？", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*重连*/
                                    sendMsgToSer("4=1");
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*重置上课状态*/
                                    SharedPreferences.Editor editor=classInfo.edit();
                                    editor.putBoolean("isClassStart",false);
                                    editor.commit();
                                    flashUI();
                                }
                            }).show();
                            break;
                        case 2:
                            Toast.makeText(MainActivity.this,"登录认证失败，请输入正确的学号密码！",Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            /*UI变化*/
                            flashUI();
                            break;
                        case 4:
                            Toast.makeText(MainActivity.this,"已经离开！",Toast.LENGTH_SHORT).show();
                            /*UI变化*/
                            flashUI();
                            break;
                        case 5:
                            Toast.makeText(MainActivity.this,"连接断开！",Toast.LENGTH_SHORT).show();
                            flashUI();
                            break;
                        case 6:
                            Toast.makeText(MainActivity.this,"签到成功,开始上课！",Toast.LENGTH_SHORT).show();
                            flashUI();
                            break;
                        case 7:
                            Toast.makeText(MainActivity.this,"收到成绩，已经签退！",Toast.LENGTH_SHORT).show();
                            /*UI变化*/
                            flashUI();
                            break;
                        case 8:
                            /*单纯的UI变化*/
                            flashUI();
                            break;
                        case 9:
                            Toast.makeText(MainActivity.this,"回到课堂！",Toast.LENGTH_SHORT).show();

                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    DialogHelp.getMessageDialog(MainActivity.this,"wifi已断开！").show();
                    break;
                case 3:
                    flashUI();
                    break;
                case 4:
                    if (tem[1].equals("startclass!")){
                        Toast.makeText(MainActivity.this,"注册并签到成功！",Toast.LENGTH_SHORT).show();
                    }else if (tem[1].equals("hassigned!")){
                        Toast.makeText(MainActivity.this,"该学号已经被注册！！",Toast.LENGTH_SHORT).show();
                    }
                    else if (tem[1].equals("noinclass!")){
                        Toast.makeText(MainActivity.this,"该学号不属于本班级！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this,"当前未开放注册！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

