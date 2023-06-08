package com.santisj.foodfast.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.santisj.foodfast.EnterActivity;
import com.santisj.foodfast.HomeActivity;
import com.santisj.foodfast.R;
import com.santisj.foodfast.controllers.FirebaseFoodfast;
import com.santisj.foodfast.enums.FoodIcons;
import com.santisj.foodfast.objects.FoodItem;
import com.santisj.foodfast.objects.FoodSection;
import com.santisj.foodfast.objects.Order;
import com.santisj.foodfast.objects.User;
import com.santisj.foodfast.utils.FoodItemSerializer;
import com.santisj.foodfast.utils.SaveData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FoodsFragment extends Fragment {

    public static final String EXTRA_OBJ_ITEM = "com.santisj.foodsFragment.obj_item";

    private RecyclerView rvFoodsSections, rvFoodsItems;

    private FirebaseRecyclerAdapter<FoodSection, SectionViewHolder> adapter;
    private FirebaseRecyclerAdapter<FoodItem, ItemViewHolder> adapterItem;
    private DatabaseReference databaseReference;
    private boolean sectionsDone;

    public FoodsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sectionsDone = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_foods, container, false);
        rvFoodsSections = (RecyclerView) view.findViewById(R.id.rv_sections);
        rvFoodsItems = (RecyclerView) view.findViewById(R.id.rv_foods_items);
        rvFoodsItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFoodsSections.setLayoutManager(new NpaLinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        databaseReference = FirebaseFoodfast.sections();

        FirebaseRecyclerOptions<FoodSection> options =
                new FirebaseRecyclerOptions.Builder<FoodSection>()
                        .setQuery(databaseReference, FoodSection.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FoodSection, SectionViewHolder>(options) {
            private int selectedPosition = 0;

            @Override
            protected void onBindViewHolder(@NonNull SectionViewHolder holder, int position, @NonNull FoodSection model) {
                holder.bindData(model);

                if (selectedPosition == position) {
                    holder.getSectionIcon().setColorFilter(ContextCompat.getColor(getContext(),
                            R.color.section_selected));
                } else {
                    holder.getSectionIcon().setColorFilter(ContextCompat.getColor(getContext(),
                            R.color.section_unselected));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int actualPosition = holder.getAbsoluteAdapterPosition();
                        int previousSelectedPosition = selectedPosition;
                        selectedPosition = actualPosition;

                        changeSectionItems(getRef(actualPosition).getKey());

                        notifyItemChanged(previousSelectedPosition);
                        notifyItemChanged(selectedPosition);
                    }
                });
            }

            @NonNull
            @Override
            public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_foods_sections, parent, false);
                sectionsDone = true;
                return new SectionViewHolder(view);
            }

            @NonNull
            @Override
            public ObservableSnapshotArray<FoodSection> getSnapshots() {
                return super.getSnapshots();
            }
        };

        rvFoodsSections.setAdapter(adapter);

        FirebaseDatabase.getInstance(FirebaseFoodfast.databaseURL).getReference()
                .child("restaurant").child("sections_food").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            changeSectionItems(snap.getKey());
                            break;
                        }
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        if (sectionsDone) {
            adapterItem.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapterItem.stopListening();
    }

    public void changeSectionItems(String sectionId) {

        Query keyQuery = FirebaseFoodfast.sectionItems(sectionId);
        DatabaseReference dataRef = FirebaseFoodfast.foodsReference();

        FirebaseRecyclerOptions<FoodItem> optionsItems =
                new FirebaseRecyclerOptions.Builder<FoodItem>()
                        .setIndexedQuery(keyQuery, dataRef, FoodItem.class)
                        .setLifecycleOwner(this)
                        .build();

        adapterItem = new FirebaseRecyclerAdapter<FoodItem, ItemViewHolder>(optionsItems) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull FoodItem model) {
                holder.setItemQuantityText(getQuantityInMap(model));
                holder.bindData(model);

                if (model.isIsAvailable()) {
                    holder.itemAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                            if (actualQuantity <= 99 && actualQuantity >= 0) {
                                int newQuantity = actualQuantity + 1;
                                sendOrder(newQuantity, model, getContext());
                                saveData();
                                holder.setItemQuantityText(newQuantity);
                            }
                        }
                    });

                    holder.itemRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                            if (actualQuantity > 0) {
                                int newQuantity = actualQuantity - 1;
                                sendOrder(newQuantity, model, getContext());
                                saveData();
                                holder.setItemQuantityText(newQuantity);
                            }
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                            FoodDetailsFragment detailsFragment = FoodDetailsFragment.newInstance(model, actualQuantity);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_up, 0, 0,
                                    R.anim.slide_down);

                            HomeActivity.goneNavBar();

                            fragmentTransaction.replace(R.id.layout_app, detailsFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                                @Override
                                public void onBackStackChanged() {
                                    holder.setItemQuantityText(getQuantityInMap(model));
                                }
                            });
                        }
                    });
                }
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_foods_food,
                        parent, false);
                return new ItemViewHolder(view);
            }
        };

        rvFoodsItems.setAdapter(adapterItem);
    }

    public static void sendOrder(int newQuantity, FoodItem model, Context context) {
        Map<String, Order> orders = EnterActivity.user.getOrderMap();

        for (Map.Entry<String, Order> entry : orders.entrySet()) {
            if (entry.getValue().getItem().getItemId().equals(model.getItemId())) {
                entry.getValue().setQuantity(newQuantity);
                FirebaseFoodfast.addOrder(EnterActivity.user.getTableId(), (Order) entry.getValue(), context);
                return;
            }
        }

        Order order = new Order(model, newQuantity);
        orders.put(model.getItemId(), order);
        FirebaseFoodfast.addOrder(EnterActivity.user.getTableId(), order, context);
    }

    public static int getQuantityInMap(FoodItem model) {
        Map<String, Order> orders = EnterActivity.user.getOrderMap();

        for (Map.Entry<String, Order> entry : orders.entrySet()) {
            if (entry.getValue().getItem().getItemId().equals(model.getItemId())) {
                return entry.getValue().getQuantity();
            }
        }
        return 0;
    }

    public void saveData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(
                SaveData.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(FoodItem.class, new FoodItemSerializer())
                .create();
        String json = gson.toJson(EnterActivity.user);
        editor.putString("UserLogged", json);
        editor.apply();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        private TextView sectionName;
        private ImageView sectionIcon;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionName = itemView.findViewById(R.id.card_foods_section_title);
            sectionIcon = itemView.findViewById(R.id.card_foods_section_icon);
        }

        public void bindData(FoodSection section) {
            sectionName.setText(section.getSectionName());
            int resourceId = itemView.getResources().getIdentifier(section.getSectionIcon(),
                    "drawable", itemView.getContext().getPackageName());
            sectionIcon.setImageResource(resourceId);
        }

        public ImageView getSectionIcon() {
            return sectionIcon;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemIcon;
        private TextView itemName;
        private TextView itemPrice;
        private TextView itemQuantity;
        private ImageButton itemAdd;
        private ImageButton itemRemove;
        private ConstraintLayout constraintLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemIcon = itemView.findViewById(R.id.card_foods_food_icon);
            itemName = itemView.findViewById(R.id.card_foods_food_title);
            itemPrice = itemView.findViewById(R.id.card_foods_food_price);
            itemQuantity = itemView.findViewById(R.id.card_foods_food_quantity);
            itemAdd = (ImageButton) itemView.findViewById(R.id.card_foods_food_add);
            itemRemove = (ImageButton) itemView.findViewById(R.id.card_foods_food_remove);
            constraintLayout = itemView.findViewById(R.id.constraint_foods_food);
        }

        public void bindData(FoodItem item) {

            String imageURL = "";
            if (item.getImage() != null) {
                imageURL = item.getImage();
                Picasso.with(itemView.getContext())
                        .load(imageURL)
                        .error(R.drawable.navigation_error)
                        .placeholder(R.drawable.navigation_loading)
                        .into(itemIcon);
            } else {
                itemIcon.setImageResource(R.drawable.navigation_placeholder);
            }

            if (item.getName() != null) {
                itemName.setText(item.getName());
            }

            if (!Double.isNaN(item.getPrice())) {
                itemPrice.setText(item.getPrice() + "€");
            }

            if (!item.isIsAvailable()) {
                itemAdd.setVisibility(View.GONE);
                itemRemove.setVisibility(View.GONE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.card_foods_food_quantity, ConstraintSet.RIGHT, R.id.constraint_foods_food, ConstraintSet.RIGHT, 10);
                constraintSet.connect(R.id.card_foods_food_quantity, ConstraintSet.LEFT, R.id.constraint_foods_food, ConstraintSet.LEFT, 10);
                constraintSet.connect(R.id.card_foods_food_quantity, ConstraintSet.TOP, R.id.constraint_foods_food, ConstraintSet.TOP, 40);
                constraintSet.applyTo(constraintLayout);

                itemQuantity.setText(R.string.not_available);
            }
        }

        public TextView getItemQuantity() {
            return itemQuantity;
        }

        public void setItemQuantityText(int quantity) {
            this.itemQuantity.setText(String.valueOf(quantity));
        }

        public ImageButton getItemAdd() {
            return itemAdd;
        }

        public ImageButton getItemRemove() {
            return itemRemove;
        }
    }

    public static class NpaGridLayoutManager extends GridLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public NpaGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public NpaGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public NpaGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }
    }

    public static class NpaLinearLayoutManager extends LinearLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public NpaLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }
    }
}