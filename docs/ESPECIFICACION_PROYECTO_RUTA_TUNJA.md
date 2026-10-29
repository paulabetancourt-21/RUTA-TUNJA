# ESPECIFICACION DEL PROYECTO: MAPA INTERACTIVO DE RUTAS DE TUNJA (JAVA SWING - MVP)

Proyecto: proyecto ruta tunja  
Tecnología: Java 17 + Maven + Java Swing  
Arquitectura: Modelo - Vista - Presentador (MVP)  
Dominio base: `co.edu.uptc`  
Convención: `camelCase` en inglés para clases, métodos, variables, atributos, paquetes. Nombres descriptivos pero concisos (que indiquen su responsabilidad).  
Regla de métodos: Máximo 5 líneas. Únicamente en casos extremos hasta 10. Si un método tiende a crecer, se debe dividir en métodos privados con nombres descriptivos.  
Dominio obligatorio: todo el código debe usar el paquete base `co.edu.uptc`.  
Regla: NINGÚN dato quemado en código. SOLO archivo de configuración `.yml`.  
Regla: Interfaz ÚNICAMENTE con menús de Swing (`JMenuBar`, `JMenu`, `JMenuItem`). NO botones. NO menús desplegables.  
Regla: Accesos rápidos con `Alt + <letra>`.  
Regla: No inventar. Cualquier decisión mínima se debe consultar.  

## 1. OBJETIVO
Desarrollar una aplicación de escritorio en Java Swing para el mapa interactivo de la ciudad de Tunja, que permita visualizar el mapa base con todas sus vías, construir el grafo (nodos y aristas) sobre ese mapa, calcular la ruta más corta entre dos puntos con el algoritmo de Dijkstra y visualizarla con un color diferente al resto. Todo vehicular. Sin peatones. 

## 2. REQUISITOS OBLIGATORIOS
- Arquitectura Modelo - Vista - Presentador (MVP)
- Paquetes bajo `co.edu.uptc.*`
- `camelCase` en inglés
- Métodos cortos: max 5 líneas, solo casos extremos max 10
- Interfaz únicamente con menús Swing. Sin botones. Sin menús desplegables.
- Accesos rápidos `Alt + <letra>`
- Multilenguaje: 3 lenguajes principales + posibilidad de añadir más. Textos desde `i18n`
- Grafo: Lista de Adyacencia o Matriz de Adyacencia (decidir con justificación)
- Mapa base de Tunja con todas sus vías. Sobre él se crean nodos/aristas
- Nodos con coordenadas geográficas
- Aristas con peso. Inicialmente distancia en kilómetros calculada con fórmula a partir de coordenadas geográficas
- Información del grafo en archivos `.csv`
- Algoritmo Dijkstra
- Ruta resultante con color distinto
- Ningún dato quemado. Solo archivo de configuración `.yml`
- Logs con rotación cada 3 días
- Maven
- Zoom del mapa. Al ampliar/reducir, todos los complementos de la vista deben ajustarse (nodos, aristas, marcadores, ruta, etc.)
- Al dar clic sobre a un nodo, debe existir radio de tolerancia
- Al crear el grafo se debe poder escoger sentido de vías (solo un sentido / doble sentido)
- Configurar: punto del nodo (tamaño/representación), grosor de línea/arista, color de línea/arista
- Configuración YAML: carga perezosa o carga rápida (decidir)
- Únicamente ruta vehicular

## 3. ANÁLISIS DEL BOCETO
- Ventana principal con barra de menús: `Via`, `Route`, `Options`, `Help` (según boceto; los nombres finales se decidirán con i18n)
- Menú `Via`: `Crear nuevo nodo (Ctrl-N)`, `Crear nueva arista (Ctrl-E)`
- Menú `Route`: `Calcular ruta (Ctrl-R)`, `Guardar ruta (Ctrl-G)`, `Limpiar ruta (Ctrl-L)`
- Barra inferior/estado: `Lat - Lon - Zoom 100%`
- Faltante: opción para configurar punto del nodo, grosor de línea, color de línea, sentido al crear vía. Se debe ubicar en menú según respuesta.

## 4. ESTRUCTURA PROPUESTA CON DOMINIO co.edu.uptc
Todo va directamente bajo el dominio `co.edu.uptc`. 

