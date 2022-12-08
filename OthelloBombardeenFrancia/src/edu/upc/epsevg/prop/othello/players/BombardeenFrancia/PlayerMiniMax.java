/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.BombardeenFrancia;

import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;

/**
 *
 * @author BOMBARDEENFRANCIA
 */
public class PlayerMiniMax implements IPlayer, IAuto{

    /**
     * @param args the command line arguments
     */
    private String name = "Hakimi";
    private GameStatus s;
    private float maxGB;
    private int depth;
    private long numNodes;
    private int _poda;
    
    public PlayerMiniMax(float f){
    
        this.maxGB = f;
        _poda = 0;
    
    }
    
    
    public static void main(String args[])  {
        //llamamos minimax
        
    }

    @Override
    public Move move(GameStatus gs) {
       Move resultatMove = minimax(gs);
      
       return resultatMove;
    }

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        return "Minimax(" + name + ")";
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////|- MINIMAX -|/////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    
    public Move minimax(GameStatus t){
        int valor = Integer.MIN_VALUE, col = 0, fil = 0;
        for (int i = 0; i < t.getSize(); i++){
            for (int j = 0; j < t.getSize(); j++){
                Point newPos = new Point(i,j);
                
                if(t.canMove(newPos, t.getPos(i,j))){
                    GameStatus newT = new GameStatus(t);
                    newT.movePiece(newPos);
                    //Vamos al nodo MIN             //alpha             beta
                    depth++;
                    int newV = MIN(newT, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if(newV > valor){
                        valor = newV;
                        col = i;
                        fil = j;
                    }
                }            
            }           
        }  
        
        //Transformación minimax a tipo Move
        
        Point colfil = new Point(col,fil);
        
        System.out.println("COLFIL: "+colfil);
          
        return new Move( colfil, 0L, 0, SearchType.MINIMAX);    
    }

    public int MAX(GameStatus t, int alpha, int beta){
        //Caso base
        if (depth == 10 || t.checkGameOver()){
            return Heuristica(t);
        }
        for(int i = 0; i < t.getSize(); i++){
            for(int j = 0; j < t.getSize(); j++){
                Point newPos = new Point(i,j);
                
                if(t.canMove(newPos, t.getPos(i,j))){
                    GameStatus newT = new GameStatus(t);
                    newT.movePiece(newPos);
                    //Si gana, return
                    if(newT.isGameOver()) return Integer.MAX_VALUE;
                    //if(newT.solucio(i, _myf*-1)) return Integer.MIN_VALUE;
                    //Vamos al MIN
                    depth++;
                    int value = MIN(newT, alpha, beta);
                    alpha = Math.max(value, alpha);
                    //Hacemos poda alpha-beta
                    if(alpha >= beta){
                        //System.out.println("SE ROMPE");
                        _poda++;
                        break;
                    }
                 }
            }
        }
        return alpha;
    }


    public int MIN(GameStatus t, int alpha, int beta){
        //Caso base
        if (depth == 10 || t.checkGameOver()){
            return Heuristica(t);
        }
        for(int i = 0; i < t.getSize(); i++){
            for(int j = 0; j < t.getSize(); j++){
                Point newPos = new Point(i,j);
                
                if(t.canMove(newPos, t.getPos(i,j))){
                    GameStatus newT = new GameStatus(t);
                    newT.movePiece(newPos);
                    //if(newT.solucio(i, _myf)) return Integer.MAX_VALUE;
                    if(newT.solucio(i, _myf*-1))  return Integer.MIN_VALUE;
                    //Vamos al MAX
                    depth++;
                    int value = MAX(newT, alpha, beta);
                    beta = Math.min(value, beta);
                    //Hacemos poda alpha-beta
                    if(alpha >= beta){
                        //System.out.println("SE ROMPE");
                        _poda++;
                        break;
                    }
                }            
            }
        }
        return beta;
    }
}
