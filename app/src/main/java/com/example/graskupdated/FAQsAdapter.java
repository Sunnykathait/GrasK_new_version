package com.example.graskupdated;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsViewHolder> {

    private final ArrayList<ArrayList<String>> dataset;

    public FAQsAdapter(ArrayList<ArrayList<String>> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public FAQsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout_faq, parent, false);

        return new FAQsViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQsViewHolder holder, int position) {
        ArrayList<String> faq = dataset.get(position);
        holder.txt_faqQuestion.setText(faq.get(0));
        holder.txt_faqAnswer.setText(faq.get(1));

        holder.txt_showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.txt_faqAnswer.getVisibility() == View.GONE) {
                    holder.txt_faqAnswer.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_faqAnswer.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class FAQsViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_faqQuestion, txt_faqAnswer, txt_showHide;

        public FAQsViewHolder(View view) {
            super(view);
            txt_faqQuestion = view.findViewById(R.id.txt_faqQuestion);
            txt_faqAnswer = view.findViewById(R.id.txt_faqAnswer);
            txt_showHide = view.findViewById(R.id.txt_showFaq);
        }
    }
}