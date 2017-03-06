package com.vansuita.passwordvault.frag.base;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.Store;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.VaultCnt;
import com.vansuita.passwordvault.fire.dao.VaultDAO;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.util.Visible;
import com.vansuita.passwordvault.view.Snack;

import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vansuita.passwordvault.R.menu.cab;

/**
 * Created by jrvansuita on 17/01/17.
 */

public abstract class BaseStoreFragment<T extends Bean> extends Fragment implements IBaseStoreFragment<T> {

    @BindView(R.id.screen_title)
    TextView tvTitle;

    @BindView(R.id.title)
    EditText edTitle;

    @BindView(R.id.title_label)
    TextInputLayout tilTitle;

    @BindView(R.id.dates)
    TextView tvDates;


    @BindView(R.id.save)
    Button btSave;

    FrameLayout vChildHolder;

    private MenuItem delete;
    private MenuItem favorite;
    private MenuItem palette;

    private Store activity;

    /**
     * A Bean instance
     **/
    private T object;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (Store) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_store, null, false);

        vChildHolder = (FrameLayout) view.findViewById(R.id.child_holder);
        inflater.inflate(getChildViewId(), vChildHolder);

        ButterKnife.bind(this, view);

        setup();

        loadObject();

        if (!object.isNew())
            load(object);

        return view;
    }

    private void loadObject() {
        if (getArguments() != null && getArguments().get(VaultCnt.NAME) != null) {
            object = (T) getArguments().getSerializable(VaultCnt.NAME);
        } else {
            object = newObjectInstance();
        }
    }

    private T newObjectInstance() {
        try {
            return (T) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();

        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return null;
        }
    }

    private void setup() {
        setHasOptionsMenu(true);

        tvTitle.setText(getScreenTitle());
        Visible.with(tvDates).gone(true);

        if (getSubmitElement() != null)
            getSubmitElement().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent e) {
                    if ((e.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        save(null);
                        return true;
                    }
                    return false;
                }
            });

        onSetup();
    }

    public void load(T object) {
        btSave.setText(R.string.update);

        Visible.with(tvDates).gone(false);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        String dates = getString(R.string.creation) + " " + dateFormat.format(object.getDate()) + "   ";
        dates += getString(R.string.last_update) + " " + dateFormat.format(object.getLastDate());

        tvDates.setText(dates);

        edTitle.setText(object.getTitle());

        onLoad(object);
    }

    public boolean canStore() {
        boolean titleOk = UI.error(tilTitle, Validation.isEmpty(edTitle), R.string.forgot_title);

        return titleOk && onCanStore();
    }

    @OnClick(R.id.save)
    protected void save(View v) {
        autoFillTitle();

        if (canStore()) {
            store();
            VaultDAO.put(object);
            finish();
        }
    }

    private void store() {
        object.setTitle(edTitle.getText().toString());
        onStore(object);
    }

    @Override
    public String getAutoFillTitleValue() {
        return "";
    }

    protected void autoFillTitle() {
        if (Validation.isEmpty(edTitle) && !getAutoFillTitleValue().isEmpty())
            edTitle.setText(getAutoFillTitleValue());
    }

    private void clear() {
        edTitle.setText("");
        object = newObjectInstance();
        getArguments().remove(VaultCnt.NAME);
        btSave.setText(R.string.save);

        onClear();

        getActivity().invalidateOptionsMenu();
        edTitle.requestFocus();
    }

    private void finish() {
        Util.hideKeyboard(getActivity());

        Snack.show(edTitle, object.isNew() ? R.string.saved : R.string.updated, R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(cab, menu);
        UI.menuVisibility(menu, false);

        delete = menu.findItem(R.id.action_delete);
        favorite = menu.findItem(R.id.action_favorite);
        palette = menu.findItem(R.id.action_palette);

        favorite.setVisible(true);
        palette.setVisible(Pref.with(getContext()).canChangeItemsColor());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_favorite:

                if (object.isNew()) {
                    object.setFavorite(!object.isFavorite());
                } else {
                    VaultDAO.favorite(object);
                }

                getActivity().invalidateOptionsMenu();

                break;

            case R.id.action_delete:
                if (!object.isNew()) {
                    Snack.show(edTitle, R.string.moved_to_trash, R.string.close, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    });

                    VaultDAO.trash(object);
                    clear();
                }
                break;

            case R.id.action_palette:

                activity.setOnColorCallBack(new ColorChooserDialog.ColorCallback() {
                    @Override
                    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
                        if (object.isNew()) {
                            object.setColor(selectedColor);
                        } else {
                            VaultDAO.color(selectedColor, object);
                        }

                        getActivity().invalidateOptionsMenu();
                    }
                });

                new ColorChooserDialog.Builder(activity, R.string.color_palette)
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

            case android.R.id.home:
                getActivity().finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        delete.setVisible(!object.isNew());

        if (object.isFavorite()) {
            Icon.on(favorite).color(R.color.favorite).icon(R.mipmap.ic_favorite).put();
        } else {
            favorite.setIcon(R.mipmap.ic_favorite);
        }

        if (object.getColor() != 0) {
            Icon.on(palette).color(object.getColor()).icon(R.mipmap.ic_palette).put();
        }
    }

    protected void applyPassword(TextInputLayout in){
        in.setPasswordVisibilityToggleEnabled(!Pref.with(getContext()).isHidePasswords());
    }
}
