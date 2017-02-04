package com.vansuita.passwordvault.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.enums.ECategory;

/**
 * Created by jrvansuita on 16/01/17.
 */

public class CategoryChooserAdapter extends RecyclerView.Adapter<CategoryChooserAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_option, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ECategory e = ECategory.values()[position];

        holder.title.setText(e.getTitleRes());
        Icon.on(holder.icon).color(R.color.secondary_text).pressedEffect(false).icon(e.getIconRes()).put();
    }

    @Override
    public int getItemCount() {
        return ECategory.values().length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final ImageView icon;
        final CategoryChooserAdapter adapter;

        ViewHolder(View v, CategoryChooserAdapter adapter) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            icon = (ImageView) v.findViewById(R.id.icon);

            this.adapter = adapter;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.onItemClick == null)
                return;

            adapter.onItemClick.onItemClicked(ECategory.values()[getAdapterPosition()]);
        }
    }

    public interface OnItemClick {
        void onItemClicked(ECategory type);
    }

    private OnItemClick onItemClick;

    public void setOnClickItem(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
}