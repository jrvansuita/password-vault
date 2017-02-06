package com.vansuita.passwordvault.fire.adapter;


import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.vansuita.passwordvault.bean.Bean;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Matteo on 24/08/2015.
 * Updated on 19/06/2016 following https://firebase.google.com/support/guides/firebase-android.
 * <p>
 * This class is a generic way of backing an Android RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location.
 * It marshals received data into the given class type.
 * Extend this class and provide an implementation of the abstract methods, which will notify when
 * the adapter list changes.
 * <p>
 * This class also simplifies the management of configuration change (e.g.: device rotation)
 * allowing the restore of the list.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the
 *            given Firebase location
 */
public abstract class FirebaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private Query mQuery;
    private ArrayList<T> mItems;
    private ArrayList<T> mShowItems;
    private ArrayList<String> mKeys;

    /**
     * @param query The Firebase location to watch for data changes.
     *              Can also be a slice of a location, using some combination of
     *              <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     */
    public FirebaseRecyclerAdapter(Query query) {
        this(query, null, null);
    }

    /**
     * @param query The Firebase location to watch for data changes.
     *              Can also be a slice of a location, using some combination of
     *              <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param items List of items that will load the adapter before starting the listener.
     *              Generally null or empty, but this can be useful when dealing with a
     *              configuration change (e.g.: reloading the adapter after a device rotation).
     *              Be careful: keys must be coherent with this list.
     * @param keys  List of keys of items that will load the adapter before starting the listener.
     *              Generally null or empty, but this can be useful when dealing with a
     *              configuration change (e.g.: reloading the adapter after a device rotation).
     *              Be careful: items must be coherent with this list.
     */
    public FirebaseRecyclerAdapter(Query query,
                                   @Nullable ArrayList<T> items,
                                   @Nullable ArrayList<String> keys) {
        this.mQuery = query;
        if (items != null && keys != null) {
            this.mItems = items;
            this.mShowItems = items;
            this.mKeys = keys;
        } else {
            mItems = new ArrayList<T>();
            mShowItems = new ArrayList<T>();
            mKeys = new ArrayList<String>();
        }
        query.addChildEventListener(mListener);
    }

    private ChildEventListener mListener = new ChildEventListener() {

        private void add(String key, T o) {
            mItems.add(o);
            mShowItems.add(o);
            mKeys.add(key);
        }


        private void addAt(int pos, String key, T o) {
            mItems.add(pos, o);
            mShowItems.add(pos, o);
            mKeys.add(pos, key);
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
           //String clazz =  dataSnapshot.child(VaultCnt.CLAZZ).getValue(String.class);

           // if (clazz != null) {

                String key = dataSnapshot.getKey();

                if (!mKeys.contains(key)) {
                    T item = getConvertedObject(dataSnapshot);
                    int insertedPosition;
                    if (previousChildName == null) {
                        addAt(0, key, item);
                        insertedPosition = 0;
                    } else {
                        int previousIndex = mKeys.indexOf(previousChildName);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mItems.size()) {
                            add(key, item);
                        } else {
                            addAt(nextIndex, key, item);
                        }

                        insertedPosition = nextIndex;
                    }

                    notifyItemInserted(insertedPosition);
                    itemAdded(item, key, insertedPosition);
                }
            //}
        }


        private void set(int pos, T o) {
            if (pos >= 0) {
                mItems.set(pos, o);
                mShowItems.set(pos, o);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                T oldItem = mItems.get(index);
                T newItem = getConvertedObject(dataSnapshot);

                set(index, newItem);

                notifyItemChanged(index);
                itemChanged(oldItem, newItem, key, index);
            }
        }


        private void remove(int pos) {
            mKeys.remove(pos);
            mShowItems.remove(pos);
            mItems.remove(pos);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                T item = mItems.get(index);

                remove(index);

                notifyItemRemoved(index);
                itemRemoved(item, key, index);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();

            int index = mKeys.indexOf(key);
            T item = getConvertedObject(dataSnapshot);

            remove(index);

            int newPosition;
            if (previousChildName == null) {
                addAt(0, key, item);
                newPosition = 0;
            } else {
                int previousIndex = mKeys.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == mItems.size()) {
                    add(key, item);
                } else {
                    addAt(nextIndex, key, item);
                }
                newPosition = nextIndex;
            }
            notifyItemMoved(index, newPosition);
            itemMoved(item, key, index, newPosition);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur.");
        }

    };

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (mShowItems != null) ? mShowItems.size() : 0;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    public void destroy() {
        mQuery.removeEventListener(mListener);
    }

    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<T> getItems() {
        return mShowItems;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return mKeys;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public T getItem(int position) {
        return mShowItems.get(position);
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(T item) {
        return mShowItems != null && mShowItems.size() > 0 ? mShowItems.indexOf(item) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(T item) {
        return mShowItems != null && mShowItems.contains(item);
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */

    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected void itemAdded(T item, String key, int position) {

    }

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected void itemChanged(T oldItem, T newItem, String key, int position) {

    }

    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected void itemRemoved(T item, String key, int position) {

    }

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected void itemMoved(T item, String key, int oldPosition, int newPosition) {

    }

    /**
     * Converts the data snapshot to generic object
     *
     * @param snapshot Result
     * @return Data converted
     */
    protected T getConvertedObject(DataSnapshot snapshot) {
        return snapshot.getValue(getGenericClass());
    }

    /**
     * Returns a class reference from generic T.
     */
    @SuppressWarnings("unchecked")
    private Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mShowItems.clear();
                mShowItems.addAll((Collection) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mItems;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }


            protected List getFilteredResults(String constraint) {
                List results = new ArrayList<>();

                for (Object item : mItems) {
                    if (((Bean) item).getTitle().toLowerCase().contains(constraint)) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }


}

