package ch.so.agi.ili2gpkg;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

@MicronautTest
public class MainControllerTest {

    @Inject
    EmbeddedServer embeddedServer;

    @Test
    public void testIndex() throws Exception {
        try(RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient.class, embeddedServer.getURL())) {
            assertEquals(HttpStatus.OK, client.toBlocking().exchange("/ili2gpkg").status());
        }
    }
    
    @Test
    public void validation_Ok_Raw() throws Exception {
        try(HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {

            final MultipartBody body = MultipartBody.builder()
                    .addPart("file", new File("src/test/data/254900.itf"))
                    .build();
            
            HttpResponse response = client.toBlocking().exchange(
                    HttpRequest.POST("/ili2gpkg", body)
                            .contentType(MediaType.MULTIPART_FORM_DATA_TYPE), File.class);
            
            Map<String, List<String>> headers = response.getHeaders().asMap();
            System.out.println(headers);

            assertTrue(headers.get("content-disposition").get(0).equalsIgnoreCase("attachment; filename=254900.gpkg"));
            assertTrue(headers.get("Content-Type").get(0).equalsIgnoreCase("application/octet-stream"));
            assertTrue(Integer.valueOf(headers.get("Content-Length").get(0).split(",")[0]) > 1720000);          
        }
    }
    
    @Test
    public void validation_Ok_Zip() throws Exception {
        try(HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {

            final MultipartBody body = MultipartBody.builder()
                    .addPart("file", new File("src/test/data/LRO.zip"))
                    .build();
            
            HttpResponse response = client.toBlocking().exchange(
                    HttpRequest.POST("/ili2gpkg", body)
                            .contentType(MediaType.MULTIPART_FORM_DATA_TYPE), File.class);
            
            Map<String, List<String>> headers = response.getHeaders().asMap();
            System.out.println(headers);
            
            assertTrue(headers.get("content-disposition").get(0).equalsIgnoreCase("attachment; filename=LRO.gpkg"));
            assertTrue(headers.get("Content-Type").get(0).equalsIgnoreCase("application/octet-stream"));
            assertTrue(Integer.valueOf(headers.get("Content-Length").get(0).split(",")[0]) > 1780000);          
        }
    }
}
