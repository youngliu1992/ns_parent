package com.creditease.ns.transporter.util;

import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import java.util.zip.CRC32;

public class BinLogUtil {
    public BinLogUtil() {
    }

    public static void binlogWrite(Message message) throws Exception {
        String identify = "tpbin";
        byte version = true;
        long crcheadercode = 0L;
        int lengthOfBody = false;
        int dataoffset = false;
        int lengthOfHeader = false;
        int lengthOfData = false;
        byte[] body = message.getBody();
        if (body != null) {
            int var13 = body.length;
        }

        Header header = message.getHeader();
        byte[] headerbytes = ProtoStuffSerializeUtil.serializeForCommon(header);
        int lengthOfHeader = headerbytes.length;
        CRC32 crc32 = new CRC32();
        crc32.update(headerbytes);
        crcheadercode = crc32.getValue();
    }
}
