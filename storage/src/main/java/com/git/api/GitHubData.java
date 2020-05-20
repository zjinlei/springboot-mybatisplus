package com.git.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.seata.common.util.CollectionUtils;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/12/13
 */
public class GitHubData {
    public static void main(String[] args) {
        //截止至2020年5月20日18点全部issue id
        List<Integer> ids = Arrays.asList(166, 187, 190, 191, 194, 337, 348, 497, 527, 535, 546, 557, 635, 670, 714, 751, 796, 811, 884, 894, 895, 912, 951, 959, 983, 984, 993,
            1003, 1008, 1039, 1073, 1078, 1097, 1111, 1144, 1152, 1153, 1180, 1184, 1196, 1208, 1238, 1243, 1246, 1248, 1249, 1251, 1253, 1256, 1277, 1283, 1284, 1285, 1293, 1296,
            1309, 1316, 1321, 1322, 1325, 1346, 1369, 1381, 1383, 1384, 1393, 1402, 1422, 1457, 1458, 1462, 1479, 1487, 1502, 1560, 1578, 1588, 1634, 1639, 1653, 1700, 1714, 1724,
            1740, 1741, 1745, 1748, 1769, 1772, 1773, 1840, 1847, 1864, 1881, 1887, 1891, 1892, 1915, 1921, 1924, 1940, 1964, 1970, 1976, 1977, 1989, 1991, 1998, 2002, 2009, 2010,
            2011, 2023, 2046, 2047, 2057, 2073, 2084, 2088, 2096, 2122, 2127, 2133, 2168, 2173, 2181, 2183, 2196, 2213, 2216, 2218, 2226, 2229, 2231, 2246, 2250, 2267, 2277, 2278,
            2281, 2294, 2295, 2298, 2300, 2302, 2313, 2314, 2315, 2316, 2317, 2325, 2327, 2332, 2334, 2338, 2339, 2341, 2352, 2356, 2358, 2360, 2366, 2375, 2383, 2389, 2393, 2394,
            2399, 2404, 2411, 2421, 2429, 2430, 2431, 2442, 2451, 2454, 2457, 2459, 2460, 2461, 2462, 2471, 2472, 2474, 2478, 2485, 2488, 2497, 2499, 2500, 2506, 2513, 2515, 2516,
            2518, 2520, 2521, 2527, 2531, 2533, 2534, 2536, 2538, 2539, 2545, 2547, 2555, 2556, 2563, 2564, 2567, 2570, 2576, 2587, 2589, 2592, 2593, 2594, 2595, 2600, 2604, 2605,
            2608, 2614, 2621, 2630, 2632, 2633, 2634, 2635, 2636, 2637, 2638, 2640, 2641, 2643, 2644, 2651, 2654, 2661, 2662, 2664, 2665, 2668, 2671, 2672, 2674, 2677, 2682, 2686,
            2687, 2688, 2689, 2690, 2692, 2693, 2695, 2696, 2697, 2700, 2702, 2703);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Success loading Mysql Driver!");
        } catch (Exception e) {
            System.out.print("Error loading Mysql Driver!");
            return;
        }
        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://rm-2zetd9474ydd1g5955o.mysql.rds.aliyuncs.com:3306/fescar", "workshop", "Workshop123");
            System.out.println("Success connect Mysql server!");
            for (Integer id : ids) {
                String url = "https://api.github.com/repos/seata/seata/issues/" + id + "/comments";
                int page = 0;
                int per_page = 100;
                List<IssueComment> issueComments = getJsonArray(url, page, per_page, new HashMap<String, String>());
                if (CollectionUtils.isEmpty(issueComments)) {
                    continue;
                }
                String sql = "insert into issue_comment values (?,?,?,?,?)";
                PreparedStatement stmt = connect.prepareStatement(sql);
                for (IssueComment issueComment : issueComments) {
                    stmt.setInt(1, issueComment.getId());
                    stmt.setInt(2, id);
                    stmt.setString(3, issueComment.getUser().getLogin());
                    stmt.setString(4, issueComment.getHtml_url());
                    stmt.setString(5, issueComment.getBody());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                stmt.close();
                Thread.sleep(10000);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<IssueComment> getJsonArray(String url, int page, int size, Map<String, String> paramMap) {
        paramMap.put("page", String.valueOf(page));
        paramMap.put("per_page", String.valueOf(size));
        paramMap.put("access_token", "c9c275d5652a3d0ac12112f7167db47be4ed9246");
        String issue = HttpUtil.sendHttpsGet(url, new HashMap<String, String>(), paramMap);
        //e.g. [{"url":"https://api.github.com/repos/seata/seata/issues/comments/611371549","html_url":"https://github.com/seata/seata/issues/2527#issuecomment-611371549","issue_url":"https://api.github.com/repos/seata/seata/issues/2527","id":611371549,"node_id":"MDEyOklzc3VlQ29tbWVudDYxMTM3MTU0OQ==","user":{"login":"slievrly","id":8758457,"node_id":"MDQ6VXNlcjg3NTg0NTc=","avatar_url":"https://avatars1.githubusercontent.com/u/8758457?v=4","gravatar_id":"","url":"https://api.github.com/users/slievrly","html_url":"https://github.com/slievrly","followers_url":"https://api.github.com/users/slievrly/followers","following_url":"https://api.github.com/users/slievrly/following{/other_user}","gists_url":"https://api.github.com/users/slievrly/gists{/gist_id}","starred_url":"https://api.github.com/users/slievrly/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/slievrly/subscriptions","organizations_url":"https://api.github.com/users/slievrly/orgs","repos_url":"https://api.github.com/users/slievrly/repos","events_url":"https://api.github.com/users/slievrly/events{/privacy}","received_events_url":"https://api.github.com/users/slievrly/received_events","type":"User","site_admin":false},"created_at":"2020-04-09T07:16:35Z","updated_at":"2020-04-09T07:16:35Z","author_association":"MEMBER","body":"![image](https://user-images.githubusercontent.com/8758457/78868199-1462c100-7a75-11ea-87d3-8aa7cbbcbbd9.png)\r\n"}]
        System.out.println("issue: " + issue);
        List<IssueComment> issueComments = new Gson().fromJson(issue, new TypeToken<List<IssueComment>>() {
        }.getType());
        return issueComments;
    }
}
