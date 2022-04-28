package com.interview.practicall.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.interview.practicall.R;
import com.interview.practicall.adapter.ImagesAdapter;
import com.interview.practicall.database.DatabaseHelper;
import com.interview.practicall.model.ImagesModel;
import com.interview.practicall.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowAllActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rvImagess;
    List<ImagesModel> imagesModelList=new ArrayList<>();
    List<ImagesModel> imagesModelListForShow=new ArrayList<>();
    DatabaseReference reference;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        reference = FirebaseDatabase.getInstance().getReference();
        helper = new DatabaseHelper(ShowAllActivity.this);
        initView();
        if (isNetworkConnected()){
            getDataFromStorage();
        }else {
            showData();
        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                if (isNetworkConnected()){
                    getDataFromStorage();
                }else {
                    showData();
                }
            }
        });
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private void getDataFromStorage() {
        Utils.showProgressDialog(ShowAllActivity.this,false,"Loading");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Images");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectData((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        Utils.dismisProgressDialog();
                    }
                });

    }


    private void collectData(Map<String,Object> users) {
        imagesModelList.clear();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            Log.d("dataaaimagee",String.valueOf(singleUser.get("imagelink")));
            ImagesModel model = new ImagesModel();
            model.setUrl(String.valueOf(singleUser.get("imagelink")));
            imagesModelList.add(model);
        }

        helper.deleteCard();
        setDataToLocalDB();
    }

    private void setDataToLocalDB() {
        for (int i = 0; i < imagesModelList.size(); i++) {
            helper.insertData(imagesModelList.get(i));
        }
        Utils.dismisProgressDialog();
        showData();
    }

    private void showData() {
        imagesModelListForShow.clear();
        Cursor res = helper.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(ShowAllActivity.this, "No Data Found!!", Toast.LENGTH_SHORT).show();

        }
        while (res.moveToNext()) {
            ImagesModel rModel = new ImagesModel();
            rModel.setUrl(res.getString(1));
            imagesModelListForShow.add(rModel);
        }

        ImagesAdapter itemAdp = new ImagesAdapter(ShowAllActivity.this, imagesModelListForShow);
        rvImagess.setLayoutManager(new GridLayoutManager(ShowAllActivity.this,2, LinearLayoutManager.VERTICAL,false));
        rvImagess.setAdapter(itemAdp);

    }


    private void initView() {
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        rvImagess = (RecyclerView) findViewById(R.id.rvImagess);
    }
}