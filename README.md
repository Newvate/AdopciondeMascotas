# Hogar Fiel — adopción de mascotas

Aplicación web de demostración hecha con Java. Incluye un catálogo de perros y gatos, filtros por tipo, raza, ubicación y edad, ordenamiento, ficha de detalles y formulario de contacto.

## Requisitos

- JDK 25 (el proyecto está configurado para compilar con Java 25).
- Maven.

## Ejecutar en NetBeans

Abre la carpeta del proyecto con **File > Open Project** en Apache NetBeans. Selecciona **Run Project** o pulsa **F6**; el proyecto tiene configurada esa acción para iniciar el servidor Java. Después visita <http://localhost:8080>. Para detenerlo, pulsa el botón Stop de la ventana Output.

## Ejecutar desde una terminal

```sh
mvn compile exec:java
```

El puerto predeterminado es `8080`; puedes cambiarlo definiendo la variable de entorno `PORT` antes de iniciar la aplicación.

Las imágenes del catálogo se cargan desde Unsplash, por lo que se requiere conexión a internet para mostrarlas. Los datos de las mascotas son ejemplos; el formulario confirma la recepción en el servidor, pero todavía no guarda solicitudes en una base de datos ni envía correos.
