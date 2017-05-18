package com.aghagha.tagg.models;

public class Laporan {
    private int id;
    private String judul,
            dibuat,
            nilai,
            mean,
            max;
    public Laporan(Integer id, String judul, String dibuat, String nilai, String mean, String max){
        this.id = id;
        this.judul = judul;
        this.dibuat = dibuat;
        this.nilai = nilai;
        this.mean = mean;
        this.max = max;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getDibuat() {
        return dibuat;
    }

    public String getNilai() {
        return nilai;
    }

    public String getMean() {
        return mean;
    }

    public String getMax() {
        return max;
    }
}