```text
src/main/java/co/edu/uptc/
├── config/
├── dto/
├── i18n/
├── log/
├── model/
├── presenter/
├── util/
└── view/

src/main/resources/
├── config/
├── csv/
├── i18n/
└── map/

src/test/java/co/edu/uptc/

docs/
logs/
```

### 4.1 ¿Por qué cada paquete y qué va en cada uno?

| Paquete | Propósito | Qué se almacena | Qué NO va |
| :-- | :-- | :-- | :-- |
| `co.edu.uptc.config` | Configuración de la aplicación. Única fuente de configuración debe ser el archivo `.yml`. | Clases que carguen/parseen `application.yml`, interfaces para acceso a configuración, valores tipados. Debe contemplar carga perezosa o carga rápida según decisión. | Datos quemados. Lógica de negocio. Código Swing. |
| `co.edu.uptc.model` | Modelo (lógica pura). Sin dependencias de Swing. | Entidades: `Node`, `Edge`, `Graph`. Lógica de Dijkstra. Cálculo de peso con coordenadas geográficas. Reglas del grafo. Interfaces del modelo (`GraphService`, etc.). | Componentes Swing, acceso a archivos directo sin abstracción, textos UI, datos quemados. |
| `co.edu.uptc.view` | Vista (únicamente interfaz). Implementa con Swing. | Ventana principal, panel del mapa, barra de menús (`JMenuBar`, `JMenu`, `JMenuItem`), dibujo del mapa/nodos/aristas/ruta, manejo de zoom, radio de tolerancia al click, ajustes al escalar. ÚNICAMENTE menús, sin botones ni menús desplegables. Interfaces de la vista. | Lógica de negocio, algoritmo Dijkstra, lectura directa de CSV/YAML, reglas del grafo. |
| `co.edu.uptc.presenter` | Presentador (MVP). Desacopla vista y modelo. | Implementa el contrato entre vista y modelo. Orquesta acciones (crear nodo, crear arista, calcular ruta, limpiar ruta, guardar ruta), traduce eventos de menú, coordina carga de CSV/config/i18n, integra logs. Implementaciones de interfaces. | Código Swing directo en exceso, lógica pura del grafo (va a model), dibujo. |
| `co.edu.uptc.dto` | Objetos de transferencia. | DTOs para transportar datos entre capas sin exponer entidades completas innecesariamente. En inglés, camelCase. | Lógica, componentes UI. |
| `co.edu.uptc.util` | Utilidades puras, reutilizables. | Conversión coordenadas geográficas <-> píxeles, cálculo de distancia geográfica, validaciones, radio de tolerancia, utilidades para escalado/zoom, helpers genéricos. Sin estado de UI ni lógica de negocio compleja. | Datos quemados, acceso a recursos sin abstracción, Swing. |
| `co.edu.uptc.i18n` | Internacionalización. | Interfaces y clases para cargar archivos de traducción. Soporta 3 lenguajes principales + posibilidad de añadir más. Lee desde `src/main/resources/i18n/`. Todos los textos de UI deben venir de aquí. | Textos hardcodeados en vista/presentador. |
| `co.edu.uptc.log` | Logs. | Configuración de `java.util.logging` con rotación cada 3 días, formateadores, configuración de handlers. Todo via configuración donde sea posible. | Datos quemados. Lógica de negocio. |
| `co.edu.uptc` (raíz) | Punto de entrada. | Clase `Main` (camelCase según convención pero nombre clásico permitido para entry point). Inicializa capas siguiendo desacoplamiento. | Cualquier otra clase. | 

### 4.2 Desacoplamiento con interfaces
TODO el código debe estar **desacoplado con interfaces**. 

- **Vista**: Debe exponer una interfaz (ej. `MapView`) que el presentador use. La implementación concreta (`MapViewImpl`) usa Swing.
- **Presentador**: Debe implementar una interfaz (ej. `MapPresenter`) que la vista use para comunicar eventos, y usar interfaces del modelo.
- **Modelo**: Debe exponer interfaces (ej. `GraphService`, `RouteService`, `DijkstraService`) con sus implementaciones concretas. Sin importar Swing.
- **Configuración/I18n/Log**: Deben ir por interfaces para permitir reemplazo y mantener desacoplamiento.
- **Inyección**: Se debe trabajar por **interfaces** y **constructor** (sin `new` innecesarios entre capas). Las implementaciones concretas se instancian en `Main` o en un `initializer` siguiendo MVP.

