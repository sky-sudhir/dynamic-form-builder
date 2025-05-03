package com.masai.formbuilder.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.masai.formbuilder.dto.FieldOptionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldOption {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;
    
    private String label;
    
    @ManyToOne
    private Field field;
       
    public static FieldOption mapToResponse(FieldOptionDto fod, Field f) {
    	return FieldOption.builder()
    			.value(fod.getValue())
    			.label(fod.getLabel())
    			.field(f)
    			.build();
    }
}
