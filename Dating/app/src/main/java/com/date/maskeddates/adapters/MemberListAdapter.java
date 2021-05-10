package com.date.maskeddates.adapters;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.date.maskeddates.R;
import com.date.maskeddates.main.UsersActivity;
import com.date.maskeddates.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by a on 3/28/2017.
 */

public class MemberListAdapter extends BaseAdapter {

    private UsersActivity _context;
    private ArrayList<User> _datas = new ArrayList<>();
    private ArrayList<User> _alldatas = new ArrayList<>();

    public MemberListAdapter(UsersActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<User> datas) {

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
            convertView = inflater.inflate(R.layout.userlist_item, parent, false);

            holder.picture = (CircleImageView) convertView.findViewById(R.id.picture);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.age = (TextView) convertView.findViewById(R.id.age);
            holder.gender = (TextView) convertView.findViewById(R.id.gender);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.male = (ImageView) convertView.findViewById(R.id.maleicon);
            holder.female = (ImageView) convertView.findViewById(R.id.femaleicon);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final User user = (User) _datas.get(position);

        holder.name.setText(user.get_name());
        holder.age.setText(user.get_age());
        holder.address.setText(user.get_address());
        if(user.get_gender().toLowerCase().equals("male")){
            holder.female.setVisibility(View.GONE);
        }else holder.male.setVisibility(View.GONE);
        holder.gender.setText(user.get_gender());

        Picasso.with(_context)
                .load(user.get_fbPhoto())
                .transform(new RoundedCornersTransformation(30, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.picture);

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

            for (User user : _alldatas){

                if (user instanceof User) {

                    String value = ((User) user).get_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(user);
                    }
                    else {
                        value = ((User) user).get_age().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(user);
                        }
                        else {
                            value = ((User) user).get_gender().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(user);
                            }
                            else {
                                value = ((User) user).get_address().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(user);
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        ImageView male, female;
        CircleImageView picture;
        TextView name, age, address, gender;

    }
}



