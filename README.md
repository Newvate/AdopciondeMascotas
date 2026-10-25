# Hogar Fiel — adopción de mascotas

Aplicación web de demostración hecha con Java. Incluye un catálogo de perros y gatos con búsqueda por nombre, raza y ubicación, filtros por tipo, raza, ubicación, edad y tamaño, carga progresiva de resultados, estados visuales, favoritos guardados en el navegador, ficha y solicitud de adopción, proceso de adopción, recomendaciones para adoptar con confianza, información de donaciones y formulario de voluntariado.

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

Las imágenes del catálogo se cargan desde Unsplash, por lo que se requiere conexión a internet para mostrarlas. Los datos y estados de las mascotas son ejemplos; los favoritos se guardan en el almacenamiento local del navegador. El panel de apoyo no procesa pagos: solicita al equipo los datos verificados de cuentas/Yape. Los formularios confirman la recepción en el servidor, pero todavía no guardan solicitudes en una base de datos ni envían correos.
