package com.example.junxiantan.memento;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {
    private Button chooseDate, add, query;
    private EditText date, subject, body;
    private ListView result;
    private LinearLayout title;
    MyDatabaseHelper mydbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseDate=(Button)findViewById(R.id.chooseDate);
        add = (Button)findViewById(R.id.add);
        query = (Button) findViewById(R.id.query);
        date = (EditText) findViewById(R.id.date);
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        result = (ListView) findViewById(R.id.result);
        title=(LinearLayout)findViewById(R.id.title);

        title.setVisibility(View.INVISIBLE);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前日期
                Calendar c = Calendar.getInstance();
                //日期选择器对话框
                new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            //日期改变监听器
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                            }

                        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        MyOnClickListerner myOnClickListerner = new MyOnClickListerner();
        add.setOnClickListener(myOnClickListerner);
        query.setOnClickListener(myOnClickListerner);
    }
    private class MyOnClickListerner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //创建数据库辅助类
            mydbHelper = new MyDatabaseHelper(MainActivity.this,"memento.db",null,1);
            //获取SQLite数据库
            SQLiteDatabase db = mydbHelper.getReadableDatabase();
            String subStr = subject.getText().toString();
            String bodyStr = body.getText().toString();
            String dateStr = date.getText().toString();

            switch (v.getId()){
                case R.id.add:
                    title.setVisibility(View.INVISIBLE);
                    addMemento(db,subStr,bodyStr,dateStr);
                    Toast.makeText(MainActivity.this,"添加备忘录成功！",1000).show();
                    result.setAdapter(null);
                    break;
                case R.id.query:
                    title.setVisibility(View.VISIBLE);
                    Cursor cursor = queryMemento(db,subStr,bodyStr,dateStr);
                    SimpleCursorAdapter resultAdapter = new SimpleCursorAdapter(
                            MainActivity.this,R.layout.result,cursor,
                            new String[]{"_id","subject","body","date"},
                            new int[] { R.id.memento_num, R.id.memento_subject,
                                    R.id.memento_body, R.id.memento_date });
                    result.setAdapter(resultAdapter);
                    break;
                default:
                    break;
            }
        }
    }

   public void addMemento(SQLiteDatabase db, String subject, String body,
           String date){
        db.execSQL("insert into memento_tb values(null,?,?,?)",new String[]{
                subject,body,date});
        this.subject.setText("");
        this.body.setText("");
        this.date.setText("");
   }

   public Cursor queryMemento(SQLiteDatabase db,String subject, String body,
                              String date){
        Cursor cursor = db.rawQuery(
                "select*from mememto_tb where subject like ? and body like ? and date like ?",
                new String[] { "%" + subject + "%", "%" + body + "%",
                        "%" + date + "%" });
        return cursor;
   }

    @Override
    protected void onDestroy() {
        if (mydbHelper!=null){
            mydbHelper.close();
        }
    }
}
