package be.heh.myproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class HomeActivity extends Activity {

    private Session session;
    Button btn_home_logout;
    TextView tv_home_title;
    TextView tv_home_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new Session(getApplicationContext());

        tv_home_title = findViewById(R.id.tv_home_title);
        tv_home_email = findViewById(R.id.tv_home_email);

        btn_home_logout = findViewById(R.id.btn_home_logout);

        //Toast.makeText(getApplicationContext(),"User Login Status: " + session.isUserLoggedIn(), Toast.LENGTH_LONG).show();

        // If User is not logged in , This will redirect user to LoginActivity
        // and finish current activity from activity stack.
        if (session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(Session.KEY_EMAIl);

        String rights = user.get(Session.KEY_RIGHTS);

        tv_home_email.setText(Html.fromHtml("<b>" + email + "</b> est connecté !"));
    }

    public void onHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.btn_home_logout:

                session.logoutUser();

                break;
        }
    }
}

