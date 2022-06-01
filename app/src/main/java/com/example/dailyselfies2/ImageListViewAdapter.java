package com.example.dailyselfies2;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ImageListViewAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Bitmap> images;
    ArrayList<String> fileName;
    LayoutInflater inflater;

    public ImageListViewAdapter(Context context, ArrayList<Bitmap> images, ArrayList<String> fileName){
        this.context = context;
        if (images==null) this.images = new ArrayList<>();
        else this.images = images;
        if (fileName==null) this.fileName = new ArrayList<>();
        else this.fileName = fileName;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(Bitmap bmp, String photoName) {
        images.add(bmp);
        fileName.add(photoName);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.my_custom_list,null);
        }
        ImageView imageView = convertView.findViewById(R.id.ivIcon);
        TextView tvName = convertView.findViewById(R.id.tvItem);
        tvName.setText(fileName.get(position));
        imageView.setImageBitmap(images.get(position));

        return convertView;
    }

}
