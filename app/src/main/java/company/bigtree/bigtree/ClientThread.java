package company.bigtree.bigtree;

/**
 *
 * @auther shenzebang
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable {
    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    Handler handler;
    // 定义接收UI线程的Handler对象
    Handler revHandler;
    // 该线程处理Socket所对用的输入输出流
    BufferedReader br = null;
    OutputStream os = null;
    InputStream is = null;

    String content=null;

    private String ipAddress;

    public ClientThread(Handler handler , String ipAdd) {
        this.handler = handler;
        this.ipAddress = ipAdd;
    }

    @Override
    public void run() {
        s = new Socket();
        try {
            s.connect(new InetSocketAddress(ipAddress, 9999), 4000);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
            is = s.getInputStream();
            // 启动一条子线程来读取服务器相应的数据
            new Thread() {
                @Override
                public void run() {
                    // 不断的读取Socket输入流的内容
                    try {
                        while (( content = br.readLine()) != null) {
                            // 每当读取到来自服务器的数据之后，发送的消息通知程序
                            // 界面显示该数据
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            String sss= msg.obj.toString();
                            Log.e("ThreadReceiveMsg",sss);
                            handler.sendMessage(msg);
                            content=null;
                        }
                    } catch (IOException io) {
                        Log.e("IOException11",io.toString());
                        Message msg = new Message();
                        msg.what = 0x234;
                        msg.obj = "网络连接超时！";
                        handler.sendMessage(msg);
                    }
                }
            }.start();
            // 为当前线程初始化Looper
            Looper.prepare();
            // 创建revHandler对象
            revHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    // 接收到UI线程的中用户输入的数据
                    if (msg.what == 0x345) {
                        // 将用户在文本框输入的内容写入网络
                        try {
                            os.write((msg.obj.toString() + "\r\n")
                                    .getBytes("gbk"));
                            Log.e("ThreadSendMsg",msg.obj.toString());
                        } catch (Exception e) {
                            Log.e("ThreadExceptionMsg",e.toString());
                            Message msg1 = new Message();
                            msg.what = 0x234;
                            msg.obj = "网络连接超时！";
                            handler.sendMessage(msg1);
                        }
                    }
                }

            };
            // 启动Looper
            Looper.loop();

        } catch (SocketTimeoutException e) {
            Log.e("SocketTimeoutException",e.toString());
            Message msg = new Message();
            msg.what = 0x234;
            msg.obj = "网络连接超时！";
            handler.sendMessage(msg);
        } catch (IOException io) {
            Log.e("IOException",io.toString());
            Message msg = new Message();
            msg.what = 0x234;
            handler.sendMessage(msg);
        }

    }
}
