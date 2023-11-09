package com.example;

import net.tirasa.adsddl.ntsd.SDDL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.hierynomus.msdtyp.SecurityDescriptor;
import com.hierynomus.msdtyp.SecurityInformation;
import com.hierynomus.smb.SMBBuffer;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.codec.binary.Base64;

public class Main {

    public static String getSd(String shareName, String folderPath) {

        String hostName = new String("DCSERVER.test.local");
        String domain = new String("test.local");
        String userName = new String("sharad");
        String password = new String("Attivo2$");

        String sdString = new String();
        SMBClient client = new SMBClient();
        DiskShare share;
        Connection connection;
        try {
            // making the connection ot the host
            connection = client.connect(hostName);
            AuthenticationContext ac = new AuthenticationContext(userName, password.toCharArray(), domain);

            Session session = connection.authenticate(ac);

            // Connect to Share
            share = (DiskShare) session.connectShare(shareName);
            /*
             * for (FileIdBothDirectoryInformation f : share.list(folderPath)) {
             * System.out.println("File : " + f.getFileName());
             * }
             */
            // create config for retreiving the objects
            Set<SecurityInformation> securityInformationSet = new HashSet<>();
            securityInformationSet.add(SecurityInformation.DACL_SECURITY_INFORMATION);

            // Getting the security descirptor
            /* dirEntryPath must be SERVER.doma.in/SHARENAME/path/to/file/filename.ext */
            String dirEntryPath = folderPath;
            SecurityDescriptor sd = share.getSecurityInfo(dirEntryPath, securityInformationSet);

            System.out.println("***********************************");
            System.out.println("Security_Descriptor" + sd.toString());
            System.out.println("***********************************");

            SMBBuffer smbBuffer = new SMBBuffer();
            sd.write(smbBuffer);
            sdString = new String(smbBuffer.array(), StandardCharsets.ISO_8859_1);

            byte[] securityDescriptor = Base64.decodeBase64(sdString);

            SDDL sddl = new SDDL(securityDescriptor);

            sdString = new String(securityDescriptor, StandardCharsets.ISO_8859_1);

            System.out.println("Received security descriptor: " + sdString);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sdString;

    }

    public static void main(String[] args) {
        System.out.println("Hello world!");

        String sd = getSd("SYSVOL", "New Folder\\New Text Document.txt");
        // main function

        return;

    }

}