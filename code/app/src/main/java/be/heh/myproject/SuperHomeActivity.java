package be.heh.myproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

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

        // If User is not logged in , This will redirect user to LoginActivity
        // and finish current activity from activity stack.
        if (session.checkLogin())
            finish();

        // get user data from session
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
