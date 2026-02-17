# BackendEntradas_DISOFT_G16

Backend de **venta de entradas para eventos** del proyecto de Diseño de Software, realizado por Alejandro Fernández Muñoz. Gestiona:

- Escenarios (estadios, teatros, etc.).
- Espectáculos (conciertos, obras, etc.).
- Entradas asociadas a cada espectáculo (por zona o asiento concreto).
- Estados de las entradas (disponible, reservada, vendida).

Este backend será consumido por

- **Frontend_DISOFT_G16** (https://github.com/AlejandroFernandezMunoz/Frontend_DISOFT_G16) 
- **BackendUsuarios_DISOFT_G16** (https://github.com/AlejandroFernandezMunoz/BackendUsuarios_DISOFT_G16)

# Compilar

```
./mvnw clean compile
```

# Ejecutar la aplicación

```
./mvnw spring-boot:run
```

La aplicación quedará disponible en:

- `http://localhost:8080`

# Estructura

<img width="400" height="390" alt="Captura de pantalla 2026-02-16 a las 20 15 20" src="https://github.com/user-attachments/assets/960aec11-a258-41fa-ad44-686b12e7504b" />

