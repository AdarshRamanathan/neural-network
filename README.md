##Overview

#####Synopsis
A Java library that implements neural networks. Currently work-in-progress.

#####Author
Adarsh Ramanathan

#####Version
0.1

#####Date
2015/02/17

##Usage

####NeuralNetwork	(Class)
#####Constructor Summary
- `public NeuralNetwork(int neuronCount, int inputCount, int outputCount)`
creates a neural network with _neuronCount_ neurons. the created network has an input vector of dimension _inputCount_, and an output vector of dimension _outputCount_.
_neuronCount_ must be atleast as large as the sum of _inputCount_ and _outputCount_.
Initially, no synapses are present (all synaptic weights default to zero).

#####Field Summary
- `public boolean debug`
if set to true, prints debug messages for various events in the network. defaults to false.

#####Method Summary
- `public static NeuralNetwork readFromFile(String filename)`
deserialize a NeuralNetwork object from a file.

- `public boolean writeToFile(String filename)`
serialize the NeuralNetwork object to a file. Appends an extension of '.ann' to the filename if not already present.
returns true if serialization was successful, false otherwise.

- `public void modSynapse(Neuron target, Neuron source, float weight)`
set the synaptic weight from the _source_ to the _target_ Neuron to _weight_.

- `public void modSynapse(int target, int source, float weight)`
same as above, resolves neuron index numbers to Neuron objects, then calls `modSynapse(Neuron, Neuron, float)`

- `public float getWeight(Neuron target, Neuron source)`
returns the synaptic weight from the _source_ to the _target_ Neuron.

- `public float getWeight(int target, int source)`
same as above, resolves neuron index numbers to Neuron objects, then returns the value returned by `getWeight(Neuron, Neuron)`

- `public boolean isConnected(Neuron target, Neuron source)`
returns true if _source_ is connected to _target_ (ie, the synaptic weight from _source_ to _target_ is non-zero).

- `public boolean isConnected(int target, int source)`
same as above, resolves neuron index numbers to Neuron objects, then returns the value returned by `isConnected(Neuron, Neuron)`
	
- `public float[] map(float[] inputs)`
maps an input vector to an output vector through the neural network.

##Requirements
Java SE 8 or later. May or may not work with older versions.				

##Current progress	
#####NeuralNetwork
- Neuron model
- InputNeuron model
- Generic network architecture model with a HashMap for storing synaptic weights, an way to update said weights, a firing queue, and a way to feed the network input and read its output.
- Classes with global constants and globally useful utility functions.
			
#####Training/Evolution
- The beginnings of an evolutionary algorithm based training paradigm, which will be capable of deciding both the network architecture and the synaptic weights.
Capability to represent a given NeuralNetwork object as a DNASequence object, along with the ability to generate offspring with crossover and mutation functions.

##Pending work	
#####NeuralNetwork
- Feedforward network architecture model, built on top of the generic model, with specific constraints imposed on the synapses (only allow synapses from layer k to layer k + 1).
			
#####Training/Evolution
- Fitness function for the GA
- Cross validation to stop training when error is minimized.
- Backpropogation based training
- Unsupervised learning / Hebbian learning?

#####Other
- Some sort of GUI for manually creating networks.