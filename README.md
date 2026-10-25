# Interacciones Amistosas

Mod para **Minecraft Java 1.21.1 + NeoForge 21.1** que añade interacciones afectuosas entre jugadores,
entregas de objetos y una ruleta de emociones personalizable.

## Qué hace

### Gestos entre dos (clic derecho con la mano vacía)

Se abre una rueda con 8 opciones:

| # | Interacción | Partículas |
|---|---|---|
| 1 | Abrazar | corazones |
| 2 | Chocar los cinco | chispas |
| 3 | Estrechar la mano | chispas |
| 4 | Beso en la mejilla | corazones |
| 5 | Beso en la boca | corazones |
| 6 | Palmaditas en la cabeza | corazones |
| 7 | Chocar puños | chispas |
| 8 | Tomar de la mano | corazones |

El centro de la rueda (o `Tab`) cambia el modo:

- **Pedir** (por defecto): el otro jugador ve un aviso y responde con `Y` (aceptar) o `N` (rechazar). Si no responde en 15 s, caduca.
- **Forzar**: la interacción ocurre sin preguntar. El receptor ve "(sin preguntar)" en el mensaje. El servidor puede desactivarlo.

Al ocurrir, los dos jugadores se deslizan solos (~0.3 s) hasta la distancia justa del gesto y quedan cara a cara;
después se anima el gesto por fases (preparación → contacto → mantener → soltar), salen partículas y suena un efecto.
La cámara no cambia. Si os separáis a mitad, el gesto se corta. No se puede si hay una pared en medio,
estáis a distinta altura o alguien duerme, monta, vuela con élitros o nada.

### Entregar objetos y flores (clic derecho con algo en la mano)

1. Quien da extiende la mano con el objeto y espera. Con una **flor** (cualquiera de la etiqueta
   `minecraft:flowers`) lo hace con vergüenza: mira al suelo, mano en la nuca, se mece y salen corazones.
2. Quien recibe ve un aviso con el icono y la cantidad ("Ana te ofrece 12× Diamante") y acepta (`Y`) o rechaza (`N`).
3. Si acepta, se acercan, quien recibe alarga la mano y el objeto pasa a su inventario cuando las manos
   se juntan (si no cabe, cae a sus pies).

Se entrega **el montón entero** que tengas en la mano (para dar menos, sepáralo antes); las flores, de una en una.
Agachado (`Shift`) el clic derecho usa el objeto como siempre, sin ofrecerlo.

### Ruleta de emociones (tecla `G`)

8 casillas con emociones que haces tú solo. Las que se repiten (bailar, llorar, sentarse…) siguen hasta
que las cortas o te mueves; el resto dura unos segundos. **Para cortar la emoción en curso**, abre la ruleta
y elige una casilla vacía o el centro ("Detener").

Con la ruleta abierta, `E` abre el **repertorio**: eliges una casilla (clic o `1`-`8`) y luego la emoción
que quieres en ella; clic derecho o `Supr` vacía la casilla. Se guarda al salir con `Esc`.

Repertorio (21): bailar, baile robot, saltar de alegría, saludar, aplaudir, celebrar, reír, llorar, sentarse,
enojarse, patalear, brazos cruzados, pensar, encogerse de hombros, mano a la cara, timidez, presumir,
reverencia, saludo militar, señalar y bostezar.

### Idiomas

Español e inglés, según el idioma del juego. El español sirve para todas las variantes de Minecraft
(es_es, es_mx, es_ar, es_cl, es_ec, es_uy, es_ve).

## Instalar

1. Instala NeoForge 21.1.x para Minecraft 1.21.1.
2. Copia `build/libs/interacciones-1.0.0.jar` a la carpeta `mods/` **del cliente y del servidor** (el mod es obligatorio en ambos lados).

## Configuración

**Servidor** — `config/interacciones-server.toml` (se crea al primer arranque y se sincroniza a los clientes):

| Clave | Por defecto | Qué hace |
|---|---|---|
| `permitirForzar` | `true` | Si es `false`, el modo Forzar desaparece de la rueda y el servidor lo rechaza. |
| `distanciaMaxima` | `2.5` | Bloques máximos para pedir o entregar; al ocurrir se acercan solos hasta tocarse. |
| `segundosParaResponder` | `15` | Tiempo para aceptar o rechazar. |
| `segundosEntreInteracciones` | `3` | Espera mínima entre peticiones del mismo jugador. |

