//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.mq.model.Message;

public class MessageConvertUtil {
    public MessageConvertUtil() {
    }

    public static ServiceMessage convertToServiceMessage(Message message) throws Exception {
        byte[] body = message.getBody();
        if (body != null) {
            ServiceMessage mqMessage = (ServiceMessage)ProtoStuffSerializeUtil.unSerializeForCommon(body);
            mqMessage.setHeader(message.getHeader());
            return mqMessage;
        } else {
            throw new IllegalArgumentException("格式错误，没有消息体");
        }
    }

    public static Message convertToMessage(ServiceMessage serviceMessage) throws Exception {
        Message message = new Message();
        message.setHeader(serviceMessage.getHeader());
        message.setBody(ProtoStuffSerializeUtil.serializeForCommon(serviceMessage));
        return message;
    }
}
