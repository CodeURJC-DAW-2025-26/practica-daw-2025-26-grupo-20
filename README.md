# Mokaf

## 👥 Miembros del Equipo

| Nombre y Apellidos            | Correo URJC                     | Usuario GitHub |
| :---------------------------- | :------------------------------ | :------------- |
| Gonzalo Pérez Roca            | g.perezr.2019@alumnos.urjc.es   | gonzaloperz    |
| Jordi Guix Betancor           | j.guix.2023@alumnos.urjc.es     | Jordigb44      |
| Guillermo Blázquez Barbacid   | g.blazquez.2019@alumnos.urjc.es | Blazk0o        |
| Elinee Nathalie Freites Muñoz | en.freites.2022@alumnos.urjc.es | ElineeF        |
| Alexandra Cararus Verdes      | a.cararus.2021@alumnos.urjc.es  | alexandraaCS   |

---

## 🎭 **Preparación 1: Definición del Proyecto**

### **Descripción del Tema**

Mokaf es una aplicación web diseñada para una cafetería de especialidad. Permite a los usuarios consultar la carta de cafés, bebidas frías, postres y más. Los usuarios pueden registrarse para acceder a funcionalidades personalizadas, mientras que los administradores tienen herramientas para gestionar el catálogo de productos y visualizar estadísticas de ventas.

### **Entidades**

Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **Usuario (User)**: Entidad base que representa a cualquier usuario registrado en el sistema.
2. **Cliente (Customer)**: Extiende de Usuario. Representa a los clientes de la cafetería. Incluye información de envío.
3. **Administrador (Admin)**: Extiende de Usuario. Representa al personal con permisos de gestión. Tiene un ID de empleado.
4. **Producto (Product)**: Representa los artículos del menú (cafés, postres, etc.).
5. **Pedido (Order)**: (Planificado) Representa la compra realizada por un cliente.

**Relaciones entre entidades:**

- **Herencia**: Cliente y Administrador heredan de Usuario (Estrategia JOINED).
- **Usuario - Pedido**: Un clientes puede realizar múltiples pedidos (1:N).
- **Pedido - Producto**: Un pedido puede contener varios productos (N:M).
- **Producto - Categoría**: Los productos se categorizan (Hot, Cold, Blended, Desserts, Non-Coffee).

### **Permisos de los Usuarios**

Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

- **Usuario Anónimo**:
  - Permisos: Visualización de la página de inicio, menú completo, página "Sobre Nosotros" y contacto. Acceso a Login y Registro.
  - No es dueño de ninguna entidad.

- **Usuario Registrado (Cliente)**:
  - Permisos: Gestión de su perfil de usuario, visualización del carrito de compra.
  - Es dueño de: Su Perfil de Usuario, sus Pedidos.

- **Administrador**:
  - Permisos: Acceso al panel de estadísticas y gestión de productos.
  - Es dueño de: Gestión del catálogo de productos.

### **Imágenes**

Indicar qué entidades tendrán asociadas una o varias imágenes:

- **Producto**: Cada ítem del menú tiene una imagen representativa (ej. `Expreso.png`, `Croisants.png`).
- **Estadísticas**: El dashboard incluye visualizaciones gráficas pre-generadas.

### **Gráficos**

Indicar qué información se mostrará usando gráficos y de qué tipo serán:

- **Estadísticas de Ventas**: Gráfico de barras/líneas mostrando el rendimiento del negocio (visualizado actualmente como imagen estática en la sección de estadísticas).

### **Tecnología Complementaria**

Indicar qué tecnología complementaria se empleará:

- **Java 21**: Lenguaje de programación principal.
- **Spring Boot 3.5.6**: Framework para el backend.
- **Mustache**: Motor de plantillas para generar el HTML en el servidor.
- **Hibernate / JPA**: Para la persistencia de datos (MySQL).
- **Bootstrap 5.3.3**: Framework CSS para el diseño responsivo.
- **FontAwesome**: Para iconos.

### **Algoritmo o Consulta Avanzada**

Indicar cuál será el algoritmo o consulta avanzada que se implementará:

- **Algoritmo/Consulta**: Sistema de recomendación de productos (Planificado).
- **Descripción**: Sugerir productos basados en la categoría del último pedido del usuario.

---

## 🛠 **Preparación 2: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://youtu.be/uH2RzrRA0A4)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**

Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](./images/naviation-diagram.png)

