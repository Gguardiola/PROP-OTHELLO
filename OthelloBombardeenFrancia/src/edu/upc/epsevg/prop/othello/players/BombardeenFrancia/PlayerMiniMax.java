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
import java.util.ArrayList;

/**
 *
 * @author BOMBARDEENFRANCIA
 */
public class PlayerMiniMax implements IPlayer, IAuto{

    private String name = "Hakimi";
    private int _depth;
    private long numNodes;
    private int _poda;
    private CellType BF;
    private CellType rival;
    
    private int[][] tablaPosibilidades = {
        { 120, -20, 20,  5,  5, 20, -20, 120},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        { 120, -20, 20,  5,  5, 20, -20, 120}
    };
    /*
    private int[][] tablaPosibilidades = {
        {4, -3,  2,  2,  2,  2, -3,  4},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 4, -3,  2,  2,  2,  2, -3,  4}
    };*/
    
      
    public PlayerMiniMax(int i){
        _poda = 0;
        _depth = i;
        numNodes = 0;
    }
    
    
    @Override
    public Move move(GameStatus gs) {
       numNodes = 0;
       BF = gs.getCurrentPlayer();
       rival = CellType.opposite(BF);
       Move resultatMove = minimax(gs, _depth);
       return resultatMove;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    @Override
    public String getName() {
        return name;
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////|- MINIMAX -|/////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    
    public Move minimax(GameStatus t, int depth){
        int valor = Integer.MIN_VALUE;
        //System.out.println("lol");
        ArrayList<Point> ap = t.getMoves();
        Move RES = new Move(ap.get(0), 0, 0, SearchType.MINIMAX); //Si no ponemos .get(0), en algun caso falla y NO DEBERÍA
        for (Point p : ap){
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al nodo MIN             //alpha             beta
            int newV = MIN(newT, depth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if(newV > valor){
                valor = newV;
                RES = new Move(p , numNodes, _depth, SearchType.MINIMAX);
            }
        }
        //Transformación minimax a tipo Move
        return RES;
    }

    public int MAX(GameStatus t, int depth, int alpha, int beta){
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            return Heuristica(t);
        }
        
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MIN
            int value = MIN(newT, depth-1, alpha, beta);
            alpha = Math.max(value, alpha);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                _poda++;
                break;
            }
        }
        return alpha;
    }


    public int MIN(GameStatus t, int depth, int alpha, int beta){
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            return Heuristica(t);
        }
        
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MAX
            int value = MAX(newT, depth-1, alpha, beta);
            beta = Math.min(value, beta);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                _poda++;
                break;
            }
        }
        
        return beta;
    }
    
    public int Heuristica(GameStatus t){
        numNodes = numNodes + 1;
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
}
