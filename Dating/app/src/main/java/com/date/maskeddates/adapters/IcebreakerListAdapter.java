package com.date.maskeddates.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.date.maskeddates.R;
import com.date.maskeddates.main.IcebreakerActivity;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 6/12/2018.
 */

public class IcebreakerListAdapter extends BaseAdapter {

    private IcebreakerActivity _context;
    private ArrayList<String> _datas = new ArrayList<>();
    private ArrayList<String> _alldatas = new ArrayList<>();

    public IcebreakerListAdapter(IcebreakerActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<String> datas) {

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



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.icebreaker_list_item, parent, false);

            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.submit = (TextView) convertView.findViewById(R.id.submitButton);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/Comfortaa_Bold.ttf");

        final String question =  _datas.get(position);

        holder.text.setText(question);
        holder.text.setTypeface(font);
        holder.submit.setTypeface(font);

        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.submitQuestion(question);
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

            for (String question : _alldatas){

                if (question instanceof String) {

                    String value = question.toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(question);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {
        TextView text, submit;

    }
}




