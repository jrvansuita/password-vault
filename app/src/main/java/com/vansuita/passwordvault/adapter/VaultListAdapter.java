package com.vansuita.passwordvault.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.bean.Email;
import com.vansuita.passwordvault.cnt.VaultCnt;
import com.vansuita.passwordvault.enums.EEmailDomain;
import com.vansuita.passwordvault.fire.adapter.FirebaseRecyclerAdapter;
import com.vansuita.passwordvault.fire.dao.VaultDAO;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Visible;
import com.vansuita.passwordvault.view.Ripple;
import com.vansuita.passwordvault.view.Snack;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jrvansuita on 04/02/17.
 */

public class VaultListAdapter extends FirebaseRecyclerAdapter<VaultListAdapter.ViewHolder, Bean> {

    private ArrayList<Integer> dataSelected = new ArrayList();

    public VaultListAdapter(Query query) {
        super(query);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView tvTitle;
        TextView tvDate;
        ImageView ivIcon;
        View vFavorite;
        View vColor;
        View vSelected;

        public ViewHolder(View v) {
            super(v);

            root = v.findViewById(R.id.content);
            tvTitle = (TextView) v.findViewById(R.id.title);
            tvDate = (TextView) v.findViewById(R.id.date);
            ivIcon = (ImageView) v.findViewById(R.id.icon);
            vFavorite = v.findViewById(R.id.favorite);
            vColor = v.findViewById(R.id.color);
            vSelected = v.findViewById(R.id.selected);

            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null)
                        callback.onIconClicked(getLayoutPosition());
                }
            });

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClicked(getLayoutPosition(), false);
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callback.onItemClicked(getLayoutPosition(), true);
                    return true;
                }
            });
        }
    }

    @Override
    public VaultListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VaultListAdapter.ViewHolder holder, int position) {
        boolean isSelected = dataSelected.contains(position);
        holder.root.setActivated(isSelected);
        Bean bean = getItem(position);


        Pref pref = Pref.with(holder.ivIcon.getContext());

        holder.tvTitle.setText(bean.getTitle());

        CharSequence s = DateUtils.getRelativeTimeSpanString(bean.getLastTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL);

        holder.tvDate.setText(s);

        UI.setFavorite(holder.vFavorite, bean.isFavorite());

        boolean isDarken = Util.isColorDark(bean.getColor());
        boolean canColor = pref.canChangeItemsColor();

        if (!canColor)
            bean.setColor(Color.WHITE);

        if (bean instanceof Email && pref.showEmailDomainsIcons()) {
            EEmailDomain domain = EEmailDomain.findDomain(((Email) bean).getEmail());
            Icon.on(holder.ivIcon).icon(domain.getIcon()).put();
        } else {
            Icon.on(holder.ivIcon).color(android.R.color.white).icon(bean.getCategory().getIconRes()).put();
        }

        Visible.with(holder.vSelected).gone(!isSelected);

        if (isSelected) {
            holder.vSelected.startAnimation(AnimationUtils.loadAnimation(holder.vSelected.getContext(), R.anim.expand_in));
        }

        holder.vColor.setVisibility(isSelected ? View.GONE : View.VISIBLE);

        if (canColor) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.ivIcon.getContext(), isDarken && !isSelected ? android.R.color.white : R.color.secondary_text));
            holder.tvDate.setTextColor(ContextCompat.getColor(holder.ivIcon.getContext(), isDarken && !isSelected ? android.R.color.white : R.color.secondary_text));
        }

        ViewCompat.setBackground(holder.vColor, Ripple.getAdaptiveRippleDrawable(bean.getColor(), Util.darker(bean.getColor())));

    }

    @Override
    protected Bean getConvertedObject(DataSnapshot snap) {
        try {
            String clazz = snap.child(VaultCnt.CLAZZ).getValue(String.class);
            return (Bean) snap.getValue(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public ArrayList<Integer> getDataSelected() {
        return dataSelected;
    }

    public int getSelectedCount() {
        return dataSelected.size();
    }


    /* Click interaction */

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback callback;

    public interface Callback {
        void onItemClicked(int index, boolean longClick);

        void onIconClicked(int index);

    }

    public void toggleSelected(int index) {
        final boolean newState = !dataSelected.contains(index);
        if (newState)
            dataSelected.add(index);
        else
            dataSelected.remove((Integer) index);

        notifyItemChanged(index);
    }

    public void clearSelected() {
        if (getSelectedCount() > 0) {
            dataSelected.clear();
            notifyDataSetChanged();
        }
    }

    public void attachSwipe(RecyclerView rv) {
        if (Pref.with(rv.getContext()).isSwipeToDeleteActive())
            swipeToDismiss.attachToRecyclerView(rv);
    }

    ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final Bean bean = getItem(viewHolder.getLayoutPosition());
            VaultDAO.trash(bean);
            Snack.show(viewHolder.itemView, R.string.moved_to_trash, R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VaultDAO.trash(bean);
                }
            });
        }
    });


    @Override
    protected void itemAdded(Bean item, String key, int position) {
        super.itemAdded(item, key, position);

        callAdapterLoad(true);
    }

    @Override
    protected void itemRemoved(Bean item, String key, int position) {
        super.itemRemoved(item, key, position);

        callAdapterLoad(getItemCount() > 0);
    }

    private boolean last = false;

    private void callAdapterLoad(boolean hasItems){
        if (last != hasItems){
            if (onAdapterLoad != null) {
                last = hasItems;
                onAdapterLoad.onLoad(hasItems);
            }
        }
    }

    private OnAdapterLoad onAdapterLoad;

    public void setOnAdapterLoad(OnAdapterLoad onAdapterLoad) {
        this.onAdapterLoad = onAdapterLoad;
    }

    public interface OnAdapterLoad {
        void onLoad(boolean hasItems);
    }
}