Notas:
- Dominio termina en `co.edu.uptc`
- `model`, `view`, `presenter` en inglés con `camelCase`
- Clases, métodos, variables 100% `camelCase` en inglés
- Métodos cortos: máximo 5 líneas. Únicamente en casos extremos hasta 10. Si crece, dividir en métodos privados.
- Paquetes en minúsculas
- NINGÚN dato quemado en código. SOLO archivo de configuración `.yml`

## 5. FASES DETALLADAS CON LISTA DE CHEQUEO

Cada fase debe estar completamente cumplida. NO se pasa a la siguiente fase si algún ítem de su lista de chequeo está sin marcar. Todo debe cumplir: `co.edu.uptc`, `camelCase` inglés, métodos max 5 líneas (extremos 10), sin datos quemados, solo `.yml`, interfaces, desacoplamiento MVP.

### FASE 0: ANÁLISIS Y APROBACIÓN
**Objetivo:** Dejar todo claro antes de escribir código. Sin crear clases funcionales.  
**Estado:** PENDIENTE (ACTUAL)

#### Actividades
- [ ] Revisar especificación completa
- [ ] Confirmar reglas (dominio, camelCase, métodos cortos, interfaces, menús únicos, sin datos quemados)
- [ ] Leer boceto y tener claras las opciones faltantes
- [ ] Responder todas las preguntas del apartado 10

#### Criterio para pasar a FASE 1
- [ ] TODAS las preguntas del apartado 10 han sido respondidas por el usuario
- [ ] No existen dudas sin resolver
- [ ] Se aprueba pasar a FASE 1

### FASE 1: REESTRUCTURACIÓN A CO.EDU.UPTC
**Objetivo:** Tener la estructura exacta bajo dominio correcto, sin carpetas innecesarias.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Verificar paquetes: `config`, `dto`, `i18n`, `log`, `model`, `presenter`, `util`, `view`
- [ ] Ajustar `pom.xml` con `groupId` `co.edu.uptc` y `mainClass` correcto
- [ ] Crear carpetas `src/main/resources/{config,csv,i18n,map}`, `docs`, `logs`, `src/test/java/co/edu/uptc/`
- [ ] Confirmar estructura limpia y sin `com.*`

#### Criterio para pasar a FASE 2
- [ ] Estructura revisada y aprobada
- [ ] Sin `com.*` ni `rutatunja`
- [ ] Paquetes listos para recibir interfaces
- [ ] Dominio `co.edu.uptc` correcto en todo

### FASE 2: CONFIGURACIÓN YAML + DESACOPLAMIENTO
**Objetivo:** Única fuente de configuración es `.yml`. Implementar carga según decisión (perezosa/rápida). Todo por interfaces.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear `src/main/resources/config/application.yml` (sin datos quemados)
- [ ] Crear interfaz `ConfigService` en `co.edu.uptc.config`
- [ ] Crear implementación concreta `YamlConfigService` en `co.edu.uptc.config` (camelCase inglés)
- [ ] Implementar carga perezosa o carga rápida según respuesta de FASE 0
- [ ] Crear DTOs necesarios en `co.edu.uptc.dto` si aplica
- [ ] Instanciar por constructor e interfaces en `Main`
- [ ] Verificar métodos cortos (máx 5 líneas)

#### Criterio para pasar a FASE 3
- [ ] `application.yml` existe sin datos quemados
- [ ] `ConfigService` (interface) + implementación concreta creadas
- [ ] Tipo de carga perezosa implementado correctamente
- [ ] Desacoplamiento verificado
- [ ] Sin datos hardcodeados

### FASE 3: INTERNACIONALIZACIÓN (MULTILENGUAJE)
**Objetivo:** 3 lenguajes principales (`es`, `en`, `fr`) + posibilidad de añadir más. Todos los textos UI desde `i18n`.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear interfaz `LanguageService` en `co.edu.uptc.i18n`
- [ ] Crear implementación concreta `YamlLanguageService` en `co.edu.uptc.i18n` (prohibido uso de `.properties`)
- [ ] Crear archivos `.yml` en `src/main/resources/i18n/` para `es.yml`, `en.yml`, `fr.yml`
- [ ] Crear mecanismo extensible (solo agregar archivo para añadir más lenguajes, sin tocar código)
- [ ] Integrar accesos `Alt + <letra>` según menús
- [ ] Revisar que NO existan textos hardcodeados en `view` ni `presenter`

