// CompanyController.java
package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CompanyDto;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateCompanyRequest;
import vn.edu.fpt.HRVinaPortal.services.recruitment.CompanyService;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/companies", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDto> create(@Valid @RequestBody CreateOrUpdateCompanyRequest req) {
        CompanyDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDto> update(@PathVariable Integer id,
                                             @Valid @RequestBody CreateOrUpdateCompanyRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