> [Descripción del flujo de navegación:"Los usuarios no autenticados, al igual que a cualquier rol puede acceder a la página de inicio, ver el menú de productos disponibles, consultar las distintas sucursales, viistar la sección de Nosotros, página de contacto, acceso al login y registro y a las páginas de errores.]

> [Un usuario registrado, podrá comprar productos, añadir productos al carrito, verificar el resúmen de su pedido, acceder al su perfil, consultar el historial de pedidos y getsión de datos personales.]

> [Por último pero no menos importante, el rol de Administrador tendrá acceso a su propio perfil, gestión de productos (crear, eliminar y editar), visualizar estadísticas de las últimas ventas y tener una visión global del funcionamiento del negocio."]


## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

![Diagrama de Navegación](./src/main/resources/static/images/Screen_nav.png)

#### **Capturas de Pantalla Actualizadas**

#### **1. Página Principal / Home**

![Página Principal](images/Inicio.png)

### 2. Menu
![Menu](images/Menu1.png)

![Menu](images/Menu2.png)

![Menu](images/Menu3.png)

### 3. Nosotros

![Nosotros](images/Nosotros.png)

### 4. Sucursales
![Sucursales](images/Sucursales.png)

### 5. Contacto

![Contacto](images/Contacto.png)

### 6. Login/Registro

![Login](images/Login-Registro.png)

### 7. Producto

####  Usuario sin Login
![Producto](images/Producto1.png)

#### Usuario registrado
![Producto](images/ProductoUser.png)
![Producto](images/Producto2.png)
![Producto](images/Producto3.png)


### 8. Reseñas

![Reseña](images/ReviewsAdmin.png)
![Reseña](images/ReviewUser.png)

### 9. Perfil Usuario/Administrador

![Perfil](images/PerfilUser.png)

![Perfil](images/PerfilAdmin.png)

### 10. Gestion

![Gestion](images/Gestion-User.png)

![Gestion](images/Gestion-Productos.png)

### 11. Estadísticas

![Estadísticas](images/Estadísticas.png)

### 12. Pedidos

#### Historial pedidos User

![Pedidos](images/Pedidos.png)

#### Historico pedidos Administrador

![Pedidos](images/Historico-Pedidos.png)

### 13. Carrito Compra

![Carrito](images/Carrito.png)


### **Instrucciones de Ejecución**

#### **Requisitos Previos**

- **Java**: version 21 or higher.
- **Maven**: version 4.0 or higher.
- **MySQL**: version 8.0 or higher.
- **Git**: For cloning the repository
- **Spring Boot Dependencies**: Ensure that all necessary dependencies are included in the pom.xml file for the application.

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20.git
   cd practica-daw-2025-26-grupo-20
   ```
2. **Ejecutar MySQL workbench**:
   Abrir MySQL workbench y crear un schema con el nombre mokaf_db, para poder ver la base de datos. Ver que en el `application.properties`.

   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/mokaf_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Madrid
   spring.datasource.username=root
   spring.datasource.password=1234
   ```

3. **Ejecutar la aplicación**:
   ```bash
   mvn clean install -DSkipTest
   mvn spring-boot:run
   ```
4. **Acceder a la aplicación**:
   Abre tu navegador y ve a `http://localhost:8443`.

#### **Credenciales de prueba**

Actualmente hay de ambos tipos de usuarios en DatabaseInitializer; aconsejamos utilizar la cuenta de admin; `admin@mokaf.com` , `admin123` y puedes utilizar la opción de **Registro** en el menú para crear una cuenta nueva y probar las funcionalidades de usuario registrado.

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](./src/main/resources/static/images/diagramER.png)

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](./src/main/resources/static/images/MokafDiagram.drawio.png)


### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - Jordi Guix Betancor**

**Responsabilidades:**

- Implementación del sistema de notificaciones por correo electrónico, incluyendo la generación y envío de facturas en PDF tras la confirmación de pedidos.
- Desarrollo del algoritmo de recomendación dinámica de productos en el menú, personalizando sugerencias para usuarios registrados y filtrando por "best-sellers" para visitantes.
- Creación de un sistema de filtrado de categorías con paginación AJAX en el servidor para optimizar el rendimiento y la experiencia de usuario.
- Implementación de un formulario de contacto robusto con validación en el servidor y gestión de consultas vía email.
- Refuerzo de la seguridad en el registro de usuarios mediante la validación compleja de contraseñas y formatos de correo electrónico.

