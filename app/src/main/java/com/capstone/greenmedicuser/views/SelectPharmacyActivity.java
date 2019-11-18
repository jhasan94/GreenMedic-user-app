package com.capstone.greenmedicuser.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.database.MyDatabase;
import com.capstone.greenmedicuser.models.GpsCoordinate;
import com.capstone.greenmedicuser.models.Medicine;
import com.capstone.greenmedicuser.models.Order;
import com.capstone.greenmedicuser.models.OrderRequest;
import com.capstone.greenmedicuser.models.Pharmacy;
import com.capstone.greenmedicuser.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectPharmacyActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private GoogleMap mMap;
    MyDatabase myDatabase;
    User userDetails;
    List<Order> orders;
    List<String> pharmacyNameList= new ArrayList<String>();
    List<Pharmacy> pharmacyList= new ArrayList<Pharmacy>();
    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    ImageButton locatePharma;

    @BindView(R.id.find_pharm)
    AutoCompleteTextView findPharma;

    @BindView(R.id.route_length_value)
    TextView routeLength;
    double distance=0;
    float totalCost=0;
    private String orderid;
    int delivery=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pharmacy);
        setUserDetails();

        ButterKnife.bind(this);
        //findPharmacyList();
        myDatabase = new MyDatabase(getApplicationContext());

        locatePharma = (ImageButton) findViewById(R.id.locat_pharma);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPharma);
        mapFragment.getMapAsync(this);



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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        findPharmacyList();


        locatePharma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pharmName =findPharma.getText().toString();
                boolean isFound = checkPharmacyeName(pharmName);

                if (!isFound || pharmName.isEmpty()){
                    findPharma.setError("Pharmacy not found!");
                    findPharma.requestFocus();
                    return;
                }
                Pharmacy pharmacy = findPharmacy(pharmName);

                GpsCoordinate gpSmodule = new GpsCoordinate(pharmacy.getGeoLocation());
                LatLng latLng1 = new LatLng(gpSmodule.getLatitude(),gpSmodule.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,16),5000,null);

                GpsCoordinate userGps = new GpsCoordinate(userDetails.getGeoLocation());
                LatLng userlatLag = new LatLng(userGps.getLatitude(),userGps.getLongitude());

                distance =distance(userlatLag,latLng1);
                routeLength.setText(String.format("%.2f m",distance));



            }
        });


    }


    private void findPharmacyList(){

        mDatabase.child("pharmacies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Pharmacy pharmacy = child.getValue(Pharmacy.class);

                    if (pharmacy != null && !pharmacy.getGeoLocation().equals("000,000")) {
                        pharmacyList.add(pharmacy);
                        pharmacyNameList.add(pharmacy.getPharmacyName());
                    }
                }
                showPharma(pharmacyNameList);
                setIconsOnMap(pharmacyList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "canceled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setIconsOnMap(List<Pharmacy> pharmacyList) {

        for (int i = 0; i<pharmacyList.size();i++){

            Pharmacy pharmacy = pharmacyList.get(i);
            GpsCoordinate gpSmodule = new GpsCoordinate(pharmacy.getGeoLocation());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(gpSmodule.getLatitude(),gpSmodule.getLongitude()))
                    .title(pharmacy.getPharmacyName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pharmacy_locate))
            );
            marker.setTag(0);


        }

    }





    private void showPharma(List<String> pharmacyNameList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, pharmacyNameList);

        findPharma.setThreshold(0);
        findPharma.setAdapter(adapter);


    }

    @OnClick(R.id.create_request)
    public void submitOrder(){

        String pharmName =findPharma.getText().toString();
        boolean isFound = checkPharmacyeName(pharmName);

        if (!isFound || pharmName.isEmpty()){
            findPharma.setError("Pharmacy not found!");
            findPharma.requestFocus();
            return;
        }

        orders = myDatabase.getCart();
        for (Order order : orders){
            totalCost+= (Float.parseFloat(order.getPrice()));
        }
        if (distance <3000){
            delivery=30;
        }else {
            delivery=60;
        }
        totalCost+=delivery;

        submitToDB(orders,totalCost,pharmName);
        totalCost=0;
    }

    private void submitToDB(List<Order> orders, float totalCost, String pharmName) {
        orderid = String.valueOf(System.currentTimeMillis());
/*        OrderRequest orderRequest = new OrderRequest(orderid
                ,
                userDetails.getFullName(),
                mAuth.getPhoneNumber().toString(),
                userDetails.getUserAddress(),
                totalCost+"",
                orders
        );*/

        Pharmacy pharmacy = findPharmacy(pharmName);

        OrderRequest orderRequest = new OrderRequest(
                orderid,
                mAuth.getPhoneNumber(),
                userDetails.getFullName(),
                userDetails.getGeoLocation(),
                userDetails.getUserAddress(),
                totalCost+"",
                "71",
                orders,
                "na",
                pharmacy.getPharmacyAddress(),
                pharmacy.getGeoLocation()



                );



        mDatabase.child("OrderRequests").child(orderid)
                .setValue(orderRequest, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(getApplicationContext(),"Delivery Cost :"+delivery+" and Order Submitted",Toast.LENGTH_SHORT).show();
                        delivery=0;
                    }
                });
        new MyDatabase(getBaseContext()).cleanCart();

        // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean checkPharmacyeName(String pharmName) {
        for (String pharmacy: pharmacyNameList
        ) {
            if (pharmacy.equals(pharmName)){
                return true;
            }

        }
        return false;
    }

    private Pharmacy findPharmacy(String pharmName){
        for (Pharmacy pharmacy :
                pharmacyList) {
            if (pharmacy.getPharmacyName().equals(pharmName)){
                return pharmacy;
            }

        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myDatabase.cleanCart();
    }


    public double distance(LatLng start,LatLng end){
        return SphericalUtil.computeDistanceBetween(start, end);
    }
}