#### Criterio para pasar a FASE 4
- [ ] Archivos `es.yml`, `en.yml`, `fr.yml` creados (sin `.properties`)
- [ ] `LanguageService` + implementación concretas creadas
- [ ] Mecanismo extensible funcional
- [ ] Sin textos hardcodeados
- [ ] Accesos `Alt + <letra>` definidos

### FASE 4: MODELO - GRAFO Y DIJKSTRA
**Objetivo:** Lógica pura sin Swing. Decidir estructura de grafo con justificación. Implementar Dijkstra.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear entidades `Node`, `Edge`, `Graph` en `co.edu.uptc.model` (camelCase inglés)
- [ ] Crear interfaces `GraphService`, `RouteService`, `DijkstraService` en `co.edu.uptc.model`
- [ ] Crear implementaciones concretas en `co.edu.uptc.model`
- [ ] Implementar cálculo de peso con coordenadas geográficas
- [ ] Implementar algoritmo Dijkstra
- [ ] Documentar y aplicar decisión: Lista de Adyacencia o Matriz de Adyacencia con justificación
- [ ] Asegurar métodos cortos y sin imports de Swing
- [ ] Sin datos quemados

#### Criterio para pasar a FASE 5
- [ ] Entidades, interfaces e implementaciones creadas
- [ ] Cálculo de peso por coordenadas funcional
- [ ] Dijkstra funcional
- [ ] Estructura de grafo elegida y justificada en documentación
- [ ] Completamente desacoplado de `view`
- [ ] Sin dependencia de Swing

### FASE 5: CARGA CSV
**Objetivo:** Leer nodos y aristas desde archivos `.csv`. Sin datos quemados.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear interfaz `CsvReaderService` en `co.edu.uptc.model` o `co.edu.uptc.util` (manteniendo desacoplamiento)
- [ ] Crear implementación concreta
- [ ] Leer archivos `nodes.csv` y `edges.csv`
- [ ] Aplicar estructura exacta `id,lat,lon` y `fromId,toId,oneWay`
- [ ] Calcular peso únicamente con coordenadas geográficas (no leerlo desde CSV)
- [ ] Validar datos leídos
- [ ] Métodos cortos

#### Criterio para pasar a FASE 6
- [ ] Lectura de `nodes.csv` y `edges.csv` funcional
- [ ] Estructura exacta respetada
- [ ] Peso calculado, no extraído desde CSV
- [ ] Sin datos quemados
- [ ] Integrado correctamente con `model`

### FASE 6: VISTA SWING (SOLO MENÚS)
**Objetivo:** Únicamente menús de Swing. Mapa, zoom, radio de tolerancia, configuración gráfica.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear interfaz `MapView` en `co.edu.uptc.view`
- [ ] Crear implementación concreta `MapViewImpl` en `co.edu.uptc.view` con Swing
- [ ] Crear `JMenuBar` con menús `Via`, `Route`, `Preferences`, `Help` (nombres según `i18n`)
- [ ] Agregar items: `Crear nuevo nodo`, `Crear nueva arista`, `Calcular ruta`, `Guardar ruta`, `Limpiar ruta`
- [ ] Crear menú `Preferences` únicamente para ajustes visuales o estéticos (tamaño del nodo, color de la ruta seleccionada, grosor de línea, color de elementos gráficos). Sin configuraciones técnicas.
- [ ] Implementar configuración de sentido (solo un sentido / doble sentido) al crear arista desde `Via > Crear nueva arista`
- [ ] Aplicar accesos únicamente `Alt + <letra>`
- [ ] Crear panel de mapa con imagen base `.png` desde `src/main/resources/map/`
- [ ] Implementar zoom que ajuste mapa Y todos los complementos (nodos, aristas, marcadores, ruta)
- [ ] Implementar radio de tolerancia al click sobre nodo (parametrizable)
- [ ] Dibujar nodos, aristas, ruta con color distinto, marcador origen/destino
- [ ] Agregar barra de estado con Lat, Lon, Zoom
- [ ] Verificar: ÚNICAMENTE menús. SIN botones. SIN menús desplegables
- [ ] Sin lógica de negocio

