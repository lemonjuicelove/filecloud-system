package com.github.jfcloud.jos.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zj
 * @date 2021/12/31
 */
public class CollectUtil {
    private static final Logger log = LoggerFactory.getLogger(CollectUtil.class);

    public CollectUtil() {
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("x-forwarded-for");
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }

        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }

        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }

    public String getLocalIp() {
        InetAddress addr = null;
        String ip = "";

        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException var4) {
            log.error("获取本地IP失败");
        }

        if (addr != null) {
            ip = addr.getHostAddress().toString();
        }

        return ip;
    }
}
