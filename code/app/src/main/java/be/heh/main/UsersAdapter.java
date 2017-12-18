package be.heh.main;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import be.heh.models.User;

/**
 * Created by alexandre on 11/12/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {

    Context context;

    ImageView iv_userItem_userIcon;

    TextView tv_userItem_lastname;
    TextView tv_userItem_firstname;
    TextView tv_userItem_email;
    TextView tv_userItem_rights;

    ImageButton btn_userItem_passwordIcon;
    ImageButton btn_userItem_rightsIcon;
    ImageButton btn_userItem_removeIcon;

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = getItem(position);
        final String rights;
        Integer icon;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }


        iv_userItem_userIcon = convertView.findViewById(R.id.iv_userItem_userIcon);
        tv_userItem_lastname = convertView.findViewById(R.id.tv_userItem_lastname);
        tv_userItem_firstname = convertView.findViewById(R.id.tv_userItem_firstname);
        tv_userItem_email = convertView.findViewById(R.id.tv_userItem_email);
        tv_userItem_rights = convertView.findViewById(R.id.tv_userItem_rights);
        btn_userItem_passwordIcon = convertView.findViewById(R.id.btn_userItem_passwordIcon);
        btn_userItem_rightsIcon = convertView.findViewById(R.id.btn_userItem_rightsIcon);
        btn_userItem_removeIcon = convertView.findViewById(R.id.btn_userItem_removeIcon);


        if (user.getRights().equals("2")) {
            rights = context.getString(R.string.read);
            icon = R.drawable.r_user;
        } else if (user.getRights().equals("1")) {
            rights = context.getString(R.string.read_write);
            icon = R.drawable.rw_user;
        } else {
            rights = context.getString(R.string.super_user);
            btn_userItem_rightsIcon.setVisibility(View.GONE);
            btn_userItem_removeIcon.setVisibility(View.GONE);
            icon = R.drawable.super_user;
        }

        iv_userItem_userIcon.setImageResource(icon);
        tv_userItem_lastname.setText(Html.fromHtml(context.getString(R.string.lastname) + " : <b>" + user.getLastname() + "</b>"));
        tv_userItem_firstname.setText(Html.fromHtml(context.getString(R.string.firstname) + " : <b>" + user.getFirstname() + "</b>"));
        tv_userItem_email.setText(Html.fromHtml(context.getString(R.string.email) + " : <b>" + user.getEmail() + "</b>"));
        tv_userItem_rights.setText(Html.fromHtml(context.getString(R.string.rights) + " : <b>" + rights + "</b>"));

        btn_userItem_passwordIcon.setTag(position);
        btn_userItem_rightsIcon.setTag(position);
        btn_userItem_removeIcon.setTag(position);

        return convertView;
    }
}