package com.example.managa_manager_v3;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    TextView in4name;
    EditText edtsearch;
    RecyclerView recyclerView;
    ArrayList<Truyen> listtruyen;
    ArrayList<Truyen> searchlist;
    //ArrayList<Truyen> mylist = new ArrayList<>();;
    truyenAdapter truyenAdapter;

    Button btnintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, v.getPaddingBottom());
        //    return insets;
        //});

        edtsearch = findViewById(R.id.edtsearch);
        btnintent = findViewById(R.id.btnintent);
        recyclerView = findViewById(R.id.rcvTruyen);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);

        listtruyen = new ArrayList<>();
        searchlist = new ArrayList<>();
        truyenAdapter = new truyenAdapter(this, listtruyen);
        recyclerView.setAdapter(truyenAdapter);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Manga");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listtruyen.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        listtruyen.add(truyen);
                    }
                }

                Collections.sort(listtruyen, new Comparator<Truyen>() {
                    @Override
                    public int compare(Truyen o1, Truyen o2) {
                        int sosanhten = o1.tenTruyen.compareToIgnoreCase(o2.tenTruyen);
                        if (sosanhten != 0 ){
                            return sosanhten;
                        }
                        else {
                            return Integer.compare(o1.tapSo, o2.tapSo);
                        }
                    }
                });
                filter(edtsearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent1);
            }
        });

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
        });

    }
    private void filter (String text){
        searchlist.clear();
        for(Truyen item : listtruyen){
            if(item.tenTruyen.toLowerCase().contains(text.toLowerCase())) searchlist.add(item);
        }
        truyenAdapter.updateList(searchlist);
    }
}