#### Criterio para pasar a FASE 7
- [ ] Solo `JMenuBar`, `JMenu`, `JMenuItem`
- [ ] Sin botones ni menús desplegables
- [ ] Menú `Preferences` creado únicamente para ajustes visuales o estéticos
- [ ] Zoom ajusta todo correctamente
- [ ] Radio de tolerancia **10px** únicamente en `application.yml`
- [ ] Ajustes visuales presentes en `Preferences`
- [ ] Sentido configurable al crear arista
- [ ] Accesos únicamente `Alt + <letra>`
- [ ] Conversión geográfica implementada leyendo coordenadas desde `application.yml` o metadatos (sin interfaz configurable)

### FASE 7: PRESENTADOR MVP
**Objetivo:** Desacoplar completamente vista y modelo. Todo por interfaces e inyección por constructor.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear interfaz `MapPresenter` en `co.edu.uptc.presenter`
- [ ] Crear implementación concreta `MapPresenterImpl` en `co.edu.uptc.presenter`
- [ ] Conectar eventos de menús desde `view` hacia `presenter`
- [ ] Orquestar acciones usando únicamente interfaces del `model`
- [ ] Integrar `LanguageService`
- [ ] Integrar `ConfigService`
- [ ] Integrar `LogService`
- [ ] Instanciar todo desde `Main` por constructor
- [ ] Verificar métodos cortos
- [ ] Sin acoplamiento directo entre `view` y `model`

#### Criterio para pasar a FASE 8
- [ ] Flujo completo vía interfaces
- [ ] Sin import directo innecesario entre `view` y `model`
- [ ] Presentador coordina correctamente todas las acciones
- [ ] Inyección por constructor aplicada

### FASE 8: LOGS CON ROTACIÓN CADA 3 DÍAS
**Objetivo:** Sistema de logs con rotación cada 3 días usando `java.util.logging`.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Crear interfaz `LogService` en `co.edu.uptc.log`
- [ ] Crear implementación concreta con `java.util.logging`
- [ ] Configurar rotación cada 3 días
- [ ] Usar ruta `logs/`
- [ ] Usar formato `mapRouteTunja-yyyy-MM-dd.log`
- [ ] Integrar logs en puntos relevantes sin ensuciar lógica
- [ ] Métodos cortos

#### Criterio para pasar a FASE 9
- [ ] Rotación cada 3 días funcional
- [ ] Ruta `logs/` y formato `mapRouteTunja-yyyy-MM-dd.log` correctos
- [ ] Integrado sin romper desacoplamiento
- [ ] Sin datos quemados

### FASE 9: VALIDACIÓN FINAL
**Objetivo:** Validar que TODO cumple con la especificación al pie de la letra.  
**Estado:** BLOQUEADO

#### Actividades
- [ ] Verificar dominio `co.edu.uptc` en todos los paquetes
- [ ] Verificar `camelCase` inglés en clases, métodos, variables, atributos
- [ ] Verificar métodos: max 5 líneas, extremos 10
- [ ] Verificar sin datos quemados
- [ ] Verificar ÚNICAMENTE archivo `.yml` para configuración
- [ ] Verificar desacoplamiento con interfaces
- [ ] Verificar inyección por constructor
- [ ] Verificar interfaz SOLO con menús Swing (sin botones, sin menús desplegables)
- [ ] Verificar accesos únicamente `Alt + <letra>`
- [ ] Verificar zoom ajusta TODOS los complementos
- [ ] Verificar radio de tolerancia al click **10px** (técnico, únicamente en pplication.yml)
- [ ] Verificar sentido configurable al crear arista
- [ ] Verificar menú `Preferences` con configuración punto del nodo, grosor de línea, color de línea, radio de tolerancia y georreferenciación
- [ ] Verificar grafo + Dijkstra funcional
- [ ] Verificar ruta resultante con color distinto
- [ ] Verificar carga CSV con estructura exacta (`nodes.csv`, `edges.csv`)
- [ ] Verificar mapa base con TODAS las vías (`.png` en `src/main/resources/map/`)
- [ ] Verificar carga perezosa según decisión
- [ ] Verificar multilenguaje `es`, `en`, `fr` con archivos `.yml` + extensible (sin `.properties`)
- [ ] Verificar logs con rotación 3 días (`mapRouteTunja-yyyy-MM-dd.log` en `logs/`)
- [ ] Verificar uso exclusivo de ruta vehicular

