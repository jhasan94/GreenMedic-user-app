package com.capstone.greenmedicuser.viewholders;

import android.view.View;
import android.widget.TextView;

import com.capstone.greenmedicuser.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    @BindView(R.id.user_address_order)
    public TextView userAddress;

    @BindView(R.id.phone_no_user)
    public TextView userPhone;


    @BindView(R.id.pharm_address_order)
    public TextView pharmAddress;

    @BindView(R.id.phone_no_pharm)
    public TextView pharmacyPhone;


    @BindView(R.id.order_status)
    public TextView orderStatus;


    @BindView(R.id.cost_order)
    public TextView orderCost;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onClick(View v) {

    }
}
