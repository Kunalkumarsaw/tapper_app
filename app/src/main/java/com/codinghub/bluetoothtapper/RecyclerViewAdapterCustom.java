package com.codinghub.bluetoothtapper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterCustom  extends RecyclerView.Adapter<RecyclerViewAdapterCustom.ViewHolder> {

    private final ArrayList<String> localDataSet;
    public RecyclerViewAdapterCustom(ArrayList<String> DataSet){
        localDataSet=DataSet;
    }
    @NonNull
    @Override
    public RecyclerViewAdapterCustom.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterCustom.ViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textRecyclerView);
        }
        public TextView getTextView() {
            return text;
        }
    }
}
