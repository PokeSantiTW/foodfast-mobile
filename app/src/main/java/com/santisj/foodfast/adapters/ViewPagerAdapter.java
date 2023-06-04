package com.santisj.foodfast.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.santisj.foodfast.R;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    private final int[] images = {
            R.drawable.foodfast512v1,
            R.drawable.scanqr,
            R.drawable.personeating
    };

    private final int[] onboarding_titles = {
        R.string.txt_onBoard_welcome,
        R.string.txt_onBoard_scanQR,
        R.string.txt_onBoard_finish
    };

    private final int[] descriptions = {
        R.string.txt_onBoard_desc_welcome,
        R.string.txt_onBoard_desc_scanQR,
        R.string.txt_onBoard_desc_finish
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return onboarding_titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_onboarding, container, false);

        ImageView onBoardingImage = (ImageView) view.findViewById(R.id.img_logoFood);
        TextView onBoardingTitle = (TextView) view.findViewById(R.id.txt_slider_title);
        TextView onBoardingDescription = (TextView) view.findViewById(R.id.txt_slider_description);

        onBoardingImage.setImageResource(images[position]);
        onBoardingTitle.setText(onboarding_titles[position]);
        onBoardingDescription.setText(descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
