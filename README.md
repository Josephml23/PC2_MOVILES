INTEGRANTES 
1.- Kana Huillcapacco Cesar Augusto 
2.- Villar Collantes Alexis Andree
3.- Joseph Fredy Marcani Leyva 

# StockMin

App Android de control de inventario para almacen en campo minero.
Funciona 100% offline, persiste localmente con Room y aplica MVVM + UDF sobre Jetpack Compose y Material 3.

---

## 1. Requisitos

- Android Studio Koala o superior
- JDK 17
- Gradle 8.5 (incluido con AGP 8.5.2)
- minSdk 24, targetSdk 34

## 2. Como abrir el proyecto

1. En Android Studio: **File -> Open** y selecciona la carpeta raiz `StockMin`.
2. Espera a que termine el Gradle sync (descargara las dependencias).
3. Selecciona un emulador (API 26+) o un dispositivo fisico.
4. Run **app**.

> No requiere configurar Firebase, Google Services, ni permisos de internet.

## 3. Estructura del proyecto

```
com.tecsup.stockmin
├── data
│   ├── local
│   │   ├── dao              # ItemDao, MovimientoDao
│   │   ├── entity           # ItemEntity, MovimientoEntity
│   │   └── StockMinDatabase
│   └── repository
│       ├── Mappers          # Entity <-> Domain
│       └── StockRepository  # Punto unico de acceso a datos
├── domain
│   └── model                # Item, Movimiento, Categoria, TipoMovimiento
├── ui
│   ├── components           # Utilidades (Fechas)
│   ├── navigation           # Rutas + NavGraph
│   ├── screens
│   │   ├── catalog          # Pantalla 1
│   │   ├── form             # Pantalla 2
│   │   └── detail           # Pantalla 3
│   └── theme                # Material 3 + dynamic color
├── viewmodel
│   ├── CatalogoViewModel
│   ├── FormItemViewModel
│   ├── DetalleViewModel
│   └── StockMinVMFactory
├── MainActivity
└── StockMinApp              # Service Locator manual
```

## 4. Funcionalidades cubiertas (brief)

| Requisito                                                    | Implementado |
|--------------------------------------------------------------|:-:|
| Catalogo con nombre, categoria, stock actual y stock minimo  | OK |
| Categorias: Herramienta / Insumo / EPP                       | OK |
| Registrar movimiento (salida/entrada) con trabajador y fecha | OK |
| Alerta visual cuando stock < minimo                          | OK |
| Historial de movimientos por item                            | OK |
| Buscar item por nombre                                       | OK |
| Persistencia local (Room)                                    | OK |
| Funciona sin internet                                        | OK |
| Tres pantallas minimas (catalogo, formulario, detalle)       | OK |

Y, segun el brief, NO se implementan:
sincronizacion en la nube, lectura QR/barras, exportacion a PDF, login.

## 5. Pregunta de sustento

> ¿Como hiciste para que la UI reaccione automaticamente cuando el stock cambia tras un movimiento? ¿Que patron usaste?

La UI reacciona sola gracias al patron **MVVM + UDF (Unidirectional Data Flow)** apoyado en **Flows reactivos de Room**.

El recorrido de un movimiento es asi:

1. **DAO reactivo.** `ItemDao.observarPorId(id)` y `MovimientoDao.observarPorItem(itemId)` no devuelven listas estaticas: devuelven `Flow<...>`. Cada vez que la tabla cambia, Room emite un nuevo valor automaticamente.
2. **Repository.** `StockRepository.observarItem(...)` y `observarMovimientos(...)` mapean esos Flows de entidades a Flows de modelos de dominio (`Flow<Item?>`, `Flow<List<Movimiento>>`). La UI nunca toca el DAO.
3. **ViewModel.** `DetalleViewModel` combina ambos Flows con `combine(...)` y los expone como un unico `StateFlow<DetalleUiState>` mediante `stateIn(...)`. Es la **unica fuente de verdad** para la pantalla.
4. **UI Compose.** La pantalla observa con `collectAsStateWithLifecycle()`. Cualquier nuevo valor del StateFlow dispara una recomposicion automatica.
5. **El bucle.** Cuando el usuario confirma un movimiento, el ViewModel llama a `repository.registrarMovimiento(...)`. Adentro:
   - Se inserta el movimiento en la tabla `movimientos`.
   - Se actualiza el stock con `itemDao.actualizarStock(id, nuevoStock)`.
   - Ambos cambios disparan emisiones de los Flows de Room.
   - El `combine` recibe los nuevos valores y produce un nuevo `DetalleUiState`.
   - Compose recompone solo lo que depende de ese estado: la tarjeta de stock cambia el numero, aparece la nueva fila en el historial y, si se cruzo el umbral minimo, el contenedor se pinta en color de error.

La clave es que la UI **no consulta** la base de datos despues del movimiento ni se entera "manualmen<img width="494" height="1002" alt="WhatsApp Image 2026-05-04 at 19 02 42" src="https://github.com/user-attachments/assets/c8c55955-e11d-47c5-8dd3-2453265fdd85" />
<img width="470" height="1022" alt="WhatsApp Image 2026-05-04 at 19 02 29" src="https://github.com/user-attachments/assets/fd8344c1-cac7-4857-94ec-0df9239f4f4f" />
<img width="468" height="1004" alt="WhatsApp Image 2026-05-04 at 19 05 33" src="https://github.com/user-attachments/assets/83916872-4a59-4c5c-9ac5-3a50d0f03c82" />
<img width="490" height="986" alt="WhatsApp Image 2026-05-04 at 19 03 30" src="https://github.com/user-attachments/assets/70a412d9-f998-4727-91fb-10191c590920" />
te" de que algo cambio. Solo se suscribe al StateFlow y reacciona. Todo el flujo va en una sola direccion:

```
Usuario -> Evento -> ViewModel -> Repository -> Room
                                                 |
                                                 v
                                              Flow<Item>, Flow<List<Movimiento>>
                                                 |
                                                 v
              UI <- StateFlow<UiState> <- combine en ViewModel
```

Eso es UDF: los eventos suben, el estado baja, y nunca hay un atajo lateral.

## 6. Datos de prueba

La app arranca con la BD vacia. Para una demostracion rapida, se sugiere agregar manualmente al menos:

- "Taladro percutor" (Herramienta) - stock 5, minimo 2
- "Guantes de nitrilo" (EPP) - stock 12, minimo 20  *(dispara alerta)*
- "Cinta aislante" (Insumo) - stock 8, minimo 3

Luego registra movimientos sobre cada uno para ver la actualizacion en vivo del stock y del historial.

---

**Mauricio Castillo - Tecsup**   
