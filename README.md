# BackendEntradas_DISOFT_G16

Backend de **venta de entradas para espectáculos** del proyecto de Diseño de Software, realizado por Alejandro Fernández Muñoz. Gestiona:

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

