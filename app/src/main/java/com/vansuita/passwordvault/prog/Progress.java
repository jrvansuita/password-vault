package com.vansuita.passwordvault.prog;

import android.app.ProgressDialog;
import android.content.Context;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class Progress {

    private Context context;
    private ProgressDialog progressDialog;

    Progress(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setMessage(context.getString(R.string.loading));
    }


    public static Progress get(Context context) {
        return new Progress(context);
    }


    public Progress message(String m) {
        progressDialog.setMessage(m);
        return this;
    }

    public Progress message(int m) {
        progressDialog.setMessage(context.getString(m));
        return this;
    }

    public Progress show() {
        progressDialog.show();
        return this;
    }

    public Progress hide() {
        progressDialog.hide();
        return this;
    }
}
