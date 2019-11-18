package com.capstone.greenmedicuser.views;

import android.os.Bundle;

import com.capstone.greenmedicuser.models.OrderRequest;
import com.capstone.greenmedicuser.viewholders.OrderAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.capstone.greenmedicuser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PendingOrderActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;


    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database;
    DatabaseReference requests;
    String userPhoneNo = mAuth.getPhoneNumber();

    OrderAdapter orderAdapter;


    List<OrderRequest> orderRequestList = new ArrayList<OrderRequest>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        database = FirebaseDatabase.getInstance();
        requests = database.getReference().child("OrderRequests");

        recyclerView =(RecyclerView)findViewById(R.id.recyclerOrderView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();

    }
    private void loadOrders() {

        requests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderRequestList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child:children){
                    OrderRequest orderRequest = child.getValue(OrderRequest.class);
                    if (orderRequest.getUserPhone().equals(userPhoneNo) && !orderRequest.getOrderStatus().equals("1")){
                        orderRequestList.add(orderRequest);
                    }

                }
                setOrderList(orderRequestList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void setOrderList(List<OrderRequest> orderRequestList) {
        orderAdapter = new OrderAdapter(orderRequestList,getApplicationContext());

        recyclerView.setAdapter(orderAdapter);


    }

}
