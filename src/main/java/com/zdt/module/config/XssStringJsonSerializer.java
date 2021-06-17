package com.zdt.module.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 * @version: 1.00.00
 * @description: 序列化代码
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-17 18:45
 */
public class XssStringJsonSerializer extends JsonDeserializer<String> {
    public XssStringJsonSerializer(Class<String> string) {
        super();
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        if (value != null) {
            return Jsoup.clean(value, Whitelist.relaxed()).trim();
        }
        return null;
    }


    @Override
    public Class<String> handledType() {
        return String.class;
    }

}
