package com.example.shiftsfinalproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shiftsfinalproj.Shift;

import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ViewHolder> {

    private List<Shift> shiftList;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    ShiftAdapter(Context context, List<Shift> data, ItemClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.shiftList = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_shift, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shift shift = shiftList.get(position);
        holder.shiftInfoTextView.setText(shift.toString());// Customize this to display shift info
        holder.editButton.setOnClickListener(v -> clickListener.onEditClick(shift));
        holder.deleteButton.setOnClickListener(v -> clickListener.onDeleteClick(shift));
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView shiftInfoTextView;
        Button editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            shiftInfoTextView = itemView.findViewById(R.id.shiftInfoTextView);
            editButton = itemView.findViewById(R.id.editShiftButton);
            deleteButton = itemView.findViewById(R.id.deleteShiftButton);
        }
    }

    public interface ItemClickListener {
        void onEditClick(Shift shift);
        void onDeleteClick(Shift shift);
    }
}
