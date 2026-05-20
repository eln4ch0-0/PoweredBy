🎮 Tienda de Juegos Digitales — Proyecto Microservicios
Plataforma de venta de videojuegos digitales donde el usuario obtiene propiedad permanente del juego al comprarlo, a diferencia de plataformas como Steam donde la compra es una licencia de uso revocable.

Proyecto académico — DSY1103 Desarrollo FullStack I — Duoc UC

👥 Equipo
Nombre	Apellido	Sección
Ignacio	Vera	003D
Ian	Avila	003D
📖 Descripción del Proyecto
Sistema distribuido para la venta de videojuegos digitales construido sobre una arquitectura de microservicios. El dominio modela una tienda en línea inspirada en plataformas conocidas, pero con una diferencia conceptual clave:

En otras Plataformas, el usuario adquiere una licencia revocable: la plataforma puede retirar el acceso al juego.
En esta plataforma, el usuario adquiere la propiedad permanente del juego: una vez comprado, el juego queda en su biblioteca de forma definitiva.
Esta diferencia conceptual impacta directamente en el modelo de datos: cada compra es un evento inmutable que congela el precio histórico y registra una entrada permanente en la biblioteca del usuario.

Funcionalidades implementadas en esta entrega
✅ Registro y administración de usuarios con billetera digital
✅ Catálogo de juegos con géneros y desarrolladores asociados
✅ Búsqueda de juegos por género y por rango de precio
✅ Proceso de compra con validaciones de saldo, disponibilidad y duplicidad
✅ Biblioteca personal de juegos por usuario
✅ Historial de compras
✅ Comunicación REST entre microservicios con manejo de timeouts y errores
✅ Validaciones de entrada con Bean Validation
✅ Manejo centralizado de excepciones con @RestControllerAdvice
✅ Logs estructurados con SLF4J para trazabilidad
🏗️ Arquitectura
El sistema está compuesto por 3 microservicios independientes, cada uno con su propia base de datos y responsabilidad clara.


Cada microservicio cumple el patrón Controller → Service → Repository (CSR) y se comunica con los demás únicamente mediante REST HTTP (nunca acceso directo a base de datos ajena).

Microservicio	Puerto	Base de datos	Responsabilidad
ms.usuarios	8081	ms_usuarios_db	Gestión de usuarios y billetera digital
ms.catalogo	8082	ms_catalogo_db	Catálogo de juegos, géneros y desarrolladores
ms.compras	8083	ms_compras_db	Compras y biblioteca personal de usuarios
🛠️ Tecnologías
Tecnología	Versión	Uso
Java	17	Lenguaje de programación
Spring Boot	4.0.6	Framework principal
Spring Data JPA	—	Persistencia con Hibernate
Spring Validation	—	Validaciones declarativas
Spring WebFlux (WebClient)	—	Cliente HTTP entre microservicios
MySQL	8.x	Base de datos relacional
Maven	3.9+	Gestión de dependencias
Lombok	—	Reducción de boilerplate
SLF4J + Logback	—	Logging estructurado
📋 Prerrequisitos
Antes de ejecutar el proyecto necesitas tener instalado:

JDK 17 o superior
Maven
MySQL corriendo localmente en el puerto 3306
IntelliJ IDEA
Postman
📁 Estructura del Repositorio
proyecto-tienda-juegos/
│
├── ms.usuarios/          # Microservicio de Usuarios (puerto 8081)
│   ├── src/
│   │   └── main/
│   │       ├── java/cl/duoc/tienda/ms/usuarios/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   ├── model/
│   │       │   ├── dto/
│   │       │   └── exception/
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
├── ms.catalogo/          # Microservicio de Catálogo (puerto 8082)
│   └── ... (misma estructura)
│
├── ms.compras/           # Microservicio de Compras (puerto 8083)
│   ├── src/main/java/cl/duoc/tienda/ms/compras/
│   │   ├── client/       # ← clientes WebClient para los otros MS
│   │   ├── config/       # ← configuración de WebClient
│   │   └── ... (misma estructura base)
│   └── pom.xml
│
└── README.md             # Este archivo
⚙️ Instalación y Ejecución
1. Clonar el repositorio
git clone https://github.com/<eln4ch0-0>/<PoweredBy>.git
cd <PoweredBy>
2. Configurar MySQL
Asegúrate de que MySQL esté corriendo en localhost:3306. Las bases de datos se crean automáticamente gracias al parámetro createDatabaseIfNotExist=true en cada application.properties, así que no necesitas crearlas manualmente.

