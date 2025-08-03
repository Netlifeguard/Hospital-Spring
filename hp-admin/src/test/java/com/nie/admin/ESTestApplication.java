package com.nie.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@SpringBootTest
@Profile("test")
public class ESTestApplication {
    private RestHighLevelClient client;

//    @BeforeEach
//    void client() throws IOException{
//        client = new RestHighLevelClient(RestClient.builder(
//                HttpHost.create("http://192.168.142.133:9200")
//        ));
//    }

    @Test
    public void c_index() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("javaclient");//创建索引的请求对象
        request.source(MAPPING, XContentType.JSON);//请求参数，即索引的mapping映射
        client.indices().create(request, RequestOptions.DEFAULT);//发送创建请求
    }

    @Test
    public void c_doc() throws IOException {
        final IndexRequest request = new IndexRequest("javaclient").id("102");
        request.source(DOC, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }


    @Test
    void g_doc() throws IOException {
        final GetRequest request = new GetRequest("javaclient", "101");
        final GetResponse response = client.get(request, RequestOptions.DEFAULT);
        final String source = response.getSourceAsString();
        log.info(source);
    }

    @Test
    void u_doc() throws IOException {
        final UpdateRequest request = new UpdateRequest("javaclient", "101");
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "三狗子");
        request.doc(map);
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void s_doc() throws IOException {
        final SearchRequest request = new SearchRequest("javaclient");
        request.source().query(QueryBuilders.matchQuery("name", "三狗子"));//精确查询;
        request.source().sort("age", SortOrder.DESC);
        request.source().highlighter(SearchSourceBuilder.highlight().field("name").preTags("<em>").postTags("</em>"));
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        final SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            log.info(hit.getSourceAsString());
        }


    }


    private final String DOC = """
            {
            "name":"张三",
            "age":18
            }
            """;

    private final String MAPPING = """
            {
              "mappings": {
                "properties": {
                  "name": {
                    "type": "text"
                  },
                  "age": {
                    "type": "byte",
                    "index": false
                  }
                }
              }
            }
            """;


    @AfterEach
    void closeClient() throws IOException {
        if (client != null)
            client.close();
    }
}
