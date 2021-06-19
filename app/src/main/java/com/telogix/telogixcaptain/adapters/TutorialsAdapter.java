package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.activities.Fragments.VideoViewFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TutorialsAdapter extends RecyclerView.Adapter<TutorialsAdapter.ViewHolder> {

    ArrayList<HashMap<String,String>> tutoriallist=new ArrayList<>();
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    // data is passed into the constructor
    public TutorialsAdapter(Context context, ArrayList<HashMap<String,String>> tutoriallist) {
        this.mInflater = LayoutInflater.from(context);
        this.tutoriallist = tutoriallist;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_tutorialdesign, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtvideoTitle.setText("Tutorial #" + (position+1));
        if (tutoriallist.get(position).get("type").equals("pdf"))
        {
            holder.imgtutorialType.setImageResource(R.drawable.placeholder_pdf);
        }
        else{
            holder.imgtutorialType.setImageResource(R.drawable.placeholdervideo);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return tutoriallist.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtvideoTitle;
        ImageView imgtutorialType;

        ViewHolder(View itemView) {
            super(itemView);
            txtvideoTitle = itemView.findViewById(R.id.txtvideoTitle);
            imgtutorialType=itemView.findViewById(R.id.imgtutorialtype);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            if(tutoriallist.get(getAdapterPosition()).get("type").equals("pdf"))
            {
                try {


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+tutoriallist.get(getAdapterPosition()).get("url")));
                context.startActivity(browserIntent);
            }
                catch (Exception ex)
                {
ex.printStackTrace();
                }
            }
            else {
                ChangeFragment(new VideoViewFragment(), tutoriallist.get(getAdapterPosition()), view);
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return tutoriallist.get(id).get("id");
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    private void ChangeFragment(Fragment fragment, HashMap<String,String> body,View v) {


            Bundle b=new Bundle();
            b.putString("type",""+body.get("type"));
            b.putString("url",""+body.get("url"));
            b.putString("title",""+body.get("title"));
fragment.setArguments(b);
        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
                .commit();

    }

}