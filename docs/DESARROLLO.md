# Desarrollo de Player Interactions

Guía interna (en español) para entender y cambiar el código. El `README.md` en inglés es la cara pública
del mod (GitHub y Modrinth). El nombre visible es **Player Interactions**, pero el mod id sigue siendo
`interacciones` y el código sigue en español: cambiar el id rompería las configuraciones y claves ya guardadas.

## Compilar y probar

Requiere **JDK 21** (`mise.toml` lo fija; `mise install` lo descarga). Gradle usa el wrapper incluido.

```sh
export JAVA_HOME="$(mise where java)"
./gradlew build        # compila, pasa los tests y genera build/libs/player-interactions-1.0.0.jar
./gradlew test         # solo los tests de la lógica pura
./gradlew runClient    # abre Minecraft con el mod
./gradlew runServer    # servidor dedicado de pruebas (run/, online-mode=false)
```

**Probar con dos jugadores:** `runServer`, y en dos terminales `./gradlew runClient --args="--username Ana"` y
`--username Beto`; conecta ambos a `localhost` (puede hacer falta `--no-configuration-cache` en el segundo).

## Mapa del código

`src/main/java/com/leonardo/interacciones/`

| Carpeta / archivo | Qué hace |
|---|---|
| `InteraccionesAmistosas.java` | Punto de entrada: registra configuraciones, red y eventos del servidor. |
| `Configuracion.java` | Reglas del servidor (forzar, golpes, distancia, tiempos). |
| `ConfiguracionCliente.java` | Las dos ruedas de cada jugador (emociones e interacciones). |
| `NombreObjeto.java` | Cómo se nombra un objeto en los textos ("3× Diamante"). |
| `dominio/` | Lógica pura, sin Minecraft, con tests. |
| `dominio/TipoInteraccion.java` | **Tabla de interacciones**: duración, distancia, clase, disposición, daño, empuje. |
| `dominio/ClaseInteraccion.java`, `Disposicion.java` | Gesto / sin permiso / entrega; cara a cara / por detrás. |
| `dominio/TipoEmocion.java` | Emociones: duración, si se repiten, si están en el repertorio. |
| `dominio/ModoPeticion.java` | Pedir o Forzar. |
| `dominio/RegistroSolicitudes.java` | Solicitudes pendientes, caducidad y enfriamiento. |
| `dominio/ResultadoPeticion.java` | Motivos de aceptación/rechazo. |
| `dominio/Acercamiento.java` | Dónde queda cada jugador, el trayecto del deslizamiento y hacia dónde mira. |
| `animacion/` | Motor de fotogramas clave y coreografías, lógica pura con tests. |
| `animacion/CatalogoAnimaciones.java` | Registro: qué coreografía anima a cada papel en cada interacción (y forzada). |
| `animacion/GestosAfectuosos.java`, `GolpesYBromas.java`, `Entregas.java`, `ReaccionesForzadas.java` | **Coreografías de las interacciones.** |
| `animacion/CatalogoEmociones.java` | Registro de emociones (y las primeras coreografías). |
| `animacion/EmocionesConObjeto.java`, `EmocionesDeCuerpoEntero.java`, `EmocionesDePose.java` | **Coreografías de las emociones nuevas.** |
| `animacion/MomentosEmocion.java`, `ObjetoLanzado.java` | Momentos de efecto y vuelo del objeto, compartidos con el servidor. |
| `animacion/Postura.java`, `Giro.java` | Una pose: piezas, elevación, vuelta y voltereta (convenciones en la cabecera). |
| `animacion/Animacion.java`, `Fotograma.java`, `Suavizado.java`, `Papel.java` | Secuencia, pose clave, curvas y papel. |
| `red/` | Los paquetes de red y su registro (`RegistroRed`). |
| `servidor/GestorSolicitudes.java` | Valida peticiones, consentimiento, caducidad. |
| `servidor/GestorEntregas.java` | Ofertas de objetos y el traspaso de inventario. |
| `servidor/EmocionesEnCurso.java` | Emociones de cada jugador; las reenvía a quien empieza a verlo. |
| `servidor/EfectosEmocion.java` | Partículas, sonidos, comer y lanzar de cada emoción. |
| `servidor/PlanAcercamiento.java` | Comprueba si pueden juntarse (paredes, altura, estado). |
| `servidor/InteraccionesEnCurso.java` | Fases de cada interacción: acercar, animar, contacto (entrega o golpe), cortar. |
| `servidor/EjecutorInteraccion.java` | Empieza o corta el gesto en los clientes; daño, retroceso, partículas y sonido. |
| `servidor/AvisosJugador.java` | Mensajes en la barra de acción. |
| `cliente/ClicEnJugador.java` | Clic derecho sobre un jugador: rueda (mano vacía) u ofrecer (con objeto). |
| `cliente/RuedaRadial.java`, `PantallaRueda.java`, `PantallaRuedaEmociones.java` | Base de las ruedas y las dos ruedas. |
| `cliente/PantallaRepertorio.java` | Editor genérico de cualquiera de las dos ruedas. |
| `cliente/BarrasPantalla.java`, `DibujoRueda.java`, `ColoresInterfaz.java` | Barras, dibujo del anillo y tokens de color. |
| `cliente/AvisoSolicitud.java`, `SolicitudActual.java` | Aviso "X quiere…" / "X te ofrece…" y respuesta con teclas. |
| `cliente/TeclasInteraccion.java`, `AbrirRuedaEmociones.java`, `RegistroCliente.java` | Teclas, abrir la ruleta, altas del cliente. |
| `cliente/ReceptorCliente.java` | Recibe los paquetes del servidor. |
| `cliente/AnimacionesActivas.java` | Qué anima cada jugador, bucles y desvanecido al cortar. |
| `cliente/PosesInteraccion.java` | Reinicia el modelo y le aplica la pose (piezas). |
| `cliente/TransformacionCuerpo.java` | Mueve el cuerpo entero al dibujar (elevación, vuelta, voltereta). |
| `cliente/ObjetoEnElAire.java` | Dibuja el objeto volando en el malabar y oculta el de la mano. |
| `cliente/SigiloAgachado.java` | Mantiene pulsado "agacharse" durante la emoción de sigilo. |
| `mixin/PoseJugadorMixin.java` | Engancha `PosesInteraccion` a `PlayerModel.setupAnim`. |
| `mixin/ObjetoEnManoMixin.java` | Deja de dibujar el objeto de la mano mientras vuela. |

