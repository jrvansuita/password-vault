package com.vansuita.passwordvault.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.BeanCnt;
import com.vansuita.passwordvault.enums.ECategory;

/**
 * Created by jrvansuita on 16/01/17.
 */

public class Store extends AppCompatActivity {

    private FrameLayout root;

    public static Intent openingIntent(Context context, ECategory e) {
        return openingIntent(context, e, null);
    }

    public static Intent openingIntent(Context context, Bean bean) {
        return openingIntent(context, bean.getCategory(), bean);
    }

    private static Intent openingIntent(Context context, ECategory e, Bean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ECategory.TYPE, e);
        bundle.putSerializable(BeanCnt.NAME, bean);
        Intent intent = new Intent(context, Store.class);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        root.setId(R.id.root_view);

        setContentView(root);

        onSetup();

    }

    private void onSetup() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                new Exception();

            Class<? extends Fragment> clazz = ((ECategory) bundle.getSerializable(ECategory.TYPE)).getFragClass();
            Fragment fragment = clazz.newInstance();
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction().add(R.id.root_view, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