| Nº  | Commits                                                                                                                                                                                          | Files                                                                                                                                                                                                           |
| :-: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|  1  | [feat: Implement order confirmation emails with attached PDF invoices](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/bff00e073b448b49e29f0e21b63148ecdb6a3f713)   | [OrderEmailService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/bff00e073b448b49e29f0e21b63148ecdb6a3f713/src/main/java/es/codeurjc/mokaf/service/OrderEmailService.java)   |
|  2  | [feat: add dynamic product recommendations to the menu](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/ce85a6265c89b6b0e6d088db02f6bad70228325c)                   | [ProductService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/ce85a6265c89b6b0e6d088db02f6bad70228325c/src/main/java/es/codeurjc/mokaf/service/ProductService.java)          |
|  3  | [feat: Implement category filtering with server-side pagination and AJAX](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/d349bdb812e5bec224859a88c2b56f84614ae24c) | [MenuController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/d349bdb812e5bec224859a88c2b56f84614ae24c/src/main/java/es/codeurjc/mokaf/controller/MenuController.java)       |
|  4  | [feat: Implement a functional contact form with server-side validation](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/59ebd1f6e62c4f5db3d019e5abfefe12c4d41f6d)   | [ContactController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/59ebd1f6e62c4f5db3d019e5abfefe12c4d41f6d/src/main/java/es/codeurjc/mokaf/controller/ContactController.java) |
|  5  | [feat: Add email and password complexity validation to registration](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/e944a7042d130460bf887af4f4f5bbc219593bad)      | [AuthController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/e944a7042d130460bf887af4f4f5bbc219593bad/src/main/java/es/codeurjc/mokaf/controller/AuthController.java)       |

---

#### **Alumno 2 - [Gonzalo Pérez Roca]**

**Responsabilidades:**
- Implementación entidades de la base de datos y modelo Relacional.
- Siembra de datos en la base de datos.
- Implementación MVC de Productos y Reviews.
- Implementación de la paginación en las Review.
- Implementación de la Gestión de Productos(Admin).

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Add Models and Repository. NO esta terminado](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/29236a06f9004ad097a4d356d48b31ec5376d84f) | [pom.xml](pom.xml) |
|  2  | [Update DatabaseInitializer.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/6b98ae59f3704cb75f1479a8c83b5a140008136e) | [DatabaseInitializer.java](src/main/java/es/codeurjc/mokaf/config/DatabaseInitializer.java) |
|  3  | [ADMIN PRODUCTS](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/64586826e78f57c1be261da3fcc40189fb9d94ad) | [GestionController]( https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/controller/GestionController.java)|
|  4  | [Add Pageable-Review- NewReviewSeed](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/54ad14c35e70740f2a52a15f3cf0a2b6fbeaed39) | [ReviewService](URL_archivo_4) |
|  5  | [Static Reviews, present mustaches](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/f6fb74512965c00cee0abcb61a7c76303d022d31) | [Product.mustache](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/resources/templates/product.mustache) |

---

#### **Alumno 3 - Guillermo Blázquez Barbacid**

