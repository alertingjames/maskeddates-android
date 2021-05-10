package com.date.maskeddates.adapters;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.date.maskeddates.R;
import com.date.maskeddates.main.EndDatesActivity;
import com.date.maskeddates.models.Conversation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by a on 3/28/2017.
 */

public class ActiveDatesAdapter extends BaseAdapter {

    private EndDatesActivity _context;
    private ArrayList<Conversation> _datas = new ArrayList<>();
    private ArrayList<Conversation> _alldatas = new ArrayList<>();

    public ActiveDatesAdapter(EndDatesActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Conversation> datas) {

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
            convertView = inflater.inflate(R.layout.active_dates_list_item, parent, false);

            holder.picture = (CircleImageView) convertView.findViewById(R.id.picture);
            holder.button = (TextView) convertView.findViewById(R.id.button);
            holder.name = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/Comfortaa_Bold.ttf");

        final Conversation notification = (Conversation) _datas.get(position);

        holder.name.setText(rename(notification.getSender_name()));
        holder.name.setTypeface(font);

        if(notification.getUnraveled() < 4)
            Picasso.with(_context)
                    .load(notification.getSender_photo())
                    .transform(new BlurTransformation(_context, 25, 1))
                    .error(R.mipmap.appicon)
                    .placeholder(R.mipmap.appicon)
                    .into(holder.picture);
        else Picasso.with(_context)
                .load(notification.getSender_photo())
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into(holder.picture);

        if(notification.getOption().equals("block")) {
            holder.button.setText("Date again");
            holder.button.setBackgroundResource(R.drawable.red_light_fill_radio_round);
            holder.button.setTextColor(_context.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.button.setText("End date");
            holder.button.setBackgroundResource(R.drawable.red_round);
            holder.button.setTextColor(Color.WHITE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.getActive() == 1) {
                    _context.endDate(notification);
                }
                else if(notification.getOption().equals("block") && notification.getActive() == 0)
                {
                    _context.dateAgain(notification);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getActive() == 1) {
                    _context.endDate(notification);
                }
                else if(notification.getOption().equals("block") && notification.getActive() == 0)
                {
                    _context.dateAgain(notification);
                }
            }
        });

        return convertView;
    }

    private String rename(String name){
        String firstName = "", lastName = "";
        if(name.contains(" ")){
            if(name.indexOf(" ") >= 1) {
                firstName = name.substring(0, name.indexOf(" "));
                lastName=name.substring(name.indexOf(" ")+1,name.length());
            }
            else {
                firstName=name;
                lastName="";
            }
        }else {
            firstName=name;
            lastName="";
        }
        if(lastName.length() != 0)
            return firstName + " " + lastName.substring(0, 1) + ".";
        else return firstName;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (Conversation notification : _alldatas){

                if (notification instanceof Conversation) {

                    String value = ((Conversation) notification).getSender_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(notification);
                    }
                    else {
                        value = ((Conversation) notification).getNotitime().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(notification);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        CircleImageView picture;
        TextView name;
        TextView button;
    }
}



