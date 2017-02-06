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
import com.vansuita.passwordvault.enums.EEmailDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jrvansuita on 18/01/17.
 */

public class DomainAdapter extends ArrayAdapter<EEmailDomain> {

    private List<EEmailDomain> original = new ArrayList();
    private ArrayList<EEmailDomain> suggestions = new ArrayList<>();

    public DomainAdapter(Context context) {
        super(context, 0);

        original.addAll(Arrays.asList(EEmailDomain.values()));
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.domain_item, null);

        EEmailDomain domain = getItem(position);
        ((ImageView) v.findViewById(R.id.icon)).setImageResource(domain.getIcon());
        ((TextView) v.findViewById(R.id.name)).setText(domain.getDomain());

        return v;
    }

    @Nullable
    @Override
    public EEmailDomain getItem(int position) {
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
                for (EEmailDomain domain : original) {
                    if (domain.getDomain().toLowerCase().startsWith(constraint.toString().toLowerCase()))
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

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((EEmailDomain)resultValue).getDomain();
        }
    }

}
