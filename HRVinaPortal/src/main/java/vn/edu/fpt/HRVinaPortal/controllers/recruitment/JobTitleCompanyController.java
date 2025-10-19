// JobTitleCompanyController.java
package vn.edu.fpt.HRVinaPortal.controllers.recruitment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.CreateOrUpdateJobTitleCompanyRequest;
import vn.edu.fpt.HRVinaPortal.dto.recruitment.JobTitleCompanyDto;
import vn.edu.fpt.HRVinaPortal.services.recruitment.JobTitleCompanyService;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/job-title-company", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class JobTitleCompanyController {

    private final JobTitleCompanyService service;

    @GetMapping
    public ResponseEntity<List<JobTitleCompanyDto>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobTitleCompanyDto> one(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<JobTitleCompanyDto>> byCompany(@PathVariable Integer companyId) {
        return ResponseEntity.ok(service.getByCompany(companyId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobTitleCompanyDto> create(@Valid @RequestBody CreateOrUpdateJobTitleCompanyRequest req) {
        JobTitleCompanyDto created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobTitleCompanyDto> update(@PathVariable Integer id,
                                                     @Valid @RequestBody CreateOrUpdateJobTitleCompanyRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
