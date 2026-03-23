package com.example.manga_manager_v2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText edtten, edttacgia, edttap, edtgia, edttaiban;
    Button btninsert, btnsearch, btnshow, btnupdate;
    ListView lv;
    ArrayList<EditText> checklist = new ArrayList<>();
    public ArrayList<String> mylist;
    public ArrayList<String> mylistsystem;
    public ArrayAdapter myadapter;
    String Madangchon = "";
    String Chonmahienthi = "";
    Database_Sqlite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, v.getPaddingBottom());
            return insets;
        });
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // Ánh xạ biến
        edtten = findViewById(R.id.edtName);
        edttacgia = findViewById(R.id.edtTG);
        edttap = findViewById(R.id.edtChap);
        edttaiban = findViewById(R.id.edtTB);
        edtgia = findViewById(R.id.edtGia);
        // Anh xa BTN
        btninsert = findViewById(R.id.btninsert);
        btnsearch = findViewById(R.id.btnsearch);
        btnupdate = findViewById(R.id.btnupdate);
        btnshow = findViewById(R.id.btnshow);

        lv = findViewById(R.id.lv);
        mylist = new ArrayList<>();
        mylistsystem = new ArrayList<>();
        myadapter = new ArrayAdapter<>(MainActivity.this, R.layout.textlist,mylist);
        lv.setAdapter(myadapter);

        // kiem tra loi bo trong
        checklist.add(edtten);
        checklist.add(edttacgia);
        checklist.add(edttap);
        checklist.add(edtgia);

        db = new Database_Sqlite(MainActivity.this);

        // UPDATE FIREBASE VAO SPQLITE
        DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
        mydata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                db.DeleteCache();
                if(snapshot.exists())
                {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        Truyen truyenLayVe = snapshot1.getValue(Truyen.class);
                        if (truyenLayVe != null) db.DongboTruyenFirebase(truyenLayVe);
                    }
                }
                if (lv.getVisibility() == View.VISIBLE) loadDataSQL();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // INSERT BUTTON
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText et : checklist) {
                    if (et.getText().toString().trim().isEmpty()) {
                        et.setError("Ô này không được bỏ trống.");
                        et.requestFocus();
                        return;
                    }
                }
                try {
                    //cai dat bien
                    String tenTruyen = edtten.getText().toString().trim();
                    String tenTacgia = edttacgia.getText().toString().trim();
                    int tapSo = Integer.parseInt(edttap.getText().toString().trim());
                    int giaTien = Integer.parseInt(edtgia.getText().toString().trim());
                    String maTruyenHienThi = CreateID(tenTruyen, tapSo, giaTien);
                    String maHeThong = MaHeThong();
                    //Firebase
                    DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
                    Truyen truyen = new Truyen(maHeThong,maTruyenHienThi, tenTruyen, tenTacgia, tapSo, giaTien);

                    mydata.child(maHeThong).setValue(truyen)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                                    for (EditText et : checklist) {
                                        et.setText("");
                                    }
                                    edtten.requestFocus();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Lỗi mạng: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Lỗi nhập dữ liệu!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // SHOW BUTTON
        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv.getVisibility() == View.GONE) {
                    lv.setVisibility(View.VISIBLE);
                    btnshow.setText("Ẩn danh sách");
                    loadDataSQL();
                }
                else {
                    lv.setVisibility(View.GONE);
                    btnshow.setText("Danh sách");
                }
            }
        });
        // DELETE BUTTON
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String in4 = mylist.get(position);
                String parts[] = in4.split("\n");
                String tenTruyen = parts[1].replace("Tên truyện: ", "");
                String Deletein4 = mylistsystem.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Xác nhận xóa truyện.");
                builder.setMessage("Bạn có chắc chắn muốn xóa truyện " + tenTruyen + " không?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
                        mydata.child(Deletein4).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Đã xóa truyện: " + tenTruyen, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        // UP DATA LEN TEXT
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String in4 = mylist.get(position)
                        .replace("Mã truyện: #", "")
                        .replace("Tên truyện: ", "")
                        .replace("Tên tác giả: ", "")
                        .replace("Tập số: ", "")
                        .replace("Giá tiền: " , "")
                        .replace(" VNĐ", "");
                String parts[] = in4.split("\n");
                Madangchon = mylistsystem.get(position);
                Chonmahienthi = parts[0];
                edtten.setText(parts[1]);
                edttacgia.setText(parts[2]);
                edttap.setText(parts[3]);
                edtgia.setText(parts[4]);
            }
        });
        // UPDATE BUTTON
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Madangchon.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng chọn truyện để sửa.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (EditText et : checklist) {
                    if(et.getText().toString().trim().isEmpty()) {
                        et.setError("Ô này không được bỏ trống.");
                        et.requestFocus();
                        return;
                    }
                }
                try {
                    String newten = edtten.getText().toString().trim();
                    String newtacgia = edttacgia.getText().toString().trim();
                    int newtap = Integer.parseInt(edttap.getText().toString().trim());
                    int newgia = Integer.parseInt(edtgia.getText().toString().trim());
                    String newMaHienThi = CreateID(newten, newtap,newgia);
                    Truyen newtruyen = new Truyen(Madangchon, newMaHienThi, newten, newtacgia, newtap, newgia);

                    DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
                    mydata.child(Madangchon).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                mydata.child(Madangchon).setValue(newtruyen).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        for (EditText e : checklist) e.setText("");
                                        edtten.requestFocus();
                                        Madangchon = "";
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Cập nhật thất bại. \nTruyện đã bị xóa hoặc chưa tồn tại.", Toast.LENGTH_SHORT).show();
                                for (EditText e : checklist) e.setText("");
                                edtten.requestFocus();
                                Madangchon = "";
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Lỗi mạng khi kiểm tra: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // TAO ID HIEN THI
    private String CreateID(String ten, int tap, int gia){
        String kytudau = "";
        if (!ten.isEmpty())
        {
            kytudau = ten.substring(0,1).toUpperCase();
        }
        String kytugiua1 = String.format("%02d", tap);
        String kytucuoi = String.format("%03d",(gia/1000))+"K";
        return kytudau + kytugiua1 + kytucuoi;
    }
    // TAO ID HE THONG
    private String MaHeThong() {
        long ID = System.currentTimeMillis();
        return "" + ID;
    }
    private void loadDataSQL() {
        mylist.clear();
        mylistsystem.clear();
        ArrayList<Truyen> danhsach = db.laytruyen();

        if (danhsach.isEmpty()) {
            Toast.makeText(MainActivity.this, "Danh sách hiện đang trống.", Toast.LENGTH_SHORT).show();
        } else {
            for(Truyen t : danhsach)
            {
                mylistsystem.add(t.maHeThong);
                String thongTin = "Mã truyện: #" + t.maTruyenHienThi
                        + "\nTên truyện: " + t.tenTruyen
                        + "\nTên tác giả: " + t.tacGia
                        + "\nTập số: " + t.tapSo
                        + "\nGiá tiền: " + t.giaTien + " VNĐ";
                mylist.add(thongTin);
            }
        }
        myadapter.notifyDataSetChanged();
    }
}