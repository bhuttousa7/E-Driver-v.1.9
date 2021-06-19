package com.telogix.telogixcaptain.ApiSignature;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;
import com.telogix.telogixcaptain.Interfaces.offline_response_interface;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.Utils.Constants;
import com.telogix.telogixcaptain.Utils.connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.paperdb.Paper;

/**
 * Created by TALHA on 19-Dec-16.
 */

public class httpvolley{
    static Context context;
    private static String notitoken ="";
    private static StringRequest sr;
    public httpvolley(Context context)
    {
        httpvolley.context =context;

    }
    public static void stringrequestpostDriver(String phpscriptname,int requestType, final HashMap<String,String> hashMap,String token, final response_interface res) {
        if (res instanceof Fragment || res instanceof android.app.Fragment) {
            context = ((Fragment) res).getActivity();
        } else if (res instanceof Activity) {
            context = (Context) res;
        }

//        RequestQueue requestQueue= Volley.newRequestQueue(context);
        singleton.getinstance(context);
        singleton.getrequestqueue(context);

        StringRequest stringRequest = new StringRequest(requestType, connection.completeurl(phpscriptname), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    res.onResponse(response);
                } catch (JSONException e) {
                    NetworkConsume.getInstance().hideProgress();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkConsume.getInstance().hideProgress();
                try {
                    if(error.networkResponse.statusCode==401)
                    {
//        NetworkConsume.getInstance().Logout(context);
//        singleton.getsharedpreference_editor(context).clear().commit();
//        Paper.book().delete(Constants.KEY_ROLE_ID);
                    }
                    else
                    {

                    }
                }
                catch (Exception ex){
                    NetworkConsume.getInstance().hideProgress();
                    res.onError(error);
                }
                finally {
                    NetworkConsume.getInstance().hideProgress();
                    res.onError(error);
                }


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

//                String bearer = "Bearer "+singleton.getsharedpreference(context).getString("token","");
                String bearer = "Bearer "+token;
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
//                headers.put("User-agent", "My useragent");

            }

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> MyData = hashMap;
                //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        singleton.getrequestqueue(context).add(stringRequest);

    }

