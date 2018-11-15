package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Activity from where the user picks a course to battle with,
 * add a question to, or view their stats in
 */
public class CourseSelectActivity extends AppCompatActivity implements SelectedCourseDialog.SelectedCourseDialogListener, CourseAddDialog.BottomSheetListener {

    public static final String TAG = "CourseSelectActivity"; //tag for sending info through intents
    private ListView CourseListView;
    private FloatingActionButton fab;
    private TextView name;
    private ImageView profile_pic;
private String user_json;
    private String user_name;
    private String profile_pic_url;
    private String email;
    private String alias;
    private boolean exit;
    private JSONObject u_json;
    private Intent intent;
    private Bundle extras;
    private boolean isProf;
    private String username;

    // each course header(eg. CPEN) is a key to a list of course codes (eg. 311, 321, 331)
    private Map<String, List<String>> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        CourseListView = findViewById(R.id.list_view);
        fab = findViewById(R.id.fab);
        name = findViewById(R.id.name);
        profile_pic = findViewById(R.id.profile_pic);

        intent = getIntent();
        extras = intent.getExtras();

        // coming from the LoginActivity, the intent may come with information relating to the user
        if(extras.containsKey("name")) {
            user_name = intent.getStringExtra("name");
            name.setText(user_name + "      |      student");
        }
        else
            name.setText("error");

        if(extras.containsKey("profile_pic_url")) {
            profile_pic_url = intent.getStringExtra("profile_pic_url");
            Picasso.with(this).load(profile_pic_url)
                    .transform(new ProfilePicTransformation(200, 0,Color.WHITE))
                    .into(profile_pic);
        }

