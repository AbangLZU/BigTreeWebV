package company.bigtree.bigtree;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author shenzebang
 * */
public class InputDialogFragment extends DialogFragment {

    private final String TAG="InputDialogFragment";

    private SharedPreferences userInfo;

    private Global global;

    private String fragmentTag;

    @Bind(R.id.id_text)
    EditText idText;

    @Bind(R.id.ip_text)
    EditText ipText;

    @Bind(R.id.id_ok)
    BootstrapButton idOk;

    @Bind(R.id.password_text)
    TextView passwordText;

    @OnClick(R.id.id_ok)
    public void clickIdOk(){
        if (checkNull()){
            if (fragmentTag.equals("loginFragment")){
                if (checkIP(ipText.getText().toString())&&checkAccount(idText.getText().toString())
                        &&checkPassword(passwordText.getText().toString())){
                    InfoSendListener infoSendListener=(InfoSendListener)getActivity();
                    infoSendListener.onInfoSend(fragmentTag,ipText.getText().toString(),idText.getText().toString(),passwordText.getText().toString());
                    this.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "格式不符合要求！", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if (checkIP(ipText.getText().toString())&&checkAccount(idText.getText().toString())
                        &&checkPassword(passwordText.getText().toString())){
                    InfoSendListener infoSendListener=(InfoSendListener)getActivity();
                    infoSendListener.onInfoSend(fragmentTag,ipText.getText().toString(),idText.getText().toString(),passwordText.getText().toString());
                    this.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "格式不符合要求！", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(getActivity(), "内容不能为空！", Toast.LENGTH_SHORT).show();
        }
    }

    /*通过非空检查则返回true*/
    private boolean checkNull(){
        if (ipText.getText().toString().equals("")||idText.getText().toString().equals("")||passwordText.getText().toString().equals("")){
            return false;
        }else{
            return true;
        }
    }

    private boolean checkIP(String str){
        String ip = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private boolean checkAccount(String str){
        Pattern pattern = Pattern.compile("^\\d{12}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private boolean checkPassword(String str){
        Pattern pattern = Pattern.compile("^\\d{6}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private boolean checkSame(){
        if (idText.getText().toString().equals(passwordText.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    private void initView(){
        if (fragmentTag.equals("loginFragment")){
            idText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            String studentId=userInfo.getString("userNumber","null");
            if (studentId.equals("null")){
                idText.setHint("你的学号");
            }
            else {
                idText.setText(studentId);
            }

            String ipAddress=userInfo.getString("ipAddress","null");
            if (ipAddress.equals("null")){
                ipText.setHint("主机IP地址");
            }
            else {
                ipText.setText(ipAddress);
            }
            String passW=userInfo.getString("userPassword","null");
            if (passW.equals("null")){
                passwordText.setHint("你的密码");
            }
            else {
                passwordText.setText(passW);
            }
        }
        else {
            ipText.setHint("请输入IP");
            idText.setHint("请输入学号");
            passwordText.setHint("请输入密码");
            idOk.setText("注册");
            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    public interface InfoSendListener{
        void onInfoSend(String fragmentTag,String ip , String id,String password);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global=(Global)getActivity().getApplication();
        userInfo=getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        fragmentTag=getTag();
        Log.e(TAG,fragmentTag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_input_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);

        initView();


        return view;
    }
}
