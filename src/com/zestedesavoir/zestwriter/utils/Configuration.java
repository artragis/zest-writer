package com.zestedesavoir.zestwriter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.zestedesavoir.zestwriter.MainApp;

public class Configuration {
    private Properties conf;
    private String appName = "zestwriter";
    private String confFileName = "conf.properties";
    private File confFile;
    private final static String WORKSPACE_KEY = "data.workspace";
    private final static String SMART_EDITOR_KEY = "editor.smart";
    private final static String SERVER_PROTOCOL_KEY = "server.protocol";
    private final static String SERVER_HOST_KEY = "server.host";
    private final static String SERVER_PORT_KEY = "server.port";
    private StorageSaver offlineSaver;
    private StorageSaver onlineSaver;
    private LocalDirectoryFactory workspaceFactory;

    public Configuration() {
        String homeDir = System.getProperty("user.home");
        String confDirPath = homeDir+File.separator+"."+this.appName;
        String confFilePath = confDirPath+File.separator+this.confFileName;
        File confDir = new File(confDirPath);
        confFile = new File(confFilePath);
        if(!confDir.exists()) {
            confDir.mkdir();
        }

        // defaults config
        Properties props = new Properties();
        try {
            props.load(MainApp.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        conf = new Properties(props);

        if(!confFile.exists()) {
            System.out.println("not exist : "+confFile.getAbsolutePath());
            JFileChooser fr = new JFileChooser();
            FileSystemView fw = fr.getFileSystemView();
            conf.setProperty(WORKSPACE_KEY, fw.getDefaultDirectory().getAbsolutePath() + File.separator + "zwriter-workspace");
            saveConfFile();
        }
        else {
            try {
                conf.load(new FileInputStream(confFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(Entry<?, ?> entry:props.entrySet()) {
            if(!conf.containsKey(entry.getKey())) {
                conf.putIfAbsent(entry.getKey(), entry.getValue());
                saveConfFile();
            }
        }

    }

    private void saveConfFile() {
        try {
            conf.store(new FileOutputStream(confFile), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for(Entry<?, ?> entry:conf.entrySet()) {
            result.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        return result.toString();
    }

    public String getWorkspacePath() {
        return conf.getProperty(WORKSPACE_KEY);
    }

    public void setWorkspacePath(String workspacePath) {
        conf.setProperty(WORKSPACE_KEY, workspacePath);
        saveConfFile();
        this.workspaceFactory = new LocalDirectoryFactory(workspacePath);
    }

    public String getProtocol() {
        if(conf.containsKey(SERVER_PROTOCOL_KEY)) {
            return conf.getProperty(SERVER_PROTOCOL_KEY);
        } else {
            return "http";
        }
    }

    public String getPort() {
        if(conf.containsKey(SERVER_PORT_KEY)) {
            return conf.getProperty(SERVER_PORT_KEY);
        } else {
            return "80";
        }
    }

    public String getHost() {
        if(conf.containsKey(SERVER_HOST_KEY)) {
            return conf.getProperty(SERVER_HOST_KEY);
        } else {
            return "localhost";
        }
    }


    public boolean isSmartEditor() {
        return conf.getProperty(SMART_EDITOR_KEY).equalsIgnoreCase("true");
    }

    public void setSmartEditor(boolean smart) {
        conf.setProperty(SMART_EDITOR_KEY, ""+smart);
        saveConfFile();
    }

    public StorageSaver getOfflineSaver() {
        return offlineSaver;
    }

    public StorageSaver getOnlineSaver() {
        return onlineSaver;
    }

    public LocalDirectoryFactory getWorkspaceFactory() {
        return workspaceFactory;
    }

    public void loadWorkspace() throws IOException{

        this.workspaceFactory = new LocalDirectoryFactory(getWorkspacePath());

        try{
            offlineSaver = workspaceFactory.getOfflineSaver();
            onlineSaver = workspaceFactory.getOnlineSaver();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

}