package com.example.managa_manager_v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class truyenAdapter extends RecyclerView.Adapter<truyenAdapter.TruyenViewHolder> {
    private Context context;
    private ArrayList<Truyen> mylist;

    public truyenAdapter(Context context, ArrayList<Truyen> mylist){
        this.context = context;
        this.mylist = mylist;
    }

    @NonNull
    @Override
    public truyenAdapter.TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //android.util.Log.d("KiemTraRAM", "Đang TẠO MỚI một khung CardView trên RAM!");
        View view = LayoutInflater.from(context).inflate(R.layout.customlistview, parent, false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull truyenAdapter.TruyenViewHolder holder, int position) {
        Truyen truyen = mylist.get(position);
        //android.util.Log.d("KiemTraRAM", "Đang ĐỔ DỮ LIỆU cho truyện: " + truyen.tenTruyen);
        if (truyen == null) return;

        holder.maHienThi.setText("Mã truyện: " + truyen.maHienThi);
        holder.edtten.setText("Tên truyện: " + truyen.tenTruyen);
        holder.edttacgia.setText("Tác giả: " + truyen.tacGia);
        holder.edttap.setText("Tập: " + truyen.tapSo);
        holder.edtgia.setText("Giá: " + (truyen.giaTien)/1000 + "K");
        holder.edtsoluong.setText("Số lượng: " + truyen.soLuong);
        holder.edtdate.setText("Ngày nhập: " + truyen.ngayNhap);

        holder.imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder buider = new AlertDialog.Builder(context);
                buider.setTitle("Tùy chọn");
                buider.setMessage("Bạn muốn làm gì với " + truyen.tenTruyen+ "?");
                buider.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, MainActivity2.class);

                        intent.putExtra("IS_UPDATE", true);
                        intent.putExtra("MA_HE_THONG", truyen.maHeThong);
                        intent.putExtra("TEN_TRUYEN", truyen.tenTruyen);
                        intent.putExtra("TAC_GIA", truyen.tacGia);
                        intent.putExtra("TAP", truyen.tapSo);
                        intent.putExtra("GIA", truyen.giaTien);
                        intent.putExtra("SO_LUONG", truyen.soLuong);
                        intent.putExtra("NGAY_NHAP", truyen.ngayNhap);

                        context.startActivity(intent);
                    }
                });
                buider.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference mydata = FirebaseDatabase.getInstance().getReference("Manga");
                        mydata.child(truyen.maHeThong).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Lỗi: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                buider.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                buider.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mylist != null){
            return mylist.size();
        }
        return 0;
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder{
        ImageView imgpic, imgbtn;
        TextView maHienThi, edtten, edttacgia, edttap, edtgia, edtsoluong, edtdate;

        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            imgpic = itemView.findViewById(R.id.imgpic);
            imgbtn = itemView.findViewById(R.id.imgbtn);
            maHienThi =itemView.findViewById(R.id.tvMaHienThi);
            edtten = itemView.findViewById(R.id.tvtenTruyen);
            edttacgia = itemView.findViewById(R.id.tvTacGia);
            edttap = itemView.findViewById(R.id.tvTap);
            edtgia = itemView.findViewById(R.id.tvtien);
            edtsoluong = itemView.findViewById(R.id.tvsoluong);
            edtdate = itemView.findViewById(R.id.tvdate);

        }
    }
    public void updateList(ArrayList<Truyen> newlist){
        this.mylist = newlist;
        notifyDataSetChanged();
    }
}
