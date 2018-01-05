package be.heh.automatons;

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

import be.heh.main.R;
import be.heh.models.Automaton;

/**
 * Created by alexandre on 11/12/17.
 */

public class AutomatonsAdapter extends ArrayAdapter<Automaton> {

    Context context;

    ImageView iv_automatonItem_automatonIcon;

    TextView tv_automatonItem_name;
    TextView tv_automatonItem_type;
    TextView tv_automatonItem_ip;
    TextView tv_automatonItem_infos;

    ImageButton btn_automatonItem_seeIcon;
    ImageButton btn_automatonItem_editIcon;
    ImageButton btn_automatonItem_removeIcon;

    public AutomatonsAdapter(Context context, ArrayList<Automaton> automaton) {
        super(context, 0, automaton);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Automaton automaton = getItem(position);
        final String type;
        Integer icon;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_automaton, parent, false);
        }


        iv_automatonItem_automatonIcon = convertView.findViewById(R.id.iv_automatonItem_automatonIcon);
        tv_automatonItem_name = convertView.findViewById(R.id.tv_automatonItem_name);
        tv_automatonItem_type = convertView.findViewById(R.id.tv_automatonItem_type);
        tv_automatonItem_ip = convertView.findViewById(R.id.tv_automatonItem_ip);
        tv_automatonItem_infos = convertView.findViewById(R.id.tv_automatonItem_infos);
        btn_automatonItem_seeIcon = convertView.findViewById(R.id.btn_automatonItem_seeIcon);
        btn_automatonItem_editIcon = convertView.findViewById(R.id.btn_automatonItem_editIcon);
        btn_automatonItem_removeIcon = convertView.findViewById(R.id.btn_automatonItem_removeIcon);

        if (automaton.getType().equals("0")) {
            type = context.getString(R.string.pills);
            icon = R.drawable.pill_automaton;
        } else {
            type = context.getString(R.string.liquid);
            icon = R.drawable.liquid_automaton;
        }

        iv_automatonItem_automatonIcon.setImageResource(icon);
        tv_automatonItem_name.setText(Html.fromHtml(context.getString(R.string.name) + " : <b>" + automaton.getName() + "</b>"));
        tv_automatonItem_type.setText(Html.fromHtml(context.getString(R.string.type) + " : <b>" + type + "</b>"));
        tv_automatonItem_ip.setText(Html.fromHtml(context.getString(R.string.ip) + " : <b>" + automaton.getIp() + "</b>"));
        tv_automatonItem_infos.setText(Html.fromHtml(context.getString(R.string.rack) + " : <b>" + automaton.getRack() + "</b> " +
                context.getString(R.string.slot) + " : <b>" + automaton.getSlot() + "</b> " +
                context.getString(R.string.databloc) + " : <b>" + automaton.getDataBloc() + "</b>"));

        btn_automatonItem_seeIcon.setTag(position);
        btn_automatonItem_editIcon.setTag(position);
        btn_automatonItem_removeIcon.setTag(position);

        return convertView;
    }
}