package org.vetti.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class MPUtils {

    public boolean validateSignature(String payload, String signatureHeader, String accessToken) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(accessToken.getBytes(), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hash = hmac.doFinal(payload.getBytes());

            String calculatedSignature = Base64.getEncoder().encodeToString(hash);
            return calculatedSignature.equals(signatureHeader);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