**Cliente** — `config/interacciones-client.toml`: `ruletaEmociones`, las 8 casillas de tu ruleta
(mejor editarla desde el juego con `G` → `E`).

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

**Probar con dos jugadores:** arranca `runServer`, y en dos terminales
`./gradlew runClient --args="--username Ana"` y `./gradlew runClient --args="--username Beto"`;
conecta ambos a `localhost`. (Con la caché de configuración de Gradle puede hacer falta
lanzar el segundo cliente con `--no-configuration-cache`.)

## Mapa del código

`src/main/java/com/leonardo/interacciones/`

| Carpeta / archivo | Qué hace |
|---|---|
| `InteraccionesAmistosas.java` | Punto de entrada: registra configuraciones, red y eventos del servidor. |
| `Configuracion.java` | Reglas del servidor (forzar, distancia, tiempos). |
| `ConfiguracionCliente.java` | La ruleta de emociones de cada jugador. |
| `NombreObjeto.java` | Cómo se nombra un objeto en los textos ("3× Diamante"). |
| `dominio/` | Lógica pura, sin Minecraft, con tests. |
| `dominio/TipoInteraccion.java` | Gestos de la rueda y entregas: duración, distancia de contacto, claves de texto. |
| `dominio/TipoEmocion.java` | Emociones: duración, si se repiten, si están en el repertorio. |
| `dominio/ModoPeticion.java` | Pedir o Forzar. |
| `dominio/RegistroSolicitudes.java` | Solicitudes pendientes, caducidad y enfriamiento. |
| `dominio/ResultadoPeticion.java` | Motivos de aceptación/rechazo. |
| `dominio/Acercamiento.java` | Dónde queda cada jugador para tocarse y el trayecto del deslizamiento. |
| `animacion/` | Motor de fotogramas clave, lógica pura con tests. |
| `animacion/CatalogoAnimaciones.java` | **La coreografía de cada gesto entre dos** (actor y receptor). |
| `animacion/CatalogoEmociones.java` | **La coreografía de cada emoción** (y las poses de ofrecer). |
| `animacion/Postura.java`, `Giro.java` | Una pose: peso, cabeza, torso, brazos, piernas, elevación (convenciones en la cabecera). |
| `animacion/Animacion.java`, `Fotograma.java`, `Suavizado.java`, `Papel.java` | Secuencia, pose clave, curvas de velocidad y papel. |
| `red/` | Los 9 paquetes de red y su registro (`RegistroRed`). |
| `servidor/GestorSolicitudes.java` | Valida peticiones, gestiona el consentimiento, caduca solicitudes. |
| `servidor/GestorEntregas.java` | Ofertas de objetos y el traspaso de un inventario a otro. |
| `servidor/EmocionesEnCurso.java` | Emociones de cada jugador; las corta si se mueve. |
| `servidor/PlanAcercamiento.java` | Comprueba si pueden juntarse (paredes, altura, estado) y calcula destinos. |
| `servidor/InteraccionesEnCurso.java` | Fases de cada gesto: acercar, animar, entregar el objeto, cortar si se separan. |
| `servidor/EjecutorInteraccion.java` | Empieza o corta el gesto en los clientes; partículas y sonido. |
| `servidor/AvisosJugador.java` | Mensajes en la barra de acción. |
| `cliente/ClicEnJugador.java` | Clic derecho sobre un jugador: rueda (mano vacía) u ofrecer (con objeto). |
| `cliente/RuedaRadial.java` | Base de las ruedas: anillo, centro, ratón y teclado. |
| `cliente/PantallaRueda.java` | Rueda de gestos (Pedir / Forzar). |
| `cliente/PantallaRuedaEmociones.java` | Ruleta de emociones. |
| `cliente/PantallaRepertorio.java` | Editor de la ruleta. |
| `cliente/BarrasPantalla.java` | Barras superior e inferior comunes a las pantallas. |
| `cliente/DibujoRueda.java` | Dibuja sectores y líneas del anillo con triángulos. |
| `cliente/ColoresInterfaz.java` | Tokens de color (única fuente). |
| `cliente/AvisoSolicitud.java` | Aviso "X quiere abrazarte" / "X te ofrece…" y respuesta con teclas. |
| `cliente/SolicitudActual.java` | La solicitud pendiente de responder en este cliente. |
| `cliente/TeclasInteraccion.java` | Teclas Aceptar / Rechazar / Ruleta. |
| `cliente/AbrirRuedaEmociones.java` | Abre la ruleta al pulsar su tecla. |
| `cliente/RegistroCliente.java` | Alta de teclas y de la capa del HUD. |
| `cliente/ReceptorCliente.java` | Recibe los paquetes del servidor. |
| `cliente/AnimacionesActivas.java` | Qué anima cada jugador, bucles y desvanecido al cortar. |
| `cliente/PosesInteraccion.java` | Reinicia el modelo y le aplica la pose en curso. |
| `mixin/PoseJugadorMixin.java` | Engancha `PosesInteraccion` al principio y al final de `PlayerModel.setupAnim`. |

