import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;

public class NeuralNetwork
    implements Serializable
{
    public boolean debug;
    
    private ArrayList<Neuron> neurons;
    private HashMap<Neuron, HashMap<Neuron, Float>> weightMatrix;
    private LinkedList<Neuron> activationQueue;
    
    private int inputCount, outputCount;
    
    public NeuralNetwork(int neuronCount, int inputCount, int outputCount)
    {
        if(inputCount + outputCount > neuronCount)
            throw new RuntimeException("Cannot have fewer neurons than the sum of inputs and outputs.");
        
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        
        debug = false;
        
        neurons = new ArrayList<Neuron>(neuronCount);
        weightMatrix = new HashMap<Neuron, HashMap<Neuron, Float>>();
        activationQueue = new LinkedList<Neuron>();
        
        for(int i = 0 ; i< inputCount ; i++)
        {
            neurons.add(new InputNeuron(this));
        }
        
        for(int i = 0 ; i < neuronCount - inputCount ; i++)
        {
            neurons.add(new Neuron(this));
        }
        
        for(int i = 0 ; i < neuronCount ; i++)
            neurons.get(i).identifier = Integer.toString(i);
    }
    
    public boolean writeToFile(String fileName)
    {
        try
        {
            if(fileName.substring(fileName.length() - 4).equals(".ann"))
            {
                
            }
            else
            {
                fileName += ".ann";
            }
            FileOutputStream fout = new FileOutputStream(fileName);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(this);
            oout.close();
            fout.close();
            return true;
        }
        catch(IOException e)
        {
            System.err.println(e);
            return false;
        }
    }
    
    public static NeuralNetwork readFromFile(String fileName)
    {
        NeuralNetwork n = null;
        try
        {
            FileInputStream fin = new FileInputStream(fileName);
            ObjectInputStream oin = new ObjectInputStream(fin);
            n = (NeuralNetwork)oin.readObject();
            oin.close();
            fin.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
        catch(ClassNotFoundException e)
        {
            System.err.println(e);
        }
        
        return n;
    }
    
    public int getNeuronCount()
    {
        return neurons.size();
    }
    
    public void setInputs(float[] inputVector)
    {
        if(inputVector.length != inputCount)
        {
            throw new RuntimeException("Illegal input vector length.");
        }
        
        for(int i = 0 ; i < inputCount ; i++)
        {
            ((InputNeuron)neurons.get(i)).inputValue = inputVector[i];
        }
    }
    
    public float[] getOutputs()
    {
        float[] outputVector = new float[outputCount];
        
        for(int i = 0 ; i < outputCount ; i++)
        {
            outputVector[i] = neurons.get(i + inputCount).outputValue;
        }
        
        return outputVector;
    }
    
    private boolean enqueueActivation(Neuron n)
    {
        if(activationQueue.contains(n))
            return false;
        
        activationQueue.addLast(n);
        return true;
    }
    
    public void fire()
    {
        int activationCount = GlobalConstants.MAX_ACTIVATIONS;
        activationQueue.clear();
        for(int i = 0 ; i < inputCount ; i++)
        {
            activationQueue.add(neurons.get(i));
        }
        
        //cascade forward from input neurons, initially enqueue all downstream neurons too
        boolean addedNeurons;
        do
        {
            addedNeurons = false;
            for(int i = 0 ; i < activationQueue.size() ; i++)
            {
                ArrayList<Neuron> l = activationQueue.get(i).getOutputs();
                for(int j = 0 ; j < l.size() ; j++)
                {
                    addedNeurons = addedNeurons || enqueueActivation(l.get(j));
                }
            }
        }
        while(addedNeurons);
        
        while(!activationQueue.isEmpty())
        {
            if(activationCount <= 0)
                throw new ConvergenceException("The network failed to converge on a solution.");
            
            Neuron n = activationQueue.iterator().next();
            activationQueue.remove(n);
            if(n.fire())
            {
                //add n's outputs to the firing queue
                ArrayList<Neuron> tmp = n.getOutputs();
                for(int i = 0 ; i < tmp.size() ; i++)
                {
                    enqueueActivation(tmp.get(i));
                }
            }
            activationCount--;
        }
    }
    
    public float[] map(float[] inputVector)
    {
        setInputs(inputVector);
        fire();
        return getOutputs();
    }
    
    public void modSynapse(Neuron target, Neuron source, float weight)
    {
        if(GlobalUtils.epsilonEquals(weight,0.0f))
        {
            source.removeOutput(target);
            target.removeInput(source);
            clearWeight(target, source);
        }
        else
        {
            source.addOutput(target);
            target.addInput(source);
            setWeight(target, source, weight);
        }
   }
    
    public void modSynapse(int target, int source, float weight)
    {
        modSynapse(neurons.get(target), neurons.get(source), weight);
    }
    
    public float getWeight(Neuron target, Neuron source)
    {
        HashMap<Neuron, Float> h = weightMatrix.get(target);
        if(h == null)
        {
            return 0.0f;
        }
        
        Float f = h.get(source);
        
        if(f == null)
        {
            return 0.0f;
        }
        
        return f;
    }
    
    public float getWeight(int target, int source)
    {
        return getWeight(neurons.get(target), neurons.get(source));
    }
    
    public HashMap<Neuron, Float> getWeights(Neuron target)
    {
        return weightMatrix.get(target);
    }
    
    public void printWeights()
    {
        for(int i = 0 ; i < neurons.size() ; i++)
        {
            for(int j = 0 ; j < neurons.size() ; j++)
            {
                System.out.print(getWeight(neurons.get(j), neurons.get(i)) + "  ");
            }
            System.out.println();
        }
    }
    
    public void setWeight(Neuron target, Neuron source, float w)
    {
        HashMap<Neuron, Float> h = weightMatrix.get(target);
        
        if(h == null)
        {
            h = new HashMap<Neuron, Float>();
            weightMatrix.put(target, h);
        }
        
        h.put(source, w);
    }
    
    public void clearWeight(Neuron target, Neuron source)
    {
        HashMap<Neuron, Float> h = weightMatrix.get(target);
        
        if(h == null)
            return;
        
        h.remove(source);
    }
    
    public boolean isConnected(Neuron target, Neuron source)
    {
        HashMap<Neuron, Float> h = weightMatrix.get(target);
        
        if(h == null || h.get(source) == null)
            return false;
        
        return true;
    }
    
    public boolean isConnected(int target, int source)
    {
        return isConnected(neurons.get(target), neurons.get(source));
    }
}