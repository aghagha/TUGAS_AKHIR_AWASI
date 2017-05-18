package com.aghagha.tagg.utilities;

/**
 * Created by aghagha on 19/04/2017.
 */

public class NetworkUtils {
    //server localhost/bismillahTAselesai/public/api/login
    static String ip = "http://10.151.34.113";
    public static final String server = ip+"/bismillahTAselesai/public/api/";
    public static final String serverDir = ip+"/bismillahTAselesai/public/uploads/";
    //test login
    public static final String login = server + "login";
    public static final String register = server + "register";
    public static final String userdetail = server + "userDetail";
    public static final String userpass = server + "userPass";
    public static final String post_profil_image = server + "userImage";
    public static final String profil_image = serverDir + "gambar/profil/";

    public static final String berita_list = server + "beritaGuru";
    public static final String berita_image = serverDir + "gambar/berita/";
    public static final String add_topik = server + "addTopik";
    public static final String komentar = server + "komentar";

    public static final String forum_guru = server + "forumGuru";
    public static final String forum_murid = server + "forumMurid";
    public static final String topik_gambar = serverDir + "gambar/topik/";

    public static final String tugas = server + "tugasGuru";
    public static final String tugasMurid = server + "tugasMurid";
    public static final String mapel = server + "mapel";

    public static final String laporan = server + "laporan";

    public static final String nilaGuru = server + "nilaiGuru";

    public static final String murid = server + "murid";
}
