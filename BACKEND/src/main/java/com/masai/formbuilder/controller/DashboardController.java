package com.masai.formbuilder.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masai.formbuilder.dto.AnalyticsDto;
import com.masai.formbuilder.dto.MessageResponse;
import com.masai.formbuilder.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/analytics/{formId}")
    public ResponseEntity<?> getAnalytics(@PathVariable Long formId) {
        AnalyticsDto dto = dashboardService.getAnalytics(formId);
        return ResponseEntity.ok(new MessageResponse("", dto));
    }
    
    @GetMapping("/export/excel/{formId}")
    public ResponseEntity<InputStreamResource> exportExcel(@PathVariable Long formId) throws IOException {
        ByteArrayInputStream stream = dashboardService.exportResponsesAsExcel(formId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=form_responses.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(new InputStreamResource(stream));
    }
    
    @GetMapping("/forms/{formId}/user-responses")
    public ResponseEntity<MessageResponse> getUserResponses(@PathVariable Long formId) {
    	
    	return ResponseEntity.ok(new MessageResponse("", dashboardService.getUserResponses(formId)));
    }

}
