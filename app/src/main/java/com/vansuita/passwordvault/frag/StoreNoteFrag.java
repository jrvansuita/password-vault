package com.vansuita.passwordvault.frag;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.bean.Note;
import com.vansuita.passwordvault.fire.database.Vault;
import com.vansuita.passwordvault.frag.base.BaseStoreFragment;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Validation;

import butterknife.BindView;

/**
 * Created by jrvansuita on 26/11/16.
 */

public class StoreNoteFrag extends BaseStoreFragment {

    @BindView(R.id.note)
    EditText edNote;

    @BindView(R.id.note_label)
    TextInputLayout tilNote;

    @Override
    public int getChildViewId() {
        return R.layout.note_store;
    }

    @Override
    public int getScreenTitle() {
        return R.string.note_registering;
    }

    @Override
    public String getAutoFillTitleValue() {
        return "";
    }

    @Override
    public TextView getSubmitElement() {
        return edNote;
    }


    @Override
    protected void onLoad(Bean bean) {
        super.onLoad(bean);

        Note note = (Note) bean;

        edNote.setText(note.getNote());
    }

    @Override
    public boolean canStore() {
        return UI.error(tilNote, Validation.isEmpty(edNote), R.string.error_field_required);
    }


    @Override
    public void onStore() {
        Note note = super.getObject(Note.class);

        if (note != null) {
            note.setTitle(getTitleValue());
            note.setNote(edNote.getText().toString());
            Vault.put(note);
            super.onFinish();
        }
    }


    @Override
    protected void onClear() {
        super.onClear();

        edNote.setText("");
    }
}
