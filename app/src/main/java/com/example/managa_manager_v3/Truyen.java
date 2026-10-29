package com.example.managa_manager_v3;

public class Truyen {
    public String maHeThong, maHienThi, tenTruyen, tacGia, ngayNhap;
    public int tapSo, soLuong, giaTien;
    public Truyen(){}
    public Truyen(String maHeThong, String maHienThi, String tenTruyen, String tenTacGia, int tapSo, int giaTien, int soLuong, String ngayNhap){
        this.maHeThong = maHeThong;
        this.maHienThi = maHienThi;
        this.tenTruyen = tenTruyen;
        this.tacGia = tenTacGia;
        this.tapSo = tapSo;
        this.giaTien = giaTien;
        this.soLuong = soLuong;
        this.ngayNhap = ngayNhap;
    }

}
