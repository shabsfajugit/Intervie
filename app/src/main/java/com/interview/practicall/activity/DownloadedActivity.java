package com.interview.practicall.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.interview.practicall.R;
import com.interview.practicall.adapter.ImagesAdapter;
import com.interview.practicall.database.DatabaseHelper;
import com.interview.practicall.model.ImagesModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadedActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rvImagess;
    List<ImagesModel> imagesModelListForShow=new ArrayList<>();
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);
        helper = new DatabaseHelper(DownloadedActivity.this);
        initView();
        showData();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                showData();
            }
        });
    }


    private void showData() {
        imagesModelListForShow.clear();
        Cursor res = helper.getAllDataDownloaded();
        if (res.getCount() == 0) {
            Toast.makeText(DownloadedActivity.this, "No Data Found!!", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()) {
            ImagesModel rModel = new ImagesModel();
            rModel.setUrl(res.getString(1));
            imagesModelListForShow.add(rModel);
        }

        ImagesAdapter itemAdp = new ImagesAdapter(DownloadedActivity.this, imagesModelListForShow);
        rvImagess.setLayoutManager(new GridLayoutManager(DownloadedActivity.this,2, LinearLayoutManager.VERTICAL,false));
        rvImagess.setAdapter(itemAdp);

    }


    private void initView() {
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        rvImagess = (RecyclerView) findViewById(R.id.rvImagess);
    }
}