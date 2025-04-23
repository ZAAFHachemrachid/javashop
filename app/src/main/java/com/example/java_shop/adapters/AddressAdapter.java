package com.example.java_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Address;
import com.google.android.material.chip.Chip;

public class AddressAdapter extends ListAdapter<Address, AddressAdapter.AddressViewHolder> {
    private final AddressClickListener listener;

    public AddressAdapter(AddressClickListener listener) {
        super(new AddressDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = getItem(position);
        holder.bind(address, listener);
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private final TextView addressType;
        private final Chip defaultChip;
        private final TextView streetAddress;
        private final TextView cityStatePostal;
        private final Button editAddressButton;
        private final Button deleteAddressButton;
        private final Button setDefaultButton;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            addressType = itemView.findViewById(R.id.addressType);
            defaultChip = itemView.findViewById(R.id.defaultChip);
            streetAddress = itemView.findViewById(R.id.streetAddress);
            cityStatePostal = itemView.findViewById(R.id.cityStatePostal);
            editAddressButton = itemView.findViewById(R.id.editAddressButton);
            deleteAddressButton = itemView.findViewById(R.id.deleteAddressButton);
            setDefaultButton = itemView.findViewById(R.id.setDefaultButton);
        }

        public void bind(Address address, AddressClickListener listener) {
            addressType.setText("Shipping Address"); // Could be made dynamic if we add address types
            defaultChip.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
            streetAddress.setText(address.getStreet());
            cityStatePostal.setText(String.format("%s, %s %s",
                    address.getCity(), address.getState(), address.getPostalCode()));

            editAddressButton.setOnClickListener(v -> listener.onEditAddress(address));
            deleteAddressButton.setOnClickListener(v -> listener.onDeleteAddress(address));
            setDefaultButton.setOnClickListener(v -> listener.onSetDefaultAddress(address));
            setDefaultButton.setVisibility(address.isDefault() ? View.GONE : View.VISIBLE);
        }
    }

    private static class AddressDiffCallback extends DiffUtil.ItemCallback<Address> {
        @Override
        public boolean areItemsTheSame(@NonNull Address oldItem, @NonNull Address newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Address oldItem, @NonNull Address newItem) {
            return oldItem.getStreet().equals(newItem.getStreet()) &&
                   oldItem.getCity().equals(newItem.getCity()) &&
                   oldItem.getState().equals(newItem.getState()) &&
                   oldItem.getPostalCode().equals(newItem.getPostalCode()) &&
                   oldItem.isDefault() == newItem.isDefault();
        }
    }

    public interface AddressClickListener {
        void onEditAddress(Address address);
        void onDeleteAddress(Address address);
        void onSetDefaultAddress(Address address);
    }
}