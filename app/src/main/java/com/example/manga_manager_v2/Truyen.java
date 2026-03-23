package com.example.manga_manager_v2;

public class Truyen {
    public String maTruyenHienThi, maHeThong, tenTruyen, tacGia;
    public int tapSo, giaTien;

    public Truyen(){}

    public Truyen(String maHeThong,String maTruyenHienThi, String tenTruyen, String tacGia, int tapSo, int giaTien){
        this.maHeThong = maHeThong;
        this.maTruyenHienThi = maTruyenHienThi;
        this.tenTruyen = tenTruyen;
        this.tacGia = tacGia;
        this.tapSo = tapSo;
        this.giaTien = giaTien;
    }
}
