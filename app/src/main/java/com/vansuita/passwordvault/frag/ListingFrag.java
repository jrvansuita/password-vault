package com.vansuita.passwordvault.frag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.Main;
import com.vansuita.passwordvault.act.Store;
import com.vansuita.passwordvault.adapter.VaultListingAdapter;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.fire.database.Vault;
import com.vansuita.passwordvault.lis.IOnFireData;
import com.vansuita.passwordvault.recycle.DividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class ListingFrag extends Fragment implements VaultListingAdapter.Callback {

    @BindView(R.id.recycle_view)
    RecyclerView rvVaultList;

    private Main main;
    private MaterialCab cab;
    private VaultListingAdapter adapter;
    private Vault vault;

    public static ListingFrag newInstance(ECategory e) {
        ListingFrag f = new ListingFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ECategory.TYPE, e);
        f.setArguments(bundle);
        return f;
    }

    private ECategory getCategory() {
        Bundle bundle = getArguments();
        ECategory e = null;

        if (bundle != null)
            return (ECategory) bundle.getSerializable(ECategory.TYPE);

        return e;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.main = ((Main) getActivity());
        this.cab = main.getCab();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new VaultListingAdapter();
        adapter.setCallback(this);
        vault = Vault.on(getCategory()).listeners(new IOnFireData() {

            @Override
            public void add(Bean data) {
                adapter.add(data);
            }

            @Override
            public void changed(Bean data) {
                adapter.update(data);
            }

            @Override
            public void removed(String key) {
                adapter.remove(key);
            }

            @Override
            public void moved(Bean data, String previousChild) {
                adapter.move(data, previousChild);
            }
        });
        vault.get();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_frag, null);

        ButterKnife.bind(this, view);

        setup();

        setRetainInstance(true);

        return view;
    }

    private void setup() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvVaultList.setLayoutManager(mLayoutManager);
        rvVaultList.setItemAnimator(new DefaultItemAnimator());

        rvVaultList.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        rvVaultList.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int index, boolean longClick) {
        if (longClick || (cab.isActive())) {
            onIconClicked(index);
            return;
        }else if (!longClick){
            startActivity(Store.openingIntent(getContext(), adapter.getItem(index)));
        }
    }

    @Override
    public void onIconClicked(int index) {
        adapter.toggleSelected(index);

        if (adapter.getSelectedCount() == 0) {
            cab.finish();
            return;
        }

        if (!cab.isActive())
            cab.start(callback);

        cab.setTitle(getString(R.string.x_selected, adapter.getSelectedCount()));
    }


    MaterialCab.Callback callback = new MaterialCab.Callback() {
        @Override
        public boolean onCabCreated(MaterialCab cab, Menu menu) {
            main.selectionState(true);
            return true;
        }

        @Override
        public boolean onCabItemClicked(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    new MaterialDialog.Builder(main)
                            .title(R.string.delete)
                            .content(R.string.want_delete)
                            .autoDismiss(true)
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    for (Integer pos : adapter.getDataSelected()) {
                                        Vault.delete(adapter.getItem(pos).getKey());
                                    }

                                    onCabFinished(cab);
                                }
                            })

                            .show();

            }
            return true;
        }

        @Override
        public boolean onCabFinished(MaterialCab cab) {
            main.selectionState(false);
            adapter.clearSelected();
            return true;
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && adapter != null) {
            adapter.clearSelected();
        }
    }


}
