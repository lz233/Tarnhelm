package cn.ac.lz233.tarnhelm.service;

interface ShizukuCallback {
    void onOpNoted(String op, int uid, String packageName, String attributionTag, int flags, int result);
}