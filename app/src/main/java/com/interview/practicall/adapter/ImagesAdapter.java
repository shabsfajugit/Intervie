package com.interview.practicall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.interview.practicall.R;
import com.interview.practicall.activity.ShowAllActivity;
import com.interview.practicall.database.DatabaseHelper;
import com.interview.practicall.model.ImagesModel;
import com.interview.practicall.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    Context context;
    List<ImagesModel> imagesModelList;
    DatabaseHelper helper;

    public ImagesAdapter(Context context, List<ImagesModel> homeProductItemList) {
        this.context = context;
        this.imagesModelList = homeProductItemList;
        helper = new DatabaseHelper(context);
    }

    public ImagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images,parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImagesModel model = imagesModelList.get(position);

        Log.d("dataaa", String.valueOf(context));
        Glide.with(context).load(model.getUrl()).into(holder.image);

        if (context instanceof ShowAllActivity){
            holder.ivDownload.setVisibility(View.VISIBLE);
        }else {
            holder.ivDownload.setVisibility(View.GONE);
        }


        holder.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure Download this image ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                helper.insertDataInDownload(imagesModelList.get(position));
                                Toast.makeText(context, "Download Success!!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView image;
        AppCompatImageView ivDownload;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ivDownload = itemView.findViewById(R.id.ivDownload);

        }
    }
}
