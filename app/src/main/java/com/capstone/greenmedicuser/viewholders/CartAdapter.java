package com.capstone.greenmedicuser.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.models.Order;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

        private List<Order> orderList = new ArrayList<>();
        private Context context;

        public CartAdapter(List<Order> orderList, Context context) {
                this.orderList = orderList;
                this.context = context;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
            return new CartViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            holder.mediName.setText(orderList.get(position).getMedicineName());
            holder.mediDosage.setText(orderList.get(position).getDosageForm());
            holder.mediElement.setText(orderList.get(position).getElement());
            holder.mediStrength.setText(orderList.get(position).getStrength());
            holder.mediCompany.setText(orderList.get(position).getCompany());
            holder.mediCost.setText(orderList.get(position).getPrice());
            holder.qtty.setText(orderList.get(position).getQuantity());
            holder.perunit_cost_txt.setText(orderList.get(position).getPerUnitPrice()+"tk");

        }

        @Override
        public int getItemCount() {
                return orderList.size();
        }
}