    public static void stringrequestpost(String phpscriptname,int requestType, final HashMap<String,String> hashMap, final response_interface res) {
        notitoken = singleton.getsharedpreference(context).getString("notitoken","");

        if (res instanceof Fragment || res instanceof android.app.Fragment) {
            context = ((Fragment) res).getActivity();
        } else if (res instanceof Activity) {
            context = (Context) res;
        }
        if(notitoken!="" && !phpscriptname.contains("api/UserToken/SaveToken?Token=")
        && !phpscriptname.contains("api/UserToken/ExpireToken") && !phpscriptname.contains("token")){checkAuth();}
//        RequestQueue requestQueue= Volley.newRequestQueue(context);
//        singleton.getinstance(context);
//        singleton.getrequestqueue(context);

        StringRequest stringRequest = new StringRequest(requestType, connection.completeurl(phpscriptname), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    res.onResponse(response);

                } catch (JSONException e) {

                    NetworkConsume.getInstance().hideProgress();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkConsume.getInstance().hideProgress();
                try {
                    if(error.networkResponse.statusCode==400)
                    {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject data = new JSONObject(responseBody);
                        //String response = data.optString("ModelState");
                        if(responseBody.contains("ModelState")){
                            String response = data.optString("ModelState");
                            Toast.makeText(context,response,Toast.LENGTH_SHORT).show();

                        }

                       // Toast.makeText(context,data.toString(),Toast.LENGTH_SHORT).show();
                    }

                    if(error.networkResponse.statusCode==401)
                    {
//        NetworkConsume.getInstance().Logout(context);
//        singleton.getsharedpreference_editor(context).clear().commit();
//        Paper.book().delete(Constants.KEY_ROLE_ID);

                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject data = new JSONObject(responseBody);
                        // String response = data.optString("ModelState");
                        Log.d("--error401",data.toString());
                        Toast.makeText(context,data.toString(),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject data = new JSONObject(responseBody);
                        if(responseBody.contains("error_description")){
                            String response = data.optString("error_description");
                            Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                            Log.d("-error_else",response);

                        }
                    }
                }
                catch (Exception ex){
                    NetworkConsume.getInstance().hideProgress();
                    res.onError(error);
                }
                finally {
                    NetworkConsume.getInstance().hideProgress();
                    res.onError(error);
                }


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String bearer = "Bearer "+ singleton.getsharedpreference(context).getString("token","");

                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
//                headers.put("User-agent", "My useragent");

            }

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> MyData = hashMap;
                //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        singleton.getrequestqueue(context).add(stringRequest);
//        if(sr != null){
//            singleton.getrequestqueue(context).add(sr);}

    }
    public static void stringrequestpost_offline(String phpscriptname,int requestType, final HashMap<String,String> hashMap, final offline_response_interface res) {
        notitoken = singleton.getsharedpreference(context).getString("notitoken","");

        if (res instanceof Fragment || res instanceof android.app.Fragment) {
            context = ((Fragment) res).getActivity();
        } else if (res instanceof Activity) {
            context = (Context) res;
        }
        if(notitoken!="" && !phpscriptname.contains("api/UserToken/SaveToken?Token=")
                && !phpscriptname.contains("api/UserToken/ExpireToken") && !phpscriptname.contains("token")){checkAuth();}
//        RequestQueue requestQueue= Volley.newRequestQueue(context);
        singleton.getinstance(context);
        singleton.getrequestqueue(context);

        StringRequest stringRequest = new StringRequest(requestType, connection.completeurl(phpscriptname), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    if(response.equals("false")){
//                        OneSignal.deleteTag("userID");
//                        NetworkConsume.getInstance().Logout(context);
//                        singleton.getsharedpreference_editor(context).clear().commit();
//                        Paper.book().delete(Constants.KEY_ROLE_ID);
//                        Toast.makeText(context.getApplicationContext(), "Another device is using this account", Toast.LENGTH_SHORT).show();
//                    }
                    res.onOfflineResponse(response);
                } catch (JSONException e) {
                    NetworkConsume.getInstance().hideProgress();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkConsume.getInstance().hideProgress();
                try {
                    if(error.networkResponse.statusCode==401)
                    {
//        NetworkConsume.getInstance().Logout(context);
//        singleton.getsharedpreference_editor(context).clear().commit();
//        Paper.book().delete(Constants.KEY_ROLE_ID);
                    }
                    else
                    {

                    }
                }
                catch (Exception ex){
                    NetworkConsume.getInstance().hideProgress();
                    res.onOfflineError(error);
                }
                finally {
                    NetworkConsume.getInstance().hideProgress();
                    res.onOfflineError(error);
                }


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String bearer = "Bearer "+ singleton.getsharedpreference(context).getString("token","");

                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
//                headers.put("User-agent", "My useragent");

            }

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> MyData = hashMap;
                //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        singleton.getrequestqueue(context).add(stringRequest);

    }


    public void stringrequestget(String phpscriptname, final HashMap<String,String> hashMap)
    {
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, connection.completeurl(phpscriptname), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context,""+response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,""+error,Toast.LENGTH_LONG).show();
            }
        }  ){
            protected HashMap<String, String> getParams() {
                HashMap<String, String> MyData = hashMap;
                //Add the data you'd like to send to the server.
                return MyData;
            }
        };    }
    public void jsonrequestpost(String phpscriptname, final HashMap<String,String> hashMap)
    {
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, phpscriptname, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
    public void jsonrequestget(String phpscriptname, final HashMap<String,String> hashMap)
    {
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, phpscriptname, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public static synchronized void checkAuth(){
        notitoken = singleton.getsharedpreference(context).getString("notitoken","");
         sr = new StringRequest(Request.Method.POST, connection.completeurl("api/Routes/CheckAuth?token=" + notitoken), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("false") || response.contains("False")){
                    OneSignal.deleteTag("userID");

                    singleton.getsharedpreference_editor(context).clear().commit();
                    Paper.book().delete(Constants.KEY_ROLE_ID);
                    singleton.getsharedpreference_editor(context).putBoolean("LoggedInFromAnotherAccount",true).commit();
                    Toast.makeText(context.getApplicationContext(), "Another device is using this account", Toast.LENGTH_SHORT).show();
                    NetworkConsume.getInstance().Logout(context);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {

                 String bearer = "Bearer "+ singleton.getsharedpreference(context).getString("token","");

                 Map<String, String> headersSys = super.getHeaders();
                 Map<String, String> headers = new HashMap<String, String>();
                 headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
                 headers.put("Authorization", bearer);
                 headers.putAll(headersSys);
                 return headers;
//                headers.put("User-agent", "My useragent");

             }

             @Override
             protected HashMap<String, String> getParams() {
                 HashMap<String, String> MyData = new HashMap<>();
                 //Add the data you'd like to send to the server.
                 return MyData;
             }
         };

        singleton.getrequestqueue(context).add(sr);

    }
    public static void stringsynchronousrequest(String phpscriptname,int requestType, final HashMap<String,String> hashMap, final response_interface res) {
        notitoken = singleton.getsharedpreference(context).getString("notitoken","");

        if (res instanceof Fragment || res instanceof android.app.Fragment) {
            context = ((Fragment) res).getActivity();
        } else if (res instanceof Activity) {
            context = (Context) res;
        }
        if(notitoken!="" && !phpscriptname.contains("api/UserToken/SaveToken?Token=")
                && !phpscriptname.contains("api/UserToken/ExpireToken") && !phpscriptname.contains("token")){checkAuth();}
//        RequestQueue requestQueue= Volley.newRequestQueue(context);
//        singleton.getinstance(context);
//        singleton.getrequestqueue(context);
        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest = new StringRequest(requestType, connection.completeurl(phpscriptname), future, future)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String bearer = "Bearer "+ singleton.getsharedpreference(context).getString("token","");

                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
//                headers.put("User-agent", "My useragent");

            }

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> MyData = hashMap;
                //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        singleton.getrequestqueue(context).add(stringRequest);
try{

    String response = future.get(1, TimeUnit.MILLISECONDS);

}catch (ExecutionException | InterruptedException | TimeoutException e){

}
    }


}
