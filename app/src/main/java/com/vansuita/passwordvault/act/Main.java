package com.vansuita.passwordvault.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.adapter.CategoryListPageAdapter;
import com.vansuita.passwordvault.adapter.VaultItemChooserAdapter;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.fire.storage.ImageStorage;
import com.vansuita.passwordvault.lis.IOnResult;
import com.vansuita.passwordvault.receiver.NetworkStateChangeReceiver;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.view.Snack;
import com.vansuita.pickimage.IPickResult;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vansuita.passwordvault.R.id.fab;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IPickResult.IPickResultUri, ViewPager.OnPageChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.navigation)
    NavigationView navigation;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tabs)
    SmartTabLayout tabLayout;

    private MaterialCab cab;
    private FirebaseAuth auth;
    private MaterialDialog progress;
    private CategoryListPageAdapter categoryListPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(Main.this);
        onSetup();

        onNavigationSelected(R.id.nav_all);

        internet();
        internetChecking();
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void onSetup() {
        this.auth = FirebaseAuth.getInstance();

        this.progress = new MaterialDialog.Builder(this)
                .contentColorRes(R.color.primary_text)
                .widgetColorRes(R.color.primary_text)
                .content(R.string.loading)
                .progress(true, 0).build();

        setSupportActionBar(toolbar);

        navigation.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        categoryListPageAdapter = new CategoryListPageAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(categoryListPageAdapter);
        pager.addOnPageChangeListener(this);

        tabLayout.setViewPager(pager);

        this.cab = new MaterialCab(this, R.id.cab_stub)
                .setTitleRes(R.string.category)
                .setMenu(R.menu.cab)
                .setPopupMenuTheme(R.style.ThemeOverlay_AppCompat_Light)
                .setBackgroundColorRes(R.color.primary_inactive)
                .setCloseDrawableRes(R.drawable.mcab_nav_back);
    }

    @OnClick(fab)
    public void onFabClick(View v) {

        VaultItemChooserAdapter adapter = new VaultItemChooserAdapter();

        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title(R.string.category)
                .adapter(adapter, null)
                .cancelable(true)
                .show();

        adapter.setOnClickItem(new VaultItemChooserAdapter.OnItemClick() {
            @Override
            public void onItemClicked(ECategory type) {
                startActivity(Store.openingIntent(Main.this, type));
                md.dismiss();
            }
        });
    }

    public boolean onNavigationItemSelected(MenuItem i) {
        return onNavigationSelected(i.getItemId());
    }

    public boolean onNavigationSelected(int res) {
        switch (res) {
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(Main.this, Login.class));
                finish();
                break;
        }

        navigation.setCheckedItem(res);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (cab.isActive()) {
            cab.finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        switch (i.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(i);
        }
    }

    private TextView tvName;
    private TextView tvEmail;
    private ImageView imAvatar;

    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {
                View header = navigation.getHeaderView(0);

                tvName = (TextView) header.findViewById(R.id.name);
                tvEmail = (TextView) header.findViewById(R.id.email);

                tvName.setText(Util.coalesce(user.getDisplayName(), getString(R.string.unknow)));
                tvEmail.setText(Util.coalesce(user.getEmail(), getString(R.string.unknow)));

                imAvatar = (ImageView) header.findViewById(R.id.avatar);

                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(ContextCompat.getColor(Main.this, R.color.primary_dark))
                        .borderWidthDp(2)
                        .cornerRadiusDp(32)
                        .oval(false)
                        .build();


                RequestCreator r;

                if (user.getPhotoUrl() == null) {
                    r = Picasso.with(Main.this).load(R.mipmap.no_pic);
                } else {
                    r = Picasso.with(Main.this).load(user.getPhotoUrl());
                }

                r.fit().transform(transformation).into(imAvatar);

                imAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PickImageDialog.on(Main.this, new PickSetup());
                    }
                });
            }
        }
    };

    @Override
    public void onPickImageResult(Uri uri) {
        progress.show();
        ImageStorage.with(this).setName(auth.getCurrentUser().getUid()).setOnResult(new IOnResult<Uri>() {
            @Override
            public void onResult(Uri result) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(result)
                        .build();

                auth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progress.hide();
                                Snack.show(navigation, R.string.user_profile_updated);
                            }
                        });
            }
        }).store(uri);
    }

    public MaterialCab getCab() {
        return cab;
    }


    public void selectionState(boolean is) {
        if (is) {
            UI.setStatusBarColor(Main.this, R.color.primary_inactive);
            tabLayout.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.primary_inactive));
        } else {
            UI.setStatusBarColor(Main.this, R.color.primary_dark);
            tabLayout.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.primary));
        }
    }

    @Override

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (cab.isActive())
            cab.finish();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void internetChecking() {
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                internet();
            }
        }, intentFilter);
    }

    private void internet() {
        if (!Util.internet(this)) {
            Snack.show(pager, R.string.no_internet, R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    internet();
                }
            });
        }
    }

}

