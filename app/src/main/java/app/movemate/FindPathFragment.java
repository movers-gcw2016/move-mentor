package app.movemate;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.movemate.ListAdapter.Path;
import app.movemate.ListAdapter.PathsAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class FindPathFragment extends Fragment {
    View view;
    ListView rv;
    PathsAdapter pathsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view =  inflater.inflate(R.layout.fragment_find, container, false);

        rv = (ListView) view.findViewById(R.id.groupList);
        rv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Path p = (Path)rv.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putString("path",p.path.toString());
                Fragment f = new PathFragment();
                f.setArguments(b);
                ((MainActivity)getActivity()).nextFrag(f);

            }
        });
        pathsAdapter = new PathsAdapter(getActivity(),R.layout.path_list_layout);
        rv.setAdapter(pathsAdapter);

        populateList();

        return view;
    }


    public void populateList(){
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResources().getString(R.string.loading_path));
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = getArguments().getString("url");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int count=jsonArray.length()-1;
                            while(count>=0){
                                JSONObject JO = jsonArray.getJSONObject(count);
                                Path path = new Path(JO);
                                count--;
                                pathsAdapter.add(path);

                            }
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toasty.error(getActivity(), getString(R.string.error_getpath), Toast.LENGTH_SHORT, true).show();
            }
        });

        queue.add(stringRequest);
    }

}
