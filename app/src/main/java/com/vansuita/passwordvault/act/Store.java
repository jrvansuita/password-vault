package com.vansuita.passwordvault.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.VaultCnt;
import com.vansuita.passwordvault.enums.ECategory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jrvansuita on 16/01/17.
 */

public class Store extends AbstractActivity implements ColorChooserDialog.ColorCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public static Intent openingIntent(Context context, ECategory e) {
        return openingIntent(context, e, null);
    }

    public static Intent openingIntent(Context context, Bean bean) {
        return openingIntent(context, bean.getCategory(), bean);
    }

    private static Intent openingIntent(Context context, ECategory e, Bean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ECategory.TAG, e);
        bundle.putSerializable(VaultCnt.NAME, bean);
        Intent intent = new Intent(context, Store.class);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.store_activity);

        ButterKnife.bind(Store.this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onSetup();
    }

    private void onSetup() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                new Exception();

            Class<? extends Fragment> clazz = ((ECategory) bundle.getSerializable(ECategory.TAG)).getFragClass();
            Fragment fragment = clazz.newInstance();
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.fragment_holder, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private ColorChooserDialog.ColorCallback onColorCallBack;

    public void setOnColorCallBack(ColorChooserDialog.ColorCallback onColorCallBack) {
        this.onColorCallBack = onColorCallBack;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (onColorCallBack != null) {
            onColorCallBack.onColorSelection(dialog, selectedColor);
        }
    }
}
