package com.date.maskeddates.carousel;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.date.maskeddates.R;
import com.date.maskeddates.main.MembershipActivity;
import com.date.maskeddates.models.Membership;

import java.util.ArrayList;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private MembershipActivity context;
    private FragmentManager fragmentManager;
    private float scale;
    private ArrayList<Membership> memberships = new ArrayList<>();

    public CarouselPagerAdapter(MembershipActivity context, ArrayList<Membership> data, FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.memberships.addAll(data);
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == MembershipActivity.FIRST_PAGE)
                scale = BIG_SCALE;
            else
                scale = SMALL_SCALE;

  //          position = position % getCount();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ItemFragment.newInstance(context, position, memberships.get(position), scale);
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            count = MembershipActivity.count * MembershipActivity.LOOPS;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        count = memberships.size();
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }
}
