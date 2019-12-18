package com.git.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.joda.time.DateTime;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/12/13
 */
public class GitHubData {
    public static void main(String[] args) {
        //issue https://github.com/seata/seata/pulls
        DateTime baseTime = new DateTime(2019, 4, 1, 0, 0);
        String url = "https://api.github.com/repos/seata/seata/commits";
        int page = 0;
        int per_page = 50;
        List<Commits> jsonArray = getJsonArray(url, page, per_page, new HashMap<String, String>());
        while (jsonArray.size() == per_page) {
            ++page;
            jsonArray.stream().forEach(t -> {
                System.out.println(JSON.toJSON(t));
            });
            jsonArray = getJsonArray(url, page, per_page, new HashMap<String, String>());
        }

    }

    public static List<Commits> getJsonArray(String url, int page, int size, Map<String, String> paramMap) {
        paramMap.put("page", String.valueOf(page));
        paramMap.put("per_page", String.valueOf(size));
        paramMap.put("access_token", "");
        String issue = HttpUtil.sendHttpsGet(url, new HashMap<String, String>(), paramMap);
        List<Commits> commits = new Gson().fromJson(issue, new TypeToken<List<Commits>>() {}.getType());
        return commits;
    }
}