Si tus credenciales de MySQL son distintas a root / sin contraseña, ajusta los archivos:

ms.usuarios/src/main/resources/application.properties
ms.catalogo/src/main/resources/application.properties
ms.compras/src/main/resources/application.properties
3. Ejecutar los microservicios
Orden recomendado: primero ms.usuarios y ms.catalogo (son independientes), luego ms.compras (depende de los otros dos).

Opción A — Desde IntelliJ
Abrir cada proyecto y ejecutar Application.java con el botón ▶️.

Opción B — Desde terminal (recomendado para correr los 3 al mismo tiempo)
# Terminal 1
cd ms.usuarios
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows

# Terminal 2
cd ms.catalogo
./mvnw spring-boot:run

# Terminal 3
cd ms.compras
./mvnw spring-boot:run
4. Verificar que todo está funcionando
curl http://localhost:8081/api/usuarios
curl http://localhost:8082/api/juegos
curl http://localhost:8083/api/compras
Los tres deben responder con [] (lista vacía) o con datos existentes, y código HTTP 200.

🔌 Endpoints REST
MS-Usuarios — http://localhost:8081
Método	Endpoint	Descripción
GET	/api/usuarios	Listar todos los usuarios
GET	/api/usuarios/{id}	Obtener usuario por ID
POST	/api/usuarios	Crear un nuevo usuario
PUT	/api/usuarios/{id}	Actualizar usuario
DELETE	/api/usuarios/{id}	Eliminar usuario
PUT	/api/usuarios/{id}/recargar-saldo	Recargar saldo en la billetera
PUT	/api/usuarios/{id}/descontar-saldo	Descontar saldo (consumido por MS-Compras)
MS-Catalogo — http://localhost:8082
Método	Endpoint	Descripción
GET	/api/juegos	Listar todos los juegos
GET	/api/juegos?genero={id}	Filtrar juegos por género
GET	/api/juegos?precioMax={n}	Filtrar juegos disponibles bajo cierto precio
GET	/api/juegos/{id}	Obtener juego por ID
POST	/api/juegos	Crear un nuevo juego
PUT	/api/juegos/{id}	Actualizar juego
PUT	/api/juegos/{id}/disponibilidad?disponible={bool}	Cambiar disponibilidad
DELETE	/api/juegos/{id}	Eliminar juego
GET/POST/PUT/DELETE	/api/generos[/{id}]	CRUD de géneros
GET/POST/PUT/DELETE	/api/desarrolladores[/{id}]	CRUD de desarrolladores
MS-Compras — http://localhost:8083
Método	Endpoint	Descripción
POST	/api/compras	Realizar una compra (orquesta el flujo completo)
GET	/api/compras	Listar todas las compras
GET	/api/compras/{id}	Obtener una compra específica
GET	/api/compras/usuario/{usuarioId}	Historial de compras de un usuario
GET	/api/biblioteca/{usuarioId}	Biblioteca completa de un usuario
GET	/api/biblioteca/{usuarioId}/{juegoId}	Verificar si un usuario posee un juego
🔄 Flujo de Compra (End-to-End)
Ejemplo paso a paso del caso de uso principal:

# 1. Crear un usuario
curl -X POST http://localhost:8081/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gamer01",
    "email": "gamer01@correo.cl",
    "password": "secreta123",
    "nombreCompleto": "Juan Pérez"
  }'

# 2. Recargar saldo del usuario
curl -X PUT http://localhost:8081/api/usuarios/1/recargar-saldo \
  -H "Content-Type: application/json" \
  -d '{"monto": 50000}'

# 3. Crear un género
curl -X POST http://localhost:8082/api/generos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "RPG", "descripcion": "Juegos de rol"}'

# 4. Crear un desarrollador
curl -X POST http://localhost:8082/api/desarrolladores \
  -H "Content-Type: application/json" \
  -d '{"nombre": "CD Projekt Red", "pais": "Polonia"}'

