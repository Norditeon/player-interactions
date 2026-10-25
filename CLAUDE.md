# CLAUDE.md — Interacciones Amistosas

Mod NeoForge 21.1.264 para Minecraft 1.21.1, en **Java 21** (NeoForge exige Java; Rust no es viable aquí).
Paquete `com.leonardo.interacciones`, mod id `interacciones`. Mapa del código y flujo en `README.md`.

## Arquitectura
- `dominio/` y `animacion/`: lógica pura sin imports de Minecraft → testeable con JUnit (`src/test`). Mantenerlo así.
- Flujo de un gesto: `GestorSolicitudes.pedir` (valida + `PlanAcercamiento`) → `InteraccionesEnCurso` (ACERCANDO 6 ticks
  con teleport absoluto + banderas relativas, luego ACTUANDO, corta si se separan) → `EjecutorInteraccion`.
- Entregas (`TipoInteraccion.entregaObjeto()`): `GestorEntregas` guarda la oferta ligada a (dador, receptor) y pone al
  dador la emoción OFRECER_*; el objeto cambia de inventario en `InteraccionesEnCurso` al llegar a `MOMENTO_ENTREGA`,
  que coincide con el fotograma de `CatalogoAnimaciones` donde se juntan las manos.
- Emociones: `EmocionesEnCurso` (servidor, una por jugador; las elegibles se cortan al moverse) → `ReproducirEmocion`.
  En el cliente todo pasa por `AnimacionesActivas` (gestos, emociones, bucles y desvanecido al cortar).
- La ruleta de cada jugador vive en `ConfiguracionCliente` (config CLIENT, claves de `TipoEmocion`).
- `red/`: records `CustomPacketPayload` + `StreamCodec`. Los enums viajan por índice (`CodecsDominio`).
- `servidor/`: autoridad. Todo se valida aquí (distancia, enfriamiento, `permitirForzar`); el cliente nunca decide.
- `cliente/`: rueda (`Screen` propio), aviso en HUD (`RegisterGuiLayersEvent`), animaciones.
  `RegistroRed` llama a `ReceptorCliente` solo dentro de lambdas para no cargar clases de cliente en servidor dedicado.
- `mixin/PoseJugadorMixin`: inyecta en `PlayerModel.setupAnim` HEAD (`resetPose` de todas las piezas: el modelo es
  compartido entre jugadores y vainilla no reinicia `head.zRot` ni posiciones de torso) y TAIL (aplica la pose de
  `CatalogoAnimaciones`). Tras modificar piezas hay que copiar capas exteriores. `edadEnTicks == 0` = mano en primera persona.
- `ServerGamePacketListenerImpl.teleport`: x/y/z son SIEMPRE absolutos en el servidor; las banderas relativas solo
  afectan al paquete al cliente (pasar 0,0,0 manda al jugador a las coordenadas 0 0 0).

## Convenciones
- Nombres y comentarios en español; cada archivo empieza con un comentario de cabecera (qué hace, de quién recibe, a quién da).
- Colores solo en `ColoresInterfaz` (tokens del sistema de diseño terminal: radio 0, un acento, líneas de 1 px).
- Textos solo en `lang/es_es.json` y `lang/en_us.json` (las variantes es_* se copian al compilar desde es_es);
  `TraduccionesTest` exige que ambos tengan las mismas claves y todas las dinámicas.
- Animaciones: si una pieza se mueve en algún fotograma de peso > 0, se define en todos (null = "lo del fotograma vecino").
- Suscriptores `@EventBusSubscriber` públicos con métodos `public static`; sin parámetro `bus` (obsoleto).

## Comandos
`export JAVA_HOME="$(mise where java)"` y luego `./gradlew build | test | runClient | runServer`.
Config de pruebas: `run/config/interacciones-server.toml`; `run/server.properties` tiene `online-mode=false`.
