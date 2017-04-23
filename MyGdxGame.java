package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;

import javax.swing.JOptionPane;
import javax.swing.text.View;

/**
 * Mi primer juego
 * Catalina Saavedra
 */
public class MyGdxGame extends ApplicationAdapter implements InputProcessor
{
	//Objeto que recoge el mapa de baldosas
	private TiledMap mapa;

	//Objeto con el que se pinta el mapa de baldosas
	private OrthogonalTiledMapRenderer mapaRenderer;

	// Cámara que nos da la vista del juego
	private OrthographicCamera camara;

	// Atributo en el que se cargará la hoja de sprites del mosquetero.
	private Texture img;
	//Atributo que permite dibujar imágenes 2D, en este caso el sprite.
	private SpriteBatch sb;
	// Atributo que permitirá la representación de la imagen de textura anterior.
	private Sprite sprite;

	//Constantes que indican el número de filas y columnas de la hoja de sprites.
	private static final int FRAME_COLS = 3;
	private static final int FRAME_ROWS = 4;

	//Animación que se muestra en el método render()
	private Animation jugador;
	//Animaciones para cada una de las direcciones de movimiento del personaje del jugador.
	private Animation jugadorArriba;
	private Animation jugadorDerecha;
	private Animation jugadorAbajo;
	private Animation jugadorIzquierda;
	// Tamaño del mapa de baldosas.
	private int mapaAncho, mapaAlto;
	//Atributos que indican la anchura y la altura de un tile del mapa de baldosas
	int anchoCelda, altoCelda;
	//Posición actual del jugador.
	private float jugadorX, jugadorY;
	// Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación
	// , servirá para determinar cual es el frame que se debe representar.
	private float stateTime;

	//Contendrá el frame que se va a mostrar en cada momento.
	private TextureRegion cuadroActual;

	//Atributos que indican la anchura y altura del sprite animado del jugador.
	int anchoJugador, altoJugador;

	/**************** NPC **********************/
	//Animaciones posicionales relacionadas con los NPC del juego
	private Animation<TextureRegion> noJugadorArriba;
	private Animation<TextureRegion> noJugadorDerecha;
	private Animation<TextureRegion> noJugadorAbajo;
	private Animation<TextureRegion> noJugadorIzquierda;
	//Array con los objetos Animation de los NPC
	private Animation[] noJugador;
	//Atributos que indican la anchura y altura del sprite animado de los NPC.
	int anchoNoJugador, altoNoJugador;
	//Posición inicial X de cada uno de los NPC
	private float[] noJugadorX;
	//Posición inicial Y de cada uno de los NPC
	private float[] noJugadorY;
	//Posición final X de cada uno de los NPC
	private float[] destinoX;
	//Posición final Y de cada uno de los NPC
	private float[] destinoY;
	//Número de NPC que van a aparecer en el juego
	private static final int numeroNPCs = 10;
	// Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación
	//de los NPC , servirá para determinar cual es el frame que se debe representar.
	private float stateTimeNPC = 0;

	//******************Musica y sonidos**************************+
	// Música de fondo del juego
	private Music musica;

	// Sonidos
	private Sound sonidoPasos;
	private Sound sonidoColisionEnemigo;
	private Sound sonidoObstaculo;
	private Sound sonidoCaidaAgujero;

	//************************************************************
	/** Numeración de capas:
	 *  Capa 0: Suelo
	 *  Capa 1: Decoracion Suelo
	 *  Capa 2: Agujero
	 *  Capa 3: Barco/Plataforma
	 *  Capa 4: Paredes/Obstaculos
	 *  Capa 5: Decoracion Paredes
	 *  Capa 6: Objetos
	 *  Capa 7: Alto Nivel
	 */
	private static final int CAPA_SUELO = 0;
	private static final int CAPA_DECORACION_SUELO = 1;
	private static final int CAPA_AGUJERO = 2;
	private static final int CAPA_BARCO_PLATAFORMA = 3;
	private static final int CAPA_PAREDES_OBSTACULOS = 4;
	private static final int CAPA_DECORACION_PAREDES = 5;
	private static final int CAPA_OBJETOS = 6;
	private static final int CAPA_ALTO_NIVEL = 7;

