package de.gaalop.visualizer.zerofinding;

import de.gaalop.OptimizationException;
import de.gaalop.cfg.AssignmentNode;
import de.gaalop.dfg.MultivectorComponent;
import de.gaalop.tba.cfgImport.optimization.maxima.MaximaDifferentiater;
import de.gaalop.visualizer.Point3d;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.RecognitionException;

/**
 * Implements a zero finder method, which samples a cube and searches at
 * every sample point in a neighborhood along the gradient a zero point
 * 
 * @author christian
 */
public class GradientMethod extends PrepareZerofinder {

    /**
     * Differentiate the codepieces with respect to ox,oy,z
     * @param codePieces 
     */
    private void diffentiateCodePieces(LinkedList<CodePiece> codePieces) {
        //differentiate each item of codePieces with respect to ox,oy,oz with the help of maxima  to _V_PRODUCT_SDx/y/z
        for (CodePiece cp: codePieces) {
            //Differntiate with respect to ox
            MaximaDifferentiater differentiater = new MaximaDifferentiater();
            LinkedList<AssignmentNode> derived;
            try {
                derived = differentiater.differentiate(cp, maximaCommand, "_V_ox");
                for (AssignmentNode d: derived) {
                    d.setVariable(new MultivectorComponent(d.getVariable().getName()+"Dx", 0));
                    cp.add(d);
                }
            } catch (OptimizationException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RecognitionException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Differntiate with respect to oy
            differentiater = new MaximaDifferentiater();
            try {
                derived = differentiater.differentiate(cp, maximaCommand, "_V_oy");
                for (AssignmentNode d: derived) {
                    d.setVariable(new MultivectorComponent(d.getVariable().getName()+"Dy", 0));
                    cp.add(d);
                }
            } catch (OptimizationException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RecognitionException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Differntiate with respect to oz
            differentiater = new MaximaDifferentiater();
            try {
                derived = differentiater.differentiate(cp, maximaCommand, "_V_oz");
                for (AssignmentNode d: derived) {
                    d.setVariable(new MultivectorComponent(d.getVariable().getName()+"Dz", 0));
                    cp.add(d);
                }
            } catch (OptimizationException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RecognitionException ex) {
                Logger.getLogger(RayMethod.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Prepares the graph, given by a list of assignment nodes, i.e. create code pieces, ...
     * @param nodes The list of nodes
     * @return The generated code pieces
     */
    private LinkedList<CodePiece> prepareGraph(LinkedList<AssignmentNode> nodes) {
       
        //Insert expressions, like Maxima,  !!!
        InsertingExpression.insertExpressions(nodes);
        
        //search _V_PRODUCT and apply the sum of the squares = _V_PRODUCT_S
        //and store the result in myNodes
        LinkedList<AssignmentNode> myNodes = createSumOfSquares(nodes);

        //Optimize pieces of code for each multivector to be rendered
        LinkedList<CodePiece> codePieces = optimizeCodePieces(myNodes);
        
        //differentiate each item of codePieces with respect to ox,oy,oz with the help of maxima  to _V_PRODUCT_SDx/y/z
        diffentiateCodePieces(codePieces);
        
        return codePieces;
    }

    @Override
    public HashMap<String, LinkedList<Point3d>> findZeroLocations(HashMap<MultivectorComponent, Double> globalValues, LinkedList<AssignmentNode> assignmentNodes) {
        LinkedList<CodePiece> codePieces = prepareGraph(assignmentNodes);
        
        HashMap<String, LinkedList<Point3d>> result = new HashMap<String, LinkedList<Point3d>>();
        for (CodePiece cp: codePieces) {
            //search zero locations of mv cp.name in every CodePiece cp
            LinkedList<Point3d> points = searchZeroLocations(cp, globalValues);
            result.put(cp.nameOfMultivector, points);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Gradient Method";
    }
    
    /**
     * Searches zero locations in a neigboorhood in a code piece,
     * starts a number of search threads
     * @param cp The code piece
     * @param globalValues The global initialised values
     * @return The zero locations points
     */
    private LinkedList<Point3d> searchZeroLocations(CodePiece cp, HashMap<MultivectorComponent, Double> globalValues) {
        LinkedList<Point3d> points = new LinkedList<Point3d>();
        float a = cubeEdgeLength;
        float dist = density;
        
        int processorCount = Runtime.getRuntime().availableProcessors();
        
        GradientMethodThread[] threads = new GradientMethodThread[processorCount];
        for (int i=0;i<processorCount;i++) {
            float from = (i*2*a)/((float) processorCount) - a;
            float to = ((i != processorCount-1) ? ((i+1)*2*a)/((float) processorCount) : 2*a) - a; 

            threads[i] = new GradientMethodThread(from, to, a, dist, globalValues, cp);
            threads[i].start();
        }

        for (int i=0;i<threads.length;i++) {
            try {
                threads[i].join();
                points.addAll(threads[i].points);
            } catch (InterruptedException ex) {
                Logger.getLogger(DiscreteCubeMethod.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return points;
    }
    
}