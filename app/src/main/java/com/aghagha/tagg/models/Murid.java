package com.aghagha.tagg.models;

/**
 * Created by aghagha on 11/05/2017.
 */

public class Murid {
    private String id;
    private String nama;
    private String kelas;
    private String wali;
    private String gambar;
    private String sekolah;

    public Murid (String id, String nama, String kelas, String wali, String gambar, String sekolah){
        this.id = id;
        this.nama = nama;
        this.kelas = kelas;
        this.wali = wali;
        this.gambar = gambar;
        this.sekolah = sekolah;
    }

    public String getNama() {
        return nama;
    }

    public String getKelas() {
        return kelas;
    }

    public String getWali() {
        return wali;
    }

    public String getGambar() {
        return gambar;
    }

    public String getId() {
        return id;
    }

    public String getSekolah() {
        return sekolah;
    }
}