	//Estas dos variables almacenaran las celdas donde hay obstaculos/paredes:
	private boolean[][] obstaculos;
	//Capa del mapa del baldosas que contiene los obstáculos
	private TiledMapTileLayer capaObstaculos;

	//Estas dos variables almacenaran las celdas donde hay agujeros:
	private boolean[][] agujerosNegros;
	private TiledMapTileLayer capaAgujerosNegros;

	//Estas dos variables almacenaran las celdas donde hay collares que son los objetos a recolectar:
	private boolean[][] collaresObjetos;
	private TiledMapTileLayer capaCollaresObjetos;
	//contador para saber cuantos collares a juntado ya nuestro personaje
	int cantidadCollaresEncontrados = 6;

	//Variable BitmapFont para pintar texto
	private BitmapFont font;
	//Variable verifica las ciadas a los agujeros
	boolean haCaido = false;
	boolean hayColision = false;
	//Variable vidas.
	int cantidadViedas = 3;
	/**
	 *
	 */
	@Override
	public void create ()
	{
		//Creamos una cámara y la vinculamos con el lienzo del juego.
		//En este caso le damos unos valores de tamaño que haga que el juego
		//se muestre de forma idéntica en todas las plataformas.
		camara = new OrthographicCamera(640, 480);
		//Posicionamos la vista de la cámara para que su vértice inferior izquierdo sea (0,0)
		camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
		//Vinculamos los eventos de entrada a esta clase.
		Gdx.input.setInputProcessor(this);
		camara.update();

		// Cargamos la imagen de los frames del mosquetero en el objeto img de la clase Texture.
		img = new Texture(Gdx.files.internal("luchador.png"));

		//Sacamos los frames de img en un array de TextureRegion.
		TextureRegion[][] tmp = TextureRegion.split(img, img.getWidth() / FRAME_COLS, img.getHeight() / FRAME_ROWS);

		// Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
		// será de 500 milisegundos.
		jugadorArriba = new Animation(0.150f, tmp[3]);
		jugadorDerecha = new Animation(0.150f, tmp[2]);
		jugadorAbajo = new Animation(0.150f, tmp[0]);
		jugadorIzquierda = new Animation(0.150f, tmp[1]);
		//En principio se utiliza la animación del jugador arriba como animación por defecto.
		jugador = jugadorAbajo;
		// Posición inicial del jugador.
		jugadorX = 300;
		jugadorY = 425;
		//Ponemos a cero el atributo stateTime, que marca el tiempo e ejecución de la animación.
		stateTime = 0f;

		//Creamos el objeto SpriteBatch que nos permitirá representar adecuadamente el sprite
		//en el método render()
		sb = new SpriteBatch();

		//Cargamos el mapa de baldosas desde la carpeta de assets
		mapa = new TmxMapLoader().load("miMapa1.tmx");
		mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

		//Determinamos el alto y ancho del mapa de baldosas. Para ello necesitamos extraer la capa
		//base del mapa y, a partir de ella, determinamos el número de celdas a lo ancho y alto,
		//así como el tamaño de la celda, que multiplicando por el número de celdas a lo alto y
		//ancho, da como resultado el alto y ancho en pixeles del mapa.
		TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
		anchoCelda = (int) capa.getTileWidth();
		altoCelda = (int) capa.getTileHeight();
		mapaAncho = capa.getWidth() * anchoCelda;
		mapaAlto = capa.getHeight() * altoCelda;

		//Cargamos en los atributos del ancho y alto del sprite sus valores
		cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime);
		anchoJugador = cuadroActual.getRegionHeight();
		altoJugador = cuadroActual.getRegionHeight();

		//Inicializamos el apartado referente a los NPC
		noJugador = new Animation[numeroNPCs];
		noJugadorX = new float[numeroNPCs];
		noJugadorY = new float[numeroNPCs];
		destinoX = new float[numeroNPCs];
		destinoY = new float[numeroNPCs];

		//Creamos las animaciones posicionales de los NPC
		//Cargamos la imagen de los frames del malo en el objeto img de la clase Texture.
		img = new Texture(Gdx.files.internal("enemigo.png"));