# 5. Crear un juego
curl -X POST http://localhost:8082/api/juegos \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "The Witcher 3",
    "descripcion": "RPG de mundo abierto",
    "precio": 19990,
    "fechaLanzamiento": "2015-05-19",
    "generoId": 1,
    "desarrolladorId": 1
  }'

# 6. Realizar la compra (esto dispara la comunicación entre los 3 MS)
curl -X POST http://localhost:8083/api/compras \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "juegoId": 1}'

# 7. Verificar que el saldo fue descontado
curl http://localhost:8081/api/usuarios/1

# 8. Verificar que el juego está en la biblioteca
curl http://localhost:8083/api/biblioteca/1
🚨 Manejo de Errores
Todos los microservicios devuelven respuestas de error consistentes en formato JSON:

{
  "timestamp": "2026-05-19T18:42:30.123",
  "status": 404,
  "error": "Usuario con id 999 no existe"
}
Códigos HTTP utilizados
Código	Significado	Cuándo se devuelve
200	OK	Operación exitosa
201	Created	Recurso creado correctamente
204	No Content	Eliminación exitosa
400	Bad Request	Validación fallida o regla de negocio violada
404	Not Found	El recurso solicitado no existe
409	Conflict	Duplicidad (ej: email ya registrado)
503	Service Unavailable	Un microservicio dependiente no responde
500	Internal Server Error	Error no controlado
🧠 Decisiones de Diseño
Decisiones técnicas relevantes que se pueden defender en la presentación técnica:

1. Snapshot de precio en Compra
La entidad Compra almacena tituloJuego y precioPagado localmente, no solo el juegoId. Esto permite que el historial de compras sea inmutable incluso si el precio del juego cambia en el futuro o si el juego se elimina del catálogo.

2. @JsonIgnoreProperties(ignoreUnknown = true) en DTOs externos
Los DTOs que reciben datos desde otros microservicios ignoran campos desconocidos. Esto desacopla los contratos y permite evolucionar los microservicios independientemente sin romper a los consumidores.

3. WebClient con timeout configurable
La configuración de WebClient incluye un timeout de 5 segundos (configurable por properties). Si un microservicio dependiente no responde a tiempo, la operación falla con 503 Service Unavailable en lugar de quedarse colgada.

4. Una excepción genérica por tipo de error en MS-Catalogo
En lugar de tener JuegoNoEncontradoException, GeneroNoEncontradoException, DesarrolladorNoEncontradoException, se usa una sola RecursoNoEncontradoException con un mensaje descriptivo. Esto reduce duplicación de código cuando hay múltiples entidades en un mismo microservicio.

5. Restricción única en la biblioteca
La tabla biblioteca tiene una restricción única sobre (usuario_id, juego_id). Como la propiedad del juego es permanente, no tiene sentido que un usuario tenga el mismo juego dos veces. Esta regla se valida tanto a nivel de base de datos como en el servicio.

6. URLs de servicios externos como propiedades
Las URLs de los microservicios consumidos por ms.compras están en application.properties (no hardcoded). Esto facilita el despliegue en distintos ambientes (dev, test, producción) y futuras integraciones con un Service Discovery.

7. @Transactional en operaciones que tocan múltiples tablas
CompraService.realizarCompra() está marcado como @Transactional para garantizar atomicidad local: si falla la inserción en biblioteca, también se hace rollback de compras. La consistencia distribuida (con MS-Usuarios) está fuera del alcance de esta entrega y requeriría el patrón Saga.

🌿 Convenciones de Commits
Para mantener un historial ordenado y trazable, se usan los siguientes prefijos:

Prefijo	Significado	Ejemplo
feat:	Nueva funcionalidad	feat: agregar endpoint de recarga de saldo
fix:	Corrección de bug	fix: corregir validación de email duplicado
refactor:	Refactorización sin cambio funcional	refactor: extraer mapper a clase utilitaria
docs:	Cambios en documentación	docs: agregar sección de endpoints al README
config:	Cambios de configuración	config: ajustar puerto de ms.catalogo
test:	Pruebas	test: agregar tests para CompraService
📜 Licencia
Este es un proyecto académico desarrollado en el contexto del curso DSY1103 - Desarrollo FullStack I de Duoc UC. Su uso es exclusivamente educativo.
