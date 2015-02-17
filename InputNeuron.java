public class InputNeuron extends Neuron
{
    public float inputValue;
    
    public InputNeuron(NeuralNetwork n)
    {
        super(n);
        inputValue = 0.0f;
    }
    
    @Override
    public float transferFunction(float activationValue)
    {
        return inputValue;
    }
}