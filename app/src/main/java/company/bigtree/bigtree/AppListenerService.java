package company.bigtree.bigtree;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import company.bigtree.bigtree.android_socket.Client;
import company.bigtree.bigtree.android_socket.ISocketResponse;
import company.bigtree.bigtree.android_socket.Packet;

public class AppListenerService extends Service {

    private Context cont=this;

    private boolean nowState,lastState;
    private Global global;

    private ListenFromActivity listenFromActivity;

    //定义一个WifiManager对象
    private WifiManager mWifiManager;

    SharedPreferences userInfo;//用于保存用户登录成功后的用户信息：密码(userPassword)，学号(userNumber) 姓名（userName）, IP地址(ipAddress)
    SharedPreferences recordInfo;
    SharedPreferences classInfo;// isClassStart(boolean)目前是否上课 countTime(int)总时间 lastTime(long)离线的时间点
    private String userNumber;
    private String userPassword;

    private Vibrator vibrator;

    private boolean isOut=false;

    Thread listenThread=null;

    private int countTimeTemp;
    private long lastTimeTemp;

    /**
     * 连接超时的次数
     * */
    private int outOfTimeNumber=0;


    /**
     * 注册判断开关
     * 默认为false
     * 当使用时设置为true，并且在用完后马上还原为false
     * */
    private boolean isSign=false;

    /**
     * 提交记录判断开关
     * 默认为false
     * 当使用时设置为true，并且在用完后马上还原为false
     * */
    private boolean isSendResult=false;

    /**
     * 线程锁
     * 默认为false
     * 当开启一遍以后设为true
     * */
    private PowerManager.WakeLock weakeLock;
    private WifiManager.WifiLock wifiLock;

    /**
     * SocketClient
     * */
    private Client socketClient=null;

    private final static String TAG="AppListenerService";

    /*Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 如果消息来自子线程
            if (msg.what == 0x123) {
                // 将读取的内容追加显示在文本框中
                String sss= msg.obj.toString();
                Log.e("getMessage",sss);
                analyseResult(sss);
            }
            if (msg.what == 0x234){
                *//*连接超时*//*
                Log.e("AppListenerService", "msg234");
                sendMsgToAct("1=1");
                startVibration();
            }
        }
    };*/

    private ISocketResponse socketResponse=new ISocketResponse() {
        @Override
        public void onSocketResponse(String txt) {
            Log.e("Receive from Server", txt);
            if (txt.equals("404")){
                outOfTimeNumber++;
                if (outOfTimeNumber>=2){
                    sendMsgToAct("1=1");
                    socketClient.close();
                    outOfTimeNumber=0;
                }
            }
            else {
                analyseResult(txt);
            }
        }
    };

