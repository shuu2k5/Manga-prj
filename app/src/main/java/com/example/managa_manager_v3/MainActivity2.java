package com.example.managa_manager_v3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    EditText edtten, edttacgia, edttap, edtsoluong, edtgiatien, edtdate;
    Button btninsert ,btnback;
    ArrayList<EditText> checklist =new ArrayList<>();
    String MaDangChon = "";
    Boolean isUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        edtten = findViewById(R.id.edtten);
        edttacgia = findViewById(R.id.edttacgia);
        edttap = findViewById(R.id.edttap);
        edtsoluong = findViewById(R.id.edtsoluong);
        edtgiatien = findViewById(R.id.edtgia);
        edtdate = findViewById(R.id.edtdate);

        btnback = findViewById(R.id.btnback);
        btninsert = findViewById(R.id.btninsert);

        btnback.setOnClickListener(v -> finish());
        edtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Calendar calendar = Calendar.getInstance();
                int nam = calendar.get(Calendar.YEAR);
                int thang = calendar.get(Calendar.MONTH);
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);

                //
                DatePickerDialog dialog = new DatePickerDialog(MainActivity2.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectDay = dayOfMonth + "/" + (month+1) + "/" + year;
                        edtdate.setText(selectDay);
                    }
                }, nam, thang, ngay);
                dialog.show();
            }
        });

        checklist.add(edtten);
        checklist.add(edttacgia);
        checklist.add(edttap);
        checklist.add(edtsoluong);
        checklist.add(edtgiatien);
        checklist.add(edtdate);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("IS_UPDATE")){
            isUpdate = intent.getBooleanExtra("IS_UPDATE", false);
            if (isUpdate){
                MaDangChon = intent.getStringExtra("MA_HE_THONG");
                String TenTruyen = intent.getStringExtra("TEN_TRUYEN");
                String TenTacGia = intent.getStringExtra("TAC_GIA");
                int TapSo = intent.getIntExtra("TAP",0);
                int GiaTien = intent.getIntExtra("GIA",0);
                int SoLuong = intent.getIntExtra("SO_LUONG",0);
                String NgayNhap = intent.getStringExtra("NGAY_NHAP");

                edtten.setText(TenTruyen);
                edttacgia.setText(TenTacGia);
                edttap.setText(String.valueOf(TapSo));
                edtgiatien.setText(String.valueOf(GiaTien));
                edtsoluong.setText(String.valueOf(SoLuong));
                edtdate.setText(NgayNhap);
            }
        }

        if(isUpdate){
            btninsert.setText("Cập nhật");
        } else {
            btninsert.setText("Thêm truyện");
        }

        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(EditText e : checklist) {
                    if(e.getText().toString().trim().isEmpty()) {
                        e.setError("Ô này không được bỏ trống.");
                        e.requestFocus();
                        return;
                    }
                }

                try {
                    String tenTruyen = edtten.getText().toString().trim();
                    String tenTacGia = edttacgia.getText().toString().trim();
                    int tapSo = Integer.parseInt(edttap.getText().toString().trim());
                    int giaTien = Integer.parseInt(edtgiatien.getText().toString().trim());
                    int soLuong = Integer.parseInt(edtsoluong.getText().toString().trim());
                    String ngayNhap = edtdate.getText().toString().trim();
                    String maHienThi = CreateID(tenTruyen, tenTacGia,tapSo, giaTien ,soLuong,ngayNhap);

                    if (isUpdate){
                        Truyen newtruyen =new Truyen(MaDangChon, maHienThi , tenTruyen, tenTacGia,tapSo, giaTien , soLuong,ngayNhap);
                        DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
                        mydata.child(MaDangChon).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    mydata.child(MaDangChon).setValue(newtruyen)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MainActivity2.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity2.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else {
                                    Toast.makeText(MainActivity2.this, "Cập nhật thất bại. \nTruyện đã bị xóa hoặc chưa tồn tại.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity2.this, "Lỗi mạng khi kiểm tra: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String maHeThong = CreateIdSystem();
                        DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");

                        Truyen truyen = new Truyen(maHeThong, maHienThi,tenTruyen, tenTacGia,tapSo,giaTien , soLuong,ngayNhap);

                        mydata.child(maHeThong).setValue(truyen)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity2.this,"Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity2.this,"Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }


                }catch (Exception e ) {
                    Toast.makeText(MainActivity2.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private String CreateID(String ten, String ten2 , int tap , int gia, int soluong, String ngaynhap){
        String kytu1 = "";
        if(!ten.isEmpty()){
            kytu1 = ten.substring(0,1).toUpperCase();
        }
        String kytu2 = "";
        if(!ten2.isEmpty()){
            kytu2 = ten2.substring(0,1).toUpperCase();
        }
        String kytu3 = String.format("%03d",tap);
        String kytu4 = String.format("%03d", gia/1000) + "K";
        //String kytu5 = String.format("%02d",soluong);
        //String kytu6 = ngaynhap.replace("/", "");
        return kytu1 + kytu2 + kytu3 + kytu4;// + kytu5;// + kytu6;
    }
    private String CreateIdSystem (){
        long id = System.currentTimeMillis();
        return ""+id;
    }
}