Recursos: `src/main/resources/assets/interacciones/lang/` (en, es, fr, de, ru, ja, zh; las demás variantes de español
se copian de `es_es.json` al compilar), `interacciones.mixins.json` y `src/main/templates/META-INF/neoforge.mods.toml`.

## Flujo de datos

Interacciones y entregas:

```
[Cliente A] clic derecho → ClicEnJugador ─┬─ mano vacía → PantallaRueda ──PedirInteraccion──┐
                                           └─ con objeto ──────────────────OfrecerObjeto────┤
                                                                                             ▼
                                   [Servidor] GestorSolicitudes (+ GestorEntregas para objetos)
                                     valida: golpes/forzar permitidos, distancia, enfriamiento, PlanAcercamiento
                     gesto en modo PEDIR / entrega ──SolicitudRecibida──▶ [Cliente B] AvisoSolicitud ──Y/N──┐
                     modo FORZAR o golpe ────────────────────────────────────────────────────────────────┤
                                                                                                          ▼
                                   InteraccionesEnCurso: ACERCANDO (6 ticks) → ACTUANDO
                                     contacto: GestorEntregas.entregar o EjecutorInteraccion.impactar
            ◀──ReproducirInteraccion(forzada) / DetenerAnimacion── AnimacionesActivas ← CatalogoAnimaciones
                                                                    → PosesInteraccion + TransformacionCuerpo
```

Emociones:

```
G → PantallaRuedaEmociones ──HacerEmocion / DetenerMiEmocion──▶ EmocionesEnCurso → EfectosEmocion (partículas)
            ◀──ReproducirEmocion / DetenerAnimacion── AnimacionesActivas ← CatalogoEmociones
                                       → PosesInteraccion, TransformacionCuerpo, ObjetoEnElAire, SigiloAgachado
```

## Cómo cambiar…

- **Añadir una interacción:** una fila en `TipoInteraccion` (clave, duración, distancia, clase, disposición, daño,
  empuje), su coreografía en el archivo del tema y su registro en `CatalogoAnimaciones`, sus casos en
  `EjecutorInteraccion.particulaDe` y `sonidoDe` (el compilador avisa), y sus 4 textos (`nombre`, `pedido`, `hiciste`,
  `recibiste`) en los dos idiomas (un test avisa si falta alguno). Si es un gesto con permiso, puedes darle una
  reacción forzada en `ReaccionesForzadas` (si no, usa la normal).
- **Añadir una emoción:** una fila en `TipoEmocion`, su caso en `CatalogoEmociones.crear`, su texto
  `emocion.interacciones.<clave>` y, si quieres, partículas en `EfectosEmocion.alAvanzar`.
- **Cambiar el daño o el empuje de un golpe:** columnas de `TipoInteraccion` (2 de daño = un corazón).
- **Cambiar una animación:** sus fotogramas (momento 0..1, curva y pose). Convenciones en la cabecera de `Postura.java`.
- **Cambiar las ruedas por defecto:** `RULETA_POR_DEFECTO` y `RUEDA_POR_DEFECTO` en `ConfiguracionCliente`.
- **Cambiar qué cuenta como flor:** `ItemTags.FLOWERS` en `GestorEntregas.alOfrecer`.
- **Cambiar colores:** solo en `ColoresInterfaz`. **Textos:** `lang/*.json` (`en_us.json` es la referencia).
- **Añadir un idioma:** copia `en_us.json` con el código de Minecraft (p. ej. `pt_br.json`), tradúcelo y añádelo a
  `IDIOMAS` en `TraduccionesTest`, que comprueba que tenga todas las claves y los mismos `%s`.
- **Cambiar el formato de un paquete:** súbelo en `RegistroRed.VERSION_PROTOCOLO`.
- **Cambiar el nombre visible del mod:** `mod_name` en `gradle.properties` (y `key.categories.interacciones` en `lang/`);
  el del `.jar` y la dirección de Modrinth, `mod_slug`.
- **Cambiar el icono:** edita `docs/icono.svg` (la rueda con los colores de `ColoresInterfaz`) y regenera el PNG del mod con
  `rsvg-convert -w 256 -h 256 docs/icono.svg -o src/main/resources/icon.png`. En Modrinth se sube a mano (o con la API).

## Publicar en Modrinth

1. Sube `mod_version` en `gradle.properties` y añade su sección `## <versión>` arriba en `CHANGELOG.md` (en inglés): es el changelog de Modrinth.
2. Ten tu token en `~/.gradle/gradle.properties` como `modrinthToken=mrp_...` (o en `MODRINTH_TOKEN`). Nunca en el repo.
3. `./gradlew modrinth -PmodrinthPrueba` muestra lo que se enviaría sin subir nada.
4. `./gradlew modrinth` compila y sube la versión al proyecto `mod_slug` y actualiza su descripción con el `README.md` (lo que esté entre `<!-- modrinth_exclude.start -->` y `.end -->` no se sube).
