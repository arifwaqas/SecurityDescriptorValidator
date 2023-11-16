package com.example;

import net.tirasa.adsddl.ntsd.SDDL;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.tomcat.util.http.fileupload.FileUtils;

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

            try (FileOutputStream fos = new FileOutputStream("newfile.txt")) {
                fos.write(smbBuffer.array());
                // fos.close(); There is no more need for this line since you had created the
                // instance of "fos" inside the try. And this will automatically close the
                // OutputStream
            }

            sdString = new String(smbBuffer.array(), StandardCharsets.ISO_8859_1);

            try (PrintWriter out = new PrintWriter("filename.txt")) {
                out.println();
            }

            /* at RemoteSMBHandler.java */

            byte[] securityDescriptor = Base64.decodeBase64(sdString);
            String decodedSdString = new String(securityDescriptor, StandardCharsets.ISO_8859_1);

            /* at VerifyGPO.java */
            byte[] securityDescriptor2 = decodedSdString.getBytes(StandardCharsets.ISO_8859_1);

            // SDDL sddl = new SDDL(securityDescriptor2);
            SDDL sddl = new SDDL(securityDescriptor2);
            sdString = new String(securityDescriptor, StandardCharsets.ISO_8859_1);

            System.out.println("Received security descriptor: " + sdString);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sdString;

    }


    public static void validateSD(String filepath){

        File file = new File(filepath);

        try {
            FileInputStream fin = new FileInputStream(file);

            DataInputStream din = new DataInputStream(fin);

            byte b[] = new byte[(int)file.length()];
            din.read(b);

            din.close();

            byte[] decodedBytes = Base64.decodeBase64(b);
            // Validation usign the library we are using at backend
            // Our Target Function
            SDDL sddl = new SDDL(decodedBytes);
            // --------------
            System.out.println("Received security descriptor: " + sddl.toString());

        } catch (FileNotFoundException fe) {
            System.out.println("FileNotFoundException : " + fe);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
        } catch(Exception ex){
            System.out.println("Unknown exception caught : " + ex.getMessage());
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world!");

        validateSD("C:/input.txt");

        
        // String sd = getSd("SYSVOL", "New Folder\\New Text Document.txt");
        // main function

        return;

    }

}