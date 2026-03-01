package es.codeurjc.mokaf.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.mockito.ArgumentMatchers.any;
import es.codeurjc.mokaf.service.ProductService;
import es.codeurjc.mokaf.service.AllergenService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;

@WebMvcTest(controllers = MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "applicationProductService")
    private ProductService productService;

    @MockitoBean
    private AllergenService allergenService;

    @Test
    @WithMockUser
    void testShowMenu() throws Exception {
        Product p1 = new Product("Expreso", "Desc", null, new BigDecimal("2.5"), Category.HOT);
        p1.setId(1L);
        Product p2 = new Product("Latte", "Desc", null, new BigDecimal("3.5"), Category.HOT);
        p2.setId(2L);
        List<Product> products = Arrays.asList(p1, p2);
        Page<Product> pagedProducts = new PageImpl<>(products);

        when(productService.getProductsPage(0, 6)).thenReturn(pagedProducts);
        when(productService.getBestSellingProducts(4)).thenReturn(products);
        when(allergenService.getAllAllergens()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", products))
                .andExpect(model().attribute("title", "Menú"))
                .andExpect(model().attribute("currentPage", "menu"));
    }
}
