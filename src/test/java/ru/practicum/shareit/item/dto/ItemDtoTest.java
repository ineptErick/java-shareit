package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemReplyDto> json;

    @Test
    public void testSerializeDto() throws Exception {
        ItemReplyDto dto = new ItemReplyDto();
        dto.setId(1L);
        dto.setName("Test Item1");

        String jsonOutput = json.write(dto).getJson();
        String expectedJson = "{\"id\":1,\"name\":\"Test Item1\",\"description\":null,\"available\":null," +
                "\"lastBooking\":null,\"nextBooking\":null,\"comments\":null,\"requestId\":null}";

        assertThat(jsonOutput).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserializeDto() throws Exception {
        String jsonInput = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@test.com\"}";
        ItemReplyDto expectedDto = new ItemReplyDto();
        expectedDto.setId(1L);
        expectedDto.setName("Test User");

        ItemReplyDto dto = json.parse(jsonInput).getObject();
// ItemDtoTest.testDeserializeDto:46
        assertThat(dto).isEqualTo(expectedDto);
    }

    @Test
    public void testInvalidDto() {
        ItemReplyDto item = new ItemReplyDto();
        item.setName("");
        item.setDescription("");
        item.setAvailable(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ItemReplyDto>> violations = validator.validate(item);

        assertEquals(3, violations.size());
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("name must not be blank"))
        );
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("description must not be blank"))
        );
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("available must not be null"))
        );
    }
}