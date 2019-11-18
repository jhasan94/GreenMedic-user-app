package com.capstone.greenmedicuser.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.models.Order;
import com.capstone.greenmedicuser.models.OrderRequest;
import com.capstone.greenmedicuser.views.MainActivity;
import com.capstone.greenmedicuser.views.MainActivityFragment;
import com.capstone.greenmedicuser.views.MedicineListActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private List<OrderRequest> orderList = new ArrayList<>();
    private Context context;

    public OrderAdapter(List<OrderRequest> orderList,Context context) {
        this.orderList = orderList;
        this.context = context;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout,parent,false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.pharmAddress.setText(orderList.get(position).getAcceptedPharmacyAddress());
        holder.userAddress.setText(orderList.get(position).getUserAddress());
        holder.orderStatus.setText(convertStatus(orderList.get(position).getOrderStatus()));
        holder.orderCost.setText(orderList.get(position).getOrderTotalCost());
        holder.userPhone.setText(orderList.get(position).getUserPhone());
        holder.pharmacyPhone.setText(orderList.get(position).getAcceptedPharmacyPhoneNo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(context,orderList.get(position).getOrderID(),Toast.LENGTH_SHORT).show();*/

                Intent intent = new Intent(context, MedicineListActivity.class);
                intent.putExtra("order_id",orderList.get(position).getOrderID());
                intent.putExtra("total_cost",orderList.get(position).getOrderTotalCost());
                intent.putExtra("status",orderList.get(position).getOrderStatus());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    private String convertStatus(String orderStatus) {
        if (orderStatus.equals("0")){
            return "finding pharmacy";
        }else if (orderStatus.equals("99")){
            return "accepted";
        }else if (orderStatus.equals("71")){
            return "submitted to pharmacy";
        }else{
            return "completed";
        }



    }
}
