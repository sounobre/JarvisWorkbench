package com.dnobretech.jarvisworkbench.shared.error;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @Validated
    static class TestController {

        @GetMapping("/test-404")
        void throwNotFound(){
            throw new ResourceNotFoundException("Not Found");
        }

        @GetMapping("/test-500")
        void throwUnexpected(){
            throw new RuntimeException("Database password leaked: 123456");
        }

        @PostMapping("/test-validation")
        void testValidation(@Valid @RequestBody TestDto testDto){}

        @GetMapping("/test-constraint/{id}")
        void testConstraint(@Size(min = 10) @PathVariable String id){}

        @GetMapping("/test-business")
        void throwBusiness(){
            throw new BusinessException("Business rule violated");
        }

        @PostMapping("/test-invalid-request")
        void testInvalidRequest(@Valid @RequestBody TestDto testDto){}

    }
    record TestDto(@NotBlank(message = "Name is mandatory") String name) {}

    @Test
    void shouldHandleResourceNotFound() throws Exception {
        mockMvc.perform(get("/test-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/test-404"))
                .andExpect(jsonPath("$.fields").isEmpty());
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test-500"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected internal error"))
                .andExpect(jsonPath("$.path").value("/test-500"))
                .andExpect(jsonPath("$.fields").isEmpty())
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.message").value(not("Database password leaked: 123456")));
    }

    @Test
    void shouldHandleMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/test-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Invalid request data"))
                .andExpect(jsonPath("$.path").value("/test-validation"))
                .andExpect(jsonPath("$.fields[0].name").value("name"))
                .andExpect(jsonPath("$.fields[0].message").value("Name is mandatory"));
    }

    @Test
    void shouldHandleConstraintViolation() throws Exception {
        mockMvc.perform(get("/test-constraint/123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Invalid request parameters"))
                .andExpect(jsonPath("$.path").value("/test-constraint/123"))
                .andExpect(jsonPath("$.fields[0].name").value("id"))
                .andExpect(jsonPath("$.fields[0].message").value("size must be between 10 and 2147483647"));
    }

    @Test
    void shouldHandleBusinessException() throws Exception {
        mockMvc.perform(get("/test-business"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Business rule violated"))
                .andExpect(jsonPath("$.path").value("/test-business"))
                .andExpect(jsonPath("$.fields").isEmpty());

    }

    @Test
    void shouldHandleMessageNotReadable() throws Exception {
        mockMvc.perform(post("/test-invalid-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"sdf\" : }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed request body"))
                .andExpect(jsonPath("$.path").value("/test-invalid-request"))
                .andExpect(jsonPath("$.fields").isEmpty());

    }
}
