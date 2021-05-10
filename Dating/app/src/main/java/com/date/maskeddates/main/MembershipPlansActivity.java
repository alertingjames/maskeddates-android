package com.date.maskeddates.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.date.maskeddates.Config.SkuIds;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.vivek.inapppurchaselib.utils.IabHelper;
import app.vivek.inapppurchaselib.utils.Purchase;
import app.vivek.inapppurchaselib.v3.VKInAppConstants;
import app.vivek.inapppurchaselib.v3.VKInAppProperties;
import app.vivek.inapppurchaselib.v3.VKInAppPurchaseActivity;
import app.vivek.inapppurchaselib.v3.VKLogger;

public class MembershipPlansActivity extends AppCompatActivity {

    ImageButton savorButton, popularButton, standardButton;
    LinearLayout savor, popular, standard;
    TextView buyButton;
    LinearLayout[] layouts;
    ImageButton[] buttons;
    String[] productIDs;
    int[] expiredMonths;
    int[] matches;
    int[] premiumIndexs;
    int index = 0;
    LoadingDots progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_plans);

        VKInAppProperties.BASE_64_KEY = "";

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");

        ((TextView)findViewById(R.id.month1)).setTypeface(font);
        ((TextView)findViewById(R.id.months1)).setTypeface(font);
        ((TextView)findViewById(R.id.matches1)).setTypeface(font);
        ((TextView)findViewById(R.id.amount1)).setTypeface(font);
        ((TextView)findViewById(R.id.m1)).setTypeface(font);
        ((TextView)findViewById(R.id.d1)).setTypeface(font);

        ((TextView)findViewById(R.id.month2)).setTypeface(font);
        ((TextView)findViewById(R.id.months2)).setTypeface(font);
        ((TextView)findViewById(R.id.matches2)).setTypeface(font);
        ((TextView)findViewById(R.id.amount2)).setTypeface(font);
        ((TextView)findViewById(R.id.m2)).setTypeface(font);
        ((TextView)findViewById(R.id.d2)).setTypeface(font);

        ((TextView)findViewById(R.id.month3)).setTypeface(font);
        ((TextView)findViewById(R.id.months3)).setTypeface(font);
        ((TextView)findViewById(R.id.matches3)).setTypeface(font);
        ((TextView)findViewById(R.id.amount3)).setTypeface(font);
        ((TextView)findViewById(R.id.m3)).setTypeface(font);
        ((TextView)findViewById(R.id.d3)).setTypeface(font);

        ((TextView)findViewById(R.id.title)).setTypeface(font);
        ((TextView)findViewById(R.id.subtitle)).setTypeface(font);

        ((TextView)findViewById(R.id.savorText)).setTypeface(font);
        ((TextView)findViewById(R.id.popularText)).setTypeface(font);
        ((TextView)findViewById(R.id.standardText)).setTypeface(font);

        ((TextView)findViewById(R.id.month1)).setText(String.valueOf(Commons.plan.getMonths3()));
        ((TextView)findViewById(R.id.month2)).setText(String.valueOf(Commons.plan.getMonths2()));
        ((TextView)findViewById(R.id.month3)).setText(String.valueOf(Commons.plan.getMonths1()));

        ((TextView)findViewById(R.id.matches1)).setText(String.valueOf(Commons.plan.getDates3()));
        ((TextView)findViewById(R.id.amount1)).setText(String.valueOf(Commons.plan.getPrice3()));

        ((TextView)findViewById(R.id.matches2)).setText(String.valueOf(Commons.plan.getDates2()));
        ((TextView)findViewById(R.id.amount2)).setText(String.valueOf(Commons.plan.getPrice2()));

        ((TextView)findViewById(R.id.matches3)).setText(String.valueOf(Commons.plan.getDates1()));
        ((TextView)findViewById(R.id.amount3)).setText(String.valueOf(Commons.plan.getPrice1()));

        progressBar = (LoadingDots)findViewById(R.id.dots);

        savor = (LinearLayout)findViewById(R.id.savor);
        popular = (LinearLayout)findViewById(R.id.popular);
        standard = (LinearLayout)findViewById(R.id.standard);

        savorButton = (ImageButton)findViewById(R.id.savorButton);
        popularButton = (ImageButton)findViewById(R.id.popularButton);
        standardButton = (ImageButton)findViewById(R.id.standardButton);

        layouts = new LinearLayout[]{savor, popular, standard};
        buttons = new ImageButton[]{savorButton, popularButton, standardButton};
        buttons[0].setBackgroundResource(R.drawable.radioicon2);

        productIDs = new String[]{"111", "222", "333"};
        expiredMonths = new int[]{Commons.plan.getMonths3(), Commons.plan.getMonths2(), Commons.plan.getMonths1()};
        matches = new int[]{Commons.plan.getDates3(), Commons.plan.getDates2(), Commons.plan.getDates1()};
        premiumIndexs = new int[]{3,2,1};

        for(int i=0; i<layouts.length; i++){
            int finalI = i;
            layouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearSelected();
                    buttons[finalI].setBackgroundResource(R.drawable.radioicon2);
                    index = finalI;
                }
            });
        }

        buyButton = ((TextView)findViewById(R.id.buyButton));
        buyButton.setTypeface(font);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProduct(productIDs[index]);
            }
        });
    }

    private void clearSelected(){
        for(int i=0; i<buttons.length; i++){
            buttons[i].setBackgroundResource(R.drawable.radioicon);
        }
    }

    private void buyProduct(String productID){
        Intent mIntent=new Intent(MembershipPlansActivity.this, VKInAppPurchaseActivity.class);
        mIntent.putExtra(VKInAppConstants.INAPP_SKU_ID, SkuIds.SKU_INAPP_TEST);
        mIntent.putExtra(VKInAppConstants.INAPP_SKU_TYPE,IabHelper.ITEM_TYPE_INAPP);
        mIntent.putExtra(VKInAppConstants.INAPP_PRODUCT_TYPE,VKInAppConstants.INAPP_CONSUMABLE);
        startActivityForResult(mIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==101){
                String mInapSkuId=data.getExtras().getString(VKInAppConstants.INAPP_SKU_ID);
                if(data.getExtras().getString(VKInAppConstants.INAPP_PURCHASE_TOKEN)!=null)
                    VKLogger.e("Purchase Token",data.getExtras().getString(VKInAppConstants.INAPP_PURCHASE_TOKEN));
                Purchase purchaseReceipt;
                if(data.getExtras().containsKey(VKInAppConstants.INAPP_PURCHASE_INFO)){
                    purchaseReceipt= (Purchase) data.getExtras().getSerializable(VKInAppConstants.INAPP_PURCHASE_INFO);
                    VKLogger.e("OrderId:- "+purchaseReceipt.getOrderId()+"\n Token:-"+purchaseReceipt.getToken());
                }

                // {"productId":"appsinvo_day_sub_test","type":"subs","price":"₹ 10.00","price_amount_micros":10000000,"price_currency_code":"INR","title":"OneDaySubscription (InApp Test)","description":"Testing Purpose"}
                int value=data.getExtras().getInt("response_code");
                switch (value) {
                    case VKInAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY:
                        responseAlertDialog("You have successfully consume "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY:
                        responseAlertDialog("You have successfully purchase "+mInapSkuId+" product.");
//                        upgradeUser();
                        break;
                    case VKInAppConstants.RESULT_PROPUR_SUCC_CONSUME_FAIL:
                        responseAlertDialog("You have failed to consume "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.RESULT_SUBS_CONTINUE:
                        responseAlertDialog("Your subsription is continue for id "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.ERROR_DEVICE_NOT_SUPPORT_SUBS:
                        responseAlertDialog(getString(R.string.error_msg_not_support_subs));
                        break;
                    case VKInAppConstants.ERROR_BASE_64_KEY_NOT_SETUP:
                        responseAlertDialog(getString(R.string.error_msg_base64key));
//                        upgradeUser();
                        break;
                    case VKInAppConstants.ERROR_PACKAGE_NAME:
                        responseAlertDialog(getString(R.string.error_msg_package_name));
                        break;
                    case VKInAppConstants.ERROR_DEVICE_NOT_SUPPORT_INAPP:
                        responseAlertDialog(getString(R.string.error_msg_not_support_inapp));
                        break;
                    case VKInAppConstants.ERROR_PRODUCT_PURCHASE:
                        responseAlertDialog(getString(R.string.error_msg_in_purchase));
                        break;

                    default:
                        responseAlertDialog("Error is occured "+value);
                        break;
                }


            }
        }
    }


    /**
     * Show the InApp purchase status dialog
     * @param message
     */
    private void responseAlertDialog(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setCancelable(false);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        bld.create().show();
    }

    public void upgradeUser() {

        long time = new Date().getTime() + 86400000 * 30 * expiredMonths[index];
        String url = ReqConst.SERVER_URL + "upgradeUser";

        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("expired_time", String.valueOf(time) + "_" + String.valueOf(premiumIndexs[index])); Log.d("EXPIRED===>", params.get("expired_time"));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void parseRegisterResponse(String json) {

        progressBar.setVisibility(View.GONE);

        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 0) {
                showToast("You have been upgraded.");
            }
            else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}


































