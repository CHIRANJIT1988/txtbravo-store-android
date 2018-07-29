package educing.tech.store.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import educing.tech.store.R;
import educing.tech.store.helper.Blur;
import educing.tech.store.model.Advertisement;

import static educing.tech.store.configuration.Configuration.ADVERTISEMENT_URL;


public class AdvertisementRecyclerAdapter extends RecyclerView.Adapter<AdvertisementRecyclerAdapter.VersionViewHolder>
{

    private List<Advertisement> advertisementList;

    private Context context;
    private OnItemClickListener clickListener;


    public AdvertisementRecyclerAdapter(Context context, List<Advertisement> advertisementList)
    {
        this.context = context;
        this.advertisementList = advertisementList;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_advertisement, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, int i)
    {

        final Advertisement advertisementObj = advertisementList.get(i);

        try
        {

            long output = Long.valueOf(advertisementObj.timestamp)/1000L;
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(String.valueOf(output * 1000)), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            versionViewHolder.timestamp.setText(String.valueOf(timeAgo));
        }

        catch (Exception e)
        {
            Log.v("Exception", "" + e.getMessage());
        }

        versionViewHolder.description.setText(advertisementObj.message);

        if(!advertisementObj.file_name.equals(""))
        {

            versionViewHolder.advertisement_image.setVisibility(View.VISIBLE);

            Transformation blurTransformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source)
                {
                    Bitmap blurred = Blur.fastblur(context, source, 10);
                    source.recycle();
                    return blurred;
                }

                @Override
                public String key()
                {
                    return "blur()";
                }
            };


            Picasso.with(context)
                .load(ADVERTISEMENT_URL + advertisementObj.file_name) // thumbnail url goes here
                .transform(blurTransformation)
                .into(versionViewHolder.advertisement_image, new Callback() {

                    @Override
                    public void onSuccess()
                    {

                        Picasso.with(context)
                                .load(ADVERTISEMENT_URL + advertisementObj.file_name) // image url goes here
                                .into(versionViewHolder.advertisement_image);
                    }

                    @Override
                    public void onError()
                    {

                    }
                });
        }

        else
        {
            versionViewHolder.advertisement_image.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount()
    {
        return advertisementList == null ? 0 : advertisementList.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView description, timestamp;
        ImageView advertisement_image;

        public VersionViewHolder(View itemView)
        {

            super(itemView);

            description = (TextView) itemView.findViewById(R.id.description);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            advertisement_image = (ImageView) itemView.findViewById(R.id.advertisement_image);

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