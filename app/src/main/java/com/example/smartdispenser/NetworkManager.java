package com.example.smartdispenser;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NetworkManager {
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    public NetworkManager(Context context) {
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("NSD", "Discovery start failed: Error code=" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("NSD", "Discovery stop failed: Error code=" + errorCode);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d("NSD", "Discovery started: " + serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("NSD", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d("NSD", "Service found success" + serviceInfo);
                if (!serviceInfo.getServiceType().equals(NsdManager.PROTOCOL_DNS_SD)) {
                    return;
                }
                // 服务发现成功
                if (serviceInfo.getServiceName().contains("ESP32")) { // 假设你的ESP32服务名称包含"ESP32"
                    // 获取IP地址和端口号
                    String ipAddress = serviceInfo.getHost().getHostAddress();
                    int port = serviceInfo.getPort();
                    // 构建连接URL
                    String ESP32Url = "http://" + ipAddress + ":" + port;
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e("NSD", "Service lost" + serviceInfo);
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(null, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }
}