    //打开wifi
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            sendMsgToAct("2=wifiOff");
        }
    }

    private void sendMsgToCom(String str){
        /*每次发送之前判断是否需要重连
        * */
        Packet packet=new Packet();
        packet.pack(str);
        if (socketClient==null){
            socketClient=new Client(this.getApplicationContext(),socketResponse);
            socketClient.open(userInfo.getString("ipAddress","null"),9999);
        }
        if (socketClient.isNeedConn()){
            String ipAddress=userInfo.getString("ipAddress", "null");
            if (!ipAddress.equals("null")){
                socketClient.open(ipAddress, 9999);

                socketClient.send(packet);
            }
        }else {
            socketClient.send(packet);
        }
        Log.e("SendMsgToServer", str);
    }

    private void sendMsgToAct(String str){
        Intent intent=new Intent(global.getServiceToActivity());
        intent.putExtra("msg", str);
        sendBroadcast(intent);
    }

    private void  analyseResult(String string){
        String[] ss=string.split(":");
        if (ss[0].equals("4")){
            if (ss[1].equals("connected!")){
                /** 连接成功的操作*/
                /*if (isSign){
                    //注册操作
                    sendMsgToCom("0:" + userNumber + ":" + userPassword + ":\n");
                    isSign=false;
                }
                else {
                    //签到操作
                    sendMsgToCom("1:"+userNumber+":"+userPassword+":\n");
                }*/
            }
        }
        else if (ss[0].equals("0")){
            //注册返回消息分析
            sendMsgToAct("4="+ss[1]);
            if (ss[1].equals("startclass!")){
                //登录成功操作  进行下课操作

                startVibration();
                SharedPreferences.Editor editor=userInfo.edit();
                editor.putString("userNumber", userNumber);
                editor.putString("userPassword", userPassword);
                editor.commit();

                SharedPreferences.Editor editor1=classInfo.edit();
                editor1.putBoolean("isClassStart", true);
                editor1.putInt("countTime", 0);
                editor1.putLong("lastTime", SystemClock.elapsedRealtime());
                editor1.commit();

                listenAppTask();

                sendMsgToAct("1=3");
            }
        }
        else if (ss[0].equals("1")){
            if (ss[1].equals("startclass!")){
                /** 开始上课,刷新UI，重置状态量，开始监听*/
                startVibration();

                SharedPreferences.Editor editor=userInfo.edit();
                editor.putString("userNumber", userNumber);
                editor.putString("userPassword", userPassword);
                editor.commit();

                SharedPreferences.Editor editor1=classInfo.edit();
                editor1.putBoolean("isClassStart", true);
                editor1.putInt("countTime", 0);
                editor1.putLong("lastTime", SystemClock.elapsedRealtime());
                editor1.commit();

                listenAppTask();

                sendMsgToAct("1=6");
            }
            else if (ss[1].equals("failure!")){
                /** 登录认证失败*/
                sendMsgToAct("1=2");
            }
        }
        else if (ss[0].equals("2")){
            /** 正常返回数据分析*/
            Log.e(TAG,"正常返回数据=>"+ss[1]);
            sendMsgToAct("0=" + ss[1]);
        }
        else {
            /*以3打头*/
            if (ss[0].equals("3")){
                /** 下课操作,收到结果，重置状态量，刷新UI*/
                startVibration();
                sendMsgToAct("1=7");
                SharedPreferences.Editor editor1=classInfo.edit();
                editor1.putBoolean("isClassStart", false);
                editor1.putInt("countTime", 0);
                editor1.putLong("lastTime", SystemClock.elapsedRealtime());
                editor1.commit();

                SharedPreferences.Editor editor=recordInfo.edit();
                editor.putString("id",ss[1]);
                editor.putString("name",ss[2]);
                editor.putString("onlineT", ss[3]);
                editor.putString("classT", ss[4]);
                editor.putString("checkS",ss[5]);
                editor.putString("answerS",ss[6]);
                editor.commit();
            }
        }
    }


    private void listenAppTask(){
        /** 获取系统服务 ActivityManager */
        if (listenThread==null){
            listenThread=new Thread(new DetectTopPackageRunnable());
            listenThread.start();
        }
    }


    /** 获得wakeLock和wifiLock，使熄屏时CPU和wifi仍然可以使用*/
    private void getLock(){
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        weakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        weakeLock.setReferenceCounted(true);
        weakeLock.acquire();

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,"SwiFTP");
        wifiLock.setReferenceCounted(true);
        wifiLock.acquire();

        Log.e("wakeLockState", "" + weakeLock.isHeld());
    }

    /** 释放wakelock和wifilock*/
    private void releaseLock(){
        if (weakeLock.isHeld()){
            weakeLock.release();
        }
        if (wifiLock.isHeld()){
            wifiLock.release();
        }
        weakeLock=null;
        wifiLock=null;
    }

    /** 震动*/
    private void startVibration(){
        long [] pattern = {100,600};   // 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    /*单纯切回，未杀死进程*/
    private void whenBack(){
        /*告知已经上线*/
        sendMsgToCom("5:"+userInfo.getString("userNumber",userNumber)+":"+userInfo.getString("userPassword",userPassword)+":\n");
        lastTimeTemp=SystemClock.elapsedRealtime();
        int onceTime=(int)((lastTimeTemp-classInfo.getLong("lastTime",lastTimeTemp))/1000);
        if (onceTime>=0){
            countTimeTemp=classInfo.getInt("countTime",0)+onceTime;
        }
        else {
            countTimeTemp=classInfo.getInt("countTime",0);
        }
        SharedPreferences.Editor editor1=classInfo.edit();
        editor1.putInt("countTime", countTimeTemp);
        editor1.putLong("lastTime", lastTimeTemp);
        editor1.commit();

        isOut=false;

        Log.e(TAG, countTimeTemp + "  " + isOut);
        Log.e(TAG, "离线->在线");
        startVibration();
        sendMsgToAct("1=9");
    }

    /*单纯切出，不杀死进程*/
    private void whenOut(){
        /*告知已经离线*/
        sendMsgToCom("6:"+userInfo.getString("userNumber",userNumber)+":"+userInfo.getString("userPassword",userPassword)+":\n");
        sendMsgToCom("");
        SharedPreferences.Editor editor1=classInfo.edit();
        editor1.putLong("lastTime", SystemClock.elapsedRealtime());
        editor1.commit();

        isOut=true;

        Log.e(TAG, countTimeTemp + "  " + isOut);
        Log.e(TAG, "在线->离线");
        startVibration();
        sendMsgToAct("1=4");
    }

    private void startConnect(){
        socketClient=new Client(this.getApplicationContext(),socketResponse);

        socketClient.open("192.168.199.167",9999);
    }


    public AppListenerService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        global=(Global)getApplication();

        //getLock();

        userInfo=getSharedPreferences("user", Activity.MODE_PRIVATE);
        recordInfo=getSharedPreferences("recordInfo", Activity.MODE_PRIVATE);
        classInfo=getSharedPreferences("classInfo",Activity.MODE_PRIVATE);

        /*注册广播，收听来自Activity的消息*/
        listenFromActivity=new ListenFromActivity();
        IntentFilter filter = new IntentFilter(global.getActivityToService());
        registerReceiver(listenFromActivity, filter);

        mWifiManager=(WifiManager) getSystemService(WIFI_SERVICE);
        openWifi();

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if (classInfo.getBoolean("isClassStart",false)){
            listenAppTask();
            whenBack();
        }

        startConnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service is destroy");
        vibrator.cancel();
        if (!isOut){
            whenOut();
        }

        unregisterReceiver(listenFromActivity);

        socketClient.close();

        //releaseLock();
    }

    /**
     * 规则：
     * 0=xxxx 登录
     * 1=xxxx 要发送的数据
     * 2=xxxx 注册消息
     * 3=xxxx 提交考勤记录
     * */
    private class ListenFromActivity extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String msgContent=intent.getStringExtra("msg");
            Log.e("ReceiveFromActivity",msgContent);
            String[] tem=msgContent.split("=");
            int tempInt=Integer.parseInt(tem[0]);
            switch (tempInt){
                case 0:
                    //登录消息
                    userNumber=tem[1];
                    userPassword=tem[2];
                    sendMsgToCom("1:"+userNumber+":"+userPassword+":\n");
                    break;
                case 1:
                    sendMsgToCom("18393919594:"+tem[1]+"\n");
                    break;
                case 2:
                    userNumber=tem[1];
                    userPassword=tem[2];
                    isSign=true;
                    //sendMsgToCom("3:"+tem[1]+":"+tem[2]+":\n");
                    sendMsgToCom("0:"+userNumber+":"+userPassword+":\n");
                    break;
                case 3:
                    isSendResult=true;
                    sendMsgToCom("3:"+userInfo.getString("userNumber","null")+":"+userInfo.getString("userPassword","null")+":"+classInfo.getInt("countTime",0)+":\n");
                    break;
                case 4:
                    /*重连操作*/
                    String ipAddress=userInfo.getString("ipAddress", "null");
                    if (!ipAddress.equals("null")){
                        socketClient.open(ipAddress, 9999);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class DetectTopPackageRunnable implements Runnable {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        @Override
        public void run() {

            while (true){
                String[] activePackages;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    /*5.0 版本以后适配*/
                    activePackages = getActivePackages();
                    if (activePackages.length==0){
                        lastState=nowState;
                        nowState=false;
                        if (lastState!=nowState){
                            /*发生了变化，在线->离线*/
                            if (classInfo.getBoolean("isClassStart",false)){
                                whenOut();
                            }
                        }
                    }
                    else {
                        lastState=nowState;
                        nowState=true;
                        if (lastState!=nowState){
                            /*发生了变化，离线->在线*/
                            if (classInfo.getBoolean("isClassStart",false)){
                                whenBack();
                            }
                        }
                    }
                } else {
                    activePackages = getActivePackagesCompat();
                    if (activePackages.length!=0){
                        String packageName = activePackages[0];
                        if (!packageName.equals("company.bigtree.bigtree")){
                            lastState=nowState;
                            nowState=false;
                            if (lastState!=nowState){
                                /*发生了变化，在线->离线*/
                                if (classInfo.getBoolean("isClassStart",false)){
                                    whenOut();
                                }
                            }
                        }else{
                            lastState=nowState;
                            nowState=true;
                            if (lastState != nowState){
                            /*发生了变化，离线->在线*/
                                if (classInfo.getBoolean("isClassStart",false)){
                                    whenBack();
                                }
                            }
                        }
                    }
                }
                /** 为了便于观察信息的输出，程序休眠1秒 */
                SystemClock.sleep(1000);
            }
        }

        String[] getActivePackagesCompat() {
            final List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);
            final ComponentName componentName = taskInfo.get(0).topActivity;
            final String[] activePackages = new String[1];
            activePackages[0] = componentName.getPackageName();
            return activePackages;
        }

        String[] getActivePackages() {
            final Set<String> activePackages = new HashSet<String>();
            final List<ActivityManager.RunningAppProcessInfo> processInfos = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    activePackages.addAll(Arrays.asList(processInfo.pkgList));
                }
            }
            return activePackages.toArray(new String[activePackages.size()]);
        }
    }
}
