package com.santisj.foodfast.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.santisj.foodfast.EnterActivity;
import com.santisj.foodfast.HomeActivity;
import com.santisj.foodfast.R;
import com.santisj.foodfast.controllers.FirebaseFoodfast;
import com.santisj.foodfast.objects.FoodItem;
import com.santisj.foodfast.utils.FoodItemSerializer;
import com.santisj.foodfast.utils.SaveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private RecyclerView rvSearch;
    private SearchView searchView;

    private CustomFirebaseRecyclerAdapter adapter;
    private DatabaseReference searchReference;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.setQuery("", true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_search, container, false);

        searchReference = FirebaseFoodfast.foodsReference();

        rvSearch = (RecyclerView) view.findViewById(R.id.search_rv);
        rvSearch.setLayoutManager(new FoodsFragment.NpaGridLayoutManager(getContext(), 2));
        searchView = view.findViewById(R.id.search_search);

        FirebaseRecyclerOptions<FoodItem> options =
                new FirebaseRecyclerOptions.Builder<FoodItem>()
                        .setQuery(searchReference, FoodItem.class)
                        .build();

        adapter = new CustomFirebaseRecyclerAdapter(options);

        rvSearch.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });

        return view;
    }

    public class CustomFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<FoodItem, FoodsFragment.ItemViewHolder> implements Filterable {

        private List<FoodItem> filteredItems;

        public CustomFirebaseRecyclerAdapter(@NonNull FirebaseRecyclerOptions<FoodItem> options) {
            super(options);
            this.filteredItems = new ArrayList<>();
        }

        @Override
        protected void onBindViewHolder(@NonNull FoodsFragment.ItemViewHolder holder, int position, @NonNull FoodItem model) {
            FoodItem item = filteredItems.get(position);
            holder.setItemQuantityText(FoodsFragment.getQuantityInMap(filteredItems.get(position)));
            holder.bindData(item);

            if (model.isIsAvailable()) {
                holder.getItemAdd().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                        if (actualQuantity <= 99 && actualQuantity >= 0) {
                            int newQuantity = actualQuantity + 1;
                            FoodsFragment.sendOrder(newQuantity, item, getContext());
                            saveData();
                            holder.setItemQuantityText(newQuantity);
                        }
                    }
                });

                holder.getItemRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                        if (actualQuantity > 0) {
                            int newQuantity = actualQuantity - 1;
                            FoodsFragment.sendOrder(newQuantity, item, getContext());
                            saveData();
                            holder.setItemQuantityText(newQuantity);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int actualQuantity = Integer.parseInt(holder.getItemQuantity().getText().toString());
                        FoodDetailsFragment detailsFragment = FoodDetailsFragment.newInstance(item, actualQuantity);
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_up, 0, 0,
                                R.anim.slide_down);

                        HomeActivity.goneNavBar();

                        fragmentTransaction.replace(R.id.layout_app, detailsFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        }

        @NonNull
        @Override
        public FoodsFragment.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_foods_food, parent, false);
            return new FoodsFragment.ItemViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return filteredItems.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String query = constraint.toString().toLowerCase();
                    FilterResults results = new FilterResults();
                    int position = 0;

                    if (query.isEmpty()) {
                        results.values = getSnapshots();
                    } else {
                        List<FoodItem> filteredList = new ArrayList<>();
                        for (FoodItem item : getSnapshots()) {
                            if (item.getName().toLowerCase().contains(query)) {
                                filteredList.add(item);
                            }
                        }
                        results.values = filteredList;
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredItems = (List<FoodItem>) results.values;
                    notifyDataSetChanged();
                }
            };
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
    }

}