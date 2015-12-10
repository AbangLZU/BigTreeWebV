package company.bigtree.bigtree;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity {

    @Bind(R.id.standard_toolbar_title)
    TextView toolbarTitle;

    @Bind(R.id.center_text)
    TextView centerText;

    @OnClick(R.id.back_icon)
    public void clickBack(){
        AboutUsActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        toolbarTitle.setText("关于我们");

        Typeface liShu = Typeface.createFromAsset(this.getAssets(),
                "lishu.ttf");
        centerText.setTypeface(liShu);

    }
}
