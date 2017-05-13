package com.aghagha.tagg.models;

/**
 * Created by aghagha on 24/04/2017.
 */

public class Berita {
    private int id_tipe_berita;
    private String judul;
    private String konten;
    private String gambar;
    private String lampiran;
    private String created_at;

    public Berita(){

    }

    public Berita(int id_tipe_berita,String judul, String konten, String gambar, String lampiran, String created_at){
        this.id_tipe_berita = id_tipe_berita;
        this.judul = judul;
        this.konten = konten;
        this.gambar = gambar;
        this.lampiran = lampiran;
        this.created_at = created_at;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKonten() {
        return konten;
    }

    public void setKonten(String konten) {
        this.konten = konten;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLampiran() {
        return lampiran;
    }

    public String getCreated_at() {
        return created_at;
    }
}
