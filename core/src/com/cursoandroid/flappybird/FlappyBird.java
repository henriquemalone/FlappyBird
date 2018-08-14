package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.css.Rect;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private int contador = 0;
    private SpriteBatch batch;
    private Texture[] passaro;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoAlto;
    private Texture gameover;
    private Random numeroRandom;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle RetanguloCanoTopo;
    private Rectangle RetanguloCanoBaixo;
    //private ShapeRenderer shape;

    //Atributos configuração
    private int movimento = 0;
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0; // 0 -> jogo não iniciado   1 -> jogo iniciado  2->Game Over
    private int pontuacao = 0;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical = 0;
    private float posicaoMovimentoCanoHorizontal = 0;
    private float espacoEntreCanos = 0;
    private float deltaTime;
    private float espacoEntreCanosRandom;
    private boolean marcouPonto = false;

    //Câmera - REsolução em diferentes celulares
    private OrthographicCamera camera;
    private Viewport viewport;

    /*///////////////PROBLEMA NA RESOLUÇÃO, ALTERAR AS LINHAS ABAIXO*/
    private final float VIRTUAL_WIDTH = 728;
    private final float VIRTUAL_HEIGHT = 1024;

	
	@Override
	public void create () {
        //Gdx.app.log("Create", "Inicializado o jogo");

        //Adicionar imagem
        batch = new SpriteBatch();

        //Numero Aleatorio pra posicionar os canos
        numeroRandom = new Random();

        //Colisão
        passaroCirculo = new Circle();
        /*RetanguloCanoBaixo = new Rectangle();
        RetanguloCanoTopo = new Rectangle();
        shape = new ShapeRenderer();*/

        //Fonte da pontuação
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        //Fonte reinciar partida
        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        //Add imagem do passaro
        passaro = new Texture[3];
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoAlto = new Texture("cano_topo.png");
        canoBaixo = new Texture("cano_baixo.png");
        gameover = new Texture("game_over.png");

        //Configuração da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;


        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;
        espacoEntreCanos = 300;
	}

	@Override
	public void render () {

	    camera.update();

	    //Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

	    /*contador++;
        Gdx.app.log("Render", "Renderizando o jogo" + contador);*/
        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;

        //Animaçao das asas batendo
        if (variacao > 2) {
            variacao = 0;
        }

	    //Estado do jogo
	    if(estadoJogo == 0){ //nao iniciado
            //Verifica se a tela foi tocada
	        if(Gdx.input.justTouched()){
	            estadoJogo = 1;
            }
        } else { //iniciado

            velocidadeQueda++;

            //Queda do passado
            if (posicaoInicialVertical > 0) {
                posicaoInicialVertical -= velocidadeQueda;
            }

            if(estadoJogo == 1){
                //Movimentação dos canos
                posicaoMovimentoCanoHorizontal -= deltaTime * 400;

                //Efeito do touch
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }

                //Verifica se o cano saiu inteiramente da tela para aparecer de novo do outro lado
                if (posicaoMovimentoCanoHorizontal < -canoAlto.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    espacoEntreCanosRandom = numeroRandom.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //Verifica pontuação
                if(posicaoMovimentoCanoHorizontal < 120){
                    if(!marcouPonto){
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            } else { //tela game over
                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    //Volta o passaro pro lugar inicial
                    posicaoInicialVertical = alturaDispositivo / 2;
                    //Volta o cano pro lugar inicial
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }
            }
        }

        //Configurar dados da projeçãom da camera
        batch.setProjectionMatrix(camera.combined);

	    batch.begin();
	    batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoAlto, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + espacoEntreCanosRandom);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + espacoEntreCanosRandom);
        batch.draw(passaro[(int)variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if(estadoJogo == 2){
            batch.draw(gameover, larguraDispositivo / 2 - gameover.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - gameover.getWidth() / 2, alturaDispositivo / 2);
        }
	    batch.end();

	    //Cria o circulo que representa o passaro
	    passaroCirculo.set(120 + passaro[0].getWidth() / 2, posicaoInicialVertical + passaro[0].getHeight() / 2, passaro[0].getWidth() / 2);
	    RetanguloCanoBaixo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + espacoEntreCanosRandom, canoBaixo.getWidth(), canoBaixo.getHeight());
        RetanguloCanoTopo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + espacoEntreCanosRandom, canoAlto.getWidth(), canoAlto.getHeight());

	    //Desenhar formas
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(RetanguloCanoBaixo.x, RetanguloCanoBaixo.y, RetanguloCanoBaixo.width, RetanguloCanoBaixo.height);
        shape.rect(RetanguloCanoTopo.x, RetanguloCanoTopo.y, RetanguloCanoTopo.width, RetanguloCanoTopo.height);
        //shape.setColor(Color.RED);
        shape.end();*/

        //Teste de Colisão
        if(Intersector.overlaps(passaroCirculo, RetanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, RetanguloCanoTopo) || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){
            estadoJogo = 2;
        }

	}

	@Override
	public void resize (int width, int height) {
        viewport.update(width, height);
	}
}