- Implementación de la seguridad de la aplicación.
- Implementación de la sesión de los usuarios.
- Implementación de los perfiles de usuario y administrador.
- Personalización y actualizacion de los perfiles.
- Implementación de la seguridad de las imágenes privadas.

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Seguridad de los usuarios y sus imagenes](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/841905dcd28c98bd737f2cb46cacca2a2bb6916e#diff-1b3e30b52d60a14864bdd58685318226b6fcf8ac4120bec12ca014926e610997) | [ImageSeervice.java](src/main/java/es/codeurjc/mokaf/service/ImageService.java) |
|  2  | [Creación de la sesiones permanentes por usuario](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/cb90e1ea4dd58b6deb247d4b53e15ed943b29fd6) | [AuthController](src/main/java/es/codeurjc/mokaf/controller/AuthController.java) |
|  3  | [Configuración de csrf y un debuger](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/2c70cfe6bd1ce0996295946bb0c970c631a09bc8) | [CsrfModelInterceptor](src/main/java/es/codeurjc/mokaf/config/CsrfModelInterceptor.java) |
|  4  | [Configuración de seguridad](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/3ca6c89b7b72b52028f4cd5ea15f78f97077588e) | [SecurityConfig](src/main/java/es/codeurjc/mokaf/config/SecurityConfig.java) |
|  5  | [Profile save changes and images for users](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/f20e83f0224173910d58e9793b4690e2c7a7b80e) | [ProfileController](src/main/java/es/codeurjc/mokaf/controller/ProfileController.java) |

---

#### **Alumno 4 - [Elinee Nathalie Freites Muñoz]**

- Implementación del carrito y compras.
- Implementación de estadísticas de administrador.
- Implementación de diagrama de clases y templates
- Implementación de descuento por sucursal
- Implementación de rating de productos mejor valorados.
- Implementación de ganancias totales por sucursal y sucursal destacada.

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Statistics and Branch fixed](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/22d30f9fe0580916bd537d7f085c1e985298cf67) | [StatisticsService](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/22d30f9fe0580916bd537d7f085c1e985298cf67#diff-30af0402e948daf26c29b0546234c002186891fa15400407f17a163ab88924b8) |
|  2  | [Cart impementation, braches discount and order create after paying.](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/30388e7e660e40436359d457fec96c0bad20c3d2) | [CartService](src/main/java/es/codeurjc/mokaf/service/CartService.java) |
|  3  | [Better Stats](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/cfb96db25a2603b589a9ef1e24367af25ec3568e) | [StatisticsService](src/main/java/es/codeurjc/mokaf/service/StatisticsService.java) |
|  4  | [DatabaseInit Changes and deleting Branch attribute](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/bd5d4b30c2a33163b50f9c2f74401f96f8489068) | [DatabaseInitializer](src/main/java/es/codeurjc/mokaf/config/DatabaseInitializer.java) |
|  5  | [Fixing controllers and paths](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/aeb02f7c3b67da06d9a0ff7fbfd9d947ee3ff4bb) | [MainController](src/main/java/es/codeurjc/mokaf/controller/MainController.java) |

---

#### **Alumno 5 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**

📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**

📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/complete-classes-diagram.png)

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**

- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)

#### **Pasos para ejecutar con docker-compose:**

1. **Clonar el repositorio** (si no lo has hecho ya):

   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   cd [repositorio]
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **Construcción de la Imagen Docker**

#### **Requisitos:**

- Docker instalado en el sistema

#### **Pasos para construir y publicar la imagen:**

1. **Navegar al directorio de Docker**:

   ```bash
   cd docker
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**

### **Despliegue en Máquina Virtual**

#### **Requisitos:**

- Acceso a la máquina virtual (SSH)
- Clave privada para autenticación
- Conexión a la red correspondiente o VPN configurada

#### **Pasos para desplegar:**

1. **Conectar a la máquina virtual**:

   ```bash
   ssh -i [ruta/a/clave.key] [usuario]@[IP-o-dominio-VM]
   ```

   Ejemplo:

   ```bash
   ssh -i ssh-keys/app.key vmuser@10.100.139.XXX
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **URL de la Aplicación Desplegada**

🌐 **URL de acceso**: `https://[nombre-app].etsii.urjc.es:8443`

#### **Credenciales de Usuarios de Ejemplo**

| Rol                | Usuario | Contraseña |
| :----------------- | :------ | :--------- |
| Administrador      | admin   | admin123   |
| Usuario Registrado | user1   | user123    |
| Usuario Registrado | user2   | user123    |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - Jordi Guix Betancor**

**Responsabilidades:**

- Implementation of the email notification system, including the generation and sending of PDF invoices after order confirmation.
- Development of the dynamic product recommendation algorithm in the menu, customizing suggestions for registered users and filtering by "best-sellers" for visitors.
- Creation of a category filtering system with AJAX paging on the server to optimize performance and user experience.
- Implementation of a robust contact form with server validation and email query management.
- Strengthening security in user registration through complex validation of passwords and email formats.

