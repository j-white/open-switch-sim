package com.opennms.switchsim.daemon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AgentConfigData {
    public URL m_moFile;
    public InetAddress m_listenAddr;
    public int m_listenPort;
    private static final Logger LOG = LoggerFactory.getLogger(AgentConfigData.class);
    
    public AgentConfigData() {
    }
    
    protected AgentConfigData(String moFileSpec, String listenAddr, int listenPort) throws UnknownHostException, MalformedURLException, IOException  {
        if (moFileSpec.contains("://") || moFileSpec.startsWith("file:")) {
            m_moFile = new URL(moFileSpec);
        } else {
        	ClassPathResource resource = new ClassPathResource(moFileSpec);
        	m_moFile = resource.getURL();
        	LOG.info(m_moFile.toString());
        	
        } 
        m_listenAddr = InetAddress.getByName(listenAddr);
        m_listenPort = listenPort;
    }

    public URL getMoFile() {
        return m_moFile;
    }

    public void setMoFile(URL moFile) {
        m_moFile = moFile;
    }

    public InetAddress getListenAddr() {
        return m_listenAddr;
    }

    public void setListenAddr(InetAddress listenAddr) {
        m_listenAddr = listenAddr;
    }

    public long getListenPort() {
        return m_listenPort;
    }

    public void setListenPort(int listenPort) {
        m_listenPort = listenPort;
    }
}