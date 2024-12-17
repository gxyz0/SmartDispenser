package com.example.smartdispenser;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;

public class NetworkManager {
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;

    private String IP = null;
    private int PORT = 0;
    public String URL = null;

    // 单例模式 返回NetworkManager
    private static NetworkManager INSTANCE;
    public static synchronized NetworkManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetworkManager(context);
        }
        return INSTANCE;
    }

    private NetworkManager(Context context) {
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
                // 服务发现成功
                Log.d("NSD", "Service found success" + serviceInfo);
                // 解析域名
                resolveService(serviceInfo);
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e("NSD", "Service lost" + serviceInfo);
            }
        };
    }

    private void resolveService(NsdServiceInfo serviceInfo) {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e("NSD", "Resolve failed: Error code:" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo resolvedServiceInfo) {
                Log.d("NSD", "Service resolved. " + resolvedServiceInfo);
                try {
                    InetAddress host = resolvedServiceInfo.getHost();
                    if (host != null) {
                        // 获取IP地址 端口号 URL
                        IP = host.getHostAddress();
                        PORT = resolvedServiceInfo.getPort();
                        URL = "http://" + IP + ":" + PORT;
                        Log.d("ESP32Url", URL);
                    } else {
                        Log.e("NSD", "Host is null for resolved service: " + resolvedServiceInfo);
                    }
                } catch (Exception e) {
                    Log.e("NSD", "Exception: " + e.getMessage());
                }
            }
        };
        mNsdManager.resolveService(serviceInfo, mResolveListener);
    }

    public void startDiscovery() {
        mNsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }
}
