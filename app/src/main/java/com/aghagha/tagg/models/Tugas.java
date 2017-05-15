package com.aghagha.tagg.models;

/**
 * Created by aghagha on 06/05/2017.
 */

public class Tugas {
    private int id_tugas;
    private String judul,
                konten,
                deadline,
                mapel,
                dibuat,
                status,
                cek;
    private Boolean telat;
    public Tugas(){

    }

    public Tugas(int id_tugas, String dibuat, String deadline, String mapel, String judul, String konten, String status){
        this.id_tugas = id_tugas;
        this.judul = judul;
        this.konten = konten;
        this.deadline = deadline;
        this.mapel = mapel;
        this.dibuat = dibuat;
        this.status = status;
        this.cek = "";
    }
    public Tugas(int id_tugas, String dibuat, String deadline, String mapel, String judul, String konten, String status, String cek, Boolean telat){
        this.id_tugas = id_tugas;
        this.judul = judul;
        this.konten = konten;
        this.deadline = deadline;
        this.mapel = mapel;
        this.dibuat = dibuat;
        this.status = status;
        this.cek = cek;
        this.telat = telat;
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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getMapel() {
        return mapel;
    }

    public void setMapel(String mapel) {
        this.mapel = mapel;
    }

    public String getDibuat() {
        return dibuat;
    }

    public void setDibuat(String dibuat) {
        this.dibuat = dibuat;
    }

    public int getId_tugas() {
        return id_tugas;
    }

    public void setId_tugas(int id_tugas) {
        this.id_tugas = id_tugas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCek() {
        return cek;
    }

    public Boolean getTelat() {
        return telat;
    }
}
