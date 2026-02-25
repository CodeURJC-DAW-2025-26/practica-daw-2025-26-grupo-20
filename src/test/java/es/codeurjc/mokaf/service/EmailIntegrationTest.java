package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
public class EmailIntegrationTest {

    @Autowired
    private OrderEmailService orderEmailService;

    @Test
    public void testSendOrderEmailDirectly() {
        // 1. Crear un Usuario falso para el envío
        User fakeUser = new User();
        fakeUser.setId(999L);
        fakeUser.setName("Cliente de Prueba");
        // PON AQUÍ TU CORREO PERSONAL PARA RECIBIR LA PRUEBA (o déjalo así para
        // mandarlo al correo de la tienda)
        fakeUser.setEmail("mokafcoffee@gmail.com");

        // 2. Crear una Sucursal falsa
        Branch fakeBranch = new Branch();
        fakeBranch.setId(999L);
        fakeBranch.setName("Local Mokaf Central");

        // 3. Crear una Orden falsa
        Order fakeOrder = new Order();
        fakeOrder.setUser(fakeUser);
        fakeOrder.setBranch(fakeBranch);
        fakeOrder.setStatus(Order.Status.PAID);
        fakeOrder.setPaidAt(LocalDateTime.now());
        fakeOrder.setSubtotalAmount(new BigDecimal("12.50"));
        fakeOrder.setTotalAmount(new BigDecimal("12.50"));

        // Al no tener ID porque no está en BD, le intentamos poner uno de alguna manera
        // pero JPA lo inyecta luego, así que usaremos reflection para saltarnos esto y
        // no complicarlo
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(fakeOrder, 8888L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Crear un Producto e Item falso
        Product fakeProduct = new Product();
        fakeProduct.setName("Café Mocha XXL");

        OrderItem fakeItem = new OrderItem(fakeProduct, 2, new BigDecimal("6.25"), new BigDecimal("6.25"),
                new BigDecimal("12.50"));
        fakeItem.setOrder(fakeOrder);
        fakeOrder.addItem(fakeItem);

        // 5. ¡Llamamos al servicio real para que genere PDF y envíe correo!
        System.out.println("Intentando enviar correo PDF de prueba a: " + fakeUser.getEmail());
        orderEmailService.sendOrderConfirmationWithPdf(fakeOrder);
        System.out.println("Envío completado. ¡Revisa tu bandeja de entrada!");
    }
}
