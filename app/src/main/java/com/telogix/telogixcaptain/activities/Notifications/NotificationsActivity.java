package com.telogix.telogixcaptain.activities.Notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.ApiSignature.NetworkConsume;
import com.telogix.telogixcaptain.ApiSignature.httpvolley;
import com.telogix.telogixcaptain.Interfaces.response_interface;
import com.telogix.telogixcaptain.adapters.NotificationsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;

public class NotificationsActivity extends AppCompatActivity implements response_interface {

    private NotificationsAdapter notificationsAdapter;
    private RecyclerView listnotification;
    private ArrayList<NotificationPojo> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar=findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listnotification=findViewById(R.id.listnotification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listnotification.setLayoutManager(mLayoutManager);
        listnotification.setItemAnimator(new DefaultItemAnimator());
        notificationList=new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(notificationList);
        listnotification.setAdapter(notificationsAdapter);
            getNotification();
//        FirebaseListenerSetup();
    }

    private void getNotification() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notitoken", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        NetworkConsume.getInstance().ShowProgress(NotificationsActivity.this);
                        HashMap hashMap = new HashMap();
                        httpvolley.stringrequestpost("api/PushNotification/GetNotificationsByToken?Days=2&Token="+token, Request.Method.GET, hashMap, NotificationsActivity.this);

                        Log.d("NotiToken",token);
                    }
                });

    }

    private void FirebaseListenerSetup() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        NetworkConsume.getInstance().ShowProgress(this);
        DatabaseReference usersRef = ref.child("notifications");
        ArrayList<String> list = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList deviceID = new ArrayList();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uid = ds.getKey();
                    usersRef.child(uid).child("desc").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //  Toast.makeText(getContext(),""+databaseError.getDetails(),Toast.LENGTH_SHORT).show();

                        }
                    });
                    usersRef.child(uid).child("desc").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //              Toast.makeText(getContext(),""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                            list.add(dataSnapshot.getValue().toString());
                            notificationsAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //  Toast.makeText(getContext(),""+databaseError.getDetails(),Toast.LENGTH_SHORT).show();

                        }
                    });

                    deviceID.add(uid);
                    //  latlngchangelistener=new ValueEventListener() {

                }
//                notificationList=new ArrayList<>();
//                notificationsAdapter = new NotificationsAdapter(this,notificationList);
//                listnotification.setAdapter(notificationsAdapter);

                //  child.addListenerForSingleValueEvent(latlngchangelistener);

                NetworkConsume.getInstance().hideProgress();

                //Do what you need to do with your list

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                NetworkConsume.getInstance().hideProgress();

                Toast.makeText(NotificationsActivity.this, "" + databaseError.getDetails(), Toast.LENGTH_SHORT).show();

            }
        };

        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == 16908332) {
            onBackPressed();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    public void onResponse(String response) throws JSONException {
        NetworkConsume.getInstance().hideProgress();
         notificationList=new ArrayList<NotificationPojo>();

        try{
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("data"))
            {
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++)
                {

                    NotificationPojo notificationPojo=new NotificationPojo();
                    notificationPojo.setNotificationID(jsonArray.getJSONObject(i).getInt("NotificationID"));
                    notificationPojo.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                    notificationPojo.setDescription(jsonArray.getJSONObject(i).getString("Description"));
                    notificationPojo.setCreatedDate(jsonArray.getJSONObject(i).getString("CreatedDate"));
                    notificationPojo.setUserToken(jsonArray.getJSONObject(i).getString("UserToken"));
                    notificationList.add(notificationPojo);
                }
                notificationsAdapter = new NotificationsAdapter(notificationList);
                listnotification.setAdapter(notificationsAdapter);
            }
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onError(VolleyError Error) {
        NetworkConsume.getInstance().hideProgress();
    }
}
