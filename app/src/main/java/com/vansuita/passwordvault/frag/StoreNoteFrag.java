package com.vansuita.passwordvault.frag;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Note;
import com.vansuita.passwordvault.frag.base.BaseStoreFragment;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Validation;

import butterknife.BindView;

/**
 * Created by jrvansuita on 26/11/16.
 */

public class StoreNoteFrag extends BaseStoreFragment<Note> {

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
    public void onSetup() {
    }

    @Override
    public void onLoad(Note note) {
        edNote.setText(note.getNote());
    }

    @Override
    public boolean onCanStore() {
        return UI.error(tilNote, Validation.isEmpty(edNote), R.string.error_field_required);
    }

    @Override
    public void onStore(Note note) {
        note.setNote(edNote.getText().toString());
    }

    @Override
    public void onClear() {
        edNote.setText("");
    }
}
