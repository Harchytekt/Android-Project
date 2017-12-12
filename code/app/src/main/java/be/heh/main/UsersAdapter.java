package be.heh.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import db.User;

/**
 * Created by alexandre on 11/12/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {

    ImageView iv_userItem_userIcon;
    TextView tv_userItem_lastname;
    TextView tv_userItem_firstname;
    TextView tv_userItem_email;
    TextView tv_userItem_rights;
    ImageButton btn_userItem_editIcon;
    ImageButton btn_userItem_removeIcon;
    CharSequence[] rights = {" Lecture seule "," Lecture-Écriture "};
    AlertDialog rightsDialog;
    String chosenRights;
    User user;

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);
        final String rights;
        Integer icon;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }


        iv_userItem_userIcon = convertView.findViewById(R.id.iv_userItem_userIcon);
        tv_userItem_lastname = convertView.findViewById(R.id.tv_userItem_lastname);
        tv_userItem_firstname = convertView.findViewById(R.id.tv_userItem_firstname);
        tv_userItem_email = convertView.findViewById(R.id.tv_userItem_email);
        tv_userItem_rights = convertView.findViewById(R.id.tv_userItem_rights);
        btn_userItem_editIcon = convertView.findViewById(R.id.btn_userItem_editIcon);
        btn_userItem_removeIcon = convertView.findViewById(R.id.btn_userItem_removeIcon);


        if (user.getRights().equals("2")) {
            rights = "Lecture seule";
            icon = R.drawable.r_user;
        } else if (user.getRights().equals("1")) {
            rights = "Lecture-Écriture";
            icon = R.drawable.rw_user;
        } else {
            rights = "Super-utilisateur";
            btn_userItem_removeIcon.setVisibility(View.GONE);
            icon = R.drawable.super_user;
        }

        iv_userItem_userIcon.setImageResource(icon);
        tv_userItem_lastname.setText(Html.fromHtml("Nom de famille : <b>" + user.getLastname() + "</b>"));
        tv_userItem_firstname.setText(Html.fromHtml("Prénom : <b>" + user.getFirstname() + "</b>"));
        tv_userItem_email.setText(Html.fromHtml("Email : <b>" + user.getEmail() + "</b>"));
        tv_userItem_rights.setText(Html.fromHtml("Droits : <b>" + rights + "</b>"));
        btn_userItem_editIcon.setTag(position);
        btn_userItem_removeIcon.setTag(position);

        return convertView;
    }
}