package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class RedFactUtilsTest {

    @Test
    public void sendUrlToSftp() {

        new RedFactUtils().sendUrlToSftp("https://www.google.co.uk/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
          "~/.ssh/id_rsa" , "image.png","atex","dmdesk.teckbote.de", 22, "/home/bilder");
    }

    @Test
    public void sendUrlToLocalSftp() {

        new RedFactUtils().sendUrlToSftp("https://www.google.co.uk/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
          "~/.ssh/id_rsa", "image2.png","Leigh Warren","localhost", 22, "");
    }

    @Test
    public void testJschLocal() throws Exception {

        String srcUrl = "https://www.google.co.uk/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
        String destFilename = "image.png";

        JSch ssh = new JSch();
        Session session = ssh.getSession("Leigh Warren:","localhost",22);
        session.setConfig("StrictHostKeyChecking", "no");

        session.setConfig("PreferredAuthentications",
          "password,gssapi-with-mic,publickey,keyboard-interactive");

        session.setTimeout(15000);
        session.setPassword("leigh");
        session.connect();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.connect();
        sftpChannel.cd(".");
        sftpChannel.put(new URL(srcUrl).openStream(), destFilename);
        sftpChannel.quit();
//        session.append(new URL(imageServiceUrl+srcUrl).openStream(),destPath+ File.separator+destFilename);

        session.disconnect();
    }

    @Test
    public void testJschRemote() throws Exception {

        String srcUrl = "https://www.google.co.uk/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
        String destFilename = "image.png";

        JSch ssh = new JSch();
        String privateKey = "~/.ssh/id_rsa";

        ssh.addIdentity(privateKey);
        Session session = ssh.getSession("atex","dmdesk.teckbote.de",22);
        session.setConfig("StrictHostKeyChecking", "no");

//        session.setConfig("PreferredAuthentications",
//          "password");

        session.setTimeout(15000);
        session.connect();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.connect();
        sftpChannel.cd("/home/bilder");
        sftpChannel.put(new URL(srcUrl).openStream(), destFilename);
        sftpChannel.quit();
//        session.append(new URL(imageServiceUrl+srcUrl).openStream(),destPath+ File.separator+destFilename);

        session.disconnect();
    }

}
