# Compresor Huffman

Este repositorio contiene una aplicación Java de escritorio que implementa compresión y descompresión de archivos mediante codificación Huffman. El proyecto se presenta como una utilidad con interfaz Swing que calcula frecuencias de bytes, construye un árbol de Huffman, genera códigos prefijo y luego escribe y lee una representación comprimida del archivo.

La implementación es directa y sencilla: las clases Java se mantienen en el paquete por defecto para que puedan compilarse directamente desde la raíz del repositorio sin requerir una estructura de paquetes personalizada.

Demo del compresor; Probabilidades y árbol.

<img width="500" height="420" alt="Tree" src="https://github.com/user-attachments/assets/606a670a-58c6-4746-aeba-7119e850f123" /> <img width="500" height="420" alt="Captura de pantalla 2026-05-11 202347" src="https://github.com/user-attachments/assets/d950451a-2192-4d59-8ee6-8b8d4bffb72a" />




## Propósito del proyecto

La aplicación está enfocada a demostrar el algoritmo de Huffman y algunas métricas de teoría de la información. En el código actual, las funcionalidades principales son:

- Compresión de archivos con codificación Huffman
- Descompresión desde el formato comprimido interno
- Cálculo de frecuencias de símbolos y asignación de códigos
- Visualización del árbol de Huffman en la interfaz gráfica
- Cálculo de entropía, longitud media, eficiencia y tiempo de ejecución
- Interfaz Swing para seleccionar archivos y operar sobre ellos de forma interactiva

## Contenido del repositorio

En la raíz del proyecto se encuentran los archivos fuente Java y los ejecutables listos para usar:

- `Huffman.java` — punto de entrada de la aplicación
- `Vista.java` — interfaz Swing y acciones del usuario
- `Controlador.java` — controlador que coordina el modelo y la estructura de datos
- `Modelo.java` — lógica de compresión/descompresión, generación de códigos y estadísticas
- `Estructura.java` — estructura del árbol y lectores/escritores de archivos
- `BinaryHeap.java` — cola de prioridad usada en la construcción del árbol
- `Simbolo.java` — representación de símbolos usada por la interfaz
- `ResultadoCompresion.java` — modelo de resultados de compresión
- `ProgressCallback.java` — interfaz para la barra de progreso
- `WinHuff_jar.jar` — paquete ejecutable Java
- `WinHuff_x86_x64.exe` — ejecutable para Windows
- `quijote.txt`, `quijote.huff`, `quijote_dec.txt` — archivos de ejemplo para pruebas y demostración

## Cómo funciona la aplicación

El flujo implementado en el código es:

1. Seleccionar un archivo desde la interfaz.
2. Contar la frecuencia de cada valor de byte.
3. Construir un árbol de Huffman con la estructura de prioridad disponible.
4. Generar códigos prefijo para cada símbolo.
5. Escribir el archivo comprimido junto con los metadatos necesarios para reconstruirlo.
6. Leer de nuevo el archivo comprimido y recuperar los bytes originales y su extensión.
7. Mostrar las estadísticas resultantes en la interfaz.

La implementación actual calcula y expone:

- Entropía
- Longitud media de código
- Eficiencia relativa
- Tiempo de compresión
- Visualización del árbol de Huffman

## Ejecución desde el código fuente

Como las clases están en el paquete por defecto, la compilación se realiza desde la raíz del repositorio:

```bash
javac *.java
java Huffman
```

Esto lanza la ventana gráfica donde puedes elegir un archivo y comprimirdescomprimirlo.

## Ejecutables listos para usar

El repositorio ya incluye ejecutables para usuarios en la raíz del proyecto, para que puedan utilizarse directamente sin recompilar:

- `WinHuff_jar.jar`
- `WinHuff_x86_x64.exe`

Estos archivos están pensados como artefactos de distribución para usuarios que quieran ejecutar la aplicación sin compilar el código fuente.

## Nota sobre la estructura del proyecto

Los archivos Java se mantienen actualmente en la raíz del repositorio para conservar compatibilidad con la compilación en paquete por defecto que usa el código actual. Es una opción práctica para este repositorio y mantiene la aplicación simple de compilar. Si más adelante se refactoriza el proyecto a un esquema con paquetes, puede reorganizarse bajo un directorio `src/` sin cambiar el algoritmo subyacente.

## Licencia

La descripción del repositorio indica que el proyecto es propietario y que el código se distribuye bajo una nota de copyright incluida en el README original. Se deben respetar los términos de uso y licencia originales antes de redistribuir o modificar el código.

## Archivos de ejemplo

El repositorio incluye archivos de prueba que permiten evaluar el compresor y observar su salida:

- `quijote.txt` — texto original de ejemplo
- `quijote.huff` — muestra comprimida generada por la aplicación
- `quijote_dec.txt` — resultado descomprimido para comparación

Este proyecto se entiende mejor como una implementación compacta y didáctica del algoritmo de Huffman, más que como una librería de compresión generalista lista para producción.
