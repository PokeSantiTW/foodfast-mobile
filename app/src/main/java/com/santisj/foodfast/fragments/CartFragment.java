package com.santisj.foodfast.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.santisj.foodfast.EnterActivity;
import com.santisj.foodfast.R;
import com.santisj.foodfast.controllers.FirebaseFoodfast;
import com.santisj.foodfast.objects.FoodItem;
import com.santisj.foodfast.objects.Kitchen;
import com.santisj.foodfast.objects.Order;
import com.santisj.foodfast.utils.FoodItemSerializer;
import com.santisj.foodfast.utils.SaveData;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvOrders;
    private TextView txtPrice, txtTable;
    private EditText note;
    private Button sendButton;

    private DatabaseReference orderReference;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    private DecimalFormat df;
    private double totalPrice = 0.0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        totalPrice = 0.0;
        System.out.println("ON START");
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("ON STOP");
        adapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cart, container,
                false);

        df = new DecimalFormat("#.00");

        rvOrders = (RecyclerView) view.findViewById(R.id.cart_orders);
        txtPrice = view.findViewById(R.id.cart_price);
        txtTable = view.findViewById(R.id.cart_table);
        note = view.findViewById(R.id.cart_note);
        sendButton = view.findViewById(R.id.cart_button);

//        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, false));
        rvOrders.setLayoutManager(new FoodsFragment.NpaLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        orderReference = FirebaseFoodfast.orders(EnterActivity.user.getTableId());

        String numMesa = "Número de mesa: <b>" + EnterActivity.user.getTableId() + "</b>";
        txtTable.setText(Html.fromHtml(numMesa, Html.FROM_HTML_MODE_LEGACY));

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(orderReference, Order.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                totalPrice += model.getFinalPrice();
                String total = "Precio total: <b>" + df.format(totalPrice) + "€</b>";
                txtPrice.setText(Html.fromHtml(total, Html.FROM_HTML_MODE_LEGACY));

                holder.bindData(model);
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.rv_cart_food, parent,
                        false);
                return new OrderViewHolder(view);
            }
        };

        rvOrders.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getItemCount() == 0) {
                    emptyCartDialog();
                    return;
                }
                confirmSendDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void confirmSendDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_send_title)
                .setMessage(R.string.dialog_send_description)
                .setIcon(R.drawable.ic_send_green)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noteText = note.getText().toString();
                        List<Order> orders = new ArrayList<>(adapter.getSnapshots());

                        Kitchen finalOrder = new Kitchen(noteText, EnterActivity.user.getTableId(), orders);
                        FirebaseFoodfast.sendToKitchen(finalOrder, getContext());

                        // Limpiamos todas las ordenes y dejamos limpio el carrito
                        EnterActivity.user.getOrderMap().clear();
                        FirebaseFoodfast.deleteOrders(EnterActivity.user.getTableId());
                        txtPrice.setText(R.string.cart_price);
                        note.setText("");
                        saveData();
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void emptyCartDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_send_empty_title)
                .setMessage(R.string.dialog_send_empty_description)
                .setIcon(R.drawable.navigation_placeholder)
                .setPositiveButton("Ok", null)
                .show();
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

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView quantity;
        private TextView price;
        private ImageView image;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cart_food_title);
            quantity = itemView.findViewById(R.id.cart_food_quantity);
            price = itemView.findViewById(R.id.cart_food_price);
            image = itemView.findViewById(R.id.cart_food_image);
        }

        public void bindData(Order item) {
            String imageURL = "";
            if (item.getItem().getImage() != null) {
                imageURL = item.getItem().getImage();
                Picasso.with(itemView.getContext())
                        .load(imageURL)
                        .error(R.drawable.navigation_error)
                        .placeholder(R.drawable.navigation_loading)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.placeholder);
            }

            String quantityFormatted = "Cantidad: <b>" + item.getQuantity() + "</b>";
            quantity.setText(Html.fromHtml(quantityFormatted, Html.FROM_HTML_MODE_LEGACY));

            String priceFormatted = "Precio: <b>" + item.getFinalPrice() + "€</b>";
            price.setText(Html.fromHtml(priceFormatted, Html.FROM_HTML_MODE_LEGACY));

            title.setText(item.getItem().getName());
        }

        public void setQuantityText(int quantity) {
            String quantityFormatted = "Cantidad: <b>" + quantity + "</b>";
            this.quantity.setText(Html.fromHtml(quantityFormatted, Html.FROM_HTML_MODE_LEGACY));
        }
    }
}