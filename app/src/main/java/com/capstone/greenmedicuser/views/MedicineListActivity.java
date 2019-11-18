package com.capstone.greenmedicuser.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.models.Order;
import com.capstone.greenmedicuser.viewholders.OrderedMedicineAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MedicineListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("OrderRequests");
    @BindView(R.id.total_cost_medi)
    TextView totalCostMedi;

    @BindView(R.id.cancel_button)
    Button cancelOrderBt;

    List<Order> medicineList = new ArrayList<Order>();

    String totalCOST;

    String orderID;
    OrderedMedicineAdapter orderedMedicineAdapter;

    boolean orderStatus=true;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        ButterKnife.bind(this);

        recyclerView =(RecyclerView)findViewById(R.id.recyclerOrderMedicineView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent().hasExtra("order_id") && getIntent().hasExtra("total_cost")  && getIntent().hasExtra("status")){
            orderID =getIntent().getStringExtra("order_id");
            totalCOST = getIntent().getStringExtra("total_cost");
            if (getIntent().getStringExtra("status").equals("0") ||getIntent().getStringExtra("status").equals("71") ){
                orderStatus=false;
            }
        }

        if (!orderStatus){
            cancelOrderBt.setVisibility(View.VISIBLE);
        }



        totalCostMedi.setText(totalCOST);
        findMedicineList(orderID);

    }



    private void findMedicineList(String orderID) {
        mDatabase.child(orderID).child("medicines").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot child:
                         children) {
                        Order order = child.getValue(Order.class);
                        medicineList.add(order);

                    }
                    loadMedicineList(medicineList);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMedicineList(List<Order> medicineList) {
        orderedMedicineAdapter = new OrderedMedicineAdapter(medicineList,getApplicationContext());

        recyclerView.setAdapter(orderedMedicineAdapter);

    }
    @OnClick(R.id.cancel_button)
    public void cancelOrder(){


        try {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Order")
                    .setMessage("Do you really want to cancel order ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            mDatabase.child(orderID).removeValue();
                            finish();
                            Toast.makeText(MedicineListActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();



        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
