package club.facecard.ipayproxy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
public class IPayProxyController {

    @Value("${ipay_url}")
    private  String IPAY_URL;

    private RestTemplate rest = new RestTemplate();

    @PostMapping
    public HttpEntity<?> createPayment(@RequestBody String jsonBody) throws IOException {
        HttpEntity<String> body = new HttpEntity<>(jsonBody);
        ResponseEntity<String> stringResponseEntity = rest.exchange(IPAY_URL, HttpMethod.POST, body, String.class);
        return stringResponseEntity;
    }

    @PostMapping("/cards")
    public HttpEntity<String> getCards(@RequestBody String jsonBody) throws IOException {
        HttpEntity<String> body = new HttpEntity<>(jsonBody);
        ResponseEntity<String> stringResponseEntity = rest.exchange(IPAY_URL, HttpMethod.POST, body, String.class);
        return stringResponseEntity;
    }
}
