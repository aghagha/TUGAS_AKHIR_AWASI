package com.aghagha.tagg.models;

/**
 * Created by aghagha on 10/05/2017.
 */

public class Komentar {
    private String nama;
    private String konten;
    private String waktu;
    private String gambar;
    public Komentar(String nama, String konten, String waktu, String gambar){
        this.nama = nama;
        this.konten = konten;
        this.waktu = waktu;
        this.gambar = gambar;
    }

    public String getNama() {
        return nama;
    }

    public String getKonten() {
        return konten;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getGambar() {
        return gambar;
    }
}
