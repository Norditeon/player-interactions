# Interacciones Amistosas

Mod para **Minecraft Java 1.21.1 + NeoForge 21.1** que añade interacciones entre jugadores (afectuosas,
de amistad y también golpes y bromas), entregas de objetos y una ruleta de emociones. Las dos ruedas
se personalizan desde un repertorio.

## Qué hace

### Interacciones entre dos (clic derecho con la mano vacía)

Se abre una rueda con 8 casillas que eliges tú (`E` con la rueda abierta abre el repertorio).

| Tipo | Interacciones |
|---|---|
| Gestos (piden permiso) | abrazar, chocar los cinco, estrechar la mano, beso en la mejilla, beso en la boca, palmaditas en la cabeza, chocar puños, tomar de la mano, saludo de colegas, besar la mano, hacer cosquillas, consolar, bailar juntos |
| Golpes y bromas (sin permiso) | cachetada (½ ♥), puñetazo (1 ♥), empujar, patada en el trasero (1 ♥), nalgada |

- **Pedir** (por defecto): el otro ve un aviso y responde con `Y` / `N`. Si no responde en 15 s, caduca.
- **Forzar** (centro de la rueda o `Tab`): el gesto ocurre sin preguntar y el otro **reacciona distinto**:
  si le fuerzas un beso se echa atrás y se limpia la boca, si le abrazas se queda tieso y forcejea, etc.
- **Golpes y bromas**: nunca preguntan. El daño cuenta como un ataque tuyo, así que respeta el PvP del
  servidor; se marcan en rojo en la rueda. La patada y la nalgada se hacen **por detrás**: el otro queda de espaldas.

Al ocurrir, los dos se deslizan solos (~0.3 s) hasta la distancia justa del gesto. El gesto se anima por fases,
con partículas y sonido; el golpe llega justo cuando la mano (o el pie) toca al otro.

### Entregar objetos y flores (clic derecho con algo en la mano)

1. Quien da extiende la mano y espera. Con una **flor** (etiqueta `minecraft:flowers`) lo hace con vergüenza.
2. Quien recibe ve el aviso con el icono y la cantidad ("Ana te ofrece 12× Diamante") y acepta (`Y`) o rechaza (`N`).
3. Si acepta, se acercan y el objeto pasa a su inventario cuando las manos se juntan (si no cabe, cae a sus pies).

Se entrega el montón entero de la mano; las flores, de una en una. Agachado, el clic derecho usa el objeto como siempre.

### Ruleta de emociones (tecla `G`)

8 casillas que eliges tú: con la ruleta abierta, `E` edita las emociones e `I` la **rueda de interacciones**
(así no hace falta tener a otro jugador delante para configurarla). Las que se repiten siguen **aunque camines**, hasta que
las cortas: abre la ruleta y elige una casilla vacía o el centro ("Detener"). Muchas sueltan partículas
(lágrimas al llorar, una nube que te llueve encima, notas al bailar, corazones al estar enamorado…).

Repertorio (41): bailar, baile robot, perreo, saltar de alegría, saludar, aplaudir, celebrar, reír, llorar,
sentarse, mirar el horizonte, acostarse, meditar, enojarse, patalear, golpear el suelo, brazos cruzados, pensar,
encogerse de hombros, mano a la cara, timidez, enamorado, beso al aire, suplicar, presumir, pose de héroe,
pose sexy, derrotado bajo la lluvia, reverencia, saludo militar, señalar, ¡alto!, bostezar, dar un giro,
voltereta mortal, entrenar puñetazos, caminar sigiloso (te agacha de verdad: lento y sin caer de los bordes),
y las que usan **el objeto de tu mano**: comer, practicar con la espada, malabares y lanzar (lo tira de verdad, lejos).

### Idiomas

Según el idioma del juego: español (todas sus variantes), inglés, francés, alemán, ruso, japonés y chino simplificado.

## Instalar

1. Instala NeoForge 21.1.x para Minecraft 1.21.1.
2. Copia `build/libs/interacciones-1.0.0.jar` a la carpeta `mods/` **del cliente y del servidor** (obligatorio en ambos).

## Configuración

**Servidor** — `config/interacciones-server.toml` (se sincroniza a los clientes):

| Clave | Por defecto | Qué hace |
|---|---|---|
| `permitirForzar` | `true` | Si es `false`, el modo Forzar desaparece de la rueda y el servidor lo rechaza. |
| `permitirGolpes` | `true` | Si es `false`, no se pueden hacer golpes ni bromas sin permiso. |
| `distanciaMaxima` | `2.5` | Bloques máximos para pedir o entregar; al ocurrir se acercan solos. |
| `segundosParaResponder` | `15` | Tiempo para aceptar o rechazar. |
| `segundosEntreInteracciones` | `3` | Espera mínima entre peticiones del mismo jugador. |

**Cliente** — `config/interacciones-client.toml`: `ruletaEmociones` y `ruedaInteracciones` (mejor desde el juego con `E`).

Las teclas (aceptar, rechazar, ruleta) se cambian en *Opciones → Controles → Interacciones amistosas*.

## Desarrollo

Requiere **JDK 21** (`mise.toml` lo fija; `mise install` lo descarga). Gradle usa el wrapper incluido.

```sh
export JAVA_HOME="$(mise where java)"
./gradlew build        # compila, pasa los tests y genera build/libs/interacciones-1.0.0.jar
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
