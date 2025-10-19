// src/main/java/vn/edu/fpt/HRVinaPortal/dto/recruitment/JobFiltersDto.java
package vn.edu.fpt.HRVinaPortal.dto.recruitment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class JobFiltersDto {
    private List<JobTitleCompanyDto> titles;
    private List<CompanyDto> companies;
    private List<String> locations;
    private List<String> workTypes;
}
