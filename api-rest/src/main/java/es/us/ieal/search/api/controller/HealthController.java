package es.us.ieal.search.api.controller;

import es.us.ieal.search.generated.api.HealthApi;
import es.us.ieal.search.generated.model.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements HealthApi {
    @Override
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = new HealthResponse();
        response.setStatus("UP");
        response.setService("servicio-buscador-ieal");
        response.setVersion("0.1.0");
        return ResponseEntity.ok(response);
    }
}
