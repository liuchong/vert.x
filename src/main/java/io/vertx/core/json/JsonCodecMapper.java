package io.vertx.core.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.spi.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

class JsonCodecMapper {

  private static final Map<Class, JsonMapper> codecMap;

  static {
    Map<Class, JsonMapper> map = new HashMap<>();
    ServiceLoader<JsonMapper> codecServiceLoader = ServiceLoader.load(JsonMapper.class);
    for (JsonMapper j : codecServiceLoader) {
      map.put(j.getTargetClass(), j);
    }
    codecMap = map;
  }

  private static <T> JsonMapper codec(Class<T> c) {
    return codecMap.get(c);
  }

  public static <T> T decode(Object json, Class<T> c) {
    if (json == null) {
      return null;
    }
    JsonMapper<T, Object> codec = (JsonMapper<T, Object>) codecMap.get(c);
    if (codec == null) {
      throw new IllegalStateException("Unable to find codec for class " + c.getName());
    }
    return codec.deserialize(json);
  }

  public static <T> T decodeBuffer(Buffer value, Class<T> c) {
    return decode(Json.decodeValue(value), c);
  }

  public static Object encode(Object value) {
    if (value == null) {
      return null;
    }
    JsonMapper<Object, Object> codec = (JsonMapper<Object, Object>) codecMap.get(value.getClass());
    if (codec == null) {
      throw new IllegalStateException("Unable to find codec for class " + value.getClass().getName());
    }
    return codec.serialize(value);
  }

  public static Buffer encodeBuffer(Object value) {
    return Json.encodeToBuffer(encode(value));
  }
}
