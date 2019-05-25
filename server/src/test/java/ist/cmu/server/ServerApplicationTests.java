package ist.cmu.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerApplicationTests {

    public String login() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "mihail");
        map.add("password", "admin");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        System.out.println(entity.toString());
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080" + "/login", HttpMethod.POST, entity, String.class);
        return response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split(";")[0];

    }


    @Test
    public void addUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "mihailno98vo");
        map.add("password", "admin1");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        System.out.println(entity);
        restTemplate.exchange("http://localhost:8080/addUser", HttpMethod.POST, entity, Void.class);
    }

    @Test
    public void createAlbum() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", login());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "mihailnovo");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        restTemplate.exchange("http://localhost:8080/cmu/createAlbum", HttpMethod.POST, entity, Void.class);
    }

    @Test
    public void getUserAlbums() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", login());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/cmu/getUserAlbums", HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());

    }

    @Test
    public void addPhotoToAlbum() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", login());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "fotos");
        map.add("url", "www.instagram.com");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        System.out.println(entity);
        restTemplate.exchange("http://localhost:8080/cmu/addPhotoToAlbum", HttpMethod.POST, entity, Void.class);
    }

    @Test
    public void getUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", login());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        System.out.println(restTemplate.exchange("http://localhost:8080/cmu/users/mihail", HttpMethod.GET, entity, String.class));
    }
    @Test
    public void logout() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", login());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        System.out.println(entity);
        System.out.println(restTemplate.exchange("http://localhost:8080/logout", HttpMethod.POST, entity, String.class));
    }

}

