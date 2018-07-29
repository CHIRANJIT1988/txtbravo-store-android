package educing.tech.store.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import educing.tech.store.R;
import educing.tech.store.helper.Helper;
import educing.tech.store.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;


public class InboxRecyclerAdapter extends RecyclerView.Adapter<InboxRecyclerAdapter.VersionViewHolder>
{

    private List<Order> list;

    private Context context;
    private OnItemClickListener clickListener;


    public InboxRecyclerAdapter(Context context, List<Order> list)
    {
        this.context = context;
        this.list  = list;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_inbox, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i)
    {

        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()

        Order order = list.get(i);

        if(order.order_status.equalsIgnoreCase("RECEIVED"))
        {
            versionViewHolder.order_title.setText(String.valueOf("New Order Received !!"));
            background.getPaint().setColor(Color.parseColor("#ffc107"));
        }

        if(order.order_status.equalsIgnoreCase("PACKED"))
        {
            versionViewHolder.order_title.setText(String.valueOf("Order Packed !!"));
            background.getPaint().setColor(Color.parseColor("#00BCD4"));
        }

        if(order.order_status.equalsIgnoreCase("OUT"))
        {
            versionViewHolder.order_title.setText(String.valueOf("Out for Delivery !!"));
            background.getPaint().setColor(Color.parseColor("#ff5722"));
        }

        if(order.order_status.equalsIgnoreCase("DELIVERED"))
        {
            versionViewHolder.order_title.setText(String.valueOf("Order Delivered !!"));
            background.getPaint().setColor(Color.parseColor("#8bc34a"));
        }

        if(order.order_status.equalsIgnoreCase("CANCELLED"))
        {
            versionViewHolder.order_title.setText(String.valueOf("Order Cancelled !!"));
            background.getPaint().setColor(Color.parseColor("#e51c23"));
        }

        versionViewHolder.order_status.setText(order.order_status.substring(0, 1).toUpperCase());
        versionViewHolder.order_status.setBackground(background);

        if(order.read_status == 1)
        {
            versionViewHolder.order_title.setTypeface(null, Typeface.NORMAL);
        }

        else
        {
            versionViewHolder.order_title.setTypeface(null, Typeface.BOLD);
        }

        /*StringBuilder sAddress = new StringBuilder().append(Helper.toCamelCase(order.getAddress())).append(", ")
                .append(Helper.toCamelCase(order.getLandmark())).append(", ").append(Helper.toCamelCase(order.getCity()))
                .append(", ").append(order.getPincode());*/

        versionViewHolder.order_by.setText(String.valueOf("Ordered by ~ " + Helper.toCamelCase(order.customer_name)));
        versionViewHolder.order_number.setText(Helper.toCamelCase("Order Number ~ " + order.order_no));

        try
        {

            if(DateUtils.isToday(Long.parseLong(order.timestamp)))
            {
                String datetime = new SimpleDateFormat("hh:mm a").format(Long.parseLong(order.timestamp));
                versionViewHolder.timestamp.setText(datetime);
            }

            else
            {
                String datetime = new SimpleDateFormat("dd/MM/yyyy").format(Long.parseLong(order.timestamp));
                versionViewHolder.timestamp.setText(datetime);
            }


            //long output = Long.valueOf(list.get(i).getTimestamp())/1000L;
            //CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(String.valueOf(output * 1000)), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            //versionViewHolder.timestamp.setText(timeAgo);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount()
    {
        return list == null ? 0 : list.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView order_status;
        TextView order_title;
        TextView order_by;
        TextView order_number;
        TextView timestamp;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            order_status = (TextView) itemView.findViewById(R.id.order_status);
            order_title = (TextView) itemView.findViewById(R.id.order_title);
            order_by = (TextView) itemView.findViewById(R.id.order_by);
            order_number = (TextView) itemView.findViewById(R.id.order_number);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}