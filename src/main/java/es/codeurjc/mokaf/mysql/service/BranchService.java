package es.codeurjc.mokaf.mysql.service;

import es.codeurjc.mokaf.mysql.model.Branch;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("mysqlBranchService")
public class BranchService {

    private final List<Branch> branches = new ArrayList<>();

    public BranchService() {
        // Mock data
        Branch mainBranch = new Branch("Mokaf Madrid", "Calle del Café, 123, 28001 Madrid, España",
                new BigDecimal("0.0"));
        // Temporarily using description to store full address for similarity with
        // previous hardcoded strings
        // Ideally Branch entity should have structured address fields or we parse it
        branches.add(mainBranch);
    }

    public Branch getMainBranch() {
        if (branches.isEmpty()) {
            return new Branch("Default Branch", "Unknown Location", BigDecimal.ZERO);
        }
        return branches.get(0);
    }

    public String[] getOpeningHours() {
        return new String[] {
                "Lunes a Viernes: 7:00 - 22:00",
                "Sábados: 8:00 - 23:00",
                "Domingos: 9:00 - 21:00"
        };
    }

    public String getMapUrl() {
        return "https://maps.google.com/maps?q=Calle+del+Café,+123,+Madrid&t=&z=15&ie=UTF8&iwloc=&output=embed";
    }

    public String getLocationName() {
        return "Mokaf Café de Especialidad";
    }

    public String getMetroInfo() {
        return "Metro: Sol (Líneas 1, 2, 3)";
    }

    public String getBusInfo() {
        return "Autobuses: 3, 5, 9, 15, 20, 51, 52, 53";
    }

    public record FAQ(String id, String question, String answer) {
    }

    public List<FAQ> getFAQs() {
        List<FAQ> faqs = new ArrayList<>();
        faqs.add(new FAQ("faq1", "¿Hacen envíos a domicilio?",
                "Sí, realizamos envíos a través de las principales plataformas de delivery y también tenemos servicio propio en la zona centro."));
        faqs.add(new FAQ("faq2", "¿Tienen opciones sin gluten?",
                "¡Por supuesto! Contamos con una amplia variedad de postres y panes sin gluten, además de leches vegetales certificadas."));
        faqs.add(new FAQ("faq3", "¿Puedo reservar una mesa?",
                "Sí, aceptamos reservas para grupos de más de 4 personas. Para grupos menores, atendemos por orden de llegada."));
        faqs.add(new FAQ("faq4", "¿Venden granos de café?",
                "Sí, vendemos todos nuestros orígenes en grano o molidos al momento según tu cafetera."));
        return faqs;
    }

    public String getContactPhone() {
        return "+34 910 123 456";
    }

    public String getContactEmail() {
        return "info@mokaf.com";
    }

    public String getContactSupportHigh() {
        return "Atención al cliente: 24/7";
    }
}
