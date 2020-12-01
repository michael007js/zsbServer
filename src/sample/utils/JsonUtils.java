package sample.utils;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    /**
     * 将对象转成字符串
     */
    public static <T> String formatToJsonString(T t) {
        String json = new Gson().toJson(t);
        return json;
    }

    /**
     * 将字符串转换成复杂列表
     */
    public static <T> T formatToObject(String json, Class<T> cls) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        return gson.fromJson(jsonObject, cls);

    }

    /**
     * 将复杂列表转换成字符串（因为路由框架无法使用putParcelableArrayList方法）
     */
    public static <T> String formatToJsonString(ArrayList<T> list) {
        String json = new Gson().toJson(list);
        return json;
    }

    /**
     * 将字符串转换成复杂列表（因为路由框架无法使用putParcelableArrayList方法）非基本数据类型
     */
    public static <T> ArrayList<T> formatToList(String json, Class<T> cls) {
        ArrayList<T> list = new ArrayList<>();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            list.add(gson.fromJson(jsonElement, cls));
        }
        return list;
    }

    /**
     * 将字符串转换成复杂列表（因为路由框架无法使用putParcelableArrayList方法）基本数据类型
     */
    public static <T> ArrayList<T> formatToList2(String json, Class cls) {
        Type type = new ParameterizedTypeImpl(cls);
        ArrayList<T> list = new Gson().fromJson(json, type);
        return list;
    }


    public  static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
