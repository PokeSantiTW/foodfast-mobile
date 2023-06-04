package com.santisj.foodfast.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.santisj.foodfast.HomeActivity;
import com.santisj.foodfast.R;
import com.santisj.foodfast.objects.FoodItem;
import com.squareup.picasso.Picasso;

public class FoodDetailsFragment extends Fragment {

    private FoodItem foodItem;
    private int itemQuantity;

    private ImageView image;
    private TextView title;
    private TextView subtitle;
    private TextView price;
    private TextView quantity;
    private ImageButton closeButton, addButton, removeButton;

    public FoodDetailsFragment() {
        // Required empty public constructor
    }

    public static FoodDetailsFragment newInstance(FoodItem item, int actualQuantity) {
        FoodDetailsFragment fragment = new FoodDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("foodItem", item);;
        args.putInt("itemQuantity", actualQuantity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.appearNavBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_food_details,
                container, false);

        image = view.findViewById(R.id.details_image);
        title = view.findViewById(R.id.details_title);
        subtitle = view.findViewById(R.id.details_subtitle);
        price = view.findViewById(R.id.details_price);
        quantity = view.findViewById(R.id.details_quantity);
        closeButton = view.findViewById(R.id.details_close);
        addButton = view.findViewById(R.id.details_add);
        removeButton = view.findViewById(R.id.details_remove);

        if (getArguments() != null) {
            foodItem = (FoodItem) getArguments().getSerializable("foodItem");
            itemQuantity = (Integer) getArguments().getInt("itemQuantity");

            if (foodItem.getImage() == null) {
                image.setImageResource(R.drawable.navigation_placeholder);
            } else {
                Picasso.with(getContext())
                        .load(foodItem.getImage())
                        .placeholder(R.drawable.navigation_loading)
                        .error(R.drawable.navigation_error)
                        .into(image);
            }
            title.setText(foodItem.getName());
            subtitle.setText(foodItem.getDescription());
            price.setText(foodItem.getPrice() + "€");
            quantity.setText(String.valueOf(itemQuantity));

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation slideDownClose = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
                    slideDownClose.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(FoodDetailsFragment.this)
                                    .commit();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    getView().startAnimation(slideDownClose);
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantityInText = Integer.parseInt(quantity.getText().toString());
                    if (quantityInText <= 99 && quantityInText >= 0) {
                        int newQuantity = quantityInText + 1;
                        FoodsFragment.sendOrder(newQuantity, foodItem, getContext());
                        quantity.setText(String.valueOf(newQuantity));
                    }
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }
}