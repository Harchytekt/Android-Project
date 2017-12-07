package be.heh.myproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashMap;

import be.heh.session.Session;

public class SuperHomeActivity extends Activity {

    private Session session;
    Button btn_superHome_logout;
    TextView tv_superHome_title;
    TextView tv_superHome_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_home);

        session = new Session(getApplicationContext());

        tv_superHome_title = findViewById(R.id.tv_superHome_title);
        tv_superHome_email = findViewById(R.id.tv_superHome_email);

        btn_superHome_logout = findViewById(R.id.btn_superHome_logout);

        //Toast.makeText(getApplicationContext(),"User Login Status: " + session.isUserLoggedIn(), Toast.LENGTH_LONG).show();

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(Session.KEY_EMAIl);
        String rights = user.get(Session.KEY_RIGHTS);

        tv_superHome_email.setText(Html.fromHtml("<b>" + email + "</b> est connecté !"));
    }

    public void onSuperHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.btn_superHome_logout:

                session.logoutUser();

                break;
        }
    }
}
