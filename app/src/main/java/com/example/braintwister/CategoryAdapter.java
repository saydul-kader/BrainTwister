package com.example.braintwister;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     holder.setData(categoryModelList.get(position).getUrl(),categoryModelList.get(position).getName(),categoryModelList.get(position).getSets());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.imageview);
            textView = itemView.findViewById(R.id.title);
        }

        private void setData(String url, final String title, final int sets){
            Glide.with(itemView.getContext()).load(url).into(circleImageView);
            textView.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setintent = new Intent(itemView.getContext(),SetsActivity.class);
                    setintent.putExtra("title",title);
                    setintent.putExtra("sets",sets);
                    itemView.getContext().startActivity(setintent);
                }
            });
        }
    }
}
