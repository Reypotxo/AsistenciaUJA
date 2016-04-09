/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.uja.telematica.DAO;

import android.nfc.NdefRecord;


import java.io.UnsupportedEncodingException;

/**
 * An NFC Text Record
 */
public class NfcTextRecord {

    private final String texto;

    private NfcTextRecord(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public static NfcTextRecord Parse(NdefRecord record) {
        /*
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));*/
        try {
            byte[] payload = record.getPayload();
            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */
            String textEncoding = GenericTypes.UNDEFINED_STRING;
            if((payload[0] & 0200) == 0)
            {
                textEncoding = "UTF-8";
            }
            else
            {
                textEncoding = "UTF-16";
            }
            int languageCodeLength = payload[0] & 0077;
            //String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            String text =
                new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return new NfcTextRecord(text);
        } catch (UnsupportedEncodingException e) {
            // Solo se lanza cuando el tag viene mal
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isText(NdefRecord record) {
        try {
            Parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
