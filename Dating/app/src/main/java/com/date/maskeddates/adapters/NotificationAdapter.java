package com.date.maskeddates.adapters;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.date.maskeddates.Config.MapUtils;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.main.NotificationsActivity;
import com.date.maskeddates.models.Conversation;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by a on 3/28/2017.
 */

public class NotificationAdapter extends BaseAdapter {

    private NotificationsActivity _context;
    private ArrayList<Conversation> _datas = new ArrayList<>();
    private ArrayList<Conversation> _alldatas = new ArrayList<>();

    public NotificationAdapter(NotificationsActivity context){

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
            convertView = inflater.inflate(R.layout.notification_list_item, parent, false);

            holder.picture = (CircleImageView) convertView.findViewById(R.id.picture);
            holder.noticon = (ImageView) convertView.findViewById(R.id.noticon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.datetime = (TextView) convertView.findViewById(R.id.datetime);
            holder.past = (TextView) convertView.findViewById(R.id.past);
            holder.option = (ImageView) convertView.findViewById(R.id.option);
            holder.status = (ImageView) convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/Comfortaa_Bold.ttf");

        final Conversation notification = (Conversation) _datas.get(position);

        holder.name.setText(rename(notification.getSender_name()));
        holder.text.setText(StringEscapeUtils.unescapeJava(notification.getNotitext()));
        try{
            if(MapUtils.notiMap.get(notification.getSender_id()) != null && MapUtils.notiMap.get(notification.getSender_id()).length() > 0) {
                holder.text.setText(StringEscapeUtils.unescapeJava(MapUtils.notiMap.get(notification.getSender_id())));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.name.setTypeface(font);
        holder.text.setTypeface(font);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(notification.getNotitime())));
        holder.datetime.setText(myDate);
        if(Commons.notifiedConversationIds.contains(notification.getSender_id())) {
            notification.setRead("");
        }
        else notification.setRead("true");
        Log.d("Read+++", notification.getRead());
        if(notification.getRead().length() == 0)
            holder.noticon.setVisibility(View.VISIBLE);
        else
            holder.noticon.setVisibility(View.GONE);

        if(Commons.onlineConversationIds.contains(notification.getSender_email())) {
            try{
                notification.setStatus(Commons.onlines.get(Commons.onlineConversationIds.indexOf(notification.getSender_email())).getStatus());
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else notification.setStatus("offline");

        if(notification.getOption().equals("declined")) {
            holder.option.setVisibility(View.VISIBLE);
            holder.option.setImageResource(R.drawable.love_gray);
        }else if(notification.getOption().equals("block")) {
            holder.past.setVisibility(View.VISIBLE);
        }else if(notification.getOption().equals("blocked")) {
            holder.option.setVisibility(View.VISIBLE);
            holder.option.setImageResource(R.drawable.lovelock);
        }else if(notification.getActive() > 0) {
            holder.option.setVisibility(View.VISIBLE);
            holder.option.setImageResource(R.drawable.ondateicon);
        }

        try{
            if(MapUtils.optionMap.get(notification.getSender_id()) != null && MapUtils.optionMap.get(notification.getSender_id()).length() > 0) {
                String option = MapUtils.optionMap.get(notification.getSender_id());
                if(option.equals("declined")) {
                    holder.option.setVisibility(View.VISIBLE);
                    holder.option.setImageResource(R.drawable.love_gray);
                }else if(option.equals("block")) {
                    holder.past.setVisibility(View.VISIBLE);
                }else if(option.equals("blocked")) {
                    holder.option.setVisibility(View.VISIBLE);
                    holder.option.setImageResource(R.drawable.lovelock);
                }else if(notification.getActive() > 0) {
                    holder.option.setVisibility(View.VISIBLE);
                    holder.option.setImageResource(R.drawable.ondateicon);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(notification.getStatus().equals("online")) {
            holder.status.setVisibility(View.VISIBLE);
        }
        else
            holder.status.setVisibility(View.GONE);

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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.readNotification(notification);
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
        TextView name, text, datetime, past;
        ImageView option, status;
        ImageView noticon;
    }
}



