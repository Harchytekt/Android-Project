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

import be.heh.database.Automaton;

/**
 * Created by alexandre on 11/12/17.
 */

public class AutomatonsAdapter extends ArrayAdapter<Automaton> {

    ImageView iv_automatonItem_automatonIcon;

    TextView tv_automatonItem_name;
    TextView tv_automatonItem_ip;
    TextView tv_automatonItem_infos;
    TextView tv_automatonItem_type;

    /*ImageButton btn_userItem_passwordIcon;
    ImageButton btn_userItem_rightsIcon;
    ImageButton btn_userItem_removeIcon;*/

    public AutomatonsAdapter(Context context, ArrayList<Automaton> automaton) {
        super(context, 0, automaton);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Automaton automaton = getItem(position);
        final String type;
        Integer icon;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_automaton, parent, false);
        }


        iv_automatonItem_automatonIcon = convertView.findViewById(R.id.iv_automatonItem_automatonIcon);
        tv_automatonItem_name = convertView.findViewById(R.id.tv_automatonItem_name);
        tv_automatonItem_ip = convertView.findViewById(R.id.tv_automatonItem_ip);
        tv_automatonItem_infos = convertView.findViewById(R.id.tv_automatonItem_infos);
        tv_automatonItem_type = convertView.findViewById(R.id.tv_automatonItem_type);
        /*btn_userItem_passwordIcon = convertView.findViewById(R.id.btn_userItem_passwordIcon);
        btn_userItem_rightsIcon = convertView.findViewById(R.id.btn_userItem_rightsIcon);
        btn_userItem_removeIcon = convertView.findViewById(R.id.btn_userItem_removeIcon);*/

        if (automaton.getType().equals("0")) {
            type = "Conditionnement de comprimés";
            icon = R.drawable.pill_automaton;
        } else {
            type = "Asservissement de niveau de liquide";
            icon = R.drawable.liquid_automaton;
        }

        iv_automatonItem_automatonIcon.setImageResource(icon);
        tv_automatonItem_name.setText(Html.fromHtml("Nom : <b>" + automaton.getName() + "</b>"));
        tv_automatonItem_ip.setText(Html.fromHtml("IP : <b>" + automaton.getIp() + "</b>"));
        tv_automatonItem_infos.setText(Html.fromHtml("Rack : <b>" + automaton.getRack() + "</b>    "
        + "Slot : <b>" + automaton.getSlot() + "</b>"));
        tv_automatonItem_type.setText(Html.fromHtml("Type : <b>" + type + "</b>"));
        /*btn_userItem_passwordIcon.setTag(position);
        btn_userItem_rightsIcon.setTag(position);
        btn_userItem_removeIcon.setTag(position);*/

        return convertView;
    }
}