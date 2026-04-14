package edu.cupk.simple_library_system.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PageResponseSerializer extends JsonSerializer<PageResponse<?>> {

    @Override
    public void serialize(PageResponse<?> response, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        
        // 写入 code
        gen.writeNumberField("code", response.getCode());
        
        // 写入 message（如果不为 null）
        if (response.getMessage() != null) {
            gen.writeStringField("message", response.getMessage());
        }
        
        // 写入 count（如果不为 null）
        if (response.getCount() != null) {
            gen.writeNumberField("count", response.getCount());
        }
        
        // 写入 data - 优先使用 singleData，如果为 null 则使用 data
        if (response.getSingleData() != null) {
            gen.writeObjectField("data", response.getSingleData());
        } else if (response.getData() != null) {
            gen.writeObjectField("data", response.getData());
        }
        
        gen.writeEndObject();
    }
}
