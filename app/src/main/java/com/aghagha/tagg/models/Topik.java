package com.aghagha.tagg.models;

/**
 * Created by aghagha on 08/05/2017.
 */

public class Topik {
    private String id;
    private String id_;
    private String judul;
    private String konten;
    private String tanggal;
    private String gambar;
    private String komentar;
    public Topik(){

    }

    public Topik(String id, String id_,String judul, String konten,String tanggal, String gambar, String komentar){
        this.id = id;
        this.id_ = id_;
        this.judul = judul;
        this.konten = konten;
        this.tanggal = tanggal;
        this.gambar = gambar;
        this.komentar = komentar;
    }

    public String getId() {
        return id;
    }

    public String getId_() {
        return id_;
    }

    public String getJudul() {
        return judul;
    }

    public String getKonten() {
        return konten;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getGambar() {
        return gambar;
    }

    public String getKomentar() {
        return komentar;
    }
}
