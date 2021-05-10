package com.date.maskeddates.main;

import android.app.ProgressDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.date.maskeddates.R;
import com.date.maskeddates.carousel.CarouselPagerAdapter;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Membership;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MembershipActivity extends AppCompatActivity {

    public final static int LOOPS = 1000;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    public static int count = 2; //ViewPager items size

    public static int FIRST_PAGE = 1;
    ArrayList<Membership> memberships = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setMessage("Wait");

        pager = (ViewPager) findViewById(R.id.viewpager);

        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels * 3/ 20) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerAdapter(this, memberships, getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);

//        getPlans();

        float[] prices = {21.99f, 27.99f, 42.99f};
        int[] months = {3, 6, 12};
        String[] products = {"111", "222", "333"};

        for(int i=0; i<prices.length; i++){
            Membership membership = new Membership();
            membership.setAmount(prices[i]);
            membership.setValidity(months[i]);
            membership.setProduct_id(products[i]);
            memberships.add(membership);
        }

        adapter = new CarouselPagerAdapter(this, memberships, getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getPlans(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ReqConst.SERVER_URL + "getMemberships";
        pd.show();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseResponse(String json){
        pd.dismiss();
        try{
            JSONObject response = new JSONObject(json);
            JSONArray data = response.getJSONArray("plans");
            for(int i=0; i<data.length(); i++){
                Membership membership = new Membership();
                membership.set_idx(Integer.parseInt(data.getJSONObject(i).getString("id")));
                membership.setValidity(Integer.parseInt(data.getJSONObject(i).getString("validity")));
                membership.setAmount(Float.parseFloat(data.getJSONObject(i).getString("amount")));
                membership.setProduct_id(data.getJSONObject(i).getString("product_id"));
                if(membership.getAmount()!= 0.0f)
                    memberships.add(membership);
            }
            adapter = new CarouselPagerAdapter(this, memberships, getSupportFragmentManager());
            pager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}





















