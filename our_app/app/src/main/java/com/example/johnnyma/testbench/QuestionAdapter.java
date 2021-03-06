package com.example.johnnyma.testbench;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to set up the list view in ProfessorActivity.
 */
public class QuestionAdapter extends BaseAdapter {

    private Context c;
    private LayoutInflater mInflater;
    private List<Question> Questions;

    public QuestionAdapter(Context c, List<Question> Questions) {
        this.c = c;
        this.Questions = Questions;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Questions.size();
    }

    @Override
    public Object getItem(int i) {
        return Questions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // set up all the ui contents in each part of the list view
        View v = mInflater.inflate(R.layout.detail_question, null);

        TextView question_text = (TextView) v.findViewById(R.id.question);
        TextView answer_text = (TextView) v.findViewById(R.id.answer);

        TextView rate0 = (TextView) v.findViewById(R.id.rate0);
        TextView rate1 = (TextView) v.findViewById(R.id.rate1);
        TextView rate2 = (TextView) v.findViewById(R.id.rate2);
        TextView rate3 = (TextView) v.findViewById(R.id.rate3);
        TextView rate4 = (TextView) v.findViewById(R.id.rate4);
        TextView rate5 = (TextView) v.findViewById(R.id.rate5);

        ImageView verified_question = (ImageView) v.findViewById(R.id.verified_question);
        ImageView reported_question = (ImageView) v.findViewById(R.id.reported_question);

        // take a question to display on this part of the list view
        Question question = Questions.get(i);

        question_text.setText(question.getBody());
        answer_text.setText(question.getCorrectAnswer());

        List<TextView> ratings = new ArrayList<TextView>();
        ratings.add(rate0);
        ratings.add(rate1);
        ratings.add(rate2);
        ratings.add(rate3);
        ratings.add(rate4);
        ratings.add(rate5);

        int rating = question.getRating();
        if(rating != -1 && rating <= 5)
            ratings.get(rating).setTextColor(ContextCompat.getColor(c, R.color.answerColor));


        if(!question.isVerified())
            verified_question.setVisibility(View.INVISIBLE);

        if(!question.isReported())
            reported_question.setVisibility(View.INVISIBLE);

        return v;
    }
}