		//Sacamos los frames de img en un array de TextureRegion.
		tmp = TextureRegion.split(img, img.getWidth() / FRAME_COLS, img.getHeight() / FRAME_ROWS);

		// Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
		// será de 150 milisegundos.
		noJugadorArriba = new Animation(0.150f, tmp[3]);
		noJugadorArriba.setPlayMode(Animation.PlayMode.LOOP);
		noJugadorDerecha = new Animation(0.150f, tmp[2]);
		noJugadorDerecha.setPlayMode(Animation.PlayMode.LOOP);
		noJugadorAbajo = new Animation(0.150f, tmp[0]);
		noJugadorAbajo.setPlayMode(Animation.PlayMode.LOOP);
		noJugadorIzquierda = new Animation(0.150f, tmp[1]);
		noJugadorIzquierda.setPlayMode(Animation.PlayMode.LOOP);

		//Cargamos en los atributos del ancho y alto del sprite del monstruo sus valores
		cuadroActual = (TextureRegion) noJugadorAbajo.getKeyFrame(stateTimeNPC);
		anchoNoJugador = cuadroActual.getRegionWidth();
		altoNoJugador = cuadroActual.getRegionHeight();

		//Se inicializan, la animación por defecto y, de forma aleatoria, las posiciones
		//iniciales y finales de los NPC. Para simplificar un poco, los NPC pares, se moveran
		//de forma vertical y los impares de forma horizontal.
		for (int i = 0; i < numeroNPCs; i++) {
			noJugadorX[i] = (float) (Math.random() * mapaAncho);
			noJugadorY[i] = (float) (Math.random() * mapaAlto);

			if (i % 2 == 0) {
				// NPC par => mover de forma vertical
				destinoX[i] = noJugadorX[i];
				destinoY[i] = (float) (Math.random() * mapaAlto);
				//Determinamos cual de las animaciones verticales se utiliza.
				if (noJugadorY[i] < destinoY[i]) {
					noJugador[i] = noJugadorArriba;
				} else {
					noJugador[i] = noJugadorAbajo;
				}
			} else {
				// NPC impar => mover de forma horizontal
				destinoX[i] = (float) (Math.random() * mapaAncho);
				destinoY[i] = noJugadorY[i];
				//Determinamos cual de las animaciones horizontales se utiliza.
				if (noJugadorX[i] < destinoX[i]) {
					noJugador[i] = noJugadorDerecha;
				} else {
					noJugador[i] = noJugadorIzquierda;
				}
			}
		}

		// Ponemos a cero el atributo stateTimeNPC, que marca el tiempo e ejecución de la animación
		// de los NPC.
		stateTimeNPC = 0f;

		//Inicializamos la música de fondo del juego y la reproducimos.
		musica = Gdx.audio.newMusic(Gdx.files.internal("dungeon.mp3"));
		musica.play();

		//Inicializamos los atributos de los efectos de sonido.
		sonidoColisionEnemigo = Gdx.audio.newSound(Gdx.files.internal("qubodup-PowerDrain.ogg"));
		sonidoPasos = Gdx.audio.newSound(Gdx.files.internal("Fantozzi-SandR3.ogg"));
		sonidoObstaculo = Gdx.audio.newSound(Gdx.files.internal("wall.ogg"));
		sonidoCaidaAgujero = Gdx.audio.newSound(Gdx.files.internal("caida.mp3"));


		//Cargamos la capa de los obstáculos, que es la tercera en el TiledMap.
		capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(CAPA_PAREDES_OBSTACULOS);
		capaAgujerosNegros = (TiledMapTileLayer) mapa.getLayers().get(CAPA_AGUJERO);
		capaCollaresObjetos = (TiledMapTileLayer) mapa.getLayers().get(CAPA_OBJETOS);

		//Cargamos la matriz de los obstáculos del mapa de baldosas.
		int anchoCapa = capaObstaculos.getWidth();
		int altoCapa = capaObstaculos.getHeight();

		obstaculos = new boolean[anchoCapa][altoCapa];
		for (int x = 0; x < anchoCapa; x++) {
			for (int y = 0; y < altoCapa; y++) {
				obstaculos[x][y] = (capaObstaculos.getCell(x, y) != null);
			}
		}

