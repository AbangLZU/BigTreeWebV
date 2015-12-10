package company.bigtree.bigtree;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowRecord extends AppCompatActivity {

    SharedPreferences recordInfo;

    String[] recordStrings;
    String[] titleStrings={"姓名","学号","考勤时长","在线时长","考勤得分","答题得分"};


    @Bind(R.id.record_list)
    ListView recordList;

    @Bind({R.id.btn_guide_day,R.id.btn_guide_week,R.id.btn_guide_month})
    List<Button> buttonList;

    @OnClick(R.id.btn_guide_day)
    public void clickDay(){
        /*单击查看3天内的记录*/
        buttonList.get(0).setTextColor(Color.parseColor("#0099cc"));
        buttonList.get(0).setBackgroundResource(R.drawable.guide_btn_pink_left);
        buttonList.get(1).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(1).setBackgroundResource(R.drawable.guide_btn_trans_center);
        buttonList.get(2).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(2).setBackgroundResource(R.drawable.guide_btn_trans_right);
    }

    @OnClick(R.id.btn_guide_week)
    public void clickWeek(){
        /*单击查看1周内的记录*/
        buttonList.get(0).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(0).setBackgroundResource(R.drawable.guide_btn_trans_left);
        buttonList.get(1).setTextColor(Color.parseColor("#0099cc"));
        buttonList.get(1).setBackgroundResource(R.drawable.guide_btn_pink_center);
        buttonList.get(2).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(2).setBackgroundResource(R.drawable.guide_btn_trans_right);
    }

    @OnClick(R.id.btn_guide_month)
    public void clickMonth(){
        /*单击查看1月内的记录*/
        buttonList.get(0).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(0).setBackgroundResource(R.drawable.guide_btn_trans_left);
        buttonList.get(1).setTextColor(Color.parseColor("#ffffff"));
        buttonList.get(1).setBackgroundResource(R.drawable.guide_btn_trans_center);
        buttonList.get(2).setTextColor(Color.parseColor("#0099cc"));
        buttonList.get(2).setBackgroundResource(R.drawable.guide_btn_pink_right);
    }



    /** 秒换算成时分秒*/
    public static String formatSecond(String second){
        String  html="0秒";
        if(second!=null){
            double s=Double.parseDouble(second);
            String format;
            Object[] array;
            Integer hours =(int) (s/(60*60));
            Integer minutes = (int) (s/60-hours*60);
            Integer seconds = (int) (s-minutes*60-hours*60*60);
            if(hours>0){
                format="%1$,d时%2$,d分%3$,d秒";
                array=new Object[]{hours,minutes,seconds};
            }else if(minutes>0){
                format="%1$,d分%2$,d秒";
                array=new Object[]{minutes,seconds};
            }else{
                format="%1$,d秒";
                array=new Object[]{seconds};
            }
            html= String.format(format, array);
        }
        return html;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        ButterKnife.bind(this);

        recordInfo=getSharedPreferences("recordInfo", Activity.MODE_PRIVATE);

        recordStrings=new String[6];

        recordStrings[0]=recordInfo.getString("name","");
        recordStrings[1]=recordInfo.getString("id","");
        recordStrings[2]=recordInfo.getString("classT","");
        recordStrings[3]=recordInfo.getString("onlineT","");
        recordStrings[4]=recordInfo.getString("checkS","");
        recordStrings[5]=recordInfo.getString("answerS","");

        if (recordStrings[0].equals("")){
            Toast.makeText(this,"没有记录",Toast.LENGTH_SHORT).show();
        }else {
            recordStrings[2]=formatSecond(recordStrings[2]);
            recordStrings[3]=formatSecond(recordStrings[3]);
        }

        ArrayList<Map<String,Object>> results=new ArrayList<Map<String, Object>>();
        for (int i=0;i<recordStrings.length;i++){
            Map<String,Object> result=new HashMap<String, Object>();
            result.put("title",titleStrings[i]);
            result.put("value",recordStrings[i]);
            results.add(result);
        }

        SimpleAdapter simpleAdapter=new SimpleAdapter(this,
                results,
                R.layout.record_list_line,
                new String[]{"title","value"},
                new int[]{R.id.title,R.id.value});

        recordList.setAdapter(simpleAdapter);
    }
}
