### 1. Normal arriving rate

ArrivingRate_East = 1/7
ArrivingRate_West = 1/10

##### 1.1 One parameter estimate (carPassingTime_mean)

Real carPassingTime_mean = 4

1.1.1 Real LightSwitchTime = 90 && PF LightSwitchTime  = 120
1.1.2 Real LightSwitchTime = 120 && PF LightSwitchTime = 120
1.1.3 Real lightSwitchTime = 150 && PF LightSwitchTime = 120

This experiment is designed to test when the lightSwitchTime is not that accurate, the performance of estimate of carPassingTime_mean.

Is the previous task for the experiment 1.2

##### 1.2 Two parameters estimate (carPassingTime_mean, lightSwitchTime)

1.2.1 Real LightSwitchTime = 90

1.2.2 Real LightSwitchTime = 120
Result: carPassingTime_mean (GOOD) && lightSwitchTime (BAD)

1.2.3 Real lightSwitchTime = 150

### 2. High Arriving rate

ArrivingRate_East = 1/5
ArrivingRate_West = 1/6

##### 2.1 One parameter estimate (carPassingTime_mean)

Real carPassingTime_mean = 4

2.1.1 Real LightSwitchTime = 90 && PF LightSwitchTime  = 120
2.1.2 Real LightSwitchTime = 120 && PF LightSwitchTime = 120
2.1.3 Real lightSwitchTime = 150 && PF LightSwitchTime = 120

This experiment is designed for test when the lightSwitchTime is not that accurate, the performance of estimate of carPassingTime_mean.

Is the previous task for the experiment 1.2

##### 2.2 Two parameters estimate (carPassingTime_mean, lightSwitchTime)

2.2.1 Real LightSwitchTime = 90
2.2.2 Real LightSwitchTime = 120
2.2.3 Real lightSwitchTime = 150

Let's see if one parameter of lightSwitchTime could get a good result in low arriving rate.

##### 2.3 One parameters estimate (Light SwitchTime)

Two ways to try to improve the estimate result

1. Adjust the processing noise of bootstrap, keep more particles in different situation, at least in first few step
2. Adjust the Gaussian noise, like the lightSwitch parameters, if the number itself big enough, we need add the gaussian noise may with 2, or find a way to keep a stable standard for how much noised should be added.

weight = 0.8 lightSwitchTimeSigma = 3.0

In the context of discrete events, the Kullback-Leibler (KL) divergence remains a tool for measuring the difference between two discrete probability distributions. It is typically used for probability distributions or probability mass functions, rather than the probability density functions of continuous variables.

For discrete probability distributions, the KL divergence is defined as follows:

\[ D_{KL}(P \| Q) = \sum_{i} P(i) \log\left(\frac{P(i)}{Q(i)}\right) \]

Here, \(P\) and \(Q\) are two probability distributions defined over the same set of discrete events, \(P(i)\) is the probability of event \(i\) in distribution \(P\), and \(Q(i)\) is the probability of event \(i\) in distribution \(Q\). The KL divergence measures the amount of information loss when we assume the true distribution is \(Q\) but use \(P\) to approximate events.

### Using KL Divergence in Particle Filters (Discrete Case)

In particle filters for discrete events, KL divergence can be used to measure the discrepancy between the probability distribution estimated by a particle set and the actual (or expected) probability distribution.

1. **Estimating Discrete Distributions**: Based on the particles' weights and the discrete events they represent, you can estimate the discrete probability distribution \(P\) represented by the particle set. This typically involves normalizing the particles' weights so their sum equals 1.
2. **Selecting a Reference Distribution**: \(Q\) is usually a reference probability distribution determined based on models or prior knowledge. This could be a distribution based on historical data or some theoretical model.
3. **Computing KL Divergence**: Use the formula above to calculate the KL divergence between \(P\) and \(Q\). If an event has a probability of 0 in distribution \(Q\) (meaning it's considered impossible) but has a non-zero probability in distribution \(P\), then the KL divergence is infinite, usually indicating a model inconsistency or estimation problem.
4. **Interpreting KL Divergence**: If the KL divergence is close to 0, it indicates that the probability distribution estimated by the particle filter is very close to the reference distribution. If the KL divergence is large, it indicates significant differences between the two distributions, suggesting the particle filter may not have captured the characteristics of the posterior distribution well.
5. **Taking Action**: Based on the KL divergence results, you may need to adjust the parameters of the particle filter, such as by increasing the number of particles, adjusting the resampling strategy, or reinitializing the filter to improve performance.

### Considerations When Using KL Divergence

- When \(Q(i) = 0\) and \(P(i) > 0\), meaning an event is considered impossible in the reference distribution but has a probability in the particle filter's estimated distribution, the value of \(P(i) / Q(i)\) is undefined, leading to an infinite KL divergence.
- KL divergence is not symmetric, i.e., \(D_{KL}(P \| Q) \neq D_{KL}(Q \| P)\).
- KL divergence cannot measure the similarity between two distributions, only indicating how much information loss occurs when using one distribution to approximate another.


### Measurement Data
Process base Measurement
State base Measurement 

real 8   particles 6  --> large different
real 6   particles 4  --> small different


7      9 weight = 0.9
7      6 weight = 0.5


Experiment 1.3 









