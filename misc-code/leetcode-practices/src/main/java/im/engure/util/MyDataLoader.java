package im.engure.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class MyDataLoader {
    /**
     * 加载二维数组。文件结构："[ [..], [..], .. ]"
     *
     * @param classPathFileName resources 目录下的文件名，比如 "1353.json"
     */
    public static int[][] loadTwoDimensionsJSONArray(String classPathFileName) {
        FileReader fileReader = FileReader.create(new ClassPathResource(classPathFileName).getFile());
        String json = fileReader.readString();
        JSONArray jsonArr = JSON.parseArray(json);
        Object[] array = jsonArr.toArray();
        int[][] data = new int[array.length][];
        int i = 0;
        for (Object item : array) {
            JSONArray ja = ((JSONArray) item);
            List<Integer> l = ja.toJavaList(int.class);
            data[i] = new int[l.size()];
            for (int j = 0; j < l.size(); j++) {
                data[i][j] = l.get(j);
            }
            i++;
        }
        return data;
    }
}
