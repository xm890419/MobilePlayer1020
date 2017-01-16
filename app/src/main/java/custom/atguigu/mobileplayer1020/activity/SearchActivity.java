package custom.atguigu.mobileplayer1020.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;

import custom.atguigu.mobileplayer1020.R;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etSearch;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private void findViews() {
        setContentView(R.layout.activity_search);
        //SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5838f0d9");
        etSearch = (EditText) findViewById(R.id.et_search);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);

        ivVoice.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice:
                Toast.makeText(this, "语音输入", Toast.LENGTH_SHORT).show();

                break;
            case R.id.tv_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
