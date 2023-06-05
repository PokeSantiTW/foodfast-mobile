package com.santisj.foodfast.controllers;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santisj.foodfast.R;
import com.santisj.foodfast.objects.Kitchen;
import com.santisj.foodfast.objects.Order;

public class FirebaseFoodfast {

    public static String databaseURL = "https://foodfast-tfg-default-rtdb.europe-west1.firebasedatabase.app/";

    public static DatabaseReference sections() {
        return FirebaseDatabase.getInstance(databaseURL)
                .getReference().child("restaurant").child("sections_food");
    }

    public static DatabaseReference sectionItems(String sectionId) {
        return FirebaseDatabase.getInstance(databaseURL).getReference().child("restaurant")
                .child("sections_food").child(sectionId)
                .child("items");
    }

    public static DatabaseReference foodsReference() {
        return FirebaseDatabase.getInstance(databaseURL).getReference().child("restaurant")
                .child("foods");
    }

    public static void addOrder(int tableId, Order order, Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance(databaseURL)
                .getReference("restaurant").child("tables").child(String.valueOf(tableId))
                .child("orders").child(order.getOrderId());
        if (order.getQuantity() == 0) {
            reference.removeValue();
        } else {
            reference.setValue(order).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.dialog_failure_title)
                            .setMessage(R.string.dialog_failure_description)
                            .setIcon(R.drawable.navigation_error)
                            .setPositiveButton("Ok", null).show();
                }
            });
        }
    }

    public static void sendToKitchen(Kitchen item, Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance(databaseURL)
                .getReference("kitchen");
        DatabaseReference newEntry = reference.push();
        newEntry.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_send_complete_title)
                        .setMessage(R.string.dialog_send_complete_description)
                        .setIcon(R.drawable.ic_done_green)
                        .setPositiveButton("Ok", null).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_send_failure_title)
                        .setMessage(R.string.dialog_send_failure_description)
                        .setIcon(R.drawable.navigation_error)
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    public static DatabaseReference orders(int tableId) {
        return FirebaseDatabase.getInstance(databaseURL).getReference().child("restaurant")
                .child("tables").child(String.valueOf(tableId)).child("orders");
    }

    public static void deleteOrders(int tableId) {
        DatabaseReference ref = FirebaseDatabase.getInstance(databaseURL).getReference()
                .child("restaurant").child("tables")
                .child(String.valueOf(tableId)).child("orders");

        ref.removeValue();
    }

    public static DatabaseReference tables() {
        return FirebaseDatabase.getInstance(databaseURL).getReference().child("restaurant")
                .child("tables");
    }

    public static void setAvailable(int tableId, boolean isAvailable) {
        DatabaseReference refTable = FirebaseDatabase.getInstance(databaseURL).getReference()
                .child("restaurant").child("tables").child(String.valueOf(tableId))
                .child("isTableAvailable");

        refTable.setValue(isAvailable);
    }

}
