package com.vansuita.passwordvault.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrvansuita on 18/01/17.
 */

public class DomainAdapter extends ArrayAdapter<Domain> {

    private List<Domain> original = new ArrayList();
    private ArrayList<Domain> suggestions = new ArrayList<>();

    public DomainAdapter(Context context) {
        super(context, 0);


        original.add(new Domain("gmail.com", R.mipmap.gmail));
        original.add(new Domain("outlook.com", R.mipmap.outlook));
        original.add(new Domain("hotmail.com", R.mipmap.hotmail));
        original.add(new Domain("yahoo.com", R.mipmap.yahoo));
        original.add(new Domain("terra.com", R.mipmap.terra));
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.domain_item, null);

        Domain domain = getItem(position);
        ((ImageView) v.findViewById(R.id.icon)).setImageResource(domain.getIcon());
        ((TextView) v.findViewById(R.id.name)).setText(domain.getName());

        return v;
    }

    @Nullable
    @Override
    public Domain getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new CustomFilter();


    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (original != null && constraint != null)
                for (Domain domain : original) {
                    if (domain.getName().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        suggestions.add(domain);
                }

            FilterResults results = new FilterResults();
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public Domain findDomain(String name) {
        if (name != null)
            for (Domain domain : original)
                if (domain.getName().equalsIgnoreCase(name.toString().toLowerCase()))
                    return domain;

        return null;
    }
}
