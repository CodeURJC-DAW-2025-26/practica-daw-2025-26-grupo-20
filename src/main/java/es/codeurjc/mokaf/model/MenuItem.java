package es.codeurjc.mokaf.model;

public class MenuItem {

    private String name;
    private String description;
    private String price;
    private String image;
    private String category;

    // Constructor
    public MenuItem(String name, String description, String price, String image, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getImage() { return image; }
    public String getCategory() { return category; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
    public void setCategory(String category) { this.category = category; }

    // ToString para mejor depuración
    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}