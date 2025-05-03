package com.masai.formbuilder.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {
	private Long formId;
    private Long totalResponses;
    private Map<String, Map<String, Long>> categoricalSummary;
    private Map<String, Map<String, Long>> numericSummary;
    private Map<String, Long> textSummary;  
}
