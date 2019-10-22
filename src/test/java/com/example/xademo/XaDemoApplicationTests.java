package com.example.xademo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = XaDemoApplication.class, webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT )
/**
 * Have run out of time and ideas on this one. Committing incomplete stuff but the general idea is there
 */
class XaDemoApplicationTests {

	String [] pets = {"Dora","Mora","No-ra"}; //No-ra doesn't make it, sometimes..
	private final String SERVER_ROOT = "http://localhost:8080/";
	@Test
	void withJTAEnabled() throws IOException {
		post("kumcat");
		String st = get("pets");


	}

	private String get(String pets) throws IOException {
		HttpUriRequest request = new HttpGet( SERVER_ROOT + "pets" );
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
		return EntityUtils.toString(httpResponse.getEntity());
	}

	private void post(String data) throws IOException {
		 post(data,false);
	}

	private void post(String data, boolean rollback) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		String server = SERVER_ROOT;
		if(rollback){
			server += "?rollback=true";
		}
		HttpPost httpPost = new HttpPost(server);
		String json = "{\"name\":\"kumcat\"}";
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = client.execute(httpPost);
	}

}
