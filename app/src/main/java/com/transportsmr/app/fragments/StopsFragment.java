package com.transportsmr.app.fragments;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.transportsmr.app.R;
import com.transportsmr.app.fragments.base.BaseStopsRecyclerFragment;

public class StopsFragment extends Fragment {
    private StopsPagerAdapter stopsPagerAdapter;
    private Unbinder unbinder;

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.stops_pager_strip_title)
    PagerTitleStrip pagerTabStrip;

    public StopsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopsPagerAdapter = new StopsPagerAdapter(getChildFragmentManager(), getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stops, container, false);
        unbinder = ButterKnife.bind(this, view);
        //neatest / favorite list
        viewPager.setAdapter(stopsPagerAdapter);
        pagerTabStrip.setTextColor(Color.BLACK);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class StopsPagerAdapter extends FragmentPagerAdapter {
        public static final int NEAREST_TAB_POSITION = 0;
        private final Application app;
        private BaseStopsRecyclerFragment favoriteFragment;
        private BaseStopsRecyclerFragment nearestFragment;

        public StopsPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int position) {
            return (position == NEAREST_TAB_POSITION) ?
                    (nearestFragment = new NearestStopsFragment())
                    : (favoriteFragment = new FavoriteStopsFragment());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseStopsRecyclerFragment fragment = (BaseStopsRecyclerFragment) super.instantiateItem(container, position);
            if (position == NEAREST_TAB_POSITION)
                nearestFragment = fragment;
            else
                favoriteFragment = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == NEAREST_TAB_POSITION) ? app.getString(R.string.stops_nearest) : app.getString(R.string.stops_favorite);
        }
    }
}
