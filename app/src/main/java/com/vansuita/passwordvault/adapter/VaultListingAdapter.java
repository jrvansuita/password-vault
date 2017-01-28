package com.vansuita.passwordvault.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class VaultListingAdapter extends RecyclerView.Adapter<VaultListingAdapter.ViewHolder> {

    private LinkedList data = new LinkedList();
    private HashMap<String, Object> map = new HashMap();
    private ArrayList<Integer> dataSelected = new ArrayList();

    @Override
    public VaultListingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean isSelected = dataSelected.contains(position);
        holder.root.setActivated(isSelected);
        Bean b = getItem(position);

        holder.tvTitle.setText(b.getTitle());
        holder.tvDate.setText(DateFormat.getDateTimeInstance().format(b.getDate()));
        holder.ivIcon.setImageResource(isSelected ? R.mipmap.ic_checked : b.getCategory().getIconRes());

        boolean isDarken = Util.isColorDark(b.getColor());

        holder.tvTitle.setTextColor(ContextCompat.getColor(holder.ivIcon.getContext(), isDarken && !isSelected ? android.R.color.white : R.color.primary_text));
        holder.tvDate.setTextColor(ContextCompat.getColor(holder.ivIcon.getContext(), isDarken && !isSelected ? android.R.color.white : R.color.secondary_text));

        holder.vColor.setBackgroundColor(b.getColor());

        UI.setFavorite(holder.vFavorite, !isSelected && b.isFavorite());
    }

    public ArrayList<Integer> getDataSelected() {
        return dataSelected;
    }

    public int getSelectedCount() {
        return dataSelected.size();
    }


    public Bean getItem(int position) {
        return (Bean) data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView tvTitle;
        TextView tvDate;
        ImageView ivIcon;
        View vFavorite;
        View vColor;

        public ViewHolder(View v) {
            super(v);

            root = v.findViewById(R.id.content);
            tvTitle = (TextView) v.findViewById(R.id.title);
            tvDate = (TextView) v.findViewById(R.id.date);
            ivIcon = (ImageView) v.findViewById(R.id.icon);
            vFavorite = v.findViewById(R.id.favorite);
            vColor = v.findViewById(R.id.color);

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
                    return false;
                }
            });

        }
    }

    /* Click interation */


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


    /* List interation */

    private int addItem(Bean o) {
        data.add(o);
        map.put(o.getKey(), o);
        return data.size() - 1;
    }

    public void add(Bean o) {
        notifyItemInserted(addItem(o));
    }

    private int addItemAt(int pos, Bean o) {
        data.add(pos, o);
        map.put(o.getKey(), o);
        return pos;
    }

    private int updateItem(Bean o) {
        int pos = data.indexOf(map.get(o.getKey()));

        if (pos >= 0) {
            data.set(pos, o);
            map.put(o.getKey(), o);
        }

        return pos;
    }

    public void update(Bean o) {
        notifyItemChanged(updateItem(o));
    }

    private int removeItem(String key) {
        int pos = data.indexOf(map.get(key));
        data.remove(pos);
        map.remove(key);
        return pos;
    }

    public void remove(String key) {
        notifyItemRemoved(removeItem(key));
    }

    public void move(Bean o, String prev) {
        int pos = data.indexOf(map.get(o.getKey()));

        data.remove(pos);

        if (prev == null) {
            addItemAt(0, o);
        } else {
            int prevPos = data.indexOf(map.get(prev));
            int nextIndex = prevPos + 1;

            if (nextIndex == data.size()) {
                addItem(o);
            } else {
                addItemAt(nextIndex, o);
            }
        }

        int finalPos = data.indexOf(map.get(o.getKey()));

        notifyItemMoved(pos, finalPos);
    }


    public void attachSwipe(RecyclerView rv) {
        // swipeToDismiss.attachToRecyclerView(rv);
    }

    /*ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final Bean bean = getItem(viewHolder.getLayoutPosition());
            DataAccess.trash(bean);
            Snack.show(viewHolder.itemView, R.string.deleted, R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataAccess.trash(bean);
                }
            });
        }
    });*/
}
