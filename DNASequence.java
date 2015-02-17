import java.util.Random;
import java.io.*;

public class DNASequence
{
    private String sequence;
    private int inputCount, outputCount, maxNeurons;
    
    public DNASequence(String sequence, int inputCount, int outputCount, int maxNeurons)
    {
        this.sequence = new String(sequence);
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.maxNeurons = maxNeurons;
    }
    
    public DNASequence(NeuralNetwork n, int inputCount, int outputCount, int maxNeurons)
    {
        sequence = "";
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.maxNeurons = maxNeurons;
        
        int neuronCount = n.getNeuronCount();
        sequence += leftpad(Integer.toHexString(neuronCount));
        
        for(int i = inputCount ; i < neuronCount ; i++)
        {
            for(int j = 0 ; j < neuronCount ; j++)
            {
                float w = n.getWeight(i, j);
                int x = Float.floatToRawIntBits(w);
                sequence += leftpad(Integer.toHexString(x));
            }
            Random r = new Random();
            for(int j = neuronCount ; j < maxNeurons ; j++)
            {
                sequence += leftpad(Integer.toHexString(r.nextInt()));
            }
        }
        
        for(int i = neuronCount ; i < maxNeurons ; i++)
        {
            Random r = new Random();
            for(int j = 0 ; j < maxNeurons ; j++)
            {
                sequence += leftpad(Integer.toHexString(r.nextInt()));
            }
        }
    }
    
    public static DNASequence[] getOffspring(DNASequence d1, DNASequence d2)
    {
        if(d1.inputCount != d2.inputCount || d1.outputCount != d2.outputCount || d1.maxNeurons != d2.maxNeurons || d1.sequence.length() != d2.sequence.length())
            throw new RuntimeException("Seems like you're trying to cross a dolphin with a glowworm. So, here, catch.");
        
        String s1, s2;
        
        if(Math.random() < GlobalConstants.CROSSOVER_RATE)
        {
            int crossoverPoint = (int)(d1.sequence.length() * Math.random());
            s1 = new String(d1.sequence.substring(0, crossoverPoint) + d2.sequence.substring(crossoverPoint));
            s2 = new String(d2.sequence.substring(0, crossoverPoint) + d1.sequence.substring(crossoverPoint));
        }
        else
        {
            s1 = new String(d1.sequence);
            s2 = new String(d2.sequence);
        }
        
        s1 = mutate(s1);
        s2 = mutate(s2);
        
        DNASequence[] ret = new DNASequence[2];
        ret[0] = new DNASequence(s1, d1.inputCount, d1.outputCount, d1.maxNeurons);
        ret[1] = new DNASequence(s2, d2.inputCount, d2.outputCount, d2.maxNeurons);
        
        return ret;
    }
    
    private static String mutate(String str)
    {
        StringBuilder s1 = new StringBuilder(str);
        
        for(int i = 0 ; i < s1.length() ; i++)
        {
            StringBuilder s = new StringBuilder(leftpad(Integer.toString(Integer.parseUnsignedInt(s1.substring(i, i + 1), 16), 2)).substring(4));
            
            for(int j = 0 ; j < 4 ; j++)
            {
                if(Math.random() < GlobalConstants.MUTATION_RATE)
                {
                    if(s.charAt(j) == '1')
                        s.setCharAt(j, '0');
                    else
                        s.setCharAt(j, '1');
                }
            }
            
            s1.setCharAt(i, Integer.toHexString(Integer.parseUnsignedInt(s.toString(), 2)).charAt(0));
        }
        
        return s1.toString();
    }
    
    private static String leftpad(String s)
    {
        return "00000000".substring(s.length()) + s;
    }
    
    public NeuralNetwork decode()
    {
        String s = diffuse(sequence);
        int neuronCount = getNextInt(s);
        s = s.substring(8);
        
        NeuralNetwork n = new NeuralNetwork(neuronCount, inputCount, outputCount);
        
        for(int i = inputCount ; i < neuronCount ; i++)
        {
            for(int j = 0 ; j < neuronCount ; j++)
            {
                float w = getNextFloat(s);
                s = s.substring(8);
                n.modSynapse(i, j, w);
            }
            for(int j = neuronCount ; j < maxNeurons ; j++)
            {
                s = s.substring(8);
            }
        }
        
        return n;
    }
    
    private static String diffuse(String s)
    {
        return s;
    }
    
    public String toString()
    {
        return sequence;
    }
    
    public boolean writeToFile(String fileName)
    {
        if(fileName.substring(fileName.length() - 4).equals(".dna"))
        {
            
        }
        else
        {
            fileName += ".dna";
        }
        
        try
        {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(Integer.toString(inputCount));
            bw.newLine();
            bw.write(Integer.toString(outputCount));
            bw.newLine();
            bw.write(Integer.toString(maxNeurons));
            bw.newLine();
            bw.write(sequence);
            
            bw.flush();
            bw.close();
            fw.close();
            return true;
        }
        catch(IOException e)
        {
            System.err.println(e);
            return false;
        }
    }
    
    public static DNASequence readFromFile(String fileName)
    {
        DNASequence d = null;
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            int inputCount = Integer.parseInt(br.readLine());
            int outputCount = Integer.parseInt(br.readLine());
            int maxNeurons = Integer.parseInt(br.readLine());
            d = new DNASequence(br.readLine(), inputCount, outputCount, maxNeurons);
            br.close();
            fr.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
        
        return d;
    }
    
    private static int getNextInt(String s)
    {
        int ret = Integer.parseUnsignedInt(s.substring(0, 8), 16);
        return ret;
    }
    
    private static float getNextFloat(String s)
    {
        int tmp = Integer.parseUnsignedInt(s.substring(0, 8), 16);
        float ret = Float.intBitsToFloat(tmp);
        return ret;
    }
}