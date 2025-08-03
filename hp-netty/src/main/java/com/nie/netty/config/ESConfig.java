package com.nie.netty.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nie.feign.dto.MessageDTO;
import com.nie.netty.pojo.HitsData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;

@Slf4j
//@Component
public class ESConfig {
    private static final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.142.133:9200")
    ));

    static {
        // 添加 JVM 关闭钩子，在应用关闭时释放 Elasticsearch 客户端资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
                System.out.println("Es client closed.");
            } catch (Exception e) {
                log.info("Failed to close : " + e.getMessage());
            }
        }));
    }

    private final String MAPPING = """
            {
              "mappings": {
                "properties": {
                "msgId": {
                    "type": "keyword"
                  },
                  "userId": {
                    "type": "keyword"
                  },
                  "content": {
                    "type": "text"
                  },
                  "sendTime": {
                    "type": "keyword"            
                  }
                }
              }
            }
            """;

    private final String AI_MAPPING = """
            {
              "mappings": {
                "properties": {
                "role": {
                    "type": "keyword"
                  },
                  "content": {
                    "type": "text"
                  },
                  "sendTime": {
                    "type": "keyword"            
                  }
                }
              }
            }
            """;

    private ObjectMapper objectMapper = ObjectMapperConfig.createObjectMapper();

    public void createDoc(RestHighLevelClient client, String userId, String msg, String msgId) throws Exception {
        try {
            final IndexRequest request = new IndexRequest("chatrecords_" + userId).id(msgId);
            request.source(msg, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.info("create doc Failed : " + e.getMessage());
        }
    }

    public HitsData searchWithPrecise(RestHighLevelClient client, String userId) throws Exception {
        final SearchRequest request = new SearchRequest("chatrecords_" + userId);
        request.source().query(QueryBuilders.matchQuery("userId", userId));
        request.source().sort("sendTime", SortOrder.DESC);
        request.source().highlighter(SearchSourceBuilder.highlight().field("content").preTags("<em>").postTags("</em>"));
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        long totalHit = response.getHits().getTotalHits().value;
        SearchHit[] hits = response.getHits().getHits();
        HitsData result = new HitsData();
        ArrayList<MessageDTO> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            MessageDTO messageDTO = objectMapper.readValue(source, MessageDTO.class);
            list.add(messageDTO);
        }

        result.setTotal(totalHit);
        result.setHits(list);
        return result;
    }

    public void initES(String userId) {
        try {

            log.info("ES initing.......");

            boolean exists = client.indices().exists(new GetIndexRequest("chatrecords_" + userId), RequestOptions.DEFAULT);
            if (!exists) {
                final CreateIndexRequest request = new CreateIndexRequest("chatrecords_" + userId);
                request.source(MAPPING, XContentType.JSON);
                client.indices().create(request, RequestOptions.DEFAULT);
                log.info("Index chatrecords_{} created successfully.", userId);
            } else {
                log.info("Index chatrecords_{} already exists.", userId);
            }
        } catch (Exception e) {
            log.info("Failed : " + e.getMessage());
        }
    }

    public static RestHighLevelClient getClient() {
        return client;
    }


    // @PreDestroy
    public void closeES() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    public String deleteRecords(RestHighLevelClient client, String userId) {
        final DeleteIndexRequest request = new DeleteIndexRequest("chatrecords_" + userId);
        try {
            client.indices().delete(request, RequestOptions.DEFAULT);
            return "delete success";
        } catch (Exception e) {
            log.info("delete failed : " + e.getMessage());
            return "delete failed";
        }
    }

    public String deleteRecords2(RestHighLevelClient client, String userId) {
        final DeleteByQueryRequest request = new DeleteByQueryRequest("chatrecords_" + userId);
        try {
            request.setQuery(QueryBuilders.matchAllQuery());
            client.deleteByQuery(request, RequestOptions.DEFAULT);
            return "delete success";
        } catch (Exception e) {
            log.info("delete failed : " + e.getMessage());
            return "delete failed";
        }
    }

}
