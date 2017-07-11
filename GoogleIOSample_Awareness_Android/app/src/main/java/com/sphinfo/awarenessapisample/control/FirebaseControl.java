package com.sphinfo.awarenessapisample.control;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by yongkyuncho on 6/15/17.
 */

public class FirebaseControl {

    private FirebaseDatabase databaseInstanse = null;

    public FirebaseControl(){

    }

    public FirebaseDatabase getDatabase(){

        if(this.databaseInstanse == null){
            this.databaseInstanse = FirebaseDatabase.getInstance();
        }

        return this.databaseInstanse;
    }

    public DatabaseReference getReference(String referenceName){

        DatabaseReference databaseReference = null;
        if(referenceName == null){
            databaseReference = this.getDatabase().getReference();
        }
        else{
            databaseReference = this.getDatabase().getReference(referenceName);
        }

        return databaseReference;
    }




    public void writeData(Object value){
        DatabaseReference databaseReference = this.getReference(null);
        databaseReference.setValue(value);
    }


    public void writeData(String referenceName, Object value){


        DatabaseReference databaseReference = this.getReference(referenceName);

        Log.d("FirebaseControl", " referenceName : " + referenceName);
        Log.d("FirebaseControl", " value         : " + value);
        Log.d("FirebaseControl", " DatabaseReference : " + databaseReference.toString());

        databaseReference.setValue(value);
    }
}
