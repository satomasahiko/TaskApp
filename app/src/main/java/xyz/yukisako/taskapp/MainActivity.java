package xyz.yukisako.taskapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "xyz.yukisako.taskapp.TASK";

    private Realm mRealm;
    //Realmの中身が変更された時に呼ばれるやつっぽい？
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            reloadListView();
        }
    };
    private ListView mListView;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InputActivity.class);
                startActivity(intent);
            }
        });

        //Realmの設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        //ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        //ListViewをタップした時の処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view, int position, long id){
                //入力編集画面への遷移
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());

                startActivity(intent);
            }
        });

        //ListViewを長押し
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //タスクの削除
                return true;
            }
        });

//        //アプリ起動時に表示テスト用のタスクの作成
//        addTaskForTest();

        reloadListView();
    }

    private void reloadListView() {
        //Realmから日時順に取得
        RealmResults<Task> taskRealmResults = mRealm.where(Task.class).findAllSorted("date", Sort.DESCENDING);
        //結果をTaskListにセット(TaskListクラスのコンストラクタ)
        mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));
        //TaskのListView用アダプタへ渡す
        mListView.setAdapter(mTaskAdapter);
        //表示を更新
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }
//
//    private void addTaskForTest(){
//        Task task = new Task();
//        task.setTitle("ブログ記事の更新");
//        task.setContents("Android開発の記録をブログにまとめる");
//        task.setDate(new Date());
//        task.setId(0);
//        mRealm.beginTransaction();
//        mRealm.copyToRealmOrUpdate(task);
//        mRealm.commitTransaction();
//    }
}
