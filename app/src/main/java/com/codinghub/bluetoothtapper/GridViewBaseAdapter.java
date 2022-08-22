package com.codinghub.bluetoothtapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class GridViewBaseAdapter extends BaseAdapter {
    List<String> letters;
    ArrayList<Integer> isCorrect;
    Context context;

    public  GridViewBaseAdapter(Context context,List<String> letters, ArrayList<Integer> isCorrect){
        this.letters= letters;
        this.isCorrect= isCorrect;
        this.context = context;
    }
    @Override
    public int getCount() {
        return letters.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false);
        }
        TextView textView = view.findViewById(R.id.textViewGrid);
        LottieAnimationView imageView = view.findViewById(R.id.imageGrid);
        textView.setText(letters.get(position));
        if (isCorrect.get(position) ==1){
            imageView.setAnimation(R.raw.right);
        }else if (isCorrect.get(position)==0){
            imageView.setAnimation(R.raw.wrong);
        }else {
            imageView.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
