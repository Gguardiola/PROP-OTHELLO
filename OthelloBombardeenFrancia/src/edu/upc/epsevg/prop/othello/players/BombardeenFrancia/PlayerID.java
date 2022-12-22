/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.BombardeenFrancia;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Implementació del Jugador amb timeout (Iterative Deepening).
 * @author Àlex y Gabriel
 */
public class PlayerID implements IPlayer, IAuto{

    private long[][][] zobristTable = new long[8][8][2];
    private String name = "Hakimi";
    private boolean RTO = false;
    HashMap<Long, Integer> Hash;
    private CellType BF, rival;
    private long numNodes;
    private int _depth; 
    
    /*private int[][] tablaPosibilidades = {
        { 120, -20, 20,  5,  5, 20, -20, 120},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        { 120, -20, 20,  5,  5, 20, -20, 120}
    };*/
    
    private int[][] tablaPosibilidades = {
        { 4, -3,  2,  2,  2,  2, -3,  4},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 4, -3,  2,  2,  2,  2, -3,  4}
    };
    /**
     * Constructora del jugador PlayerID
     */
    public PlayerID(){
        numNodes = 0;
        _depth = 0;
        Random random = new Random();
        for (int i = 0; i < 8; i++){//Amplada tablero
            for (int j = 0; j < 8; j++){//Altura tablero
              for (int k = 0; k < 2; k++){//Fichas 
                zobristTable[i][j][k] = random.nextLong();
              }
            }
          }
        Hash = new HashMap<>();
    }
    
    @Override
    public Move move(GameStatus gs){
       BF = gs.getCurrentPlayer();
       rival = CellType.opposite(BF);
       Move resultatMove = null;
       numNodes = 0;
       int prof = 1;
       RTO = false;
       while(!RTO){ // PERROR. true
           _depth = prof;
           resultatMove = minimax(gs, prof, resultatMove);
           prof++;
       }
       return resultatMove;
    }

    @Override
    public void timeout() {
        RTO = true;
    }

    @Override
    public String getName() {
        return name;
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////|- MINIMAX -|/////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /**
     * Función que elige el mejor movimiento donde colocaremos nuestra ficha.
     * @param t         Tablero actual de la partida.
     * @param depth     Profundiad actual de nuestro algoritmo.
     * @param lastres   Mejor movimiento anterior
     * @return          Devuelve el movimiento óptimo para nuestra ficha.
     */
    public Move minimax(GameStatus t, int depth, Move lastres){
        int valor = Integer.MIN_VALUE;
        ArrayList<Point> ap = t.getMoves();
        Move RES = new Move(ap.get(0), 0, 0, SearchType.MINIMAX); //Si no ponemos .get(0), en algun caso falla y NO DEBERÍA
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al nodo MIN             //alpha             beta
            Integer newV = MIN(newT, depth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //Si está en null, se ha quedado en mitad de alguna amplada
            if(newV == null){
                --_depth;
                return lastres;
            }
            //Mejor movimiento
            if(newV > valor){
                valor = newV;
                RES = new Move(p , numNodes, _depth, SearchType.MINIMAX);
            }   
        }
        return RES;    
    }
    /**
     * Función que devuelve el valor heurístico más grande de los movimientos estudiados.
     * @param t     Tablero con una nueva ficha en una determinada posición.
     * @param depth Profundidad restante que le queda por analizar al algoritmo.
     * @param alpha Valor de α para realizar la poda alfa-beta.
     * @param beta  Valor de β para realizar la poda alfa-beta.
     * @return      Devuelve el valor heurístico máximo entre todas las posibilidades comprobadas.
     */
    public Integer MAX(GameStatus t, int depth, int alpha, int beta){
        //Si el tiempo se acaba antes de finalizar la amplada, return null
        if(RTO) return null;
        //Se acaba la partida
        if(t.checkGameOver()){
            CellType win = t.GetWinner();
            if(win == BF){
                return 1000;
            }else if(win == rival){
                return -1000;
            }
        }
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            numNodes = numNodes + 1;
            long hashvalue = getZobristHash(t);
            if(Hash.containsKey(hashvalue)){    //Tablero repetido, return Hash
                return Hash.get(hashvalue);
            }else{                              //Nuevo tablero, return heuristica
                int h = Heuristica(t);
                Hash.put(hashvalue, h);
                return h;
            }
        }
        //Lista de movimientos
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MIN
            Integer value = MIN(newT, depth-1, alpha, beta);
            //Si el tiempo se acaba antes de finalizar la amplada, return null
            if(value == null) return null;
            alpha = Math.max(value, alpha);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                break;
            }
        }
        return alpha;
    }
    /**
     * Función que devuelve el valor heurístico más pequeño de los movimientos estudiados.
     * @param t     Tablero con una nueva ficha en una determinada posición.
     * @param depth Profundidad restante que le queda por analizar al algoritmo.
     * @param alpha Valor de α para realizar la poda alfa-beta.
     * @param beta  Valor de β para realizar la poda alfa-beta.
     * @return      Devuelve el valor heurístico mínimo entre todas las posibilidades comprobadas.
     */
    public Integer MIN(GameStatus t, int depth, int alpha, int beta){
        //Si el tiempo se acaba antes de finalizar la amplada, return null
        if(RTO) return null;
        //Se acaba la partida
        if(t.checkGameOver()){
            CellType win = t.GetWinner();
            if(win == BF){
                return 1000;
            }else if(win == rival){
                return -1000;
            }
        }
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            numNodes = numNodes + 1;
            long hashvalue = getZobristHash(t);
            if(Hash.containsKey(hashvalue)){
                return Hash.get(hashvalue);
            }else{
                int h = Heuristica(t);
                Hash.put(hashvalue, h);
                return h;
            }
        }
        //Lista de movimientos
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MAX
            Integer value = MAX(newT, depth-1, alpha, beta);
            //Si el tiempo se acaba antes de finalizar la amplada, return null
            if(value == null) return null;
            beta = Math.min(value, beta);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                break;
            }
        }
        return beta;
    }
    /**
     * Función que calcula el valor Heuristico del tablero.
     * @param t Tablero actual.
     * @return  Devuelve el valor de la heurística.
     */
    public int Heuristica(GameStatus t){
        int valorHeur = 0;
        for (int i = 0; i < t.getSize(); i++) {
            for (int j = 0; j < t.getSize(); j++) {
                if (t.getPos(i, j) == BF) {
                    valorHeur += tablaPosibilidades[i][j];
                } else if (t.getPos(i, j) == rival) {
                    valorHeur -= tablaPosibilidades[i][j];
                }
            }
        }
        return valorHeur;
    }
    /**
     * Función que calcula la key del HashMap.
     * @param t Tablero actual.
     * @return  Devuelve el valor de la key del HashMap.
     */
    public long getZobristHash(GameStatus t) {
        long hash = 0;
        for(int i = 0; i < t.getSize(); i++){
            for(int j = 0; j < t.getSize(); j++){
                CellType piece = t.getPos(i, j);
                int k;
                if(piece == CellType.PLAYER1){
                    k = 0;
                }else if(piece == CellType.PLAYER2){
                    k = 1;
                }else{
                    k = -1;
                }
                if(k != -1) hash ^= zobristTable[i][j][k];
            }
        }
        return hash;
    }

}
