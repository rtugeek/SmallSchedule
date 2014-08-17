package com.free.schedule;

import com.free.schedule.view.PageControlView;
import com.free.schedule.view.XyzGallery;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class GuideYou extends Activity implements OnItemSelectedListener {

    private View mViewDoor = null;
    private ImageView mImageLeft = null;
    private ImageView mImageRight = null;
    private XyzGallery mGallery = null;
    private GalleryAdapter mAdapter = null;
    private PageControlView mIndicateView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whats_news);

        mGallery = (XyzGallery) findViewById(R.id.what_news_gallery);
        mAdapter = new GalleryAdapter(this);
        mGallery.setFadingEdgeLength(0);
        mGallery.setSpacing(60);
        mGallery.setAdapter(mAdapter);
        mGallery.setOnItemSelectedListener(this);

        mIndicateView = (PageControlView) findViewById(R.id.what_news_page_control);
        mIndicateView.setIndication(mGallery.getCount(), 0);
        mViewDoor = findViewById(R.id.mm_door);
    }

    private class GalleryAdapter extends BaseAdapter implements
            OnClickListener, AnimationListener {

        private Context mContext;
        private LayoutInflater mInfater = null;

        private int[] mImgs = new int[] {
                R.layout.whats_news_gallery_fornew_one,
                R.layout.whats_news_gallery_fornew_two,
                R.layout.whats_news_gallery_fornew_three,
                R.layout.whats_news_gallery_fornew_last};

        public GalleryAdapter(Context ctx) {
            mContext = ctx;
            mInfater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImgs.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return Integer.valueOf(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = mInfater.inflate(mImgs[position], null);
                if (position == mImgs.length - 1) {
                	GuideYou.this.finish();
                }
            }
            return convertView;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mViewDoor.setVisibility(View.VISIBLE);
            mImageLeft.startAnimation(setAnimation(R.anim.slide_left));
            mImageRight.startAnimation(setAnimation(R.anim.slide_right));
        }

        private Animation setAnimation(int resId) {
            Animation anim = AnimationUtils.loadAnimation(mContext, resId);
            anim.setAnimationListener(this);
            return anim;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
            mImageLeft.setVisibility(View.GONE);
            mImageRight.setVisibility(View.GONE);
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            Editor edit = sp.edit();
            edit.putBoolean("first", false);
            edit.commit();
            GuideYou.this.finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        if (mIndicateView != null) {
            mIndicateView.setIndication(parent.getCount(), position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

}
