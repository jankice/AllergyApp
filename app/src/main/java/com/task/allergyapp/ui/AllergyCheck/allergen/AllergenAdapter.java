package com.task.allergyapp.ui.AllergyCheck.allergen;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.task.allergyapp.R;

import java.util.ArrayList;

public class AllergenAdapter extends RecyclerView.Adapter<AllergenAdapter.ViewHolder> {

    Context mContext;
    ArrayList<String> mArrayList;

    public AllergenAdapter(Context theContext, ArrayList<String> theList) {
        this.mContext = theContext;
        this.mArrayList = theList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // Context context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergen, parent, false);


        // Return a new holder instance
        return new ViewHolder(itemView);

    }
    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.textView.setText(mArrayList.get(position));


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView textView;



        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textView = itemView.findViewById(R.id.editTextItem);

        }
    }
}
