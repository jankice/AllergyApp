package com.task.allergyapp.ui.AllergyCheck.allergen;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.allergyapp.R;
import com.task.allergyapp.data.LoginContext;

import java.util.ArrayList;


public class AllergenFragment extends Fragment {

    private AllergenViewModel allergenViewModel;
    private static RecyclerView.Adapter allergenAdapter;
    private ArrayList<String> myAllergen = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private EditText edittextAddallergen;
    private int REQUEST_CODE_PERMISSIONS = 102;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allergenViewModel =
                ViewModelProviders.of(this).get(AllergenViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allergen, container, false);

        final TextView textView = root.findViewById(R.id.text_allergen);
        recyclerView = root.findViewById(R.id.recyclerViewAllergen);
        LinearLayout linearLayout = root.findViewById(R.id.linerLayoutItem);
        edittextAddallergen = root.findViewById(R.id.edittextAddallergen);

        runtimePermission();

        initializingFirebase();

        myAllergen = loadArray();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myAllergen!=null){

                    String value = edittextAddallergen.getText().toString();

                    if(!(value.isEmpty())){
                        myAllergen.add(value);
                    }

                }

                //TODO- I am saving values to preference ,
                // as saving to firebase is not working. (Gives Permission denied for some reason)
                saveValuesToPref();

                edittextAddallergen.setText("");
                setAdapter();
            }
        });

        setAdapter();
        return root;
    }

    private void runtimePermission() {
        allergenViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(allPermissionsGranted()){

                } else{
                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
            }
        });
    }

    private void initializingFirebase() {
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("allergen");

        firebaseAuthWithGoogle();
    }

    private boolean saveValuesToPref() {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor mEdit1 = sp.edit();
            /* sKey is an array */
            mEdit1.putInt("Status_size", myAllergen.size());

            for(int i=0;i<myAllergen.size();i++)
            {
                mEdit1.remove("Status_" + i);
                mEdit1.putString("Status_" + i, myAllergen.get(i));
            }

            return mEdit1.commit();

    }

    private ArrayList<String> loadArray() {

        ArrayList<String> allergenList = new ArrayList<>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(getContext());
        int size = mSharedPreference1.getInt("Status_size", 0);
        for (int i = 0; i < size; i++) {
            allergenList.add(mSharedPreference1.getString("Status_" + i, null));
        }

        return allergenList;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //firebaseAuthWithGoogle();
        getExistingData();

    }

    FirebaseUser user;
    private void firebaseAuthWithGoogle() {

        AuthCredential credential = GoogleAuthProvider.getCredential(LoginContext.getAccount().getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("allergen");
                            myRef.child("values").setValue(myAllergen);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());

                        }

                    }
                });
    }

    private void getExistingData() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("DataBaseValue", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Data", "Failed to read value.", error.toException());
            }
        });
    }

    private void setAdapter() {
        allergenAdapter = new AllergenAdapter(getContext(),myAllergen);
        recyclerView.setAdapter(allergenAdapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));
    }


    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){

            } else{
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

}