#### Criterio para FINALIZAR PROYECTO
- [ ] TODOS los ítems de esta fase marcados
- [ ] Sin desviaciones de la especificación
- [ ] Estructura limpia bajo `co.edu.uptc`
- [ ] Cumple todas las reglas al pie de la letra

## 10. DECISIONES TOMADAS
Las decisiones fueron tomadas con base en lo indicado por el desarrollador. Todo lo que queda sin valor exacto se deja parametrizable desde el archivo de configuración o desde el menú correspondiente, sin datos quemados en código.

### A) Grafo
- **Estructura elegida:** Lista de Adyacencia.
- **Justificación:** Para el mapa de Tunja el grafo es disperso. La lista de adyacencia consume menos memoria, es más eficiente para el algoritmo de Dijkstra y se adapta mejor al grado variable de los nodos.

### B) Configuración YAML
- **Tipo de carga:** Carga perezosa.
- **Observación:** Únicamente se cargarán los valores necesarios en el momento de su uso, manteniendo la regla de utilizar exclusivamente el archivo de configuración `.yml` sin datos quemados en código.

### C) Multilenguaje
- **Idiomas principales:** Español (`es`), Inglés (`en`), Francés (`fr`).
- **Formato de archivos:** Se deberán utilizar archivos `.yml` ubicados en `src/main/resources/i18n/` (ej.: `es.yml`, `en.yml`, `fr.yml`).
- **Extensibilidad:** Debe existir un mecanismo que permita añadir más idiomas sin necesidad de modificar código.

### D) Archivos CSV
- **Nombres exactos:** `nodes.csv` y `edges.csv`.
- **Estructura de `nodes.csv`:**
  ```csv
  id,lat,lon
  ```
  - `id`: Identificador numérico único del nodo.
  - `lat`: Latitud geográfica.
  - `lon`: Longitud geográfica.
- **Estructura de `edges.csv`:**
  ```csv
  fromId,toId,oneWay
  ```
  - `fromId`: Identificador del nodo origen.
  - `toId`: Identificador del nodo destino.
  - `oneWay`: `true` si la vía es solo un sentido, `false` si es doble sentido.
- **Peso:** No se almacenará en el archivo CSV. El peso se calculará con las coordenadas geográficas al momento de cargar el grafo.

### E) Mapa base
- **Tipo de archivo:** Imagen `.png`.
- **Ubicación:** `src/main/resources/map/`
- **Nombre propuesto:** `tunjaMap.png` (parametrizable desde `application.yml`).
- **Coordenadas geográficas <-> píxeles:** Las coordenadas geográficas para la conversión serán proporcionadas por el usuario. **NO** serán configurables desde la interfaz. Se almacenarán en `application.yml` o se obtendrán desde los metadatos de la imagen `tunjaMap.png`. Únicamente se leerán para realizar la conversión. 

### F) Interfaz / Boceto
- **Menú de configuración:** Se creará un menú **`Preferencias`** en la barra de menús **únicamente para ajustes visuales o estéticos** (tamaño del nodo, color de la ruta seleccionada, grosor de línea, color de elementos gráficos, entre otros).
- **Sentido de vía:** Se configurará al momento de crear la arista desde `Via > Crear nueva arista`, permitiendo elegir entre "solo un sentido" o "doble sentido".
- **Accesos rápidos:** Únicamente **`Alt + <letra>`**.
- **Radio de tolerancia:** **10px**. Parámetro técnico. Se configurará únicamente desde `application.yml`.

### G) Logs
- **Ruta:** `logs/` en la raíz del proyecto.
- **Rotación:** Cada **3 días**.
- **Formato de nombre:** `mapRouteTunja-yyyy-MM-dd.log`
- **Implementación:** Mediante `java.util.logging` con `FileHandler` configurado para rotación cada 3 días.

### H) Extras
- **Estado:** Sin elementos adicionales pendientes del boceto por el momento.

## 11. ESTADO ACTUAL
- **Fase actual:** FASE 0 - ANÁLISIS Y APROBACIÓN (COMPLETADA)
- **Siguiente fase:** FASE 1 - REESTRUCTURACIÓN A CO.EDU.UPTC