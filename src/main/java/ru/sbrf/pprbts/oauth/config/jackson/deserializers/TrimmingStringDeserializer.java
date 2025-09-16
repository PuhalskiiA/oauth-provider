package ru.sbrf.pprbts.oauth.config.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;


public final class TrimmingStringDeserializer extends StdDeserializer<Object> {

    public TrimmingStringDeserializer() {
        super(String.class);
    }

    @SneakyThrows
    @Override
    public String deserialize(JsonParser parser, DeserializationContext deserializationContext) {
        return StringUtils.trim(parser.getValueAsString());
    }
}
