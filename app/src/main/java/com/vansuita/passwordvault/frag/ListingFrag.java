package com.vansuita.passwordvault.frag;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.Main;
import com.vansuita.passwordvault.act.Store;
import com.vansuita.passwordvault.adapter.VaultListAdapter;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.enums.EShowType;
import com.vansuita.passwordvault.fire.dao.VaultDAO;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.view.Snack;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class ListingFrag extends Fragment implements VaultListAdapter.Callback, SearchView.OnQueryTextListener {

    @BindView(R.id.recycle_view)
    RecyclerView rvVaultList;

    private Main main;
    private MaterialCab cab;
    private VaultListAdapter adapter;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private MenuItem editMenuItem;
    private ECategory category;
    private EShowType showType;

    public static ListingFrag newInstance(ECategory category) {
        return newInstance(category, EShowType.HOME);
    }

    public static ListingFrag newInstance(ECategory category, EShowType showType) {
        ListingFrag f = new ListingFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ECategory.TAG, category);
        bundle.putSerializable(EShowType.TAG, showType);
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

        setHasOptionsMenu(true);


        Bundle bundle = getArguments();
        this.category = (ECategory) bundle.getSerializable(ECategory.TAG);
        this.showType = (EShowType) bundle.getSerializable(EShowType.TAG);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        if (Pref.with(getContext()).isLastFirst()) {
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
        }

        rvVaultList.setLayoutManager(layoutManager);
        rvVaultList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            if (adapter == null) {
                this.adapter = new VaultListAdapter(VaultDAO.on(category, showType).get());
                this.adapter.setCallback(this);
                this.adapter.attachSwipe(rvVaultList);

                rvVaultList.setAdapter(adapter);
            }

        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onIconClicked(int index) {
        onItemSelected(index);
    }

    @Override
    public void onItemClicked(int index, boolean longClick) {
        if (longClick || (cab.isActive())) {
            onItemSelected(index);
            return;
        } else if (!longClick) {
            openEditItem(index);
        }
    }

    private void openEditItem(int index) {
        startActivity(Store.openingIntent(getContext(), adapter.getItem(index)));
    }


    private void onItemSelected(int index) {
        adapter.toggleSelected(index);

        if (adapter.getSelectedCount() == 0) {
            cab.finish();
            return;
        }

        if (!cab.isActive())
            cab.start(callback);

        cab.setTitle(getString(R.string.x_selected, adapter.getSelectedCount()));

        if (editMenuItem != null) {
            editMenuItem.setVisible(adapter.getSelectedCount() == 1);
        }
    }


    MaterialCab.Callback callback = new MaterialCab.Callback() {

        @Override
        public boolean onCabCreated(MaterialCab cab, Menu menu) {
            main.selectionState(true);


            ViewCompat.setElevation(cab.getToolbar(), 0.01f);

            menu.findItem(R.id.action_favorite).setVisible(!isShowingTrash());
            menu.findItem(R.id.action_palette).setVisible(!isShowingTrash() && Pref.with(getContext()).canChangeItemsColor());
            menu.findItem(R.id.action_undo).setVisible(isShowingTrash());
            editMenuItem = menu.findItem(R.id.action_edit);

            return true;
        }

        @Override
        public boolean onCabItemClicked(final MenuItem item) {
            int msg = 0;

            switch (item.getItemId()) {
                case R.id.action_favorite:
                    msg = R.string.toggled_favorite;

                    for (Integer pos : adapter.getDataSelected()) {
                        VaultDAO.favorite(adapter.getItem(pos));
                    }
                    break;

                case R.id.action_undo:
                    msg = R.string.restored;

                    for (Integer pos : adapter.getDataSelected()) {
                        VaultDAO.trash(adapter.getItem(pos));
                    }
                    break;

                case R.id.action_palette:

                    main.setOnColorCallBack(new ColorChooserDialog.ColorCallback() {
                        @Override
                        public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
                            for (Integer pos : adapter.getDataSelected()) {
                                VaultDAO.color(selectedColor, adapter.getItem(pos));
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
                            .allowUserColorInput(false)
                            .customButton(R.string.update)
                            .show();

                    break;

                case R.id.action_delete:
                    msg = isShowingTrash() ? R.string.deleted : R.string.moved_to_trash;

                    for (Integer pos : adapter.getDataSelected()) {
                        if (isShowingTrash()) {
                            VaultDAO.delete(adapter.getItem(pos));
                        } else {
                            VaultDAO.trash(adapter.getItem(pos));
                        }
                    }
                    break;


                case R.id.action_edit:
                    openEditItem(adapter.getDataSelected().get(0));
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
            editMenuItem = null;
            return true;
        }
    };

    private boolean isShowingTrash() {
        return showType == EShowType.TRASH;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && adapter != null) {
            adapter.clearSelected();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_hint));
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (searchMenuItem.isActionViewExpanded()) {
            MenuItemCompat.collapseActionView(searchMenuItem);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }
}
