package com.capstone.greenmedicuser.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.database.MyDatabase;
import com.capstone.greenmedicuser.models.Medicine;
import com.capstone.greenmedicuser.models.Order;
import com.capstone.greenmedicuser.models.OrderRequest;
import com.capstone.greenmedicuser.models.User;
import com.capstone.greenmedicuser.viewholders.CartAdapter;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MakeOrderActivity extends AppCompatActivity {
    int i=0;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ElegantNumberButton quantityMedi;
    List<Order> orders;
    MyDatabase myDatabase;
    User userDetails;
    float totalCost=0;
    @BindView(R.id.search_medi)
    AutoCompleteTextView searchMedicine;
    String orderid;
    @BindView(R.id.total_cost)
    TextView totalcost;

    @BindView(R.id.add_medi_to_cart)
    Button addMediToCart;
    FirebaseUser mAuth =FirebaseAuth.getInstance().getCurrentUser();


    RecyclerView orderListView;

    DatabaseReference request;

    List<Medicine> medicineList= new ArrayList<Medicine>();
    String[] medicineNameList = new String[10];


    CartAdapter cartAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        setUserDetails();
        ButterKnife.bind(this);
        findMedicineList();

        quantityMedi = (ElegantNumberButton) findViewById(R.id.qty_bt);

        myDatabase = new  MyDatabase(getApplicationContext());
        myDatabase.cleanCart();

//      Firebase
        request=mDatabase.child("OrderRequests");

//      recycleView Init
        orderListView = (RecyclerView)findViewById(R.id.order_list_view);
        orderListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        orderListView.setLayoutManager(layoutManager);



    }

    private void setUserDetails(){
        mDatabase.child("users").child(mAuth.getPhoneNumber()+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot !=null){
                    userDetails =dataSnapshot.getValue(User.class);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "canceled", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void findMedicineList() {
        mDatabase.child("Medicines").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children
                ) {
                    Medicine medicine = child.getValue(Medicine.class);
                    medicineList.add(medicine);
                }
                List<Medicine> medicineList1 = medicineList;

                for (i=0;i<medicineList1.size();i++){
                    if (medicineList1.get(i).getName()!=null) {
                        medicineNameList[i] = medicineList1.get(i).getName()+" "+medicineList1.get(i).getStrength()+" ("+medicineList1.get(i).getDosageForm()+")";
                    }
                }

                showMedicine(medicineNameList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "canceled", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void showMedicine(String[] medicineNameList) {
        //Creating the instance of ArrayAdapter containing list of medicine names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, medicineNameList);

        searchMedicine.setThreshold(0);
        searchMedicine.setAdapter(adapter);
    }

    @OnClick(R.id.add_medi_to_cart)
    public void addToCart(){
        String mediNameWithOthers =searchMedicine.getText().toString();

        String mediName = getOnlyMedicine(mediNameWithOthers);

        String qty =quantityMedi.getNumber();
        boolean isFound =checkMedicineName(mediName);



        if (mediName.isEmpty() || !isFound){
            searchMedicine.setError("Empty or Not Found!");
            searchMedicine.requestFocus();
            return;
        }
        addToDB(mediName,qty);


    }

    private void addToDB(String mediName,String qty) {

        int quantity = Integer.parseInt(qty);


        for (Medicine medicine: medicineList
        ) {
            if (medicine.getName().endsWith(mediName)){

                float mediPrice = Float.parseFloat(medicine.getPrice())*quantity;

                Order order = new Order(
                        medicine.getName(),
                        mediPrice+"",
                        medicine.getStrength()+"",
                        medicine.getCompany()+"",
                        medicine.getQuantity()+"",
                        medicine.getElement()+"",
                        medicine.getDosageForm()+"",
                        medicine.getPrice()
                );

                myDatabase.addToCart(order);

                loadOrderList();


            }

        }

    }


    private boolean checkMedicineName(String mediName) {
        for (Medicine medicine: medicineList
             ) {
            if (medicine.getName().endsWith(mediName)){
                return true;
            }

        }
        return false;
    }

    private void loadOrderList() {
        orders = myDatabase.getCart();

        cartAdapter = new CartAdapter(orders,this);
        orderListView.setAdapter(cartAdapter);

//      set total cost
        totalCost=0;
        for (Order order : orders){
            totalCost+= (Float.parseFloat(order.getPrice()));
        }
        totalcost.setText(String.format("%s", totalCost));

    }

    @OnClick(R.id.findPharm)
    public void findPharmacy(){

        if (myDatabase.getCart().isEmpty()){
            Toast.makeText(getApplicationContext(),"Your order is empty!",Toast.LENGTH_SHORT).show();
        }else {
            totalCost+=30;
            orderid = String.valueOf(System.currentTimeMillis());
            OrderRequest orderRequest = new OrderRequest(orderid
                    ,
                    userDetails.getFullName(),
                    mAuth.getPhoneNumber().toString(),
                    userDetails.getUserAddress(),
                    userDetails.getGeoLocation(),
                    totalCost+"",
                    orders
            );
            totalCost=0;

            mDatabase.child("OrderRequests").child(orderid)
                    .setValue(orderRequest, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(getApplicationContext(),"Delivery Cost =  30 and Order Submitted",Toast.LENGTH_SHORT).show();
                        }
                    });
            new MyDatabase(getBaseContext()).cleanCart();
            // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
            finish();

        }



    }
    @OnClick(R.id.selectPharm)
    public void selectPharmacy(){

        if (myDatabase.getCart().isEmpty()){
            Toast.makeText(getApplicationContext(),"Your order is empty!",Toast.LENGTH_SHORT).show();
        }else{

            Intent intent = new Intent(getApplicationContext(),SelectPharmacyActivity.class);
            finish();
            startActivity(intent);
        }

    }
    private String getOnlyMedicine(String medicineName){
        String onlyMediname[] =medicineName.split(" ");

        return onlyMediname[0];
    }


}