		agujerosNegros = new boolean[anchoCapa][altoCapa];
		for (int x = 0; x < anchoCapa; x++) {
			for (int y = 0; y < altoCapa; y++) {
				agujerosNegros[x][y] = (capaAgujerosNegros.getCell(x, y) != null);
			}
		}

		collaresObjetos = new boolean[anchoCapa][altoCapa];
		for (int x = 0; x < anchoCapa; x++) {
			for (int y = 0; y < altoCapa; y++) {
				collaresObjetos[x][y] = (capaCollaresObjetos.getCell(x, y) != null);
			}
		}

		//Crea un BitmapFont utilizando el tipo de letra Arial 15pt por defecto incluido en el archivo JAR libgdx.
		font = new BitmapFont();

	}

	/**
	 *
	 */
	@Override
	public void render ()
	{
		//Ponemos el color del fondo a negro
		Gdx.gl.glClearColor(0, 0, 0, 1);
		//Borramos la pantalla
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Actualizamos la cámara del juego
		camara.update();
		//Vinculamos el objeto de dibuja el TiledMap con la cámara del juego
		mapaRenderer.setView(camara);
		//Dibujamos el TiledMap
		//Dibujamos las tres primeras capas del TiledMap (no incluye a la de altura)
		int[] capas = {0, 1, 2, 3, 4, 5, 6, 7};
		mapaRenderer.render(capas);

		// extraemos el tiempo de la última actualización del sprite y la acumulamos a stateTime.
		stateTime += Gdx.graphics.getDeltaTime();
		//Extraermos el frame que debe ir asociado a al momento actual.
		cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime);
		// le indicamos al SpriteBatch que se muestre en el sistema de coordenadas
		// específicas de la cámara.
		sb.setProjectionMatrix(camara.combined);
		//Inicializamos el objeto SpriteBatch
		sb.begin();

		//Dibuja texto en la posición especificada.
		if ( haCaido == true || hayColision == true)
		{
			// Pinta la pantalla en negro y desactiva los sonidos
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			font.getData().setScale(3f);
			font.setColor(Color.RED);
			//if (cantidadViedas != 0) {
				//pintamos el texto
				//font.draw(sb, "Has caido por un agujero, \n Te quedan : " + cantidadViedas + " vidas", 2, 300);
				cantidadViedas--;
				//volverArrancarJuego();
			//si no le quedan mas vidas se termina el juego
			//}else if (cantidadViedas == 0) {
				//pintamos el juego
			if(hayColision == true)
				font.draw(sb, "Te ha atrapado un globo malo!!!! \n \n Fin del juego", 2, 300);
			else if(haCaido == true)
				font.draw(sb, "Has caido por un agujero!!! \n \n Fin del juego", 2, 300);
			else if(cantidadCollaresEncontrados == 0) {
				font.setColor(Color.CORAL);
				font.draw(sb, "Has GANADO!!!!!! \n \n Fin del juego", 2, 300);
			}
			finJuego();
			//	}
		}else
		{
			//Pintamos el objeto Sprite a través del objeto SpriteBatch
			sb.draw(cuadroActual, jugadorX, jugadorY);
			//Con este codigo hacemos que camine mientras
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
				daleMovimientoTecleando(Input.Keys.LEFT);
			else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				daleMovimientoTecleando(Input.Keys.RIGHT);
			else if (Gdx.input.isKeyPressed(Input.Keys.UP))
				daleMovimientoTecleando(Input.Keys.UP);
			else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
				daleMovimientoTecleando(Input.Keys.DOWN);
			//en el caso que sea por medio de pantalla tactil para que el personaje siga el dedo
			//usaremmos el siguiente metodo y caso
			else if (Gdx.input.isTouched())
				daleMovimientoTactil(Gdx.input.getX(),  Gdx.input.getY());

			//Dibujamos las animaciones de los NPC
			for (int i = 0; i < numeroNPCs; i++) {
				actualizaNPC(i, 0.5f);
				cuadroActual = (TextureRegion) noJugador[i].getKeyFrame(stateTimeNPC);
				sb.draw(cuadroActual, noJugadorX[i], noJugadorY[i]);
			}
		}

		//Finalizamos el objeto SpriteBatch
		sb.end();
		//encargado que no se vea la capa superior si es que se muestra
		//el texto en la pantalla
		if ( haCaido != true && hayColision != true) {
			//Pintamos la  capa de alto nivel del mapa de baldosas.
			capas = new int[1];
			capas[0] = CAPA_ALTO_NIVEL;
			mapaRenderer.render(capas);
		}
		//Hace falta tambien ponerlo aqui porque de lo contrario no se detectara
		//en el caso de que un malo choque con el personaje principal
		detectaColisionMalos();

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		jugadorArriba.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorAbajo.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorIzquierda.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorDerecha.setPlayMode(Animation.PlayMode.NORMAL);
		return false;
	}

	/**
	 * Este metodo es auxiliar para que no el srpite
	 * se desplace tecleando solo una vez y se mueva
	 * mientras esta este pulsada.
	 * @param keycode
	 */
	public void daleMovimientoTecleando(int keycode)
	{
		//Si pulsamos uno de los cursores, se desplaza el sprite
		//de forma adecuada un pixel, y se pone a cero el
		//atributo que marca el tiempo de ejecución de la animación,
		//provocando que la misma se reinicie.
		stateTime = 0;

		//Guardamos la posición anterior del jugador por si al desplazarlo se topa
		//con un obstáculo y podamos volverlo a la posición anterior.
		float jugadorAnteriorX = jugadorX;
		float jugadorAnteriorY = jugadorY;

		if (keycode == Input.Keys.LEFT) {
			jugadorX += -5;
			jugador = jugadorIzquierda;
		}
		if (keycode == Input.Keys.RIGHT) {
			jugadorX += 5;
			jugador = jugadorDerecha;
		}
		if (keycode == Input.Keys.UP) {
			jugadorY += 5;
			jugador = jugadorArriba;
		}
		if (keycode == Input.Keys.DOWN) {
			jugadorY += -5;
			jugador = jugadorAbajo;
		}

		//Si pulsamos la tecla del número 1, se alterna la visibilidad de la primera capa
		//del mapa de baldosas.
		if (keycode == Input.Keys.NUM_1)
			mapa.getLayers().get(0).setVisible(!mapa.getLayers().get(0).isVisible());
		//Si pulsamos la tecla del número 2, se alterna la visibilidad de la segunda capa
		//del mapa de baldosas.
		if (keycode == Input.Keys.NUM_2)
			mapa.getLayers().get(1).setVisible(!mapa.getLayers().get(1).isVisible());

		calcularColisionObstáculo(jugadorAnteriorX, jugadorAnteriorY);
		calcularColisionAgujerosNegros();
		//Comprobamos si hay o no colisiones entre el jugador y los malos
		detectaColisionMalos();
		calcularColisionCollaresObjetos();
	//	return false;
	}

	/**
	 * Este metodo es auxiliar para que no el srpite
	 * se desplace tecleando solo una vez y se mueva
	 * mientras esta este pulsada.
	 * @param screenX
	 * @param screenY
	 */
	public void daleMovimientoTactil(float screenX, float screenY)
	{
		// Vector en tres dimensiones que recoge las coordenadas donde se ha hecho click
		// o toque de la pantalla.
		Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
		// Transformamos las coordenadas del vector a coordenadas de nuestra cámara.
		Vector3 posicion = camara.unproject(clickCoordinates);

		//Se pone a cero el atributo que marca el tiempo de ejecución de la animación,
		//provocando que la misma se reinicie.
		stateTime = 0;

		//Guardamos la posición anterior del jugador por si al desplazarlo se topa
		//con un obstáculo y podamos volverlo a la posición anterior.
		float jugadorAnteriorX = jugadorX;
		float jugadorAnteriorY = jugadorY;

		//Si se ha pulsado por encima de la animación, se sube esta 5 píxeles y se reproduce la
		//animación del jugador desplazándose hacia arriba.
		if ((jugadorY + 48) < posicion.y) {
			jugadorY += 5;
			jugador = jugadorArriba;
			//Si se ha pulsado por debajo de la animación, se baja esta 5 píxeles y se reproduce
			//la animación del jugador desplazándose hacia abajo.
		} else if ((jugadorY) > posicion.y) {
			jugadorY -= 5;
			jugador = jugadorAbajo;
		}
		//Si se ha pulsado mas de 24 a la derecha de la animación, se mueve esta 5 píxeles a la derecha y
		//se reproduce la animación del jugador desplazándose hacia la derecha.
		if ((jugadorX + 24) < posicion.x) {
			jugadorX += 5;
			jugador = jugadorDerecha;
			//Si se ha pulsado más de 24 a la izquierda de la animación, se mueve esta 5 píxeles a la
			// izquierda y se reproduce la animación del jugador desplazándose hacia la izquierda.
		} else if ((jugadorX - 24) > posicion.x) {
			jugadorX -= 5;
			jugador = jugadorIzquierda;
		}
		calcularColisionObstáculo(jugadorAnteriorX, jugadorAnteriorY);
		calcularColisionAgujerosNegros();
		//Comprobamos si hay o no colisiones entre el jugador y los malos
		detectaColisionMalos();
		calcularColisionCollaresObjetos();
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return true;
	}

	/**
	 * Detecta colicion los los abstaculas, y paredes
	 * @param jugadorAnteriorX
	 * @param jugadorAnteriorY
	 */
	public void calcularColisionObstáculo(float jugadorAnteriorX, float jugadorAnteriorY)
	{
		// Detectamos las colisiones con los obstáculos del mapa y si el jugador se sale del mismo.
		// para poner al jugador en su posición anterior
		if ((jugadorX < 0 || jugadorY < 0 ||
				jugadorX > (mapaAncho - anchoJugador) ||
				jugadorY > (mapaAlto - altoJugador)) ||
				((obstaculos[(int) ((jugadorX + anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)]) ||
						(obstaculos[(int) ((jugadorX + 3 * anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)]))) {
			jugadorX = jugadorAnteriorX;
			jugadorY = jugadorAnteriorY;
			sonidoObstaculo.play(0.5f);

		}else {
			sonidoPasos.play(0.25f);
		}
	}

	/**
	 *  Detecta la colicion con el agujero
	 */
	public void calcularColisionAgujerosNegros()
	{
		if(((agujerosNegros[(int) ((jugadorX + anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)]) ||
				(agujerosNegros[(int) ((jugadorX + 3 * anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)]))) {
			haCaido = true;
			//sonido de la caida
			musica.pause();
			sonidoPasos.stop();
			sonidoColisionEnemigo.stop();
			sonidoCaidaAgujero.play(0.25f);
		}else {
			haCaido = false;
		}
	}

	/**
	 *
	 */
	private void detectaColisionMalos()
	{
		if( hayColision != true) {
			//Vamos a comprobar que el rectángulo que rodea al jugador, no se solape
			//con el rectángulo de alguno de los NPC. Primero calculamos el rectángulo
			//en torno al jugador.
			Rectangle rJugador = new Rectangle(jugadorX,jugadorY,anchoJugador,altoJugador);
			Rectangle rNPC;
			//Ahora recorremos el array de NPC, para cada uno generamos su rectángulo envolvente
			//y comprobamos si se solapa o no con el del Jugador.
			for (int i = 0; i < numeroNPCs; i++) {
				rNPC = new Rectangle(noJugadorX[i], noJugadorY[i], anchoNoJugador, altoNoJugador);
				//Se comprueba si se solapan.
				if (rJugador.overlaps(rNPC)) {
					//hacer lo que haya que hacer en este caso, como puede ser reproducir un efecto
					//de sonido, una animación del jugador alternativa y, posiblemente, que este muera
					//y se acabe la partida actual. En principio, en este caso, lo único que se hace
					//es mostrar un mensaje en la consola de texto.
					System.out.println("Ahi esta el malo!!!");
					sonidoColisionEnemigo.play(0.25f);
					hayColision = true;
				}
			}
		}
	}

	/**
	 *  Detecta la colicion con el o encuentro con los collares
	 */
	public void calcularColisionCollaresObjetos()
	{
		int pos1 = (int) ((jugadorX + anchoJugador / 4) / anchoCelda);
		int pos2 = ((int) (jugadorY) / altoCelda);
		int pos3 = (int) ((jugadorX + 3 * anchoJugador / 4) / anchoCelda);
		int pos4 = ((int) (jugadorY) / altoCelda);
		if ((collaresObjetos[pos1][pos2])
				&& ((capaCollaresObjetos.getCell(pos1, pos2).getTile()) != null)){
			capaCollaresObjetos.getCell(pos1, pos2).setTile(null);
			cantidadCollaresEncontrados--;
			System.out.println("Ya tienes un collar!!! Quedan por recolectar: " + cantidadCollaresEncontrados
					+ " collares");
		}else if ((collaresObjetos[pos3][pos4])
				&& capaCollaresObjetos.getCell((pos3), (pos4)).getTile() != null) {
			capaCollaresObjetos.getCell(pos3, pos4).setTile(null);
			cantidadCollaresEncontrados--;
				System.out.println("Ya tienes un collar!!! Quedan por recolectar: " + cantidadCollaresEncontrados
							+ " collares");
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		jugadorArriba.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorAbajo.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorIzquierda.setPlayMode(Animation.PlayMode.NORMAL);
		jugadorDerecha.setPlayMode(Animation.PlayMode.NORMAL);//Dibujamos las animaciones de los NPC

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/**
	 * Método que permite cambiar las coordenadas del NPC en la posición "i",
	 * dada una variación "delta" en ambas coordenadas.
	 * @param i
	 * @param delta
	 */
	private void actualizaNPC(int i, float delta) {
		if( ((noJugadorY[i] + delta > destinoY[i]) && (destinoY[i] > noJugadorY[i]))
				|| (noJugadorY[i] - delta < destinoY[i]) && (destinoY[i] < noJugadorY[i]))
		{
			destinoY[i] = (float) (Math.random() * mapaAlto);
			//Determinamos cual de las animaciones verticales se utiliza.
			if (noJugadorY[i] < destinoY[i]) {
				noJugador[i] = noJugadorArriba;
			} else {
				noJugador[i] = noJugadorAbajo;
			}
		}
		else if (destinoY[i] > noJugadorY[i]) {
			noJugadorY[i] += delta;
			noJugador[i] = noJugadorArriba;
		}
		else if (destinoY[i] < noJugadorY[i]) {
			noJugadorY[i] -= delta;
			noJugador[i] = noJugadorAbajo;
		}

		if( ((noJugadorX[i] + delta > destinoX[i]) && (destinoX[i] > noJugadorX[i]))
				|| (noJugadorX[i] - delta < destinoX[i]) && (destinoX[i] < noJugadorX[i]))
		{
			destinoX[i] = (float) (Math.random() * mapaAncho);
			//Determinamos cual de las animaciones horizontales se utiliza.
			if (noJugadorX[i] < destinoX[i]) {
				noJugador[i] = noJugadorDerecha;
			} else {
				noJugador[i] = noJugadorIzquierda;
			}
		}
		else if (destinoX[i] > noJugadorX[i]) {
			noJugadorX[i] += delta;
			noJugador[i] = noJugadorDerecha;
		}
		else if (destinoX[i] < noJugadorX[i]) {
			noJugadorX[i] -= delta;
			noJugador[i] = noJugadorIzquierda;
		}
	}

	/**
	 * Metodo para terminar el juego
	 */
	private void finJuego(){
		// Espera 3 segundos y cierra el juego
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						Gdx.app.exit();
						System.exit(0);
					}
				},
				3000
		);
	}

	/**
	 * Metodo para terminar el juego
	 */
	private void volverArrancarJuego(){
		// Espera 3 segundos y cierra el juego
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						create();
					}
				},
				3000
		);
	}

	/**
	 * Liberar recursos del sistema
	 */
	@Override
	public void dispose () {
		mapa.dispose();
		mapaRenderer.dispose();
		img.dispose();
		sb.dispose();
		musica.dispose();
		sonidoObstaculo.dispose();
		sonidoPasos.dispose();
		sonidoColisionEnemigo.dispose();
		font.dispose();
	}

}
