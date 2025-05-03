package com.masai.formbuilder.dto;

import com.masai.formbuilder.model.FieldOption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldOptionDto {
	private Long id;
	private String value;
	private String label;
	
	public static FieldOptionDto mapToResponse(FieldOption fo) {
		return FieldOptionDto.builder()
				.id(fo.getId())
				.value(fo.getValue())
				.label(fo.getLabel())
				.build();
	}
}
