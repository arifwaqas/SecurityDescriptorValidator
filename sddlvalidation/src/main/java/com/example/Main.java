package com.example;

import net.tirasa.adsddl.ntsd.SDDL;
import net.tirasa.adsddl.ntsd.SID;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        String strjson = new String(
                "RDpBSShBO0lEOzB4MTIwMGE5Ozs7QVUpKEE7SUQ7MHgxMjAwYTk7OztTTykoQTtJRDtGQTs7O0JBKShBO0lEO0ZBOzs7U1kp");

        byte[] decoded = Base64.getDecoder().decode(strjson);

        String decodedString = new String(decoded, StandardCharsets.ISO_8859_1);

        System.out.println("Decoded string: " + decodedString);

        String sd = new String(
                "D:(A;ID;FA;;;SY)(A;ID;FA;;;BA)(A;ID;FA;;;S-1-5-21-2976576824-3568875664-3867986209-7232415)");

        byte[] securityDescriptor = sd.getBytes(StandardCharsets.ISO_8859_1);

        SDDL sddl = new SDDL(securityDescriptor);

        SID ownerSid = sddl.getOwner();

        String ownerSidString = new String(ownerSid.toString());

        System.out.println(ownerSidString);

        return;

    }

    public static boolean validateSDDL(Map<String, String> mapSDDLMap) {
        return true;
    }
}