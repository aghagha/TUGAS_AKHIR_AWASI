package com.aghagha.tagg.models;

/**
 * Created by aghagha on 20/05/2017.
 */

public class Jadwal {
    private String waktu;
    private String kelas;
    private String mapel;

    public Jadwal(String waktu, String kelas, String mapel){
        this.waktu = waktu;
        this.kelas = kelas;
        this.mapel = mapel;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getKelas() {
        return kelas;
    }

    public String getMapel() {
        return mapel;
    }
}
