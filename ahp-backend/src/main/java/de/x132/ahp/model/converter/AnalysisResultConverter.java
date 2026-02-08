package de.x132.ahp.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.x132.ahp.model.json.AnalysisResult;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class AnalysisResultConverter implements AttributeConverter<AnalysisResult, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(AnalysisResult attribute) {
    if (attribute == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      log.error("Error converting AnalysisResult to JSON", e);
      throw new IllegalArgumentException("Error converting AnalysisResult to JSON", e);
    }
  }

  @Override
  public AnalysisResult convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.readValue(dbData, AnalysisResult.class);
    } catch (JsonProcessingException e) {
      log.error("Error converting JSON to AnalysisResult", e);
      // In case of error (e.g. legacy data mismatch), we might return null or empty
      // object,
      // but throwing helps identify data issues.
      throw new IllegalArgumentException("Error converting JSON to AnalysisResult", e);
    }
  }
}
