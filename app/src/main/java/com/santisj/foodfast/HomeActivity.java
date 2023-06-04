package com.santisj.foodfast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.santisj.foodfast.controllers.FirebaseFoodfast;
import com.santisj.foodfast.fragments.CartFragment;
import com.santisj.foodfast.fragments.FoodsFragment;
import com.santisj.foodfast.fragments.SearchFragment;
import com.santisj.foodfast.utils.SaveData;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private SearchFragment searchFragment;
    private FoodsFragment foodsFragment;
    private CartFragment cartFragment;

    private static Animation fadeOut;
    private static Animation fadeIn;
    public static BottomNavigationView navbar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navbar = bottomNavigationView;

        searchFragment = new SearchFragment();
        foodsFragment = new FoodsFragment();
        cartFragment = new CartFragment();

        context = this;

        bottomNavigationView.setSelectedItemId(R.id.menu_item_foods);
        replaceFragment(foodsFragment);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_exit:
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.dialog_exit_title)
                                .setMessage(R.string.dialog_exit_description)
                                .setIcon(R.drawable.ic_logout_green)
                                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        int tableId = EnterActivity.user.getTableId();
                                        FirebaseFoodfast.deleteOrders(tableId);
                                        FirebaseFoodfast.setAvailable(tableId, true);
                                        EnterActivity.user.getOrderMap().clear();
                                        deleteData();
                                        finish();
                                    }
                                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        bottomNavigationView.setSelectedItemId(R.id.menu_item_foods);
                                    }
                                }).show();
                        return true;
                    case R.id.menu_item_search:
                        replaceFragment(searchFragment);
                        return true;
                    case R.id.menu_item_foods:
                        replaceFragment(foodsFragment);
                        return true;
                    case R.id.menu_item_cart:
                        replaceFragment(cartFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void deleteData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SaveData.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
        editor.apply();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public static void goneNavBar() {
        navbar.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                navbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void appearNavBar() {
        navbar.startAnimation(fadeIn);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                navbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}