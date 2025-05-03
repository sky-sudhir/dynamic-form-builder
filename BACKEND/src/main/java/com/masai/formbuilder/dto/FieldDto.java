package com.masai.formbuilder.dto;

import java.util.ArrayList;
import java.util.List;

import com.masai.formbuilder.enums.FieldType;
import com.masai.formbuilder.model.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto {
	private String id;
    private String label;
    private String placeHolder;
    private FieldType type;
    private List<FieldOptionDto> options = new ArrayList<>();
    private boolean required;
    private String conditionalFieldId;
    private String conditionalFieldValue;
    private Integer step;
    
    public FieldDto(Field field) {
        this.id = field.getFieldId();
        this.label = field.getLabel();
        this.placeHolder = field.getPlaceHolder();
        this.options = field.getOptions().stream().map(FieldOptionDto::mapToResponse).toList();
        this.type = field.getType();
        this.required = field.isRequired();
        this.conditionalFieldId = field.getConditionalFieldId();
        this.conditionalFieldValue = field.getConditionalFieldValue();
        this.step = field.getStep();
    }
}
