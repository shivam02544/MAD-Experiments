package com.prabhat.mad_expriment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactsAdapter extends ArrayAdapter<ContactModel> {

    private final Context context;
    private final int resource;

    public ContactsAdapter(@NonNull Context context, int resource, @NonNull List<ContactModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View itemView;

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvAvatarInitial = itemView.findViewById(R.id.tvAvatarInitial);
            holder.tvContactName = itemView.findViewById(R.id.tvContactName);
            holder.tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            holder.btnShareContact = itemView.findViewById(R.id.btnShareContact);
            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }

        ContactModel contact = getItem(position);
        if (contact != null) {
            holder.tvContactName.setText(contact.getName());
            holder.tvContactPhone.setText(contact.getPhoneNumber());

            String name = contact.getName();
            if (name != null && !name.trim().isEmpty()) {
                holder.tvAvatarInitial.setText("" + Character.toUpperCase(name.trim().charAt(0)));
            } else {
                holder.tvAvatarInitial.setText("C");
            }

            holder.btnShareContact.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Contact Details:\nName: " + contact.getName() + "\nPhone: " + contact.getPhoneNumber();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(shareIntent, "Share Contact via"));
            });
        }

        return itemView;
    }

    private static class ViewHolder {
        TextView tvAvatarInitial;
        TextView tvContactName;
        TextView tvContactPhone;
        ImageButton btnShareContact;
    }
}