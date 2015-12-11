package company.bigtree.bigtree;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnswerActivity extends AppCompatActivity{

    private ListenFromService listenFromService;
    private Global global;

    SharedPreferences userInfo;//用于保存用户登录成功后的用户信息：姓名，学号
    SharedPreferences classInfo; //isClassStart(boolean)目前是否上课 countTime(int)总时间 lastTime(long)离线的时间点



    /*************控件注解**************/

    @Bind({R.id.button_a,R.id.button_b,R.id.button_c,R.id.button_d,R.id.button_e,R.id.button_f
            ,R.id.button_g,R.id.button_h,R.id.button_i})
    List<BootstrapButton> buttonList;

    @Bind(R.id.show_text)
    EditText showText;

    @Bind(R.id.button_delete)
    BootstrapButton delete;

    @Bind(R.id.standard_toolbar_title)
    TextView toolbarTitle;

    @OnClick(R.id.back_icon)
    public void clickBack(){
        AnswerActivity.this.finish();
    }

    @OnClick(R.id.button_send)
    public void clickSend(){
        String showString=showText.getText().toString();
        if (showString.equals("")){
        }
        else{
            if (showString.substring(showString.length()-1).equals(":")){
                /**发送消息*/
                sendMsgToSer("1="+showString);
                showText.setText("");
            }
            else {
                Toast.makeText(AnswerActivity.this,"请先确定再发送！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.button_ok)
    public void clickOk(){
        String showString= showText.getText().toString();
        if (showString.isEmpty())
        {
        }
        else {
            if (showString.substring(showString.length()-1).equals(":")) {

            }else {
                showText.append(":");
            }
        }
    }

    /*************控件注解**************/


    /***************自定义函数**************/
    private void initView(){
        for (int i=0;i<buttonList.size();i++){
            final int j=i;
            buttonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textString = showText.getText().toString();
                    if (textString.endsWith(":") || textString.equals("")) {
                        showText.append(buttonList.get(j).getText().toString());
                    } else {
                        String[] texts = textString.split(":");
                        String last = texts[texts.length - 1];
                        if (last.contains(buttonList.get(j).getText().toString())) {
                            return;
                        } else {
                            showText.append(buttonList.get(j).getText().toString());
                        }
                    }
                }
            });
        }

        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showText.setText("");
                return true;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showText.getText().toString().isEmpty()) {
                } else {
                    showText.getText().delete(showText.getSelectionStart() - 1
                            , showText.getSelectionStart());
                }
            }
        });
    }

    private void sendMsgToSer(String str){
        Intent intent=new Intent(global.getActivityToService());
        intent.putExtra("msg", str);
        sendBroadcast(intent);

    }

    private void resultAnalyse(String str){
        DialogHelp.getMessageDialog(this,str).show();
    }


    /***************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        ButterKnife.bind(this);



        global=(Global)getApplication();
        userInfo=getSharedPreferences("user", Activity.MODE_PRIVATE);
        classInfo=getSharedPreferences("classInfo",Activity.MODE_PRIVATE);

        /*注册广播，收听来自Activity的消息*/
        listenFromService=new ListenFromService();
        IntentFilter filter = new IntentFilter(global.getServiceToActivity());
        registerReceiver(listenFromService, filter);

        initView();

        if (!global.isStartedService()){
            /*启动后台Service*/
            global.setStartedService(true);
            Intent intent=new Intent(this,AppListenerService.class);
            startService(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listenFromService);
        stopService(new Intent(this, AppListenerService.class));
    }

    /**
     * 数字=消息
     * 0=返回的普通字符
     * */

    private class ListenFromService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgContent=intent.getStringExtra("msg");
            String[] tem=msgContent.split("=");
            int tempInt=Integer.parseInt(tem[0]);
            switch (tempInt){
                case 0:
                    //普通字符
                    resultAnalyse(tem[1]);
                    break;
                default:
                    break;
            }
        }
    }
}

