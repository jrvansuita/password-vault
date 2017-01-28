package com.vansuita.passwordvault.frag;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.Main;
import com.vansuita.passwordvault.act.Store;
import com.vansuita.passwordvault.adapter.VaultListingAdapter;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.VaultCnt;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.fire.dao.DataAccess;
import com.vansuita.passwordvault.lis.IOnFireData;
import com.vansuita.passwordvault.view.Snack;

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

    private ECategory category;
    private boolean isShowingTrash;

    public static ListingFrag newInstance(ECategory e) {
        return newInstance(e, false);
    }

    public static ListingFrag newInstance(ECategory e, boolean trash) {
        ListingFrag f = new ListingFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ECategory.TYPE, e);
        bundle.putBoolean(VaultCnt.TRASH, trash);
        f.setArguments(bundle);
        return f;
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

        this.adapter = new VaultListingAdapter();
        this.adapter.setCallback(this);

        Bundle bundle = getArguments();
        this.category = (ECategory) bundle.getSerializable(ECategory.TYPE);
        this.isShowingTrash = bundle.getBoolean(VaultCnt.TRASH);

        DataAccess.on(category).trash(isShowingTrash).listeners(new IOnFireData() {

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
        }).get();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_frag, null);

        ButterKnife.bind(this, view);

        setup();

        return view;
    }

    private void setup() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvVaultList.setLayoutManager(mLayoutManager);
        rvVaultList.setItemAnimator(new DefaultItemAnimator());

        //rvVaultList.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        rvVaultList.setAdapter(adapter);
        adapter.attachSwipe(rvVaultList);

    }

    @Override
    public void onItemClicked(int index, boolean longClick) {
        if (longClick || (cab.isActive())) {
            onIconClicked(index);
            return;
        } else if (!longClick) {
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

            ViewCompat.setElevation(cab.getToolbar(), 0.01f);

            menu.findItem(R.id.action_favorite).setVisible(!isShowingTrash);
            menu.findItem(R.id.action_palette).setVisible(!isShowingTrash);
            menu.findItem(R.id.action_undo).setVisible(isShowingTrash);
            menu.findItem(R.id.action_settings).setVisible(false);

            return true;
        }

        @Override
        public boolean onCabItemClicked(final MenuItem item) {
            int msg = 0;

            switch (item.getItemId()) {
                case R.id.action_favorite:
                    msg = R.string.toggled_favorite;

                    for (Integer pos : adapter.getDataSelected()) {
                        DataAccess.favorite(adapter.getItem(pos));
                    }
                    break;

                case R.id.action_undo:
                    msg = R.string.restored;

                    for (Integer pos : adapter.getDataSelected()) {
                        DataAccess.trash(adapter.getItem(pos));
                    }
                    break;

                case R.id.action_palette:

                    main.setOnColorCallBack(new ColorChooserDialog.ColorCallback() {
                        @Override
                        public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
                            for (Integer pos : adapter.getDataSelected()) {
                                DataAccess.color(selectedColor, adapter.getItem(pos));
                            }

                            cab.finish();
                        }
                    });

                    new ColorChooserDialog.Builder(main, R.string.color_palette)
                            .titleSub(R.string.shade)
                            .accentMode(false)
                            .doneButton(R.string.md_done_label)
                            .cancelButton(R.string.md_cancel_label)
                            .backButton(R.string.md_back_label)
                            .dynamicButtonColor(false)
                            .show();

                    break;

                case R.id.action_delete:
                    msg = isShowingTrash ? R.string.deleted : R.string.moved_to_trash;

                    for (Integer pos : adapter.getDataSelected()) {
                        if (isShowingTrash) {
                            DataAccess.delete(adapter.getItem(pos));
                        } else {
                            DataAccess.trash(adapter.getItem(pos));
                        }
                    }
                    break;
            }

            if (msg > 0) {
                Snack.show(rvVaultList, msg, null);
                cab.finish();
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
