package db;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class utile {
    public static <T> List<T> readJsonFile(
            ObjectMapper mapper,
            String resourcePath,
            Class<T> clazz) throws Exception {

        try (InputStream is = utile.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new Exception("JSON 파일을 찾을 수 없습니다: " + resourcePath);
            }
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return mapper.readValue(is, listType);
        }
    }
}
