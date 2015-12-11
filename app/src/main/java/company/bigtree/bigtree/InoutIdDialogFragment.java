package company.bigtree.bigtree;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzebang on 15/12/11.
 */
public class InoutIdDialogFragment extends DialogFragment {

    private SharedPreferences userInfo;

    private Global global;

    private String fragmentTag;

    @Bind(R.id.student_id)
    EditText inputId;

    @OnClick(R.id.id_ok)
    public void clickOk(){
        InfoSendListener infoSendListener=(InfoSendListener)getActivity();
        infoSendListener.onInfoSend(inputId.getText().toString());
        InoutIdDialogFragment.this.dismiss();
    }

    public interface InfoSendListener{
        void onInfoSend(String account);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global=(Global)getActivity().getApplication();
        userInfo=getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.inout_id_fragment, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);

        inputId.setText(userInfo.getString("account",""));

        return view;
    }
}
