package app.movemate;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);


        try {
            final JSONObject json = new JSONObject(getIntent().getStringExtra("info"));
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://movemate-api.azurewebsites.net/api/students/getphoto?id="+json.getString("StudentId");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String imageBytes = response;
                            byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
                            Glide.with(ProfileActivity.this).load(imageByteArray)
                                    .fitCenter()
                                    .bitmapTransform(new CropCircleTransformation(ProfileActivity.this))
                                    .into((ImageView) findViewById(R.id.pic));



                        }
                    }
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                    return map;
                }
            };

            queue.add(stringRequest);

            TextView name = (TextView)findViewById(R.id.name);
            name.setText(json.getString("Name"));
            TextView rate = (TextView)findViewById(R.id.feedback);
            Double r = json.getDouble("TotalFeedback");
            String rs ;
            if(r>5){
                rs = "N.A.";
            }else{
                rs = new DecimalFormat("##.#").format(r);
            }
            rate.setText(rs+"/5");

            Button call = (Button)findViewById(R.id.call_btn);
            Button sms = (Button)findViewById(R.id.sms_btn);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        String n = json.getString("PhoneNumber");
                        intent.setData(Uri.parse("tel:"+n));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String n = json.getString("PhoneNumber");
                        Intent intentsms = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + n ) );
                        intentsms.putExtra( "sms_body", "" );
                        startActivity( intentsms );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
