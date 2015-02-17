import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class GeneticAlgorithm
{
    private int inputDimensionality, outputDimensionality, maxNeurons;
    private HashMap<float[], float[]> dataPoints;
    private ArrayList<HashMap<float[], float[]>> folds;
    
    private ArrayList<DNASequence> population;
    
    private double validationErrorRateMin;
    
    public GeneticAlgorithm(int inputDimensionality, int outputDimensionality, int maxNeurons)
    {
        this.inputDimensionality = inputDimensionality;
        this.outputDimensionality = outputDimensionality;
        this.maxNeurons = maxNeurons;
        
        dataPoints = new HashMap<float[], float[]>();
        
        validationErrorRateMin = 1.0;
    }
    
    public void addDataPoint(float[] in, float[] out)
    {
        dataPoints.put(in, out);
    }
    
    public float fitnessFunction(NeuralNetwork n)
    {
        float[] trainingError = new float[folds.size()];
        float[] validationError = new float[folds.size()];
        
        for(int i = 0 ; i < folds.size() ; i++)
        {
            for(int j = 0 ; j < folds.size() ; j++)
            {
                if(j == i)
                    continue;
                
                
            }
            
            trainingError[i] = (float)(Math.sqrt(trainingError[i]));
        }
        return 0.0f;
    }
    
    public float fitnessFunction(DNASequence d)
    {
        return fitnessFunction(d.decode());
    }
    
    public void partitionForCrossValidation()
    {
        partitionForCrossValidation(10);
    }
    
    public void partitionForCrossValidation(int k)
    {
        if(dataPoints.size() < k)
            throw new RuntimeException("Not enough data points for " + k + "-fold CV.");
        
        folds = new ArrayList<HashMap<float[], float[]>>(k);
        
        int i = 0;
        for(HashMap.Entry<float[], float[]> entry : dataPoints.entrySet())
        {
            folds.get(i % k).put(entry.getKey(), entry.getValue());
            i++;
        }
    }
}