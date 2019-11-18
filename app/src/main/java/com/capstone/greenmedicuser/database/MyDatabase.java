package com.capstone.greenmedicuser.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.capstone.greenmedicuser.models.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DB_NAME = "GreenMedicUserDB.db";
    private static final int DB_VER = 1;



    public MyDatabase(Context context) {
        super(context, DB_NAME,null ,DB_VER);

    }


    public List<Order> getCart(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

/*        String[] sqlSelect = {
                "MedicineName","Price",
                "Strength","company","quantity"
                ,"Element","DosageForm"
                };
        String sqlTable = "OrderDetails";*/

        String[] sqlSelect = {
                "MedicineName","Price",
                "Strength","company","quantity"
                ,"Element","DosageForm","perUnitPrice"
        };
        String sqlTable = "OrderDetails";

        qb.setTables(sqlTable);

        Cursor cursor = qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                result.add(new Order(
                        cursor.getString(cursor.getColumnIndex("MedicineName")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Strength")),
                        cursor.getString(cursor.getColumnIndex("company")),
                        cursor.getString(cursor.getColumnIndex("quantity")),
                        cursor.getString(cursor.getColumnIndex("Element")),
                        cursor.getString(cursor.getColumnIndex("DosageForm")),
                                cursor.getString(cursor.getColumnIndex("perUnitPrice")))
                        );
            }while (cursor.moveToNext());

        }
        return result;

    }

    public void addToCart(Order order){
        SQLiteDatabase db = getWritableDatabase();

        String query = String.format("INSERT INTO OrderDetails(MedicineName,Price,Strength,company,quantity,Element,DosageForm,perUnitPrice)VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                order.getMedicineName(),order.getPrice(),order.getStrength(),order.getCompany(),order.getQuantity(),order.getElement(),order.getDosageForm(),order.getPerUnitPrice());

        db.execSQL(query);
    }


    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM OrderDetails");

        db.execSQL(query);
    }



}
