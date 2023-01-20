package com.example.firebasecrudexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreEx extends AppCompatActivity {

    FirebaseFirestore db;
    LinearLayout LL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_ex);

        //Initialize database variable
        db = FirebaseFirestore.getInstance();

        //Find where to to list data
        LL = findViewById(R.id.ScrollText);

        //Read Data for first time
        readData();
    }

    /*
    CRUD OPERATION
    C - Create
    R - Read
    U - Update
    D - Delete
    */

    //Click on floating button to show panel to add Data
    public void showCreatePanel(View v)
    {
        //Add EditText
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        //show Dialog to add Text
        new AlertDialog.Builder(this)
                .setTitle("Create new Data")
                .setMessage("Type anything to save data into firebase")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        createData(input.getText().toString());
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //Show edit panel to edit Data
    public void showEditPanel(String id)
    {
        //Add EditText
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        //show Dialog to edit Text
        new AlertDialog.Builder(this)
                .setTitle("Edit existing Data")
                .setMessage("Type anything to edit data into firebase")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateData(id, input.getText().toString());
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //Show delete panel to delete Data
    public void showDeletePanel(String id)
    {
        //show Dialog to delete data
        new AlertDialog.Builder(this)
                .setTitle("Delete  Data")
                .setMessage("Type anything to edit data into firebase")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData(id);
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //create
    private void createData(String data)
    {
        //Save data in HashMap format
        Map<String, Object> user = new HashMap<>();
        user.put("name", data);

        //Send Data to firebase
        db.collection("list")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(FirestoreEx.this, "Successfully save", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirestoreEx.this, "Failed to send data", Toast.LENGTH_SHORT).show();
                    }
                });

        readData();
    }

    //read
    private void readData()
    {
        //Remove all child in List if not empty
        if(LL.getChildCount() > 0)
        {
            LL.removeAllViews();
        }

        //Get all data from Database
        db.collection("list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //create a layout for each row
                                LinearLayout newLL = new LinearLayout(FirestoreEx.this);
                                LinearLayout.LayoutParams newLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
                                newLL.setLayoutParams(newLP);
                                newLL.setOrientation(LinearLayout.HORIZONTAL);

                                //create textview to display data
                                TextView text = new TextView(FirestoreEx.this);
                                text.setText(document.getData().toString());

                                //create button to edit data
                                Button btn1 = new Button(FirestoreEx.this);
                                btn1.setText("Edit");
                                btn1.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v)
                                    {
                                        showEditPanel(document.getId());
                                    }
                                });

                                //create button to delete data
                                Button btn2 = new Button(FirestoreEx.this);
                                btn2.setText("Delete");
                                btn2.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v)
                                    {
                                        showDeletePanel(document.getId());
                                    }
                                });

                                //display textview,edit btn and delete button in 1 row
                                newLL.addView(text);
                                newLL.addView(btn1);
                                newLL.addView(btn2);

                                //add row to scrollbar
                                LL.addView(newLL);
                            }
                        } else {
                            Toast.makeText(FirestoreEx.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //update
    private void updateData(String id, String data)
    {
        //find collection(table) name list
        db.collection("list").document(id)
                .update("name",data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FirestoreEx.this, "Successfully update data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirestoreEx.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                    }
                });
        readData();
    }

    //delete
    private void deleteData(String id)
    {
        db.collection("list").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FirestoreEx.this, "Successfully deleted data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirestoreEx.this, "Failed to deleted data", Toast.LENGTH_SHORT).show();
                    }
                });
        readData();
    }
}