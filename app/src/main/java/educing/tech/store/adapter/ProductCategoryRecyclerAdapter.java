package educing.tech.store.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import educing.tech.store.R;
import educing.tech.store.activities.StoreConfigurationActivity;
import educing.tech.store.app.MyApplication;
import educing.tech.store.helper.Helper;
import educing.tech.store.helper.OnTaskCompleted;
import educing.tech.store.model.Product;
import educing.tech.store.mysql.db.send.AttachStore;
import educing.tech.store.sqlite.SQLiteDatabaseHelper;

import java.util.List;

import static educing.tech.store.configuration.Configuration.PRODUCT_CATEGORY_URL;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.TABLE_PRODUCT_CATEGORY;
import static educing.tech.store.sqlite.SQLiteDatabaseHelper.KEY_STATUS;


public class ProductCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    private List<Product> categoryList;
    private OnItemClickListener clickListener;
    private Context context = null;
    private OnTaskCompleted listener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private SQLiteDatabaseHelper helper;


    public ProductCategoryRecyclerAdapter(List<Product> categoryList, Context context, OnTaskCompleted listener)
    {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;

        this.helper = new SQLiteDatabaseHelper(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        if(i == TYPE_HEADER)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_item_next, viewGroup, false);
            return new VHHeader(view);
        }

        else if(i == TYPE_ITEM)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_product_category, viewGroup, false);
            return new VersionViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i)
    {

        if( i == 0)
        {

            /*if (holder instanceof VHHeader)
            {
                VHHeader header = (VHHeader) holder;
            }*/

            return;
        }

        if(holder instanceof VersionViewHolder)
        {

            VersionViewHolder versionViewHolder = (VersionViewHolder) holder;

            if (imageLoader == null)
            {
                imageLoader = MyApplication.getInstance().getImageLoader();
            }


            Product product = categoryList.get(i-1);

            versionViewHolder.thumbnail.setImageUrl(PRODUCT_CATEGORY_URL + product.category_thumbnail, imageLoader);
            versionViewHolder.category_name.setText(Helper.toCamelCase(product.category_name));

            versionViewHolder.switch_active.setTag(i);


            if(product.status == 0)
            {
                versionViewHolder.switch_active.setChecked(false);
            }

            else
            {
                versionViewHolder.switch_active.setChecked(true);
            }
        }
    }


    @Override
    public int getItemCount()
    {
        return categoryList == null ? 0 : categoryList.size() + 1;
    }


    class VHHeader extends RecyclerView.ViewHolder
    {

        Button btnSave;

        public VHHeader(View itemView)
        {

            super(itemView);

            btnSave = (Button) itemView.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(onButtonClickListener);
        }


        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                if(v.getId() == R.id.btnSave)
                {
                    if(helper.dbRowCount(TABLE_PRODUCT_CATEGORY, KEY_STATUS, "1") != 0)
                    {
                        listener.onTaskCompleted(true, 300, "save");
                    }

                    else
                    {
                        Toast.makeText(context, "No Category Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView category_name;
        SwitchCompat switch_active;
        NetworkImageView thumbnail;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            thumbnail = (NetworkImageView) itemView.findViewById(R.id.category_image);
            category_name = (TextView) itemView.findViewById(R.id.category_name);
            switch_active = (SwitchCompat) itemView.findViewById(R.id.switch_active);

            switch_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {

                    int pos = (int) buttonView.getTag();

                    Log.v("POSITION: ", "" + pos);

                    if(isChecked)
                    {
                        helper.updateCategoryStatus(categoryList.get(pos-1).getCategoryId(), 1);
                        categoryList.get(pos-1).setStatus(1);
                    }

                    else
                    {
                        helper.updateCategoryStatus(categoryList.get(pos-1).getCategoryId(), 0);
                        categoryList.get(pos-1).setStatus(0);
                    }


                    int count_category = helper.dbRowCount(TABLE_PRODUCT_CATEGORY, KEY_STATUS, "1");

                    if(count_category == 0)
                    {
                        StoreConfigurationActivity.ib_category.setBackgroundResource(R.drawable.ib_order_progress);
                        StoreConfigurationActivity.ib_category.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_white_24dp));
                    }

                    else
                    {
                        StoreConfigurationActivity.ib_category.setBackgroundResource(R.drawable.ib_order_status_completed);
                        StoreConfigurationActivity.ib_category.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_white_24dp));
                    }

                    new AttachStore(context).execute();
                }
            });
        }


        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    @Override
    public int getItemViewType(int position)
    {

        if(position == 0)
        {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    /*public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }*/
}