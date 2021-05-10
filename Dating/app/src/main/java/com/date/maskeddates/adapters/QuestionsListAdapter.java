package com.date.maskeddates.adapters;

/**
 * Created by sonback123456 on 6/4/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.main.QuestionsActivity;
import com.date.maskeddates.models.Questionnaire;

import java.util.ArrayList;

/**
 * Created by a on 4/27/2017.
 */

public class QuestionsListAdapter extends BaseAdapter {

    private QuestionsActivity _context;
    private ArrayList<Questionnaire> _datas = new ArrayList<>();
    private ArrayList<Questionnaire> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public QuestionsListAdapter(QuestionsActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Questionnaire> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.questionnaire_list_item, parent, false);

            holder.text=(TextView)convertView.findViewById(R.id.text);
            holder.isactive=(TextView) convertView.findViewById(R.id.isactive);
            holder.answer=(TextView)convertView.findViewById(R.id.answer);
            holder.answerButton=(TextView) convertView.findViewById(R.id.answerButton);
            holder.checkMark = (ImageView) convertView.findViewById(R.id.checkMark);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Questionnaire questionnaire = (Questionnaire) _datas.get(position);

        holder.text.setText(questionnaire.get_text());
        holder.isactive.setText(questionnaire.get_isactive());

        holder.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.holder = holder;
                _context.answer(questionnaire.get_text());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (Questionnaire questionnaire : _alldatas){

                if (questionnaire instanceof Questionnaire) {

                    String value = ((Questionnaire) questionnaire).get_text().toLowerCase();
                    String value1 = ((Questionnaire) questionnaire).get_isactive().toLowerCase();
                    if (value.contains(charText) || value1.contains(charText)) {
                        _datas.add(questionnaire);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class CustomHolder {
        public TextView text;
        public TextView isactive;
        public TextView answerButton;
        public TextView answer;
        public ImageView checkMark;
    }
}
