        if(extras.containsKey("user_json")) {
            user_json = intent.getStringExtra("user_json");
            //Toast.makeText(CourseSelectActivity.this, user_json, Toast.LENGTH_SHORT).show();

            Log.d("BELHTDFG","user_json orig: " +user_json);

            try {
                u_json = new JSONObject(user_json.substring(1, user_json.length()-1));
                GlobalTokens.USER_ID = u_json.getString("_id");
                Log.d("BELHTDFG","u_json: " +u_json.getString("_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(extras.containsKey("email")) {
                email = intent.getStringExtra("email");
            }

            try {
                username = u_json.getString("username");
            } catch (JSONException e) {
                username = promptUsername();

//                try {
//                    user_json = new OkHttpTask().execute(OkHttpTask.GET_USER_DETAILS, email).get();
//                    u_json = new JSONObject(user_json.substring(1, user_json.length()-1));
//                    username = u_json.getString("username");
//                } catch (InterruptedException ed) {
//                    user_json = null;
//                    Log.d("BELHTDFG","InterruptedException");
//                } catch (ExecutionException ed) {
//                    user_json = null;
//                    Log.d("BELHTDFG","ExecutionException");
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }

            }


            Log.d("BELHTDFG","user_json: " +GlobalTokens.USER_ID);


            String course_json;
            try {
                course_json = new OkHttpTask().execute(OkHttpTask.GET_USER_COURSES, "").get();
            } catch (InterruptedException e) {
                course_json = null;
                Log.d("BELHTDFG","InterruptedException");
            } catch (ExecutionException e) {
                course_json = null;
                Log.d("BELHTDFG","ExecutionException");
            }
            if(course_json != null) {
                Toast.makeText(CourseSelectActivity.this, course_json, Toast.LENGTH_SHORT).show();
            }

        }
        fillCourses();



        CourseAdapter courseAdapter = new CourseAdapter(this, Courses, getSupportFragmentManager());
        CourseListView.setAdapter(courseAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseAddDialog bottomSheet = new CourseAddDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                //Toast.makeText(CourseSelectActivity.this, "add course", Toast.LENGTH_SHORT).show();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    alias = u_json.getString("alias");
                    isProf = u_json.getBoolean("is_professor");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent userprofile = new Intent(CourseSelectActivity.this, UserProfileActivity.class);
                userprofile.putExtra("alias", alias);
                userprofile.putExtra("username", username);
                userprofile.putExtra("isProf", isProf);
                userprofile.putExtra("email", email);
                userprofile.putExtra("profile_pic_url", profile_pic_url);
                startActivity(userprofile);
            }
        });

    }

    /**
     * Initializes and loads the map `Courses` with the courses tied to
     * the user's account.
     */
    private void fillCourses() {
        // TODO: remove mock implemetation and use REST API
        Courses = new HashMap<String, List<String>>();

        List<String> elec_courses = new ArrayList<>();
        elec_courses.add("201");
        elec_courses.add("221");
        elec_courses.add("301");
        Courses.put("ELEC", elec_courses);

        List<String> cpen_courses = new ArrayList<>();
        cpen_courses.add("311");
        cpen_courses.add("321");
        cpen_courses.add("331");
        cpen_courses.add("341");
        cpen_courses.add("351");
        Courses.put("CPEN", cpen_courses);

        List<String> math_courses = new ArrayList<>();
        math_courses.add("311");
        math_courses.add("321");
        math_courses.add("331");
        math_courses.add("341");
        math_courses.add("351");
        Courses.put("MATH", math_courses);

        List<String> cpsc_courses = new ArrayList<>();
        cpsc_courses.add("311");
        cpsc_courses.add("321");
        cpsc_courses.add("331");
        cpsc_courses.add("341");
        cpsc_courses.add("351");
        Courses.put("CPSC", cpsc_courses);

        List<String> cons_courses = new ArrayList<>();
        cons_courses.add("311");
        cons_courses.add("321");
        cons_courses.add("331");
        cons_courses.add("341");
        cons_courses.add("351");
        Courses.put("CONS", cons_courses);

    }

    /**
     * Abstract method implementation that allows SelectCourseActivity to
     * determine the action decided on in the SelectedCourseDialog
     * @param action - action to be taken with the course
     * @param course - course whose action is to be enacted
     */
    @Override
    public void chooseCourseView(int action, String course) {
        switch (action) {
            case CourseActionDefs.BATTLE:
                //Toast.makeText(this, "BATTLE " + course, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MatchmakingActivity.class);
                intent.putExtra(TAG, course);
                intent.putExtra("name", user_name);
                startActivity(intent);
                break;
            case CourseActionDefs.ADD_QUESTION:
                Toast.makeText(this, "ADD QUESTION " + course, Toast.LENGTH_SHORT).show();
                break;
            case CourseActionDefs.GET_STATS:
                Toast.makeText(this, "GET STATS " + course, Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }

    }
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    @Override
    public void onButtonClicked(String text) {

    }

    private String promptUsername()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CourseSelectActivity.this);

        LayoutInflater inflater=CourseSelectActivity.this.getLayoutInflater();
        //this is what I did to added the layout to the alert dialog
        View layout=inflater.inflate(R.layout.dialog_assign_username,null);
        alert.setView(layout);

        final EditText usernameInput=(EditText)layout.findViewById(R.id.username_text);
        final TextView error_text = (TextView) layout.findViewById(R.id.error_text);
        alert.setCancelable(false).setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String un_resp;
                        try {
                            un_resp = new OkHttpTask().execute(OkHttpTask.SET_USERNAME, usernameInput.getText().toString()).get();
                        } catch (InterruptedException e) {
                            un_resp = null;
                            Log.d("BELHTDFG","InterruptedException");
                        } catch (ExecutionException e) {
                            un_resp = null;
                            Log.d("BELHTDFG","ExecutionException");
                        }
                        if(un_resp != null) {
                            if(un_resp.equals("409"))
                            {
                                //Toast.makeText(CourseSelectActivity.this, "username already taken. Please choose another", Toast.LENGTH_SHORT).show();
                                error_text.setVisibility(View.VISIBLE);
                                ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
                                ViewCompat.setBackgroundTintList(usernameInput, colorStateList);
                            } else {
                                Toast.makeText(CourseSelectActivity.this, "username added!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }
                    }
                });
            }
        });
        dialog.show();
        return usernameInput.getText().toString();
    }

}


