package com.ngobrol.ngobrolonlen.EncryptionUtils;

import android.util.Base64;

public class Encoding {
    public static String base64Encode(final byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    public static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }
}
