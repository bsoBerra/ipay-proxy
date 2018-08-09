package club.facecard.ipayproxy.controller;

import club.facecard.ipayproxy.dto.SlackRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@Slf4j
public class IPayProxyController {

    @Value("${ipay_url}")
    private String iPayUrl;

    @Value("${send_to_server}")
    private boolean sendToServer;

    @Value("{server_url}")
    private String serverUrl;

    @Value("${send_to_slack}")
    private boolean sendToSlack;

    @Value("{slack_url}")
    private String slackUrl;

    private RestTemplate rest = new RestTemplate();

    private ObjectMapper mapper = new ObjectMapper();

    @PostMapping
    public HttpEntity<?> createPayment(@RequestBody String jsonBody) throws IOException {
        return getHttpEntity(jsonBody, iPayUrl);
    }

    @PostMapping("/cards")
    public HttpEntity<?> getCards(@RequestBody String jsonBody) throws IOException {
        return getHttpEntity(jsonBody, iPayUrl);
    }

    @PostMapping("/success")
    public ResponseEntity getIPaySuccessResponse(@RequestBody String jsonBody) {
        log.info("IPay success callback request: {}", jsonBody);
        callback(jsonBody);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/error")
    public ResponseEntity getIPayErrorResponse(@RequestBody String jsonBody) {
        log.info("IPay error callback request: {}", jsonBody);
        callback(jsonBody);
        return ResponseEntity.ok().build();
    }

    private void callback(String jsonBody) {
        try {
            if (sendToServer) {
                getHttpEntity(jsonBody, serverUrl);
            }
            if (sendToSlack) {
                SlackRequest slackRequest = new SlackRequest();
                slackRequest.setText(jsonBody);
                String slackMessage = mapper.writeValueAsString(slackRequest);
                getHttpEntity(slackMessage, slackUrl);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<?> getHttpEntity(String jsonBody, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        log.info("jsonBody: {}", jsonBody);
        log.info("body: {}", body);
        ResponseEntity<String> stringResponseEntity = rest.exchange(url, HttpMethod.POST, body, String.class);
        log.info("response: {}", stringResponseEntity.getBody());
        log.info("stringResponseEntity: {}", stringResponseEntity);
        return stringResponseEntity;
    }

}
