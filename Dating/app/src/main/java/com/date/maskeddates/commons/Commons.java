package com.date.maskeddates.commons;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.date.maskeddates.adapters.QuestionsListAdapter;
import com.date.maskeddates.classes.ViewPagerCustomDuration;
import com.date.maskeddates.models.Notification;
import com.date.maskeddates.models.Online;
import com.date.maskeddates.models.Plan;
import com.date.maskeddates.models.Questionnaire;
import com.date.maskeddates.models.User;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sonback123456 on 6/3/2018.
 */

public class Commons {
    public static User thisUser = new User();
    public static QuestionsListAdapter.CustomHolder holder;
    public static ArrayList<Questionnaire> questionnaires = new ArrayList<>();
    public static ArrayList<Questionnaire> questionnaires2 = new ArrayList<>();
    public static ViewPagerCustomDuration viewPager = null;
    public static android.support.v7.widget.Toolbar toolbar = null;
    public static User user = new User();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Notification> notifications = new ArrayList<>();
    public static boolean event = false;
    public static boolean event2 = false;
    public static boolean event3 = false;
    public static ArrayList<Integer> notifiedConversationIds = new ArrayList<>();
    public static ArrayList<String> onlineConversationIds = new ArrayList<>();
    public static NotificationManager notificationManager = null;
    public static ArrayList<Online> onlines = new ArrayList<>();
    public static Map mapping=null;
    public static LinearLayout imagePortion=null;
    public static LinearLayout alert_post=null;
    public static LinearLayout qa_sublayout=null;
    public static FrameLayout deletepostbackground=null;
    public static ImageView mapImage=null;
    public static String compressedvideoUrl="";
    public static Bitmap bitmap = null;
    public static LatLng latLng=null;
    public static File destination=null;
    public static String imageUrl = "";
    public static Bitmap map=null;
    public static LatLng requestLatlng=null;
    public static Uri videouri = null;
    public static String videoThumbStr = "";
    public static String mapScreenshotStr = "";
    public static EditText messageArea = null;
    public static ArrayList<String> photoUrls = new ArrayList<>();
    public static boolean edit_profile_flag = false;
    public static TextView textView = null;
    public static String lat = "";
    public static String lng = "";
    public static TextView postDate = null;
    public static NestedScrollView container = null;

    public static long premium_expired = 0;
    public static int max_dates = 3;
    public static boolean premium_expired_flag = false;

    public static ArrayList<Integer> active_userids = new ArrayList<>();
    public static TextView[] profileViews = new TextView[]{};
    public static CircleImageView profileImage;
    public static ImageView backgroundImage;
    public static Plan plan = new Plan();
}