Recursos: `src/main/resources/assets/interacciones/lang/` (`es_es.json` y `en_us.json`; las demás variantes
de español se copian de `es_es.json` al compilar, ver `build.gradle`), `interacciones.mixins.json`,
y `src/main/templates/META-INF/neoforge.mods.toml` (metadatos del mod).

## Flujo de datos

Gestos y entregas:

```
[Cliente A] clic derecho → ClicEnJugador ─┬─ mano vacía → PantallaRueda ──PedirInteraccion──┐
                                           └─ con objeto ──────────────────OfrecerObjeto────┤
                                                                                             ▼
                                   [Servidor] GestorSolicitudes (+ GestorEntregas para objetos)
                                     valida: distancia, enfriamiento, forzar, PlanAcercamiento
                                     (entrega: EmocionesEnCurso pone a A a ofrecer)
                     modo PEDIR ──SolicitudRecibida──▶ [Cliente B] AvisoSolicitud ──ResponderSolicitud (Y/N)──┐
                     modo FORZAR ─────────────────────────────────────────────────────────────────────────┤
                                                                                                           ▼
                                   InteraccionesEnCurso: ACERCANDO (6 ticks) → ACTUANDO
                                     (entrega: GestorEntregas.entregar a mitad del gesto)
            ◀──ReproducirInteraccion / DetenerAnimacion── AnimacionesActivas → PosesInteraccion ← CatalogoAnimaciones
```

Emociones:

```
G → PantallaRuedaEmociones ──HacerEmocion / DetenerMiEmocion──▶ EmocionesEnCurso (corta si te mueves)
            ◀──ReproducirEmocion / DetenerAnimacion── AnimacionesActivas → PosesInteraccion ← CatalogoEmociones
```

## Cómo cambiar…

- **Añadir un gesto a la rueda:** nuevo valor en `TipoInteraccion` (clave, duración, distancia de contacto,
  `false`), su coreografía en `CatalogoAnimaciones`, su caso en `EjecutorInteraccion.particulaDe` y `sonidoDe`
  (el compilador avisa si falta), y sus 4 textos (`nombre`, `pedido`, `hiciste`, `recibiste`) en
  `es_es.json` y `en_us.json` (un test avisa si falta alguno).
- **Añadir una emoción al repertorio:** nuevo valor en `TipoEmocion` (clave, duración, cíclica, `true`), su caso en
  `CatalogoEmociones.crear` y su texto `emocion.interacciones.<clave>` en los dos idiomas. Un test comprueba que
  las cíclicas cierren el bucle y las demás vuelvan al reposo.
- **Cambiar una animación:** sus fotogramas en `CatalogoAnimaciones` o `CatalogoEmociones` (momento 0..1, curva y pose).
  Los ángulos son radianes; las convenciones están en la cabecera de `Postura.java`.
- **Cambiar la ruleta por defecto:** `RULETA_POR_DEFECTO` en `ConfiguracionCliente`.
- **Cambiar qué cuenta como flor:** `ItemTags.FLOWERS` en `GestorEntregas.alOfrecer`.
- **Cambiar lo cerca que quedan:** `distanciaContacto` en `TipoInteraccion` (bloques entre centros; por
  debajo de 0.6 los cuerpos se empujan). Si cambias mucho una, revisa los brazos en el catálogo.
- **Cambiar colores de la interfaz:** solo en `ColoresInterfaz`.
- **Cambiar textos:** `lang/es_es.json` y `lang/en_us.json`.
- **Cambiar las reglas por defecto:** `Configuracion.java`.
- **Cambiar el formato de un paquete:** súbelo en `RegistroRed.VERSION_PROTOCOLO`.
