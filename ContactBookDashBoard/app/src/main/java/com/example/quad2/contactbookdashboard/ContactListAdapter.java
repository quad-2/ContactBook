package com.example.quad2.contactbookdashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by quad2 on 11/10/16.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contactList;

    public ContactListAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contact_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (contactList.get(position).getLastContacted() != null && !contactList.get(position).getLastContacted().equalsIgnoreCase("0")) {
            String date = getDate(contactList.get(position).getLastContacted());
            holder.lastContactTime.setText("Last Contacted : " + date);
        } else {
            holder.lastContactTime.setText("You have not contacted them!");
        }
        holder.phoneNumber.setText(contactList.get(position).getPhoneNumber());
        holder.name.setText(contactList.get(position).getName());
        holder.email.setText(contactList.get(position).getEmail());
        if (contactList.get(position).getImageURI() != null) {
            Picasso.with(context).load(contactList.get(position).getImageURI())
                    .into(holder.contactImage);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView name;
        TextView email;
        TextView lastContactTime;
        TextView phoneNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            contactImage = (ImageView) itemView.findViewById(R.id.contact_image);
            email = (TextView) itemView.findViewById(R.id.email);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            lastContactTime = (TextView) itemView.findViewById(R.id.last_contact_time);
            phoneNumber = (TextView) itemView.findViewById(R.id.contact_number);
        }
    }

    private String getDate(String timestampString) {
        long time = Long.parseLong(timestampString);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
