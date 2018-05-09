package geller.omry.anyvisiontask.Controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

import geller.omry.anyvisiontask.Model.FeedItem;
import geller.omry.anyvisiontask.R;

/**
 * Created by omry on 5/8/2018.
 */

public class RSSFeedRecyclerAdapter extends RecyclerView.Adapter {
    private List<FeedItem> feedItems;
    private Context context;

    public RSSFeedRecyclerAdapter(Context context,List<FeedItem> feedItems) {
        this.feedItems = feedItems;
        this.context=context;
    }
    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView description;
        ImageView image;
        CardView cardView;
        Button descriptionButton;
        FeedItem feedItem;
        public ItemHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.feed_item_title);

            date=(TextView)itemView.findViewById(R.id.feed_item_date);

            image=(ImageView)itemView.findViewById(R.id.feed_item_image);

            description=(TextView)itemView.findViewById(R.id.feed_item_description);

            descriptionButton=(Button)itemView.findViewById(R.id.feed_item_desc_btn);

            descriptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(description.getVisibility() == View.GONE) {

                        descriptionButton.setText("Less");

                        description.setVisibility(View.VISIBLE);
                    }
                    else {
                        descriptionButton.setText("More");

                        description.setVisibility(View.GONE);
                    }

                }
            });
            cardView=(CardView)itemView.findViewById(R.id.feed_card);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setData(Uri.parse(feedItem.getUrl()));

                    context.startActivity(intent);
                }
            });
        }
        public void setFeedItem(FeedItem feedItem){
            this.feedItem=feedItem;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View menuItemLayoutView = LayoutInflater.from(context).inflate(
                R.layout.feed_item_layout, parent, false);

        return new ItemHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final  ItemHolder itemHolder=(ItemHolder)holder;

        FeedItem feedItem=feedItems.get(position);

        itemHolder.setFeedItem(feedItem);

        itemHolder.title.setText(feedItem.getTitle());

        itemHolder.date.setText(feedItem.getDate());

        itemHolder.description.setText(feedItem.getDescription());

        Glide.with(context).load(feedItem.getImage()).into(itemHolder.image);

    }
    public void setItemList(List<FeedItem> list){
        feedItems.clear();
        feedItems.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return feedItems == null ? 0 : feedItems.size();
    }
}
