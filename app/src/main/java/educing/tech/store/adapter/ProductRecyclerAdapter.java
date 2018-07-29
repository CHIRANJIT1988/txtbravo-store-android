package educing.tech.store.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import educing.tech.store.R;
import educing.tech.store.activities.PinchZoomActivity;
import educing.tech.store.helper.Helper;
import educing.tech.store.model.Product;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import static educing.tech.store.configuration.Configuration.MEDIA_DIRECTORY_NAME;


public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.VersionViewHolder>
{

    private DecimalFormat df = new DecimalFormat("0.00");

    private List<Product> productList;
    private OnItemClickListener clickListener;
    private Context context = null;

    private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MEDIA_DIRECTORY_NAME + "/";


    public ProductRecyclerAdapter(List<Product> productList, Context context)
    {
        this.productList = productList;
        this.context = context;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_product, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i)
    {

        Product product = productList.get(i);

        versionViewHolder.thumbNail.setTag(i);


        if(!product.product_thumbnail.equals(""))
        {

            if(fileExist(product.product_thumbnail))
            {

                try
                {

                    // bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // downsizing image as it throws OutOfMemory Exception for larger images
                    options.inSampleSize = 4; //power of 2 like 2, 4, 8, 16

                    final Bitmap bitmap = BitmapFactory.decodeFile(PATH + product.product_thumbnail, options);

                    versionViewHolder.thumbNail.setImageBitmap(bitmap);
                }

                catch(Exception e)
                {
                    int res = context.getResources().getIdentifier("no_image", "drawable", context.getPackageName());
                    versionViewHolder.thumbNail.setImageResource(res);
                }
            }

            else
            {
                int res = context.getResources().getIdentifier("no_image", "drawable", context.getPackageName());
                versionViewHolder.thumbNail.setImageResource(res);
            }
        }

        else
        {
            int res = context.getResources().getIdentifier("no_image", "drawable", context.getPackageName());
            versionViewHolder.thumbNail.setImageResource(res);
        }

        versionViewHolder.tvName.setText(Helper.toCamelCase(product.product_name));
        versionViewHolder.tvWeight.setText(String.valueOf(product.weight + " " + product.unit.toLowerCase()));
        versionViewHolder.tvPrice.setText(String.valueOf(df.format(product.price)));
        versionViewHolder.tvDiscountPrice.setText(String.valueOf(df.format(product.discount_price)));

        if(product.price !=  product.discount_price)
        {
            versionViewHolder.tvDiscountPrice.setVisibility(View.VISIBLE);
            versionViewHolder.tvPrice.setPaintFlags(versionViewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        else
        {
            versionViewHolder.tvPrice.setPaintFlags(versionViewHolder.tvPrice.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
            versionViewHolder.tvDiscountPrice.setVisibility(View.GONE);
        }

        if(product.status == 1)
        {
            versionViewHolder.tvStatus.setText(String.valueOf("ACTIVE"));
            versionViewHolder.tvStatus.setTextColor(Color.parseColor("#259b24"));
        }

        else
        {
            versionViewHolder.tvStatus.setText(String.valueOf("INACTIVE"));
            versionViewHolder.tvStatus.setTextColor(Color.parseColor("#727272"));
        }
    }


    @Override
    public int getItemCount()
    {
        return productList == null ? 0 : productList.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tvName;
        TextView tvWeight;
        TextView tvPrice;
        TextView tvDiscountPrice;
        TextView tvStatus;
        ImageView thumbNail;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            thumbNail = (ImageView) itemView.findViewById(R.id.product_image);
            tvName = (TextView) itemView.findViewById(R.id.product_name);
            tvWeight = (TextView) itemView.findViewById(R.id.weight);
            tvPrice = (TextView) itemView.findViewById(R.id.price);
            tvDiscountPrice = (TextView) itemView.findViewById(R.id.discount_price);
            tvStatus = (TextView) itemView.findViewById(R.id.product_status);

            itemView.setOnClickListener(this);
            thumbNail.setOnClickListener(onThumbnailClickListener);
        }

        private View.OnClickListener onThumbnailClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int pos = (int) v.getTag();

                if(!productList.get(pos).product_thumbnail.equals(""))
                {

                    if(fileExist(productList.get(pos).product_thumbnail))
                    {
                        Intent intent = new Intent(context, PinchZoomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("URL", PATH + productList.get(pos).product_thumbnail);
                        context.startActivity(intent);
                    }
                }
            }
        };

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


    private boolean fileExist(String file_name)
    {

        File imgFile = new File(PATH + file_name);
        {
            return imgFile.exists();
        }
    }
}