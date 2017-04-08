package com.vansuita.passwordvault.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
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
import com.vansuita.passwordvault.adapter.CategoryChooserAdapter;
import com.vansuita.passwordvault.adapter.CategoryListPageAdapter;
import com.vansuita.passwordvault.adapter.FavoriteListPageAdapter;
import com.vansuita.passwordvault.adapter.TrashListPageAdapter;
import com.vansuita.passwordvault.ads.Ads;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.fire.account.Account;
import com.vansuita.passwordvault.fire.storage.ImageStorage;
import com.vansuita.passwordvault.lis.IOnResult;
import com.vansuita.passwordvault.pref.Billing;
import com.vansuita.passwordvault.receiver.NetworkStateChangeReceiver;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.view.Snack;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class Main extends AbstractActivity implements ColorChooserDialog.ColorCallback, IPickResult, NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

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
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private MaterialCab cab;
    private FirebaseAuth auth;
    private MaterialDialog progress;
    private Ads advertise;


    public static Intent intent(Context context) {
        Intent intent = new Intent(context, Main.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(Main.this);
        onSetup();

        onNavigationSelected(R.id.home);

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        pager.addOnPageChangeListener(this);

        this.cab = new MaterialCab(this, R.id.cab_stub)
                .setTitle(getString(R.string.category))
                .setMenu(R.menu.cab)
                .setPopupMenuTheme(R.style.ThemeOverlay_AppCompat_Light)
                .setBackgroundColorRes(R.color.primary_inactive)
                .setCloseDrawableRes(R.drawable.mcab_nav_back);


        if (!Billing.with(this).isRemoveAdsPurchased()) {
            //Bottom banner
            Ads.with(getWindow()).showBannerAd();

            //Fullscreen banner
            advertise = Ads.with(this).setAdUnitId(R.string.banner_ad_unit_id);
            advertise.loadFullScreenAd();
        }
    }

    private void swapViewPagerAdapter(PagerAdapter adapter) {
        pager.setAdapter(adapter);
        tabLayout.setViewPager(pager);
    }

    @OnClick(R.id.fab)
    public void onFabClick(View v) {
        CategoryChooserAdapter adapter = new CategoryChooserAdapter();

        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title(R.string.category)
                .adapter(adapter, null)
                .cancelable(true)
                .show();

        adapter.setOnClickItem(new CategoryChooserAdapter.OnItemClick() {
            @Override
            public void onItemClicked(ECategory type) {
                startActivity(Store.openingIntent(Main.this, type));
                md.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem i) {
        return onNavigationSelected(i.getItemId());
    }

    public boolean onNavigationSelected(int res) {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        switch (res) {
            case R.id.home:
                //Set the toolbar title
                toolbar.setTitle(R.string.app_name);
                //Change the adapter
                swapViewPagerAdapter(new CategoryListPageAdapter(getSupportFragmentManager(), this));
                //Activate auto hide toolbar on scroll
                toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                //Show the tabs
                tabLayout.setVisibility(View.VISIBLE);
                //Show the fab
                fab.show();
                break;
            case R.id.favorite:
                //Set the toolbar title
                toolbar.setTitle(R.string.favorites);
                //Change the adapter
                swapViewPagerAdapter(new FavoriteListPageAdapter(getSupportFragmentManager(), this));
                //Activate auto hide toolbar on scroll
                toolbarLayoutParams.setScrollFlags(0);
                //Show the tabs
                tabLayout.setVisibility(View.GONE);
                //Show the fab
                fab.hide();
                break;
            case R.id.trash:
                toolbar.setTitle(R.string.trash);
                //Change the adapter
                swapViewPagerAdapter(new TrashListPageAdapter(getSupportFragmentManager(), this));
                //Deactivate auto hide toolbar on scroll
                toolbarLayoutParams.setScrollFlags(0);
                //Hide the tabs
                tabLayout.setVisibility(View.GONE);
                //Hide the fab
                fab.hide();
                break;

            case R.id.settings:
                startActivity(new Intent(Main.this, Preferences.class));
                break;

            case R.id.logout:
                Account.with(Main.this).signOut();
                break;
            case R.id.lock:
                Lock.start(this, false);
                break;
            case R.id.exit:
                System.exit(0);
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
            advertise.showFullScreenAd();
            super.onBackPressed();
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
                tvEmail.setText(Util.coalesce(user.getEmail(), ""));

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
                        PickSetup setup = new PickSetup().setMaxSize(300);

                        Lock.isIgnoreAction(true);

                        PickImageDialog.build(setup).show(Main.this);
                    }
                });
            }
        }
    };

    @Override
    public void onPickResult(PickResult r) {
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
        }).store(r.getUri());
    }

    public MaterialCab getCab() {
        return cab;
    }

    public void selectionState(boolean is) {
        if (is) {
            UI.setStatusBarColor(Main.this, R.color.primary_inactive);
            tabLayout.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.primary_inactive));
            fab.hide();
        } else {
            UI.setStatusBarColor(Main.this, android.R.color.transparent);
            tabLayout.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.primary));
            fab.show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lock:
                Lock.start(this, false);
                return true;
            case R.id.exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

