import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class Neuron
    implements Serializable
{
    public float bias;
    public float outputValue;
    public String identifier;
    
    private ArrayList<Neuron> inputs;
    private ArrayList<Neuron> outputs;
    private NeuralNetwork parentNetwork;
    
    public Neuron(NeuralNetwork n)
    {
        parentNetwork = n;
        bias = 0.0f;
        
        inputs = new ArrayList<Neuron>();
        outputs = new ArrayList<Neuron>();
    }
    
    //vanilla sigmoid transfer function - override in derived classes if required
    public float transferFunction(float activationValue)
    {
        return (float)(1 / (1 + Math.exp(bias - activationValue)));
    }
    
    public boolean addInput(Neuron n)
    {
        if(inputs.contains(n))
            return false;
        
        inputs.add(n);
        return true;
    }
    
    public boolean addOutput(Neuron n)
    {
        if(outputs.contains(n))
            return false;
        
        outputs.add(n);
        return true;
    }
    
    public boolean removeInput(Neuron n)
    {
        if(inputs.contains(n))
        {
            inputs.remove(n);
            return true;
        }
        return false;
    }
    
    public boolean removeOutput(Neuron n)
    {
        if(outputs.contains(n))
        {
            outputs.remove(n);
            return true;
        }
        return false;
    }
    
    public ArrayList<Neuron> getOutputs()
    {
        return outputs;
    }
    
    public boolean fire()
    {   
        float oldOutput = outputValue;
        
        float activationValue = 0.0f;
        HashMap<Neuron, Float> inputWeights = parentNetwork.getWeights(this);
        
        for(int i = 0 ; i < inputs.size() ; i++)
        {
            Float w = parentNetwork.getWeight(this, inputs.get(i));
            activationValue += inputs.get(i).outputValue * w;
        }
        
        outputValue = transferFunction(activationValue);
        if(parentNetwork.debug)
        {
            System.out.println("firing " + identifier + " with output " + outputValue + " (delta = " + Math.abs(outputValue - oldOutput) + ")");
        }
        
        return (Math.abs(oldOutput - outputValue) > GlobalConstants.PROPOGATION_THRESHOLD);
    }
}