| Nº  | Commits                                                                                                                                                                                          | Files                                                                                                                                                                    |
| :-: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|  1  | [feat: Implement order confirmation emails with attached PDF invoices](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/pull/36)                                            | [OrderEmailService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/service/OrderEmailService.java) |
|  2  | [feat: add dynamic product recommendations to the menu](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/ce85a6265c89b6b0e6d088db02f6bad70228325c)                   | [ProductService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/service/ProductService.java)       |
|  3  | [feat: Implement category filtering with server-side pagination and AJAX](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/d349bdb812e5bec224859a88c2b56f84614ae24c) | [MenuController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/controller/MenuController.java)    |
|  4  | [feat: Implement a functional contact form with server-side validation](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/59ebd1f6e62c4f5db3d019e5abfefe12c4d41f6d)   | [MainController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/controller/MainController.java)    |
|  5  | [feat: Add email and password complexity validation to registration](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/e944a7042d130460bf887af4f4f5bbc219593bad)      | [AuthController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/main/src/main/java/es/codeurjc/mokaf/controller/AuthController.java)    |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](URL_del_video)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**

- **Node.js**: versión 18.x o superior
- **npm**: versión 9.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**

   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/)

   Verifica la instalación:

   ```bash
   node --version
   npm --version
   ```

2. **Clonar el repositorio** (si no lo has hecho ya)

   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

3. **Navegar a la carpeta del proyecto React**

   ```bash
   cd frontend
   ```

4. **AQUÍ LOS SIGUIENTES PASOS**

### **Diagrama de Clases y Templates de la SPA**

Diagrama mostrando los componentes React, hooks personalizados, servicios y sus relaciones:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - Jordi Guix Betancor**

**Responsabilidades:**

- Implementación del sistema de notificaciones por correo electrónico, incluyendo la generación y envío de facturas en PDF tras la confirmación de pedidos.
- Desarrollo del algoritmo de recomendación dinámica de productos en el menú, personalizando sugerencias para usuarios registrados y filtrando por "best-sellers" para visitantes.
- Creación de un sistema de filtrado de categorías con paginación AJAX en el servidor para optimizar el rendimiento y la experiencia de usuario.
- Implementación de un formulario de contacto robusto con validación en el servidor y gestión de consultas vía email.
- Refuerzo de la seguridad en el registro de usuarios mediante la validación compleja de contraseñas y formatos de correo electrónico.

| Nº  | Commits                                                                                                                                                                                          | Files                                                                                                                                                                                                           |
| :-: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|  1  | [feat: Implement order confirmation emails with attached PDF invoices](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/bff00e073b448b49e29f0e21b63148ecdb6a3f713)   | [OrderEmailService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/bff00e073b448b49e29f0e21b63148ecdb6a3f713/src/main/java/es/codeurjc/mokaf/service/OrderEmailService.java)   |
|  2  | [feat: add dynamic product recommendations to the menu](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/ce85a6265c89b6b0e6d088db02f6bad70228325c)                   | [ProductService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/ce85a6265c89b6b0e6d088db02f6bad70228325c/src/main/java/es/codeurjc/mokaf/service/ProductService.java)          |
|  3  | [feat: Implement category filtering with server-side pagination and AJAX](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/d349bdb812e5bec224859a88c2b56f84614ae24c) | [MenuController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/d349bdb812e5bec224859a88c2b56f84614ae24c/src/main/java/es/codeurjc/mokaf/controller/MenuController.java)       |
|  4  | [feat: Implement a functional contact form with server-side validation](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/59ebd1f6e62c4f5db3d019e5abfefe12c4d41f6d)   | [ContactController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/59ebd1f6e62c4f5db3d019e5abfefe12c4d41f6d/src/main/java/es/codeurjc/mokaf/controller/ContactController.java) |
|  5  | [feat: Add email and password complexity validation to registration](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/commit/e944a7042d130460bf887af4f4f5bbc219593bad)      | [AuthController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-20/blob/e944a7042d130460bf887af4f4f5bbc219593bad/src/main/java/es/codeurjc/mokaf/controller/AuthController.java)       |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº  |               Commits                |           Files           |
| :-: | :----------------------------------: | :-----------------------: |
|  1  | [Descripción commit 1](URL_commit_1) | [Archivo1](URL_archivo_1) |
|  2  | [Descripción commit 2](URL_commit_2) | [Archivo2](URL_archivo_2) |
|  3  | [Descripción commit 3](URL_commit_3) | [Archivo3](URL_archivo_3) |
|  4  | [Descripción commit 4](URL_commit_4) | [Archivo4](URL_archivo_4) |
|  5  | [Descripción commit 5](URL_commit_5) | [Archivo5](URL_archivo_5) |
