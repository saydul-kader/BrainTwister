package com.example.braintwister;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookamarksAdapter extends RecyclerView.Adapter<BookamarksAdapter.Viewholder> {

    private List<QuestionModel> list;

    public BookamarksAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
       holder.setData(list.get(position).getQuestion(),list.get(position).getCorrectAns(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        TextView ques,ans;
        ImageButton deletebtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.question);
            ans = itemView.findViewById(R.id.answer);
            deletebtn = itemView.findViewById(R.id.delete_btn);

        }
        private void setData(String question, String answer, final int position){
            this.ques.setText(question);
            this.ans.setText(answer);

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}
