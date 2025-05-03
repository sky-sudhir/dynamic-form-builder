package com.masai.formbuilder.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masai.formbuilder.dto.AnalyticsDto;
import com.masai.formbuilder.dto.UserResponseDto;
import com.masai.formbuilder.enums.FieldType;
import com.masai.formbuilder.exception.BadRequestException;
import com.masai.formbuilder.model.Field;
import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.model.Response;
import com.masai.formbuilder.model.User;
import com.masai.formbuilder.repository.FieldRepository;
import com.masai.formbuilder.repository.FormRepository;
import com.masai.formbuilder.repository.ResponseRepository;


@Service
public class DashboardService {
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private contextService contextService;
    @Autowired
    private FieldRepository fieldRepository;

    public AnalyticsDto getAnalytics(Long formId) {
        Form form = formRepository.findById(formId).orElseThrow();
        User user = contextService.getCurrentUser();
        
        if (!form.getCreator().getEmail().equals(user.getEmail())) {
            throw new BadRequestException("Unauthorized access to form analytics");
        }

        List<Response> responses = responseRepository.findByFormId(formId);
        AnalyticsDto dto = new AnalyticsDto();
        dto.setFormId(formId);
        dto.setTotalResponses((long) responses.size());

        Map<String, Map<String, Long>> categoricalSummary = new HashMap<>();
        Map<String, Map<String, Long>> numericSummary = new HashMap<>();
        
        Map<String, Long> textSummary = new HashMap<>();
        Set<String> stopwords = Set.of("the", "is", "a", "an", "and", "to", "of", "it", "in", "on", "for", "with");

        for (Response response : responses) {
            Map<String, Object> answerMap;
            try {
                answerMap = new ObjectMapper().readValue(response.getAnswersJson(), Map.class);
            } catch (JsonProcessingException e) {
                continue;
            }

            for (Map.Entry<String, Object> entry : answerMap.entrySet()) {
                String key = entry.getKey();
                Field field = fieldRepository.findByFieldId(key);
                              
                Object value = entry.getValue();
                if (field.getType() == FieldType.NUMBER) {
                	int numValue;
                    try {
                        numValue = Integer.parseInt(String.valueOf(value));
                    } catch (NumberFormatException e) {
                        continue; // skip invalid number
                    }
                	String rangeKey = getRangeKey(numValue, 10); // grouping by 10 (e.g., 0–9, 10–19)

                	numericSummary.computeIfAbsent(field.getLabel(), k -> new HashMap<>())
                	    .merge(rangeKey, 1L, Long::sum);
                } else {
                    String valStr = String.valueOf(value);
                    categoricalSummary.computeIfAbsent(field.getLabel(), k -> new HashMap<>())
                        .merge(valStr, 1L, Long::sum);
                    
                    
                    if (field.getType() == FieldType.TEXT) {
                    	String text = String.valueOf(value).toLowerCase();
                        String[] words = text.split("\\W+");

                        for (String word : words) {
                            if (!stopwords.contains(word) && word.length() > 1) {
                                textSummary.merge(word, 1L, Long::sum);
                            }
                        }
                    }
                }
            }
        }
        dto.setCategoricalSummary(!categoricalSummary.isEmpty() ? categoricalSummary : null);
        dto.setNumericSummary(!numericSummary.isEmpty() ? numericSummary : null);
        dto.setTextSummary(!textSummary.isEmpty() ? textSummary : null); 
        return dto;
    }
    
    public ByteArrayInputStream exportResponsesAsExcel(Long formId) throws IOException {
        Form form = formRepository.findById(formId).orElseThrow();
        User user = contextService.getCurrentUser();
        
        if (!form.getCreator().getEmail().equals(user.getEmail())) {
            throw new BadRequestException("Unauthorized access to form analytics");
        }

        List<Response> responses = responseRepository.findByFormId(formId);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Responses");

            if (!responses.isEmpty()) {
                Map<String, Object> firstAnswers = new ObjectMapper().readValue(responses.get(0).getAnswersJson(), Map.class);

                // ✅ Header Style (bold + colored background)
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont font = ((XSSFWorkbook) workbook).createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // ✅ Write header row
                Row headerRow = sheet.createRow(0);
                int col = 0;
                for (String header : firstAnswers.keySet()) {
                    Cell cell = headerRow.createCell(col++);
                    Field field = fieldRepository.findByFieldId(header);
                    
                    cell.setCellValue(field.getLabel());
                    cell.setCellStyle(headerStyle);
                }

                // ✅ Write data rows
                int rowNum = 1;
                for (Response response : responses) {
                    Map<String, Object> answers = new ObjectMapper().readValue(response.getAnswersJson(), Map.class);
                    Row row = sheet.createRow(rowNum++);
                    int cellNum = 0;
                    for (Object value : answers.values()) {
                        row.createCell(cellNum++).setCellValue(String.valueOf(value));
                    }
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


    public void notifyWebhook(Response response, String webhookUrl) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(response.getAnswersJson(), headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch (Exception e) {
            // Log failure but don't block response saving
            System.err.println("Webhook POST failed: " + e.getMessage());
        }
    }
    public List<UserResponseDto> getUserResponses(Long formId) {
        Form form = formRepository.findById(formId).orElseThrow();


            List<Response> responses = responseRepository.findByFormId(form.getId());

            List<UserResponseDto> userResponses = responses.stream()
                .map(response -> {
                    Map<String, Object> parsedAnswers = parseJson(response.getAnswersJson());

                    Map<String, Object> labeledAnswers = new HashMap<>();
                    parsedAnswers.forEach((fieldId, value) -> {
                    	Field field = fieldRepository.findByFieldId(fieldId);
                        if (field != null) {
                            labeledAnswers.put(field.getLabel(), value);
                        }
                    });
                    return new UserResponseDto(
                        response.getEmail(),
                        response.getIpAddress(),
                        response.getSubmittedAt().toString(),
                        labeledAnswers
                    );
                }).collect(Collectors.toList());
            
            return userResponses;
    }
    
    private Map<String, Object> parseJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON format in answersJson", e);
        }
    }
    
    private String getRangeKey(int value, int interval) {
        int lower = (value / interval) * interval;
        int upper = lower + interval - 1;
        return lower + "–" + upper;
    }

}

