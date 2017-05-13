package com.aghagha.tagg.models;

/**
 * Created by aghagha on 08/05/2017.
 */

public class Nilai {
    private String id, nama, nilai, status;

    public Nilai(){

    }

    public Nilai(String id, String nama, String nilai,String status){
        this.id = id;
        this.nama = nama;
        this.nilai = nilai;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
