package es.codeurjc.mokaf.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "order_items",
    indexes = {
        @Index(name = "idx_items_order", columnList = "order_id"),
        @Index(name = "idx_items_product", columnList = "product_id")
    }
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // NOT NULL. En BD, el FK puede tener ON DELETE CASCADE si luego usas schema SQL;
    // con Hibernate, la cascada se gestiona a nivel JPA desde Order->items.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "final_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalUnitPrice;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    public OrderItem() {}

    public OrderItem(Product product, int quantity, BigDecimal unitPrice, BigDecimal finalUnitPrice, BigDecimal lineTotal) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.finalUnitPrice = finalUnitPrice;
        this.lineTotal = lineTotal;
    }

    // Getters / Setters
    public Long getId() { return id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getFinalUnitPrice() { return finalUnitPrice; }
    public void setFinalUnitPrice(BigDecimal finalUnitPrice) { this.finalUnitPrice = finalUnitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    @Override
    public String toString() {
        return "OrderItem{id=" + id + ", quantity=" + quantity + "}";
    }
}
