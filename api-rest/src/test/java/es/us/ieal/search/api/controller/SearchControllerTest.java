package es.us.ieal.search.api.controller;

import es.us.ieal.search.api.exception.ApiExceptionHandler;
import es.us.ieal.search.domain.entity.SearchPage;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest {
    private ContentUseCase useCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        useCase = mock(ContentUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SearchController(useCase))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void devuelveBusquedaVaciaConFormatoPaginado() throws Exception {
        when(useCase.search(any(), any())).thenReturn(new SearchPage(0, 0, 20, List.of()));

        mockMvc.perform(get("/api/v1/search").param("q", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void rechazaConsultaVacia() throws Exception {
        mockMvc.perform(get("/api/v1/search").param("q", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rechazaConsultaAusente() throws Exception {
        mockMvc.perform(get("/api/v1/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rechazaPaginacionInvalida() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "america")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
