package cn.ac.lz233.tarnhelm.service;

import cn.ac.lz233.tarnhelm.service.ShizukuCallback;

interface IClipboardShizukuService {
    void destroy() = 16777114; // Destroy method defined by Shizuku server
    void exit() = 1; // Exit method defined by user
    void start() = 2;
    void addCallback(ShizukuCallback callback) = 3;
}