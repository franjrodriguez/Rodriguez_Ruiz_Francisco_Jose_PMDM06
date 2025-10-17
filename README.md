# 🌀 Gincanicoot

**Gincanicoot** es una aplicación Android desarrollada en Java que combina geolocalización, gamificación y estilo visual inspirado en *Crash Bandicoot*.  
El jugador debe desplazarse físicamente para descubrir puntos de interés, resolver desafíos y completar una divertida gymkana virtual.

---

## 🚀 Características principales

- 🗺️ **Integración completa con Google Maps**
  - Carga dinámica de puntos de interés desde un archivo JSON.
  - Marcadores personalizados con estilo temático (*Crash boxes* y frutas).

- 🎮 **Jugador animado**
  - Icono de jugador en movimiento con animación paso a paso.
  - Seguimiento de cámara y rutas temáticas visuales.

- 💡 **Desafíos interactivos**
  - Cada marcador incluye un reto con código secreto que desbloquea su estado.
  - Diálogos personalizados y efectos visuales al completar desafíos.

- 🧠 **Arquitectura modular**
  - `data/` → Carga y modelo de puntos desde JSON.
  - `UI/` → Estilos del mapa y componentes visuales.
  - `utils/` → Constantes globales y utilidades.
  - `assets/` → Archivo `checkpoint_bandicoot.json` con los puntos del mapa.

---

## 🏗️ Estructura del proyecto

```text
app/
├── src/
│ ├── main/
│ │ ├── java/dam/pmdm/gincanicoot/
│ │ │ ├── UI/CrashMapStyler.java
│ │ │ ├── data/LocationLoader.java
│ │ │ ├── data/LocationPoint.java
│ │ │ ├── utils/Constants.java
│ │ │ └── MainActivity.java
│ │ ├── assets/checkpoint_bandicoot.json
│ │ └── res/
│ │ ├── layout/
│ │ ├── drawable/
│ │ ├── font/
│ │ └── mipmap/
├── build.gradle.kts
├── proguard-rules.pro
└── .gitignore
```

---

## ⚙️ Tecnologías utilizadas

- **Lenguaje:** Java  
- **SDK:** Android API 34+  
- **Servicios:** Google Maps SDK for Android  
- **Librerías:**  
  - `com.google.android.gms:play-services-maps`  
  - `com.google.android.gms:play-services-location`  
  - `com.google.android.material:material`  

---

## 🧩 Conceptos destacados

- Control del ciclo de vida de una `Activity` con integración de mapas.
- Manejo asíncrono de permisos y actualizaciones de ubicación.
- Animaciones personalizadas con `Handler` y `Runnable`.
- Creación de rutas visuales entre puntos con `Polyline`.
- Uso de `Snackbar` y `Dialog` para interacción fluida.

---

## 🎯 Objetivo del proyecto

Este proyecto fue desarrollado con fines didácticos para demostrar:
- Integración avanzada de Google Maps con lógica personalizada.
- Diseño modular y reutilizable en Android.
- Técnicas de animación y feedback visual en tiempo real.

Ideal para usar como base para:
- Juegos de exploración o gymkanas geolocalizadas.
- Aplicaciones turísticas con retos interactivos.
- Apps educativas basadas en movimiento y localización.

---

## 📸 Capturas de pantalla

| Vista principal | Diálogo de reto | Ruta temática |
|------------------|------------------|----------------|
| ![Mapa](./screenshots/map_view.png) | ![Reto](./screenshots/challenge_dialog.png) | ![Ruta](./screenshots/thematic_route.png) |

*(Las capturas deben ubicarse en `app/screenshots/` y actualizarse con tus imágenes reales.)*

---

## 👨‍💻 Autor

**Francisco José Rodríguez Ruiz**  
📍 Guadix, España  
💼 Backend & Android Developer  
🐾 Amante del aprendizaje constante, los paseos con Kenzo y la comida japonesa 🍣  

---

## 🧠 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.  
Puedes usarlo, modificarlo o aprender de él libremente.

---

> “El mapa no es el territorio… pero si lo haces divertido, ¡nadie notará la diferencia!” 🦊

---
